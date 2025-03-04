package org.mcaccess.minecraftaccess.features.point_of_interest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Locks on to the nearest entity or block.<br><br>
 * Keybindings and combinations:<br>
 * 1. Locking Key (default: Y) = Locks onto the nearest entity or block<br>
 * 2. Alt key + Locking Key = Unlocks from the currently locked entity or block<br>
 */
@Slf4j
public class LockingHandler {
    @Getter
    private static final LockingHandler instance;
    private Config.POI.Locking config;
    private Entity lockedOnEntity = null;
    private BlockPos3d lockedOnBlock = null;
    private boolean isLockedOnWhereEyeOfEnderDisappears = false;
    private Map<Property<?>, Comparable<?>> entriesOfLockedOnBlock;
    private final Interval interval = Interval.defaultDelay();
    private boolean aimAssistActive = false;
    // 0 = can't shoot, 1 = can shoot
    private int lastAimAssistCue = -1;
    // -1 = null, 1 = starting, 2 = half drawn, 3 = fully drawn
    private int lastBowState = -1;

    private boolean onPOIMarkingNow = false;

    static {
        instance = new LockingHandler();
    }

    private LockingHandler() {
    }

    public void update(boolean onMarking) {
        this.onPOIMarkingNow = onMarking;
        loadConfig();
        if (!config.enabled) return;
        if (!interval.isReady()) return;
        try {
            mainLogic();
        } catch (Exception e) {
            log.error("An error while updating LockingHandler", e);
        }
    }

    /**
     * Loads the configs from the config.json
     */
    private void loadConfig() {
        config = Config.getInstance().poi.locking;
        interval.setDelay(config.delay, Interval.Unit.Millisecond);
    }

    private void mainLogic() {
        Minecraft minecraftClient = Minecraft.getInstance();

        if (minecraftClient.player == null) return;
        if (minecraftClient.level == null) return;
        if (minecraftClient.screen != null) return;

        handleLockingKeyPressing();
        lookAtLockedTarget();
        bowAimingAssist();
    }

