package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.ExoSuitChipComponent;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TacticalDodgeHandler {
    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();
    private static final long COOLDOWN_TICKS = 30L;
    private static final long DODGE_ENERGY_COST = 40L;

    private TacticalDodgeHandler() {
    }

    public static void initialize() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(TacticalDodgeHandler::handleDamage);
    }

    private static boolean handleDamage(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity instanceof ServerPlayer player)) {
            return true;
        }

        if (amount <= 0.0f) {
            return true;
        }

        SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player);
        if (!suit.hasChip(ExoSuitChipComponent.EVASIVE_REFLEX_THRUSTER)) {
            return true;
        }

        long currentTick = player.level().getGameTime();
        Long lastDodge = COOLDOWNS.get(player.getUUID());
        if (lastDodge != null && currentTick - lastDodge < COOLDOWN_TICKS) {
            return true;
        }

        if (!SuitSurvivalHandler.consumePlayerEnergy(player, DODGE_ENERGY_COST)) {
            return true;
        }

        COOLDOWNS.put(player.getUUID(), currentTick);
        executeDodge(player, source);
        return false;
    }

    private static void executeDodge(ServerPlayer player, DamageSource source) {
        ServerLevel level = (ServerLevel) player.level();
        Entity attacker = source.getEntity();
        boolean isSandworm = attacker instanceof SandwormEntity;

        double distance = isSandworm ? 20.0 : 7.0;

        Vec3 playerPos = player.position();
        Vec3 awayDir;
        if (attacker != null) {
            Vec3 diff = playerPos.subtract(attacker.position());
            awayDir = new Vec3(diff.x, 0, diff.z);
        } else {
            awayDir = player.getLookAngle().reverse();
            awayDir = new Vec3(awayDir.x, 0, awayDir.z);
        }

        if (awayDir.lengthSqr() < 1e-4) {
            awayDir = new Vec3(0, 0, 1);
        } else {
            awayDir = awayDir.normalize();
        }

        double[] angleOffsets = {0.0, 35.0, -35.0, 60.0, -60.0, 90.0, -90.0};
        BlockPos bestPos = null;
        int currentY = player.getBlockY();

        for (double deg : angleOffsets) {
            double rad = Math.toRadians(deg);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            double rotatedX = awayDir.x * cos - awayDir.z * sin;
            double rotatedZ = awayDir.x * sin + awayDir.z * cos;

            int targetX = (int) Math.floor(playerPos.x + rotatedX * distance);
            int targetZ = (int) Math.floor(playerPos.z + rotatedZ * distance);

            for (int dy = 6; dy >= 0; dy--) {
                BlockPos check = new BlockPos(targetX, currentY + dy, targetZ);
                if (isSafeDodgeLocation(level, check)) {
                    if (bestPos == null || check.getY() > bestPos.getY()) {
                        bestPos = check;
                    }
                }
            }
        }

        if (bestPos == null) {
            for (int dy = -1; dy >= -2; dy--) {
                BlockPos check = new BlockPos((int) Math.floor(playerPos.x + awayDir.x * distance), currentY + dy, (int) Math.floor(playerPos.z + awayDir.z * distance));
                if (isSafeDodgeLocation(level, check) && level.canSeeSky(check)) {
                    bestPos = check;
                    break;
                }
            }
        }

        if (bestPos != null) {
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    player.getX(), player.getY() + 0.8, player.getZ(),
                    18, 0.25, 0.4, 0.25, 0.08);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.1f, 1.5f);

            player.teleportTo(bestPos.getX() + 0.5, bestPos.getY(), bestPos.getZ() + 0.5);
            player.resetFallDistance();

            level.sendParticles(ParticleTypes.POOF,
                    bestPos.getX() + 0.5, bestPos.getY() + 0.8, bestPos.getZ() + 0.5,
                    15, 0.25, 0.4, 0.25, 0.05);
            level.playSound(null, bestPos.getX() + 0.5, bestPos.getY(), bestPos.getZ() + 0.5,
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.35f, 1.8f);
        }
    }

    private static boolean isSafeDodgeLocation(ServerLevel level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isSolid()
                && level.getBlockState(pos).isAir()
                && level.getBlockState(pos.above()).isAir();
    }
}
