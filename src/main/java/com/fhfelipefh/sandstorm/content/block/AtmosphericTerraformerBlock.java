package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.TerraformingIndexComponent;
import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericTerraformerBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.phys.BlockHitResult;

public class AtmosphericTerraformerBlock extends Block implements EntityBlock {
    public static final long DEFAULT_CAPACITY = 250000L;
    public static final long DEFAULT_TRANSFER_RATE = 2500L;
    public static final long INITIAL_CHARGE = 100000L;
    public static final long ENERGY_PER_CYCLE = 2000L;

    private final TerraformingIndexComponent terraformingIndex = new TerraformingIndexComponent();

    public AtmosphericTerraformerBlock(Properties properties) {
        super(properties);
    }

    public TerraformingIndexComponent getTerraformingIndex() {
        return terraformingIndex;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AtmosphericTerraformerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof AtmosphericTerraformerBlockEntity terraformer) {
                terraformer.serverTick(lvl, pos, st);
            }
        };
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AtmosphericTerraformerBlockEntity terraformer) {
                if (stack.is(Items.WATER_BUCKET)) {
                    if (terraformer.addWater(1000) > 0) {
                        if (!player.getAbilities().instabuild) {
                            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                        }
                        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                } else if (stack.is(SandStormItems.POTABLE_WATER_BOTTLE)) {
                    if (terraformer.addWater(250) > 0) {
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
                        }
                        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                } else if (stack.is(SandStormItems.BRACKISH_WATER_BOTTLE)) {
                    if (terraformer.addWater(150) > 0) {
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
                        }
                        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                } else if (stack.is(Items.POTION)) {
                    if (terraformer.addWater(250) > 0) {
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
                        }
                        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                } else if (stack.is(SandStormItems.MINERAL_SALT)) {
                    terraformer.addMineralUnits(16);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.SUCCESS;
                } else if (stack.is(Items.BONE_MEAL)) {
                    terraformer.addMineralUnits(8);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.SUCCESS;
                } else if (stack.is(SandStormItems.RAW_LITHIUM_SALTS)) {
                    terraformer.addMineralUnits(24);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.SUCCESS;
                } else if (AtmosphericTerraformerBlockEntity.isSeedItem(stack)) {
                    terraformer.addSeedUnits(8);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.SUCCESS;
                } else if (AtmosphericTerraformerBlockEntity.isSaplingItem(stack)) {
                    ItemStack existing = terraformer.getItem(AtmosphericTerraformerBlockEntity.SLOT_SAPLINGS);
                    if (existing.isEmpty()) {
                        terraformer.setItem(AtmosphericTerraformerBlockEntity.SLOT_SAPLINGS, stack.copyWithCount(1));
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                        level.playSound(null, pos, SoundEvents.CHERRY_SAPLING_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    } else if (ItemStack.isSameItemSameComponents(existing, stack) && existing.getCount() < existing.getMaxStackSize()) {
                        existing.grow(1);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                        terraformer.setChanged();
                        level.playSound(null, pos, SoundEvents.CHERRY_SAPLING_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                } else if (stack.is(SandStormItems.ELECTRIC_COMPONENT)) {
                    terraformer.getEnergyStorage().receiveEnergy(50000L);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.8f, 1.2f);
                    return InteractionResult.SUCCESS;
                } else if (stack.is(Items.REDSTONE_BLOCK)) {
                    terraformer.getEnergyStorage().receiveEnergy(9000L);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.8f, 1.2f);
                    return InteractionResult.SUCCESS;
                } else if (stack.is(Items.REDSTONE)) {
                    terraformer.getEnergyStorage().receiveEnergy(1000L);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.8f, 1.2f);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AtmosphericTerraformerBlockEntity terraformer) {
                player.openMenu(terraformer);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AtmosphericTerraformerBlockEntity terraformer) {
                terraformer.triggerDissipation(serverLevel);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AtmosphericTerraformerBlockEntity terraformer) {
            terraformer.triggerDissipation(level);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
}
