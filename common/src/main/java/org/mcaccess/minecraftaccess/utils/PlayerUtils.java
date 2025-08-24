package org.mcaccess.minecraftaccess.utils;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.features.point_of_interest.BlockPos3d;

/**
 * This class provides delegate calls to {@link LocalPlayer}.
 * The main reason for this class is that {@link LocalPlayer} cannot be mocked by Mockito. <p>
 * ({@link LocalPlayer} constructor requires -> {@link ClientLevel} constructor -> {@link Level} static init block -> {@link Registries},
 * somehow the {@link Registries} cannot finish its static assignments in class loading.
 * We can replace Mockito with more powerful PowerMock to resolve this problem, but PowerMock is sticking on Junit 4,
 * we can't go back to Junit 4 from 5 since some of the mechanisms currently used for unit testing have no alternatives in 4.
 * Forgive me for doing this, but it's the most economical way.)
 */
public final class PlayerUtils {
    // A way to get exactly at what part of the entity the player is looking when locked on it
    public static Vec3 currentEntityLookingAtPosition = null;
    private static Minecraft client = Minecraft.getInstance();

    private PlayerUtils() {
    }

    public static void playSoundOnPlayer(Holder.Reference<SoundEvent> sound, float volume, float pitch) {
        client.player.playSound(sound.value(), volume, pitch);
    }

    public static void lookAt(Vec3 position) {
        client.player.lookAt(EntityAnchorArgument.Anchor.EYES, position);
    }

    /**
     * Let player looks at entity even the entity exposes a very small part of its body
     */
    public static void lookAt(Entity entity) {
        Vec3 playerEyePos = client.player.getEyePosition();

        // Try to look at entity's eyes or Enderman's stomach first.
        boolean targetIsEnderman = entity instanceof EnderMan;
        Vec3 firstPos = targetIsEnderman ? entity.blockPosition().getCenter() : entity.getEyePosition();
        if (isPlayerCanSee(playerEyePos, firstPos, entity)) {
            lookAt(firstPos);
            currentEntityLookingAtPosition = firstPos;
            return;
        }

        // Then start to find a possible position to target at the entity.
        // This part of code is copied from Explosion.getExposure()
        AABB box = entity.getBoundingBox();
        double stepX = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double stepY = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double stepZ = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        // Don't know how these two lengths are determined
        double initX = (1.0 - Math.floor(1.0 / stepX) * stepX) / 2.0;
        double initZ = (1.0 - Math.floor(1.0 / stepZ) * stepZ) / 2.0;
        if (stepX < 0.0 || stepY < 0.0 || stepZ < 0.0) {
            lookAt(firstPos);
            currentEntityLookingAtPosition = firstPos;
            return;
        }

        // Never look at Enderman's face
        double maxY = targetIsEnderman ? Mth.lerp(0.7, box.minY, box.maxY) / 2 : box.maxY;

        for (double i = 0.0; i <= 1.0; i += stepX) {
            for (double j = 0.0; j <= 1.0; j += stepY) {
                for (double k = 0.0; k <= 1.0; k += stepZ) {
                    double px = Mth.lerp(i, box.minX, box.maxX);
                    double py = Mth.lerp(j, box.minY, maxY);
                    double pz = Mth.lerp(k, box.minZ, box.maxZ);
                    Vec3 vec3d = new Vec3(px + initX, py, pz + initZ);
                    if (isPlayerCanSee(playerEyePos, vec3d, entity)) {
                        lookAt(vec3d);
                        currentEntityLookingAtPosition = vec3d;
                        return;
                    }
                }
            }
        }

        // Make sure to look at entity even the player can't see it.
        lookAt(firstPos);
        currentEntityLookingAtPosition = firstPos;
    }

    public static void lookAt(BlockPos position) {
        lookAt(position.getCenter());
    }

    public static void lookAt(BlockPos3d position) {
        lookAt(position.getAccuratePosition());
    }

    public static boolean isPlayerCanSee(Vec3 playerEyePos, Vec3 somewhereOnEntity, Entity entity) {
        BlockHitResult hitResult = entity.level().clip(
                new ClipContext(somewhereOnEntity, playerEyePos,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE, entity));
        return hitResult.getType() == HitResult.Type.MISS;
    }

    public static int getExperienceLevel() {
        return client.player.experienceLevel;
    }

    /**
     * @return percentage-based number
     */
    public static float getExperienceProgress() {
        return client.player.experienceProgress * 100;
    }

    public static boolean isInFluid() {
        LocalPlayer player = client.player;
        return player.isSwimming()
                || player.isUnderWater()
                || player.isInWater()
                || player.isInLava();
    }

