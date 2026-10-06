package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.defense.AcousticDefenseTracker;
import com.fhfelipefh.sandstorm.content.defense.KineticShieldTracker;
import com.fhfelipefh.sandstorm.content.entity.CrawlerDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CyberHoundEntity;
import com.fhfelipefh.sandstorm.content.entity.DerelictAutomatonEntity;
import com.fhfelipefh.sandstorm.content.entity.LaborerUnitEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.entity.ScoutDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.ScrapSentinelEntity;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DesertMobSpawnManager {
    private static int tickCounter = 0;

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(DesertMobSpawnManager::handleServerTick);
    }

    public static void handleServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter % 50 != 0) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            attemptSpawnForPlayer(player);
        }

        if (tickCounter % 250 == 0) {
            for (ServerLevel level : server.getAllLevels()) {
                if (SandStormWorldHelper.isSandStormWorld(level)) {
                    cleanupDistantArtificialMobs(level);
                }
            }
        }
    }

    public static void attemptSpawnForPlayer(ServerPlayer player) {
        if (!player.isAlive() || player.isSpectator()) {
            return;
        }
        ServerLevel level = player.level();
        if (!SandStormWorldHelper.isSandStormWorld(level)) {
            return;
        }
        if (SeismicSurvivalHandler.getTracker().isInsideSafeZone(player.getBlockX(), player.getBlockZ())) {
            return;
        }
        if (KineticShieldTracker.isInsideShield(level.dimension(), player.blockPosition())) {
            return;
        }
        if (AcousticDefenseTracker.isInsideAcousticDamping(level.dimension(), player.blockPosition())) {
            return;
        }

        AABB localBounds = player.getBoundingBox().inflate(64.0);
        List<PathfinderMob> nearbyMobs = level.getEntitiesOfClass(
                PathfinderMob.class,
                localBounds,
                DesertMobSpawnManager::isArtificialMob
        );
        if (nearbyMobs.size() >= 10) {
            return;
        }

        RandomSource random = level.getRandom();
        for (int attempt = 0; attempt < 12; attempt++) {
            double distance = 24.0 + random.nextDouble() * 32.0;
            double baseHeading = Math.toRadians(player.getYRot() + 180.0f);
            double angle = baseHeading + (random.nextDouble() - 0.5) * Math.PI * 1.5;

            int candidateX = Mth.floor(player.getX() + Math.cos(angle) * distance);
            int candidateZ = Mth.floor(player.getZ() + Math.sin(angle) * distance);
            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, candidateX, candidateZ);

            BlockPos spawnPos = new BlockPos(candidateX, surfaceY, candidateZ);
            BlockPos groundPos = spawnPos.below();

            if (!isSpawnableGround(level.getBlockState(groundPos))) {
                continue;
            }
            if (!level.getBlockState(spawnPos).isAir() || !level.getBlockState(spawnPos.above()).isAir()) {
                continue;
            }
            if (!level.getBiome(spawnPos).is(Biomes.DESERT)) {
                continue;
            }
            if (isPositionVisibleToAnyPlayer(level, spawnPos)) {
                continue;
            }

            EntityType<? extends Mob> entityType = selectArtificialMobType(random);
            Mob mob = entityType.create(level, EntitySpawnReason.NATURAL);
            if (mob != null) {
                mob.setPos(candidateX + 0.5, surfaceY, candidateZ + 0.5);
                mob.addTag(VanillaMonsterSuppressionHandler.ALLOWED_TAG);
                level.addFreshEntity(mob);
                break;
            }
        }
    }

    public static boolean isPositionVisibleToAnyPlayer(ServerLevel level, BlockPos pos) {
        Vec3 targetCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        for (ServerPlayer player : level.players()) {
            if (!player.isAlive() || player.isSpectator()) {
                continue;
            }
            double distSq = player.distanceToSqr(targetCenter);
            if (distSq < 256.0) {
                return true;
            }
            if (distSq > 6400.0) {
                continue;
            }

            Vec3 eyePos = player.getEyePosition();
            Vec3 toPos = targetCenter.subtract(eyePos).normalize();
            Vec3 lookAngle = player.getLookAngle();
            double dot = lookAngle.dot(toPos);

            if (dot > 0.25) {
                ClipContext clipContext = new ClipContext(
                        eyePos,
                        targetCenter,
                        ClipContext.Block.VISUAL,
                        ClipContext.Fluid.NONE,
                        player
                );
                if (level.clip(clipContext).getType() == HitResult.Type.MISS) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSpawnableGround(BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }
        return state.is(Blocks.SAND)
                || state.is(Blocks.RED_SAND)
                || state.is(Blocks.SANDSTONE)
                || state.is(Blocks.RED_SANDSTONE)
                || state.is(Blocks.SUSPICIOUS_SAND)
                || state.is(Blocks.TERRACOTTA);
    }

    public static EntityType<? extends Mob> selectArtificialMobType(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 30) {
            return SandStormEntities.DERELICT_AUTOMATON;
        } else if (roll < 55) {
            return SandStormEntities.CYBER_HOUND;
        } else if (roll < 75) {
            return SandStormEntities.LABORER_UNIT;
        } else if (roll < 90) {
            return SandStormEntities.SCOUT_DRONE;
        } else {
            return SandStormEntities.CRAWLER_DRONE;
        }
    }

    public static boolean isArtificialMob(Entity entity) {
        return entity instanceof DerelictAutomatonEntity
                || entity instanceof CyberHoundEntity
                || entity instanceof LaborerUnitEntity
                || entity instanceof ScoutDroneEntity
                || entity instanceof CrawlerDroneEntity
                || entity instanceof ScrapSentinelEntity;
    }

    public static void cleanupDistantArtificialMobs(ServerLevel level) {
        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) {
            return;
        }
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof PathfinderMob mob && isArtificialMob(mob)) {
                if (mob.hasCustomName() || mob.isPersistenceRequired()) {
                    continue;
                }
                if (mob instanceof CyberHoundEntity hound && hound.isTame()) {
                    continue;
                }
                boolean nearAnyPlayer = false;
                for (ServerPlayer player : players) {
                    if (player.distanceToSqr(mob) <= 65536.0) {
                        nearAnyPlayer = true;
                        break;
                    }
                }
                if (!nearAnyPlayer) {
                    mob.discard();
                }
            }
        }
    }
}
