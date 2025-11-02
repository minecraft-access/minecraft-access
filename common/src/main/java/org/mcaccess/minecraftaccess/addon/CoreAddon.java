package org.mcaccess.minecraftaccess.addon;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.addon.statuses.Air;
import org.mcaccess.minecraftaccess.addon.statuses.Armour;
import org.mcaccess.minecraftaccess.addon.statuses.Durability;
import org.mcaccess.minecraftaccess.addon.statuses.Frost;
import org.mcaccess.minecraftaccess.addon.statuses.GameMode;
import org.mcaccess.minecraftaccess.addon.statuses.Health;
import org.mcaccess.minecraftaccess.addon.statuses.Hunger;
import org.mcaccess.minecraftaccess.api.AddonRegistry;
import org.mcaccess.minecraftaccess.api.MinecraftAccessAddon;

public class CoreAddon implements MinecraftAccessAddon {
    public static final GameMode GAME_MODE_STAT = new GameMode();
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public void init(@NotNull AddonRegistry registry) {
        registry.register("health", new Health());
        registry.register("hunger", new Hunger());
        registry.register("armour", new Armour());
        registry.register("air", new Air());
        registry.register("frost", new Frost());
        registry.register("game_mode", GAME_MODE_STAT);
        registry.register("durability/main_hand", new Durability(
                () -> {
                    assert client.player != null;
                    return client.player.getMainHandItem();
                },
                () -> Config.getInstance().playerWarnings.durabilityWarnings.enableHeldItems
        ));
        registry.register("durability/offhand", new Durability(
                () -> {
                    assert client.player != null;
                    return client.player.getOffhandItem();
                },
                () -> Config.getInstance().playerWarnings.durabilityWarnings.enableHeldItems
        ));
        registry.register("durability/head", new Durability(
                () -> {
                    assert client.player != null;
                    return client.player.getInventory().getItem(36);
                },
                () -> Config.getInstance().playerWarnings.durabilityWarnings.enableWornArmor
        ));
        registry.register("durability/chest", new Durability(
                () -> {
                    assert client.player != null;
                    return client.player.getInventory().getItem(37);
                },
                () -> Config.getInstance().playerWarnings.durabilityWarnings.enableWornArmor
        ));
        registry.register("durability/legs", new Durability(
                () -> {
                    assert client.player != null;
                    return client.player.getInventory().getItem(38);
                },
                () -> Config.getInstance().playerWarnings.durabilityWarnings.enableWornArmor
        ));
        registry.register("durability/feet", new Durability(
                () -> {
                    assert client.player != null;
                    return client.player.getInventory().getItem(39);
                },
                () -> Config.getInstance().playerWarnings.durabilityWarnings.enableWornArmor
        ));
    }
}
