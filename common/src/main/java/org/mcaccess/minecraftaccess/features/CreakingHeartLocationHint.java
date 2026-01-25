package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.balm.platform.event.callback.PlayerCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;

public class CreakingHeartLocationHint implements BalmClientModule {
    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "creaking_heart_location_hint");
    }

    @Override
    public void initialize() {
        PlayerCallback.Attack.Before.EVENT.register(this::playSoundAtHeart);
    }

    private boolean playSoundAtHeart(Player player, Entity target) {
        if (target instanceof Creaking creaking) {
            if (creaking.getHomePos() == null) return  true;
                player.level().playLocalSound(creaking.getHomePos(), SoundEvents., SoundSource.HOSTILE, Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.HOSTILE), 1.0F, false);
        }
        return  true;
    }
}