    /**
     * The value of MinecraftClient.crosshairTarget field is ray cast result that not including the fluid blocks.
     * So use this method to get what fluid the player might be looking at.
     *
     * @return fluid block if player isn't in fluid and is looking at a fluid block,
     *     or MinecraftClient.crosshairTarget otherwise
     */
    public static HitResult crosshairTarget(double rayCastDistance) {
        BlockHitResult fluidHitResult = crosshairFluidTarget(rayCastDistance);
        if (fluidHitResult.getType() == HitResult.Type.BLOCK && !isInFluid()) {
            return fluidHitResult;
        } else {
            return client.hitResult;
        }
    }

    private static BlockHitResult crosshairFluidTarget(double rayCastDistance) {
        Entity camera = Objects.requireNonNull(client.getCameraEntity());
        HitResult hit = camera.pick(rayCastDistance, 0.0F, true);
        // Whatever the inner values are, they are not used.
        BlockHitResult missed = BlockHitResult.miss(Vec3.ZERO, Direction.UP, BlockPos.ZERO);

        if (hit.getType() != HitResult.Type.BLOCK) return missed;

        BlockPos blockPos = ((BlockHitResult) hit).getBlockPos();
        ClientLevel world = client.level;

        BlockState blockState = world.getBlockState(blockPos);
        boolean thisBlockIsFluidBlock = blockState.is(Blocks.WATER) || blockState.is(Blocks.LAVA);
        if (!thisBlockIsFluidBlock) return missed;

        FluidState fluidState = world.getFluidState(blockPos);
        if (fluidState.isEmpty()) return missed;

        return (BlockHitResult) hit;
    }

    /**
     * Players have dynamic interaction range since 1.20.6.
     *
     * @return minimum value between block range and entity range
     */
    public static double getInteractionRange() {
        LocalPlayer player = client.player;
        return Math.min(player.blockInteractionRange(), player.entityInteractionRange());
    }

    /**
     * One full ham icon = two hunger points
     * <a href="https://minecraft.wiki/w/Hunger">wiki</a>
     *
     * @return number of ham shank in HUD
     */
    public static double getHunger() {
        LocalPlayer player = client.player;
        double hungerPoints = player.getFoodData().getFoodLevel();
        return hungerPoints / 2;
    }

    /**
     * One full heart = two health points
     * <a href="https://minecraft.wiki/w/Health">wiki</a>
     *
     * @return number of heart in HUD
     */
    public static double getHearts() {
        LocalPlayer player = client.player;
        double healthPoints = player.getHealth();
        return healthPoints / 2;
    }

    /**
     * Air supply value is keeping at 300 when player's head is in the air.
     * <a href="https://minecraft.wiki/w/Damage#Drowning">wiki</a>
     *
     * @return number of bubble in HUD
     */
    public static long getAir() {
        LocalPlayer player = client.player;
        double air = player.getAirSupply();
        return Math.round((air / 30) * 10) / 10;
    }

    public static void narrateCurrentPlayerEffects() {
        Collection<MobEffectInstance> effects = client.player.getActiveEffects();
        if (effects.isEmpty()) {
            MainClass.narrate(I18n.get("minecraft_access.effect_narration.no_effects"), true);
            return;
        }
        String narration = effects.stream().map(NarrationUtils::narrateEffect)
                .collect(Collectors.joining(I18n.get("minecraft_access.other.words_connection")));
        MainClass.narrate(narration, true);
    }

    public static boolean isPlayerTyping() {
        Screen currentScreen = client.screen;
        return currentScreen != null && (currentScreen.getFocused() instanceof EditBox || currentScreen instanceof KeyBindsScreen);
    }

    public static boolean isCreative() {
        if (client.gameMode == null) return false;
        GameType currentGameMode = client.gameMode.getPlayerMode();
        return currentGameMode == GameType.CREATIVE;
    }

    public static boolean isSpectator() {
        if (client.gameMode == null) return false;
        GameType currentGameMode = client.gameMode.getPlayerMode();
        return currentGameMode == GameType.SPECTATOR;
    }

    public static boolean isAdventure() {
        if (client.gameMode == null) return false;
        GameType currentGameMode = client.gameMode.getPlayerMode();
        return currentGameMode == GameType.ADVENTURE;
    }

    public static boolean isSurvival() {
        if (client.gameMode == null) return false;
        GameType currentGameMode = client.gameMode.getPlayerMode();
        return currentGameMode == GameType.SURVIVAL;
    }

    public static boolean isHardCore() {
        if (client.level == null) return false;
        LevelData levelData = client.level.getLevelData();
        return levelData.isHardcore();
    }

    public static boolean isPlayerSpectating() {
        return !client.getCameraEntity().is(client.player);
    }
}
