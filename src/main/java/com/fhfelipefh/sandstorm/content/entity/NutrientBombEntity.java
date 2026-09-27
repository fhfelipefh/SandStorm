package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.world.NutrientTerraformingManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class NutrientBombEntity extends ThrowableItemProjectile {

    public NutrientBombEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public NutrientBombEntity(Level level, LivingEntity shooter) {
        super(SandStormEntities.NUTRIENT_BOMB, shooter, level, new ItemStack(SandStormItems.NUTRIENT_BOMB));
    }

    @Override
    protected Item getDefaultItem() {
        return SandStormItems.NUTRIENT_BOMB;
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0f, 1.2f);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.CHORUS_FLOWER_GROW, SoundSource.NEUTRAL, 1.0f, 0.8f);
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 0.1, this.getZ(), 24, 0.5, 0.3, 0.5, 0.1);
            serverLevel.sendParticles(ParticleTypes.COMPOSTER, this.getX(), this.getY() + 0.1, this.getZ(), 16, 0.4, 0.2, 0.4, 0.05);
            NutrientTerraformingManager.inoculateArea(serverLevel, hitResult.getBlockPos(), 4);
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }
}
