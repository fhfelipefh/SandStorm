package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SpaceSuitItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class RappelHarnessEntity extends PathfinderMob {
    private static final EntityDataAccessor<BlockPos> DATA_ANCHOR_POS =
            SynchedEntityData.defineId(RappelHarnessEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Float> DATA_MIN_Y =
            SynchedEntityData.defineId(RappelHarnessEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_MAX_Y =
            SynchedEntityData.defineId(RappelHarnessEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_ASCENDING =
            SynchedEntityData.defineId(RappelHarnessEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DESCENDING =
            SynchedEntityData.defineId(RappelHarnessEntity.class, EntityDataSerializers.BOOLEAN);

    public RappelHarnessEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ANCHOR_POS, BlockPos.ZERO);
        builder.define(DATA_MIN_Y, 0.0f);
        builder.define(DATA_MAX_Y, 256.0f);
        builder.define(DATA_ASCENDING, false);
        builder.define(DATA_DESCENDING, false);
    }

    public BlockPos getAnchorPos() {
        return this.entityData.get(DATA_ANCHOR_POS);
    }

    public void setAnchorPos(BlockPos pos) {
        this.entityData.set(DATA_ANCHOR_POS, pos);
    }

    public double getMinY() {
        return this.entityData.get(DATA_MIN_Y).doubleValue();
    }

    public void setMinY(double minY) {
        this.entityData.set(DATA_MIN_Y, (float) minY);
    }

    public double getMaxY() {
        return this.entityData.get(DATA_MAX_Y).doubleValue();
    }

    public void setMaxY(double maxY) {
        this.entityData.set(DATA_MAX_Y, (float) maxY);
    }

    public boolean isAscending() {
        return this.entityData.get(DATA_ASCENDING);
    }

    public boolean isDescending() {
        return this.entityData.get(DATA_DESCENDING);
    }

    @Override
    public boolean showVehicleHealth() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float scale) {
        return new Vec3(0.0, 0.0, 0.0);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), 0.0f);
        this.yRotO = this.getYRot();
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();

        player.resetFallDistance();
        this.resetFallDistance();

        BlockPos anchor = getAnchorPos();
        double targetX = anchor.equals(BlockPos.ZERO) ? this.getX() : anchor.getX() + 0.5;
        double targetZ = anchor.equals(BlockPos.ZERO) ? this.getZ() : anchor.getZ() + 0.5;

        boolean suitBoost = player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof SpaceSuitItem;
        double climbSpeed = suitBoost ? 0.45 : 0.30;
        double descendSpeed = suitBoost ? 0.50 : 0.35;

        boolean movingUp = player.zza > 0 || player.isJumping();
        boolean movingDown = player.zza < 0;

        if (!this.level().isClientSide()) {
            this.entityData.set(DATA_ASCENDING, movingUp);
            this.entityData.set(DATA_DESCENDING, movingDown);
        }

        double newY = this.getY();
        if (movingUp) {
            newY = Math.min(this.getY() + climbSpeed, getMaxY());
            this.setDeltaMovement(0.0, climbSpeed, 0.0);
            if (this.tickCount % 6 == 0) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.CHAIN_STEP, SoundSource.PLAYERS, 0.7f, 1.4f);
            }
            if (suitBoost && this.level() instanceof ServerLevel serverLevel && this.tickCount % 2 == 0) {
                serverLevel.sendParticles(ParticleTypes.CRIT, targetX, newY, targetZ, 2, 0.1, 0.1, 0.1, 0.05);
            }
        } else if (movingDown) {
            newY = Math.max(this.getY() - descendSpeed, getMinY());
            this.setDeltaMovement(0.0, -descendSpeed, 0.0);
            if (this.tickCount % 6 == 0) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.CHAIN_STEP, SoundSource.PLAYERS, 0.6f, 1.1f);
            }
            if (suitBoost && this.level() instanceof ServerLevel serverLevel && this.tickCount % 2 == 0) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, targetX, newY, targetZ, 1, 0.1, 0.1, 0.1, 0.02);
            }
        } else {
            this.setDeltaMovement(0.0, 0.0, 0.0);
        }

        this.setPos(targetX, newY, targetZ);
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        this.noPhysics = true;

        if (!this.level().isClientSide()) {
            BlockPos anchor = getAnchorPos();
            if (!anchor.equals(BlockPos.ZERO) && !this.level().getBlockState(anchor).is(SandStormBlocks.RAPPEL_ANCHOR) && !this.level().getBlockState(anchor).is(SandStormBlocks.GLOWING_RAPPEL_ANCHOR)) {
                this.discard();
                return;
            }
            if (!this.isVehicle() && this.tickCount > 10) {
                this.discard();
            }
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (passenger instanceof LivingEntity living) {
            living.resetFallDistance();
        }
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("anchorX", getAnchorPos().getX());
        output.putInt("anchorY", getAnchorPos().getY());
        output.putInt("anchorZ", getAnchorPos().getZ());
        output.putFloat("minY", (float) getMinY());
        output.putFloat("maxY", (float) getMaxY());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        int ax = input.getIntOr("anchorX", 0);
        int ay = input.getIntOr("anchorY", 0);
        int az = input.getIntOr("anchorZ", 0);
        setAnchorPos(new BlockPos(ax, ay, az));
        setMinY(input.getFloatOr("minY", 0.0f));
        setMaxY(input.getFloatOr("maxY", 256.0f));
    }
}
