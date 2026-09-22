package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.DesalinationFilterBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.NaniteFabricatorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.Printer3DBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.ThermalGeneratorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.WirelessSolarReceiverBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.Set;

public class SandStormBlocks {
    public static final SandstoneWorkbenchBlock SANDSTONE_WORKBENCH = register("sandstone_workbench",
            new SandstoneWorkbenchBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("sandstone_workbench")))
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(1.5f)
                    .sound(SoundType.STONE)));
    public static final BrackishWaterBlock BRACKISH_AQUIFER = register("brackish_aquifer",
            new BrackishWaterBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("brackish_aquifer")))
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(1.5f)
                    .sound(SoundType.MUD)));
    public static final ThumperBlock THUMPER = register("thumper",
            new ThumperBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("thumper")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(3.0f)
                    .sound(SoundType.ANVIL)));
    public static final Printer3DBlock PRINTER_3D = register("printer_3d",
            new Printer3DBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("printer_3d")))
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(3.5f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    public static final DesalinationFilterBlock DESALINATION_FILTER = register("desalination_filter",
            new DesalinationFilterBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("desalination_filter")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.0f)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
    public static final NaniteFabricatorBlock NANITE_FABRICATOR = register("nanite_fabricator",
            new NaniteFabricatorBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("nanite_fabricator")))
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(4.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final FluidPipeBlock FLUID_PIPE = register("fluid_pipe",
            new FluidPipeBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("fluid_pipe")))
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(1.5f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    public static final BuriedTechRuinsBlock BURIED_TECH_RUINS = register("buried_tech_ruins",
            new BuriedTechRuinsBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("buried_tech_ruins")))
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.NETHER_BRICKS)));
    public static final AncientDataCoreBlock ANCIENT_DATA_CORE = register("ancient_data_core",
            new AncientDataCoreBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("ancient_data_core")))
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(2.5f, 4.0f)
                    .lightLevel(state -> 7)
                    .sound(SoundType.AMETHYST)));
    public static final DroneDockBlock DRONE_DOCK = register("drone_dock",
            new DroneDockBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("drone_dock")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(3.5f)
                    .sound(SoundType.HEAVY_CORE)));
    public static final AssemblyBayBlock ASSEMBLY_BAY = register("assembly_bay",
            new AssemblyBayBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("assembly_bay")))
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(4.5f)
                    .sound(SoundType.NETHERITE_BLOCK)));
    public static final AtmosphericTerraformerBlock ATMOSPHERIC_TERRAFORMER = register("atmospheric_terraformer",
            new AtmosphericTerraformerBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("atmospheric_terraformer")))
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(5.0f)
                    .sound(SoundType.GLASS)
                    .noOcclusion()));
    public static final WirelessSolarReceiverBlock WIRELESS_SOLAR_RECEIVER = register("wireless_solar_receiver",
            new WirelessSolarReceiverBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("wireless_solar_receiver")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.5f)
                    .lightLevel(state -> 4)
                    .sound(SoundType.COPPER), 1));
    public static final WirelessSolarReceiverBlock WIRELESS_SOLAR_RECEIVER_TIER2 = register("wireless_solar_receiver_tier2",
            new WirelessSolarReceiverBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("wireless_solar_receiver_tier2")))
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(4.5f)
                    .lightLevel(state -> 8)
                    .sound(SoundType.HEAVY_CORE), 2));
    public static final ThermalGeneratorBlock THERMAL_GENERATOR = register("thermal_generator",
            new ThermalGeneratorBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("thermal_generator")))
                    .mapColor(MapColor.COLOR_RED)
                    .strength(4.0f)
                    .lightLevel(state -> state.getValue(ThermalGeneratorBlock.LIT) ? 14 : 0)
                    .sound(SoundType.NETHERITE_BLOCK)));

    public static final BlockEntityType<Printer3DBlockEntity> PRINTER_3D_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("printer_3d"),
            new BlockEntityType<>(Printer3DBlockEntity::new, Set.of(PRINTER_3D))
    );
    public static final BlockEntityType<NaniteFabricatorBlockEntity> NANITE_FABRICATOR_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("nanite_fabricator"),
            new BlockEntityType<>(NaniteFabricatorBlockEntity::new, Set.of(NANITE_FABRICATOR))
    );
    public static final BlockEntityType<DesalinationFilterBlockEntity> DESALINATION_FILTER_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("desalination_filter"),
            new BlockEntityType<>(DesalinationFilterBlockEntity::new, Set.of(DESALINATION_FILTER))
    );
    public static final BlockEntityType<ThermalGeneratorBlockEntity> THERMAL_GENERATOR_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("thermal_generator"),
            new BlockEntityType<>(ThermalGeneratorBlockEntity::new, Set.of(THERMAL_GENERATOR))
    );
    public static final BlockEntityType<WirelessSolarReceiverBlockEntity> WIRELESS_SOLAR_RECEIVER_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("wireless_solar_receiver"),
            new BlockEntityType<>(WirelessSolarReceiverBlockEntity::new, Set.of(WIRELESS_SOLAR_RECEIVER, WIRELESS_SOLAR_RECEIVER_TIER2))
    );

    public static <T extends Block> T register(String path, T block) {
        T registeredBlock = Registry.register(BuiltInRegistries.BLOCK, SandStormMod.id(path), block);
        SandStormItems.register(path, new BlockItem(registeredBlock, SandStormItems.properties(path).useBlockDescriptionPrefix()));
        return registeredBlock;
    }

    public static void initialize() {
        WirelessSolarReceiverManager.initialize();
        ThermalGeneratorManager.initialize();
        registerTransferApi();
    }

    private static void registerTransferApi() {
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, PRINTER_3D_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, NANITE_FABRICATOR_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, DESALINATION_FILTER_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, THERMAL_GENERATOR_BE);
        FluidStorage.SIDED.registerForBlockEntity(DesalinationFilterBlockEntity::getFluidStorage, DESALINATION_FILTER_BE);
        FluidStorage.SIDED.registerForBlockEntity(ThermalGeneratorBlockEntity::getFluidStorage, THERMAL_GENERATOR_BE);
    }
}
