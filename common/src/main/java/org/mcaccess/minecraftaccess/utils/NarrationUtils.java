package org.mcaccess.minecraftaccess.utils;

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
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.mcaccess.minecraftaccess.config.config_maps.ReadCrosshairConfigMap;
import org.mcaccess.minecraftaccess.mixin.BaseSpawnerAccessor;
import org.mcaccess.minecraftaccess.utils.position.Orientation;

import java.text.DecimalFormat;
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
    public static final Predicate<BlockState> IS_REDSTONE_WIRE = (BlockState state) -> state.getBlock() instanceof RedStoneWireBlock;

    public static String narrateEntity(Entity entity) {
        // When the entity is named, this value is its custom name,
        // otherwise it is its type.
        String nameOrType = entity.getName().getString();
        boolean entityIsSitting = false;
        String type = entity.hasCustomName() ? I18n.get(entity.getType().getDescriptionId()) : nameOrType;
        boolean isDroppedItem = entity instanceof ItemEntity itemEntity && itemEntity.onGround() || entity instanceof AbstractArrow abstractArrow && abstractArrow.pickup.equals(AbstractArrow.Pickup.ALLOWED);

        String variant = getVariantInfo(entity);
        if (!Strings.isBlank(variant)) {
            Map<String, String> map = Map.of("variant", variant, "animal", type);
            type = I18n.get("minecraft_access.other.animal_variant_format", map);
        }

        // Add its type in front of its name if it has been renamed with name tag,
        // so even if there are two different types of entities that named the same name,
        // the mod can make the player tell the difference:
        // "Cat Neko", "Dog Neko"... where "Neko" is the entity's name and "Cat" or "Dog" is its type
        String text = entity.hasCustomName() ? type + " " + nameOrType : type;

        List<String> equipments = new ArrayList<>();

        if (ReadCrosshairConfigMap.getInstance().isSpeakAdditionalEntityPoses()) {
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
                default -> {
                    log.warn("Unhandled pose found: {} for additional pose narration in Narration Utils", entity.getPose().name());
                }
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

        if(entity instanceof TamableAnimal tamableAnimal &&tamableAnimal.isTame())
    text =I18n.get("minecraft_access.read_crosshair.tamed",text);

        if(entityIsSitting)
    text =I18n.get("minecraft_access.read_crosshair.sitting",text);

        if(entity instanceof
    Mob mob &&mob.isBaby())
    text =I18n.get("minecraft_access.read_crosshair.baby",text);

        if(entity instanceof
    Leashable leashable &&leashable.isLeashed())
    text =I18n.get("minecraft_access.read_crosshair.leashed",text);

        if(entity instanceof
    Sheep sheep)

    {
        text = getSheepInfo(sheep, text);
    } else if(entity instanceof
    ZombieVillager zombieVillager &&zombieVillager.isConverting())

    {
        text = I18n.get("minecraft_access.read_crosshair.zombie_villager_is_curing", text);
    } else if(isDroppedItem)

    {
        text = I18n.get("minecraft_access.point_of_interest.locking.dropped_item", text);
    }

        if(entity instanceof
    LivingEntity livingEntity)

    {
        for (ItemStack equipment : livingEntity.getAllSlots()) {
            if (equipment.isEmpty())
                continue;
            String equipmentName = equipment.getHoverName().getString();
            equipments.add(equipmentName);
        }
    }

        return text;
}

/**
 * Get variant text of wolf, cat, axolotl
 */
private static String getVariantInfo(Entity animal) {
    return switch (animal) {
        case Cat cat ->
                I18n.get(String.format("minecraft_access.cat_variant.%s", cat.getVariant().unwrapKey().map(ResourceKey::location).map(ResourceLocation::toShortLanguageKey).orElse("other")));
        case Wolf wolf ->
                I18n.get(String.format("minecraft_access.wolf_variant.%s", wolf.getVariant().unwrapKey().map(ResourceKey::location).map(ResourceLocation::toShortLanguageKey).orElse("other")));
        case Axolotl axolotl -> I18n.get("minecraft_access.axolotl_variant." + axolotl.getVariant().getName());
        default -> "";
    };
}

private static String addSittingInfo(String currentQuery) {
    return I18n.get("minecraft_access.read_crosshair.is_sitting", currentQuery);
}

private static String getSheepInfo(Sheep sheep, String currentQuery) {
    String dyedColor = sheep.getColor().getName();
    String translatedColor = I18n.get("color.minecraft." + dyedColor);
    String shearable = sheep.readyForShearing() ?
            I18n.get("minecraft_access.read_crosshair.shearable", currentQuery) :
            I18n.get("minecraft_access.read_crosshair.not_shearable", currentQuery);
    return translatedColor + " " + shearable;
}

    public static String narrateNumber(double d) {
        DecimalFormat df = new DecimalFormat();
        return d >= 0 ? String.valueOf(df.format(d)) : I18n.get("minecraft_access.other.negative", df.format(-d));
    }

public static String narrateRelativePositionOfPlayerAnd(BlockPos blockPos) {
    Minecraft minecraftClient = Minecraft.getInstance();
    if (minecraftClient == null) return "up";
    if (minecraftClient.player == null) return "up";

    Direction dir = minecraftClient.player.getDirection();

//        Vec3 diff = minecraftClient.player.getEyePosition().subtract(Vec3.ofCenter(blockPos)); // post 1.18
    Vec3 diff = new Vec3(minecraftClient.player.getX(), minecraftClient.player.getEyeY(), minecraftClient.player.getZ()).subtract(Vec3.atCenterOf(blockPos)); // pre 1.18
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
    return I18n.get("minecraft_access.util.position_difference_" + (blocks < 0 ? key1 : key2), Math.abs(blocks));
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
public static Tuple<String, String> narrateBlockForContentChecking(BlockPos pos, String side) {
    Minecraft client = Minecraft.getInstance();
    if (Objects.isNull(client)) return new Tuple<>("", "");
    ClientLevel clientWorld = client.level;
    if (clientWorld == null) return new Tuple<>("", "");

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
    if (blockState.is(Blocks.WATER) || blockState.is(Blocks.LAVA)) {
        toSpeak = NarrationUtils.narrateFluidBlock(blockPos);
        return new Tuple<>(toSpeak, toSpeak);
    }

    if (blockEntity != null) {
        if (blockState.is(BlockTags.ALL_SIGNS)) {
            toSpeak = getSignInfo((SignBlockEntity) blockEntity, client.player, toSpeak);
        } else if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
            Tuple<String, String> beehiveInfo = getBeehiveInfo(beehiveBlockEntity, blockState, toSpeak, currentQuery);
            toSpeak = beehiveInfo.getA();
            currentQuery = beehiveInfo.getB();
        } else
            // Speak monster spawner mob type
            if (blockEntity instanceof SpawnerBlockEntity spawner) {
                // Will not support non-vanilla custom configured multiple-mob spawner (like generated with command)
                Entity entity = ((BaseSpawnerAccessor) spawner.getSpawner()).getDisplayEntity();
                // Monster spawners that are gotten from the creative inventory are empty.
                String entityName = I18n.get("minecraft_access.read_crosshair.spawner_empty");
                if (entity != null) {
                    entityName = Objects.requireNonNull(entity.getDisplayName()).getString();
                }
                toSpeak = entityName + " " + toSpeak;
                currentQuery = entityName + currentQuery;
            }
    }

    if (block instanceof BushBlock || block instanceof CocoaBlock) {
        Tuple<String, String> cropsInfo = getCropsInfo(block, blockState, toSpeak, currentQuery);
        toSpeak = cropsInfo.getA();
        currentQuery = cropsInfo.getB();
    } else if (block instanceof FarmBlock && blockState.getValue(FarmBlock.MOISTURE) == FarmBlock.MAX_MOISTURE) {
        toSpeak = I18n.get("minecraft_access.crop.wet_farmland", toSpeak);
        currentQuery = "wet" + currentQuery;
    } else if (block instanceof EndPortalFrameBlock endPortalFrame) {
        if (blockState.getValue(EndPortalFrameBlock.HAS_EYE)) {
            toSpeak = I18n.get("minecraft_access.read_crosshair.end_portal_frame_with_eye", toSpeak);
        } else {
            toSpeak = I18n.get("minecraft_access.read_crosshair.end_portal_frame_empty", toSpeak);
        }
    }

    // Redstone related
    Tuple<String, String> redstoneRelatedInfo = getRedstoneRelatedInfo(clientWorld, blockPos, block, blockState, toSpeak, currentQuery);
    toSpeak = redstoneRelatedInfo.getA();
    currentQuery = redstoneRelatedInfo.getB();

    if (clientWorld.getFluidState(blockPos).is(Fluids.WATER)) {
        toSpeak = I18n.get("minecraft_access.crop.water_logged", toSpeak);
        currentQuery = "waterlogged" + currentQuery;
    }

    return new Tuple<>(toSpeak, currentQuery);
}

/**
 * @param pos  block position (in the client world)
 * @param side if side is provided, then the invoker is ReadCrosshair
 */
public static String narrateBlock(BlockPos pos, String side) {
    return narrateBlockForContentChecking(pos, side).getA();
}

private static String getSignInfo(SignBlockEntity signEntity, LocalPlayer
        player, String toSpeak) {
    String[] lines = new String[4];

    for (int i = 0; i < 4; i++) {
//            lines[i] = signEntity.getTextOnRow(i, false).getString(); // Pre 1.20.x
        lines[i] = signEntity.getText(signEntity.isFacingFrontText(player)).getMessage(i, false).getString();
    }
    String content = String.join(", ", lines);
    return I18n.get("minecraft_access.read_crosshair.sign_" + (signEntity.isFacingFrontText(player) ? "front" : "back") + "_content", toSpeak, content);
}

private static @NotNull Tuple<String, String> getRedstoneRelatedInfo(ClientLevel
                                                                             world, BlockPos blockPos, Block block, BlockState blockState, String toSpeak, String
                                                                             currentQuery) {
    boolean isEmittingPower = world.hasSignal(blockPos, Direction.DOWN);
    boolean isReceivingPower = world.hasNeighborSignal(blockPos);

    if (block instanceof PistonBaseBlock) {
        String facing = blockState.getValue(PistonBaseBlock.FACING).getName();
        toSpeak = I18n.get("minecraft_access.read_crosshair.facing", toSpeak, I18n.get("minecraft_access.direction." + facing));
        currentQuery += "facing " + facing;
        if (isReceivingPower) {
            toSpeak = I18n.get("minecraft_access.read_crosshair.powered", toSpeak);
            currentQuery += "powered";
        }
    } else if ((block instanceof GlowLichenBlock || block instanceof RedstoneLampBlock) && (isReceivingPower || isEmittingPower)) {
        toSpeak = I18n.get("minecraft_access.read_crosshair.powered", toSpeak);
        currentQuery += "powered";
//        } else if ((block instanceof RedstoneTorchBlock || block instanceof LeverBlock || block instanceof AbstractButtonBlock) && isEmittingPower) { // pre 1.19.3
    } else if (block instanceof RedStoneWireBlock) {
        Tuple<String, String> p = getRedstoneWireInfo(blockState, blockPos, toSpeak, currentQuery);
        toSpeak = p.getA();
        currentQuery = p.getB();
    } else if ((block instanceof RedstoneTorchBlock || block instanceof LeverBlock || block instanceof ButtonBlock) && isEmittingPower) { // From 1.19.3
        toSpeak = I18n.get("minecraft_access.read_crosshair.powered", toSpeak);
        currentQuery += "powered";
    } else if (block instanceof DoorBlock doorBlock && doorBlock.isOpen(blockState)) {
        toSpeak = I18n.get("minecraft_access.read_crosshair.opened", toSpeak);
        currentQuery += "open";
    } else if (block instanceof HopperBlock) {
        toSpeak = I18n.get("minecraft_access.read_crosshair.facing", toSpeak, I18n.get("minecraft_access.direction." + blockState.getValue(HopperBlock.FACING).getName()));
        currentQuery += "facing " + blockState.getValue(HopperBlock.FACING).getName();
        if (isReceivingPower) {
            toSpeak = I18n.get("minecraft_access.read_crosshair.locked", toSpeak);
            currentQuery += "locked";
        }
    } else if (block instanceof ObserverBlock) {
        toSpeak = I18n.get("minecraft_access.read_crosshair.facing", toSpeak, I18n.get("minecraft_access.direction." + blockState.getValue(ObserverBlock.FACING).getName()));
        currentQuery += "facing " + blockState.getValue(ObserverBlock.FACING).getName();
        if (isEmittingPower) {
            toSpeak = I18n.get("minecraft_access.read_crosshair.powered", toSpeak);
            currentQuery += "powered";
        }
    } else if (block instanceof DispenserBlock) {
        toSpeak = I18n.get("minecraft_access.read_crosshair.facing", toSpeak, I18n.get("minecraft_access.direction." + blockState.getValue(DispenserBlock.FACING).getName()));
        currentQuery += "facing " + blockState.getValue(DispenserBlock.FACING).getName();
        if (isReceivingPower) {
            toSpeak = I18n.get("minecraft_access.read_crosshair.powered", toSpeak);
            currentQuery += "powered";
        }
    } else if (block instanceof ComparatorBlock) {
        ComparatorMode mode = blockState.getValue(ComparatorBlock.MODE);
        Direction facing = blockState.getValue(ComparatorBlock.FACING);
        String correctFacing = I18n.get("minecraft_access.direction." + Orientation.getOppositeDirectionKey(facing.getName()).toLowerCase());
        toSpeak = I18n.get("minecraft_access.read_crosshair.comparator_info", toSpeak, correctFacing, mode);
        if (isReceivingPower) {
            toSpeak = I18n.get("minecraft_access.read_crosshair.powered", toSpeak);
            currentQuery += "powered";
        }
        currentQuery += "mode:" + mode + " facing:" + correctFacing;
    } else if (block instanceof RepeaterBlock) {
        boolean locked = blockState.getValue(RepeaterBlock.LOCKED);
        int delay = blockState.getValue(RepeaterBlock.DELAY);
        Direction facing = blockState.getValue(ComparatorBlock.FACING);
        String correctFacing = I18n.get("minecraft_access.direction." + Orientation.getOppositeDirectionKey(facing.getName()).toLowerCase());

        toSpeak = I18n.get("minecraft_access.read_crosshair.repeater_info", toSpeak, correctFacing, delay);
        currentQuery += "delay:" + delay + " facing:" + correctFacing;
        if (locked) {
            toSpeak = I18n.get("minecraft_access.read_crosshair.locked", toSpeak);
            currentQuery += "locked";
        }
    } else if (isReceivingPower) { // For all the other blocks
        toSpeak = I18n.get("minecraft_access.read_crosshair.powered", toSpeak);
        currentQuery += "powered";
    }

    return new Tuple<>(toSpeak, currentQuery);
}

private static @NotNull Tuple<String, String> getRedstoneWireInfo(BlockState
                                                                          blockState, BlockPos pos, String toSpeak, String currentQuery) {
    int powerLevel = blockState.getValue(RedStoneWireBlock.POWER);
    if (powerLevel > 0) {
        toSpeak = I18n.get("minecraft_access.read_crosshair.redstone_wire_power", toSpeak, powerLevel);
        currentQuery += "power level " + powerLevel;
    }

    List<String> connectedDirections = Direction.Plane.HORIZONTAL.stream()
            .map(direction -> {
                String directionName = I18n.get("minecraft_access.direction." + direction.getName());

                switch (blockState.getValue(RedStoneWireBlock.PROPERTY_BY_DIRECTION.get(direction))) {
                    case UP -> {
                        return directionName + " " + I18n.get("minecraft_access.direction.up");
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
        Iterable<BlockPos> threePosAtSide = BlockPos.betweenClosed(pos.offset(1, -1, 0), pos.offset(1, 1, 0));
        boolean result = WorldUtils.checkAnyOfBlocks(threePosAtSide, IS_REDSTONE_WIRE);
        // If there's no redstone wire on x+1 side,
        // then current wire is not connected to that side,
        // so it's not connected to all directions.
        if (!result) return new Tuple<>(toSpeak, currentQuery);
    }

    String directionsToSpeak = String.join(I18n.get("minecraft_access.other.words_connection"), connectedDirections);
    toSpeak = I18n.get("minecraft_access.read_crosshair.redstone_wire_connection", toSpeak, directionsToSpeak);
    currentQuery += "connected to " + connectedDirections;

    return new Tuple<>(toSpeak, currentQuery);
}

private static @NotNull Tuple<String, String> getBeehiveInfo
        (BeehiveBlockEntity blockEntity, BlockState blockState, String
                toSpeak, String currentQuery) {
    boolean isSmoked = blockEntity.isSedated();
    int honeyLevel = blockState.getValue(BeehiveBlock.HONEY_LEVEL);
    Direction facingDirection = blockState.getValue(BeehiveBlock.FACING);

    if (isSmoked) {
        toSpeak = I18n.get("minecraft_access.read_crosshair.bee_hive_smoked", toSpeak);
        currentQuery += "smoked";
    }

    if (honeyLevel > 0) {
        toSpeak = I18n.get("minecraft_access.read_crosshair.bee_hive_honey_level", toSpeak, honeyLevel);
        currentQuery += ("honey-level:" + honeyLevel);
    }

    toSpeak = I18n.get("minecraft_access.read_crosshair.bee_hive_facing", toSpeak, facingDirection.getName());
    currentQuery += ("facing:" + facingDirection.getName());

    return new Tuple<>(toSpeak, currentQuery);
}

/**
 * Blocks that can be planted and have growing stages (age) and harvestable.<br>
 * Including wheat, carrot, potato, beetroot, nether wart, cocoa bean,
 * torch flower, pitcher crop.<br>
 * Watermelon vein and pumpkin vein are not harvestable so not be included here.
 */
private static @NotNull Tuple<String, String> getCropsInfo(Block
                                                                   block, BlockState blockState, String toSpeak, String currentQuery) {
    int currentAge, maxAge;

    switch (block) {
        case CropBlock ignored -> {
            if (block instanceof BeetrootBlock) {
                // Beetroot have a different max_age of 3
                currentAge = blockState.getValue(BeetrootBlock.AGE);
                maxAge = BeetrootBlock.MAX_AGE;
            } else if (block instanceof TorchflowerCropBlock) {
                currentAge = blockState.getValue(TorchflowerCropBlock.AGE);
                maxAge = TorchflowerCropBlock.MAX_AGE;
            } else {
                // While wheat, carrots, and potatoes have max_age of 7
                currentAge = blockState.getValue(CropBlock.AGE);
                maxAge = CropBlock.MAX_AGE;
            }
        }
        case CocoaBlock ignored -> {
            currentAge = blockState.getValue(CocoaBlock.AGE);
            maxAge = CocoaBlock.MAX_AGE;
        }
        case NetherWartBlock ignored -> {
            currentAge = blockState.getValue(NetherWartBlock.AGE);
            maxAge = NetherWartBlock.MAX_AGE;
        }
        case PitcherCropBlock ignored -> {
            currentAge = blockState.getValue(PitcherCropBlock.AGE);
            maxAge = PitcherCropBlock.MAX_AGE;
        }
        case null, default -> {
            return new Tuple<>(toSpeak, currentQuery);
        }
    }

    String configKey = checkCropRipeLevel(currentAge, maxAge);
    return new Tuple<>(I18n.get(configKey, toSpeak), I18n.get(configKey, currentQuery));
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
    String name = getFluidI18NName(fluidState.holder());
    int level = fluidState.getAmount();
    String levelString = level < 8 ? I18n.get("minecraft_access.read_crosshair.fluid_level", level) : "";
    return name + " " + levelString;
}

private static String getFluidI18NName(Holder<Fluid> fluid) {
    String translationKey = fluid.unwrap().map(
            (fluidKey) -> "block." + fluidKey.location().getNamespace() + "." + fluidKey.location().getPath(),
            (fluidValue) -> "[unregistered " + fluidValue + "]"
    );
    return I18n.get(translationKey);
}
        }
