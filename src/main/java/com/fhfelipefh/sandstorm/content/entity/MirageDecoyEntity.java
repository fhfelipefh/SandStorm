package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class MirageDecoyEntity extends PathfinderMob {
    private int lifespanTicks = 0;

    public MirageDecoyEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.38)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.2));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        Level lvl = level();
        if (lvl instanceof ServerLevel serverLevel) {
            lifespanTicks++;
            if (lifespanTicks % 2 == 0) {
                serverLevel.sendParticles(ParticleTypes.PORTAL, getX(), getY() + 0.9, getZ(), 2, 0.2, 0.4, 0.2, 0.05);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 0.8, getZ(), 1, 0.15, 0.3, 0.15, 0.02);
            }
            if (lifespanTicks > 140) {
                lvl.playSound(null, blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 0.8f, 1.8f);
                serverLevel.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.9, getZ(), 15, 0.3, 0.5, 0.3, 0.05);
                discard();
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        level.playSound(null, blockPosition(), SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.NEUTRAL, 1.0f, 1.6f);
        level.sendParticles(ParticleTypes.WAX_OFF, getX(), getY() + 1.0, getZ(), 12, 0.2, 0.3, 0.2, 0.1);
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
