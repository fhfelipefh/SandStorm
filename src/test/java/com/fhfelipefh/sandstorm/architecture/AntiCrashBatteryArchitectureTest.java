package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AntiCrashBatteryArchitectureTest {

    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data");
    private static final Path MENUS_DIR = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "gui");
    private static final Path BLOCK_ENTITIES_DIR = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "entity");
    private static final Path STORAGE_DIR = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "storage");
    private static final Path CLIENT_DIR = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client");
    private static final Path BLOCKS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");
    private static final Path ITEMS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SandStormItems.java");
    private static final Path ENTITIES_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "SandStormEntities.java");

    private static final Pattern REGISTER_ID_PATTERN = Pattern.compile(
            "register\\(\"([a-z0-9_]+)\""
    );

    private static final Pattern CHECK_CONTAINER_DATA_PATTERN = Pattern.compile(
            "checkContainerDataCount\\s*\\(\\s*data\\s*,\\s*(\\d+|[A-Z0-9_]+)\\s*\\)"
    );

    private static final Pattern CHECK_CONTAINER_SIZE_PATTERN = Pattern.compile(
            "checkContainerSize\\s*\\(\\s*container\\s*,\\s*(\\d+|[A-Z0-9_]+)\\s*\\)"
    );

    private static final Pattern CONSTANT_DEF_PATTERN = Pattern.compile(
            "(?:public|private|protected)\\s+static\\s+final\\s+int\\s+([A-Z0-9_]+)\\s*=\\s*(\\d+)\\s*;"
    );

    private static final Pattern GET_COUNT_PATTERN = Pattern.compile(
            "public\\s+int\\s+getCount\\s*\\(\\s*\\)\\s*\\{\\s*return\\s*(\\d+)\\s*;\\s*\\}"
    );

    private static final Pattern ENTITY_TYPE_FIELD_PATTERN = Pattern.compile(
            "public\\s+static\\s+final\\s+EntityType<([A-Za-z0-9_]+)>\\s+([A-Z0-9_]+)\\s*="
    );

    private static final Pattern ENTITY_ATTRIBUTE_REG_PATTERN = Pattern.compile(
            "FabricDefaultAttributeRegistry\\.register\\s*\\(\\s*([A-Z0-9_]+)\\s*,"
    );

    private static final Pattern BE_TYPE_FIELD_PATTERN = Pattern.compile(
            "public\\s+static\\s+final\\s+BlockEntityType<[^>]+>\\s+([A-Z0-9_]+)\\s*=\\s*Registry\\.register\\s*\\(\\s*BuiltInRegistries\\.BLOCK_ENTITY_TYPE\\s*,\\s*SandStormMod\\.id\\(\"([^\"]+)\"\\)\\s*,\\s*new\\s+BlockEntityType<[^>]*>\\s*\\([^,]+,\\s*Set\\.of\\(([^)]+)\\)\\s*\\)\\s*\\);"
    );

    private static final Pattern ENTITY_RENDERER_REGISTER_PATTERN = Pattern.compile(
            "EntityRendererRegistry\\.register\\s*\\(\\s*SandStormEntities\\.([A-Z0-9_]+)\\s*,"
    );

    private static final Pattern BLOCK_ENTITY_RENDERER_REGISTER_PATTERN = Pattern.compile(
            "BlockEntityRendererRegistry\\.register\\s*\\(\\s*SandStormBlocks\\.([A-Z0-9_]+)\\s*,"
    );

    @Test
    void allTagFilesMustOnlyReferenceRegisteredIdentifiers() throws IOException {
        Set<String> registeredKeys = getRegisteredSandstormKeys();
        List<String> violations = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(DATA_DIR)) {
            List<Path> tagFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().contains("tags") && p.toString().endsWith(".json"))
                    .toList();

            assertFalse(tagFiles.isEmpty(), "Tag files must exist in data directory");

            for (Path tagFile : tagFiles) {
                try (FileReader reader = new FileReader(tagFile.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    if (!parsed.isJsonObject()) {
                        continue;
                    }
                    JsonObject obj = parsed.getAsJsonObject();
                    if (!obj.has("values")) {
                        continue;
                    }
                    JsonArray values = obj.getAsJsonArray("values");
                    for (JsonElement elem : values) {
                        if (!elem.isJsonPrimitive()) {
                            continue;
                        }
                        String val = elem.getAsString();
                        if (val.startsWith("#")) {
                            continue;
                        }
                        if (val.startsWith("sandstorm:")) {
                            String path = val.substring("sandstorm:".length());
                            if (tagFile.toString().contains("world_preset")) {
                                Path presetPath = DATA_DIR.resolve("sandstorm").resolve("worldgen").resolve("world_preset").resolve(path + ".json");
                                if (!Files.exists(presetPath)) {
                                    violations.add("Tag " + tagFile + " references unknown world preset: " + val);
                                }
                            } else if (!registeredKeys.contains(path)) {
                                violations.add("Tag " + tagFile + " references unknown sandstorm identifier: " + val);
                            }
                        }
                    }
                }
            }
        }

        assertTrue(violations.isEmpty(), "Found tag references to unknown registry keys:\n" + String.join("\n", violations));
    }

    @Test
    void allLivingEntityTypesMustHaveRegisteredAttributes() throws IOException {
        String content = Files.readString(ENTITIES_JAVA);
        List<String> violations = new ArrayList<>();

        Set<String> registeredAttributeEntities = new HashSet<>();
        Matcher attrMatcher = ENTITY_ATTRIBUTE_REG_PATTERN.matcher(content);
        while (attrMatcher.find()) {
            registeredAttributeEntities.add(attrMatcher.group(1));
        }

        Matcher entityMatcher = ENTITY_TYPE_FIELD_PATTERN.matcher(content);
        while (entityMatcher.find()) {
            String entityClass = entityMatcher.group(1);
            String fieldName = entityMatcher.group(2);

            if (isLivingEntityClass(entityClass)) {
                if (!registeredAttributeEntities.contains(fieldName)) {
                    violations.add("LivingEntity type " + fieldName + " (" + entityClass + ") missing attribute supplier registration in SandStormEntities.initialize()");
                }
            }
        }

        assertTrue(violations.isEmpty(), "Found living entity types without registered attributes:\n" + String.join("\n", violations));
    }

    @Test
    void allBlockEntityTypesMustHaveValidBlocksAndRegistrations() throws IOException {
        String content = Files.readString(BLOCKS_JAVA);
        List<String> violations = new ArrayList<>();

        Set<String> declaredBlocks = new HashSet<>();
        Matcher blockMatcher = Pattern.compile("public\\s+static\\s+final\\s+[A-Za-z0-9_]+\\s+([A-Z0-9_]+)\\s*=\\s*register\\(").matcher(content);
        while (blockMatcher.find()) {
            declaredBlocks.add(blockMatcher.group(1));
        }

        Matcher beMatcher = BE_TYPE_FIELD_PATTERN.matcher(content);
        int count = 0;
        while (beMatcher.find()) {
            count++;
            String beFieldName = beMatcher.group(1);
            String blockArgs = beMatcher.group(3).trim();

            if (blockArgs.isEmpty()) {
                violations.add("BlockEntityType " + beFieldName + " has empty valid blocks set");
                continue;
            }

            String[] blocks = blockArgs.split(",");
            for (String blockRef : blocks) {
                String trimmed = blockRef.trim();
                if (!declaredBlocks.contains(trimmed)) {
                    violations.add("BlockEntityType " + beFieldName + " references unknown block: " + trimmed);
                }
            }
        }

        assertTrue(count > 0, "SandStormBlocks must declare at least one BlockEntityType");
        assertTrue(violations.isEmpty(), "Found invalid BlockEntityType declarations:\n" + String.join("\n", violations));
    }

    @Test
    void allContainerDataMenusMustMatchBlockEntityDataCount() throws IOException {
        List<String> violations = new ArrayList<>();

        try (Stream<Path> stream = Files.list(MENUS_DIR)) {
            List<Path> menuFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith("Menu.java"))
                    .toList();

            for (Path menuFile : menuFiles) {
                String content = Files.readString(menuFile);
                Matcher matcher = CHECK_CONTAINER_DATA_PATTERN.matcher(content);
                if (matcher.find()) {
                    String countToken = matcher.group(1);
                    int requiredCount = resolveInt(countToken, content);

                    assertTrue(requiredCount > 0, "Required container data count must be resolved for " + menuFile.getFileName());

                    String menuSimpleName = menuFile.getFileName().toString().replace(".java", "");
                    String baseName = menuSimpleName.replace("Menu", "");
                    if (isEntityMenu(baseName)) {
                        continue;
                    }
                    Path bePath = findBlockEntityFile(baseName);

                    if (bePath == null) {
                        violations.add("Could not find matching BlockEntity source for " + menuSimpleName);
                        continue;
                    }

                    String beContent = Files.readString(bePath);
                    int beCount = extractGetCount(beContent);
                    if (beCount < 0 && beContent.contains("extends BaseMachineBlockEntity")) {
                        beCount = 6;
                    }

                    if (beCount < 0) {
                        violations.add("Could not resolve getCount() in " + bePath.getFileName() + " for " + menuSimpleName);
                    } else if (beCount < requiredCount) {
                        violations.add("ContainerData count mismatch in " + menuSimpleName + ": menu requires "
                                + requiredCount + " but " + bePath.getFileName() + " provides " + beCount);
                    }
                }
            }
        }

        assertTrue(violations.isEmpty(), "Found menu/block entity container data count contract violations:\n" + String.join("\n", violations));
    }

    @Test
    void allContainerSizeMenusMustMatchBlockEntityContainerSize() throws IOException {
        List<String> violations = new ArrayList<>();

        try (Stream<Path> stream = Files.list(MENUS_DIR)) {
            List<Path> menuFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith("Menu.java"))
                    .toList();

            for (Path menuFile : menuFiles) {
                String content = Files.readString(menuFile);
                Matcher matcher = CHECK_CONTAINER_SIZE_PATTERN.matcher(content);
                if (matcher.find()) {
                    String countToken = matcher.group(1);
                    int requiredSize = resolveInt(countToken, content);

                    assertTrue(requiredSize > 0, "Required container size must be resolved for " + menuFile.getFileName());

                    String menuSimpleName = menuFile.getFileName().toString().replace(".java", "");
                    String baseName = menuSimpleName.replace("Menu", "");
                    if (isEntityMenu(baseName)) {
                        continue;
                    }
                    Path bePath = findBlockEntityFile(baseName);

                    if (bePath == null) {
                        violations.add("Could not find matching BlockEntity source for " + menuSimpleName);
                        continue;
                    }

                    String beContent = Files.readString(bePath);
                    int beSize = extractContainerSize(beContent);

                    if (beSize > 0 && beSize < requiredSize) {
                        violations.add("Container size mismatch in " + menuSimpleName + ": menu requires "
                                + requiredSize + " but " + bePath.getFileName() + " provides " + beSize);
                    }
                }
            }
        }

        assertTrue(violations.isEmpty(), "Found menu/block entity container size violations:\n" + String.join("\n", violations));
    }

    @Test
    void allClientRenderersMustReferenceRegisteredEntitiesAndBlocks() throws IOException {
        Path clientFile = CLIENT_DIR.resolve("SandStormClient.java");
        assertTrue(Files.exists(clientFile), "SandStormClient.java must exist");

        String content = Files.readString(clientFile);
        List<String> violations = new ArrayList<>();

        String entitiesContent = Files.readString(ENTITIES_JAVA);
        Set<String> declaredEntities = new HashSet<>();
        Matcher entityFieldMatcher = Pattern.compile("public\\s+static\\s+final\\s+EntityType<[^>]+>\\s+([A-Z0-9_]+)\\s*=").matcher(entitiesContent);
        while (entityFieldMatcher.find()) {
            declaredEntities.add(entityFieldMatcher.group(1));
        }

        Matcher entityMatcher = ENTITY_RENDERER_REGISTER_PATTERN.matcher(content);
        while (entityMatcher.find()) {
            String entityName = entityMatcher.group(1);
            if (!declaredEntities.contains(entityName)) {
                violations.add("SandStormClient registers renderer for undeclared entity: " + entityName);
            }
        }

        String blocksContent = Files.readString(BLOCKS_JAVA);
        Set<String> declaredBlockEntityTypes = new HashSet<>();
        Matcher beFieldMatcher = Pattern.compile("public\\s+static\\s+final\\s+BlockEntityType<[^>]+>\\s+([A-Z0-9_]+)\\s*=").matcher(blocksContent);
        while (beFieldMatcher.find()) {
            declaredBlockEntityTypes.add(beFieldMatcher.group(1));
        }

        Matcher blockEntityMatcher = BLOCK_ENTITY_RENDERER_REGISTER_PATTERN.matcher(content);
        while (blockEntityMatcher.find()) {
            String beName = blockEntityMatcher.group(1);
            if (!declaredBlockEntityTypes.contains(beName)) {
                violations.add("SandStormClient registers renderer for undeclared block entity type: " + beName);
            }
        }

        assertTrue(violations.isEmpty(), "Found client renderer registration violations:\n" + String.join("\n", violations));
    }

    @Test
    void allRecipesMustHaveValidOutputsAndIngredients() throws IOException {
        Path recipesDir = DATA_DIR.resolve("sandstorm").resolve("recipe");
        assertTrue(Files.exists(recipesDir), "Recipe directory must exist");

        Set<String> registeredKeys = getRegisteredSandstormKeys();
        List<String> violations = new ArrayList<>();

        try (Stream<Path> stream = Files.list(recipesDir)) {
            List<Path> recipeFiles = stream.filter(p -> p.toString().endsWith(".json")).toList();
            for (Path recipeFile : recipeFiles) {
                try (FileReader reader = new FileReader(recipeFile.toFile())) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    if (!json.has("type")) {
                        violations.add("Recipe missing type in " + recipeFile.getFileName());
                        continue;
                    }
                    if (json.has("result")) {
                        JsonObject result = json.getAsJsonObject("result");
                        if (!result.has("id")) {
                            violations.add("Recipe result missing id in " + recipeFile.getFileName());
                        } else {
                            String resultId = result.get("id").getAsString();
                            if (resultId.startsWith("sandstorm:")) {
                                String path = resultId.substring("sandstorm:".length());
                                if (!registeredKeys.contains(path)) {
                                    violations.add("Recipe result references unregistered sandstorm item: " + resultId + " in " + recipeFile.getFileName());
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(violations.isEmpty(), "Found invalid recipe definitions:\n" + String.join("\n", violations));
    }

    @Test
    void allOverriddenRecipesMustNotUseFailingLoadConditions() throws IOException {
        List<String> violations = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(DATA_DIR)) {
            List<Path> recipeFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json") && p.getParent() != null && "recipe".equals(p.getParent().getFileName().toString()))
                    .toList();

            for (Path recipeFile : recipeFiles) {
                try (FileReader reader = new FileReader(recipeFile.toFile())) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    if (json.has("fabric:load_conditions")) {
                        violations.add("Recipe " + recipeFile + " uses fabric:load_conditions which causes unbound registry values in Minecraft 1.21.4");
                    }
                }
            }
        }

        assertTrue(violations.isEmpty(), "Found recipes with fabric:load_conditions that cause registry unbinding:\n" + String.join("\n", violations));
    }

    private Set<String> getRegisteredSandstormKeys() throws IOException {
        Set<String> keys = new HashSet<>();
        String itemsContent = Files.readString(ITEMS_JAVA);
        Matcher itemMatcher = REGISTER_ID_PATTERN.matcher(itemsContent);
        while (itemMatcher.find()) {
            keys.add(itemMatcher.group(1));
        }

        String blocksContent = Files.readString(BLOCKS_JAVA);
        Matcher blockMatcher = REGISTER_ID_PATTERN.matcher(blocksContent);
        while (blockMatcher.find()) {
            keys.add(blockMatcher.group(1));
        }
        return keys;
    }

    private boolean isLivingEntityClass(String entityClass) {
        if ("NutrientBombEntity".equals(entityClass) || "MedBaySeatEntity".equals(entityClass)) {
            return false;
        }
        return true;
    }

    private boolean isEntityMenu(String baseName) {
        return "CyborgTelemetry".equals(baseName) || "NomadScavenger".equals(baseName);
    }

    private Path findBlockEntityFile(String baseName) {
        if ("QuantumController".equals(baseName)) {
            return STORAGE_DIR.resolve("QuantumNetworkControllerBlockEntity.java");
        }

        String[] possibleNames = new String[]{
                baseName + "BlockEntity.java",
                baseName + "GeneratorBlockEntity.java",
                baseName + "PodBlockEntity.java",
                baseName + "VatBlockEntity.java"
        };

        for (String name : possibleNames) {
            Path p1 = BLOCK_ENTITIES_DIR.resolve(name);
            if (Files.exists(p1)) {
                return p1;
            }
            Path p2 = STORAGE_DIR.resolve(name);
            if (Files.exists(p2)) {
                return p2;
            }
        }
        return null;
    }

    private int resolveInt(String token, String content) {
        if (token.matches("\\d+")) {
            return Integer.parseInt(token);
        }
        Matcher constMatcher = CONSTANT_DEF_PATTERN.matcher(content);
        while (constMatcher.find()) {
            if (constMatcher.group(1).equals(token)) {
                return Integer.parseInt(constMatcher.group(2));
            }
        }
        return -1;
    }

    private int extractGetCount(String content) {
        Matcher matcher = GET_COUNT_PATTERN.matcher(content);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    private int extractContainerSize(String content) {
        Matcher sizeMatcher = Pattern.compile("public\\s+static\\s+final\\s+int\\s+(?:CONTAINER_SIZE|SLOT_COUNT)\\s*=\\s*(\\d+)\\s*;").matcher(content);
        if (sizeMatcher.find()) {
            return Integer.parseInt(sizeMatcher.group(1));
        }
        Matcher listMatcher = Pattern.compile("NonNullList\\.withSize\\s*\\(\\s*(\\d+|[A-Z0-9_]+)\\s*,").matcher(content);
        if (listMatcher.find()) {
            return resolveInt(listMatcher.group(1), content);
        }
        return -1;
    }
}
