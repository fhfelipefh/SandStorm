package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class CyborgUpgradeItem extends Item {

    public enum CyborgUpgradeType {
        ACID_CHITIN_PLATING("acid_chitin_plating", Rarity.RARE, 1),
        CRYO_TREHALOSE_CELL("cryo_trehalose_cell", Rarity.RARE, 2),
        LONG_RANGE_LIDAR_LENS("long_range_lidar_lens", Rarity.UNCOMMON, 4),
        PIEZO_HOVER_THRUSTER("piezo_hover_thruster", Rarity.EPIC, 8);

        private final String id;
        private final Rarity rarity;
        private final int maskBit;

        CyborgUpgradeType(String id, Rarity rarity, int maskBit) {
            this.id = id;
            this.rarity = rarity;
            this.maskBit = maskBit;
        }

        public String getId() {
            return this.id;
        }

        public Rarity getRarity() {
            return this.rarity;
        }

        public int getMaskBit() {
            return this.maskBit;
        }

        public int getBitmask() {
            return this.maskBit;
        }
    }

    private final CyborgUpgradeType upgradeType;

    public CyborgUpgradeItem(CyborgUpgradeType upgradeType, Properties properties) {
        super(properties.rarity(upgradeType.getRarity()));
        this.upgradeType = upgradeType;
    }

    public CyborgUpgradeType getUpgradeType() {
        return this.upgradeType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);

        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.cyborg_upgrade.category")
                .withStyle(ChatFormatting.DARK_GRAY));

        switch (this.upgradeType) {
            case ACID_CHITIN_PLATING -> {
                tooltipAdder.accept(Component.translatable("tooltip.sandstorm.acid_chitin_plating.desc1")
                        .withStyle(ChatFormatting.GREEN));
                tooltipAdder.accept(Component.translatable("tooltip.sandstorm.acid_chitin_plating.desc2")
                        .withStyle(ChatFormatting.DARK_GREEN));
            }
            case CRYO_TREHALOSE_CELL -> {
                tooltipAdder.accept(Component.translatable("tooltip.sandstorm.cryo_trehalose_cell.desc1")
                        .withStyle(ChatFormatting.AQUA));
                tooltipAdder.accept(Component.translatable("tooltip.sandstorm.cryo_trehalose_cell.desc2")
                        .withStyle(ChatFormatting.DARK_AQUA));
            }
            case LONG_RANGE_LIDAR_LENS -> {
                tooltipAdder.accept(Component.translatable("tooltip.sandstorm.long_range_lidar_lens.desc1")
                        .withStyle(ChatFormatting.YELLOW));
                tooltipAdder.accept(Component.translatable("tooltip.sandstorm.long_range_lidar_lens.desc2")
                        .withStyle(ChatFormatting.GOLD));
            }
            case PIEZO_HOVER_THRUSTER -> {
                tooltipAdder.accept(Component.translatable("tooltip.sandstorm.piezo_hover_thruster.desc1")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
                tooltipAdder.accept(Component.translatable("tooltip.sandstorm.piezo_hover_thruster.desc2")
                        .withStyle(ChatFormatting.DARK_PURPLE));
            }
        }

        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.cyborg_upgrade.instruction")
                .withStyle(ChatFormatting.GRAY));
    }
}
