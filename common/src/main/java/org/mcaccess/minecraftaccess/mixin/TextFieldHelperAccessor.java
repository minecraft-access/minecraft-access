package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.font.TextFieldHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(TextFieldHelper.class)
public interface TextFieldHelperAccessor {
    @Accessor
    Supplier<String> getGetMessageFn();
}
