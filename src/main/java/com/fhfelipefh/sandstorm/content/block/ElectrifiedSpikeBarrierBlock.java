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
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ElectrifiedSpikeBarrierBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final VoxelShape COLLISION_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
    private static final VoxelShape OUTLINE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

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
        return OUTLINE_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.scheduleTick(pos, this, 10);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        AABB damageBox = new AABB(pos).inflate(0.15);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, damageBox);
        for (LivingEntity target : targets) {
            applyShock(level, pos, state, target);
        }
        level.scheduleTick(pos, this, 10);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        applyShock(level, pos, state, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        applyShock(level, pos, state, entity);
    }

    private void applyShock(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity living) {
            boolean isElectrified = state.getValue(POWERED);
            if (isElectrified) {
                boolean fullyInsulated = living.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof SpaceSuitItem
                        && living.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof SpaceSuitItem
                        && living.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof SpaceSuitItem
                        && living.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof SpaceSuitItem;

                float damage = fullyInsulated ? 2.0f : 10.0f;
                boolean hurt = living.hurtServer(serverLevel, serverLevel.damageSources().lightningBolt(), damage);
                if (hurt) {
                    living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 2));

                    Vec3 pushVec = living.position().subtract(Vec3.atCenterOf(pos)).normalize().scale(0.5);
                    living.push(pushVec.x, 0.2, pushVec.z);

                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, living.getX(), living.getY() + living.getBbHeight() * 0.5, living.getZ(), 12, 0.35, 0.35, 0.35, 0.15);
                    serverLevel.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 0.7f, 1.7f);
                    serverLevel.playSound(null, pos, SoundEvents.SHIELD_BLOCK.value(), SoundSource.BLOCKS, 0.8f, 1.4f);
                }
            } else {
                boolean hurt = living.hurtServer(serverLevel, serverLevel.damageSources().cactus(), 4.0f);
                if (hurt) {
                    Vec3 pushVec = living.position().subtract(Vec3.atCenterOf(pos)).normalize().scale(0.25);
                    living.push(pushVec.x, 0.1, pushVec.z);
                    serverLevel.playSound(null, pos, SoundEvents.THORNS_HIT, SoundSource.BLOCKS, 0.8f, 1.0f);
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED) && random.nextFloat() < 0.35f) {
            double px = pos.getX() + random.nextDouble();
            double py = pos.getY() + random.nextDouble();
            double pz = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0.0, 0.05, 0.0);
        }
    }
}
