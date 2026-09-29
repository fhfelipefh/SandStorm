package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.ElectricFencePylonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ElectricFencePylonBlock extends Block implements EntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);

    public ElectricFencePylonBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, false)
                .setValue(CONNECTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, CONNECTED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricFencePylonBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof ElectricFencePylonBlockEntity pylon) {
                ElectricFencePylonBlockEntity.serverTick(lvl, pos, st, pylon);
            }
        };
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            if (state.getValue(CONNECTED)) {
                if (random.nextFloat() < 0.35f) {
                    double px = pos.getX() + 0.2 + random.nextDouble() * 0.6;
                    double py = pos.getY() + 0.2 + random.nextDouble() * 0.7;
                    double pz = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
                    level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0.0, 0.02, 0.0);
                }
                if (random.nextFloat() < 0.08f) {
                    level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.25f, 1.8f, false);
                }
            } else {
                if (random.nextFloat() < 0.10f) {
                    double px = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
                    double py = pos.getY() + 0.8 + random.nextDouble() * 0.2;
                    double pz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
                    level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0.0, 0.01, 0.0);
                }
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof ElectricFencePylonBlockEntity pylon) {
                    pylon.cycleMode();
                    Component modeName = switch (pylon.getMode()) {
                        case 0 -> Component.translatable("gui.sandstorm.electric_fence.mode_redstone");
                        case 1 -> Component.translatable("gui.sandstorm.electric_fence.mode_always_on");
                        case 2 -> Component.translatable("gui.sandstorm.electric_fence.mode_always_off");
                        default -> Component.empty();
                    };
                    player.sendSystemMessage(Component.translatable("gui.sandstorm.electric_fence.mode_switched", modeName));
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ElectricFencePylonBlockEntity pylon) {
                player.openMenu(pylon);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ElectricFencePylonBlockEntity pylon) {
            for (int i = 0; i < pylon.getContainerSize(); i++) {
                ItemStack item = pylon.getItem(i);
                if (!item.isEmpty()) {
                    popResource(level, pos, item);
                }
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
}
