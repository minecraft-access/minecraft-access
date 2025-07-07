package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationThunk;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.duck.NarrationThunkExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({
        EditBox.class,
        MultiLineEditBox.class,
})
abstract class InputMixin {
    @Redirect(method = "updateWidgetNarration", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/narration/NarrationElementOutput;add(Lnet/minecraft/client/gui/narration/NarratedElementType;Lnet/minecraft/network/chat/Component;)V"))
    private void setDeduplication(NarrationElementOutput instance, NarratedElementType type, Component contents) {
        NarrationThunk<?> thunk = NarrationThunk.from(contents);
        ((NarrationThunkExt) thunk).setDeduplication(switch (Minecraft.getInstance().screen) {
            case BookEditScreenAccessor bookEditScreen -> bookEditScreen.getCurrentPage();
            case null, default -> this;
        });
        instance.add(type, thunk);
    }
}
