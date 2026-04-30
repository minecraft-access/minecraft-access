package org.mcaccess.minecraftaccess.features;

import java.util.Optional;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.balm.client.platform.util.SessionLocal;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.events.ClientPlayingTick;
import org.mcaccess.minecraftaccess.utils.events.ServerChangeDetector;
import org.mcaccess.minecraftaccess.utils.i18n.Narratable;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;
import org.mcaccess.minecraftaccess.utils.i18n.Untranslated;

public class NarrateHeldItem implements BalmClientModule {
    private ServerChangeDetector<String> previousItemName = new ServerChangeDetector<>();
    private ServerChangeDetector<Integer> previousItemCount = new ServerChangeDetector<>();
    private ServerChangeDetector<Integer> previousSelectedSlot = new ServerChangeDetector<>();

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "narrate_held_item");
    }

    @Override
    public void initialize() {
        ClientPlayingTick.AFTER.register(this::tick);

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.narrate_held_item/mainhand"))
                .withDefault(InputBinding.key(InputConstants.KEY_GRAVE))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    narrateHand(false);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.narrate_held_item/offhand"))
                .withDefault(InputBinding.key(InputConstants.KEY_GRAVE, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    narrateHand(true);
                    return true;
                })
                .build();
    }

    private void tick(Minecraft client, Player player, Level level) {
        if (player.isSpectator()) return;

        ItemStack currentItemStack = player.getMainHandItem();
        int selectedSlot = player.getInventory().getSelectedSlot();
        Narratable baseItemName = getItemName(currentItemStack);
        int itemCount = currentItemStack.getCount();

        Narratable itemNameWithCount;
        if (currentItemStack.getCount() != 1 && !currentItemStack.isEmpty()) {
            itemNameWithCount = new Translation.Delimited(' ')
                    .put(currentItemStack.getCount())
                    .put(baseItemName);
        } else {
            itemNameWithCount = baseItemName;
        }

        boolean nameChanged = previousItemName.update(baseItemName.getString());
        boolean countChanged = previousItemCount.update(itemCount);
        boolean slotChanged = previousSelectedSlot.update(selectedSlot);

        if (nameChanged || slotChanged) {
            new Translation("minecraft_access.other.selected")
                    .variable("item").put(itemNameWithCount)
                    .narrate(true);
        } else if (countChanged && Config.getInstance().features.narrateHeldItemsCountWhenChanged) {
            Untranslated.FORMATTER.put(itemCount).narrate(true);
        }
    }

    private Narratable getItemName(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return new Translation("minecraft_access.inventory_controls.empty_slot").variable("item").put("");
        }

        Translation.Delimited itemName = new Translation.Delimited();
        itemName.put(itemStack.getHoverName());

        Optional.ofNullable(itemStack.get(DataComponents.JUKEBOX_PLAYABLE))
                .flatMap(jukeboxPlayable -> jukeboxPlayable.song().unwrapKey())
                .ifPresent(discNumber -> itemName.put("jukebox_song", discNumber));

        return itemName;
    }

    private void narrateHand(boolean hasAltDown) {
        assert Minecraft.getInstance().player != null;
        if (Minecraft.getInstance().player.isSpectator()) return;

        LocalPlayer player = Minecraft.getInstance().player;
        Translation.Vanilla hand;
        ItemStack heldItem;

        if (!hasAltDown) {
            hand = new Translation.Vanilla("options.mainHand");
            assert player != null;
            heldItem = player.getMainHandItem();
        } else {
            hand = new Translation.Vanilla("minecraft_access.other.offhand");
            assert player != null;
            heldItem = player.getOffhandItem();
        }

        Narratable heldItemName = getItemName(heldItem);
        int heldItemCount = heldItem.getCount();
        if (heldItemCount != 1 && !heldItem.isEmpty()) {
            heldItemName = new Translation.Delimited(' ')
                    .put(heldItemCount)
                    .put(heldItemName);
        }

        new Translation.Delimited(Untranslated.FORMATTER.put(": "))
                .put(hand)
                .put(heldItemName)
                .narrate(false);
    }
}
