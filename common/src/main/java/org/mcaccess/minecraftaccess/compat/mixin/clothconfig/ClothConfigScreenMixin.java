package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.AbstractTabbedConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

/**
 * remap=false: suppress warnings since cloth isn't part of original game
 */
@Mixin(value = ClothConfigScreen.class, remap = false)
abstract class ClothConfigScreenMixin extends AbstractTabbedConfigScreen {
    ClothConfigScreenMixin(Screen parent, Component title, ResourceLocation backgroundLocation) {
        super(parent, title, backgroundLocation);
    }

    /**
     * Override inherited {@link Screen#setInitialFocus()}'s logic to not automatically focus on next component after reinitialize.
     * Due to unexpected behavior combination, pressing Enter key on {@link ClothConfigTabButton} always results in focusing on cancel button, which is different from mouse clicking on {@link ClothConfigTabButton} (which clean focus to null).
     * ({@link ClothConfigTabButton#onPress()} triggers {@link ClothConfigScreen#init()}, which triggers {@link Screen#setInitialFocus()})
     */
    @Override
    protected void setInitialFocus() {
    }
}
