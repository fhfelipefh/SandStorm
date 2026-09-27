package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
            if (level.getRandom().nextFloat() < 0.50f) {
                popResource(level, pos, new ItemStack(Items.COPPER_INGOT, 1 + level.getRandom().nextInt(3)));
            }
            if (level.getRandom().nextFloat() < 0.50f) {
                popResource(level, pos, new ItemStack(Items.REDSTONE, 1 + level.getRandom().nextInt(3)));
            }
            if (level.getRandom().nextFloat() < 0.40f) {
                popResource(level, pos, new ItemStack(Items.GOLD_NUGGET, 1 + level.getRandom().nextInt(4)));
            }
            if (level.getRandom().nextFloat() < 0.25f) {
                popResource(level, pos, new ItemStack(Items.OBSIDIAN, 1));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
