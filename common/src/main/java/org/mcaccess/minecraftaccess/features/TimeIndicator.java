package org.mcaccess.minecraftaccess.features;

import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;

public class TimeIndicator implements BalmClientModule {
    private Times previousTime = null;

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "time_indicator");
    }

    @Override
    public void initialize() {
        ClientTickCallback.ClientPlayerTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            previousTime = null;
        });
    }

    private void tick(Player player) {
        if (player.level().dimensionType().hasFixedTime() || player.level().dimensionType().hasCeiling()) return;
        if (!player.level().canSeeSky(BlockPos.containing(player.getEyePosition()))) return;

        Times currentTime;

        long time = player.level().getDayTime() % 24000;

        if (time >= 10000 && time <= 12999) {
            currentTime = Times.AFTERNOON;
        } else if (time >= 13000 && time <= 23999) {
            currentTime = Times.NIGHT;
        } else {
            currentTime = Times.DAY;
        }

        if (currentTime == previousTime) return;

        switch (currentTime) {
            case AFTERNOON -> MainClass.narrate(I18n.get("minecraft_access.time.afternoon"), false);
            case DAY -> MainClass.narrate(I18n.get("minecraft_access.time.day"), false);
            case NIGHT -> MainClass.narrate(I18n.get("minecraft_access.time.night"), false);
        }

        previousTime = currentTime;
    }

    private enum Times {
        DAY,
        AFTERNOON,
        NIGHT
    }
}
