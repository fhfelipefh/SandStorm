package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.RappelAnchorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RappelRopeBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);

    public RappelRopeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockPos anchorPos = findAnchorPos(level, pos);
            if (anchorPos != null) {
                BlockEntity be = level.getBlockEntity(anchorPos);
                if (be instanceof RappelAnchorBlockEntity anchor) {
                    anchor.attachPlayer(player, pos);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return useWithoutItem(state, level, pos, player, hitResult);
    }

    public static BlockPos findAnchorPos(Level level, BlockPos startPos) {
        BlockPos current = startPos.above();
        int checks = 0;
        while (checks < 130 && current.getY() < level.getMaxY()) {
            BlockState st = level.getBlockState(current);
            if (st.is(SandStormBlocks.RAPPEL_ANCHOR)) {
                return current;
            }
            if (!st.is(SandStormBlocks.RAPPEL_ROPE)) {
                return null;
            }
            current = current.above();
            checks++;
        }
        return null;
    }
}
