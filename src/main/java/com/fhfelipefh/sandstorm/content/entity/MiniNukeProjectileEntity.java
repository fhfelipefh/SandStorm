package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MiniNukeProjectileEntity extends ThrowableItemProjectile {

    public MiniNukeProjectileEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MiniNukeProjectileEntity(Level level, LivingEntity shooter) {
        super(SandStormEntities.MINI_NUKE_PROJECTILE, shooter, level, new ItemStack(SandStormItems.MINI_NUKE));
    }

    @Override
    protected Item getDefaultItem() {
        return SandStormItems.MINI_NUKE;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY(), this.getZ(), 2, 0.08, 0.08, 0.08, 0.01);
            serverLevel.sendParticles(ParticleTypes.GLOW, this.getX(), this.getY(), this.getZ(), 1, 0.04, 0.04, 0.04, 0.01);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        detonate(hitResult.getLocation());
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        detonate(hitResult.getLocation());
    }

    private void detonate(Vec3 hitPos) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos centerPos = BlockPos.containing(hitPos);

        serverLevel.explode(this, hitPos.x, hitPos.y, hitPos.z, 14.0f, Level.ExplosionInteraction.BLOCK);

        AABB epicenterBox = new AABB(hitPos.x - 22.0, hitPos.y - 14.0, hitPos.z - 22.0, hitPos.x + 22.0, hitPos.y + 14.0, hitPos.z + 22.0);
        List<LivingEntity> victims = serverLevel.getEntitiesOfClass(LivingEntity.class, epicenterBox, LivingEntity::isAlive);
        for (LivingEntity victim : victims) {
            double dist = victim.position().distanceTo(hitPos);
            if (dist <= 6.0) {
                victim.hurtServer(serverLevel, this.damageSources().explosion(this, this.getOwner()), 1000.0f);
            } else if (dist <= 14.0) {
                victim.hurtServer(serverLevel, this.damageSources().explosion(this, this.getOwner()), 500.0f);
            } else if (dist <= 22.0) {
                float factor = (float) (1.0 - (dist - 14.0) / 8.0);
                victim.hurtServer(serverLevel, this.damageSources().explosion(this, this.getOwner()), Math.max(50.0f, 250.0f * factor));
            }
        }

        int radius = 5;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        BlockPos targetPos = centerPos.offset(x, y, z);
                        BlockState state = serverLevel.getBlockState(targetPos);
                        if (state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.SANDSTONE)) {
                            if (serverLevel.getRandom().nextFloat() < 0.65f) {
                                serverLevel.setBlock(targetPos, SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 3);
                            }
                        } else if (state.isAir() && serverLevel.getBlockState(targetPos.below()).isSolidRender()) {
                            if (serverLevel.getRandom().nextFloat() < 0.25f) {
                                serverLevel.setBlock(targetPos, SandStormBlocks.RADIOACTIVE_SLUDGE.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
        }

        AreaEffectCloud cloud = new AreaEffectCloud(serverLevel, hitPos.x, hitPos.y, hitPos.z);
        cloud.setRadius(12.0f);
        cloud.setDuration(1200);
        cloud.setRadiusOnUse(0.0f);
        cloud.setRadiusPerTick(-0.008f);
        cloud.setCustomParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE);
        cloud.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 3));
        cloud.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 2));
        cloud.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 2));
        cloud.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 1));
        serverLevel.addFreshEntity(cloud);

        serverLevel.playSound(null, centerPos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 6.0f, 0.4f);
        serverLevel.playSound(null, centerPos, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 4.0f, 0.5f);
        serverLevel.playSound(null, centerPos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 5.0f, 0.6f);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, hitPos.x, hitPos.y + 0.5, hitPos.z, 8, 1.2, 1.2, 1.2, 0.0);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION, hitPos.x, hitPos.y + 1.0, hitPos.z, 4, 0.8, 0.8, 0.8, 0.0);
        serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, hitPos.x, hitPos.y + 1.0, hitPos.z, 160, 4.5, 3.5, 4.5, 0.12);
        serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, hitPos.x, hitPos.y + 1.0, hitPos.z, 2, 0.0, 0.0, 0.0, 0.0);

        this.discard();
    }
}
