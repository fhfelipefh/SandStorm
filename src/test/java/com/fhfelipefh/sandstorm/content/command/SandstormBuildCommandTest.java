package com.fhfelipefh.sandstorm.content.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SandstormBuildCommandTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void registerShouldAddAllBuildCommandsAndAliases() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        SandstormBuildCommand.register(dispatcher);

        CommandNode<CommandSourceStack> sandstormNode = dispatcher.getRoot().getChild("sandstorm");
        assertNotNull(sandstormNode);
        assertNotNull(sandstormNode.getChild("build"));
        assertNotNull(sandstormNode.getChild("build").getChild("structure"));
        assertNotNull(sandstormNode.getChild("build").getChild("structure").getChild("pos"));
        assertNotNull(sandstormNode.getChild("structure"));
        assertNotNull(sandstormNode.getChild("structure").getChild("structure"));
        assertNotNull(sandstormNode.getChild("instant_build"));
        assertNotNull(sandstormNode.getChild("finish_build"));
        assertNotNull(sandstormNode.getChild("clear_area"));
        assertNotNull(sandstormNode.getChild("clear_area").getChild("radius"));

        CommandNode<CommandSourceStack> sandstormBuildNode = dispatcher.getRoot().getChild("sandstorm_build");
        assertNotNull(sandstormBuildNode);
        assertNotNull(sandstormBuildNode.getChild("structure"));
        assertNotNull(sandstormBuildNode.getChild("structure").getChild("pos"));

        CommandNode<CommandSourceStack> buildStructureNode = dispatcher.getRoot().getChild("build_structure");
        assertNotNull(buildStructureNode);
        assertNotNull(buildStructureNode.getChild("structure"));

        assertNotNull(dispatcher.getRoot().getChild("sandstorm_finish"));
        assertNotNull(dispatcher.getRoot().getChild("sandstorm_instant"));
        assertNotNull(dispatcher.getRoot().getChild("sandstorm_clear"));
        assertNotNull(dispatcher.getRoot().getChild("sandstorm_clear").getChild("radius"));

        assertNotNull(dispatcher.getRoot().getChild("build_colony"));
        assertNotNull(dispatcher.getRoot().getChild("build_defense"));
        assertNotNull(dispatcher.getRoot().getChild("build_greenhouse"));
        assertNotNull(dispatcher.getRoot().getChild("build_mining"));
        assertNotNull(dispatcher.getRoot().getChild("build_energy"));
        assertNotNull(dispatcher.getRoot().getChild("build_ruins"));
        assertNotNull(dispatcher.getRoot().getChild("build_dome"));
        assertNotNull(dispatcher.getRoot().getChild("build_citadel"));
        assertNotNull(dispatcher.getRoot().getChild("build_silo"));
        assertNotNull(dispatcher.getRoot().getChild("build_pyramid"));
        assertNotNull(dispatcher.getRoot().getChild("build_castle"));
        assertNotNull(dispatcher.getRoot().getChild("build_colossal_castle"));
        assertNotNull(dispatcher.getRoot().getChild("build_castelo"));
    }
}
