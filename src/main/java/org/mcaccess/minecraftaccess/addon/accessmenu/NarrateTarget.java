package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.api.WorldNarrator;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class NarrateTarget implements AccessMenuFunction {
    @Override
    public void execute() {
        HitResult hit = PlayerUtils.crosshairTarget(20.0);
        if (hit == null) return;
        switch (hit.getType()) {
            case MISS, ENTITY -> new Translation("minecraft_access.access_menu.target_missed").narrate(true);
            case BLOCK -> {
                BlockPos blockPos = ((BlockHitResult) hit).getBlockPos();
                new Translation.Delimited()
                        .put(MainClass.registry(WorldNarrator.class).get(Config.getInstance().narrateCrosshair.narrator).narrate(blockPos))
                        .put(NarrationUtils.narrateRelativePositionOfPlayerAnd(blockPos))
                        .narrate(true);
            }
        }
    }
}
