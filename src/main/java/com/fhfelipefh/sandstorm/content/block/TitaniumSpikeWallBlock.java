package com.fhfelipefh.sandstorm.content.block;

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
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class TitaniumSpikeWallBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    private static final VoxelShape COLLISION_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
    private static final VoxelShape OUTLINE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public TitaniumSpikeWallBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
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
            applySpikeDamage(level, pos, state, target);
        }
        level.scheduleTick(pos, this, 10);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        applySpikeDamage(level, pos, state, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        applySpikeDamage(level, pos, state, entity);
    }

    private void applySpikeDamage(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity living) {
            boolean hurt = living.hurtServer(serverLevel, serverLevel.damageSources().cactus(), 8.0f);
            if (hurt) {
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1));
                Vec3 pushVec = living.position().subtract(Vec3.atCenterOf(pos)).normalize().scale(0.35);
                living.push(pushVec.x, 0.15, pushVec.z);

                serverLevel.sendParticles(ParticleTypes.CRIT, living.getX(), living.getY() + living.getBbHeight() * 0.5, living.getZ(), 8, 0.25, 0.25, 0.25, 0.15);
                serverLevel.playSound(null, pos, SoundEvents.THORNS_HIT, SoundSource.BLOCKS, 1.0f, 0.9f);
                serverLevel.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.4f, 1.8f);
            }
        }
    }
}
