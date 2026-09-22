package com.fhfelipefh.sandstorm.content.block;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class DewCondenserBlock extends Block {
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
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(WATER_LEVEL) < 3;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.canSeeSky(pos)) {
            return;
        }
        boolean isNight = level.getSkyDarken() >= 4;
        if (isNight) {
            int currentWater = state.getValue(WATER_LEVEL);
            if (currentWater < 3) {
                level.setBlock(pos, state.setValue(WATER_LEVEL, currentWater + 1), 3);
                level.playSound(null, pos, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER, SoundSource.BLOCKS, 0.5f, 1.2f);
            }
        }
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
        if (stack.is(Items.GLASS_BOTTLE)) {
            int water = state.getValue(WATER_LEVEL);
            if (water > 0) {
                if (!level.isClientSide()) {
                    level.setBlock(pos, state.setValue(WATER_LEVEL, water - 1), 3);
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
                }
                return InteractionResult.SUCCESS;
            } else {
                if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.dew_condenser_empty"), true);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            int water = state.getValue(WATER_LEVEL);
            boolean isNight = level.getSkyDarken() >= 4;
            Component status = isNight
                    ? Component.translatable("telemetry.sandstorm.dew_condenser_condensing")
                    : Component.translatable("telemetry.sandstorm.dew_condenser_waiting_night");
            serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.dew_condenser_status", water, 3, status), true);
        }
        return InteractionResult.SUCCESS;
    }
}
