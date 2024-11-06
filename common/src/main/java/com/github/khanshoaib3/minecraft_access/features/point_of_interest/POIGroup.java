package com.github.khanshoaib3.minecraft_access.features.point_of_interest;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

import com.github.khanshoaib3.minecraft_access.utils.WorldUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class POIGroup {
    private String name;
    private SoundEvent sound;
    private float soundPitch;
    private List<Entity> entities = new ArrayList<>();
    private TreeMap<BlockPos, BlockState> blocks;
    private Function<Entity, Boolean> entityFilter;
    private BlockFilter blockFilter;

    public POIGroup(String name, SoundEvent sound, float soundPitch, Function<Entity, Boolean> entityFilter, BlockFilter blockFilter) {
        this.name = name;
        this.sound = sound;
        this.soundPitch = soundPitch;
        this.entityFilter = entityFilter;
        this.blockFilter = blockFilter;
    }

    public String getName() {
        return name;
    }

    public SoundEvent getSound() {
        return sound;
    }

    public float getSoundPitch() {
        return soundPitch;
    }

    public void filterEntities(List<Entity> scannedEntities) {
        entities.clear();
        for (Entity e : scannedEntities) {
            if (entityFilter.apply(e)) {
                this.entities.add(e);
            }
        }
    }

    public void filterBlocks(List<BlockPos> blockPositions) {
        blocks.clear();
        for (BlockPos p : blockPositions) {
            BlockState b = WorldUtils.getClientWorld().getBlockState(p);
            if (blockFilter.apply(b, p)) {
                blocks.put(p, b);
            }
        }
    }

    public TreeMap<Double, Entity> getEntities() {
        TreeMap<Double, Entity> distanceEntity = new TreeMap<Double, Entity>();

        for (Entity e : entities) {
            double distance = MinecraftClient.getInstance().player.distanceTo(e);
            distanceEntity.put(distance, e);
        }

        return distanceEntity;
    }

    public TreeMap<BlockPos, BlockState> getBlocks() {
        return blocks;
    }

    public boolean isBlockInGroup(BlockState block, BlockPos pos) {
        return blockFilter.apply(block, pos);
    }

    public boolean isEntityInGroup(Entity entity) {
        return entityFilter.apply(entity);
    }
}
