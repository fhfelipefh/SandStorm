package com.fhfelipefh.sandstorm.content.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KineticEjectorTrapBlock extends Block {
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_ARMED = Block.box(1, 0, 1, 15, 2, 15);
    private static final VoxelShape SHAPE_EXTENDED = Block.box(1, 0, 1, 15, 8, 15);

    public KineticEjectorTrapBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(CHARGED, true)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return defaultBlockState().setValue(FACING, facing).setValue(CHARGED, true);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(CHARGED) ? SHAPE_ARMED : SHAPE_EXTENDED;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && state.getValue(CHARGED)) {
            triggerAreaEjection(level, pos, state);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (!level.isClientSide() && level.hasNeighborSignal(pos) && state.getValue(CHARGED)) {
            triggerAreaEjection(level, pos, state);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (state.getValue(CHARGED) && entity instanceof LivingEntity living) {
            triggerEjection(level, pos, state, living);
        }
    }

    public void triggerAreaEjection(Level level, BlockPos pos, BlockState state) {
        AABB area = new AABB(pos).inflate(0.3, 1.2, 0.3);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area);
        if (!targets.isEmpty()) {
            for (LivingEntity target : targets) {
                triggerEjection(level, pos, state, target);
            }
        } else {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, 14, 0.25, 0.2, 0.25, 0.12);
                serverLevel.sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8, 0.2, 0.3, 0.2, 0.15);
            }
            level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 1.3f, 1.4f);
            level.setBlock(pos, state.setValue(CHARGED, false), 3);
            level.scheduleTick(pos, this, 60);
        }
    }

    private void triggerEjection(Level level, BlockPos pos, BlockState state, LivingEntity entity) {
        Direction facing = state.getValue(FACING);
        Vec3 currentMovement = entity.getDeltaMovement();
        double impulseX = currentMovement.x + (facing.getStepX() * 0.45);
        double impulseY = 1.35;
        double impulseZ = currentMovement.z + (facing.getStepZ() * 0.45);

        entity.setDeltaMovement(impulseX, impulseY, impulseZ);

        if (level instanceof ServerLevel serverLevel) {
            entity.hurtServer(serverLevel, level.damageSources().flyIntoWall(), 4.0f);
            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    pos.getX() + 0.5,
                    pos.getY() + 0.4,
                    pos.getZ() + 0.5,
                    14,
                    0.25,
                    0.2,
                    0.25,
                    0.12
            );
            serverLevel.sendParticles(
                    ParticleTypes.CRIT,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    8,
                    0.2,
                    0.3,
                    0.2,
                    0.15
            );
        }

        level.playSound(
                null,
                pos,
                SoundEvents.PISTON_EXTEND,
                SoundSource.BLOCKS,
                1.3f,
                1.4f
        );

        level.setBlock(pos, state.setValue(CHARGED, false), 3);
        level.scheduleTick(pos, this, 60);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(CHARGED)) {
            level.setBlock(pos, state.setValue(CHARGED, true), 3);
            level.playSound(
                    null,
                    pos,
                    SoundEvents.PISTON_CONTRACT,
                    SoundSource.BLOCKS,
                    0.9f,
                    1.1f
            );
        }
    }
}
