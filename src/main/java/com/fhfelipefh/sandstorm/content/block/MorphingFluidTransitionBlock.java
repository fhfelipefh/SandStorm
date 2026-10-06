package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.MorphingFluidTransitionBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MorphingFluidTransitionBlock extends Block implements EntityBlock {

    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 4);

    private static final VoxelShape SHAPE_STAGE_0 = Block.box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_STAGE_1 = Block.box(0, 0, 0, 16, 12, 16);
    private static final VoxelShape SHAPE_STAGE_2 = Block.box(0, 0, 0, 16, 8, 16);
    private static final VoxelShape SHAPE_STAGE_3 = Block.box(0, 0, 0, 16, 4, 16);
    private static final VoxelShape SHAPE_STAGE_4 = Block.box(0, 0, 0, 16, 2, 16);

    public MorphingFluidTransitionBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_GRAY)
                .strength(-1.0f, 3600000.0f)
                .sound(SoundType.SLIME_BLOCK)
                .noCollision()
                .noOcclusion();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(STAGE)) {
            case 0 -> SHAPE_STAGE_0;
            case 1 -> SHAPE_STAGE_1;
            case 2 -> SHAPE_STAGE_2;
            case 3 -> SHAPE_STAGE_3;
            default -> SHAPE_STAGE_4;
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MorphingFluidTransitionBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof MorphingFluidTransitionBlockEntity entity) {
                    MorphingFluidTransitionBlockEntity.clientTick(lvl, pos, st, entity);
                }
            };
        } else {
            return (lvl, pos, st, be) -> {
                if (be instanceof MorphingFluidTransitionBlockEntity entity) {
                    MorphingFluidTransitionBlockEntity.serverTick(lvl, pos, st, entity);
                }
            };
        }
    }
}
