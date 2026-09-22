package com.fhfelipefh.sandstorm.core;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.command.SandstormClaimCommand;
import com.fhfelipefh.sandstorm.content.command.SandstormWeatherCommand;
import com.fhfelipefh.sandstorm.content.command.SandwormShowcaseCommand;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.gui.SandStormMenus;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.network.ClaimQuestRewardPayload;
import com.fhfelipefh.sandstorm.content.network.FlashlightTogglePayload;
import com.fhfelipefh.sandstorm.content.survival.FlashlightStateServer;
import com.fhfelipefh.sandstorm.content.network.SandstormWeatherPayload;
import com.fhfelipefh.sandstorm.content.network.SuitSyncPayload;
import com.fhfelipefh.sandstorm.content.network.SyncPlayerQuestsPayload;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.BedRestrictionHandler;
import com.fhfelipefh.sandstorm.content.survival.FusedSpaceSuitHandler;
import com.fhfelipefh.sandstorm.content.survival.MultiplayerSpawnHandler;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import com.fhfelipefh.sandstorm.content.survival.TechnologyToolRestrictionHandler;
import com.fhfelipefh.sandstorm.content.world.DimensionPortalRestrictionHandler;
import com.fhfelipefh.sandstorm.content.world.NutrientTerraformingManager;
import com.fhfelipefh.sandstorm.content.world.ProceduralRuinsManager;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import com.fhfelipefh.sandstorm.content.world.SandStormWorldGen;
import com.fhfelipefh.sandstorm.content.world.SpaceshipLandingManager;
import com.fhfelipefh.sandstorm.content.world.VanillaMonsterSuppressionHandler;
import com.fhfelipefh.sandstorm.content.recipe.RecipeUnlockHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
        PayloadTypeRegistry.clientboundPlay().register(
                SuitSyncPayload.TYPE,
                SuitSyncPayload.STREAM_CODEC
        );
        PayloadTypeRegistry.clientboundPlay().register(
                SandstormWeatherPayload.TYPE,
                SandstormWeatherPayload.STREAM_CODEC
        );
        PayloadTypeRegistry.clientboundPlay().register(
                SyncPlayerQuestsPayload.TYPE,
                SyncPlayerQuestsPayload.STREAM_CODEC
        );
        PayloadTypeRegistry.serverboundPlay().register(
                ClaimQuestRewardPayload.TYPE,
                ClaimQuestRewardPayload.STREAM_CODEC
        );
        PayloadTypeRegistry.serverboundPlay().register(
                FlashlightTogglePayload.TYPE,
                FlashlightTogglePayload.STREAM_CODEC
        );
        SandStormSoundEvents.initialize();
        SandStormItems.initialize();
        SandStormBlocks.initialize();
        SandStormMenus.initialize();
        SuitSurvivalHandler.initialize();
        VanillaMonsterSuppressionHandler.initialize();
        SandstormWeatherHandler.initialize();
        SandStormEntities.initialize();
        SeismicSurvivalHandler.initialize();
        TechnologyToolRestrictionHandler.initialize();
        BedRestrictionHandler.initialize();
        SandStormWorldGen.initialize();
        ProceduralRuinsManager.initialize();
        SpaceshipLandingManager.initialize();
        FusedSpaceSuitHandler.initialize();
        MultiplayerSpawnHandler.initialize();
        DimensionPortalRestrictionHandler.initialize();
        NutrientTerraformingManager.initialize();
        QuestRewardHandler.initialize();
        SandwormShowcaseCommand.initialize();
        SandstormWeatherCommand.initialize();
        SandstormClaimCommand.initialize();
        RecipeUnlockHandler.initialize();
        ServerPlayNetworking.registerGlobalReceiver(
                FlashlightTogglePayload.TYPE,
                (payload, context) -> {
                    FlashlightStateServer.setFlashlight(context.player().getUUID(), payload.enabled());
                }
        );
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static Identifier mcId(String path) {
        return Identifier.fromNamespaceAndPath("minecraft", path);
    }

    public static ResourceKey<Item> itemKey(String path) {
        return ResourceKey.create(Registries.ITEM, id(path));
    }

    public static ResourceKey<Block> blockKey(String path) {
        return ResourceKey.create(Registries.BLOCK, id(path));
    }
}
