package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.equipment.ArmorType;

public class SandStormItems {
    public static ResourceKey<Item> itemKey(String path) {
        return SandStormMod.itemKey(path);
    }

    public static Item.Properties properties(String path) {
        return new Item.Properties().setId(itemKey(path));
    }

    public static final SpaceRationItem SPACE_RATION = register("space_ration", new SpaceRationItem(properties("space_ration")));
    public static final SpaceSuitItem SPACE_SUIT_HELMET = register("space_suit_helmet", new SpaceSuitItem(ArmorType.HELMET, properties("space_suit_helmet")));
    public static final SpaceSuitItem SPACE_SUIT_CHESTPLATE = register("space_suit_chestplate", new SpaceSuitItem(ArmorType.CHESTPLATE, properties("space_suit_chestplate")));
    public static final SpaceSuitItem SPACE_SUIT_LEGGINGS = register("space_suit_leggings", new SpaceSuitItem(ArmorType.LEGGINGS, properties("space_suit_leggings")));
    public static final SpaceSuitItem SPACE_SUIT_BOOTS = register("space_suit_boots", new SpaceSuitItem(ArmorType.BOOTS, properties("space_suit_boots")));
    public static final BrackishWaterBottleItem BRACKISH_WATER_BOTTLE = register("brackish_water_bottle", new BrackishWaterBottleItem(properties("brackish_water_bottle")));
    public static final Item SANDWORM_CHITIN = register("sandworm_chitin", new Item(properties("sandworm_chitin").rarity(Rarity.UNCOMMON)));
    public static final Item SANDWORM_TOOTH = register("sandworm_tooth", new Item(properties("sandworm_tooth").rarity(Rarity.RARE)));
    public static final Item RAW_SILICON = register("raw_silicon", new Item(properties("raw_silicon")));
    public static final Item SILICON_WAFER = register("silicon_wafer", new Item(properties("silicon_wafer")));
    public static final Item MINERAL_SALT = register("mineral_salt", new Item(properties("mineral_salt")));
    public static final PotableWaterBottleItem POTABLE_WATER_BOTTLE = register("potable_water_bottle", new PotableWaterBottleItem(properties("potable_water_bottle")));
    public static final Item CIRCUIT_BOARD = register("circuit_board", new Item(properties("circuit_board").rarity(Rarity.UNCOMMON)));
    public static final Item NANO_ACTUATOR = register("nano_actuator", new Item(properties("nano_actuator").rarity(Rarity.RARE)));
    public static final AnomalyRadarItem ANOMALY_RADAR = register("anomaly_radar", new AnomalyRadarItem(properties("anomaly_radar")));
    public static final Item TECH_DISC = register("tech_disc", new Item(properties("tech_disc").rarity(Rarity.RARE)));
    public static final Item SCRAP_METAL = register("scrap_metal", new Item(properties("scrap_metal").rarity(Rarity.UNCOMMON)));
    public static final SonicCannonItem SONIC_CANNON = register("sonic_cannon", new SonicCannonItem(properties("sonic_cannon")));
    public static final PlasmaRifleItem PLASMA_RIFLE = register("plasma_rifle", new PlasmaRifleItem(properties("plasma_rifle")));
    public static final VibroCrysknifeItem VIBRO_CRYSKNIFE = register("vibro_crysknife", new VibroCrysknifeItem(properties("vibro_crysknife")));
    public static final AtmosphericAnalyzerItem ATMOSPHERIC_ANALYZER = register("atmospheric_analyzer", new AtmosphericAnalyzerItem(properties("atmospheric_analyzer")));
    public static final SurvivalDatapadItem SURVIVAL_DATAPAD = register("survival_datapad", new SurvivalDatapadItem(properties("survival_datapad").rarity(Rarity.RARE).stacksTo(1)));
    public static final NutrientBombItem NUTRIENT_BOMB = register("nutrient_bomb", new NutrientBombItem(properties("nutrient_bomb").stacksTo(16)));
    public static final FilterCartridgeItem FILTER_CARTRIDGE = register("filter_cartridge", new FilterCartridgeItem(properties("filter_cartridge").durability(1000).rarity(Rarity.UNCOMMON)));
    public static final TechBucketItem TECH_BUCKET = register("tech_bucket", new TechBucketItem(properties("tech_bucket").stacksTo(1).rarity(Rarity.RARE)));
    public static final SpawnEggItem SANDWORM_SPAWN_EGG = register("sandworm_spawn_egg", new SpawnEggItem(properties("sandworm_spawn_egg").spawnEgg(SandStormEntities.SANDWORM)));

    public static final CreativeModeTab SANDSTORM_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            SandStormMod.id("general"),
            FabricCreativeModeTab.builder()
                    .icon(() -> new ItemStack(SPACE_SUIT_CHESTPLATE))
                    .title(Component.translatable("itemGroup.sandstorm.general"))
                    .displayItems((context, entries) -> {
                        entries.accept(SURVIVAL_DATAPAD);
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
                        entries.accept(FILTER_CARTRIDGE);
                        entries.accept(TECH_BUCKET);
                        entries.accept(ANOMALY_RADAR);
                        entries.accept(TECH_DISC);
                        entries.accept(SCRAP_METAL);
                        entries.accept(SONIC_CANNON);
                        entries.accept(PLASMA_RIFLE);
                        entries.accept(VIBRO_CRYSKNIFE);
                        entries.accept(ATMOSPHERIC_ANALYZER);
                        entries.accept(SandStormBlocks.BRACKISH_AQUIFER);
                        entries.accept(SandStormBlocks.THUMPER);
                        entries.accept(SandStormBlocks.PRINTER_3D);
                        entries.accept(SandStormBlocks.DESALINATION_FILTER);
                        entries.accept(SandStormBlocks.NANITE_FABRICATOR);
                        entries.accept(SandStormBlocks.FLUID_PIPE);
                        entries.accept(SandStormBlocks.BURIED_TECH_RUINS);
                        entries.accept(SandStormBlocks.ANCIENT_DATA_CORE);
                        entries.accept(SandStormBlocks.DRONE_DOCK);
                        entries.accept(SandStormBlocks.ASSEMBLY_BAY);
                        entries.accept(SandStormBlocks.ATMOSPHERIC_TERRAFORMER);
                        entries.accept(SandStormBlocks.WIRELESS_SOLAR_RECEIVER);
                        entries.accept(SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2);
                        entries.accept(NUTRIENT_BOMB);
                        entries.accept(SANDWORM_CHITIN);
                        entries.accept(SANDWORM_TOOTH);
                        entries.accept(SANDWORM_SPAWN_EGG);
                    })
                    .build()
    );

    public static <T extends Item> T register(String path, T item) {
        return Registry.register(BuiltInRegistries.ITEM, SandStormMod.id(path), item);
    }

    public static void initialize() {
    }
}
