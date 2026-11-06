package com.fhfelipefh.sandstorm.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin {
    @Shadow
    protected abstract void doPlayerRide(Player player);

    @Inject(method = "createBaseHorseAttributes", at = @At("RETURN"), cancellable = true)
    private static void sandstorm$doubleHorseFallResistance(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue()
                .add(Attributes.SAFE_FALL_DISTANCE, 12.0)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0.25);
    }

    @Redirect(
            method = "causeFallDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/equine/AbstractHorse;propagateFallToPassengers(DFLnet/minecraft/world/damagesource/DamageSource;)V"
            )
    )
    private void sandstorm$propagateReducedFallToPassengers(AbstractHorse horse, double fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (horse.isVehicle()) {
            double effectiveDistance = Math.max(0.0, fallDistance - horse.getAttributeValue(Attributes.SAFE_FALL_DISTANCE));
            float riderMultiplier = damageMultiplier * 0.5f;
            for (Entity passenger : horse.getPassengers()) {
                passenger.causeFallDamage(effectiveDistance, riderMultiplier, damageSource);
            }
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void sandstorm$priorityMountWhenRushing(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        AbstractHorse horse = (AbstractHorse) (Object) this;
        if (hand == InteractionHand.MAIN_HAND && horse.isTamed() && !horse.isBaby() && !horse.isVehicle()) {
            if (player.isSprinting()) {
                this.doPlayerRide(player);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Redirect(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;isSecondaryUseActive()Z"
            )
    )
    private boolean sandstorm$suppressInventoryScreenWhenMounting(Player player) {
        AbstractHorse horse = (AbstractHorse) (Object) this;
        if (player.isSprinting()) {
            return false;
        }
        ItemStack main = player.getMainHandItem();
        if (!main.isEmpty() && !horse.isEquippableInSlot(main, EquipmentSlot.BODY)) {
            return false;
        }
        return player.isSecondaryUseActive();
    }
}
