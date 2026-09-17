package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public class BrackishWaterBottleItem extends Item {
    public static final FoodProperties BRACKISH_FOOD = new FoodProperties(1, 0.1f, true);

    public BrackishWaterBottleItem(Properties properties) {
        super(properties
                .food(BRACKISH_FOOD)
                .stacksTo(16));
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide()) {
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 160, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 1));
        }

        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        return result;
    }
}
