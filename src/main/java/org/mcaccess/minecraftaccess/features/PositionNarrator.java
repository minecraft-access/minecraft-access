package org.mcaccess.minecraftaccess.features;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;

/**
 * Adds key bindings to narrate the player's position.<br><br>
 * Keybindings and combinations:<br>
 * 1. Narrate Player Position Key (default: G) = Narrates the player's x y and z position.<br>
 * 2. Left Alt + X = Narrates only the x position.<br>
 * 3. Left Alt + C = Narrates only the y position.<br>
 * 4. Left Alt + Z = Narrates only the z position.<br>
 */
public class PositionNarrator implements BalmClientModule {
    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "position_narrator");
    }

    @Override
    public void initialize() {
        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "player_position.narrate_player_position"))
                .withDefault(InputBinding.key(InputConstants.KEY_V))
                .overrideCategory(KeyMappingCategories.PLAYER_POSITION)
                .handleWorldInput(event -> {
                    PlayerPositionUtils.getNarratableXYZPosition().narrate(true);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "player_position.narrate_coordinate/x"))
                .withDefault(InputBinding.key(InputConstants.KEY_X, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.PLAYER_POSITION)
                .handleWorldInput(event -> {
                    PlayerPositionUtils.getNarratableXPos().narrate(true);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "player_position.narrate_coordinate/y"))
                .withDefault(InputBinding.key(InputConstants.KEY_C, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.PLAYER_POSITION)
                .handleWorldInput(event -> {
                    PlayerPositionUtils.getNarratableYPos().narrate(true);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "player_position.narrate_coordinate/z"))
                .withDefault(InputBinding.key(InputConstants.KEY_Z, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.PLAYER_POSITION)
                .handleWorldInput(event -> {
                    PlayerPositionUtils.getNarratableZPos().narrate(true);
                    return true;
                })
                .build();
    }
}
