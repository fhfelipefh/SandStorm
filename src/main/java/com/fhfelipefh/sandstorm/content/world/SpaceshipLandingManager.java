package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.survival.SpawnSafety;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
        if (data.isPlaced() && data.getCabinPos().getY() > level.getMinY() + 10) {
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

        if (!data.isPlaced() || data.getCabinPos().getY() <= level.getMinY() + 10) {
            placeSpaceshipCrashSite(server, level, data);
        } else {
            cachedCabinSpawnPos = data.getCabinPos();
            if (!SpawnSafety.isSafePosition(level, cachedCabinSpawnPos)) {
                carveCabinInterior(level, cachedCabinSpawnPos);
            }
            ensureCabinWorkstations(level, cachedCabinSpawnPos);
            level.setRespawnData(LevelData.RespawnData.of(Level.OVERWORLD, cachedCabinSpawnPos, 0.0f, 0.0f));
        }
    }

    public static int findGroundSurfaceY(ServerLevel level, int x, int z) {
        int h = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(x, Math.min(h + 2, level.getMaxY() - 1), z);
        while (p.getY() > level.getMinY()) {
            var state = level.getBlockState(p);
            if (!state.isAir() && state.isSolid()) {
                return p.getY();
            }
            p.move(Direction.DOWN);
        }
        return level.getMinY() + 64;
    }

    private static void placeSpaceshipCrashSite(MinecraftServer server, ServerLevel level, SpaceshipSavedData data) {
        level.getChunk(0, 0);
        level.getChunk(0, 1);
        level.getChunk(0, -1);
        level.getChunk(1, 0);
        level.getChunk(-1, 0);

        int minSurface = Integer.MAX_VALUE;
        for (int x = -6; x <= 6; x += 2) {
            for (int z = -4; z <= 11; z += 2) {
                int groundY = findGroundSurfaceY(level, x, z);
                if (groundY > level.getMinY() + 5) {
                    minSurface = Math.min(minSurface, groundY);
                }
            }
        }

        if (minSurface == Integer.MAX_VALUE || minSurface < level.getMinY() + 10) {
            minSurface = level.getHeight(Heightmap.Types.MOTION_BLOCKING, 0, 4) - 1;
        }

        int surfaceY = minSurface;
        BlockPos originPos = new BlockPos(-6, surfaceY, -4);
        BlockPos cabinSpawn = new BlockPos(0, surfaceY + 1, 4);

        for (int x = -7; x <= 7; x++) {
            for (int z = -5; z <= 12; z++) {
                BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(x, surfaceY - 1, z);
                while (p.getY() >= level.getMinY() && (level.getBlockState(p).isAir() || !level.getBlockState(p).isSolid())) {
                    level.setBlock(p, Blocks.SANDSTONE.defaultBlockState(), 2);
                    p.move(Direction.DOWN);
                }
            }
        }

        for (int x = -3; x <= 3; x++) {
            for (int z = -5; z >= -12; z--) {
                for (int dy = 1; dy <= 4; dy++) {
                    level.setBlock(new BlockPos(x, surfaceY + dy, z), Blocks.AIR.defaultBlockState(), 3);
                }
                BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(x, surfaceY, z);
                while (p.getY() >= level.getMinY() && (level.getBlockState(p).isAir() || !level.getBlockState(p).isSolid())) {
                    level.setBlock(p, (p.getY() == surfaceY) ? Blocks.SAND.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState(), 2);
                    p.move(Direction.DOWN);
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

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -8; dz <= 4; dz++) {
                BlockPos floorPos = cabinSpawn.offset(dx, -1, dz);
                if (level.getBlockState(floorPos).isAir()) {
                    level.setBlock(floorPos, Blocks.SMOOTH_STONE.defaultBlockState(), 3);
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
        ensureCabinWorkstations(level, cabinSpawn);
    }

    public static void ensureCabinWorkstations(ServerLevel level, BlockPos cabinSpawn) {
        level.setBlock(cabinSpawn.offset(-2, -1, 3), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(-2, 0, 3), Blocks.CRAFTING_TABLE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(-2, 1, 3), Blocks.AIR.defaultBlockState(), 3);

        level.setBlock(cabinSpawn.offset(-2, -1, 2), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(-2, 0, 2), SandStormBlocks.PRINTER_3D.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(-2, 1, 2), Blocks.AIR.defaultBlockState(), 3);

        level.setBlock(cabinSpawn.offset(-2, -1, 1), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(-2, 0, 1), SandStormBlocks.NANITE_FABRICATOR.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(-2, 1, 1), Blocks.AIR.defaultBlockState(), 3);

        level.setBlock(cabinSpawn.offset(2, -1, 2), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(2, 0, 2), SandStormBlocks.DESALINATION_FILTER.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(2, 1, 2), Blocks.AIR.defaultBlockState(), 3);

        level.setBlock(cabinSpawn.offset(2, -1, 1), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(2, 0, 1), Blocks.FURNACE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(2, 1, 1), Blocks.AIR.defaultBlockState(), 3);

        level.setBlock(cabinSpawn.offset(-2, 3, 2), Blocks.SEA_LANTERN.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(2, 3, 2), Blocks.SEA_LANTERN.defaultBlockState(), 3);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = 0; dz <= 2; dz++) {
                level.setBlock(cabinSpawn.offset(dx, 5, dz), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 3);
            }
        }

        BlockPos receiverPos = cabinSpawn.offset(0, 6, 1);
        level.setBlock(receiverPos, SandStormBlocks.WIRELESS_SOLAR_RECEIVER.defaultBlockState(), 3);
        WirelessSolarReceiverManager.registerReceiver(level.dimension(), receiverPos, 1);

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -2; dz <= 4; dz++) {
                for (int dy = 6; dy <= 25; dy++) {
                    BlockPos p = cabinSpawn.offset(dx, dy, dz);
                    if (!p.equals(receiverPos) && !level.getBlockState(p).isAir()) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -8; dz <= 12; dz++) {
                for (int dy = 1; dy <= 25; dy++) {
                    BlockPos p = cabinSpawn.offset(dx, dy, dz);
                    if (!p.equals(receiverPos) && (level.getBlockState(p).is(SandStormBlocks.WIRELESS_SOLAR_RECEIVER) || level.getBlockState(p).is(SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2))) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    public static BlockPos getCabinSpawnPos() {
        return cachedCabinSpawnPos;
    }

    public static void setCachedCabinSpawnPos(BlockPos pos) {
        cachedCabinSpawnPos = pos;
    }
}
