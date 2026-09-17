package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class SandStormBlocks {
    public static final BrackishWaterBlock BRACKISH_AQUIFER = register("brackish_aquifer",
            new BrackishWaterBlock(BlockBehaviour.Properties.of()
                    .setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, SandStormMod.id("brackish_aquifer")))
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(1.5f)
                    .sound(SoundType.MUD)));
    public static final ThumperBlock THUMPER = register("thumper",
            new ThumperBlock(BlockBehaviour.Properties.of()
                    .setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, SandStormMod.id("thumper")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(3.0f)
                    .sound(SoundType.ANVIL)));
    public static final Printer3DBlock PRINTER_3D = register("printer_3d",
            new Printer3DBlock(BlockBehaviour.Properties.of()
                    .setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, SandStormMod.id("printer_3d")))
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(3.5f)
                    .sound(SoundType.METAL)));
    public static final DesalinationFilterBlock DESALINATION_FILTER = register("desalination_filter",
            new DesalinationFilterBlock(BlockBehaviour.Properties.of()
                    .setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, SandStormMod.id("desalination_filter")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.0f)
                    .sound(SoundType.COPPER)));
    public static final NaniteFabricatorBlock NANITE_FABRICATOR = register("nanite_fabricator",
            new NaniteFabricatorBlock(BlockBehaviour.Properties.of()
                    .setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, SandStormMod.id("nanite_fabricator")))
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(4.0f)
                    .sound(SoundType.NETHERITE_BLOCK)));
    public static final BuriedTechRuinsBlock BURIED_TECH_RUINS = register("buried_tech_ruins",
            new BuriedTechRuinsBlock(BlockBehaviour.Properties.of()
                    .setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, SandStormMod.id("buried_tech_ruins")))
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.NETHER_BRICKS)));
    public static final AncientDataCoreBlock ANCIENT_DATA_CORE = register("ancient_data_core",
            new AncientDataCoreBlock(BlockBehaviour.Properties.of()
                    .setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, SandStormMod.id("ancient_data_core")))
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(2.5f, 4.0f)
                    .lightLevel(state -> 7)
                    .sound(SoundType.AMETHYST)));
    public static final DroneDockBlock DRONE_DOCK = register("drone_dock",
            new DroneDockBlock(BlockBehaviour.Properties.of()
                    .setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, SandStormMod.id("drone_dock")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(3.5f)
                    .sound(SoundType.HEAVY_CORE)));
    public static final AssemblyBayBlock ASSEMBLY_BAY = register("assembly_bay",
            new AssemblyBayBlock(BlockBehaviour.Properties.of()
                    .setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, SandStormMod.id("assembly_bay")))
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(4.5f)
                    .sound(SoundType.NETHERITE_BLOCK)));
    public static final AtmosphericTerraformerBlock ATMOSPHERIC_TERRAFORMER = register("atmospheric_terraformer",
            new AtmosphericTerraformerBlock(BlockBehaviour.Properties.of()
                    .setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, SandStormMod.id("atmospheric_terraformer")))
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(5.0f)
                    .sound(SoundType.GLASS)));

    public static <T extends Block> T register(String path, T block) {
        T registeredBlock = Registry.register(BuiltInRegistries.BLOCK, SandStormMod.id(path), block);
        SandStormItems.register(path, new BlockItem(registeredBlock, new Item.Properties()));
        return registeredBlock;
    }

    public static void initialize() {
    }
}
