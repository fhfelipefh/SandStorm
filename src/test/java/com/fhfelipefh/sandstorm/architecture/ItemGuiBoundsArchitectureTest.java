package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemGuiBoundsArchitectureTest {

    private static final Path ITEM_MODELS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item");

    @Test
    void allThreeDimensionalItemModelsMustFitInsideGuiSlot() throws IOException {
        assertTrue(Files.isDirectory(ITEM_MODELS_DIR), "Item models directory must exist");

        List<Path> modelFiles;
        try (Stream<Path> stream = Files.list(ITEM_MODELS_DIR)) {
            modelFiles = stream.filter(p -> p.toString().endsWith(".json")).sorted().toList();
        }

        for (Path modelFile : modelFiles) {
            JsonObject json = JsonParser.parseString(Files.readString(modelFile)).getAsJsonObject();
            if (!json.has("elements")) {
                continue;
            }

            String name = modelFile.getFileName().toString();
            assertTrue(json.has("display"), "3D item model must define display block: " + name);
            JsonObject display = json.getAsJsonObject("display");
            assertTrue(display.has("gui"), "3D item model must define display.gui transform: " + name);

            JsonObject gui = display.getAsJsonObject("gui");
            double[] rot = readVec3(gui, "rotation", 0.0);
            double[] trans = readVec3(gui, "translation", 0.0);
            double[] scale = readVec3(gui, "scale", 1.0);

            double minX = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;

            JsonArray elements = json.getAsJsonArray("elements");
            for (JsonElement elemEl : elements) {
                JsonObject elem = elemEl.getAsJsonObject();
                double[] from = readVec3(elem, "from", 0.0);
                double[] to = readVec3(elem, "to", 16.0);

                double[] elemOrigin = null;
                String elemAxis = null;
                double elemAngle = 0.0;
                if (elem.has("rotation")) {
                    JsonObject elemRot = elem.getAsJsonObject("rotation");
                    elemOrigin = readVec3(elemRot, "origin", 8.0);
                    elemAxis = elemRot.get("axis").getAsString();
                    elemAngle = elemRot.get("angle").getAsDouble();
                }

                for (int xi = 0; xi < 2; xi++) {
                    for (int yi = 0; yi < 2; yi++) {
                        for (int zi = 0; zi < 2; zi++) {
                            double px = xi == 0 ? from[0] : to[0];
                            double py = yi == 0 ? from[1] : to[1];
                            double pz = zi == 0 ? from[2] : to[2];

                            if (elemOrigin != null) {
                                double[] rotatedElem = rotateAroundAxis(
                                        px - elemOrigin[0],
                                        py - elemOrigin[1],
                                        pz - elemOrigin[2],
                                        elemAxis,
                                        elemAngle
                                );
                                px = rotatedElem[0] + elemOrigin[0];
                                py = rotatedElem[1] + elemOrigin[1];
                                pz = rotatedElem[2] + elemOrigin[2];
                            }

                            double sx = (px - 8.0) * scale[0];
                            double sy = (py - 8.0) * scale[1];
                            double sz = (pz - 8.0) * scale[2];

                            double[] v = rotateZ(rotateY(rotateX(sx, sy, sz, rot[0]), rot[1]), rot[2]);
                            double gx = v[0] + trans[0] + 8.0;
                            double gy = v[1] + trans[1] + 8.0;

                            minX = Math.min(minX, gx);
                            maxX = Math.max(maxX, gx);
                            minY = Math.min(minY, gy);
                            maxY = Math.max(maxY, gy);
                        }
                    }
                }
            }

            assertTrue(minX >= 0.0 && maxX <= 16.0,
                    "3D item model " + name + " overflows GUI slot horizontally: x=[" + minX + ", " + maxX + "]");
            assertTrue(minY >= 0.0 && maxY <= 16.0,
                    "3D item model " + name + " overflows GUI slot vertically: y=[" + minY + ", " + maxY + "]");
        }
    }

    private static double[] readVec3(JsonObject obj, String key, double defaultVal) {
        if (!obj.has(key)) {
            return new double[]{defaultVal, defaultVal, defaultVal};
        }
        JsonArray arr = obj.getAsJsonArray(key);
        return new double[]{arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), arr.get(2).getAsDouble()};
    }

    private static double[] rotateAroundAxis(double x, double y, double z, String axis, double angleDeg) {
        return switch (axis) {
            case "x" -> rotateX(x, y, z, angleDeg);
            case "y" -> rotateY(x, y, z, angleDeg);
            case "z" -> rotateZ(x, y, z, angleDeg);
            default -> new double[]{x, y, z};
        };
    }

    private static double[] rotateX(double x, double y, double z, double deg) {
        double rad = Math.toRadians(deg);
        double c = Math.cos(rad);
        double s = Math.sin(rad);
        return new double[]{x, y * c - z * s, y * s + z * c};
    }

    private static double[] rotateY(double x, double y, double z, double deg) {
        double rad = Math.toRadians(deg);
        double c = Math.cos(rad);
        double s = Math.sin(rad);
        return new double[]{x * c + z * s, y, -x * s + z * c};
    }

    private static double[] rotateZ(double x, double y, double z, double deg) {
        double rad = Math.toRadians(deg);
        double c = Math.cos(rad);
        double s = Math.sin(rad);
        return new double[]{x * c - y * s, x * s + y * c, z};
    }

    private static double[] rotateY(double[] v, double deg) {
        return rotateY(v[0], v[1], v[2], deg);
    }

    private static double[] rotateZ(double[] v, double deg) {
        return rotateZ(v[0], v[1], v[2], deg);
    }
}
