package org.mcaccess.minecraftaccess.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

public class StructureIndicator {
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        if (client.player == null) return;
        if (client.screen != null) return;
        LocalPlayer player = client.player;
        Level world = client.level;


    }

}
