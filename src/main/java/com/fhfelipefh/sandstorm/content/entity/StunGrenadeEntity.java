package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class StunGrenadeEntity extends ThrowableItemProjectile {

    public StunGrenadeEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public StunGrenadeEntity(Level level, LivingEntity shooter) {
        super(SandStormEntities.STUN_GRENADE, shooter, level, new ItemStack(SandStormItems.STUN_GRENADE));
    }

    @Override
    protected Item getDefaultItem() {
        return SandStormItems.STUN_GRENADE;
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        detonate();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        detonate();
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            detonate();
        }
    }

    private void detonate() {
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();

            serverLevel.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.2f, 1.8f);
            serverLevel.playSound(null, x, y, z, SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.0f, 2.0f);
            serverLevel.playSound(null, x, y, z, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 0.8f, 1.6f);

            serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, y + 0.2, z, 1, 0.0, 0.0, 0.0, 0.0);
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, x, y + 0.2, z, 1, 0.0, 0.0, 0.0, 0.0);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y + 0.2, z, 30, 0.5, 0.5, 0.5, 0.15);

            AABB effectBox = new AABB(x - 7.0, y - 7.0, z - 7.0, x + 7.0, y + 7.0, z + 7.0);
            List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, effectBox, LivingEntity::isAlive);

            for (LivingEntity target : targets) {
                double dist = target.distanceTo(this);
                if (dist <= 7.0) {
                    double factor = 1.0 - (dist / 7.0);
                    target.hurtServer(serverLevel, serverLevel.damageSources().generic(), (float) (4.0 * factor + 1.0));

                    if (target instanceof Player player) {
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, (int) (140 * factor) + 40, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, (int) (100 * factor) + 20, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, (int) (80 * factor) + 20, 2));
                    } else if (target instanceof Mob mob) {
                        mob.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, (int) (120 * factor) + 40, 3));
                        mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, (int) (120 * factor) + 40, 2));
                        mob.setTarget(null);
                        mob.getNavigation().stop();
                    }
                }
            }
            this.discard();
        }
    }
}
