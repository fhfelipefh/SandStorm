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

public class HeavySapBottleItem extends Item {
    public static final FoodProperties SAP_FOOD = new FoodProperties(6, 0.8f, false);

    public HeavySapBottleItem(Properties properties) {
        super(properties
                .food(SAP_FOOD)
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
            entity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 300, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 0));
        }

        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        return result;
    }
}
