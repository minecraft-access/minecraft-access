package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ItemStack.class, priority = 0)
public class ItemStackMixin {
    @Inject(at = @At("RETURN"), method = "getTooltipLines")
    private void getTooltipLinesMixin(TooltipContext context, Player player, TooltipFlag type, CallbackInfoReturnable<List<Component>> info) {
        if (Minecraft.getInstance().level == null) return;
        List<Component> list = info.getReturnValue();
        ItemStack itemStack = (ItemStack) ((Object) this);
        if (!itemStack.isDamageableItem()) return;

        int totalDurability = itemStack.getMaxDamage();
        int remainingDurability = totalDurability - itemStack.getDamageValue();
        list.add(1, Component.nullToEmpty((I18n.get("item.durability", remainingDurability, totalDurability).formatted(ChatFormatting.GREEN))));
    }
}
