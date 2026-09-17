package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BuriedTechRuinsBlock extends Block {

    public BuriedTechRuinsBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && !player.isCreative()) {
            popResource(level, pos, new ItemStack(SandStormItems.SCRAP_METAL, 1 + level.getRandom().nextInt(2)));
            if (level.getRandom().nextFloat() < 0.30f) {
                popResource(level, pos, new ItemStack(SandStormItems.TECH_DISC, 1));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
