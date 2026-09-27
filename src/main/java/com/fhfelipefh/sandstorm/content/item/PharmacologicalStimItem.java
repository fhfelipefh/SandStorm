package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class PharmacologicalStimItem extends Item {
    public static final FoodProperties STIM_FOOD = new FoodProperties(0, 0.0f, true);

    public enum StimType {
        ADRENAL("adrenal_stim"),
        BIOFOAM("biofoam_cartridge"),
        MYOMER("myomer_stim"),
        ENDOTHERMIC("endothermic_serum"),
        GRAV_DAMPENER("grav_dampener_stim"),
        DETOX("detox_ampoule"),
        STEALTH("stealth_nano_drape");

        private final String id;

        StimType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private final StimType type;

    public PharmacologicalStimItem(StimType type, Properties properties) {
        super(properties
                .food(STIM_FOOD)
                .stacksTo(16)
                .rarity(Rarity.UNCOMMON));
        this.type = type;
    }

    public StimType getStimType() {
        return type;
    }

    public StimType getType() {
        return type;
    }

    public void applyStimEffect(LivingEntity entity) {
        switch (type) {
            case ADRENAL -> entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 3600, 1));
            case BIOFOAM -> {
                entity.heal(8.0f);
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 1));
            }
            case MYOMER -> entity.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 3600, 1));
            case ENDOTHERMIC -> {
                entity.clearFire();
                entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0));
            }
            case GRAV_DAMPENER -> {
                entity.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 3600, 1));
                entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 3600, 0));
            }
            case DETOX -> {
                entity.getActiveEffects().stream()
                        .filter(instance -> !instance.getEffect().value().isBeneficial())
                        .map(MobEffectInstance::getEffect)
                        .toList()
                        .forEach(entity::removeEffect);
                entity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 0));
            }
            case STEALTH -> entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1800, 0));
        }
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide()) {
            applyStimEffect(entity);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack empty = new ItemStack(SandStormItems.EMPTY_CARTRIDGE);
            if (result.isEmpty()) {
                return empty;
            }
            if (!player.getInventory().add(empty)) {
                if (level instanceof ServerLevel serverLevel) {
                    player.spawnAtLocation(serverLevel, empty);
                }
            }
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.stim.pharma_header").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.stim." + type.getId() + ".desc1").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.stim." + type.getId() + ".desc2").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.stim." + type.getId() + ".effect").withStyle(ChatFormatting.GREEN));
    }
}
