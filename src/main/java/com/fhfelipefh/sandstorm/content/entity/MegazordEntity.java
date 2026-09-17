package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MegazordEntity extends PathfinderMob {
    public static final long DEFAULT_BATTERY_CAPACITY = 100000L;
    public static final double SHOCKWAVE_RADIUS = 16.0;

    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(DEFAULT_BATTERY_CAPACITY, 1000L, 1000L);

    public MegazordEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        energyStorage.setStoredEnergy(DEFAULT_BATTERY_CAPACITY);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.ARMOR, 25.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_DAMAGE, 30.0)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public float getSeismicVibrationOutput() {
        return 6.0f;
    }

    public boolean triggerSonicShockwave() {
        if (!energyStorage.hasEnergy(2000L)) {
            return false;
        }

        energyStorage.extractEnergy(2000L);
        Level level = this.level();

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            AABB area = this.getBoundingBox().inflate(SHOCKWAVE_RADIUS);
            List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, area, entity ->
                    entity != this && !(entity instanceof Player) && (entity instanceof Enemy || entity instanceof SandwormEntity)
            );

            for (LivingEntity target : targets) {
                target.hurtServer(serverLevel, this.damageSources().sonicBoom(this), 25.0f);
                Vec3 push = target.position().subtract(this.position()).normalize().scale(1.8);
                target.setDeltaMovement(push.x, 0.5, push.z);
            }

            serverLevel.playSound(null, this.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.5f, 1.0f);
        }

        return true;
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN)) {
            return true;
        }
        return super.isInvulnerableTo(level, source);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        if (!this.isVehicle()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable(
                            "telemetry.sandstorm.megazord_cockpit",
                            energyStorage.getStoredEnergy(),
                            energyStorage.getCapacity()
                    ), true);
                }
                this.level().playSound(null, this.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 0.7f);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }
}
