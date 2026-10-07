package com.fhfelipefh.sandstorm.content.quest;

import com.fhfelipefh.sandstorm.core.SandStormMod;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuestRegistry {
    private static final Map<String, QuestData> QUESTS = new LinkedHashMap<>();

    static {
        register(new QuestData("suit_diagnostics", 1, "quest.sandstorm.suit_diagnostics.title", "quest.sandstorm.suit_diagnostics.task", "quest.sandstorm.suit_diagnostics.note", SandStormMod.id("space_suit_helmet"), SandStormMod.id("space_suit_helmet"), List.of(), SandStormMod.id("space_ration"), 2, "sandstorm.battery_60"));
        register(new QuestData("compact_sandstone", 1, "quest.sandstorm.compact_sandstone.title", "quest.sandstorm.compact_sandstone.task", "quest.sandstorm.compact_sandstone.note", SandStormMod.mcId("sandstone"), SandStormMod.mcId("sandstone"), List.of("suit_diagnostics"), SandStormMod.mcId("sandstone"), 4));
        register(new QuestData("emergency_workbench", 1, "quest.sandstorm.emergency_workbench.title", "quest.sandstorm.emergency_workbench.task", "quest.sandstorm.emergency_workbench.note", SandStormMod.id("sandstone_workbench"), SandStormMod.id("sandstone_workbench"), List.of("compact_sandstone"), SandStormMod.id("space_ration"), 2));
        register(new QuestData("sandstone_furnace", 1, "quest.sandstorm.sandstone_furnace.title", "quest.sandstorm.sandstone_furnace.task", "quest.sandstorm.sandstone_furnace.note", SandStormMod.id("sandstone_furnace"), SandStormMod.id("sandstone_furnace"), List.of("emergency_workbench"), SandStormMod.id("raw_silicon"), 4));
        register(new QuestData("tool_base", 1, "quest.sandstorm.tool_base.title", "quest.sandstorm.tool_base.task", "quest.sandstorm.tool_base.note", SandStormMod.id("tool_base"), SandStormMod.id("tool_base"), List.of("sandstone_furnace"), SandStormMod.id("raw_silicon"), 2));
        register(new QuestData("electric_component", 1, "quest.sandstorm.electric_component.title", "quest.sandstorm.electric_component.task", "quest.sandstorm.electric_component.note", SandStormMod.id("electric_component"), SandStormMod.id("electric_component"), List.of("tool_base"), SandStormMod.id("raw_silicon"), 2));
        register(new QuestData("silicon_pickaxe", 1, "quest.sandstorm.silicon_pickaxe.title", "quest.sandstorm.silicon_pickaxe.task", "quest.sandstorm.silicon_pickaxe.note", SandStormMod.id("silicon_pickaxe"), SandStormMod.id("silicon_pickaxe"), List.of("electric_component"), SandStormMod.id("space_ration"), 2));
        register(new QuestData("collect_coal", 1, "quest.sandstorm.collect_coal.title", "quest.sandstorm.collect_coal.task", "quest.sandstorm.collect_coal.note", SandStormMod.mcId("coal"), SandStormMod.mcId("coal"), List.of("silicon_pickaxe"), SandStormMod.mcId("torch"), 4));

        register(new QuestData("glass_canister", 2, "quest.sandstorm.glass_canister.title", "quest.sandstorm.glass_canister.task", "quest.sandstorm.glass_canister.note", SandStormMod.mcId("glass_bottle"), SandStormMod.mcId("glass_bottle"), List.of("collect_coal"), SandStormMod.id("brackish_water_bottle"), 2));
        register(new QuestData("brackish_brine", 2, "quest.sandstorm.brackish_brine.title", "quest.sandstorm.brackish_brine.task", "quest.sandstorm.brackish_brine.note", SandStormMod.id("brackish_water_bottle"), SandStormMod.id("brackish_water_bottle"), List.of("glass_canister"), SandStormMod.mcId("sand"), 4));
        register(new QuestData("silicon_smelting", 2, "quest.sandstorm.silicon_smelting.title", "quest.sandstorm.silicon_smelting.task", "quest.sandstorm.silicon_smelting.note", SandStormMod.id("raw_silicon"), SandStormMod.id("raw_silicon"), List.of("collect_coal"), SandStormMod.id("scrap_metal"), 2));
        register(new QuestData("silicon_wafer", 2, "quest.sandstorm.silicon_wafer.title", "quest.sandstorm.silicon_wafer.task", "quest.sandstorm.silicon_wafer.note", SandStormMod.id("silicon_wafer"), SandStormMod.id("silicon_wafer"), List.of("silicon_smelting"), SandStormMod.id("circuit_board"), 2));
        register(new QuestData("desalination_system", 2, "quest.sandstorm.desalination_system.title", "quest.sandstorm.desalination_system.task", "quest.sandstorm.desalination_system.note", SandStormMod.id("desalination_filter"), SandStormMod.id("desalination_filter"), List.of("silicon_wafer", "brackish_brine"), SandStormMod.id("filter_cartridge"), 1));
        register(new QuestData("thermal_purification", 2, "quest.sandstorm.thermal_purification.title", "quest.sandstorm.thermal_purification.task", "quest.sandstorm.thermal_purification.note", SandStormMod.id("potable_water_bottle"), SandStormMod.id("potable_water_bottle"), List.of("desalination_system"), SandStormMod.id("potable_water_bottle"), 2));

        register(new QuestData("circuit_manufacturing", 3, "quest.sandstorm.circuit_manufacturing.title", "quest.sandstorm.circuit_manufacturing.task", "quest.sandstorm.circuit_manufacturing.note", SandStormMod.id("circuit_board"), SandStormMod.id("circuit_board"), List.of("silicon_wafer", "thermal_purification"), SandStormMod.id("silicon_wafer"), 4));
        register(new QuestData("additive_printer", 3, "quest.sandstorm.additive_printer.title", "quest.sandstorm.additive_printer.task", "quest.sandstorm.additive_printer.note", SandStormMod.id("printer_3d"), SandStormMod.id("printer_3d"), List.of("circuit_manufacturing"), SandStormMod.id("scrap_metal"), 4));
        register(new QuestData("wireless_solar_receiver", 3, "quest.sandstorm.wireless_solar_receiver.title", "quest.sandstorm.wireless_solar_receiver.task", "quest.sandstorm.wireless_solar_receiver.note", SandStormMod.id("wireless_solar_receiver"), SandStormMod.id("wireless_solar_receiver"), List.of("circuit_manufacturing"), SandStormMod.id("scrap_metal"), 4));
        register(new QuestData("anomaly_sensor", 3, "quest.sandstorm.anomaly_sensor.title", "quest.sandstorm.anomaly_sensor.task", "quest.sandstorm.anomaly_sensor.note", SandStormMod.id("anomaly_radar"), SandStormMod.id("anomaly_radar"), List.of("circuit_manufacturing"), SandStormMod.id("circuit_board"), 2));

        register(new QuestData("seismic_thumper", 4, "quest.sandstorm.seismic_thumper.title", "quest.sandstorm.seismic_thumper.task", "quest.sandstorm.seismic_thumper.note", SandStormMod.id("thumper"), SandStormMod.id("thumper"), List.of("circuit_manufacturing"), SandStormMod.id("scrap_metal"), 4));
        register(new QuestData("seismic_bait", 4, "quest.sandstorm.seismic_bait.title", "quest.sandstorm.seismic_bait.task", "quest.sandstorm.seismic_bait.note", SandStormMod.id("thumper"), SandStormMod.id("thumper"), List.of("seismic_thumper"), SandStormMod.id("scrap_metal"), 4, "sandstorm.thumper_activated"));
        register(new QuestData("sandworm_hunt", 4, "quest.sandstorm.sandworm_hunt.title", "quest.sandstorm.sandworm_hunt.task", "quest.sandstorm.sandworm_hunt.note", SandStormMod.id("sandworm_spawn_egg"), SandStormMod.id("sandworm_spawn_egg"), List.of("seismic_bait"), SandStormMod.id("circuit_board"), 4, "sandstorm.kill_sandworm"));
        register(new QuestData("sandworm_harvest", 4, "quest.sandstorm.sandworm_harvest.title", "quest.sandstorm.sandworm_harvest.task", "quest.sandstorm.sandworm_harvest.note", SandStormMod.id("sandworm_chitin"), SandStormMod.id("sandworm_chitin"), List.of("sandworm_hunt"), SandStormMod.id("silicon_wafer"), 6));
        register(new QuestData("sonic_defense", 4, "quest.sandstorm.sonic_defense.title", "quest.sandstorm.sonic_defense.task", "quest.sandstorm.sonic_defense.note", SandStormMod.id("sonic_cannon"), SandStormMod.id("sonic_cannon"), List.of("circuit_manufacturing"), SandStormMod.id("circuit_board"), 2));
        register(new QuestData("nanite_assembler", 4, "quest.sandstorm.nanite_assembler.title", "quest.sandstorm.nanite_assembler.task", "quest.sandstorm.nanite_assembler.note", SandStormMod.id("nanite_fabricator"), SandStormMod.id("nanite_fabricator"), List.of("additive_printer"), SandStormMod.id("nano_actuator"), 2));
        register(new QuestData("mecha_assembly", 4, "quest.sandstorm.mecha_assembly.title", "quest.sandstorm.mecha_assembly.task", "quest.sandstorm.mecha_assembly.note", SandStormMod.id("assembly_bay"), SandStormMod.id("assembly_bay"), List.of("nanite_assembler"), SandStormMod.id("tech_disc"), 1));
        register(new QuestData("megazord_upgrades", 4, "quest.sandstorm.megazord_upgrades.title", "quest.sandstorm.megazord_upgrades.task", "quest.sandstorm.megazord_upgrades.note", SandStormMod.id("megazord_flight_module"), SandStormMod.id("megazord_flight_module"), List.of("mecha_assembly"), SandStormMod.id("vectored_thruster"), 2));
        register(new QuestData("reprogrammer_tool", 3, "quest.sandstorm.reprogrammer_tool.title", "quest.sandstorm.reprogrammer_tool.task", "quest.sandstorm.reprogrammer_tool.note", SandStormMod.id("reprogrammer_tool"), SandStormMod.id("reprogrammer_tool"), List.of("circuit_manufacturing"), SandStormMod.id("scrap_metal"), 4));
        register(new QuestData("cyber_hound_companion", 3, "quest.sandstorm.cyber_hound_companion.title", "quest.sandstorm.cyber_hound_companion.task", "quest.sandstorm.cyber_hound_companion.note", SandStormMod.id("cyber_hound_spawn_egg"), SandStormMod.id("cyber_hound_spawn_egg"), List.of("reprogrammer_tool"), SandStormMod.id("circuit_board"), 2, "sandstorm.tame_cyber_hound"));
        register(new QuestData("molecular_modifier", 4, "quest.sandstorm.molecular_modifier.title", "quest.sandstorm.molecular_modifier.task", "quest.sandstorm.molecular_modifier.note", SandStormMod.id("molecular_modifier"), SandStormMod.id("molecular_modifier"), List.of("nanite_assembler", "sandworm_harvest"), SandStormMod.id("titanium_chitin_composite"), 2));
        register(new QuestData("mechanical_menace", 4, "quest.sandstorm.mechanical_menace.title", "quest.sandstorm.mechanical_menace.task", "quest.sandstorm.mechanical_menace.note", SandStormMod.id("derelict_automaton_spawn_egg"), SandStormMod.id("derelict_automaton_spawn_egg"), List.of("sonic_defense"), SandStormMod.id("titanium_chitin_composite"), 2, "sandstorm.kill_automaton"));

        register(new QuestData("atmospheric_probe", 5, "quest.sandstorm.atmospheric_probe.title", "quest.sandstorm.atmospheric_probe.task", "quest.sandstorm.atmospheric_probe.note", SandStormMod.id("atmospheric_analyzer"), SandStormMod.id("atmospheric_analyzer"), List.of("nanite_assembler"), SandStormMod.id("circuit_board"), 2));
        register(new QuestData("nutrient_bomb", 5, "quest.sandstorm.nutrient_bomb.title", "quest.sandstorm.nutrient_bomb.task", "quest.sandstorm.nutrient_bomb.note", SandStormMod.id("nutrient_bomb"), SandStormMod.id("nutrient_bomb"), List.of("atmospheric_probe"), SandStormMod.id("space_ration"), 4));
        register(new QuestData("planet_terraformer", 5, "quest.sandstorm.planet_terraformer.title", "quest.sandstorm.planet_terraformer.task", "quest.sandstorm.planet_terraformer.note", SandStormMod.id("atmospheric_terraformer"), SandStormMod.id("atmospheric_terraformer"), List.of("atmospheric_probe", "mecha_assembly"), SandStormMod.id("nano_actuator"), 2));
        register(new QuestData("biosphere_cartridge", 5, "quest.sandstorm.biosphere_cartridge.title", "quest.sandstorm.biosphere_cartridge.task", "quest.sandstorm.biosphere_cartridge.note", SandStormMod.id("biosphere_cartridge_cryo"), SandStormMod.id("biosphere_cartridge_cryo"), List.of("planet_terraformer"), SandStormMod.id("biosphere_cartridge_oasis"), 1));
        register(new QuestData("cryo_chiller", 5, "quest.sandstorm.cryo_chiller.title", "quest.sandstorm.cryo_chiller.task", "quest.sandstorm.cryo_chiller.note", SandStormMod.id("cryogenic_atmospheric_chiller"), SandStormMod.id("cryogenic_atmospheric_chiller"), List.of("biosphere_cartridge"), SandStormMod.id("titanium_chitin_composite"), 4));
        register(new QuestData("amniotic_incubator", 5, "quest.sandstorm.amniotic_incubator.title", "quest.sandstorm.amniotic_incubator.task", "quest.sandstorm.amniotic_incubator.note", SandStormMod.id("amniotic_incubator"), SandStormMod.id("amniotic_incubator"), List.of("biosphere_cartridge"), SandStormMod.id("xeno_grass_seeds"), 8));
        register(new QuestData("planetary_genesis", 5, "quest.sandstorm.planetary_genesis.title", "quest.sandstorm.planetary_genesis.task", "quest.sandstorm.planetary_genesis.note", SandStormMod.mcId("grass_block"), SandStormMod.mcId("grass_block"), List.of("planet_terraformer"), SandStormMod.mcId("grass_block"), 8));
    }

    private static void register(QuestData quest) {
        QUESTS.put(quest.id(), quest);
    }

    public static Map<String, QuestData> getAllQuests() {
        return Collections.unmodifiableMap(QUESTS);
    }

    public static List<QuestData> getQuestsForChapter(int chapter) {
        return QUESTS.values().stream().filter(q -> q.chapter() == chapter).toList();
    }

    public static QuestData getQuest(String id) {
        return QUESTS.get(id);
    }
}
