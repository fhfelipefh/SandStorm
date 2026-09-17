package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ThumperBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ThumperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            boolean nextState = !state.getValue(POWERED);
            level.setBlock(pos, state.setValue(POWERED, nextState), 3);
            level.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.6f, 0.6f);
        }
        return InteractionResult.SUCCESS;
    }

    public static boolean isActive(BlockState state) {
        return state.hasProperty(POWERED) && state.getValue(POWERED);
    }
}
