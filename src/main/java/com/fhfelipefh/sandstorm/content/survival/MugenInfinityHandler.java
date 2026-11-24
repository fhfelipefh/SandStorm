package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.ExoSuitChipComponent;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.network.MugenStatePayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MugenInfinityHandler {
    public static final long PASSIVE_ENERGY_COST_PER_TICK = 40L;
    public static final long BASE_BLOCK_ENERGY_COST = 250L;
    public static final long DAMAGE_MULTIPLIER_ENERGY_COST = 50L;
    public static final long KNOCKBACK_BLOCK_ENERGY_COST = 50L;

    private static final Map<UUID, Boolean> LAST_ACTIVE_STATE = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_IMPACT_SOUND_TICK = new ConcurrentHashMap<>();
    private static final Map<Integer, Boolean> CLIENT_ACTIVE_ENTITIES = new ConcurrentHashMap<>();
    private static final Map<Integer, Long> CLIENT_IMPACT_FLASH_UNTIL = new ConcurrentHashMap<>();

    private MugenInfinityHandler() {
    }

    public static void initialize() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(MugenInfinityHandler::handleDamage);
    }

    public static boolean isMugenActive(ServerPlayer player) {
        if (player == null || player.isCreative() || player.isSpectator()) {
            return false;
        }
        SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player);
        if (!suit.hasChip(ExoSuitChipComponent.ASYMPTOTIC_INFINITY_BARRIER) || !suit.isFullSuitEquipped()) {
            return false;
        }
        return SuitSurvivalHandler.getTotalAvailableEnergy(player) > 0L;
    }

    public static void tickPlayer(ServerPlayer player, SuitPowerComponent suit) {
        if (player == null) {
            return;
        }
        boolean hasChip = suit.hasChip(ExoSuitChipComponent.ASYMPTOTIC_INFINITY_BARRIER) && suit.isFullSuitEquipped();
        boolean active = false;
        if (hasChip && !player.isCreative() && !player.isSpectator()) {
            long available = SuitSurvivalHandler.getTotalAvailableEnergy(player);
            if (available > 0L) {
                SuitSurvivalHandler.drainUpToEnergy(player, PASSIVE_ENERGY_COST_PER_TICK);
                active = true;
                if (player.tickCount % 4 == 0 && player.level() instanceof ServerLevel sl) {
                    double angle = (player.tickCount * 0.35) % (Math.PI * 2.0);
                    double rx = Math.cos(angle) * 0.95;
                    double rz = Math.sin(angle) * 0.95;
                    sl.sendParticles(ParticleTypes.END_ROD, player.getX() + rx, player.getY() + 1.0, player.getZ() + rz, 1, 0.02, 0.15, 0.02, 0.005);
                }
            }
        } else if (hasChip && (player.isCreative() || player.isSpectator())) {
            active = true;
        }

        Boolean prev = LAST_ACTIVE_STATE.get(player.getUUID());
        if (prev == null || prev != active || player.tickCount % 20 == 0) {
            LAST_ACTIVE_STATE.put(player.getUUID(), active);
            broadcastState(player, active, false);
        }
    }

    private static boolean handleDamage(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity instanceof ServerPlayer player)) {
            return true;
        }
        if (amount <= 0.0f || source.is(DamageTypes.GENERIC_KILL) || source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return true;
        }
        if (!isMugenActive(player)) {
            return true;
        }

        long cost = calculateDamageBlockCost(amount);
        SuitSurvivalHandler.drainUpToEnergy(player, cost);
        triggerDeflectionEffects(player);
        return false;
    }

    public static boolean shouldCancelKnockback(Entity entity) {
        if (entity instanceof ServerPlayer player) {
            if (!isMugenActive(player)) {
                return false;
            }
            SuitSurvivalHandler.drainUpToEnergy(player, KNOCKBACK_BLOCK_ENERGY_COST);
            triggerDeflectionEffects(player);
            return true;
        }
        if (entity instanceof Player player && player.level().isClientSide()) {
            return isClientMugenActive(player.getId());
        }
        return false;
    }

    public static long calculateDamageBlockCost(float amount) {
        return BASE_BLOCK_ENERGY_COST + Math.round(Math.max(0.0f, amount) * DAMAGE_MULTIPLIER_ENERGY_COST);
    }

    private static void triggerDeflectionEffects(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel sl)) {
            return;
        }
        long gameTime = sl.getGameTime();
        Long lastSound = LAST_IMPACT_SOUND_TICK.get(player.getUUID());
        if (lastSound == null || gameTime - lastSound >= 6L) {
            LAST_IMPACT_SOUND_TICK.put(player.getUUID(), gameTime);
            sl.playSound(null, player.getX(), player.getY() + 1.0, player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.9f, 1.65f);
            sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY() + 1.0, player.getZ(), 10, 0.65, 0.65, 0.65, 0.03);
            sl.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(), 5, 0.55, 0.55, 0.55, 0.02);
        }
        broadcastState(player, true, true);
    }

    private static void broadcastState(ServerPlayer player, boolean active, boolean impactFlash) {
        MugenStatePayload payload = new MugenStatePayload(player.getId(), active, impactFlash);
        ServerPlayNetworking.send(player, payload);
        for (ServerPlayer tracker : PlayerLookup.tracking(player)) {
            ServerPlayNetworking.send(tracker, payload);
        }
    }

    public static void updateClientState(int entityId, boolean active, boolean impactFlash) {
        if (active) {
            CLIENT_ACTIVE_ENTITIES.put(entityId, Boolean.TRUE);
        } else {
            CLIENT_ACTIVE_ENTITIES.remove(entityId);
        }
        if (impactFlash) {
            CLIENT_IMPACT_FLASH_UNTIL.put(entityId, System.currentTimeMillis() + 400L);
        }
    }

    public static boolean isClientMugenActive(int entityId) {
        return Boolean.TRUE.equals(CLIENT_ACTIVE_ENTITIES.get(entityId));
    }

    public static float getClientImpactFlashStrength(int entityId) {
        Long until = CLIENT_IMPACT_FLASH_UNTIL.get(entityId);
        if (until == null) {
            return 0.0f;
        }
        long remaining = until - System.currentTimeMillis();
        if (remaining <= 0L) {
            CLIENT_IMPACT_FLASH_UNTIL.remove(entityId);
            return 0.0f;
        }
        return Math.clamp(remaining / 400.0f, 0.0f, 1.0f);
    }

    public static void clearClientState() {
        CLIENT_ACTIVE_ENTITIES.clear();
        CLIENT_IMPACT_FLASH_UNTIL.clear();
    }

    public static void removePlayer(UUID uuid) {
        LAST_ACTIVE_STATE.remove(uuid);
        LAST_IMPACT_SOUND_TICK.remove(uuid);
    }
}
