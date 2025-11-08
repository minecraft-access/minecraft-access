package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;


/**
 * Locks on to the nearest entity or block.<br><br>
 * Keybindings and combinations:<br>
 * 1. Locking Key (default: Y) = Locks onto the nearest entity or block<br>
 * 2. Alt key + Locking Key = Unlocks from the currently locked entity or block<br>
 */
@Slf4j
public class LockingHandler {
    private final Minecraft client = Minecraft.getInstance();
    private Config.POI.Locking config;
    private Entity lockedOnEntity = null;
    private BlockPos3d lockedOnBlockPos = null;
    private boolean isLockedOnWhereEyeOfEnderDisappears = false;
    private Map<Property<?>, Comparable<?>> entriesOfLockedOnBlock;
    private final Interval interval = Interval.defaultDelay();
    private boolean aimAssistActive = false;
    // 0 = can't shoot, 1 = can shoot
    private int lastAimAssistCue = -1;
    // -1 = null, 1 = starting, 2 = half drawn, 3 = fully drawn
    private int lastBowState = -1;

    LockingHandler() {
    }

    /**
     * Loads the configs from the config.json
     */
    private void loadConfig() {
        config = Config.getInstance().poi.locking;
        interval.setDelay(config.delay, Interval.Unit.MILLISECOND);
    }

    public void tick() {
        loadConfig();
        if (!interval.isReady()) return;
        if (client.player == null) return;
        if (client.level == null) return;
        if (client.screen != null) return;

        handleLockingKeyPressing();
        if (isPlayerLocked()) {
            lookAtLockedTarget();
        }
        bowAimingAssist();
    }

    private void handleLockingKeyPressing() {
        boolean isLockingKeyPressed = KeyMappingsHandler.Keys.LOCKING_HANDLER_KEY.mapping.isDown();
        if (isLockingKeyPressed && client.hasAltDown()) {
            if (isPlayerLocked()) {
                unlock(true, true);
                interval.beReady();
            }
        } else if (isLockingKeyPressed) {
            assert client.getCameraEntity() != null;
            if (client.getCameraEntity().is(client.player)) {
                relock();
            } else {
                MainClass.narrate(I18n.get("minecraft_access.other.camera_locked"), true);
            }
            interval.reset();
        } else {
            interval.beReady();
        }
    }

    private void lookAtLockedTarget() {
        assert client.getCameraEntity() != null;
        if (!client.getCameraEntity().is(client.player)) {
            unlock(true, true);
            return;
        }

        if (lockedOnEntity != null) {
            if (unlockFromDeadEntity()) return;
            PlayerUtils.lookAt(lockedOnEntity);
        }

        if (lockedOnBlockPos != null) {
            if (unlockFromAirBlock()) return;
            assert client.level != null;
            BlockState blockState = client.level.getBlockState(BlockPos.containing(lockedOnBlockPos.getAccuratePosition()));

            if (unlockFromLadderIfClimbingOnIt(blockState)) return;

            // Entries are different properties of blocks when they're in different states,
            // for example, opened chest and closed chest are different states of chest block,
            // they are different entries when invoking getEntries().
            Map<Property<?>, Comparable<?>> entries = blockState.getValues();
            boolean entriesOfLockedBlockNotChanged = entries.values() == entriesOfLockedOnBlock.values();

            if (entriesOfLockedBlockNotChanged || isLockedOnWhereEyeOfEnderDisappears) {
                assert client.player != null;
                client.player.lookAt(EntityAnchorArgument.Anchor.EYES, lockedOnBlockPos.getAccuratePosition());
            } else {
                // Unlock if the state of locked block is changed
                unlock(true, true);
            }
        }
    }

