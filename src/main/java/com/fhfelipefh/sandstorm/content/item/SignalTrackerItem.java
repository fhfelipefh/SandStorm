package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.tracker.TransponderTrackerSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class SignalTrackerItem extends Item {

    public SignalTrackerItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            TransponderTrackerSavedData data = TransponderTrackerSavedData.get(serverLevel);
            List<TransponderTrackerSavedData.TransponderRecord> records = data.getRecords();
            if (records.isEmpty()) {
                player.sendOverlayMessage(Component.translatable("message.sandstorm.signal_tracker.no_signals"));
                return InteractionResult.SUCCESS;
            }
            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = customData.copyTag();
            int targetIndex = tag.getInt("target_index").orElse(0);
            targetIndex = (targetIndex + 1) % records.size();
            tag.putInt("target_index", targetIndex);
            tag.putString("target_uuid", records.get(targetIndex).id().toString());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            player.sendOverlayMessage(Component.translatable("message.sandstorm.signal_tracker.target_selected", records.get(targetIndex).name()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof ServerPlayer player && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)) {
            if (level.getGameTime() % 10 == 0) {
                TransponderTrackerSavedData data = TransponderTrackerSavedData.get(level);
                List<TransponderTrackerSavedData.TransponderRecord> records = data.getRecords();
                if (records.isEmpty()) {
                    return;
                }
                for (TransponderTrackerSavedData.TransponderRecord record : records) {
                    if (!record.isBlock()) {
                        Entity target = level.getEntity(record.id());
                        if (target != null) {
                            if (!target.isAlive()) {
                                data.unregister(record.id());
                                continue;
                            }
                            data.updatePosition(record.id(), target.blockPosition());
                        }
                    }
                }
                CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                String targetUuidStr = customData.copyTag().getString("target_uuid").orElse("");
                TransponderTrackerSavedData.TransponderRecord activeRecord = null;
                if (!targetUuidStr.isEmpty()) {
                    try {
                        activeRecord = data.getRecord(UUID.fromString(targetUuidStr));
                    } catch (Exception ignored) {
                    }
                }
                if (activeRecord == null) {
                    List<TransponderTrackerSavedData.TransponderRecord> dimensionRecords = data.getRecordsForDimension(level.dimension().identifier().toString());
                    if (!dimensionRecords.isEmpty()) {
                        activeRecord = findClosestRecord(player, dimensionRecords);
                    }
                }
                if (activeRecord == null) {
                    return;
                }
                if (!activeRecord.dimension().equals(level.dimension().identifier().toString())) {
                    Component msg = Component.literal("[TRACKER] ✦ ").withStyle(ChatFormatting.DARK_AQUA)
                            .append(Component.literal("\"" + activeRecord.name() + "\" ").withStyle(ChatFormatting.YELLOW))
                            .append(Component.literal("[").withStyle(ChatFormatting.GRAY))
                            .append(Component.translatable("message.sandstorm.signal_tracker.other_dimension").withStyle(ChatFormatting.RED))
                            .append(Component.literal("]").withStyle(ChatFormatting.GRAY));
                    player.sendOverlayMessage(msg);
                    return;
                }
                double dx = activeRecord.x() + 0.5 - player.getX();
                double dz = activeRecord.z() + 0.5 - player.getZ();
                int dy = activeRecord.y() - (int) player.getY();
                int dist = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
                double targetAngle = Math.toDegrees(Math.atan2(-dx, dz));
                double relAngle = Mth.wrapDegrees(targetAngle - player.getYRot());
                String arrow = getDirectionArrow(relAngle);
                ChatFormatting distColor = getDistanceColor(dist);
                String heightIndicator = dy > 4 ? " ▲" : (dy < -4 ? " ▼" : "");
                Component msg = Component.literal("[TRACKER] ").withStyle(ChatFormatting.DARK_AQUA)
                        .append(Component.literal(arrow + " ").withStyle(distColor))
                        .append(Component.literal(dist + "m").withStyle(ChatFormatting.WHITE))
                        .append(Component.literal(heightIndicator + " ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal("\"" + activeRecord.name() + "\"").withStyle(ChatFormatting.YELLOW));
                player.sendOverlayMessage(msg);
            }
        }
    }

    private static TransponderTrackerSavedData.TransponderRecord findClosestRecord(Player player, List<TransponderTrackerSavedData.TransponderRecord> records) {
        TransponderTrackerSavedData.TransponderRecord closest = null;
        double closestDistSq = Double.MAX_VALUE;
        for (TransponderTrackerSavedData.TransponderRecord rec : records) {
            double dx = rec.x() + 0.5 - player.getX();
            double dz = rec.z() + 0.5 - player.getZ();
            double distSq = dx * dx + dz * dz;
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = rec;
            }
        }
        return closest;
    }

    public static String getDirectionArrow(double relAngle) {
        if (relAngle >= -22.5 && relAngle < 22.5) {
            return "⬆";
        } else if (relAngle >= 22.5 && relAngle < 67.5) {
            return "↗";
        } else if (relAngle >= 67.5 && relAngle < 112.5) {
            return "➡";
        } else if (relAngle >= 112.5 && relAngle < 157.5) {
            return "↘";
        } else if (relAngle >= -67.5 && relAngle < -22.5) {
            return "↖";
        } else if (relAngle >= -112.5 && relAngle < -67.5) {
            return "⬅";
        } else if (relAngle >= -157.5 && relAngle < -112.5) {
            return "↙";
        } else {
            return "⬇";
        }
    }

    public static ChatFormatting getDistanceColor(int dist) {
        if (dist < 25) {
            return ChatFormatting.GREEN;
        } else if (dist < 75) {
            return ChatFormatting.AQUA;
        } else if (dist < 200) {
            return ChatFormatting.YELLOW;
        } else if (dist < 600) {
            return ChatFormatting.GOLD;
        } else {
            return ChatFormatting.RED;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, flag);
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.signal_tracker.desc").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.signal_tracker.use").withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.signal_tracker.range").withStyle(ChatFormatting.GREEN));
    }
}
