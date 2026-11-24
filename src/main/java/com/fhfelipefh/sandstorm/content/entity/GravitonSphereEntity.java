package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.CompanionTrapHelper;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.MugenInfinityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GravitonSphereEntity extends ThrowableItemProjectile {
    public static final int MODE_BLUE_LAPSE = 0;
    public static final int MODE_RED_REVERSAL = 1;
    public static final int MODE_VOID_PURPLE = 2;

    private static final EntityDataAccessor<Integer> DATA_MODE = SynchedEntityData.defineId(GravitonSphereEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_COLLAPSING = SynchedEntityData.defineId(GravitonSphereEntity.class, EntityDataSerializers.BOOLEAN);
    private static final DustParticleOptions BLUE_DUST = new DustParticleOptions(0x00B8FF, 1.8f);
    private static final DustParticleOptions RED_DUST = new DustParticleOptions(0xFF1E42, 1.9f);
    private static final DustParticleOptions PURPLE_DUST = new DustParticleOptions(0x9B30FF, 2.3f);
    private static final DustParticleOptions MAGENTA_DUST = new DustParticleOptions(0xE066FF, 1.9f);

    private final Set<Integer> piercedEntityIds = new HashSet<>();
    private int ageTicks = 0;
    private int collapseTicks = 0;

    public GravitonSphereEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public GravitonSphereEntity(Level level, LivingEntity shooter, int mode) {
        super(SandStormEntities.GRAVITON_SPHERE, shooter, level, new ItemStack(
                switch (mode) {
                    case MODE_RED_REVERSAL -> SandStormItems.GRAVITON_REVERSAL_CANNON;
                    case MODE_VOID_PURPLE -> SandStormItems.GRAVITON_VOID_CANNON;
                    default -> SandStormItems.GRAVITON_LAPSE_CANNON;
                }
        ));
        this.setNoGravity(true);
        this.entityData.set(DATA_MODE, mode);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MODE, MODE_BLUE_LAPSE);
        builder.define(DATA_COLLAPSING, false);
    }

    public int getSphereMode() {
        if (this.entityData == null) {
            return MODE_BLUE_LAPSE;
        }
        return this.entityData.get(DATA_MODE);
    }

    public boolean isCollapsing() {
        if (this.entityData == null) {
            return false;
        }
        return this.entityData.get(DATA_COLLAPSING);
    }

    @Override
    protected Item getDefaultItem() {
        return switch (getSphereMode()) {
            case MODE_RED_REVERSAL -> SandStormItems.GRAVITON_REVERSAL_CANNON;
            case MODE_VOID_PURPLE -> SandStormItems.GRAVITON_VOID_CANNON;
            default -> SandStormItems.GRAVITON_LAPSE_CANNON;
        };
    }

    @Override
    public void tick() {
        super.tick();
        this.ageTicks++;
        int mode = getSphereMode();

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (mode == MODE_BLUE_LAPSE) {
            tickBlueLapse(serverLevel);
        } else if (mode == MODE_VOID_PURPLE) {
            tickVoidPurple(serverLevel);
        } else {
            tickRedReversal(serverLevel);
        }
    }

    private void tickBlueLapse(ServerLevel serverLevel) {
        boolean collapsing = isCollapsing();
        if (collapsing) {
            this.setDeltaMovement(Vec3.ZERO);
            this.collapseTicks++;
        }

        Vec3 center = this.position();
        serverLevel.sendParticles(BLUE_DUST, center.x, center.y, center.z, collapsing ? 10 : 5, 0.55, 0.55, 0.55, 0.02);
        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, center.x, center.y, center.z, collapsing ? 7 : 3, 0.4, 0.4, 0.4, 0.04);
        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, collapsing ? 12 : 4, 0.9, 0.9, 0.9, 0.08);
        if (collapsing) {
            double angle = this.collapseTicks * 0.55;
            for (int i = 0; i < 4; i++) {
                double a = angle + i * (Math.PI * 0.5);
                double r = Math.max(0.6, 3.2 - this.collapseTicks * 0.08);
                serverLevel.sendParticles(ParticleTypes.END_ROD, center.x + Math.cos(a) * r, center.y, center.z + Math.sin(a) * r, 1, 0.02, 0.02, 0.02, 0.01);
            }
        }

        applyBlueAttraction(serverLevel, center, collapsing);

        if (collapsing) {
            if (this.collapseTicks % 3 == 0) {
                pullNearbyBlocks(serverLevel, center, 3.6, 4);
            }
            if (this.collapseTicks >= 30) {
                detonateBlueCollapse(serverLevel, center);
                this.discard();
            }
        } else {
            if (this.ageTicks % 6 == 0) {
                pullNearbyBlocks(serverLevel, center, 2.8, 2);
            }
            if (this.ageTicks >= 55) {
                beginBlueCollapse(serverLevel);
            }
        }
    }

    private void tickRedReversal(ServerLevel serverLevel) {
        Vec3 center = this.position();
        serverLevel.sendParticles(RED_DUST, center.x, center.y, center.z, 7, 0.35, 0.35, 0.35, 0.02);
        serverLevel.sendParticles(ParticleTypes.FLAME, center.x, center.y, center.z, 3, 0.2, 0.2, 0.2, 0.03);
        serverLevel.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, 2, 0.15, 0.15, 0.15, 0.02);

        if (this.ageTicks >= 40) {
            detonateRedReversal(serverLevel, center);
        }
    }

    private void applyBlueAttraction(ServerLevel serverLevel, Vec3 center, boolean collapsing) {
        double radius = collapsing ? 12.0 : 9.5;
        AABB area = new AABB(center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius);
        Entity owner = this.getOwner();

        List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive);
        for (LivingEntity target : entities) {
            if (target == owner || CompanionTrapHelper.isTamedCompanion(target)) {
                continue;
            }
            if (target instanceof ServerPlayer sp && MugenInfinityHandler.isMugenActive(sp)) {
                continue;
            }
            Vec3 diff = center.subtract(target.position().add(0.0, target.getBbHeight() * 0.5, 0.0));
            double dist = diff.length();
            if (dist > 0.35 && dist <= radius) {
                double strength = collapsing ? 0.55 : 0.38;
                Vec3 pull = diff.normalize().scale(Math.min(1.15, strength * (radius / (dist + 1.2))));
                target.setDeltaMovement(target.getDeltaMovement().scale(0.6).add(pull.x, pull.y + 0.08, pull.z));
                target.needsSync = true;
            }
            if (dist <= 3.6 && this.ageTicks % 5 == 0) {
                target.hurtServer(serverLevel, serverLevel.damageSources().indirectMagic(this, owner), 10.0f);
            }
        }

        List<FallingBlockEntity> fallingBlocks = serverLevel.getEntitiesOfClass(FallingBlockEntity.class, area);
        for (FallingBlockEntity fb : fallingBlocks) {
            Vec3 diff = center.subtract(fb.position());
            double dist = diff.length();
            if (dist > 0.4 && dist <= radius) {
                Vec3 pull = diff.normalize().scale(0.55);
                fb.setDeltaMovement(fb.getDeltaMovement().scale(0.55).add(pull));
                fb.needsSync = true;
            }
        }

        List<ItemEntity> items = serverLevel.getEntitiesOfClass(ItemEntity.class, area);
        for (ItemEntity item : items) {
            Vec3 diff = center.subtract(item.position());
            double dist = diff.length();
            if (dist > 0.4 && dist <= radius) {
                item.setDeltaMovement(item.getDeltaMovement().scale(0.6).add(diff.normalize().scale(0.45)));
            }
        }
    }

    private void pullNearbyBlocks(ServerLevel serverLevel, Vec3 center, double radius, int maxBlocks) {
        BlockPos origin = BlockPos.containing(center);
        int r = (int) Math.ceil(radius);
        List<BlockPos> candidates = new ArrayList<>();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx * dx + dy * dy + dz * dz <= radius * radius) {
                        BlockPos pos = origin.offset(dx, dy, dz);
                        if (isMovableTerrainBlock(serverLevel, pos)) {
                            candidates.add(pos);
                        }
                    }
                }
            }
        }
        int spawned = 0;
        while (!candidates.isEmpty() && spawned < maxBlocks) {
            int idx = serverLevel.getRandom().nextInt(candidates.size());
            BlockPos pos = candidates.remove(idx);
            BlockState state = serverLevel.getBlockState(pos);
            FallingBlockEntity fb = FallingBlockEntity.fall(serverLevel, pos, state);
            if (fb != null) {
                Vec3 pull = center.subtract(fb.position()).normalize().scale(0.65);
                fb.setDeltaMovement(pull.x, pull.y + 0.25, pull.z);
                fb.dropItem = false;
                spawned++;
            }
        }
    }

    private void beginBlueCollapse(ServerLevel serverLevel) {
        if (isCollapsing()) {
            return;
        }
        this.entityData.set(DATA_COLLAPSING, true);
        this.setDeltaMovement(Vec3.ZERO);
        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.6f, 0.65f);
    }

    private void detonateBlueCollapse(ServerLevel serverLevel, Vec3 center) {
        double radius = 6.5;
        AABB area = new AABB(center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius);
        Entity owner = this.getOwner();
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive)) {
            if (target == owner || CompanionTrapHelper.isTamedCompanion(target)) {
                continue;
            }
            if (target instanceof ServerPlayer sp && MugenInfinityHandler.isMugenActive(sp)) {
                continue;
            }
            target.hurtServer(serverLevel, serverLevel.damageSources().explosion(this, owner), 40.0f);
        }
        serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, center.x, center.y, center.z, 2, 0.2, 0.2, 0.2, 0.0);
        serverLevel.sendParticles(BLUE_DUST, center.x, center.y, center.z, 60, 2.2, 2.2, 2.2, 0.15);
        serverLevel.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 2.5f, 1.35f);
        serverLevel.playSound(null, center.x, center.y, center.z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.6f, 1.25f);
    }

    private void detonateRedReversal(ServerLevel serverLevel, Vec3 center) {
        double blastRadius = 12.0;
        AABB area = new AABB(center.x - blastRadius, center.y - blastRadius, center.z - blastRadius,
                center.x + blastRadius, center.y + blastRadius, center.z + blastRadius);
        Entity owner = this.getOwner();

        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive)) {
            if (target == owner || CompanionTrapHelper.isTamedCompanion(target)) {
                continue;
            }
            if (target instanceof ServerPlayer sp && MugenInfinityHandler.isMugenActive(sp)) {
                continue;
            }
            Vec3 diff = target.position().add(0.0, 0.5, 0.0).subtract(center);
            double dist = Math.max(0.4, diff.length());
            if (dist <= blastRadius) {
                double falloff = 1.0 - (dist / blastRadius) * 0.65;
                float damage = (float) Math.max(18.0, 52.0 * falloff);
                target.hurtServer(serverLevel, serverLevel.damageSources().explosion(this, owner), damage);

                Vec3 pushDir = diff.normalize();
                double impulse = 2.8 * falloff;
                target.setDeltaMovement(pushDir.x * impulse, Math.max(0.75, pushDir.y * impulse + 0.55), pushDir.z * impulse);
                target.needsSync = true;
            }
        }

        BlockPos origin = BlockPos.containing(center);
        int blockRadius = 3;
        int flungBlocks = 0;
        for (int dx = -blockRadius; dx <= blockRadius; dx++) {
            for (int dy = -blockRadius; dy <= blockRadius; dy++) {
                for (int dz = -blockRadius; dz <= blockRadius; dz++) {
                    if (dx * dx + dy * dy + dz * dz <= blockRadius * blockRadius && flungBlocks < 28) {
                        BlockPos pos = origin.offset(dx, dy, dz);
                        if (isMovableTerrainBlock(serverLevel, pos)) {
                            BlockState state = serverLevel.getBlockState(pos);
                            FallingBlockEntity fb = FallingBlockEntity.fall(serverLevel, pos, state);
                            if (fb != null) {
                                Vec3 away = new Vec3(pos.getX() + 0.5 - center.x, pos.getY() + 0.5 - center.y, pos.getZ() + 0.5 - center.z);
                                if (away.lengthSqr() < 0.05) {
                                    away = new Vec3(0.0, 1.0, 0.0);
                                }
                                Vec3 launch = away.normalize().scale(1.15 + serverLevel.getRandom().nextDouble() * 0.6);
                                fb.setDeltaMovement(launch.x, Math.max(0.5, launch.y + 0.45), launch.z);
                                fb.dropItem = false;
                                flungBlocks++;
                            }
                        }
                    }
                }
            }
        }

        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 2, 0.5, 0.5, 0.5, 0.0);
        serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, center.x, center.y, center.z, 3, 0.4, 0.4, 0.4, 0.0);
        serverLevel.sendParticles(RED_DUST, center.x, center.y, center.z, 85, 3.0, 2.5, 3.0, 0.25);
        serverLevel.playSound(null, center.x, center.y, center.z, SandStormSoundEvents.MEGAZORD_SHOCKWAVE, SoundSource.PLAYERS, 2.4f, 1.15f);
        serverLevel.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 2.5f, 0.85f);
        this.discard();
    }

    public static boolean isVoidDestructibleSpeed(float destroySpeed) {
        return destroySpeed >= 0.0f;
    }

    private boolean isVoidDestructibleBlock(ServerLevel level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.hasBlockEntity()) {
            return false;
        }
        return isVoidDestructibleSpeed(state.getDestroySpeed(level, pos));
    }

    private boolean isMovableTerrainBlock(ServerLevel level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.hasBlockEntity() || !state.getFluidState().isEmpty()) {
            return false;
        }
        float speed = state.getDestroySpeed(level, pos);
        return speed >= 0.0f && speed <= 15.0f;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (hitResult.getEntity() == this.getOwner()) {
            return;
        }
        int mode = getSphereMode();
        if (mode == MODE_BLUE_LAPSE) {
            beginBlueCollapse(serverLevel);
        } else if (mode == MODE_VOID_PURPLE) {
            if (hitResult.getEntity() instanceof LivingEntity living && !CompanionTrapHelper.isTamedCompanion(living)) {
                if (!(living instanceof ServerPlayer sp && MugenInfinityHandler.isMugenActive(sp))) {
                    if (this.piercedEntityIds.add(living.getId())) {
                        living.hurtServer(serverLevel, serverLevel.damageSources().indirectMagic(this, this.getOwner()), 95.0f);
                    }
                }
            }
        } else {
            detonateRedReversal(serverLevel, hitResult.getLocation());
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        int mode = getSphereMode();
        if (mode == MODE_BLUE_LAPSE) {
            beginBlueCollapse(serverLevel);
        } else if (mode == MODE_VOID_PURPLE) {
            BlockPos hitPos = hitResult.getBlockPos();
            if (!isVoidDestructibleBlock(serverLevel, hitPos)) {
                detonateVoidPurple(serverLevel, hitResult.getLocation());
            } else {
                carveVoidCorridor(serverLevel, hitResult.getLocation(), 3);
            }
        } else {
            detonateRedReversal(serverLevel, hitResult.getLocation());
        }
    }

    private void tickVoidPurple(ServerLevel serverLevel) {
        Vec3 center = this.position();
        serverLevel.sendParticles(PURPLE_DUST, center.x, center.y, center.z, 10, 0.75, 0.75, 0.75, 0.03);
        serverLevel.sendParticles(MAGENTA_DUST, center.x, center.y, center.z, 6, 0.55, 0.55, 0.55, 0.02);
        serverLevel.sendParticles(BLUE_DUST, center.x, center.y, center.z, 2, 0.75, 0.75, 0.75, 0.02);
        serverLevel.sendParticles(RED_DUST, center.x, center.y, center.z, 2, 0.75, 0.75, 0.75, 0.02);
        serverLevel.sendParticles(ParticleTypes.WITCH, center.x, center.y, center.z, 6, 0.65, 0.65, 0.65, 0.04);
        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 5, 0.85, 0.85, 0.85, 0.06);
        serverLevel.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, 2, 0.35, 0.35, 0.35, 0.01);
        if (this.ageTicks % 5 == 0) {
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, center.x, center.y, center.z, 1, 0.1, 0.1, 0.1, 0.0);
        }

        carveVoidCorridor(serverLevel, center, 3);

        double sweepRadius = 6.0;
        AABB area = new AABB(center.x - sweepRadius, center.y - sweepRadius, center.z - sweepRadius,
                center.x + sweepRadius, center.y + sweepRadius, center.z + sweepRadius);
        Entity owner = this.getOwner();
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive)) {
            if (target == owner || CompanionTrapHelper.isTamedCompanion(target)) {
                continue;
            }
            if (target instanceof ServerPlayer sp && MugenInfinityHandler.isMugenActive(sp)) {
                continue;
            }
            Vec3 diff = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0).subtract(center);
            double dist = Math.max(0.25, diff.length());
            if (dist <= sweepRadius) {
                if (this.piercedEntityIds.add(target.getId())) {
                    target.hurtServer(serverLevel, serverLevel.damageSources().indirectMagic(this, owner), 95.0f);
                }
                Vec3 shear = this.getDeltaMovement().normalize().scale(0.95).add(diff.normalize().scale(0.75));
                target.setDeltaMovement(shear.x, Math.max(0.4, shear.y + 0.3), shear.z);
                target.needsSync = true;
            }
        }

        if (this.ageTicks % 6 == 0) {
            serverLevel.playSound(null, center.x, center.y, center.z, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.PLAYERS, 1.3f, 0.55f);
        }

        if (this.ageTicks >= 48) {
            detonateVoidPurple(serverLevel, center);
        }
    }

    private void carveVoidCorridor(ServerLevel serverLevel, Vec3 center, int radius) {
        BlockPos origin = BlockPos.containing(center);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq <= radius * radius + 1) {
                        BlockPos pos = origin.offset(dx, dy, dz);
                        if (isVoidDestructibleBlock(serverLevel, pos)) {
                            if (distSq >= radius * radius - 1 && serverLevel.getRandom().nextInt(5) == 0) {
                                BlockState state = serverLevel.getBlockState(pos);
                                FallingBlockEntity fb = FallingBlockEntity.fall(serverLevel, pos, state);
                                if (fb != null) {
                                    Vec3 out = new Vec3(dx, dy + 0.4, dz).normalize().scale(1.05);
                                    fb.setDeltaMovement(out);
                                    fb.dropItem = false;
                                    fb.needsSync = true;
                                }
                            } else {
                                serverLevel.destroyBlock(pos, false);
                            }
                        }
                    }
                }
            }
        }
    }

    private void detonateVoidPurple(ServerLevel serverLevel, Vec3 center) {
        carveVoidCorridor(serverLevel, center, 5);
        double blastRadius = 13.5;
        AABB area = new AABB(center.x - blastRadius, center.y - blastRadius, center.z - blastRadius,
                center.x + blastRadius, center.y + blastRadius, center.z + blastRadius);
        Entity owner = this.getOwner();
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive)) {
            if (target == owner || CompanionTrapHelper.isTamedCompanion(target)) {
                continue;
            }
            if (target instanceof ServerPlayer sp && MugenInfinityHandler.isMugenActive(sp)) {
                continue;
            }
            Vec3 diff = target.position().add(0.0, 0.5, 0.0).subtract(center);
            double dist = Math.max(0.35, diff.length());
            if (dist <= blastRadius) {
                double falloff = 1.0 - (dist / blastRadius) * 0.55;
                float damage = (float) Math.max(55.0, 140.0 * falloff);
                target.hurtServer(serverLevel, serverLevel.damageSources().explosion(this, owner), damage);
                Vec3 blast = diff.normalize().scale(2.8 * falloff);
                target.setDeltaMovement(blast.x, Math.max(0.75, blast.y + 0.55), blast.z);
                target.needsSync = true;
            }
        }

        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 2, 0.4, 0.4, 0.4, 0.0);
        serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, center.x, center.y, center.z, 4, 0.6, 0.6, 0.6, 0.0);
        serverLevel.sendParticles(PURPLE_DUST, center.x, center.y, center.z, 110, 3.2, 2.8, 3.2, 0.25);
        serverLevel.sendParticles(MAGENTA_DUST, center.x, center.y, center.z, 70, 2.4, 2.2, 2.4, 0.2);
        serverLevel.sendParticles(ParticleTypes.WITCH, center.x, center.y, center.z, 45, 2.5, 2.5, 2.5, 0.15);
        serverLevel.playSound(null, center.x, center.y, center.z, SandStormSoundEvents.MEGAZORD_SHOCKWAVE, SoundSource.PLAYERS, 2.8f, 0.85f);
        serverLevel.playSound(null, center.x, center.y, center.z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 2.4f, 0.75f);
        serverLevel.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 2.6f, 0.7f);
        this.discard();
    }
}
