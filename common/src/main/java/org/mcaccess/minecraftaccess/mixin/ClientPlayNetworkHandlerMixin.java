package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Slf4j
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements TickablePacketListener, ClientPlayPacketListener {
    @Shadow
    private ClientWorld world;

    @Inject(at = @At("HEAD"), method = "onItemPickupAnimation")
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        // This method will be executed at the head of injected method.
        // Invoke ahead here do no harm, according to this method's implementation.
        NetworkThreadUtils.forceMainThread(packet, this, client);

        if (!OtherConfigsMap.getInstance().isFishingHarvestEnabled()) return;

        ClientPlayerEntity player = client.player;
        if (player == null) return;
        // Am "I" holding a fishing pod? (will be Air if holding nothing)
        if (player.getMainHandStack().getItem() instanceof FishingRodItem) {
            int cId = packet.getCollectorEntityId();
            int pId = player.getId();
            // Is this item picked by "me" or other players?
            if (cId == pId) {
                Entity entity = this.world.getEntityById(packet.getEntityId());
                // This item might be an ExperienceOrbEntity and we don't want to speak this sort of thing.
                if (entity instanceof ItemEntity itemEntity) {
                    String name = I18n.translate(itemEntity.getStack().getItem().getTranslationKey());
                    log.debug("Fishing harvest: %s".formatted(name));

                    // Have observed this speak will interrupt adventure achievement, level up notification or so,
                    // it should be at low priority.
                    MainClass.speakWithNarrator(name, false);
                }
            }
        }
    }
}
