package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(TutorialToast.class)
public interface TutorialToastAccessor {
    @Accessor
    List<FormattedCharSequence> getLines();
}
