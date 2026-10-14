package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoorBlockstateArchitectureTest {

    private static final Path ASSETS_ROOT = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path BLOCKSTATES_DIR = ASSETS_ROOT.resolve("blockstates");
    private static final Path MODELS_BLOCK_DIR = ASSETS_ROOT.resolve("models").resolve("block");

    private static final Set<String> VANILLA_DOOR_BLOCKS = Set.of("morphing_alloy_door");

    private record ExpectedDoorVariant(String modelSuffix, Integer yRotation) {
    }

    private static final Map<String, ExpectedDoorVariant> EXPECTED_VARIANTS = Map.ofEntries(
            Map.entry("facing=east,half=lower,hinge=left,open=false", new ExpectedDoorVariant("_bottom_left", null)),
            Map.entry("facing=east,half=lower,hinge=left,open=true", new ExpectedDoorVariant("_bottom_left_open", 90)),
            Map.entry("facing=east,half=lower,hinge=right,open=false", new ExpectedDoorVariant("_bottom_right", null)),
            Map.entry("facing=east,half=lower,hinge=right,open=true", new ExpectedDoorVariant("_bottom_right_open", 270)),
            Map.entry("facing=east,half=upper,hinge=left,open=false", new ExpectedDoorVariant("_top_left", null)),
            Map.entry("facing=east,half=upper,hinge=left,open=true", new ExpectedDoorVariant("_top_left_open", 90)),
            Map.entry("facing=east,half=upper,hinge=right,open=false", new ExpectedDoorVariant("_top_right", null)),
            Map.entry("facing=east,half=upper,hinge=right,open=true", new ExpectedDoorVariant("_top_right_open", 270)),
            Map.entry("facing=north,half=lower,hinge=left,open=false", new ExpectedDoorVariant("_bottom_left", 270)),
            Map.entry("facing=north,half=lower,hinge=left,open=true", new ExpectedDoorVariant("_bottom_left_open", null)),
            Map.entry("facing=north,half=lower,hinge=right,open=false", new ExpectedDoorVariant("_bottom_right", 270)),
            Map.entry("facing=north,half=lower,hinge=right,open=true", new ExpectedDoorVariant("_bottom_right_open", 180)),
            Map.entry("facing=north,half=upper,hinge=left,open=false", new ExpectedDoorVariant("_top_left", 270)),
            Map.entry("facing=north,half=upper,hinge=left,open=true", new ExpectedDoorVariant("_top_left_open", null)),
            Map.entry("facing=north,half=upper,hinge=right,open=false", new ExpectedDoorVariant("_top_right", 270)),
            Map.entry("facing=north,half=upper,hinge=right,open=true", new ExpectedDoorVariant("_top_right_open", 180)),
            Map.entry("facing=south,half=lower,hinge=left,open=false", new ExpectedDoorVariant("_bottom_left", 90)),
            Map.entry("facing=south,half=lower,hinge=left,open=true", new ExpectedDoorVariant("_bottom_left_open", 180)),
            Map.entry("facing=south,half=lower,hinge=right,open=false", new ExpectedDoorVariant("_bottom_right", 90)),
            Map.entry("facing=south,half=lower,hinge=right,open=true", new ExpectedDoorVariant("_bottom_right_open", null)),
            Map.entry("facing=south,half=upper,hinge=left,open=false", new ExpectedDoorVariant("_top_left", 90)),
            Map.entry("facing=south,half=upper,hinge=left,open=true", new ExpectedDoorVariant("_top_left_open", 180)),
            Map.entry("facing=south,half=upper,hinge=right,open=false", new ExpectedDoorVariant("_top_right", 90)),
            Map.entry("facing=south,half=upper,hinge=right,open=true", new ExpectedDoorVariant("_top_right_open", null)),
            Map.entry("facing=west,half=lower,hinge=left,open=false", new ExpectedDoorVariant("_bottom_left", 180)),
            Map.entry("facing=west,half=lower,hinge=left,open=true", new ExpectedDoorVariant("_bottom_left_open", 270)),
            Map.entry("facing=west,half=lower,hinge=right,open=false", new ExpectedDoorVariant("_bottom_right", 180)),
            Map.entry("facing=west,half=lower,hinge=right,open=true", new ExpectedDoorVariant("_bottom_right_open", 90)),
            Map.entry("facing=west,half=upper,hinge=left,open=false", new ExpectedDoorVariant("_top_left", 180)),
            Map.entry("facing=west,half=upper,hinge=left,open=true", new ExpectedDoorVariant("_top_left_open", 270)),
            Map.entry("facing=west,half=upper,hinge=right,open=false", new ExpectedDoorVariant("_top_right", 180)),
            Map.entry("facing=west,half=upper,hinge=right,open=true", new ExpectedDoorVariant("_top_right_open", 90))
    );

    private static final Map<String, String> EXPECTED_MODEL_PARENTS = Map.of(
            "_bottom_left", "minecraft:block/door_bottom_left",
            "_bottom_left_open", "minecraft:block/door_bottom_left_open",
            "_bottom_right", "minecraft:block/door_bottom_right",
            "_bottom_right_open", "minecraft:block/door_bottom_right_open",
            "_top_left", "minecraft:block/door_top_left",
            "_top_left_open", "minecraft:block/door_top_left_open",
            "_top_right", "minecraft:block/door_top_right",
            "_top_right_open", "minecraft:block/door_top_right_open"
    );

    @Test
    void doorBlockstatesMustHaveThirtyTwoVariantsMatchingVanilla() throws IOException {
        for (String doorName : VANILLA_DOOR_BLOCKS) {
            Path blockstatePath = BLOCKSTATES_DIR.resolve(doorName + ".json");
            assertTrue(Files.exists(blockstatePath), "Blockstate must exist: " + blockstatePath);

            try (FileReader reader = new FileReader(blockstatePath.toFile())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                assertTrue(json.has("variants"), "Blockstate must have variants: " + doorName);
                JsonObject variants = json.getAsJsonObject("variants");
                assertEquals(32, variants.size(), "Door blockstate must define exactly 32 variants: " + doorName);

                for (Map.Entry<String, ExpectedDoorVariant> expected : EXPECTED_VARIANTS.entrySet()) {
                    String variantKey = expected.getKey();
                    assertTrue(variants.has(variantKey), "Missing variant " + variantKey + " in " + doorName);

                    JsonObject variantObj = variants.getAsJsonObject(variantKey);
                    assertTrue(variantObj.has("model"), "Variant " + variantKey + " must specify model");
                    String modelRef = variantObj.get("model").getAsString();
                    String expectedSuffix = expected.getValue().modelSuffix();
                    assertTrue(modelRef.endsWith(expectedSuffix),
                            "Variant " + variantKey + " model " + modelRef + " must end with " + expectedSuffix);

                    Integer expectedY = expected.getValue().yRotation();
                    if (expectedY == null) {
                        assertTrue(!variantObj.has("y") || variantObj.get("y").getAsInt() == 0,
                                "Variant " + variantKey + " should have y=0 or omitted");
                    } else {
                        assertTrue(variantObj.has("y"), "Variant " + variantKey + " missing y rotation");
                        assertEquals(expectedY.intValue(), variantObj.get("y").getAsInt(),
                                "Variant " + variantKey + " incorrect y rotation");
                    }
                }
            }
        }
    }

    @Test
    void doorModelsMustExistAndInheritFromVanillaDoorParents() throws IOException {
        for (String doorName : VANILLA_DOOR_BLOCKS) {
            for (Map.Entry<String, String> entry : EXPECTED_MODEL_PARENTS.entrySet()) {
                String suffix = entry.getKey();
                String expectedParent = entry.getValue();

                Path modelPath = MODELS_BLOCK_DIR.resolve(doorName + suffix + ".json");
                assertTrue(Files.exists(modelPath), "Door block model must exist: " + modelPath);

                try (FileReader reader = new FileReader(modelPath.toFile())) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    assertTrue(json.has("parent"), "Model " + modelPath + " must have parent");
                    assertEquals(expectedParent, json.get("parent").getAsString(),
                            "Model " + modelPath + " must inherit from " + expectedParent);
                }
            }
        }
    }

    @Test
    void doorItemsMustBeDoubleHighBlockItemInRegistration() throws IOException {
        Path sandStormBlocksPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");
        assertTrue(Files.exists(sandStormBlocksPath), "SandStormBlocks.java must exist");
        String content = Files.readString(sandStormBlocksPath);
        assertTrue(content.contains("DoubleHighBlockItem"), "SandStormBlocks must use DoubleHighBlockItem for DoorBlock registration");
        assertTrue(content.contains("registeredBlock instanceof DoorBlock"), "SandStormBlocks must check registeredBlock instanceof DoorBlock");
    }

    @Test
    void showcaseAndGalleryMustPlaceBothDoorHalves() throws IOException {
        Path showcasePath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseAutomation.java");
        assertTrue(Files.exists(showcasePath), "ShowcaseAutomation.java must exist");
        String showcaseContent = Files.readString(showcasePath);
        assertTrue(showcaseContent.contains("targetBlock instanceof DoorBlock"), "ShowcaseAutomation must check targetBlock instanceof DoorBlock");
        assertTrue(showcaseContent.contains("DoubleBlockHalf.LOWER"), "ShowcaseAutomation must set DoubleBlockHalf.LOWER for doors");
        assertTrue(showcaseContent.contains("DoubleBlockHalf.UPPER"), "ShowcaseAutomation must set DoubleBlockHalf.UPPER for doors");

        Path galleryPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ExhibitionGalleryManager.java");
        assertTrue(Files.exists(galleryPath), "ExhibitionGalleryManager.java must exist");
        String galleryContent = Files.readString(galleryPath);
        assertTrue(galleryContent.contains("block instanceof DoorBlock"), "ExhibitionGalleryManager must check block instanceof DoorBlock");
        assertTrue(galleryContent.contains("DoubleBlockHalf.LOWER"), "ExhibitionGalleryManager must set DoubleBlockHalf.LOWER for doors");
        assertTrue(galleryContent.contains("DoubleBlockHalf.UPPER"), "ExhibitionGalleryManager must set DoubleBlockHalf.UPPER for doors");
    }

    @Test
    void showcaseHologramsMustBeMarkersAndHaveNoBasePlate() throws IOException {
        Path showcasePath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseAutomation.java");
        assertTrue(Files.exists(showcasePath), "ShowcaseAutomation.java must exist");
        String content = Files.readString(showcasePath);
        assertTrue(content.contains("stand.setNoBasePlate(true);"), "ShowcaseAutomation must set noBasePlate for holograms");
        assertTrue(content.contains("ArmorStand.CLIENT_FLAG_MARKER"), "ShowcaseAutomation must set CLIENT_FLAG_MARKER for holograms");
    }
}
