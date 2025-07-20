package org.mcaccess.minecraftaccess.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;

import java.util.Optional;

public class NarrateHeldItem {
    private String previousItemName = "";
    private int previousItemCount = 0;
    private int previousSelectedSlot = 0;

    public void tick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack currentItemStack = player.getMainHandItem();
        int selectedSlot = player.getInventory().getSelectedSlot();
        String baseItemName = getItemName(currentItemStack);
        int itemCount = currentItemStack.getCount();

        String itemNameWithCount = currentItemStack.isStackable() ? itemCount + " " + baseItemName : baseItemName;

        boolean nameChanged = !previousItemName.equals(baseItemName);
        boolean countChanged = itemCount != previousItemCount;
        boolean slotChanged = selectedSlot != previousSelectedSlot;

        if (nameChanged || slotChanged) {
            MainClass.narrate(I18n.get("minecraft_access.other.selected", itemNameWithCount), true);
        } else if (countChanged && Config.getInstance().features.narrateHeldItemsCountWhenChanged) {
            MainClass.narrate(String.valueOf(itemCount), true);
        }

        previousItemName = baseItemName;
        previousItemCount = itemCount;
        previousSelectedSlot = selectedSlot;
    }

    private String getItemName(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return I18n.get("minecraft_access.inventory_controls.empty_slot", "");
        }

        StringBuilder itemName = new StringBuilder();
        itemName.append(itemStack.getHoverName().getString());

        Optional.ofNullable(itemStack.get(DataComponents.JUKEBOX_PLAYABLE))
                .flatMap(jukeboxPlayable -> jukeboxPlayable.song().key())
                .ifPresent(discNumber -> itemName.append(" ").append(I18n.get("jukebox_song.minecraft." + discNumber.location().getPath())));

        return itemName.toString();
    }
}
