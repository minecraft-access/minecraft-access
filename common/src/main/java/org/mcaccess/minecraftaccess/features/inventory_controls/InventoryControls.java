package org.mcaccess.minecraftaccess.features.inventory_controls;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.mixin.AbstractContainerScreenAccessor;
import org.mcaccess.minecraftaccess.mixin.AbstractRecipeBookScreenAccessor;
import org.mcaccess.minecraftaccess.mixin.AnvilScreenAccessor;
import org.mcaccess.minecraftaccess.mixin.CreativeModeInventoryScreenAccessor;
import org.mcaccess.minecraftaccess.mixin.EditBoxAccessor;
import org.mcaccess.minecraftaccess.mixin.RecipeBookComponentAccessor;
import org.mcaccess.minecraftaccess.mixin.RecipeBookPageAccessor;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

/**
 * This features lets us use keyboard in inventory screens. Works with all default minecraft screens.
 * <p>
 * Key binds and combinations:
 * (all key binds are re-mappable(except two keys) from the game's controls menu and these key binds do not interrupt with any other key with same key.)<br>
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
public class InventoryControls implements BalmClientModule {
    private Config.InventoryControls config;
    private final Interval interval = Interval.defaultDelay();

    private AbstractContainerScreenAccessor previousScreen = null;
    private AbstractContainerScreenAccessor currentScreen = null;

    private List<SlotsGroup> currentSlotsGroupList = null;
    private SlotsGroup currentGroup = null;
    private int currentGroupIndex = 0;
    private SlotItem currentSlotItem = null;
    private RecipeBookComponent<?> currentRecipeBookWidget = null;
    private String previousSlotText = "";

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls");
    }

    @Override
    public void initialize() {
        ClientTickCallback.AFTER.register(this::tick);

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.change_group/previous"))
                .withDefault(InputBinding.key(InputConstants.KEY_C, KeyModifiers.of(KeyModifier.SHIFT)))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    log.debug("Group key pressed");
                    changeGroup(false);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.change_group/next"))
                .withDefault(InputBinding.key(InputConstants.KEY_C))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    log.debug("Group key pressed");
                    changeGroup(true);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.switch_tab/previous"))
                .withDefault(InputBinding.key(InputConstants.KEY_V, KeyModifiers.of(KeyModifier.SHIFT)))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    log.debug("Switch Tab key pressed");
                    if (currentScreen instanceof InventoryScreen || currentScreen instanceof CraftingScreen) {
                        changeRecipeTab(false);
                        return true;
                    } else if (currentScreen instanceof CreativeModeInventoryScreen) {
                        changeCreativeInventoryTab(false);
                        return true;
                    }
                    return false;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.switch_tab/next"))
                .withDefault(InputBinding.key(InputConstants.KEY_V))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    log.debug("Switch Tab key pressed");
                    if (currentScreen instanceof InventoryScreen || currentScreen instanceof CraftingScreen) {
                        changeRecipeTab(true);
                        return true;
                    } else if (currentScreen instanceof CreativeModeInventoryScreen) {
                        changeCreativeInventoryTab(true);
                        return true;
                    }
                    return false;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.move/left"))
                .withDefault(InputBinding.key(InputConstants.KEY_J))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    log.debug("Left key pressed");
                    focusSlotItemAt(FocusDirection.LEFT);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.move/right"))
                .withDefault(InputBinding.key(InputConstants.KEY_L))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    log.debug("Right key pressed");
                    focusSlotItemAt(FocusDirection.RIGHT);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.move/up"))
                .withDefault(InputBinding.key(InputConstants.KEY_I))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    log.debug("Up key pressed");
                    focusSlotItemAt(FocusDirection.UP);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.scroll/up"))
                .withDefault(InputBinding.key(InputConstants.KEY_I, KeyModifiers.of(KeyModifier.SHIFT)))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    if (currentGroup.isScrollable) {
                        log.debug("Up key pressed");
                        if (isRecipeBookOpen()) {
                            clickPreviousRecipeBookPage();
                            return true;
                        } else {
                            MouseUtils.Wheel.UP.scroll();
                            return true;
                        }
                    }
                    return false;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.move/down"))
                .withDefault(InputBinding.key(InputConstants.KEY_K))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    log.debug("Down key pressed");
                    focusSlotItemAt(FocusDirection.DOWN);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.scroll/down"))
                .withDefault(InputBinding.key(InputConstants.KEY_K, KeyModifiers.of(KeyModifier.SHIFT)))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    if (currentGroup.isScrollable) {
                        log.debug("Down key pressed");
                        if (isRecipeBookOpen()) {
                            clickNextRecipeBookPage();
                            return true;
                        } else {
                            MouseUtils.Wheel.DOWN.scroll();
                            return true;
                        }
                    }
                    return false;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.jump_to_textbox"))
                .withDefault(InputBinding.key(InputConstants.KEY_T))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    if (CreativeModeInventoryScreenAccessor.getSelectedTab().getType() == CreativeModeTab.Type.SEARCH && currentScreen instanceof CreativeModeInventoryScreen creativeInventoryScreen) {
                        setSearchBoxFocus(((CreativeModeInventoryScreenAccessor) creativeInventoryScreen).getSearchBox(), true);
                        return true;
                    } else if (currentScreen instanceof AnvilScreen anvilScreen) {
                        setSearchBoxFocus(((AnvilScreenAccessor) anvilScreen).getName(), true);
                        return true;
                    } else if (isRecipeBookOpen()) {
                        // resolve can-not-enter-characters-issue https://github.com/minecraft-access/minecraft-access/issues/67
                        Minecraft.getInstance().screen.setFocused(currentRecipeBookWidget);
                        setSearchBoxFocus(((RecipeBookComponentAccessor) currentRecipeBookWidget).getSearchBox(), true);
                        return true;
                    }
                    return false;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.toggle_craftable"))
                .withDefault(InputBinding.key(InputConstants.KEY_R))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    if (currentRecipeBookWidget == null) return false;
                    if (!currentRecipeBookWidget.isVisible()) return false;

                    CycleButton<Boolean> toggleCraftableButton = ((RecipeBookComponentAccessor) currentRecipeBookWidget).getFilterButton();

                    int x = toggleCraftableButton.getX() + 8;
                    int y = toggleCraftableButton.getY() + 4;

                    MouseUtils.Coordinates p = MouseUtils.calcRealPositionOfWidget(x, y);
                    MouseUtils.moveAndLeftClick(p.x(), p.y());
                    moveToSlotItem(currentSlotItem, 100);

                    String narration = toggleCraftableButton.getValue()
                            ? ((RecipeBookComponentAccessor) currentRecipeBookWidget).callGetRecipeFilterName().getString()
                            : I18n.get("gui.recipebook.toggleRecipes.all");
                    MainClass.narrate(narration, true);

                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.fuel_status"))
                .withDefault(InputBinding.key(InputConstants.KEY_U))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(event -> {
                    if (currentScreen.getMenu() instanceof AbstractFurnaceMenu furnace) {
                        MainClass.narrate(I18n.get("minecraft_access.inventory_controls.fuel_status",
                                Math.round(furnace.getLitProgress() * 100),
                                Math.round(furnace.getBurnProgress() * 100)), true);
                        return true;
                    } else if (currentScreen instanceof BrewingStandScreen brewingStand) {
                        BrewingStandMenu menu = brewingStand.getMenu();
                        MainClass.narrate(I18n.get("minecraft_access.inventory_controls.fuel_status",
                                (menu.getFuel() * 100) / BrewingStandBlockEntity.FUEL_USES,
                                (menu.getBrewingTicks() * 100) / PotionBrewing.BREWING_TIME_SECONDS * 20), true);
                        return true;
                    }
                    return false;
                })
                .build();
    }

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
            return value;
        }
    }

    public InventoryControls() {
        loadConfig();
    }

    private void tick(Minecraft client) {
        if (!interval.isReady()) return;

        if (client.player == null) return;
        if (client.screen == null) {
            previousScreen = null;
            currentScreen = null;
            currentGroupIndex = 0;
            currentGroup = null;
            currentRecipeBookWidget = null;
            return;
        }
        if (!(client.screen instanceof AbstractContainerScreen)) return;
        if (!config.enabled) {
            return;
        }

        loadConfig();
        currentScreen = (AbstractContainerScreenAccessor) client.screen;
        currentRecipeBookWidget = getRecipeBookWidget(client.screen);
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
            if (config.autoOpenRecipeBook && currentRecipeBookWidget != null) {
                if (!currentRecipeBookWidget.isVisible()) currentRecipeBookWidget.toggleVisibility();
                setSearchBoxFocus(((RecipeBookComponentAccessor) currentRecipeBookWidget).getSearchBox(), false);
            }
            //</editor-fold>

            refreshGroupListAndSelectFirstGroup(false); // Interrupt is false to let it narrate the screen's name
        }

        if (currentSlotsGroupList.isEmpty()) return;

        if (config.narrateFocusedSlotChanges) {
            String slotNarrationText = getCurrentSlotNarrationText();
            if (!previousSlotText.equals(slotNarrationText)) {
                previousSlotText = slotNarrationText;
                MainClass.narrate(previousSlotText, true);
            }
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
    private void loadConfig() {
        config = Config.getInstance().inventoryControls;
        interval.setDelay(config.delayMilliseconds, Interval.Unit.MILLISECOND);
    }

    /**
     * Handles the key inputs.
     */
    private boolean keyListener() {
        Minecraft client = Minecraft.getInstance();
        boolean isEnterPressed = InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_RETURN)
                || InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_NUMPADENTER);
        boolean disableInputForSearchBox = false;

        //<editor-fold desc="When using a search box">
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

        if (isRecipeBookOpen()) {
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

        return false;
    }

    private boolean isRecipeBookOpen() {
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
            MainClass.narrate(I18n.get("minecraft_access.inventory_controls.no_slot_in_direction", I18n.get(focusDirection.getString())), true);
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
     * @param interrupt Whether to stop the narrator from narrating the previous message or not.
     */
    private void focusSlotItem(@NotNull SlotItem slotItem, boolean interrupt) {
        currentSlotItem = slotItem;
        moveToSlotItem(currentSlotItem);

        String narration = getCurrentSlotNarrationText();
        if (!narration.isEmpty()) {
            previousSlotText = narration;
            MainClass.narrate(narration, interrupt);
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
        String info = "%s %s".formatted(currentGroup.getSlotPrefix(slot),
                (itemStack.getCount() != 1 && !itemStack.isEmpty()) ? String.valueOf(itemStack.getCount()) : "");

        // <name> <description>
        StringBuilder toolTipString = new StringBuilder();
        List<Component> toolTipList = itemStack.getTooltipLines(TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
        for (Component line : toolTipList) {
            toolTipString.append(line.getString()).append(' ');
        }

        Optional.ofNullable(itemStack.get(DataComponents.JUKEBOX_PLAYABLE))
                .flatMap(jukeboxPlayable -> jukeboxPlayable.song().key())
                .ifPresent(discNumber -> toolTipString.append(' ').append(I18n.get("jukebox_song.minecraft." + discNumber.identifier().getPath())));

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
     * @param interrupt Whether to stop the narrator from narrating the previous message or not.
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
        MainClass.narrate(I18n.get("minecraft_access.inventory_controls.group_selected",
                currentGroup.isScrollable ? I18n.get("minecraft_access.inventory_controls.scrollable") : "",
                currentGroup.getGroupName()), interrupt);
        focusSlotItem(currentGroup.getFirstGroupItem(), false);
    }

    /**
     * Changes the selected tab for creative inventory screen.
     *
     * @param goForward Whether to switch to next tab or previous tab.
     */
    private void changeCreativeInventoryTab(boolean goForward) {
        if (!(currentScreen instanceof CreativeModeInventoryScreen creativeInventoryScreen)) return;

        int tab = CreativeModeTabs.tabs().indexOf(CreativeModeInventoryScreenAccessor.getSelectedTab());

        if (goForward && tab + 1 < CreativeModeTabs.tabs().size()) {
            ((CreativeModeInventoryScreenAccessor) creativeInventoryScreen).invokeSelectTab(CreativeModeTabs.tabs().get(tab + 1));
            refreshGroupListAndSelectFirstGroup(false);
        } else if (!goForward && tab - 1 >= 0) {
            ((CreativeModeInventoryScreenAccessor) creativeInventoryScreen).invokeSelectTab(CreativeModeTabs.tabs().get(tab - 1));
            refreshGroupListAndSelectFirstGroup(false);
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
            w.setCanLoseFocus(origin);
        }
    }
}
