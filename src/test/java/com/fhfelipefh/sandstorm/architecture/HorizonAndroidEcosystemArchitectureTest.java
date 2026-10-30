package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HorizonAndroidEcosystemArchitectureTest {

    private static final byte[] PNG_HEADER = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    private static final List<String> HORIZON_MOBS = List.of(
            "grazer",
            "fanghorn",
            "frostclaw",
            "glinthawk",
            "waterwing"
    );

    @Test
    void allHorizonEntitiesMustImplementDeactivatableAndroid() throws IOException {
        Path entityDir = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity");
        for (String mob : HORIZON_MOBS) {
            String className = Character.toUpperCase(mob.charAt(0)) + mob.substring(1) + "Entity.java";
            Path file = entityDir.resolve(className);
            assertTrue(Files.exists(file), "Entity file missing: " + className);

            String content = Files.readString(file);
            assertTrue(content.contains("implements DeactivatableAndroid") || content.contains(", DeactivatableAndroid"),
                    className + " must implement DeactivatableAndroid");
            assertTrue(content.contains("isDeactivated()"),
                    className + " must implement isDeactivated()");
            assertTrue(content.contains("setDeactivated("),
                    className + " must implement setDeactivated()");
        }
    }

    @Test
    void allHorizonEntitiesMustBeRegisteredInSandStormEntities() throws IOException {
        Path entitiesFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "SandStormEntities.java");
        assertTrue(Files.exists(entitiesFile));
        String content = Files.readString(entitiesFile);

        for (String mob : HORIZON_MOBS) {
            String upper = mob.toUpperCase();
            assertTrue(content.contains("public static final ResourceKey<EntityType<?>> " + upper + "_KEY"),
                    "Missing ResourceKey for: " + mob);
            assertTrue(content.contains("public static final EntityType<"),
                    "Missing EntityType declaration in SandStormEntities");
            assertTrue(content.contains("FabricDefaultAttributeRegistry.register(" + upper + ","),
                    "Missing attribute registration for: " + upper);
        }
    }

    @Test
    void allHorizonSpawnEggsMustBeRegisteredInSandStormItemsAndCreativeTab() throws IOException {
        Path itemsFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SandStormItems.java");
        assertTrue(Files.exists(itemsFile));
        String content = Files.readString(itemsFile);

        for (String mob : HORIZON_MOBS) {
            String upper = mob.toUpperCase();
            String eggField = upper + "_SPAWN_EGG";
            assertTrue(content.contains(eggField), "Missing spawn egg field: " + eggField);
            assertTrue(content.contains("entries.accept(" + eggField + ")"),
                    "Spawn egg " + eggField + " must be accepted in creative tab");
        }
    }

    @Test
    void allHorizonRenderersAndModelsMustExistAndBeRegisteredInClient() throws IOException {
        Path rendererDir = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer");
        Path clientFile = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "SandStormClient.java");
        assertTrue(Files.exists(clientFile));
        String clientContent = Files.readString(clientFile);

        for (String mob : HORIZON_MOBS) {
            String prefix = Character.toUpperCase(mob.charAt(0)) + mob.substring(1);
            Path rendererPath = rendererDir.resolve(prefix + "Renderer.java");
            Path modelPath = rendererDir.resolve(prefix + "Model.java");
            Path statePath = rendererDir.resolve(prefix + "RenderState.java");

            assertTrue(Files.exists(rendererPath), "Missing renderer: " + rendererPath);
            assertTrue(Files.exists(modelPath), "Missing model: " + modelPath);
            assertTrue(Files.exists(statePath), "Missing render state: " + statePath);

            assertTrue(clientContent.contains("EntityRendererRegistry.register(SandStormEntities." + mob.toUpperCase() + ", " + prefix + "Renderer::new);"),
                    "Renderer for " + mob + " must be registered in SandStormClient");
        }
    }

    @Test
    void allHorizonTexturesAndPngHeadersMustBeValid() throws IOException {
        Path texRoot = Path.of("src", "main", "resources", "assets", "sandstorm", "textures");

        for (String mob : HORIZON_MOBS) {
            Path activeTex = texRoot.resolve("entity").resolve(mob).resolve(mob + ".png");
            Path deactTex = texRoot.resolve("entity").resolve(mob).resolve(mob + "_deactivated.png");
            Path eggTex = texRoot.resolve("item").resolve(mob + "_spawn_egg.png");

            assertValidPng(activeTex);
            assertValidPng(deactTex);
            assertValidPng(eggTex);
        }
    }

    private void assertValidPng(Path path) throws IOException {
        assertTrue(Files.exists(path), "Missing PNG texture: " + path);
        assertTrue(Files.size(path) >= 8, "PNG file too small: " + path);
        byte[] header = new byte[8];
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            assertEquals(8, fis.read(header));
            assertArrayEquals(PNG_HEADER, header, "Invalid PNG signature in: " + path);
        }
    }

    @Test
    void allHorizonRecipesAndItemDefinitionsMustBeValidJson() throws IOException {
        Path assetsItemsDir = Path.of("src", "main", "resources", "assets", "sandstorm", "items");
        Path modelsItemDir = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item");
        Path recipeDir = Path.of("src", "main", "resources", "data", "sandstorm", "recipe");

        for (String mob : HORIZON_MOBS) {
            String itemName = mob + "_spawn_egg";
            Path itemDef = assetsItemsDir.resolve(itemName + ".json");
            Path itemModel = modelsItemDir.resolve(itemName + ".json");
            Path recipeFile = recipeDir.resolve(itemName + ".json");

            assertTrue(Files.exists(itemDef), "Missing item def: " + itemDef);
            assertTrue(Files.exists(itemModel), "Missing model: " + itemModel);
            assertTrue(Files.exists(recipeFile), "Missing recipe: " + recipeFile);

            try (FileReader reader = new FileReader(itemDef.toFile())) {
                JsonElement elem = JsonParser.parseReader(reader);
                assertTrue(elem.isJsonObject());
            }
            try (FileReader reader = new FileReader(itemModel.toFile())) {
                JsonElement elem = JsonParser.parseReader(reader);
                assertTrue(elem.isJsonObject());
            }
            try (FileReader reader = new FileReader(recipeFile.toFile())) {
                JsonElement elem = JsonParser.parseReader(reader);
                assertTrue(elem.isJsonObject());
                JsonObject obj = elem.getAsJsonObject();
                assertTrue(obj.has("type"));
                assertTrue(obj.has("result"));
                assertEquals("sandstorm:" + itemName, obj.getAsJsonObject("result").get("id").getAsString());
            }
        }
    }

    @Test
    void allHorizonEntitiesMustBeLocalizedInAllLanguages() throws IOException {
        Path langDir = Path.of("src", "main", "resources", "assets", "sandstorm", "lang");
        List<String> langs = List.of("en_us.json", "es_es.json", "pt_br.json");

        for (String lang : langs) {
            Path file = langDir.resolve(lang);
            assertTrue(Files.exists(file), "Missing lang file: " + lang);
            try (FileReader reader = new FileReader(file.toFile())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (String mob : HORIZON_MOBS) {
                    String entityKey = "entity.sandstorm." + mob;
                    String deactKey = "entity.sandstorm." + mob + ".deactivated";
                    String eggKey = "item.sandstorm." + mob + "_spawn_egg";

                    assertTrue(json.has(entityKey), "Missing " + entityKey + " in " + lang);
                    assertTrue(json.has(deactKey), "Missing " + deactKey + " in " + lang);
                    assertTrue(json.has(eggKey), "Missing " + eggKey + " in " + lang);

                    assertNotNull(json.get(entityKey).getAsString());
                    assertNotNull(json.get(deactKey).getAsString());
                    assertNotNull(json.get(eggKey).getAsString());
                }
            }
        }
    }

    @Test
    void allHorizonAndroidsMustBeDeactivatableByEmp() throws IOException {
        Path empHandlerFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "EmpParalysisHandler.java");
        assertTrue(Files.exists(empHandlerFile), "EmpParalysisHandler.java must exist");
        String content = Files.readString(empHandlerFile);
        assertTrue(content.contains("entity instanceof DeactivatableAndroid"), "EMP handler must target DeactivatableAndroid");
        assertTrue(content.contains("android.setDeactivated(true)"), "EMP paralysis must deactivate androids");
        assertTrue(content.contains("android.setDeactivated(false)"), "EMP recovery must reactivate androids");
    }
}

