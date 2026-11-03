package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.entity.ApexSandwormEntity;
import com.fhfelipefh.sandstorm.content.entity.CyberHoundEntity;
import com.fhfelipefh.sandstorm.content.entity.CyberSteedEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.fhfelipefh.sandstorm.content.entity.WardenAndroidEntity;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class ReprogrammerToolItem extends Item {
    private static final long REPROGRAM_POWER_COST = 4000L;
    private static final long HACK_HOSTILE_POWER_COST = 3500L;

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

        if (interactionTarget instanceof CyberSteedEntity steed) {
            Level level = player.level();
            if (steed.isTame()) {
                if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_steed_already_tamed"), true);
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

                steed.tame(player);
                steed.setOrderedToSit(false);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        steed.getX(), steed.getY() + 0.8, steed.getZ(),
                        45, 0.4, 0.5, 0.4, 0.1);
                serverLevel.playSound(null, steed.getX(), steed.getY(), steed.getZ(),
                        SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.5f);
                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_steed_reprogrammed"), true);
            }
            return InteractionResult.SUCCESS;
        }

        if (interactionTarget instanceof Enemy || interactionTarget instanceof Monster) {
            Level level = player.level();
            if (interactionTarget instanceof WardenAndroidEntity || interactionTarget instanceof MegazordEntity || interactionTarget instanceof ApexSandwormEntity) {
                if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.reprogrammer_immune_target"), true);
                }
                return InteractionResult.FAIL;
            }

            if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                if (!serverPlayer.isCreative()) {
                    SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(serverPlayer);
                    if (suit.getStoredEnergy() < HACK_HOSTILE_POWER_COST) {
                        serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.reprogrammer_insufficient_power"), true);
                        return InteractionResult.FAIL;
                    }
                    suit.consumeEnergy(HACK_HOSTILE_POWER_COST);
                }

                double maxHp = interactionTarget.getMaxHealth();
                double successChance = Math.max(0.04, 0.24 - (maxHp / 250.0) * 0.16);

                if (serverLevel.getRandom().nextDouble() < successChance) {
                    if (interactionTarget instanceof Mob mob) {
                        mob.setTarget(null);
                        List<LivingEntity> enemies = serverLevel.getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(16.0),
                                e -> e != mob && e != player && (e instanceof Enemy || e instanceof Monster) && e.isAlive());
                        if (!enemies.isEmpty()) {
                            mob.setTarget(enemies.get(serverLevel.getRandom().nextInt(enemies.size())));
                        }
                        mob.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 1200, 0));
                        mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 0));
                    }
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            interactionTarget.getX(), interactionTarget.getY() + 0.8, interactionTarget.getZ(),
                            30, 0.4, 0.5, 0.4, 0.1);
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            interactionTarget.getX(), interactionTarget.getY() + 0.8, interactionTarget.getZ(),
                            20, 0.3, 0.4, 0.3, 0.08);
                    serverLevel.playSound(null, interactionTarget.getX(), interactionTarget.getY(), interactionTarget.getZ(),
                            SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.6f);
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.reprogrammer_hack_success"), true);
                } else {
                    if (interactionTarget instanceof Mob mob) {
                        mob.setTarget(player);
                        mob.addEffect(new MobEffectInstance(MobEffects.SPEED, 80, 0));
                    }
                    serverLevel.sendParticles(ParticleTypes.SMOKE,
                            interactionTarget.getX(), interactionTarget.getY() + 0.8, interactionTarget.getZ(),
                            15, 0.3, 0.3, 0.3, 0.05);
                    serverLevel.playSound(null, interactionTarget.getX(), interactionTarget.getY(), interactionTarget.getZ(),
                            SoundEvents.SHULKER_BULLET_HIT, SoundSource.PLAYERS, 1.0f, 0.7f);
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.reprogrammer_hack_failed"), true);
                    player.getCooldowns().addCooldown(stack, 100);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(stack, player, interactionTarget, hand);
    }
}
