package org.mcaccess.minecraftaccess.features.point_of_interest;

import lombok.Getter;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.WorldNarrator;
import org.mcaccess.minecraftaccess.utils.KeyMappingsHandler;

public class POIMarking implements BalmClientModule {
    @Getter
    private boolean isMarked = false;

    @Getter
    private Entity markedEntity = null;

    @Getter
    private Block markedBlock = null;

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "poi/marking");
    }

    @Override
    public void initialize() {
        ClientTickCallback.ClientLevelTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            isMarked = false;
            markedEntity = null;
            markedBlock = null;
        });
    }

    /**
     * Perform this feature before the normal POI scan,
     * and suppress the normal POI scan (by switching their targets to marked target)
     * if this feature is enabled.
     */
    private void tick(Level level) {
        if (Config.getInstance().poi.marking.enabled) {
            boolean controlPressed = Minecraft.getInstance().hasControlDown();
            boolean altPressed = Minecraft.getInstance().hasAltDown();
            boolean lockingKeyPressed = KeyMappingsHandler.Keys.LOCKING_HANDLER_KEY.mapping.isDown();

            if (lockingKeyPressed && altPressed && controlPressed) {
                unmark();
            } else if (controlPressed && lockingKeyPressed) {
                mark();
            }

        } else {
            unmark();
        }
    }

    private void mark() {
        if (isMarked) return;

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

                String name = MainClass.registry(WorldNarrator.class).get(Config.getInstance().narrateCrosshair.narrator).narrate(pos);
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.marking.marked", name), true);
            }
            case ENTITY -> {
                Entity entity = ((EntityHitResult) hit).getEntity();
                markedEntity = entity;

                String name = MainClass.registry(WorldNarrator.class).get(Config.getInstance().narrateCrosshair.narrator).narrate(entity);
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.marking.marked", name), true);
            }
        }

        isMarked = true;
    }

    private void unmark() {
        if (!isMarked) return;
        markedBlock = null;
        markedEntity = null;
        isMarked = false;
        MainClass.narrate(I18n.get("minecraft_access.point_of_interest.marking.unmarked"), true);
    }
}
