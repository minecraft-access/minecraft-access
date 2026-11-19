package org.mcaccess.minecraftaccess.features;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
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
        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "inventory_controls.fuel_status"))
                .withDefault(InputBinding.key(InputConstants.KEY_U))
                .overrideCategory(KeyMappingCategories.INVENTORY_CONTROLS)
                .handleScreenInput(e -> {
                    if (e.screen() instanceof MenuAccess<?> menuAccess) {
                        AbstractContainerMenu menu = menuAccess.getMenu();
                        if (menu instanceof AbstractFurnaceMenu furnace) {
                            MainClass.narrate(I18n.get("minecraft_access.inventory_controls.fuel_status",
                                    Math.round(furnace.getLitProgress() * 100),
                                    Math.round(furnace.getBurnProgress() * 100)), true);
                            return true;
                        } else if (menu instanceof BrewingStandMenu brewingStand) {
                            int fuelPercent = brewingStand.getTotalFuel() > 0 ? (brewingStand.getFuel() * 100) / brewingStand.getTotalFuel() : 0;
                            int brewingTicks = brewingStand.getBrewingTicks();
                            int totalBrewingTicks = brewingStand.getTotalBrewingTicks();
                            int brewPercent = (brewingTicks > 0 && totalBrewingTicks > 0)
                                    ? ((totalBrewingTicks - brewingTicks) * 100) / totalBrewingTicks
                                    : 0;
                            MainClass.narrate(I18n.get("minecraft_access.inventory_controls.fuel_status", fuelPercent, brewPercent), true);
                            return true;
                        }
                    }

                    return false;
                })
                .build();
    }
}
