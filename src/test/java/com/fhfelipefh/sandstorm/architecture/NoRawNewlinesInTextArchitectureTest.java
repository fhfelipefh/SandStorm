package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NoRawNewlinesInTextArchitectureTest {

    private static final int MAX_SAFE_LINE_LENGTH = 50;
    private static final Pattern STRING_LITERAL_PATTERN = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
    private static final Pattern FORMAT_CODE_PATTERN = Pattern.compile("§.");

    @Test
    void allProductionFilesMustHaveZeroRawNewlines() throws IOException {
        List<String> violations = new ArrayList<>();
        checkNewlinesInDirectory(Path.of("src", "main", "java"), violations);
        checkNewlinesInDirectory(Path.of("src", "client", "java"), violations);
        checkNewlinesInDirectory(Path.of("src", "main", "resources", "assets", "sandstorm", "lang"), violations);

        assertTrue(violations.isEmpty(), "Found prohibited raw newline escape sequences (\\n or \\r) causing [LF] in Minecraft:\n" + String.join("\n", violations));
    }

    @Test
    void allClientUiStringsMustNotOverflowScreen() throws IOException {
        List<String> violations = new ArrayList<>();
        Path clientGuiPath = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "gui");
        if (Files.exists(clientGuiPath)) {
            try (Stream<Path> paths = Files.walk(clientGuiPath)) {
                paths.filter(p -> p.toString().endsWith(".java")).forEach(path -> {
                    try {
                        List<String> lines = Files.readAllLines(path);
                        for (int i = 0; i < lines.size(); i++) {
                            String line = lines.get(i);
                            if (line.contains("LOGGER") || line.contains("throw ") || line.contains("Identifier") || line.contains("@At") || line.contains("ResourceLocation")) {
                                continue;
                            }
                            Matcher matcher = STRING_LITERAL_PATTERN.matcher(line);
                            while (matcher.find()) {
                                String literal = matcher.group();
                                String inner = literal.substring(1, literal.length() - 1);
                                String clean = FORMAT_CODE_PATTERN.matcher(inner).replaceAll("");
                                if (clean.length() > MAX_SAFE_LINE_LENGTH) {
                                    violations.add(path + ":" + (i + 1) + " -> \"" + clean + "\" (length=" + clean.length() + " > " + MAX_SAFE_LINE_LENGTH + ")");
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        assertTrue(violations.isEmpty(), "Found GUI strings exceeding maximum safe length (" + MAX_SAFE_LINE_LENGTH + " chars). Long texts must be split into multiple lines using List<Component>:\n" + String.join("\n", violations));
    }

    private void checkNewlinesInDirectory(Path root, List<String> violations) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().endsWith(".java") || p.toString().endsWith(".json")).forEach(path -> {
                try {
                    List<String> lines = Files.readAllLines(path);
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
                        if (line.contains("\\n") || line.contains("\\r")) {
                            violations.add(path + ":" + (i + 1) + " -> " + line.trim());
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
