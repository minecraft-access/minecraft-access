package org.mcaccess.minecraftaccess.features;

import java.util.Objects;
import java.util.Optional;

import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingsHandler;

public class NarrateHeldItem implements BalmClientModule {
    private String previousItemName = null;
    private Integer previousItemCount = null;
    private Integer previousSelectedSlot = null;

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "narrate_held_item");
    }

    @Override
    public void initialize() {
        ClientTickCallback.ClientLevelTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            previousItemName = null;
            previousItemCount = null;
            previousSelectedSlot = null;
        });
    }

    private void tick(Level level) {
        assert Minecraft.getInstance().player != null;
        if (Minecraft.getInstance().player.isSpectator()) {
            return;
        }

        while (KeyMappingsHandler.Keys.NARRATE_HELD_ITEM_KEY.mapping.consumeClick()) {
            narrateHand(Minecraft.getInstance().hasAltDown());
        }

        ItemStack currentItemStack = Minecraft.getInstance().player.getMainHandItem();
        int selectedSlot = Minecraft.getInstance().player.getInventory().getSelectedSlot();
        String baseItemName = getItemName(currentItemStack);
        int itemCount = currentItemStack.getCount();

        String itemNameWithCount = (currentItemStack.getCount() != 1 && !currentItemStack.isEmpty()) ? itemCount + " " + baseItemName : baseItemName;

        boolean nameChanged = !Objects.equals(previousItemName, baseItemName);
        boolean countChanged = !Objects.equals(itemCount, previousItemCount);
        boolean slotChanged = !Objects.equals(selectedSlot, previousSelectedSlot);

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
                .ifPresent(discNumber -> itemName.append(' ').append(I18n.get("jukebox_song.minecraft." + discNumber.identifier().getPath())));

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
