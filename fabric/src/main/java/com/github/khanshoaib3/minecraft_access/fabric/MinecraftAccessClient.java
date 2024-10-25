package com.github.khanshoaib3.minecraft_access.fabric;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

public class MinecraftAccessClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
                MainClass.init();

        // Since 1.19.4 (that's when the Fabric add the condition code below):
        // It seems that Fabric loads classes in mod jars on-demand.
        // If the KeyBindingsHandler class is not invoked here, the static block of this class will not be executed.
        // After the game is initialized, invoking (what we did in the constructor of KeyBindingsHandler class) on
        // dev.architectury.registry.client.keymappings.KeyMappingRegistry.register
        // would throw an exception and terminate the game:
        // net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl.registerKeyBinding(): GameOptions has already been initialized
        //
        // According to <a href="https://fabricmc.net/wiki/tutorial:keybinds">Fabric doc</a>,
        // custom key bindings registration should be registered in ClientModInitializer endpoint.
        //
        // And this will resolve issue: https://github.com/khanshoaib3/minecraft-access/issues/171
        for (KeyBinding kb : KeyBindingsHandler.getInstance().getKeys()) {
            KeyBindingHelper.registerKeyBinding(kb);
        }
    }
}
