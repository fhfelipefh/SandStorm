package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.block.RappelAnchorBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.entity.RappelAnchorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class RappelWinchItem extends Item {

    public RappelWinchItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockPos placePos = clickedPos.relative(face);
        BlockState targetState = level.getBlockState(placePos);

        if (!targetState.canBeReplaced()) {
            return InteractionResult.PASS;
        }

        if (!level.getBlockState(clickedPos).isSolid()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            Direction facing = face.getAxis().isHorizontal() ? face : (player != null ? player.getDirection().getOpposite() : Direction.NORTH);
            BlockState anchorState = SandStormBlocks.RAPPEL_ANCHOR.defaultBlockState()
                    .setValue(RappelAnchorBlock.FACING, facing);
            level.setBlock(placePos, anchorState, 3);

            if (level.getBlockEntity(placePos) instanceof RappelAnchorBlockEntity anchorBe) {
                anchorBe.startDeployment();
            }

            level.playSound(null, placePos, SoundEvents.COPPER_BULB_TURN_ON, SoundSource.BLOCKS, 1.0f, 1.2f);
            level.playSound(null, placePos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f);

            if (player != null && !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.rappel_winch_desc"));
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.rappel_winch_usage"));
    }
}
