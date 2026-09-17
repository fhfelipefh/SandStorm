package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class I18nParityTest {

    private static final Path LANG_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "lang");

    @Test
    void allLanguageFilesMustHaveIdenticalKeys() throws IOException {
        Set<String> ptBrKeys = readKeys(LANG_DIR.resolve("pt_br.json"));
        Set<String> enUsKeys = readKeys(LANG_DIR.resolve("en_us.json"));
        Set<String> esEsKeys = readKeys(LANG_DIR.resolve("es_es.json"));

        assertTrue(!ptBrKeys.isEmpty());

        Set<String> missingInEn = new HashSet<>(ptBrKeys);
        missingInEn.removeAll(enUsKeys);
        assertEquals(Set.of(), missingInEn, "Keys present in pt_br but missing in en_us");

        Set<String> missingInEs = new HashSet<>(ptBrKeys);
        missingInEs.removeAll(esEsKeys);
        assertEquals(Set.of(), missingInEs, "Keys present in pt_br but missing in es_es");

        Set<String> missingInPtFromEn = new HashSet<>(enUsKeys);
        missingInPtFromEn.removeAll(ptBrKeys);
        assertEquals(Set.of(), missingInPtFromEn, "Keys present in en_us but missing in pt_br");

        Set<String> missingInPtFromEs = new HashSet<>(esEsKeys);
        missingInPtFromEs.removeAll(ptBrKeys);
        assertEquals(Set.of(), missingInPtFromEs, "Keys present in es_es but missing in pt_br");
    }

    private Set<String> readKeys(Path path) throws IOException {
        try (FileReader reader = new FileReader(path.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            return json.keySet();
        }
    }
}
