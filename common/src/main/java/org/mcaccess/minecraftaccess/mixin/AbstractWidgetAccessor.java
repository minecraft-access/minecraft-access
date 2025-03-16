package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {
    @Accessor
    void setMessage(Component message);

    @Invoker
    int callGetX();

    @Invoker
    int callGetY();
}
