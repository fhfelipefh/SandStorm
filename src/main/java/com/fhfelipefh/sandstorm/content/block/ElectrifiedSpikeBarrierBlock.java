package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SpaceSuitItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ElectrifiedSpikeBarrierBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ElectrifiedSpikeBarrierBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        applyShock(level, pos, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        applyShock(level, pos, entity);
    }

    private void applyShock(Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity living) {
            boolean fullyInsulated = living.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof SpaceSuitItem
                    && living.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof SpaceSuitItem
                    && living.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof SpaceSuitItem
                    && living.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof SpaceSuitItem;

            if (fullyInsulated) {
                living.hurtServer(serverLevel, serverLevel.damageSources().cactus(), 2.0f);
            } else {
                living.hurtServer(serverLevel, serverLevel.damageSources().lightningBolt(), 8.0f);
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 3));
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, living.getX(), living.getY() + 0.5, living.getZ(), 10, 0.3, 0.3, 0.3, 0.1);
                serverLevel.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 0.5f, 1.6f);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.2f) {
            double px = pos.getX() + random.nextDouble();
            double py = pos.getY() + random.nextDouble();
            double pz = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0.0, 0.05, 0.0);
        }
    }
}
