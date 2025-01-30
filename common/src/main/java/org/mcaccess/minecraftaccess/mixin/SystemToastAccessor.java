package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(SystemToast.class)
public interface SystemToastAccessor {
    @Accessor
    Component getTitle();

    @Accessor
    List<FormattedCharSequence> getMessageLines();
}
