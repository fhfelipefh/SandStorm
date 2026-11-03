package com.fhfelipefh.sandstorm.content.technical;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import java.util.IdentityHashMap;
import java.util.Map;

public class TechnicalSpecificationRegistry {

    private static final Map<Item, TechnicalSpecEntry> SPECS = new IdentityHashMap<>();
    private static boolean initialized = false;

    public static synchronized void ensureInitialized() {
        if (!initialized) {
            registerAll();
            initialized = true;
        }
    }

    public static TechnicalSpecEntry get(Item item) {
        ensureInitialized();
        return SPECS.get(item);
    }

    public static boolean has(Item item) {
        ensureInitialized();
        return SPECS.containsKey(item);
    }

    public static int size() {
        ensureInitialized();
        return SPECS.size();
    }

    private static void register(ItemLike itemLike, TechnicalSpecEntry entry) {
        if (itemLike != null && itemLike.asItem() != null) {
            SPECS.put(itemLike.asItem(), entry);
        }
    }

    private static void registerAll() {
        register(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 15))
                .add(Component.translatable("tooltip.sandstorm.tech.energy_capacity", "500.000"))
                .add(Component.translatable("tooltip.sandstorm.tech.buffer_chests"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_constructor"))
                .build());

        register(SandStormBlocks.QUANTUM_DISK_DRIVE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 15))
                .add(Component.translatable("tooltip.sandstorm.tech.disk_drive_slots"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_quantum"))
                .build());

        register(SandStormBlocks.QUANTUM_ACCESS_TERMINAL, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 5))
                .add(Component.translatable("tooltip.sandstorm.tech.network_quantum"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_terminal"))
                .build());

        register(SandStormBlocks.QUANTUM_NETWORK_CONTROLLER, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 10))
                .add(Component.translatable("tooltip.sandstorm.tech.network_quantum"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_controller"))
                .build());

        register(SandStormBlocks.QUANTUM_NETWORK_CABLE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.network_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_cable"))
                .build());

        register(SandStormBlocks.PRINTER_3D, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 10))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_printer"))
                .build());

        register(SandStormBlocks.NANITE_FABRICATOR, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 20))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_nanite"))
                .build());

        register(SandStormBlocks.CHEMICAL_REFINERY, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 15))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_refinery"))
                .build());

        register(SandStormBlocks.ATMOSPHERIC_TERRAFORMER, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 40))
                .add(Component.translatable("tooltip.sandstorm.tech.energy_capacity", "500.000"))
                .add(Component.translatable("tooltip.sandstorm.tech.range_radius", 48))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_terraformer"))
                .build());

        register(SandStormBlocks.LITHO_PLASMA_EXTRACTOR, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 25))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_litho"))
                .build());

        register(SandStormBlocks.DEEP_CORE_DRILL, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 30))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_drill"))
                .build());

        register(SandStormBlocks.DEEP_CORE_BOREHOLE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 20))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_borehole"))
                .build());

        register(SandStormBlocks.AUTONOMOUS_SONIC_TURRET, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 15))
                .add(Component.translatable("tooltip.sandstorm.tech.range_blocks", 24))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_turret"))
                .build());

        register(SandStormBlocks.KINETIC_SHIELD_GENERATOR, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 20))
                .add(Component.translatable("tooltip.sandstorm.tech.range_radius", 16))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_kinetic_shield"))
                .build());

        register(SandStormBlocks.PLASMA_SHIELD_GENERATOR, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 35))
                .add(Component.translatable("tooltip.sandstorm.tech.range_radius", 24))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_plasma_shield"))
                .build());

        register(SandStormBlocks.WIRELESS_SOLAR_RECEIVER, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_generation", 60))
                .add(Component.translatable("tooltip.sandstorm.tech.network_orbital"))
                .build());

        register(SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_generation", 120))
                .add(Component.translatable("tooltip.sandstorm.tech.network_orbital"))
                .build());

        register(SandStormBlocks.THERMAL_GENERATOR, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_generation", 40))
                .add(Component.translatable("tooltip.sandstorm.tech.function_thermal_gen"))
                .build());

        register(SandStormBlocks.BASIC_THERMAL_GENERATOR, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_generation", 16))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_basic"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_basic_thermal_gen"))
                .build());

        register(SandStormBlocks.SOLID_STATE_ACCUMULATOR, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_capacity", "1.000.000"))
                .add(Component.translatable("tooltip.sandstorm.tech.energy_transfer", 500))
                .add(Component.translatable("tooltip.sandstorm.tech.network_cable"))
                .build());

        register(SandStormBlocks.BIOREACTOR_VAT, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 12))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_bioreactor"))
                .build());

        register(SandStormBlocks.CYBORG_INCUBATOR_VAT, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 25))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_incubator"))
                .build());

        register(SandStormBlocks.CYBORG_DOCKING_STATION, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 10))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_dock"))
                .build());

        register(SandStormBlocks.DESALINATION_FILTER, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 5))
                .add(Component.translatable("tooltip.sandstorm.tech.function_desalination"))
                .build());

        register(SandStormBlocks.DEW_CONDENSER, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_passive"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_dew"))
                .build());

        register(SandStormBlocks.HYDROPONIC_CHAMBER, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.energy_usage", 8))
                .add(Component.translatable("tooltip.sandstorm.tech.network_wpt_cable"))
                .add(Component.translatable("tooltip.sandstorm.tech.function_hydroponic"))
                .build());

        register(SandStormItems.CYBERNETIC_COMMAND_UPLINK, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.range_blocks", 64))
                .add(Component.translatable("tooltip.sandstorm.tech.function_uplink"))
                .build());

        register(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_1K, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.cartridge_capacity_1k"))
                .add(Component.translatable("tooltip.sandstorm.tech.energy_passive"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_quantum"))
                .build());

        register(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_4K, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.cartridge_capacity_4k"))
                .add(Component.translatable("tooltip.sandstorm.tech.energy_passive"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_quantum"))
                .build());

        register(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_16K, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.cartridge_capacity_16k"))
                .add(Component.translatable("tooltip.sandstorm.tech.energy_passive"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_quantum"))
                .build());

        register(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.cartridge_capacity_64k"))
                .add(Component.translatable("tooltip.sandstorm.tech.energy_passive"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_quantum"))
                .build());

        register(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_DIMENSIONAL, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.cartridge_capacity_dimensional"))
                .add(Component.translatable("tooltip.sandstorm.tech.energy_passive"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_quantum"))
                .build());

        register(SandStormItems.PLASMA_RIFLE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.range_blocks", 40))
                .add(Component.translatable("tooltip.sandstorm.tech.function_plasma_rifle"))
                .build());

        register(SandStormItems.HEAVY_PLASMA_CANNON, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.range_blocks", 48))
                .add(Component.translatable("tooltip.sandstorm.tech.function_heavy_plasma"))
                .build());

        register(SandStormItems.SONIC_CANNON, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.range_blocks", 16))
                .add(Component.translatable("tooltip.sandstorm.tech.function_sonic_cannon"))
                .build());

        register(SandStormItems.VIBRO_CRYSKNIFE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.function_vibro_knife"))
                .build());

        register(SandStormItems.SANDBOARD, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.function_sandboard"))
                .build());

        register(SandStormItems.SPACE_SUIT_HELMET, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.function_space_suit"))
                .build());
        register(SandStormItems.SPACE_SUIT_CHESTPLATE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.function_space_suit"))
                .build());
        register(SandStormItems.SPACE_SUIT_LEGGINGS, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.function_space_suit"))
                .build());
        register(SandStormItems.SPACE_SUIT_BOOTS, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.function_space_suit"))
                .build());

        register(SandStormItems.ORBITAL_SURVEY_SATELLITE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.function_satellite"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_orbital"))
                .build());
        register(SandStormItems.ORBITAL_SOLAR_REFLECTOR_SATELLITE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.function_satellite"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_orbital"))
                .build());
        register(SandStormItems.SAR_GEOLOGICAL_SATELLITE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.function_satellite"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_orbital"))
                .build());
        register(SandStormItems.ORBITAL_KINETIC_LANCE_SATELLITE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.function_satellite"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_orbital"))
                .build());
        register(SandStormItems.WEATHER_RECON_SATELLITE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.function_satellite"))
                .add(Component.translatable("tooltip.sandstorm.tech.network_orbital"))
                .build());
        register(SandStormItems.SUBSPACE_WARP_CORE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.subspace_warp_core_lore"))
                .add(Component.translatable("tooltip.sandstorm.tech.subspace_warp_core_function"))
                .build());

        register(SandStormItems.SUIT_UPGRADE_BATTERY, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_suit_chestplate"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_suit_battery"))
                .build());
        register(SandStormItems.SUIT_UPGRADE_THERMAL, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_suit_leggings"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_suit_thermal"))
                .build());
        register(SandStormItems.SUIT_UPGRADE_SEISMIC, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_suit_boots"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_suit_seismic"))
                .build());
        register(SandStormItems.SUIT_UPGRADE_VISOR, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_suit_helmet"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_suit_visor"))
                .build());
        register(SandStormItems.SUIT_UPGRADE_JETPACK, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_suit_chestplate"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_suit_jetpack"))
                .build());

        register(SandStormItems.COPPER_COIL, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_component"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_copper_coil"))
                .build());

        register(SandStormItems.STEED_UPGRADE_BATTERY, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_steed"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_steed_battery"))
                .build());
        register(SandStormItems.STEED_UPGRADE_SPEED, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_steed"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_steed_speed"))
                .build());
        register(SandStormItems.STEED_UPGRADE_CARGO, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_steed"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_steed_cargo"))
                .build());
        register(SandStormItems.STEED_UPGRADE_SONIC_TURRET, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_steed"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_steed_turret"))
                .build());

        register(SandStormItems.MEGAZORD_FLIGHT_MODULE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_megazord"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_megazord_flight"))
                .build());
        register(SandStormItems.MEGAZORD_SUBMERSIBLE_HULL, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_megazord"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_megazord_sub"))
                .build());
        register(SandStormItems.MEGAZORD_TACTICAL_OVERDRIVE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_megazord"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_megazord_overdrive"))
                .build());

        register(SandStormItems.PIEZO_HOVER_THRUSTER, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_cyborg"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_piezo_thruster"))
                .build());
        register(SandStormItems.CYBERNETIC_COMMAND_UPLINK, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_cyborg"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_command_uplink"))
                .build());
        register(SandStormItems.NEURAL_SYNAPSE_LINK, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_cyborg"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_neural_synapse"))
                .build());

        register(SandStormItems.VIBRO_RESONATOR_MODULE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_tool"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_vibro_resonator"))
                .build());
        register(SandStormItems.THERMAL_PLASMA_EMITTER, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_tool"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_thermal_plasma"))
                .build());
        register(SandStormItems.KINETIC_FOCUS_MODULE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_tool"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_kinetic_focus"))
                .build());
        register(SandStormItems.CAVITATION_FREQUENCY_CORE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_tool"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_cavitation_core"))
                .build());
        register(SandStormItems.ATOMIC_PHASE_DISRUPTER, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_tool"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_atomic_disrupter"))
                .build());
        register(SandStormItems.SPECTROMETRIC_SIFTER, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_tool"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_spectrometric_sifter"))
                .build());

        register(SandStormItems.SELF_HEALING_NANITE_MATRIX, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_armor"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_self_healing_matrix"))
                .build());
        register(SandStormItems.TITANIUM_LATTICE_COATING, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_armor"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_titanium_lattice"))
                .build());
        register(SandStormItems.BALLISTIC_DAMPENER_MESH, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_armor"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_ballistic_dampener"))
                .build());
        register(SandStormItems.ABLATIVE_THERMAL_PLATING, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_armor"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_ablative_plating"))
                .build());
        register(SandStormItems.PNEUMATIC_FALL_DAMPERS, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_armor"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_fall_dampers"))
                .build());
        register(SandStormItems.REACTIVE_SHOCK_PLATING, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_molecular_armor"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_reactive_shock"))
                .build());

        register(SandStormItems.BIOSPHERE_CARTRIDGE_PLAINS, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_terraformer"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_biosphere_plains"))
                .build());
        register(SandStormItems.BIOSPHERE_CARTRIDGE_FOREST, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_terraformer"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_biosphere_forest"))
                .build());
        register(SandStormItems.BIOSPHERE_CARTRIDGE_JUNGLE, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_terraformer"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_biosphere_jungle"))
                .build());
        register(SandStormItems.BIOSPHERE_CARTRIDGE_CRYO, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_terraformer"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_biosphere_cryo"))
                .build());
        register(SandStormItems.BIOSPHERE_CARTRIDGE_FUNGAL, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_terraformer"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_biosphere_fungal"))
                .build());
        register(SandStormItems.BIOSPHERE_CARTRIDGE_MAGNETIC, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_terraformer"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_biosphere_magnetic"))
                .build());
        register(SandStormItems.BIOSPHERE_CARTRIDGE_OASIS, TechnicalSpecEntry.builder()
                .add(Component.translatable("tooltip.sandstorm.tech.slot_terraformer"))
                .add(Component.translatable("tooltip.sandstorm.tech.desc_biosphere_oasis"))
                .build());
    }
}
