package org.mcaccess.minecraftaccess.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public final class EffectNarrator {
    private static final String GAINED = "minecraft_access.effect_narration.gained";
    private static final String LOST = "minecraft_access.effect_narration.lost";

    private EffectNarrator() {
    }

    public static void narrateGained(MobEffectInstance effect) {
        // Don't narrate if the player already has the same effect
        // This will prevent duplicate narration of beacon, conduit power
        if (Minecraft.getInstance().player.hasEffect(effect.getEffect())) return;
        String effectName = NarrationUtils.narrateEffect(effect);
        MainClass.narrate(I18n.get(GAINED) + ' ' + effectName, false);
    }

    public static void narrateLost(MobEffect effect) {
        String effectName = I18n.get(effect.getDescriptionId());
        MainClass.narrate(I18n.get(LOST) + ' ' + effectName, false);
    }
}
