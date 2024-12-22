package com.github.khanshoaib3.minecraft_access.mixin;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(I18n.class)
public interface I18NAccessor {
    @Accessor
    static Language getLanguage() {
        throw new AssertionError();
    }
}
