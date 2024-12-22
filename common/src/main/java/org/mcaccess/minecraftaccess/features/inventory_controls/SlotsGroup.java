package com.github.khanshoaib3.minecraft_access.features.inventory_controls;

import com.github.khanshoaib3.minecraft_access.MainClass;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SlotsGroup {
    private final @NotNull String groupKey;
    private final @Nullable String groupName;
    private final @Nullable Byte index;
    public List<SlotItem> slotItems;
    public boolean isScrollable = false;

    private final HashMap<Slot, String> slotNamePrefixMap;

    public SlotsGroup(@NotNull String groupKey, @Nullable String groupName, @Nullable Byte index, @Nullable List<SlotItem> slotItems) {
        this.slotNamePrefixMap = new HashMap<>();
        this.groupKey = groupKey;
        this.groupName = groupName;
        this.index = index;
        this.slotItems = Objects.requireNonNullElseGet(slotItems, ArrayList::new);
    }

    public SlotsGroup(@NotNull String groupKey, @Nullable List<SlotItem> slotItems) {
        this(groupKey, null, null, slotItems);
    }

    public SlotsGroup(@NotNull String groupKey) {
        this(groupKey, null, null, null);
    }

    public void setSlotPrefix(Slot slot, String prefix) {
        this.slotNamePrefixMap.put(slot, prefix);
    }

    public String getSlotPrefix(Slot slot) {
        String output = this.slotNamePrefixMap.get(slot);
        return output != null ? output : "";
    }

    public SlotItem getFirstGroupItem() {
        return this.slotItems.get(0);
    }

    public SlotItem getLastGroupItem() {
        return this.slotItems.get(this.slotItems.size() - 1);
    }

    public boolean hasSlotItemAbove(@NotNull SlotItem slotItem) {
        return (slotItem.upSlotItem != null) || (slotItem.y != this.getFirstGroupItem().y);
    }

    public boolean hasSlotItemBelow(@NotNull SlotItem slotItem) {
        return (slotItem.downSlotItem != null) || (slotItem.y != this.getLastGroupItem().y);
    }

    public boolean hasSlotItemLeft(@NotNull SlotItem slotItem) {
        return (slotItem.leftSlotItem != null) || (slotItem.x != this.getFirstGroupItem().x);
    }

    public boolean hasSlotItemRight(@NotNull SlotItem slotItem) {
        return (slotItem.rightSlotItem != null) || (slotItem.x != this.getLastGroupItem().x);
    }

    void mapTheGroupList(int factor) {
        mapTheGroupList(factor, false);
    }

    // Maps the list into 2d form like a matrix, the factor being the no. of columns and transpose is whether to transpose the matrix or not
    void mapTheGroupList(int factor, boolean transpose) {
        int size = this.slotItems.size();
        for (int i = 0; i < size; i++) {
            int above = i - factor;
            int right = i + 1;
            int below = i + factor;
            int left = i - 1;

            if (above >= 0)
                if (transpose)
                    this.slotItems.get(i).leftSlotItem = this.slotItems.get(above);
                else
                    this.slotItems.get(i).upSlotItem = this.slotItems.get(above);

            if (right < size && right % factor != 0)
                if (transpose)
                    this.slotItems.get(i).downSlotItem = this.slotItems.get(right);
                else
                    this.slotItems.get(i).rightSlotItem = this.slotItems.get(right);

            if (below < size)
                if (transpose)
                    this.slotItems.get(i).rightSlotItem = this.slotItems.get(below);
                else
                    this.slotItems.get(i).downSlotItem = this.slotItems.get(below);

            if (left >= 0 && (left + 1) % factor != 0)
                if (transpose)
                    this.slotItems.get(i).upSlotItem = this.slotItems.get(left);
                else
                    this.slotItems.get(i).leftSlotItem = this.slotItems.get(left);
        }
    }

    // Sets the row and column as prefix
    void setRowColumnPrefixForSlots() {
        int size = (int) Math.round(Math.sqrt(this.slotItems.size()));
        int i = 0;

        for (int row = 1; row <= size; row++) {
            for (int column = 1; column <= size; column++) {
                Slot slot = this.slotItems.get(i).slot;
                String prefix = MainClass.inventoryControls.getRowAndColumnFormat().formatted(row, column);

                this.setSlotPrefix(slot, prefix);
                ++i;
            }
        }
    }

    public String getGroupName() {
        String key = String.format("minecraft_access.slot_group.%s", groupKey);
        String translation = groupName == null || I18n.hasTranslation(key) ? I18n.translate(key) : groupName;
        return index == null ? translation : String.format("%s %d", translation, index);
    }
}
