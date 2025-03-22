package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.utils.ui.NarrationMessages;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ClothConfigTabButton.class, remap = false)
abstract class ClothConfigTabButtonMixin extends AbstractButton {
    @Final
    @Shadow
    private ClothConfigScreen screen;
    @Unique
    private NarrationMessages.Position mca$position;

    ClothConfigTabButtonMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(at = @At("HEAD"), method = "updateWidgetNarration")
    public void updateWidgetNarration(NarrationElementOutput builder, CallbackInfo ci) {
        builder.add(NarratedElementType.TITLE, this.createNarrationMessage());
        builder.add(NarratedElementType.USAGE, Component.translatable("narration.tab_navigation.usage"));

        if (this.mca$position == null) {
            List<ClothConfigTabButton> buttons = ((ClothConfigScreenAccessor) this.screen).getTabButtons();
            //noinspection SuspiciousMethodCalls
            this.mca$position = new NarrationMessages.Position(buttons.indexOf(this), buttons.size(), NarrationMessages.Position.Type.TAB);
        }
        builder.add(NarratedElementType.POSITION, mca$position.toComponent());
    }
}
