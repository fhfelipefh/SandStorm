package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
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

public class HalophytePlantBlock extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.box(4.0, 0.0, 4.0, 12.0, 4.0, 12.0),
            Block.box(3.0, 0.0, 3.0, 13.0, 7.0, 13.0),
            Block.box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0),
            Block.box(1.0, 0.0, 1.0, 15.0, 13.0, 15.0)
    };

    public HalophytePlantBlock(Properties properties) {
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
        return below.is(Blocks.SAND) || below.is(Blocks.RED_SAND) || below.is(SandStormBlocks.SALINIZED_SAND) || below.is(Blocks.DIRT);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < 3 && level.getMaxLocalRawBrightness(pos) >= 8) {
            int newAge = age + 1;
            level.setBlockAndUpdate(pos, state.setValue(AGE, newAge));
            if (newAge == 3) {
                BlockPos belowPos = pos.below();
                if (level.getBlockState(belowPos).is(SandStormBlocks.SALINIZED_SAND)) {
                    level.setBlockAndUpdate(belowPos, Blocks.SAND.defaultBlockState());
                }
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (age == 3) {
            if (!level.isClientSide()) {
                popResource(level, pos, new ItemStack(SandStormItems.MINERAL_SALT, 1 + level.getRandom().nextInt(2)));
                popResource(level, pos, new ItemStack(SandStormBlocks.HALOPHYTE_PLANT));
                level.setBlockAndUpdate(pos, state.setValue(AGE, 1));
                level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && (player == null || !player.getAbilities().instabuild)) {
            popResource(level, pos, new ItemStack(SandStormBlocks.HALOPHYTE_PLANT));
            if (state.getValue(AGE) == 3) {
                popResource(level, pos, new ItemStack(SandStormItems.MINERAL_SALT));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
