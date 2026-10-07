package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.BiosphereDoorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BiosphereDoorBlock extends Block implements EntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty SIDE = BooleanProperty.create("side");
    private static final VoxelShape CLOSED_SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public BiosphereDoorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(OPEN, false).setValue(SIDE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN, SIDE);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(OPEN) ? Shapes.empty() : CLOSED_SHAPE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(OPEN) ? Shapes.empty() : CLOSED_SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BiosphereDoorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, blockEntity) -> {
            if (blockEntity instanceof BiosphereDoorBlockEntity door) {
                door.serverTick(lvl, pos, blockState);
            }
        };
    }
}
