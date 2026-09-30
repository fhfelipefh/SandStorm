package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class BuilderDroneEntity extends PathfinderMob {
    private static final EntityDataAccessor<BlockPos> DATA_TARGET_POS = SynchedEntityData.defineId(BuilderDroneEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockPos> DATA_CONSTRUCTOR_POS = SynchedEntityData.defineId(BuilderDroneEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> DATA_IS_WELDING = SynchedEntityData.defineId(BuilderDroneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_BLOCK = SynchedEntityData.defineId(BuilderDroneEntity.class, EntityDataSerializers.BOOLEAN);

    private int weldTicks = 0;
    private static final int REQUIRED_WELD_TICKS = 30;

    public BuilderDroneEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FLYING_SPEED, 0.40)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TARGET_POS, BlockPos.ZERO);
        builder.define(DATA_CONSTRUCTOR_POS, BlockPos.ZERO);
        builder.define(DATA_IS_WELDING, false);
        builder.define(DATA_HAS_BLOCK, false);
    }

    public BlockPos getTargetPos() {
        return this.entityData.get(DATA_TARGET_POS);
    }

    public void setTargetPos(BlockPos pos) {
        this.entityData.set(DATA_TARGET_POS, pos != null ? pos : BlockPos.ZERO);
    }

    public BlockPos getConstructorPos() {
        return this.entityData.get(DATA_CONSTRUCTOR_POS);
    }

    public void setConstructorPos(BlockPos pos) {
        this.entityData.set(DATA_CONSTRUCTOR_POS, pos != null ? pos : BlockPos.ZERO);
    }

    public boolean isWelding() {
        return this.entityData.get(DATA_IS_WELDING);
    }

    public void setWelding(boolean welding) {
        this.entityData.set(DATA_IS_WELDING, welding);
    }

    public boolean hasBlock() {
        return this.entityData.get(DATA_HAS_BLOCK);
    }

    public void setHasBlock(boolean hasBlock) {
        this.entityData.set(DATA_HAS_BLOCK, hasBlock);
    }

    public int getWeldTicks() {
        return weldTicks;
    }

    public boolean isWeldComplete() {
        return weldTicks >= REQUIRED_WELD_TICKS;
    }

    public void resetWeld() {
        this.weldTicks = 0;
        this.setWelding(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);

        Level lvl = this.level();
        if (lvl.isClientSide()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) lvl;
        float storm = SandstormWeatherHandler.getWeather().isActive() ? (float) SandstormWeatherHandler.getWeather().getIntensity() : 0.0f;
        BlockPos constructor = getConstructorPos();

        if (!constructor.equals(BlockPos.ZERO) && serverLevel.isLoaded(constructor)) {
            if (!serverLevel.getBlockState(constructor).is(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR)) {
                this.discard();
                return;
            }
        }

        if (storm >= 0.75f && !constructor.equals(BlockPos.ZERO)) {
            setWelding(false);
            navigateTo(Vec3.atCenterOf(constructor).add(0.0, 1.5, 0.0));
            return;
        }

        BlockPos target = getTargetPos();
        if (target.equals(BlockPos.ZERO)) {
            if (!constructor.equals(BlockPos.ZERO)) {
                navigateTo(Vec3.atCenterOf(constructor).add(0.0, 2.0, 0.0));
            }
            return;
        }

        Vec3 targetHover = Vec3.atCenterOf(target).add(0.0, 1.8, 0.0);
        double distSq = this.position().distanceToSqr(targetHover);

        if (distSq > 2.5) {
            setWelding(false);
            weldTicks = 0;
            navigateTo(targetHover);
        } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.3));
            setWelding(true);
            weldTicks++;

            Vec3 beamStart = this.position().add(0.0, 0.2, 0.0);
            Vec3 beamEnd = Vec3.atCenterOf(target);
            applyLaserHazard(serverLevel, beamStart, beamEnd);

            if (this.random.nextFloat() < 0.4f) {
                serverLevel.sendParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        beamEnd.x + (this.random.nextDouble() - 0.5) * 0.4,
                        beamEnd.y + 0.5,
                        beamEnd.z + (this.random.nextDouble() - 0.5) * 0.4,
                        2, 0.0, 0.05, 0.0, 0.02
                );
            }
        }
    }

    private void navigateTo(Vec3 dest) {
        Vec3 toDest = dest.subtract(this.position());
        double dist = toDest.length();
        if (dist > 0.1) {
            Vec3 move = toDest.normalize().scale(Math.min(dist * 0.15, 0.35));
            this.setDeltaMovement(move);
        }
    }

    private void applyLaserHazard(ServerLevel level, Vec3 start, Vec3 end) {
        AABB box = new AABB(
                Math.min(start.x, end.x) - 1.0,
                Math.min(start.y, end.y) - 1.0,
                Math.min(start.z, end.z) - 1.0,
                Math.max(start.x, end.x) + 1.0,
                Math.max(start.y, end.y) + 1.0,
                Math.max(start.z, end.z) + 1.0
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != this && e.isAlive());
        for (LivingEntity entity : entities) {
            Vec3 pos = entity.position().add(0.0, entity.getBbHeight() * 0.5, 0.0);
            double distToRay = distancePointToSegment(pos, start, end);
            if (distToRay <= 0.75) {
                entity.hurtServer(level, entity.damageSources().inFire(), 0.8f);
                entity.igniteForSeconds(2);
            }
        }
    }

    private double distancePointToSegment(Vec3 point, Vec3 segStart, Vec3 segEnd) {
        Vec3 v = segEnd.subtract(segStart);
        Vec3 w = point.subtract(segStart);
        double c1 = w.dot(v);
        if (c1 <= 0) {
            return point.distanceTo(segStart);
        }
        double c2 = v.dot(v);
        if (c2 <= c1) {
            return point.distanceTo(segEnd);
        }
        double b = c1 / c2;
        Vec3 pb = segStart.add(v.scale(b));
        return point.distanceTo(pb);
    }
}
