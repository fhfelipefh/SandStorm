package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ThermalGeneratorBlock extends Block {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public ThermalGeneratorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide()) {
            ThermalGeneratorManager.registerGenerator(level.dimension(), pos, state.getValue(LIT) ? ThermalGeneratorManager.TICKS_PER_BUCKET : 0);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        ThermalGeneratorManager.unregisterGenerator(level.dimension(), pos);
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.LAVA_BUCKET)) {
            if (!level.isClientSide()) {
                int current = ThermalGeneratorManager.getBurnTime(level.dimension(), pos);
                if (current >= ThermalGeneratorManager.MAX_BURN_TICKS) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(Component.literal("§e[Gerador Térmico]§r Tanque de lava cheio! (" + (current / 20) + "s)"), true);
                    }
                    return InteractionResult.SUCCESS;
                }
                int updated = ThermalGeneratorManager.addFuel(level.dimension(), pos, ThermalGeneratorManager.TICKS_PER_BUCKET);
                if (!player.isCreative()) {
                    stack.shrink(1);
                    ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                    if (stack.isEmpty()) {
                        player.setItemInHand(hand, emptyBucket);
                    } else if (!player.getInventory().add(emptyBucket)) {
                        popResource(level, pos, emptyBucket);
                    }
                }
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.setBlock(pos, state.setValue(LIT, true), 3);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.literal("§6[Gerador Térmico]§r Lava adicionada! Combustível: " + (updated / 20) + "s | WPT: 60 J/tick"), true);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            int burnTime = ThermalGeneratorManager.getBurnTime(level.dimension(), pos);
            if (burnTime > 0) {
                serverPlayer.sendSystemMessage(Component.literal("§6[Gerador Térmico]§r Ativo | Queima: " + (burnTime / 20) + "s | WPT: TRANSMITINDO (60 J/tick, 32m)"), true);
            } else {
                serverPlayer.sendSystemMessage(Component.literal("§c[Gerador Térmico]§r INATIVO - Abasteça com um Balde de Lava!"), true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            if (random.nextFloat() < 0.25f) {
                level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.6f, 1.0f, false);
            }
            double x = pos.getX() + 0.2 + random.nextDouble() * 0.6;
            double y = pos.getY() + 1.0 + random.nextDouble() * 0.1;
            double z = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.04, 0.0);
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.02, 0.0);
            if (random.nextFloat() < 0.3f) {
                level.addParticle(ParticleTypes.LAVA, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }
}
