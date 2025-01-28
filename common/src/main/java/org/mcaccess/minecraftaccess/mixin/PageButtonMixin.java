package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PageButton.class)
public class PageButtonMixin {
    @Shadow @Final private boolean isForward;

    @Inject(at = @At("HEAD"), method = "renderWidget")
    public void renderButton(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci){
        if(this.isForward)
            ((AbstractWidgetAccessor)this).setMessage(Component.literal(I18n.get("minecraft_access.menus.book_screen.next_page_button_name")));
        else
            ((AbstractWidgetAccessor)this).setMessage(Component.literal(I18n.get("minecraft_access.menus.book_screen.previous_page_button_name")));
    }
}
