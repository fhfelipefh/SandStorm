package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class ChitinolyticFungusBlock extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.box(4.0, 0.0, 4.0, 12.0, 5.0, 12.0),
            Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0),
            Block.box(2.0, 0.0, 2.0, 14.0, 11.0, 14.0),
            Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0)
    };

    public ChitinolyticFungusBlock(Properties properties) {
        super(properties.randomTicks().noCollision());
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(AGE)];
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(Blocks.STONE)
                || below.is(Blocks.DEEPSLATE)
                || below.is(Blocks.SANDSTONE)
                || below.is(SandStormBlocks.BURIED_TECH_RUINS)
                || below.is(SandStormBlocks.ANCIENT_DATA_CORE);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < 3 && random.nextFloat() < 0.25f) {
            level.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (stack.is(SandStormItems.SANDWORM_CHITIN) && age < 3) {
            if (!level.isClientSide()) {
                if (player == null || !player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
                level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6, 0.25, 0.25, 0.25, 0.05);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (age == 3) {
            if (!level.isClientSide()) {
                popResource(level, pos, new ItemStack(SandStormItems.CHITOSAN_EXTRACT, 1 + level.getRandom().nextInt(2)));
                popResource(level, pos, new ItemStack(SandStormBlocks.CHITINOLYTIC_FUNGUS));
                level.setBlockAndUpdate(pos, state.setValue(AGE, 1));
                level.playSound(null, pos, SoundEvents.FUNGUS_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && (player == null || !player.getAbilities().instabuild)) {
            popResource(level, pos, new ItemStack(SandStormBlocks.CHITINOLYTIC_FUNGUS));
            if (state.getValue(AGE) == 3) {
                popResource(level, pos, new ItemStack(SandStormItems.CHITOSAN_EXTRACT));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
