package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.CyborgIncubatorVatBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class CyborgIncubatorVatBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 4);

    public CyborgIncubatorVatBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, STAGE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, false)
                .setValue(STAGE, 0);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CyborgIncubatorVatBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof CyborgIncubatorVatBlockEntity incubator) {
                incubator.tickServer((ServerLevel) lvl, pos, st);
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CyborgIncubatorVatBlockEntity incubator) {
            player.openMenu(incubator);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CyborgIncubatorVatBlockEntity incubator) {
            Containers.dropContents(level, pos, incubator);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        boolean active = state.getValue(POWERED);
        int stage = state.getValue(STAGE);
        if (active || stage > 0) {
            double px = pos.getX() + 0.25 + random.nextDouble() * 0.50;
            double py = pos.getY() + 0.30 + random.nextDouble() * 0.50;
            double pz = pos.getZ() + 0.25 + random.nextDouble() * 0.50;

            level.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, px, py + 0.2, pz, 0.0, 0.02, 0.0);
            if (random.nextFloat() < 0.35f) {
                level.addParticle(ParticleTypes.BUBBLE_POP, px, py + 0.3, pz, 0.0, 0.03, 0.0);
            }
            if (active && random.nextFloat() < 0.25f) {
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py + 0.4, pz, 0.0, 0.01, 0.0);
            }
            if (active && random.nextFloat() < 0.08f) {
                level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.25f, 1.4f, false);
            }
        }
    }
}
