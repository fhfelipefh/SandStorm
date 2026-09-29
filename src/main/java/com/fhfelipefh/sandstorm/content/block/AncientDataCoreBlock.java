package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.entity.ScrapSentinelEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AncientDataCoreBlock extends Block {

    public AncientDataCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            ScrapSentinelEntity.alertNearbySentinels(level, pos, 32.0, player);
            if (!player.isCreative()) {
                popResource(level, pos, new ItemStack(SandStormItems.TECH_DISC, 1));
                popResource(level, pos, new ItemStack(SandStormItems.CIRCUIT_BOARD, 1));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
