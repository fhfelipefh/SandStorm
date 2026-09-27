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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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

public class CorrosiveChitinSpikeWallBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    private static final VoxelShape COLLISION_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
    private static final VoxelShape OUTLINE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public CorrosiveChitinSpikeWallBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
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
            applyCorrosion(level, pos, target);
        }
        level.scheduleTick(pos, this, 10);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        applyCorrosion(level, pos, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        applyCorrosion(level, pos, entity);
    }

    private void applyCorrosion(Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity living) {
            boolean hurt = living.hurtServer(serverLevel, serverLevel.damageSources().cactus(), 6.0f);
            if (hurt) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));

                Vec3 pushVec = living.position().subtract(Vec3.atCenterOf(pos)).normalize().scale(0.3);
                living.push(pushVec.x, 0.1, pushVec.z);

                EquipmentSlot[] slots = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
                EquipmentSlot targetSlot = slots[serverLevel.getRandom().nextInt(slots.length)];
                ItemStack armor = living.getItemBySlot(targetSlot);
                if (!armor.isEmpty() && armor.isDamageableItem()) {
                    armor.hurtAndBreak(2, living, targetSlot);
                }

                serverLevel.sendParticles(ParticleTypes.ITEM_SLIME, living.getX(), living.getY() + living.getBbHeight() * 0.5, living.getZ(), 10, 0.3, 0.3, 0.3, 0.1);
                serverLevel.playSound(null, pos, SoundEvents.SLIME_BLOCK_HIT, SoundSource.BLOCKS, 0.8f, 1.2f);
                serverLevel.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.6f, 1.5f);
            }
        }
    }
}
