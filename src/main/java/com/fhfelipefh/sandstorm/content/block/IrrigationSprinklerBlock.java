package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.IrrigationSprinklerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IrrigationSprinklerBlock extends Block implements EntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    private static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 14.0, 11.0);

    public IrrigationSprinklerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IrrigationSprinklerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof IrrigationSprinklerBlockEntity sprinkler) {
                IrrigationSprinklerBlockEntity.serverTick(lvl, pos, st, sprinkler);
            }
        };
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(ACTIVE)) {
            return;
        }

        int radius = 8;
        if (level.getBlockEntity(pos) instanceof IrrigationSprinklerBlockEntity be) {
            int beRadius = be.getPressureRadius();
            if (beRadius > 0) {
                radius = beRadius;
            }
        }

        float angle = (level.getGameTime() * 22.0f + (pos.hashCode() & 0xFF)) % 360.0f;
        double rad = Math.toRadians(angle);

        double originX = pos.getX() + 0.5;
        double originY = pos.getY() + 0.85;
        double originZ = pos.getZ() + 0.5;

        double distance = 1.5 + random.nextDouble() * (radius - 1.5);
        double speedH = 0.08 * (distance / (double) radius) * (radius * 0.45);
        double vx = Math.cos(rad) * speedH;
        double vy = 0.22 + random.nextDouble() * 0.12;
        double vz = Math.sin(rad) * speedH;

        level.addParticle(ParticleTypes.SPLASH, originX, originY, originZ, vx, vy, vz);
        level.addParticle(ParticleTypes.FALLING_WATER, originX, originY, originZ, vx * 0.85, vy * 0.85, vz * 0.85);

        double landX = originX + Math.cos(rad) * distance;
        double landZ = originZ + Math.sin(rad) * distance;
        level.addParticle(ParticleTypes.RAIN, landX, originY + 0.5, landZ, 0.0, -0.2, 0.0);

        if (random.nextInt(5) == 0) {
            level.playLocalSound(originX, originY, originZ, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.3f, 1.8f, false);
        }
    }
}
