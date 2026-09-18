package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.survival.SpawnSafety;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelData;

import java.util.Optional;

public class SpaceshipLandingManager {

    private static BlockPos cachedCabinSpawnPos = new BlockPos(0, 75, 4);

    public static void initialize() {
        ServerLevelEvents.LOAD.register(SpaceshipLandingManager::onLevelLoad);
        ServerLifecycleEvents.SERVER_STARTED.register(SpaceshipLandingManager::onServerStarted);
    }

    public static void onLevelLoad(MinecraftServer server, ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        server.getGameRules().set(GameRules.RESPAWN_RADIUS, 0, server);
        server.getGameRules().set(GameRules.SPAWN_PHANTOMS, false, server);
        server.getGameRules().set(GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS, false, server);
        SpaceshipSavedData data = level.getDataStorage().computeIfAbsent(SpaceshipSavedData.TYPE);
        if (data.isPlaced() && data.getCabinPos().getY() >= 60) {
            cachedCabinSpawnPos = data.getCabinPos();
        }
    }

    public static void onServerStarted(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        ensureSpaceshipPlaced(server, overworld);
    }

    public static void ensureSpaceshipPlaced(MinecraftServer server, ServerLevel level) {
        server.getGameRules().set(GameRules.RESPAWN_RADIUS, 0, server);
        server.getGameRules().set(GameRules.SPAWN_PHANTOMS, false, server);
        server.getGameRules().set(GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS, false, server);
        SpaceshipSavedData data = level.getDataStorage().computeIfAbsent(SpaceshipSavedData.TYPE);

        if (!data.isPlaced() || data.getCabinPos().getY() < 60) {
            placeSpaceshipCrashSite(server, level, data);
        } else {
            cachedCabinSpawnPos = data.getCabinPos();
            if (!SpawnSafety.isSafePosition(level, cachedCabinSpawnPos)) {
                carveCabinInterior(level, cachedCabinSpawnPos);
            }
            level.setRespawnData(LevelData.RespawnData.of(Level.OVERWORLD, cachedCabinSpawnPos, 0.0f, 0.0f));
        }
    }

    private static void placeSpaceshipCrashSite(MinecraftServer server, ServerLevel level, SpaceshipSavedData data) {
        level.getChunk(0, 0);
        level.getChunk(0, 1);
        level.getChunk(0, -1);
        level.getChunk(1, 0);
        level.getChunk(-1, 0);

        int maxSurface = 64;
        for (int x = -6; x <= 6; x += 2) {
            for (int z = -4; z <= 11; z += 2) {
                int h = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                if (h > maxSurface) {
                    maxSurface = h;
                }
            }
        }

        BlockPos testSky = new BlockPos(0, maxSurface, 0);
        while (!level.canSeeSky(testSky) && testSky.getY() < 120) {
            testSky = testSky.above();
        }
        if (testSky.getY() > maxSurface) {
            maxSurface = testSky.getY();
        }

        if (maxSurface < 64) {
            maxSurface = 64;
        }

        int surfaceY = maxSurface;
        BlockPos originPos = new BlockPos(-6, surfaceY, -4);
        BlockPos cabinSpawn = new BlockPos(0, surfaceY + 1, 4);

        for (int x = -6; x <= 6; x++) {
            for (int z = -4; z <= 11; z++) {
                for (int y = surfaceY - 1; y >= surfaceY - 4; y--) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (level.getBlockState(p).isAir()) {
                        level.setBlock(p, Blocks.SANDSTONE.defaultBlockState(), 2);
                    }
                }
            }
        }

        StructureTemplateManager templateManager = server.getStructureTemplateManager();
        Optional<StructureTemplate> templateOpt = templateManager.get(SandStormMod.id("spaceship_crash_site"));
        if (templateOpt.isPresent()) {
            StructureTemplate template = templateOpt.get();
            StructurePlaceSettings settings = new StructurePlaceSettings().setIgnoreEntities(false);
            template.placeInWorld(level, originPos, originPos, settings, level.getRandom(), 2);
        }

        carveCabinInterior(level, cabinSpawn);

        level.setRespawnData(LevelData.RespawnData.of(Level.OVERWORLD, cabinSpawn, 0.0f, 0.0f));
        data.setCabinPos(cabinSpawn.getX(), cabinSpawn.getY(), cabinSpawn.getZ());
        data.setPlaced(true);
        level.getDataStorage().set(SpaceshipSavedData.TYPE, data);
        cachedCabinSpawnPos = cabinSpawn;
        SandStormMod.LOGGER.info("SandStorm: Crashed spaceship placed safely with 4x4 blast door at {}", cachedCabinSpawnPos);
    }

    public static void carveCabinInterior(ServerLevel level, BlockPos cabinSpawn) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -3; dz <= 4; dz++) {
                for (int dy = 0; dy <= 3; dy++) {
                    level.setBlock(cabinSpawn.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        for (int dx = -2; dx <= 1; dx++) {
            for (int dz = -8; dz <= -4; dz++) {
                for (int dy = 0; dy <= 3; dy++) {
                    level.setBlock(cabinSpawn.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        for (int dx = -3; dx <= 2; dx++) {
            for (int dz = -8; dz <= -4; dz++) {
                BlockPos p = cabinSpawn.offset(dx, 0, dz);
                while (!level.canSeeSky(p) && p.getY() < 120) {
                    p = p.above();
                    level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        level.setBlock(cabinSpawn.below(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3);
    }

    public static BlockPos getCabinSpawnPos() {
        return cachedCabinSpawnPos;
    }

    public static void setCachedCabinSpawnPos(BlockPos pos) {
        cachedCabinSpawnPos = pos;
    }
}
