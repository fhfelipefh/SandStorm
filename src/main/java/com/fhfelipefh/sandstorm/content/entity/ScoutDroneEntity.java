package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ScoutDroneEntity extends PathfinderMob implements Enemy, RangedAttackMob {

    public ScoutDroneEntity(EntityType<? extends ScoutDroneEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0, 30, 45, 14.0f));
        this.goalSelector.addGoal(2, new DesertExplorationWanderGoal(this, 0.95));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        Level currentLevel = this.level();
        if (currentLevel instanceof ServerLevel serverLevel) {
            Vec3 start = this.getEyePosition();
            Vec3 end = target.getEyePosition();
            Vec3 diff = end.subtract(start);
            int steps = (int) Math.max(3.0, diff.length() * 2.0);

            for (int i = 0; i <= steps; i++) {
                double t = i / (double) steps;
                Vec3 point = start.add(diff.scale(t));
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, point.x, point.y, point.z, 1, 0.0, 0.0, 0.0, 0.02);
            }

            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SandStormSoundEvents.PLASMA_RIFLE_FIRE, SoundSource.HOSTILE, 1.0f, 1.8f);

            target.hurtServer(serverLevel, damageSources().mobAttack(this), 4.5f);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(serverLevel, damageSource, recentlyHit);
        int scrapCount = 1 + this.random.nextInt(2);
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SCRAP_METAL, scrapCount));
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.LOOSE_WIRES, 1 + this.random.nextInt(2)));
        if (this.random.nextFloat() < 0.25f) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.CIRCUIT_BOARD, 1));
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }
}
