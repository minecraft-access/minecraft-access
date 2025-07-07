package org.mcaccess.minecraftaccess.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookViewScreen.class)
public class BookViewScreenMixin {
    @Shadow
    private int currentPage;
    @Shadow
    private BookViewScreen.BookAccess bookAccess;

    @Inject(at = @At("HEAD"), method = "keyPressed")
    public void repeatPageContents(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == InputConstants.KEY_R) {
            MainClass.speakWithNarrator(this.bookAccess.getPage(this.currentPage).getString(), true);
        }
    }
}
