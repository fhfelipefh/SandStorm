package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSwarmManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandstormDebugTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldRegisterAllDebugCommandsAndSubcommands() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        SandstormDebugCommand.register(dispatcher);

        CommandNode<CommandSourceStack> debugNode = dispatcher.getRoot().getChild("sandstorm_debug");
        assertNotNull(debugNode);
        assertNotNull(debugNode.getChild("phase"));
        assertNotNull(debugNode.getChild("spawn"));
        assertNotNull(debugNode.getChild("robot"));
        assertNotNull(debugNode.getChild("setup"));
        assertNotNull(debugNode.getChild("structure"));
        assertNotNull(debugNode.getChild("suit"));
        assertNotNull(debugNode.getChild("suit").getChild("refill"));
        assertNotNull(debugNode.getChild("suit").getChild("drain"));
        assertNotNull(debugNode.getChild("swarm"));
        assertNotNull(debugNode.getChild("swarm").getChild("status"));
        assertNotNull(debugNode.getChild("swarm").getChild("reset"));
        assertNotNull(debugNode.getChild("swarm").getChild("order"));
        assertNotNull(debugNode.getChild("weather"));
        assertNotNull(debugNode.getChild("weather").getChild("start"));
        assertNotNull(debugNode.getChild("weather").getChild("stop"));
        assertNotNull(debugNode.getChild("seismic"));
        assertNotNull(debugNode.getChild("list"));

        CommandNode<CommandSourceStack> sandstormNode = dispatcher.getRoot().getChild("sandstorm");
        assertNotNull(sandstormNode);
        assertNotNull(sandstormNode.getChild("debug"));
        assertNotNull(sandstormNode.getChild("debug").getChild("phase"));
        assertNotNull(sandstormNode.getChild("debug").getChild("spawn"));
        assertNotNull(sandstormNode.getChild("debug").getChild("robot"));
        assertNotNull(sandstormNode.getChild("debug").getChild("setup"));
        assertNotNull(sandstormNode.getChild("debug").getChild("structure"));
        assertNotNull(sandstormNode.getChild("debug").getChild("suit"));
        assertNotNull(sandstormNode.getChild("debug").getChild("swarm"));
        assertNotNull(sandstormNode.getChild("debug").getChild("weather"));
        assertNotNull(sandstormNode.getChild("debug").getChild("seismic"));
        assertNotNull(sandstormNode.getChild("debug").getChild("list"));
    }

    @Test
    void shouldValidateSupportedPhasesList() {
        List<String> phases = SandstormDebugCommand.getSupportedPhases();
        assertNotNull(phases);
        for (int i = 1; i <= 31; i++) {
            assertTrue(phases.contains(String.valueOf(i)));
        }
        assertTrue(phases.contains("all"));
        assertEquals(32, phases.size());
    }

    @Test
    void shouldValidateSupportedSpawnablesList() {
        List<String> spawnables = SandstormDebugCommand.getSupportedSpawnables();
        assertNotNull(spawnables);
        assertTrue(spawnables.contains("cyborg_excavator"));
        assertTrue(spawnables.contains("cyborg_builder"));
        assertTrue(spawnables.contains("cyborg_harvester"));
        assertTrue(spawnables.contains("excavator_vehicle"));
        assertTrue(spawnables.contains("megazord"));
        assertTrue(spawnables.contains("cargo_drone"));
        assertTrue(spawnables.contains("builder_drone"));
        assertTrue(spawnables.contains("sandworm"));
        assertTrue(spawnables.contains("sandboard"));
        assertEquals(9, spawnables.size());
    }

    @Test
    void shouldValidateSupportedFacilitiesList() {
        List<String> facilities = SandstormDebugCommand.getSupportedFacilities();
        assertNotNull(facilities);
        assertTrue(facilities.contains("cyborg_outpost"));
        assertTrue(facilities.contains("medbay_clinic"));
        assertTrue(facilities.contains("bioreactor_lab"));
        assertTrue(facilities.contains("molecular_workshop"));
        assertTrue(facilities.contains("spike_fortress"));
        assertTrue(facilities.contains("power_station"));
        assertTrue(facilities.contains("megastructure_site"));
        assertTrue(facilities.contains("hydroponics_dome"));
        assertTrue(facilities.contains("deep_drill_station"));
        assertTrue(facilities.contains("defense_perimeter"));
        assertTrue(facilities.contains("refinery_complex"));
        assertTrue(facilities.contains("terraformer_dome"));
        assertTrue(facilities.contains("maglev_station"));
        assertTrue(facilities.contains("starter_base"));
        assertTrue(facilities.contains("ancient_ruin_site"));
        assertTrue(facilities.contains("clone_facility"));
        assertTrue(facilities.contains("plasma_defense_complex"));
        assertTrue(facilities.contains("geothermal_well"));
        assertEquals(18, facilities.size());
    }

    @Test
    void shouldTestSwarmManagerOrdersIntegration() {
        CyborgSwarmManager manager = CyborgSwarmManager.getInstance();
        manager.setGlobalTacticalOrder(0);
        assertEquals(0, manager.getGlobalTacticalOrder());

        manager.setGlobalTacticalOrder(3);
        assertEquals(3, manager.getGlobalTacticalOrder());

        UUID dummyId = UUID.randomUUID();
        BlockPos target = new BlockPos(100, 64, 100);
        assertTrue(manager.tryReserveBlock(dummyId, target));
        assertTrue(manager.isBlockReserved(target));

        manager.releaseAll();
        assertFalse(manager.isBlockReserved(target));
    }
}
