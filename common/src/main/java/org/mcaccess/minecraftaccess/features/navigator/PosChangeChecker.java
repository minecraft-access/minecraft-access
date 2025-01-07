package org.mcaccess.minecraftaccess.features.navigator;

import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;
import lombok.extern.slf4j.Slf4j;
import org.mcaccess.minecraftaccess.utils.system.CustomSounds;
import org.mcaccess.minecraftaccess.config.config_maps.PosChangeCheckerConfigMap;

/**
 * This code checks the player's current coordinates, compares them to their
 * last coordinates, and plays a sound if those coordinates change.
 * The most common use of this will likely be for Y, but it's possible players
 * may want all 3 options.
 */

@Slf4j
public class PosChangeChecker {
    int lastX;
    int lastY;
    int lastZ;

    public void init() {
        try {
            _init();
        } catch (Exception e) {
            log.error("The PosChangeChecker was unable to launch. Error: ", e);
        }
    }

    private void _init() {
        lastX = 0;
        lastY = 0;
        lastZ = 0;
    }

    public void compareStats() {
        if (PosChangeCheckerConfigMap.getInstance().isPlaySoundForXChanges()) {
            int newX = (int) PlayerPositionUtils.getX();
            if (newX > lastX) {
                CustomSounds.playSoundOnPlayer("x_up", 1f, 1f);
            }
            if (newX < lastX) {
                CustomSounds.playSoundOnPlayer("x_down", 1f, 1f);
            }
            lastX = newX;
        }

        if (PosChangeCheckerConfigMap.getInstance().isPlaySoundForYChanges()) {
            int newY = (int) PlayerPositionUtils.getY();
            if (newY > lastY) {
                CustomSounds.playSoundOnPlayer("y_up", 1f, 1f);
            }
            if (newY < lastY) {
                CustomSounds.playSoundOnPlayer("y_down", 1f, 1f);
            }
            lastY = newY;
        }

        if (PosChangeCheckerConfigMap.getInstance().isPlaySoundForZChanges()) {
            int newZ = (int) PlayerPositionUtils.getZ();
            if (newZ > lastZ) {
                CustomSounds.playSoundOnPlayer("z_up", 1f, 1f);
            }
            if (newZ < lastZ) {
                CustomSounds.playSoundOnPlayer("z_down", 1f, 1f);
            }
            lastZ = newZ;
        }
    }
}