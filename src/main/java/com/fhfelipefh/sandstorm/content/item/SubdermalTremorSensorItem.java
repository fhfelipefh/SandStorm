package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.List;

public class SubdermalTremorSensorItem extends CyborgUpgradeItem {

    public static final double SCAN_RADIUS = 64.0;
    public static final int COOLDOWN_TICKS = 40;

    public SubdermalTremorSensorItem(Properties properties) {
        super(CyborgUpgradeType.SUBDERMAL_TREMOR_SENSOR, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof ServerPlayer player) {
            if (player.tickCount % 20 == 0) {
                List<SandwormEntity> worms = level.getEntitiesOfClass(
                        SandwormEntity.class,
                        player.getBoundingBox().inflate(SCAN_RADIUS),
                        worm -> worm.isAlive() && !worm.isSpectator()
                );

                if (!worms.isEmpty()) {
                    SandwormEntity closest = worms.stream()
                            .min(Comparator.comparingDouble(w -> w.distanceToSqr(player)))
                            .orElse(worms.getFirst());

                    int distance = (int) Math.sqrt(closest.distanceToSqr(player));
                    String direction = getCardinalDirection(player, closest);

                    player.sendSystemMessage(Component.translatable(
                            "hud.sandstorm.tremor_sensor.worm_detected", distance, direction
                    ).withStyle(ChatFormatting.GOLD), true);

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.8f);
                } else {
                    int chunkX = player.getBlockX() >> 4;
                    int chunkZ = player.getBlockZ() >> 4;
                    double vibration = SeismicSurvivalHandler.getTracker().getVibration(chunkX, chunkZ);
                    if (vibration >= 15.0) {
                        int vibPercent = (int) Math.min(100.0, vibration);
                        player.sendSystemMessage(Component.translatable(
                                "hud.sandstorm.tremor_sensor.vibration_activity", vibPercent
                        ).withStyle(ChatFormatting.YELLOW), true);
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8f, 1.9f);

            List<SandwormEntity> worms = serverLevel.getEntitiesOfClass(
                    SandwormEntity.class,
                    player.getBoundingBox().inflate(SCAN_RADIUS),
                    Entity::isAlive
            );

            if (worms.isEmpty()) {
                player.sendSystemMessage(Component.translatable("hud.sandstorm.tremor_sensor.all_clear")
                        .withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(Component.translatable("hud.sandstorm.tremor_sensor.threat_count", worms.size())
                        .withStyle(ChatFormatting.RED));
            }
        }

        return InteractionResult.SUCCESS;
    }

    private static String getCardinalDirection(Player player, Entity target) {
        double dx = target.getX() - player.getX();
        double dz = target.getZ() - player.getZ();
        double angle = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
        if (angle < 0.0) {
            angle += 360.0;
        }

        if (angle >= 337.5 || angle < 22.5) {
            return "S";
        } else if (angle < 67.5) {
            return "SW";
        } else if (angle < 112.5) {
            return "W";
        } else if (angle < 157.5) {
            return "NW";
        } else if (angle < 202.5) {
            return "N";
        } else if (angle < 247.5) {
            return "NE";
        } else if (angle < 292.5) {
            return "E";
        } else {
            return "SE";
        }
    }
}
