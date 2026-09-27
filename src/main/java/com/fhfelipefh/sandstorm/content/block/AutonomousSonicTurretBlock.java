package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.AutonomousSonicTurretBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AutonomousSonicTurretBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final VoxelShape BASE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 6.0, 15.0);
    private static final VoxelShape TURRET_SHAPE = Block.box(3.0, 6.0, 3.0, 13.0, 15.0, 13.0);
    private static final VoxelShape COMPLETE_SHAPE = Shapes.or(BASE_SHAPE, TURRET_SHAPE);

    public AutonomousSonicTurretBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COMPLETE_SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AutonomousSonicTurretBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof AutonomousSonicTurretBlockEntity turret) {
                    turret.clientTick(lvl, pos, st);
                }
            };
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof AutonomousSonicTurretBlockEntity turret) {
                turret.serverTick(lvl, pos, st);
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AutonomousSonicTurretBlockEntity turret) {
                player.openMenu(turret);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AutonomousSonicTurretBlockEntity turret && turret.getEnergy() >= AutonomousSonicTurretBlockEntity.ENERGY_PER_SHOT) {
            if (random.nextFloat() < 0.15f) {
                double px = pos.getX() + 0.35 + random.nextDouble() * 0.3;
                double py = pos.getY() + 0.5 + random.nextDouble() * 0.4;
                double pz = pos.getZ() + 0.35 + random.nextDouble() * 0.3;
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0.0, 0.01, 0.0);
            }
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AutonomousSonicTurretBlockEntity turret) {
            for (int i = 0; i < turret.getContainerSize(); i++) {
                ItemStack item = turret.getItem(i);
                if (!item.isEmpty()) {
                    popResource(level, pos, item);
                }
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
}
