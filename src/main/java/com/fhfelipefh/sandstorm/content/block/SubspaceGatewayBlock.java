package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SubspaceGatewayBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class SubspaceGatewayBlock extends Block implements EntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public SubspaceGatewayBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SubspaceGatewayBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof SubspaceGatewayBlockEntity gateway) {
                SubspaceGatewayBlockEntity.serverTick(lvl, pos, st, gateway);
            }
        };
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (!level.isClientSide() && entity instanceof ServerPlayer player) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SubspaceGatewayBlockEntity gateway) {
                gateway.attemptSubspaceJump(player);
            }
        }
    }

    public static int getSubspaceFuelEnergy(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        if (stack.is(SandStormItems.SUBSPACE_WARP_CORE) || stack.is(SandStormItems.SUPERHEATED_LITHIUM_CAPSULE)) {
            return 50000;
        }
        if (stack.is(SandStormBlocks.SOLID_STATE_ACCUMULATOR.asItem())) {
            return 25000;
        }
        if (stack.is(Items.COAL_BLOCK)) {
            return 16000;
        }
        if (stack.is(Items.BLAZE_ROD)) {
            return 5000;
        }
        if (stack.is(Items.MAGMA_BLOCK) || stack.is(Items.GLOWSTONE)) {
            return 4000;
        }
        if (stack.is(Items.FIRE_CHARGE)) {
            return 3000;
        }
        if (stack.is(Items.BLAZE_POWDER)) {
            return 2500;
        }
        if (stack.is(SandStormItems.RAW_LITHIUM_SALTS) || stack.is(Items.MAGMA_CREAM)) {
            return 2000;
        }
        if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) {
            return 1600;
        }
        if (stack.is(Items.GLOWSTONE_DUST)) {
            return 1000;
        }
        return BaseMachineBlockEntity.getFuelEnergy(stack);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SubspaceGatewayBlockEntity gateway) {
            if (stack.is(Items.LAVA_BUCKET)) {
                if (gateway.getEnergy() < SubspaceGatewayBlockEntity.MAX_ENERGY) {
                    gateway.setEnergy(gateway.getEnergy() + 20000);
                    if (!player.getAbilities().instabuild) {
                        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    }
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0f, 1.0f);
                    player.sendSystemMessage(
                            Component.translatable("telemetry.sandstorm.subspace_gateway_fueled", gateway.getEnergy(), SubspaceGatewayBlockEntity.MAX_ENERGY)
                    );
                    return InteractionResult.SUCCESS;
                }
            }
            int fuelValue = getSubspaceFuelEnergy(stack);
            if (fuelValue > 0 && gateway.getEnergy() < SubspaceGatewayBlockEntity.MAX_ENERGY) {
                gateway.setEnergy(gateway.getEnergy() + fuelValue);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.8f, 1.2f);
                player.sendSystemMessage(
                        Component.translatable("telemetry.sandstorm.subspace_gateway_fueled", gateway.getEnergy(), SubspaceGatewayBlockEntity.MAX_ENERGY)
                );
                return InteractionResult.SUCCESS;
            }
            player.sendSystemMessage(
                    Component.translatable(gateway.getCachedThermalRate() > 0
                            ? "telemetry.sandstorm.subspace_gateway_geothermal_status"
                            : "telemetry.sandstorm.subspace_gateway_status", gateway.getEnergy(), SubspaceGatewayBlockEntity.MAX_ENERGY)
            );
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SubspaceGatewayBlockEntity gateway) {
            player.sendSystemMessage(
                    Component.translatable(gateway.getCachedThermalRate() > 0
                            ? "telemetry.sandstorm.subspace_gateway_geothermal_status"
                            : "telemetry.sandstorm.subspace_gateway_status", gateway.getEnergy(), SubspaceGatewayBlockEntity.MAX_ENERGY)
            );
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            if (random.nextInt(3) == 0) {
                double x = pos.getX() + 0.2 + random.nextDouble() * 0.6;
                double y = pos.getY() + 1.0;
                double z = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
                level.addParticle(ParticleTypes.PORTAL, x, y, z, (random.nextDouble() - 0.5) * 0.2, random.nextDouble() * 0.2, (random.nextDouble() - 0.5) * 0.2);
            }
        }
        if (level.dimension() == Level.NETHER && random.nextInt(4) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 0.9;
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.02, 0.0);
        }
    }
}
