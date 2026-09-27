package com.fhfelipefh.sandstorm.content.entity.cyborg;

import com.fhfelipefh.sandstorm.content.block.CyborgDockingStationBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CyborgSwarmManager {
    private static final CyborgSwarmManager INSTANCE = new CyborgSwarmManager();

    private final Map<Long, UUID> reservedBlocks = new ConcurrentHashMap<>();
    private final Set<BlockPos> registeredDocks = Collections.synchronizedSet(new HashSet<>());
    private volatile int globalTacticalOrder = 0;

    private CyborgSwarmManager() {
    }

    public static CyborgSwarmManager getInstance() {
        return INSTANCE;
    }

    public int getGlobalTacticalOrder() {
        return this.globalTacticalOrder;
    }

    public void setGlobalTacticalOrder(int order) {
        this.globalTacticalOrder = order;
    }

    public boolean tryReserveBlock(UUID cyborgId, BlockPos pos) {
        if (cyborgId == null || pos == null) {
            return false;
        }
        long key = pos.asLong();
        UUID existing = this.reservedBlocks.putIfAbsent(key, cyborgId);
        return existing == null || existing.equals(cyborgId);
    }

    public void releaseBlock(UUID cyborgId, BlockPos pos) {
        if (cyborgId == null || pos == null) {
            return;
        }
        long key = pos.asLong();
        UUID current = this.reservedBlocks.get(key);
        if (cyborgId.equals(current)) {
            this.reservedBlocks.remove(key);
        }
    }

    public void releaseAll(UUID cyborgId) {
        if (cyborgId == null) {
            return;
        }
        this.reservedBlocks.entrySet().removeIf(entry -> cyborgId.equals(entry.getValue()));
    }

    public void releaseAll() {
        this.reservedBlocks.clear();
    }

    public boolean isBlockReserved(BlockPos pos, UUID excludeCyborgId) {
        if (pos == null) {
            return false;
        }
        UUID holder = this.reservedBlocks.get(pos.asLong());
        if (holder == null) {
            return false;
        }
        return excludeCyborgId == null || !excludeCyborgId.equals(holder);
    }

    public boolean isBlockReserved(BlockPos pos) {
        return isBlockReserved(pos, null);
    }

    public void registerDock(BlockPos pos) {
        if (pos != null) {
            this.registeredDocks.add(pos.immutable());
        }
    }

    public void registerDock(ResourceKey<Level> dimension, BlockPos pos) {
        registerDock(pos);
    }

    public void unregisterDock(BlockPos pos) {
        if (pos != null) {
            this.registeredDocks.remove(pos);
        }
    }

    public void unregisterDock(ResourceKey<Level> dimension, BlockPos pos) {
        unregisterDock(pos);
    }

    public BlockPos findNearestAvailableDock(ResourceKey<Level> dimension, BlockPos fromPos, double maxDistance) {
        if (fromPos == null || this.registeredDocks.isEmpty()) {
            return null;
        }

        BlockPos bestPos = null;
        double bestDistSq = maxDistance * maxDistance;

        synchronized (this.registeredDocks) {
            for (BlockPos dockPos : this.registeredDocks) {
                double distSq = fromPos.distSqr(dockPos);
                if (distSq < bestDistSq) {
                    bestDistSq = distSq;
                    bestPos = dockPos;
                }
            }
        }

        return bestPos;
    }

    public BlockPos findNearestAvailableDock(Level level, BlockPos fromPos, double maxDistance) {
        if (level == null || fromPos == null || this.registeredDocks.isEmpty()) {
            return null;
        }

        BlockPos bestPos = null;
        double bestDistSq = maxDistance * maxDistance;

        synchronized (this.registeredDocks) {
            for (BlockPos dockPos : this.registeredDocks) {
                if (!level.isLoaded(dockPos)) {
                    continue;
                }
                BlockState state = level.getBlockState(dockPos);
                if (state.getBlock() instanceof CyborgDockingStationBlock) {
                    boolean occupied = state.getValue(CyborgDockingStationBlock.OCCUPIED);
                    if (!occupied) {
                        double distSq = fromPos.distSqr(dockPos);
                        if (distSq < bestDistSq) {
                            bestDistSq = distSq;
                            bestPos = dockPos;
                        }
                    }
                }
            }
        }

        return bestPos;
    }

    public void broadcastEmergency(ServerLevel level, CyborgEntity distressedCyborg, DamageSource source) {
        if (level == null || distressedCyborg == null) {
            return;
        }

        AABB alertBox = distressedCyborg.getBoundingBox().inflate(48.0);
        List<CyborgEntity> nearbyCyborgs = level.getEntitiesOfClass(CyborgEntity.class, alertBox);

        distressedCyborg.setVisorState(2);

        LivingEntity attacker = source != null && source.getEntity() instanceof LivingEntity living ? living : null;

        for (CyborgEntity ally : nearbyCyborgs) {
            if (ally == distressedCyborg) {
                continue;
            }

            ally.setVisorState(2);

            if (attacker != null && ally.getTarget() == null && !attacker.getUUID().equals(ally.getOwnerUUID())) {
                ally.setTarget(attacker);
            }
        }
    }

    public int getReservedBlockCount() {
        return this.reservedBlocks.size();
    }

    public int getRegisteredDockCount() {
        return this.registeredDocks.size();
    }

    public void clear() {
        this.reservedBlocks.clear();
        this.registeredDocks.clear();
    }
}
