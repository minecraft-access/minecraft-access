package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.toast.AdvancementToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementToast.class)
public interface AdvancementToastAccessor {
    @Accessor
    AdvancementEntry getAdvancement();
}
