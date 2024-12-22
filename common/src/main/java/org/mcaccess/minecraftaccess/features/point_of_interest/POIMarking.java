package com.github.khanshoaib3.minecraft_access.features.point_of_interest;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.POIMarkingConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.NarrationUtils;
import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class POIMarking {
    @Getter
    private static final POIMarking instance;
    private static final POIBlocks poiBlocks;
    private static final POIEntities poiEntities;
    private static final LockingHandler lockingHandler;
    private boolean onMarking = false;
    private Entity markedEntity = null;
    private Block markedBlock = null;

    static {
        instance = new POIMarking();
        poiBlocks = POIBlocks.getInstance();
        poiEntities = POIEntities.getInstance();
        lockingHandler = LockingHandler.getInstance();
    }

    /**
     * Perform this feature before the normal POI scan,
     * and suppress the normal POI scan (by switching their targets to marked target)
     * if this feature is enabled.
     */
    public void update() {
        if (POIMarkingConfigMap.getInstance().isEnabled()) {
            boolean controlPressed = Screen.hasControlDown();
            boolean AltPressed = Screen.hasAltDown();
            boolean lockingKeyPressed = KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().lockingHandlerKey);

            if (lockingKeyPressed && AltPressed && controlPressed) {
                unmark();
            } else if (controlPressed && lockingKeyPressed) {
                mark();
            }

        } else {
            unmark();
        }

        // Trigger other POI features
        poiBlocks.update(onMarking, markedBlock);
        poiEntities.update(onMarking, markedEntity);
        // Locking Handler (POI Locking) should be after POI Scan features
        lockingHandler.update(onMarking);
    }

    private void mark() {
        if (onMarking) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        HitResult hit = client.crosshairTarget;
        if (hit == null) return;

        switch (hit.getType()) {
            case MISS -> {
                return;
            }
            case BLOCK -> {
                ClientWorld world = client.world;
                if (world == null) return;
                BlockPos pos = ((BlockHitResult) hit).getBlockPos();
                markedBlock = world.getBlockState(pos).getBlock();

                String name = NarrationUtils.narrateBlock(pos, "");
                MainClass.speakWithNarrator(I18n.translate("minecraft_access.point_of_interest.marking.marked", name), true);
            }
            case ENTITY -> {
                Entity e = ((EntityHitResult) hit).getEntity();
                markedEntity = e;

                String name = NarrationUtils.narrateEntity(e);
                MainClass.speakWithNarrator(I18n.translate("minecraft_access.point_of_interest.marking.marked", name), true);
            }
        }

        onMarking = true;
    }

    private void unmark() {
        if (!onMarking) return;
        onMarking = false;
        markedEntity = null;
        markedBlock = null;
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.point_of_interest.marking.unmarked"), true);
    }
}
