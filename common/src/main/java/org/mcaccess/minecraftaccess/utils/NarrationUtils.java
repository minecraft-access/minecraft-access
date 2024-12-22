package org.mcaccess.minecraftaccess.utils;

import org.mcaccess.minecraftaccess.mixin.MobSpawnerLogicAccessor;
import org.mcaccess.minecraftaccess.utils.position.Orientation;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.*;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Translate input objects to narration text.
 */
@Slf4j
public class NarrationUtils {
    public static final Predicate<BlockState> IS_REDSTONE_WIRE = (BlockState state) -> state.getBlock() instanceof RedstoneWireBlock;

    public static String narrateEntity(Entity entity) {
        // When the entity is named, this value is its custom name,
        // otherwise it is its type.
        String nameOrType = entity.getName().getString();
        String type = entity.hasCustomName() ? I18n.translate(entity.getType().getTranslationKey()) : nameOrType;

        String variant = getVariantInfo(entity);
        if (!Strings.isBlank(variant)) {
            Map<String, String> map = Map.of("variant", variant, "animal", type);
            //noinspection SuperfluousFormat
            type = I18n.translate("minecraft_access.other.animal_variant_format", map);
        }

        // Add its type in front of its name if it has been renamed with name tag,
        // so even if there are two different types of entities that named the same name,
        // the mod can make the player tell the difference:
        // "Cat Neko", "Dog Neko"... where "Neko" is the entity's name and "Cat" or "Dog" is its type
        String text = entity.hasCustomName() ? type + " " + nameOrType : type;

        List<String> equipments = new ArrayList<>();

        if (entity instanceof AnimalEntity animalEntity) {
            switch (animalEntity) {
                case SheepEntity sheepEntity -> text = getSheepInfo(sheepEntity, text);
                case TameableEntity tameableEntity -> {
                    // wolf, cat, parrot
                    String isTamedText = I18n.translate("minecraft_access.read_crosshair.is_tamed", text);
                    text = tameableEntity.isTamed() ? isTamedText : text;
                    text = tameableEntity.isInSittingPose() ? addSittingInfo(text) : text;
                }
                case FoxEntity foxEntity -> text = foxEntity.isSitting() ? addSittingInfo(text) : text;
                case PandaEntity pandaEntity -> text = pandaEntity.isSitting() ? addSittingInfo(text) : text;
                case CamelEntity camelEntity -> text = camelEntity.isSitting() ? addSittingInfo(text) : text;
                default -> {
                }
            }

            if (animalEntity.isBaby())
                text = I18n.translate("minecraft_access.read_crosshair.animal_entity_baby", text);
            if (animalEntity.isLeashed())
                text = I18n.translate("minecraft_access.read_crosshair.animal_entity_leashed", text);
        }

        if (entity instanceof LivingEntity livingEntity) {
            for (var equipment : livingEntity.getEquippedItems()) {
                if (equipment.isEmpty())
                    continue;
                String equipmentName = I18n.translate(equipment.getTranslationKey());
                equipments.add(equipmentName);
            }
        }

        if (entity instanceof HostileEntity) {
            if (entity instanceof ZombieVillagerEntity zombieVillagerEntity) {
                text = zombieVillagerEntity.isConverting() ?
                        I18n.translate("minecraft_access.read_crosshair.zombie_villager_is_curing", text) :
                        text;
            }
        }

        if (!equipments.isEmpty()) {
            String wordConnection = I18n.translate("minecraft_access.other.words_connection");
            var values = Map.of("entity", text, "equipments", String.join(wordConnection, equipments));
            //noinspection SuperfluousFormat
            text = I18n.translate("minecraft_access.other.entity_with_equipments", values);
        }

        return text;
    }

