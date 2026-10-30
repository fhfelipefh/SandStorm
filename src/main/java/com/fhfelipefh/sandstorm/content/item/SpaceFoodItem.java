package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class SpaceFoodItem extends Item {
    public enum FoodType {
        THERMAL_PASTE("tooltip.sandstorm.thermal_nutrient_paste"),
        ELECTROLYTE_JUICE("tooltip.sandstorm.electrolyte_juice"),
        CRYO_RATION("tooltip.sandstorm.cryo_glazed_ration"),
        FEAST("tooltip.sandstorm.hydroponic_feast"),
        GENERIC(null);

        private final String tooltipKey;

        FoodType(String tooltipKey) {
            this.tooltipKey = tooltipKey;
        }

        public String getTooltipKey() {
            return tooltipKey;
        }
    }

    private final FoodType foodType;

    public SpaceFoodItem(FoodType foodType, Properties properties) {
        super(properties);
        this.foodType = foodType;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        if (!level.isClientSide() && livingEntity instanceof Player player) {
            SuitPowerComponent suit = (player instanceof ServerPlayer sp)
                    ? SuitSurvivalHandler.getOrCreateSuit(sp)
                    : SuitSurvivalHandler.getOrCreateSuit(player.getUUID());

            if (this.foodType == FoodType.THERMAL_PASTE) {
                suit.getThermal().setCurrentTemperature(37.0);
            } else if (this.foodType == FoodType.ELECTROLYTE_JUICE) {
                suit.getEnergyStorage().receiveEnergy(500);
                if (!player.getAbilities().instabuild) {
                    player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
                }
            }
        }

        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, tooltipFlag);
        if (this.foodType.getTooltipKey() != null) {
            tooltipConsumer.accept(Component.translatable(this.foodType.getTooltipKey()));
        }
    }
}
