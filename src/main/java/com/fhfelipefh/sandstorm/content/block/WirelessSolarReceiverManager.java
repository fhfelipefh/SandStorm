package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.component.WirelessChargerComponent;
import com.fhfelipefh.sandstorm.content.block.entity.WirelessSolarReceiverBlockEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.PlayerSuitSavedData;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WirelessSolarReceiverManager {
    public record ReceiverEvaluation(double radiusSq, long rate) {}
    private static final Map<ResourceKey<Level>, Map<BlockPos, Integer>> RECEIVER_MAP = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Map<BlockPos, ReceiverEvaluation>> CACHED_RECEIVER_MAP = new ConcurrentHashMap<>();
    private static final WirelessChargerComponent CHARGER_TIER1 = new WirelessChargerComponent(1);
    private static final WirelessChargerComponent CHARGER_TIER2 = new WirelessChargerComponent(2);
    private static final WirelessChargerComponent CHARGER_TIER3 = new WirelessChargerComponent(3);
    private static final WirelessChargerComponent CHARGER_TIER4 = new WirelessChargerComponent(4);
    private static final WirelessChargerComponent CHARGER_TIER5 = new WirelessChargerComponent(5);

    private static WirelessChargerComponent getCharger(int tier) {
        return switch (tier) {
            case 1 -> CHARGER_TIER1;
            case 2 -> CHARGER_TIER2;
            case 3 -> CHARGER_TIER3;
            case 4 -> CHARGER_TIER4;
            default -> CHARGER_TIER5;
        };
    }

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 != 0) {
                return;
            }
            for (ServerLevel level : server.getAllLevels()) {
                tickLevel(level);
            }
        });
    }

    public static void registerReceiver(ResourceKey<Level> dim, BlockPos pos, int tier) {
        RECEIVER_MAP.computeIfAbsent(dim, k -> new ConcurrentHashMap<>()).put(pos.immutable(), tier);
        CACHED_RECEIVER_MAP.computeIfAbsent(dim, k -> new ConcurrentHashMap<>()).put(pos.immutable(), new ReceiverEvaluation(0.0, 0L));
    }

    public static void unregisterReceiver(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, Integer> map = RECEIVER_MAP.get(dim);
        if (map != null) {
            map.remove(pos);
        }
        Map<BlockPos, ReceiverEvaluation> cMap = CACHED_RECEIVER_MAP.get(dim);
        if (cMap != null) {
            cMap.remove(pos);
        }
    }

    public static void clear() {
        RECEIVER_MAP.clear();
        CACHED_RECEIVER_MAP.clear();
    }

    public static Map<BlockPos, Integer> getReceivers(ResourceKey<Level> dim) {
        return RECEIVER_MAP.getOrDefault(dim, Map.of());
    }

    public static long getCachedReceiverChargeAt(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, ReceiverEvaluation> cachedMap = CACHED_RECEIVER_MAP.get(dim);
        if (cachedMap == null || cachedMap.isEmpty()) {
            return 0L;
        }
        long maxCharge = 0L;
        for (Map.Entry<BlockPos, ReceiverEvaluation> entry : cachedMap.entrySet()) {
            ReceiverEvaluation eval = entry.getValue();
            if (eval.radiusSq() > 0 && eval.rate() > maxCharge) {
                if (pos.distSqr(entry.getKey()) <= eval.radiusSq()) {
                    maxCharge = eval.rate();
                }
            }
        }
        return maxCharge;
    }

    public static long getPrimaryWptChargeAt(Level level, BlockPos pos) {
        long thermalCharge = ThermalGeneratorManager.getWptChargeAt(level, pos);
        long basicThermalCharge = BasicThermalGeneratorManager.getWptChargeAt(level, pos);
        long accumulatorCharge = SolidStateAccumulatorManager.getWptChargeAt(level, pos);
        long exchangerCharge = SupercriticalHeatExchangerManager.getWptChargeAt(level, pos);
        long maxCharge = Math.max(Math.max(thermalCharge, basicThermalCharge), Math.max(accumulatorCharge, exchangerCharge));
        Map<BlockPos, ReceiverEvaluation> cachedMap = CACHED_RECEIVER_MAP.get(level.dimension());
        if (cachedMap != null && !cachedMap.isEmpty()) {
            for (Map.Entry<BlockPos, ReceiverEvaluation> entry : cachedMap.entrySet()) {
                ReceiverEvaluation eval = entry.getValue();
                if (eval.radiusSq() > 0 && eval.rate() > maxCharge) {
                    if (pos.distSqr(entry.getKey()) <= eval.radiusSq()) {
                        maxCharge = eval.rate();
                    }
                }
            }
            return maxCharge;
        }
        Map<BlockPos, Integer> map = RECEIVER_MAP.get(level.dimension());
        if (map == null || map.isEmpty()) {
            return maxCharge;
        }
        double weather = SandstormWeatherHandler.getWeather().getSolarEfficiencyMultiplier();
        boolean isDay = level.getSkyDarken() < 4;
        int skyDarken = level.getSkyDarken();

        for (Map.Entry<BlockPos, Integer> entry : map.entrySet()) {
            BlockPos rPos = entry.getKey();
            int tier = entry.getValue();
            if (!level.isLoaded(rPos)) {
                continue;
            }
            boolean canSeeSky = level.canSeeSky(rPos.above());
            double receiverWeather = weather;
            BlockEntity be = level.getBlockEntity(rPos);
            if (be instanceof WirelessSolarReceiverBlockEntity wbe && wbe.hasRefractionLens()) {
                receiverWeather = Math.max(receiverWeather, 0.40);
            }
            WirelessChargerComponent charger = getCharger(tier);
            double effectiveRadius = charger.calculateEffectiveRadius(canSeeSky, isDay, skyDarken, receiverWeather);
            long rate = charger.calculateTransferRate(canSeeSky, isDay, skyDarken, receiverWeather);
            if (effectiveRadius > 0 && rate > 0) {
                if (pos.distSqr(rPos) <= effectiveRadius * effectiveRadius) {
                    if (rate > maxCharge) {
                        maxCharge = rate;
                    }
                }
            }
        }
        return maxCharge;
    }

    public static long getWptChargeAt(Level level, BlockPos pos) {
        long primaryCharge = getPrimaryWptChargeAt(level, pos);
        long relayCharge = WptRelayTowerManager.getWptChargeAt(level, pos);
        return Math.max(primaryCharge, relayCharge);
    }

    public static void tickLevel(ServerLevel level) {
        if (level.players().isEmpty()) {
            return;
        }

        Map<BlockPos, Integer> map = RECEIVER_MAP.get(level.dimension());
        if (map == null || map.isEmpty()) {
            return;
        }

        double weather = SandstormWeatherHandler.getWeather().getSolarEfficiencyMultiplier();
        boolean isDay = level.getSkyDarken() < 4;
        int skyDarken = level.getSkyDarken();

        for (Map.Entry<BlockPos, Integer> entry : map.entrySet()) {
            BlockPos pos = entry.getKey();
            int tier = entry.getValue();

            if (!level.isLoaded(pos)) {
                continue;
            }

            if (!(level.getBlockState(pos).getBlock() instanceof WirelessSolarReceiverBlock)) {
                map.remove(pos);
                continue;
            }

            boolean canSeeSky = level.canSeeSky(pos.above());
            double receiverWeather = weather;
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof WirelessSolarReceiverBlockEntity wbe && wbe.hasRefractionLens()) {
                receiverWeather = Math.max(receiverWeather, 0.40);
            }
            WirelessChargerComponent charger = getCharger(tier);
            double effectiveRadius = charger.calculateEffectiveRadius(canSeeSky, isDay, skyDarken, receiverWeather);
            long transferRatePerTick = charger.calculateTransferRate(canSeeSky, isDay, skyDarken, receiverWeather);

            double radiusSq = effectiveRadius * effectiveRadius;
            CACHED_RECEIVER_MAP.computeIfAbsent(level.dimension(), k -> new ConcurrentHashMap<>())
                    .put(pos.immutable(), new ReceiverEvaluation(radiusSq, transferRatePerTick));

            if (effectiveRadius <= 0.0 || transferRatePerTick <= 0) {
                continue;
            }

            for (ServerPlayer player : level.players()) {
                if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= radiusSq) {
                    if (SuitSurvivalHandler.countEquippedSuitPieces(player) > 0) {
                        SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player);
                        long playerRate = transferRatePerTick;
                        if (playerRate < charger.calculateTransferRate(canSeeSky, isDay, skyDarken, 0.40)
                                && PlayerSuitSavedData.get(level.getServer()).hasUpgrade(player.getUUID(), "advanced_lens")) {
                            playerRate = charger.calculateTransferRate(canSeeSky, isDay, skyDarken, 0.40);
                        }
                        long totalTransfer = playerRate * 20;
                        long received = suit.getEnergyStorage().receiveEnergy(totalTransfer);
                        boolean batteryNearlyFull = suit.getEnergyStorage().getStoredEnergy() >= suit.getEnergyStorage().getCapacity() * 0.98;
                        if (received > 0 && !batteryNearlyFull && player.tickCount % 200 == 0) {
                            level.playSound(null, player.blockPosition(), SandStormSoundEvents.SUIT_SOLAR_CHARGE, SoundSource.PLAYERS, 0.08f, 1.0f);
                        }
                    }
                }
            }
        }
    }
}