    /**
     * Get variant text of wolf, cat, axolotl
     */
    private static String getVariantInfo(Entity animal) {
        return switch (animal) {
            case WolfEntity wolf -> getDogCatVariantInfo(wolf.getVariant());
            case CatEntity cat -> getDogCatVariantInfo(cat.getVariant());
            case AxolotlEntity axolotl -> {
                String color = axolotl.getVariant().getName();
                yield I18n.translate("minecraft_access.axolotl_variant." + color);
            }
            default -> "";
        };
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static String getDogCatVariantInfo(RegistryEntry<?> entry) {
        var variantType = entry.value();
        String variantTypeName = switch (variantType) {
            case WolfVariant ignored -> "wolf_variant";
            case CatVariant ignored -> "cat_variant";
            default -> "";
        };

        Identifier variant = entry.getKey().get().getValue();
        String color = variant.toShortTranslationKey();
        String transKey = "minecraft_access." + variantTypeName + "." + color;
        return I18n.translate(transKey);
    }

    private static String addSittingInfo(String currentQuery) {
        return I18n.translate("minecraft_access.read_crosshair.is_sitting", currentQuery);
    }

    private static String getSheepInfo(SheepEntity sheepEntity, String currentQuery) {
        String dyedColor = sheepEntity.getColor().getName();
        String translatedColor = I18n.translate("color.minecraft." + dyedColor);
        String shearable = sheepEntity.isShearable() ?
                I18n.translate("minecraft_access.read_crosshair.shearable", currentQuery) :
                I18n.translate("minecraft_access.read_crosshair.not_shearable", currentQuery);
        return translatedColor + " " + shearable;
    }

    public static String narrateNumber(double d) {
        return d >= 0 ? String.valueOf(d) : I18n.translate("minecraft_access.other.negative", -d);
    }

    public static String narrateRelativePositionOfPlayerAnd(BlockPos blockPos) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) return "up";
        if (minecraftClient.player == null) return "up";

        Direction dir = minecraftClient.player.getHorizontalFacing();

//        Vec3d diff = minecraftClient.player.getEyePos().subtract(Vec3d.ofCenter(blockPos)); // post 1.18
        Vec3d diff = new Vec3d(minecraftClient.player.getX(), minecraftClient.player.getEyeY(), minecraftClient.player.getZ()).subtract(Vec3d.ofCenter(blockPos)); // pre 1.18
        BlockPos diffBlockPos = new BlockPos((int) diff.x, (int) diff.y, (int) diff.z); // post 1.20
//        BlockPos diffBlockPos = new BlockPos(Math.round(diff.x), Math.round(diff.y), Math.round(diff.z));

        String diffXBlockPos = "";
        String diffYBlockPos = "";
        String diffZBlockPos = "";

        if (diffBlockPos.getX() != 0) {
            if (dir == Direction.NORTH) {
                diffXBlockPos = getDifferenceString(diffBlockPos.getX(), "right", "left");
            } else if (dir == Direction.SOUTH) {
                diffXBlockPos = getDifferenceString(diffBlockPos.getX(), "left", "right");
            } else if (dir == Direction.EAST) {
                diffXBlockPos = getDifferenceString(diffBlockPos.getX(), "away", "behind");
            } else if (dir == Direction.WEST) {
                diffXBlockPos = getDifferenceString(diffBlockPos.getX(), "behind", "away");
            }
        }

        if (diffBlockPos.getY() != 0) {
            diffYBlockPos = getDifferenceString(diffBlockPos.getY(), "up", "down");
        }

        if (diffBlockPos.getZ() != 0) {
            if (dir == Direction.SOUTH) {
                diffZBlockPos = getDifferenceString(diffBlockPos.getZ(), "away", "behind");
            } else if (dir == Direction.NORTH) {
                diffZBlockPos = getDifferenceString(diffBlockPos.getZ(), "behind", "away");
            } else if (dir == Direction.EAST) {
                diffZBlockPos = getDifferenceString(diffBlockPos.getZ(), "right", "left");
            } else if (dir == Direction.WEST) {
                diffZBlockPos = getDifferenceString(diffBlockPos.getZ(), "left", "right");
            }
        }

