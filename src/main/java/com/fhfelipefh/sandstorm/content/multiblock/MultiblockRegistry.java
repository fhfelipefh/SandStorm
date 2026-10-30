package com.fhfelipefh.sandstorm.content.multiblock;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiblockRegistry {
    private static final Map<String, MultiblockStructure> STRUCTURES = new LinkedHashMap<>();

    static {
        registerWptRelayTower();
        registerVaultDoorChamber();
        registerSubspaceGateway();
        registerQuantumCluster();
        registerOrbitalLaunchComplex();
        registerSuperPistonArray();
    }

    private static void register(MultiblockStructure structure) {
        STRUCTURES.put(structure.getId(), structure);
    }

    private static void registerWptRelayTower() {
        List<MultiblockBlockRequirement> reqs = new ArrayList<>();
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 0, 0), SandStormBlocks.SOLID_STATE_ACCUMULATOR, 0x00B0FF, "Acumulador de Estado Sólido"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 1, 0), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2, 0xFFD54F, "Receptor Solar T2"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 2, 0), SandStormBlocks.WPT_RELAY_TOWER, 0x00E5FF, "Torre de Relé WPT"));

        register(new MultiblockStructure(
                "wpt_relay_tower",
                "multiblock.sandstorm.wpt_relay_tower.title",
                "multiblock.sandstorm.wpt_relay_tower.desc",
                "wireless_solar_receiver",
                reqs
        ));
    }

    private static void registerVaultDoorChamber() {
        List<MultiblockBlockRequirement> reqs = new ArrayList<>();
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 1, 0), SandStormBlocks.VAULT_DOOR_CONTROLLER, 0xFF6D00, "Controlador de Porta 3x3"));

        reqs.add(new MultiblockBlockRequirement(new BlockPos(-1, 1, 0), SandStormBlocks.VAULT_DOOR_DUMMY, 0xFFAB40, "Porta Mecânica"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(1, 1, 0), SandStormBlocks.VAULT_DOOR_DUMMY, 0xFFAB40, "Porta Mecânica"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(-1, 2, 0), SandStormBlocks.VAULT_DOOR_DUMMY, 0xFFAB40, "Porta Mecânica"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 2, 0), SandStormBlocks.VAULT_DOOR_DUMMY, 0xFFAB40, "Porta Mecânica"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(1, 2, 0), SandStormBlocks.VAULT_DOOR_DUMMY, 0xFFAB40, "Porta Mecânica"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(-1, 3, 0), SandStormBlocks.VAULT_DOOR_DUMMY, 0xFFAB40, "Porta Mecânica"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 3, 0), SandStormBlocks.VAULT_DOOR_DUMMY, 0xFFAB40, "Porta Mecânica"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(1, 3, 0), SandStormBlocks.VAULT_DOOR_DUMMY, 0xFFAB40, "Porta Mecânica"));

        reqs.add(new MultiblockBlockRequirement(new BlockPos(-2, 1, 0), SandStormBlocks.VAULT_WALL_SEAM, 0x78909C, "Pilar de Junção"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(-2, 2, 0), SandStormBlocks.VAULT_WALL_SEAM, 0x78909C, "Pilar de Junção"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(-2, 3, 0), SandStormBlocks.VAULT_WALL_SEAM, 0x78909C, "Pilar de Junção"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(2, 1, 0), SandStormBlocks.VAULT_WALL_SEAM, 0x78909C, "Pilar de Junção"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(2, 2, 0), SandStormBlocks.VAULT_WALL_SEAM, 0x78909C, "Pilar de Junção"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(2, 3, 0), SandStormBlocks.VAULT_WALL_SEAM, 0x78909C, "Pilar de Junção"));

        reqs.add(new MultiblockBlockRequirement(new BlockPos(-1, 4, 0), SandStormBlocks.VAULT_WALL_PANEL, 0x90A4AE, "Painel de Aço"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 4, 0), SandStormBlocks.VAULT_WALL_PANEL, 0x90A4AE, "Painel de Aço"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(1, 4, 0), SandStormBlocks.VAULT_WALL_PANEL, 0x90A4AE, "Painel de Aço"));

        register(new MultiblockStructure(
                "vault_door_chamber",
                "multiblock.sandstorm.vault_door_chamber.title",
                "multiblock.sandstorm.vault_door_chamber.desc",
                "mechanical_menace",
                reqs
        ));
    }

    private static void registerSubspaceGateway() {
        List<MultiblockBlockRequirement> reqs = new ArrayList<>();
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 0, 0), SandStormBlocks.SUBSPACE_GATEWAY, 0xAB47BC, "Portal Subespacial"));

        reqs.add(new MultiblockBlockRequirement(new BlockPos(-2, 0, 0), SandStormBlocks.PIEZO_QUARTZ_BLOCK, 0xEA80FC, "Bobina Piezoelétrica"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(2, 0, 0), SandStormBlocks.PIEZO_QUARTZ_BLOCK, 0xEA80FC, "Bobina Piezoelétrica"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 0, -2), SandStormBlocks.PIEZO_QUARTZ_BLOCK, 0xEA80FC, "Bobina Piezoelétrica"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 0, 2), SandStormBlocks.PIEZO_QUARTZ_BLOCK, 0xEA80FC, "Bobina Piezoelétrica"));

        reqs.add(new MultiblockBlockRequirement(new BlockPos(-2, 1, 0), Blocks.SEA_LANTERN, 0x80DEEA, "Lente de Foco"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(2, 1, 0), Blocks.SEA_LANTERN, 0x80DEEA, "Lente de Foco"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 1, -2), Blocks.SEA_LANTERN, 0x80DEEA, "Lente de Foco"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 1, 2), Blocks.SEA_LANTERN, 0x80DEEA, "Lente de Foco"));

        reqs.add(new MultiblockBlockRequirement(new BlockPos(-2, 0, -2), SandStormBlocks.SOLID_STATE_ACCUMULATOR, 0x00B0FF, "Acumulador 500 kJ"));

        register(new MultiblockStructure(
                "subspace_gateway",
                "multiblock.sandstorm.subspace_gateway.title",
                "multiblock.sandstorm.subspace_gateway.desc",
                "planet_terraformer",
                reqs
        ));
    }

    private static void registerQuantumCluster() {
        List<MultiblockBlockRequirement> reqs = new ArrayList<>();
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 0, 0), SandStormBlocks.QUANTUM_NETWORK_CONTROLLER, 0x2979FF, "Processador Central"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(1, 0, 0), SandStormBlocks.QUANTUM_NETWORK_CABLE, 0x00E5FF, "Cabo de Dados"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(2, 0, 0), SandStormBlocks.QUANTUM_DISK_DRIVE, 0x651FFF, "Unidade de Discos"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 0, 1), SandStormBlocks.QUANTUM_NETWORK_CABLE, 0x00E5FF, "Cabo de Dados"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 0, 2), SandStormBlocks.QUANTUM_ACCESS_TERMINAL, 0x00E676, "Terminal de Acesso"));

        register(new MultiblockStructure(
                "quantum_computer_cluster",
                "multiblock.sandstorm.quantum_cluster.title",
                "multiblock.sandstorm.quantum_cluster.desc",
                "additive_printer",
                reqs
        ));
    }

    private static void registerOrbitalLaunchComplex() {
        List<MultiblockBlockRequirement> reqs = new ArrayList<>();
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 0, 0), SandStormBlocks.ORBITAL_MASS_DRIVER, 0xFF5722, "Canhão de Massa"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(1, 0, 0), SandStormBlocks.ORBITAL_GROUND_STATION, 0x00BCD4, "Estação de Terra"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(-1, 0, 0), SandStormBlocks.SOLID_STATE_ACCUMULATOR, 0x0091EA, "Acumulador"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 0, 1), SandStormBlocks.SPECTRAL_SURVEY_TELESCOPE, 0x9C27B0, "Telescópio"));

        register(new MultiblockStructure(
                "orbital_launch_complex",
                "multiblock.sandstorm.orbital_launch.title",
                "multiblock.sandstorm.orbital_launch.desc",
                "atmospheric_probe",
                reqs
        ));
    }

    private static void registerSuperPistonArray() {
        List<MultiblockBlockRequirement> reqs = new ArrayList<>();
        reqs.add(new MultiblockBlockRequirement(new BlockPos(-2, 0, 0), SandStormBlocks.COMPACT_SLAB_PISTON, 0xFF7043, "Pistão Meia-Laje"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(0, 0, 0), SandStormBlocks.HEAVY_KINETIC_PISTON, 0xF4511E, "Pistão Cinético"));
        reqs.add(new MultiblockBlockRequirement(new BlockPos(2, 0, 0), SandStormBlocks.TECTONIC_KINETIC_PISTON, 0xD84315, "Pistão Tectônico"));

        register(new MultiblockStructure(
                "super_piston_array",
                "multiblock.sandstorm.super_piston.title",
                "multiblock.sandstorm.super_piston.desc",
                "mecha_assembly",
                reqs
        ));
    }

    public static Map<String, MultiblockStructure> getAllStructures() {
        return Collections.unmodifiableMap(STRUCTURES);
    }

    public static Collection<MultiblockStructure> getAll() {
        return Collections.unmodifiableCollection(STRUCTURES.values());
    }

    public static MultiblockStructure getStructure(String id) {
        return STRUCTURES.get(id);
    }

    public static MultiblockStructure get(String id) {
        return STRUCTURES.get(id);
    }

    public static MultiblockStructure getStructureForQuest(String questId) {
        if (questId == null || questId.isEmpty()) {
            return null;
        }
        for (MultiblockStructure s : STRUCTURES.values()) {
            if (questId.equals(s.getAssociatedQuestId())) {
                return s;
            }
        }
        return null;
    }
}

