package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.NutrientBombEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class NutrientBombItem extends Item {

    public NutrientBombItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.SPLASH_POTION_THROW,
                SoundSource.PLAYERS,
                0.5f,
                0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f)
        );
        if (!level.isClientSide()) {
            NutrientBombEntity bomb = new NutrientBombEntity(level, player);
            bomb.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 0.9f, 1.0f);
            level.addFreshEntity(bomb);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("item.sandstorm.nutrient_bomb.desc_1").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("item.sandstorm.nutrient_bomb.desc_2").withStyle(ChatFormatting.GRAY));
    }
}
