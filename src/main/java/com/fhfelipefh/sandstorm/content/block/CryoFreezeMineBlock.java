package com.fhfelipefh.sandstorm.content.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CryoFreezeMineBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 2, 14);

    public CryoFreezeMineBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && !state.getValue(ACTIVE)) {
            triggerCryoDetonation(level, pos, state);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (!level.isClientSide() && level.hasNeighborSignal(pos) && !state.getValue(ACTIVE)) {
            triggerCryoDetonation(level, pos, state);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (entity instanceof LivingEntity && !state.getValue(ACTIVE)) {
            triggerCryoDetonation(level, pos, state);
        }
    }

    public void triggerCryoDetonation(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(ACTIVE, true), 3);
        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 1.6f);
        level.playSound(null, pos, SoundEvents.POWDER_SNOW_FALL, SoundSource.BLOCKS, 1.2f, 0.8f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 30, 0.4, 0.2, 0.4, 0.15);
            serverLevel.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 10, 0.2, 0.1, 0.2, 0.05);

            AABB freezeArea = new AABB(pos).inflate(4.5);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, freezeArea);
            for (LivingEntity target : targets) {
                target.setTicksFrozen(400);
                target.hurtServer(serverLevel, level.damageSources().freeze(), 8.0f);
                target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 4, false, false, false));
            }
        }

        level.scheduleTick(pos, this, 60);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.3f) {
            double x = pos.getX() + 0.2 + (random.nextDouble() * 0.6);
            double y = pos.getY() + 0.15;
            double z = pos.getZ() + 0.2 + (random.nextDouble() * 0.6);
            level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0.0, 0.02, 0.0);
        }
    }
}
