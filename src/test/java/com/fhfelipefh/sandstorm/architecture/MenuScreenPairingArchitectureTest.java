package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuScreenPairingArchitectureTest {

    private static final Pattern MENU_FIELD_PATTERN = Pattern.compile(
            "public\\s+static\\s+final\\s+MenuType<[^>]+>\\s+([A-Z0-9_]+)\\s*="
    );

    private static final Pattern SCREEN_REGISTRATION_PATTERN = Pattern.compile(
            "MenuScreens\\.register\\(SandStormMenus\\.([A-Z0-9_]+),\\s*([A-Za-z0-9_]+)::new\\)"
    );

    @Test
    void allRegisteredMenusMustHaveMatchingClientScreens() throws IOException {
        Path menusFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "gui", "SandStormMenus.java");
        Path clientFile = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "SandStormClient.java");
        Path screensDir = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "gui");

        assertTrue(Files.exists(menusFile), "SandStormMenus.java must exist");
        assertTrue(Files.exists(clientFile), "SandStormClient.java must exist");
        assertTrue(Files.exists(screensDir), "Client screens directory must exist");

        String menusContent = Files.readString(menusFile);
        String clientContent = Files.readString(clientFile);

        Set<String> declaredMenus = new HashSet<>();
        Matcher menuMatcher = MENU_FIELD_PATTERN.matcher(menusContent);
        while (menuMatcher.find()) {
            declaredMenus.add(menuMatcher.group(1));
        }

        assertFalse(declaredMenus.isEmpty(), "SandStormMenus must declare at least one menu");

        Set<String> registeredMenus = new HashSet<>();
        List<String> violations = new ArrayList<>();
        Matcher clientMatcher = SCREEN_REGISTRATION_PATTERN.matcher(clientContent);
        while (clientMatcher.find()) {
            String menuName = clientMatcher.group(1);
            String screenClass = clientMatcher.group(2);
            registeredMenus.add(menuName);

            if (!declaredMenus.contains(menuName)) {
                violations.add("SandStormClient registers unknown menu: " + menuName);
            }

            Path screenFilePath = screensDir.resolve(screenClass + ".java");
            if (!Files.exists(screenFilePath)) {
                violations.add("Screen class file not found: " + screenFilePath);
            } else {
                String screenContent = Files.readString(screenFilePath);
                boolean isContainerScreen = screenContent.contains("extends AbstractContainerScreen") || screenContent.contains("extends BaseMachineScreen");
                if (!isContainerScreen) {
                    violations.add("Screen class " + screenClass + " must extend AbstractContainerScreen or BaseMachineScreen");
                }
            }
        }

        for (String menu : declaredMenus) {
            if (!registeredMenus.contains(menu)) {
                violations.add("Menu declared in SandStormMenus missing client screen registration: " + menu);
            }
        }

        assertTrue(violations.isEmpty(), "Found menu/screen pairing violations:\n" + String.join("\n", violations));
    }
}
