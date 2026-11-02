package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.content.multiblock.MultiblockRegistry;
import com.fhfelipefh.sandstorm.content.multiblock.MultiblockStructure;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiblockHologramArchitectureTest {

    private static final Path MULTIBLOCK_REGISTRY_PATH = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "multiblock", "MultiblockRegistry.java"
    );
    private static final Path MULTIBLOCK_RENDERER_PATH = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "MultiblockHologramRenderer.java"
    );
    private static final Path CLIENT_INIT_PATH = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "SandStormClient.java"
    );
    private static final Path DATAPAD_SCREEN_PATH = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "gui", "SurvivalDatapadScreen.java"
    );
    private static final Path DATAPAD_ITEM_PATH = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SurvivalDatapadItem.java"
    );
    private static final Path DATAPAD_HELPER_PATH = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "gui", "DatapadClientHelper.java"
    );

    @BeforeAll
    static void setup() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BuiltInRegistries.BLOCK, false);
        frozenField.set(BuiltInRegistries.ITEM, false);
        frozenField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());
    }

    @Test
    void testAllCanonicalMultiblocksRegistered() {
        List<String> canonicalIds = List.of(
                "wpt_relay_tower",
                "vault_door_chamber",
                "subspace_gateway",
                "quantum_computer_cluster",
                "orbital_launch_complex",
                "super_piston_array"
        );

        for (String id : canonicalIds) {
            MultiblockStructure structure = MultiblockRegistry.get(id);
            assertNotNull(structure, "Multiblock structure must exist: " + id);
            assertFalse(structure.getRequirements().isEmpty(), "Structure requirements must not be empty: " + id);
            assertTrue(structure.getTotalBlocks() > 0, "Structure must require at least 1 block: " + id);
        }
    }

    @Test
    void testMultiblockAssociatedQuestsExistInQuestRegistry() {
        for (MultiblockStructure structure : MultiblockRegistry.getAll()) {
            if (structure.getAssociatedQuestId() != null) {
                assertNotNull(QuestRegistry.getQuest(structure.getAssociatedQuestId()),
                        "Associated quest must exist in QuestRegistry: " + structure.getAssociatedQuestId());
            }
        }
    }

    @Test
    void testClientRendererRegisteredInSandStormClient() throws IOException {
        assertTrue(Files.exists(CLIENT_INIT_PATH), "SandStormClient.java must exist");
        String content = Files.readString(CLIENT_INIT_PATH);
        assertTrue(content.contains("MultiblockHologramRenderer::render"),
                "SandStormClient must register MultiblockHologramRenderer in COLLECT_SUBMITS");
    }

    @Test
    void testDatapadIntegratesMultiblockHologramSupport() throws IOException {
        assertTrue(Files.exists(DATAPAD_SCREEN_PATH), "SurvivalDatapadScreen.java must exist");
        String content = Files.readString(DATAPAD_SCREEN_PATH);
        assertTrue(content.contains("MultiblockHologramRenderer.setActiveStructure"),
                "Datapad must be able to project multiblock structures");
        assertTrue(content.contains("MultiblockHologramRenderer.clearActiveStructure"),
                "Datapad must be able to clear active projections");
        assertTrue(content.contains("tabCount = 6"),
                "Datapad must configure 6 tabs for multiblocks");
        assertTrue(content.contains("drawHologramIconButton"),
                "Datapad must use icon button for hologram projection");
        assertTrue(content.contains("drawHologramIcon"),
                "Datapad must render visual hologram icon");
        assertTrue(content.contains("clear_header_btn"),
                "Datapad header must offer clear button when hologram is active");
    }

    @Test
    void testNoSingleBlockQuestsPollutedWithMultiblockStructure() {
        for (MultiblockStructure structure : MultiblockRegistry.getAll()) {
            assertFalse("additive_printer".equals(structure.getAssociatedQuestId()),
                    "Multiblock structure must not be associated with single block additive printer: " + structure.getId());
        }
    }

    @Test
    void testDatapadInWorldClearSupport() throws IOException {
        assertTrue(Files.exists(DATAPAD_ITEM_PATH), "SurvivalDatapadItem.java must exist");
        String itemContent = Files.readString(DATAPAD_ITEM_PATH);
        assertTrue(itemContent.contains("clientScreenOpener") && itemContent.contains("appendHoverText"),
                "SurvivalDatapadItem must support client opener and hint");

        assertTrue(Files.exists(DATAPAD_HELPER_PATH), "DatapadClientHelper.java must exist");
        String helperContent = Files.readString(DATAPAD_HELPER_PATH);
        assertTrue(helperContent.contains("isShiftKeyDown") && helperContent.contains("clearActiveStructure"),
                "DatapadClientHelper must handle in-world hologram deactivation on shift-click");
    }

    @Test
    void testHologramLocalizationKeysExist() throws IOException {
        List<String> langs = List.of("pt_br.json", "en_us.json", "es_es.json");
        List<String> requiredKeys = List.of(
                "gui.sandstorm.datapad.multiblock.clear_header_btn",
                "gui.sandstorm.datapad.multiblock.holo_clear_tip",
                "gui.sandstorm.datapad.multiblock.holo_proj_tip",
                "item.sandstorm.survival_datapad.hologram_hint"
        );

        for (String lang : langs) {
            Path langPath = Path.of("src", "main", "resources", "assets", "sandstorm", "lang", lang);
            assertTrue(Files.exists(langPath), "Language file must exist: " + lang);
            String langContent = Files.readString(langPath);
            for (String key : requiredKeys) {
                assertTrue(langContent.contains("\"" + key + "\""),
                        "Language file " + lang + " must contain key: " + key);
            }
        }
    }

    @Test
    void testFilesExistAndHaveNoComments() throws IOException {
        List<Path> paths = List.of(
                MULTIBLOCK_REGISTRY_PATH,
                MULTIBLOCK_RENDERER_PATH,
                Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "multiblock", "MultiblockBlockRequirement.java"),
                Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "multiblock", "MultiblockStructure.java")
        );

        for (Path path : paths) {
            assertTrue(Files.exists(path), "File must exist: " + path);
            String content = Files.readString(path);
            assertFalse(content.contains("//"), "Must not contain single-line comments in " + path);
            assertFalse(content.contains("/*"), "Must not contain multi-line comments in " + path);
        }
    }
}
