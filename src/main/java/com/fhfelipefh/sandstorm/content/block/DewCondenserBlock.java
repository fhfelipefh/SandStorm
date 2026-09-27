package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.DewCondenserBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class DewCondenserBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty WATER_LEVEL = IntegerProperty.create("water_level", 0, 3);

    public DewCondenserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATER_LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATER_LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATER_LEVEL, 0);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DewCondenserBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof DewCondenserBlockEntity condenser) {
                condenser.serverTick(lvl, pos, st);
            }
        };
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        boolean isNight = level.getSkyDarken() >= 4;
        if (state.getValue(WATER_LEVEL) > 0 || isNight) {
            double px = pos.getX() + 0.3 + random.nextDouble() * 0.4;
            double py = pos.getY() + 0.85;
            double pz = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
            if (random.nextFloat() < 0.25f) {
                level.addParticle(ParticleTypes.SPLASH, px, py - 0.4, pz, 0.0, 0.01, 0.0);
            }
            if (random.nextFloat() < 0.15f) {
                level.addParticle(ParticleTypes.FALLING_WATER, px, py - 0.3, pz, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DewCondenserBlockEntity condenser) {
                if (stack.is(Items.GLASS_BOTTLE)) {
                    if (condenser.drainWater(250) >= 250) {
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            ItemStack bottle = new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE);
                            if (stack.isEmpty()) {
                                player.setItemInHand(hand, bottle);
                            } else if (!player.getInventory().add(bottle)) {
                                popResource(level, pos, bottle);
                            }
                        }
                        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    } else if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.dew_condenser_empty"), true);
                        return InteractionResult.SUCCESS;
                    }
                } else if (stack.is(Items.BUCKET)) {
                    if (condenser.drainWater(1000) >= 1000) {
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            ItemStack bucket = new ItemStack(Items.WATER_BUCKET);
                            if (stack.isEmpty()) {
                                player.setItemInHand(hand, bucket);
                            } else if (!player.getInventory().add(bucket)) {
                                popResource(level, pos, bucket);
                            }
                        }
                        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    } else if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.dew_condenser_empty"), true);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DewCondenserBlockEntity condenser) {
                player.openMenu(condenser);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DewCondenserBlockEntity condenser) {
            for (int i = 0; i < condenser.getContainerSize(); i++) {
                ItemStack item = condenser.getItem(i);
                if (!item.isEmpty()) {
                    popResource(level, pos, item);
                }
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
}
