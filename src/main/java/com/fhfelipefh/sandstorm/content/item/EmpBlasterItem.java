package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.network.EmpDeafenPayload;
import com.fhfelipefh.sandstorm.content.world.EmpParalysisHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Consumer;

public class EmpBlasterItem extends Item {
    public static final int COOLDOWN_TICKS = 400;
    public static final double EMP_RADIUS = 48.0;
    public static final int PLAYER_BACKLASH_TICKS = 140;
    public static final int MIN_PARALYSIS_TICKS = 1200;
    public static final int MAX_PARALYSIS_TICKS = 2400;

    public EmpBlasterItem(Properties properties) {
        super(properties.durability(20).stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);
        heldStack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(heldStack));

        player.setSprinting(false);
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, PLAYER_BACKLASH_TICKS, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, PLAYER_BACKLASH_TICKS, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, PLAYER_BACKLASH_TICKS, 4, false, false, true));

        if (!level.isClientSide()) {
            if (player instanceof ServerPlayer serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, new EmpDeafenPayload(PLAYER_BACKLASH_TICKS));
            }
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.8f, 1.6f);
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.2f, 0.7f);

                serverLevel.sendParticles(ParticleTypes.SONIC_BOOM,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        1, 0.0, 0.0, 0.0, 0.0);

                for (int deg = 0; deg < 360; deg += 15) {
                    double rad = Math.toRadians(deg);
                    double dx = Math.cos(rad) * 2.5;
                    double dz = Math.sin(rad) * 2.5;
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            player.getX() + dx, player.getY() + 0.8, player.getZ() + dz,
                            4, 0.1, 0.1, 0.1, 0.08);
                }

                AABB empArea = player.getBoundingBox().inflate(EMP_RADIUS);
                List<LivingEntity> targets = serverLevel.getEntitiesOfClass(
                        LivingEntity.class, empArea, EmpParalysisHandler::isAndroidOrAutomaton);

                for (LivingEntity target : targets) {
                    double dist = Math.sqrt(target.distanceToSqr(player));
                    double proximityFactor = Math.max(0.0, Math.min(1.0, 1.0 - (dist / EMP_RADIUS)));
                    int paralysisTicks = MIN_PARALYSIS_TICKS + (int) (proximityFactor * (MAX_PARALYSIS_TICKS - MIN_PARALYSIS_TICKS));
                    EmpParalysisHandler.paralyze(target, paralysisTicks);
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ(),
                            12, 0.25, 0.25, 0.25, 0.08);
                }

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(
                            Component.translatable("telemetry.sandstorm.emp_blaster_fired", targets.size()), true);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.CROSSBOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("item.sandstorm.emp_blaster.tooltip1").withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("item.sandstorm.emp_blaster.tooltip2").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("item.sandstorm.emp_blaster.tooltip3").withStyle(ChatFormatting.DARK_RED));
        tooltipConsumer.accept(Component.translatable("item.sandstorm.emp_blaster.tooltip4").withStyle(ChatFormatting.RED));
    }
}
