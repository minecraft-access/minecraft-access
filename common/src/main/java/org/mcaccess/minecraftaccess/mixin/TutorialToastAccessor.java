package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(TutorialToast.class)
public interface TutorialToastAccessor {
    @Accessor
    List<OrderedText> getText();
}
