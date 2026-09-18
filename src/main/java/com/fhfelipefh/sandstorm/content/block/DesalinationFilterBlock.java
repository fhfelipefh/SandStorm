package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DesalinationFilterBlock extends Block {

    public DesalinationFilterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(SandStormItems.BRACKISH_WATER_BOTTLE)) {
            if (!level.isClientSide()) {
                stack.shrink(1);
                player.getInventory().add(new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE));
                player.getInventory().add(new ItemStack(SandStormItems.MINERAL_SALT, 2));
                level.playSound(null, pos, com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents.DESALINATION_PROCESS, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
