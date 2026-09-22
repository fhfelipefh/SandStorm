package com.fhfelipefh.sandstorm.content.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SandstormClaimCommandTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void registerShouldAddClaimCommands() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        SandstormClaimCommand.register(dispatcher);

        CommandNode<CommandSourceStack> claimNode = dispatcher.getRoot().getChild("claim");
        assertNotNull(claimNode);
        assertNotNull(claimNode.getChild("all"));
        assertNotNull(claimNode.getChild("questId"));

        CommandNode<CommandSourceStack> resgatarNode = dispatcher.getRoot().getChild("resgatar");
        assertNotNull(resgatarNode);
        assertNotNull(resgatarNode.getChild("tudo"));
        assertNotNull(resgatarNode.getChild("questId"));

        CommandNode<CommandSourceStack> sandstormNode = dispatcher.getRoot().getChild("sandstorm");
        assertNotNull(sandstormNode);
        assertNotNull(sandstormNode.getChild("claim"));
        assertNotNull(sandstormNode.getChild("claim").getChild("all"));
        assertNotNull(sandstormNode.getChild("claim").getChild("questId"));
    }
}
