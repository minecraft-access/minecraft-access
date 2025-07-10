package org.mcaccess.minecraftaccess.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.features.inventory_controls.InventoryControls;
import org.mcaccess.minecraftaccess.mixin.GuiAccessor;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

import java.util.function.Function;

/**
 * This class is responsible for narrating hotbar when no inventory screen is opened.
 * For narrating hotbar when any inventory screen is opened, see {@link InventoryControls#getCurrentSlotNarrationText()}
 */
public class NarrateHeldItem {
    private String previousItemName = "";
    private int previousItemCount = 0;
    private int previousHotbarSlot = 0;
    public static final Function<String, String> HOTBAR_I18N = narration -> I18n.get("minecraft_access.other.selected", narration);
    public static final Function<String, String> EMPTY_SLOT_I18N = narration -> I18n.get("minecraft_access.inventory_controls.empty_slot", narration);

    public void tick() {
        ItemStack currentStack = ((GuiAccessor) Minecraft.getInstance().gui).getLastToolHighlight();
        int heldItemTooltipFade = ((GuiAccessor) Minecraft.getInstance().gui).getToolHighlightTimer();
        boolean currentStackIsEmpty = currentStack.isEmpty();
        LocalPlayer player = Minecraft.getInstance().player;

        if (heldItemTooltipFade == 0 && currentStackIsEmpty && player != null) {
            // Narrate "empty slot" when the selected slot is empty
            narrateIfHeldChanged("", 0, player.getInventory().getSelectedSlot(), NarrateHeldItem.EMPTY_SLOT_I18N);
        }

        if (!currentStackIsEmpty && player != null) {
            // Narrate held item's name and count
            narrateIfHeldChanged(currentStack.getHoverName().getString(), currentStack.getCount(), player.getInventory().getSelectedSlot(), NarrateHeldItem.HOTBAR_I18N);
        }
    }

    private void narrateIfHeldChanged(String itemName, int itemCount, int hotbarSlot, Function<String, String> i18n) {
        boolean nameChanged = !previousItemName.equals(itemName);
        boolean countChanged = previousItemCount != itemCount;
        boolean slotChanged = previousHotbarSlot != hotbarSlot;

        if (nameChanged || slotChanged) {
            String itemCountText = itemCount == 0 ? "" : NarrationUtils.narrateNumber(itemCount) + " ";
            MainClass.narrate(i18n.apply(itemCountText + itemName), true);
        } else if (countChanged && Config.getInstance().features.narrateHeldItemsCountWhenChanged) {
            MainClass.narrate(String.valueOf(itemCount), true);
        }
        previousItemName = itemName;
        previousItemCount = itemCount;
        previousHotbarSlot = hotbarSlot;
    }
}
