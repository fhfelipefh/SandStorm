package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.component.SandstormWeatherComponent;
import com.fhfelipefh.sandstorm.content.network.SandstormWeatherPayload;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class SandstormWeatherHandler {
    public static final long NEW_WORLD_GRACE_PERIOD_TICKS = 24000L;
    private static final SandstormWeatherComponent WEATHER = new SandstormWeatherComponent();
    private static long nextSandstormGameTime = NEW_WORLD_GRACE_PERIOD_TICKS;

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.overworld() != null) {
                handleServerTick(server.overworld(), server.overworld().getGameTime());
            }
        });
    }

    public static void handleServerTick(long gameTime) {
        WEATHER.tick();

        if (!WEATHER.isActive() && gameTime >= nextSandstormGameTime) {
            WEATHER.startSandstorm(4000, 0.85);
            nextSandstormGameTime = gameTime + 14000;
        }
    }

    public static void handleServerTick(ServerLevel level, long gameTime) {
        WEATHER.tick();

        if (level != null) {
            SandstormSavedData data = level.getDataStorage().computeIfAbsent(SandstormSavedData.TYPE);
            nextSandstormGameTime = data.getNextSandstormGameTime();

            if (!data.isFirstStormTriggered() && gameTime < NEW_WORLD_GRACE_PERIOD_TICKS) {
                if (data.getNextSandstormGameTime() < NEW_WORLD_GRACE_PERIOD_TICKS) {
                    data.setNextSandstormGameTime(NEW_WORLD_GRACE_PERIOD_TICKS);
                    nextSandstormGameTime = NEW_WORLD_GRACE_PERIOD_TICKS;
                }
            } else if (!WEATHER.isActive() && gameTime >= data.getNextSandstormGameTime()) {
                WEATHER.startSandstorm(4000, 0.85);
                data.setFirstStormTriggered(true);
                data.setNextSandstormGameTime(gameTime + 14000L);
                nextSandstormGameTime = data.getNextSandstormGameTime();
            }

            if (gameTime % 20 == 0) {
                SandstormWeatherPayload payload = new SandstormWeatherPayload(WEATHER.isActive(), WEATHER.getIntensity());
                for (ServerPlayer player : level.players()) {
                    ServerPlayNetworking.send(player, payload);
                }
            }

            if (WEATHER.isActive()) {
                if (gameTime % 80 == 0) {
                    playWeatherWindSound(level);
                }
                if (WEATHER.getIntensity() >= 0.35 && gameTime % 40 == 0) {
                    depositSandDrifts(level);
                }
                if (WEATHER.getIntensity() >= 0.75 && gameTime % 30 == 0) {
                    triggerIonDischarges(level);
                }
            }
        }
    }

    public static void depositSandDrifts(ServerLevel level) {
        if (level == null || level.players().isEmpty()) {
            return;
        }
        for (ServerPlayer player : level.players()) {
            if (!level.canSeeSky(player.blockPosition())) {
                continue;
            }
            int dx = level.getRandom().nextInt(25) - 12;
            int dz = level.getRandom().nextInt(25) - 12;
            BlockPos checkPos = player.blockPosition().offset(dx, 0, dz);
            BlockPos topPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, checkPos);
            if (topPos.distSqr(player.blockPosition()) < 16) {
                continue;
            }
            if (SeismicSurvivalHandler.getTracker().isInsideSafeZone(topPos.getX(), topPos.getZ())) {
                continue;
            }
            BlockState below = level.getBlockState(topPos.below());
            if (below.isSolid() && level.getBlockState(topPos).isAir()) {
                level.setBlock(topPos, Blocks.SAND.defaultBlockState(), 3);
            }
        }
    }

    public static void triggerIonDischarges(ServerLevel level) {
        if (level == null || level.players().isEmpty()) {
            return;
        }
        for (ServerPlayer player : level.players()) {
            if (!level.canSeeSky(player.blockPosition())) {
                continue;
            }
            if (level.getRandom().nextFloat() > 0.35f) {
                continue;
            }
            int dx = level.getRandom().nextInt(41) - 20;
            int dz = level.getRandom().nextInt(41) - 20;
            if (dx * dx + dz * dz < 36) {
                continue;
            }
            BlockPos checkPos = player.blockPosition().offset(dx, 0, dz);
            BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, checkPos);

            if (SeismicSurvivalHandler.getTracker().isInsideSafeZone(surfacePos.getX(), surfacePos.getZ())) {
                continue;
            }

            BlockState below = level.getBlockState(surfacePos.below());
            if (below.is(Blocks.SAND) || below.is(Blocks.SANDSTONE) || below.is(Blocks.RED_SAND)) {
                LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.NATURAL);
                if (bolt != null) {
                    bolt.setVisualOnly(false);
                    bolt.snapTo(surfacePos.getX() + 0.5, surfacePos.getY(), surfacePos.getZ() + 0.5);
                    level.addFreshEntity(bolt);

                    if (below.is(Blocks.SAND) && level.getRandom().nextFloat() < 0.35f) {
                        level.setBlock(surfacePos.below(), Blocks.GLASS.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    public static SoundEvent getWindSoundForIntensity(double intensity, float randomRoll) {
        if (randomRoll < 0.20f) {
            return SandStormSoundEvents.WEATHER_SANDSTORM_WIND_HOWL;
        }
        if (intensity < 0.35) {
            return SandStormSoundEvents.WEATHER_SANDSTORM_WIND_LIGHT;
        }
        if (intensity < 0.70) {
            return SandStormSoundEvents.WEATHER_SANDSTORM_WIND_MEDIUM;
        }
        return SandStormSoundEvents.WEATHER_SANDSTORM_WIND_HEAVY;
    }

    private static void playWeatherWindSound(ServerLevel level) {
        double intensity = WEATHER.getIntensity();
        float roll = level.getRandom().nextFloat();
        SoundEvent selectedSound = getWindSoundForIntensity(intensity, roll);
        float baseVolume = (float) Math.clamp(0.15 + intensity * 0.25, 0.10, 0.40);
        float pitch = (float) (0.9 + (level.getRandom().nextFloat() * 0.2));

        for (ServerPlayer player : level.players()) {
            boolean insideSafeZone = SeismicSurvivalHandler.getTracker().isInsideSafeZone(player.getBlockX(), player.getBlockZ());
            if (player.level().canSeeSky(player.blockPosition())) {
                float effectiveVolume = insideSafeZone ? baseVolume * 0.35f : baseVolume;
                level.playSound(null, player.blockPosition(), selectedSound, SoundSource.WEATHER, effectiveVolume, pitch);
            }
        }
    }

    public static void triggerSandstorm(int durationTicks, double intensity) {
        WEATHER.startSandstorm(durationTicks, intensity);
    }

    public static void stopSandstorm() {
        WEATHER.stopSandstorm();
    }

    public static void resetWeather() {
        WEATHER.reset();
        nextSandstormGameTime = NEW_WORLD_GRACE_PERIOD_TICKS;
    }

    public static SandstormWeatherComponent getWeather() {
        return WEATHER;
    }

    public static void setNextSandstormGameTime(long time) {
        nextSandstormGameTime = time;
    }

    public static long getNextSandstormGameTime() {
        return nextSandstormGameTime;
    }
}
