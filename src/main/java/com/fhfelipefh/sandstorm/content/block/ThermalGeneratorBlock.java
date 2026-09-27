package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.ThermalGeneratorBlockEntity;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ThermalGeneratorBlock extends Block implements EntityBlock {
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ThermalGeneratorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof ThermalGeneratorBlockEntity machine) {
                machine.serverTick(lvl, pos, st);
            }
        };
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ThermalGeneratorBlockEntity machine) {
            for (int i = 0; i < machine.getContainerSize(); i++) {
                ItemStack item = machine.getItem(i);
                if (!item.isEmpty()) {
                    popResource(level, pos, item);
                }
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ThermalGeneratorBlockEntity generator) {
            if (stack.is(Items.LAVA_BUCKET)) {
                if (!level.isClientSide()) {
                    int currentLava = generator.getLavaAmount();
                    if (currentLava + 1000 <= ThermalGeneratorBlockEntity.MAX_LAVA_MB) {
                        generator.setLavaAmount(currentLava + 1000);
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
                        if (player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendSystemMessage(Component.translatable(
                                    "message.sandstorm.thermal_generator.lava_added",
                                    generator.getLavaAmount(),
                                    ThermalGeneratorBlockEntity.MAX_LAVA_MB
                            ), true);
                        }
                    } else if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(Component.translatable(
                                "message.sandstorm.thermal_generator.tank_full"
                        ), true);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (!level.isClientSide()) {
                player.openMenu(generator);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ThermalGeneratorBlockEntity generator) {
                player.openMenu(generator);
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
