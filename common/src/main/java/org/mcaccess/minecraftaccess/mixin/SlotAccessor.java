package com.github.khanshoaib3.minecraft_access.mixin;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Slot.class)
public interface SlotAccessor {
    /**
     * Returns the slot index, not to be confused with list index, this index is specific to the type of slot.
     * For example, if a slot's index is 0 then it is a hotbar slot if the slot's inventory is of type PlayerInventory while it is the item input slot for furnaces.
     * Refer to the specific screen handler classes for a screen to get information about the index of screen specific slots
     * @return the index of the slot
     */
    @Accessor
    int getIndex();
}
