package com.fhfelipefh.sandstorm.content.entity.cyborg;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CyborgExcavatorEntity extends CyborgEntity {
    private int miningTicks = 0;
    private BlockPos currentTargetBlock = null;

    public CyborgExcavatorEntity(EntityType<? extends CyborgEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CyborgEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    public CyborgSpecialty getSpecialty() {
        return CyborgSpecialty.EXCAVATOR;
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
    public void tick() {
        super.tick();

        Level lvl = this.level();
        if (lvl.isClientSide()) {
            return;
        }

        if (getRoutine() == CyborgRoutine.AUTONOMOUS_WORK && hasZone() && getEnergy() > 0) {
            tickAutonomousMining((ServerLevel) lvl);
        } else if (getRoutine() == CyborgRoutine.FOLLOW_OPERATOR) {
            tickFollowOperator();
        }
    }

    private void tickAutonomousMining(ServerLevel level) {
        if (currentTargetBlock == null || level.isEmptyBlock(currentTargetBlock)) {
            if (currentTargetBlock != null) {
                CyborgSwarmManager.getInstance().releaseBlock(this.getUUID(), currentTargetBlock);
            }
            currentTargetBlock = findNextBlockToMine(level);
            miningTicks = 0;
        }

        if (currentTargetBlock == null) {
            setVisorState(0);
            return;
        }

        double distSq = this.distanceToSqr(currentTargetBlock.getX() + 0.5, currentTargetBlock.getY() + 0.5, currentTargetBlock.getZ() + 0.5);
        if (distSq > 12.0) {
            this.getNavigation().moveTo(currentTargetBlock.getX() + 0.5, currentTargetBlock.getY() + 0.5, currentTargetBlock.getZ() + 0.5, 1.0);
            setVisorState(1);
        } else {
            this.getNavigation().stop();
            this.getLookControl().setLookAt(currentTargetBlock.getX() + 0.5, currentTargetBlock.getY() + 0.5, currentTargetBlock.getZ() + 0.5);
            setVisorState(0);
            miningTicks++;

            if (this.random.nextFloat() < 0.35f) {
                level.sendParticles(ParticleTypes.CRIT,
                        currentTargetBlock.getX() + 0.5, currentTargetBlock.getY() + 0.5, currentTargetBlock.getZ() + 0.5,
                        3, 0.2, 0.2, 0.2, 0.05);
            }

            if (miningTicks >= 40) {
                miningTicks = 0;
                consumeEnergy(25);
                CyborgSwarmManager.getInstance().releaseBlock(this.getUUID(), currentTargetBlock);
                level.destroyBlock(currentTargetBlock, true, this);
                currentTargetBlock = null;
            }
        }
    }

    private void tickFollowOperator() {
        Player owner = null;
        if (getOwnerUUID() != null) {
            owner = this.level().getPlayerByUUID(getOwnerUUID());
        }

        if (owner != null && this.distanceToSqr(owner) > 16.0) {
            this.getNavigation().moveTo(owner, 1.1);
            setVisorState(1);
        }
    }

    private BlockPos findNextBlockToMine(ServerLevel level) {
        BlockPos min = getZoneMin();
        BlockPos max = getZoneMax();

        for (int y = max.getY(); y >= min.getY(); --y) {
            for (int x = min.getX(); x <= max.getX(); ++x) {
                for (int z = min.getZ(); z <= max.getZ(); ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!state.isAir() && !state.is(Blocks.BEDROCK) && state.getDestroySpeed(level, pos) >= 0) {
                        if (CyborgSwarmManager.getInstance().tryReserveBlock(this.getUUID(), pos)) {
                            return pos;
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
