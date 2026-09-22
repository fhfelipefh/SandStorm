package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SandboardEntity extends PathfinderMob {

    public SandboardEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ARMOR, 2.0);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5f);
        this.yRotO = this.getYRot();
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.yBodyRot;
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return new Vec3(player.xxa * 0.5f, 0.0, player.zza > 0 ? player.zza : player.zza * 0.3f);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        BlockState stateOn = this.getBlockStateOn();
        boolean onSand = stateOn.is(BlockTags.SAND) || stateOn.getBlock() instanceof SandBlock;
        float baseSpeed = onSand ? 0.35f : 0.15f;
        double verticalDelta = this.getY() - this.yo;
        if (onSand && verticalDelta < -0.05) {
            float boost = (float) Math.min(0.55, Math.abs(verticalDelta) * 3.0);
            return baseSpeed + boost;
        } else if (onSand && verticalDelta > 0.02) {
            return Math.max(0.08f, baseSpeed - (float) verticalDelta * 2.0f);
        }
        return baseSpeed;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isVehicle() && this.level().isClientSide()) {
            BlockState stateOn = this.getBlockStateOn();
            boolean onSand = stateOn.is(BlockTags.SAND) || stateOn.getBlock() instanceof SandBlock;
            double speedSqr = this.getDeltaMovement().horizontalDistanceSqr();
            if (onSand && speedSqr > 0.05) {
                double px = this.getX() + (this.random.nextDouble() - 0.5) * 0.6;
                double py = this.getY() + 0.05;
                double pz = this.getZ() + (this.random.nextDouble() - 0.5) * 0.6;
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, stateOn), px, py, pz, 0.0, 0.1, 0.0);
            }
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!this.level().isClientSide() && this.isAlive()) {
            ItemStack stack = new ItemStack(SandStormItems.SANDBOARD);
            if (passenger instanceof Player player && player.getInventory().add(stack)) {
                player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.PLAYERS, 0.8f, 1.2f);
            } else {
                this.spawnAtLocation((ServerLevel) this.level(), stack);
            }
            this.discard();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (!this.level().isClientSide() && this.isAlive()) {
            this.spawnAtLocation(level, new ItemStack(SandStormItems.SANDBOARD));
            this.discard();
        }
        return true;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.isVehicle()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }
}
