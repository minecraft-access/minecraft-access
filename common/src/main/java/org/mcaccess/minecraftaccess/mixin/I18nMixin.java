package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.utils.NamedFormatter;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Map;

@Mixin(I18n.class)
public class I18nMixin {
    @Unique
    private static Language minecraft_access$enLanguage;

    /**
     * Use NamedFormat.format() instead of String.format() (in original logic)
     * when translation key has "{}"
     */
    @SuppressWarnings("unchecked")
    @Inject(at = @At("HEAD"), method = "translate", cancellable = true)
    private static void useNamedFormatter(String key, Object[] args, CallbackInfoReturnable<String> cir) {
        if (args.length == 1 && args[0] instanceof Map) {
            Map<String, Object> params = (Map<String, Object>) args[0];

            String pattern = I18NAccessor.getLanguage().get(key);
            String result = NamedFormatter.format(pattern, params);

            // fallback to English
            if (result.startsWith("minecraft_access")) {
                pattern = minecraft_access$getEnglishI18Nof(key);
                result = NamedFormatter.format(pattern, params);
            }

            cir.setReturnValue(result);
            cir.cancel();
        }
    }

    /**
     * Add a fallback mechanism to I18n in case it fails on unsupported languages.
     */
    @Inject(at = @At("RETURN"), method = "translate", cancellable = true)
    private static void fallbackFailedI18NToEnglish(String key, Object[] args, CallbackInfoReturnable<String> cir) {
        // if result still is config key, That's because I18n failed, fallback to English
        if (cir.getReturnValue().startsWith("minecraft_access")) {
            String pattern = minecraft_access$getEnglishI18Nof(key);
            try {
                String result = String.format(pattern, args);
                cir.setReturnValue(result);
            } catch (IllegalFormatException illegalFormatException) {
                cir.setReturnValue("Format error: key:[" + key + "] args:[" + Arrays.toString(args) + "]");
            }
        }
    }


    @Unique
    private static String minecraft_access$getEnglishI18Nof(String key) {
        if (minecraft_access$enLanguage == null) {
            minecraft_access$enLanguage = Language.getInstance();
        }
        return minecraft_access$enLanguage.get(key);
    }

}
