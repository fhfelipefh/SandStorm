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

class NoInlineImportsArchitectureTest {

    private static final Pattern INLINE_IMPORT_PATTERN = Pattern.compile(
            "\\b(com|net|org|java|javax|io|dev)\\.([a-z0-9_]+\\.)+[A-Z][a-zA-Z0-9_$]*\\b"
    );

    private static final Pattern STRING_LITERAL_PATTERN = Pattern.compile(
            "\"(\\\\.|[^\"])*\""
    );

    @Test
    void allSourceFilesMustHaveZeroInlineImports() throws IOException {
        List<String> violations = new ArrayList<>();
        checkDirectory(Path.of("src", "main", "java"), violations);
        checkDirectory(Path.of("src", "client", "java"), violations);
        checkDirectory(Path.of("src", "test", "java"), violations);

        assertTrue(violations.isEmpty(), "Found prohibited inline imports in Java files:\n" + String.join("\n", violations));
    }

    private void checkDirectory(Path root, List<String> violations) throws IOException {
        if (!Files.exists(root)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().endsWith(".java")).forEach(path -> {
                try {
                    checkFileForInlineImports(path, violations);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void checkFileForInlineImports(Path path, List<String> violations) throws IOException {
        List<String> lines = Files.readAllLines(path);

        for (int i = 0; i < lines.size(); i++) {
            String rawLine = lines.get(i);
            String trimmed = rawLine.trim();

            if (trimmed.startsWith("package ") || trimmed.startsWith("import ")) {
                continue;
            }

            if (trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("*")) {
                continue;
            }

            String sanitized = STRING_LITERAL_PATTERN.matcher(rawLine).replaceAll("\"\"");
            Matcher matcher = INLINE_IMPORT_PATTERN.matcher(sanitized);

            while (matcher.find()) {
                violations.add(path + ":" + (i + 1) + " -> " + matcher.group());
            }
        }
    }
}
