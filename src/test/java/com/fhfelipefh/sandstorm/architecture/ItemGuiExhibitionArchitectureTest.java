package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemGuiExhibitionArchitectureTest {

    private static final Path ITEMS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SandStormItems.java");
    private static final Path BLOCKS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");
    private static final Path ASSETS_ITEMS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "items");
    private static final Path ASSETS_MODELS_ITEM_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item");
    private static final Path ASSETS_MODELS_BLOCK_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block");

    private static final Pattern REGISTER_PATTERN = Pattern.compile("public\\s+static\\s+final\\s+\\w+\\s+(\\w+)\\s*=\\s*register\\(\"([^\"]+)\"");
    private static final Pattern TAB_DISPLAY_PATTERN = Pattern.compile("\\.displayItems\\(\\s*\\(context,\\s*entries\\)\\s*->\\s*\\{(.*?)\\}\\s*\\)", Pattern.DOTALL);

    @Test
    void everyRegisteredItemAndBlockMustBeExhibitedInCreativeTab() throws IOException {
        assertTrue(Files.exists(ITEMS_JAVA));
        assertTrue(Files.exists(BLOCKS_JAVA));

        String itemsSource = Files.readString(ITEMS_JAVA);
        String blocksSource = Files.readString(BLOCKS_JAVA);

        Matcher tabMatcher = TAB_DISPLAY_PATTERN.matcher(itemsSource);
        assertTrue(tabMatcher.find(), "Could not find displayItems in SANDSTORM_TAB");
        String tabContent = tabMatcher.group(1);

        List<String> violations = new ArrayList<>();

        Matcher itemMatcher = REGISTER_PATTERN.matcher(itemsSource);
        while (itemMatcher.find()) {
            String fieldName = itemMatcher.group(1);
            if ("SANDSTORM_TAB".equals(fieldName)) {
                continue;
            }
            String expectedCall = "entries.accept(" + fieldName + ")";
            if (!tabContent.contains(expectedCall)) {
                violations.add("Item field '" + fieldName + "' is not exhibited in creative tab (missing " + expectedCall + ")");
            }
        }

        Matcher blockMatcher = REGISTER_PATTERN.matcher(blocksSource);
        while (blockMatcher.find()) {
            String fieldName = blockMatcher.group(1);
            String expectedCall = "entries.accept(SandStormBlocks." + fieldName + ")";
            if (!tabContent.contains(expectedCall)) {
                violations.add("Block field '" + fieldName + "' is not exhibited in creative tab (missing " + expectedCall + ")");
            }
        }

        assertTrue(violations.isEmpty(), "Found items/blocks missing from creative tab:\n" + String.join("\n", violations));
    }

    @Test
    void everyRegisteredItemAndBlockMustHaveDefinitionInAssetsItems() throws IOException {
        List<String> violations = new ArrayList<>();
        String itemsSource = Files.readString(ITEMS_JAVA);
        String blocksSource = Files.readString(BLOCKS_JAVA);

        Matcher itemMatcher = REGISTER_PATTERN.matcher(itemsSource);
        while (itemMatcher.find()) {
            String pathName = itemMatcher.group(2);
            Path defFile = ASSETS_ITEMS_DIR.resolve(pathName + ".json");
            if (!Files.exists(defFile)) {
                violations.add("Item '" + pathName + "' lacks 1.21.4 definition file: " + defFile);
            }
        }

        Matcher blockMatcher = REGISTER_PATTERN.matcher(blocksSource);
        while (blockMatcher.find()) {
            String pathName = blockMatcher.group(2);
            Path defFile = ASSETS_ITEMS_DIR.resolve(pathName + ".json");
            if (!Files.exists(defFile)) {
                violations.add("Block item '" + pathName + "' lacks 1.21.4 definition file: " + defFile);
            }
        }

        assertTrue(violations.isEmpty(), "Found missing item definitions in assets/sandstorm/items:\n" + String.join("\n", violations));
    }

    @Test
    void railItemModelsMustNotDirectlyInheritFromFlatRailBlocks() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(ASSETS_MODELS_ITEM_DIR)) {
            return;
        }

        try (var stream = Files.list(ASSETS_MODELS_ITEM_DIR)) {
            stream.filter(p -> {
                String name = p.getFileName().toString();
                return name.endsWith("_rail.json") || name.equals("rail.json");
            }).forEach(modelPath -> {
                try (FileReader reader = new FileReader(modelPath.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    if (parsed.isJsonObject()) {
                        JsonObject obj = parsed.getAsJsonObject();
                        if (obj.has("parent")) {
                            String parent = obj.get("parent").getAsString();
                            if (parent.contains("rail_flat") || parent.startsWith("sandstorm:block/")) {
                                violations.add(modelPath.getFileName() + " inherits from flat block model '" + parent + "' which is invisible in GUI. Rail items must inherit from 'minecraft:item/generated'.");
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(), "Found rail item models inheriting from flat block models:\n" + String.join("\n", violations));
    }

    @Test
    void everyItemDefinitionMustResolveToAnExistingModel() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(ASSETS_ITEMS_DIR)) {
            return;
        }

        try (var stream = Files.list(ASSETS_ITEMS_DIR)) {
            stream.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try (FileReader reader = new FileReader(path.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    if (parsed.isJsonObject()) {
                        JsonObject root = parsed.getAsJsonObject();
                        if (root.has("model") && root.get("model").isJsonObject()) {
                            JsonObject modelObj = root.getAsJsonObject("model");
                            if (modelObj.has("model")) {
                                String modelRef = modelObj.get("model").getAsString();
                                if (modelRef.startsWith("sandstorm:item/")) {
                                    String relative = modelRef.substring("sandstorm:item/".length()) + ".json";
                                    Path target = ASSETS_MODELS_ITEM_DIR.resolve(relative);
                                    if (!Files.exists(target)) {
                                        violations.add(path.getFileName() + " -> references missing item model: " + target);
                                    }
                                } else if (modelRef.startsWith("sandstorm:block/")) {
                                    String relative = modelRef.substring("sandstorm:block/".length()) + ".json";
                                    Path target = ASSETS_MODELS_BLOCK_DIR.resolve(relative);
                                    if (!Files.exists(target)) {
                                        violations.add(path.getFileName() + " -> references missing block model: " + target);
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(), "Found item definitions referencing non-existent models:\n" + String.join("\n", violations));
    }
}
