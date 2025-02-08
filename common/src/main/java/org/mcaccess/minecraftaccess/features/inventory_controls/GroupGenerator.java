package org.mcaccess.minecraftaccess.features.inventory_controls;

import com.google.common.base.CaseFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.mixin.*;

import java.util.*;

public class GroupGenerator {

    /**
     * Saved by RecipeBookPageMixin
     */
    public static List<RecipeCollection> recipesOnTheScreen;

    public static List<SlotsGroup> generateGroupsFromSlots(AbstractContainerScreenAccessor screen) {
        if (screen instanceof CreativeModeInventoryScreen creativeInventoryScreen) {
            return creativeInventoryGroups(creativeInventoryScreen);
        }

        if (screen instanceof InventoryScreen || screen instanceof CraftingScreen) {
            return inventoryAndCraftingScreensGroups(screen);
        }

        return commonGroups(screen);
    }

    private static @NotNull List<SlotsGroup> commonGroups(@NotNull AbstractContainerScreenAccessor screen) {
        List<SlotsGroup> foundGroups = new ArrayList<>();

        List<Slot> slots = new ArrayList<>(screen.getMenu().slots);

        SlotsGroup hotbarGroup = new SlotsGroup("hotbar", null);
        SlotsGroup playerInventoryGroup = new SlotsGroup("player_inventory", null);
        SlotsGroup armourGroup = new SlotsGroup("armour", null);
        SlotsGroup offHandGroup = new SlotsGroup("off_hand", null);
        SlotsGroup itemOutputGroup = new SlotsGroup("item_output", null);
        SlotsGroup itemInputGroup = new SlotsGroup("item_input", null);
        SlotsGroup recipesGroup = new SlotsGroup("recipes", null);
        SlotsGroup tradesGroup = new SlotsGroup("trades", null);
        SlotsGroup fuelInputGroup = new SlotsGroup("fuel_input", null);
        SlotsGroup craftingOutputGroup = new SlotsGroup("crafting_output", null);
        SlotsGroup craftingInputGroup = new SlotsGroup("crafting_input", null);
        SlotsGroup bannerInputGroup = new SlotsGroup("banner_input", null);
        SlotsGroup dyeInputGroup = new SlotsGroup("dye_input", null);
        SlotsGroup patternInputGroup = new SlotsGroup("pattern_input", null);
        SlotsGroup smithingTemplateInputGroup = new SlotsGroup("smithing_template_input");
        SlotsGroup materialInputGroup = new SlotsGroup("material_input", null);
        SlotsGroup potionGroup = new SlotsGroup("potion", null);
        SlotsGroup ingredientGroup = new SlotsGroup("ingredient", null);
        SlotsGroup blockInventoryGroup = new SlotsGroup("block_inventory", null);
        SlotsGroup beaconConfirmButtonsGroup = new SlotsGroup("beacon_confirm_buttons", null);
        SlotsGroup primaryBeaconPowersButtonsGroup = new SlotsGroup("primary_beacon_powers_buttons", null);
        SlotsGroup secondaryBeaconPowersButtonsGroup = new SlotsGroup("secondary_beacon_powers_buttons", null);
        SlotsGroup lapisLazuliInputGroup = new SlotsGroup("lapis_lazuli_input", null);
        SlotsGroup enchantsGroup = new SlotsGroup("enchants", null);
        List<SlotItem> unknownSlots = new ArrayList<>(slots.size());

        for (Slot s : slots) {
            int index = ((SlotAccessor) s).getSlot();

            //<editor-fold desc="Group player inventory slot items">
            if (s.container instanceof Inventory && index >= 0 && index <= 8) {
                hotbarGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            if (s.container instanceof Inventory && index >= 9 && index <= 35) {
                playerInventoryGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            if (s.container instanceof Inventory && index >= 36 && index <= 39) {
                armourGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            if (s.container instanceof Inventory && index == 40) {
                offHandGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Group furnace(blast furnace, regular furnace and smoker) screen slot items">
            if (screen.getMenu() instanceof AbstractFurnaceMenu && index == 2) {
                itemOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof AbstractFurnaceMenu && index == 1) {
                fuelInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof AbstractFurnaceMenu && index == 0) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Group villager trading screen slot items">
            if (screen.getMenu() instanceof MerchantMenu && (index == 0 || index == 1)) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof MerchantMenu && index == 2) {
                itemOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Group stone cutter screen slot items">
            if (screen.getMenu() instanceof StonecutterMenu && index == 0) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof StonecutterMenu && index == 1) {
                itemOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Group cartography screen slot items">
            if (screen.getMenu() instanceof CartographyTableMenu && (index == 0 || index == 1)) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof CartographyTableMenu && index == 2) {
                itemOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Group loom screen slot items">
            if (screen.getMenu() instanceof LoomMenu && index == 0) {
                if (s.container.getContainerSize() == 3) {
                    bannerInputGroup.slotItems.add(new SlotItem(s));
                } else if (s.container.getContainerSize() == 1) {
                    itemOutputGroup.slotItems.add(new SlotItem(s));
                }
                continue;
            }

            if (screen.getMenu() instanceof LoomMenu && index == 1) {
                dyeInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof LoomMenu && index == 2) {
                patternInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Group anvil screen slot items">
            if (screen.getMenu() instanceof AnvilMenu && (index == 0 || index == 1)) {
                    itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof AnvilMenu && index == 2) {
                itemOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

                        //<editor-fold desc="Group smithing table screen slot items">
            if (screen.getMenu() instanceof SmithingMenu) {
                switch (index) {
                    case 0:
                        smithingTemplateInputGroup.slotItems.add(new SlotItem(s));
                        break;
                    case 1:
                        itemInputGroup.slotItems.add(new SlotItem(s));
                        break;
                    case 2:
                        materialInputGroup.slotItems.add(new SlotItem(s));
                        break;
                        case 3:
                        itemOutputGroup.slotItems.add(new SlotItem(s));
                        break;
                }

                continue;
            }
                        //</editor-fold>

            //<editor-fold desc="Group brewing stand screen slot items">
            if (screen.getMenu() instanceof BrewingStandMenu && index >= 0 && index <= 2) {
                potionGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof BrewingStandMenu && index == 3) {
                ingredientGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof BrewingStandMenu && index == 4) {
                fuelInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Group grind stone screen slot items">
            if (screen.getMenu() instanceof GrindstoneMenu && (index == 0 || index == 1)) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof GrindstoneMenu && index == 2) {
                itemOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Group beacon screen slot items">
            if (screen.getMenu() instanceof BeaconMenu && index == 0) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Group enchantment screen slot items">
            if (screen.getMenu() instanceof EnchantmentMenu && index == 0) {
                itemInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof EnchantmentMenu && index == 1) {
                lapisLazuliInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Group storage container(chests, hopper, dispenser, etc.) inventory slot items">
            if (screen.getMenu() instanceof ChestMenu && s.container instanceof SimpleContainer) {
                blockInventoryGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof DispenserMenu && s.container instanceof SimpleContainer) {
                blockInventoryGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (screen.getMenu() instanceof HopperMenu && s.container instanceof SimpleContainer) {
                blockInventoryGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Group crafting related slot items">
            if (s.container instanceof ResultContainer && !(s instanceof FurnaceResultSlot)) {
                craftingOutputGroup.slotItems.add(new SlotItem(s));
                continue;
            }

            if (s.container instanceof TransientCraftingContainer) {
                craftingInputGroup.slotItems.add(new SlotItem(s));
                continue;
            }
            //</editor-fold>

            unknownSlots.add(new SlotItem(s));
        }

        //<editor-fold desc="Group recipe group slot items if any">
        if (screen instanceof StonecutterScreen stonecutterScreen) {
            // Refer to StoneCutterScreen.java -->> renderRecipeIcons()
            int x = screen.getLeftPos() + 52;
            int y = screen.getTopPos() + 14;
            int scrollOffset = ((StonecutterScreenAccessor) stonecutterScreen).getStartIndex();

            for (int i = scrollOffset; i < scrollOffset + 12 && i < stonecutterScreen.getMenu().getNumberOfVisibleRecipes(); ++i) {
                int j = i - scrollOffset;
                int k = x + j % 4 * 16;
                int l = j / 4;
                int m = y + l * 18 + 2;

                int realX = k - screen.getLeftPos() + 8;
                int realY = m - screen.getTopPos() + 8;
                recipesGroup.slotItems.add(new SlotItem(realX, realY, j));
            }
        }

        if (screen instanceof LoomScreen loomScreen) {
            // Refer to LoomScreen.java -->> drawBackground()
            int i = screen.getLeftPos();
            int j = screen.getTopPos();
            if (((LoomScreenAccessor) loomScreen).isDisplayPatterns()) {
                int l = i + 60;
                int m = j + 13;
                List<Holder<BannerPattern>> list = loomScreen.getMenu().getSelectablePatterns();
                block0:
                for (int n = 0; n < 4; ++n) {
                    for (int o = 0; o < 4; ++o) {
                        int p = n + ((LoomScreenAccessor) loomScreen).getStartRow();
                        int q = p * 4 + o;
                        if (q >= list.size()) break block0;
                        int r = l + o * 14;
                        int s = m + n * 14;

                        int realX = r - screen.getLeftPos() + 8;
                        int realY = s - screen.getTopPos() + 8;

                        recipesGroup.slotItems.add(new SlotItem(realX, realY, n, o));
                    }
                }
            }
        }
        //</editor-fold>

        //<editor-fold desc="Group beacon screen buttons (refer to BeaconScreen.java -->> init())">
        if (screen.getMenu() instanceof BeaconMenu) {
            int l;
            int k;
            int j;
            int i;
            beaconConfirmButtonsGroup.slotItems.add(new SlotItem(173, screen.getTopPos() + 107, "Done Button"));
            beaconConfirmButtonsGroup.slotItems.add(new SlotItem(199, screen.getTopPos() + 107, "Cancel Button"));
            for (i = 0; i <= 2; ++i) {
                j = BeaconBlockEntity.BEACON_EFFECTS.get(i).size();
                k = j * 22 + (j - 1) * 2;
                for (l = 0; l < j; ++l) {
                    primaryBeaconPowersButtonsGroup.slotItems.add(new SlotItem(85 + l * 24 - k / 2, screen.getTopPos() + 22 + i * 25));
                }
            }
            j = BeaconBlockEntity.BEACON_EFFECTS.get(3).size() + 1;
            k = j * 22 + (j - 1) * 2;
            for (l = 0; l < j - 1; ++l) {
                secondaryBeaconPowersButtonsGroup.slotItems.add(new SlotItem(176 + l * 24 - k / 2, screen.getTopPos() + 47));
            }
            secondaryBeaconPowersButtonsGroup.slotItems.add(new SlotItem(176 + (j - 1) * 24 - k / 2, screen.getTopPos() + 47));
        }
        //</editor-fold>

        //<editor-fold desc="Group enchantment screen enchant buttons (EnchantScreen.java -->> render())">
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null
                && screen.getMenu() instanceof EnchantmentMenu enchantmentScreenHandler) {
            boolean bl = Minecraft.getInstance().player.getAbilities().instabuild;
            int i = enchantmentScreenHandler.getGoldCount();
            for (int j = 0; j < 3; ++j) {
                int k = enchantmentScreenHandler.costs[j];
                int l = enchantmentScreenHandler.levelClue[j];
                // copied from 1.21.3 EnchantmentScreen.render() L172
                Optional<Holder.Reference<Enchantment>> optional = Minecraft.getInstance()
                        .level
                        .registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .get(l);

                int m = j + 1;
                if (optional.isEmpty()) break;
                StringBuilder clueText = new StringBuilder(Component.translatable("container.enchant.clue", Enchantment.getFullname(optional.get(), l)).withStyle(ChatFormatting.WHITE).getString());
                if (!bl) {
                    if (Minecraft.getInstance().player.experienceLevel < k) {
                        clueText.append(Component.translatable("container.enchant.level.requirement", enchantmentScreenHandler.costs[j]).withStyle(ChatFormatting.RED).getString());
                    } else {
                        MutableComponent mutableText = m == 1 ? Component.translatable("container.enchant.lapis.one") : Component.translatable("container.enchant.lapis.many", m);
                        clueText.append(mutableText.withStyle(i >= m ? ChatFormatting.GRAY : ChatFormatting.RED).getString());
                        MutableComponent mutableText2 = m == 1 ? Component.translatable("container.enchant.level.one") : Component.translatable("container.enchant.level.many", m);
                        clueText.append(mutableText2.withStyle(ChatFormatting.GRAY).getString());
                    }
                }

                enchantsGroup.slotItems.add(new SlotItem(80, 21 + 19 * j, clueText.toString()));
            }
        }
        //</editor-fold>

        //<editor-fold desc="Group merchant trades">
        if (screen instanceof MerchantScreen merchantScreen) {
            MerchantMenu merchantScreenHandler = merchantScreen.getMenu();
            MerchantOffers tradeOfferList = merchantScreenHandler.getOffers();
            if (!tradeOfferList.isEmpty()) {
                int i = (merchantScreen.width - screen.getImageWidth()) / 2;
                int j = (merchantScreen.height - screen.getImageHeight()) / 2;
                int k = j + 16 + 1;
                int l = i + 5 + 5;
                for (int z = 0; z < tradeOfferList.size() && z < 7; z++) {
                    int n = k + 11;

                    tradesGroup.slotItems.add(new SlotItem(l - screen.getLeftPos(), n - screen.getTopPos(), z));
                    k += 20;
                }
            }
        }
        //</editor-fold>

        //<editor-fold desc="Add Groups to foundGroups, in the order of interactive frequency">

        // Inventory groups first, you always start a process with picking up items.
        //<editor-fold desc="Add Normal Inventory Groups to foundGroups">
        // Container inventory first (you open a container for items inside it)
        if (!blockInventoryGroup.slotItems.isEmpty()) {
            if (screen.getMenu() instanceof DispenserMenu)
                blockInventoryGroup.mapTheGroupList(3);
            else if (screen.getMenu() instanceof ChestMenu)
                blockInventoryGroup.mapTheGroupList(9);
            else if (screen.getMenu() instanceof HopperMenu)
                blockInventoryGroup.mapTheGroupList(5);
            foundGroups.add(blockInventoryGroup);
        }

        // Then normal inventory (you open inventory screen for items inside it)
        if (!playerInventoryGroup.slotItems.isEmpty()) {
            playerInventoryGroup.mapTheGroupList(9);
            foundGroups.add(playerInventoryGroup);
        }

        // Finally the hotbar (you may want to put picked items on the hotbar (num-key hotkeys are fast, yup))
        if (!hotbarGroup.slotItems.isEmpty()) {
            hotbarGroup.mapTheGroupList(9);
            foundGroups.add(hotbarGroup);
        }
        //</editor-fold>

        // Then the input and output groups, you may want to put picked items into the input slots and get the result from output slots.
        //<editor-fold desc="Add Input Groups to foundGroups">
        if (!craftingInputGroup.slotItems.isEmpty()) {
            craftingInputGroup.setRowColumnPrefixForSlots();
            foundGroups.add(craftingInputGroup);
        }

        if (!itemInputGroup.slotItems.isEmpty()) {
            itemInputGroup.mapTheGroupList(4);
            foundGroups.add(itemInputGroup);
        }

        if (!fuelInputGroup.slotItems.isEmpty()) {
            foundGroups.add(fuelInputGroup);
        }

        if (!ingredientGroup.slotItems.isEmpty()) {
            foundGroups.add(ingredientGroup);
        }

        if (!potionGroup.slotItems.isEmpty()) {
            potionGroup.mapTheGroupList(3);
            foundGroups.add(potionGroup);
        }

        if (!smithingTemplateInputGroup.slotItems.isEmpty()) {
            foundGroups.add(smithingTemplateInputGroup);
        }

        if (!materialInputGroup.slotItems.isEmpty()) {
            foundGroups.add(materialInputGroup);
        }

        if (!bannerInputGroup.slotItems.isEmpty()) {
            foundGroups.add(bannerInputGroup);
        }

        if (!dyeInputGroup.slotItems.isEmpty()) {
            foundGroups.add(dyeInputGroup);
        }

        if (!patternInputGroup.slotItems.isEmpty()) {
            foundGroups.add(patternInputGroup);
        }

        if (!lapisLazuliInputGroup.slotItems.isEmpty()) {
            foundGroups.add(lapisLazuliInputGroup);
        }
        //</editor-fold>

        //<editor-fold desc="Add Output Groups to foundGroups">
        if (!craftingOutputGroup.slotItems.isEmpty()) {
            foundGroups.add(craftingOutputGroup);
        }

        if (!itemOutputGroup.slotItems.isEmpty()) {
            itemOutputGroup.mapTheGroupList(4);
            foundGroups.add(itemOutputGroup);
        }

        // Unknown slots come from screens in other mods (or unsupported original screens)
        // that use original SlotItem class to represent item slots.
        // One SlotItem represents one slot.
        foundGroups.addAll(separateUnknownSlotsIntoGroups(unknownSlots));
        //</editor-fold>

        // Then the non-item-related groups you want to interact with (after you put items into input slots, enchant for example).
        //<editor-fold desc="Add Screen Specific Groups">
        if (!recipesGroup.slotItems.isEmpty()) {
            recipesGroup.isScrollable = true;
            recipesGroup.mapTheGroupList(4);
            foundGroups.add(recipesGroup);
        }

        if (!beaconConfirmButtonsGroup.slotItems.isEmpty()) {
            beaconConfirmButtonsGroup.mapTheGroupList(2);
            foundGroups.add(beaconConfirmButtonsGroup);
        }

        if (!primaryBeaconPowersButtonsGroup.slotItems.isEmpty()) {
            primaryBeaconPowersButtonsGroup.mapTheGroupList(2);
            foundGroups.add(primaryBeaconPowersButtonsGroup);
        }

        if (!secondaryBeaconPowersButtonsGroup.slotItems.isEmpty()) {
            secondaryBeaconPowersButtonsGroup.mapTheGroupList(2);
            foundGroups.add(secondaryBeaconPowersButtonsGroup);
        }

        if (!enchantsGroup.slotItems.isEmpty()) {
            enchantsGroup.mapTheGroupList(3, true);
            foundGroups.add(enchantsGroup);
        }

        if (!tradesGroup.slotItems.isEmpty()) {
            tradesGroup.isScrollable = true;
            tradesGroup.mapTheGroupList(7, true);
            foundGroups.add(tradesGroup);
        }
        //</editor-fold>

        // Finally, the armour groups, low interactive frequency, will only show in inventory screen.
        //<editor-fold desc="Add Armour Inventory Groups">
        if (!armourGroup.slotItems.isEmpty()) {
            foundGroups.add(armourGroup);
        }

        if (!offHandGroup.slotItems.isEmpty()) {
            foundGroups.add(offHandGroup);
        }
        //</editor-fold>
        //</editor-fold>

        return foundGroups;
    }

    /**
     * Separate unknown slots into groups according to their position on the screen,
     * and give these group names according to slot instances' types (classes).
     *
     * @return Separation result
     */
    @NotNull
    private static List<SlotsGroup> separateUnknownSlotsIntoGroups(List<SlotItem> slots) {
        // An unsorted list may result in many groups being created where there should only be one
        slots.sort(Comparator.comparing(slot -> ((SlotItem) slot).y).thenComparing(slot -> ((SlotItem) slot).x));

        // Separate unknown slots into groups.
        // Coordinates adjacency is used instead of slot.inventory to calculate grouping
        // because some mods have: 1. non-adjacent slots in the same inventory 2. adjacent slots in different inventories.
        List<List<SlotItem>> separatedSlots = new ArrayList<>(slots.size());
        for (SlotItem current : slots) {
            // Search for a group which already has one slot that is adjacent to the current slot,
            // or create a new group if there is no such group.
            List<SlotItem> targetGroup = separatedSlots.stream()
                    .filter(group -> group.stream().anyMatch(groupSlot -> twoSlotsAreAdjacent(current, groupSlot)))
                    .findFirst()
                    .orElseGet(() -> {
                        List<SlotItem> group = new ArrayList<>(slots.size());
                        separatedSlots.add(group);
                        return group;
                    });
            // Add the current slot to this group
            targetGroup.add(current);
        }

        List<SlotsGroup> result = new ArrayList<>();

        // Naming groups
        Map<String, Byte> duplicateCounters = new HashMap<>(separatedSlots.size());
        for (List<SlotItem> group : separatedSlots) {
            String groupKey;
            @Nullable String groupName;
            @Nullable Byte index;
            boolean allSlotsHaveSameType = group.stream()
                    .map(slot -> slot.slot.getClass())
                    .distinct()
                    .limit(2)
                    .count() == 1;
            if (allSlotsHaveSameType) {
                // Set group name to unique slot class name
                // e.g. ModAbcSlot -> mod_abc_slot
                groupName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, group.getFirst().slot.getClass().getSimpleName());
                groupKey = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, group.getFirst().slot.getClass().getCanonicalName());
                // Don't use vanilla obfuscated class names
                if (groupName.startsWith("class_")) {
                    groupKey = "unknown";
                    groupName = null;
                }
            } else {
                groupKey = "unknown";
                groupName = null;
            }

            // Already have a group with the same name
            if (duplicateCounters.containsKey(groupKey)) {
                byte n = (byte) (duplicateCounters.get(groupKey) + 1);
                duplicateCounters.put(groupKey, n);
                index = n;
            } else {
                duplicateCounters.put(groupName, (byte) 1);
                index = null;
            }

            result.add(new SlotsGroup(groupKey, groupName, index, group));
        }

        return result;
    }

    private static boolean twoSlotsAreAdjacent(SlotItem currSlot, SlotItem groupSlot) {
        boolean adjacentOnX = groupSlot.x == currSlot.x && (groupSlot.y == currSlot.y + 18 || groupSlot.y == currSlot.y - 18);
        boolean adjacentOnY = groupSlot.y == currSlot.y && (groupSlot.x == currSlot.x + 18 || groupSlot.x == currSlot.x - 18);
        return adjacentOnX || adjacentOnY;
    }

    private static @NotNull List<SlotsGroup> inventoryAndCraftingScreensGroups(@NotNull AbstractContainerScreenAccessor screen) {
        List<SlotsGroup> foundGroups = commonGroups(screen);

        RecipeBookComponent<?> recipeBookWidget = null;
        if (screen instanceof AbstractRecipeBookScreen<?> recipeBookScreen) {
            recipeBookWidget = ((AbstractRecipeBookScreenAccessor) recipeBookScreen).getRecipeBookComponent();
        }
        if (recipeBookWidget == null || !recipeBookWidget.isVisible()) {
            return foundGroups;
        }

        RecipeBookComponentAccessor recipeBookComponentAccessor = (RecipeBookComponentAccessor) recipeBookWidget;
        SlotsGroup recipesGroup = new SlotsGroup("recipes", null);
        List<RecipeButton> slots = ((RecipeBookPageAccessor) recipeBookComponentAccessor.getRecipeBookPage()).getButtons();

        for (int i = 0; i < slots.size() && i < GroupGenerator.recipesOnTheScreen.size(); i++) {
            RecipeButton animatedResultButton = slots.get(i);
            int realX = animatedResultButton.getX() - screen.getLeftPos() + 10;
            int realY = animatedResultButton.getY() - screen.getTopPos() + 10;
            recipesGroup.slotItems.add(new SlotItem(realX, realY));
        }

        if (!recipesGroup.slotItems.isEmpty()) {
            recipesGroup.isScrollable = true;
            recipesGroup.mapTheGroupList(5);

            // Put recipe book group behind the crafting output group,
            // so user can easily access crafting output after clicking on recipe book.
            if (screen instanceof CraftingScreen) {
                // player_inventory, hotbar, crafting_input, crafting_output <put here>
                foundGroups.add(recipesGroup);
            } else {
                // screen instanceof InventoryScreen
                // player_inventory, hotbar, crafting_input, crafting_output, <put here>, armour, off_hand
                foundGroups.add(foundGroups.size() - 2, recipesGroup);
            }
        }

        return foundGroups;
    }

    private static @NotNull List<SlotsGroup> creativeInventoryGroups(@NotNull CreativeModeInventoryScreen creativeInventoryScreen) {
        List<SlotsGroup> foundGroups = new ArrayList<>();
        List<Slot> slots = new ArrayList<>(creativeInventoryScreen.getMenu().slots);
        if (CreativeModeInventoryScreenAccessor.getSelectedTab().getType() == CreativeModeTab.Type.INVENTORY) {
            SlotsGroup deleteItemGroup = new SlotsGroup("delete_items", null);
            SlotsGroup offHandGroup = new SlotsGroup("off_hand", null);
            SlotsGroup hotbarGroup = new SlotsGroup("hotbar", null);
            SlotsGroup armourGroup = new SlotsGroup("armour", null);
            SlotsGroup playerInventoryGroup = new SlotsGroup("player_inventory", null);

            for (Slot s : slots) {
                if (s.x < 0 || s.y < 0) continue;

                int index = ((SlotAccessor) s).getSlot();

                if (index == 0) {
                    deleteItemGroup.slotItems.add(new SlotItem(s));
                    continue;
                }

                if (index >= 5 && index <= 8) {
                    armourGroup.slotItems.add(new SlotItem(s));
                    continue;
                }

                if (index >= 9 && index <= 35) {
                    playerInventoryGroup.slotItems.add(new SlotItem(s));
                    continue;
                }

                if (index >= 36 && index <= 44) {
                    hotbarGroup.slotItems.add(new SlotItem(s));
                    continue;
                }

                if (index == 45) {
                    offHandGroup.slotItems.add(new SlotItem(s));
                }
            }

            armourGroup.mapTheGroupList(2, true);
            playerInventoryGroup.mapTheGroupList(9);
            hotbarGroup.mapTheGroupList(9);

            foundGroups.add(armourGroup);
            foundGroups.add(offHandGroup);
            foundGroups.add(playerInventoryGroup);
            foundGroups.add(hotbarGroup);
            foundGroups.add(deleteItemGroup);
        } else {
            SlotsGroup hotbarGroup = new SlotsGroup("hotbar", null);
            SlotsGroup tabInventoryGroup = new SlotsGroup("tab_inventory", null);
            tabInventoryGroup.isScrollable = true;

            for (Slot s : slots) {
                if (s.x < 0 || s.y < 0) continue;

                int index = ((SlotAccessor) s).getSlot();

                if (index >= 0 && index <= 8 && s.container instanceof Inventory) {
                    hotbarGroup.slotItems.add(new SlotItem(s));
                    continue;
                }

                if (index >= 0 && index <= 44) {
                    tabInventoryGroup.slotItems.add(new SlotItem(s));
                }
            }

            tabInventoryGroup.mapTheGroupList(9);
            hotbarGroup.mapTheGroupList(9);

            foundGroups.add(tabInventoryGroup);
            foundGroups.add(hotbarGroup);
        }
        return foundGroups;
    }
}
