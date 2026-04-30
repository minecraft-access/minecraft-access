package org.mcaccess.minecraftaccess.utils.i18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.demonwav.mcdev.annotations.Translatable;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.mixin.I18NAccessor;

@Slf4j
@ApiStatus.Internal
public class Translation implements Narratable {
    private static final @NotNull Pattern PLACEHOLDER_REGEX = Pattern.compile("\\{(?<placeholder>\\w+)}");

    private final @NotNull String key;
    private @Nullable String variant;
    private @Nullable String fallback;
    private final @NotNull Map<@NotNull String, Supplier<@NotNull String>> variables = new HashMap<>();

    @Contract(pure = true)
    public Translation(@NotNull @Translatable(required = false) String key) {
        if (key.contains("/")) {
            this.key = key.split("/")[0];
            variant = key.split("/")[0];
        } else {
            this.key = key;
        }
    }

    @Contract(pure = true)
    public Translation(@NotNull String type, @NotNull Identifier identifier) {
        this(identifier.toLanguageKey(type));
    }

    @Contract(pure = true)
    public Translation(@NotNull String type, @NotNull ResourceKey<?> key) {
        this(type, key.identifier());
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Optional<@NotNull Translation> fromHolder(@NotNull String type, @NotNull Holder<?> holder) {
        return holder.unwrapKey().map(key -> new Translation(type, key));
    }

    @Contract(value = "-> new", pure = true)
    public @NotNull Translation copy() {
        Translation translation = new Translation(key);
        translation.variant = variant;
        translation.fallback = fallback;
        translation.variables.putAll(variables);
        return translation;
    }

    @Contract(value = "-> new", pure = true)
    public @NotNull Unmodifiable unmodifiableView() {
        return new Unmodifiable();
    }

    @Contract(pure = true)
    private @NotNull String getKey() {
        return variant != null ? String.format("%s/%s", key, variant) : key;
    }

    @Contract(pure = true)
    public boolean exists() {
        return I18NAccessor.getLanguage().has(getKey());
    }

    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull Translation variant(@Nullable String variant) {
        this.variant = variant;
        return this;
    }

    @Contract(value = "_, _ -> this", mutates = "this")
    public @NotNull Translation variant(@NotNull String variant, boolean apply) {
        return variant(apply ? variant : null);
    }

    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull Translation fallback(@Nullable String fallback) {
        this.fallback = fallback;
        return this;
    }

    public @NotNull Formatter<@NotNull Translation> variable(@NotNull String variable) {
        return new Formatter<>() {
            @Override
            @NotNull Translation apply(@Nullable Supplier<@NotNull String> value) {
                if (value != null) {
                    variables.put(variable, value);
                }
                return Translation.this;
            }
        };
    }

    @Override
    public @NotNull String getString() {
        if (!exists() && fallback == null) {
            log.warn("Untranslated key: {}", getKey());
        }
        return PLACEHOLDER_REGEX.matcher(I18NAccessor.getLanguage().getOrDefault(getKey(), fallback != null ? fallback : getKey()))
                .replaceAll(match -> {
                    String placeholder = match.group("placeholder");
                    if (!variables.containsKey(placeholder)) {
                        log.warn("Missing placeholder {} for string {}", placeholder, getKey());
                    }
                    return variables.getOrDefault(placeholder, String::new).get();
                });
    }

    public final class Unmodifiable implements Narratable {
        private Unmodifiable() {
        }

        @Contract(value = "-> new", pure = true)
        public @NotNull Translation copy() {
            return Translation.this.copy();
        }

        @Override
        public @NotNull String getString() {
            return Translation.this.getString();
        }
    }

    public static class Delimited extends Formatter<@NotNull Delimited> implements Narratable {
        private final @NotNull Translation delimeter;
        private final @NotNull List<@NotNull Supplier<@NotNull String>> contents = new ArrayList<>();

        public Delimited(@NotNull Translation delimeter) {
            this.delimeter = delimeter;
        }

        public Delimited(@NotNull @Translatable String delimeter) {
            this(new Translation(delimeter));
        }

        public Delimited() {
            this("minecraft_access.other.words_connection");
        }

        @Override
        @NotNull Delimited apply(@Nullable Supplier<@NotNull String> value) {
            if (value != null) {
                contents.add(value);
            }
            return this;
        }

        @Override
        public @NotNull String getString() {
            return contents.stream()
                    .map(Supplier::get)
                    .collect(Collectors.joining(delimeter.getString()));
        }
    }

    public static class Vanilla extends Formatter<@NotNull Vanilla> implements Narratable {
        private final @NotNull String key;
        private final @NotNull List<@NotNull Supplier<@NotNull String>> variables = new ArrayList<>();

        public Vanilla(@NotNull @Translatable String key) {
            this.key = key;
        }

        @Contract(pure = true)
        public Vanilla(@NotNull String type, @NotNull Identifier identifier) {
            this(identifier.toLanguageKey(type));
        }

        @Contract(pure = true)
        public Vanilla(@NotNull String type, @NotNull ResourceKey<?> key) {
            this(type, key.identifier());
        }

        @Contract(value = "_, _ -> new", pure = true)
        public static @NotNull Optional<@NotNull Vanilla> fromHolder(@NotNull String type, @NotNull Holder<?> holder) {
            return holder.unwrapKey().map(key -> new Vanilla(type, key));
        }

        @Override
        @NotNull Vanilla apply(@Nullable Supplier<@NotNull String> value) {
            if (value != null) {
                variables.add(value);
            }
            return this;
        }

        @Override
        public @NotNull String getString() {
            return I18n.get(key, variables.stream().map(Supplier::get).toArray());
        }
    }
}
