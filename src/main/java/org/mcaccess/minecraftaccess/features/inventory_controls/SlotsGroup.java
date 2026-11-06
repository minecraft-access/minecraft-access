package org.mcaccess.minecraftaccess.features.inventory_controls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.ClientConfig;

public class SlotsGroup {
    private final @NotNull String groupKey;
    private final @Nullable String groupName;
    private final @Nullable Byte index;
    public List<SlotItem> slotItems;
    public boolean isScrollable = false;

    private final Map<Slot, String> slotNamePrefixMap;

    public SlotsGroup(@NotNull String groupKey, @Nullable String groupName, @Nullable Byte index, @Nullable List<SlotItem> slotItems) {
        slotNamePrefixMap = new HashMap<>();
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
        slotNamePrefixMap.put(slot, prefix);
    }

    public String getSlotPrefix(Slot slot) {
        String output = slotNamePrefixMap.get(slot);
        return output != null ? output : "";
    }

    public SlotItem getFirstGroupItem() {
        return slotItems.getFirst();
    }

    public SlotItem getLastGroupItem() {
        return slotItems.getLast();
    }

    public boolean hasSlotItemAbove(@NotNull SlotItem slotItem) {
        return slotItem.upSlotItem != null || slotItem.y != getFirstGroupItem().y;
    }

    public boolean hasSlotItemBelow(@NotNull SlotItem slotItem) {
        return slotItem.downSlotItem != null || slotItem.y != getLastGroupItem().y;
    }

    public boolean hasSlotItemLeft(@NotNull SlotItem slotItem) {
        return slotItem.leftSlotItem != null || slotItem.x != getFirstGroupItem().x;
    }

    public boolean hasSlotItemRight(@NotNull SlotItem slotItem) {
        return slotItem.rightSlotItem != null || slotItem.x != getLastGroupItem().x;
    }

    void mapTheGroupList(int factor) {
        mapTheGroupList(factor, false);
    }

    // Maps the list into 2d form like a matrix, the factor being the no. of columns and transpose is whether to transpose the matrix or not
    void mapTheGroupList(int factor, boolean transpose) {
        int size = slotItems.size();
        for (int i = 0; i < size; i++) {
            int above = i - factor;
            int right = i + 1;
            int below = i + factor;
            int left = i - 1;

            if (above >= 0) {
                if (transpose) {
                    slotItems.get(i).leftSlotItem = slotItems.get(above);
                } else {
                    slotItems.get(i).upSlotItem = slotItems.get(above);
                }
            }
            if (right < size && right % factor != 0) {
                if (transpose) {
                    slotItems.get(i).downSlotItem = slotItems.get(right);
                } else {
                    slotItems.get(i).rightSlotItem = slotItems.get(right);
                }
            }
            if (below < size) {
                if (transpose) {
                    slotItems.get(i).rightSlotItem = slotItems.get(below);
                } else {
                    slotItems.get(i).downSlotItem = slotItems.get(below);
                }
            }
            if (left >= 0 && (left + 1) % factor != 0) {
                if (transpose) {
                    slotItems.get(i).upSlotItem = slotItems.get(left);
                } else {
                    slotItems.get(i).leftSlotItem = slotItems.get(left);
                }
            }
        }
    }

    // Sets the row and column as prefix
    void setRowColumnPrefixForSlots() {
        int size = (int) Math.round(Math.sqrt(slotItems.size()));
        int i = 0;

        for (int row = 1; row <= size; row++) {
            for (int column = 1; column <= size; column++) {
                Slot slot = slotItems.get(i).slot;
                String prefix = ClientConfig.getInstance().inventoryControls.rowAndColumnFormat.formatted(row, column);

                setSlotPrefix(slot, prefix);
                ++i;
            }
        }
    }

    public String getGroupName() {
        String key = String.format("minecraft_access.slot_group.%s", groupKey);
        String translation = groupName == null || Language.getInstance().has(key) ? I18n.get(key) : groupName;
        return index == null ? translation : String.format("%s %d", translation, index);
    }
}
