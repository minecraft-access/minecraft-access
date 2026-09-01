package org.mcaccess.minecraftaccess.addon.statuses;

import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class Durability implements Status {
    private final Supplier<ItemStack> itemStack;
    private final Supplier<Boolean> enabled;

    public Durability(Supplier<ItemStack> itemStack, Supplier<Boolean> enabled) {
        this.itemStack = itemStack;
        this.enabled = enabled;
    }

    @Override
    public @NotNull String message() {
        ItemStack item = itemStack.get();
        return new Translation("minecraft_access.player_status.durability")
                .variable("item").put(item.getItemName())
                .variable("remaining").put(item.getMaxDamage() - item.getDamageValue())
                .variable("max").put(item.getMaxDamage())
                .getString();
    }

    @Override
    public @NotNull Visibility visibility() {
        return Visibility.NONE;
    }

    @Override
    public @NotNull WarningLevel warning() {
        if (!enabled.get()) {
            return WarningLevel.NONE;
        }

        ItemStack item = itemStack.get();

        if (item == null || !item.isDamageableItem() || !item.isDamaged()) {
            return WarningLevel.NONE;
        }

        int durability = item.getMaxDamage() - item.getDamageValue();

        if (item.nextDamageWillBreak()) {
            return WarningLevel.FINAL;
        }
        if (durability <= Config.getInstance().playerWarnings.durabilityWarnings.secondThreshold) {
            return WarningLevel.CRITICAL;
        }
        if (durability <= Config.getInstance().playerWarnings.durabilityWarnings.firstThreshold) {
            return WarningLevel.WARNING;
        }
        return WarningLevel.NONE;
    }
}
