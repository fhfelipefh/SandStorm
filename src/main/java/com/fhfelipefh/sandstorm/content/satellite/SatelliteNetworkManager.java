package com.fhfelipefh.sandstorm.content.satellite;

import com.fhfelipefh.sandstorm.content.quest.PlayerQuestSavedData;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import com.fhfelipefh.sandstorm.content.world.SandstormSavedData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SatelliteNetworkManager {
    public static final String SATELLITE_ACTIVE_TAG = "sandstorm.satellite_active";

    public static boolean isSatelliteActive(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            SatelliteSavedData data = serverLevel.getDataStorage().computeIfAbsent(SatelliteSavedData.TYPE);
            return data.isSatelliteActive();
        }
        return false;
    }

    public static void registerSatelliteLaunched(ServerLevel level, Player player) {
        SatelliteSavedData data = level.getDataStorage().computeIfAbsent(SatelliteSavedData.TYPE);
        data.setSatelliteActive(true);
        data.setLaunchGameTime(level.getGameTime());

        player.addTag(SATELLITE_ACTIVE_TAG);
        if (level.getServer() != null) {
            PlayerQuestSavedData questData = PlayerQuestSavedData.get(level.getServer());
            questData.markConditionCompleted(player.getUUID(), SATELLITE_ACTIVE_TAG);
            if (player instanceof ServerPlayer sp) {
                QuestRewardHandler.syncPlayerQuests(sp, questData);
            }
        }

        player.sendSystemMessage(Component.translatable("telemetry.sandstorm.satellite_deployed"));
        level.playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.2f);
    }

    public static long getTicksUntilNextSandstorm(ServerLevel level) {
        SandstormSavedData weatherData = level.getDataStorage().computeIfAbsent(SandstormSavedData.TYPE);
        long nextTime = weatherData.getNextSandstormGameTime();
        long current = level.getGameTime();
        return Math.max(0L, nextTime - current);
    }
}
