package com.fhfelipefh.sandstorm.content.block;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CryoVaultReactorManager {

    private static final Map<ResourceKey<Level>, Map<BlockPos, Boolean>> REACTORS = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Set<BlockPos>> TRACKED_PERMAFROST = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Set<BlockPos>> TRACKED_ICE = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Set<BlockPos>> TRACKED_WATER = new ConcurrentHashMap<>();

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(CryoVaultReactorManager::onServerTick);
    }

    public static void registerReactor(ResourceKey<Level> dim, BlockPos pos, boolean operational) {
        REACTORS.computeIfAbsent(dim, k -> new ConcurrentHashMap<>()).put(pos.immutable(), operational);
    }

    public static void unregisterReactor(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, Boolean> map = REACTORS.get(dim);
        if (map != null) {
            map.remove(pos);
        }
    }

    public static void setReactorOperational(ResourceKey<Level> dim, BlockPos pos, boolean operational) {
        REACTORS.computeIfAbsent(dim, k -> new ConcurrentHashMap<>()).put(pos.immutable(), operational);
    }

    public static boolean isReactorOperational(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, Boolean> map = REACTORS.get(dim);
        if (map == null) {
            return false;
        }
        return map.getOrDefault(pos, false);
    }

    public static boolean hasBrokenReactor(ResourceKey<Level> dim) {
        Map<BlockPos, Boolean> map = REACTORS.get(dim);
        if (map == null || map.isEmpty()) {
            return false;
        }
        for (Boolean op : map.values()) {
            if (!op) {
                return true;
            }
        }
        return false;
    }

    public static void registerPermafrost(ResourceKey<Level> dim, BlockPos pos) {
        TRACKED_PERMAFROST.computeIfAbsent(dim, k -> ConcurrentHashMap.newKeySet()).add(pos.immutable());
    }

    public static void unregisterPermafrost(ResourceKey<Level> dim, BlockPos pos) {
        Set<BlockPos> set = TRACKED_PERMAFROST.get(dim);
        if (set != null) {
            set.remove(pos);
        }
    }

    public static void clear() {
        REACTORS.clear();
        TRACKED_PERMAFROST.clear();
        TRACKED_ICE.clear();
        TRACKED_WATER.clear();
    }

    public static Set<BlockPos> getTrackedPermafrost(ResourceKey<Level> dim) {
        return TRACKED_PERMAFROST.getOrDefault(dim, Collections.emptySet());
    }

    public static Set<BlockPos> getTrackedIce(ResourceKey<Level> dim) {
        return TRACKED_ICE.getOrDefault(dim, Collections.emptySet());
    }

    public static Set<BlockPos> getTrackedWater(ResourceKey<Level> dim) {
        return TRACKED_WATER.getOrDefault(dim, Collections.emptySet());
    }

    public static void onServerTick(MinecraftServer server) {
        long tick = server.getTickCount();
        if (tick % 25 != 0) {
            return;
        }

        for (ServerLevel level : server.getAllLevels()) {
            ResourceKey<Level> dim = level.dimension();
            Map<BlockPos, Boolean> dimReactors = REACTORS.get(dim);
            if (dimReactors == null || dimReactors.isEmpty()) {
                continue;
            }

            boolean broken = hasBrokenReactor(dim);
            if (broken) {
                processThaw(level, dim);
            } else {
                processRefreeze(level, dim);
            }
        }
    }

    public static void processThaw(ServerLevel level, ResourceKey<Level> dim) {
        Set<BlockPos> permafrostSet = TRACKED_PERMAFROST.getOrDefault(dim, Collections.emptySet());
        int thawedPermafrost = 0;
        Iterator<BlockPos> pIt = permafrostSet.iterator();
        while (pIt.hasNext() && thawedPermafrost < 2) {
            BlockPos pos = pIt.next();
            if (!level.isLoaded(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (state.is(SandStormBlocks.BRITTLE_PERMAFROST)) {
                pIt.remove();
                level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.8f, 1.3f);
                TRACKED_ICE.computeIfAbsent(dim, k -> ConcurrentHashMap.newKeySet()).add(pos);
                thawedPermafrost++;
            } else if (!state.is(Blocks.ICE) && !state.is(Blocks.WATER)) {
                pIt.remove();
            }
        }

        Set<BlockPos> iceSet = TRACKED_ICE.getOrDefault(dim, Collections.emptySet());
        int thawedIce = 0;
        Iterator<BlockPos> iceIt = iceSet.iterator();
        while (iceIt.hasNext() && thawedIce < 2) {
            BlockPos pos = iceIt.next();
            if (!level.isLoaded(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.ICE)) {
                iceIt.remove();
                level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
                level.playSound(null, pos, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER, SoundSource.BLOCKS, 0.8f, 1.0f);
                TRACKED_WATER.computeIfAbsent(dim, k -> ConcurrentHashMap.newKeySet()).add(pos);
                thawedIce++;
            } else if (!state.is(SandStormBlocks.BRITTLE_PERMAFROST) && !state.is(Blocks.WATER)) {
                iceIt.remove();
            }
        }
    }

    public static void processRefreeze(ServerLevel level, ResourceKey<Level> dim) {
        Set<BlockPos> waterSet = TRACKED_WATER.getOrDefault(dim, Collections.emptySet());
        int refrozenWater = 0;
        Iterator<BlockPos> wIt = waterSet.iterator();
        while (wIt.hasNext() && refrozenWater < 2) {
            BlockPos pos = wIt.next();
            if (!level.isLoaded(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.WATER)) {
                wIt.remove();
                level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                level.playSound(null, pos, SoundEvents.POWDER_SNOW_PLACE, SoundSource.BLOCKS, 0.8f, 1.2f);
                TRACKED_ICE.computeIfAbsent(dim, k -> ConcurrentHashMap.newKeySet()).add(pos);
                refrozenWater++;
            } else if (!state.is(Blocks.ICE) && !state.is(SandStormBlocks.BRITTLE_PERMAFROST)) {
                wIt.remove();
            }
        }

        Set<BlockPos> iceSet = TRACKED_ICE.getOrDefault(dim, Collections.emptySet());
        int refrozenIce = 0;
        Iterator<BlockPos> iceIt = iceSet.iterator();
        while (iceIt.hasNext() && refrozenIce < 2) {
            BlockPos pos = iceIt.next();
            if (!level.isLoaded(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.ICE)) {
                iceIt.remove();
                level.setBlock(pos, SandStormBlocks.BRITTLE_PERMAFROST.defaultBlockState(), 3);
                level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.8f, 1.4f);
                TRACKED_PERMAFROST.computeIfAbsent(dim, k -> ConcurrentHashMap.newKeySet()).add(pos);
                refrozenIce++;
            } else if (!state.is(Blocks.WATER) && !state.is(SandStormBlocks.BRITTLE_PERMAFROST)) {
                iceIt.remove();
            }
        }
    }
}
