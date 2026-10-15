package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MaglevConveyorBlock extends Block {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

    public MaglevConveyorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);

        Direction facing = state.getValue(FACING);
        double boost = entity instanceof ItemEntity ? 0.28 : 0.38;
        Vec3 current = entity.getDeltaMovement();

        double newX = facing.getAxis() == Direction.Axis.X ? facing.getStepX() * boost : current.x * 0.8;
        double newZ = facing.getAxis() == Direction.Axis.Z ? facing.getStepZ() * boost : current.z * 0.8;

        entity.setDeltaMovement(newX, current.y, newZ);

        if (!level.isClientSide() && entity instanceof ItemEntity itemEntity) {
            BlockPos targetPos = pos.relative(facing);
            BlockEntity targetBe = level.getBlockEntity(targetPos);

            if (targetBe instanceof Container container) {
                ItemStack stack = itemEntity.getItem();
                ItemStack remaining = insertIntoContainer(container, stack);

                if (remaining.isEmpty()) {
                    itemEntity.discard();
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4f, 1.4f);
                } else if (remaining.getCount() != stack.getCount()) {
                    itemEntity.setItem(remaining);
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.25f) {
            Direction facing = state.getValue(FACING);
            double px = pos.getX() + 0.2 + random.nextDouble() * 0.6;
            double py = pos.getY() + 0.9;
            double pz = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz,
                    facing.getStepX() * 0.1, 0.02, facing.getStepZ() * 0.1);
        }
    }

    private static ItemStack insertIntoContainer(Container container, ItemStack stack) {
        ItemStack toInsert = stack.copy();

        for (int i = 0; i < container.getContainerSize(); i++) {
            if (!container.canPlaceItem(i, toInsert)) {
                continue;
            }

            ItemStack slotStack = container.getItem(i);
            if (slotStack.isEmpty()) {
                container.setItem(i, toInsert);
                container.setChanged();
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameComponents(slotStack, toInsert)) {
                int maxCount = Math.min(container.getMaxStackSize(), slotStack.getMaxStackSize());
                int space = maxCount - slotStack.getCount();
                if (space > 0) {
                    int add = Math.min(space, toInsert.getCount());
                    slotStack.grow(add);
                    toInsert.shrink(add);
                    container.setChanged();
                    if (toInsert.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        return toInsert;
    }
}
