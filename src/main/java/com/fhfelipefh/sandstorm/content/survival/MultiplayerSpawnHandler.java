package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.world.SandStormWorldHelper;
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
        if (level.dimension() == Level.OVERWORLD && SandStormWorldHelper.isSandStormWorld(server)) {
            server.getGameRules().set(GameRules.RESPAWN_RADIUS, 0, server);
            server.getGameRules().set(GameRules.SPAWN_PHANTOMS, false, server);
            server.getGameRules().set(GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS, false, server);
            server.getGameRules().set(GameRules.SPAWN_MOBS, false, server);
            server.getGameRules().set(GameRules.SPAWN_MONSTERS, false, server);
            server.getGameRules().set(GameRules.SPAWN_PATROLS, false, server);
            server.getGameRules().set(GameRules.SPAWN_WANDERING_TRADERS, false, server);
            server.getGameRules().set(GameRules.SPAWN_WARDENS, false, server);
            server.getGameRules().set(GameRules.WATER_SOURCE_CONVERSION, false, server);
        }
    }

    public static void relocatePlayerToCrashCabin(ServerPlayer player) {
        if (!SandStormWorldHelper.isSandStormWorld(player.level())) {
            return;
        }
        BlockPos cabinPos = SpaceshipLandingManager.getCabinSpawnPos();
        SpawnSafety.teleportSafely(player, cabinPos);
    }
}
