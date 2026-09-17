package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ZeroCommentsArchitectureTest {

    private static final Pattern COMMENT_PATTERN = Pattern.compile("^\\s*(//|/\\*|\\*)");

    @Test
    void allSourceFilesMustHaveZeroComments() throws IOException {
        List<String> violations = new ArrayList<>();
        checkDirectory(Path.of("src", "main", "java"), violations);
        checkDirectory(Path.of("src", "test", "java"), violations);

        assertTrue(violations.isEmpty(), "Found prohibited comments in Java files:\n" + String.join("\n", violations));
    }

    private void checkDirectory(Path root, List<String> violations) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().endsWith(".java")).forEach(path -> {
                try {
                    List<String> lines = Files.readAllLines(path);
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
                        if (COMMENT_PATTERN.matcher(line).find()) {
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
