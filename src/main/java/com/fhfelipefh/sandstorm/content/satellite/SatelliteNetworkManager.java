package com.fhfelipefh.sandstorm.content.satellite;

import com.fhfelipefh.sandstorm.content.quest.PlayerQuestSavedData;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.world.SandstormSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SatelliteNetworkManager {
    public static final String SATELLITE_ACTIVE_TAG = "sandstorm.satellite_active";

    public static boolean isSatelliteActive(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            SatelliteSavedData data = serverLevel.getDataStorage().computeIfAbsent(SatelliteSavedData.TYPE);
            return data.isSatelliteActive();
        }
        return false;
    }

    public static boolean isWeatherReconActive(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            SatelliteSavedData data = serverLevel.getDataStorage().computeIfAbsent(SatelliteSavedData.TYPE);
            return data.isWeatherReconActive();
        }
        return false;
    }

    public static boolean isSolarReflectorActive(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            SatelliteSavedData data = serverLevel.getDataStorage().computeIfAbsent(SatelliteSavedData.TYPE);
            return data.isSolarReflectorActive();
        }
        return false;
    }

    public static boolean isSarGeologicalActive(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            SatelliteSavedData data = serverLevel.getDataStorage().computeIfAbsent(SatelliteSavedData.TYPE);
            return data.isSarGeologicalActive();
        }
        return false;
    }

    public static boolean isKineticLanceActive(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            SatelliteSavedData data = serverLevel.getDataStorage().computeIfAbsent(SatelliteSavedData.TYPE);
            return data.isKineticLanceActive();
        }
        return false;
    }

    public static int getActiveSatelliteCount(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            SatelliteSavedData data = serverLevel.getDataStorage().computeIfAbsent(SatelliteSavedData.TYPE);
            return data.getSatelliteCount();
        }
        return 0;
    }

    public static void registerSatelliteLaunched(ServerLevel level, Player player) {
        registerSpecificSatelliteLaunched(level, player, SatelliteType.SURVEY);
    }

    public static void registerSpecificSatelliteLaunched(ServerLevel level, Player player, SatelliteType type) {
        SatelliteSavedData data = level.getDataStorage().computeIfAbsent(SatelliteSavedData.TYPE);
        data.setSatelliteActive(true);
        data.setLaunchGameTime(level.getGameTime());
        data.incrementSatelliteCount();

        switch (type) {
            case WEATHER_RECON -> data.setWeatherReconActive(true);
            case SOLAR_REFLECTOR -> data.setSolarReflectorActive(true);
            case SAR_GEOLOGICAL -> data.setSarGeologicalActive(true);
            case KINETIC_LANCE -> data.setKineticLanceActive(true);
            case SURVEY -> {
            }
        }

        if (player != null) {
            player.addTag(SATELLITE_ACTIVE_TAG);
            if (level.getServer() != null) {
                PlayerQuestSavedData questData = PlayerQuestSavedData.get(level.getServer());
                questData.markConditionCompleted(player.getUUID(), SATELLITE_ACTIVE_TAG);
                if (player instanceof ServerPlayer sp) {
                    QuestRewardHandler.syncPlayerQuests(sp, questData);
                }
            }
            player.sendSystemMessage(Component.translatable("telemetry.sandstorm.satellite_deployed_" + type.getId()));
            level.playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.2f);
        }
    }

    public static boolean triggerKineticStrike(ServerLevel level, BlockPos targetPos, Player caller) {
        if (!isKineticLanceActive(level) || targetPos == null) {
            return false;
        }

        double tx = targetPos.getX() + 0.5;
        double ty = targetPos.getY();
        double tz = targetPos.getZ() + 0.5;

        for (int y = 320; y >= (int) ty; y -= 16) {
            level.sendParticles(ParticleTypes.SONIC_BOOM, tx, y, tz, 2, 0.5, 2.0, 0.5, 0.1);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, tx, y, tz, 8, 0.8, 1.0, 0.8, 0.2);
        }

        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, tx, ty + 0.5, tz, 1, 0.0, 0.0, 0.0, 0.0);
        level.playSound(null, targetPos, SandStormSoundEvents.MEGAZORD_SHOCKWAVE, SoundSource.WEATHER, 3.0f, 0.9f);

        AABB damageArea = new AABB(targetPos).inflate(16.0);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, damageArea, e -> e instanceof Enemy || (caller != null && !caller.equals(e) && !e.isAlliedTo(caller)));
        DamageSource damageSource = level.damageSources().fellOutOfWorld();
        for (LivingEntity target : targets) {
            target.hurtServer(level, damageSource, 100.0f);
        }

        if (caller != null) {
            caller.sendSystemMessage(Component.translatable("telemetry.sandstorm.kinetic_strike_impact", targetPos.getX(), targetPos.getY(), targetPos.getZ(), targets.size()));
        }

        return true;
    }

    public static long getTicksUntilNextSandstorm(ServerLevel level) {
        SandstormSavedData weatherData = level.getDataStorage().computeIfAbsent(SandstormSavedData.TYPE);
        long nextTime = weatherData.getNextSandstormGameTime();
        long current = level.getGameTime();
        return Math.max(0L, nextTime - current);
    }
}
