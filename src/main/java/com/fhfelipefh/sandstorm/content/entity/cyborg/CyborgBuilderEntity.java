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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CyborgBuilderEntity extends CyborgEntity {
    private int buildTicks = 0;
    private BlockPos currentTargetPos = null;

    public CyborgBuilderEntity(EntityType<? extends CyborgEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CyborgEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    public CyborgSpecialty getSpecialty() {
        return CyborgSpecialty.BUILDER;
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
            tickAutonomousBuilding((ServerLevel) lvl);
        } else if (getRoutine() == CyborgRoutine.FOLLOW_OPERATOR) {
            tickFollowOperator();
        }
    }

    private void tickAutonomousBuilding(ServerLevel level) {
        if (currentTargetPos == null || !level.isEmptyBlock(currentTargetPos)) {
            currentTargetPos = findNextPositionToBuild(level);
            buildTicks = 0;
        }

        if (currentTargetPos == null) {
            setVisorState(0);
            return;
        }

        double distSq = this.distanceToSqr(currentTargetPos.getX() + 0.5, currentTargetPos.getY() + 0.5, currentTargetPos.getZ() + 0.5);
        if (distSq > 12.0) {
            this.getNavigation().moveTo(currentTargetPos.getX() + 0.5, currentTargetPos.getY() + 0.5, currentTargetPos.getZ() + 0.5, 1.0);
            setVisorState(1);
        } else {
            this.getNavigation().stop();
            this.getLookControl().setLookAt(currentTargetPos.getX() + 0.5, currentTargetPos.getY() + 0.5, currentTargetPos.getZ() + 0.5);
            setVisorState(0);
            buildTicks++;

            if (this.random.nextFloat() < 0.35f) {
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        currentTargetPos.getX() + 0.5, currentTargetPos.getY() + 0.5, currentTargetPos.getZ() + 0.5,
                        3, 0.2, 0.2, 0.2, 0.05);
            }

            if (buildTicks >= 30) {
                buildTicks = 0;
                placeBlockFromInventory(level, currentTargetPos);
                currentTargetPos = null;
            }
        }
    }

    private void placeBlockFromInventory(ServerLevel level, BlockPos pos) {
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                BlockState stateToPlace = blockItem.getBlock().defaultBlockState();
                level.setBlock(pos, stateToPlace, 3);
                stack.shrink(1);
                consumeEnergy(20);
                break;
            }
        }
    }

    private BlockPos findNextPositionToBuild(ServerLevel level) {
        BlockPos min = getZoneMin();
        BlockPos max = getZoneMax();

        for (int y = min.getY(); y <= max.getY(); ++y) {
            for (int x = min.getX(); x <= max.getX(); ++x) {
                for (int z = min.getZ(); z <= max.getZ(); ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (level.isEmptyBlock(pos) && level.getBlockState(pos.below()).isSolid()) {
                        return pos;
                    }
                }
            }
        }
        return null;
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

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }
}
