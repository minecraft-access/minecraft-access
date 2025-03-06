package org.mcaccess.minecraftaccess.features;

import lombok.Getter;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.effect.MobEffectInstance;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;

import java.util.ArrayList;
import java.util.List;

public class EffectNarration {
    @Getter
    private static EffectNarration instance = new EffectNarration();

    private List<MobEffectInstance> previousEffects = new ArrayList<>();

    public void update() {
        List<MobEffectInstance> currentEffects = new ArrayList<>(WorldUtils.getClientPlayer().getActiveEffects());

        List<MobEffectInstance> newEffects = new ArrayList<>();
        List<MobEffectInstance> lostEffects = new ArrayList<>();

        for (MobEffectInstance effect : currentEffects) {
            boolean isNewEffect = true;
            for (MobEffectInstance previousEffect : previousEffects) {
                if (effect.getEffect().equals(previousEffect.getEffect())) {
                    isNewEffect = false;
                    break;
                }
            }
            if (isNewEffect) {
                newEffects.add(effect);
            }
        }

        for (MobEffectInstance effect : previousEffects) {
            boolean isLostEffect = true;
            for (MobEffectInstance currentEffect : currentEffects) {
                if (effect.getEffect().equals(currentEffect.getEffect())) {
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
            toSpeak.append(I18n.get("minecraft_access.effect_narration.gained")).append(" ");
            for (MobEffectInstance effect : newEffects) {
                toSpeak.append(I18n.get(effect.getDescriptionId())).append(", ");
            }
        }

        if (!lostEffects.isEmpty()) {
            toSpeak.append(I18n.get("minecraft_access.effect_narration.lost")).append(" ");
            for (MobEffectInstance effect : lostEffects) {
                toSpeak.append(I18n.get(effect.getDescriptionId())).append(", ");
            }
        }

        MainClass.speakWithNarratorIfNotEmpty(toSpeak.toString(), true);

        previousEffects = currentEffects;
    }

    public void narrateCurrentPlayerEffects() {
        if (WorldUtils.getClientPlayer().getActiveEffects().isEmpty()) {
            MainClass.speakWithNarrator(I18n.get("minecraft_access.effect_narration.no_effects"), true);
            return;
        }

        StringBuilder toSpeak = new StringBuilder();
        for (MobEffectInstance effect : WorldUtils.getClientPlayer().getActiveEffects()) {
            toSpeak.append(NarrationUtils.narrateEffect(effect));
        }

        MainClass.speakWithNarrator(toSpeak.toString(), true);
    }
}
