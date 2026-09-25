package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class MolecularUpgradeItem extends Item {

    public enum ModuleSlotType {
        OVERCLOCK,
        NANOCOATING
    }

    public enum Category {
        WEAPON,
        TOOL,
        ARMOR,
        BOOTS,
        UNIVERSAL;

        public boolean isApplicableTo(ItemStack stack) {
            if (stack == null || stack.isEmpty()) {
                return false;
            }
            Item item = stack.getItem();
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            String path = id != null ? id.getPath() : "";

            return switch (this) {
                case WEAPON -> item instanceof VibroCrysknifeItem
                        || item instanceof PlasmaRifleItem
                        || item instanceof HeavyPlasmaCannonItem
                        || item instanceof SonicCannonItem
                        || path.endsWith("_sword")
                        || stack.is(ItemTags.SWORDS);
                case TOOL -> path.endsWith("_pickaxe")
                        || path.endsWith("_axe")
                        || path.endsWith("_shovel")
                        || path.endsWith("_hoe")
                        || stack.is(ItemTags.PICKAXES)
                        || stack.is(ItemTags.AXES)
                        || stack.is(ItemTags.SHOVELS);
                case ARMOR -> item instanceof SpaceSuitItem
                        || path.endsWith("_helmet")
                        || path.endsWith("_chestplate")
                        || path.endsWith("_leggings")
                        || path.endsWith("_boots")
                        || stack.is(ItemTags.HEAD_ARMOR)
                        || stack.is(ItemTags.CHEST_ARMOR)
                        || stack.is(ItemTags.LEG_ARMOR)
                        || stack.is(ItemTags.FOOT_ARMOR);
                case BOOTS -> (item instanceof SpaceSuitItem suit && suit.getArmorType() == ArmorType.BOOTS)
                        || path.endsWith("_boots")
                        || stack.is(ItemTags.FOOT_ARMOR);
                case UNIVERSAL -> stack.isDamageableItem()
                        || item instanceof SpaceSuitItem
                        || item instanceof VibroCrysknifeItem
                        || item instanceof PlasmaRifleItem
                        || item instanceof HeavyPlasmaCannonItem
                        || item instanceof SonicCannonItem
                        || path.endsWith("_sword")
                        || path.endsWith("_pickaxe")
                        || path.endsWith("_axe")
                        || path.endsWith("_shovel")
                        || path.endsWith("_hoe")
                        || path.endsWith("_helmet")
                        || path.endsWith("_chestplate")
                        || path.endsWith("_leggings")
                        || path.endsWith("_boots")
                        || stack.is(ItemTags.SWORDS)
                        || stack.is(ItemTags.PICKAXES)
                        || stack.is(ItemTags.HEAD_ARMOR)
                        || stack.is(ItemTags.CHEST_ARMOR)
                        || stack.is(ItemTags.LEG_ARMOR)
                        || stack.is(ItemTags.FOOT_ARMOR);
            };
        }
    }

    public enum UpgradeType {
        VIBRO_RESONATOR("vibro_resonator_module", Category.WEAPON, ModuleSlotType.OVERCLOCK, Enchantments.SHARPNESS, 3),
        THERMAL_PLASMA("thermal_plasma_emitter", Category.WEAPON, ModuleSlotType.OVERCLOCK, Enchantments.FIRE_ASPECT, 2),
        KINETIC_FOCUS("kinetic_focus_module", Category.WEAPON, ModuleSlotType.OVERCLOCK, Enchantments.KNOCKBACK, 2),
        CAVITATION_CORE("cavitation_frequency_core", Category.TOOL, ModuleSlotType.OVERCLOCK, Enchantments.EFFICIENCY, 4),
        ATOMIC_PHASE("atomic_phase_disrupter", Category.TOOL, ModuleSlotType.OVERCLOCK, Enchantments.SILK_TOUCH, 1),
        SPECTROMETRIC_SIFTER("spectrometric_sifter", Category.TOOL, ModuleSlotType.OVERCLOCK, Enchantments.FORTUNE, 3),
        SELF_HEALING_NANITES("self_healing_nanite_matrix", Category.UNIVERSAL, ModuleSlotType.NANOCOATING, Enchantments.MENDING, 1),
        TITANIUM_LATTICE("titanium_lattice_coating", Category.UNIVERSAL, ModuleSlotType.NANOCOATING, Enchantments.UNBREAKING, 3),
        BALLISTIC_DAMPENER("ballistic_dampener_mesh", Category.ARMOR, ModuleSlotType.OVERCLOCK, Enchantments.PROTECTION, 4),
        ABLATIVE_PLATING("ablative_thermal_plating", Category.ARMOR, ModuleSlotType.OVERCLOCK, Enchantments.FIRE_PROTECTION, 4),
        FALL_DAMPERS("pneumatic_fall_dampers", Category.BOOTS, ModuleSlotType.OVERCLOCK, Enchantments.FEATHER_FALLING, 4),
        REACTIVE_SHOCK("reactive_shock_plating", Category.ARMOR, ModuleSlotType.OVERCLOCK, Enchantments.THORNS, 3);

        private final String id;
        private final Category category;
        private final ModuleSlotType slotType;
        private final ResourceKey<Enchantment> enchantmentKey;
        private final int level;

        UpgradeType(String id, Category category, ModuleSlotType slotType, ResourceKey<Enchantment> enchantmentKey, int level) {
            this.id = id;
            this.category = category;
            this.slotType = slotType;
            this.enchantmentKey = enchantmentKey;
            this.level = level;
        }

        public String getId() {
            return id;
        }

        public Category getCategory() {
            return category;
        }

        public ModuleSlotType getSlotType() {
            return slotType;
        }

        public ResourceKey<Enchantment> getEnchantmentKey() {
            return enchantmentKey;
        }

        public int getLevel() {
            return level;
        }

        public void applyTo(ItemStack target, Level levelContext) {
            if (target == null || target.isEmpty() || levelContext == null) {
                return;
            }
            levelContext.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(registry -> {
                registry.get(enchantmentKey).ifPresent(holder -> target.enchant(holder, level));
            });
        }
    }

    private final UpgradeType upgradeType;

    public MolecularUpgradeItem(UpgradeType upgradeType, Properties properties) {
        super(properties);
        this.upgradeType = upgradeType;
    }

    public UpgradeType getUpgradeType() {
        return upgradeType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.molecular_upgrade.header").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.molecular_upgrade.category." + upgradeType.getCategory().name().toLowerCase()).withStyle(ChatFormatting.DARK_AQUA));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.molecular_upgrade." + upgradeType.getId() + ".desc").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.molecular_upgrade." + upgradeType.getId() + ".effect").withStyle(ChatFormatting.GREEN));
    }
}
