package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import me.shedaniel.clothconfig2.gui.AbstractTabbedConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.mixin.ScreenAccessor;
import org.mcaccess.minecraftaccess.utils.ui.NavigationUtils;

/**
 * remap=false: suppress warnings since cloth isn't part of original game
 */
@Mixin(value = ClothConfigScreen.class, remap = false)
abstract class ClothConfigScreenMixin extends AbstractTabbedConfigScreen {
    @Shadow
    @Final
    private List<ClothConfigTabButton> tabButtons;

    @Shadow
    private AbstractWidget buttonRightTab;

    @Shadow
    private AbstractWidget buttonLeftTab;

    @Shadow
    public ClothConfigScreen.ListWidget<AbstractConfigEntry<AbstractConfigEntry<?>>> listWidget;

    ClothConfigScreenMixin(Screen parent, Component title, ResourceLocation backgroundLocation) {
        super(parent, title, backgroundLocation);
    }

    @Override
    @Shadow
    public abstract Component getSelectedCategory();

    /**
     * @implNote enable remap since this is a vanilla method from {@link Screen#init()}
     */
    @Inject(at = @At("TAIL"), method = "init", remap = true)
    private void addComponentsAsNarratables(CallbackInfo ci) {
        List<NarratableEntry> narratables = ((ScreenAccessor) this).getNarratables();
        if (listWidget != null) {
            narratables.addAll(listWidget.children());
        }
        narratables.addAll(tabButtons);
    }

    /**
     * Override inherited {@link Screen#setInitialFocus()}'s logic to not automatically focus on next component after reinitialize.
     * Due to unexpected behavior combination, pressing Enter key on {@link ClothConfigTabButton} always results in focusing on cancel button,
     * which is different from mouse clicking on {@link ClothConfigTabButton} (which clean focus to null).
     * ({@link ClothConfigTabButton#onPress()} triggers {@link ClothConfigScreen#init()}, which triggers {@link Screen#setInitialFocus()})
     */
    @Override
    protected void setInitialFocus() {
    }

    @Override
    public @NotNull Component getNarrationMessage() {
        return super.getNarrationMessage().copy().append(I18n.get("minecraft_access.other.words_connection")).append(getSelectedCategory());
    }

    /**
     * Inspired by {@link TabNavigationBar#keyPressed(int, int, int)}.
     * Use Control + Tab (and Control + Shift + Tab) to switch between tab buttons.
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (Minecraft.getInstance().hasControlDown() && event.key() == InputConstants.KEY_TAB) {
            switchCategory(!Minecraft.getInstance().hasShiftDown());
            return true;
        }
        return super.keyPressed(event);
    }

    @Unique
    private void switchCategory(boolean forward) {
        int nextIndex = selectedCategoryIndex + (forward ? 1 : -1);
        if (nextIndex < 0 || nextIndex >= tabButtons.size()) {
            MainClass.narrate(I18n.get("minecraft_access.other.reached_the_border"), true);
            return;
        }

        ClothConfigTabButton tabButton = tabButtons.get(nextIndex);

        if (tabButton.isMouseOver(tabButton.getX() + 1, tabButton.getY() + 1)) {
            // the tab button is visible, click it
            tabButton.mouseClicked(new MouseButtonEvent(tabButton.getX() + 1, tabButton.getY() + 1, new MouseButtonInfo(0, 0)), false);
        } else {
            // the tab button is invisible, scroll tab menu
            AbstractWidget arrowButton = forward ? buttonRightTab : buttonLeftTab;
            arrowButton.mouseClicked(new MouseButtonEvent(arrowButton.getX() + 1, arrowButton.getY() + 1, new MouseButtonInfo(0, 0)), false);
            // but the scroll needs ticking to be finished, so directly trigger the tab button by calling onPress
            tabButton.onPress(new MouseButtonInfo(0, 0));
        }
    }
}
