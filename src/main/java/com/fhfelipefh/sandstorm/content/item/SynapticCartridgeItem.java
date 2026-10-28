package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class SynapticCartridgeItem extends Item {

    public SynapticCartridgeItem(Item.Properties properties) {
        super(properties);
    }

    public static int getStoredXp(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.copyTag().getInt("stored_xp").orElse(0);
    }

    public static void setStoredXp(ItemStack stack, int amount) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putInt("stored_xp", Math.max(0, amount));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static int getLevelForXp(int xp) {
        if (xp <= 0) {
            return 0;
        }
        int level = 0;
        while (getXpForLevel(level + 1) <= xp) {
            level++;
        }
        return level;
    }

    public static int getXpForLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        if (level <= 16) {
            return level * level + 6 * level;
        }
        if (level <= 31) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        }
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    public static int getXpToNextLevel(int currentLevel) {
        if (currentLevel >= 30) {
            return 112 + (currentLevel - 30) * 9;
        }
        if (currentLevel >= 15) {
            return 37 + (currentLevel - 15) * 5;
        }
        return 7 + currentLevel * 2;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            int stored = getStoredXp(stack);
            if (player.isShiftKeyDown()) {
                if (stored > 0) {
                    int toExtract = Math.min(stored, getXpToNextLevel(player.experienceLevel));
                    player.giveExperiencePoints(toExtract);
                    setStoredXp(stack, stored - toExtract);
                    serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, player.getX(), player.getY() + 1.0, player.getZ(), 8, 0.2, 0.2, 0.2, 0.02);
                    sendTelemetryMessage(player, stack);
                    return InteractionResult.SUCCESS;
                }
            } else {
                int playerTotalXp = (int) (player.experienceProgress * getXpToNextLevel(player.experienceLevel));
                int neededForLevel = getXpToNextLevel(Math.max(0, player.experienceLevel - 1));
                int toDrain = player.experienceLevel > 0 ? neededForLevel : playerTotalXp;
                if (toDrain > 0) {
                    player.giveExperiencePoints(-toDrain);
                    setStoredXp(stack, stored + toDrain);
                    serverLevel.sendParticles(ParticleTypes.GLOW, player.getX(), player.getY() + 1.0, player.getZ(), 8, 0.2, 0.2, 0.2, 0.02);
                    sendTelemetryMessage(player, stack);
                    return InteractionResult.SUCCESS;
                }
            }
            sendTelemetryMessage(player, stack);
        }
        return InteractionResult.SUCCESS;
    }

    private void sendTelemetryMessage(Player player, ItemStack stack) {
        int stored = getStoredXp(stack);
        int approxLevel = getLevelForXp(stored);
        Component msg = Component.literal("[CARTUCHO SINÁPTICO] ").withStyle(ChatFormatting.DARK_AQUA)
                .append(Component.literal(stored + " pts ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal("(~Nvl " + approxLevel + ")").withStyle(ChatFormatting.GREEN));
        player.sendOverlayMessage(msg);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, flag);
        int stored = getStoredXp(stack);
        int approxLevel = getLevelForXp(stored);
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.synaptic_cartridge.desc").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.synaptic_cartridge.use").withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.literal("Armazenado: " + stored + " pts (~" + approxLevel + " Níveis)").withStyle(ChatFormatting.GREEN));
    }
}

