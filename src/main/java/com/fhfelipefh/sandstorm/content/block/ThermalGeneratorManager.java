package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThermalGeneratorManager {
    private static final Map<ResourceKey<Level>, Map<BlockPos, Integer>> GENERATOR_MAP = new ConcurrentHashMap<>();
    public static final int TICKS_PER_BUCKET = 4000;
    public static final int MAX_BURN_TICKS = 16000;
    public static final double EFFECTIVE_RADIUS = 32.0;
    public static final long TRANSFER_RATE_PER_TICK = 60L;

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 != 0) {
                return;
            }
            for (ServerLevel level : server.getAllLevels()) {
                tickLevel(level);
            }
        });
    }

    public static void registerGenerator(ResourceKey<Level> dim, BlockPos pos, int initialBurnTime) {
        GENERATOR_MAP.computeIfAbsent(dim, k -> new ConcurrentHashMap<>()).put(pos.immutable(), Math.max(0, initialBurnTime));
    }

    public static void unregisterGenerator(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, Integer> map = GENERATOR_MAP.get(dim);
        if (map != null) {
            map.remove(pos);
        }
    }

    public static int addFuel(ResourceKey<Level> dim, BlockPos pos, int fuelTicks) {
        Map<BlockPos, Integer> map = GENERATOR_MAP.computeIfAbsent(dim, k -> new ConcurrentHashMap<>());
        int current = map.getOrDefault(pos, 0);
        int updated = Math.min(MAX_BURN_TICKS, current + fuelTicks);
        map.put(pos.immutable(), updated);
        return updated;
    }

    public static Map<BlockPos, Integer> getGenerators(ResourceKey<Level> dim) {
        return GENERATOR_MAP.getOrDefault(dim, Map.of());
    }

    public static int getBurnTime(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, Integer> map = GENERATOR_MAP.get(dim);
        if (map == null) {
            return 0;
        }
        return map.getOrDefault(pos, 0);
    }

    public static void clear() {
        GENERATOR_MAP.clear();
    }

    public static long getWptChargeAt(Level level, BlockPos pos) {
        Map<BlockPos, Integer> map = GENERATOR_MAP.get(level.dimension());
        if (map == null || map.isEmpty()) {
            return 0L;
        }
        double radiusSq = EFFECTIVE_RADIUS * EFFECTIVE_RADIUS;
        for (Map.Entry<BlockPos, Integer> entry : map.entrySet()) {
            if (entry.getValue() > 0 && pos.distSqr(entry.getKey()) <= radiusSq) {
                return TRANSFER_RATE_PER_TICK;
            }
        }
        return 0L;
    }

    public static void tickLevel(ServerLevel level) {
        Map<BlockPos, Integer> map = GENERATOR_MAP.get(level.dimension());
        if (map == null || map.isEmpty()) {
            return;
        }

        double radiusSq = EFFECTIVE_RADIUS * EFFECTIVE_RADIUS;

        for (Map.Entry<BlockPos, Integer> entry : map.entrySet()) {
            BlockPos pos = entry.getKey();
            int burnTime = entry.getValue();

            if (!level.isLoaded(pos)) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof ThermalGeneratorBlock)) {
                map.remove(pos);
                continue;
            }

            if (burnTime > 0) {
                int newBurnTime = Math.max(0, burnTime - 20);
                map.put(pos, newBurnTime);

                boolean shouldBeLit = newBurnTime > 0;
                if (state.getValue(ThermalGeneratorBlock.LIT) != shouldBeLit) {
                    level.setBlock(pos, state.setValue(ThermalGeneratorBlock.LIT, shouldBeLit), 3);
                }

                if (!level.players().isEmpty()) {
                    AABB searchBox = new AABB(
                            pos.getX() - EFFECTIVE_RADIUS, pos.getY() - EFFECTIVE_RADIUS, pos.getZ() - EFFECTIVE_RADIUS,
                            pos.getX() + EFFECTIVE_RADIUS + 1, pos.getY() + EFFECTIVE_RADIUS + 1, pos.getZ() + EFFECTIVE_RADIUS + 1
                    );
                    for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, searchBox)) {
                        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= radiusSq) {
                            if (SuitSurvivalHandler.countEquippedSuitPieces(player) > 0) {
                                SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player);
                                long totalTransfer = TRANSFER_RATE_PER_TICK * 20;
                                long received = suit.getEnergyStorage().receiveEnergy(totalTransfer);
                                boolean batteryNearlyFull = suit.getEnergyStorage().getStoredEnergy() >= suit.getEnergyStorage().getCapacity() * 0.98;
                                if (received > 0 && !batteryNearlyFull && player.tickCount % 200 == 0) {
                                    level.playSound(null, player.blockPosition(), SandStormSoundEvents.SUIT_SOLAR_CHARGE, SoundSource.PLAYERS, 0.08f, 0.9f);
                                }
                            }
                        }
                    }
                }
            } else if (state.getValue(ThermalGeneratorBlock.LIT)) {
                level.setBlock(pos, state.setValue(ThermalGeneratorBlock.LIT, false), 3);
            }
        }
    }
}
