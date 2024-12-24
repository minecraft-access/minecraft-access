package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.entity.ItemEntity;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.config.config_maps.POILockingConfigMap;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.BowItem;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
    private boolean enabled = true;
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

    private boolean lockOnBlocks;
    private boolean speakDistance;
    private boolean unlockingSound;
    private boolean aimAssistEnabled;
    private boolean aimAssistAudioCuesEnabled;
    private float aimAssistAudioCuesVolume;
    private boolean onPOIMarkingNow = false;

    static {
        instance = new LockingHandler();
    }

    private LockingHandler() {
    }

    public void update(boolean onMarking) {
        this.onPOIMarkingNow = onMarking;
        loadConfigurations();
        if (!enabled) return;
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
    private void loadConfigurations() {
        POILockingConfigMap map = POILockingConfigMap.getInstance();
        this.enabled = map.isEnabled();
        this.lockOnBlocks = map.isLockOnBlocks();
        this.speakDistance = map.isSpeakDistance();
        this.unlockingSound = map.isUnlockingSound();
        this.interval.setDelay(map.getDelay(), Interval.Unit.Millisecond);
        this.aimAssistEnabled = map.isAimAssistEnabled();
        this.aimAssistAudioCuesEnabled = map.isAimAssistAudioCuesEnabled();
        this.aimAssistAudioCuesVolume = map.getAimAssistAudioCuesVolume();
    }

    private void mainLogic() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.world == null) return;
        if (minecraftClient.currentScreen != null) return;

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
            Map<Property<?>, Comparable<?>> entries = blockState.getEntries();
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
        ClientPlayerEntity player = WorldUtils.getClientPlayer();
        if (aimAssistEnabled && !aimAssistActive && player.isUsingItem() && player.getActiveItem().getItem() instanceof BowItem) {
            TreeMap<Double, Entity> hostileEntities = POIEntities.getInstance().builtInGroups.get("hostile").getEntities();
            if (!hostileEntities.isEmpty()) {
                Entity entity = hostileEntities.firstEntry().getValue();
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

        if (aimAssistAudioCuesEnabled && aimAssistActive) {
            float bowPullingProgress = BowItem.getPullProgress(player.getItemUseTime());

            int bowState = -1;
            if (bowPullingProgress >= 0f && bowPullingProgress < 0.50f) bowState = 0;
            if (bowPullingProgress >= 0.50f && bowPullingProgress < 1f) bowState = 1;
            if (bowPullingProgress == 1f) bowState = 2;

            if (PlayerUtils.isPlayerCanSee(player.getEyePos(), PlayerUtils.currentEntityLookingAtPosition, lockedOnEntity)) {
                if (lastAimAssistCue != 1 || bowState != lastBowState) {
                    PlayerUtils.playSoundOnPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING, aimAssistAudioCuesVolume, bowState);
                    lastAimAssistCue = 1;
                }
            } else if (lastAimAssistCue != 0 || bowState != lastBowState) {
                PlayerUtils.playSoundOnPlayer(SoundEvents.BLOCK_NOTE_BLOCK_BASS, aimAssistAudioCuesVolume, bowState);
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
            if (this.unlockingSound) {
                PlayerUtils.playSoundOnPlayer(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, 0.4f, 2f);
            } else {
                MainClass.speakWithNarrator(I18n.translate("narrator.button.difficulty_lock.unlocked"), true);
            }
        }
    }

    private void relock() {
        Map<String, POIGroup> entityGroups = POIEntities.getInstance().builtInGroups;
        for (POIGroup group : entityGroups.values()) {
            TreeMap<Double, Entity> map = group.getEntities();
            if (!map.isEmpty()) {
                Entity entity = map.firstEntry().getValue();
                if (lockOnEntity(entity)) return;
            }
        }

        if (this.lockOnBlocks || onPOIMarkingNow) {
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

            Vec3d playerPos = PlayerPositionUtils.getPlayerPosition().orElseThrow();
            double distance = lockedOnBlock.toCenterPos().distanceTo(playerPos);
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
        if (lockedOnEntity instanceof EyeOfEnderEntity) {
            lockOnBlock(lockedOnEntity.getBlockPos().toCenterPos());
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

        String toSpeak = "";
        if(entity instanceof ItemEntity) {
         toSpeak+=I18n.translate("minecraft_access.point_of_interest.locking.dropped_item", NarrationUtils.narrateEntity(entity));
        } else {
            toSpeak += NarrationUtils.narrateEntity(entity);
        }
        if (this.speakDistance) {
            toSpeak += " " + NarrationUtils.narrateRelativePositionOfPlayerAnd(entity.getBlockPos());
        }
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.point_of_interest.locking.locked", toSpeak), true);
        return true;
    }

    private void findAndLockOnNearestBlock() {
        Double minPlayerDistance = Double.MAX_VALUE;
        Vec3d nearestBlockPosition = null;

        List<TreeMap<Double, Vec3d>> scannedBlockMaps = POIBlocks.getInstance().getLockingCandidates();
        for (TreeMap<Double, Vec3d> map : scannedBlockMaps) {
            if (!map.isEmpty()) {
                Entry<Double, Vec3d> closestOneInThisType = map.firstEntry();
                Double distanceWithPlayer = closestOneInThisType.getKey();
                if (distanceWithPlayer < minPlayerDistance) {
                    minPlayerDistance = distanceWithPlayer;
                    nearestBlockPosition = closestOneInThisType.getValue();
                }
            }
        }

        if (nearestBlockPosition != null) {
            lockOnBlock(nearestBlockPosition);
        }
    }

    private void lockOnBlock(Vec3d position) {
        unlock(false);

        BlockState blockState = WorldUtils.getClientWorld().getBlockState(WorldUtils.blockPosOf(position));
        entriesOfLockedOnBlock = blockState.getEntries();

        Vec3d absolutePosition = position;
        Block blockType = blockState.getBlock();

        // Special cases for non-cube blocks
        if (blockType instanceof DoorBlock) {
            absolutePosition = NonCubeBlockAbsolutePositions.getDoorPos(position);
        } else if (blockType instanceof TrapdoorBlock) {
            absolutePosition = NonCubeBlockAbsolutePositions.getTrapDoorPos(position);
        } else if (blockType instanceof ButtonBlock) {
            absolutePosition = NonCubeBlockAbsolutePositions.getButtonPos(position);
        } else if (blockType instanceof LadderBlock) {
            absolutePosition = NonCubeBlockAbsolutePositions.getLadderPos(position);
        } else if (blockType instanceof LeverBlock) {
            absolutePosition = NonCubeBlockAbsolutePositions.getLeverPos(position);
        }

        lockedOnBlock = new BlockPos3d(absolutePosition);

        String blockDescription = NarrationUtils.narrateBlock(lockedOnBlock, "");
        if (this.speakDistance) {
            blockDescription += " " + NarrationUtils.narrateRelativePositionOfPlayerAnd(lockedOnBlock);
        }
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.point_of_interest.locking.locked", blockDescription), true);
    }
}