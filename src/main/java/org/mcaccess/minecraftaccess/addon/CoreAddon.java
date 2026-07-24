package org.mcaccess.minecraftaccess.addon;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.Balm;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.FluidTags;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.ModConfig;
import org.mcaccess.minecraftaccess.addon.accessmenu.Biome;
import org.mcaccess.minecraftaccess.addon.accessmenu.FluidDetector;
import org.mcaccess.minecraftaccess.addon.accessmenu.LightLevel;
import org.mcaccess.minecraftaccess.addon.accessmenu.NarrateTarget;
import org.mcaccess.minecraftaccess.addon.accessmenu.OpenConfig;
import org.mcaccess.minecraftaccess.addon.accessmenu.RefreshScreenReader;
import org.mcaccess.minecraftaccess.addon.accessmenu.TargetPosition;
import org.mcaccess.minecraftaccess.addon.accessmenu.Time;
import org.mcaccess.minecraftaccess.addon.accessmenu.Weather;
import org.mcaccess.minecraftaccess.addon.accessmenu.XP;
import org.mcaccess.minecraftaccess.addon.statuses.Air;
import org.mcaccess.minecraftaccess.addon.statuses.Armour;
import org.mcaccess.minecraftaccess.addon.statuses.Durability;
import org.mcaccess.minecraftaccess.addon.statuses.Frost;
import org.mcaccess.minecraftaccess.addon.statuses.GameMode;
import org.mcaccess.minecraftaccess.addon.statuses.Health;
import org.mcaccess.minecraftaccess.addon.statuses.Hunger;
import org.mcaccess.minecraftaccess.addon.worldnarrators.Jade;
import org.mcaccess.minecraftaccess.addon.worldnarrators.MinecraftAccess;
import org.mcaccess.minecraftaccess.api.AddonRegistry;
import org.mcaccess.minecraftaccess.api.MinecraftAccessAddon;

@MinecraftAccessAddon.NeoForge
public class CoreAddon implements MinecraftAccessAddon {
    public static final GameMode GAME_MODE_STAT = new GameMode();

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
                    assert Minecraft.getInstance().player != null;
                    return Minecraft.getInstance().player.getMainHandItem();
                },
                () -> ModConfig.getInstance().durabilityWarnings.enableHeldItems
        ));
        registry.register("durability/offhand", new Durability(
                () -> {
                    assert Minecraft.getInstance().player != null;
                    return Minecraft.getInstance().player.getOffhandItem();
                },
                () -> ModConfig.getInstance().durabilityWarnings.enableHeldItems
        ));
        registry.register("durability/head", new Durability(
                () -> {
                    assert Minecraft.getInstance().player != null;
                    return Minecraft.getInstance().player.getInventory().getItem(36);
                },
                () -> ModConfig.getInstance().durabilityWarnings.enableWornArmor
        ));
        registry.register("durability/chest", new Durability(
                () -> {
                    assert Minecraft.getInstance().player != null;
                    return Minecraft.getInstance().player.getInventory().getItem(37);
                },
                () -> ModConfig.getInstance().durabilityWarnings.enableWornArmor
        ));
        registry.register("durability/legs", new Durability(
                () -> {
                    assert Minecraft.getInstance().player != null;
                    return Minecraft.getInstance().player.getInventory().getItem(38);
                },
                () -> ModConfig.getInstance().durabilityWarnings.enableWornArmor
        ));
        registry.register("durability/feet", new Durability(
                () -> {
                    assert Minecraft.getInstance().player != null;
                    return Minecraft.getInstance().player.getInventory().getItem(39);
                },
                () -> ModConfig.getInstance().durabilityWarnings.enableWornArmor
        ));

        registry.register("narrate_target", new NarrateTarget())
                .withDefaultKeybind(InputConstants.KEY_B);
        registry.register("target_position", new TargetPosition());
        registry.register("light_level", new LightLevel());
        registry.register("find_water", new FluidDetector(FluidTags.WATER));
        registry.register("find_lava", new FluidDetector(FluidTags.LAVA));
        registry.register("biome", new Biome());
        registry.register("time", new Time());
        registry.register("xp", new XP());
        registry.register("refresh_screen_reader", new RefreshScreenReader());
        registry.register("config", new OpenConfig());
        registry.register("weather", new Weather());

        registry.register("minecraft_access", new MinecraftAccess());
        if (Balm.platform().isModLoaded("jade")) {
            registry.register("jade", new Jade());
        }
    }
}
