package org.mcaccess.minecraftaccess.features;

import java.util.Set;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.ChatOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.MouseSettingsScreen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.ClientConfig;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.config.RegistrySingleSelect;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

/**
 * Moves the mouse to the top left of the screen and then performs left click.
 * This fixes the bug in which the mouse cursor interrupts when navigating through the screen elements
 * which results in infinite narrating of `Screen element x out of x` by the narrator
 */
@Slf4j
public final class MenuFix implements BalmClientModule {
    private static Screen previous;
    private static final Set<Class<? extends Screen>> MENUS_NEED_FIX = Set.of(
            TitleScreen.class,
            OptionsScreen.class,
            ControlsScreen.class,
            OnlineOptionsScreen.class,
            SkinCustomizationScreen.class,
            SoundOptionsScreen.class,
            VideoSettingsScreen.class,
            LanguageSelectScreen.class,
            ChatOptionsScreen.class,
            PackSelectionScreen.class,
            AccessibilityOptionsScreen.class,
            MouseSettingsScreen.class,
            KeyBindsScreen.class,
            SelectWorldScreen.class,
            CreateWorldScreen.class,
            EditWorldScreen.class,
            JoinMultiplayerScreen.class,
            DirectJoinServerScreen.class,
            ManageServerScreen.class,
            RegistrySingleSelect.SelectionScreen.class
    );

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "menu_fix");
    }

    @Override
    public void initialize() {
        ClientTickCallback.AFTER.register(this::tick);

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.menu_fix"))
                .withDefault(InputBinding.key(InputConstants.KEY_R, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleScreenInput(_ -> {
                    Minecraft client = Minecraft.getInstance();
                    if (MENUS_NEED_FIX.contains(client.gui.screen().getClass())) {
                        log.debug("Performing menu fix on {}", client.gui.screen().getTitle().getString());
                        moveMouseCursor();
                    }
                    return true;
                })
                .build();
    }

    private void tick(Minecraft client) {
        if (client.gui.screen() == previous) {
            return;
        }

        previous = client.gui.screen();

        if (!ClientConfig.getInstance().general.menuFixEnabled || client.gui.screen() == null) {
            return;
        }

        if (MENUS_NEED_FIX.contains(client.gui.screen().getClass())) {
            log.debug("Performing menu fix on {}", client.gui.screen().getTitle().getString());
            moveMouseCursor();
        }
    }

    /**
     * Moves the mouse cursor to x=10 y=10 relative to the Minecraft window location.
     */
    private static void moveMouseCursor() {
        MouseUtils.moveAndLeftClick(10, 10);
    }
}
