package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.MorphingMatrixCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

public class MorphingMatrixCoreBlock extends Block implements EntityBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty HOLOGRAM = BooleanProperty.create("hologram");

    public MorphingMatrixCoreBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, false)
                .setValue(HOLOGRAM, false));
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_CYAN)
                .strength(50.0f, 1200.0f)
                .sound(SoundType.HEAVY_CORE)
                .lightLevel(state -> 12)
                .requiresCorrectToolForDrops();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, HOLOGRAM);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MorphingMatrixCoreBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof MorphingMatrixCoreBlockEntity coreBe && lvl instanceof ServerLevel serverLevel) {
                    MorphingMatrixCoreBlockEntity.serverTick(serverLevel, pos, st, coreBe);
                }
            };
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof MorphingMatrixCoreBlockEntity coreBe) {
                MorphingMatrixCoreBlockEntity.clientTick(lvl, pos, st, coreBe);
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MorphingMatrixCoreBlockEntity coreBe) {
                if (player.isShiftKeyDown()) {
                    boolean nextHolo = !coreBe.isHologramActive();
                    coreBe.setHologramActive(nextHolo);
                    level.setBlock(pos, state.setValue(HOLOGRAM, nextHolo), 3);
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, nextHolo ? 1.5f : 0.8f);
                    Component msg = nextHolo
                            ? Component.translatable("message.sandstorm.morphing_core.hologram_on")
                            : Component.translatable("message.sandstorm.morphing_core.hologram_off");
                    player.sendSystemMessage(msg);
                } else {
                    int count = coreBe.scanAndSaveStructure(serverLevel);
                    level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1.0f, 1.2f);
                    Component msg = Component.translatable("message.sandstorm.morphing_core.saved", count, coreBe.getReserveBlocks());
                    player.sendSystemMessage(msg);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (!level.isClientSide()) {
            boolean hasSignal = level.hasNeighborSignal(pos);
            if (hasSignal != state.getValue(POWERED)) {
                level.setBlock(pos, state.setValue(POWERED, hasSignal), 3);
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof MorphingMatrixCoreBlockEntity coreBe) {
                    if (hasSignal) {
                        coreBe.startLiquefaction();
                    } else {
                        coreBe.startSolidification();
                    }
                }
            }
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MorphingMatrixCoreBlockEntity coreBe) {
            coreBe.dropStoredContents(level, pos);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
    }
}
