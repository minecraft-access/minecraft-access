package org.mcaccess.minecraftaccess.utils.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.mcaccess.minecraftaccess.MainClass;

@Slf4j
public final class ConfigExtension {
    private static final Pattern FORMAT_STRING_PLACEHOLDER = Pattern.compile("%(?<type>[^%])");

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
        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> {
                    Registry annotation = field.getAnnotation(Registry.class);
                    return Collections.singletonList(new RegistryReorderable(annotation.registry(), annotation.i18n(), i18n, field, config, defaults));
                },
                field -> field.getType() == ResourceLocation[].class,
                Registry.class
        );
        registry.registerAnnotationTransformer(
                (guis, i18n, field, config, defaults, registryAccess) -> {
                    FormatString annotation = field.getAnnotation(FormatString.class);
                    return guis.stream()
                            .peek(gui -> {
                                @SuppressWarnings("unchecked")
                                AbstractConfigListEntry<String> entry = gui;
                                entry.setErrorSupplier(() -> {
                                    String value = (String) gui.getValue();
                                    if (!validateFormatString(value, annotation.value())) {
                                        return Optional.of(Component.translatable("minecraft_access.config.invalid_format_string"));
                                    }
                                    return Optional.empty();
                                });
                            })
                            .toList();
                },
                field -> field.getType() == String.class,
                FormatString.class
        );
    }

    public static <T> void validate(T config, T defaults) {
        for (Field field : config.getClass().getFields()) {
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(ConfigEntry.Gui.Excluded.class)) {
                continue;
            }
            if (field.isAnnotationPresent(ConfigEntry.Gui.TransitiveObject.class) || field.isAnnotationPresent(ConfigEntry.Gui.CollapsibleObject.class)) {
                validate(getField(config, field), getField(defaults, field));
            }
            if (field.isAnnotationPresent(FormatString.class) && field.getType() == String.class) {
                FormatString annotation = field.getAnnotation(FormatString.class);
                String value = getField(config, field);
                if (!validateFormatString(value, annotation.value())) {
                    log.error("Invalid format string \"{}\"", value);
                    setField(config, field, getField(defaults, field));
                }
            }
            if (field.isAnnotationPresent(Registry.class) && field.getType() == ResourceLocation.class) {
                Registry annotation = field.getAnnotation(Registry.class);
                ResourceLocation value = getField(config, field);
                if (!MainClass.registry(annotation.registry()).containsKey(value)) {
                    log.error("Invalid registry value \"{}\"", value);
                    setField(config, field, getField(defaults, field));
                }
            }
            if (field.isAnnotationPresent(Registry.class) && field.getType() == ResourceLocation[].class) {
                Registry annotation = field.getAnnotation(Registry.class);
                ResourceLocation[] value = getField(config, field);
                ResourceLocation[] filtered = Arrays.stream(value)
                        .filter(MainClass.registry(annotation.registry())::containsKey)
                        .toArray(ResourceLocation[]::new);
                if (!Arrays.equals(value, filtered)) {
                    log.error("Invalid registry values {}", Arrays.stream(value).filter(key -> !Arrays.asList(filtered).contains(key)).toList());
                    setField(config, field, filtered);
                }
            }
        }
    }

    public static <T extends ConfigData> ConfigSerializer<T> serialiser(Config definition, Class<T> configClass) {
        Gson gson = new GsonBuilder()
                .setFormattingStyle(FormattingStyle.PRETTY.withIndent("    "))
                .registerTypeAdapter(ResourceLocation.class, new ResourceLocationAdapter())
                .create();
        return new GsonConfigSerializer<>(definition, configClass, gson);
    }

    @SuppressWarnings("unchecked")
    static <T> T getField(Object config, Field field) {
        try {
            return (T) field.get(config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static void setField(Object config, Field field, Object value) {
        try {
            field.set(config, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean validateFormatString(String string, char[] placeholders) {
        Matcher matcher = FORMAT_STRING_PLACEHOLDER.matcher(string);
        for (char type : placeholders) {
            if (!matcher.find()) {
                return false;
            }
            if (!Objects.equals(matcher.group("type"), String.valueOf(type))) {
                return false;
            }
        }
        return !matcher.find();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Registry {
        Class<?> registry();

        String i18n();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface FormatString {
        char[] value();
    }
}
