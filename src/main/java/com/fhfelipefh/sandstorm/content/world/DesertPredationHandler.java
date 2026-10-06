package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.defense.AcousticDefenseTracker;
import com.fhfelipefh.sandstorm.content.defense.KineticShieldTracker;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DesertPredationHandler {
    private record PendingAmbush(ServerLevel level, UUID mobUuid, BlockPos strikePos, int ticksRemaining) {
        PendingAmbush decrement() {
            return new PendingAmbush(level, mobUuid, strikePos, ticksRemaining - 1);
        }
    }

    private static final List<PendingAmbush> PENDING_AMBUSHES = new ArrayList<>();
    private static int globalCooldown = 0;
    private static int scanTickCounter = 0;

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(DesertPredationHandler::handleServerTick);
    }

    public static void handleServerTick(MinecraftServer server) {
        processPendingAmbushes();

        if (globalCooldown > 0) {
            globalCooldown--;
        }

        scanTickCounter++;
        if (scanTickCounter % 60 != 0 || globalCooldown > 0 || !PENDING_AMBUSHES.isEmpty()) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (attemptAmbushNearPlayer(player)) {
                break;
            }
        }
    }

    public static void processPendingAmbushes() {
        if (PENDING_AMBUSHES.isEmpty()) {
            return;
        }

        for (int i = PENDING_AMBUSHES.size() - 1; i >= 0; i--) {
            PendingAmbush ambush = PENDING_AMBUSHES.get(i);
            if (ambush.ticksRemaining() <= 1) {
                PENDING_AMBUSHES.remove(i);
                executeAmbush(ambush);
            } else {
                PENDING_AMBUSHES.set(i, ambush.decrement());
            }
        }
    }

    private static boolean attemptAmbushNearPlayer(ServerPlayer player) {
        if (!player.isAlive() || player.isSpectator()) {
            return false;
        }
        ServerLevel level = player.level();
        if (!SandStormWorldHelper.isSandStormWorld(level)) {
            return false;
        }
        if (SeismicSurvivalHandler.getTracker().isInsideSafeZone(player.getBlockX(), player.getBlockZ())) {
            return false;
        }
        if (KineticShieldTracker.isInsideShield(level.dimension(), player.blockPosition())) {
            return false;
        }
        if (AcousticDefenseTracker.isInsideAcousticDamping(level.dimension(), player.blockPosition())) {
            return false;
        }

        AABB box = player.getBoundingBox().inflate(48.0);
        List<PathfinderMob> nearbyMobs = level.getEntitiesOfClass(
                PathfinderMob.class,
                box,
                DesertMobSpawnManager::isArtificialMob
        );

        for (PathfinderMob mob : nearbyMobs) {
            if (!mob.isAlive()) {
                continue;
            }
            double distSq = player.distanceToSqr(mob);
            if (distSq < 256.0 || distSq > 2025.0) {
                continue;
            }

            Vec3 look = player.getLookAngle();
            Vec3 toMob = new Vec3(mob.getX() - player.getX(), mob.getEyeY() - player.getEyeY(), mob.getZ() - player.getZ()).normalize();
            if (look.dot(toMob) <= 0.2) {
                continue;
            }

            ClipContext clip = new ClipContext(
                    player.getEyePosition(),
                    mob.getEyePosition(),
                    ClipContext.Block.VISUAL,
                    ClipContext.Fluid.NONE,
                    player
            );
            if (level.clip(clip).getType() != HitResult.Type.MISS) {
                continue;
            }

            if (!DesertExplorationWanderGoal.isDesertSurface(mob.getBlockStateOn())) {
                continue;
            }

            if (!level.getEntitiesOfClass(SandwormEntity.class, mob.getBoundingBox().inflate(48.0)).isEmpty()) {
                continue;
            }

            int roll = level.getRandom().nextInt(15);
            if (roll != 0) {
                continue;
            }

            level.playSound(null, mob.blockPosition(), SandStormSoundEvents.SANDWORM_RUMBLE, SoundSource.HOSTILE, 1.8f, 0.75f);
            level.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                    mob.getX(), mob.getY() + 0.3, mob.getZ(), 45, 1.2, 0.4, 1.2, 0.2
            );

            mob.getNavigation().stop();
            mob.setDeltaMovement(0.0, 0.0, 0.0);

            PENDING_AMBUSHES.add(new PendingAmbush(level, mob.getUUID(), mob.blockPosition(), 25));
            globalCooldown = 1200;
            return true;
        }

        return false;
    }

    private static void executeAmbush(PendingAmbush ambush) {
        ServerLevel level = ambush.level();
        if (!level.isLoaded(ambush.strikePos())) {
            return;
        }

        PathfinderMob victim = null;
        for (Entity entity : level.getAllEntities()) {
            if (entity.getUUID().equals(ambush.mobUuid()) && entity instanceof PathfinderMob pMob) {
                victim = pMob;
                break;
            }
        }

        SandwormEntity worm = SandStormEntities.SANDWORM.create(level, EntitySpawnReason.TRIGGERED);
        if (worm == null) {
            return;
        }

        worm.setWormSize(2, true);
        worm.setPos(ambush.strikePos().getX() + 0.5, ambush.strikePos().getY(), ambush.strikePos().getZ() + 0.5);
        if (victim != null && victim.isAlive()) {
            worm.setTarget(victim);
        }
        level.addFreshEntity(worm);
        worm.triggerBreachShockwave();
        worm.triggerBiteAnimation(ambush.strikePos());

        if (victim != null && victim.isAlive()) {
            victim.hurtServer(level, level.damageSources().mobAttack(worm), 65.0f);
            level.playSound(null, ambush.strikePos(), SoundEvents.GENERIC_EAT.value(), SoundSource.HOSTILE, 1.8f, 0.6f);
            level.playSound(null, ambush.strikePos(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1.0f, 0.5f);
            level.sendParticles(ParticleTypes.EXPLOSION, victim.getX(), victim.getY() + 0.8, victim.getZ(), 2, 0.3, 0.3, 0.3, 0.0);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, victim.getX(), victim.getY() + 0.8, victim.getZ(), 20, 0.4, 0.4, 0.4, 0.2);
            victim.spawnAtLocation(level, new ItemStack(SandStormItems.SCRAP_METAL, 2 + level.getRandom().nextInt(3)));
        }
    }
}
