package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import me.shedaniel.clothconfig2.gui.widget.DynamicEntryListWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.utils.ui.NavigationUtils;

/**
 * Different from original GUI, the cloth config GUI will keep mouse clicked options highlighted even if the focus has changed.
 * This behavior forbids original navigation works since highlighted options will be skipped when navigated through.
 * This mixin removes the highlighting behavior of cloth config GUI.
 */
@Mixin({DynamicElementListWidget.ElementEntry.class, DynamicEntryListWidget.class})
abstract class CleanMouseClickingFocus implements ContainerEventHandler {
    @Inject(method = "setFocused", at = @At("TAIL"))
    public void cleanPeerFocusStatesOnFocusChanging(GuiEventListener guiEventListener, CallbackInfo ci) {
        children().stream().filter(c -> c != guiEventListener).forEach(NavigationUtils::clearFocus);
    }
}
