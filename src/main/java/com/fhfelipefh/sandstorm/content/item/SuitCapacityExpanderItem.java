package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class SuitCapacityExpanderItem extends Item {
    public static final int MAX_EXPANDERS = 20;

    public SuitCapacityExpanderItem(Properties properties) {
        super(properties.stacksTo(16).rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!(chestplate.getItem() instanceof SpaceSuitItem)) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.translatable("message.sandstorm.chip_require_suit"));
            }
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        SuitPowerComponent suit = (player instanceof ServerPlayer sp)
                ? SuitSurvivalHandler.getOrCreateSuit(sp)
                : SuitSurvivalHandler.getOrCreateSuit(player.getUUID());

        if (suit.getCapacityTier() >= MAX_EXPANDERS) {
            player.sendSystemMessage(Component.translatable("message.sandstorm.capacity_expander_max"));
            return InteractionResult.FAIL;
        }

        suit.addCapacityTier();
        if (player instanceof ServerPlayer sp) {
            SuitSurvivalHandler.savePlayerSuit(sp);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 1.0f, 1.4f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_NETHERITE.value(), SoundSource.PLAYERS, 1.0f, 1.2f);

        if (!player.getAbilities().instabuild) {
            heldStack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (action == ClickAction.SECONDARY && slot.getItem().getItem() instanceof SpaceSuitItem) {
            Level level = player.level();
            if (level.isClientSide()) {
                return true;
            }

            SuitPowerComponent suit = (player instanceof ServerPlayer sp)
                    ? SuitSurvivalHandler.getOrCreateSuit(sp)
                    : SuitSurvivalHandler.getOrCreateSuit(player.getUUID());

            if (suit.getCapacityTier() >= MAX_EXPANDERS) {
                player.sendSystemMessage(Component.translatable("message.sandstorm.capacity_expander_max"));
                return true;
            }

            suit.addCapacityTier();
            if (player instanceof ServerPlayer sp) {
                SuitSurvivalHandler.savePlayerSuit(sp);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 1.0f, 1.4f);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_NETHERITE.value(), SoundSource.PLAYERS, 1.0f, 1.2f);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, flag);
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.suit_capacity_expander.desc").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.suit_capacity_expander.limit").withStyle(ChatFormatting.DARK_AQUA));
    }
}

