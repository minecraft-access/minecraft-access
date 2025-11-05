package org.mcaccess.minecraftaccess.utils.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.resources.ResourceLocation;

public final class ConfigExtension {
    private ConfigExtension() {
    }

    public static void apply(GuiRegistry registry) {
        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> {
                    Registry annotation = field.getAnnotation(Registry.class);
                    return Collections.singletonList(new RegistrySingleSelect(annotation.registry(), annotation.i18n(), i18n, field, config, defaults));
                },
                field -> field.getType() == ResourceLocation.class,
                Registry.class
        );
    }

    public static <T extends ConfigData> ConfigSerializer<T> serialiser(Config definition, Class<T> configClass) {
        Gson gson = new GsonBuilder()
                .setFormattingStyle(FormattingStyle.PRETTY.withIndent("    "))
                .registerTypeAdapter(ResourceLocation.class, new ResourceLocationAdapter())
                .create();
        return new GsonConfigSerializer<>(definition, configClass, gson);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Registry {
        Class<?> registry();

        String i18n();
    }

}
