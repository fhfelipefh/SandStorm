package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.gui.NomadScavengerMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.network.MagneticInterferencePayload;
import java.util.EnumSet;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NomadScavengerEntity extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> FLEEING =
            SynchedEntityData.defineId(NomadScavengerEntity.class, EntityDataSerializers.BOOLEAN);

    public NomadScavengerEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLEEING, false);
    }

    public boolean isFleeing() {
        return this.entityData.get(FLEEING);
    }

    public void setFleeing(boolean fleeing) {
        this.entityData.set(FLEEING, fleeing);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new NomadScavengerFleeGoal(this, 1.45));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    public static boolean isDawnOrDusk(Level level) {
        long time = Math.abs(level.getDefaultClockTime()) % 24000L;
        return (time >= 22500L || time <= 2000L) || (time >= 11500L && time <= 14000L);
    }

    public static boolean checkNomadScavengerSpawnRules(
            EntityType<? extends PathfinderMob> type,
            ServerLevelAccessor level,
            EntitySpawnReason reason,
            BlockPos pos,
            RandomSource random
    ) {
        return isDawnOrDusk(level.getLevel()) && level.getBlockState(pos.below()).is(BlockTags.SAND);
    }

    private Player tradingPlayer;

    public boolean isTrading() {
        return this.tradingPlayer != null;
    }

    public void setTradingPlayer(Player player) {
        this.tradingPlayer = player;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.tradingPlayer != null) {
            if (this.tradingPlayer.isDeadOrDying()
                    || this.distanceToSqr(this.tradingPlayer) > 64.0
                    || !(this.tradingPlayer.containerMenu instanceof NomadScavengerMenu)) {
                this.tradingPlayer = null;
            } else {
                this.getLookControl().setLookAt(this.tradingPlayer, 30.0f, 30.0f);
            }
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            this.setTradingPlayer(serverPlayer);
            this.setFleeing(false);
            this.getNavigation().stop();
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, playerInv, p) -> new NomadScavengerMenu(containerId, playerInv),
                    Component.translatable("gui.sandstorm.nomad_scavenger")
            ));
            this.playSound(SoundEvents.VILLAGER_TRADE, 1.0f, 0.8f);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean hurt = super.hurtServer(level, source, amount);
        if (hurt) {
            triggerMagneticEscape(level);
        }
        return hurt;
    }

    private void triggerMagneticEscape(ServerLevel level) {
        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.HOSTILE, 1.5f, 0.8f);
        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 0.8f, 1.8f);

        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY() + 0.8, this.getZ(), 50, 0.8, 0.8, 0.8, 0.2);
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 30, 0.5, 0.5, 0.5, 0.05);
        level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.2, this.getZ(), 25, 0.5, 0.3, 0.5, 0.05);

        AABB area = this.getBoundingBox().inflate(16.0);
        List<ServerPlayer> players = level.getEntitiesOfClass(ServerPlayer.class, area);
        for (ServerPlayer player : players) {
            ServerPlayNetworking.send(player, new MagneticInterferencePayload(140));
        }

        if (this.isAlive()) {
            this.discard();
        } else {
            dropPenalizedLoot(level);
        }
    }

    private void dropPenalizedLoot(ServerLevel level) {
        int wires = 1 + this.random.nextInt(3);
        int scrap = 2 + this.random.nextInt(3);
        this.spawnAtLocation(level, new ItemStack(SandStormItems.LOOSE_WIRES, wires));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.WORTHLESS_SCRAP, scrap));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.IRON_GOLEM_STEP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    private static class NomadScavengerFleeGoal extends Goal {
        private final NomadScavengerEntity mob;
        private final double speedModifier;
        private Player targetPlayer;

        public NomadScavengerFleeGoal(NomadScavengerEntity mob, double speedModifier) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.mob.isTrading()) {
                return false;
            }

            Player nearest = this.mob.level().getNearestPlayer(this.mob, 12.0);
            if (nearest == null || nearest.isSpectator() || nearest.isCreative()) {
                return false;
            }

            if (nearest.isCrouching()) {
                return false;
            }

            boolean isSprinting = nearest.isSprinting();
            boolean movingCloser = nearest.distanceToSqr(this.mob) < 64.0;
            if (isSprinting || movingCloser || this.mob.isFleeing()) {
                this.targetPlayer = nearest;
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            this.mob.setFleeing(true);
            if (this.targetPlayer != null) {
                Vec3 fleePos = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.targetPlayer.position());
                if (fleePos != null) {
                    this.mob.getNavigation().moveTo(fleePos.x, fleePos.y, fleePos.z, this.speedModifier);
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            if (this.mob.isTrading()) {
                return false;
            }
            return !this.mob.getNavigation().isDone() && this.targetPlayer != null && this.targetPlayer.isAlive() && !this.targetPlayer.isCrouching();
        }

        @Override
        public void stop() {
            this.targetPlayer = null;
            this.mob.setFleeing(false);
        }
    }
}
