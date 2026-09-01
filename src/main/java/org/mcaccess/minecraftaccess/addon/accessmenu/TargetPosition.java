package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class TargetPosition implements AccessMenuFunction {
    @Override
    public void execute() {
        HitResult hit = PlayerUtils.crosshairTarget(20.0);
        if (hit == null) return;
        switch (hit.getType()) {
            case MISS, ENTITY -> new Translation("minecraft_access.access_menu.target_missed").narrate(true);
            case BLOCK -> {
                BlockHitResult blockHitResult = (BlockHitResult) hit;
                BlockPos blockPos = blockHitResult.getBlockPos();
                MainClass.narrate(NarrationUtils.narrateCoordinatesOf(blockPos), true);
            }
        }
    }
}
