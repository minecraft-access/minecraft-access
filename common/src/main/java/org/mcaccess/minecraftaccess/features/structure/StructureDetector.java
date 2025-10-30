package org.mcaccess.minecraftaccess.features.structure;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.mcaccess.minecraftaccess.MainClass;

@Slf4j
public class StructureDetector {
    private int tickCounter = 0;

    private int villagers = 0;
    private int cats = 0;
    private int golems = 0;
    private boolean isPlayerInside = false;

    public void tick() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;
        tickCounter++;
        if (tickCounter >= 150) {
            reset();
            find(mc.level, mc.player);
            tickCounter = 0;
        }
    }

    private void find(Level world, LocalPlayer player) {
        List<Entity> entities = world.getEntities(player, player.getBoundingBox().inflate(50D));

        for (Entity e : entities) {
            if (e instanceof Villager) villagers++;
            if (e instanceof Cat) cats++;
            if (e instanceof IronGolem) golems++;
        }

        if (!isPlayerInside && villagers >= 3 && cats >= 1) {
            MainClass.narrate(I18n.get("minecraft_access.structuredetect.near"), false);
            isPlayerInside = true;
        }

        if (isPlayerInside && villagers == 0) {
            MainClass.narrate(I18n.get("minecraft_access.structuredetect.out"), false);
            isPlayerInside = false;
            reset();
            return;
        }
    }

    private void reset() {
        cats = 0;
        villagers = 0;
    }

}
