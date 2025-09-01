package org.mcaccess.minecraftaccess.features;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.resources.language.I18n;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.mixin.BossHealthOverlayAccessor;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;

public class HUDStatus {
    private Minecraft client = Minecraft.getInstance().getInstance();
    private Boolean wasHidden = Minecraft.getInstance().options.hideGui;
    private boolean bossbarKeyIsDown = false;
    private int bossIndex = 0;

    public void tick() {
        Boolean isHidden = client.options.hideGui;

        if (wasHidden != isHidden) {
            MainClass.narrate(I18n.get(String.format("minecraft_access.hud_status.announce_%s", isHidden ? "hidden" : "shown")), true);
            wasHidden = isHidden;
        }

        if (KeyBindingsHandler.NARRATE_BOSSBARS_KEY.mapping.consumeClick()) {
            if (!bossbarKeyIsDown) {
                bossbarKeyIsDown = true;
                narrateBossBars();
            }
        } else if (!KeyBindingsHandler.NARRATE_BOSSBARS_KEY.mapping.isDown()) {
            bossbarKeyIsDown = false;
        }
    }

    private void narrateBossBars() {
        List<LerpingBossEvent> bosses = new ArrayList<>(
                ((BossHealthOverlayAccessor) client.gui.getBossOverlay()).getEvents().values()
        );

        if (bosses.isEmpty()) {
            MainClass.narrate(I18n.get("minecraft_access.other.no_bossbars"), true);
            return;
        }

        bossIndex = (bossIndex + 1) % bosses.size();
        LerpingBossEvent currentBoss = bosses.get(bossIndex);
        String name = currentBoss.getName().getString();
        int healthPercent = Math.round(currentBoss.getProgress() * 100);
        MainClass.narrate(I18n.get("minecraft_access.other.bossbar_status", name, healthPercent), true);
    }
}
