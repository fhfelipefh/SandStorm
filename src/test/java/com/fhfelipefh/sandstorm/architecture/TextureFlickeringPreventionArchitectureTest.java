package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextureFlickeringPreventionArchitectureTest {

    private static final Path MODELS_CLIENT_DIR = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer");
    private static final Path ITEM_MODELS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item");

    private static final Pattern BOX_PATTERN = Pattern.compile(
            "addBox\\s*\\(\\s*(-?\\d+(?:\\.\\d+)?f?)\\s*,\\s*(-?\\d+(?:\\.\\d+)?f?)\\s*,\\s*(-?\\d+(?:\\.\\d+)?f?)\\s*,\\s*(-?\\d+(?:\\.\\d+)?f?)\\s*,\\s*(-?\\d+(?:\\.\\d+)?f?)\\s*,\\s*(-?\\d+(?:\\.\\d+)?f?)\\s*\\)"
    );

    private static final Pattern PART_PATTERN = Pattern.compile(
            "addOrReplaceChild\\s*\\(\\s*\"([^\"]+)\"\\s*,\\s*CubeListBuilder\\.create\\(\\)(.*?PartPose\\.offset\\s*\\(\\s*(-?\\d+(?:\\.\\d+)?f?)\\s*,\\s*(-?\\d+(?:\\.\\d+)?f?)\\s*,\\s*(-?\\d+(?:\\.\\d+)?f?)\\s*\\))",
            Pattern.DOTALL
    );

    private record ModelBox(float minX, float maxX, float minY, float maxY, float minZ, float maxZ, String partName) {
        boolean overlaps1D(float min1, float max1, float min2, float max2) {
            return Math.max(min1, min2) < (Math.min(max1, max2) - 0.05f);
        }

        boolean hasCoplanarFace(ModelBox other) {
            if (Math.abs(this.minY - other.minY) < 0.01f && overlaps1D(this.minX, this.maxX, other.minX, other.maxX) && overlaps1D(this.minZ, this.maxZ, other.minZ, other.maxZ)) {
                return true;
            }
            if (Math.abs(this.maxY - other.maxY) < 0.01f && overlaps1D(this.minX, this.maxX, other.minX, other.maxX) && overlaps1D(this.minZ, this.maxZ, other.minZ, other.maxZ)) {
                return true;
            }
            if (Math.abs(this.minX - other.minX) < 0.01f && overlaps1D(this.minY, this.maxY, other.minY, other.maxY) && overlaps1D(this.minZ, this.maxZ, other.minZ, other.maxZ)) {
                return true;
            }
            if (Math.abs(this.maxX - other.maxX) < 0.01f && overlaps1D(this.minY, this.maxY, other.minY, other.maxY) && overlaps1D(this.minZ, this.maxZ, other.minZ, other.maxZ)) {
                return true;
            }
            if (Math.abs(this.minZ - other.minZ) < 0.01f && overlaps1D(this.minX, this.maxX, other.minX, other.maxX) && overlaps1D(this.minY, this.maxY, other.minY, other.maxY)) {
                return true;
            }
            if (Math.abs(this.maxZ - other.maxZ) < 0.01f && overlaps1D(this.minX, this.maxX, other.minX, other.maxX) && overlaps1D(this.minY, this.maxY, other.minY, other.maxY)) {
                return true;
            }
            return false;
        }
    }

    private record OffsetKey(float x, float y, float z) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OffsetKey offsetKey = (OffsetKey) o;
            return Math.abs(offsetKey.x - x) < 0.01f && Math.abs(offsetKey.y - y) < 0.01f && Math.abs(offsetKey.z - z) < 0.01f;
        }

        @Override
        public int hashCode() {
            return Objects.hash(Math.round(x * 10.0f), Math.round(y * 10.0f), Math.round(z * 10.0f));
        }
    }

    private static float parseNumber(String val) {
        return Float.parseFloat(val.replace("f", "").trim());
    }

    @Test
    void cyberSteedModelMustHaveZeroCoplanarFacesAcrossAllParts() throws IOException {
        Path steedModel = MODELS_CLIENT_DIR.resolve("CyberSteedModel.java");
        assertTrue(Files.exists(steedModel), "CyberSteedModel.java must exist");
        String content = Files.readString(steedModel);

        Matcher partMatcher = PART_PATTERN.matcher(content);
        Map<OffsetKey, List<ModelBox>> boxesByOffset = new HashMap<>();

        while (partMatcher.find()) {
            String partName = partMatcher.group(1);
            String chunk = partMatcher.group(2);
            float ox = parseNumber(partMatcher.group(3));
            float oy = parseNumber(partMatcher.group(4));
            float oz = parseNumber(partMatcher.group(5));
            OffsetKey key = new OffsetKey(ox, oy, oz);

            Matcher boxMatcher = BOX_PATTERN.matcher(chunk);
            while (boxMatcher.find()) {
                float x = parseNumber(boxMatcher.group(1));
                float y = parseNumber(boxMatcher.group(2));
                float z = parseNumber(boxMatcher.group(3));
                float w = parseNumber(boxMatcher.group(4));
                float h = parseNumber(boxMatcher.group(5));
                float l = parseNumber(boxMatcher.group(6));
                boxesByOffset.computeIfAbsent(key, k -> new ArrayList<>())
                        .add(new ModelBox(x, x + w, y, y + h, z, z + l, partName));
            }
        }

        List<String> flickeringViolations = new ArrayList<>();
        for (Map.Entry<OffsetKey, List<ModelBox>> entry : boxesByOffset.entrySet()) {
            List<ModelBox> list = entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    ModelBox b1 = list.get(i);
                    ModelBox b2 = list.get(j);
                    if (b1.hasCoplanarFace(b2)) {
                        flickeringViolations.add(b1.partName() + " vs " + b2.partName());
                    }
                }
            }
        }

        assertTrue(flickeringViolations.isEmpty(), "Coplanar flickering detected in CyberSteedModel: " + flickeringViolations);
    }

    @Test
    void dorsalAttachmentsMustNeverBeFlushWithTorsoSurface() throws IOException {
        Path steedModel = MODELS_CLIENT_DIR.resolve("CyberSteedModel.java");
        assertTrue(Files.exists(steedModel), "CyberSteedModel.java must exist");
        String content = Files.readString(steedModel);

        Matcher partMatcher = PART_PATTERN.matcher(content);
        List<ModelBox> dorsalBoxes = new ArrayList<>();
        float torsoTopY = -5.0f;

        while (partMatcher.find()) {
            String partName = partMatcher.group(1);
            if (!partName.equals("body") && !partName.equals("fuel_canister") && !partName.equals("sonic_turret") && !partName.equals("battery_pack")) {
                continue;
            }
            String chunk = partMatcher.group(2);
            Matcher boxMatcher = BOX_PATTERN.matcher(chunk);
            while (boxMatcher.find()) {
                float x = parseNumber(boxMatcher.group(1));
                float y = parseNumber(boxMatcher.group(2));
                float z = parseNumber(boxMatcher.group(3));
                float w = parseNumber(boxMatcher.group(4));
                float h = parseNumber(boxMatcher.group(5));
                float l = parseNumber(boxMatcher.group(6));
                dorsalBoxes.add(new ModelBox(x, x + w, y, y + h, z, z + l, partName));
            }
        }

        for (ModelBox box : dorsalBoxes) {
            if (box.partName().equals("body") && box.minX() == -5.0f && box.maxX() == 5.0f) {
                continue;
            }
            boolean flushBottom = Math.abs(box.maxY() - torsoTopY) < 0.05f;
            assertFalse(flushBottom, "Dorsal element must not sit flush on torso top face: " + box.partName());
        }
    }

    @Test
    void allModelBoxesMustHavePositiveDimensionsAndNonZeroVolume() throws IOException {
        List<String> zeroVolumeBoxes = new ArrayList<>();
        try (Stream<Path> stream = Files.list(MODELS_CLIENT_DIR)) {
            List<Path> modelFiles = stream.filter(p -> p.getFileName().toString().endsWith("Model.java")).toList();
            for (Path modelFile : modelFiles) {
                String content = Files.readString(modelFile);
                Matcher matcher = BOX_PATTERN.matcher(content);
                while (matcher.find()) {
                    float w = parseNumber(matcher.group(4));
                    float h = parseNumber(matcher.group(5));
                    float l = parseNumber(matcher.group(6));
                    if (w <= 0.0f || h <= 0.0f || l <= 0.0f) {
                        zeroVolumeBoxes.add(modelFile.getFileName() + " has zero dimension: " + w + "x" + h + "x" + l);
                    }
                }
            }
        }

        assertTrue(zeroVolumeBoxes.isEmpty(), "Found model boxes with zero or negative volume: " + zeroVolumeBoxes);
    }

    @Test
    void allClientModelsMustUsePowerOfTwoLayerDimensions() throws IOException {
        Pattern layerPattern = Pattern.compile("LayerDefinition\\.create\\s*\\(\\s*\\w+\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
        List<String> invalidLayerDimensions = new ArrayList<>();

        try (Stream<Path> stream = Files.list(MODELS_CLIENT_DIR)) {
            List<Path> modelFiles = stream.filter(p -> p.getFileName().toString().endsWith("Model.java")).toList();
            for (Path modelFile : modelFiles) {
                String content = Files.readString(modelFile);
                Matcher matcher = layerPattern.matcher(content);
                if (matcher.find()) {
                    int width = Integer.parseInt(matcher.group(1));
                    int height = Integer.parseInt(matcher.group(2));
                    boolean validW = (width > 0) && ((width & (width - 1)) == 0);
                    boolean validH = (height > 0) && ((height & (height - 1)) == 0);
                    if (!validW || !validH) {
                        invalidLayerDimensions.add(modelFile.getFileName() + " dimensions: " + width + "x" + height);
                    }
                }
            }
        }

        assertTrue(invalidLayerDimensions.isEmpty(), "Models with non power-of-two texture dimensions: " + invalidLayerDimensions);
    }

    @Test
    void railItemModelsMustNeverInheritFlatBlockModels() throws IOException {
        try (Stream<Path> stream = Files.list(ITEM_MODELS_DIR)) {
            List<Path> railItemModels = stream.filter(p -> p.getFileName().toString().endsWith("_rail.json")).toList();
            assertFalse(railItemModels.isEmpty(), "Rail item models must be present");
            for (Path railModel : railItemModels) {
                String jsonStr = Files.readString(railModel);
                JsonObject obj = JsonParser.parseString(jsonStr).getAsJsonObject();
                assertTrue(obj.has("parent"), "Rail model must have parent: " + railModel.getFileName());
                String parent = obj.get("parent").getAsString();
                assertTrue(parent.equals("minecraft:item/generated") || parent.equals("item/generated"),
                        "Rail item must inherit from generated: " + railModel.getFileName());
            }
        }
    }
}
