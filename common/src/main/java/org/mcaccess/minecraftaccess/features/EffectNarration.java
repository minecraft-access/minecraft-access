package org.mcaccess.minecraftaccess.features;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffectInstance;

public class EffectNarration {
    @Getter
    private static EffectNarration instance = new EffectNarration();

    public void update() {
        if (MinecraftClient.getInstance() == null) return;
        if (MinecraftClient.getInstance().currentScreen != null) return;
        if (MinecraftClient.getInstance().player == null) return;

        if (KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().speakEffectsKey)) {
            if (MinecraftClient.getInstance().player.getStatusEffects().isEmpty()) {
                MainClass.speakWithNarrator(I18n.translate("minecraft_access.effect_narration.no_effects"), true);
                return;
            }

            String toSpeak = "";
            for (StatusEffectInstance effect : MinecraftClient.getInstance().player.getStatusEffects()) {
                toSpeak += NarrationUtils.narrateEffect(effect);
            }

            MainClass.speakWithNarrator(toSpeak, true);
        }
    }
}
