package com.github.khanshoaib3.minecraft_access.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    @Accessor
    InputUtil.Key getBoundKey();

    @Accessor
    int getTimesPressed();

    @Accessor
    void setTimesPressed(int timesPressed);
}
