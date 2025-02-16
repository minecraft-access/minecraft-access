package org.mcaccess.minecraftaccess.features.inventory_controls;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.config.config_maps.InventoryControlsConfigMap;
import org.mcaccess.minecraftaccess.mixin.*;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

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
    @Getter
    private String rowAndColumnFormat;
    private final Interval interval = Interval.defaultDelay();
    private Minecraft minecraftClient;

    private AbstractContainerScreenAccessor previousScreen = null;
    private AbstractContainerScreenAccessor currentScreen = null;

    private List<SlotsGroup> currentSlotsGroupList = null;
    private SlotsGroup currentGroup = null;
    private int currentGroupIndex = 0;
    private SlotItem currentSlotItem = null;
    private RecipeBookComponent<?> currentRecipeBookWidget = null;
    private String previousSlotText = "";
    private boolean speakFocusedSlotChanges = true;

    private enum FocusDirection {
        UP("gui.up"),
        DOWN("gui.down"),
        LEFT("minecraft_access.inventory_controls.direction_left"),
        RIGHT("minecraft_access.inventory_controls.direction_right");

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

    public void update() {
        if (!interval.isReady()) return;
        this.minecraftClient = Minecraft.getInstance();

        if (minecraftClient == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen == null) {
            previousScreen = null;
            currentScreen = null;
            currentGroupIndex = 0;
            currentGroup = null;
            currentRecipeBookWidget = null;
            return;
        }
        if (!(minecraftClient.screen instanceof AbstractContainerScreen)) return;

        try {
            loadConfigurations();
            currentScreen = (AbstractContainerScreenAccessor) minecraftClient.screen;
            currentRecipeBookWidget = getRecipeBookWidget(minecraftClient.screen);
            currentSlotsGroupList = GroupGenerator.generateGroupsFromSlots(currentScreen);

            interval.adjustNextReadyTimeBy(keyListener());

            // On screen open
            if (previousScreen != currentScreen) {
                previousScreen = currentScreen;
                if (currentScreen instanceof AnvilScreen anvilScreen) {
                    setSearchBoxFocus(((AnvilScreenAccessor) anvilScreen).getName(), false);
                }
                if (currentScreen instanceof CreativeModeInventoryScreen creativeInventoryScreen) {
                    EditBox searchBox = ((CreativeModeInventoryScreenAccessor) creativeInventoryScreen).getSearchBox();
                    if (searchBox.canConsumeInput()) {
                        setSearchBoxFocus(searchBox, false);
                    }
                }

                //<editor-fold desc="Always open recipe book on screen open">
                if (autoOpenRecipeBook && currentRecipeBookWidget != null) {
                    if (!currentRecipeBookWidget.isVisible()) currentRecipeBookWidget.toggleVisibility();
                    setSearchBoxFocus(((RecipeBookComponentAccessor) currentRecipeBookWidget).getSearchBox(), false);
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

        } catch (Exception e) {
            log.error("Error encountered in Inventory Controls feature.", e);
        }
    }

    private @Nullable RecipeBookComponent<?> getRecipeBookWidget(Screen screen) {
        if (screen instanceof AbstractRecipeBookScreen<?> recipeBookScreen) {
            return ((AbstractRecipeBookScreenAccessor) recipeBookScreen).getRecipeBookComponent();
        }
        return null;
    }

    /**
     * Load configs from config.json
     */
    private void loadConfigurations() {
        InventoryControlsConfigMap map = InventoryControlsConfigMap.getInstance();
        autoOpenRecipeBook = map.isAutoOpenRecipeBook();
        rowAndColumnFormat = map.getRowAndColumnFormat();
        interval.setDelay(map.getDelayInMilliseconds(), Interval.Unit.Millisecond);
        speakFocusedSlotChanges = map.isSpeakFocusedSlotChanges();
    }

    /**
     * Handles the key inputs.
     */
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
        if (currentScreen instanceof CreativeModeInventoryScreen creativeInventoryScreen) {
            EditBox searchBox = ((CreativeModeInventoryScreenAccessor) creativeInventoryScreen).getSearchBox();
            if (searchBox.canConsumeInput()) {
                disableInputForSearchBox = true;
                if (isEnterPressed) {
                    setSearchBoxFocus(searchBox, false);
                    refreshGroupListAndSelectFirstGroup(true);
                    return true;
                }
            }
        }

        if (currentScreen instanceof AnvilScreen anvilScreen) {
            EditBox searchBox = ((AnvilScreenAccessor) anvilScreen).getName();
            if (searchBox.canConsumeInput()) {
                disableInputForSearchBox = true;
                if (isEnterPressed) {
                    setSearchBoxFocus(searchBox, false);
                    previousSlotText = "";
                    return true;
                }
            }
        }

        if (recipeBookIsOpening()) {
            EditBox searchBox = ((RecipeBookComponentAccessor) currentRecipeBookWidget).getSearchBox();
            if (searchBox.canConsumeInput()) {
                disableInputForSearchBox = true;
                if (isEnterPressed) {
                    setSearchBoxFocus(searchBox, false);
                    previousSlotText = "";
                    return true;
                }
            }
        }
        //</editor-fold>

        if (disableInputForSearchBox) return true; // Skip other key inputs if using a search box

        if (isGroupKeyPressed) {
            log.debug("Group key pressed");
            changeGroup(!isLeftShiftPressed);
            return true;
        }
        if (isSwitchTabKeyPressed) {
            log.debug("Switch Tab key pressed");
            if (currentScreen instanceof InventoryScreen || currentScreen instanceof CraftingScreen)
                changeRecipeTab(!isLeftShiftPressed);
            else if (currentScreen instanceof CreativeModeInventoryScreen)
                changeCreativeInventoryTab(!isLeftShiftPressed);

            return true;
        }

        if (isUpKeyPressed) {
            log.debug("Up key pressed");
            if (isLeftShiftPressed && currentGroup.isScrollable) {
                if (recipeBookIsOpening()) {
                    clickPreviousRecipeBookPage();
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
                if (recipeBookIsOpening()) {
                    clickNextRecipeBookPage();
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
            if (CreativeModeInventoryScreenAccessor.getSelectedTab().getType() == CreativeModeTab.Type.SEARCH && currentScreen instanceof CreativeModeInventoryScreen creativeInventoryScreen) {
                setSearchBoxFocus(((CreativeModeInventoryScreenAccessor) creativeInventoryScreen).getSearchBox(), true);
            } else if (currentScreen instanceof AnvilScreen anvilScreen) {
                setSearchBoxFocus(((AnvilScreenAccessor) anvilScreen).getName(), true);
            } else if (recipeBookIsOpening()) {
                // resolve can-not-enter-characters-issue https://github.com/khanshoaib3/minecraft-access/issues/67
                minecraftClient.screen.setFocused(currentRecipeBookWidget);
                setSearchBoxFocus(((RecipeBookComponentAccessor) currentRecipeBookWidget).getSearchBox(), true);
            }
            return true;
        }

        if (isToggleCraftableKeyPressed) {
            if (currentRecipeBookWidget == null) return false;
            if (!currentRecipeBookWidget.isVisible()) return false;

            StateSwitchingButton toggleCraftableButton = ((RecipeBookComponentAccessor) currentRecipeBookWidget).getFilterButton();

            int x = toggleCraftableButton.getX() + 8;
            int y = toggleCraftableButton.getY() + 4;

            MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(x, y);
            MouseUtils.moveAndLeftClick(p.x(), p.y());
            moveToSlotItem(currentSlotItem, 100);

            String text = toggleCraftableButton.isStateTriggered()
                    ? I18n.get("gui.recipebook.toggleRecipes.all")
                    : ((RecipeBookComponentAccessor) currentRecipeBookWidget).callGetRecipeFilterName().getString();
            log.debug("Recipe toggle key pressed, {}", text);
            MainClass.speakWithNarrator(text, true);

            return true;
        }

        return false;
    }

    private boolean recipeBookIsOpening() {
        return currentRecipeBookWidget != null && currentRecipeBookWidget.isVisible();
    }

    private void clickPreviousRecipeBookPage() {
        RecipeBookPageAccessor area = (RecipeBookPageAccessor) ((RecipeBookComponentAccessor) currentRecipeBookWidget).getRecipeBookPage();
        int x = area.getBackButton().getX() + 3;
        int y = area.getBackButton().getY() + 3;
        MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(x, y);
        MouseUtils.moveAndLeftClick(p.x(), p.y());
        moveToSlotItem(currentSlotItem, 100);
    }

    private void clickNextRecipeBookPage() {
        RecipeBookPageAccessor area = (RecipeBookPageAccessor) ((RecipeBookComponentAccessor) currentRecipeBookWidget).getRecipeBookPage();
        int x = area.getForwardButton().getX() + 3;
        int y = area.getForwardButton().getY() + 3;
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
            MainClass.speakWithNarrator(I18n.get("minecraft_access.inventory_controls.no_slot_in_direction", I18n.get(focusDirection.getString())), true);
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

        MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(currentScreen.getLeftPos() + x, currentScreen.getTopPos() + y);
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

        MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(currentScreen.getLeftPos() + x, currentScreen.getTopPos() + y);
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
            return Objects.requireNonNullElse(currentSlotItem.getNarratableText(), I18n.get("minecraft_access.inventory_controls.Unknown"));
        }
        if (!slot.hasItem()) {
            return I18n.get("minecraft_access.inventory_controls.empty_slot", currentGroup.getSlotPrefix(slot));
        }

        ItemStack itemStack = slot.getItem();
        // <slot row col prefix> <count>
        String info = "%s %d".formatted(currentGroup.getSlotPrefix(slot), itemStack.getCount());

        // <name> <description>
        StringBuilder toolTipString = new StringBuilder();
        List<Component> toolTipList = itemStack.getTooltipLines(TooltipContext.EMPTY, minecraftClient.player, TooltipFlag.NORMAL);
        for (Component line : toolTipList) {
            toolTipString.append(line.getString()).append(" ");
        }

        // issue #311 after 1.21: Can't get Music Discs' name from Tooltip, use this instead
        Optional.ofNullable(itemStack.get(DataComponents.JUKEBOX_PLAYABLE)).ifPresent((jukeboxPlayableComponent) -> {
            String discNumber = jukeboxPlayableComponent.song().key().location().getPath();
            toolTipString.append(" ").append(I18n.get("jukebox_song.minecraft." + discNumber));
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
        nextGroupIndex = Mth.clamp(nextGroupIndex, 0, currentSlotsGroupList.size() - 1);

        if (nextGroupIndex == currentGroupIndex) return; // Skip if already at the first or last group
        currentGroupIndex = nextGroupIndex;
        selectGroup(true);
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
        selectGroup(interrupt);
    }

    private void selectGroup(boolean interrupt) {
        currentGroup = currentSlotsGroupList.get(currentGroupIndex);
        log.debug("Group(name:{}) {}/{} selected", currentGroup.getGroupName(), currentGroupIndex + 1, currentSlotsGroupList.size());
        MainClass.speakWithNarrator(I18n.get("minecraft_access.inventory_controls.group_selected",
                currentGroup.isScrollable ? I18n.get("minecraft_access.inventory_controls.scrollable") : "",
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
        if (!(currentScreen instanceof CreativeModeInventoryScreen creativeInventoryScreen)) return;

        ListIterator<CreativeModeTab> nextTab = CreativeModeTabs.tabs().listIterator();

        //noinspection StatementWithEmptyBody
        while (nextTab.hasNext() && nextTab.next() != CreativeModeInventoryScreenAccessor.getSelectedTab()) {
        }

        if (goForward && nextTab.hasNext()) {
            ((CreativeModeInventoryScreenAccessor) creativeInventoryScreen).invokeSelectTab(nextTab.next());
            refreshGroupListAndSelectFirstGroup(false);
        } else if (!goForward) {
            nextTab.previous();
            if (nextTab.hasPrevious()) {
                ((CreativeModeInventoryScreenAccessor) creativeInventoryScreen).invokeSelectTab(nextTab.previous());
                refreshGroupListAndSelectFirstGroup(false);
            }
        }
    }

    /**
     * Changes the selected tab for inventory/crafting screen.
     *
     * @param goForward Whether to switch to next tab or previous tab.
     */
    private void changeRecipeTab(boolean goForward) {
        if (currentRecipeBookWidget == null) return;
        if (!currentRecipeBookWidget.isVisible()) return;

        RecipeBookComponentAccessor recipeBookComponentAccessor = (RecipeBookComponentAccessor) currentRecipeBookWidget;
        int currentTabIndex = recipeBookComponentAccessor.getTabButtons().indexOf(recipeBookComponentAccessor.getSelectedTab());

        int nextTabIndex = currentTabIndex + (goForward ? 1 : -1);
        nextTabIndex = Mth.clamp(nextTabIndex, 0, recipeBookComponentAccessor.getTabButtons().size() - 1);

        int x = recipeBookComponentAccessor.getTabButtons().get(nextTabIndex).getX() + 9;
        int y = recipeBookComponentAccessor.getTabButtons().get(nextTabIndex).getY() + 9;

        MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(x, y);
        MouseUtils.moveAndLeftClick(p.x(), p.y());
        moveToSlotItem(currentSlotItem, 100);

        ExtendedRecipeBookCategory category = recipeBookComponentAccessor.getSelectedTab().getCategory();
        log.debug("Change tab to {}", ((SearchRecipeBookCategory) category).name());
    }

    /**
     * Encapsulate the changes against the vanilla code here.
     * Correspond to the vanilla code after 1.20.x
     */
    private void setSearchBoxFocus(EditBox w, boolean focus) {
        if (focus) {
            log.debug("T key pressed, selecting the search box.");
            w.setFocused(true);
        } else {
            log.debug("Enter key pressed, deselecting the search box.");
            boolean origin = ((EditBoxAccessor) w).getCanLoseFocus();
            w.setCanLoseFocus(true);
            w.setFocused(false);
            // set origin value back since we don't know what it is and don't want to screw up the inner state.
            w.setCanLoseFocus(origin);
        }
    }
}
