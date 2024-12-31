package com.github.khanshoaib3.minecraft_access.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClothConfigTabButton.class)
abstract class ClothConfigTabButtonMixin extends PressableWidget {
    ClothConfigTabButtonMixin(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Inject(at = @At("HEAD"), method = "appendClickableNarrations")
    void appendClickableNarrations(NarrationMessageBuilder builder, CallbackInfo ci) {
        builder.put(NarrationPart.TITLE, this.getNarrationMessage());
    }
}
