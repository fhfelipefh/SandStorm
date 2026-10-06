package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.effect.SonicBlastVisualEffect;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Consumer;

public class SonicCannonItem extends Item {
    public static final int COOLDOWN_TICKS = 25;
    public static final double CANNON_RANGE = 24.0;
    public static final float DAMAGE_AMOUNT = 20.0f;
    public static final long ENERGY_COST = 2500L;

    public SonicCannonItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        SuitPowerComponent suit = (player instanceof ServerPlayer sp) ? SuitSurvivalHandler.getOrCreateSuit(sp) : SuitSurvivalHandler.getOrCreateSuit(player.getUUID());
        if (!suit.consumeEnergy(ENERGY_COST)) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("tooltip.sandstorm.weapon.no_power").withStyle(ChatFormatting.RED), true);
                serverPlayer.level().playSound(null, player.blockPosition(), SandStormSoundEvents.SUIT_BATTERY_LOW, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            return InteractionResult.FAIL;
        }

        player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Vec3 eyePos = player.getEyePosition();
            Vec3 lookVec = player.getLookAngle();
            AABB effectBounds = new AABB(eyePos, eyePos.add(lookVec.scale(CANNON_RANGE))).inflate(4.0);

            List<LivingEntity> enemies = serverLevel.getEntitiesOfClass(LivingEntity.class, effectBounds, entity ->
                    entity != player && (entity instanceof Enemy || entity instanceof SandwormEntity)
            );

            for (LivingEntity enemy : enemies) {
                enemy.hurtServer(serverLevel, player.damageSources().sonicBoom(player), DAMAGE_AMOUNT);
                Vec3 knockback = lookVec.scale(1.5);
                enemy.setDeltaMovement(knockback.x, 0.4, knockback.z);
            }

            SonicBlastVisualEffect.spawnSonicBlast(serverLevel, player, CANNON_RANGE);
            serverLevel.playSound(null, player.blockPosition(), SandStormSoundEvents.SONIC_CANNON_BLAST, SoundSource.PLAYERS, 1.2f, 1.2f);

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.sonic_blast", enemies.size()), true);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.CROSSBOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.weapon.damage", (int) DAMAGE_AMOUNT).withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.weapon.energy_cost", ENERGY_COST).withStyle(ChatFormatting.GOLD));
    }
}
