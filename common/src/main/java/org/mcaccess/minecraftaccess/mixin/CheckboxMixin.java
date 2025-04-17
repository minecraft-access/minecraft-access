package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Checkbox.class)
public abstract class CheckboxMixin extends AbstractWidget {
    @Shadow
    private boolean selected;

    /**
     * @author emassey0135
     * @reason the patched logic is completely different from the original,
     * and we want to suppress the execution of original logic
     */
    @Overwrite
    public void updateWidgetNarration(NarrationElementOutput builder) {
        if (selected) {
            builder.add(NarratedElementType.TITLE, Component.translatable("minecraft_access.gui.checkbox_checked", this.getMessage()));
        } else {
            builder.add(NarratedElementType.TITLE, Component.translatable("minecraft_access.gui.checkbox_unchecked", this.getMessage()));
        }
    }

    CheckboxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }
}
