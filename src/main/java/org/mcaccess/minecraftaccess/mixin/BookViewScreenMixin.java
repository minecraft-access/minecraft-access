package org.mcaccess.minecraftaccess.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.mcaccess.minecraftaccess.MainClass;

@Mixin(BookViewScreen.class)
abstract class BookViewScreenMixin {
    @Shadow
    private int currentPage;

    @Shadow
    private BookViewScreen.BookAccess bookAccess;

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void repeatPageContents(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (event.key() == InputConstants.KEY_R) {
            MainClass.narrate(bookAccess.getPage(currentPage).getString(), true);
        }
    }
}