    private void handleLockingKeyPressing() {
        boolean isLockingKeyPressed = KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().lockingHandlerKey);
        if (isLockingKeyPressed && Screen.hasAltDown()) {
            if (lockedOnEntity != null || lockedOnBlock != null) {
                unlock(true);
                interval.beReady();
            }
        } else if (isLockingKeyPressed) {
            relock();
            interval.reset();
        } else {
            interval.beReady();
        }
    }

    private void lookAtLockedTarget() {
        if (lockedOnEntity != null) {
            if (unlockFromDeadEntity()) return;
            PlayerUtils.lookAt(lockedOnEntity);
        }

        if (lockedOnBlock != null) {
            BlockState blockState = WorldUtils.getClientWorld().getBlockState(WorldUtils.blockPosOf(lockedOnBlock.getAccuratePosition()));

            if (unlockFromLadderIfClimbingOnIt(blockState)) return;

            // Entries are different properties of blocks when they're in different states,
            // for example, opened chest and closed chest are different states of chest block,
            // they are different entries when invoking getEntries().
            Map<Property<?>, Comparable<?>> entries = blockState.getValues();
            boolean entriesOfLockedBlockNotChanged = entries.values() == entriesOfLockedOnBlock.values();

            if (entriesOfLockedBlockNotChanged || isLockedOnWhereEyeOfEnderDisappears)
                PlayerUtils.lookAt(lockedOnBlock);
            else {
                // Unlock if (the state of) locked block is changed
                unlock(true);
            }
        }
    }

    /**
     * Automatically locks on to the nearest hostile entity when the player is pulling a bow.
     */
    private void bowAimingAssist() {
        LocalPlayer player = WorldUtils.getClientPlayer();
        if (config.aimAssistEnabled && !aimAssistActive && player.isUsingItem() && player.getUseItem().getItem() instanceof BowItem) {
            List<Entity> hostileEntities = POIEntities.getInstance().hostileGroup.getItems();
            if (!hostileEntities.isEmpty()) {
                Entity entity = hostileEntities.stream()
                        .min(Comparator.comparingDouble(e -> WorldUtils.getClientPlayer().distanceTo(e)))
                        .get();
                if (lockOnEntity(entity)) {
                    aimAssistActive = true;
                }
            }
        }

        if (aimAssistActive && !player.isUsingItem()) {
            unlock(false);
            aimAssistActive = false;
            lastAimAssistCue = -1;
            lastBowState = -1;
        }

        if (config.aimAssistAudioCuesEnabled && aimAssistActive) {
            float bowPullingProgress = BowItem.getPowerForTime(player.getTicksUsingItem());

            int bowState = -1;
            if (bowPullingProgress >= 0f && bowPullingProgress < 0.50f) bowState = 0;
            if (bowPullingProgress >= 0.50f && bowPullingProgress < 1f) bowState = 1;
            if (bowPullingProgress == 1f) bowState = 2;

            if (PlayerUtils.isPlayerCanSee(player.getEyePosition(), PlayerUtils.currentEntityLookingAtPosition, lockedOnEntity)) {
                if (lastAimAssistCue != 1 || bowState != lastBowState) {
                    PlayerUtils.playSoundOnPlayer(SoundEvents.NOTE_BLOCK_PLING, config.aimAssistAudioCuesVolume, bowState);
                    lastAimAssistCue = 1;
                }
            } else if (lastAimAssistCue != 0 || bowState != lastBowState) {
                PlayerUtils.playSoundOnPlayer(SoundEvents.NOTE_BLOCK_BASS, config.aimAssistAudioCuesVolume, bowState);
                lastAimAssistCue = 0;
            }

            lastBowState = bowState;
        }
    }

    private void unlock(boolean speak) {
        lockedOnEntity = null;
        entriesOfLockedOnBlock = null;
        lockedOnBlock = null;
        isLockedOnWhereEyeOfEnderDisappears = false;

        if (speak) {
            if (config.unlockingSound) {
                PlayerUtils.playSoundOnPlayer(SoundEvents.NOTE_BLOCK_BASEDRUM, 0.4f, 2f);
            } else {
                MainClass.speakWithNarrator(I18n.get("narrator.button.difficulty_lock.unlocked"), true);
            }
        }
    }

    private void relock() {
        for (POIGroup<Entity> group : POIEntities.getInstance().groups) {
            Optional<Entity> nearest = group.getItems().stream()
                    .min(Comparator.comparingDouble(entity -> WorldUtils.getClientPlayer().distanceTo(entity)));
            if (nearest.map(this::lockOnEntity).orElse(false)) {
                return;
            }
        }

        if (config.lockOnBlocks || onPOIMarkingNow) {
            findAndLockOnNearestBlock();
        }
    }

    /**
     * Automatically unlock from the ladder after the player starting climbing the ladder.
     * When you stand directly in front of the ladder, the distance is 1.5,
     * since the player position is player's leg (player standing y + 1),
     * and the mod will lock on the ladder at the same height of the player head (player standing y + 2).
     *
     * @param blockState state of locked block, taken from world
     * @return true if unlocked
     */
    private boolean unlockFromLadderIfClimbingOnIt(BlockState blockState) {
        if (Blocks.LADDER.equals(blockState.getBlock())) {

            Vec3 playerPos = PlayerPositionUtils.getPlayerPosition().orElseThrow();
            double distance = lockedOnBlock.getCenter().distanceTo(playerPos);
            if (distance <= 0.5) {
                unlock(true);
                return true;
            }
        }
        return false;
    }

    /**
     * If the entity has dead, we'll automatically unlock from it.
     *
     * @return true if unlocked
     */
    private boolean unlockFromDeadEntity() {
        if (lockedOnEntity.isAlive()) return false;

        // When the eye of ender disappears, its isAlive() will also return false.
        // Change the lock target to the last (block) position (somewhere floating in the air) where the eye of ender disappeared,
        // so the player can continue walking until being under that position.
        if (lockedOnEntity instanceof EyeOfEnder) {
            lockOnBlock(lockedOnEntity.blockPosition());
            isLockedOnWhereEyeOfEnderDisappears = true;
        }

        unlock(true);
        return true;
    }

    /**
     * @return true if locked
     */
    public boolean lockOnEntity(Entity entity) {
        if (!entity.isAlive()) return false;

        unlock(false);
        lockedOnEntity = entity;

        String toSpeak = NarrationUtils.narrateEntity(entity);

        if (config.speakDistance) {
            toSpeak += " " + NarrationUtils.narrateRelativePositionOfPlayerAnd(entity.blockPosition());
        }
        MainClass.speakWithNarrator(I18n.get("minecraft_access.point_of_interest.locking.locked", toSpeak), true);
        return true;
    }

    private void findAndLockOnNearestBlock() {
        POIBlocks.getINSTANCE().getLockingCandidates().stream()
                .min(Comparator.comparingDouble(a -> WorldUtils.getClientPlayer().getEyePosition().distanceTo(a.getCenter())))
                .ifPresent(this::lockOnBlock);
    }

    private void lockOnBlock(BlockPos position) {
        unlock(false);

        BlockState blockState = WorldUtils.getClientWorld().getBlockState(position);
        entriesOfLockedOnBlock = blockState.getValues();

        Vec3 absolutePosition = switch (blockState.getBlock()) {
            case DoorBlock ignored -> NonCubeBlockAbsolutePositions.getDoorPos(position.getCenter());
            case TrapDoorBlock ignored -> NonCubeBlockAbsolutePositions.getTrapDoorPos(position.getCenter());
            case ButtonBlock ignored -> NonCubeBlockAbsolutePositions.getButtonPos(position.getCenter());
            case LadderBlock ignored -> NonCubeBlockAbsolutePositions.getLadderPos(position.getCenter());
            case LeverBlock ignored -> NonCubeBlockAbsolutePositions.getLeverPos(position.getCenter());
            default -> position.getCenter();
        };

        lockedOnBlock = new BlockPos3d(position, absolutePosition);

        String blockDescription = NarrationUtils.narrateBlock(lockedOnBlock, "");
        if (config.speakDistance) {
            blockDescription += " " + NarrationUtils.narrateRelativePositionOfPlayerAnd(lockedOnBlock);
        }
        MainClass.speakWithNarrator(I18n.get("minecraft_access.point_of_interest.locking.locked", blockDescription), true);
    }
}