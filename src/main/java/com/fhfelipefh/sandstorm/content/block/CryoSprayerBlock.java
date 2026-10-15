package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class CryoSprayerBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public CryoSprayerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) {
            return;
        }
        boolean hasSignal = level.hasNeighborSignal(pos);
        if (hasSignal != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, hasSignal), 3);
            if (hasSignal) {
                performCryoSpray((ServerLevel) level, pos, state.getValue(FACING));
                level.scheduleTick(pos, this, 6);
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED)) {
            performCryoSpray(level, pos, state.getValue(FACING));
            level.scheduleTick(pos, this, 6);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(POWERED)) {
            return;
        }
        Direction facing = state.getValue(FACING);
        double startX = pos.getX() + 0.5 + facing.getStepX() * 0.55;
        double startY = pos.getY() + 0.5 + facing.getStepY() * 0.55;
        double startZ = pos.getZ() + 0.5 + facing.getStepZ() * 0.55;

        for (int i = 0; i < 6; i++) {
            double speed = 0.25 + random.nextDouble() * 0.15;
            double vx = facing.getStepX() * speed + (random.nextDouble() - 0.5) * 0.08;
            double vy = facing.getStepY() * speed + (random.nextDouble() - 0.5) * 0.08;
            double vz = facing.getStepZ() * speed + (random.nextDouble() - 0.5) * 0.08;
            level.addParticle(ParticleTypes.SNOWFLAKE, startX, startY, startZ, vx, vy, vz);
        }
    }

    private void performCryoSpray(ServerLevel level, BlockPos pos, Direction facing) {
        level.playSound(null, pos, SoundEvents.SNOW_GOLEM_SHOOT, SoundSource.BLOCKS, 1.2F, 1.6F);

        for (int distance = 1; distance <= 5; distance++) {
            BlockPos targetPos = pos.relative(facing, distance);
            BlockState targetState = level.getBlockState(targetPos);

            if (targetState.is(Blocks.WATER)) {
                level.setBlock(targetPos, Blocks.PACKED_ICE.defaultBlockState(), 3);
            } else if (targetState.is(Blocks.LAVA)) {
                level.setBlock(targetPos, Blocks.OBSIDIAN.defaultBlockState(), 3);
            } else if (targetState.is(Blocks.FIRE) || targetState.is(Blocks.SOUL_FIRE)) {
                level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
            }

            AABB sprayBounds = new AABB(targetPos).inflate(0.2);
            List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, sprayBounds);
            for (LivingEntity victim : victims) {
                victim.hurtServer(level, level.damageSources().freeze(), 3.0F);
                victim.setTicksFrozen(Math.min(victim.getTicksRequiredToFreeze() + 80, victim.getTicksFrozen() + 100));
                victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1));
            }

            if (targetState.isSolid() && !targetState.is(Blocks.WATER) && !targetState.is(Blocks.LAVA)) {
                break;
            }
        }
    }
}
