package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public class SandMaglevRailBlock extends RailBlock {
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;
    private static final double MIN_SPEED = 0.75;
    private static final double MAX_SPEED = 1.25;
    private static final double ACCELERATION_FACTOR = 1.45;

    public SandMaglevRailBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(WATERLOGGED, false));
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, WATERLOGGED);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        boostEntity(level, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        boostEntity(level, entity);
    }

    private void boostEntity(Level level, Entity entity) {
        if (level.isClientSide()) {
            return;
        }

        Vec3 motion = entity.getDeltaMovement();
        double hDistSqr = motion.x * motion.x + motion.z * motion.z;
        if (hDistSqr > 0.00005) {
            double currentSpeed = Math.sqrt(hDistSqr);
            double targetSpeed = Math.min(MAX_SPEED, Math.max(MIN_SPEED, currentSpeed * ACCELERATION_FACTOR));
            double factor = targetSpeed / currentSpeed;
            entity.setDeltaMovement(motion.x * factor, motion.y, motion.z * factor);
        } else if (entity.isVehicle()) {
            Entity passenger = entity.getFirstPassenger();
            if (passenger != null) {
                float yRot = passenger.getYRot();
                double rad = Math.toRadians(yRot);
                double vx = -Math.sin(rad) * MIN_SPEED;
                double vz = Math.cos(rad) * MIN_SPEED;
                entity.setDeltaMovement(vx, motion.y, vz);
            }
        }
    }
}
