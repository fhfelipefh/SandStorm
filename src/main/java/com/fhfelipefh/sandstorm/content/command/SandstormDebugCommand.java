package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.block.AcousticDefensePylonBlock;
import com.fhfelipefh.sandstorm.content.block.DeepCoreBoreholeBlock;
import com.fhfelipefh.sandstorm.content.block.HoloTacticalSpireBlock;
import com.fhfelipefh.sandstorm.content.block.KineticRailgunBlock;
import com.fhfelipefh.sandstorm.content.block.LithoPlasmaExtractorBlock;
import com.fhfelipefh.sandstorm.content.block.OrbitalGroundStationBlock;
import com.fhfelipefh.sandstorm.content.block.OrbitalMassDriverBlock;
import com.fhfelipefh.sandstorm.content.block.PlasmaShieldGeneratorBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SpectralSurveyTelescopeBlock;
import com.fhfelipefh.sandstorm.content.block.SupercriticalHeatExchangerBlock;
import com.fhfelipefh.sandstorm.content.block.entity.AcousticDefensePylonBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.DeepCoreBoreholeBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.HoloTacticalSpireBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.KineticRailgunBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.LithoPlasmaExtractorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.OrbitalGroundStationBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.OrbitalMassDriverBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.PlasmaShieldGeneratorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.QuantumSleeperPodBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SupercriticalHeatExchangerBlockEntity;
import com.fhfelipefh.sandstorm.content.entity.AquiferBeetleEntity;
import com.fhfelipefh.sandstorm.content.entity.BuilderDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CargoDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CrawlerDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CyberHoundEntity;
import com.fhfelipefh.sandstorm.content.entity.CyberneticGolemEntity;
import com.fhfelipefh.sandstorm.content.entity.DerelictAutomatonEntity;
import com.fhfelipefh.sandstorm.content.entity.LaborerUnitEntity;
import com.fhfelipefh.sandstorm.content.entity.ScoutDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
import com.fhfelipefh.sandstorm.content.entity.GolemMetalTier;
import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.entity.SandboardEntity;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.entity.ScrapSentinelEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgBuilderEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgExcavatorEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgHarvesterEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSwarmManager;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.survival.PlayerSuitSavedData;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import com.fhfelipefh.sandstorm.content.world.ShowcaseAutomation;
import com.fhfelipefh.sandstorm.content.world.structure.ColossalCastleGenerator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class SandstormDebugCommand {

    private static final List<String> PHASES = List.of(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "all"
    );

    private static final List<String> SPAWNABLES = List.of(
            "cyborg_excavator",
            "cyborg_builder",
            "cyborg_harvester",
            "excavator_vehicle",
            "megazord",
            "megazord_flight",
            "megazord_sub",
            "megazord_apex",
            "cargo_drone",
            "builder_drone",
            "sandworm",
            "sandboard",
            "scrap_sentinel",
            "scrap_sentinel_dormant",
            "aquifer_beetle",
            "aquifer_beetle_hibernating",
            "derelict_automaton",
            "cyber_hound",
            "laborer_unit",
            "scout_drone",
            "crawler_drone",
            "cybernetic_golem"
    );

    private static final List<String> FACILITIES = List.of(
            "cyborg_outpost",
            "medbay_clinic",
            "bioreactor_lab",
            "molecular_workshop",
            "spike_fortress",
            "power_station",
            "megastructure_site",
            "hydroponics_dome",
            "deep_drill_station",
            "defense_perimeter",
            "refinery_complex",
            "terraformer_dome",
            "maglev_station",
            "starter_base",
            "ancient_ruin_site",
            "clone_facility",
            "plasma_defense_complex",
            "geothermal_well",
            "orbital_array",
            "colossal_castle",
            "computer"
    );

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandstorm_debug")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("phase")
                        .then(Commands.argument("phaseId", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggestPhases(builder))
                                .executes(ctx -> executePhaseKit(ctx, StringArgumentType.getString(ctx, "phaseId")))
                        )
                )
                .then(Commands.literal("spawn")
                        .then(Commands.argument("entityType", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggestSpawnables(builder))
                                .executes(ctx -> executeSpawn(ctx, StringArgumentType.getString(ctx, "entityType")))
                        )
                )
                .then(Commands.literal("robot")
                        .then(Commands.argument("entityType", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggestSpawnables(builder))
                                .executes(ctx -> executeSpawn(ctx, StringArgumentType.getString(ctx, "entityType")))
                        )
                )
                .then(Commands.literal("golem")
                        .executes(ctx -> executeGolem(ctx, "iron"))
                        .then(Commands.argument("tier", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggestGolemTiers(builder))
                                .executes(ctx -> executeGolem(ctx, StringArgumentType.getString(ctx, "tier")))
                        )
                )
                .then(Commands.literal("setup")
                        .then(Commands.argument("facility", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggestFacilities(builder))
                                .executes(ctx -> executeSetup(ctx, StringArgumentType.getString(ctx, "facility")))
                        )
                )
                .then(Commands.literal("structure")
                        .then(Commands.argument("facility", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggestFacilities(builder))
                                .executes(ctx -> executeSetup(ctx, StringArgumentType.getString(ctx, "facility")))
                        )
                )
                .then(Commands.literal("suit")
                        .then(Commands.literal("refill")
                                .executes(SandstormDebugCommand::executeSuitRefill)
                        )
                        .then(Commands.literal("drain")
                                .executes(SandstormDebugCommand::executeSuitDrain)
                        )
                )
                .then(Commands.literal("swarm")
                        .then(Commands.literal("status")
                                .executes(SandstormDebugCommand::executeSwarmStatus)
                        )
                        .then(Commands.literal("reset")
                                .executes(SandstormDebugCommand::executeSwarmReset)
                        )
                        .then(Commands.literal("order")
                                .then(Commands.argument("orderId", IntegerArgumentType.integer(0, 3))
                                        .executes(ctx -> executeSwarmOrder(ctx, IntegerArgumentType.getInteger(ctx, "orderId")))
                                )
                        )
                )
                .then(Commands.literal("weather")
                        .then(Commands.literal("start")
                                .executes(ctx -> executeWeather(ctx, true, 0.8))
                                .then(Commands.argument("intensity", DoubleArgumentType.doubleArg(0.05, 1.0))
                                        .executes(ctx -> executeWeather(ctx, true, DoubleArgumentType.getDouble(ctx, "intensity")))
                                )
                        )
                        .then(Commands.literal("stop")
                                .executes(ctx -> executeWeather(ctx, false, 0.0))
                        )
                )
                .then(Commands.literal("seismic")
                        .executes(SandstormDebugCommand::executeSeismic)
                )
                .then(Commands.literal("list")
                        .executes(SandstormDebugCommand::executeList)
                )
        );

        dispatcher.register(Commands.literal("sandstorm")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("debug")
                        .then(Commands.literal("phase")
                                .then(Commands.argument("phaseId", StringArgumentType.word())
                                        .suggests((ctx, builder) -> suggestPhases(builder))
                                        .executes(ctx -> executePhaseKit(ctx, StringArgumentType.getString(ctx, "phaseId")))
                                )
                        )
                        .then(Commands.literal("spawn")
                                .then(Commands.argument("entityType", StringArgumentType.word())
                                        .suggests((ctx, builder) -> suggestSpawnables(builder))
                                        .executes(ctx -> executeSpawn(ctx, StringArgumentType.getString(ctx, "entityType")))
                                )
                        )
                        .then(Commands.literal("robot")
                                .then(Commands.argument("entityType", StringArgumentType.word())
                                        .suggests((ctx, builder) -> suggestSpawnables(builder))
                                        .executes(ctx -> executeSpawn(ctx, StringArgumentType.getString(ctx, "entityType")))
                                )
                        )
                        .then(Commands.literal("golem")
                                .executes(ctx -> executeGolem(ctx, "iron"))
                                .then(Commands.argument("tier", StringArgumentType.word())
                                        .suggests((ctx, builder) -> suggestGolemTiers(builder))
                                        .executes(ctx -> executeGolem(ctx, StringArgumentType.getString(ctx, "tier")))
                                )
                        )
                        .then(Commands.literal("setup")
                                .then(Commands.argument("facility", StringArgumentType.word())
                                        .suggests((ctx, builder) -> suggestFacilities(builder))
                                        .executes(ctx -> executeSetup(ctx, StringArgumentType.getString(ctx, "facility")))
                                )
                        )
                        .then(Commands.literal("structure")
                                .then(Commands.argument("facility", StringArgumentType.word())
                                        .suggests((ctx, builder) -> suggestFacilities(builder))
                                        .executes(ctx -> executeSetup(ctx, StringArgumentType.getString(ctx, "facility")))
                                )
                        )
                        .then(Commands.literal("suit")
                                .then(Commands.literal("refill")
                                        .executes(SandstormDebugCommand::executeSuitRefill)
                                )
                                .then(Commands.literal("drain")
                                        .executes(SandstormDebugCommand::executeSuitDrain)
                                )
                        )
                        .then(Commands.literal("swarm")
                                .then(Commands.literal("status")
                                        .executes(SandstormDebugCommand::executeSwarmStatus)
                                )
                                .then(Commands.literal("reset")
                                        .executes(SandstormDebugCommand::executeSwarmReset)
                                )
                                .then(Commands.literal("order")
                                        .then(Commands.argument("orderId", IntegerArgumentType.integer(0, 4))
                                                .executes(ctx -> executeSwarmOrder(ctx, IntegerArgumentType.getInteger(ctx, "orderId")))
                                        )
                                )
                        )
                        .then(Commands.literal("weather")
                                .then(Commands.literal("start")
                                        .executes(ctx -> executeWeather(ctx, true, 0.8))
                                        .then(Commands.argument("intensity", DoubleArgumentType.doubleArg(0.05, 1.0))
                                                .executes(ctx -> executeWeather(ctx, true, DoubleArgumentType.getDouble(ctx, "intensity")))
                                        )
                                )
                                .then(Commands.literal("stop")
                                        .executes(ctx -> executeWeather(ctx, false, 0.0))
                                )
                        )
                        .then(Commands.literal("seismic")
                                .executes(SandstormDebugCommand::executeSeismic)
                        )
                        .then(Commands.literal("list")
                                .executes(SandstormDebugCommand::executeList)
                        )
                )
        );

        dispatcher.register(Commands.literal("sandstorm_golem")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeGolem(ctx, "all"))
                .then(Commands.argument("tier", StringArgumentType.word())
                        .suggests((ctx, builder) -> suggestGolemTiers(builder))
                        .executes(ctx -> executeGolem(ctx, StringArgumentType.getString(ctx, "tier")))
                )
        );
    }

    private static CompletableFuture<Suggestions> suggestPhases(SuggestionsBuilder builder) {
        for (String p : PHASES) {
            builder.suggest(p);
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestSpawnables(SuggestionsBuilder builder) {
        for (String s : SPAWNABLES) {
            builder.suggest(s);
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestFacilities(SuggestionsBuilder builder) {
        for (String f : FACILITIES) {
            builder.suggest(f);
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestGolemTiers(SuggestionsBuilder builder) {
        List<String> tiers = List.of("all", "iron", "copper", "gold", "netherite", "composite", "boost", "kit");
        for (String tier : tiers) {
            if (tier.startsWith(builder.getRemaining().toLowerCase(Locale.ROOT))) {
                builder.suggest(tier);
            }
        }
        return builder.buildFuture();
    }

    private static void giveItem(ServerPlayer player, ItemLike item, int count) {
        if (item == null || count <= 0) {
            return;
        }
        ItemStack stack = new ItemStack(item, count);
        boolean added = player.getInventory().add(stack);
        if (!added && !stack.isEmpty() && player.level() instanceof ServerLevel sl) {
            ItemEntity entity = player.spawnAtLocation(sl, stack);
            if (entity != null) {
                entity.setNoPickUpDelay();
                entity.setTarget(player.getUUID());
            }
        }
    }

    private static int executePhaseKit(CommandContext<CommandSourceStack> ctx, String phaseId) {
        CommandSourceStack source = ctx.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
            return 0;
        }

        String normalized = phaseId.toLowerCase(Locale.ROOT);
        boolean isAll = "all".equals(normalized);

        if (isAll || "1".equals(normalized)) {
            giveItem(player, SandStormItems.SPACE_SUIT_HELMET, 1);
            giveItem(player, SandStormItems.SPACE_SUIT_CHESTPLATE, 1);
            giveItem(player, SandStormItems.SPACE_SUIT_LEGGINGS, 1);
            giveItem(player, SandStormItems.SPACE_SUIT_BOOTS, 1);
            giveItem(player, SandStormItems.POTABLE_WATER_BOTTLE, 4);
            giveItem(player, SandStormItems.BRACKISH_WATER_BOTTLE, 4);
            giveItem(player, SandStormItems.FILTER_CARTRIDGE, 4);
            giveItem(player, SandStormBlocks.DESALINATION_FILTER, 1);
            giveItem(player, SandStormItems.SILICON_PICKAXE, 1);
            giveItem(player, SandStormBlocks.SANDSTONE_FURNACE, 1);
            giveItem(player, SandStormBlocks.SANDSTONE_WORKBENCH, 1);
            giveItem(player, SandStormBlocks.DEW_CONDENSER, 1);
            PlayerSuitSavedData.get((ServerLevel) player.level()).setSuitData(player.getUUID(), 50000L, 37.0);
            player.setAirSupply(player.getMaxAirSupply());
            player.setHealth(player.getMaxHealth());
        }

        if (isAll || "2".equals(normalized)) {
            giveItem(player, SandStormBlocks.PRINTER_3D, 1);
            giveItem(player, SandStormBlocks.NANITE_FABRICATOR, 1);
            giveItem(player, SandStormBlocks.WIRELESS_SOLAR_RECEIVER, 2);
            giveItem(player, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2, 2);
            giveItem(player, SandStormBlocks.DRONE_DOCK, 1);
            giveItem(player, SandStormBlocks.ASSEMBLY_BAY, 1);
            giveItem(player, SandStormItems.CIRCUIT_BOARD, 8);
            giveItem(player, SandStormItems.NANO_ACTUATOR, 8);
            giveItem(player, SandStormItems.RAW_SILICON, 16);
            giveItem(player, SandStormItems.SILICON_WAFER, 16);
            giveItem(player, SandStormItems.ELECTRIC_COMPONENT, 8);
            giveItem(player, SandStormItems.VIBRO_CRYSKNIFE, 1);
            giveItem(player, SandStormItems.PLASMA_RIFLE, 1);
        }

        if (isAll || "3".equals(normalized)) {
            giveItem(player, SandStormItems.SONIC_CANNON, 1);
            giveItem(player, SandStormItems.ANOMALY_RADAR, 1);
            giveItem(player, SandStormItems.ATMOSPHERIC_ANALYZER, 1);
            giveItem(player, SandStormBlocks.THUMPER, 2);
            giveItem(player, SandStormBlocks.AUTONOMOUS_SONIC_TURRET, 2);
            giveItem(player, SandStormItems.SANDWORM_CHITIN, 8);
            giveItem(player, SandStormItems.SANDWORM_TOOTH, 4);
            giveItem(player, SandStormItems.SANDWORM_SPAWN_EGG, 1);
        }

        if (isAll || "4".equals(normalized)) {
            giveItem(player, SandStormBlocks.ATMOSPHERIC_TERRAFORMER, 1);
            giveItem(player, SandStormItems.NUTRIENT_BOMB, 8);
            giveItem(player, SandStormItems.POTABLE_WATER_BOTTLE, 4);
        }

        if (isAll || "5".equals(normalized)) {
            giveItem(player, SandStormBlocks.PRINTER_3D, 1);
            giveItem(player, SandStormBlocks.NANITE_FABRICATOR, 1);
            giveItem(player, SandStormBlocks.DESALINATION_FILTER, 1);
            giveItem(player, SandStormBlocks.SANDSTONE_WORKBENCH, 1);
            giveItem(player, SandStormBlocks.SANDSTONE_FURNACE, 1);
            giveItem(player, SandStormItems.SILICON_PICKAXE, 1);
            giveItem(player, SandStormItems.REPAIR_TOOL, 1);
            giveItem(player, SandStormItems.GEOLOGICAL_SCANNER, 1);
            giveItem(player, SandStormItems.FIELD_PROBE, 1);
            giveItem(player, SandStormItems.SCRAP_METAL, 16);
            giveItem(player, SandStormItems.TOOL_BASE, 4);
            giveItem(player, SandStormItems.STRUCTURAL_PLATE, 8);
            giveItem(player, SandStormItems.CIRCUIT_MOUNT, 8);
            giveItem(player, SandStormItems.PRESSURE_SEAL, 8);
        }

        if (isAll || "6".equals(normalized)) {
            giveItem(player, SandStormBlocks.ANCIENT_DATA_CORE, 2);
            giveItem(player, SandStormBlocks.BURIED_TECH_RUINS, 4);
            giveItem(player, SandStormBlocks.BRACKISH_AQUIFER, 4);
            giveItem(player, SandStormItems.ANOMALY_RADAR, 1);
            giveItem(player, SandStormItems.TECH_DISC, 4);
        }

        if (isAll || "7".equals(normalized)) {
            giveItem(player, SandStormBlocks.SANDSTONE_WORKBENCH, 1);
            giveItem(player, SandStormBlocks.SANDSTONE_FURNACE, 1);
            giveItem(player, SandStormBlocks.DEW_CONDENSER, 2);
            giveItem(player, SandStormItems.SANDBOARD, 1);
            giveItem(player, SandStormItems.SILICON_PICKAXE, 1);
        }

        if (isAll || "8".equals(normalized)) {
            giveItem(player, SandStormItems.SUIT_UPGRADE_JETPACK, 1);
            giveItem(player, SandStormItems.PROPELLANT_CARTRIDGE, 8);
            giveItem(player, SandStormItems.EMPTY_CARTRIDGE, 8);
            giveItem(player, SandStormBlocks.CHEMICAL_REFINERY, 1);
            giveItem(player, SandStormBlocks.FLUID_PIPE, 16);
            giveItem(player, SandStormItems.SUIT_UPGRADE_BATTERY, 1);
            giveItem(player, SandStormItems.SUIT_UPGRADE_THERMAL, 1);
            giveItem(player, SandStormItems.SUIT_UPGRADE_SEISMIC, 1);
            giveItem(player, SandStormItems.SUIT_UPGRADE_VISOR, 1);
        }

        if (isAll || "9".equals(normalized)) {
            giveItem(player, SandStormBlocks.FULGURITE_GLASS, 16);
            giveItem(player, SandStormBlocks.ELECTRIFIED_SAND, 16);
            giveItem(player, SandStormItems.ANOMALY_RADAR, 1);
            giveItem(player, SandStormItems.ATMOSPHERIC_ANALYZER, 1);
        }

        if (isAll || "10".equals(normalized)) {
            giveItem(player, SandStormBlocks.AUTONOMOUS_SONIC_TURRET, 2);
            giveItem(player, SandStormBlocks.THUMPER, 2);
            giveItem(player, SandStormItems.SONIC_CANNON, 1);
            giveItem(player, SandStormItems.SANDWORM_SPAWN_EGG, 1);
            giveItem(player, SandStormItems.SANDWORM_CHITIN, 8);
            giveItem(player, SandStormItems.SANDWORM_TOOTH, 4);
        }

        if (isAll || "11".equals(normalized)) {
            giveItem(player, SandStormItems.SPACE_SUIT_HELMET, 1);
            giveItem(player, SandStormItems.SPACE_SUIT_CHESTPLATE, 1);
            giveItem(player, SandStormItems.SPACE_SUIT_LEGGINGS, 1);
            giveItem(player, SandStormItems.SPACE_SUIT_BOOTS, 1);
            giveItem(player, SandStormItems.SANDBOARD, 1);
            giveItem(player, SandStormItems.SPACE_RATION, 8);
        }

        if (isAll || "12".equals(normalized)) {
            giveItem(player, SandStormBlocks.HYDROPONIC_CHAMBER, 2);
            giveItem(player, SandStormItems.XENO_GRASS_SEEDS, 8);
            giveItem(player, SandStormBlocks.HEAVY_SAP_CACTUS, 4);
            giveItem(player, SandStormItems.SAMPLING_SYRINGE, 1);
            giveItem(player, SandStormItems.HEAVY_SAP_BOTTLE, 4);
            giveItem(player, SandStormItems.FLEXIBLE_BIOPOLYMER, 8);
            giveItem(player, SandStormBlocks.HALOPHYTE_PLANT, 4);
            giveItem(player, SandStormBlocks.SALINIZED_SAND, 16);
        }

        if (isAll || "13".equals(normalized)) {
            giveItem(player, SandStormBlocks.WPT_RELAY_TOWER, 2);
            giveItem(player, SandStormBlocks.SMART_FLUID_PIPE, 16);
            giveItem(player, SandStormBlocks.GRID_MONITOR_CONSOLE, 1);
            giveItem(player, SandStormBlocks.SOLID_STATE_ACCUMULATOR, 2);
        }

        if (isAll || "14".equals(normalized)) {
            giveItem(player, SandStormBlocks.PIEZO_QUARTZ_BLOCK, 8);
            giveItem(player, SandStormBlocks.BUDDING_PIEZO_QUARTZ, 4);
            giveItem(player, SandStormBlocks.PIEZO_QUARTZ_CLUSTER, 4);
            giveItem(player, SandStormItems.PIEZO_QUARTZ_SHARD, 16);
            giveItem(player, SandStormItems.PIEZO_RESONATOR, 4);
            giveItem(player, SandStormBlocks.DEEP_CORE_DRILL, 1);
            giveItem(player, SandStormItems.PRESSURIZED_FOSSIL_FLUID_BUCKET, 2);
        }

        if (isAll || "15".equals(normalized)) {
            giveItem(player, SandStormItems.HEAVY_PLASMA_CANNON, 1);
            giveItem(player, SandStormBlocks.KINETIC_SHIELD_GENERATOR, 1);
            giveItem(player, SandStormItems.TITANIUM_CHITIN_COMPOSITE, 8);
            giveItem(player, SandStormItems.ORBITAL_SURVEY_SATELLITE, 1);
        }

        if (isAll || "16".equals(normalized)) {
            giveItem(player, SandStormBlocks.FULGURITE_GLASS, 16);
            giveItem(player, SandStormBlocks.ELECTRIFIED_SAND, 16);
            giveItem(player, SandStormBlocks.FOSSILIZED_AMBER, 8);
            giveItem(player, SandStormBlocks.ANCIENT_REED_BLOCK, 8);
            giveItem(player, SandStormBlocks.THERMAL_SPRING_STONE, 8);
            giveItem(player, SandStormItems.ANCIENT_REED, 8);
            giveItem(player, SandStormItems.ANCIENT_SEED, 8);
        }

        if (isAll || "17".equals(normalized)) {
            giveItem(player, SandStormBlocks.SAND_MAGLEV_RAIL, 32);
            giveItem(player, SandStormBlocks.HABITAT_DOME, 2);
            giveItem(player, SandStormBlocks.AUTO_ASSEMBLY_LINE, 2);
        }

        if (isAll || "18".equals(normalized)) {
            giveItem(player, SandStormBlocks.TITANIUM_SPIKE_WALL, 8);
            giveItem(player, SandStormBlocks.RETRACTABLE_SPIKE_WALL, 8);
            giveItem(player, SandStormBlocks.ELECTRIFIED_SPIKE_BARRIER, 8);
            giveItem(player, SandStormBlocks.CORROSIVE_CHITIN_SPIKE_WALL, 8);
            giveItem(player, SandStormBlocks.KINETIC_FLOOR_SPIKES, 16);
            giveItem(player, SandStormBlocks.CRUSHING_SPIKE_GATE, 2);
        }

        if (isAll || "19".equals(normalized)) {
            giveItem(player, SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR, 1);
            giveItem(player, Blocks.SMOOTH_SANDSTONE, 64);
            giveItem(player, SandStormBlocks.FULGURITE_GLASS, 32);
            giveItem(player, SandStormItems.TITANIUM_CHITIN_COMPOSITE, 16);
            giveItem(player, SandStormBlocks.SMART_FLUID_PIPE, 16);
        }

        if (isAll || "20".equals(normalized)) {
            giveItem(player, SandStormItems.SURVIVAL_DATAPAD, 1);
            giveItem(player, SandStormItems.ANOMALY_RADAR, 1);
            giveItem(player, SandStormItems.ATMOSPHERIC_ANALYZER, 1);
            giveItem(player, SandStormItems.ORBITAL_SURVEY_SATELLITE, 1);
            giveItem(player, SandStormItems.SANDBOARD, 1);
            giveItem(player, SandStormItems.SPACE_SUIT_HELMET, 1);
            giveItem(player, SandStormItems.SPACE_SUIT_CHESTPLATE, 1);
            giveItem(player, SandStormItems.SPACE_SUIT_LEGGINGS, 1);
            giveItem(player, SandStormItems.SPACE_SUIT_BOOTS, 1);
        }

        if (isAll || "21".equals(normalized)) {
            giveItem(player, SandStormBlocks.BIOREACTOR_VAT, 2);
            giveItem(player, SandStormBlocks.RADIOTROPHIC_MYCELIUM, 4);
            giveItem(player, SandStormBlocks.CHITINOLYTIC_FUNGUS, 4);
            giveItem(player, SandStormBlocks.CRYO_XEROPHILIC_LICHEN, 4);
            giveItem(player, SandStormBlocks.HALOPHYTE_SUCCULENT, 4);
            giveItem(player, SandStormBlocks.DUNE_EPHEDRA, 4);
            giveItem(player, SandStormItems.RADIOPROTECTIVE_MELANIN, 8);
            giveItem(player, SandStormItems.CHITOSAN_EXTRACT, 8);
            giveItem(player, SandStormItems.TREHALOSE_SUGAR, 8);
            giveItem(player, SandStormItems.OSMOLYTE_GLYCEROL, 8);
            giveItem(player, SandStormItems.NEUROACTIVE_ALKALOIDS, 8);
        }

        if (isAll || "22".equals(normalized)) {
            giveItem(player, SandStormItems.HYPO_INJECTOR, 1);
            giveItem(player, SandStormItems.ADRENAL_STIM, 4);
            giveItem(player, SandStormItems.BIOFOAM_CARTRIDGE, 4);
            giveItem(player, SandStormItems.MYOMER_STIM, 4);
            giveItem(player, SandStormItems.ENDOTHERMIC_SERUM, 4);
            giveItem(player, SandStormItems.GRAV_DAMPENER_STIM, 4);
            giveItem(player, SandStormItems.DETOX_AMPOULE, 4);
            giveItem(player, SandStormItems.STEALTH_NANO_DRAPE, 4);
        }

        if (isAll || "23".equals(normalized)) {
            giveItem(player, SandStormBlocks.MOLECULAR_MODIFIER, 1);
            giveItem(player, SandStormItems.VIBRO_RESONATOR_MODULE, 1);
            giveItem(player, SandStormItems.THERMAL_PLASMA_EMITTER, 1);
            giveItem(player, SandStormItems.KINETIC_FOCUS_MODULE, 1);
            giveItem(player, SandStormItems.CAVITATION_FREQUENCY_CORE, 1);
            giveItem(player, SandStormItems.ATOMIC_PHASE_DISRUPTER, 1);
            giveItem(player, SandStormItems.SPECTROMETRIC_SIFTER, 1);
            giveItem(player, SandStormItems.SELF_HEALING_NANITE_MATRIX, 1);
            giveItem(player, SandStormItems.TITANIUM_LATTICE_COATING, 1);
            giveItem(player, SandStormItems.BALLISTIC_DAMPENER_MESH, 1);
            giveItem(player, SandStormItems.ABLATIVE_THERMAL_PLATING, 1);
            giveItem(player, SandStormItems.PNEUMATIC_FALL_DAMPERS, 1);
            giveItem(player, SandStormItems.REACTIVE_SHOCK_PLATING, 1);
            giveItem(player, SandStormItems.SILICON_PICKAXE, 1);
            giveItem(player, SandStormItems.SPACE_SUIT_CHESTPLATE, 1);
        }

        if (isAll || "24".equals(normalized)) {
            giveItem(player, SandStormBlocks.BIO_REGENERATION_POD, 1);
            giveItem(player, SandStormItems.POTABLE_WATER_BOTTLE, 8);
            giveItem(player, SandStormItems.BIOFOAM_CARTRIDGE, 4);
        }

        if (isAll || "25".equals(normalized)) {
            giveItem(player, SandStormBlocks.CYBORG_INCUBATOR_VAT, 1);
            giveItem(player, SandStormItems.BIOMECHANICAL_CHASSIS_FRAME, 2);
            giveItem(player, SandStormItems.SYNTHETIC_MYOMER_BUNDLE, 4);
            giveItem(player, SandStormItems.BIO_NEURAL_CORE, 2);
            giveItem(player, SandStormItems.BIO_COOLANT_CANISTER, 4);
            giveItem(player, SandStormItems.ASSEMBLED_CYBORG_FRAME, 1);
        }

        if (isAll || "26".equals(normalized)) {
            giveItem(player, SandStormItems.CYBERNETIC_COMMAND_UPLINK, 1);
            giveItem(player, SandStormItems.CYBORG_EXCAVATOR_SPAWN_EGG, 2);
            giveItem(player, SandStormItems.CYBORG_BUILDER_SPAWN_EGG, 2);
            giveItem(player, SandStormItems.CYBORG_HARVESTER_SPAWN_EGG, 2);
        }

        if (isAll || "27".equals(normalized)) {
            giveItem(player, SandStormBlocks.CYBORG_DOCKING_STATION, 2);
            giveItem(player, SandStormItems.ACID_CHITIN_PLATING, 2);
            giveItem(player, SandStormItems.CRYO_TREHALOSE_CELL, 2);
            giveItem(player, SandStormItems.LONG_RANGE_LIDAR_LENS, 2);
            giveItem(player, SandStormItems.PIEZO_HOVER_THRUSTER, 2);
        }

        if (isAll || "28".equals(normalized)) {
            giveItem(player, SandStormBlocks.HOLO_TACTICAL_SPIRE, 1);
            giveItem(player, SandStormItems.NEURAL_SYNAPSE_LINK, 1);
            giveItem(player, SandStormItems.ORBITAL_RECON_PROBE, 4);
        }

        if (isAll || "29".equals(normalized)) {
            giveItem(player, SandStormBlocks.QUANTUM_SLEEPER_POD, 2);
            giveItem(player, SandStormItems.QUANTUM_MIND_MATRIX, 2);
            giveItem(player, SandStormItems.CHITOSAN_EXTRACT, 8);
            giveItem(player, SandStormItems.TREHALOSE_SUGAR, 8);
            giveItem(player, SandStormItems.OSMOLYTE_GLYCEROL, 8);
            giveItem(player, SandStormItems.POTABLE_WATER_BOTTLE, 8);
        }

        if (isAll || "30".equals(normalized)) {
            giveItem(player, SandStormBlocks.PLASMA_SHIELD_GENERATOR, 1);
            giveItem(player, SandStormBlocks.KINETIC_RAILGUN, 2);
            giveItem(player, SandStormBlocks.ACOUSTIC_DEFENSE_PYLON, 4);
            giveItem(player, SandStormItems.KINETIC_SLUG, 64);
            giveItem(player, SandStormItems.SUPERCONDUCTOR_TOROID, 4);
            giveItem(player, SandStormItems.PLASMA_FOCUS_CRYSTAL, 4);
            giveItem(player, SandStormBlocks.SOLID_STATE_ACCUMULATOR, 2);
            giveItem(player, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2, 2);
        }

        if (isAll || "31".equals(normalized)) {
            giveItem(player, SandStormBlocks.DEEP_CORE_BOREHOLE, 1);
            giveItem(player, SandStormBlocks.LITHO_PLASMA_EXTRACTOR, 1);
            giveItem(player, SandStormBlocks.SUPERCRITICAL_HEAT_EXCHANGER, 1);
            giveItem(player, SandStormItems.GEOTHERMAL_CORE_DRILL_BIT, 2);
            giveItem(player, SandStormItems.RAW_LITHIUM_SALTS, 16);
            giveItem(player, SandStormItems.SUPERHEATED_LITHIUM_CAPSULE, 8);
            giveItem(player, SandStormItems.MANTLE_ALLOY_INGOT, 8);
            giveItem(player, SandStormItems.THERMAL_RADIATOR_FIN, 4);
            giveItem(player, SandStormItems.BIO_COOLANT_CANISTER, 8);
            giveItem(player, SandStormBlocks.SMART_FLUID_PIPE, 16);
            giveItem(player, SandStormBlocks.WPT_RELAY_TOWER, 2);
            giveItem(player, SandStormBlocks.SOLID_STATE_ACCUMULATOR, 2);
            giveItem(player, Items.WATER_BUCKET, 4);
        }

        if (isAll || "32".equals(normalized)) {
            giveItem(player, SandStormItems.MEGAZORD_FLIGHT_MODULE, 1);
            giveItem(player, SandStormItems.MEGAZORD_SUBMERSIBLE_HULL, 1);
            giveItem(player, SandStormItems.MEGAZORD_TACTICAL_OVERDRIVE, 1);
            giveItem(player, SandStormItems.VECTORED_THRUSTER, 4);
            giveItem(player, SandStormItems.HYDRO_BALLAST_PUMP, 4);
            giveItem(player, SandStormBlocks.ASSEMBLY_BAY, 1);
            giveItem(player, SandStormBlocks.ANCIENT_DATA_CORE, 2);
            giveItem(player, SandStormItems.MANTLE_ALLOY_INGOT, 8);
            giveItem(player, SandStormItems.SUPERCONDUCTOR_TOROID, 4);
            giveItem(player, SandStormBlocks.SOLID_STATE_ACCUMULATOR, 2);
        }

        if (isAll || "33".equals(normalized) || "orbital".equals(normalized)) {
            giveItem(player, SandStormBlocks.ORBITAL_MASS_DRIVER, 1);
            giveItem(player, SandStormBlocks.ORBITAL_GROUND_STATION, 1);
            giveItem(player, SandStormBlocks.SPECTRAL_SURVEY_TELESCOPE, 1);
            giveItem(player, SandStormItems.WEATHER_RECON_SATELLITE, 2);
            giveItem(player, SandStormItems.ORBITAL_SOLAR_REFLECTOR_SATELLITE, 2);
            giveItem(player, SandStormItems.SAR_GEOLOGICAL_SATELLITE, 2);
            giveItem(player, SandStormItems.ORBITAL_KINETIC_LANCE_SATELLITE, 2);
            giveItem(player, SandStormBlocks.SOLID_STATE_ACCUMULATOR, 4);
            giveItem(player, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2, 2);
        }

        player.containerMenu.broadcastChanges();
        player.inventoryMenu.broadcastChanges();

        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "§6[SandStorm]§r Kit de testes da §bFase %s§r entregue ao inventário com sucesso!", phaseId)), true);
        return 1;
    }

    private static int executeSpawn(CommandContext<CommandSourceStack> ctx, String entityType) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        Vec3 pos = source.getPosition();
        ServerPlayer player = source.getEntity() instanceof ServerPlayer sp ? sp : null;
        String normalized = entityType.toLowerCase(Locale.ROOT);

        switch (normalized) {
            case "cyborg_excavator" -> {
                CyborgExcavatorEntity cyborg = SandStormEntities.CYBORG_EXCAVATOR.create(level, EntitySpawnReason.COMMAND);
                if (cyborg != null) {
                    cyborg.setPos(pos.x, pos.y, pos.z);
                    cyborg.setOwnerUUID(player != null ? player.getUUID() : null);
                    cyborg.setEnergy(50000);
                    cyborg.setCoolant(4000);
                    cyborg.setIntegrity(100);
                    cyborg.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
                    level.addFreshEntity(cyborg);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Ciborgue Escavador instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "cyborg_builder" -> {
                CyborgBuilderEntity cyborg = SandStormEntities.CYBORG_BUILDER.create(level, EntitySpawnReason.COMMAND);
                if (cyborg != null) {
                    cyborg.setPos(pos.x, pos.y, pos.z);
                    cyborg.setOwnerUUID(player != null ? player.getUUID() : null);
                    cyborg.setEnergy(50000);
                    cyborg.setCoolant(4000);
                    cyborg.setIntegrity(100);
                    cyborg.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
                    cyborg.getInventory().addItem(new ItemStack(Blocks.SANDSTONE, 64));
                    cyborg.getInventory().addItem(new ItemStack(Blocks.SMOOTH_SANDSTONE, 64));
                    level.addFreshEntity(cyborg);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Ciborgue Construtor instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "cyborg_harvester" -> {
                CyborgHarvesterEntity cyborg = SandStormEntities.CYBORG_HARVESTER.create(level, EntitySpawnReason.COMMAND);
                if (cyborg != null) {
                    cyborg.setPos(pos.x, pos.y, pos.z);
                    cyborg.setOwnerUUID(player != null ? player.getUUID() : null);
                    cyborg.setEnergy(50000);
                    cyborg.setCoolant(4000);
                    cyborg.setIntegrity(100);
                    cyborg.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
                    level.addFreshEntity(cyborg);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Ciborgue Colhedor instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "excavator_vehicle" -> {
                ExcavatorVehicleEntity vehicle = SandStormEntities.EXCAVATOR_VEHICLE.create(level, EntitySpawnReason.COMMAND);
                if (vehicle != null) {
                    vehicle.setPos(pos.x, pos.y, pos.z);
                    vehicle.getEnergyStorage().receiveEnergy(50000L);
                    level.addFreshEntity(vehicle);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Veículo Escavador tripulável instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "megazord" -> {
                MegazordEntity mecha = SandStormEntities.MEGAZORD.create(level, EntitySpawnReason.COMMAND);
                if (mecha != null) {
                    mecha.setPos(pos.x, pos.y, pos.z);
                    mecha.getEnergyStorage().receiveEnergy(100000L);
                    level.addFreshEntity(mecha);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Mecha Titânico Megazord instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "megazord_flight" -> {
                MegazordEntity mecha = SandStormEntities.MEGAZORD.create(level, EntitySpawnReason.COMMAND);
                if (mecha != null) {
                    mecha.setPos(pos.x, pos.y, pos.z);
                    mecha.setFlightModule(true);
                    mecha.getEnergyStorage().receiveEnergy(100000L);
                    level.addFreshEntity(mecha);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Megazord Aero Striker (Voador) instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "megazord_sub" -> {
                MegazordEntity mecha = SandStormEntities.MEGAZORD.create(level, EntitySpawnReason.COMMAND);
                if (mecha != null) {
                    mecha.setPos(pos.x, pos.y, pos.z);
                    mecha.setSubmersibleModule(true);
                    mecha.getEnergyStorage().receiveEnergy(100000L);
                    level.addFreshEntity(mecha);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Megazord Subaquático Abissal instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "megazord_apex" -> {
                MegazordEntity mecha = SandStormEntities.MEGAZORD.create(level, EntitySpawnReason.COMMAND);
                if (mecha != null) {
                    mecha.setPos(pos.x, pos.y, pos.z);
                    mecha.setFlightModule(true);
                    mecha.setSubmersibleModule(true);
                    mecha.setOverdriveModule(true);
                    mecha.getEnergyStorage().receiveEnergy(MegazordEntity.OVERDRIVE_BATTERY_CAPACITY);
                    level.addFreshEntity(mecha);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Megazord Apex Dominator instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "cargo_drone" -> {
                CargoDroneEntity drone = SandStormEntities.CARGO_DRONE.create(level, EntitySpawnReason.COMMAND);
                if (drone != null) {
                    drone.setPos(pos.x, pos.y, pos.z);
                    drone.getEnergyStorage().receiveEnergy(10000L);
                    level.addFreshEntity(drone);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Drone de Carga autônomo instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "builder_drone" -> {
                BuilderDroneEntity drone = SandStormEntities.BUILDER_DRONE.create(level, EntitySpawnReason.COMMAND);
                if (drone != null) {
                    drone.setPos(pos.x, pos.y + 1.0, pos.z);
                    level.addFreshEntity(drone);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Drone Construtor Operário instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "sandworm" -> {
                SandwormEntity worm = SandStormEntities.SANDWORM.create(level, EntitySpawnReason.COMMAND);
                if (worm != null) {
                    worm.setPos(pos.x, pos.y, pos.z);
                    worm.setShowcaseMode(true);
                    worm.setWormSize(2, true);
                    level.addFreshEntity(worm);
                    source.sendSuccess(() -> Component.literal("§6[SandStorm] Verme de Areia Colossal instanciado em modo Showcase!"), true);
                    return 1;
                }
            }
            case "sandboard" -> {
                SandboardEntity board = SandStormEntities.SANDBOARD.create(level, EntitySpawnReason.COMMAND);
                if (board != null) {
                    board.setPos(pos.x, pos.y, pos.z);
                    level.addFreshEntity(board);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Prancha de Areia (Sandboard) instanciada com sucesso!"), true);
                    return 1;
                }
            }
            case "scrap_sentinel" -> {
                ScrapSentinelEntity sentinel = SandStormEntities.SCRAP_SENTINEL.create(level, EntitySpawnReason.COMMAND);
                if (sentinel != null) {
                    sentinel.setPos(pos.x, pos.y, pos.z);
                    sentinel.setDormant(false);
                    level.addFreshEntity(sentinel);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Sentinela de Sucata (Ativo) instanciada com sucesso!"), true);
                    return 1;
                }
            }
            case "scrap_sentinel_dormant" -> {
                ScrapSentinelEntity sentinel = SandStormEntities.SCRAP_SENTINEL.create(level, EntitySpawnReason.COMMAND);
                if (sentinel != null) {
                    sentinel.setPos(pos.x, pos.y, pos.z);
                    sentinel.setDormant(true);
                    level.addFreshEntity(sentinel);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Sentinela de Sucata (Dormente) instanciada com sucesso!"), true);
                    return 1;
                }
            }
            case "derelict_automaton" -> {
                DerelictAutomatonEntity automaton = SandStormEntities.DERELICT_AUTOMATON.create(level, EntitySpawnReason.COMMAND);
                if (automaton != null) {
                    automaton.setPos(pos.x, pos.y, pos.z);
                    level.addFreshEntity(automaton);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Autômato Abandonado instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "cyber_hound" -> {
                CyberHoundEntity hound = SandStormEntities.CYBER_HOUND.create(level, EntitySpawnReason.COMMAND);
                if (hound != null) {
                    hound.setPos(pos.x, pos.y, pos.z);
                    level.addFreshEntity(hound);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Cão Cibernético instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "laborer_unit" -> {
                LaborerUnitEntity unit = SandStormEntities.LABORER_UNIT.create(level, EntitySpawnReason.COMMAND);
                if (unit != null) {
                    unit.setPos(pos.x, pos.y, pos.z);
                    level.addFreshEntity(unit);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Unidade Operária instanciada com sucesso!"), true);
                    return 1;
                }
            }
            case "scout_drone" -> {
                ScoutDroneEntity scout = SandStormEntities.SCOUT_DRONE.create(level, EntitySpawnReason.COMMAND);
                if (scout != null) {
                    scout.setPos(pos.x, pos.y, pos.z);
                    level.addFreshEntity(scout);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Drone Explorador instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "crawler_drone" -> {
                CrawlerDroneEntity crawler = SandStormEntities.CRAWLER_DRONE.create(level, EntitySpawnReason.COMMAND);
                if (crawler != null) {
                    crawler.setPos(pos.x, pos.y, pos.z);
                    level.addFreshEntity(crawler);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Drone Rastreador instanciado com sucesso!"), true);
                    return 1;
                }
            }
            case "aquifer_beetle" -> {
                AquiferBeetleEntity beetle = SandStormEntities.AQUIFER_BEETLE.create(level, EntitySpawnReason.COMMAND);
                if (beetle != null) {
                    beetle.setPos(pos.x, pos.y, pos.z);
                    beetle.setHibernating(false);
                    level.addFreshEntity(beetle);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Besouro Aquífero (Ativo) instanciado!"), true);
                    return 1;
                }
            }
            case "aquifer_beetle_hibernating" -> {
                AquiferBeetleEntity beetle = SandStormEntities.AQUIFER_BEETLE.create(level, EntitySpawnReason.COMMAND);
                if (beetle != null) {
                    beetle.setPos(pos.x, pos.y, pos.z);
                    beetle.setHibernating(true);
                    level.addFreshEntity(beetle);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Besouro Aquífero (Hibernando) instanciado!"), true);
                    return 1;
                }
            }
            case "cybernetic_golem" -> {
                CyberneticGolemEntity golem = SandStormEntities.CYBERNETIC_GOLEM.create(level, EntitySpawnReason.COMMAND);
                if (golem != null) {
                    golem.setPos(pos.x, pos.y, pos.z);
                    golem.setMetalTier(GolemMetalTier.IRON);
                    level.addFreshEntity(golem);
                    source.sendSuccess(() -> Component.literal("§a[SandStorm] Golem Cibernético instanciado com sucesso!"), true);
                    return 1;
                }
            }
            default -> {
                source.sendFailure(Component.literal("§c[SandStorm] Entidade desconhecida. Opções: " + String.join(", ", SPAWNABLES)));
                return 0;
            }
        }

        source.sendFailure(Component.literal("§c[SandStorm] Falha ao criar a entidade solicitada."));
        return 0;
    }

    private static int executeGolem(CommandContext<CommandSourceStack> ctx, String tierParam) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        ServerPlayer player = source.getEntity() instanceof ServerPlayer sp ? sp : null;
        Vec3 pos = source.getPosition();
        String normalized = tierParam.toLowerCase(Locale.ROOT);

        if ("kit".equals(normalized)) {
            if (player != null) {
                giveItem(player, SandStormBlocks.CYBERNETIC_GOLEM_HEAD, 5);
                giveItem(player, Blocks.IRON_BLOCK, 4);
                giveItem(player, Blocks.RAW_COPPER_BLOCK, 4);
                giveItem(player, Blocks.GOLD_BLOCK, 4);
                giveItem(player, Blocks.NETHERITE_BLOCK, 4);
                giveItem(player, Blocks.HEAVY_CORE, 4);
                player.containerMenu.broadcastChanges();
                player.inventoryMenu.broadcastChanges();
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§r Kit de construção de autômatos entregue ao inventário!"), true);
                return 1;
            } else {
                source.sendFailure(Component.literal("§c[SandStorm] O comando kit deve ser executado por um jogador."));
                return 0;
            }
        }

        if ("boost".equals(normalized) || "overdrive".equals(normalized)) {
            if (player == null) {
                source.sendFailure(Component.literal("§c[SandStorm] O comando boost deve ser executado por um jogador."));
                return 0;
            }
            List<CyberneticGolemEntity> nearby = level.getEntitiesOfClass(CyberneticGolemEntity.class, player.getBoundingBox().inflate(24.0));
            if (nearby.isEmpty()) {
                source.sendFailure(Component.literal("§c[SandStorm] Nenhum Golem Cibernético encontrado nas proximidades."));
                return 0;
            }
            for (CyberneticGolemEntity golem : nearby) {
                golem.setOverdrive(true);
                golem.setHeat(1.0f);
            }
            source.sendSuccess(() -> Component.literal("§6[SandStorm]§r Sobrecarga de propulsão (Overdrive Boost) acionada nos golens próximos!"), true);
            return 1;
        }

        if ("all".equals(normalized) || "lineup".equals(normalized)) {
            Vec3 forward;
            Vec3 right;
            Vec3 origin;
            float yaw;

            if (player != null) {
                Vec3 look = player.getLookAngle();
                forward = new Vec3(look.x, 0.0, look.z).normalize();
                if (forward.lengthSqr() < 0.01) {
                    forward = new Vec3(0, 0, 1);
                }
                right = new Vec3(-forward.z, 0.0, forward.x).normalize();
                origin = player.position().add(forward.scale(5.0));
                yaw = (float) (Math.atan2(-forward.x, forward.z) * (180.0 / Math.PI));
            } else {
                forward = new Vec3(0, 0, 1);
                right = new Vec3(1, 0, 0);
                origin = pos.add(0, 0, 4);
                yaw = 180.0f;
            }

            GolemMetalTier[] tiers = GolemMetalTier.values();
            for (int i = 0; i < tiers.length; i++) {
                double offset = (i - 2) * 2.8;
                Vec3 spawnPos = origin.add(right.scale(offset));
                CyberneticGolemEntity golem = SandStormEntities.CYBERNETIC_GOLEM.create(level, EntitySpawnReason.COMMAND);
                if (golem != null) {
                    golem.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                    golem.setYRot(yaw);
                    golem.setYHeadRot(yaw);
                    golem.setMetalTier(tiers[i]);
                    level.addFreshEntity(golem);

                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            spawnPos.x, spawnPos.y + 1.2, spawnPos.z, 25, 0.4, 0.6, 0.4, 0.12);
                    level.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z,
                            SoundEvents.BEACON_POWER_SELECT, SoundSource.NEUTRAL, 1.0f, 1.0f + (i * 0.15f));
                }
            }

            source.sendSuccess(() -> Component.literal("§6[SandStorm]§r Formação completa instanciada lado a lado: Ferro, Cobre, Ouro, Netherita e Compósito!"), true);
            return 1;
        }

        GolemMetalTier tier = GolemMetalTier.byId(normalized);
        Vec3 forward;
        Vec3 spawnPos;
        float yaw;

        if (player != null) {
            Vec3 look = player.getLookAngle();
            forward = new Vec3(look.x, 0.0, look.z).normalize();
            if (forward.lengthSqr() < 0.01) {
                forward = new Vec3(0, 0, 1);
            }
            spawnPos = player.position().add(forward.scale(4.0));
            yaw = (float) (Math.atan2(-forward.x, forward.z) * (180.0 / Math.PI));
        } else {
            spawnPos = pos;
            yaw = 0.0f;
        }

        CyberneticGolemEntity golem = SandStormEntities.CYBERNETIC_GOLEM.create(level, EntitySpawnReason.COMMAND);
        if (golem != null) {
            golem.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            golem.setYRot(yaw);
            golem.setYHeadRot(yaw);
            golem.setMetalTier(tier);
            level.addFreshEntity(golem);

            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    spawnPos.x, spawnPos.y + 1.2, spawnPos.z, 25, 0.4, 0.6, 0.4, 0.12);
            level.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z,
                    SoundEvents.BEACON_POWER_SELECT, SoundSource.NEUTRAL, 1.0f, 1.2f);

            source.sendSuccess(() -> Component.literal("§6[SandStorm]§r Golem Cibernético (" + tier.getId().toUpperCase(Locale.ROOT) + ") instanciado com sucesso!"), true);
            return 1;
        }

        source.sendFailure(Component.literal("§c[SandStorm] Falha ao instanciar o Golem Cibernético."));
        return 0;
    }

    private static int executeSetup(CommandContext<CommandSourceStack> ctx, String facility) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        BlockPos center = BlockPos.containing(source.getPosition());
        ServerPlayer player = source.getEntity() instanceof ServerPlayer sp ? sp : null;
        String normalized = facility.toLowerCase(Locale.ROOT);

        switch (normalized) {
            case "cyborg_outpost" -> {
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 4; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                BlockPos spirePos = center.offset(0, 1, 0);
                level.setBlock(spirePos, SandStormBlocks.HOLO_TACTICAL_SPIRE.defaultBlockState().setValue(HoloTacticalSpireBlock.ACTIVE, true), 3);
                if (level.getBlockEntity(spirePos) instanceof HoloTacticalSpireBlockEntity spireBe) {
                    spireBe.setTacticalOrder(1);
                }
                level.setBlock(center.offset(2, 1, 0), SandStormBlocks.CYBORG_DOCKING_STATION.defaultBlockState(), 3);
                level.setBlock(center.offset(-2, 1, 0), SandStormBlocks.CYBORG_DOCKING_STATION.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, 2), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, -2), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);

                spawnCyborg(level, center.offset(1, 1, 1), 0, player);
                spawnCyborg(level, center.offset(-1, 1, 1), 1, player);
                spawnCyborg(level, center.offset(1, 1, -1), 2, player);

                source.sendSuccess(() -> Component.literal("§a[SandStorm] Posto Avançado Cibernético montado com sucesso! (Torre Holo-Tática, Docas, Receptores e Enxame)"), true);
                return 1;
            }
            case "medbay_clinic" -> {
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 3; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.BIO_REGENERATION_POD.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 1), SandStormBlocks.CYBORG_INCUBATOR_VAT.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, -1), SandStormBlocks.WPT_RELAY_TOWER.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, -1), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Clínica Médica Bio-Regenerativa montada com sucesso!"), true);
                return 1;
            }
            case "bioreactor_lab" -> {
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), SandStormBlocks.SALINIZED_SAND.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 3; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.WPT_RELAY_TOWER.defaultBlockState(), 3);
                level.setBlock(center.offset(2, 1, 0), SandStormBlocks.BIOREACTOR_VAT.defaultBlockState(), 3);
                level.setBlock(center.offset(-2, 1, 0), SandStormBlocks.BIOREACTOR_VAT.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 1), SandStormBlocks.RADIOTROPHIC_MYCELIUM.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, 1), SandStormBlocks.CHITINOLYTIC_FUNGUS.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, -1), SandStormBlocks.HALOPHYTE_SUCCULENT.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, -1), SandStormBlocks.DUNE_EPHEDRA.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Laboratório de Biorreatores e Cultivo Extremófilo montado com sucesso!"), true);
                return 1;
            }
            case "molecular_workshop" -> {
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 3; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.MOLECULAR_MODIFIER.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 1), SandStormBlocks.PRINTER_3D.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, 1), SandStormBlocks.NANITE_FABRICATOR.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, -1), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, -1), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Oficina de Engenharia Molecular e Manufatura 3D montada com sucesso!"), true);
                return 1;
            }
            case "spike_fortress" -> {
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 3; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                for (int dx = -3; dx <= 3; dx++) {
                    level.setBlock(center.offset(dx, 1, -3), SandStormBlocks.TITANIUM_SPIKE_WALL.defaultBlockState(), 3);
                    level.setBlock(center.offset(dx, 1, 3), SandStormBlocks.ELECTRIFIED_SPIKE_BARRIER.defaultBlockState(), 3);
                }
                for (int dz = -2; dz <= 2; dz++) {
                    level.setBlock(center.offset(-3, 1, dz), SandStormBlocks.CORROSIVE_CHITIN_SPIKE_WALL.defaultBlockState(), 3);
                    level.setBlock(center.offset(3, 1, dz), SandStormBlocks.RETRACTABLE_SPIKE_WALL.defaultBlockState(), 3);
                }
                level.setBlock(center.offset(0, 1, 3), SandStormBlocks.CRUSHING_SPIKE_GATE.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.AUTONOMOUS_SONIC_TURRET.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 0), SandStormBlocks.KINETIC_FLOOR_SPIKES.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, 0), SandStormBlocks.KINETIC_FLOOR_SPIKES.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Fortaleza Perimétrica de Espinhos e Torreta Sônica montada com sucesso!"), true);
                return 1;
            }
            case "power_station" -> {
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 4; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.WPT_RELAY_TOWER.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 0), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, 0), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, 1), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, -1), SandStormBlocks.GRID_MONITOR_CONSOLE.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Estação Elétrica WPT de Alta Tensão montada com sucesso!"), true);
                return 1;
            }
            case "megastructure_site" -> {
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 4; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR.defaultBlockState(), 3);
                level.setBlock(center.offset(2, 1, 0), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
                level.setBlock(center.offset(-2, 1, 0), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
                BuilderDroneEntity d1 = SandStormEntities.BUILDER_DRONE.create(level, EntitySpawnReason.COMMAND);
                if (d1 != null) {
                    d1.setPos(center.getX() + 2.5, center.getY() + 3.0, center.getZ() + 2.5);
                    level.addFreshEntity(d1);
                }
                BuilderDroneEntity d2 = SandStormEntities.BUILDER_DRONE.create(level, EntitySpawnReason.COMMAND);
                if (d2 != null) {
                    d2.setPos(center.getX() - 2.5, center.getY() + 3.0, center.getZ() - 2.5);
                    level.addFreshEntity(d2);
                }
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Canteiro de Manufatura de Megaestruturas montado com sucesso!"), true);
                return 1;
            }
            case "hydroponics_dome" -> {
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), SandStormBlocks.XENO_GRASS_BLOCK.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 3; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.HYDROPONIC_CHAMBER.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 1), SandStormBlocks.HEAVY_SAP_CACTUS.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, -1), SandStormBlocks.HALOPHYTE_PLANT.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, -1), SandStormBlocks.WIRELESS_SOLAR_RECEIVER.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Estufa Hidropônica Xeno-Adaptada montada com sucesso!"), true);
                return 1;
            }
            case "deep_drill_station" -> {
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 4; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.DEEP_CORE_DRILL.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 0), SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, 0), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, 1), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Estação de Perfuratriz de Poço Profundo montada com sucesso!"), true);
                return 1;
            }
            case "defense_perimeter" -> {
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.POLISHED_ANDESITE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 3; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.KINETIC_SHIELD_GENERATOR.defaultBlockState(), 3);
                level.setBlock(center.offset(2, 1, 0), SandStormBlocks.AUTONOMOUS_SONIC_TURRET.defaultBlockState(), 3);
                level.setBlock(center.offset(-2, 1, 0), SandStormBlocks.AUTONOMOUS_SONIC_TURRET.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, 2), SandStormBlocks.THUMPER.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, -2), SandStormBlocks.ELECTRIFIED_SPIKE_BARRIER.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Perímetro de Defesa com Escudo Cinético e Torretas montado com sucesso!"), true);
                return 1;
            }
            case "refinery_complex" -> {
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 4; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.CHEMICAL_REFINERY.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 0), SandStormBlocks.DEW_CONDENSER.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, 0), SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, 1), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, -1), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Complexo de Refinaria Química e Dutos de Fluidos montado com sucesso!"), true);
                return 1;
            }
            case "terraformer_dome" -> {
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), SandStormBlocks.XENO_GRASS_BLOCK.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 4; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.ATMOSPHERIC_TERRAFORMER.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 0), SandStormBlocks.HALOPHYTE_PLANT.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, 0), SandStormBlocks.HEAVY_SAP_CACTUS.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, 1), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, -1), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Cúpula Ecológica de Terraformação Atmosférica montada com sucesso!"), true);
                return 1;
            }
            case "maglev_station" -> {
                for (int dx = -4; dx <= 4; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
                        level.setBlock(center.offset(dx, 1, dz), Blocks.AIR.defaultBlockState(), 3);
                        level.setBlock(center.offset(dx, 2, dz), Blocks.AIR.defaultBlockState(), 3);
                    }
                }
                for (int dx = -4; dx <= 4; dx++) {
                    level.setBlock(center.offset(dx, 1, 0), SandStormBlocks.SAND_MAGLEV_RAIL.defaultBlockState(), 3);
                }
                level.setBlock(center.offset(0, 1, 1), SandStormBlocks.AUTO_ASSEMBLY_LINE.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, -1), SandStormBlocks.HABITAT_DOME.defaultBlockState(), 3);
                level.setBlock(center.offset(2, 1, 1), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Estação Maglev com Domo e Linha de Montagem montada com sucesso!"), true);
                return 1;
            }
            case "starter_base" -> {
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.SANDSTONE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 3; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.SANDSTONE_WORKBENCH.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 0), SandStormBlocks.SANDSTONE_FURNACE.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, 0), SandStormBlocks.DESALINATION_FILTER.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, 1), SandStormBlocks.DEW_CONDENSER.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Base Inicial de Sobrevivência em Arenito montada com sucesso!"), true);
                return 1;
            }
            case "ancient_ruin_site" -> {
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.RED_SANDSTONE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 3; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                level.setBlock(center.offset(0, 1, 0), SandStormBlocks.ANCIENT_DATA_CORE.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 1), SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, -1), SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, -1), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, 1), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Sítio Arqueológico com Ruínas Tecnológicas e Núcleo montado com sucesso!"), true);
                return 1;
            }
            case "clone_facility" -> {
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.SMOOTH_BASALT.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 4; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                BlockPos podPos1 = center.offset(-1, 1, 0);
                BlockPos podPos2 = center.offset(1, 1, 0);
                level.setBlock(podPos1, SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState(), 3);
                level.setBlock(podPos2, SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, 2), SandStormBlocks.WPT_RELAY_TOWER.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, -2), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
                level.setBlock(center.offset(-2, 1, 2), SandStormBlocks.BIOREACTOR_VAT.defaultBlockState(), 3);
                level.setBlock(center.offset(2, 1, 2), SandStormBlocks.HOLO_TACTICAL_SPIRE.defaultBlockState(), 3);

                BlockEntity be1 = level.getBlockEntity(podPos1);
                if (be1 instanceof QuantumSleeperPodBlockEntity podBe1) {
                    podBe1.setCustomPodName("Alpha-Station Pod");
                    podBe1.setOwnerUuid(player.getUUID());
                    podBe1.setStoredEnergy(100000);
                    podBe1.setBioNutrients(100);
                    podBe1.setHasClone(true);
                }
                BlockEntity be2 = level.getBlockEntity(podPos2);
                if (be2 instanceof QuantumSleeperPodBlockEntity podBe2) {
                    podBe2.setCustomPodName("Beta-Station Pod");
                    podBe2.setOwnerUuid(player.getUUID());
                    podBe2.setStoredEnergy(100000);
                    podBe2.setBioNutrients(100);
                    podBe2.setHasClone(false);
                }
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Complexo Biocibernético de Clonagem Quântica montado com sucesso! (Casulos Alpha e Beta vinculados)"), true);
                return 1;
            }
            case "plasma_defense_complex" -> {
                for (int dx = -4; dx <= 4; dx++) {
                    for (int dz = -4; dz <= 4; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 4; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                BlockPos shieldPos = center.offset(0, 1, 0);
                level.setBlock(shieldPos, SandStormBlocks.PLASMA_SHIELD_GENERATOR.defaultBlockState().setValue(PlasmaShieldGeneratorBlock.ACTIVE, true), 3);
                if (level.getBlockEntity(shieldPos) instanceof PlasmaShieldGeneratorBlockEntity shieldBe) {
                    shieldBe.setStoredEnergy(500000);
                    shieldBe.setShieldActive(true);
                    shieldBe.setFieldRadius(48);
                }

                BlockPos railgunPos1 = center.offset(3, 1, 0);
                BlockPos railgunPos2 = center.offset(-3, 1, 0);
                level.setBlock(railgunPos1, SandStormBlocks.KINETIC_RAILGUN.defaultBlockState().setValue(KineticRailgunBlock.LIT, true), 3);
                level.setBlock(railgunPos2, SandStormBlocks.KINETIC_RAILGUN.defaultBlockState().setValue(KineticRailgunBlock.LIT, true), 3);
                if (level.getBlockEntity(railgunPos1) instanceof KineticRailgunBlockEntity rgBe1) {
                    rgBe1.setStoredEnergy(100000);
                    rgBe1.setItem(0, new ItemStack(SandStormItems.KINETIC_SLUG, 32));
                }
                if (level.getBlockEntity(railgunPos2) instanceof KineticRailgunBlockEntity rgBe2) {
                    rgBe2.setStoredEnergy(100000);
                    rgBe2.setItem(0, new ItemStack(SandStormItems.KINETIC_SLUG, 32));
                }

                BlockPos pylon1 = center.offset(3, 1, 3);
                BlockPos pylon2 = center.offset(-3, 1, 3);
                BlockPos pylon3 = center.offset(3, 1, -3);
                BlockPos pylon4 = center.offset(-3, 1, -3);
                level.setBlock(pylon1, SandStormBlocks.ACOUSTIC_DEFENSE_PYLON.defaultBlockState().setValue(AcousticDefensePylonBlock.ACTIVE, true), 3);
                level.setBlock(pylon2, SandStormBlocks.ACOUSTIC_DEFENSE_PYLON.defaultBlockState().setValue(AcousticDefensePylonBlock.ACTIVE, true), 3);
                level.setBlock(pylon3, SandStormBlocks.ACOUSTIC_DEFENSE_PYLON.defaultBlockState().setValue(AcousticDefensePylonBlock.ACTIVE, true), 3);
                level.setBlock(pylon4, SandStormBlocks.ACOUSTIC_DEFENSE_PYLON.defaultBlockState().setValue(AcousticDefensePylonBlock.ACTIVE, true), 3);
                if (level.getBlockEntity(pylon1) instanceof AcousticDefensePylonBlockEntity pylBe1) {
                    pylBe1.setStoredEnergy(50000);
                }
                if (level.getBlockEntity(pylon2) instanceof AcousticDefensePylonBlockEntity pylBe2) {
                    pylBe2.setStoredEnergy(50000);
                }
                if (level.getBlockEntity(pylon3) instanceof AcousticDefensePylonBlockEntity pylBe3) {
                    pylBe3.setStoredEnergy(50000);
                }
                if (level.getBlockEntity(pylon4) instanceof AcousticDefensePylonBlockEntity pylBe4) {
                    pylBe4.setStoredEnergy(50000);
                }

                level.setBlock(center.offset(0, 1, 3), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, -3), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);

                source.sendSuccess(() -> Component.literal("§a[SandStorm] Complexo Planetário de Defesa de Plasma montado com sucesso! (Escudo 48m, 2 Railguns com Slugs, 4 Pilones Acústicos e Energia WPT)"), true);
                return 1;
            }
            case "geothermal_well" -> {
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 4; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                BlockPos boreholePos = center.offset(0, 1, 0);
                level.setBlock(boreholePos, SandStormBlocks.DEEP_CORE_BOREHOLE.defaultBlockState().setValue(DeepCoreBoreholeBlock.LIT, true), 3);
                BlockEntity boreholeBe = level.getBlockEntity(boreholePos);
                if (boreholeBe instanceof DeepCoreBoreholeBlockEntity be) {
                    be.setItem(DeepCoreBoreholeBlockEntity.SLOT_DRILL_BIT, new ItemStack(SandStormItems.GEOTHERMAL_CORE_DRILL_BIT));
                    be.setItem(DeepCoreBoreholeBlockEntity.SLOT_COOLANT_IN, new ItemStack(Items.WATER_BUCKET));
                }

                BlockPos extractorPos = center.offset(2, 1, 0);
                level.setBlock(extractorPos, SandStormBlocks.LITHO_PLASMA_EXTRACTOR.defaultBlockState().setValue(LithoPlasmaExtractorBlock.LIT, true), 3);
                BlockEntity extractorBe = level.getBlockEntity(extractorPos);
                if (extractorBe instanceof LithoPlasmaExtractorBlockEntity be) {
                    be.setItem(LithoPlasmaExtractorBlockEntity.SLOT_SALT_IN, new ItemStack(SandStormItems.RAW_LITHIUM_SALTS, 16));
                    be.setItem(LithoPlasmaExtractorBlockEntity.SLOT_CANISTER_IN, new ItemStack(SandStormItems.BIO_COOLANT_CANISTER, 4));
                }

                BlockPos exchangerPos = center.offset(-2, 1, 0);
                level.setBlock(exchangerPos, SandStormBlocks.SUPERCRITICAL_HEAT_EXCHANGER.defaultBlockState().setValue(SupercriticalHeatExchangerBlock.LIT, true), 3);
                BlockEntity exchangerBe = level.getBlockEntity(exchangerPos);
                if (exchangerBe instanceof SupercriticalHeatExchangerBlockEntity be) {
                    be.setItem(SupercriticalHeatExchangerBlockEntity.SLOT_WATER_IN, new ItemStack(Items.WATER_BUCKET));
                    be.setItem(SupercriticalHeatExchangerBlockEntity.SLOT_THERMAL_CORE, new ItemStack(SandStormItems.THERMAL_RADIATOR_FIN, 2));
                }

                level.setBlock(center.offset(0, 1, 2), SandStormBlocks.WPT_RELAY_TOWER.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, -2), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
                level.setBlock(center.offset(1, 1, 0), SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState(), 3);
                level.setBlock(center.offset(-1, 1, 0), SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState(), 3);

                source.sendSuccess(() -> Component.literal("§a[SandStorm] Complexo Geotérmico de Poço do Manto & Sifão Lito-Plasmático montado com sucesso! (Perfuratriz, Extrator Lito-Plasma e Trocador Térmico)"), true);
                return 1;
            }
            case "orbital_array" -> {
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        level.setBlock(center.offset(dx, 0, dz), Blocks.POLISHED_ANDESITE.defaultBlockState(), 3);
                        for (int dy = 1; dy <= 4; dy++) {
                            level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
                BlockPos driverPos = center.offset(-2, 1, 0);
                level.setBlock(driverPos, SandStormBlocks.ORBITAL_MASS_DRIVER.defaultBlockState().setValue(OrbitalMassDriverBlock.LIT, true), 3);
                BlockEntity driverBe = level.getBlockEntity(driverPos);
                if (driverBe instanceof OrbitalMassDriverBlockEntity be) {
                    be.setEnergy(200000);
                    be.setItem(0, new ItemStack(SandStormItems.WEATHER_RECON_SATELLITE, 1));
                }

                BlockPos stationPos = center.offset(0, 1, 0);
                level.setBlock(stationPos, SandStormBlocks.ORBITAL_GROUND_STATION.defaultBlockState().setValue(OrbitalGroundStationBlock.ACTIVE, true), 3);
                BlockEntity stationBe = level.getBlockEntity(stationPos);
                if (stationBe instanceof OrbitalGroundStationBlockEntity be) {
                    be.setEnergy(150000);
                }

                BlockPos telescopePos = center.offset(2, 1, 0);
                level.setBlock(telescopePos, SandStormBlocks.SPECTRAL_SURVEY_TELESCOPE.defaultBlockState().setValue(SpectralSurveyTelescopeBlock.ACTIVE, true), 3);

                level.setBlock(center.offset(0, 1, 2), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
                level.setBlock(center.offset(0, 1, -2), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);

                source.sendSuccess(() -> Component.literal("§a[SandStorm] Complexo de Matriz Orbital montado com sucesso! (Acelerador de Cargas, Estação Terrena e Telescópio Espectral)"), true);
                return 1;
            }
            case "colossal_castle", "castle", "castelo" -> {
                int blocks = ColossalCastleGenerator.generate(level, center);
                source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "§a[SandStorm] Castelo Colossal de Pedra gerado em [%d, %d, %d]! (%d blocos)", center.getX(), center.getY(), center.getZ(), blocks)), true);
                return blocks;
            }
            case "computer", "quantum_computer" -> {
                ShowcaseAutomation.buildQuantumComputerStation(level, center);
                source.sendSuccess(() -> Component.literal("§a[SandStorm] Computador Quântico completo e energizado montado com sucesso!"), true);
                return 1;
            }
            default -> {
                source.sendFailure(Component.literal("§c[SandStorm] Instalação desconhecida. Opções: " + String.join(", ", FACILITIES)));
                return 0;
            }
        }
    }

    private static void spawnCyborg(ServerLevel level, BlockPos pos, int type, ServerPlayer player) {
        CyborgEntity cyborg = switch (type) {
            case 0 -> SandStormEntities.CYBORG_EXCAVATOR.create(level, EntitySpawnReason.COMMAND);
            case 1 -> SandStormEntities.CYBORG_BUILDER.create(level, EntitySpawnReason.COMMAND);
            default -> SandStormEntities.CYBORG_HARVESTER.create(level, EntitySpawnReason.COMMAND);
        };
        if (cyborg != null) {
            cyborg.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            cyborg.setOwnerUUID(player != null ? player.getUUID() : null);
            cyborg.setEnergy(50000);
            cyborg.setCoolant(4000);
            cyborg.setIntegrity(100);
            cyborg.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
            level.addFreshEntity(cyborg);
        }
    }

    private static int executeSuitRefill(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
            return 0;
        }

        PlayerSuitSavedData.get((ServerLevel) player.level()).setSuitData(player.getUUID(), 50000L, 37.0);
        player.setAirSupply(player.getMaxAirSupply());
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0f);

        source.sendSuccess(() -> Component.literal("§b[SandStorm] Traje Espacial 100% recarregado! (50.000 J, 37.0°C, Oxigênio e Saturação Plenos)"), true);
        return 1;
    }

    private static int executeSuitDrain(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
            return 0;
        }

        PlayerSuitSavedData.get((ServerLevel) player.level()).setSuitData(player.getUUID(), 0L, 48.0);
        player.setAirSupply(player.getMaxAirSupply() / 10);

        source.sendSuccess(() -> Component.literal("§c[SandStorm] Alerta Crítico! Traje drenado (0 J, 48.0°C de calor extremo e oxigênio em 10%)."), true);
        return 1;
    }

    private static int executeSwarmStatus(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        CyborgSwarmManager manager = CyborgSwarmManager.getInstance();
        int order = manager.getGlobalTacticalOrder();
        String orderName = switch (order) {
            case 0 -> "CONVERGE_AT_TARGET";
            case 1 -> "OPTIMAL_COORDINATED_WORK";
            case 2 -> "PATROL_PERIMETER";
            case 3 -> "SEISMIC_ALERT_EVACUATE";
            case 4 -> "STANDBY_HOLD_POSITION";
            default -> "UNKNOWN";
        };

        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "§6[SandStorm Swarm]§r Ordem Ativa: §b%d (%s)§r | Mutex Ativo: §e%s", order, orderName, manager != null ? "Operacional" : "Inativo")), false);
        return 1;
    }

    private static int executeSwarmReset(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        CyborgSwarmManager.getInstance().releaseAll();
        source.sendSuccess(() -> Component.literal("§a[SandStorm Swarm] Todas as travas de Voxel Mutex e reservas espaciais foram liberadas!"), true);
        return 1;
    }

    private static int executeSwarmOrder(CommandContext<CommandSourceStack> ctx, int orderId) {
        CommandSourceStack source = ctx.getSource();
        CyborgSwarmManager.getInstance().setGlobalTacticalOrder(orderId);
        String orderName = switch (orderId) {
            case 0 -> "CONVERGE_AT_TARGET";
            case 1 -> "OPTIMAL_COORDINATED_WORK";
            case 2 -> "PATROL_PERIMETER";
            case 3 -> "SEISMIC_ALERT_EVACUATE";
            case 4 -> "STANDBY_HOLD_POSITION";
            default -> "CUSTOM";
        };
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "§6[SandStorm Swarm]§r Nova diretriz tática global transmitida: §b%d (%s)§r!", orderId, orderName)), true);
        return 1;
    }

    private static int executeWeather(CommandContext<CommandSourceStack> ctx, boolean start, double intensity) {
        CommandSourceStack source = ctx.getSource();
        if (start) {
            SandstormWeatherHandler.triggerSandstorm(6000, intensity);
            source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "§6[SandStorm]§r Tempestade de Areia iniciada com intensidade %.2f!", intensity)), true);
        } else {
            SandstormWeatherHandler.stopSandstorm();
            source.sendSuccess(() -> Component.literal("§a[SandStorm]§r Tempestade de Areia cessada!"), true);
        }
        return 1;
    }

    private static int executeSeismic(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            SeismicSurvivalHandler.spawnWormEncounter(player);
            source.sendSuccess(() -> Component.literal("§c[SandStorm] Perturbação sísmica severa provocada! Verme de Areia emergindo!"), true);
            return 1;
        }
        int chunkX = (int) (source.getPosition().x) >> 4;
        int chunkZ = (int) (source.getPosition().z) >> 4;
        SeismicSurvivalHandler.recordVibration(chunkX, chunkZ, 120.0);
        source.sendSuccess(() -> Component.literal("§e[SandStorm] Vibração sísmica de 120.0 gravada no setor!"), true);
        return 1;
    }

    private static int executeList(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        source.sendSuccess(() -> Component.literal("§6=== SandStorm Debug Suite ==="), false);
        source.sendSuccess(() -> Component.literal("§b/sandstorm debug phase <1..32|all>§r: Kits de teste de todas as 32 fases"), false);
        source.sendSuccess(() -> Component.literal("§b/sandstorm debug spawn|robot <entidade>§r: " + String.join(", ", SPAWNABLES)), false);
        source.sendSuccess(() -> Component.literal("§b/sandstorm debug setup|structure <instalação>§r: " + String.join(", ", FACILITIES)), false);
        source.sendSuccess(() -> Component.literal("§b/sandstorm debug suit refill|drain§r: Controle de energia/temperatura do traje"), false);
        source.sendSuccess(() -> Component.literal("§b/sandstorm debug swarm status|reset|order <0..3>§r: Gestão tática do enxame de robôs"), false);
        source.sendSuccess(() -> Component.literal("§b/sandstorm debug weather start [0.05..1.0]|stop§r: Simulação de tempestades de areia"), false);
        source.sendSuccess(() -> Component.literal("§b/sandstorm debug seismic§r: Simulação de abalo sísmico e convocação de Shai-Hulud"), false);
        source.sendSuccess(() -> Component.literal("§b/sandstorm_golem [tier|all|kit]§r: Invoca golens cibernéticos ou formação lado a lado"), false);
        return 1;
    }

    public static List<String> getSupportedPhases() {
        return PHASES;
    }

    public static List<String> getSupportedSpawnables() {
        return SPAWNABLES;
    }

    public static List<String> getSupportedFacilities() {
        return FACILITIES;
    }
}
