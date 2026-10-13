package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.IrrigationHoseBlock;
import com.fhfelipefh.sandstorm.content.block.IrrigationSprinklerBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class IrrigationSprinklerBlockEntity extends BlockEntity {
    private boolean active = false;
    private int pressureRadius = 0;
    private int tickCounter = 0;

    public IrrigationSprinklerBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.IRRIGATION_SPRINKLER_BE, pos, state);
    }

    public boolean isActive() {
        return active;
    }

    public int getPressureRadius() {
        return pressureRadius;
    }

    public void setActive(boolean active, int radius) {
        boolean changed = (this.active != active) || (this.pressureRadius != radius);
        this.active = active;
        this.pressureRadius = radius;
        if (changed) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.hasProperty(IrrigationSprinklerBlock.ACTIVE) && current.getValue(IrrigationSprinklerBlock.ACTIVE) != active) {
                    level.setBlock(worldPosition, current.setValue(IrrigationSprinklerBlock.ACTIVE, active), 3);
                } else {
                    level.sendBlockUpdated(worldPosition, current, current, 3);
                }
            }
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, IrrigationSprinklerBlockEntity be) {
        be.tickCounter++;
        if (be.tickCounter % 20 != 0 || !be.active || be.pressureRadius <= 0) {
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            be.performTerraforming(serverLevel, pos);
        }
    }

    private void performTerraforming(ServerLevel serverLevel, BlockPos pos) {
        RandomSource random = serverLevel.getRandom();
        int r = this.pressureRadius;
        int attempts = Math.max(2, Math.min(6, r / 2));

        for (int i = 0; i < attempts; i++) {
            int dx = random.nextInt(r * 2 + 1) - r;
            int dz = random.nextInt(r * 2 + 1) - r;
            if (dx * dx + dz * dz > r * r || (dx == 0 && dz == 0)) {
                continue;
            }

            int targetX = pos.getX() + dx;
            int targetZ = pos.getZ() + dz;

            BlockPos targetSurface = findGroundSurface(serverLevel, targetX, pos.getY(), targetZ);
            if (targetSurface == null) {
                continue;
            }

            if (!hasLineOfSight(serverLevel, pos, targetSurface)) {
                continue;
            }

            BlockState targetState = serverLevel.getBlockState(targetSurface);
            if (isAridBlock(targetState)) {
                serverLevel.setBlock(targetSurface, Blocks.DIRT.defaultBlockState(), 3);
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, targetSurface.getX() + 0.5, targetSurface.getY() + 1.1, targetSurface.getZ() + 0.5, 5, 0.3, 0.1, 0.3, 0.05);
                serverLevel.sendParticles(ParticleTypes.SPLASH, targetSurface.getX() + 0.5, targetSurface.getY() + 1.05, targetSurface.getZ() + 0.5, 4, 0.2, 0.1, 0.2, 0.05);
                serverLevel.playSound(null, targetSurface, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.25f, 1.0f);
                break;
            } else if (isDirtBlock(targetState)) {
                serverLevel.setBlock(targetSurface, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, targetSurface.getX() + 0.5, targetSurface.getY() + 1.1, targetSurface.getZ() + 0.5, 6, 0.3, 0.1, 0.3, 0.05);
                serverLevel.sendParticles(ParticleTypes.SPLASH, targetSurface.getX() + 0.5, targetSurface.getY() + 1.05, targetSurface.getZ() + 0.5, 4, 0.2, 0.1, 0.2, 0.05);
                serverLevel.playSound(null, targetSurface, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.3f, 1.0f);
                break;
            } else if (isGrassBlock(targetState)) {
                BlockPos above = targetSurface.above();
                if (serverLevel.getBlockState(above).isAir() && random.nextInt(100) < 18) {
                    BlockState foliage = getRandomFlora(random);
                    serverLevel.setBlock(above, foliage, 3);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, above.getX() + 0.5, above.getY() + 0.2, above.getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0.02);
                    serverLevel.playSound(null, above, SoundEvents.CHERRY_SAPLING_PLACE, SoundSource.BLOCKS, 0.3f, 1.0f);
                    break;
                }
            }
        }
    }

    private static BlockPos findGroundSurface(ServerLevel level, int x, int startY, int z) {
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int dy = 3; dy >= -4; dy--) {
            p.set(x, startY + dy, z);
            if (!level.isLoaded(p)) {
                continue;
            }
            BlockState state = level.getBlockState(p);
            if (!state.isAir() && !state.canBeReplaced() && state.isSolid()) {
                BlockState aboveState = level.getBlockState(p.above());
                if (aboveState.isAir() || aboveState.canBeReplaced()) {
                    return p.immutable();
                }
            }
        }
        return null;
    }

    public static boolean hasLineOfSight(Level level, BlockPos sprinklerPos, BlockPos targetPos) {
        double x0 = sprinklerPos.getX() + 0.5;
        double y0 = sprinklerPos.getY() + 0.85;
        double z0 = sprinklerPos.getZ() + 0.5;

        double x1 = targetPos.getX() + 0.5;
        double y1 = targetPos.getY() + 1.0;
        double z1 = targetPos.getZ() + 0.5;

        double dx = x1 - x0;
        double dy = y1 - y0;
        double dz = z1 - z0;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 0.5) {
            return true;
        }

        int steps = (int) Math.ceil(dist * 3.0);
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

        for (int i = 1; i < steps; i++) {
            double t = (double) i / steps;
            double arcBonus = Math.sin(t * Math.PI) * 0.45;
            double cx = x0 + dx * t;
            double cy = y0 + dy * t + arcBonus;
            double cz = z0 + dz * t;

            checkPos.set(Math.floor(cx), Math.floor(cy), Math.floor(cz));

            if (checkPos.equals(sprinklerPos) || checkPos.equals(targetPos)) {
                continue;
            }

            BlockState state = level.getBlockState(checkPos);
            if (isObstacle(state)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isObstacle(BlockState state) {
        if (state.isAir()) {
            return false;
        }
        Block block = state.getBlock();
        if (block instanceof IrrigationHoseBlock || block instanceof IrrigationSprinklerBlock) {
            return false;
        }
        if (isBiosphereBarrier(block)) {
            return true;
        }
        return state.isSolid() || !state.canBeReplaced();
    }

    public static boolean isBiosphereBarrier(Block block) {
        if (block == Blocks.TINTED_GLASS
                || block == Blocks.IRON_BLOCK
                || block == Blocks.GLASS) {
            return true;
        }
        var id = BuiltInRegistries.BLOCK.getKey(block);
        if (id != null && "sandstorm".equals(id.getNamespace())) {
            String path = id.getPath();
            return "fulgurite_glass".equals(path)
                    || "morphing_alloy_window".equals(path)
                    || "biosphere_door".equals(path)
                    || "biosphere_fence".equals(path)
                    || "biosphere_fence_gate".equals(path);
        }
        return false;
    }

    public static boolean isAridBlock(BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.SAND
                || block == Blocks.RED_SAND
                || block == Blocks.SANDSTONE
                || block == Blocks.SMOOTH_SANDSTONE
                || block == Blocks.CUT_SANDSTONE
                || block == Blocks.RED_SANDSTONE
                || block == Blocks.SUSPICIOUS_SAND) {
            return true;
        }
        var id = BuiltInRegistries.BLOCK.getKey(block);
        return id != null && "sandstorm".equals(id.getNamespace()) && "salinized_sand".equals(id.getPath());
    }

    public static boolean isDirtBlock(BlockState state) {
        return state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT);
    }

    public static boolean isGrassBlock(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK);
    }

    private static BlockState getRandomFlora(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 65) {
            return Blocks.SHORT_GRASS.defaultBlockState();
        } else if (roll < 80) {
            return Blocks.FERN.defaultBlockState();
        } else if (roll < 90) {
            return Blocks.DANDELION.defaultBlockState();
        } else {
            return Blocks.POPPY.defaultBlockState();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("active", this.active);
        output.putInt("pressureRadius", this.pressureRadius);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.active = input.getBooleanOr("active", false);
        this.pressureRadius = input.getIntOr("pressureRadius", 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }
}
