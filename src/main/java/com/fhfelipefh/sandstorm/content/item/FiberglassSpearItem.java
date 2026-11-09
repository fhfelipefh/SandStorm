package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.FiberglassSpearEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

public class FiberglassSpearItem extends Item {

    public FiberglassSpearItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) {
            return false;
        }
        int charge = this.getUseDuration(stack, livingEntity) - timeLeft;
        if (charge < 10) {
            return false;
        }
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SandStormSoundEvents.FIBERGLASS_SPEAR_THROW,
                SoundSource.PLAYERS,
                1.0f,
                1.0f / (level.getRandom().nextFloat() * 0.4f + 1.2f) + 0.5f
        );
        if (!level.isClientSide()) {
            FiberglassSpearEntity spear = new FiberglassSpearEntity(level, player);
            spear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 2.5f, 1.0f);
            level.addFreshEntity(spear);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(stack, 20);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("item.sandstorm.fiberglass_spear.desc_1").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("item.sandstorm.fiberglass_spear.desc_2").withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("item.sandstorm.fiberglass_spear.desc_3").withStyle(ChatFormatting.DARK_AQUA));
    }
}

