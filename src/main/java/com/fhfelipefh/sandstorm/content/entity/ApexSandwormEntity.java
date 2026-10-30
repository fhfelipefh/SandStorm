package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class ApexSandwormEntity extends SandwormEntity {
    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
            UUID.randomUUID(),
            Component.translatable("entity.sandstorm.apex_sandworm"),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.NOTCHED_10
    ).setDarkenScreen(true).setCreateWorldFog(true);

    private int phase = 1;
    private int abilityCooldown = 0;

    public ApexSandwormEntity(EntityType<? extends ApexSandwormEntity> entityType, Level level) {
        super(entityType, level);
        this.setWormSize(4, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600.0)
                .add(Attributes.MOVEMENT_SPEED, 0.42)
                .add(Attributes.ATTACK_DAMAGE, 26.0)
                .add(Attributes.ARMOR, 18.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        float healthPct = this.getHealth() / this.getMaxHealth();
        if (healthPct <= 0.33f) {
            this.phase = 3;
            this.bossEvent.setColor(BossEvent.BossBarColor.RED);
        } else if (healthPct <= 0.66f) {
            this.phase = 2;
            this.bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
        } else {
            this.phase = 1;
            this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
        }

        if (this.abilityCooldown > 0) {
            this.abilityCooldown--;
        } else {
            executePhaseAbility();
        }
    }

    private void executePhaseAbility() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.phase == 1) {
            this.abilityCooldown = 160;
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, this.getX(), this.getY() + 1.5, this.getZ(), 2, 0.0, 0.0, 0.0, 0.0);
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 2.0f, 0.9f);

            AABB sonicBox = this.getBoundingBox().inflate(12.0);
            List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, sonicBox, e -> e != this && e.isAlive());
            for (LivingEntity target : targets) {
                target.hurtServer(serverLevel, this.damageSources().mobAttack(this), 18.0f);
            }
        } else if (this.phase == 2) {
            this.abilityCooldown = 120;
            serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 1.0, this.getZ(), 40, 1.2, 0.8, 1.2, 0.1);
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.HOSTILE, 2.0f, 0.8f);

            AABB blastBox = this.getBoundingBox().inflate(16.0);
            List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, blastBox, e -> e != this && e.isAlive());
            for (LivingEntity target : targets) {
                target.hurtServer(serverLevel, this.damageSources().mobAttack(this), 14.0f);
                Vec3 pushDir = target.position().subtract(this.position()).normalize().scale(1.2);
                target.push(pushDir.x, 0.4, pushDir.z);
            }
        } else if (this.phase == 3) {
            this.abilityCooldown = 100;
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 1.0, this.getZ(), 3, 1.0, 1.0, 1.0, 0.0);
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0f, 0.6f);

            AABB pullBox = this.getBoundingBox().inflate(24.0);
            List<LivingEntity> victims = serverLevel.getEntitiesOfClass(LivingEntity.class, pullBox, e -> e != this && e.isAlive());
            for (LivingEntity victim : victims) {
                Vec3 pullDir = this.position().subtract(victim.position()).normalize().scale(0.85);
                victim.setDeltaMovement(victim.getDeltaMovement().add(pullDir));
                victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2));
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (this.phase == 2 && !damageSource.is(DamageTypeTags.BYPASSES_ARMOR)) {
            level.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 2.0, this.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
            return super.hurtServer(level, damageSource, amount * 0.45f);
        }
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(SandStormItems.APEX_HEART_MATRIX, 1));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.APEX_RAILGUN_HARPOON, 1));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 8 + this.getRandom().nextInt(6)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.SANDWORM_TOOTH, 4 + this.getRandom().nextInt(4)));
    }
}
