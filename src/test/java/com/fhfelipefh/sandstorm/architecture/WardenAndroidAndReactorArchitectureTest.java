package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WardenAndroidAndReactorArchitectureTest {

    @Test
    public void testWardenAndroidAndReactorAssetsExist() {
        Path itemDef = Path.of("src/main/resources/assets/sandstorm/items/warden_singularity_core.json");
        Path itemModel = Path.of("src/main/resources/assets/sandstorm/models/item/warden_singularity_core.json");
        Path itemTex = Path.of("src/main/resources/assets/sandstorm/textures/item/warden_singularity_core.png");

        Path eggDef = Path.of("src/main/resources/assets/sandstorm/items/warden_android_spawn_egg.json");
        Path eggModel = Path.of("src/main/resources/assets/sandstorm/models/item/warden_android_spawn_egg.json");
        Path eggTex = Path.of("src/main/resources/assets/sandstorm/textures/item/warden_android_spawn_egg.png");

        Path entityTex = Path.of("src/main/resources/assets/sandstorm/textures/entity/warden_android/warden_android.png");
        Path entityDeactTex = Path.of("src/main/resources/assets/sandstorm/textures/entity/warden_android/warden_android_deactivated.png");

        Path reactorBlockstate = Path.of("src/main/resources/assets/sandstorm/blockstates/stellar_singularity_reactor.json");
        Path reactorBlockModel = Path.of("src/main/resources/assets/sandstorm/models/block/stellar_singularity_reactor.json");
        Path reactorLitModel = Path.of("src/main/resources/assets/sandstorm/models/block/stellar_singularity_reactor_lit.json");
        Path reactorItemModel = Path.of("src/main/resources/assets/sandstorm/models/item/stellar_singularity_reactor.json");
        Path reactorItemDef = Path.of("src/main/resources/assets/sandstorm/items/stellar_singularity_reactor.json");

        Path reactorLoot = Path.of("src/main/resources/data/sandstorm/loot_table/blocks/stellar_singularity_reactor.json");
        Path reactorRecipe = Path.of("src/main/resources/data/sandstorm/recipe/stellar_singularity_reactor.json");

        assertTrue(Files.exists(itemDef), "warden_singularity_core item def missing");
        assertTrue(Files.exists(itemModel), "warden_singularity_core item model missing");
        assertTrue(Files.exists(itemTex), "warden_singularity_core texture missing");

        assertTrue(Files.exists(eggDef), "warden_android_spawn_egg item def missing");
        assertTrue(Files.exists(eggModel), "warden_android_spawn_egg item model missing");
        assertTrue(Files.exists(eggTex), "warden_android_spawn_egg texture missing");

        assertTrue(Files.exists(entityTex), "warden_android texture missing");
        assertTrue(Files.exists(entityDeactTex), "warden_android_deactivated texture missing");

        assertTrue(Files.exists(reactorBlockstate), "stellar_singularity_reactor blockstate missing");
        assertTrue(Files.exists(reactorBlockModel), "stellar_singularity_reactor block model missing");
        assertTrue(Files.exists(reactorLitModel), "stellar_singularity_reactor lit model missing");
        assertTrue(Files.exists(reactorItemModel), "stellar_singularity_reactor item model missing");
        assertTrue(Files.exists(reactorItemDef), "stellar_singularity_reactor item def missing");

        assertTrue(Files.exists(reactorLoot), "stellar_singularity_reactor loot table missing");
        assertTrue(Files.exists(reactorRecipe), "stellar_singularity_reactor recipe missing");
    }

    @Test
    public void testPickaxeMineableTagIncludesReactor() throws IOException {
        Path tagPath = Path.of("src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json");
        String content = Files.readString(tagPath);
        assertTrue(content.contains("sandstorm:stellar_singularity_reactor"), "pickaxe tag must include stellar_singularity_reactor");
    }

    @Test
    public void testZeroCommentsInNewClasses() throws IOException {
        String[] files = new String[]{
                "src/main/java/com/fhfelipefh/sandstorm/content/entity/WardenAndroidEntity.java",
                "src/client/java/com/fhfelipefh/sandstorm/client/renderer/WardenAndroidRenderState.java",
                "src/client/java/com/fhfelipefh/sandstorm/client/renderer/WardenAndroidModel.java",
                "src/client/java/com/fhfelipefh/sandstorm/client/renderer/WardenAndroidRenderer.java",
                "src/main/java/com/fhfelipefh/sandstorm/content/block/StellarSingularityReactorBlock.java",
                "src/main/java/com/fhfelipefh/sandstorm/content/block/entity/StellarSingularityReactorBlockEntity.java",
                "src/main/java/com/fhfelipefh/sandstorm/content/gui/StellarSingularityReactorMenu.java",
                "src/client/java/com/fhfelipefh/sandstorm/client/gui/StellarSingularityReactorScreen.java"
        };

        for (String filePath : files) {
            File f = new File(filePath);
            assertTrue(f.exists(), "Source file must exist: " + filePath);
            String content = Files.readString(f.toPath());
            assertTrue(!content.contains("//"), "No single-line comments allowed in " + filePath);
            assertTrue(!content.contains("/*"), "No multi-line comments allowed in " + filePath);
        }
    }

    @Test
    public void testVersionBumped() throws IOException {
        String content = Files.readString(Path.of("gradle.properties"));
        assertTrue(content.contains("version=1.19.66"), "Version must be bumped to 1.19.66");
    }
}

