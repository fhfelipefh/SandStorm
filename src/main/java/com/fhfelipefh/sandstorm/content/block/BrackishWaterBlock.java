package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BrackishWaterBlock extends Block {

    public static final IntegerProperty DRAINS = IntegerProperty.create("drains", 0, 6);

    public BrackishWaterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DRAINS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DRAINS);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.GLASS_BOTTLE)) {
            if (!level.isClientSide()) {
                stack.shrink(1);
                ItemStack result = new ItemStack(SandStormItems.BRACKISH_WATER_BOTTLE);
                if (stack.isEmpty()) {
                    player.setItemInHand(hand, result);
                } else if (!player.getInventory().add(result)) {
                    popResource(level, pos, result);
                }
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);

                int nextDrains = state.getValue(DRAINS) + 1;
                int maxDrains = 3 + (Math.abs((int) (pos.asLong() ^ 0x5DEECE66DL)) % 4);
                if (nextDrains >= maxDrains) {
                    popResource(level, pos, new ItemStack(SandStormItems.MINERAL_SALT, 2 + level.getRandom().nextInt(3)));
                    level.destroyBlock(pos, false);
                } else {
                    level.setBlock(pos, state.setValue(DRAINS, nextDrains), 3);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
