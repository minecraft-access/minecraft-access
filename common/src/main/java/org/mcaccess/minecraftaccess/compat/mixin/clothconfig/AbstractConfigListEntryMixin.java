package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractConfigListEntry.class)
public class AbstractConfigListEntryMixin {
    @Shadow
    @Final
    private Component fieldName;

    /**
     * For debugging convenience
     */
    @Override
    public String toString() {
        return this.getClass() + ": " + this.fieldName.getString();
    }
}
