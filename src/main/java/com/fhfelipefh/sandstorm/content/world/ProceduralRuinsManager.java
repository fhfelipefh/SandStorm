package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.world.dungeon.AbandonedExcavationHiveDungeon;
import com.fhfelipefh.sandstorm.content.world.dungeon.AbandonedMilitaryLabDungeon;
import com.fhfelipefh.sandstorm.content.world.dungeon.AncientBunkerComplexDungeon;
import com.fhfelipefh.sandstorm.content.world.dungeon.MillennialRepositoryDungeon;
import com.fhfelipefh.sandstorm.content.world.dungeon.SubglacialCryoVaultDungeon;
import com.fhfelipefh.sandstorm.content.world.structure.AbandonedOutpostGenerator;
import com.fhfelipefh.sandstorm.content.world.structure.AncientBunkerGenerator;
import com.fhfelipefh.sandstorm.content.world.structure.FuelSiloGenerator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProceduralRuinsManager {

    public static final int OUTPOST_RARITY = 180;
    public static final int BUNKER_RARITY = 135;
    public static final int COMPLEX_DUNGEON_RARITY = 160;
    public static final int MULTIFLOOR_DUNGEON_RARITY = 220;
    public static final int MILITARY_LAB_RARITY = 320;
    public static final int EXCAVATION_HIVE_RARITY = 260;
    public static final int CRYO_VAULT_RARITY = 280;

    private record PendingRuin(ServerLevel level, BlockPos origin, int surfaceY, int ruinType, int gridWidth, int gridDepth, int floors) {}
    private static final Queue<PendingRuin> PENDING_RUINS = new ConcurrentLinkedQueue<>();

    public static void initialize() {
        ServerChunkEvents.CHUNK_LOAD.register((level, chunk, isNewChunk) -> {
            if (!isNewChunk || level.dimension() != Level.OVERWORLD) {
                return;
            }
            ChunkPos pos = chunk.getPos();
            if (pos.x() * pos.x() + pos.z() * pos.z() < 36) {
                return;
            }

            ProceduralRuinsSavedData data = ProceduralRuinsSavedData.get(level.getServer());
            long chunkKey = pos.pack();
            if (data.isChunkProcessed(chunkKey)) {
                return;
            }
            data.markChunkProcessed(chunkKey);

            BlockPos samplePos = pos.getBlockAt(8, 64, 8);
            if (!level.getBiome(samplePos).is(Biomes.DESERT) || SpaceshipLandingManager.isNearSpaceship(samplePos)) {
                return;
            }

            long seed = level.getSeed();
            long hash = hash64(chunkKey ^ seed);
            long roll = Math.abs(hash);

            int surfaceY = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, 8, 8);
            if (roll % MILITARY_LAB_RARITY == 0) {
                int dungeonY = -48 - (int) (Math.abs(hash >>> 16) % 10);
                int floors = 3 + (int) (Math.abs(hash >>> 20) % 2);
                int gridWidth = 4 + (int) (Math.abs(hash >>> 24) % 3);
                int gridDepth = 4 + (int) (Math.abs(hash >>> 28) % 3);
                BlockPos origin = new BlockPos(samplePos.getX(), dungeonY, samplePos.getZ());
                level.getServer().execute(() -> {
                    PENDING_RUINS.add(new PendingRuin(level, origin, surfaceY, 6, gridWidth, gridDepth, floors));
                });
            } else if (roll % CRYO_VAULT_RARITY == 0) {
                int dungeonY = -48 - (int) (Math.abs(hash >>> 15) % 10);
                int floors = 3 + (int) (Math.abs(hash >>> 19) % 2);
                int gridWidth = 4 + (int) (Math.abs(hash >>> 23) % 3);
                int gridDepth = 4 + (int) (Math.abs(hash >>> 27) % 3);
                BlockPos origin = new BlockPos(samplePos.getX(), dungeonY, samplePos.getZ());
                level.getServer().execute(() -> {
                    PENDING_RUINS.add(new PendingRuin(level, origin, surfaceY, 8, gridWidth, gridDepth, floors));
                });
            } else if (roll % MULTIFLOOR_DUNGEON_RARITY == 0) {
                int dungeonY = -28 - (int) (Math.abs(hash >>> 18) % 15);
                int floors = 2 + (int) (Math.abs(hash >>> 22) % 3);
                int gridWidth = 3 + (int) (Math.abs(hash >>> 26) % 3);
                int gridDepth = 3 + (int) (Math.abs(hash >>> 30) % 3);
                BlockPos origin = new BlockPos(samplePos.getX(), dungeonY, samplePos.getZ());
                level.getServer().execute(() -> {
                    PENDING_RUINS.add(new PendingRuin(level, origin, surfaceY, 5, gridWidth, gridDepth, floors));
                });
            } else if (roll % EXCAVATION_HIVE_RARITY == 0) {
                int dungeonY = -35 - (int) (Math.abs(hash >>> 17) % 15);
                int floors = 3 + (int) (Math.abs(hash >>> 21) % 2);
                int gridWidth = 4 + (int) (Math.abs(hash >>> 25) % 2);
                int gridDepth = 4 + (int) (Math.abs(hash >>> 29) % 2);
                BlockPos origin = new BlockPos(samplePos.getX(), dungeonY, samplePos.getZ());
                level.getServer().execute(() -> {
                    PENDING_RUINS.add(new PendingRuin(level, origin, surfaceY, 7, gridWidth, gridDepth, floors));
                });
            } else if (roll % COMPLEX_DUNGEON_RARITY == 0) {
                int dungeonY = -52 + (int) (Math.abs(hash >>> 20) % 33);
                int gridWidth = 3 + (int) (Math.abs(hash >>> 26) % 4);
                int gridDepth = 3 + (int) (Math.abs(hash >>> 30) % 4);
                BlockPos origin = new BlockPos(samplePos.getX(), dungeonY, samplePos.getZ());
                level.getServer().execute(() -> {
                    PENDING_RUINS.add(new PendingRuin(level, origin, surfaceY, 4, gridWidth, gridDepth, 0));
                });
            } else if (roll % OUTPOST_RARITY == 0 && surfaceY >= 72) {
                if (surfaceY > level.getMinY() + 15) {
                    BlockPos origin = new BlockPos(samplePos.getX(), surfaceY, samplePos.getZ());
                    level.getServer().execute(() -> {
                        PENDING_RUINS.add(new PendingRuin(level, origin, surfaceY, 1, 0, 0, 0));
                    });
                }
            } else if (roll % BUNKER_RARITY == 0) {
                int bunkerY = -15 + (int) (Math.abs(hash >>> 16) % 45);
                BlockPos origin = new BlockPos(samplePos.getX(), bunkerY, samplePos.getZ());
                level.getServer().execute(() -> {
                    PENDING_RUINS.add(new PendingRuin(level, origin, surfaceY, 2, 0, 0, 0));
                });
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (PENDING_RUINS.isEmpty()) {
                return;
            }
            int processed = 0;
            Iterator<PendingRuin> it = PENDING_RUINS.iterator();
            while (it.hasNext() && processed < 4) {
                PendingRuin ruin = it.next();
                if (ruin.level().isLoaded(ruin.origin())) {
                    it.remove();
                    processed++;
                    if (ruin.ruinType() == 1) {
                        AbandonedOutpostGenerator.generate(ruin.level(), ruin.origin());
                    } else if (ruin.ruinType() == 2) {
                        AncientBunkerGenerator.generate(ruin.level(), ruin.origin());
                    } else if (ruin.ruinType() == 4) {
                        AncientBunkerComplexDungeon.generate(ruin.level(), ruin.origin(), ruin.gridWidth(), ruin.gridDepth());
                    } else if (ruin.ruinType() == 5) {
                        MillennialRepositoryDungeon.generate(ruin.level(), ruin.origin(), ruin.floors(), ruin.gridWidth(), ruin.gridDepth());
                    } else if (ruin.ruinType() == 6) {
                        AbandonedMilitaryLabDungeon.generate(ruin.level(), ruin.origin(), ruin.floors(), ruin.gridWidth(), ruin.gridDepth());
                    } else if (ruin.ruinType() == 7) {
                        AbandonedExcavationHiveDungeon.generate(ruin.level(), ruin.origin(), ruin.floors(), ruin.gridWidth(), ruin.gridDepth());
                    } else if (ruin.ruinType() == 8) {
                        SubglacialCryoVaultDungeon.generate(ruin.level(), ruin.origin(), ruin.floors(), ruin.gridWidth(), ruin.gridDepth());
                    } else {
                        FuelSiloGenerator.generate(ruin.level(), ruin.origin(), ruin.surfaceY());
                    }
                }
            }
        });
    }

    public static void clear() {
        PENDING_RUINS.clear();
    }

    public static boolean generateOutpostAt(ServerLevel level, BlockPos origin) {
        return AbandonedOutpostGenerator.generate(level, origin);
    }

    public static boolean generateBunkerAt(ServerLevel level, BlockPos origin) {
        return AncientBunkerGenerator.generate(level, origin) > 0;
    }

    public static int generateComplexDungeonAt(ServerLevel level, BlockPos origin, int gridWidth, int gridDepth) {
        return AncientBunkerComplexDungeon.generate(level, origin, gridWidth, gridDepth);
    }

    public static int generateMilitaryLabAt(ServerLevel level, BlockPos origin, int floors, int gridWidth, int gridDepth) {
        return AbandonedMilitaryLabDungeon.generate(level, origin, floors, gridWidth, gridDepth);
    }

    public static int generateExcavationHiveAt(ServerLevel level, BlockPos origin, int floors, int gridWidth, int gridDepth) {
        return AbandonedExcavationHiveDungeon.generate(level, origin, floors, gridWidth, gridDepth);
    }

    public static int generateCryoVaultAt(ServerLevel level, BlockPos origin, int floors, int gridWidth, int gridDepth) {
        return SubglacialCryoVaultDungeon.generate(level, origin, floors, gridWidth, gridDepth);
    }

    public static boolean generateFuelSiloAt(ServerLevel level, BlockPos origin, int surfaceY) {
        return FuelSiloGenerator.generate(level, origin, surfaceY);
    }


    public static long hash64(long k) {
        k ^= k >>> 33;
        k *= 0xff51afd7ed558ccdL;
        k ^= k >>> 33;
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= k >>> 33;
        return k;
    }
}