        String text;
        if (dir == Direction.NORTH || dir == Direction.SOUTH)
            text = String.format("%s  %s  %s", diffZBlockPos, diffYBlockPos, diffXBlockPos);
        else
            text = String.format("%s  %s  %s", diffXBlockPos, diffYBlockPos, diffZBlockPos);
        return text;
    }

    public static String getDifferenceString(int blocks, String key1, String key2) {
        return I18n.translate("minecraft_access.util.position_difference_" + (blocks < 0 ? key1 : key2), Math.abs(blocks));
    }

    public static String narrateCoordinatesOf(BlockPos blockPos) {
        try {
            String posX = narrateNumber(blockPos.getX());
            String posY = narrateNumber(blockPos.getY());
            String posZ = narrateNumber(blockPos.getZ());
            return String.format("%s x %s y %s z", posX, posY, posZ);
        } catch (Exception e) {
            log.error("An error occurred when getting position narration.", e);
            return "";
        }
    }

    /**
     * @param pos  block position (in the client world)
     * @param side if side is provided, then the invoker is ReadCrosshair
     * @return (toSpeak, currentQuery):
     * "toSpeak" is the actual one to be spoken through Narrator,
     * "currentQuery" is kind of shortened "toSpeak" that is used for checking if target is changed compared to previous.
     */
    public static Pair<String, String> narrateBlockForContentChecking(BlockPos pos, String side) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (Objects.isNull(client)) return new Pair<>("", "");
        ClientWorld clientWorld = client.world;
        if (clientWorld == null) return new Pair<>("", "");

        // Since Minecraft uses flyweight pattern for blocks and entities,
        // All same type of blocks share one singleton Block instance,
        // While every block keep their states with a BlockState instance.
        WorldUtils.BlockInfo blockInfo = WorldUtils.getBlockInfo(pos);
        BlockPos blockPos = blockInfo.pos();
        BlockState blockState = blockInfo.state();
        Block block = blockInfo.type();
        BlockEntity blockEntity = blockInfo.entity();

        // Difference between toSpeak and currentQuery:
        // currentQuery is used for checking condition, toSpeak is actually the one to be spoken.
        // currentQuery is checked to not speak the same block repeatedly, two blocks can have same name.
        String name = block.getName().getString();
        String toSpeak = Strings.isBlank(side) ? name : name + " " + side;
        String currentQuery = name + side;

        // Different special narration (toSpeak) about different type of blocks
        try {
            if (blockState.isOf(Blocks.WATER) || blockState.isOf(Blocks.LAVA)) {
                toSpeak = NarrationUtils.narrateFluidBlock(blockPos);
                return new Pair<>(toSpeak, toSpeak);
            }

            if (blockEntity != null) {
                // The all signs tag include all types of signs, so it should also work with the hanging signs in 1.20.x
                if (blockState.isIn(BlockTags.ALL_SIGNS)) {
                    toSpeak = getSignInfo((SignBlockEntity) blockEntity, client.player, toSpeak);
                } else if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
                    Pair<String, String> beehiveInfo = getBeehiveInfo(beehiveBlockEntity, blockState, toSpeak, currentQuery);
                    toSpeak = beehiveInfo.getLeft();
                    currentQuery = beehiveInfo.getRight();
                }
            }

            if (block instanceof PlantBlock || block instanceof CocoaBlock) {
                Pair<String, String> cropsInfo = getCropsInfo(block, blockState, toSpeak, currentQuery);
                toSpeak = cropsInfo.getLeft();
                currentQuery = cropsInfo.getRight();
            }

            // Check if farmland is wet
            if (block instanceof FarmlandBlock && blockState.get(FarmlandBlock.MOISTURE) == FarmlandBlock.MAX_MOISTURE) {
                toSpeak = I18n.translate("minecraft_access.crop.wet_farmland", toSpeak);
                currentQuery = "wet" + currentQuery;
            }

            // Speak monster spawner mob type
            if (blockEntity instanceof MobSpawnerBlockEntity spawner) {
                // Will not support non-vanilla custom configured multiple-mob spawner (like generated with command)
                Entity entity = ((MobSpawnerLogicAccessor) spawner.getLogic()).getRenderedEntity();
                // Monster spawners that gotten from creative creating screen is empty.
                String entityName = "Empty";
                if (entity != null) {
                    entityName = Objects.requireNonNull(entity.getDisplayName()).getString();
                }
                toSpeak = entityName + " " + toSpeak;
                currentQuery = entityName + currentQuery;
            }

            // Redstone related
            Pair<String, String> redstoneRelatedInfo = getRedstoneRelatedInfo(clientWorld, blockPos, block, blockState, toSpeak, currentQuery);
            toSpeak = redstoneRelatedInfo.getLeft();
            currentQuery = redstoneRelatedInfo.getRight();

            if (clientWorld.getFluidState(blockPos).isOf(Fluids.WATER)) {
                toSpeak = I18n.translate("minecraft_access.crop.water_logged", toSpeak);
                currentQuery = "waterlogged" + currentQuery;
            }

        } catch (Exception e) {
            log.error("An error occurred while adding narration text for special blocks", e);
        }

        return new Pair<>(toSpeak, currentQuery);
    }

    /**
     * @param pos  block position (in the client world)
     * @param side if side is provided, then the invoker is ReadCrosshair
     */
    public static String narrateBlock(BlockPos pos, String side) {
        return narrateBlockForContentChecking(pos, side).getLeft();
    }

    private static String getSignInfo(SignBlockEntity signEntity, ClientPlayerEntity player, String toSpeak) {
        String[] lines = new String[4];

        for (int i = 0; i < 4; i++) {
//            lines[i] = signEntity.getTextOnRow(i, false).getString(); // Pre 1.20.x
            lines[i] = signEntity.getText(signEntity.isPlayerFacingFront(player)).getMessage(i, false).getString();
        }
        String content = String.join(", ", lines);
        return I18n.translate("minecraft_access.read_crosshair.sign_" + (signEntity.isPlayerFacingFront(player) ? "front" : "back") + "_content", toSpeak, content);
    }

    private static @NotNull Pair<String, String> getRedstoneRelatedInfo(ClientWorld world, BlockPos blockPos, Block block, BlockState blockState, String toSpeak, String currentQuery) {
        boolean isEmittingPower = world.isEmittingRedstonePower(blockPos, Direction.DOWN);
        boolean isReceivingPower = world.isReceivingRedstonePower(blockPos);

        if (block instanceof PistonBlock) {
            String facing = blockState.get(PistonBlock.FACING).getName();
            toSpeak = I18n.translate("minecraft_access.read_crosshair.facing", toSpeak, I18n.translate("minecraft_access.direction." + facing));
            currentQuery += "facing " + facing;
            if (isReceivingPower) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
                currentQuery += "powered";
            }
        } else if ((block instanceof GlowLichenBlock || block instanceof RedstoneLampBlock) && (isReceivingPower || isEmittingPower)) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
            currentQuery += "powered";
