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

public class Printer3DBlock extends Block {

    public Printer3DBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(SandStormItems.SILICON_WAFER)) {
            if (!level.isClientSide()) {
                stack.shrink(1);
                ItemStack result = new ItemStack(SandStormItems.CIRCUIT_BOARD);
                if (stack.isEmpty()) {
                    player.setItemInHand(hand, result);
                } else if (!player.getInventory().add(result)) {
                    popResource(level, pos, result);
                }
                level.playSound(null, pos, com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents.PRINTER_3D_CRAFT, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
