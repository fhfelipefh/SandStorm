package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CyberSteedArchitectureTest {

    private static final Path ENTITY_FILE = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "CyberSteedEntity.java");
    private static final Path RENDERER_FILE = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "CyberSteedRenderer.java");
    private static final Path MODEL_FILE = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "CyberSteedModel.java");
    private static final Path RENDER_STATE_FILE = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "CyberSteedRenderState.java");
    private static final Path REPROGRAMMER_FILE = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "ReprogrammerToolItem.java");
    private static final Path SPAWN_MANAGER_FILE = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "DesertMobSpawnManager.java");
    private static final Path HUD_OVERLAY_FILE = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "hud", "MachineCockpitHudOverlay.java");

    @Test
    void entityFileMustExistAndImplementRequiredContracts() throws IOException {
        assertTrue(Files.exists(ENTITY_FILE), "CyberSteedEntity.java must exist");
        String content = Files.readString(ENTITY_FILE);

        assertTrue(content.contains("extends TamableAnimal"), "Must extend TamableAnimal");
        assertTrue(content.contains("implements DeactivatableAndroid, HasCustomInventoryScreen"), "Must implement DeactivatableAndroid and HasCustomInventoryScreen");
        assertTrue(content.contains("openCustomInventoryScreen"), "Must implement vanilla HasCustomInventoryScreen contract");
        assertTrue(content.contains("DesertExplorationWanderGoal"), "Must use DesertExplorationWanderGoal");
        assertFalse(content.contains("WaterAvoidingRandomStrollGoal"), "Must not use vanilla stroll goal");
        assertTrue(content.contains("getControllingPassenger"), "Must implement passenger control");
        assertTrue(content.contains("getPassengerAttachmentPoint"), "Must specify seat position");
        assertTrue(content.contains("tickRidden"), "Must implement riding tick physics");
        assertTrue(content.contains("getRiddenSpeed"), "Must adjust speed on desert terrain");
        assertTrue(content.contains("public boolean showVehicleHealth() {\n        return false;\n    }"), "Must suppress vanilla horse hearts");
        assertTrue(content.contains("detonateCanister"), "Must support dorsal volatile fuel canister detonation");
        assertTrue(content.contains("DATA_CANISTER_EXPLODED"), "Must synchronize exploded canister state");
        assertTrue(content.contains("hasSonicTurret"), "Must support sonic turret upgrade");
        assertTrue(content.contains("hasBatteryPack"), "Must support battery pack upgrade");
        assertTrue(content.contains("hasSpeedOverdrive"), "Must support speed overdrive upgrade");
        assertTrue(content.contains("hasCargoPanniers"), "Must support cargo panniers upgrade");
        assertTrue(content.contains("cargoInventory"), "Must have cargo inventory container");

        int secondaryIndex = content.indexOf("if (player.isSecondaryUseActive())");
        assertTrue(secondaryIndex >= 0);
        String secondaryBlock = content.substring(secondaryIndex, secondaryIndex + 250);
        assertTrue(secondaryBlock.contains("setOrderedToSit"), "Secondary use (Shift+Click) must always toggle sit/anchor state");
        assertFalse(secondaryBlock.contains("cargoInventory"), "Secondary use must not hijack anchor toggle for cargo inventory");

        assertTrue(Files.exists(HUD_OVERLAY_FILE), "MachineCockpitHudOverlay.java must exist");
        String hudContent = Files.readString(HUD_OVERLAY_FILE);
        assertTrue(hudContent.contains("CyberSteedEntity"), "HUD must handle CyberSteedEntity");
        assertTrue(hudContent.contains("renderCyberSteedHud"), "HUD must render cyber steed telemetry");
        assertTrue(hudContent.contains("drawShieldIcon"), "HUD must render shield icon");
        assertTrue(hudContent.contains("drawCanisterIcon"), "HUD must render canister icon");
        assertTrue(hudContent.contains("drawUpgradeSocket"), "HUD must render upgrade sockets");
        int methodIndex = hudContent.indexOf("void renderCyberSteedHud");
        assertTrue(methodIndex >= 0, "renderCyberSteedHud method must exist");
        String cyberHudSnippet = hudContent.substring(methodIndex, hudContent.indexOf("void drawShieldIcon"));
        assertFalse(cyberHudSnippet.contains("extractor.text"), "Cyber Steed HUD must have ZERO text elements");
        assertFalse(cyberHudSnippet.contains("Component.literal"), "Cyber Steed HUD must have ZERO text literals");
    }

    @Test
    void clientRendererAndModelMustExist() throws IOException {
        assertTrue(Files.exists(RENDERER_FILE), "CyberSteedRenderer.java must exist");
        assertTrue(Files.exists(MODEL_FILE), "CyberSteedModel.java must exist");
        assertTrue(Files.exists(RENDER_STATE_FILE), "CyberSteedRenderState.java must exist");

        String modelContent = Files.readString(MODEL_FILE);
        assertTrue(modelContent.contains("fuel_canister"), "Model must include dorsal fuel canister");
        assertTrue(modelContent.contains("sonic_turret"), "Model must include sonic turret part");
        assertTrue(modelContent.contains("battery_pack"), "Model must include battery pack part");
        assertTrue(modelContent.contains("speed_overdrive"), "Model must include speed overdrive part");
        assertTrue(modelContent.contains("cargo_panniers"), "Model must include cargo panniers part");
        assertTrue(modelContent.contains("neck"), "Model must include articulated neck");
        assertTrue(modelContent.contains("head"), "Model must include equine robotic head");
        assertTrue(modelContent.contains("rightFrontLeg"), "Model must include quadruped legs");
        assertTrue(modelContent.contains("LayerDefinition.create(mesh, 128, 128)"), "Model must use 128x128 texture space");
    }

    @Test
    void reprogrammerAndSpawnManagerMustSupportCyberSteed() throws IOException {
        String reprogrammerContent = Files.readString(REPROGRAMMER_FILE);
        assertTrue(reprogrammerContent.contains("CyberSteedEntity"), "ReprogrammerToolItem must support CyberSteedEntity");
        assertTrue(reprogrammerContent.contains("cyber_steed_reprogrammed"), "Must send steed reprogramming feedback");

        String spawnManagerContent = Files.readString(SPAWN_MANAGER_FILE);
        assertTrue(spawnManagerContent.contains("CYBER_STEED"), "DesertMobSpawnManager must spawn CYBER_STEED");
        assertTrue(spawnManagerContent.contains("CyberSteedEntity"), "DesertMobSpawnManager must recognize CyberSteedEntity");
    }

    @Test
    void allTexturesMustExistAndBeValidPngs() throws IOException {
        List<Path> textures = List.of(
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "cyber_steed", "cyber_steed_wild.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "cyber_steed", "cyber_steed_tamed.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "cyber_steed", "cyber_steed_deactivated.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "cyber_steed_spawn_egg.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "steed_upgrade_sonic_turret.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "steed_upgrade_battery.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "steed_upgrade_speed.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "steed_upgrade_cargo.png")
        );

        byte[] expectedHeader = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        for (Path texture : textures) {
            assertTrue(Files.exists(texture), "Missing texture: " + texture);
            assertTrue(Files.size(texture) > 0, "Texture is empty: " + texture);
            byte[] bytes = Files.readAllBytes(texture);
            assertTrue(bytes.length >= 8, "Texture header too short: " + texture);
            for (int i = 0; i < 8; i++) {
                assertTrue(bytes[i] == expectedHeader[i], "Invalid PNG signature in " + texture);
            }
        }
    }
}

