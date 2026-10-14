package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.RadioactiveWasteBarrelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RadioactiveWasteBarrelBlock extends Block implements EntityBlock {

    public RadioactiveWasteBarrelBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RadioactiveWasteBarrelBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == SandStormBlocks.RADIOACTIVE_WASTE_BARREL_BE) {
            return (lvl, pos, st, be) -> RadioactiveWasteBarrelBlockEntity.tick(lvl, pos, st, (RadioactiveWasteBarrelBlockEntity) be);
        }
        return null;
    }
}
