package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.SeismicTrackerComponent;
import com.fhfelipefh.sandstorm.content.block.SandstoneWorkbenchBlock;
import com.fhfelipefh.sandstorm.content.defense.AcousticDefenseTracker;
import com.fhfelipefh.sandstorm.content.defense.KineticShieldTracker;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SeismicSurvivalHandler {
    public static final TagKey<Block> SEISMIC_SAFE_BLOCKS = TagKey.create(Registries.BLOCK, SandStormMod.id("seismic_safe_blocks"));
    private static final SeismicTrackerComponent TRACKER = new SeismicTrackerComponent(0, 0, 96.0);
    private static int tickCounter = 0;

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(SeismicSurvivalHandler::handleServerTick);
    }

    public static void handleServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter % 20 == 0) {
            TRACKER.decayAll(0.5);
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            handlePlayerMovement(player);
        }
    }

    public static double getMovementVibrationMultiplier() {
        return SandstormWeatherHandler.getWeather().getVibrationDampingFactor();
    }

    public static boolean isSeismicSafeBlock(BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }
        if (state.is(SEISMIC_SAFE_BLOCKS)) {
            return true;
        }
        Block block = state.getBlock();
        return block instanceof SlabBlock
                || block instanceof StairBlock
                || block instanceof SandstoneWorkbenchBlock
                || state.is(Blocks.SANDSTONE)
                || state.is(Blocks.CUT_SANDSTONE)
                || state.is(Blocks.SMOOTH_SANDSTONE)
                || state.is(Blocks.CHISELED_SANDSTONE)
                || state.is(Blocks.RED_SANDSTONE)
                || state.is(Blocks.CUT_RED_SANDSTONE)
                || state.is(Blocks.SMOOTH_RED_SANDSTONE)
                || state.is(Blocks.CHISELED_RED_SANDSTONE)
                || state.is(Blocks.STONE)
                || state.is(Blocks.COBBLESTONE)
                || state.is(Blocks.GRANITE)
                || state.is(Blocks.DIORITE)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.DEEPSLATE);
    }

    public static void handlePlayerMovement(ServerPlayer player) {
        if (!player.onGround() || player.isSpectator() || player.isCreative()) {
            return;
        }

        int blockX = player.getBlockX();
        int blockZ = player.getBlockZ();
        if (TRACKER.isInsideSafeZone(blockX, blockZ)) {
            return;
        }

        if (KineticShieldTracker.isInsideShield(player.level().dimension(), player.blockPosition())
                || AcousticDefenseTracker.isInsideAcousticDamping(player.level().dimension(), player.blockPosition())) {
            return;
        }

        if (isSeismicSafeBlock(player.getBlockStateOn())) {
            return;
        }

        int chunkX = blockX >> 4;
        int chunkZ = blockZ >> 4;

        double deltaDist = player.getDeltaMovement().horizontalDistanceSqr();
        double multiplier = getMovementVibrationMultiplier();
        PlayerSuitSavedData suitData = PlayerSuitSavedData.get((ServerLevel) player.level());
        if (suitData.hasUpgrade(player.getUUID(), "seismic")) {
            multiplier *= 0.25;
        }
        if (player.isSprinting()) {
            TRACKER.addVibration(chunkX, chunkZ, 0.8 * multiplier);
        } else if (deltaDist > 0.001) {
            TRACKER.addVibration(chunkX, chunkZ, 0.2 * multiplier);
        }

        double vibration = TRACKER.getVibration(chunkX, chunkZ);
        if (vibration >= 60.0 && vibration < 100.0) {
            if (player.tickCount % 60 == 0) {
                player.sendSystemMessage(Component.translatable("warning.sandstorm.worm_rumble"), true);
                player.level().playSound(null, player.blockPosition(), SandStormSoundEvents.SANDWORM_RUMBLE, SoundSource.HOSTILE, 1.0f, 0.8f);
            }
        } else if (vibration >= 100.0) {
            spawnWormEncounter(player);
            TRACKER.clearChunkVibration(chunkX, chunkZ);
        }
    }

    public static void spawnWormEncounter(ServerPlayer player) {
        ServerLevel level = player.level();
        BlockPos spawnPos = player.blockPosition().offset(
                (int) (Math.cos(player.getYRot()) * 16),
                0,
                (int) (Math.sin(player.getYRot()) * 16)
        );

        SandwormEntity worm = SandStormEntities.SANDWORM.create(level, EntitySpawnReason.TRIGGERED);
        if (worm != null) {
            int roll = level.getRandom().nextInt(100);
            int size = roll < 25 ? 1 : (roll < 80 ? 2 : (roll < 95 ? 3 : 4));
            worm.setWormSize(size, true);
            worm.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
            level.addFreshEntity(worm);
            player.sendSystemMessage(Component.translatable("warning.sandstorm.worm_emerge"), true);
            level.playSound(null, spawnPos, SandStormSoundEvents.SANDWORM_EMERGE, SoundSource.HOSTILE, 1.2f, 0.9f);
        }
    }

    public static SeismicTrackerComponent getTracker() {
        return TRACKER;
    }

    public static void recordVibration(int chunkX, int chunkZ, double amount) {
        TRACKER.addVibration(chunkX, chunkZ, amount);
    }
}
