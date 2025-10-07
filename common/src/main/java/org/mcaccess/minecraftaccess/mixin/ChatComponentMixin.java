package org.mcaccess.minecraftaccess.mixin;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.sounds.SoundEvents;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    // Adds a sound whenever a chat message is sent or received if the option is enabled in the config
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"))
    public void addMessageMixin(Component chatComponent, MessageSignature headerSignature, GuiMessageTag tag, CallbackInfo ci) {
        if (Config.getInstance().features.playNewChatMessageSound) {
            WorldUtils.getClientPlayer().playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, Config.getInstance().features.newChatMessageSoundVolume, 1);
        }
    }
}
