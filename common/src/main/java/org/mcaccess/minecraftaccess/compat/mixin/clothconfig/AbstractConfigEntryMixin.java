package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractConfigEntry.class)
abstract class AbstractConfigEntryMixin<T> extends DynamicElementListWidget.ElementEntry<AbstractConfigEntry<T>> {
    @Shadow
    public abstract Component getFieldName();

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
        builder.add(NarratedElementType.TITLE, getFieldName());
        super.updateNarration(builder);
    }
}
