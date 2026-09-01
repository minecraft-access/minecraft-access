package org.mcaccess.minecraftaccess.addon.worldnarrators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
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
import net.minecraft.world.level.block.FarmlandBlock;
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
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ListBackedContainer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.WorldNarrator;
import org.mcaccess.minecraftaccess.mixin.BaseSpawnerAccessor;
import org.mcaccess.minecraftaccess.mixin.WolfAccessor;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.i18n.Narratable;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;
import org.mcaccess.minecraftaccess.utils.position.Orientation;

@Slf4j
public class MinecraftAccess implements WorldNarrator {
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

    @Override
    public @NotNull HitResult rayCast() {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        return PlayerUtils.crosshairTarget(Math.min(player.blockInteractionRange(), player.entityInteractionRange()));
    }

    @Override
    public @Nullable String narrate(@NotNull HitResult rayCast) {
        return switch (rayCast()) {
            case BlockHitResult blockHitResult -> {
                String side = Config.getInstance().narrateCrosshair.narrateBlockFace
                        ? new Translation("minecraft_access.direction")
                                .variant(blockHitResult.getDirection().getName())
                                .getString()
                        : "";
                yield narrateBlock(blockHitResult.getBlockPos(), side).getString();
            }
            case EntityHitResult entityHitResult -> narrate(entityHitResult.getEntity());
            default -> throw new IllegalStateException("Unexpected value: " + rayCast());
        };
    }

    @Override
    public @NotNull String narrate(@NotNull BlockPos block) {
        return narrateBlock(block, "").getString();
    }

