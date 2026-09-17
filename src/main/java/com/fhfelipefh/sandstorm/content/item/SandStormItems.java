package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;

public class SandStormItems {
    public static final SpaceRationItem SPACE_RATION = register("space_ration", new SpaceRationItem());
    public static final SpaceSuitItem SPACE_SUIT_HELMET = register("space_suit_helmet", new SpaceSuitItem(ArmorType.HELMET));
    public static final SpaceSuitItem SPACE_SUIT_CHESTPLATE = register("space_suit_chestplate", new SpaceSuitItem(ArmorType.CHESTPLATE));
    public static final SpaceSuitItem SPACE_SUIT_LEGGINGS = register("space_suit_leggings", new SpaceSuitItem(ArmorType.LEGGINGS));
    public static final SpaceSuitItem SPACE_SUIT_BOOTS = register("space_suit_boots", new SpaceSuitItem(ArmorType.BOOTS));
    public static final BrackishWaterBottleItem BRACKISH_WATER_BOTTLE = register("brackish_water_bottle", new BrackishWaterBottleItem());
    public static final Item SANDWORM_CHITIN = register("sandworm_chitin", new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    public static final Item SANDWORM_TOOTH = register("sandworm_tooth", new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final Item RAW_SILICON = register("raw_silicon", new Item(new Item.Properties()));
    public static final Item SILICON_WAFER = register("silicon_wafer", new Item(new Item.Properties()));
    public static final Item MINERAL_SALT = register("mineral_salt", new Item(new Item.Properties()));
    public static final PotableWaterBottleItem POTABLE_WATER_BOTTLE = register("potable_water_bottle", new PotableWaterBottleItem());
    public static final Item CIRCUIT_BOARD = register("circuit_board", new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    public static final Item NANO_ACTUATOR = register("nano_actuator", new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final AnomalyRadarItem ANOMALY_RADAR = register("anomaly_radar", new AnomalyRadarItem());
    public static final Item TECH_DISC = register("tech_disc", new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final Item SCRAP_METAL = register("scrap_metal", new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    public static final SonicCannonItem SONIC_CANNON = register("sonic_cannon", new SonicCannonItem());

    public static final CreativeModeTab SANDSTORM_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            SandStormMod.id("general"),
            FabricCreativeModeTab.builder()
                    .icon(() -> new ItemStack(SPACE_SUIT_CHESTPLATE))
                    .title(Component.translatable("itemGroup.sandstorm.general"))
                    .displayItems((context, entries) -> {
                        entries.accept(SPACE_RATION);
                        entries.accept(SPACE_SUIT_HELMET);
                        entries.accept(SPACE_SUIT_CHESTPLATE);
                        entries.accept(SPACE_SUIT_LEGGINGS);
                        entries.accept(SPACE_SUIT_BOOTS);
                        entries.accept(BRACKISH_WATER_BOTTLE);
                        entries.accept(POTABLE_WATER_BOTTLE);
                        entries.accept(MINERAL_SALT);
                        entries.accept(RAW_SILICON);
                        entries.accept(SILICON_WAFER);
                        entries.accept(CIRCUIT_BOARD);
                        entries.accept(NANO_ACTUATOR);
                        entries.accept(ANOMALY_RADAR);
                        entries.accept(TECH_DISC);
                        entries.accept(SCRAP_METAL);
                        entries.accept(SONIC_CANNON);
                        entries.accept(com.fhfelipefh.sandstorm.content.block.SandStormBlocks.BRACKISH_AQUIFER);
                        entries.accept(com.fhfelipefh.sandstorm.content.block.SandStormBlocks.THUMPER);
                        entries.accept(com.fhfelipefh.sandstorm.content.block.SandStormBlocks.PRINTER_3D);
                        entries.accept(com.fhfelipefh.sandstorm.content.block.SandStormBlocks.DESALINATION_FILTER);
                        entries.accept(com.fhfelipefh.sandstorm.content.block.SandStormBlocks.NANITE_FABRICATOR);
                        entries.accept(com.fhfelipefh.sandstorm.content.block.SandStormBlocks.BURIED_TECH_RUINS);
                        entries.accept(com.fhfelipefh.sandstorm.content.block.SandStormBlocks.ANCIENT_DATA_CORE);
                        entries.accept(com.fhfelipefh.sandstorm.content.block.SandStormBlocks.DRONE_DOCK);
                        entries.accept(com.fhfelipefh.sandstorm.content.block.SandStormBlocks.ASSEMBLY_BAY);
                        entries.accept(SANDWORM_CHITIN);
                        entries.accept(SANDWORM_TOOTH);
                    })
                    .build()
    );

    public static <T extends Item> T register(String path, T item) {
        return Registry.register(BuiltInRegistries.ITEM, SandStormMod.id(path), item);
    }

    public static void initialize() {
    }
}
