package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class VaultWallPanelBlock extends Block {
    public static final EnumProperty<VerticalConnection> VERTICAL = EnumProperty.create("vertical", VerticalConnection.class);

    public VaultWallPanelBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(VERTICAL, VerticalConnection.SINGLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VERTICAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(VERTICAL, getConnection(context.getLevel(), context.getClickedPos()));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction.getAxis() == Direction.Axis.Y) {
            return state.setValue(VERTICAL, getConnection(level, pos));
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    private VerticalConnection getConnection(LevelReader level, BlockPos pos) {
        boolean above = level.getBlockState(pos.above()).is(this);
        boolean below = level.getBlockState(pos.below()).is(this);
        if (above && below) {
            return VerticalConnection.MIDDLE;
        }
        if (above) {
            return VerticalConnection.BOTTOM;
        }
        if (below) {
            return VerticalConnection.TOP;
        }
        return VerticalConnection.SINGLE;
    }
}
