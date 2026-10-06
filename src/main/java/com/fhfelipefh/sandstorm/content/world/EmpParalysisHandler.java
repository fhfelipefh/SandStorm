package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.entity.BuilderDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CargoDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CrawlerDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CyberHoundEntity;
import com.fhfelipefh.sandstorm.content.entity.CyberneticGolemEntity;
import com.fhfelipefh.sandstorm.content.entity.DerelictAutomatonEntity;
import com.fhfelipefh.sandstorm.content.entity.LaborerUnitEntity;
import com.fhfelipefh.sandstorm.content.entity.ScoutDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.ScrapSentinelEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EmpParalysisHandler {
    private static final Map<UUID, Integer> PARALYZED_TICKS = new ConcurrentHashMap<>();

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(EmpParalysisHandler::handleServerTick);
    }

    public static boolean isAndroidOrAutomaton(LivingEntity entity) {
        if (entity == null || entity instanceof Player) {
            return false;
        }
        if (entity instanceof DerelictAutomatonEntity
                || entity instanceof LaborerUnitEntity
                || entity instanceof ScoutDroneEntity
                || entity instanceof CrawlerDroneEntity
                || entity instanceof CyberHoundEntity
                || entity instanceof CyberneticGolemEntity
                || entity instanceof ScrapSentinelEntity
                || entity instanceof BuilderDroneEntity
                || entity instanceof CargoDroneEntity
                || entity instanceof CyborgEntity) {
            return true;
        }
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (id != null) {
            String path = id.getPath();
            return path.contains("automaton")
                    || path.contains("drone")
                    || path.contains("hound")
                    || path.contains("laborer")
                    || path.contains("cyborg")
                    || path.contains("golem")
                    || path.contains("sentinel")
                    || path.contains("robot");
        }
        return false;
    }

    public static void paralyze(LivingEntity entity, int durationTicks) {
        if (entity == null || !entity.isAlive()) {
            return;
        }
        PARALYZED_TICKS.put(entity.getUUID(), durationTicks);
        entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, durationTicks, 127, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, durationTicks, 127, false, false, false));
        if (entity instanceof Mob mob) {
            mob.setTarget(null);
            mob.getNavigation().stop();
        }
        if (entity instanceof DerelictAutomatonEntity derelict) {
            derelict.setFuse(0);
        }
        if (entity instanceof CyberneticGolemEntity golem) {
            golem.setOverdrive(false);
        }
        entity.setDeltaMovement(0.0, Math.min(0.0, entity.getDeltaMovement().y), 0.0);
    }

    public static boolean isParalyzed(Entity entity) {
        return entity != null && PARALYZED_TICKS.containsKey(entity.getUUID());
    }

    public static int getParalyzedCount() {
        return PARALYZED_TICKS.size();
    }

    private static void handleServerTick(MinecraftServer server) {
        if (PARALYZED_TICKS.isEmpty()) {
            return;
        }

        for (ServerLevel level : server.getAllLevels()) {
            Iterator<Map.Entry<UUID, Integer>> iterator = PARALYZED_TICKS.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, Integer> entry = iterator.next();
                UUID uuid = entry.getKey();
                Entity entity = level.getEntity(uuid);
                if (entity == null) {
                    continue;
                }
                if (!entity.isAlive() || !(entity instanceof LivingEntity living)) {
                    iterator.remove();
                    continue;
                }

                int remaining = entry.getValue() - 1;
                if (remaining <= 0) {
                    iterator.remove();
                    level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            living.getX(), living.getY() + 0.8, living.getZ(),
                            6, 0.2, 0.3, 0.2, 0.02);
                    level.playSound(null, living.getX(), living.getY(), living.getZ(),
                            SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 0.6f, 1.8f);
                    living.removeEffect(MobEffects.SLOWNESS);
                    living.removeEffect(MobEffects.WEAKNESS);
                    continue;
                }

                entry.setValue(remaining);
                living.setDeltaMovement(0.0, Math.min(0.0, living.getDeltaMovement().y), 0.0);
                if (living instanceof Mob mob) {
                    mob.setTarget(null);
                    mob.getNavigation().stop();
                }

                if (living.tickCount % 6 == 0) {
                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            living.getRandomX(0.4), living.getY() + living.getRandom().nextDouble() * living.getBbHeight(), living.getRandomZ(0.4),
                            2, 0.06, 0.06, 0.06, 0.03);
                }

                if (living.tickCount % 35 == 0) {
                    level.playSound(null, living.getX(), living.getY(), living.getZ(),
                            SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.NEUTRAL, 0.3f, 1.8f);
                }
            }
        }
    }
}
