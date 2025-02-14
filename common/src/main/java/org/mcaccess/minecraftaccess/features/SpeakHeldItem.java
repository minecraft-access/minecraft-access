package org.mcaccess.minecraftaccess.features;

import org.mcaccess.minecraftaccess.Config;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.features.inventory_controls.InventoryControls;

import java.util.function.Function;

/**
 * This class is responsible for speaking hotbar when no inventory screen is opened.
 * For speaking hotbar when any inventory screen is opened, see {@link InventoryControls#getCurrentSlotNarrationText()}
 */
public class SpeakHeldItem {
    private String previousItemName = "";
    private int previousItemCount = 0;
    public static final Function<String, String> HOTBAR_I18N = toSpeak -> I18n.get("minecraft_access.other.selected", toSpeak);
    public static final Function<String, String> EMPTY_SLOT_I18N = toSpeak -> I18n.get("minecraft_access.inventory_controls.empty_slot", toSpeak);

    public void speakHeldItem(ItemStack currentStack, int heldItemTooltipFade) {
        boolean currentStackIsEmpty = currentStack.isEmpty();
        if (heldItemTooltipFade == 0 && currentStackIsEmpty) {
            // Speak "empty slot" when the selected slot is empty
            speakIfHeldChanged("", 0, SpeakHeldItem.EMPTY_SLOT_I18N);
        }

        if (!currentStackIsEmpty) {
            // Speak held item's name and count
            speakIfHeldChanged(currentStack.getHoverName().getString(), currentStack.getCount(), SpeakHeldItem.HOTBAR_I18N);
        }
    }

    private void speakIfHeldChanged(String itemName, int itemCount, Function<String, String> i18n) {
        boolean nameChanged = !previousItemName.equals(itemName);
        boolean countChanged = previousItemCount != itemCount;

        if (nameChanged) {
            String itemCountText = itemCount == 0 ? "" : itemCount + " ";
            MainClass.speakWithNarrator(i18n.apply(itemCountText + itemName), true);
        } else if (countChanged && Config.getInstance().features.reportHeldItemsCountWhenChanged) {
            MainClass.speakWithNarrator(String.valueOf(itemCount), true);
        }
        previousItemName = itemName;
        previousItemCount = itemCount;
    }
}
