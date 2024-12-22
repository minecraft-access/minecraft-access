package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ItemStack.class, priority = 0)
public class DurabilityMixin {
    @Inject(at = @At("RETURN"), method = "getTooltip")
    private void getTooltipMixin(TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> info) {
        if (MinecraftClient.getInstance().world == null) return;
        List<Text> list = info.getReturnValue();
        ItemStack itemStack = (ItemStack) ((Object) this);
        if (!itemStack.isDamageable()) return;

        int totalDurability = itemStack.getMaxDamage();
        int remainingDurability = totalDurability - itemStack.getDamage();
        list.add(1, Text.of((I18n.translate("item.durability", remainingDurability, totalDurability).formatted(Formatting.GREEN))));
    }
}
