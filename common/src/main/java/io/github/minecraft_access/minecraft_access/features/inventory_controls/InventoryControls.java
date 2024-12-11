package io.github.minecraft_access.minecraft_access.features.inventory_controls;

import io.github.minecraft_access.minecraft_access.MainClass;
import io.github.minecraft_access.minecraft_access.config.config_maps.InventoryControlsConfigMap;
import io.github.minecraft_access.minecraft_access.mixin.*;
import io.github.minecraft_access.minecraft_access.utils.KeyBindingsHandler;
import io.github.minecraft_access.minecraft_access.utils.condition.Interval;
import io.github.minecraft_access.minecraft_access.utils.system.KeyUtils;
import io.github.minecraft_access.minecraft_access.utils.system.MouseUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;

/**
 * This features lets us use keyboard in inventory screens. Works with all default minecraft screens.
 * <p>
 * Key binds and combinations: (all key binds are re-mappable(except two keys) from the game's controls menu and these key binds do not interrupt with any other key with same key.)<br>
 * 1) Up Key (default: I) = Focus to slot above.<br>
 * 2) Right Key (default: L) = Focus to slot right.<br>
 * 3) Down Key (default: K) = Focus to slot down.<br>
 * 4) Left Key (default: J) = Focus to slot left.<br>
 * 5) Group Key (default: C) = Select next group.<br>
 * 6) Left Shift + Group Key = Select previous group.<br>
 * 7) Switch Tab Key (default: V) = Select next tab (only for creative inventory screen and inventory/crafting screen).<br>
 * 8) Left Shift + Switch Tab Key = Select previous tab (only for creative inventory screen and inventory/crafting screen).<br>
 * 9) Toggle Craftable Key (default: R) = Toggle between show all and show only craftable recipes in inventory/crafting screen.<br>
 * 10) T Key (not re-mappable) = Select the search box.<br>
 * 11) Enter Key (not re-mappable) = Deselect the search box.<br>
 * </p>
 */
@Slf4j
public class InventoryControls {
    private boolean autoOpenRecipeBook;
    private String rowAndColumnFormat;
    private Interval interval;
    private MinecraftClient minecraftClient;

    private HandledScreenAccessor previousScreen = null;
    private HandledScreenAccessor currentScreen = null;

    private List<SlotsGroup> currentSlotsGroupList = null;
    private SlotsGroup currentGroup = null;
    private int currentGroupIndex = 0;
    private SlotItem currentSlotItem = null;
    private String previousSlotText = "";
    private boolean speakFocusedSlotChanges = true;

    private enum FocusDirection {
        UP(I18n.translate("minecraft_access.inventory_controls.direction_up")),
        DOWN(I18n.translate("minecraft_access.inventory_controls.direction_down")),
        LEFT(I18n.translate("minecraft_access.inventory_controls.direction_left")),
        RIGHT(I18n.translate("minecraft_access.inventory_controls.direction_right"));

        private final String value;

        FocusDirection(String value) {
            this.value = value;
        }

        String getString() {
            return this.value;
        }

    }

    public InventoryControls() {
        loadConfigurations();
    }

    public String getRowAndColumnFormat() {
        return rowAndColumnFormat;
    }

    public void update() {
        if (interval != null && !interval.hasEnded()) return;
        this.minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.currentScreen == null) {
            previousScreen = null;
            currentScreen = null;
            currentGroupIndex = 0;
            currentGroup = null;
            return;
        }
        if (!(minecraftClient.currentScreen instanceof HandledScreen)) return;

