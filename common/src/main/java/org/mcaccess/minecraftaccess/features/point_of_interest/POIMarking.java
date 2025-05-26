package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

public class POIMarking {
    public POIBlocks poiBlocks;
    public POIEntities poiEntities;
    public LockingHandler lockingHandler;
    public ObjectTracker objectTracker;
    private boolean onMarking = false;
    private Entity markedEntity = null;
    private Block markedBlock = null;

    public POIMarking() {
        poiBlocks = new POIBlocks();
        poiEntities = new POIEntities();
        lockingHandler = new LockingHandler();
        objectTracker = new ObjectTracker();
    }

    /**
     * Perform this feature before the normal POI scan,
     * and suppress the normal POI scan (by switching their targets to marked target)
     * if this feature is enabled.
     */
    public void update() {
        if (Config.getInstance().poi.marking.enabled) {
            boolean controlPressed = Screen.hasControlDown();
            boolean altPressed = Screen.hasAltDown();
            boolean lockingKeyPressed = KeyUtils.isAnyPressed(KeyBindingsHandler.lockingHandlerKey);

            if (lockingKeyPressed && altPressed && controlPressed) {
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
        lockingHandler.update();
        objectTracker.update();
    }

    private void mark() {
        if (onMarking) return;

        Minecraft client = Minecraft.getInstance();
        HitResult hit = client.hitResult;
        if (hit == null) return;

        switch (hit.getType()) {
            case MISS -> {
                return;
            }
            case BLOCK -> {
                ClientLevel world = client.level;
                if (world == null) return;
                BlockPos pos = ((BlockHitResult) hit).getBlockPos();
                markedBlock = world.getBlockState(pos).getBlock();

                String name = NarrationUtils.narrateBlock(pos, "");
                MainClass.speakWithNarrator(I18n.get("minecraft_access.point_of_interest.marking.marked", name), true);
            }
            case ENTITY -> {
                Entity e = ((EntityHitResult) hit).getEntity();
                markedEntity = e;

                String name = NarrationUtils.narrateEntity(e);
                MainClass.speakWithNarrator(I18n.get("minecraft_access.point_of_interest.marking.marked", name), true);
            }
        }

        onMarking = true;
    }

    private void unmark() {
        if (!onMarking) return;
        onMarking = false;
        markedEntity = null;
        markedBlock = null;
        MainClass.speakWithNarrator(I18n.get("minecraft_access.point_of_interest.marking.unmarked"), true);
    }
}
