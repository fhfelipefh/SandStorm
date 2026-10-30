package com.fhfelipefh.sandstorm.architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SubspacePortalGeothermalSafetyArchitectureTest {
    private static final Path GATEWAY_BE = Path.of("src/main/java/com/fhfelipefh/sandstorm/content/block/entity/SubspaceGatewayBlockEntity.java");
    private static final Path GATEWAY_BLOCK = Path.of("src/main/java/com/fhfelipefh/sandstorm/content/block/SubspaceGatewayBlock.java");

    @Test
    void gatewayBlockEntityMustImplementThermalRechargeAndDestinationPriming() throws IOException {
        String content = Files.readString(GATEWAY_BE);
        assertTrue(content.contains("getThermalRechargeRate"), "Must implement thermal recharge rate method");
        assertTrue(content.contains("Level.NETHER"), "Must detect Nether dimension for thermal recharge");
        assertTrue(content.contains("Blocks.LAVA"), "Must absorb heat from adjacent lava");
        assertTrue(content.contains("gatewayBe.getEnergy() < JUMP_COST"), "Must ensure landing gate is primed with jump energy");
        assertTrue(content.contains("gatewayBe.setEnergy(JUMP_COST)"), "Must prime landing gate with jump cost");
    }

    @Test
    void gatewayBlockMustAcceptNetherAndThermalEmergencyFuels() throws IOException {
        String content = Files.readString(GATEWAY_BLOCK);
        assertTrue(content.contains("Items.LAVA_BUCKET"), "Must accept lava bucket for emergency recharge");
        assertTrue(content.contains("Items.BLAZE_ROD"), "Must accept blaze rods as fuel");
        assertTrue(content.contains("Items.MAGMA_BLOCK"), "Must accept magma blocks as fuel");
        assertTrue(content.contains("animateTick"), "Must provide visual particle feedback for active or charging states");
    }

    @Test
    void gatewayMustPreventInfiniteBounceLoopsAndDrift() throws IOException {
        String content = Files.readString(GATEWAY_BE);
        assertTrue(content.contains("TRANSIT_COOLDOWN_TICKS"), "Must define transit cooldown to prevent bounce loops");
        assertTrue(content.contains("PLAYER_COOLDOWNS"), "Must track player cooldowns across dimensions");
        assertTrue(content.contains("landingPos.getZ() + 1.5"), "Player must arrive offset onto solid platform floor");
        assertTrue(content.contains("findSubspaceGatewayNear"), "Must search nearby for existing paired gateways to prevent drift");
        assertTrue(content.contains("linkedGatewayPos"), "Must store deterministic 1:1 linked gateway coordinates");
    }
}
