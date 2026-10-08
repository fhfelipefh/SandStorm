package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandNamespaceArchitectureTest {
    private static final Path COMMANDS_DIR = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "command"
    );
    private static final Pattern ROOT_COMMAND = Pattern.compile(
            "dispatcher\\.register\\(Commands\\.literal\\(\\\"([^\\\"]+)\\\""
    );

    @Test
    void everyModCommandMustUseTheSandstormPrefix() throws IOException {
        try (Stream<Path> files = Files.walk(COMMANDS_DIR)) {
            files.filter(path -> path.toString().endsWith(".java")).forEach(this::assertCommandRootsAreNamespaced);
        }
    }

    private void assertCommandRootsAreNamespaced(Path file) {
        try {
            String source = Files.readString(file);
            Matcher matcher = ROOT_COMMAND.matcher(source);
            while (matcher.find()) {
                assertTrue(matcher.group(1).startsWith("sandstorm"),
                        () -> file + " registers an unnamespaced command: " + matcher.group(1));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not inspect command source: " + file, exception);
        }
    }
}
