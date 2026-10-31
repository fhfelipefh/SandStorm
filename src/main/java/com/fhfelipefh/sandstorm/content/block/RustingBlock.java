package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class RustingBlock extends Block {
    public static final IntegerProperty RUST = IntegerProperty.create("rust", 0, 2);

    public RustingBlock(Properties properties) {
        super(properties.randomTicks());
        registerDefaultState(stateDefinition.any().setValue(RUST, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RUST);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int rust = state.getValue(RUST);
        if (rust < 2 && random.nextFloat() < 0.05688889f) {
            level.setBlock(pos, state.setValue(RUST, rust + 1), 3);
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(RUST) < 2;
    }

    public static int selectRuinRust(RandomSource random) {
        int roll = random.nextInt(100);
        return roll < 5 ? 0 : roll < 30 ? 1 : 2;
    }

    public static BlockState convertLegacy(BlockState state) {
        Block target;
        int rust;
        if (state.is(SandStormBlocks.WEATHERED_MILITARY_PLATE)) {
            target = SandStormBlocks.MILITARY_ARMOR_PLATE;
            rust = 1;
        } else if (state.is(SandStormBlocks.RUSTED_MILITARY_PLATE) || state.is(SandStormBlocks.FRACTURED_MILITARY_PLATE)) {
            target = SandStormBlocks.MILITARY_ARMOR_PLATE;
            rust = 2;
        } else if (state.is(SandStormBlocks.WEATHERED_HAZARD_STRIPE)) {
            target = SandStormBlocks.HAZARD_STRIPE_BLOCK;
            rust = 1;
        } else if (state.is(SandStormBlocks.CORRODED_HAZARD_STRIPE)) {
            target = SandStormBlocks.HAZARD_STRIPE_BLOCK;
            rust = 2;
        } else if (state.is(SandStormBlocks.RUSTED_VENT_DUCT) || state.is(SandStormBlocks.DAMAGED_VENT_DUCT)) {
            target = SandStormBlocks.REINFORCED_VENT_DUCT;
            rust = state.is(SandStormBlocks.RUSTED_VENT_DUCT) ? 1 : 2;
        } else if (state.is(SandStormBlocks.RUSTED_STEEL_VENT_GRATE) || state.is(SandStormBlocks.BROKEN_STEEL_VENT_GRATE)) {
            target = SandStormBlocks.STEEL_VENT_GRATE;
            rust = state.is(SandStormBlocks.RUSTED_STEEL_VENT_GRATE) ? 1 : 2;
        } else {
            return state;
        }
        return target.defaultBlockState().setValue(RUST, rust);
    }
}
