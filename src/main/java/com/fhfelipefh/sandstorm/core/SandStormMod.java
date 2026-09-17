package com.fhfelipefh.sandstorm.core;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SandStormMod implements ModInitializer {
    public static final String MOD_ID = "sandstorm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing SandStorm mod");
        SandStormItems.initialize();
        com.fhfelipefh.sandstorm.content.block.SandStormBlocks.initialize();
        SuitSurvivalHandler.initialize();
        com.fhfelipefh.sandstorm.content.world.VanillaMonsterSuppressionHandler.initialize();
        com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler.initialize();
        com.fhfelipefh.sandstorm.content.entity.SandStormEntities.initialize();
        com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler.initialize();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
