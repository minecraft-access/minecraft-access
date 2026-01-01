package org.mcaccess.minecraftaccess.features;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.kuma.api.ManagedKeyMapping;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;


/**
 * Adds key binding to narrate the player's facing direction.<br>
 * - Narrate Facing Direction Key (default: H) = Narrates the player facing direction.
 */
@Slf4j
public class FacingDirection implements BalmClientModule {
    private static ManagedKeyMapping keyHorizontalFacingDirection;
    private static ManagedKeyMapping keyVerticleFacingDirection;

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "facing_direction");
    }

    @Override
    public void initialize() {
        keyHorizontalFacingDirection = Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.horizontal_facing_direction"))
                .withDefault(InputBinding.key(InputConstants.KEY_H))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    MainClass.narrate(I18n.get("minecraft_access.other.facing_direction", PlayerPositionUtils.getHorizontalFacingDirectionInWords()), true);
                    return true;
                })
                .build();

        keyVerticleFacingDirection = Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.verticle_facing_direction"))
                .withDefault(InputBinding.key(InputConstants.KEY_H, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    MainClass.narrate(I18n.get("minecraft_access.other.facing_direction", PlayerPositionUtils.getVerticalFacingDirectionInWords()), true);
                    return true;
                })
                .build();
    }
}
