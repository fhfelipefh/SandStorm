package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.GravitonSphereEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class GravitonReversalCannonItem extends Item {
    public static final int COOLDOWN_TICKS = 35;
    public static final long ENERGY_COST = 3500L;
    public static final float BLAST_DAMAGE = 52.0f;

    public GravitonReversalCannonItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel serverLevel) {
            if (!serverPlayer.getAbilities().instabuild && !SuitSurvivalHandler.consumePlayerEnergy(serverPlayer, ENERGY_COST)) {
                serverPlayer.sendSystemMessage(Component.translatable("tooltip.sandstorm.weapon.no_power").withStyle(ChatFormatting.RED), true);
                serverLevel.playSound(null, player.blockPosition(), SandStormSoundEvents.SUIT_BATTERY_LOW, SoundSource.PLAYERS, 1.0f, 1.0f);
                return InteractionResult.FAIL;
            }

            player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);
            GravitonSphereEntity sphere = new GravitonSphereEntity(serverLevel, player, GravitonSphereEntity.MODE_RED_REVERSAL);
            sphere.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 2.2f, 0.1f);
            serverLevel.addFreshEntity(sphere);
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.2f, 1.4f);
            serverLevel.playSound(null, player.blockPosition(), SandStormSoundEvents.PLASMA_RIFLE_FIRE, SoundSource.PLAYERS, 1.4f, 0.6f);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.CROSSBOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.weapon.damage", (int) BLAST_DAMAGE).withStyle(ChatFormatting.RED));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.weapon.energy_cost", ENERGY_COST).withStyle(ChatFormatting.GOLD));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.graviton_reversal.desc").withStyle(ChatFormatting.DARK_RED));
    }
}
