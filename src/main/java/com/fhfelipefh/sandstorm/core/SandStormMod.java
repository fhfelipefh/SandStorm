package com.fhfelipefh.sandstorm.core;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SandStormMod implements ModInitializer {
    public static final String MOD_ID = "sandstorm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents.initialize();
        SandStormItems.initialize();
        com.fhfelipefh.sandstorm.content.block.SandStormBlocks.initialize();
        SuitSurvivalHandler.initialize();
        com.fhfelipefh.sandstorm.content.world.VanillaMonsterSuppressionHandler.initialize();
        com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler.initialize();
        com.fhfelipefh.sandstorm.content.entity.SandStormEntities.initialize();
        com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler.initialize();
        com.fhfelipefh.sandstorm.content.survival.TechnologyToolRestrictionHandler.initialize();
        com.fhfelipefh.sandstorm.content.survival.BedRestrictionHandler.initialize();
        com.fhfelipefh.sandstorm.content.world.SandStormWorldGen.initialize();
        com.fhfelipefh.sandstorm.content.world.SpaceshipLandingManager.initialize();
        com.fhfelipefh.sandstorm.content.survival.FusedSpaceSuitHandler.initialize();
        com.fhfelipefh.sandstorm.content.survival.MultiplayerSpawnHandler.initialize();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceKey<Item> itemKey(String path) {
        return ResourceKey.create(Registries.ITEM, id(path));
    }

    public static ResourceKey<Block> blockKey(String path) {
        return ResourceKey.create(Registries.BLOCK, id(path));
    }
}
