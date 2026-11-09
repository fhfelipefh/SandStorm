package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class FiberglassSpearEntity extends ThrowableItemProjectile {

    public FiberglassSpearEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FiberglassSpearEntity(Level level, LivingEntity shooter) {
        super(SandStormEntities.FIBERGLASS_SPEAR, shooter, level, new ItemStack(SandStormItems.FIBERGLASS_SPEAR));
    }

    @Override
    protected Item getDefaultItem() {
        return SandStormItems.FIBERGLASS_SPEAR;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity entity = hitResult.getEntity();
        if (this.level() instanceof ServerLevel serverLevel) {
            entity.hurtServer(serverLevel, this.damageSources().thrown(this, this.getOwner()), 24.0f);
            serverLevel.playSound(null, this.blockPosition(), SandStormSoundEvents.FIBERGLASS_SPEAR_SHATTER, SoundSource.PLAYERS, 1.2f, 1.1f);
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GLASS.defaultBlockState()), this.getX(), this.getY(), this.getZ(), 35, 0.3, 0.3, 0.3, 0.15);
            serverLevel.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 20, 0.3, 0.3, 0.3, 0.2);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, hitResult.getBlockPos(), SandStormSoundEvents.FIBERGLASS_SPEAR_SHATTER, SoundSource.PLAYERS, 1.2f, 1.1f);
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GLASS.defaultBlockState()), this.getX(), this.getY(), this.getZ(), 25, 0.2, 0.2, 0.2, 0.1);
            this.discard();
        }
    }
}

