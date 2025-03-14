package org.mcaccess.minecraftaccess.features;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class EffectNarrator {
    private static final String GAINED = "minecraft_access.effect_narration.gained";
    private static final String LOST = "minecraft_access.effect_narration.lost";

    public static void narrateGained(MobEffectInstance effect) {
        String effectName = NarrationUtils.narrateEffect(effect);
        speakIfNotSame(I18n.get(GAINED) + " " + effectName);
    }

    public static void narrateLost(MobEffect effect) {
        String effectName = I18n.get(effect.getDescriptionId());
        speakIfNotSame(I18n.get(LOST) + " " + effectName);
    }

    private static void speakIfNotSame(String narration) {
        MainClass.speakWithNarrator(narration, false);
    }
}
