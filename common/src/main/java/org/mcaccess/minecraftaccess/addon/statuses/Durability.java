package org.mcaccess.minecraftaccess.addon.statuses;

import java.util.function.Supplier;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.Status;

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
        return I18n.get(
                "minecraft_access.player_status.durability",
                item.getItemName().getString(),
                item.getMaxDamage() - item.getDamageValue(),
                item.getMaxDamage()
        );
    }

    @Override
    public boolean show() {
        return false;
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
