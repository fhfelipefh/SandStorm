package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class EnergizedVibroBladeItem extends Item {
    public static final float BASE_DAMAGE = 8.0f;
    public static final float ACTIVE_BONUS_DAMAGE = 13.0f;
    public static final long ACTIVATION_COST = 15L;
    public static final long IDLE_COST_PER_SECOND = 2L;
    public static final long HIT_COST = 15L;

    public EnergizedVibroBladeItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.RARE));
    }

    public static boolean isMotorActive(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.copyTag().getBoolean("motor_active").orElse(false);
    }

    public static void setMotorActive(ItemStack stack, boolean active) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putBoolean("motor_active", active);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isMotorActive(stack) || super.isFoil(stack);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        boolean currentActive = isMotorActive(heldStack);
        boolean nextActive = !currentActive;

        if (nextActive) {
            if (player instanceof ServerPlayer sp) {
                if (!SuitSurvivalHandler.consumePlayerEnergy(sp, ACTIVATION_COST)) {
                    sp.sendSystemMessage(Component.translatable("tooltip.sandstorm.weapon.no_power").withStyle(ChatFormatting.RED), true);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SandStormSoundEvents.SUIT_BATTERY_LOW, SoundSource.PLAYERS, 1.0f, 1.0f);
                    return InteractionResult.FAIL;
                }
            }
            setMotorActive(heldStack, true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.9f, 1.8f);
        } else {
            setMotorActive(heldStack, false);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.9f, 1.4f);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        if (isMotorActive(stack) && attacker instanceof ServerPlayer player && target.level() instanceof ServerLevel serverLevel) {
            if (SuitSurvivalHandler.consumePlayerEnergy(player, HIT_COST)) {
                target.hurtServer(serverLevel, player.damageSources().playerAttack(player), ACTIVE_BONUS_DAMAGE);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), 14, 0.25, 0.25, 0.25, 0.08);
                serverLevel.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), 8, 0.2, 0.2, 0.2, 0.05);
                serverLevel.playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.1f, 1.7f);

                if (target instanceof SandwormEntity sandworm) {
                    sandworm.setRearingProgress(0.2f);
                    sandworm.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2));
                    serverLevel.playSound(null, target.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.4f, 2.0f);
                }
            } else {
                setMotorActive(stack, false);
                serverLevel.playSound(null, player.blockPosition(), SandStormSoundEvents.SUIT_BATTERY_LOW, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (isMotorActive(stack) && entity instanceof ServerPlayer player) {
            if (player.tickCount % 20 == 0) {
                if (!SuitSurvivalHandler.consumePlayerEnergy(player, IDLE_COST_PER_SECOND)) {
                    setMotorActive(stack, false);
                    level.playSound(null, player.blockPosition(), SandStormSoundEvents.SUIT_BATTERY_LOW, SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            }
            if (slot == EquipmentSlot.MAINHAND && player.tickCount % 6 == 0) {
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        player.getX(), player.getY() + 1.2, player.getZ(),
                        2, 0.15, 0.15, 0.15, 0.02);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, flag);
        boolean active = isMotorActive(stack);
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.energized_vibro_blade.desc").withStyle(ChatFormatting.GRAY));
        if (active) {
            tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.energized_vibro_blade.active").withStyle(ChatFormatting.AQUA));
            tooltipConsumer.accept(Component.literal("Dano Total: 21.0 - 22.0").withStyle(ChatFormatting.GOLD));
        } else {
            tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.energized_vibro_blade.inactive").withStyle(ChatFormatting.DARK_GRAY));
            tooltipConsumer.accept(Component.literal("Dano Base: 8.0").withStyle(ChatFormatting.WHITE));
        }
    }
}
