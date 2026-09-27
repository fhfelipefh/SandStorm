package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.SandstormWeatherComponent;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.network.SuitSyncPayload;
import com.fhfelipefh.sandstorm.content.quest.PlayerQuestSavedData;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.world.SandStormWorldHelper;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import com.fhfelipefh.sandstorm.content.survival.FlashlightStateServer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SuitSurvivalHandler {
    private record LastSyncState(long energy, long capacity, double temperature, int armorCount, int tick) {}
    private static final Map<UUID, SuitPowerComponent> PLAYER_SUIT_MAP = new ConcurrentHashMap<>();
    private static final Map<UUID, LastSyncState> LAST_SYNC_MAP = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> PROPELLANT_TICKS_MAP = new ConcurrentHashMap<>();
    private static MinecraftServer currentServer;

    public static void initialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> currentServer = server);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            currentServer = server;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                handlePlayerTick(player);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            currentServer = server;
            ServerPlayer player = handler.getPlayer();
            if (player != null) {
                SuitPowerComponent suit = getOrCreateSuit(player);
                int armorCount = countEquippedSuitPieces(player);
                suit.updateEquippedArmorCount(armorCount);
                long currentEnergy = suit.getEnergyStorage().getStoredEnergy();
                long currentCapacity = suit.getEnergyStorage().getCapacity();
                double currentTemp = suit.getThermal().getCurrentTemperature();
                LAST_SYNC_MAP.put(player.getUUID(), new LastSyncState(currentEnergy, currentCapacity, currentTemp, armorCount, player.tickCount));
                ServerPlayNetworking.send(
                        player,
                        new SuitSyncPayload(
                                currentEnergy,
                                currentCapacity,
                                currentTemp,
                                armorCount
                        )
                );
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (player != null) {
                FlashlightStateServer.removePlayer(player.getUUID());
                savePlayerSuit(player, server);
                removePlayer(player.getUUID());
            }
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                savePlayerSuit(player, server);
            }
            PLAYER_SUIT_MAP.clear();
            LAST_SYNC_MAP.clear();
            currentServer = null;
        });
    }

    public static void handlePlayerTick(ServerPlayer player) {
        boolean sandStormWorld = SandStormWorldHelper.isSandStormWorld(player.level());
        if (sandStormWorld) {
            FusedSpaceSuitHandler.enforceFusedSuit(player);
        }
        SuitPowerComponent suit = getOrCreateSuit(player);
        int armorCount = countEquippedSuitPieces(player);
        suit.updateEquippedArmorCount(armorCount);

        PlayerSuitSavedData suitData = PlayerSuitSavedData.get((ServerLevel) player.level());
        if (suitData.hasUpgrade(player.getUUID(), "battery")) {
            suit.setCapacity(150000L);
        } else {
            suit.setCapacity(100000L);
        }

        if (suitData.hasUpgrade(player.getUUID(), "jetpack")) {
            handleJetpackFlight(player);
        } else if (!player.isCreative() && !player.isSpectator() && player.getAbilities().mayfly) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }

        BlockPos pos = player.blockPosition();
        boolean canSeeSky = (player.tickCount % 10 == 0)
                ? player.level().canSeeSkyFromBelowWater(pos)
                : player.level().canSeeSky(pos);
        boolean isDay = player.level().getSkyDarken() < 4;
        boolean exposedToSunlight = isDay && canSeeSky && pos.getY() >= 50;
        boolean underground = pos.getY() < 50;

        double ambientTemperature = calculateAmbientTemperature(player, pos.getY(), canSeeSky, isDay);
        if (suitData.hasUpgrade(player.getUUID(), "thermal")) {
            ambientTemperature = 37.0 + (ambientTemperature - 37.0) * 0.5;
        }
        SandstormWeatherComponent weather = SandstormWeatherHandler.getWeather();
        double solarMultiplier = weather.getSolarEfficiencyMultiplier();

        suit.tick(exposedToSunlight, ambientTemperature, underground, solarMultiplier);

        if (suit.getEnergyStorage().getStoredEnergy() >= suit.getEnergyStorage().getCapacity() * 0.6) {
            if (!player.entityTags().contains("sandstorm.battery_60")) {
                player.addTag("sandstorm.battery_60");
                MinecraftServer server = player.level().getServer();
                if (server != null) {
                    PlayerQuestSavedData data = PlayerQuestSavedData.get(server);
                    data.markConditionCompleted(player.getUUID(), "sandstorm.battery_60");
                    QuestRewardHandler.syncPlayerQuests(player, data);
                    QuestRewardHandler.checkPlayerNotifications(player, data);
                }
            }
        }

        long currentEnergy = suit.getEnergyStorage().getStoredEnergy();
        long currentCapacity = suit.getEnergyStorage().getCapacity();
        double currentTemp = suit.getThermal().getCurrentTemperature();

        LastSyncState last = LAST_SYNC_MAP.get(player.getUUID());
        boolean energyStateChanged = last == null || last.energy != currentEnergy;
        boolean shouldSync = last == null
                || (player.tickCount - last.tick >= 20)
                || Math.abs(currentEnergy - last.energy) >= 5
                || (energyStateChanged && (currentEnergy == 0 || currentEnergy == currentCapacity))
                || Math.abs(currentTemp - last.temperature) >= 0.1
                || armorCount != last.armorCount;

        if (shouldSync) {
            LAST_SYNC_MAP.put(player.getUUID(), new LastSyncState(currentEnergy, currentCapacity, currentTemp, armorCount, player.tickCount));
            ServerPlayNetworking.send(
                    player,
                    new SuitSyncPayload(
                            currentEnergy,
                            currentCapacity,
                            currentTemp,
                            armorCount
                    )
            );
            MinecraftServer server = player.level().getServer();
            if (server != null) {
                PlayerSuitSavedData.get(server).setSuitData(player.getUUID(), currentEnergy, currentTemp);
            }
            updatePlayerSuitTags(player, currentEnergy, currentTemp);
        }

        if (suit.isFullSuitEquipped()) {
            boolean lowBattery = suit.getEnergyStorage().getStoredEnergy() > 0 &&
                    suit.getEnergyStorage().getStoredEnergy() < suit.getEnergyStorage().getCapacity() * 0.15;
            if (lowBattery && player.tickCount % 160 == 0) {
                player.level().playSound(null, pos, SandStormSoundEvents.SUIT_BATTERY_LOW, SoundSource.PLAYERS, 0.25f, 1.0f);
            }
            if (exposedToSunlight && !underground && player.tickCount % 200 == 0 && suit.getEnergyStorage().getStoredEnergy() < suit.getEnergyStorage().getCapacity() * 0.98) {
                player.level().playSound(null, pos, SandStormSoundEvents.SUIT_SOLAR_CHARGE, SoundSource.PLAYERS, 0.10f, 1.0f);
            }

            int flashlightMode = FlashlightStateServer.getMode(player.getUUID());
            if (flashlightMode > 0) {
                boolean hadEnergy = suit.getEnergyStorage().getStoredEnergy() > 0;
                long baseDrain = suitData.hasUpgrade(player.getUUID(), "visor") ? 1L : 2L;
                long multiplier = switch (flashlightMode) {
                    case 2 -> 2L;
                    case 3 -> 4L;
                    default -> 1L;
                };
                long drain = baseDrain * multiplier;
                suit.consumeEnergy(drain);
                if (hadEnergy && suit.getEnergyStorage().getStoredEnergy() == 0) {
                    FlashlightStateServer.setFlashlightMode(player.getUUID(), 0);
                }
            }
        }

        if (!player.isCreative() && !player.isSpectator() && sandStormWorld) {
            if (suit.getThermal().isOverheating() && player.tickCount % 30 == 0) {
                float excess = (float) (suit.getThermal().getCurrentTemperature() - 50.0);
                float damage = Math.max(1.0f, 1.0f + excess * 0.25f);
                player.hurtServer(player.level(), player.damageSources().dryOut(), damage);
            } else if (suit.getThermal().isFreezing() && player.tickCount % 30 == 0) {
                float deficit = (float) (10.0 - suit.getThermal().getCurrentTemperature());
                float damage = Math.max(1.0f, 1.0f + deficit * 0.25f);
                player.hurtServer(player.level(), player.damageSources().freeze(), damage);
                player.setTicksFrozen(Math.min(player.getTicksFrozen() + 40, 140));
            }
        }

        if (sandStormWorld && weather.canCauseSandDamage() && canSeeSky && !suit.isFullSuitEquipped()) {
            if (player.tickCount % 40 == 0) {
                player.hurtServer(player.level(), player.damageSources().dryOut(), 1.0f);
            }
        }
    }

    public static int countEquippedSuitPieces(ServerPlayer player) {
        int count = 0;
        if (isSuitItem(player.getItemBySlot(EquipmentSlot.HEAD), SandStormItems.SPACE_SUIT_HELMET)) {
            count++;
        }
        if (isSuitItem(player.getItemBySlot(EquipmentSlot.CHEST), SandStormItems.SPACE_SUIT_CHESTPLATE)) {
            count++;
        }
        if (isSuitItem(player.getItemBySlot(EquipmentSlot.LEGS), SandStormItems.SPACE_SUIT_LEGGINGS)) {
            count++;
        }
        if (isSuitItem(player.getItemBySlot(EquipmentSlot.FEET), SandStormItems.SPACE_SUIT_BOOTS)) {
            count++;
        }
        return count;
    }

    private static boolean isSuitItem(ItemStack stack, Item expected) {
        return !stack.isEmpty() && stack.is(expected);
    }

    public static double calculateAmbientTemperature(ServerPlayer player) {
        BlockPos pos = player.blockPosition();
        boolean canSeeSky = (player.tickCount % 10 == 0)
                ? player.level().canSeeSkyFromBelowWater(pos)
                : player.level().canSeeSky(pos);
        boolean isDay = player.level().getSkyDarken() < 4;
        return calculateAmbientTemperature(player, pos.getY(), canSeeSky, isDay);
    }

    public static double calculateAmbientTemperature(ServerPlayer player, int posY, boolean canSeeSky, boolean isDay) {
        if (posY < 50) {
            return Math.max(2.0, 20.0 - (50 - posY) * 0.25);
        }
        if (canSeeSky && isDay) {
            return 48.0 + 0.3 * Math.sin(player.tickCount * 0.05);
        } else if (canSeeSky) {
            return 8.0 + 0.2 * Math.cos(player.tickCount * 0.05);
        } else if (isDay) {
            return 24.0 + 0.2 * Math.sin(player.tickCount * 0.05);
        } else {
            return 16.0;
        }
    }

    public static SuitPowerComponent getOrCreateSuit(UUID playerUuid) {
        SuitPowerComponent suit = PLAYER_SUIT_MAP.get(playerUuid);
        if (suit == null) {
            suit = new SuitPowerComponent();
            if (currentServer != null) {
                PlayerSuitSavedData data = PlayerSuitSavedData.get(currentServer);
                PlayerSuitSavedData.Entry entry = data.getSuitData(playerUuid);
                if (entry != null) {
                    suit.getEnergyStorage().setStoredEnergy(entry.energy());
                    suit.getThermal().setCurrentTemperature(entry.temperature());
                }
            }
            PLAYER_SUIT_MAP.put(playerUuid, suit);
        }
        return suit;
    }

    public static SuitPowerComponent getOrCreateSuit(ServerPlayer player) {
        UUID uuid = player.getUUID();
        SuitPowerComponent suit = PLAYER_SUIT_MAP.get(uuid);
        if (suit == null) {
            suit = new SuitPowerComponent();
            MinecraftServer server = player.level().getServer() != null ? player.level().getServer() : currentServer;
            long restoredEnergy = -1L;
            double restoredTemp = -1.0;

            if (server != null) {
                PlayerSuitSavedData data = PlayerSuitSavedData.get(server);
                PlayerSuitSavedData.Entry entry = data.getSuitData(uuid);
                if (entry != null) {
                    restoredEnergy = entry.energy();
                    restoredTemp = entry.temperature();
                }
            }

            if (restoredEnergy < 0) {
                for (String tag : player.entityTags()) {
                    if (tag.startsWith("sandstorm.suit_energy:")) {
                        try {
                            restoredEnergy = Long.parseLong(tag.substring(22));
                        } catch (NumberFormatException ignored) {
                        }
                    } else if (tag.startsWith("sandstorm.suit_temp:")) {
                        try {
                            restoredTemp = Double.parseDouble(tag.substring(20)) / 100.0;
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            if (restoredEnergy >= 0) {
                suit.getEnergyStorage().setStoredEnergy(restoredEnergy);
                if (restoredTemp > 0) {
                    suit.getThermal().setCurrentTemperature(restoredTemp);
                }
            } else if (player.entityTags().contains("sandstorm.fused_suit")) {
                suit.getEnergyStorage().setStoredEnergy(50000L);
                suit.getThermal().setCurrentTemperature(37.0);
            }

            PLAYER_SUIT_MAP.put(uuid, suit);
        }
        return suit;
    }

    public static void savePlayerSuit(ServerPlayer player, MinecraftServer server) {
        SuitPowerComponent suit = PLAYER_SUIT_MAP.get(player.getUUID());
        if (suit != null) {
            long energy = suit.getEnergyStorage().getStoredEnergy();
            double temp = suit.getThermal().getCurrentTemperature();
            updatePlayerSuitTags(player, energy, temp);

            MinecraftServer s = server != null ? server : (player.level().getServer() != null ? player.level().getServer() : currentServer);
            if (s != null) {
                PlayerSuitSavedData.get(s).setSuitData(
                        player.getUUID(),
                        energy,
                        temp
                );
            }
        }
    }

    public static void savePlayerSuit(ServerPlayer player) {
        savePlayerSuit(player, null);
    }

    private static void updatePlayerSuitTags(ServerPlayer player, long energy, double temp) {
        for (String tag : new ArrayList<>(player.entityTags())) {
            if (tag.startsWith("sandstorm.suit_energy:") || tag.startsWith("sandstorm.suit_temp:")) {
                player.removeTag(tag);
            }
        }
        player.addTag("sandstorm.suit_energy:" + energy);
        player.addTag("sandstorm.suit_temp:" + Math.round(temp * 100.0));
    }

    private static boolean hasPropellant(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(SandStormItems.PROPELLANT_CARTRIDGE)) {
                return true;
            }
        }
        return false;
    }

    private static boolean consumePropellant(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(SandStormItems.PROPELLANT_CARTRIDGE)) {
                stack.shrink(1);
                ItemStack empty = new ItemStack(SandStormItems.EMPTY_CARTRIDGE);
                if (!player.getInventory().add(empty)) {
                    player.spawnAtLocation((ServerLevel) player.level(), empty);
                }
                return true;
            }
        }
        return false;
    }

    private static void handleJetpackFlight(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        UUID uuid = player.getUUID();
        int currentFuel = PROPELLANT_TICKS_MAP.getOrDefault(uuid, 0);
        boolean hasCartridge = hasPropellant(player);

        if (currentFuel > 0 || hasCartridge) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        }

        if (player.getAbilities().flying) {
            if (currentFuel <= 0) {
                if (consumePropellant(player)) {
                    currentFuel = 1200;
                    PROPELLANT_TICKS_MAP.put(uuid, currentFuel);
                    player.sendSystemMessage(Component.translatable("message.sandstorm.jetpack_refueled"), true);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.8f, 1.2f);
                } else {
                    player.getAbilities().flying = false;
                    player.getAbilities().mayfly = false;
                    player.onUpdateAbilities();
                    player.sendSystemMessage(Component.translatable("message.sandstorm.jetpack_out_of_fuel"), true);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.8f, 1.0f);
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, false, false, true));
                    PROPELLANT_TICKS_MAP.put(uuid, 0);
                    return;
                }
            }

            currentFuel--;
            PROPELLANT_TICKS_MAP.put(uuid, currentFuel);
            player.fallDistance = 0.0f;

            ServerLevel sLevel = (ServerLevel) player.level();
            double px = player.getX();
            double py = player.getY() + 0.2;
            double pz = player.getZ();
            sLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 2, 0.1, 0.05, 0.1, 0.01);
            sLevel.sendParticles(ParticleTypes.SMOKE, px, py, pz, 1, 0.05, 0.05, 0.05, 0.02);

            if (player.isSprinting()) {
                Vec3 look = player.getLookAngle();
                Vec3 motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.add(look.x * 0.06, look.y * 0.04, look.z * 0.06));
                if (currentFuel > 0) {
                    currentFuel--;
                    PROPELLANT_TICKS_MAP.put(uuid, currentFuel);
                }
            }

            if (currentFuel == 200) {
                player.sendSystemMessage(Component.translatable("message.sandstorm.jetpack_fuel_low"), true);
                sLevel.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 0.8f, 1.5f);
            }
        } else {
            if (currentFuel <= 0 && !hasCartridge && player.getAbilities().mayfly) {
                player.getAbilities().mayfly = false;
                player.onUpdateAbilities();
            }
        }
    }

    public static void removePlayer(UUID playerUuid) {
        PLAYER_SUIT_MAP.remove(playerUuid);
        LAST_SYNC_MAP.remove(playerUuid);
        PROPELLANT_TICKS_MAP.remove(playerUuid);
    }
}
