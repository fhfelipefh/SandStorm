package com.fhfelipefh.sandstorm.content.entity.cyborg;

import com.fhfelipefh.sandstorm.content.satellite.SatelliteNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

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
    protected void tickAutonomousWork(ServerLevel level) {
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

            if (miningTicks >= 35) {
                miningTicks = 0;
                consumeEnergy(25);
                CyborgSwarmManager.getInstance().releaseBlock(this.getUUID(), currentTargetBlock);
                BlockPos brokenPos = currentTargetBlock;
                level.destroyBlock(brokenPos, true, this);
                collectNearbyMinedDrops(level, brokenPos);
                currentTargetBlock = null;
            }
        }
    }

    private void collectNearbyMinedDrops(ServerLevel level, BlockPos pos) {
        AABB dropBox = new AABB(pos).inflate(2.5);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, dropBox);
        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();
            ItemStack remainder = this.inventory.addItem(stack);
            if (remainder.isEmpty()) {
                item.discard();
            } else {
                item.setItem(remainder);
            }
        }
    }

    private BlockPos findNextBlockToMine(ServerLevel level) {
        boolean hasSar = SatelliteNetworkManager.isSarGeologicalActive(level);

        if (hasZone()) {
            BlockPos min = getZoneMin();
            BlockPos max = getZoneMax();

            if (hasSar) {
                for (int y = max.getY(); y >= min.getY(); --y) {
                    for (int x = min.getX(); x <= max.getX(); ++x) {
                        for (int z = min.getZ(); z <= max.getZ(); ++z) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState state = level.getBlockState(pos);
                            if (isOreBlock(state)) {
                                if (CyborgSwarmManager.getInstance().tryReserveBlock(this.getUUID(), pos)) {
                                    return pos;
                                }
                            }
                        }
                    }
                }
            }

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
        } else {
            BlockPos center = this.blockPosition();
            int rad = 8;
            for (int dy = 2; dy >= -3; --dy) {
                for (int dx = -rad; dx <= rad; ++dx) {
                    for (int dz = -rad; dz <= rad; ++dz) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        BlockState state = level.getBlockState(pos);
                        if (!state.isAir() && !state.is(Blocks.BEDROCK) && state.getDestroySpeed(level, pos) >= 0) {
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

    private boolean isOreBlock(BlockState state) {
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        return path.endsWith("_ore")
                || state.is(BlockTags.IRON_ORES)
                || state.is(BlockTags.COPPER_ORES)
                || state.is(BlockTags.GOLD_ORES);
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
