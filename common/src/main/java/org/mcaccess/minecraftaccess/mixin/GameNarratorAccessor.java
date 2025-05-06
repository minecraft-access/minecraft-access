package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.GameNarrator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameNarrator.class)
public interface GameNarratorAccessor {
    @Invoker("narrateMessage")
    void invokeNarrateMessage(String text, boolean interrupt);
}
