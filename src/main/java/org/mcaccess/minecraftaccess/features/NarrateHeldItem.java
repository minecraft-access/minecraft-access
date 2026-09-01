package org.mcaccess.minecraftaccess.features;

import java.util.Objects;
import java.util.Optional;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.balm.client.platform.util.SessionLocal;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.TestBlock;
import net.minecraft.world.level.block.state.properties.TestBlockMode;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.events.ClientPlayingTick;

public class NarrateHeldItem implements BalmClientModule {
    private final SessionLocal<String> previousItemName = new SessionLocal<>(() -> null);
    private final SessionLocal<Integer> previousItemCount = new SessionLocal<>(() -> null);
    private final SessionLocal<Integer> previousSelectedSlot = new SessionLocal<>(() -> null);

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
                .handleScreenInput(event -> {
                    if (!(event.screen() instanceof AbstractContainerScreen)) return false;
                    MainClass.narrate(I18n.get("minecraft_access.inventory_controls.dragging",
                            getItemName(Minecraft.getInstance().player.containerMenu.getCarried(), true)), false);
                    return true;
                })
                .handleWorldInput(_ -> {
                    narrateHand(false);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.narrate_held_item/offhand"))
                .withDefault(InputBinding.key(InputConstants.KEY_GRAVE, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(_ -> {
                    narrateHand(true);
                    return true;
                })
                .build();
    }

    private void tick(Minecraft client, Player player, Level level) {
        if (player.isSpectator()) return;

        ItemStack currentItemStack = player.getMainHandItem();
        int selectedSlot = player.getInventory().getSelectedSlot();
        String baseItemName = getItemName(currentItemStack, false);
        int itemCount = currentItemStack.getCount();

        boolean nameChanged = !Objects.equals(baseItemName, previousItemName.value);
        boolean countChanged = !Objects.equals(itemCount, previousItemCount.value);
        boolean slotChanged = !Objects.equals(selectedSlot, previousSelectedSlot.value);

        if (nameChanged || slotChanged) {
            MainClass.narrate(I18n.get("minecraft_access.other.selected", getItemName(currentItemStack, true)), true);
        } else if (Config.getInstance().features.narrateHeldItemsCountWhenChanged && countChanged) {
            MainClass.narrate(String.valueOf(itemCount), true);
        }

        previousItemName.value = baseItemName;
        previousItemCount.value = itemCount;
        previousSelectedSlot.value = selectedSlot;
    }

    public static String getItemName(ItemStack itemStack, boolean addCount) {
        if (itemStack.isEmpty()) {
            return I18n.get("minecraft_access.inventory_controls.empty_slot", "");
        }

        StringBuilder itemName = new StringBuilder();
        itemName.append(itemStack.getHoverName().getString());

        Optional.ofNullable(itemStack.get(DataComponents.JUKEBOX_PLAYABLE))
                .flatMap(jukeboxPlayable -> jukeboxPlayable.song().unwrapKey())
                .ifPresent(discNumber -> itemName.append(' ').append(I18n.get("jukebox_song.minecraft." + discNumber.identifier().getPath())));

        Optional.ofNullable(itemStack.get(DataComponents.BLOCK_STATE))
                .map(blockState -> blockState.get(LightBlock.LEVEL))
                .ifPresent(level -> itemName.append(' ').append(level));

        if (itemStack.is(Items.TEST_BLOCK)) {
            TestBlockMode mode = Optional.ofNullable(itemStack.get(DataComponents.BLOCK_STATE))
                    .map(blockState -> blockState.get(TestBlock.MODE))
                    .orElse(TestBlockMode.START);
            itemName.append(' ').append(mode.getDisplayName().getString());
        }

        if (itemStack.has(DataComponents.BUNDLE_CONTENTS)) {
            itemStack.get(DataComponents.BUNDLE_CONTENTS).weight().result().ifPresent(fraction ->
                    itemName.append(' ')
                            .append(fraction.multiplyBy(Fraction.getFraction(64, 1)).getNumerator())
                            .append("/64"));
        }

        int heldItemCount = itemStack.getCount();
        return (addCount && heldItemCount != 1) ? heldItemCount + " " + itemName : itemName.toString();
    }

    private void narrateHand(boolean hasAltDown) {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        if (player.isSpectator()) return;

        String hand = I18n.get(hasAltDown ? "minecraft_access.other.offhand" : "options.mainHand");
        String heldItemName = getItemName(hasAltDown ? player.getOffhandItem() : player.getMainHandItem(), true);

        MainClass.narrate("%s: %s".formatted(hand, heldItemName), false);
    }
}
