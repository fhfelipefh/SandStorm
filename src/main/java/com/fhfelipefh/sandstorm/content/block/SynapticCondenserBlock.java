package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.SynapticCondenserBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.item.SynapticCartridgeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SynapticCondenserBlock extends Block implements EntityBlock {

    public SynapticCondenserBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SynapticCondenserBlockEntity(pos, state);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && entity instanceof Player player) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SynapticCondenserBlockEntity condenser) {
                if (player.isShiftKeyDown()) {
                    int space = condenser.getMaxCapacity() - condenser.getFluidAmount();
                    if (space > 0 && (player.experienceLevel > 0 || player.experienceProgress > 0)) {
                        int toDrain = Math.min(space, 15);
                        player.giveExperiencePoints(-toDrain);
                        condenser.insertFluid(toDrain);
                        if (level instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.5, 3, 0.2, 0.1, 0.2, 0.01);
                        }
                    }
                } else {
                    int available = condenser.getFluidAmount();
                    if (available > 0) {
                        int toGive = Math.min(available, 15);
                        condenser.extractFluid(toGive);
                        player.giveExperiencePoints(toGive);
                        if (level instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.GLOW, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.5, 3, 0.2, 0.1, 0.2, 0.01);
                        }
                    }
                }
            }
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SynapticCondenserBlockEntity condenser) {
                if (stack.is(Items.BUCKET)) {
                    if (condenser.getFluidAmount() >= 1000) {
                        condenser.extractFluid(1000);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            player.getInventory().add(new ItemStack(SandStormItems.SYNAPTIC_FLUID_BUCKET));
                        }
                        sendTelemetry(player, condenser);
                        return InteractionResult.SUCCESS;
                    }
                } else if (stack.is(SandStormItems.SYNAPTIC_FLUID_BUCKET)) {
                    int space = condenser.getMaxCapacity() - condenser.getFluidAmount();
                    if (space >= 1000) {
                        condenser.insertFluid(1000);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            player.getInventory().add(new ItemStack(Items.BUCKET));
                        }
                        sendTelemetry(player, condenser);
                        return InteractionResult.SUCCESS;
                    }
                } else if (stack.is(Items.GLASS_BOTTLE)) {
                    if (condenser.getFluidAmount() >= 10) {
                        condenser.extractFluid(10);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            player.getInventory().add(new ItemStack(Items.EXPERIENCE_BOTTLE));
                        }
                        sendTelemetry(player, condenser);
                        return InteractionResult.SUCCESS;
                    }
                } else if (stack.is(Items.EXPERIENCE_BOTTLE)) {
                    int space = condenser.getMaxCapacity() - condenser.getFluidAmount();
                    if (space >= 10) {
                        condenser.insertFluid(10);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
                        }
                        sendTelemetry(player, condenser);
                        return InteractionResult.SUCCESS;
                    }
                } else if (stack.is(SandStormItems.SYNAPTIC_CARTRIDGE)) {
                    int cartridgeXp = SynapticCartridgeItem.getStoredXp(stack);
                    if (player.isShiftKeyDown()) {
                        if (cartridgeXp > 0) {
                            int space = condenser.getMaxCapacity() - condenser.getFluidAmount();
                            int toTransfer = Math.min(space, cartridgeXp);
                            if (toTransfer > 0) {
                                condenser.insertFluid(toTransfer);
                                SynapticCartridgeItem.setStoredXp(stack, cartridgeXp - toTransfer);
                                sendTelemetry(player, condenser);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    } else {
                        int available = condenser.getFluidAmount();
                        if (available > 0) {
                            int toTransfer = Math.min(available, 500);
                            condenser.extractFluid(toTransfer);
                            SynapticCartridgeItem.setStoredXp(stack, cartridgeXp + toTransfer);
                            sendTelemetry(player, condenser);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
                sendTelemetry(player, condenser);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void sendTelemetry(Player player, SynapticCondenserBlockEntity condenser) {
        int amount = condenser.getFluidAmount();
        int max = condenser.getMaxCapacity();
        int approxLevels = SynapticCartridgeItem.getLevelForXp(amount);
        Component msg = Component.literal("[CONDENSADOR] ").withStyle(ChatFormatting.DARK_AQUA)
                .append(Component.literal(amount + " / " + max + " mB ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal("(~Nvl " + approxLevels + ")").withStyle(ChatFormatting.GREEN));
        player.sendOverlayMessage(msg);
    }
}
