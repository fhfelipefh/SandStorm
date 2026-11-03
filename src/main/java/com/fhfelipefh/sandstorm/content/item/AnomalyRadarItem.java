package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.RadarComponent;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.network.SyncAnomalyRadarPayload;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnomalyRadarItem extends Item {
    public static final int COOLDOWN_TICKS = 20;
    public static final double SCAN_RADIUS = 96.0;

    private final RadarComponent radar = new RadarComponent();

    public AnomalyRadarItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    public RadarComponent getRadar() {
        return radar;
    }

    public static List<SyncAnomalyRadarPayload.AnomalyPing> collectAnomalies(Level level, BlockPos center, double radius) {
        List<SyncAnomalyRadarPayload.AnomalyPing> pings = new ArrayList<>();
        int radInt = (int) radius;
        int minX = center.getX() - radInt;
        int maxX = center.getX() + radInt;
        int minZ = center.getZ() - radInt;
        int maxZ = center.getZ() + radInt;
        int minY = Math.max(level.getMinY(), center.getY() - 32);
        int maxY = Math.min(level.getMaxY(), center.getY() + 32);

        int minChunkX = minX >> 4;
        int maxChunkX = maxX >> 4;
        int minChunkZ = minZ >> 4;
        int maxChunkZ = maxZ >> 4;

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if (!level.hasChunk(cx, cz)) {
                    continue;
                }
                LevelChunk chunk = level.getChunk(cx, cz);
                for (BlockPos bePos : chunk.getBlockEntitiesPos()) {
                    if (bePos.getX() >= minX && bePos.getX() <= maxX && bePos.getZ() >= minZ && bePos.getZ() <= maxZ) {
                        BlockState state = chunk.getBlockState(bePos);
                        if (state.is(SandStormBlocks.CHEMICAL_REFINERY) || state.is(SandStormBlocks.RADIOACTIVE_WASTE_BARREL)) {
                            addDistinctPing(pings, bePos.getX(), bePos.getY(), bePos.getZ(), "fuel_silo");
                        } else if (state.is(SandStormBlocks.VAULT_DOOR_CONTROLLER) || state.is(SandStormBlocks.CRYOSTASIS_POD) || state.is(SandStormBlocks.CRYO_REVIVAL_VAT) || state.is(SandStormBlocks.SEISMIC_RESONANCE_CATALYST)) {
                            addDistinctPing(pings, bePos.getX(), bePos.getY(), bePos.getZ(), "seismic_vault");
                        }
                    }
                }
            }
        }

        for (int x = minX; x <= maxX; x += 4) {
            for (int z = minZ; z <= maxZ; z += 4) {
                if (!level.hasChunk(x >> 4, z >> 4)) {
                    continue;
                }
                for (int y = minY; y <= maxY; y += 2) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.is(SandStormBlocks.BURIED_TECH_RUINS)) {
                        addDistinctPing(pings, x, y, z, "buried_ruins");
                        break;
                    } else if (state.is(SandStormBlocks.ANCIENT_DATA_CORE) || state.is(SandStormBlocks.TACTICAL_RADAR_CONSOLE) || state.is(SandStormBlocks.BROKEN_RADAR_CONSOLE)) {
                        addDistinctPing(pings, x, y, z, "outpost_terminal");
                        break;
                    }
                }
            }
        }

        return pings;
    }

    private static void addDistinctPing(List<SyncAnomalyRadarPayload.AnomalyPing> list, int x, int y, int z, String type) {
        for (SyncAnomalyRadarPayload.AnomalyPing p : list) {
            if (p.type().equals(type)) {
                int dx = p.x() - x;
                int dy = p.y() - y;
                int dz = p.z() - z;
                if (dx * dx + dy * dy + dz * dz < 256) {
                    return;
                }
            }
        }
        list.add(new SyncAnomalyRadarPayload.AnomalyPing(x, y, z, type));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            BlockPos playerPos = serverPlayer.blockPosition();
            List<SyncAnomalyRadarPayload.AnomalyPing> anomalyPings = collectAnomalies(level, playerPos, SCAN_RADIUS);

            List<RadarComponent.AnomalyTarget> targets = new ArrayList<>();
            for (SyncAnomalyRadarPayload.AnomalyPing p : anomalyPings) {
                targets.add(new RadarComponent.AnomalyTarget(p.x(), p.y(), p.z(), p.type()));
            }

            Optional<RadarComponent.ScanResult> scanResult = radar.findClosestAnomaly(
                    playerPos.getX(),
                    playerPos.getY(),
                    playerPos.getZ(),
                    targets,
                    SCAN_RADIUS
            );

            boolean isIonStorm = SandstormWeatherHandler.getWeather().isActive()
                    && SandstormWeatherHandler.getWeather().getIntensity() >= 0.70;

            ServerPlayNetworking.send(serverPlayer, new SyncAnomalyRadarPayload(
                    playerPos.getX(),
                    playerPos.getY(),
                    playerPos.getZ(),
                    serverPlayer.getYRot(),
                    isIonStorm,
                    anomalyPings
            ));

            if (isIonStorm) {
                serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.radar_interference"), true);
                level.playSound(null, playerPos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.PLAYERS, 0.9f, 0.6f);
                level.playSound(null, playerPos, SoundEvents.ITEM_BREAK.value(), SoundSource.PLAYERS, 0.7f, 1.4f);

                if (scanResult.isPresent()) {
                    RadarComponent.ScanResult res = scanResult.get();
                    int scrambledDist = Math.max(5, (int) (res.horizontalDistance() * (0.6 + level.getRandom().nextDouble() * 0.8)));
                    String[] erraticDirs = {"???", "ERR", "!#$", "~~~", "NaN"};
                    String fakeDir = erraticDirs[level.getRandom().nextInt(erraticDirs.length)];
                    serverPlayer.sendSystemMessage(Component.translatable(
                            "telemetry.sandstorm.radar_signal_corrupted",
                            scrambledDist,
                            fakeDir
                    ));
                }
            } else if (scanResult.isPresent()) {
                RadarComponent.ScanResult res = scanResult.get();
                serverPlayer.sendSystemMessage(Component.translatable(
                        "telemetry.sandstorm.radar_signal",
                        (int) res.horizontalDistance(),
                        res.cardinalDirection()
                ), true);
                level.playSound(null, playerPos, SandStormSoundEvents.ANOMALY_RADAR_PING, SoundSource.PLAYERS, 0.9f, 1.8f);
            } else {
                serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.radar_none"), true);
                level.playSound(null, playerPos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.6f, 0.8f);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
