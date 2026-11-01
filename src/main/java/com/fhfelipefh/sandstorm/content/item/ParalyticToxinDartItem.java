package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.ParalyticToxinDartEntity;
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

public class ParalyticToxinDartItem extends Item {

    public ParalyticToxinDartItem(Properties properties) {
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
                SoundEvents.ARROW_SHOOT,
                SoundSource.PLAYERS,
                0.8f,
                1.4f / (level.getRandom().nextFloat() * 0.2f + 0.9f)
        );

        if (!level.isClientSide()) {
            ParalyticToxinDartEntity dart = new ParalyticToxinDartEntity(level, player);
            dart.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 1.6f, 0.8f);
            level.addFreshEntity(dart);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(itemStack, 15);

        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("item.sandstorm.paralytic_toxin_dart.desc_1").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("item.sandstorm.paralytic_toxin_dart.desc_2").withStyle(ChatFormatting.AQUA));
    }
}
