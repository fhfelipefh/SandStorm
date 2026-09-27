package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.block.BioRegenerationPodBlock;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class MedBaySeatEntity extends Entity {

    public MedBaySeatEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (!this.isVehicle() || !(this.level().getBlockState(this.blockPosition()).getBlock() instanceof BioRegenerationPodBlock)) {
                this.discard();
            }
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        Direction dir = this.getDirection();
        return new Vec3(this.getX() + dir.getStepX() * 1.1, this.getY() + 0.5, this.getZ() + dir.getStepZ() * 1.1);
    }
}
