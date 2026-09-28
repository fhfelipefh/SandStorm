package com.fhfelipefh.sandstorm.content.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SandstormTerraformerCommandTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void registerShouldAddAllSubcommands() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        SandstormTerraformerCommand.register(dispatcher);

        CommandNode<CommandSourceStack> sandstorm = dispatcher.getRoot().getChild("sandstorm");
        assertNotNull(sandstorm);
        CommandNode<CommandSourceStack> terraformerSub = sandstorm.getChild("terraformer");
        assertNotNull(terraformerSub);
        assertNotNull(terraformerSub.getChild("tier1"));
        assertNotNull(terraformerSub.getChild("tier1").getChild("lightning"));
        assertNotNull(terraformerSub.getChild("tier2"));
        assertNotNull(terraformerSub.getChild("tier2").getChild("lightning"));
        assertNotNull(terraformerSub.getChild("tier3"));
        assertNotNull(terraformerSub.getChild("tier3").getChild("lightning"));
        assertNotNull(terraformerSub.getChild("lightning"));
        assertNotNull(terraformerSub.getChild("thunder"));

        CommandNode<CommandSourceStack> standaloneTerraformer = dispatcher.getRoot().getChild("terraformer");
        assertNotNull(standaloneTerraformer);
        assertNotNull(standaloneTerraformer.getChild("tier1"));
        assertNotNull(standaloneTerraformer.getChild("tier1").getChild("lightning"));
        assertNotNull(standaloneTerraformer.getChild("tier2"));
        assertNotNull(standaloneTerraformer.getChild("tier2").getChild("lightning"));
        assertNotNull(standaloneTerraformer.getChild("tier3"));
        assertNotNull(standaloneTerraformer.getChild("tier3").getChild("lightning"));
        assertNotNull(standaloneTerraformer.getChild("lightning"));
        assertNotNull(standaloneTerraformer.getChild("thunder"));
    }
}
