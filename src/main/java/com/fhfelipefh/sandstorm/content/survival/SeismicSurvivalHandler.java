package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.SeismicTrackerComponent;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;

public class SeismicSurvivalHandler {
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

    public static void handlePlayerMovement(ServerPlayer player) {
        if (!player.onGround() || player.isSpectator() || player.isCreative()) {
            return;
        }

        BlockPos pos = player.blockPosition();
        if (TRACKER.isInsideSafeZone(pos.getX(), pos.getZ())) {
            return;
        }

        int chunkX = player.chunkPosition().x();
        int chunkZ = player.chunkPosition().z();

        double deltaDist = player.getDeltaMovement().horizontalDistanceSqr();
        double multiplier = getMovementVibrationMultiplier();
        if (player.isSprinting()) {
            TRACKER.addVibration(chunkX, chunkZ, 0.8 * multiplier);
        } else if (deltaDist > 0.001) {
            TRACKER.addVibration(chunkX, chunkZ, 0.2 * multiplier);
        }

        double vibration = TRACKER.getVibration(chunkX, chunkZ);
        if (vibration >= 60.0 && vibration < 100.0) {
            if (player.tickCount % 60 == 0) {
                player.sendSystemMessage(Component.translatable("warning.sandstorm.worm_rumble"), true);
                player.level().playSound(null, pos, SandStormSoundEvents.SANDWORM_RUMBLE, SoundSource.HOSTILE, 1.0f, 0.8f);
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
