package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class CyberSteedUpgradeItem extends Item {
    public enum UpgradeType {
        SONIC_TURRET("sonic_turret", "item.sandstorm.steed_upgrade_sonic_turret.desc"),
        BATTERY("battery", "item.sandstorm.steed_upgrade_battery.desc"),
        SPEED("speed", "item.sandstorm.steed_upgrade_speed.desc"),
        CARGO("cargo", "item.sandstorm.steed_upgrade_cargo.desc");

        private final String id;
        private final String descriptionKey;

        UpgradeType(String id, String descriptionKey) {
            this.id = id;
            this.descriptionKey = descriptionKey;
        }

        public String getId() {
            return id;
        }

        public String getDescriptionKey() {
            return descriptionKey;
        }
    }

    private final UpgradeType upgradeType;

    public CyberSteedUpgradeItem(UpgradeType upgradeType, Properties properties) {
        super(properties);
        this.upgradeType = upgradeType;
    }

    public UpgradeType getUpgradeType() {
        return upgradeType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, flag);
        tooltipConsumer.accept(Component.translatable(this.upgradeType.getDescriptionKey()));
        tooltipConsumer.accept(Component.translatable("item.sandstorm.steed_upgrade.hint"));
    }
}
