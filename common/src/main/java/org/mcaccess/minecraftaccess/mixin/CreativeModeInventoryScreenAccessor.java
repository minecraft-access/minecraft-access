package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeModeInventoryScreenAccessor {
    @Accessor
    static CreativeModeTab getSelectedTab() {
        throw new UnsupportedOperationException();
    }

    @Invoker
    void invokeSelectTab(CreativeModeTab group);

    @Accessor
    EditBox getSearchBox();
}