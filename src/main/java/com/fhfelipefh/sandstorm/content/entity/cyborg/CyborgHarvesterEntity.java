package com.fhfelipefh.sandstorm.content.entity.cyborg;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class CyborgHarvesterEntity extends CyborgEntity {
    private int harvestTicks = 0;
    private BlockPos currentTargetCrop = null;

    public CyborgHarvesterEntity(EntityType<? extends CyborgEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CyborgEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    public CyborgSpecialty getSpecialty() {
        return CyborgSpecialty.HARVESTER;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    protected void tickAutonomousWork(ServerLevel level) {
        if (isInventoryFull()) {
            tickReturnToDock(level);
            return;
        }

        BlockPos center = hasZone() ? getZoneMin() : this.blockPosition();
        AABB scanBox = hasZone()
                ? new AABB(getZoneMin().getX(), getZoneMin().getY(), getZoneMin().getZ(),
                getZoneMax().getX() + 1.0, getZoneMax().getY() + 1.0, getZoneMax().getZ() + 1.0).inflate(2.0)
                : this.getBoundingBox().inflate(8.0);

        List<ItemEntity> nearbyItems = level.getEntitiesOfClass(ItemEntity.class, scanBox);
        if (!nearbyItems.isEmpty()) {
            ItemEntity closestItem = nearbyItems.getFirst();
            double distSq = this.distanceToSqr(closestItem);
            if (distSq > 4.0) {
                this.getNavigation().moveTo(closestItem, 1.2);
                setVisorState(1);
            } else {
                ItemStack remaining = this.inventory.addItem(closestItem.getItem());
                if (remaining.isEmpty()) {
                    closestItem.discard();
                } else {
                    closestItem.setItem(remaining);
                }
                setVisorState(0);
            }
            return;
        }

        if (currentTargetCrop == null || !isMatureCrop(level, currentTargetCrop)) {
            if (currentTargetCrop != null) {
                CyborgSwarmManager.getInstance().releaseBlock(this.getUUID(), currentTargetCrop);
            }
            currentTargetCrop = findMatureCrop(level);
            harvestTicks = 0;
        }

        if (currentTargetCrop == null) {
            setVisorState(0);
            return;
        }

        double distSq = this.distanceToSqr(currentTargetCrop.getX() + 0.5, currentTargetCrop.getY() + 0.5, currentTargetCrop.getZ() + 0.5);
        if (distSq > 12.0) {
            this.getNavigation().moveTo(currentTargetCrop.getX() + 0.5, currentTargetCrop.getY() + 0.5, currentTargetCrop.getZ() + 0.5, 1.0);
            setVisorState(1);
        } else {
            this.getNavigation().stop();
            this.getLookControl().setLookAt(currentTargetCrop.getX() + 0.5, currentTargetCrop.getY() + 0.5, currentTargetCrop.getZ() + 0.5);
            setVisorState(0);
            harvestTicks++;

            if (this.random.nextFloat() < 0.35f) {
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        currentTargetCrop.getX() + 0.5, currentTargetCrop.getY() + 0.5, currentTargetCrop.getZ() + 0.5,
                        3, 0.2, 0.2, 0.2, 0.05);
            }

            if (harvestTicks >= 25) {
                harvestTicks = 0;
                harvestAndReplant(level, currentTargetCrop);
                CyborgSwarmManager.getInstance().releaseBlock(this.getUUID(), currentTargetCrop);
                currentTargetCrop = null;
            }
        }
    }

    private boolean isInventoryFull() {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            if (this.inventory.getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isMatureCrop(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        }
        return false;
    }

    private void harvestAndReplant(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CropBlock cropBlock) {
            level.destroyBlock(pos, true, this);
            level.setBlock(pos, cropBlock.getStateForAge(0), 3);
            consumeEnergy(15);
        }
    }

    private BlockPos findMatureCrop(ServerLevel level) {
        if (hasZone()) {
            BlockPos min = getZoneMin();
            BlockPos max = getZoneMax();

            for (int y = min.getY(); y <= max.getY(); ++y) {
                for (int x = min.getX(); x <= max.getX(); ++x) {
                    for (int z = min.getZ(); z <= max.getZ(); ++z) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (isMatureCrop(level, pos)) {
                            if (CyborgSwarmManager.getInstance().tryReserveBlock(this.getUUID(), pos)) {
                                return pos;
                            }
                        }
                    }
                }
            }
        } else {
            BlockPos center = this.blockPosition();
            int rad = 8;
            for (int dy = -1; dy <= 2; dy++) {
                for (int dx = -rad; dx <= rad; dx++) {
                    for (int dz = -rad; dz <= rad; dz++) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        if (isMatureCrop(level, pos)) {
                            if (CyborgSwarmManager.getInstance().tryReserveBlock(this.getUUID(), pos)) {
                                return pos;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        CyborgSwarmManager.getInstance().releaseAll(this.getUUID());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }
}
