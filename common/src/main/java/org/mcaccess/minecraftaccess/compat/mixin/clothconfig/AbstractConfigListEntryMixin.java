package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractConfigListEntry.class)
abstract class AbstractConfigListEntryMixin<T> extends AbstractConfigEntry<T> {
    @Shadow
    @Final
    private Component fieldName;


    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
        builder.add(NarratedElementType.TITLE, this.fieldName);
        super.updateNarration(builder);
    }

    /**
     * For debugging convenience
     */
    @Override
    public String toString() {
        return this.getClass() + ": " + this.fieldName.getString();
    }
}
