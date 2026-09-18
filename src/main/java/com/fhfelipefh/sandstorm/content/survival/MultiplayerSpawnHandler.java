package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.world.SpaceshipLandingManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;

public class MultiplayerSpawnHandler {

    public static void initialize() {
        ServerLevelEvents.LOAD.register(MultiplayerSpawnHandler::onLevelLoad);
    }

    public static void onLevelLoad(MinecraftServer server, ServerLevel level) {
        if (level.dimension() == Level.OVERWORLD) {
            server.getGameRules().set(GameRules.RESPAWN_RADIUS, 0, server);
        }
    }

    public static void relocatePlayerToCrashCabin(ServerPlayer player) {
        BlockPos cabinPos = SpaceshipLandingManager.getCabinSpawnPos();
        player.teleportTo(cabinPos.getX() + 0.5, cabinPos.getY(), cabinPos.getZ() + 0.5);
    }
}
