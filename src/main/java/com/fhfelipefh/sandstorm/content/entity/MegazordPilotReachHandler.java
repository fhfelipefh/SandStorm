package com.fhfelipefh.sandstorm.content.entity;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class MegazordPilotReachHandler {
    public static final double BLOCK_REACH_BONUS = 8.0;
    private static final Identifier BLOCK_REACH_MODIFIER_ID = Identifier.fromNamespaceAndPath("sandstorm", "megazord_pilot_block_reach");
    private static final AttributeModifier BLOCK_REACH_MODIFIER = new AttributeModifier(
            BLOCK_REACH_MODIFIER_ID,
            BLOCK_REACH_BONUS,
            AttributeModifier.Operation.ADD_VALUE
    );

    private MegazordPilotReachHandler() {
    }

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                updateBlockReach(player);
            }
        });
    }

    private static void updateBlockReach(ServerPlayer player) {
        AttributeInstance reach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (reach == null) {
            return;
        }
        boolean isPilot = player.getVehicle() instanceof MegazordEntity megazord
                && megazord.getControllingPassenger() == player;
        if (isPilot) {
            if (!reach.hasModifier(BLOCK_REACH_MODIFIER_ID)) {
                reach.addOrUpdateTransientModifier(BLOCK_REACH_MODIFIER);
            }
        } else {
            reach.removeModifier(BLOCK_REACH_MODIFIER_ID);
        }
    }
}
