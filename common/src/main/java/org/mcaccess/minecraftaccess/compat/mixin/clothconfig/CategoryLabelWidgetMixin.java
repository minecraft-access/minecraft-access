package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SubCategoryListEntry.CategoryLabelWidget.class, remap = false)
abstract class CategoryLabelWidgetMixin implements GuiEventListener, NarratableEntry {
    @Shadow
    @Final
    private Rectangle rectangle;

    /**
     * Make the label widget expandable through keyboard.
     * Although this widget is treated as one of {@link SubCategoryListEntry#children()},
     * it's this very widget's job to handle mouse operation.
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        Window window = Minecraft.getInstance().getWindow();
        if (InputConstants.isKeyDown(window, InputConstants.KEY_SPACE)
                || InputConstants.isKeyDown(window, InputConstants.KEY_RETURN)
                || InputConstants.isKeyDown(window, InputConstants.KEY_NUMPADENTER)) {
            mouseClicked(new MouseButtonEvent(rectangle.x + 1, rectangle.y + 1, new MouseButtonInfo(0, 0)), false);
            return true;
        }
        return false;
    }

    @Inject(method = "narrationPriority", at = @At("TAIL"), remap = true, cancellable = true)
    private void neverNarrateLabel(CallbackInfoReturnable<NarrationPriority> cir) {
        cir.setReturnValue(NarrationPriority.NONE);
    }
}
