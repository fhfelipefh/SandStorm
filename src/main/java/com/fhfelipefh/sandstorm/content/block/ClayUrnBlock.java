package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.entity.CuratorSentinelEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClayUrnBlock extends HorizontalDirectionalBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);

    public ClayUrnBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile) {
        BlockPos pos = hitResult.getBlockPos();
        if (level instanceof ServerLevel serverLevel && projectile.mayInteract(serverLevel, pos)) {
            if (projectile.getOwner() instanceof Player player) {
                CuratorSentinelEntity.notifyVandalism(level, pos, player);
            }
            level.destroyBlock(pos, true, projectile);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (!level.isClientSide() && fallDistance > 0.75) {
            if (entity instanceof Player player) {
                CuratorSentinelEntity.notifyVandalism(level, pos, player);
            }
            level.destroyBlock(pos, true, entity);
        }
        super.fallOn(level, state, pos, entity, fallDistance);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        CuratorSentinelEntity.notifyVandalism(level, pos, player);
        return super.playerWillDestroy(level, pos, state, player);
    }
}
