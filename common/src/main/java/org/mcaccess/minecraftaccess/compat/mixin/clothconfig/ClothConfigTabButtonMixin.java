package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import org.mcaccess.minecraftaccess.utils.NarrationMessages;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ClothConfigTabButton.class, remap = false)
abstract class ClothConfigTabButtonMixin extends PressableWidget {
    @Final
    @Shadow
    private ClothConfigScreen screen;
    @Unique
    private NarrationMessages.Position mca$position;

    ClothConfigTabButtonMixin(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Inject(at = @At("HEAD"), method = "appendClickableNarrations")
    void appendClickableNarrations(NarrationMessageBuilder builder, CallbackInfo ci) {
        builder.put(NarrationPart.TITLE, this.getNarrationMessage());
        builder.put(NarrationPart.USAGE, Text.translatable("narration.tab_navigation.usage"));

        if (this.mca$position == null) {
            List<ClothConfigTabButton> buttons = ((ClothConfigScreenAccessor) this.screen).getTabButtons();
            //noinspection SuspiciousMethodCalls
            this.mca$position = new NarrationMessages.Position(buttons.indexOf(this), buttons.size(), NarrationMessages.Position.Type.TAB);
        }
        builder.put(NarrationPart.POSITION, mca$position.toText());
    }
}
