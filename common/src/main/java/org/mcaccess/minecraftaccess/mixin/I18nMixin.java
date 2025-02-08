package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import org.mcaccess.minecraftaccess.utils.NamedFormatter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    @Inject(at = @At("HEAD"), method = "get", cancellable = true)
    private static void useNamedFormatter(String key, Object[] args, CallbackInfoReturnable<String> cir) {
        if (args.length == 1 && args[0] instanceof Map) {
            Map<String, Object> params = (Map<String, Object>) args[0];

            String pattern = I18NAccessor.getLanguage().getOrDefault(key);
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

    @Unique
    private static String minecraft_access$getEnglishI18Nof(String key) {
        if (minecraft_access$enLanguage == null) {
            minecraft_access$enLanguage = Language.getInstance();
        }
        return minecraft_access$enLanguage.getOrDefault(key);
    }

}
