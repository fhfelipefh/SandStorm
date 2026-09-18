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

class NoUnusedImportsArchitectureTest {

    private static final Pattern IMPORT_PATTERN = Pattern.compile("^\\s*import\\s+(?:static\\s+)?([\\w.$]+);");

    @Test
    void allSourceFilesMustHaveZeroUnusedImports() throws IOException {
        List<String> violations = new ArrayList<>();
        checkDirectory(Path.of("src", "main", "java"), violations);
        checkDirectory(Path.of("src", "client", "java"), violations);
        checkDirectory(Path.of("src", "test", "java"), violations);

        assertTrue(violations.isEmpty(), "Found unused imports in Java files:\n" + String.join("\n", violations));
    }

    private void checkDirectory(Path root, List<String> violations) throws IOException {
        if (!Files.exists(root)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().endsWith(".java")).forEach(path -> {
                try {
                    checkFileForUnusedImports(path, violations);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void checkFileForUnusedImports(Path path, List<String> violations) throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<ImportEntry> imports = new ArrayList<>();
        List<String> bodyLines = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher matcher = IMPORT_PATTERN.matcher(line);
            if (matcher.find()) {
                String fullImport = matcher.group(1);
                int lastDot = fullImport.lastIndexOf('.');
                String simpleName = (lastDot >= 0) ? fullImport.substring(lastDot + 1) : fullImport;
                if (!simpleName.equals("*")) {
                    imports.add(new ImportEntry(i + 1, line.trim(), simpleName));
                }
            } else {
                bodyLines.add(line);
            }
        }

        String bodyText = String.join("\n", bodyLines);

        for (ImportEntry imp : imports) {
            Pattern usagePattern = Pattern.compile("\\b" + Pattern.quote(imp.simpleName()) + "\\b");
            if (!usagePattern.matcher(bodyText).find()) {
                violations.add(path + ":" + imp.lineNumber() + " -> " + imp.statement());
            }
        }
    }

    private record ImportEntry(int lineNumber, String statement, String simpleName) {
    }
}
