package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTooltipLineLengthArchitectureTest {

    private static final int MAX_SAFE_LINE_LENGTH = 50;
    private static final Path LANG_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "lang");
    private static final Pattern FORMAT_CODE_PATTERN = Pattern.compile("§.");
    private static final Pattern STRING_LITERAL_PATTERN = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");

    @Test
    void allTooltipTranslationsMustNotExceedSafeLength() throws IOException {
        List<String> violations = new ArrayList<>();
        List<String> langFiles = List.of("pt_br.json", "en_us.json", "es_es.json");

        for (String langFile : langFiles) {
            Path path = LANG_DIR.resolve(langFile);
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    String key = entry.getKey();
                    if (isTooltipKey(key)) {
                        String raw = entry.getValue().getAsString();
                        String clean = FORMAT_CODE_PATTERN.matcher(raw).replaceAll("");
                        if (clean.length() > MAX_SAFE_LINE_LENGTH) {
                            violations.add(langFile + " [" + clean.length() + " chars] " + key + ": \"" + clean + "\"");
                        }
                    }
                }
            }
        }

        assertTrue(violations.isEmpty(), "Found tooltip entries exceeding " + MAX_SAFE_LINE_LENGTH + " characters:\n" + String.join("\n", violations));
    }

    @Test
    void allItemHoverTextStringLiteralsMustNotExceedSafeLength() throws IOException {
        List<String> violations = new ArrayList<>();
        Path itemDir = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item");
        if (Files.exists(itemDir)) {
            try (Stream<Path> paths = Files.walk(itemDir)) {
                paths.filter(p -> p.toString().endsWith(".java")).forEach(path -> {
                    try {
                        List<String> lines = Files.readAllLines(path);
                        boolean insideHover = false;
                        for (int i = 0; i < lines.size(); i++) {
                            String line = lines.get(i);
                            if (line.contains("appendHoverText")) {
                                insideHover = true;
                            }
                            if (insideHover) {
                                if (line.contains("}") && !line.contains("{")) {
                                    insideHover = false;
                                }
                                Matcher matcher = STRING_LITERAL_PATTERN.matcher(line);
                                while (matcher.find()) {
                                    String literal = matcher.group();
                                    String inner = literal.substring(1, literal.length() - 1);
                                    if (inner.startsWith("tooltip.sandstorm.") || inner.startsWith("item.sandstorm.") || inner.startsWith("block.sandstorm.") || inner.contains(":") || inner.contains("%")) {
                                        continue;
                                    }
                                    String clean = FORMAT_CODE_PATTERN.matcher(inner).replaceAll("");
                                    if (clean.length() > MAX_SAFE_LINE_LENGTH) {
                                        violations.add(path + ":" + (i + 1) + " [" + clean.length() + " chars] \"" + clean + "\"");
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        assertTrue(violations.isEmpty(), "Found hardcoded item hover text literals exceeding " + MAX_SAFE_LINE_LENGTH + " chars:\n" + String.join("\n", violations));
    }

    private boolean isTooltipKey(String key) {
        return key.startsWith("tooltip.sandstorm.")
                || key.endsWith(".desc")
                || key.endsWith(".desc1")
                || key.endsWith(".desc2")
                || key.contains(".desc_");
    }
}
