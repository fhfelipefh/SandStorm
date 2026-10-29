package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityModelPartsConsistencyArchitectureTest {

    private static final Path MODELS_SOURCE_DIR = Path.of("src", "client", "java");
    private static final Path CLIENT_CLASSES_DIR = Path.of("build", "classes", "java", "client");
    private static final Pattern GET_CHILD_PATTERN = Pattern.compile("\\.getChild\\s*\\(\\s*\"([^\"]+)\"\\s*\\)");
    private static final Pattern ADD_CHILD_PATTERN = Pattern.compile("addOrReplaceChild\\s*\\(\\s*\"([^\"]+)\"");

    @Test
    void allModelGetChildCallsMustMatchDefinedPartNames() throws IOException {
        try (Stream<Path> stream = Files.walk(MODELS_SOURCE_DIR)) {
            var modelFiles = stream.filter(p -> p.getFileName().toString().endsWith("Model.java")).toList();
            assertTrue(modelFiles.size() >= 30, "Expected at least 30 model files");

            for (Path modelFile : modelFiles) {
                String content = Files.readString(modelFile);
                Set<String> addedParts = new HashSet<>();
                Matcher addMatcher = ADD_CHILD_PATTERN.matcher(content);
                while (addMatcher.find()) {
                    addedParts.add(addMatcher.group(1));
                }

                Matcher getMatcher = GET_CHILD_PATTERN.matcher(content);
                while (getMatcher.find()) {
                    String requestedPart = getMatcher.group(1);
                    assertTrue(addedParts.contains(requestedPart),
                            "Model " + modelFile.getFileName() + " requests part '" + requestedPart
                                    + "' via getChild(), but it is never registered with addOrReplaceChild()!");
                }
            }
        }
    }

    @Test
    void allClientModelsMustBakeAndInstantiateSuccessfullyWithoutException() throws Exception {
        assertTrue(Files.exists(CLIENT_CLASSES_DIR), "Compiled client classes directory must exist");

        URL[] urls = new URL[]{CLIENT_CLASSES_DIR.toUri().toURL()};
        try (URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader())) {
            Path rendererDir = CLIENT_CLASSES_DIR.resolve(Path.of("com", "fhfelipefh", "sandstorm", "client", "renderer"));
            assertTrue(Files.exists(rendererDir), "Renderer directory must exist");

            try (Stream<Path> stream = Files.list(rendererDir)) {
                var modelClasses = stream
                        .filter(p -> p.getFileName().toString().endsWith("Model.class"))
                        .map(p -> {
                            String fName = p.getFileName().toString();
                            return "com.fhfelipefh.sandstorm.client.renderer." + fName.substring(0, fName.length() - 6);
                        })
                        .toList();

                assertTrue(modelClasses.size() >= 25, "Expected at least 25 compiled model classes");

                for (String className : modelClasses) {
                    Class<?> clazz = loader.loadClass(className);
                    Method createBodyLayer = null;
                    try {
                        createBodyLayer = clazz.getMethod("createBodyLayer");
                    } catch (NoSuchMethodException ignored) {
                    }

                    if (createBodyLayer != null && Modifier.isStatic(createBodyLayer.getModifiers())) {
                        Object layerDef = createBodyLayer.invoke(null);
                        assertNotNull(layerDef, "createBodyLayer must not return null for " + className);

                        Method bakeRootMethod = layerDef.getClass().getMethod("bakeRoot");
                        Object modelPartRoot = bakeRootMethod.invoke(layerDef);
                        assertNotNull(modelPartRoot, "bakeRoot must not return null for " + className);

                        Constructor<?> ctor = null;
                        for (Constructor<?> c : clazz.getConstructors()) {
                            if (c.getParameterCount() == 1 && c.getParameterTypes()[0].getSimpleName().equals("ModelPart")) {
                                ctor = c;
                                break;
                            }
                        }

                        assertNotNull(ctor, "Model class " + className + " must have a constructor taking ModelPart");
                        Object instance = ctor.newInstance(modelPartRoot);
                        assertNotNull(instance, "Failed to instantiate model " + className);
                    }
                }
            }
        }
    }
}