//        } else if ((block instanceof RedstoneTorchBlock || block instanceof LeverBlock || block instanceof AbstractButtonBlock) && isEmittingPower) { // pre 1.19.3
        } else if (block instanceof RedstoneWireBlock) {
            Pair<String, String> p = getRedstoneWireInfo(blockState, blockPos, toSpeak, currentQuery);
            toSpeak = p.getLeft();
            currentQuery = p.getRight();
        } else if ((block instanceof RedstoneTorchBlock || block instanceof LeverBlock || block instanceof ButtonBlock) && isEmittingPower) { // From 1.19.3
            toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
            currentQuery += "powered";
        } else if (block instanceof DoorBlock doorBlock && doorBlock.isOpen(blockState)) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.opened", toSpeak);
            currentQuery += "open";
        } else if (block instanceof HopperBlock) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.facing", toSpeak, I18n.translate("minecraft_access.direction." + blockState.get(HopperBlock.FACING).getName()));
            currentQuery += "facing " + blockState.get(HopperBlock.FACING).getName();
            if (isReceivingPower) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.locked", toSpeak);
                currentQuery += "locked";
            }
        } else if (block instanceof ObserverBlock) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.facing", toSpeak, I18n.translate("minecraft_access.direction." + blockState.get(ObserverBlock.FACING).getName()));
            currentQuery += "facing " + blockState.get(ObserverBlock.FACING).getName();
            if (isEmittingPower) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
                currentQuery += "powered";
            }
        } else if (block instanceof DispenserBlock) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.facing", toSpeak, I18n.translate("minecraft_access.direction." + blockState.get(DispenserBlock.FACING).getName()));
            currentQuery += "facing " + blockState.get(DispenserBlock.FACING).getName();
            if (isReceivingPower) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
                currentQuery += "powered";
            }
        } else if (block instanceof ComparatorBlock) {
            ComparatorMode mode = blockState.get(ComparatorBlock.MODE);
            Direction facing = blockState.get(ComparatorBlock.FACING);
            String correctFacing = I18n.translate("minecraft_access.direction." + Orientation.getOppositeDirectionKey(facing.getName()).toLowerCase());
            toSpeak = I18n.translate("minecraft_access.read_crosshair.comparator_info", toSpeak, correctFacing, mode);
            if (isReceivingPower) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
                currentQuery += "powered";
            }
            currentQuery += "mode:" + mode + " facing:" + correctFacing;
        } else if (block instanceof RepeaterBlock) {
            boolean locked = blockState.get(RepeaterBlock.LOCKED);
            int delay = blockState.get(RepeaterBlock.DELAY);
            Direction facing = blockState.get(ComparatorBlock.FACING);
            String correctFacing = I18n.translate("minecraft_access.direction." + Orientation.getOppositeDirectionKey(facing.getName()).toLowerCase());

            toSpeak = I18n.translate("minecraft_access.read_crosshair.repeater_info", toSpeak, correctFacing, delay);
            currentQuery += "delay:" + delay + " facing:" + correctFacing;
            if (locked) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.locked", toSpeak);
                currentQuery += "locked";
            }
        } else if (isReceivingPower) { // For all the other blocks
            toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
            currentQuery += "powered";
        }

        return new Pair<>(toSpeak, currentQuery);
    }

    private static @NotNull Pair<String, String> getRedstoneWireInfo(BlockState blockState, BlockPos pos, String toSpeak, String currentQuery) {
        int powerLevel = blockState.get(RedstoneWireBlock.POWER);
        if (powerLevel > 0) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.redstone_wire_power", toSpeak, powerLevel);
            currentQuery += "power level " + powerLevel;
        }

        List<String> connectedDirections = Direction.Type.HORIZONTAL.stream()
                .map(direction -> {
                    String directionName = I18n.translate("minecraft_access.direction." + direction.getName());

                    switch (blockState.get(RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))) {
                        case UP -> {
                            return directionName + " " + I18n.translate("minecraft_access.direction.up");
                        }
                        case SIDE -> {
                            return directionName;
                        }
                        default -> {
                            return null;
                        }
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Unconnected redstone dust now has all direction block states set to "side" since 20w18a (before 1.16)
        // https://minecraft.wiki/w/Redstone_Dust
        // So here is an additional check to see if the redstone wire is really connected to all directions
        if (connectedDirections.size() == 4) {
            // If two redstone wires are connected, they're at one of three relative positions: [side, side down, side up].
            // Take one sample relative position (x+1) then check if any block at [-1,0,1] height is also redstone wire.
            Iterable<BlockPos> threePosAtSide = BlockPos.iterate(pos.add(1, -1, 0), pos.add(1, 1, 0));
            boolean result = WorldUtils.checkAnyOfBlocks(threePosAtSide, IS_REDSTONE_WIRE);
            // If there's no redstone wire on x+1 side,
            // then current wire is not connected to that side,
            // so it's not connected to all directions.
            if (!result) return new Pair<>(toSpeak, currentQuery);
        }

        String directionsToSpeak = String.join(I18n.translate("minecraft_access.other.words_connection"), connectedDirections);
        toSpeak = I18n.translate("minecraft_access.read_crosshair.redstone_wire_connection", toSpeak, directionsToSpeak);
        currentQuery += "connected to " + connectedDirections;

        return new Pair<>(toSpeak, currentQuery);
    }

    private static @NotNull Pair<String, String> getBeehiveInfo(BeehiveBlockEntity blockEntity, BlockState blockState, String toSpeak, String currentQuery) {
        boolean isSmoked = blockEntity.isSmoked();
        int honeyLevel = blockState.get(BeehiveBlock.HONEY_LEVEL);
        Direction facingDirection = blockState.get(BeehiveBlock.FACING);

        if (isSmoked) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.bee_hive_smoked", toSpeak);
            currentQuery += "smoked";
        }

        if (honeyLevel > 0) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.bee_hive_honey_level", toSpeak, honeyLevel);
            currentQuery += ("honey-level:" + honeyLevel);
        }

        toSpeak = I18n.translate("minecraft_access.read_crosshair.bee_hive_facing", toSpeak, facingDirection.getName());
        currentQuery += ("facing:" + facingDirection.getName());

        return new Pair<>(toSpeak, currentQuery);
    }

    /**
     * Blocks that can be planted and have growing stages (age) and harvestable.<br>
     * Including wheat, carrot, potato, beetroot, nether wart, cocoa bean,
     * torch flower, pitcher crop.<br>
     * Watermelon vein and pumpkin vein are not harvestable so not be included here.
     */
    private static @NotNull Pair<String, String> getCropsInfo(Block block, BlockState blockState, String toSpeak, String currentQuery) {
        int currentAge, maxAge;

        switch (block) {
            case CropBlock ignored -> {
                if (block instanceof BeetrootsBlock) {
                    // Beetroots has a different max_age as 3
                    currentAge = blockState.get(BeetrootsBlock.AGE);
                    maxAge = BeetrootsBlock.BEETROOTS_MAX_AGE;
                } else if (block instanceof TorchflowerBlock) {
                    currentAge = blockState.get(TorchflowerBlock.AGE);
                    maxAge = 2;
                } else {
                    // While wheat, carrots, potatoes has max_age as 7
                    currentAge = blockState.get(CropBlock.AGE);
                    maxAge = CropBlock.MAX_AGE;
                }
            }
            case CocoaBlock ignored -> {
                currentAge = blockState.get(CocoaBlock.AGE);
                maxAge = CocoaBlock.MAX_AGE;
            }
            case NetherWartBlock ignored -> {
                currentAge = blockState.get(NetherWartBlock.AGE);
                // The max_age of NetherWartBlock hasn't been translated, for future compatibility, hard code it.
                maxAge = 3;
            }
            case PitcherCropBlock ignored -> {
                currentAge = blockState.get(PitcherCropBlock.AGE);
                maxAge = 4;
            }
            case null, default -> {
                return new Pair<>(toSpeak, currentQuery);
            }
        }

        String configKey = checkCropRipeLevel(currentAge, maxAge);
        return new Pair<>(I18n.translate(configKey, toSpeak), I18n.translate(configKey, currentQuery));
    }

    /**
     * @return corresponding ripe level text config key
     */
    private static String checkCropRipeLevel(Integer current, int max) {
        if (current >= max) {
            return "minecraft_access.crop.ripe";
        } else if (current < max / 2) {
            return "minecraft_access.crop.seedling";
        } else {
            return "minecraft_access.crop.half_ripe";
        }
    }

    /**
     * @param pos fluid position (in the client world)
     * @return (toSpeak, currentQuery):
     * "toSpeak" is the actual one to be spoken through Narrator,
     * "currentQuery" is kind of shortened "toSpeak" that is used for checking if target is changed compared to previous.
     */
    private static String narrateFluidBlock(BlockPos pos) {
        FluidState fluidState = WorldUtils.getClientWorld().getFluidState(pos);
        String name = getFluidI18NName(fluidState.getRegistryEntry());
        int level = fluidState.getLevel();
        String levelString = level < 8 ? I18n.translate("minecraft_access.read_crosshair.fluid_level", level) : "";
        return name + " " + levelString;
    }

    private static String getFluidI18NName(RegistryEntry<Fluid> fluid) {
        String translationKey = fluid.getKeyOrValue().map(
                (fluidKey) -> "block." + fluidKey.getValue().getNamespace() + "." + fluidKey.getValue().getPath(),
                (fluidValue) -> "[unregistered " + fluidValue + "]"
        );
        return I18n.translate(translationKey);
    }
}
