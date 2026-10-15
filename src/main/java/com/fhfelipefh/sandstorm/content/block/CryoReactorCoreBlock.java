package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

public class CryoReactorCoreBlock extends Block {

    public static final BooleanProperty OPERATIONAL = BooleanProperty.create("operational");

    public CryoReactorCoreBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(OPERATIONAL, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPERATIONAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(OPERATIONAL, true);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            CryoVaultReactorManager.registerReactor(level.dimension(), pos, state.getValue(OPERATIONAL));
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && state.getValue(OPERATIONAL)) {
            level.playSound(null, pos, SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.BLOCKS, 1.2F, 0.8F);
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.2F);
            popResource(level, pos, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 1));
            CryoVaultReactorManager.setReactorOperational(level.dimension(), pos, false);
            if (player != null && !player.isCreative() && level.getServer() != null) {
                level.getServer().execute(() -> {
                    level.setBlock(pos, defaultBlockState().setValue(OPERATIONAL, false), 3);
                });
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
        if (!state.getValue(OPERATIONAL)) {
            CryoVaultReactorManager.unregisterReactor(level.dimension(), pos);
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(OPERATIONAL)) {
            if (stack.is(Items.WATER_BUCKET)) {
                if (!level.isClientSide()) {
                    if (!player.isCreative()) {
                        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    }
                    level.setBlock(pos, state.setValue(OPERATIONAL, true), 3);
                    CryoVaultReactorManager.setReactorOperational(level.dimension(), pos, true);
                    level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.2F, 1.0F);
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.4F);
                    if (level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 12; i++) {
                            serverLevel.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 1, 0.3, 0.2, 0.3, 0.05);
                            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0.4, 0.4, 0.4, 0.02);
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (level.isClientSide() || state.getValue(OPERATIONAL)) {
            return;
        }

        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.relative(direction);
            BlockState adjacentState = level.getBlockState(adjacentPos);
            if (adjacentState.is(Blocks.WATER)) {
                level.setBlock(adjacentPos, Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos, state.setValue(OPERATIONAL, true), 3);
                CryoVaultReactorManager.setReactorOperational(level.dimension(), pos, true);
                level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.2F, 1.0F);
                level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.4F);
                if (level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 10; i++) {
                        serverLevel.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 1, 0.3, 0.2, 0.3, 0.05);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(OPERATIONAL)) {
            if (random.nextInt(3) == 0) {
                double px = pos.getX() + 0.2 + random.nextDouble() * 0.6;
                double py = pos.getY() + 0.2 + random.nextDouble() * 0.8;
                double pz = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
                level.addParticle(ParticleTypes.SNOWFLAKE, px, py, pz, 0.0, 0.01, 0.0);
            }
        } else {
            double px = pos.getX() + 0.3 + random.nextDouble() * 0.4;
            double py = pos.getY() + 0.8 + random.nextDouble() * 0.3;
            double pz = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
            level.addParticle(ParticleTypes.SMOKE, px, py, pz, 0.0, 0.04, 0.0);
            if (random.nextInt(4) == 0) {
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, px, py, pz, 0.0, 0.03, 0.0);
            }
        }
    }
}
