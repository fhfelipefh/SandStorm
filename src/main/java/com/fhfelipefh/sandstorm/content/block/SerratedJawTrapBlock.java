package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SerratedJawTrapBlock extends Block {
    public static final BooleanProperty SNAPPED = BooleanProperty.create("snapped");
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_OPEN = Block.box(1, 0, 1, 15, 2, 15);
    private static final VoxelShape SHAPE_SNAPPED = Block.box(2, 0, 2, 14, 8, 14);

    public SerratedJawTrapBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(SNAPPED, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SNAPPED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return defaultBlockState().setValue(FACING, facing).setValue(SNAPPED, false);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(SNAPPED) ? SHAPE_SNAPPED : SHAPE_OPEN;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(SNAPPED) ? SHAPE_SNAPPED : SHAPE_OPEN;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        handleEntityInteraction(level, pos, state, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        handleEntityInteraction(level, pos, state, entity);
    }

    private void handleEntityInteraction(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!state.getValue(SNAPPED)) {
            level.setBlock(pos, state.setValue(SNAPPED, true), 3);
            level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, 0.7f);
            level.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.4f, 1.8f);

            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 12, 0.2, 0.1, 0.2, 0.1);
                if (entity instanceof LivingEntity living) {
                    living.hurtServer(serverLevel, serverLevel.damageSources().generic(), 9.0f);
                    living.makeStuckInBlock(state, new Vec3(0.01, 0.01, 0.01));
                    living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 6, false, false, false));
                }
            }
        } else if (entity instanceof LivingEntity living) {
            living.makeStuckInBlock(state, new Vec3(0.01, 0.01, 0.01));
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20, 6, false, false, false));
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(SNAPPED) && player.isShiftKeyDown()) {
            level.setBlock(pos, state.setValue(SNAPPED, false), 3);
            level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0.8f, 1.2f);
            return InteractionResult.SUCCESS;
        } else if (!state.getValue(SNAPPED)) {
            handleEntityInteraction(level, pos, state, player);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (!level.isClientSide() && level.hasNeighborSignal(pos) && !state.getValue(SNAPPED)) {
            handleEntityInteraction(level, pos, state, null);
        }
    }
}
