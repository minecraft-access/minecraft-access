package org.mcaccess.minecraftaccess.addon;

import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.FluidTags;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.addon.accessmenu.FluidDetector;
import org.mcaccess.minecraftaccess.addon.accessmenu.GetBiome;
import org.mcaccess.minecraftaccess.addon.accessmenu.GetTime;
import org.mcaccess.minecraftaccess.addon.accessmenu.GetXP;
import org.mcaccess.minecraftaccess.addon.accessmenu.LightLevel;
import org.mcaccess.minecraftaccess.addon.accessmenu.NarrateTarget;
import org.mcaccess.minecraftaccess.addon.accessmenu.OpenConfig;
import org.mcaccess.minecraftaccess.addon.accessmenu.RefreshScreenReader;
import org.mcaccess.minecraftaccess.addon.accessmenu.TargetPosition;
import org.mcaccess.minecraftaccess.addon.worldnarrators.Jade;
import org.mcaccess.minecraftaccess.addon.worldnarrators.MinecraftAccess;
import org.mcaccess.minecraftaccess.addon.statuses.Air;
import org.mcaccess.minecraftaccess.addon.statuses.Armour;
import org.mcaccess.minecraftaccess.addon.statuses.Durability;
import org.mcaccess.minecraftaccess.addon.statuses.Frost;
import org.mcaccess.minecraftaccess.addon.statuses.GameMode;
import org.mcaccess.minecraftaccess.addon.statuses.Health;
import org.mcaccess.minecraftaccess.addon.statuses.Hunger;
import org.mcaccess.minecraftaccess.api.AddonRegistry;
import org.mcaccess.minecraftaccess.api.MinecraftAccessAddon;

@MinecraftAccessAddon.NeoForge
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

        registry.register("narrate_target", new NarrateTarget());
        registry.register("target_position", new TargetPosition());
        registry.register("light_level", new LightLevel());
        registry.register("find_water", new FluidDetector(FluidTags.WATER));
        registry.register("find_lava", new FluidDetector(FluidTags.LAVA));
        registry.register("biome", new GetBiome());
        registry.register("time", new GetTime());
        registry.register("xp", new GetXP());
        registry.register("refresh_screen_reader", new RefreshScreenReader());
        registry.register("config", new OpenConfig());

        registry.register("minecraft_access", new MinecraftAccess());
        if (Platform.isModLoaded("jade")) {
            registry.register("jade", new Jade());
        }
    }
}
