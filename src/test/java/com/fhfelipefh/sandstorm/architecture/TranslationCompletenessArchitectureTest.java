package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.item.CyberneticCommandUplinkItem;
import com.fhfelipefh.sandstorm.content.item.MolecularUpgradeItem;
import com.fhfelipefh.sandstorm.content.item.PharmacologicalStimItem;
import com.fhfelipefh.sandstorm.content.item.SuitUpgradeItem;
import com.fhfelipefh.sandstorm.content.megastructure.MegastructureBlueprint;
import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import com.fhfelipefh.sandstorm.content.satellite.SatelliteType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TranslationCompletenessArchitectureTest {

    private static final Path LANG_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "lang");
    private static final Path ITEMS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SandStormItems.java");
    private static final Path BLOCKS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");

    private static final Pattern REGISTER_PATTERN = Pattern.compile("register\\(\"([a-z0-9_]+)\"");
    private static final Pattern TRANSLATABLE_LITERAL_PATTERN = Pattern.compile("Component\\.translatable\\(\\s*\"([^\"]+)\"");

    @Test
    void allRegisteredItemsMustHaveTranslationsInAllLanguages() throws IOException {
        Map<String, JsonObject> langFiles = loadAllLanguages();
        String itemsSource = Files.readString(ITEMS_JAVA);
        Matcher matcher = REGISTER_PATTERN.matcher(itemsSource);
        Set<String> itemIds = new HashSet<>();
        while (matcher.find()) {
            itemIds.add(matcher.group(1));
        }

        assertFalse(itemIds.isEmpty(), "Registered items list must not be empty");

        List<String> violations = new ArrayList<>();
        for (String itemId : itemIds) {
            String expectedKey = "item.sandstorm." + itemId;
            checkKeyAcrossLanguages(expectedKey, langFiles, violations);
        }

        assertTrue(violations.isEmpty(), "Missing item translation keys:\n" + String.join("\n", violations));
    }

    @Test
    void allRegisteredBlocksMustHaveTranslationsInAllLanguages() throws IOException {
        Map<String, JsonObject> langFiles = loadAllLanguages();
        String blocksSource = Files.readString(BLOCKS_JAVA);
        Matcher matcher = REGISTER_PATTERN.matcher(blocksSource);
        Set<String> blockIds = new HashSet<>();
        while (matcher.find()) {
            blockIds.add(matcher.group(1));
        }

        assertFalse(blockIds.isEmpty(), "Registered blocks list must not be empty");

        List<String> violations = new ArrayList<>();
        for (String blockId : blockIds) {
            String expectedKey = "block.sandstorm." + blockId;
            checkKeyAcrossLanguages(expectedKey, langFiles, violations);
        }

        assertTrue(violations.isEmpty(), "Missing block translation keys:\n" + String.join("\n", violations));
    }

    @Test
    void allCodeLiteralTranslatableKeysMustExistInAllLanguages() throws IOException {
        Map<String, JsonObject> langFiles = loadAllLanguages();
        List<String> violations = new ArrayList<>();

        scanTranslatableLiteralsInDirectory(Path.of("src", "main", "java"), langFiles, violations);
        scanTranslatableLiteralsInDirectory(Path.of("src", "client", "java"), langFiles, violations);

        assertTrue(violations.isEmpty(), "Missing literal translation keys in language files:\n" + String.join("\n", violations));
    }

    @Test
    void allDynamicTranslationFamiliesMustExistInAllLanguages() throws IOException {
        Map<String, JsonObject> langFiles = loadAllLanguages();
        List<String> violations = new ArrayList<>();

        for (PharmacologicalStimItem.StimType stimType : PharmacologicalStimItem.StimType.values()) {
            checkKeyAcrossLanguages("tooltip.sandstorm.stim." + stimType.getId() + ".desc1", langFiles, violations);
            checkKeyAcrossLanguages("tooltip.sandstorm.stim." + stimType.getId() + ".desc2", langFiles, violations);
            checkKeyAcrossLanguages("tooltip.sandstorm.stim." + stimType.getId() + ".effect", langFiles, violations);
            checkKeyAcrossLanguages("tooltip.sandstorm.stim." + stimType.name().toLowerCase() + ".desc1", langFiles, violations);
            checkKeyAcrossLanguages("tooltip.sandstorm.stim." + stimType.name().toLowerCase() + ".desc2", langFiles, violations);
            checkKeyAcrossLanguages("tooltip.sandstorm.stim." + stimType.name().toLowerCase() + ".effect", langFiles, violations);
        }

        for (MolecularUpgradeItem.UpgradeType upgrade : MolecularUpgradeItem.UpgradeType.values()) {
            checkKeyAcrossLanguages("tooltip.sandstorm.molecular_upgrade." + upgrade.getId() + ".desc1", langFiles, violations);
            checkKeyAcrossLanguages("tooltip.sandstorm.molecular_upgrade." + upgrade.getId() + ".desc2", langFiles, violations);
            checkKeyAcrossLanguages("tooltip.sandstorm.molecular_upgrade." + upgrade.getId() + ".effect", langFiles, violations);
        }

        for (MolecularUpgradeItem.Category category : MolecularUpgradeItem.Category.values()) {
            checkKeyAcrossLanguages("tooltip.sandstorm.molecular_upgrade.category." + category.name().toLowerCase(), langFiles, violations);
        }

        for (SuitUpgradeItem.UpgradeType suitUpgrade : SuitUpgradeItem.UpgradeType.values()) {
            checkKeyAcrossLanguages(suitUpgrade.getTranslationKey(), langFiles, violations);
        }

        for (CyborgRoutine routine : CyborgRoutine.values()) {
            checkKeyAcrossLanguages("routine.sandstorm." + routine.getId(), langFiles, violations);
        }

        for (CyberneticCommandUplinkItem.UplinkMode mode : CyberneticCommandUplinkItem.UplinkMode.values()) {
            checkKeyAcrossLanguages("uplink.mode.sandstorm." + mode.getId(), langFiles, violations);
            checkKeyAcrossLanguages("telemetry.sandstorm.uplink_mode." + mode.getId(), langFiles, violations);
        }

        for (SatelliteType satelliteType : SatelliteType.values()) {
            checkKeyAcrossLanguages("telemetry.sandstorm.satellite_deployed_" + satelliteType.getId(), langFiles, violations);
        }

        for (MegastructureBlueprint blueprint : MegastructureBlueprint.values()) {
            checkKeyAcrossLanguages("megastructure.sandstorm." + blueprint.getId(), langFiles, violations);
            checkKeyAcrossLanguages("megastructure.sandstorm." + blueprint.getId() + ".blocks", langFiles, violations);
        }

        for (int i = 0; i <= 4; i++) {
            checkKeyAcrossLanguages("tooltip.sandstorm.cyborg_incubator.stage_" + i, langFiles, violations);
        }

        for (int chapter = 1; chapter <= 5; chapter++) {
            checkKeyAcrossLanguages("gui.sandstorm.datapad.chapter." + chapter, langFiles, violations);
            checkKeyAcrossLanguages("gui.sandstorm.datapad.tab." + chapter, langFiles, violations);
        }

        for (QuestData quest : QuestRegistry.getAllQuests().values()) {
            checkKeyAcrossLanguages(quest.titleKey(), langFiles, violations);
            checkKeyAcrossLanguages(quest.taskKey(), langFiles, violations);
            checkKeyAcrossLanguages(quest.noteKey(), langFiles, violations);
        }

        assertTrue(violations.isEmpty(), "Missing dynamic family translation keys:\n" + String.join("\n", violations));
    }

    @Test
    void allTranslationsMustHaveNoRawNewlinesAndNonEmptyValues() throws IOException {
        Map<String, JsonObject> langFiles = loadAllLanguages();
        List<String> violations = new ArrayList<>();

        for (Map.Entry<String, JsonObject> entry : langFiles.entrySet()) {
            String langName = entry.getKey();
            JsonObject json = entry.getValue();
            for (Map.Entry<String, JsonElement> prop : json.entrySet()) {
                String key = prop.getKey();
                String value = prop.getValue().getAsString();

                if (value.isBlank()) {
                    violations.add(langName + " -> " + key + " is empty/blank");
                }
                if (value.contains("\n") || value.contains("\r")) {
                    violations.add(langName + " -> " + key + " contains raw newline: " + value);
                }
            }
        }

        assertTrue(violations.isEmpty(), "Translation values validation violations:\n" + String.join("\n", violations));
    }

    private void scanTranslatableLiteralsInDirectory(Path dir, Map<String, JsonObject> langFiles, List<String> violations) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(p -> p.toString().endsWith(".java")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    Matcher matcher = TRANSLATABLE_LITERAL_PATTERN.matcher(content);
                    while (matcher.find()) {
                        String key = matcher.group(1);
                        if (key.endsWith(".") || key.endsWith("_")) {
                            continue;
                        }
                        checkKeyAcrossLanguages(key, langFiles, violations);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void checkKeyAcrossLanguages(String key, Map<String, JsonObject> langFiles, List<String> violations) {
        for (Map.Entry<String, JsonObject> entry : langFiles.entrySet()) {
            String langName = entry.getKey();
            JsonObject json = entry.getValue();
            if (!json.has(key)) {
                violations.add(langName + " is missing key: " + key);
            } else if (json.get(key).getAsString().isBlank()) {
                violations.add(langName + " has blank value for key: " + key);
            }
        }
    }

    private Map<String, JsonObject> loadAllLanguages() throws IOException {
        Map<String, JsonObject> langMap = new HashMap<>();
        String[] languages = {"pt_br.json", "en_us.json", "es_es.json"};
        for (String lang : languages) {
            Path langFile = LANG_DIR.resolve(lang);
            assertTrue(Files.exists(langFile), "Language file must exist: " + lang);
            try (FileReader reader = new FileReader(langFile.toFile())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                langMap.put(lang, json);
            }
        }
        return langMap;
    }
}