        try {
            loadConfigurations();
            boolean wasAnyKeyPressed = keyListener();

            currentScreen = (HandledScreenAccessor) minecraftClient.currentScreen;
            currentSlotsGroupList = GroupGenerator.generateGroupsFromSlots(currentScreen);

            // On screen open
            if (previousScreen != currentScreen) {
                previousScreen = currentScreen;
                if (currentScreen instanceof AnvilScreen anvilScreen) {
                    // since 1.20.x
                    setSearchBoxFocus(((AnvilScreenAccessor) anvilScreen).getNameField(), false);
                }
                if (currentScreen instanceof CreativeInventoryScreen creativeInventoryScreen) {
                    // since 1.20.x
                    TextFieldWidget searchBox = ((CreativeInventoryScreenAccessor) creativeInventoryScreen).getSearchBox();
                    if (searchBox.isActive()) {
                        setSearchBoxFocus(searchBox, false);
                    }
                }

                //<editor-fold desc="Always open recipe book on screen open">
                RecipeBookWidget recipeBookWidget = null;
                if (currentScreen instanceof InventoryScreen inventoryScreen) {
                    recipeBookWidget = inventoryScreen.getRecipeBookWidget();
                } else if (currentScreen instanceof CraftingScreen craftingScreen) {
                    recipeBookWidget = craftingScreen.getRecipeBookWidget();
                }

                if (autoOpenRecipeBook && recipeBookWidget != null) {
                    // since 1.20.x
                    if (!recipeBookWidget.isOpen()) recipeBookWidget.toggleOpen();
                    setSearchBoxFocus(((RecipeBookWidgetAccessor) recipeBookWidget).getSearchField(), false);
                }
                //</editor-fold>

                refreshGroupListAndSelectFirstGroup(false); // Interrupt is false to let it speak the screen's name
            }

            if (currentSlotsGroupList.isEmpty()) return;

            if (speakFocusedSlotChanges) {
                String slotNarrationText = getCurrentSlotNarrationText();
                if (!previousSlotText.equals(slotNarrationText)) {
                    previousSlotText = slotNarrationText;
                    MainClass.speakWithNarratorIfNotEmpty(previousSlotText, true);
                }
            }

            if (wasAnyKeyPressed) interval.start();
        } catch (Exception e) {
            log.error("Error encountered in Inventory Controls feature.", e);
        }
    }

    /**
     * Load configs from config.json
     */
    private void loadConfigurations() {
        InventoryControlsConfigMap map = InventoryControlsConfigMap.getInstance();
        autoOpenRecipeBook = map.isAutoOpenRecipeBook();
        rowAndColumnFormat = map.getRowAndColumnFormat();
        interval = Interval.inMilliseconds(map.getDelayInMilliseconds(), interval);
        speakFocusedSlotChanges = map.isSpeakFocusedSlotChanges();
    }

    /**
     * Handles the key inputs.
     */
    @SuppressWarnings("CommentedOutCode")
    private boolean keyListener() {
        KeyBindingsHandler kbh = KeyBindingsHandler.getInstance();
        boolean isGroupKeyPressed = KeyUtils.isAnyPressed(kbh.inventoryControlsGroupKey);
        boolean isUpKeyPressed = KeyUtils.isAnyPressed(kbh.inventoryControlsUpKey);
        boolean isRightKeyPressed = KeyUtils.isAnyPressed(kbh.inventoryControlsRightKey);
        boolean isDownKeyPressed = KeyUtils.isAnyPressed(kbh.inventoryControlsDownKey);
        boolean isLeftKeyPressed = KeyUtils.isAnyPressed(kbh.inventoryControlsLeftKey);
        boolean isSwitchTabKeyPressed = KeyUtils.isAnyPressed(kbh.inventoryControlsSwitchTabKey);
        boolean isToggleCraftableKeyPressed = KeyUtils.isAnyPressed(kbh.inventoryControlsToggleCraftableKey);
        boolean isLeftShiftPressed = KeyUtils.isLeftShiftPressed();
        boolean isEnterPressed = KeyUtils.isEnterPressed();
        boolean isTPressed = KeyUtils.isAnyPressed(GLFW.GLFW_KEY_T);
        boolean disableInputForSearchBox = false;

        //<editor-fold desc="When using a search box">
        if (currentScreen instanceof CreativeInventoryScreen creativeInventoryScreen) {
            // since 1.20.x
            TextFieldWidget searchBox = ((CreativeInventoryScreenAccessor) creativeInventoryScreen).getSearchBox();
            if (searchBox.isActive()) {
                disableInputForSearchBox = true;
                if (isEnterPressed) {
                    setSearchBoxFocus(searchBox, false);
                    refreshGroupListAndSelectFirstGroup(true);
                    return true;
                }
            }
        }

        if (currentScreen instanceof AnvilScreen anvilScreen) {
            // since 1.20.x
            TextFieldWidget searchBox = ((AnvilScreenAccessor) anvilScreen).getNameField();
            if (searchBox.isActive()) {
                disableInputForSearchBox = true;
                if (isEnterPressed) {
                    setSearchBoxFocus(searchBox, false);
                    previousSlotText = "";
                    return true;
                }
            }
        }
        if (currentScreen instanceof InventoryScreen inventoryScreen && inventoryScreen.getRecipeBookWidget().isOpen()) {
            // since 1.20.x
            TextFieldWidget searchBox = ((RecipeBookWidgetAccessor) inventoryScreen.getRecipeBookWidget()).getSearchField();
            if (searchBox.isActive()) {
                disableInputForSearchBox = true;
                if (isEnterPressed) {
                    setSearchBoxFocus(searchBox, false);
                    previousSlotText = "";
                    return true;
                }
            }
        }
        if (currentScreen instanceof CraftingScreen craftingScreen && craftingScreen.getRecipeBookWidget().isOpen()) {
            // since 1.20.x
            TextFieldWidget searchBox = ((RecipeBookWidgetAccessor) craftingScreen.getRecipeBookWidget()).getSearchField();
            if (searchBox.isActive()) {
                disableInputForSearchBox = true;
                if (isEnterPressed) {
                    setSearchBoxFocus(searchBox, false);
                    previousSlotText = "";
                    return true;
                }
            }
        }
        //</editor-fold>

        if (disableInputForSearchBox) return false; // Skip other key inputs if using a search box

        if (isGroupKeyPressed) {
            log.debug("Group key pressed");
            changeGroup(!isLeftShiftPressed);
            return true;
        }
        if (isSwitchTabKeyPressed) {
            log.debug("Switch Tab key pressed");
            if (currentScreen instanceof InventoryScreen || currentScreen instanceof CraftingScreen)
                changeRecipeTab(!isLeftShiftPressed);
            else if (currentScreen instanceof CreativeInventoryScreen)
                changeCreativeInventoryTab(!isLeftShiftPressed);

            return true;
        }
        if (isUpKeyPressed) {
            log.debug("Up key pressed");
            if (isLeftShiftPressed && currentGroup.isScrollable) {
                if (currentScreen instanceof InventoryScreen inventoryScreen && inventoryScreen.getRecipeBookWidget().isOpen()) {
                    clickPreviousRecipeBookPage(inventoryScreen);
                } else if (currentScreen instanceof CraftingScreen craftingScreen && craftingScreen.getRecipeBookWidget().isOpen()) {
                    clickPreviousRecipeBookPage(craftingScreen);
                } else {
                    MouseUtils.scrollUp();
                }
            } else {
                focusSlotItemAt(FocusDirection.UP);
            }
            return true;
        }
        if (isRightKeyPressed) {
            log.debug("Right key pressed");
            focusSlotItemAt(FocusDirection.RIGHT);
            return true;
        }
        if (isDownKeyPressed) {
            log.debug("Down key pressed");
            if (isLeftShiftPressed && currentGroup.isScrollable) {
                if (currentScreen instanceof InventoryScreen inventoryScreen && inventoryScreen.getRecipeBookWidget().isOpen()) {
                    clickNextRecipeBookPage(inventoryScreen);
                } else if (currentScreen instanceof CraftingScreen craftingScreen && craftingScreen.getRecipeBookWidget().isOpen()) {
                    clickNextRecipeBookPage(craftingScreen);
                } else {
                    MouseUtils.scrollDown();
                }
            } else {
                focusSlotItemAt(FocusDirection.DOWN);
            }
            return true;
        }
        if (isLeftKeyPressed) {
            log.debug("Left key pressed");
            focusSlotItemAt(FocusDirection.LEFT);
            return true;
        }
        if (isTPressed) {
            if (CreativeInventoryScreenAccessor.getSelectedTab().getType() == ItemGroup.Type.SEARCH && currentScreen instanceof CreativeInventoryScreen creativeInventoryScreen) {
                // since 1.20.x
                setSearchBoxFocus(((CreativeInventoryScreenAccessor) creativeInventoryScreen).getSearchBox(), true);
            } else if (currentScreen instanceof AnvilScreen anvilScreen) {
                // since 1.20.x
                setSearchBoxFocus(((AnvilScreenAccessor) anvilScreen).getNameField(), true);
            } else if (currentScreen instanceof InventoryScreen inventoryScreen && inventoryScreen.getRecipeBookWidget().isOpen()) {
                // since 1.20.x
                RecipeBookWidget recipeBookWidget = inventoryScreen.getRecipeBookWidget();
                // resolve can-not-enter-characters-issue https://github.com/khanshoaib3/minecraft-access/issues/67
                inventoryScreen.setFocused(recipeBookWidget);
                setSearchBoxFocus(((RecipeBookWidgetAccessor) recipeBookWidget).getSearchField(), true);
            } else if (currentScreen instanceof CraftingScreen craftingScreen && craftingScreen.getRecipeBookWidget().isOpen()) {
                // since 1.20.x
                RecipeBookWidget recipeBookWidget = craftingScreen.getRecipeBookWidget();
                // resolve can-not-enter-characters-issue https://github.com/khanshoaib3/minecraft-access/issues/67
                craftingScreen.setFocused(recipeBookWidget);
                setSearchBoxFocus(((RecipeBookWidgetAccessor) recipeBookWidget).getSearchField(), true);
            }
            return true;
        }
        if (isToggleCraftableKeyPressed) {
            RecipeBookWidget recipeBookWidget = null;
            if (currentScreen instanceof InventoryScreen inventoryScreen) {
                recipeBookWidget = inventoryScreen.getRecipeBookWidget();
            } else if (currentScreen instanceof CraftingScreen craftingScreen) {
                recipeBookWidget = craftingScreen.getRecipeBookWidget();
            }

            if (recipeBookWidget == null) return false;
            if (!recipeBookWidget.isOpen()) return false;

            ToggleButtonWidget toggleCraftableButton = ((RecipeBookWidgetAccessor) recipeBookWidget).getToggleCraftableButton();

//            int x = toggleCraftableButton.x + 8;
//            int y = toggleCraftableButton.y + 4;
            int x = toggleCraftableButton.getX() + 8;
            int y = toggleCraftableButton.getY() + 4;

            MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(x, y);
            MouseUtils.moveAndLeftClick(p.x(), p.y());
            moveToSlotItem(currentSlotItem, 100);

            log.debug("Recipe toggle key pressed, Showing %s".formatted(toggleCraftableButton.isToggled() ? "all" : "craftable only"));
            MainClass.speakWithNarrator("Showing %s".formatted(toggleCraftableButton.isToggled() ? "all" : "craftable only"), true);

            return true;
        }

        return false;
    }

    private void clickPreviousRecipeBookPage(RecipeBookProvider screen) {
        // int x = ((RecipeBookResultsAccessor) ((RecipeBookWidgetAccessor) craftingScreen.getRecipeBookWidget()).getRecipesArea()).getPrevPageButton().x + 3; // Pre 1.19.3
        // int y = ((RecipeBookResultsAccessor) ((RecipeBookWidgetAccessor) craftingScreen.getRecipeBookWidget()).getRecipesArea()).getPrevPageButton().y + 3; // Pre 1.19.3
        RecipeBookResultsAccessor area = (RecipeBookResultsAccessor) ((RecipeBookWidgetAccessor) screen.getRecipeBookWidget()).getRecipesArea();
        int x = area.getPrevPageButton().getX() + 3; // From 1.19.3
        int y = area.getPrevPageButton().getY() + 3; // From 1.19.3
        MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(x, y);
        MouseUtils.moveAndLeftClick(p.x(), p.y());
        moveToSlotItem(currentSlotItem, 100);
    }

    private void clickNextRecipeBookPage(RecipeBookProvider screen) {
        // int x = ((RecipeBookResultsAccessor) ((RecipeBookWidgetAccessor) craftingScreen.getRecipeBookWidget()).getRecipesArea()).getNextPageButton().x + 3; // Pre 1.19.3
        // int y = ((RecipeBookResultsAccessor) ((RecipeBookWidgetAccessor) craftingScreen.getRecipeBookWidget()).getRecipesArea()).getNextPageButton().y + 3; // Pre 1.19.3
        RecipeBookResultsAccessor area = (RecipeBookResultsAccessor) ((RecipeBookWidgetAccessor) screen.getRecipeBookWidget()).getRecipesArea();
        int x = area.getNextPageButton().getX() + 3; // From 1.19.3
        int y = area.getNextPageButton().getY() + 3; // From 1.19.3
        MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(x, y);
        MouseUtils.moveAndLeftClick(p.x(), p.y());
        moveToSlotItem(currentSlotItem, 100);
    }

    /**
     * Focuses a slot item in the specified direction if available.
     *
     * @param focusDirection The direction of the slot item to focus.
     */
    private void focusSlotItemAt(FocusDirection focusDirection) {
        if (currentGroup == null) {
            changeGroup(true);
            return;
        }
        if (currentSlotItem == null) {
            focusSlotItem(currentGroup.getFirstGroupItem(), true);
            return;
        }

        SlotItem slotItem = getGroupItemInDirection(focusDirection);
        if (slotItem == null) {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.inventory_controls.no_slot_in_direction", focusDirection.getString()), true);
            return;
        }

        focusSlotItem(slotItem, true);
    }

    /**
     * Returns the slot item in the specified direction if available.
     *
     * @param focusDirection The direction of the slot item.
     * @return The object of the slot item if found else null.
     */
    private SlotItem getGroupItemInDirection(FocusDirection focusDirection) {
        switch (focusDirection) {
            case UP -> {
                if (!currentGroup.hasSlotItemAbove(currentSlotItem)) return null;

                if (currentSlotItem.upSlotItem != null) return currentSlotItem.upSlotItem;

                for (SlotItem item : currentGroup.slotItems) {
                    if (item.x == currentSlotItem.x && item.y == currentSlotItem.y - 18) {
                        return item;
                    }
                }
            }
            case RIGHT -> {
                if (!currentGroup.hasSlotItemRight(currentSlotItem)) return null;

                if (currentSlotItem.rightSlotItem != null) return currentSlotItem.rightSlotItem;

                for (SlotItem item : currentGroup.slotItems) {
                    if (item.x == currentSlotItem.x + 18 && item.y == currentSlotItem.y) {
                        return item;
                    }
                }
            }
            case DOWN -> {
                if (!currentGroup.hasSlotItemBelow(currentSlotItem)) return null;

                if (currentSlotItem.downSlotItem != null) return currentSlotItem.downSlotItem;

                for (SlotItem item : currentGroup.slotItems) {
                    if (item.x == currentSlotItem.x && item.y == currentSlotItem.y + 18) {
                        return item;
                    }
                }
            }
            case LEFT -> {
                if (!currentGroup.hasSlotItemLeft(currentSlotItem)) return null;

                if (currentSlotItem.leftSlotItem != null) return currentSlotItem.leftSlotItem;

                for (SlotItem item : currentGroup.slotItems) {
                    if (item.x == currentSlotItem.x - 18 && item.y == currentSlotItem.y) {
                        return item;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Focuses at the specified slot item in the current group and narrate its details.
     *
     * @param slotItem  The object of the slot item to focus.
     * @param interrupt Whether to stop the narrator from speaking the previous message or not.
     */
    private void focusSlotItem(@NotNull SlotItem slotItem, boolean interrupt) {
        currentSlotItem = slotItem;
        moveToSlotItem(currentSlotItem);
//       log.info("Slot %d/%d selected".formatted(slotItem.slot.getIndex() + 1, currentGroup.slotItems.size()));

        String toSpeak = getCurrentSlotNarrationText();
        if (!toSpeak.isEmpty()) {
            previousSlotText = toSpeak;
            MainClass.speakWithNarrator(toSpeak, interrupt);
        }
    }

    /**
     * Moves the mouse cursor over to the slot item specified.
     *
     * @param slotItem The object of the slot item to move the mouse cursor over to.
     */
    private void moveToSlotItem(SlotItem slotItem) {
        if (slotItem == null) return;

        int x = slotItem.x;
        int y = slotItem.y;

        MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(currentScreen.getX() + x, currentScreen.getY() + y);
        MouseUtils.move(p.x(), p.y());
    }

    /**
     * Moves the mouse cursor over to the specified slot item after some delay.
     *
     * @param slotItem The object of the slot item to move the mouse cursor over to.
     * @param delay    The delay in milliseconds.
     */
    @SuppressWarnings("SameParameterValue")
    private void moveToSlotItem(SlotItem slotItem, int delay) {
        if (slotItem == null) return;

        int x = slotItem.x;
        int y = slotItem.y;

        MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(currentScreen.getX() + x, currentScreen.getY() + y);
        MouseUtils.moveAfterDelay(p.x(), p.y(), delay);
    }

    /**
     * Get the details of the current slot item to narrate.
     *
     * @return The details of the current slot item.
     */
    private String getCurrentSlotNarrationText() {
        if (currentSlotItem == null) return "";

        Slot slot = currentSlotItem.slot;
        if (slot == null) {
            return Objects.requireNonNullElse(currentSlotItem.getNarratableText(), I18n.translate("minecraft_access.inventory_controls.Unknown"));
        }
        if (!slot.hasStack()) {
            return I18n.translate("minecraft_access.inventory_controls.empty_slot", currentGroup.getSlotPrefix(slot));
        }

        ItemStack itemStack = slot.getStack();
        // <slot row col prefix> <count>
        String info = "%s %d".formatted(currentGroup.getSlotPrefix(slot), itemStack.getCount());

        // <name> <description>
        StringBuilder toolTipString = new StringBuilder();
        List<Text> toolTipList = itemStack.getTooltip(TooltipContext.DEFAULT, minecraftClient.player, TooltipType.BASIC);
        for (Text line : toolTipList) {
            toolTipString.append(line.getString()).append(" ");
        }

        // issue #311 after 1.21: Can't get Music Discs' name from Tooltip, use this instead
        Optional.ofNullable(itemStack.get(DataComponentTypes.JUKEBOX_PLAYABLE)).ifPresent((jukeboxPlayableComponent) -> {
            String discNumber = jukeboxPlayableComponent.song().key().getValue().getPath();
            toolTipString.append(" ").append(I18n.translate("jukebox_song.minecraft." + discNumber));
        });

        // <slot row col prefix> <count> <name> <description>
        return "%s %s".formatted(info, toolTipString.toString());
    }

    /**
     * Change the selected group.
     *
     * @param goForward Whether to switch to next group or previous group.
     */
    private void changeGroup(boolean goForward) {
        int nextGroupIndex = currentGroupIndex + (goForward ? 1 : -1);
        nextGroupIndex = MathHelper.clamp(nextGroupIndex, 0, currentSlotsGroupList.size() - 1);

        if (nextGroupIndex == currentGroupIndex) return; // Skip if already at the first or last group

        currentGroupIndex = nextGroupIndex;
        currentGroup = currentSlotsGroupList.get(currentGroupIndex);
        log.debug("Group(name:%s) %d/%d selected".formatted(currentGroup.getGroupName(), currentGroupIndex + 1, currentSlotsGroupList.size()));
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.inventory_controls.group_selected",
                currentGroup.isScrollable ? I18n.translate("minecraft_access.inventory_controls.scrollable") : "",
                currentGroup.getGroupName()), true);

        focusSlotItem(currentGroup.getFirstGroupItem(), false);
    }

    /**
     * Refreshes the current group list and selects the first group.
     *
     * @param interrupt Whether to stop the narrator from speaking the previous message or not.
     */
    private void refreshGroupListAndSelectFirstGroup(boolean interrupt) {
        currentSlotsGroupList = GroupGenerator.generateGroupsFromSlots(currentScreen);
        if (currentSlotsGroupList.isEmpty()) return;

        currentGroupIndex = 0;
        currentGroup = currentSlotsGroupList.get(0);
        log.debug("Group(name:%s) %d/%d selected".formatted(currentGroup.getGroupName(), currentGroupIndex + 1, currentSlotsGroupList.size()));
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.inventory_controls.group_selected",
                currentGroup.isScrollable ? I18n.translate("minecraft_access.inventory_controls.scrollable") : "",
                currentGroup.getGroupName()), interrupt);
        focusSlotItem(currentGroup.getFirstGroupItem(), false);
    }

    /**
     * Changes the selected tab for creative inventory screen.
     *
     * @param goForward Whether to switch to next tab or previous tab.
     */
    @SuppressWarnings("ConstantValue")
    private void changeCreativeInventoryTab(boolean goForward) {
        if (!(currentScreen instanceof CreativeInventoryScreen creativeInventoryScreen)) return;

        ListIterator<ItemGroup> nextTab = ItemGroups.getGroupsToDisplay().listIterator();

        //noinspection StatementWithEmptyBody
        while (nextTab.hasNext() && nextTab.next() != CreativeInventoryScreenAccessor.getSelectedTab()) {
        }

        if (goForward && nextTab.hasNext()) {
            ((CreativeInventoryScreenAccessor) creativeInventoryScreen).invokeSetSelectedTab(nextTab.next());
            refreshGroupListAndSelectFirstGroup(false);
        } else if (!goForward) {
            nextTab.previous();
            if (nextTab.hasPrevious()) {
                ((CreativeInventoryScreenAccessor) creativeInventoryScreen).invokeSetSelectedTab(nextTab.previous());
                refreshGroupListAndSelectFirstGroup(false);
            }
        }
    }

    /**
     * Changes the selected tab for inventory/crafting screen.
     *
     * @param goForward Whether to switch to next tab or previous tab.
     */
    @SuppressWarnings("CommentedOutCode")
    private void changeRecipeTab(boolean goForward) {
        RecipeBookWidget recipeBookWidget = null;
        if (currentScreen instanceof InventoryScreen inventoryScreen) {
            recipeBookWidget = inventoryScreen.getRecipeBookWidget();
        } else if (currentScreen instanceof CraftingScreen craftingScreen) {
            recipeBookWidget = craftingScreen.getRecipeBookWidget();
        }

        if (recipeBookWidget == null) return;
        if (!recipeBookWidget.isOpen()) return;

        RecipeBookWidgetAccessor recipeBookWidgetAccessor = (RecipeBookWidgetAccessor) recipeBookWidget;
        int currentTabIndex = recipeBookWidgetAccessor.getTabButtons().indexOf(recipeBookWidgetAccessor.getCurrentTab());

        int nextTabIndex = currentTabIndex + (goForward ? 1 : -1);
        nextTabIndex = MathHelper.clamp(nextTabIndex, 0, recipeBookWidgetAccessor.getTabButtons().size() - 1);

//        int x = recipeBookWidgetAccessor.getTabButtons().get(nextTabIndex).x + 9;
//        int y = recipeBookWidgetAccessor.getTabButtons().get(nextTabIndex).y + 9;
        int x = recipeBookWidgetAccessor.getTabButtons().get(nextTabIndex).getX() + 9;
        int y = recipeBookWidgetAccessor.getTabButtons().get(nextTabIndex).getY() + 9;

        MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(x, y);
        MouseUtils.moveAndLeftClick(p.x(), p.y());
        moveToSlotItem(currentSlotItem, 100);

        log.debug("Change tab to %s".formatted(recipeBookWidgetAccessor.getCurrentTab().getCategory().name()));
    }

    /**
     * Encapsulate the changes against the vanilla code here.
     * Correspond to the vanilla code after 1.20.x
     */
    private void setSearchBoxFocus(TextFieldWidget w, boolean focus) {
        if (focus) {
            log.debug("T key pressed, selecting the search box.");
            w.setFocused(true);
        } else {
            log.debug("Enter key pressed, deselecting the search box.");
            boolean origin = ((TextFieldWidgetAccessor) w).getFocusUnlocked();
            w.setFocusUnlocked(true);
            w.setFocused(false);
            // set origin value back since we don't know what it is and don't want to screw up the inner state.
            w.setFocusUnlocked(origin);
        }
    }
}
