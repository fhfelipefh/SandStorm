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
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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

public class PiezoShockTrapBlock extends Block {
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 2, 15);

    public PiezoShockTrapBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(CHARGED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && state.getValue(CHARGED)) {
            handleShockTrigger(level, pos, state, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (!level.isClientSide() && level.hasNeighborSignal(pos) && state.getValue(CHARGED)) {
            handleShockTrigger(level, pos, state, null);
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        handleShockTrigger(level, pos, state, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        handleShockTrigger(level, pos, state, entity);
    }

    private void handleShockTrigger(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!state.getValue(CHARGED)) {
            return;
        }

        level.setBlock(pos, state.setValue(CHARGED, false), 3);
        level.scheduleTick(pos, this, 100);
        level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 0.8f, 2.0f);
        level.playSound(null, pos, SoundEvents.TRIDENT_THUNDER.value(), SoundSource.BLOCKS, 0.8f, 1.5f);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 30, 0.4, 0.2, 0.4, 0.15);

            if (entity instanceof LivingEntity living) {
                boolean isMechanical = isMechanicalEntity(living);
                float damage = isMechanical ? 18.0f : 10.0f;
                int slowAmp = isMechanical ? 4 : 2;
                int slowDuration = isMechanical ? 100 : 60;

                living.hurtServer(serverLevel, serverLevel.damageSources().lightningBolt(), damage);
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, slowDuration, slowAmp));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, slowDuration, isMechanical ? 3 : 1));

                if (isMechanical && living instanceof Mob mob) {
                    mob.setTarget(null);
                    mob.getNavigation().stop();
                }

                dischargeChainedArcs(serverLevel, pos, living);
            }
        }
    }

    private void dischargeChainedArcs(ServerLevel serverLevel, BlockPos sourcePos, LivingEntity primaryTarget) {
        AABB searchBox = new AABB(sourcePos).inflate(5.0);
        List<LivingEntity> nearby = serverLevel.getEntitiesOfClass(LivingEntity.class, searchBox, target -> target != primaryTarget && target.isAlive());

        int chained = 0;
        for (LivingEntity target : nearby) {
            if (chained >= 3) {
                break;
            }
            target.hurtServer(serverLevel, serverLevel.damageSources().lightningBolt(), 6.0f);
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1));
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY() + 0.5, target.getZ(), 10, 0.2, 0.2, 0.2, 0.1);
            chained++;
        }
    }

    private boolean isMechanicalEntity(LivingEntity entity) {
        String name = entity.getType().getDescriptionId().toLowerCase();
        return name.contains("cyborg")
                || name.contains("sentinel")
                || name.contains("automaton")
                || name.contains("drone")
                || name.contains("hound")
                || name.contains("laborer");
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (!state.getValue(CHARGED)) {
            level.setBlock(pos, state.setValue(CHARGED, true), 3);
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.5f, 2.0f);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, 8, 0.2, 0.1, 0.2, 0.05);
        }
    }
}
