package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.AutonomousSonicTurretBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.ChemicalRefineryBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.DesalinationFilterBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.GridMonitorConsoleBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.HydroponicChamberBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.NaniteFabricatorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.Printer3DBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SandstoneFurnaceBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SmartFluidPipeBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SolidStateAccumulatorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.ThermalGeneratorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.WirelessSolarReceiverBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.WptRelayTowerBlockEntity;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;

import java.util.Set;

public class SandStormBlocks {
    public static final SandstoneWorkbenchBlock SANDSTONE_WORKBENCH = register("sandstone_workbench",
            new SandstoneWorkbenchBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("sandstone_workbench")))
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(0.8f)
                    .sound(SoundType.STONE)));
    public static final SandstoneFurnaceBlock SANDSTONE_FURNACE = register("sandstone_furnace",
            new SandstoneFurnaceBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("sandstone_furnace")))
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(1.5f)
                    .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 13 : 0)
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
    public static final DewCondenserBlock DEW_CONDENSER = register("dew_condenser",
            new DewCondenserBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("dew_condenser")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(2.5f)
                    .sound(SoundType.METAL)
                    .randomTicks()
                    .noOcclusion()));
    public static final NaniteFabricatorBlock NANITE_FABRICATOR = register("nanite_fabricator",
            new NaniteFabricatorBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("nanite_fabricator")))
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(4.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final ChemicalRefineryBlock CHEMICAL_REFINERY = register("chemical_refinery",
            new ChemicalRefineryBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("chemical_refinery")))
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(3.5f)
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
    public static final HydroponicChamberBlock HYDROPONIC_CHAMBER = register("hydroponic_chamber",
            new HydroponicChamberBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("hydroponic_chamber")))
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(3.5f)
                    .lightLevel(state -> state.getValue(HydroponicChamberBlock.LIT) ? 12 : 0)
                    .sound(SoundType.METAL)));
    public static final XenoGrassBlock XENO_GRASS_BLOCK = register("xeno_grass_block",
            new XenoGrassBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("xeno_grass_block")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(0.6f)
                    .sound(SoundType.GRASS)));
    public static final HeavySapCactusBlock HEAVY_SAP_CACTUS = register("heavy_sap_cactus",
            new HeavySapCactusBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("heavy_sap_cactus")))
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(1.2f)
                    .sound(SoundType.WOOL)));
    public static final SalinizedSandBlock SALINIZED_SAND = register("salinized_sand",
            new SalinizedSandBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("salinized_sand")))
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(0.7f)
                    .sound(SoundType.SAND)));
    public static final HalophytePlantBlock HALOPHYTE_PLANT = register("halophyte_plant",
            new HalophytePlantBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("halophyte_plant")))
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(0.3f)
                    .sound(SoundType.GRASS)));
    public static final AutonomousSonicTurretBlock AUTONOMOUS_SONIC_TURRET = register("autonomous_sonic_turret",
            new AutonomousSonicTurretBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("autonomous_sonic_turret")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(4.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final WptRelayTowerBlock WPT_RELAY_TOWER = register("wpt_relay_tower",
            new WptRelayTowerBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("wpt_relay_tower")))
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(3.5f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final SolidStateAccumulatorBlock SOLID_STATE_ACCUMULATOR = register("solid_state_accumulator",
            new SolidStateAccumulatorBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("solid_state_accumulator")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(4.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final SmartFluidPipeBlock SMART_FLUID_PIPE = register("smart_fluid_pipe",
            new SmartFluidPipeBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("smart_fluid_pipe")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(2.0f)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
    public static final GridMonitorConsoleBlock GRID_MONITOR_CONSOLE = register("grid_monitor_console",
            new GridMonitorConsoleBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("grid_monitor_console")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(3.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

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
    public static final BlockEntityType<ChemicalRefineryBlockEntity> CHEMICAL_REFINERY_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("chemical_refinery"),
            new BlockEntityType<>(ChemicalRefineryBlockEntity::new, Set.of(CHEMICAL_REFINERY))
    );
    public static final BlockEntityType<SandstoneFurnaceBlockEntity> SANDSTONE_FURNACE_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("sandstone_furnace"),
            new BlockEntityType<>(SandstoneFurnaceBlockEntity::new, Set.of(SANDSTONE_FURNACE))
    );
    public static final BlockEntityType<HydroponicChamberBlockEntity> HYDROPONIC_CHAMBER_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("hydroponic_chamber"),
            new BlockEntityType<>(HydroponicChamberBlockEntity::new, Set.of(HYDROPONIC_CHAMBER))
    );
    public static final BlockEntityType<AutonomousSonicTurretBlockEntity> AUTONOMOUS_SONIC_TURRET_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("autonomous_sonic_turret"),
            new BlockEntityType<>(AutonomousSonicTurretBlockEntity::new, Set.of(AUTONOMOUS_SONIC_TURRET))
    );
    public static final BlockEntityType<WptRelayTowerBlockEntity> WPT_RELAY_TOWER_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("wpt_relay_tower"),
            new BlockEntityType<>(WptRelayTowerBlockEntity::new, Set.of(WPT_RELAY_TOWER))
    );
    public static final BlockEntityType<SolidStateAccumulatorBlockEntity> SOLID_STATE_ACCUMULATOR_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("solid_state_accumulator"),
            new BlockEntityType<>(SolidStateAccumulatorBlockEntity::new, Set.of(SOLID_STATE_ACCUMULATOR))
    );
    public static final BlockEntityType<SmartFluidPipeBlockEntity> SMART_FLUID_PIPE_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("smart_fluid_pipe"),
            new BlockEntityType<>(SmartFluidPipeBlockEntity::new, Set.of(SMART_FLUID_PIPE))
    );
    public static final BlockEntityType<GridMonitorConsoleBlockEntity> GRID_MONITOR_CONSOLE_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("grid_monitor_console"),
            new BlockEntityType<>(GridMonitorConsoleBlockEntity::new, Set.of(GRID_MONITOR_CONSOLE))
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
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, CHEMICAL_REFINERY_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, SANDSTONE_FURNACE_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, THERMAL_GENERATOR_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, HYDROPONIC_CHAMBER_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, AUTONOMOUS_SONIC_TURRET_BE);
        FluidStorage.SIDED.registerForBlockEntity(DesalinationFilterBlockEntity::getFluidStorage, DESALINATION_FILTER_BE);
        FluidStorage.SIDED.registerForBlockEntity(ThermalGeneratorBlockEntity::getFluidStorage, THERMAL_GENERATOR_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, SOLID_STATE_ACCUMULATOR_BE);
        FluidStorage.SIDED.registerForBlockEntity(SmartFluidPipeBlockEntity::getFluidStorage, SMART_FLUID_PIPE_BE);
    }
}
