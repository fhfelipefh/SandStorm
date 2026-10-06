package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.CrawlerDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.LaborerUnitEntity;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import com.fhfelipefh.sandstorm.content.world.SandStormWorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DesertExplorationWanderGoal extends Goal {
    private final PathfinderMob mob;
    private final double speedModifier;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private int scanTicks = 0;
    private int walkTicks = 0;
    private int vibrationCooldown = 0;
    private float preferredHeading = 0.0f;
    private boolean hasHeading = false;

    public DesertExplorationWanderGoal(PathfinderMob mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getTarget() != null || !this.mob.isAlive()) {
            return false;
        }
        if (this.mob.isVehicle() || this.mob.isPassenger()) {
            return false;
        }
        if (this.mob instanceof TamableAnimal tamable && (tamable.isOrderedToSit() || tamable.isTame())) {
            return false;
        }
        if (this.mob.level().isClientSide()) {
            return false;
        }
        if (!SandStormWorldHelper.isSandStormWorld(this.mob.level())) {
            return false;
        }

        Vec3 target = this.findExplorationTarget();
        if (target == null) {
            return false;
        }

        this.wantedX = target.x;
        this.wantedY = target.y;
        this.wantedZ = target.z;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.getTarget() != null || !this.mob.isAlive()) {
            return false;
        }
        if (this.mob instanceof TamableAnimal tamable && (tamable.isOrderedToSit() || tamable.isTame())) {
            return false;
        }
        if (this.scanTicks > 0) {
            return true;
        }
        return !this.mob.getNavigation().isDone() && this.walkTicks < 500;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        this.walkTicks = 0;
        this.scanTicks = 0;
    }

    @Override
    public void tick() {
        this.walkTicks++;
        Level level = this.mob.level();

        this.vibrationCooldown++;
        if (this.vibrationCooldown >= 20 && this.mob.onGround() && level instanceof ServerLevel) {
            this.vibrationCooldown = 0;
            BlockState groundState = this.mob.getBlockStateOn();
            if (isDesertSurface(groundState)) {
                int chunkX = this.mob.getBlockX() >> 4;
                int chunkZ = this.mob.getBlockZ() >> 4;
                double vib = getMobVibrationIntensity(this.mob);
                SeismicSurvivalHandler.recordVibration(chunkX, chunkZ, vib);
            }
        }

        double distSq = this.mob.distanceToSqr(this.wantedX, this.wantedY, this.wantedZ);
        if ((distSq <= 9.0 || this.walkTicks >= 260) && this.scanTicks == 0) {
            this.scanTicks = 60;
            this.mob.getNavigation().stop();
        }

        if (this.scanTicks > 0) {
            this.scanTicks--;
            this.mob.getNavigation().stop();

            if (this.scanTicks > 40) {
                float targetYaw = this.mob.getYRot() + 40.0f;
                this.mob.setYHeadRot(Mth.rotateIfNecessary(this.mob.getYHeadRot(), targetYaw, 5.0f));
            } else if (this.scanTicks > 20) {
                float targetYaw = this.mob.getYRot() - 40.0f;
                this.mob.setYHeadRot(Mth.rotateIfNecessary(this.mob.getYHeadRot(), targetYaw, 5.0f));
            } else {
                this.mob.setXRot(25.0f);
            }

            if (this.scanTicks == 30 && level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, this.mob.blockPosition(), SoundEvents.COPPER_BULB_TURN_ON, SoundSource.NEUTRAL, 0.4f, 1.8f);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), 3, 0.2, 0.2, 0.2, 0.05);
            }

            if (this.scanTicks == 0) {
                Vec3 nextTarget = this.findExplorationTarget();
                if (nextTarget != null) {
                    this.wantedX = nextTarget.x;
                    this.wantedY = nextTarget.y;
                    this.wantedZ = nextTarget.z;
                    this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
                    this.walkTicks = 0;
                }
            }
        }
    }

    @Override
    public void stop() {
        this.scanTicks = 0;
        this.walkTicks = 0;
        this.mob.getNavigation().stop();
    }

    private Vec3 findExplorationTarget() {
        Level level = this.mob.level();
        RandomSource random = this.mob.getRandom();

        if (!this.hasHeading) {
            this.preferredHeading = random.nextFloat() * 360.0f;
            this.hasHeading = true;
        } else {
            this.preferredHeading += (random.nextFloat() - 0.5f) * 35.0f;
        }

        double radians = Math.toRadians(this.preferredHeading);
        double distance = 36.0 + random.nextDouble() * 28.0;

        int targetX = Mth.floor(this.mob.getX() + Math.cos(radians) * distance);
        int targetZ = Mth.floor(this.mob.getZ() + Math.sin(radians) * distance);
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, targetX, targetZ);

        BlockPos targetPos = new BlockPos(targetX, surfaceY, targetZ);
        if (Math.abs(surfaceY - this.mob.getBlockY()) <= 16 && level.getBlockState(targetPos).isAir()) {
            return new Vec3(targetX + 0.5, surfaceY, targetZ + 0.5);
        }

        return DefaultRandomPos.getPos(this.mob, 32, 10);
    }

    public static boolean isDesertSurface(BlockState state) {
        if (state == null) {
            return false;
        }
        return state.is(Blocks.SAND)
                || state.is(Blocks.RED_SAND)
                || state.is(Blocks.SANDSTONE)
                || state.is(Blocks.RED_SANDSTONE)
                || state.is(Blocks.SUSPICIOUS_SAND);
    }

    public static double getMobVibrationIntensity(PathfinderMob mob) {
        if (mob instanceof LaborerUnitEntity) {
            return 0.45;
        }
        if (mob instanceof CrawlerDroneEntity) {
            return 0.35;
        }
        return 0.20;
    }
}
