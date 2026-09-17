package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.RadarComponent;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
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

            BlockPos.betweenClosedStream(
                    playerPos.offset(-48, -24, -48),
                    playerPos.offset(48, 24, 48)
            ).forEach(pos -> {
                BlockState state = level.getBlockState(pos);
                if (state.is(SandStormBlocks.BURIED_TECH_RUINS)) {
                    targets.add(new RadarComponent.AnomalyTarget(pos.getX(), pos.getY(), pos.getZ(), "buried_ruins"));
                } else if (state.is(SandStormBlocks.ANCIENT_DATA_CORE)) {
                    targets.add(new RadarComponent.AnomalyTarget(pos.getX(), pos.getY(), pos.getZ(), "ancient_data_core"));
                }
            });

            Optional<RadarComponent.ScanResult> scanResult = radar.findClosestAnomaly(
                    playerPos.getX(),
                    playerPos.getY(),
                    playerPos.getZ(),
                    targets,
                    SCAN_RADIUS
            );

            if (scanResult.isPresent()) {
                RadarComponent.ScanResult res = scanResult.get();
                serverPlayer.sendSystemMessage(Component.translatable(
                        "telemetry.sandstorm.radar_signal",
                        (int) res.horizontalDistance(),
                        res.cardinalDirection()
                ), true);
                level.playSound(null, playerPos, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.9f, 1.8f);
            } else {
                serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.radar_none"), true);
                level.playSound(null, playerPos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.6f, 0.8f);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
