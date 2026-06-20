package org.mcaccess.minecraftaccess.mixin;

import java.util.Map;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.mcaccess.minecraftaccess.utils.NamedFormatter;

@Mixin(I18n.class)
abstract class I18nMixin {
    @Unique
    private static Language enLanguage;

    /**
     * Use NamedFormat.format() instead of String.format() (in original logic)
     * when translation key has "{}".
     */
    @SuppressWarnings("unchecked")
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private static void useNamedFormatter(String key, Object[] args, CallbackInfoReturnable<String> cir) {
        if (args.length == 1 && args[0] instanceof Map) {
            Map<String, Object> params = (Map<String, Object>) args[0];

            String pattern = Language.getInstance().getOrDefault(key);
            String result = NamedFormatter.format(pattern, params);

            // fallback to English
            if (result.startsWith("minecraft_access")) {
                pattern = getEnglishI18Nof(key);
                result = NamedFormatter.format(pattern, params);
            }

            cir.setReturnValue(result);
            cir.cancel();
        }
    }

    @Unique
    private static String getEnglishI18Nof(String key) {
        if (enLanguage == null) {
            enLanguage = Language.getInstance();
        }
        return enLanguage.getOrDefault(key);
    }
}
