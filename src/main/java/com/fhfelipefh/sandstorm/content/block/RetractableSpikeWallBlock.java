package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class RetractableSpikeWallBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final VoxelShape COLLISION_EXTENDED = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
    private static final VoxelShape OUTLINE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public RetractableSpikeWallBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean hasSignal = context.getLevel().hasNeighborSignal(context.getClickedPos());
        return defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(POWERED, hasSignal);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(POWERED) ? COLLISION_EXTENDED : Shapes.block();
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.scheduleTick(pos, this, 10);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (state.getValue(POWERED)) {
            AABB damageBox = new AABB(pos).inflate(0.2);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, damageBox);
            for (LivingEntity target : targets) {
                applyExtendedDamage(level, pos, state, target);
            }
        }
        level.scheduleTick(pos, this, 10);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        if (state.getValue(POWERED)) {
            applyExtendedDamage(level, pos, state, entity);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (state.getValue(POWERED)) {
            applyExtendedDamage(level, pos, state, entity);
        }
    }

    private void applyExtendedDamage(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity living) {
            boolean hurt = living.hurtServer(serverLevel, serverLevel.damageSources().cactus(), 10.0f);
            if (hurt) {
                Direction dir = state.getValue(FACING);
                Vec3 pushVec = new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ()).scale(0.4);
                living.push(pushVec.x, pushVec.y + 0.1, pushVec.z);

                serverLevel.sendParticles(ParticleTypes.CRIT, living.getX(), living.getY() + living.getBbHeight() * 0.5, living.getZ(), 8, 0.25, 0.25, 0.25, 0.1);
                serverLevel.playSound(null, pos, SoundEvents.THORNS_HIT, SoundSource.BLOCKS, 0.9f, 1.1f);
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (level.isClientSide()) {
            return;
        }

        boolean hasSignal = level.hasNeighborSignal(pos);
        if (hasSignal != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, hasSignal), 3);
            if (hasSignal) {
                level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.8f, 1.3f);
                if (level instanceof ServerLevel serverLevel) {
                    Direction dir = state.getValue(FACING);
                    AABB strikeBox = new AABB(pos).expandTowards(dir.getStepX() * 1.5, dir.getStepY() * 1.5, dir.getStepZ() * 1.5);
                    List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, strikeBox);
                    for (LivingEntity target : targets) {
                        target.hurtServer(serverLevel, serverLevel.damageSources().generic(), 14.0f);
                        target.push(dir.getStepX() * 1.2, dir.getStepY() * 0.8 + 0.3, dir.getStepZ() * 1.2);
                        serverLevel.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 0.5, target.getZ(), 15, 0.3, 0.3, 0.3, 0.2);
                    }
                }
            } else {
                level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.6f, 1.2f);
            }
        }
    }
}
