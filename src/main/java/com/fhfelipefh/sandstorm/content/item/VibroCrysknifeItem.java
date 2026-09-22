package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class VibroCrysknifeItem extends Item {
    public static final float BASE_DAMAGE = 9.0f;
    public static final float ARMOR_PIERCE_BONUS = 4.5f;
    public static final long ENERGY_COST = 200L;
    public static final long ENERGY_COST_PER_HIT = ENERGY_COST;

    public VibroCrysknifeItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        if (attacker instanceof Player player && target.level() instanceof ServerLevel serverLevel) {
            SuitPowerComponent suit = (player instanceof ServerPlayer sp) ? SuitSurvivalHandler.getOrCreateSuit(sp) : SuitSurvivalHandler.getOrCreateSuit(player.getUUID());
            if (suit.consumeEnergy(ENERGY_COST_PER_HIT)) {
                target.hurtServer(serverLevel, player.damageSources().magic(), ARMOR_PIERCE_BONUS);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), 8, 0.2, 0.2, 0.2, 0.05);
                serverLevel.playSound(null, target.blockPosition(), SandStormSoundEvents.VIBRO_CRYSKNIFE_HIT, SoundSource.PLAYERS, 1.0f, 1.5f);
            }
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            level.playSound(null, player.blockPosition(), SandStormSoundEvents.VIBRO_CRYSKNIFE_SWING, SoundSource.PLAYERS, 0.8f, 1.6f);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.weapon.damage", (int) BASE_DAMAGE).withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.weapon.energy_cost", ENERGY_COST_PER_HIT).withStyle(ChatFormatting.GOLD));
    }
}
