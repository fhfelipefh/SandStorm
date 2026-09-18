package com.fhfelipefh.sandstorm.content.world;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NutrientTerraformingManager {

    public enum Stage {
        SAND_TO_DIRT,
        DIRT_TO_GRASS,
        GRASS_TO_FOLIAGE
    }

    public static class InoculatedTask {
        private final ResourceKey<Level> dimension;
        private final BlockPos pos;
        private Stage stage;
        private long triggerTick;

        public InoculatedTask(ResourceKey<Level> dimension, BlockPos pos, Stage stage, long triggerTick) {
            this.dimension = dimension;
            this.pos = pos.immutable();
            this.stage = stage;
            this.triggerTick = triggerTick;
        }

        public ResourceKey<Level> getDimension() {
            return dimension;
        }

        public BlockPos getPos() {
            return pos;
        }

        public Stage getStage() {
            return stage;
        }

        public void setStage(Stage stage) {
            this.stage = stage;
        }

        public long getTriggerTick() {
            return triggerTick;
        }

        public void setTriggerTick(long triggerTick) {
            this.triggerTick = triggerTick;
        }
    }

    private static final Map<BlockPos, InoculatedTask> ACTIVE_TASKS = new ConcurrentHashMap<>();

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(NutrientTerraformingManager::onServerTick);
    }

    public static void clear() {
        ACTIVE_TASKS.clear();
    }

    public static int getActiveTaskCount() {
        return ACTIVE_TASKS.size();
    }

    public static void inoculateArea(ServerLevel level, BlockPos center, int radius) {
        long currentTick = level.getServer().getTickCount();
        int radiusSq = radius * radius;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radiusSq) {
                    continue;
                }
                for (int dy = 2; dy >= -3; dy--) {
                    BlockPos p = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(p);
                    if (isSandBlock(state.getBlock())) {
                        BlockPos above = p.above();
                        if (level.getBlockState(above).isAir() || isSandBlock(level.getBlockState(above).getBlock())) {
                            long delay = 20L * (30 + level.getRandom().nextInt(40));
                            ACTIVE_TASKS.put(p.immutable(), new InoculatedTask(level.dimension(), p, Stage.SAND_TO_DIRT, currentTick + delay));
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void onServerTick(MinecraftServer server) {
        if (server.getTickCount() % 20 != 0 || ACTIVE_TASKS.isEmpty()) {
            return;
        }

        long currentTick = server.getTickCount();
        List<InoculatedTask> toProcess = new ArrayList<>();

        for (InoculatedTask task : ACTIVE_TASKS.values()) {
            if (currentTick >= task.getTriggerTick()) {
                toProcess.add(task);
                if (toProcess.size() >= 32) {
                    break;
                }
            }
        }

        for (InoculatedTask task : toProcess) {
            ServerLevel level = server.getLevel(task.getDimension());
            if (level == null || !level.isLoaded(task.getPos())) {
                continue;
            }

            processTask(level, task, currentTick);
        }
    }

    public static void processTask(ServerLevel level, InoculatedTask task, long currentTick) {
        BlockPos pos = task.getPos();
        BlockState currentState = level.getBlockState(pos);

        switch (task.getStage()) {
            case SAND_TO_DIRT -> {
                if (isSandBlock(currentState.getBlock())) {
                    level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
                    level.sendParticles(ParticleTypes.COMPOSTER, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.5, 6, 0.3, 0.1, 0.3, 0.05);
                    level.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 0.4f, 0.9f);
                    task.setStage(Stage.DIRT_TO_GRASS);
                    task.setTriggerTick(currentTick + 20L * (45 + level.getRandom().nextInt(40)));
                } else {
                    ACTIVE_TASKS.remove(pos);
                }
            }
            case DIRT_TO_GRASS -> {
                if (currentState.is(Blocks.DIRT) || currentState.is(Blocks.COARSE_DIRT) || currentState.is(Blocks.ROOTED_DIRT)) {
                    BlockPos above = pos.above();
                    if (level.getBlockState(above).isAir()) {
                        level.setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.5, 8, 0.3, 0.1, 0.3, 0.05);
                        level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.6f, 1.0f);
                        task.setStage(Stage.GRASS_TO_FOLIAGE);
                        task.setTriggerTick(currentTick + 20L * (25 + level.getRandom().nextInt(30)));
                    } else {
                        ACTIVE_TASKS.remove(pos);
                    }
                } else {
                    ACTIVE_TASKS.remove(pos);
                }
            }
            case GRASS_TO_FOLIAGE -> {
                if (currentState.is(Blocks.GRASS_BLOCK)) {
                    BlockPos above = pos.above();
                    if (level.getBlockState(above).isAir()) {
                        int roll = level.getRandom().nextInt(100);
                        BlockState foliage = (roll < 65) ? Blocks.SHORT_GRASS.defaultBlockState()
                                : (roll < 80) ? Blocks.FERN.defaultBlockState()
                                : (roll < 90) ? Blocks.DANDELION.defaultBlockState()
                                : Blocks.POPPY.defaultBlockState();
                        level.setBlock(above, foliage, 3);
                        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, above.getX() + 0.5, above.getY() + 0.3, above.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.05);
                    }
                }
                ACTIVE_TASKS.remove(pos);
            }
        }
    }

    public static boolean isSandBlock(Block block) {
        return block == Blocks.SAND
                || block == Blocks.RED_SAND
                || block == Blocks.SANDSTONE
                || block == Blocks.SMOOTH_SANDSTONE
                || block == Blocks.CUT_SANDSTONE
                || block == Blocks.RED_SANDSTONE
                || block == Blocks.SUSPICIOUS_SAND;
    }
}
