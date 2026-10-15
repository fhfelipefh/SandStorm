package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.technical.TechnicalSpecificationRegistry;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubspaceWarpProgressionTest {

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
    void subspaceWarpCoreMustBeEpicRarity() throws IOException {
        assertNotNull(SandStormItems.SUBSPACE_WARP_CORE);
        assertTrue(TechnicalSpecificationRegistry.has(SandStormItems.SUBSPACE_WARP_CORE));
        Path itemsSource = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SandStormItems.java");
        assertTrue(Files.exists(itemsSource));
        String code = Files.readString(itemsSource);
        assertTrue(code.contains("SUBSPACE_WARP_CORE = register(\"subspace_warp_core\", new Item(properties(\"subspace_warp_core\").stacksTo(16).rarity(Rarity.EPIC)));"));
    }

    @Test
    void subspaceGatewayRecipeMustRequireSubspaceWarpCore() throws IOException {
        Path recipe = Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "subspace_gateway.json");
        assertTrue(Files.exists(recipe));
        String json = Files.readString(recipe);
        assertTrue(json.contains("sandstorm:subspace_warp_core"));
    }

    @Test
    void dimensionalStorageCartridgeRecipeMustRequireSubspaceWarpCore() throws IOException {
        Path recipe = Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "quantum_storage_cartridge_dimensional.json");
        assertTrue(Files.exists(recipe));
        String json = Files.readString(recipe);
        assertTrue(json.contains("sandstorm:subspace_warp_core"));
    }

    @Test
    void bioNeuralCoreRecipeMustRequireTechDisc() throws IOException {
        Path recipe = Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "bio_neural_core.json");
        assertTrue(Files.exists(recipe));
        String json = Files.readString(recipe);
        assertTrue(json.contains("sandstorm:tech_disc"));
    }

    @Test
    void orbitalKineticLanceRecipeMustRequireTechDisc() throws IOException {
        Path recipe = Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "orbital_kinetic_lance_satellite.json");
        assertTrue(Files.exists(recipe));
        String json = Files.readString(recipe);
        assertTrue(json.contains("sandstorm:tech_disc"));
    }

    @Test
    void dungeonCatalogsMustDistributeSubspaceWarpCoreInVaults() throws IOException {
        Path militaryLab = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "MilitaryLabCatalog.java");
        Path millennialRepo = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "MillennialRepositoryCatalog.java");
        Path ancientBunker = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "AncientBunkerCatalog.java");

        assertTrue(Files.exists(militaryLab));
        assertTrue(Files.exists(millennialRepo));
        assertTrue(Files.exists(ancientBunker));

        assertTrue(Files.readString(militaryLab).contains("SUBSPACE_WARP_CORE"));
        assertTrue(Files.readString(millennialRepo).contains("SUBSPACE_WARP_CORE"));
        assertTrue(Files.readString(ancientBunker).contains("SUBSPACE_WARP_CORE"));
    }
}
