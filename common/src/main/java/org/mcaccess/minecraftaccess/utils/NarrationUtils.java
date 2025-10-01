package org.mcaccess.minecraftaccess.utils;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.mixin.BaseSpawnerAccessor;
import org.mcaccess.minecraftaccess.mixin.WolfAccessor;
import org.mcaccess.minecraftaccess.utils.position.Orientation;

/**
 * Translate input objects to narration text.
 */
@Slf4j
public final class NarrationUtils {
    private static final Map<IntegerProperty, Integer> CROP_AGE_PROPERTIES = Map.of(
            BlockStateProperties.AGE_1, 1,
            BlockStateProperties.AGE_2, 2,
            BlockStateProperties.AGE_3, 3,
            BlockStateProperties.AGE_4, 4,
            BlockStateProperties.AGE_5, 5,
            BlockStateProperties.AGE_7, 7,
            BlockStateProperties.AGE_15, 15,
            BlockStateProperties.AGE_25, 25
    );

    private NarrationUtils() {
    }

    public static String narrateEntity(Entity entity) {
        // When the entity is named, this value is its custom name,
        // otherwise it is its type.
        String nameOrType = entity.getName().getString();
        boolean entityIsSitting = false;
        String type = entity.hasCustomName() ? I18n.get(entity.getType().getDescriptionId()) : nameOrType;
        boolean isDroppedItem = entity instanceof ItemEntity itemEntity && itemEntity.onGround()
                || entity instanceof AbstractArrow abstractArrow && abstractArrow.pickup == AbstractArrow.Pickup.ALLOWED;

        String variant = getVariantInfo(entity);
        if (!Strings.isBlank(variant)) {
            Map<String, String> map = Map.of("variant", variant, "animal", type);
            type = I18n.get("minecraft_access.other.animal_variant_format", map);
        }

        // Add its type in front of its name if it has been renamed with name tag,
        // so even if there are two different types of entities that named the same name,
        // the mod can make the player tell the difference:
        // "Cat Neko", "Dog Neko"... where "Neko" is the entity's name and "Cat" or "Dog" is its type
        String text = entity.hasCustomName() ? type + ' ' + nameOrType : type;

        List<String> equipments = new ArrayList<>();

        if (Config.getInstance().narrateCrosshair.narrateAdditionalEntityPoses) {
            switch (entity.getPose()) {
                case SLEEPING -> text = I18n.get("minecraft_access.read_crosshair.sleeping", text);
                case DYING -> text = I18n.get("minecraft_access.read_crosshair.dying", text);
                case DIGGING -> text = I18n.get("minecraft_access.read_crosshair.digging", text);
                case FALL_FLYING -> text = I18n.get("minecraft_access.read_crosshair.fall_flying", text);
                case ROARING -> text = I18n.get("minecraft_access.read_crosshair.roaring", text);
                case SLIDING -> text = I18n.get("minecraft_access.read_crosshair.sliding", text);
                case SWIMMING -> text = I18n.get("minecraft_access.read_crosshair.swimming", text);
                case SITTING -> entityIsSitting = true;
                case CROAKING -> text = I18n.get("minecraft_access.read_crosshair.croaking", text);
                case EMERGING -> text = I18n.get("minecraft_access.read_crosshair.emerging", text);
                case SHOOTING -> text = I18n.get("minecraft_access.read_crosshair.shooting", text);
                case INHALING -> text = I18n.get("minecraft_access.read_crosshair.inhaling", text);
                case SNIFFING -> text = I18n.get("minecraft_access.read_crosshair.sniffing", text);
                case CROUCHING -> text = I18n.get("minecraft_access.read_crosshair.crouching", text);
                case LONG_JUMPING -> text = I18n.get("minecraft_access.read_crosshair.long_jumping", text);
                case USING_TONGUE -> text = I18n.get("minecraft_access.read_crosshair.using_tongue", text);
                case STANDING -> {
                }
                default -> log.warn("Unhandled pose found: {} for additional pose narration in Narration Utils", entity.getPose().name());
            }
        }

        if (!entityIsSitting) {
            switch (entity) {
                case Fox fox -> entityIsSitting = fox.isSitting();
                case Panda panda -> entityIsSitting = panda.isSitting();
                case Camel camel -> entityIsSitting = camel.isCamelSitting();
                case TamableAnimal tamableAnimal -> entityIsSitting = tamableAnimal.isInSittingPose();
                default -> {
                }
            }
        }

        if (entity instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame()) {
            text = I18n.get("minecraft_access.read_crosshair.tamed", text);
        }

        if (entityIsSitting) text = I18n.get("minecraft_access.read_crosshair.sitting", text);

        if (entity instanceof Mob mob && mob.isBaby()) text = I18n.get("minecraft_access.read_crosshair.baby", text);

        if (entity instanceof Leashable leashable && leashable.isLeashed()) {
            text = I18n.get("minecraft_access.read_crosshair.leashed", text);
        }

        text = switch (entity) {
            case Sheep sheep -> getSheepInfo(sheep, text);
            case ZombieVillager zombieVillager when zombieVillager.isConverting() ->
                    I18n.get("minecraft_access.read_crosshair.zombie_villager_is_curing", text);
            case Display.ItemDisplay itemDisplay when itemDisplay.itemRenderState() != null -> {
                @SuppressWarnings("DataFlowIssue")
                String itemName = itemDisplay.itemRenderState().itemStack().getItemName().getString();
                yield I18n.get("minecraft_access.point_of_interest.locking.display_item", itemName);
            }
            case Display.TextDisplay textDisplay when textDisplay.textRenderState() != null -> {
                //noinspection DataFlowIssue
                yield textDisplay.textRenderState().text().getString();
            }
            case Display.BlockDisplay blockDisplay when blockDisplay.blockRenderState() != null -> {
                @SuppressWarnings("DataFlowIssue")
                Block ghostBlock = blockDisplay.blockRenderState().blockState().getBlock();
                yield I18n.get("minecraft_access.point_of_interest.locking.display_block", ghostBlock.getName().getString());
            }
            default -> {
                if (isDroppedItem) {
                    yield I18n.get("minecraft_access.point_of_interest.locking.dropped_item", text);
                }
                yield text;
            }
        };

        if (entity instanceof LivingEntity livingEntity) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack equipment = livingEntity.getItemBySlot(slot);
                if (equipment.isEmpty()) continue;
                String equipmentName = equipment.getHoverName().getString();
                equipments.add(equipmentName);
            }
        }

        if (!equipments.isEmpty()) {
            String wordConnection = I18n.get("minecraft_access.other.words_connection");
            var values = Map.of("entity", text, "equipments", String.join(wordConnection, equipments));
            text = I18n.get("minecraft_access.other.entity_with_equipments", values);
        }

        return text;
    }

    /**
     * Get variant text of wolf, cat, axolotl
     */
    private static String getVariantInfo(Entity animal) {
        return switch (animal) {
            case Cat cat -> I18n.get(String.format("minecraft_access.cat_variant.%s",
                    cat.getVariant().unwrapKey()
                            .map(ResourceKey::location)
                            .map(ResourceLocation::toShortLanguageKey)
                            .orElse("other")
            ));
            case Wolf wolf -> I18n.get(String.format("minecraft_access.wolf_variant.%s",
                    ((WolfAccessor) wolf).callGetVariant().unwrapKey()
                            .map(ResourceKey::location)
                            .map(ResourceLocation::toShortLanguageKey)
                            .orElse("other")
            ));
            case Axolotl axolotl -> I18n.get(String.format("minecraft_access.axolotl_variant.%s", axolotl.getVariant().getName()));
            default -> "";
        };
    }

    private static String getSheepInfo(Sheep sheep, String currentQuery) {
        String color = I18n.get("color.minecraft." + sheep.getColor().getName());
        String shearable = I18n.get(sheep.readyForShearing() ? "minecraft_access.read_crosshair.shearable" : "minecraft_access.read_crosshair.not_shearable", currentQuery);
        return color + ' ' + shearable;
    }

    public static String narrateNumber(double num) {
        DecimalFormat df = new DecimalFormat();
        num = Math.round(num * 10.0) / 10.0;
        return num >= 0 ? String.valueOf(df.format(num)) : I18n.get("minecraft_access.other.negative", df.format(-num));
    }

    public static String narrateNumber(int num) {
        return num >= 0 ? String.valueOf(num) : I18n.get("minecraft_access.other.negative", -num);
    }

    public static String narrateRelativePositionOfPlayerAnd(BlockPos blockPos) {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient.player == null) return "up";

        Direction dir = minecraftClient.player.getDirection();
        Vec3 diff = new Vec3(minecraftClient.player.getX(), minecraftClient.player.getEyeY(), minecraftClient.player.getZ()).subtract(Vec3.atCenterOf(blockPos)); // pre 1.18
        BlockPos diffBlockPos = new BlockPos((int) diff.x, (int) diff.y, (int) diff.z); // post 1.20

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
        if (dir == Direction.NORTH || dir == Direction.SOUTH) {
            text = String.format("%s  %s  %s", diffZBlockPos, diffYBlockPos, diffXBlockPos);
        } else {
            text = String.format("%s  %s  %s", diffXBlockPos, diffYBlockPos, diffZBlockPos);
        }
        return text;
    }

    public static String getDifferenceString(int blocks, String key1, String key2) {
        return I18n.get("minecraft_access.util.position_difference_" + (blocks < 0 ? key1 : key2), Math.abs(blocks));
    }

    public static String narrateCoordinatesOf(BlockPos blockPos) {
        String posX = narrateNumber(blockPos.getX());
        String posY = narrateNumber(blockPos.getY());
        String posZ = narrateNumber(blockPos.getZ());
        return String.format("%s x %s y %s z", posX, posY, posZ);
    }

    /**
     * @param pos  block position (in the client world)
     * @param side if side is provided, then the invoker is ReadCrosshair
     * @return (narration, currentQuery):
     * "narration" is the actual one to be narrated through Narrator,
     * "currentQuery" is kind of shortened "narration" that is used for checking if target is changed compared to previous.
     */
    public static Tuple<String, String> narrateBlockForContentChecking(BlockPos blockPos, String side) {
        Minecraft client = Minecraft.getInstance();
        ClientLevel clientWorld = client.level;
        if (clientWorld == null) return new Tuple<>("", "");

        // Since Minecraft uses flyweight pattern for blocks and entities,
        // All same type of blocks share one singleton Block instance,
        // While every block keep their states with a BlockState instance.
        BlockState blockState = clientWorld.getBlockState(blockPos);
        Block block = blockState.getBlock();
        BlockEntity blockEntity = clientWorld.getBlockEntity(blockPos);

        // Difference between narration and currentQuery:
        // currentQuery is used for checking condition, narration is actually the one to be narrated.
        // currentQuery is checked to not narrate the same block repeatedly, two blocks can have same name.
        String name = block.getName().getString();
        String narration = Strings.isBlank(side) ? name : name + ' ' + side;
        String currentQuery = name + side;

        // Different special narration (narration) about different type of blocks
        if (blockState.is(Blocks.WATER) || blockState.is(Blocks.LAVA)) {
            narration = narrateFluidBlock(blockPos);
            return new Tuple<>(narration, narration);
        }

        if (blockEntity != null) {
            if (blockState.is(BlockTags.ALL_SIGNS)) {
                narration = getSignInfo((SignBlockEntity) blockEntity, client.player, narration);
            } else if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
                Tuple<String, String> beehiveInfo = getBeehiveInfo(beehiveBlockEntity, blockState, narration, currentQuery);
                narration = beehiveInfo.getA();
                currentQuery = beehiveInfo.getB();
            } else
                // Narrate monster spawner mob type
                if (blockEntity instanceof SpawnerBlockEntity spawner) {
                    // Will not support non-vanilla custom configured multiple-mob spawner (like generated with command)
                    Entity entity = ((BaseSpawnerAccessor) spawner.getSpawner()).getDisplayEntity();
                    // Monster spawners that are gotten from the creative inventory are empty.
                    String entityName = I18n.get("minecraft_access.read_crosshair.spawner_empty");
                    if (entity != null) {
                        entityName = Objects.requireNonNull(entity.getDisplayName()).getString();
                    }
                    narration = entityName + ' ' + narration;
                    currentQuery = entityName + currentQuery;
                }
        }

        Tuple<String, String> cropsInfo = getCropsInfo(block, blockState, narration, currentQuery);
        narration = cropsInfo.getA();
        currentQuery = cropsInfo.getB();
        if (block instanceof FarmBlock && blockState.getValue(FarmBlock.MOISTURE) == FarmBlock.MAX_MOISTURE) {
            narration = I18n.get("minecraft_access.crop.wet_farmland", narration);
            currentQuery = "wet" + currentQuery;
        } else if (block instanceof EndPortalFrameBlock) {
            if (blockState.getValue(EndPortalFrameBlock.HAS_EYE)) {
                narration = I18n.get("minecraft_access.read_crosshair.end_portal_frame_with_eye", narration);
            } else {
                narration = I18n.get("minecraft_access.read_crosshair.end_portal_frame_empty", narration);
            }
        }

        // Redstone related
        Tuple<String, String> redstoneRelatedInfo = getRedstoneRelatedInfo(clientWorld, blockPos, block, blockState, narration, currentQuery);
        narration = redstoneRelatedInfo.getA();
        currentQuery = redstoneRelatedInfo.getB();

        if (clientWorld.getFluidState(blockPos).is(Fluids.WATER)) {
            narration = I18n.get("minecraft_access.crop.water_logged", narration);
            currentQuery = "waterlogged" + currentQuery;
        }

        return new Tuple<>(narration, currentQuery);
    }

    /**
     * @param pos  block position (in the client world)
     * @param side if side is provided, then the invoker is ReadCrosshair
     */
    public static String narrateBlock(BlockPos pos, String side) {
        return narrateBlockForContentChecking(pos, side).getA();
    }

    private static String getSignInfo(SignBlockEntity signEntity, LocalPlayer player, String narration) {
        String[] lines = new String[4];

        for (int i = 0; i < 4; i++) {
            lines[i] = signEntity.getText(signEntity.isFacingFrontText(player)).getMessage(i, false).getString();
        }
        String content = String.join(", ", lines);
        return I18n.get("minecraft_access.read_crosshair.sign_" + (signEntity.isFacingFrontText(player) ? "front" : "back") + "_content", narration, content);
    }

    private static @NotNull Tuple<String, String> getRedstoneRelatedInfo(ClientLevel world, BlockPos blockPos, Block block, BlockState blockState, String narration, String currentQuery) {
        boolean isEmittingPower = world.hasSignal(blockPos, Direction.DOWN);
        boolean isReceivingPower = world.hasNeighborSignal(blockPos);

        if (block instanceof PistonBaseBlock) {
            String facing = blockState.getValue(PistonBaseBlock.FACING).getName();
            narration = I18n.get("minecraft_access.read_crosshair.facing", narration, I18n.get("minecraft_access.direction." + facing));
            currentQuery += "facing " + facing;
            if (isReceivingPower) {
                narration = I18n.get("minecraft_access.read_crosshair.powered", narration);
                currentQuery += "powered";
            }
        } else if ((block instanceof GlowLichenBlock || block instanceof RedstoneLampBlock) && (isReceivingPower || isEmittingPower)) {
            narration = I18n.get("minecraft_access.read_crosshair.powered", narration);
            currentQuery += "powered";
        } else if (block instanceof RedStoneWireBlock) {
            Tuple<String, String> p = getRedstoneWireInfo(blockState, blockPos, narration, currentQuery);
            narration = p.getA();
            currentQuery = p.getB();
        } else if ((block instanceof RedstoneTorchBlock || block instanceof LeverBlock || block instanceof ButtonBlock) && isEmittingPower) { // From 1.19.3
            narration = I18n.get("minecraft_access.read_crosshair.powered", narration);
            currentQuery += "powered";
        } else if ((block instanceof DoorBlock doorBlock && doorBlock.isOpen(blockState)) || (block instanceof FenceGateBlock && blockState.getValue(FenceGateBlock.OPEN))) {
            narration = I18n.get("minecraft_access.read_crosshair.opened", narration);
            currentQuery += "open";
        } else if (block instanceof HopperBlock) {
            narration = I18n.get("minecraft_access.read_crosshair.facing", narration, I18n.get("minecraft_access.direction." + blockState.getValue(HopperBlock.FACING).getName()));
            currentQuery += "facing " + blockState.getValue(HopperBlock.FACING).getName();
            if (isReceivingPower) {
                narration = I18n.get("minecraft_access.read_crosshair.locked", narration);
                currentQuery += "locked";
            }
        } else if (block instanceof ObserverBlock) {
            narration = I18n.get("minecraft_access.read_crosshair.facing", narration, I18n.get("minecraft_access.direction." + blockState.getValue(ObserverBlock.FACING).getName()));
            currentQuery += "facing " + blockState.getValue(ObserverBlock.FACING).getName();
            if (isEmittingPower) {
                narration = I18n.get("minecraft_access.read_crosshair.powered", narration);
                currentQuery += "powered";
            }
        } else if (block instanceof DispenserBlock) {
            narration = I18n.get("minecraft_access.read_crosshair.facing", narration, I18n.get("minecraft_access.direction." + blockState.getValue(DispenserBlock.FACING).getName()));
            currentQuery += "facing " + blockState.getValue(DispenserBlock.FACING).getName();
            if (isReceivingPower) {
                narration = I18n.get("minecraft_access.read_crosshair.powered", narration);
                currentQuery += "powered";
            }
        } else if (block instanceof ComparatorBlock) {
            ComparatorMode mode = blockState.getValue(ComparatorBlock.MODE);
            Direction facing = blockState.getValue(ComparatorBlock.FACING);
            String correctFacing = I18n.get("minecraft_access.direction." + Orientation.getOppositeDirectionKey(facing.getName()).toLowerCase());
            narration = I18n.get("minecraft_access.read_crosshair.comparator_info", narration, correctFacing, mode);
            if (isReceivingPower) {
                narration = I18n.get("minecraft_access.read_crosshair.powered", narration);
                currentQuery += "powered";
            }
            currentQuery += "mode:" + mode + " facing:" + correctFacing;
        } else if (block instanceof RepeaterBlock) {
            boolean locked = blockState.getValue(RepeaterBlock.LOCKED);
            int delay = blockState.getValue(RepeaterBlock.DELAY);
            Direction facing = blockState.getValue(ComparatorBlock.FACING);
            String correctFacing = I18n.get("minecraft_access.direction." + Orientation.getOppositeDirectionKey(facing.getName()).toLowerCase());

            narration = I18n.get("minecraft_access.read_crosshair.repeater_info", narration, correctFacing, delay);
            currentQuery += "delay:" + delay + " facing:" + correctFacing;
            if (locked) {
                narration = I18n.get("minecraft_access.read_crosshair.locked", narration);
                currentQuery += "locked";
            }
        } else if (isReceivingPower) { // For all the other blocks
            narration = I18n.get("minecraft_access.read_crosshair.powered", narration);
            currentQuery += "powered";
        }

        return new Tuple<>(narration, currentQuery);
    }

    private static @NotNull Tuple<String, String> getRedstoneWireInfo(BlockState blockState, BlockPos pos, String narration, String currentQuery) {
        int powerLevel = blockState.getValue(RedStoneWireBlock.POWER);
        if (powerLevel > 0) {
            narration = I18n.get("minecraft_access.read_crosshair.redstone_wire_power", narration, powerLevel);
            currentQuery += "power level " + powerLevel;
        }

        List<String> connectedDirections = Direction.Plane.HORIZONTAL.stream().map(direction -> {
            String directionName = I18n.get("minecraft_access.direction." + direction.getName());

            switch (blockState.getValue(RedStoneWireBlock.PROPERTY_BY_DIRECTION.get(direction))) {
                case UP -> {
                    return directionName + ' ' + I18n.get("minecraft_access.direction.up");
                }
                case SIDE -> {
                    return directionName;
                }
                default -> {
                    return null;
                }
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        // Unconnected redstone dust now has all direction block states set to "side" since 20w18a (before 1.16)
        // https://minecraft.wiki/w/Redstone_Dust
        // So here is an additional check to see if the redstone wire is really connected to all directions
        if (connectedDirections.size() == 4) {
            // If two redstone wires are connected, they're at one of three relative positions: [side, side down, side up].
            // Take one sample relative position (x+1) then check if any block at [-1,0,1] height is also redstone wire.
            Iterable<BlockPos> threePosAtSide = BlockPos.betweenClosed(pos.offset(1, -1, 0), pos.offset(1, 1, 0));
            boolean result = WorldUtils.checkAnyOfBlocks(threePosAtSide, state -> state.getBlock() instanceof RedStoneWireBlock);
            // If there's no redstone wire on x+1 side,
            // then current wire is not connected to that side,
            // so it's not connected to all directions.
            if (!result) return new Tuple<>(narration, currentQuery);
        }

        String directionsNarration = String.join(I18n.get("minecraft_access.other.words_connection"), connectedDirections);
        narration = I18n.get("minecraft_access.read_crosshair.redstone_wire_connection", narration, directionsNarration);
        currentQuery += "connected to " + connectedDirections;

        return new Tuple<>(narration, currentQuery);
    }

    private static @NotNull Tuple<String, String> getBeehiveInfo(BeehiveBlockEntity blockEntity, BlockState blockState, String narration, String currentQuery) {
        boolean isSmoked = blockEntity.isSedated();
        int honeyLevel = blockState.getValue(BeehiveBlock.HONEY_LEVEL);
        Direction facingDirection = blockState.getValue(BeehiveBlock.FACING);

        if (isSmoked) {
            narration = I18n.get("minecraft_access.read_crosshair.bee_hive_smoked", narration);
            currentQuery += "smoked";
        }

        if (honeyLevel > 0) {
            narration = I18n.get("minecraft_access.read_crosshair.bee_hive_honey_level", narration, honeyLevel);
            currentQuery += ("honey-level:" + honeyLevel);
        }

        narration = I18n.get("minecraft_access.read_crosshair.bee_hive_facing", narration, facingDirection.getName());
        currentQuery += ("facing:" + facingDirection.getName());

        return new Tuple<>(narration, currentQuery);
    }

    private static @NotNull Tuple<String, String> getCropsInfo(Block block, BlockState blockState, String narration, String currentQuery) {
        if (block instanceof CropBlock crop) {
            return addCropGrowth(narration, currentQuery, crop.getAge(blockState), crop.getMaxAge());
        }

        // There are some growable blocks that are not crop blocks like the Torch Flower crop
        if (block instanceof BonemealableBlock || block instanceof VegetationBlock) {
            Optional<Map.Entry<IntegerProperty, Integer>> ageProperty = CROP_AGE_PROPERTIES.entrySet().stream()
                    .filter(entry -> blockState.hasProperty(entry.getKey()))
                    .findFirst();

            if (ageProperty.isPresent()) {
                return addCropGrowth(narration, currentQuery, blockState.getValue(ageProperty.get().getKey()), ageProperty.get().getValue());
            }
        }

        // No growth information found
        return new Tuple<>(narration, currentQuery);
    }

    private static @NotNull Tuple<String, String> addCropGrowth(String narration, String currentQuery, int age, int maxAge) {
        if (age == maxAge) {
            return new Tuple<>(I18n.get("minecraft_access.crop.mature", narration), I18n.get("minecraft_access.crop.mature", currentQuery));
        }
        float growth = (float) age / maxAge;
        return new Tuple<>(
                I18n.get("minecraft_access.crop.percent", (int) (growth * 100), narration),
                I18n.get("minecraft_access.crop.percent", (int) (growth * 100), currentQuery)
        );
    }

    /**
     * @param pos fluid position (in the client world)
     * @return (narration, currentQuery):
     * "narration" is the actual one to be narrated through Narrator,
     * "currentQuery" is kind of shortened "narration" that is used for checking if target is changed compared to previous.
     */
    private static String narrateFluidBlock(BlockPos pos) {
        FluidState fluidState = Minecraft.getInstance().level.getFluidState(pos);
        Optional<String> fluidName = getTranslatedName(fluidState.holder(), "block");
        int level = fluidState.getAmount();
        String levelString = level < 8 ? I18n.get("minecraft_access.read_crosshair.fluid_level", level) : "";
        return fluidName.map(name -> String.format("%s %s", name, levelString)).orElse(levelString);
    }

    /**
     * @return {EffectName} {Amplifier} {Duration}
     */
    public static String narrateEffect(MobEffectInstance effect) {
        StringBuilder result = new StringBuilder();
        result.append(I18n.get(effect.getDescriptionId())).append(' ');

        int amplifier = effect.getAmplifier();
        if (amplifier > 1) {
            result.append(amplifier).append(' ');
        }

        if (effect.isInfiniteDuration()) {
            result.append(I18n.get("effect.duration.infinite"));
        } else {
            // StatusEffectInstance#getDuration returns ticks, so we divide by 20 in order to convert to seconds
            // 1 second = 20 ticks
            Duration d = Duration.ofSeconds(effect.getDuration() / 20);
            // Note: In some languages (like Chinese), the formats of duration and instant are different,
            // while the formatting below is based on a clock instant.
            // It's tolerable rather than introducing several time related I18N keys.
            String fmt = d.toHoursPart() == 0 ? "mm':'ss" : "HH':'mm':'ss";
            result.append(DurationFormatUtils.formatDuration(d.toMillis(), fmt));
        }

        return result.toString();
    }

    /**
     * Gets the translated name from registry entry.
     *
     * @param holder the holder's registry entry
     * @param type the type of holder you want the translated name for
     * @return the holder's human readable name as an Optional
     */
    public static Optional<String> getTranslatedName(Holder<?> holder, String type) {
        Optional<String> translatedName = holder.unwrapKey().map(key -> I18n.get(key.location().toLanguageKey(type)));
        if (translatedName.isEmpty()) {
            log.error("Failed to get a valid translation of the {} name", type);
        }
        return translatedName;
    }

    @Contract(pure = true)
    public static @NotNull String formattedCharSequenceToString(@NotNull FormattedCharSequence charSequence) {
        StringBuilder builder = new StringBuilder();
        charSequence.accept((index, style, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
        });
        return builder.toString();
    }
}
