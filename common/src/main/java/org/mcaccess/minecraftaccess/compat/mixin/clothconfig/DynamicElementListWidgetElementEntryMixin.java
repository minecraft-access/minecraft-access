package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DynamicElementListWidget.ElementEntry.class)
abstract class DynamicElementListWidgetElementEntryMixin implements ContainerEventHandler {
    @Shadow
    private @Nullable GuiEventListener focused;

    @Inject(method = "setFocused", at = @At("TAIL"))
    public void cleanPeerFocusStates(GuiEventListener guiEventListener, CallbackInfo ci) {
        this.children().forEach(c -> {
            if (c != this.focused) {
                c.setFocused(false);
                if (c instanceof ContainerEventHandler c2) {
                    c2.setFocused(null);
                }
            }
        });
    }
}
