package org.mcaccess.minecraftaccess.features.point_of_interest;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.Getter;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.WorldNarrator;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;

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
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            isMarked = false;
            markedEntity = null;
            markedBlock = null;
        });

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.poi_marking/mark"))
                .withDefault(InputBinding.key(InputConstants.KEY_Y, KeyModifiers.of(KeyModifier.CONTROL)))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    mark();
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.poi_marking/unmark"))
                .withDefault(InputBinding.key(InputConstants.KEY_Y, KeyModifiers.of(KeyModifier.CONTROL, KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    unmark();
                    return true;
                })
                .build();
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