    /**
     * Automatically locks on to the nearest hostile entity when the player is pulling a bow.
     */
    private void bowAimingAssist() {
        LocalPlayer player = client.player;
        if (player == null) return;

        // Check if player is using a bow
        if (config.aimAssistEnabled && !aimAssistActive && player.isUsingItem() && player.getUseItem().getItem() instanceof BowItem) {
            List<Entity> hostileEntities = BuiltinEntityPOIGroups.HOSTILE.group.getItems();
            if (!hostileEntities.isEmpty()) {
                Entity entity = hostileEntities.stream()
                        .min(Comparator.comparingDouble(player::distanceTo))
                        .get();
                if (lockOnEntity(entity)) {
                    aimAssistActive = true;
                }
            }
        }

        // Reset when not using bow anymore
        if (aimAssistActive && (!player.isUsingItem() || !(player.getUseItem().getItem() instanceof BowItem))) {
            unlock(false, true);
            aimAssistActive = false;
            lastAimAssistCue = -1;
            lastBowState = -1;
            return;
        }

        // Handle audio cues for aiming
        if (config.aimAssistAudioCuesEnabled && aimAssistActive && lockedOnEntity != null) {
            float bowPullingProgress = BowItem.getPowerForTime(player.getTicksUsingItem());

            int bowState = -1;
            if (bowPullingProgress >= 0.0f && bowPullingProgress < 0.50f) bowState = 0;
            if (bowPullingProgress >= 0.50f && bowPullingProgress < 1.0f) bowState = 1;
            if (bowPullingProgress == 1.0f) bowState = 2;

            Vec3 eyePosition = player.getEyePosition();
            Vec3 targetPosition = PlayerUtils.currentEntityLookingAtPosition;

            if (targetPosition != null) {
                if (PlayerUtils.isVisibleToPlayer(eyePosition, targetPosition, lockedOnEntity)) {
                    if (lastAimAssistCue != 1 || bowState != lastBowState) {
                        player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), config.aimAssistAudioCuesVolume, bowState);
                        lastAimAssistCue = 1;
                    }
                } else if (lastAimAssistCue != 0 || bowState != lastBowState) {
                    player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), config.aimAssistAudioCuesVolume, bowState);
                    lastAimAssistCue = 0;
                }

                lastBowState = bowState;
            }
        }
    }

    public boolean isPlayerLocked() {
        return lockedOnBlockPos != null || lockedOnEntity != null;
    }

    private void unlock(boolean narrate, boolean isStillValid) {
        lockedOnEntity = null;
        entriesOfLockedOnBlock = null;
        lockedOnBlockPos = null;
        isLockedOnWhereEyeOfEnderDisappears = false;
        if (!isStillValid) MainClass.poiManager.objectTracker.clearCurrentObject();

        if (narrate) {
            if (config.unlockingSound) {
                assert client.player != null;
                client.player.playSound(SoundEvents.NOTE_BLOCK_BASEDRUM.value(), 0.4f, 2.0f);
            } else {
                MainClass.narrate(I18n.get("narrator.button.difficulty_lock.unlocked"), true);
            }
        }
    }

    private void relock() {
        Object target = MainClass.poiManager.objectTracker.getCurrentObject();
        if (target == null) {
            MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_selected"), true);
            return;
        }
        switch (target) {
            case Entity entity -> lockOnEntity(entity);
            case BlockPos blockPos -> lockOnBlock(blockPos);
            default -> throw new IllegalStateException("Unexpected locking target type: " + target);
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

            assert client.player != null;
            Vec3 playerPos = client.player.position();
            double distance = lockedOnBlockPos.getCenter().distanceTo(playerPos);
            if (distance <= 0.5) {
                unlock(true, true);
                return true;
            }
        }
        return false;
    }

    /**
     * If locked on entity is dead or otherwise not valid, unlock.
     *
     * @return true if unlocked
     */
    private boolean unlockFromDeadEntity() {
        // When the eye of ender disappears, its isAlive() will also return false.
        // Change the lock target to the last (block) position (somewhere floating in the air) where the eye of ender disappeared,
        // so the player can continue walking until being under that position.
        if (lockedOnEntity instanceof EyeOfEnder) {
            lockOnBlock(lockedOnEntity.blockPosition());
            isLockedOnWhereEyeOfEnderDisappears = true;
        }

        if (MainClass.poiManager.objectTracker.isObjectValid(lockedOnEntity)) return false;

        unlock(true, false);
        return true;
    }

    /**
     * If locked on block is an air block or otherwise not valid, unlock.
     *
     * @return true if unlocked
     */
    private boolean unlockFromAirBlock() {
        if (MainClass.poiManager.objectTracker.isObjectValid(lockedOnBlockPos)) return false;
        unlock(true, false);
        return true;
    }

    /**
     * @return true if locked
     */
    public boolean lockOnEntity(Entity entity) {
        if (!MainClass.poiManager.objectTracker.isObjectValid(entity)) return false;

        unlock(false, true);
        lockedOnEntity = entity;

        StringBuilder narration = new StringBuilder(NarrationUtils.narrateEntity(entity));

        if (Config.getInstance().poi.narrateDistance) {
            narration.append(' ')
                    .append(NarrationUtils.narrateRelativePositionOfPlayerAnd(entity.blockPosition()));
        }
        MainClass.narrate(I18n.get("minecraft_access.point_of_interest.locking.locked", narration), true);
        return true;
    }

    private void lockOnBlock(BlockPos position) {
        assert client.level != null;
        BlockState blockState = client.level.getBlockState(position);
        entriesOfLockedOnBlock = blockState.getValues();

        Vec3 absolutePosition = switch (blockState.getBlock()) {
            case DoorBlock ignored -> NonCubeBlockAbsolutePositions.getDoorPos(position.getCenter());
            case TrapDoorBlock ignored -> NonCubeBlockAbsolutePositions.getTrapDoorPos(position.getCenter());
            case ButtonBlock ignored -> NonCubeBlockAbsolutePositions.getButtonPos(position.getCenter());
            case LadderBlock ignored -> NonCubeBlockAbsolutePositions.getLadderPos(position.getCenter());
            case LeverBlock ignored -> NonCubeBlockAbsolutePositions.getLeverPos(position.getCenter());
            default -> position.getCenter();
        };

        lockedOnBlockPos = new BlockPos3d(position, absolutePosition);

        StringBuilder blockDescription = new StringBuilder(NarrationUtils.narrateBlock(lockedOnBlockPos, ""));
        if (Config.getInstance().poi.narrateDistance) {
            blockDescription.append(' ')
                    .append(NarrationUtils.narrateRelativePositionOfPlayerAnd(lockedOnBlockPos));
        }
        MainClass.narrate(I18n.get("minecraft_access.point_of_interest.locking.locked", blockDescription), true);
    }
}
