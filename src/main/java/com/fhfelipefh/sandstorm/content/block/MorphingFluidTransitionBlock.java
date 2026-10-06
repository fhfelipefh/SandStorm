package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.MorphingFluidTransitionBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class MorphingFluidTransitionBlock extends Block implements EntityBlock {

    public MorphingFluidTransitionBlock(BlockBehaviour.Properties properties) {
        super(properties);
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
