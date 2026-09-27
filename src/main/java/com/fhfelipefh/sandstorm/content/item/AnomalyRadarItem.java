package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.RadarComponent;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
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

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            BlockPos playerPos = serverPlayer.blockPosition();
            List<RadarComponent.AnomalyTarget> targets = new ArrayList<>();

            BlockPos min = playerPos.offset(-32, -16, -32);
            BlockPos max = playerPos.offset(32, 16, 32);

            BlockPos.betweenClosedStream(min, max)
                    .filter(pos -> {
                        BlockState s = level.getBlockState(pos);
                        return s.is(SandStormBlocks.BURIED_TECH_RUINS)
                                || s.is(SandStormBlocks.ANCIENT_DATA_CORE)
                                || s.is(SandStormBlocks.CHEMICAL_REFINERY);
                    })
                    .limit(32)
                    .forEach(pos -> {
                        BlockState s = level.getBlockState(pos);
                        String type;
                        if (s.is(SandStormBlocks.CHEMICAL_REFINERY)) {
                            type = "fuel_silo";
                        } else if (s.is(SandStormBlocks.ANCIENT_DATA_CORE)) {
                            type = "outpost_terminal";
                        } else {
                            type = "buried_ruins";
                        }
                        targets.add(new RadarComponent.AnomalyTarget(
                                pos.getX(), pos.getY(), pos.getZ(), type));
                    });

            Optional<RadarComponent.ScanResult> scanResult = radar.findClosestAnomaly(
                    playerPos.getX(),
                    playerPos.getY(),
                    playerPos.getZ(),
                    targets,
                    SCAN_RADIUS
            );

            boolean isIonStorm = SandstormWeatherHandler.getWeather().isActive()
                    && SandstormWeatherHandler.getWeather().getIntensity() >= 0.70;

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
