package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Checkbox.class)
abstract class CheckboxMixin extends AbstractWidget {
    @Shadow
    private boolean selected;

    CheckboxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    /**
     * @author emassey0135
     * @reason the patched logic is completely different from the original,
     *     and we want to suppress the execution of original logic
     */
    @Override
    @Overwrite
    public void updateWidgetNarration(NarrationElementOutput output) {
        if (selected) {
            output.add(NarratedElementType.TITLE, Component.translatable("minecraft_access.gui.checkbox_checked", getMessage()));
        } else {
            output.add(NarratedElementType.TITLE, Component.translatable("minecraft_access.gui.checkbox_unchecked", getMessage()));
        }
    }
}
