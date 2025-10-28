package org.mcaccess.minecraftaccess.features.structure;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.data.Main;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.Blocks;

import org.mcaccess.minecraftaccess.MainClass;

import java.util.List;

@Slf4j
public class StructureDetector {
    private int tickCounter = 0;
    private int hasReported = 1;

    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Level world = mc.level;
        if (player == null || world == null) return;
        tickCounter ++;
        if (tickCounter >= 150) {
        find(world, player);
        }
    }

    private void find(Level world, LocalPlayer player) {
        int radius = 50;
        BlockPos playerPos = player.blockPosition();
        List<Entity> entities = world.getEntities(player, player.getBoundingBox().inflate(50D));

        int villagers = 0;
        int cats = 0;

        for (Entity e : entities) {
            if (e instanceof Villager) villagers ++;
            if (e instanceof Cat) cats ++;
        }




        if (hasReported == 1 && villagers >= 2 && cats >= 1) {
            MainClass.narrate(I18n.get("minecraft_access.structuredetec.near"), false);
            hasReported = 2;
        }

        if (villagers == 0 && cats == 0 && hasReported == 2) {
            hasReported = 1;

        }
    }

}
