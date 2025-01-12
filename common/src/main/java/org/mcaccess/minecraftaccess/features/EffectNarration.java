package org.mcaccess.minecraftaccess.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private List<StatusEffectInstance> previousEffects = new ArrayList<>();

    public void update() {
        if (MinecraftClient.getInstance() == null) return;
        if (MinecraftClient.getInstance().currentScreen != null) return;
        if (MinecraftClient.getInstance().player == null) return;

        if (KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().speakEffectsKey)) {
            if (MinecraftClient.getInstance().player.getStatusEffects().isEmpty()) {
                MainClass.speakWithNarrator(I18n.translate("minecraft_access.effect_narration.no_effects"), true);
                return;
            }

            StringBuilder toSpeak = new StringBuilder();
            for (StatusEffectInstance effect : MinecraftClient.getInstance().player.getStatusEffects()) {
                toSpeak.append(NarrationUtils.narrateEffect(effect));
            }

            MainClass.speakWithNarrator(toSpeak.toString(), true);
        }

        List<StatusEffectInstance> currentEffects = new ArrayList<>(MinecraftClient.getInstance().player.getStatusEffects());

        List<StatusEffectInstance> newEffects = new ArrayList<>();
        List<StatusEffectInstance> lostEffects = new ArrayList<>();

        for (StatusEffectInstance effect : currentEffects) {
            boolean isNewEffect = true;
            for (StatusEffectInstance previousEffect : previousEffects) {
                if (effect.getEffectType().equals(previousEffect.getEffectType())) {
                    isNewEffect = false;
                    break;
                }
            }
            if (isNewEffect) {
                newEffects.add(effect);
            }
        }

        for (StatusEffectInstance effect : previousEffects) {
            boolean isLostEffect = true;
            for (StatusEffectInstance currentEffect : currentEffects) {
                if (effect.getEffectType().equals(currentEffect.getEffectType())) {
                    isLostEffect = false;
                    break;
                }
            }
            if (isLostEffect) {
                lostEffects.add(effect);
            }
        }

        StringBuilder toSpeak = new StringBuilder();

        if (!newEffects.isEmpty()) {
            toSpeak.append(I18n.translate("minecraft_access.effect_narration.gained")).append(" ");
            for (StatusEffectInstance effect : newEffects) {
                toSpeak.append(I18n.translate(effect.getTranslationKey())).append(", ");
            }
        }

        if (!lostEffects.isEmpty()) {
            toSpeak.append(I18n.translate("minecraft_access.effect_narration.lost")).append(" ");
            for (StatusEffectInstance effect : lostEffects) {
                toSpeak.append(I18n.translate(effect.getTranslationKey())).append(", ");
            }
        }

        MainClass.speakWithNarratorIfNotEmpty(toSpeak.toString(), true);

        previousEffects = currentEffects;
    }
}
