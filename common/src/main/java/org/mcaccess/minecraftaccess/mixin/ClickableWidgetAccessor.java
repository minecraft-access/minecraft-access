package com.github.khanshoaib3.minecraft_access.mixin;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClickableWidget.class)
public interface ClickableWidgetAccessor {
    @Accessor
    void setMessage(Text message);

    @Invoker
    int callGetX();

    @Invoker
    int callGetY();
}
