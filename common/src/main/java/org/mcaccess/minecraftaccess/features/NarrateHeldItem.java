package org.mcaccess.minecraftaccess.features;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;

public class NarrateHeldItem {
    private String previousItemName = "";
    private int previousItemCount = 0;
    private int previousSelectedSlot = 0;

    public void tick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        while (KeyBindingsHandler.NARRATE_HELD_ITEM_KEY.mapping.consumeClick()) {
            narrateHand(Minecraft.getInstance().hasAltDown());
        }

        ItemStack currentItemStack = player.getMainHandItem();
        int selectedSlot = player.getInventory().getSelectedSlot();
        String baseItemName = getItemName(currentItemStack);
        int itemCount = currentItemStack.getCount();

        String itemNameWithCount = (currentItemStack.getCount() != 1 && !currentItemStack.isEmpty()) ? itemCount + " " + baseItemName : baseItemName;

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
                .ifPresent(discNumber -> itemName.append(' ').append(I18n.get("jukebox_song.minecraft." + discNumber.location().getPath())));

        return itemName.toString();
    }

    private void narrateHand(boolean hasAltDown) {
        LocalPlayer player = Minecraft.getInstance().player;
        String hand;
        ItemStack heldItem;

        if (!hasAltDown) {
            hand = I18n.get("options.mainHand");
            assert player != null;
            heldItem = player.getMainHandItem();
        } else {
            hand = I18n.get("minecraft_access.other.offhand");
            assert player != null;
            heldItem = player.getOffhandItem();
        }

        String heldItemName = getItemName(heldItem);
        int heldItemCount = heldItem.getCount();
        heldItemName = (heldItemCount != 1 && !heldItem.isEmpty()) ? heldItemCount + " " + heldItemName : heldItemName;

        MainClass.narrate("%s: %s".formatted(hand, heldItemName), false);
    }
}
