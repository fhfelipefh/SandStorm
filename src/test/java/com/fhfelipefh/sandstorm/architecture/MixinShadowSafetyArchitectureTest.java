package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MixinShadowSafetyArchitectureTest {

    private static final Path MAIN_MIXINS_JSON = Path.of("src", "main", "resources", "sandstorm.mixins.json");
    private static final Path CLIENT_MIXINS_JSON = Path.of("src", "client", "resources", "sandstorm.client.mixins.json");
    private static final Path MAIN_MIXIN_DIR = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "mixin");
    private static final Path CLIENT_MIXIN_DIR = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin");
    private static final Path ABSTRACT_HORSE_MIXIN = MAIN_MIXIN_DIR.resolve("AbstractHorseMixin.java");

    private static final Pattern MIXIN_TARGET_PATTERN = Pattern.compile("@Mixin\\s*\\(\\s*([A-Za-z0-9_]+)\\.class\\s*\\)");
    private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\s+([a-zA-Z0-9_.]+);");
    private static final Pattern SHADOW_METHOD_PATTERN = Pattern.compile("@Shadow(?:\\s*\\([^)]*\\))?\\s+(?:(?:public|protected|private|abstract|final|static)\\s+)*[\\w<>\\[\\],.?]+\\s+([a-zA-Z0-9_]+)\\s*\\(");
    private static final Pattern SHADOW_FIELD_PATTERN = Pattern.compile("@Shadow(?:\\s*\\([^)]*\\))?\\s+(?:(?:public|protected|private|final|static)\\s+)*[\\w<>\\[\\],.?]+\\s+([a-zA-Z0-9_]+)\\s*;");
    private static final Pattern SHADOW_ANY_IS_BABY = Pattern.compile("@Shadow(?:\\s*\\([^)]*\\))?\\s+[^;{}]+?\\bisBaby\\s*\\(");
    private static final Pattern SHADOW_ANY_IS_VEHICLE = Pattern.compile("@Shadow(?:\\s*\\([^)]*\\))?\\s+[^;{}]+?\\bisVehicle\\s*\\(");
    private static final Pattern SHADOW_ANY_IS_EQUIPPABLE = Pattern.compile("@Shadow(?:\\s*\\([^)]*\\))?\\s+[^;{}]+?\\bisEquippableInSlot\\s*\\(");

    @Test
    void allRegisteredMixinsMustPhysicallyExist() throws IOException {
        assertTrue(Files.exists(MAIN_MIXINS_JSON), "sandstorm.mixins.json must exist");
        assertTrue(Files.exists(CLIENT_MIXINS_JSON), "sandstorm.client.mixins.json must exist");

        List<Path> mainMixinFiles = getMixinFiles(MAIN_MIXINS_JSON, MAIN_MIXIN_DIR, "mixins");
        assertFalse(mainMixinFiles.isEmpty(), "Must register at least one server/common mixin");
        for (Path path : mainMixinFiles) {
            assertTrue(Files.exists(path), "Main mixin must exist: " + path);
        }

        List<Path> clientMixinFiles = getMixinFiles(CLIENT_MIXINS_JSON, CLIENT_MIXIN_DIR, "client");
        assertFalse(clientMixinFiles.isEmpty(), "Must register at least one client mixin");
        for (Path path : clientMixinFiles) {
            assertTrue(Files.exists(path), "Client mixin must exist: " + path);
        }
    }

    @Test
    void shadowMembersMustBeDeclaredDirectlyInTargetClass() throws Exception {
        List<Path> allMixinFiles = new ArrayList<>();
        allMixinFiles.addAll(getMixinFiles(MAIN_MIXINS_JSON, MAIN_MIXIN_DIR, "mixins"));
        allMixinFiles.addAll(getMixinFiles(CLIENT_MIXINS_JSON, CLIENT_MIXIN_DIR, "client"));

        for (Path mixinPath : allMixinFiles) {
            validateMixinShadowTargets(mixinPath);
        }
    }

    @Test
    void abstractHorseMixinMustNeverShadowSuperclassMethods() throws IOException {
        assertTrue(Files.exists(ABSTRACT_HORSE_MIXIN), "AbstractHorseMixin.java must exist");
        String content = Files.readString(ABSTRACT_HORSE_MIXIN);

        assertFalse(SHADOW_ANY_IS_BABY.matcher(content).find(),
                "AbstractHorseMixin must not shadow isBaby() as it belongs to AgeableMob");
        assertFalse(SHADOW_ANY_IS_VEHICLE.matcher(content).find(),
                "AbstractHorseMixin must not shadow isVehicle() as it belongs to Entity");
        assertFalse(SHADOW_ANY_IS_EQUIPPABLE.matcher(content).find(),
                "AbstractHorseMixin must not shadow isEquippableInSlot() as it belongs to LivingEntity");

        assertTrue(content.contains("doPlayerRide(Player player)"),
                "AbstractHorseMixin must shadow doPlayerRide which is declared on AbstractHorse");
        assertTrue(content.contains("sandstorm$priorityMountWhenRushing"),
                "AbstractHorseMixin must provide priority mount injection");
        assertTrue(content.contains("sandstorm$suppressInventoryScreenWhenMounting"),
                "AbstractHorseMixin must suppress inventory hijack when mounting");
    }

    private List<Path> getMixinFiles(Path jsonPath, Path sourceDir, String arrayKey) throws IOException {
        List<Path> result = new ArrayList<>();
        JsonObject json = JsonParser.parseString(Files.readString(jsonPath)).getAsJsonObject();
        if (json.has(arrayKey)) {
            JsonArray array = json.getAsJsonArray(arrayKey);
            for (JsonElement element : array) {
                result.add(sourceDir.resolve(element.getAsString() + ".java"));
            }
        }
        return result;
    }

    private void validateMixinShadowTargets(Path mixinPath) throws Exception {
        String content = Files.readString(mixinPath);
        Matcher targetMatcher = MIXIN_TARGET_PATTERN.matcher(content);
        if (!targetMatcher.find()) {
            return;
        }
        String simpleTargetName = targetMatcher.group(1);

        Map<String, String> imports = new HashMap<>();
        Matcher importMatcher = IMPORT_PATTERN.matcher(content);
        while (importMatcher.find()) {
            String full = importMatcher.group(1);
            int lastDot = full.lastIndexOf('.');
            String simple = lastDot >= 0 ? full.substring(lastDot + 1) : full;
            imports.put(simple, full);
        }

        String fullClassName = imports.get(simpleTargetName);
        if (fullClassName == null) {
            return;
        }

        Class<?> targetClass;
        try {
            targetClass = Class.forName(fullClassName, false, MixinShadowSafetyArchitectureTest.class.getClassLoader());
        } catch (ClassNotFoundException | LinkageError e) {
            return;
        }

        List<String> declaredMethodNames = Arrays.stream(targetClass.getDeclaredMethods())
                .map(Method::getName)
                .toList();
        List<String> declaredFieldNames = Arrays.stream(targetClass.getDeclaredFields())
                .map(Field::getName)
                .toList();

        Matcher shadowMethodMatcher = SHADOW_METHOD_PATTERN.matcher(content);
        while (shadowMethodMatcher.find()) {
            String methodName = shadowMethodMatcher.group(1);
            if (!declaredMethodNames.contains(methodName)) {
                boolean inSuperclass = Arrays.stream(targetClass.getMethods())
                        .anyMatch(m -> m.getName().equals(methodName));
                if (inSuperclass) {
                    fail("Mixin " + mixinPath.getFileName() + " shadows method '" + methodName
                            + "' which is declared in a superclass of " + targetClass.getSimpleName()
                            + ", not in " + targetClass.getSimpleName() + " itself. This causes InvalidMixinException at runtime!");
                } else {
                    fail("Mixin " + mixinPath.getFileName() + " shadows non-existent method '" + methodName
                            + "' on target " + targetClass.getSimpleName());
                }
            }
        }

        Matcher shadowFieldMatcher = SHADOW_FIELD_PATTERN.matcher(content);
        while (shadowFieldMatcher.find()) {
            String fieldName = shadowFieldMatcher.group(1);
            if (!declaredFieldNames.contains(fieldName)) {
                fail("Mixin " + mixinPath.getFileName() + " shadows field '" + fieldName
                        + "' not declared on target " + targetClass.getSimpleName());
            }
        }
    }
}
