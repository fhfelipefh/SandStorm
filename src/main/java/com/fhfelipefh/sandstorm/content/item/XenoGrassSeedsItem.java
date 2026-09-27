package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class XenoGrassSeedsItem extends Item {
    public XenoGrassSeedsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(SandStormBlocks.SALINIZED_SAND)) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, SandStormBlocks.XENO_GRASS_BLOCK.defaultBlockState());
                level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                Player player = context.getPlayer();
                ItemStack stack = context.getItemInHand();
                if (player == null || !player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
