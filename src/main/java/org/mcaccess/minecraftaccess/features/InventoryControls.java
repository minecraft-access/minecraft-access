package org.mcaccess.minecraftaccess.features;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;

public class InventoryControls implements BalmClientModule {
    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls");
    }

    @Override
    public void initialize() {
        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.narrate_menu_info"))
                .withDefault(InputBinding.key(InputConstants.KEY_U))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .ignoreScreenFocus()
                .handleScreenInput(e -> {
                    GuiEventListener focused = e.getFocusedElement();
                    if (focused instanceof EditBox || focused instanceof MultiLineEditBox) {
                        return false;
                    }
                    return e.screen() instanceof MenuAccess<?> menuAccess && narrateMenuInfo(menuAccess.getMenu());
                })
                .build();
    }

    private boolean narrateMenuInfo(AbstractContainerMenu menu) {
        switch (menu) {
            case AbstractFurnaceMenu furnace -> {
                MainClass.narrate(I18n.get("minecraft_access.inventory_controls.fuel_status",
                        Math.round(furnace.getLitProgress() * 100),
                        Math.round(furnace.getBurnProgress() * 100)), true);
                return true;
            }
            case BrewingStandMenu brewingStand -> {
                int fuelPercent = brewingStand.getTotalFuel() > 0 ? (brewingStand.getFuel() * 100) / brewingStand.getTotalFuel() : 0;
                int brewingTicks = brewingStand.getBrewingTicks();
                int totalBrewingTicks = brewingStand.getTotalBrewingTicks();
                int brewPercent = (brewingTicks > 0 && totalBrewingTicks > 0)
                        ? ((totalBrewingTicks - brewingTicks) * 100) / totalBrewingTicks
                        : 0;
                MainClass.narrate(I18n.get("minecraft_access.inventory_controls.fuel_status", fuelPercent, brewPercent), true);
                return true;
            }
            case AnvilMenu anvil -> {
                int cost = anvil.getCost();
                if (cost > 0) {
                    assert Minecraft.getInstance().player != null;
                    if (cost >= 40 && !Minecraft.getInstance().player.hasInfiniteMaterials()) {
                        MainClass.narrate(I18n.get("container.repair.expensive"), true);
                        return true;
                    } else if (anvil.getSlot(2).hasItem()) {
                        MainClass.narrate(I18n.get("container.repair.cost", cost), true);
                        return true;
                    }
                }
            }
            case EnchantmentMenu enchantment -> {
                String costs = Arrays.stream(enchantment.costs)
                        .filter(cost -> cost > 0)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(", "));
                if (!costs.isEmpty()) {
                    MainClass.narrate(I18n.get("container.enchant.level.requirement", costs), true);
                    return true;
                }
            }
            default -> {
            }
        }

        return false;
    }
}
