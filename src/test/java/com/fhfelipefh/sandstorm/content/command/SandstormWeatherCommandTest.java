package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandstormWeatherCommandTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @BeforeEach
    void setUp() {
        SandstormWeatherHandler.resetWeather();
    }

    @Test
    void registerShouldAddAllSubcommands() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        SandstormWeatherCommand.register(dispatcher);

        CommandNode<CommandSourceStack> root = dispatcher.getRoot().getChild("sandstorm");
        assertNotNull(root);
        assertNotNull(root.getChild("start"));
        assertNotNull(root.getChild("stop"));
        assertNotNull(root.getChild("toggle"));
        assertNotNull(root.getChild("weather"));
        assertNotNull(root.getChild("weather").getChild("start"));
        assertNotNull(root.getChild("weather").getChild("stop"));
        assertNotNull(root.getChild("weather").getChild("toggle"));
    }

    @Test
    void weatherHandlerStateCanBeToggled() {
        assertFalse(SandstormWeatherHandler.getWeather().isActive());

        SandstormWeatherHandler.triggerSandstorm(6000, 0.85);
        assertTrue(SandstormWeatherHandler.getWeather().isActive());
        assertEquals(6000, SandstormWeatherHandler.getWeather().getRemainingTicks());
        assertEquals(0.85, SandstormWeatherHandler.getWeather().getTargetIntensity(), 0.001);

        SandstormWeatherHandler.stopSandstorm();
        assertFalse(SandstormWeatherHandler.getWeather().isActive());
        assertEquals(0, SandstormWeatherHandler.getWeather().getRemainingTicks());
    }
}
