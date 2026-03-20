package org.mcaccess.minecraftaccess.features.point_of_interest;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.Getter;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.balm.client.platform.util.SessionLocal;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.WorldNarrator;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;

public class POIMarking implements BalmClientModule {
    @Getter
    private final SessionLocal<Object> markedObject = new SessionLocal<>(() -> null);

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "poi/marking");
    }

    @Override
    public void initialize() {
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
        if (markedObject.value != null) return;

        Object currentObject = MainClass.poiManager.objectTracker.getCurrentObject();
        if (currentObject == null) return;

        String name;
        if (currentObject instanceof BlockPos pos) {
            assert Minecraft.getInstance().level != null;
            markedObject.value = Minecraft.getInstance().level.getBlockState(pos).getBlock();
            name = MainClass.registry(WorldNarrator.class).get(Config.getInstance().narrateCrosshair.narrator).narrate(pos);
        } else if (currentObject instanceof Entity entity) {
            markedObject.value = entity;
            name = MainClass.registry(WorldNarrator.class).get(Config.getInstance().narrateCrosshair.narrator).narrate(entity);
        } else {
            return;
        }

        MainClass.narrate(I18n.get("minecraft_access.point_of_interest.marking.marked", name), true);
    }

    private void unmark() {
        if (markedObject.value == null) return;
        markedObject.value = null;
        MainClass.narrate(I18n.get("minecraft_access.point_of_interest.marking.unmarked"), true);
    }
}
