package org.mcaccess.minecraftaccess.features.inventory_controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.mcaccess.minecraftaccess.mixin.LoomScreenAccessor;
import org.mcaccess.minecraftaccess.mixin.MerchantScreenAccessor;
import org.mcaccess.minecraftaccess.mixin.SingleItemRecipeAccessor;
import org.mcaccess.minecraftaccess.mixin.StonecutterScreenAccessor;

import java.util.List;
import java.util.Optional;

public class SlotItem {
    public SlotItem upSlotItem = null;
    public SlotItem rightSlotItem = null;
    public SlotItem downSlotItem = null;
    public SlotItem leftSlotItem = null;

    public int x;
    public int y;

    public Slot slot = null;

    private int recipeOrTradeIndex;

    private int row;
    private int column;

    private String text = null;

    public SlotItem(Slot slot) {
        this.slot = slot;
        this.x = slot.x + 9;
        this.y = slot.y + 9;
    }

    public SlotItem(int x, int y, int recipeOrTradeIndex) {
        this.x = x;
        this.y = y;
        this.recipeOrTradeIndex = recipeOrTradeIndex;
    }

    public SlotItem(int x, int y, int row, int column) {
        this.x = x;
        this.y = y;
        this.row = row;
        this.column = column;
    }

    public SlotItem(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public SlotItem(int x, int y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public String getNarratableText() {
        if (Minecraft.getInstance().screen instanceof LoomScreen loomScreen) {
            List<Holder<BannerPattern>> list = loomScreen.getMenu().getSelectablePatterns();
            if (list.isEmpty()) return "";

            int p = row + ((LoomScreenAccessor) loomScreen).getStartRow();
            int q = p * 4 + column;

            BannerPattern pattern = list.get(q).value();

            ItemStack dyeItemStack = ((LoomScreenAccessor) loomScreen).getDyeStack();
            DyeItem dyeItem = (DyeItem) dyeItemStack.getItem();
            DyeColor color = dyeItem.getDyeColor();

            // banner pattern trans key = banner pattern name + color name
            String transKey = pattern.translationKey() + "." + color.name().toLowerCase();
            return I18n.get(transKey);
        }

        if (Minecraft.getInstance().screen instanceof StonecutterScreen stonecutterScreen) {
            List<SelectableRecipe.SingleInputEntry<StonecutterRecipe>> list = stonecutterScreen.getMenu().getVisibleRecipes().entries();
            if (list.isEmpty()) return "";

            int scrollOffset = ((StonecutterScreenAccessor) stonecutterScreen).getStartIndex();
            Optional<RecipeHolder<StonecutterRecipe>> recipe = list.get(recipeOrTradeIndex + scrollOffset).recipe().recipe();
            if (recipe.isEmpty()) return "";
            StonecutterRecipe recipe1 = recipe.get().value();
            ItemStack item = ((SingleItemRecipeAccessor) recipe1).getResult();
            List<Component> toolTip = Screen.getTooltipFromItem(Minecraft.getInstance(), item);
            StringBuilder toolTipString = new StringBuilder();
            for (Component text : toolTip) {
                toolTipString.append(text.getString()).append("\n");
            }

            return "%s %s".formatted(item.getCount(), toolTipString);
        }

        if (Minecraft.getInstance().screen instanceof MerchantScreen merchantScreen) {
            MerchantOffers tradeOfferList = merchantScreen.getMenu().getOffers();
            if (tradeOfferList.isEmpty()) return I18n.get("minecraft_access.inventory_controls.Unknown");
            MerchantOffer tradeOffer = tradeOfferList.get(recipeOrTradeIndex + ((MerchantScreenAccessor) merchantScreen).getScrollOff());

            ItemStack firstBuyItem = tradeOffer.getBaseCostA();
            Optional<ItemCost> secondBuyItem = tradeOffer.getItemCostB();
            ItemStack sellItem = tradeOffer.getResult();

            // base price - discount
            int price = firstBuyItem.getCount() + tradeOffer.getSpecialPriceDiff();
            String firstBuyItemString = price + " " + firstBuyItem.getHoverName().getString();
            String secondBuyItemString = "";
            if (secondBuyItem.isPresent()) {
                ItemStack item = secondBuyItem.get().itemStack();
                secondBuyItemString = item.getCount() + " " + item.getHoverName().getString();
            }
            String sellItemString = sellItem.getCount() + " " + sellItem.getHoverName().getString();

            String tradeText;
            if (secondBuyItem.isEmpty())
                tradeText = I18n.get("minecraft_access.inventory_controls.trade_text_format", firstBuyItemString, sellItemString);
            else
                tradeText = I18n.get("minecraft_access.inventory_controls.trade_text_format_with_second_item", firstBuyItemString, secondBuyItemString, sellItemString);

            return tradeText;
        }

        if (text != null) {
            return text;
        }

        return "";
    }
}
