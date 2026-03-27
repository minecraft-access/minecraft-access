package org.mcaccess.minecraftaccess.features;

import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.events.ServerLocal;

public class ToolCues implements BalmClientModule {
    private final ServerLocal<Boolean> fishingCastSoundPlayed = new ServerLocal<>(() -> false);
    private final ServerLocal<Boolean> fishingUncastSoundPlayed = new ServerLocal<>(() -> false);
    private final ServerLocal<Integer> lastSpearStatus = new ServerLocal<>(() -> -1);
    private final ServerLocal<Integer> lastBowStatus = new ServerLocal<>(() -> -1);

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "tool_cues");
    }

    @Override
    public void initialize() {
        ClientTickCallback.AFTER.register(this::tick);
    }

    private void tick(Minecraft client) {
        LocalPlayer player = client.player;

        if (player == null) return;

        ItemStack mainHandItemStack = player.getMainHandItem();
        ItemStack offHandItemStack = player.getOffhandItem();
        if (mainHandItemStack.isEmpty() && offHandItemStack.isEmpty()) {
            lastBowStatus.value = -1;
            lastSpearStatus.value = -1;
            fishingCastSoundPlayed.value = false;
            fishingUncastSoundPlayed.value = false;
            return;
        }

        Level level = client.level;

        if (mainHandItemStack.getItem() instanceof BowItem || offHandItemStack.getItem() instanceof BowItem) {
            float bowPullingProgress = BowItem.getPowerForTime(player.getTicksUsingItem());

            if (bowPullingProgress == 0.0f) {
                lastBowStatus.value = -1;
            } else if (bowPullingProgress >= 0.0f && bowPullingProgress < 0.50f && lastBowStatus.value != 0) {
                level.playPlayerSound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1.0F, 0.5F);
                lastBowStatus.value = 0;
            } else if (bowPullingProgress >= 0.50f && bowPullingProgress < 1.0f && lastBowStatus.value != 1) {
                level.playPlayerSound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                lastBowStatus.value = 1;
            } else if (bowPullingProgress == 1.0f && lastBowStatus.value != 2) {
                level.playPlayerSound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1.0F, 2.0F);
                lastBowStatus.value = 2;
            }
        } else {
            lastBowStatus.value = -1;
        }

        if (mainHandItemStack.getItem() instanceof TridentItem || offHandItemStack.getItem() instanceof TridentItem) {

        }

        if (mainHandItemStack.getItem() instanceof FishingRodItem || offHandItemStack.getItem() instanceof FishingRodItem) {
            if (player.fishing != null && !fishingCastSoundPlayed.value) {
                level.playPlayerSound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1.0F, 2.0F);
                fishingCastSoundPlayed.value = true;
                fishingUncastSoundPlayed.value = false;
            } else if (player.fishing == null && !fishingUncastSoundPlayed.value && fishingCastSoundPlayed.value) {
                level.playPlayerSound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1.0F, 0.8F);
                fishingCastSoundPlayed.value = false;
                fishingUncastSoundPlayed.value = true;
            }
        } else {
            fishingCastSoundPlayed.value = false;
            fishingUncastSoundPlayed.value = false;
        }

        if (mainHandItemStack.is(ItemTags.SPEARS) || offHandItemStack.is(ItemTags.SPEARS)) {

        } else {
            lastSpearStatus.value = -1;
        }
    }
}
