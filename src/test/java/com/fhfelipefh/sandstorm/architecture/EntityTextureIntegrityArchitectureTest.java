package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
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
import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityTextureIntegrityArchitectureTest {

    private static final Path SRC_TEXTURES_ENTITY = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity");
    private static final Path BUILD_TEXTURES_ENTITY = Path.of("build", "resources", "main", "assets", "sandstorm", "textures", "entity");
    private static final Path CLIENT_RENDERERS_DIR = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer");

    private static final byte[] PNG_SIGNATURE = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    private static final Pattern TEXTURE_REF_PATTERN = Pattern.compile("SandStormMod\\.id\\(\\\"textures/entity/([^\\\"]+\\.png)\\\"\\)");

    @Test
    void everyEntityRendererMustReferenceExistingValidPng() throws IOException {
        assertTrue(Files.exists(CLIENT_RENDERERS_DIR), "Renderers directory must exist");
        List<Path> rendererFiles = collectJavaFiles(CLIENT_RENDERERS_DIR);
        assertFalse(rendererFiles.isEmpty(), "Client renderers must not be empty");

        int validatedReferences = 0;
        for (Path rendererFile : rendererFiles) {
            String source = Files.readString(rendererFile);
            Matcher matcher = TEXTURE_REF_PATTERN.matcher(source);
            while (matcher.find()) {
                String relativePng = matcher.group(1);
                Path pngPath = SRC_TEXTURES_ENTITY.resolve(relativePng);
                assertTrue(Files.exists(pngPath), "Entity texture referenced in " + rendererFile.getFileName() + " must exist: " + pngPath);
                assertValidPngFile(pngPath);
                validatedReferences++;
            }
        }
        assertTrue(validatedReferences >= 20, "Must have validated at least 20 entity texture references across renderers");
    }

    @Test
    void everyEntityTextureOnDiskMustBeValidNonCorruptedPng() throws IOException {
        assertTrue(Files.exists(SRC_TEXTURES_ENTITY), "Entity textures directory must exist");
        List<Path> pngFiles = collectPngFiles(SRC_TEXTURES_ENTITY);
        assertFalse(pngFiles.isEmpty(), "Entity textures must not be empty");

        for (Path png : pngFiles) {
            assertValidPngFile(png);
        }
    }

    @Test
    void everyCyberneticEntityMustHaveMatchingDeactivatedTexture() throws IOException {
        assertTrue(Files.exists(SRC_TEXTURES_ENTITY));

        Set<String> cyberneticMobs = Set.of(
                "fanghorn",
                "frostclaw",
                "glinthawk",
                "grazer",
                "waterwing",
                "cyber_steed",
                "cyber_hound",
                "crawler_drone",
                "scout_drone",
                "laborer_unit",
                "scrap_sentinel",
                "derelict_automaton"
        );

        for (String mob : cyberneticMobs) {
            Path mobDir = SRC_TEXTURES_ENTITY.resolve(mob);
            if (Files.exists(mobDir)) {
                boolean hasDeactivated = false;
                try (Stream<Path> stream = Files.list(mobDir)) {
                    hasDeactivated = stream.anyMatch(p -> p.getFileName().toString().contains("deactivated") && p.toString().endsWith(".png"));
                }
                assertTrue(hasDeactivated, "Cybernetic mob " + mob + " must have a _deactivated.png variant for EMP compliance");
            }
        }
    }

    @Test
    void everySourceEntityTextureMustBeSynchronizedInBuildOutputWhenBuildDirExists() throws IOException {
        if (!Files.exists(BUILD_TEXTURES_ENTITY)) {
            return;
        }

        List<Path> srcPngs = collectPngFiles(SRC_TEXTURES_ENTITY);
        for (Path srcPng : srcPngs) {
            Path relative = SRC_TEXTURES_ENTITY.relativize(srcPng);
            Path buildPng = BUILD_TEXTURES_ENTITY.resolve(relative);
            assertTrue(Files.exists(buildPng), "Build directory is missing texture: " + buildPng);
            assertValidPngFile(buildPng);
        }
    }

    private void assertValidPngFile(Path pngPath) throws IOException {
        byte[] header = new byte[8];
        try (FileInputStream fis = new FileInputStream(pngPath.toFile())) {
            int read = fis.read(header);
            assertTrue(read >= 8, "File too small to be a PNG: " + pngPath);
            assertArrayEquals(PNG_SIGNATURE, header, "Invalid PNG signature in " + pngPath);
        }

        BufferedImage img = ImageIO.read(pngPath.toFile());
        assertNotNull(img, "ImageIO failed to parse PNG: " + pngPath);

        int w = img.getWidth();
        int h = img.getHeight();
        assertTrue(w > 0 && h > 0, "Dimensions must be positive: " + pngPath);
        assertTrue((w & (w - 1)) == 0, "Width must be power of two: " + pngPath + " (" + w + ")");
        assertTrue((h & (h - 1)) == 0, "Height must be power of two: " + pngPath + " (" + h + ")");

        Set<Integer> uniqueColors = new HashSet<>();
        int visiblePixels = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;
                if (alpha > 0) {
                    visiblePixels++;
                    uniqueColors.add(rgb);
                }
            }
        }
        assertTrue(visiblePixels > 0, "Texture must have visible pixels: " + pngPath);
        assertTrue(uniqueColors.size() >= 2, "Texture must not be completely flat single-color fill: " + pngPath);
    }

    private List<Path> collectJavaFiles(Path dir) throws IOException {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(dir)) {
            stream.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".java")).forEach(files::add);
        }
        return files;
    }

    private List<Path> collectPngFiles(Path dir) throws IOException {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(dir)) {
            stream.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".png")).forEach(files::add);
        }
        return files;
    }
}