    @Override
    public @NotNull String narrate(@NotNull Entity entity) {
        // When the entity is named, this value is its custom name,
        // otherwise it is its type.
        String nameOrType = entity.getName().getString();
        boolean entityIsSitting = false;
        String type = entity.hasCustomName() ? new Translation.Vanilla(entity.getType().getDescriptionId()).getString() : nameOrType;
        boolean isDroppedItem = entity instanceof ItemEntity itemEntity && itemEntity.onGround()
                || entity instanceof AbstractArrow abstractArrow && abstractArrow.pickup == AbstractArrow.Pickup.ALLOWED;

        String variant = getVariantInfo(entity);
        if (!Strings.isBlank(variant)) {
            type = new Translation("minecraft_access.entity.variant")
                    .variable("variant").put(variant)
                    .variable("animal").put(type)
                    .getString();
        }

        // Add its type in front of its name if it has been renamed with name tag,
        // so even if there are two different types of entities that named the same name,
        // the mod can make the player tell the difference:
        // "Cat Neko", "Dog Neko"... where "Neko" is the entity's name and "Cat" or "Dog" is its type
        String text = entity.hasCustomName() ? type + ' ' + nameOrType : type;

        List<String> equipments = new ArrayList<>();

        if (Config.getInstance().narrateCrosshair.narrateAdditionalEntityPoses) {
            String poseVariant = switch (entity.getPose()) {
                case SLEEPING -> "sleeping";
                case DYING -> "dying";
                case DIGGING -> "digging";
                case FALL_FLYING -> "fall_flying";
                case ROARING -> "roaring";
                case SLIDING -> "sliding";
                case SWIMMING -> "swimming";
                case CROAKING -> "croaking";
                case EMERGING -> "emerging";
                case SHOOTING -> "shooting";
                case INHALING -> "inhaling";
                case SNIFFING -> "sniffing";
                case CROUCHING -> "crouching";
                case LONG_JUMPING -> "long_jumping";
                case USING_TONGUE -> "using_tongue";
                case SITTING -> {
                    entityIsSitting = true;
                    yield null;
                }
                case STANDING -> null;
                default -> {
                    log.warn("Unhandled pose found: {} for additional pose narration in Narration Utils", entity.getPose().name());
                    yield null;
                }
            };
            if (poseVariant != null) {
                text = new Translation("minecraft_access.read_crosshair.pose")
                        .variant(poseVariant)
                        .variable("entity").put(text)
                        .getString();
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
            text = new Translation("minecraft_access.read_crosshair.status").variant("tamed").variable("entity").put(text).getString();
        }

        if (entityIsSitting) {
            text = new Translation("minecraft_access.read_crosshair.status").variant("sitting").variable("entity").put(text).getString();
        }

        if (entity instanceof Mob mob && mob.isBaby()) {
            text = new Translation("minecraft_access.read_crosshair.status").variant("baby").variable("entity").put(text).getString();
        }

        if (entity instanceof Leashable leashable && leashable.isLeashed()) {
            text = new Translation("minecraft_access.read_crosshair.status").variant("leashed").variable("entity").put(text).getString();
        }

        text = switch (entity) {
            case Sheep sheep -> getSheepInfo(sheep, text);
            case ZombieVillager zombieVillager when zombieVillager.isConverting() ->
                    new Translation("minecraft_access.read_crosshair.zombie_villager.converting").variable("entity").put(text).getString();
            case Display.ItemDisplay itemDisplay when itemDisplay.itemRenderState() != null -> {
                @SuppressWarnings("DataFlowIssue")
                String itemName = itemDisplay.itemRenderState().itemStack().getItemName().getString();
                yield new Translation("minecraft_access.display_entity.item").variable("item").put(itemName).getString();
            }
            case Display.TextDisplay textDisplay when textDisplay.textRenderState() != null -> //noinspection DataFlowIssue
                    textDisplay.textRenderState().text().getString();
            case Display.BlockDisplay blockDisplay when blockDisplay.blockRenderState() != null -> {
                @SuppressWarnings("DataFlowIssue")
                Block ghostBlock = blockDisplay.blockRenderState().blockState().getBlock();
                yield new Translation("minecraft_access.display_entity.block").variable("block").put(ghostBlock.getName()).getString();
            }
            case ItemFrame frame -> {
                ItemStack item = frame.getItem();
                if (!item.isEmpty()) {
                    String itemName = item.getItemName().getString();
                    yield new Translation("minecraft_access.read_crosshair.equipped")
                            .variable("entity").put(text)
                            .variable("equipments").put(itemName)
                            .getString();
                }
                yield text;
            }
            default -> {
                if (isDroppedItem) {
                    yield new Translation("minecraft_access.read_crosshair.dropped_item").variable("item").put(text).getString();
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
            Translation.Delimited wordConnection = new Translation.Delimited();
            equipments.forEach(wordConnection::put);

            text = new Translation("minecraft_access.read_crosshair.equipped")
                    .variable("entity").put(text)
                    .variable("equipments").put(wordConnection)
                    .getString();
        }

        return text;
    }

    /**
     * Get variant text of wolf, cat, axolotl.
     */
    private static String getVariantInfo(Entity animal) {
        return switch (animal) {
            case Cat cat -> new Translation("minecraft_access.cat_variant")
                    .variant(cat.getVariant().unwrapKey()
                            .map(ResourceKey::identifier)
                            .map(Identifier::toShortLanguageKey)
                            .orElse("other"))
                    .getString();
            case Wolf wolf -> new Translation("minecraft_access.wolf_variant")
                    .variant(((WolfAccessor) wolf).callGetVariant().unwrapKey()
                            .map(ResourceKey::identifier)
                            .map(Identifier::toShortLanguageKey)
                            .orElse("other"))
                    .getString();
            case Axolotl axolotl -> new Translation("minecraft_access.axolotl_variant")
                    .variant(axolotl.getVariant().getName())
                    .getString();
            default -> "";
        };
    }

    private static String getSheepInfo(Sheep sheep, String currentQuery) {
        String color = new Translation.Vanilla("color.minecraft." + sheep.getColor().getName()).getString();
        String shearable = new Translation("minecraft_access.read_crosshair.shearable")
                .variant("not", !sheep.readyForShearing())
                .variable("entity").put(currentQuery)
                .getString();
        return color + ' ' + shearable;
    }

    /**
     * @param blockPos block position (in the client world)
     * @param side     if side is provided, then the invoker is ReadCrosshair
     * @return (narration, currentQuery):
     *      "narration" is the actual one to be narrated through Narrator,
     *      "currentQuery" is kind of shortened "narration" that is used for checking if target is changed compared to previous.
     */
    private static Narratable narrateBlock(BlockPos blockPos, String side) {
        Minecraft client = Minecraft.getInstance();

        assert client.level != null;
        BlockState blockState = client.level.getBlockState(blockPos);
        Block block = blockState.getBlock();
        BlockEntity blockEntity = client.level.getBlockEntity(blockPos);

        Narratable narration = new Translation.Delimited(' ')
                .put(block.getName())
                .putIfNotBlank(side);

        if (blockState.is(Blocks.WATER) || blockState.is(Blocks.LAVA)) {
            return narrateFluidBlock(blockPos);
        }

        if (blockEntity != null) {
            if (blockState.is(BlockTags.ALL_SIGNS)) {
                narration = getSignInfo((SignBlockEntity) blockEntity, client.player, narration);
            } else {
                switch (blockEntity) {
                    case BeehiveBlockEntity beehiveBlockEntity -> narration = getBeehiveInfo(beehiveBlockEntity, blockState, narration);
                    case SpawnerBlockEntity spawner -> {
                        Entity entity = ((BaseSpawnerAccessor) spawner.getSpawner()).getDisplayEntity();
                        String entityName = new Translation("minecraft_access.read_crosshair.spawner.empty").getString();
                        if (entity != null) {
                            entityName = Objects.requireNonNull(entity.getDisplayName()).getString();
                        }
                        narration = new Translation.Delimited(' ')
                                .put(entityName)
                                .put(narration);
                    }
                    case CampfireBlockEntity campfire -> narration = getVisibleItems(campfire.getItems(), narration);
                    case ListBackedContainer listBacked -> narration = getVisibleItems(listBacked.getItems(), narration);
                    default -> {
                    }
                }
            }
        }

        narration = getCropsInfo(block, blockState, narration);

        if (block instanceof FarmlandBlock && blockState.getValue(FarmlandBlock.MOISTURE) == FarmlandBlock.MAX_MOISTURE) {
            narration = new Translation("minecraft_access.block.farmland.wet").variable("block").put(narration);
        } else if (block instanceof EndPortalFrameBlock) {
            if (blockState.getValue(EndPortalFrameBlock.HAS_EYE)) {
                narration = new Translation("minecraft_access.block.end_portal_frame")
                        .variant("filled")
                        .variable("block").put(narration);
            } else {
                narration = new Translation("minecraft_access.block.end_portal_frame")
                        .variant("empty")
                        .variable("block").put(narration);
            }
        }

        narration = getRedstoneRelatedInfo(client.level, blockPos, block, blockState, narration);

        if (client.level.getFluidState(blockPos).is(Fluids.WATER)) {
            narration = new Translation("minecraft_access.crop.water_logged")
                    .variable("block").put(narration);
        }

        return narration;
    }

    private static Narratable getSignInfo(SignBlockEntity signEntity, LocalPlayer player, Narratable narration) {
        String[] lines = new String[4];

        for (int i = 0; i < 4; i++) {
            lines[i] = signEntity.getText(signEntity.isFacingFrontText(player)).getMessage(i, false).getString();
        }
        String content = String.join(", ", lines);
        return new Translation("minecraft_access.block.sign.content")
                .variant(signEntity.isFacingFrontText(player) ? "front" : "back")
                .variable("sign").put(narration)
                .variable("content").put(content);
    }

    private static Narratable getVisibleItems(List<ItemStack> itemList, Narratable narration) {
        Translation.Delimited items = new Translation.Delimited();
        for (ItemStack item : itemList) {
            if (!item.isEmpty()) {
                items.put(item.getItemName());
            }
        }
        if (!items.isEmpty()) {
            return new Translation("minecraft_access.read_crosshair.equipped")
                    .variable("entity").put(narration)
                    .variable("equipments").put(items);
        }
        return narration;
    }

    private static @NotNull Narratable getRedstoneRelatedInfo(
            ClientLevel world,
            BlockPos blockPos,
            Block block,
            BlockState blockState,
            Narratable currentNarration
    ) {
        boolean isEmittingPower = world.hasSignal(blockPos, Direction.DOWN);
        boolean isReceivingPower = world.hasNeighborSignal(blockPos);

        return switch (block) {
            case PistonBaseBlock _ -> {
                String facing = blockState.getValue(PistonBaseBlock.FACING).getName();
                Translation narration = new Translation("minecraft_access.block.property.facing")
                        .variable("block").put(currentNarration)
                        .variable("direction").put(new Translation("minecraft_access.direction").variant(facing));
                if (isReceivingPower) {
                    narration = new Translation("minecraft_access.block.property.powered").variable("block").put(narration);
                }
                yield narration;
            }
            case RedStoneWireBlock _ -> getRedstoneWireInfo(blockState, blockPos, currentNarration);
            case HopperBlock _ -> {
                String facing = blockState.getValue(HopperBlock.FACING).getName();
                Translation narration = new Translation("minecraft_access.block.property.facing")
                        .variable("block").put(currentNarration)
                        .variable("direction").put(new Translation("minecraft_access.direction").variant(facing));
                if (isReceivingPower) {
                    narration = new Translation("minecraft_access.block.property.locked").variable("block").put(narration);
                }
                yield narration;
            }
            case ObserverBlock _ -> {
                String facing = blockState.getValue(ObserverBlock.FACING).getName();
                Translation narration = new Translation("minecraft_access.block.property.facing")
                        .variable("block").put(currentNarration)
                        .variable("direction").put(new Translation("minecraft_access.direction").variant(facing));
                if (isEmittingPower) {
                    narration = new Translation("minecraft_access.block.property.powered").variable("block").put(narration);
                }
                yield narration;
            }
            case DispenserBlock _ -> {
                String facing = blockState.getValue(DispenserBlock.FACING).getName();
                Translation narration = new Translation("minecraft_access.block.property.facing")
                        .variable("block").put(currentNarration)
                        .variable("direction").put(new Translation("minecraft_access.direction").variant(facing));
                if (isReceivingPower) {
                    narration = new Translation("minecraft_access.block.property.powered").variable("block").put(narration);
                }
                yield narration;
            }
            case ComparatorBlock _ -> {
                ComparatorMode mode = blockState.getValue(ComparatorBlock.MODE);
                Direction facing = blockState.getValue(ComparatorBlock.FACING);
                Translation narration = new Translation("minecraft_access.block.comparator")
                        .variable("block").put(currentNarration)
                        .variable("direction").put(new Translation("minecraft_access.direction")
                                .variant(Orientation.getOppositeDirectionKey(facing.getName()).toLowerCase()))
                        .variable("mode").put(mode.toString());
                if (isReceivingPower) {
                    narration = new Translation("minecraft_access.block.property.powered").variable("block").put(narration);
                }
                yield narration;
            }
            case RepeaterBlock _ -> {
                boolean locked = blockState.getValue(RepeaterBlock.LOCKED);
                int delay = blockState.getValue(RepeaterBlock.DELAY);
                Direction facing = blockState.getValue(RepeaterBlock.FACING);
                Translation narration = new Translation("minecraft_access.block.repeater")
                        .variable("block").put(currentNarration)
                        .variable("direction").put(new Translation("minecraft_access.direction")
                                .variant(Orientation.getOppositeDirectionKey(facing.getName()).toLowerCase()))
                        .variable("delay").put(delay);
                if (locked) {
                    narration = new Translation("minecraft_access.block.property.locked").variable("block").put(narration);
                }
                yield narration;
            }
            default -> {
                if ((block instanceof GlowLichenBlock || block instanceof RedstoneLampBlock) && (isReceivingPower || isEmittingPower)) {
                    yield new Translation("minecraft_access.read_crosshair.powered").variable("block").put(currentNarration);
                }
                if ((block instanceof RedstoneTorchBlock || block instanceof LeverBlock || block instanceof ButtonBlock) && isEmittingPower) {
                    yield new Translation("minecraft_access.read_crosshair.powered").variable("block").put(currentNarration);
                }
                if (block instanceof DoorBlock doorBlock && doorBlock.isOpen(blockState)
                        || block instanceof FenceGateBlock && blockState.getValue(FenceGateBlock.OPEN)) {
                    yield new Translation("minecraft_access.read_crosshair.opened").variable("block").put(currentNarration);
                }
                if (isReceivingPower) {
                    yield new Translation("minecraft_access.read_crosshair.powered").variable("block").put(currentNarration);
                }
                yield currentNarration;
            }
        };
    }

    private static @NotNull Narratable getRedstoneWireInfo(BlockState blockState, BlockPos pos, Narratable currentNarration) {
        Narratable narration = currentNarration;
        int powerLevel = blockState.getValue(RedStoneWireBlock.POWER);
        if (powerLevel > 0) {
            narration = new Translation("minecraft_access.block.redstone_wire.power")
                    .variable("block").put(narration)
                    .variable("power").put(powerLevel);
        }

        List<String> connectedDirections = Direction.Plane.HORIZONTAL.stream().map(direction -> {
            String directionName = new Translation("minecraft_access.direction").variant(direction.getName()).getString();

            switch (blockState.getValue(RedStoneWireBlock.PROPERTY_BY_DIRECTION.get(direction))) {
                case UP -> {
                    return directionName + ' ' + new Translation("minecraft_access.direction").variant("up").getString();
                }
                case SIDE -> {
                    return directionName;
                }
                default -> {
                    return null;
                }
            }
        }).filter(Objects::nonNull).toList();

        // Unconnected redstone dust now has all direction block states set to "side" since 20w18a (before 1.16)
        // https://minecraft.wiki/w/Redstone_Dust
        // So here is an additional check to see if the redstone wire is really connected to all directions
        if (connectedDirections.size() == 4) {
            // If two redstone wires are connected, they're at one of three relative positions: [side, side down, side up].
            // Take one sample relative position (x+1) then check if any block at [-1,0,1] height is also redstone wire.
            boolean result = BlockPos.betweenClosedStream(pos.offset(1, -1, 0), pos.offset(1, 1, 0))
                    .anyMatch(blockPos -> {
                        assert Minecraft.getInstance().level != null;
                        return Minecraft.getInstance().level.getBlockState(blockPos).getBlock() instanceof RedStoneWireBlock;
                    });
            // If there's no redstone wire on x+1 side,
            // then current wire is not connected to that side,
            // so it's not connected to all directions.
            if (!result) return narration;
        }

        Translation.Delimited directionsNarration = new Translation.Delimited();
        connectedDirections.forEach(directionsNarration::put);
        return new Translation("minecraft_access.block.redstone_wire.connection")
                .variable("block").put(narration)
                .variable("connections").put(directionsNarration);
    }

    private static @NotNull Narratable getBeehiveInfo(BeehiveBlockEntity blockEntity, BlockState blockState, Narratable currentNarration) {
        Narratable narration = currentNarration;
        boolean isSmoked = blockEntity.isSedated();
        int honeyLevel = blockState.getValue(BeehiveBlock.HONEY_LEVEL);
        Direction facingDirection = blockState.getValue(BeehiveBlock.FACING);

        if (isSmoked) {
            narration = new Translation("minecraft_access.block.beehive.smoked")
                    .variable("block").put(narration);
        }

        if (honeyLevel > 0) {
            narration = new Translation("minecraft_access.block.beehive.honey_level")
                    .variable("block").put(narration)
                    .variable("level").put(honeyLevel);
        }

        narration = new Translation("minecraft_access.block.beehive.facing")
                .variable("block").put(narration)
                .variable("direction").put(facingDirection.getName());

        return narration;
    }

    private static @NotNull Narratable getCropsInfo(Block block, BlockState blockState, Narratable narration) {
        if (block instanceof CropBlock crop) {
            return addCropGrowth(narration, crop.getAge(blockState), crop.getMaxAge());
        }

        // There are some growable blocks that are not crop blocks like the Torch Flower crop
        if (block instanceof BonemealableBlock || block instanceof VegetationBlock) {
            Optional<Map.Entry<IntegerProperty, Integer>> ageProperty = CROP_AGE_PROPERTIES.entrySet().stream()
                    .filter(entry -> blockState.hasProperty(entry.getKey()))
                    .findFirst();

            if (ageProperty.isPresent()) {
                return addCropGrowth(narration, blockState.getValue(ageProperty.get().getKey()), ageProperty.get().getValue());
            }
        }

        // No growth information found
        return narration;
    }

    private static @NotNull Narratable addCropGrowth(Narratable narration, int age, int maxAge) {
        if (age == maxAge) {
            return new Translation("minecraft_access.crop.mature").variable("crop").put(narration);
        }
        float growth = (float) age / maxAge;
        return new Translation("minecraft_access.crop.percent")
                .variable("crop").put(narration)
                .variable("percent").put((int) (growth * 100));
    }

    /**
     * @param pos fluid position (in the client world)
     * @return (narration, currentQuery):
     *      "narration" is the actual one to be narrated through Narrator,
     *      "currentQuery" is kind of shortened "narration" that is used for checking if target is changed compared to previous.
     */
    private static Narratable narrateFluidBlock(BlockPos pos) {
        assert Minecraft.getInstance().level != null;
        FluidState fluidState = Minecraft.getInstance().level.getFluidState(pos);
        int level = fluidState.getAmount();
        String levelString = level < 8 ? new Translation("minecraft_access.block.fluid.level").variable("level").put(level).getString() : "";
        return new Translation.Delimited(' ')
                .put("block", fluidState.typeHolder())
                .put(levelString);
    }
}
