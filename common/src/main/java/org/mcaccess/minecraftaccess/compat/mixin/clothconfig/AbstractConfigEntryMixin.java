package com.github.khanshoaib3.minecraft_access.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractConfigEntry.class)
abstract class AbstractConfigEntryMixin<T> extends DynamicElementListWidget.ElementEntry<AbstractConfigEntry<T>> {
    @Shadow
    public abstract Text getFieldName();

    @Override
    public boolean isNarratable() {
        return true;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, getFieldName());
        super.appendNarrations(builder);
    }
}
