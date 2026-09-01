package org.mcaccess.minecraftaccess.addon.worldnarrators;

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
import net.minecraft.locale.Language;
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
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
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
                        ? I18n.get(String.format("minecraft_access.direction.%s", blockHitResult.getDirection().getName()))
                        : "";
                yield narrateBlock(blockHitResult.getBlockPos(), side);
            }
            case EntityHitResult entityHitResult -> narrate(entityHitResult.getEntity());
            default -> throw new IllegalStateException("Unexpected value: " + rayCast());
        };
    }

    @Override
    public @NotNull String narrate(@NotNull BlockPos block) {
        return narrateBlock(block, "");
    }

    @Override
    public @NotNull String narrate(@NotNull Entity entity) {
        // When the entity is named, this value is its custom name,
        // otherwise it is its type.
        String nameOrType = entity.getName().getString();
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
            String poseName = entity.getPose().getSerializedName().toLowerCase();
            if (!Objects.equals(poseName, "standing") && !Objects.equals(poseName, "sitting")) {
                String poseKey = "minecraft_access.read_crosshair." + poseName;
                if (Language.getInstance().has(poseKey)) {
                    text = I18n.get(poseKey, text);
                } else {
                    log.warn("Unhandled pose found: {} for additional pose narration in Narration Utils", poseName);
                }
            }
        }

        boolean entityIsSitting = switch (entity) {
            case Fox fox -> fox.isSitting();
            case Panda panda -> panda.isSitting();
            case Camel camel -> camel.isCamelSitting();
            case TamableAnimal tamableAnimal -> tamableAnimal.isInSittingPose();
            default -> Objects.equals(entity.getPose().getSerializedName().toLowerCase(), "sitting");
        };

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
            case Display.TextDisplay textDisplay when textDisplay.textRenderState() != null -> //noinspection DataFlowIssue
                    textDisplay.textRenderState().text().getString();
            case Display.BlockDisplay blockDisplay when blockDisplay.blockRenderState() != null -> {
                @SuppressWarnings("DataFlowIssue")
                Block ghostBlock = blockDisplay.blockRenderState().blockState().getBlock();
                yield I18n.get("minecraft_access.point_of_interest.locking.display_block", ghostBlock.getName().getString());
            }
            case ItemFrame frame -> {
                ItemStack item = frame.getItem();
                if (!item.isEmpty()) {
                    String itemName = item.getItemName().getString();
                    yield I18n.get("minecraft_access.other.entity_with_equipments", Map.of("entity", text, "equipments", itemName));
                }
                yield text;
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

            if (entity instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame()) {
                if (entity instanceof Cat cat) {
                    equipments.add(I18n.get("minecraft_access.read_crosshair.collar", I18n.get("color.minecraft." + cat.getCollarColor().getName())));
                } else if (entity instanceof Wolf wolf) {
                    equipments.add(I18n.get("minecraft_access.read_crosshair.collar", I18n.get("color.minecraft." + wolf.getCollarColor().getName())));
                }
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
     * Get variant text of wolf, cat, axolotl.
     */
    private static String getVariantInfo(Entity animal) {
        return switch (animal) {
            case Cat cat -> I18n.get(String.format("minecraft_access.cat_variant.%s",
                    cat.getVariant().unwrapKey()
                            .map(ResourceKey::identifier)
                            .map(Identifier::toShortLanguageKey)
                            .orElse("other")
            ));
            case Wolf wolf -> I18n.get(String.format("minecraft_access.wolf_variant.%s",
                    ((WolfAccessor) wolf).callGetVariant().unwrapKey()
                            .map(ResourceKey::identifier)
                            .map(Identifier::toShortLanguageKey)
                            .orElse("other")
            ));
            case Axolotl axolotl -> I18n.get(String.format("minecraft_access.axolotl_variant.%s", axolotl.getVariant().getName()));
            default -> "";
        };
    }

    private static String getSheepInfo(Sheep sheep, String currentQuery) {
        String color = I18n.get("color.minecraft." + sheep.getColor().getName());
        String shearable = I18n.get(
                sheep.readyForShearing() ? "minecraft_access.read_crosshair.shearable" : "minecraft_access.read_crosshair.not_shearable",
                currentQuery
        );
        return color + ' ' + shearable;
    }

    /**
     * @param blockPos block position (in the client world)
     * @param side     if side is provided, then the invoker is ReadCrosshair
     * @return (narration, currentQuery):
     *      "narration" is the actual one to be narrated through Narrator,
     *      "currentQuery" is kind of shortened "narration" that is used for checking if target is changed compared to previous.
     */
    private static String narrateBlock(BlockPos blockPos, String side) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return "";

        BlockState blockState = client.level.getBlockState(blockPos);
        Block block = blockState.getBlock();
        BlockEntity blockEntity = client.level.getBlockEntity(blockPos);

        String name = block.getName().getString();
        String narration = Strings.isBlank(side) ? name : name + ' ' + side;

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
                        String entityName = I18n.get("minecraft_access.read_crosshair.spawner_empty");
                        if (entity != null) {
                            entityName = Objects.requireNonNull(entity.getDisplayName()).getString();
                        }
                        narration = entityName + ' ' + narration;
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
            narration = I18n.get("minecraft_access.crop.wet_farmland", narration);
        } else if (block instanceof EndPortalFrameBlock) {
            if (blockState.getValue(EndPortalFrameBlock.HAS_EYE)) {
                narration = I18n.get("minecraft_access.read_crosshair.end_portal_frame_with_eye", narration);
            } else {
                narration = I18n.get("minecraft_access.read_crosshair.end_portal_frame_empty", narration);
            }
        }

        narration = getRedstoneRelatedInfo(client.level, blockPos, block, blockState, narration);

        if (client.level.getFluidState(blockPos).is(Fluids.WATER)) {
            narration = I18n.get("minecraft_access.crop.water_logged", narration);
        }

        return narration;
    }

    private static String getSignInfo(SignBlockEntity signEntity, LocalPlayer player, String narration) {
        String[] lines = new String[4];

        for (int i = 0; i < 4; i++) {
            lines[i] = signEntity.getText(signEntity.isFacingFrontText(player)).getMessage(i, false).getString();
        }
        String content = String.join(", ", lines);
        return I18n.get("minecraft_access.read_crosshair.sign_" + (signEntity.isFacingFrontText(player) ? "front" : "back") + "_content", narration, content);
    }

    private static String getVisibleItems(List<ItemStack> itemList, String narration) {
        StringBuilder items = new StringBuilder();
        for (ItemStack item : itemList) {
            if (!item.isEmpty()) {
                items.append(item.getItemName().getString())
                        .append(I18n.get("minecraft_access.other.words_connection"));
            }
        }
        return items.isEmpty() ? narration : I18n.get("minecraft_access.other.entity_with_equipments", Map.of("entity", narration, "equipments", items));
    }

    private static @NotNull String getRedstoneRelatedInfo(
            ClientLevel world,
            BlockPos blockPos,
            Block block,
            BlockState blockState,
            String currentNarration
    ) {
        boolean isEmittingPower = world.hasSignal(blockPos, Direction.DOWN);
        boolean isReceivingPower = world.hasNeighborSignal(blockPos);

        return switch (block) {
            case PistonBaseBlock ignored -> {
                String facing = blockState.getValue(PistonBaseBlock.FACING).getName();
                String narration = I18n.get("minecraft_access.read_crosshair.facing", currentNarration, I18n.get("minecraft_access.direction." + facing));
                yield isReceivingPower ? I18n.get("minecraft_access.read_crosshair.powered", narration) : narration;
            }
            case RedStoneWireBlock ignored -> getRedstoneWireInfo(blockState, blockPos, currentNarration);
            case HopperBlock ignored -> {
                String facing = blockState.getValue(HopperBlock.FACING).getName();
                String narration = I18n.get("minecraft_access.read_crosshair.facing", currentNarration, I18n.get("minecraft_access.direction." + facing));
                yield isReceivingPower ? I18n.get("minecraft_access.read_crosshair.locked", narration) : narration;
            }
            case ObserverBlock ignored -> {
                String facing = blockState.getValue(ObserverBlock.FACING).getName();
                String narration = I18n.get("minecraft_access.read_crosshair.facing", currentNarration, I18n.get("minecraft_access.direction." + facing));
                yield isEmittingPower ? I18n.get("minecraft_access.read_crosshair.powered", narration) : narration;
            }
            case DispenserBlock ignored -> {
                String facing = blockState.getValue(DispenserBlock.FACING).getName();
                String narration = I18n.get("minecraft_access.read_crosshair.facing", currentNarration, I18n.get("minecraft_access.direction." + facing));
                yield isReceivingPower ? I18n.get("minecraft_access.read_crosshair.powered", narration) : narration;
            }
            case ComparatorBlock ignored -> {
                ComparatorMode mode = blockState.getValue(ComparatorBlock.MODE);
                Direction facing = blockState.getValue(ComparatorBlock.FACING);
                String correctFacing = I18n.get("minecraft_access.direction." + Orientation.getOppositeDirectionKey(facing.getName()).toLowerCase());
                String narration = I18n.get("minecraft_access.read_crosshair.comparator_info", currentNarration, correctFacing, mode);
                yield isReceivingPower ? I18n.get("minecraft_access.read_crosshair.powered", narration) : narration;
            }
            case RepeaterBlock ignored -> {
                boolean locked = blockState.getValue(RepeaterBlock.LOCKED);
                int delay = blockState.getValue(RepeaterBlock.DELAY);
                Direction facing = blockState.getValue(RepeaterBlock.FACING);
                String correctFacing = I18n.get("minecraft_access.direction." + Orientation.getOppositeDirectionKey(facing.getName()).toLowerCase());
                String narration = I18n.get("minecraft_access.read_crosshair.repeater_info", currentNarration, correctFacing, delay);
                yield locked ? I18n.get("minecraft_access.read_crosshair.locked", narration) : narration;
            }
            default -> {
                if ((block instanceof GlowLichenBlock || block instanceof RedstoneLampBlock) && (isReceivingPower || isEmittingPower)) {
                    yield I18n.get("minecraft_access.read_crosshair.powered", currentNarration);
                }
                if ((block instanceof RedstoneTorchBlock || block instanceof LeverBlock || block instanceof ButtonBlock) && isEmittingPower) {
                    yield I18n.get("minecraft_access.read_crosshair.powered", currentNarration);
                }
                if (block instanceof DoorBlock doorBlock && doorBlock.isOpen(blockState)
                        || block instanceof FenceGateBlock && blockState.getValue(FenceGateBlock.OPEN)) {
                    yield I18n.get("minecraft_access.read_crosshair.opened", currentNarration);
                }
                if (isReceivingPower) {
                    yield I18n.get("minecraft_access.read_crosshair.powered", currentNarration);
                }
                yield currentNarration;
            }
        };
    }

    private static @NotNull String getRedstoneWireInfo(BlockState blockState, BlockPos pos, String currentNarration) {
        String narration = currentNarration;
        int powerLevel = blockState.getValue(RedStoneWireBlock.POWER);
        if (powerLevel > 0) {
            narration = I18n.get("minecraft_access.read_crosshair.redstone_wire_power", narration, powerLevel);
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

        String directionsNarration = String.join(I18n.get("minecraft_access.other.words_connection"), connectedDirections);
        narration = I18n.get("minecraft_access.read_crosshair.redstone_wire_connection", narration, directionsNarration);

        return narration;
    }

    private static @NotNull String getBeehiveInfo(BeehiveBlockEntity blockEntity, BlockState blockState, String currentNarration) {
        String narration = currentNarration;
        boolean isSmoked = blockEntity.isSedated();
        int honeyLevel = blockState.getValue(BeehiveBlock.HONEY_LEVEL);
        Direction facingDirection = blockState.getValue(BeehiveBlock.FACING);

        if (isSmoked) {
            narration = I18n.get("minecraft_access.read_crosshair.bee_hive_smoked", narration);
        }

        if (honeyLevel > 0) {
            narration = I18n.get("minecraft_access.read_crosshair.bee_hive_honey_level", narration, honeyLevel);
        }

        narration = I18n.get("minecraft_access.read_crosshair.bee_hive_facing", narration, facingDirection.getName());

        return narration;
    }

    private static @NotNull String getCropsInfo(Block block, BlockState blockState, String narration) {
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

    private static @NotNull String addCropGrowth(String narration, int age, int maxAge) {
        if (age == maxAge) {
            return I18n.get("minecraft_access.crop.mature", narration);
        }
        float growth = (float) age / maxAge;
        return I18n.get("minecraft_access.crop.percent", (int) (growth * 100), narration);
    }

    /**
     * @param pos fluid position (in the client world)
     * @return (narration, currentQuery):
     *      "narration" is the actual one to be narrated through Narrator,
     *      "currentQuery" is kind of shortened "narration" that is used for checking if target is changed compared to previous.
     */
    private static String narrateFluidBlock(BlockPos pos) {
        assert Minecraft.getInstance().level != null;
        FluidState fluidState = Minecraft.getInstance().level.getFluidState(pos);
        Optional<String> fluidName = NarrationUtils.getTranslatedName(fluidState.typeHolder(), "block");
        int level = fluidState.getAmount();
        String levelString = level < 8 ? I18n.get("minecraft_access.read_crosshair.fluid_level", level) : "";
        return fluidName.map(name -> String.format("%s %s", name, levelString)).orElse(levelString);
    }
}
