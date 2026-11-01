package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.SandwormState;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ParalyticToxinDartEntity extends ThrowableItemProjectile {

    public ParalyticToxinDartEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ParalyticToxinDartEntity(Level level, LivingEntity shooter) {
        super(SandStormEntities.PARALYTIC_TOXIN_DART, shooter, level, new ItemStack(SandStormItems.PARALYTIC_TOXIN_DART));
    }

    @Override
    protected Item getDefaultItem() {
        return SandStormItems.PARALYTIC_TOXIN_DART;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity entity = hitResult.getEntity();
        if (this.level() instanceof ServerLevel serverLevel) {
            entity.hurtServer(serverLevel, this.damageSources().thrown(this, this.getOwner()), 14.0f);
            if (entity instanceof SandwormEntity sandworm) {
                sandworm.setSandwormState(SandwormState.SURFACED_ASSAULT);
                sandworm.resetSurfaceTimer();
                sandworm.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 240, 4));
                sandworm.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 240, 3));
                sandworm.setDeltaMovement(0.0, 0.0, 0.0);
                serverLevel.playSound(null, sandworm.blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1.2f, 1.6f);
                serverLevel.playSound(null, sandworm.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1.4f, 1.5f);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, sandworm.getX(), sandworm.getY() + 1.5, sandworm.getZ(), 40, 0.8, 0.8, 0.8, 0.2);
                serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, sandworm.getX(), sandworm.getY() + 1.0, sandworm.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            } else if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 160, 3));
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 160, 1));
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 hitVec = hitResult.getLocation();
            AABB resonanceBox = new AABB(hitVec.x - 16.0, hitVec.y - 10.0, hitVec.z - 16.0, hitVec.x + 16.0, hitVec.y + 10.0, hitVec.z + 16.0);
            List<SandwormEntity> worms = serverLevel.getEntitiesOfClass(SandwormEntity.class, resonanceBox, LivingEntity::isAlive);
            for (SandwormEntity worm : worms) {
                worm.setSandwormState(SandwormState.SURFACED_ASSAULT);
                worm.resetSurfaceTimer();
                worm.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 240, 4));
                worm.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 240, 3));
                worm.triggerBreachShockwave();
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, worm.getX(), worm.getY() + 1.0, worm.getZ(), 30, 0.6, 0.6, 0.6, 0.2);
            }
            serverLevel.playSound(null, BlockPos.containing(hitVec), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.2f, 1.9f);
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, hitVec.x, hitVec.y + 0.2, hitVec.z, 1, 0.0, 0.0, 0.0, 0.0);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, hitVec.x, hitVec.y + 0.2, hitVec.z, 20, 0.4, 0.4, 0.4, 0.1);
            this.discard();
        }
    }
}
