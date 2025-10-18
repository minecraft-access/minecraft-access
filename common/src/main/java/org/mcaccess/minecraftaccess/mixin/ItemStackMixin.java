package org.mcaccess.minecraftaccess.mixin;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 0)
abstract class ItemStackMixin {
    @Inject(at = @At("RETURN"), method = "getTooltipLines")
    private void getTooltipLinesMixin(CallbackInfoReturnable<List<Component>> info) {
        if (Minecraft.getInstance().level == null) return;
        List<Component> list = info.getReturnValue();
        ItemStack itemStack = (ItemStack) ((Object) this);
        if (!itemStack.isDamageableItem()) return;

        int totalDurability = itemStack.getMaxDamage();
        int remainingDurability = totalDurability - itemStack.getDamageValue();
        list.add(1, Component.nullToEmpty(I18n.get("item.durability", remainingDurability, totalDurability).formatted(ChatFormatting.GREEN)));
    }
}
