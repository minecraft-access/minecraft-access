package org.mcaccess.minecraftaccess.mixin;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.FishingRodItem;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Slf4j
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin implements TickablePacketListener, ClientGamePacketListener {
    @Shadow
    private ClientLevel level;

    @Inject(at = @At("HEAD"), method = "handleTakeItemEntity")
    public void handleTakeItemEntity(ClientboundTakeItemEntityPacket packet, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) return;

        PacketUtils.ensureRunningOnSameThread(packet, this, client);
        Config.Features config = Config.getInstance().features;
        if (config.alwaysSpeakPickedUpItemsEnabled || (config.fishingHarvestEnabled && player.getMainHandItem().getItem() instanceof FishingRodItem)) {
            int cId = packet.getPlayerId();
            int pId = player.getId();
            // Is this item picked by "me" or other players?
            if (cId == pId) {
                Entity entity = this.level.getEntity(packet.getItemId());
                // This item might be an ExperienceOrbEntity and we don't want to speak this sort of thing.
                if (entity instanceof ItemEntity itemEntity) {
                    String name = I18n.get(itemEntity.getItem().getItem().getDescriptionId());
                    log.debug("Fishing harvest: %s".formatted(name));

                    // Have observed this speak will interrupt adventure achievement, level up notification or so,
                    // it should be at low priority.
                    MainClass.speakWithNarrator(I18n.get("minecraft_access.other.picked_up_item", name), false);
                }
            }
        }
    }
}
