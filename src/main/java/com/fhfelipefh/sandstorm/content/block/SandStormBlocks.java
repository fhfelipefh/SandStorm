package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.AcousticDefensePylonBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericTerraformerBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.AutoAssemblyLineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.AutonomousSonicTurretBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.BioRegenerationPodBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.BioreactorVatBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.ChemicalRefineryBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.CrushingSpikeGateBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.CyborgDockingStationBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.CyborgIncubatorVatBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.DeepCoreBoreholeBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.DeepCoreDrillBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.DesalinationFilterBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.DewCondenserBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.ElectricFencePylonBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.GridMonitorConsoleBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.HoloTacticalSpireBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.HydroponicChamberBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.KineticRailgunBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.KineticShieldGeneratorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.LithoPlasmaExtractorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.MegastructureConstructorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.MolecularModifierBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.NaniteFabricatorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.OrbitalGroundStationBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.OrbitalMassDriverBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.PlasmaShieldGeneratorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.Printer3DBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.QuantumSleeperPodBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SpectralSurveyTelescopeBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SandstoneFurnaceBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SmartFluidPipeBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SolidStateAccumulatorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SupercriticalHeatExchangerBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SubspaceGatewayBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.ThermalGeneratorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.WirelessSolarReceiverBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.WptRelayTowerBlockEntity;
import com.fhfelipefh.sandstorm.content.storage.QuantumAccessTerminalBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumAccessTerminalBlockEntity;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskDriveBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskDriveBlockEntity;
import com.fhfelipefh.sandstorm.content.storage.QuantumNetworkCableBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumNetworkControllerBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumNetworkControllerBlockEntity;

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
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

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
                    .sound(SoundType.METAL)
                    .noOcclusion()));
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
    public static final PiezoQuartzBlock PIEZO_QUARTZ_BLOCK = register("piezo_quartz_block",
            new PiezoQuartzBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("piezo_quartz_block")))
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(2.5f, 4.0f)
                    .sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops()));
    public static final BuddingPiezoQuartzBlock BUDDING_PIEZO_QUARTZ = register("budding_piezo_quartz",
            new BuddingPiezoQuartzBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("budding_piezo_quartz")))
                    .randomTicks()
                    .mapColor(MapColor.COLOR_MAGENTA)
                    .strength(3.0f, 5.0f)
                    .sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops()));
    public static final PiezoQuartzClusterBlock PIEZO_QUARTZ_CLUSTER = register("piezo_quartz_cluster",
            new PiezoQuartzClusterBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("piezo_quartz_cluster")))
                    .mapColor(MapColor.COLOR_PURPLE)
                    .forceSolidOn()
                    .noOcclusion()
                    .randomTicks()
                    .sound(SoundType.AMETHYST_CLUSTER)
                    .strength(1.5f)
                    .lightLevel(state -> 5)
                    .pushReaction(PushReaction.POPPED)));
    public static final DeepCoreDrillBlock DEEP_CORE_DRILL = register("deep_core_drill",
            new DeepCoreDrillBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("deep_core_drill")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(5.0f, 12.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .requiresCorrectToolForDrops()));
    public static final ThermalSpringStoneBlock THERMAL_SPRING_STONE = register("thermal_spring_stone",
            new ThermalSpringStoneBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("thermal_spring_stone")))
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(1.5f)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 4)));
    public static final ElectrifiedSandBlock ELECTRIFIED_SAND = register("electrified_sand",
            new ElectrifiedSandBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("electrified_sand")))
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(0.6f)
                    .sound(SoundType.SAND)));
    public static final FossilizedAmberBlock FOSSILIZED_AMBER = register("fossilized_amber",
            new FossilizedAmberBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("fossilized_amber")))
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(2.0f)
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> 6)
                    .noOcclusion()));
    public static final FulguriteGlassBlock FULGURITE_GLASS = register("fulgurite_glass",
            new FulguriteGlassBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("fulgurite_glass")))
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(1.0f)
                    .sound(SoundType.GLASS)
                    .noOcclusion()));
    public static final AncientReedBlock ANCIENT_REED_BLOCK = register("ancient_reed_block",
            new AncientReedBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("ancient_reed_block")))
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.3f)
                    .sound(SoundType.GRASS)
                    .noOcclusion()));
    public static final KineticShieldGeneratorBlock KINETIC_SHIELD_GENERATOR = register("kinetic_shield_generator",
            new KineticShieldGeneratorBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("kinetic_shield_generator")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(4.5f, 9.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final SandMaglevRailBlock SAND_MAGLEV_RAIL = register("sand_maglev_rail",
            new SandMaglevRailBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("sand_maglev_rail")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .noCollision()
                    .strength(0.7f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    public static final HabitatDomeBlock HABITAT_DOME = register("habitat_dome",
            new HabitatDomeBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("habitat_dome")))
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(3.5f)
                    .lightLevel(state -> 8)
                    .sound(SoundType.GLASS)
                    .noOcclusion()));
    public static final AutoAssemblyLineBlock AUTO_ASSEMBLY_LINE = register("auto_assembly_line",
            new AutoAssemblyLineBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("auto_assembly_line")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(4.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final TitaniumSpikeWallBlock TITANIUM_SPIKE_WALL = register("titanium_spike_wall",
            new TitaniumSpikeWallBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("titanium_spike_wall")))
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final RetractableSpikeWallBlock RETRACTABLE_SPIKE_WALL = register("retractable_spike_wall",
            new RetractableSpikeWallBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("retractable_spike_wall")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.METAL)));
    public static final ElectrifiedSpikeBarrierBlock ELECTRIFIED_SPIKE_BARRIER = register("electrified_spike_barrier",
            new ElectrifiedSpikeBarrierBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("electrified_spike_barrier")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.0f, 5.0f)
                    .sound(SoundType.COPPER)
                    .lightLevel(state -> 4)
                    .noOcclusion()));
    public static final CorrosiveChitinSpikeWallBlock CORROSIVE_CHITIN_SPIKE_WALL = register("corrosive_chitin_spike_wall",
            new CorrosiveChitinSpikeWallBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("corrosive_chitin_spike_wall")))
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(3.0f, 5.0f)
                    .sound(SoundType.BONE_BLOCK)
                    .noOcclusion()));
    public static final KineticFloorSpikesBlock KINETIC_FLOOR_SPIKES = register("kinetic_floor_spikes",
            new KineticFloorSpikesBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("kinetic_floor_spikes")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(2.5f, 4.0f)
                    .sound(SoundType.ANVIL)
                    .noOcclusion()));
    public static final CrushingSpikeGateBlock CRUSHING_SPIKE_GATE = register("crushing_spike_gate",
            new CrushingSpikeGateBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("crushing_spike_gate")))
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(4.0f, 8.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final MegastructureConstructorBlock MEGASTRUCTURE_CONSTRUCTOR = register("megastructure_constructor",
            new MegastructureConstructorBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("megastructure_constructor")))
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(5.0f, 10.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final RadiotrophicMyceliumBlock RADIOTROPHIC_MYCELIUM = register("radiotrophic_mycelium",
            new RadiotrophicMyceliumBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("radiotrophic_mycelium")))
                    .mapColor(MapColor.COLOR_BLACK)
                    .instabreak()
                    .sound(SoundType.SCULK)));
    public static final ChitinolyticFungusBlock CHITINOLYTIC_FUNGUS = register("chitinolytic_fungus",
            new ChitinolyticFungusBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("chitinolytic_fungus")))
                    .mapColor(MapColor.COLOR_ORANGE)
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .noOcclusion()));
    public static final CryoXerophilicLichenBlock CRYO_XEROPHILIC_LICHEN = register("cryo_xerophilic_lichen",
            new CryoXerophilicLichenBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("cryo_xerophilic_lichen")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .instabreak()
                    .sound(SoundType.LILY_PAD)
                    .noOcclusion()));
    public static final HalophyteSucculentBlock HALOPHYTE_SUCCULENT = register("halophyte_succulent",
            new HalophyteSucculentBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("halophyte_succulent")))
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .noOcclusion()));
    public static final DuneEphedraBlock DUNE_EPHEDRA = register("dune_ephedra",
            new DuneEphedraBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("dune_ephedra")))
                    .mapColor(MapColor.COLOR_YELLOW)
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .noOcclusion()));
    public static final BioreactorVatBlock BIOREACTOR_VAT = register("bioreactor_vat",
            new BioreactorVatBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("bioreactor_vat")))
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    public static final MolecularModifierBlock MOLECULAR_MODIFIER = register("molecular_modifier",
            new MolecularModifierBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("molecular_modifier")))
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    public static final BioRegenerationPodBlock BIO_REGENERATION_POD = register("bio_regeneration_pod",
            new BioRegenerationPodBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("bio_regeneration_pod")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
    public static final CyborgIncubatorVatBlock CYBORG_INCUBATOR_VAT = register("cyborg_incubator_vat",
            new CyborgIncubatorVatBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("cyborg_incubator_vat")))
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(4.5f, 8.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final CyborgDockingStationBlock CYBORG_DOCKING_STATION = register("cyborg_docking_station",
            new CyborgDockingStationBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("cyborg_docking_station")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(4.0f, 7.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final HoloTacticalSpireBlock HOLO_TACTICAL_SPIRE = register("holo_tactical_spire",
            new HoloTacticalSpireBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("holo_tactical_spire")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(5.0f, 9.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> state.getValue(HoloTacticalSpireBlock.ACTIVE) ? 12 : 0)
                    .noOcclusion()));
    public static final QuantumSleeperPodBlock QUANTUM_SLEEPER_POD = register("quantum_sleeper_pod",
            new QuantumSleeperPodBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("quantum_sleeper_pod")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(4.5f, 9.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> state.getValue(QuantumSleeperPodBlock.ACTIVE) ? 8 : 0)
                    .noOcclusion()));
    public static final PlasmaShieldGeneratorBlock PLASMA_SHIELD_GENERATOR = register("plasma_shield_generator",
            new PlasmaShieldGeneratorBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("plasma_shield_generator")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(5.0f, 12.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> state.getValue(PlasmaShieldGeneratorBlock.ACTIVE) ? 14 : 0)
                    .noOcclusion()));
    public static final KineticRailgunBlock KINETIC_RAILGUN = register("kinetic_railgun",
            new KineticRailgunBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("kinetic_railgun")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(6.0f, 15.0f)
                    .sound(SoundType.HEAVY_CORE)
                    .lightLevel(state -> state.getValue(KineticRailgunBlock.LIT) ? 10 : 0)
                    .noOcclusion()));
    public static final AcousticDefensePylonBlock ACOUSTIC_DEFENSE_PYLON = register("acoustic_defense_pylon",
            new AcousticDefensePylonBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("acoustic_defense_pylon")))
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(4.0f, 8.0f)
                    .sound(SoundType.COPPER)
                    .lightLevel(state -> state.getValue(AcousticDefensePylonBlock.ACTIVE) ? 8 : 0)
                    .noOcclusion()));
    public static final ElectricFencePylonBlock ELECTRIC_FENCE_PYLON = register("electric_fence_pylon",
            new ElectricFencePylonBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("electric_fence_pylon")))
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(4.0f, 8.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> state.getValue(ElectricFencePylonBlock.CONNECTED) ? 12 : (state.getValue(ElectricFencePylonBlock.LIT) ? 6 : 0))
                    .noOcclusion()));
    public static final DeepCoreBoreholeBlock DEEP_CORE_BOREHOLE = register("deep_core_borehole",
            new DeepCoreBoreholeBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("deep_core_borehole")))
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(5.0f, 12.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> state.getValue(DeepCoreBoreholeBlock.LIT) ? 10 : 0)
                    .noOcclusion()));
    public static final LithoPlasmaExtractorBlock LITHO_PLASMA_EXTRACTOR = register("litho_plasma_extractor",
            new LithoPlasmaExtractorBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("litho_plasma_extractor")))
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(4.5f, 10.0f)
                    .sound(SoundType.HEAVY_CORE)
                    .lightLevel(state -> state.getValue(LithoPlasmaExtractorBlock.LIT) ? 8 : 0)
                    .noOcclusion()));
    public static final SupercriticalHeatExchangerBlock SUPERCRITICAL_HEAT_EXCHANGER = register("supercritical_heat_exchanger",
            new SupercriticalHeatExchangerBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("supercritical_heat_exchanger")))
                    .mapColor(MapColor.COLOR_RED)
                    .strength(4.0f, 9.0f)
                    .sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(SupercriticalHeatExchangerBlock.LIT) ? 12 : 0)
                    .noOcclusion()));
    public static final OrbitalMassDriverBlock ORBITAL_MASS_DRIVER = register("orbital_mass_driver",
            new OrbitalMassDriverBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("orbital_mass_driver")))
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(5.0f, 12.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final OrbitalGroundStationBlock ORBITAL_GROUND_STATION = register("orbital_ground_station",
            new OrbitalGroundStationBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("orbital_ground_station")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(4.5f, 10.0f)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
    public static final SpectralSurveyTelescopeBlock SPECTRAL_SURVEY_TELESCOPE = register("spectral_survey_telescope",
            new SpectralSurveyTelescopeBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("spectral_survey_telescope")))
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(4.0f, 8.0f)
                    .sound(SoundType.HEAVY_CORE)
                    .noOcclusion()));
    public static final SubspaceGatewayBlock SUBSPACE_GATEWAY = register("subspace_gateway",
            new SubspaceGatewayBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("subspace_gateway")))
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(5.0f, 1200.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> state.getValue(SubspaceGatewayBlock.LIT) ? 14 : 0)
                    .noOcclusion()));
    public static final SeismicDampenerPavingBlock SEISMIC_DAMPENER_PAVING = register("seismic_dampener_paving",
            new SeismicDampenerPavingBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("seismic_dampener_paving")))
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.2f)
                    .sound(SoundType.WOOL)
                    .noOcclusion()));
    public static final Block SALT_BRICKS = register("salt_bricks",
            new Block(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("salt_bricks")))
                    .mapColor(MapColor.SNOW)
                    .strength(1.5f, 6.0f)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 2)));
    public static final StairBlock SALT_BRICK_STAIRS = register("salt_brick_stairs",
            new StairBlock(SALT_BRICKS.defaultBlockState(), BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("salt_brick_stairs")))
                    .mapColor(MapColor.SNOW)
                    .strength(1.5f, 6.0f)
                    .sound(SoundType.STONE)));
    public static final SlabBlock SALT_BRICK_SLAB = register("salt_brick_slab",
            new SlabBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("salt_brick_slab")))
                    .mapColor(MapColor.SNOW)
                    .strength(1.5f, 6.0f)
                    .sound(SoundType.STONE)));
    public static final WallBlock SALT_BRICK_WALL = register("salt_brick_wall",
            new WallBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("salt_brick_wall")))
                    .mapColor(MapColor.SNOW)
                    .strength(1.5f, 6.0f)
                    .sound(SoundType.STONE)));
    public static final QuantumNetworkControllerBlock QUANTUM_NETWORK_CONTROLLER = register("quantum_network_controller",
            new QuantumNetworkControllerBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("quantum_network_controller")))
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(4.0f, 8.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final QuantumDiskDriveBlock QUANTUM_DISK_DRIVE = register("quantum_disk_drive",
            new QuantumDiskDriveBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("quantum_disk_drive")))
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final QuantumAccessTerminalBlock QUANTUM_ACCESS_TERMINAL = register("quantum_access_terminal",
            new QuantumAccessTerminalBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("quantum_access_terminal")))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()));
    public static final QuantumNetworkCableBlock QUANTUM_NETWORK_CABLE = register("quantum_network_cable",
            new QuantumNetworkCableBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, SandStormMod.id("quantum_network_cable")))
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(1.0f, 3.0f)
                    .sound(SoundType.COPPER)
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
    public static final BlockEntityType<DewCondenserBlockEntity> DEW_CONDENSER_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("dew_condenser"),
            new BlockEntityType<>(DewCondenserBlockEntity::new, Set.of(DEW_CONDENSER))
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
    public static final BlockEntityType<DeepCoreDrillBlockEntity> DEEP_CORE_DRILL_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("deep_core_drill"),
            new BlockEntityType<>(DeepCoreDrillBlockEntity::new, Set.of(DEEP_CORE_DRILL))
    );
    public static final BlockEntityType<KineticShieldGeneratorBlockEntity> KINETIC_SHIELD_GENERATOR_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("kinetic_shield_generator"),
            new BlockEntityType<>(KineticShieldGeneratorBlockEntity::new, Set.of(KINETIC_SHIELD_GENERATOR))
    );
    public static final BlockEntityType<AutoAssemblyLineBlockEntity> AUTO_ASSEMBLY_LINE_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("auto_assembly_line"),
            new BlockEntityType<>(AutoAssemblyLineBlockEntity::new, Set.of(AUTO_ASSEMBLY_LINE))
    );
    public static final BlockEntityType<MegastructureConstructorBlockEntity> MEGASTRUCTURE_CONSTRUCTOR_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("megastructure_constructor"),
            new BlockEntityType<>(MegastructureConstructorBlockEntity::new, Set.of(MEGASTRUCTURE_CONSTRUCTOR))
    );
    public static final BlockEntityType<BioreactorVatBlockEntity> BIOREACTOR_VAT_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("bioreactor_vat"),
            new BlockEntityType<>(BioreactorVatBlockEntity::new, Set.of(BIOREACTOR_VAT))
    );
    public static final BlockEntityType<MolecularModifierBlockEntity> MOLECULAR_MODIFIER_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("molecular_modifier"),
            new BlockEntityType<>(MolecularModifierBlockEntity::new, Set.of(MOLECULAR_MODIFIER))
    );
    public static final BlockEntityType<BioRegenerationPodBlockEntity> BIO_REGENERATION_POD_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("bio_regeneration_pod"),
            new BlockEntityType<>(BioRegenerationPodBlockEntity::new, Set.of(BIO_REGENERATION_POD))
    );
    public static final BlockEntityType<CyborgIncubatorVatBlockEntity> CYBORG_INCUBATOR_VAT_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("cyborg_incubator_vat"),
            new BlockEntityType<>(CyborgIncubatorVatBlockEntity::new, Set.of(CYBORG_INCUBATOR_VAT))
    );
    public static final BlockEntityType<CyborgDockingStationBlockEntity> CYBORG_DOCKING_STATION_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("cyborg_docking_station"),
            new BlockEntityType<>(CyborgDockingStationBlockEntity::new, Set.of(CYBORG_DOCKING_STATION))
    );
    public static final BlockEntityType<HoloTacticalSpireBlockEntity> HOLO_TACTICAL_SPIRE_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("holo_tactical_spire"),
            new BlockEntityType<>(HoloTacticalSpireBlockEntity::new, Set.of(HOLO_TACTICAL_SPIRE))
    );
    public static final BlockEntityType<QuantumSleeperPodBlockEntity> QUANTUM_SLEEPER_POD_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("quantum_sleeper_pod"),
            new BlockEntityType<>(QuantumSleeperPodBlockEntity::new, Set.of(QUANTUM_SLEEPER_POD))
    );
    public static final BlockEntityType<PlasmaShieldGeneratorBlockEntity> PLASMA_SHIELD_GENERATOR_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("plasma_shield_generator"),
            new BlockEntityType<>(PlasmaShieldGeneratorBlockEntity::new, Set.of(PLASMA_SHIELD_GENERATOR))
    );
    public static final BlockEntityType<KineticRailgunBlockEntity> KINETIC_RAILGUN_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("kinetic_railgun"),
            new BlockEntityType<>(KineticRailgunBlockEntity::new, Set.of(KINETIC_RAILGUN))
    );
    public static final BlockEntityType<AcousticDefensePylonBlockEntity> ACOUSTIC_DEFENSE_PYLON_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("acoustic_defense_pylon"),
            new BlockEntityType<>(AcousticDefensePylonBlockEntity::new, Set.of(ACOUSTIC_DEFENSE_PYLON))
    );
    public static final BlockEntityType<DeepCoreBoreholeBlockEntity> DEEP_CORE_BOREHOLE_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("deep_core_borehole"),
            new BlockEntityType<>(DeepCoreBoreholeBlockEntity::new, Set.of(DEEP_CORE_BOREHOLE))
    );
    public static final BlockEntityType<LithoPlasmaExtractorBlockEntity> LITHO_PLASMA_EXTRACTOR_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("litho_plasma_extractor"),
            new BlockEntityType<>(LithoPlasmaExtractorBlockEntity::new, Set.of(LITHO_PLASMA_EXTRACTOR))
    );
    public static final BlockEntityType<SupercriticalHeatExchangerBlockEntity> SUPERCRITICAL_HEAT_EXCHANGER_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("supercritical_heat_exchanger"),
            new BlockEntityType<>(SupercriticalHeatExchangerBlockEntity::new, Set.of(SUPERCRITICAL_HEAT_EXCHANGER))
    );
    public static final BlockEntityType<OrbitalMassDriverBlockEntity> ORBITAL_MASS_DRIVER_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("orbital_mass_driver"),
            new BlockEntityType<>(OrbitalMassDriverBlockEntity::new, Set.of(ORBITAL_MASS_DRIVER))
    );
    public static final BlockEntityType<OrbitalGroundStationBlockEntity> ORBITAL_GROUND_STATION_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("orbital_ground_station"),
            new BlockEntityType<>(OrbitalGroundStationBlockEntity::new, Set.of(ORBITAL_GROUND_STATION))
    );
    public static final BlockEntityType<SpectralSurveyTelescopeBlockEntity> SPECTRAL_SURVEY_TELESCOPE_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("spectral_survey_telescope"),
            new BlockEntityType<>(SpectralSurveyTelescopeBlockEntity::new, Set.of(SPECTRAL_SURVEY_TELESCOPE))
    );
    public static final BlockEntityType<CrushingSpikeGateBlockEntity> CRUSHING_SPIKE_GATE_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("crushing_spike_gate"),
            new BlockEntityType<>(CrushingSpikeGateBlockEntity::new, Set.of(CRUSHING_SPIKE_GATE))
    );
    public static final BlockEntityType<AtmosphericTerraformerBlockEntity> ATMOSPHERIC_TERRAFORMER_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("atmospheric_terraformer"),
            new BlockEntityType<>(AtmosphericTerraformerBlockEntity::new, Set.of(ATMOSPHERIC_TERRAFORMER))
    );
    public static final BlockEntityType<SubspaceGatewayBlockEntity> SUBSPACE_GATEWAY_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("subspace_gateway"),
            new BlockEntityType<>(SubspaceGatewayBlockEntity::new, Set.of(SUBSPACE_GATEWAY))
    );
    public static final BlockEntityType<ElectricFencePylonBlockEntity> ELECTRIC_FENCE_PYLON_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("electric_fence_pylon"),
            new BlockEntityType<>(ElectricFencePylonBlockEntity::new, Set.of(ELECTRIC_FENCE_PYLON))
    );
    public static final BlockEntityType<QuantumNetworkControllerBlockEntity> QUANTUM_NETWORK_CONTROLLER_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("quantum_network_controller"),
            new BlockEntityType<>(QuantumNetworkControllerBlockEntity::new, Set.of(QUANTUM_NETWORK_CONTROLLER))
    );
    public static final BlockEntityType<QuantumDiskDriveBlockEntity> QUANTUM_DISK_DRIVE_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("quantum_disk_drive"),
            new BlockEntityType<>(QuantumDiskDriveBlockEntity::new, Set.of(QUANTUM_DISK_DRIVE))
    );
    public static final BlockEntityType<QuantumAccessTerminalBlockEntity> QUANTUM_ACCESS_TERMINAL_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SandStormMod.id("quantum_access_terminal"),
            new BlockEntityType<>(QuantumAccessTerminalBlockEntity::new, Set.of(QUANTUM_ACCESS_TERMINAL))
    );

    public static <T extends Block> T register(String path, T block) {
        T registeredBlock = Registry.register(BuiltInRegistries.BLOCK, SandStormMod.id(path), block);
        SandStormItems.register(path, new BlockItem(registeredBlock, SandStormItems.properties(path).useBlockDescriptionPrefix()));
        return registeredBlock;
    }

    public static void initialize() {
        WirelessSolarReceiverManager.initialize();
        ThermalGeneratorManager.initialize();
        SupercriticalHeatExchangerManager.initialize();
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
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, DEW_CONDENSER_BE);
        FluidStorage.SIDED.registerForBlockEntity(DewCondenserBlockEntity::getFluidStorage, DEW_CONDENSER_BE);
        FluidStorage.SIDED.registerForBlockEntity(ThermalGeneratorBlockEntity::getFluidStorage, THERMAL_GENERATOR_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, SOLID_STATE_ACCUMULATOR_BE);
        FluidStorage.SIDED.registerForBlockEntity(SmartFluidPipeBlockEntity::getFluidStorage, SMART_FLUID_PIPE_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, DEEP_CORE_DRILL_BE);
        FluidStorage.SIDED.registerForBlockEntity(DeepCoreDrillBlockEntity::getFluidStorage, DEEP_CORE_DRILL_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, KINETIC_SHIELD_GENERATOR_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, AUTO_ASSEMBLY_LINE_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, MEGASTRUCTURE_CONSTRUCTOR_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, BIOREACTOR_VAT_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, MOLECULAR_MODIFIER_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, BIO_REGENERATION_POD_BE);
        FluidStorage.SIDED.registerForBlockEntity(BioRegenerationPodBlockEntity::getFluidStorage, BIO_REGENERATION_POD_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, CYBORG_INCUBATOR_VAT_BE);
        FluidStorage.SIDED.registerForBlockEntity(CyborgIncubatorVatBlockEntity::getFluidStorage, CYBORG_INCUBATOR_VAT_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, HOLO_TACTICAL_SPIRE_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, QUANTUM_SLEEPER_POD_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, KINETIC_RAILGUN_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, DEEP_CORE_BOREHOLE_BE);
        FluidStorage.SIDED.registerForBlockEntity(DeepCoreBoreholeBlockEntity::getFluidStorage, DEEP_CORE_BOREHOLE_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, LITHO_PLASMA_EXTRACTOR_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, SUPERCRITICAL_HEAT_EXCHANGER_BE);
        FluidStorage.SIDED.registerForBlockEntity(SupercriticalHeatExchangerBlockEntity::getFluidStorage, SUPERCRITICAL_HEAT_EXCHANGER_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, ORBITAL_MASS_DRIVER_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, ORBITAL_GROUND_STATION_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, ATMOSPHERIC_TERRAFORMER_BE);
        FluidStorage.SIDED.registerForBlockEntity(AtmosphericTerraformerBlockEntity::getFluidStorage, ATMOSPHERIC_TERRAFORMER_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, ELECTRIC_FENCE_PYLON_BE);
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, QUANTUM_DISK_DRIVE_BE);
    }
}
