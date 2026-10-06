package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.entity.CyberHoundEntity;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ReprogrammerToolItem extends Item {
    private static final long REPROGRAM_POWER_COST = 4000L;

    public ReprogrammerToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (interactionTarget instanceof CyberHoundEntity hound) {
            Level level = player.level();
            if (hound.isTame()) {
                if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_hound_already_tamed"), true);
                }
                return InteractionResult.SUCCESS;
            }

            if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                if (!serverPlayer.isCreative()) {
                    SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(serverPlayer);
                    if (suit.getStoredEnergy() < REPROGRAM_POWER_COST) {
                        serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.reprogrammer_insufficient_power"), true);
                        return InteractionResult.FAIL;
                    }
                    suit.consumeEnergy(REPROGRAM_POWER_COST);
                }

                hound.tame(player);
                hound.setOrderedToSit(false);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        hound.getX(), hound.getY() + 0.5, hound.getZ(),
                        35, 0.35, 0.4, 0.35, 0.1);
                serverLevel.playSound(null, hound.getX(), hound.getY(), hound.getZ(),
                        SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.5f);
                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_hound_reprogrammed"), true);
            }
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(stack, player, interactionTarget, hand);
    }
}
