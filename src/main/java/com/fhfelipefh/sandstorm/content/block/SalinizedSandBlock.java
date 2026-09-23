package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SalinizedSandBlock extends Block {
    public SalinizedSandBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && (player == null || !player.getAbilities().instabuild)) {
            popResource(level, pos, new ItemStack(Blocks.SAND));
            if (level.getRandom().nextBoolean()) {
                popResource(level, pos, new ItemStack(SandStormItems.MINERAL_SALT));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
