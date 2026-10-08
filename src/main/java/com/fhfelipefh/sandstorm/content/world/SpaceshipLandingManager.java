package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.block.NaniteFabricatorBlock;
import com.fhfelipefh.sandstorm.content.block.Printer3DBlock;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FurnaceBlock;
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
        if (level.dimension() != Level.OVERWORLD || !SandStormWorldHelper.isSandStormWorld(server)) {
            return;
        }

        server.getGameRules().set(GameRules.RESPAWN_RADIUS, 0, server);
        server.getGameRules().set(GameRules.SPAWN_PHANTOMS, false, server);
        server.getGameRules().set(GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS, false, server);
        server.getGameRules().set(GameRules.SPAWN_MOBS, false, server);
        server.getGameRules().set(GameRules.SPAWN_MONSTERS, false, server);
        server.getGameRules().set(GameRules.SPAWN_PATROLS, false, server);
        server.getGameRules().set(GameRules.SPAWN_WANDERING_TRADERS, false, server);
        server.getGameRules().set(GameRules.SPAWN_WARDENS, false, server);
        server.getGameRules().set(GameRules.WATER_SOURCE_CONVERSION, false, server);
        server.getGameRules().set(GameRules.BLOCK_DROPS, true, server);
        SpaceshipSavedData data = level.getDataStorage().computeIfAbsent(SpaceshipSavedData.TYPE);
        if (data.isPlaced() && data.getCabinPos().getY() > level.getMinY() + 10) {
            cachedCabinSpawnPos = data.getCabinPos();
        }
    }

    public static void onServerStarted(MinecraftServer server) {
        if (!SandStormWorldHelper.isSandStormWorld(server)) {
            return;
        }
        ServerLevel overworld = server.overworld();
        ensureSpaceshipPlaced(server, overworld);
    }

    public static void ensureSpaceshipPlaced(MinecraftServer server, ServerLevel level) {
        if (!SandStormWorldHelper.isSandStormWorld(server)) {
            return;
        }
        server.getGameRules().set(GameRules.RESPAWN_RADIUS, 0, server);
        server.getGameRules().set(GameRules.SPAWN_PHANTOMS, false, server);
        server.getGameRules().set(GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS, false, server);
        server.getGameRules().set(GameRules.SPAWN_MOBS, false, server);
        server.getGameRules().set(GameRules.SPAWN_MONSTERS, false, server);
        server.getGameRules().set(GameRules.SPAWN_PATROLS, false, server);
        server.getGameRules().set(GameRules.SPAWN_WANDERING_TRADERS, false, server);
        server.getGameRules().set(GameRules.SPAWN_WARDENS, false, server);
        server.getGameRules().set(GameRules.WATER_SOURCE_CONVERSION, false, server);
        server.getGameRules().set(GameRules.BLOCK_DROPS, true, server);
        SpaceshipSavedData data = level.getDataStorage().computeIfAbsent(SpaceshipSavedData.TYPE);

        if (!data.isPlaced() || data.getCabinPos().getY() <= level.getMinY() + 10) {
            placeSpaceshipCrashSite(server, level, data);
        } else {
            cachedCabinSpawnPos = data.getCabinPos();
            if (data.getDesignVersion() < 3) {
                buildExpandedSpaceship(level, cachedCabinSpawnPos);
                data.setDesignVersion(3);
            }
            if (!SpawnSafety.isSafePosition(level, cachedCabinSpawnPos)) {
                carveCabinInterior(level, cachedCabinSpawnPos);
            }
            ensureCabinWorkstations(level, cachedCabinSpawnPos);
            level.setRespawnData(LevelData.RespawnData.of(Level.OVERWORLD, cachedCabinSpawnPos, 0.0f, 0.0f));
        }
    }

    public static int findGroundSurfaceY(ServerLevel level, int x, int z) {
        int h = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        if (h <= level.getMinY()) {
            h = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        }
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
        for (int cx = -3; cx <= 3; cx++) {
            for (int cz = -3; cz <= 3; cz++) {
                level.getChunk(cx, cz);
            }
        }

        int centerGroundY = findGroundSurfaceY(level, 0, 4);
        int maxSurface = centerGroundY;
        for (int x = -6; x <= 6; x += 2) {
            for (int z = -4; z <= 11; z += 2) {
                int groundY = findGroundSurfaceY(level, x, z);
                if (groundY > level.getMinY() + 10) {
                    maxSurface = Math.max(maxSurface, groundY);
                }
            }
        }

        int surfaceY = Math.max(centerGroundY, maxSurface - 1);
        if (surfaceY < level.getMinY() + 10) {
            surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, 0, 4);
        }

        BlockPos cabinBase = new BlockPos(0, surfaceY, 4);
        while (surfaceY < level.getMaxY() - 10 && (level.getBlockState(cabinBase).is(Blocks.STONE) || level.getBlockState(cabinBase).is(Blocks.DEEPSLATE))) {
            surfaceY++;
            cabinBase = new BlockPos(0, surfaceY, 4);
        }

        BlockPos originPos = new BlockPos(-6, surfaceY, -4);
        BlockPos cabinSpawn = new BlockPos(0, surfaceY + 1, 4);

        int minFoundationY = Math.max(surfaceY - 12, level.getMinY());
        for (int x = -7; x <= 7; x++) {
            for (int z = -5; z <= 12; z++) {
                BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(x, surfaceY - 1, z);
                while (p.getY() >= minFoundationY && (level.getBlockState(p).isAir() || !level.getBlockState(p).isSolid())) {
                    level.setBlock(p, Blocks.SANDSTONE.defaultBlockState(), 2);
                    p.move(Direction.DOWN);
                }
            }
        }

        for (int x = -3; x <= 3; x++) {
            for (int z = -5; z >= -12; z--) {
                for (int dy = 1; dy <= 5; dy++) {
                    level.setBlock(new BlockPos(x, surfaceY + dy, z), Blocks.AIR.defaultBlockState(), 3);
                }
                BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(x, surfaceY, z);
                while (p.getY() >= minFoundationY && (level.getBlockState(p).isAir() || !level.getBlockState(p).isSolid())) {
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
        buildExpandedSpaceship(level, cabinSpawn);
        data.setDesignVersion(3);
        level.getDataStorage().set(SpaceshipSavedData.TYPE, data);
        cachedCabinSpawnPos = cabinSpawn;
        SandStormMod.LOGGER.info("SandStorm: Crashed spaceship placed safely with 4x4 blast door at {}", cachedCabinSpawnPos);
    }

    private static void buildExpandedSpaceship(ServerLevel level, BlockPos cabinSpawn) {
        clearPreviousShip(level, cabinSpawn);
        BlockState hull = Blocks.POLISHED_DEEPSLATE.defaultBlockState();
        BlockState darkHull = Blocks.POLISHED_BASALT.defaultBlockState();
        BlockState metal = Blocks.IRON_BLOCK.defaultBlockState();
        BlockState accent = Blocks.PRISMARINE.defaultBlockState();
        BlockState glass = Blocks.TINTED_GLASS.defaultBlockState();
        BlockState light = Blocks.SEA_LANTERN.defaultBlockState();

        for (int z = -30; z <= 30; z++) {
            int halfWidth = fuselageHalfWidth(z);
            for (int x = -halfWidth; x <= halfWidth; x++) {
                for (int y = 0; y <= 10; y++) {
                    BlockPos pos = cabinSpawn.offset(x, y, z);
                    if (isOriginalCabin(pos, cabinSpawn)) {
                        continue;
                    }
                    boolean shell = Math.abs(x) == halfWidth || y == 0 || y == 10;
                    level.setBlock(pos, shell ? hull : Blocks.AIR.defaultBlockState(), 3);
                }
            }
            for (int x = -halfWidth + 1; x < halfWidth; x++) {
                placeIfOutsideOriginalCabin(level, cabinSpawn.offset(x, 1, z), metal, cabinSpawn);
                if (z % 5 == 0) {
                    placeIfOutsideOriginalCabin(level, cabinSpawn.offset(x, 9, z), darkHull, cabinSpawn);
                }
            }
        }

        for (int z = -20; z <= 18; z++) {
            int span = wingSpan(z);
            for (int x = 10; x <= span; x++) {
                int wingY = 2 + Math.max(0, (z - 3) / 8);
                placeIfOutsideOriginalCabin(level, cabinSpawn.offset(x, wingY, z), darkHull, cabinSpawn);
                placeIfOutsideOriginalCabin(level, cabinSpawn.offset(-x, wingY, z), darkHull, cabinSpawn);
                if (x == 10 || x == span) {
                    placeIfOutsideOriginalCabin(level, cabinSpawn.offset(x, wingY + 1, z), accent, cabinSpawn);
                    placeIfOutsideOriginalCabin(level, cabinSpawn.offset(-x, wingY + 1, z), accent, cabinSpawn);
                }
            }
        }

        for (int z = 12; z <= 27; z++) {
            int finHeight = Math.max(2, 14 - (z - 12) / 2);
            for (int y = 10; y <= finHeight; y++) {
                placeIfOutsideOriginalCabin(level, cabinSpawn.offset(0, y, z), darkHull, cabinSpawn);
                placeIfOutsideOriginalCabin(level, cabinSpawn.offset(-1, y, z), hull, cabinSpawn);
                placeIfOutsideOriginalCabin(level, cabinSpawn.offset(1, y, z), hull, cabinSpawn);
            }
        }

        for (int z = -20; z <= -12; z++) {
            int halfWidth = fuselageHalfWidth(z);
            for (int x = -halfWidth + 1; x < halfWidth; x++) {
                if (Math.abs(x) <= halfWidth - 2) {
                    placeIfOutsideOriginalCabin(level, cabinSpawn.offset(x, 6, z), glass, cabinSpawn);
                }
            }
        }

        for (int z = -18; z <= 22; z += 8) {
            int halfWidth = fuselageHalfWidth(z);
            placeIfOutsideOriginalCabin(level, cabinSpawn.offset(-halfWidth, 4, z), accent, cabinSpawn);
            placeIfOutsideOriginalCabin(level, cabinSpawn.offset(halfWidth, 4, z), accent, cabinSpawn);
            placeIfOutsideOriginalCabin(level, cabinSpawn.offset(0, 9, z), light, cabinSpawn);
        }

        for (int z = 25; z <= 34; z++) {
            for (int x : new int[]{-7, -4, 4, 7}) {
                placeIfOutsideOriginalCabin(level, cabinSpawn.offset(x, 3, z), darkHull, cabinSpawn);
                placeIfOutsideOriginalCabin(level, cabinSpawn.offset(x, 4, z), Blocks.CRYING_OBSIDIAN.defaultBlockState(), cabinSpawn);
                placeIfOutsideOriginalCabin(level, cabinSpawn.offset(x, 5, z), accent, cabinSpawn);
            }
        }

        for (int z = -16; z <= -4; z++) {
            for (int x = -2; x <= 2; x++) {
                level.setBlock(cabinSpawn.offset(x, 0, z), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(cabinSpawn.offset(x, 1, z), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(cabinSpawn.offset(x, 2, z), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(cabinSpawn.offset(x, -1, z), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
            }
        }
        for (int z = -28; z <= -17; z++) {
            for (int x = -2; x <= 2; x++) {
                level.setBlock(cabinSpawn.offset(x, 0, z), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(cabinSpawn.offset(x, 1, z), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(cabinSpawn.offset(x, 2, z), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(cabinSpawn.offset(x, -1, z), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3);
            }
        }
        for (int z = -26; z <= -16; z++) {
            placeIfOutsideOriginalCabin(level, cabinSpawn.offset(-3, 1, z), accent, cabinSpawn);
            placeIfOutsideOriginalCabin(level, cabinSpawn.offset(3, 1, z), accent, cabinSpawn);
        }
    }

    private static int fuselageHalfWidth(int z) {
        int distance = Math.abs(z + 1);
        if (distance >= 27) {
            return 2;
        }
        if (distance >= 20) {
            return 4;
        }
        if (distance >= 12) {
            return 6;
        }
        return 8;
    }

    private static int wingSpan(int z) {
        if (z < -10) {
            return 10;
        }
        if (z < 6) {
            return 10 + (z + 10);
        }
        return Math.max(10, 26 - (z - 6));
    }

    private static void clearPreviousShip(ServerLevel level, BlockPos cabinSpawn) {
        for (int x = -28; x <= 28; x++) {
            for (int y = 0; y <= 18; y++) {
                for (int z = -34; z <= 38; z++) {
                    BlockPos pos = cabinSpawn.offset(x, y, z);
                    if (!isOriginalCabin(pos, cabinSpawn) && isPreviousShipBlock(level.getBlockState(pos))) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static boolean isPreviousShipBlock(BlockState state) {
        return state.is(Blocks.POLISHED_DEEPSLATE) || state.is(Blocks.POLISHED_BASALT)
                || state.is(Blocks.IRON_BLOCK) || state.is(Blocks.TINTED_GLASS)
                || state.is(Blocks.CRYING_OBSIDIAN) || state.is(Blocks.PRISMARINE)
                || state.is(Blocks.SEA_LANTERN);
    }

    private static boolean isOriginalCabin(BlockPos pos, BlockPos cabinSpawn) {
        return pos.getX() >= cabinSpawn.getX() - 7 && pos.getX() <= cabinSpawn.getX() + 7
                && pos.getZ() >= cabinSpawn.getZ() - 8 && pos.getZ() <= cabinSpawn.getZ() + 12
                && pos.getY() <= cabinSpawn.getY() + 6;
    }

    private static void placeIfOutsideOriginalCabin(ServerLevel level, BlockPos pos, BlockState state,
                                                     BlockPos cabinSpawn) {
        if (!isOriginalCabin(pos, cabinSpawn)) {
            level.setBlock(pos, state, 3);
        }
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

        level.setBlock(cabinSpawn.below(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3);
        ensureCabinWorkstations(level, cabinSpawn);
    }

    public static void ensureCabinWorkstations(ServerLevel level, BlockPos cabinSpawn) {
        level.setBlock(cabinSpawn.offset(-2, -1, 3), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(-2, 0, 3), SandStormBlocks.SANDSTONE_WORKBENCH.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(-2, 1, 3), Blocks.AIR.defaultBlockState(), 3);

        level.setBlock(cabinSpawn.offset(-2, -1, 2), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(-2, 0, 2), SandStormBlocks.PRINTER_3D.defaultBlockState().setValue(Printer3DBlock.FACING, Direction.EAST), 3);
        level.setBlock(cabinSpawn.offset(-2, 1, 2), Blocks.AIR.defaultBlockState(), 3);

        level.setBlock(cabinSpawn.offset(-2, -1, 1), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(-2, 0, 1), SandStormBlocks.NANITE_FABRICATOR.defaultBlockState().setValue(NaniteFabricatorBlock.FACING, Direction.EAST), 3);
        level.setBlock(cabinSpawn.offset(-2, 1, 1), Blocks.AIR.defaultBlockState(), 3);

        level.setBlock(cabinSpawn.offset(2, -1, 2), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(2, 0, 2), SandStormBlocks.DESALINATION_FILTER.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(2, 1, 2), Blocks.AIR.defaultBlockState(), 3);

        level.setBlock(cabinSpawn.offset(2, -1, 1), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        level.setBlock(cabinSpawn.offset(2, 0, 1), SandStormBlocks.SANDSTONE_FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.WEST), 3);
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
                for (int dy = 7; dy <= 25; dy++) {
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

    public static boolean isNearSpaceship(BlockPos pos) {
        int dx = pos.getX() - cachedCabinSpawnPos.getX();
        int dz = pos.getZ() - cachedCabinSpawnPos.getZ();
        return dx * dx + dz * dz <= 192 * 192;
    }

    public static BlockPos getCabinSpawnPos() {
        return cachedCabinSpawnPos;
    }

    public static void setCachedCabinSpawnPos(BlockPos pos) {
        cachedCabinSpawnPos = pos;
    }
}
