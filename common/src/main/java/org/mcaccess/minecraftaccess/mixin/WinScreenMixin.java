package org.mcaccess.minecraftaccess.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WinScreen.class)
public class WinScreenMixin {
    @ModifyReturnValue(at = @At("RETURN"), method = "getNarrationMessage")
    private Component addCreditsTip(Component original) {
        return Component.translatable("minecraft_access.credits_screen.started_tip")
                .append(Component.translatable("minecraft_access.other.words_connection"))
                .append(original);
    }
}
