package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(SelectionManager.class)
public interface SelectionManagerAccessor {
    @Accessor
    Supplier<String> getStringGetter();
}
