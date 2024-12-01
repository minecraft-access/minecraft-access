package com.github.khanshoaib3.minecraft_access.features.point_of_interest;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.khanshoaib3.minecraft_access.utils.WorldUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class POIGroup {
    public final String name;
    public final SoundEvent sound;
    public final float soundPitch;

    private List<Entity> entities = new ArrayList<>();
    private TreeMap<BlockPos, BlockState> blocks = new TreeMap<>();
    private Function<Entity, Boolean> entityFilter;
    private BiFunction<BlockState, BlockPos, Boolean> blockFilter;

    public POIGroup(String name, SoundEvent sound, float soundPitch, Function<Entity, Boolean> entityFilter, BiFunction<BlockState, BlockPos, Boolean> blockFilter) {
        if (entityFilter != null && blockFilter != null) {
            throw new IllegalArgumentException("POI groups can only have one filter type");
        }

        this.name = name;
        this.sound = sound;
        this.soundPitch = soundPitch;
        this.entityFilter = entityFilter;
        this.blockFilter = blockFilter;
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

    public TreeMap<Double, Vec3d> getBlocks(boolean asDistanceAndVec3d) {
        TreeMap<Double, Vec3d> results = new TreeMap<>();

        for (BlockPos pos : blocks.keySet()) {
            Vec3d vecPos = pos.toCenterPos();
            Double distance = WorldUtils.getClientPlayer().getEyePos().distanceTo(vecPos);

            results.put(distance, vecPos);
        }

        return results;
    }

    public boolean checkAndAddBlock(BlockState block, BlockPos pos) {
        boolean result = blockFilter.apply(block, pos);
        if (result) blocks.put(pos, block);
        return result;
    }

    public boolean checkAndAddEntity(Entity entity) {
        boolean result = entityFilter.apply(entity);
        if (result) entities.add(entity);
        return result;
    }

    public void clearBlocks() {
        blocks = new TreeMap<>();
    }

    public void clearEntities() {
        entities = new ArrayList<>();
    }

    public void clear() {
        blocks = new TreeMap<>();
        entities = new ArrayList<>();
    }

    public void addBlock(BlockState block, BlockPos pos) {
        blocks.put(pos, block);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public static enum GroupType {
        ENTITY,
        BLOCK
    }

    public GroupType getType() {
        if (entityFilter != null) return GroupType.ENTITY;
        if (blockFilter != null) return GroupType.BLOCK;

        return null;
    }

    public boolean isEmpty() {
        return entities.isEmpty() && blocks.isEmpty();
    }
}
