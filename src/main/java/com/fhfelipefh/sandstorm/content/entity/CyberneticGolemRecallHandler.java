package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

public final class CyberneticGolemRecallHandler {
    private CyberneticGolemRecallHandler() {}

    public static void handleRecall(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        List<? extends CyberneticGolemEntity> golems = level.getEntities(
                SandStormEntities.CYBERNETIC_GOLEM,
                golem -> golem.isOwner(player)
        );

        if (golems.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.sandstorm.no_golems_found"), true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8f, 1.2f);
            return;
        }

        for (CyberneticGolemEntity golem : golems) {
            golem.onRemoteRecallCalled(player);
        }

        player.sendSystemMessage(Component.translatable("message.sandstorm.golem_called", golems.size()), true);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.8f, 1.6f);
    }
}
