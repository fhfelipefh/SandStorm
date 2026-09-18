package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelData;

import java.util.Optional;

public class SpaceshipLandingManager {

    private static BlockPos cachedCabinSpawnPos = new BlockPos(0, 65, 0);

    public static void initialize() {
        ServerLevelEvents.LOAD.register(SpaceshipLandingManager::onLevelLoad);
    }

    public static void onLevelLoad(MinecraftServer server, ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        server.getGameRules().set(GameRules.RESPAWN_RADIUS, 0, server);
        SandStormMod.LOGGER.info("SandStorm: Enforced spawn radius = 0");

        SpaceshipSavedData data = level.getDataStorage().computeIfAbsent(SpaceshipSavedData.TYPE);
        if (!data.isPlaced()) {
            SandStormMod.LOGGER.info("SandStorm: Placing crashed spaceship at (0, 0)...");
            placeSpaceshipCrashSite(server, level, data);
            SandStormMod.LOGGER.info("SandStorm: Crashed spaceship placed at {}", cachedCabinSpawnPos);
        } else {
            cachedCabinSpawnPos = data.getCabinPos();
            SandStormMod.LOGGER.info("SandStorm: Crashed spaceship already placed at {}", cachedCabinSpawnPos);
        }
    }

    private static void placeSpaceshipCrashSite(MinecraftServer server, ServerLevel level, SpaceshipSavedData data) {
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, 0, 0);
        if (surfaceY < level.getMinY() + 10) {
            surfaceY = 64;
        }

        BlockPos originPos = new BlockPos(-4, surfaceY, -5);
        BlockPos cabinSpawn = new BlockPos(0, surfaceY + 1, 0);

        StructureTemplateManager templateManager = server.getStructureTemplateManager();
        Optional<StructureTemplate> templateOpt = templateManager.get(SandStormMod.id("spaceship_crash_site"));
        if (templateOpt.isPresent()) {
            StructureTemplate template = templateOpt.get();
            StructurePlaceSettings settings = new StructurePlaceSettings().setIgnoreEntities(false);
            template.placeInWorld(level, originPos, originPos, settings, level.getRandom(), 2);
        }

        level.setRespawnData(LevelData.RespawnData.of(Level.OVERWORLD, cabinSpawn, 0.0f, 0.0f));
        data.setCabinPos(cabinSpawn.getX(), cabinSpawn.getY(), cabinSpawn.getZ());
        data.setPlaced(true);
        level.getDataStorage().set(SpaceshipSavedData.TYPE, data);
        cachedCabinSpawnPos = cabinSpawn;
    }

    public static BlockPos getCabinSpawnPos() {
        return cachedCabinSpawnPos;
    }

    public static void setCachedCabinSpawnPos(BlockPos pos) {
        cachedCabinSpawnPos = pos;
    }
}
