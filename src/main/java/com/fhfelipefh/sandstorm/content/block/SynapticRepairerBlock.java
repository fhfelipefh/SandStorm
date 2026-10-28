package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.SynapticRepairerBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SynapticRepairerBlock extends Block implements EntityBlock {

    public SynapticRepairerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SynapticRepairerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, SandStormBlocks.SYNAPTIC_REPAIRER_BE, (lvl, p, s, be) -> {
            if (lvl instanceof ServerLevel serverLevel) {
                SynapticRepairerBlockEntity.serverTick(serverLevel, p, s, be);
            }
        });
    }

    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> serverType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == serverType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SynapticRepairerBlockEntity repairer) {
                if (stack.is(SandStormItems.SYNAPTIC_FLUID_BUCKET)) {
                    int space = repairer.getMaxCapacity() - repairer.getFluidAmount();
                    if (space >= 1000) {
                        repairer.insertFluid(1000);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            player.getInventory().add(new ItemStack(Items.BUCKET));
                        }
                        sendTelemetry(player, repairer);
                        return InteractionResult.SUCCESS;
                    }
                }
                if (!repairer.getStoredItem().isEmpty()) {
                    if (stack.isEmpty() || player.isShiftKeyDown()) {
                        ItemStack item = repairer.getStoredItem();
                        repairer.setStoredItem(ItemStack.EMPTY);
                        player.getInventory().add(item);
                        sendTelemetry(player, repairer);
                        return InteractionResult.SUCCESS;
                    }
                } else if (!stack.isEmpty() && stack.isDamaged()) {
                    ItemStack toInsert = stack.split(1);
                    repairer.setStoredItem(toInsert);
                    sendTelemetry(player, repairer);
                    return InteractionResult.SUCCESS;
                }
                sendTelemetry(player, repairer);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void sendTelemetry(Player player, SynapticRepairerBlockEntity repairer) {
        if (!repairer.getStoredItem().isEmpty()) {
            ItemStack item = repairer.getStoredItem();
            int remaining = item.getDamageValue();
            Component msg = Component.literal("[REPARADOR] ").withStyle(ChatFormatting.DARK_AQUA)
                    .append(Component.literal(item.getHoverName().getString() + " ").withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal("(Dano: " + remaining + ") | ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(repairer.getFluidAmount() + " mB").withStyle(ChatFormatting.AQUA));
            player.sendOverlayMessage(msg);
        } else {
            Component msg = Component.literal("[REPARADOR] ").withStyle(ChatFormatting.DARK_AQUA)
                    .append(Component.literal("Vazio | ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(repairer.getFluidAmount() + " / " + repairer.getMaxCapacity() + " mB").withStyle(ChatFormatting.AQUA));
            player.sendOverlayMessage(msg);
        }
    }
}
