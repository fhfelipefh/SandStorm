package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.world.structure.AbandonedOutpostGenerator;
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
    public static final int FUEL_SILO_RARITY = 150;

    private record PendingRuin(ServerLevel level, BlockPos origin, int surfaceY, boolean isOutpost) {}
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
            if (!level.getBiome(samplePos).is(Biomes.DESERT)) {
                return;
            }

            long seed = level.getSeed();
            long hash = hash64(chunkKey ^ seed);
            long roll = Math.abs(hash);

            int surfaceY = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, 8, 8);
            if (roll % OUTPOST_RARITY == 0 && surfaceY >= 72) {
                if (surfaceY > level.getMinY() + 15) {
                    BlockPos origin = new BlockPos(samplePos.getX(), surfaceY, samplePos.getZ());
                    level.getServer().execute(() -> {
                        PENDING_RUINS.add(new PendingRuin(level, origin, surfaceY, true));
                    });
                }
            } else if ((roll / OUTPOST_RARITY) % FUEL_SILO_RARITY == 0) {
                if (surfaceY > level.getMinY() + 45) {
                    int siloY = 32 + (int) (Math.abs(hash >> 16) % 10);
                    BlockPos origin = new BlockPos(samplePos.getX(), siloY, samplePos.getZ());
                    level.getServer().execute(() -> {
                        PENDING_RUINS.add(new PendingRuin(level, origin, surfaceY, false));
                    });
                }
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
                    if (ruin.isOutpost()) {
                        AbandonedOutpostGenerator.generate(ruin.level(), ruin.origin());
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
