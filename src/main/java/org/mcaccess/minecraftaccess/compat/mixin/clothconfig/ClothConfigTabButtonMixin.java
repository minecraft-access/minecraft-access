package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import java.util.List;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.utils.ui.NarrationMessages;

@Mixin(value = ClothConfigTabButton.class, remap = false)
abstract class ClothConfigTabButtonMixin extends AbstractButton {
    @Final
    @Shadow
    private ClothConfigScreen screen;
    @Unique
    private NarrationMessages.Position position;

    ClothConfigTabButtonMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    /**
     * @implNote This method is a vanilla method from {@link AbstractWidget#updateWidgetNarration},
     *         and override in {@link ClothConfigTabButton}, but it's still a vanilla method.
     *         So we need to re-enable remap=true for this method, or it will fail finding target on production.
     */
    @Inject(at = @At("HEAD"), method = "updateWidgetNarration", remap = true)
    private void updateWidgetNarration(NarrationElementOutput narrationElementOutput, CallbackInfo ci) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("minecraft_access.gui.tab", getMessage()));
        narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.tab_navigation.usage"));

        if (position == null) {
            List<ClothConfigTabButton> buttons = ((ClothConfigScreenAccessor) screen).getTabButtons();
            //noinspection SuspiciousMethodCalls
            position = new NarrationMessages.Position(buttons.indexOf(this), buttons.size(), NarrationMessages.Position.Type.TAB);
        }
        narrationElementOutput.add(NarratedElementType.POSITION, position.toComponent());
    }
}
