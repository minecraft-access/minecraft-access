package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoomScreen.class)
public interface LoomScreenAccessor {
    @Accessor
    boolean isDisplayPatterns();

    @Accessor
    int getStartRow();

    @Accessor
    ItemStack getDyeStack();
}
