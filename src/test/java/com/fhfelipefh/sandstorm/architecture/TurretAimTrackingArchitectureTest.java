package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurretAimTrackingArchitectureTest {

    private static final Path CLIENT_INIT = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "SandStormClient.java");
    private static final Path TURRET_BLOCK = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "AutonomousSonicTurretBlock.java");
    private static final Path TURRET_BE = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "entity", "AutonomousSonicTurretBlockEntity.java");
    private static final Path TURRET_RENDERER = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "AutonomousSonicTurretRenderer.java");
    private static final Path TURRET_MODEL = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "AutonomousSonicTurretModel.java");
    private static final Path TURRET_TEXTURE = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "autonomous_sonic_turret", "autonomous_sonic_turret_mechanisms.png");
    private static final Path TURRET_BLOCK_MODEL = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block", "autonomous_sonic_turret.json");
    private static final Path TURRET_ITEM_MODEL = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block", "autonomous_sonic_turret_item.json");

    private static final byte[] PNG_HEADER = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    @Test
    void autonomousSonicTurretMustRegisterBlockEntityRendererAndClientTicker() throws IOException {
        assertTrue(Files.exists(CLIENT_INIT), "SandStormClient.java must exist");
        String clientSource = Files.readString(CLIENT_INIT);
        assertTrue(clientSource.contains("AUTONOMOUS_SONIC_TURRET_BE"), "SandStormClient must register AUTONOMOUS_SONIC_TURRET_BE");
        assertTrue(clientSource.contains("AutonomousSonicTurretRenderer::new"), "SandStormClient must bind AutonomousSonicTurretRenderer");

        assertTrue(Files.exists(TURRET_BLOCK), "AutonomousSonicTurretBlock.java must exist");
        String blockSource = Files.readString(TURRET_BLOCK);
        assertTrue(blockSource.contains("clientTick"), "AutonomousSonicTurretBlock must support client ticking for smooth tracking");

        assertTrue(Files.exists(TURRET_BE), "AutonomousSonicTurretBlockEntity.java must exist");
        String beSource = Files.readString(TURRET_BE);
        assertTrue(beSource.contains("ROTATION_SPEED"), "Turret entity must define ROTATION_SPEED");
        assertTrue(beSource.contains("AIM_TOLERANCE"), "Turret entity must define AIM_TOLERANCE");
        assertTrue(beSource.contains("currentYaw"), "Turret entity must track currentYaw");
        assertTrue(beSource.contains("currentPitch"), "Turret entity must track currentPitch");
        assertTrue(beSource.contains("clientTick"), "Turret entity must implement clientTick");
    }

    @Test
    void autonomousSonicTurretRenderAssetsMustBePhysicallyIntact() throws IOException {
        assertTrue(Files.exists(TURRET_RENDERER), "AutonomousSonicTurretRenderer must exist");
        assertTrue(Files.exists(TURRET_MODEL), "AutonomousSonicTurretModel must exist");
        assertTrue(Files.exists(TURRET_TEXTURE), "Turret mechanism texture must exist");
        assertTrue(Files.exists(TURRET_BLOCK_MODEL), "Turret base block model must exist");
        assertTrue(Files.exists(TURRET_ITEM_MODEL), "Turret item model must exist");

        byte[] header = new byte[8];
        try (FileInputStream fis = new FileInputStream(TURRET_TEXTURE.toFile())) {
            int read = fis.read(header);
            assertTrue(read >= 8, "Texture file must be at least 8 bytes");
            assertArrayEquals(PNG_HEADER, header, "Turret texture must have valid PNG header");
        }

        String blockModel = Files.readString(TURRET_BLOCK_MODEL);
        assertTrue(blockModel.contains("base_plate"), "Block model must render base plate");
        assertTrue(blockModel.contains("gimbal_mount"), "Block model must render gimbal mount");

        String itemModel = Files.readString(TURRET_ITEM_MODEL);
        assertTrue(itemModel.contains("turret_housing"), "Item model must include turret housing");
        assertTrue(itemModel.contains("barrel_left"), "Item model must include barrel left");
        assertTrue(itemModel.contains("barrel_right"), "Item model must include barrel right");
    }
}
