package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(SystemToast.class)
public interface SystemToastAccessor {
    @Accessor
    Text getTitle();

    @Accessor
    List<OrderedText> getLines();
}
