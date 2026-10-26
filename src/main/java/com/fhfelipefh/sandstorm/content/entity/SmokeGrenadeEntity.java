package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class SmokeGrenadeEntity extends ThrowableItemProjectile {

    public SmokeGrenadeEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public SmokeGrenadeEntity(Level level, LivingEntity shooter) {
        super(SandStormEntities.SMOKE_GRENADE, shooter, level, new ItemStack(SandStormItems.SMOKE_GRENADE));
    }

    @Override
    protected Item getDefaultItem() {
        return SandStormItems.SMOKE_GRENADE;
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

            serverLevel.playSound(null, x, y, z, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 0.6f);
            serverLevel.playSound(null, x, y, z, SoundEvents.SMOKER_SMOKE, SoundSource.PLAYERS, 1.2f, 0.8f);

            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 0.3, z, 50, 0.8, 0.4, 0.8, 0.05);
            serverLevel.sendParticles(ParticleTypes.POOF, x, y + 0.3, z, 30, 0.6, 0.3, 0.6, 0.08);

            AreaEffectCloud cloud = new AreaEffectCloud(serverLevel, x, y, z);
            cloud.setRadius(5.0f);
            cloud.setDuration(360);
            cloud.setRadiusOnUse(0.0f);
            cloud.setRadiusPerTick(-0.005f);
            cloud.setCustomParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE);
            cloud.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
            cloud.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
            serverLevel.addFreshEntity(cloud);

            AABB disruptBox = new AABB(x - 6.0, y - 4.0, z - 6.0, x + 6.0, y + 4.0, z + 6.0);
            List<Mob> hostiles = serverLevel.getEntitiesOfClass(Mob.class, disruptBox, Mob::isAlive);
            for (Mob mob : hostiles) {
                mob.setTarget(null);
                mob.getNavigation().stop();
            }

            this.discard();
        }
    }
}
