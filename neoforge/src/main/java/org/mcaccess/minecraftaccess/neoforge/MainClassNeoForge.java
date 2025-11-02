package org.mcaccess.minecraftaccess.neoforge;

import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.MinecraftAccessAddon;

@Slf4j
@Mod(value = MainClass.MOD_ID, dist = Dist.CLIENT)
public class MainClassNeoForge {
    public MainClassNeoForge(IEventBus modEventBus, ModContainer container) {
        modEventBus.register(this);
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> AutoConfig.getConfigScreen(Config.class, parent).get());
    }

    @SubscribeEvent
    private void onClientSetup(final FMLClientSetupEvent event) {
        List<MainClass.Addon> addons = new ArrayList<>();
        for (ModContainer mod : ModList.get().getSortedMods()) {
            mod.getModInfo()
                    .getOwningFile()
                    .getFile()
                    .getScanResult()
                    .getAnnotatedBy(MinecraftAccessAddon.NeoForge.class, ElementType.TYPE)
                    .forEach(annotation -> {
                        try {
                            MinecraftAccessAddon addon = (MinecraftAccessAddon) Class.forName(annotation.memberName()).getConstructor().newInstance();
                            addons.add(new MainClass.Addon(mod.getModId(), addon));
                        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException
                                 | NoSuchMethodException e) {
                            log.error("Unable to initialise addon for {}", mod.getModId(), e);
                        }
                    });
        }
        MainClass.init(addons);
    }
}
