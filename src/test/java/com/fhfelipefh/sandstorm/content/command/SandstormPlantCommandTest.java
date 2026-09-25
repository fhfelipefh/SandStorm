package com.fhfelipefh.sandstorm.content.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SandstormPlantCommandTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void registerShouldAddAllPlantCommandsAndAliases() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        SandstormPlantCommand.register(dispatcher);

        CommandNode<CommandSourceStack> sandstormNode = dispatcher.getRoot().getChild("sandstorm");
        assertNotNull(sandstormNode);

        CommandNode<CommandSourceStack> plantsNode = sandstormNode.getChild("plants");
        assertNotNull(plantsNode);
        assertNotNull(plantsNode.getChild("line"));
        assertNotNull(plantsNode.getChild("strip"));
        assertNotNull(plantsNode.getChild("rows"));
        assertNotNull(plantsNode.getChild("field"));
        assertNotNull(plantsNode.getChild("pos"));

        assertNotNull(sandstormNode.getChild("plant"));
        assertNotNull(sandstormNode.getChild("plant_field"));

        assertNotNull(dispatcher.getRoot().getChild("sandstorm_plants"));
        assertNotNull(dispatcher.getRoot().getChild("plant_field"));
        assertNotNull(dispatcher.getRoot().getChild("plantar"));
    }

    @Test
    void speciesListShouldContainNineLivingBotanicalSpecies() {
        assertEquals(9, SandstormPlantCommand.getSpeciesCount());
        assertEquals(List.of(
                "halophyte_plant",
                "halophyte_succulent",
                "dune_ephedra",
                "heavy_sap_cactus",
                "ancient_reed",
                "radiotrophic_mycelium",
                "chitinolytic_fungus",
                "cryo_xerophilic_lichen",
                "xeno_grass"
        ), SandstormPlantCommand.getSpeciesIds());
    }
}
