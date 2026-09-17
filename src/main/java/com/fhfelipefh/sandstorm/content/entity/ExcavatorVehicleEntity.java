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
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ExcavatorVehicleEntity extends PathfinderMob {
    public static final long DEFAULT_BATTERY_CAPACITY = 50000L;

    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(DEFAULT_BATTERY_CAPACITY, 500L, 500L);

    public ExcavatorVehicleEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        energyStorage.setStoredEnergy(DEFAULT_BATTERY_CAPACITY);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 120.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ARMOR, 16.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public float getSeismicVibrationOutput() {
        return 4.0f;
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FALL)) {
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
                            "telemetry.sandstorm.excavator_mounted",
                            energyStorage.getStoredEnergy(),
                            energyStorage.getCapacity()
                    ), true);
                }
                this.level().playSound(null, this.blockPosition(), SoundEvents.MINECART_RIDING, SoundSource.PLAYERS, 0.8f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }
}
