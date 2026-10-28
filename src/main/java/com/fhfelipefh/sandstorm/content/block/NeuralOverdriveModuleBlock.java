package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.NeuralOverdriveModuleBlockEntity;
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

public class NeuralOverdriveModuleBlock extends Block implements EntityBlock {

    public NeuralOverdriveModuleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NeuralOverdriveModuleBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, SandStormBlocks.NEURAL_OVERDRIVE_MODULE_BE, (lvl, p, s, be) -> {
            if (lvl instanceof ServerLevel serverLevel) {
                NeuralOverdriveModuleBlockEntity.serverTick(serverLevel, p, s, be);
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
            if (be instanceof NeuralOverdriveModuleBlockEntity module) {
                if (stack.is(SandStormItems.SYNAPTIC_FLUID_BUCKET)) {
                    int space = module.getMaxCapacity() - module.getFluidAmount();
                    if (space >= 1000) {
                        module.insertFluid(1000);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            player.getInventory().add(new ItemStack(Items.BUCKET));
                        }
                        sendTelemetry(player, module);
                        return InteractionResult.SUCCESS;
                    }
                }
                sendTelemetry(player, module);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void sendTelemetry(Player player, NeuralOverdriveModuleBlockEntity module) {
        Component stateComp = module.isActive()
                ? Component.literal("Ativo").withStyle(ChatFormatting.GREEN)
                : Component.literal("Standby").withStyle(ChatFormatting.GRAY);
        Component msg = Component.literal("[OVERDRIVE] ").withStyle(ChatFormatting.DARK_AQUA)
                .append(Component.literal(module.getFluidAmount() + " / " + module.getMaxCapacity() + " mB | ").withStyle(ChatFormatting.AQUA))
                .append(stateComp);
        player.sendOverlayMessage(msg);
    }
}
