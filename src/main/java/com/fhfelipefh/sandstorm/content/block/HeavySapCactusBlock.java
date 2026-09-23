package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SamplingSyringeItem;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HeavySapCactusBlock extends Block {
    public static final IntegerProperty SAP_LEVEL = IntegerProperty.create("sap_level", 0, 3);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public HeavySapCactusBlock(Properties properties) {
        super(properties.randomTicks());
        registerDefaultState(stateDefinition.any().setValue(SAP_LEVEL, 0).setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SAP_LEVEL, AGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(Blocks.SAND) || below.is(Blocks.RED_SAND) || below.is(SandStormBlocks.SALINIZED_SAND) || below.is(this);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int sap = state.getValue(SAP_LEVEL);
        boolean isDay = level.getSkyDarken() < 4;
        if (sap < 3 && isDay && level.canSeeSky(pos.above())) {
            level.setBlockAndUpdate(pos, state.setValue(SAP_LEVEL, sap + 1));
        }

        BlockPos abovePos = pos.above();
        if (level.isEmptyBlock(abovePos)) {
            int height = 1;
            while (level.getBlockState(pos.below(height)).is(this)) {
                height++;
            }
            if (height < 3) {
                int age = state.getValue(AGE);
                if (age == 15) {
                    level.setBlockAndUpdate(abovePos, defaultBlockState());
                    level.setBlockAndUpdate(pos, state.setValue(AGE, 0));
                } else {
                    level.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
                }
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int sap = state.getValue(SAP_LEVEL);
        if (sap < 3) {
            return InteractionResult.PASS;
        }

        if (stack.getItem() instanceof SamplingSyringeItem) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, state.setValue(SAP_LEVEL, 0));
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                popResource(level, pos, new ItemStack(SandStormItems.HEAVY_SAP_BOTTLE));
                if (level.getRandom().nextBoolean()) {
                    popResource(level, pos, new ItemStack(SandStormItems.FLEXIBLE_BIOPOLYMER));
                }
                stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
            }
            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.GLASS_BOTTLE)) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, state.setValue(SAP_LEVEL, 0));
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                stack.shrink(1);
                ItemStack sapBottle = new ItemStack(SandStormItems.HEAVY_SAP_BOTTLE);
                if (!player.getInventory().add(sapBottle)) {
                    popResource(level, pos, sapBottle);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        entity.hurt(level.damageSources().cactus(), 1.0f);
    }
}
