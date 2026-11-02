package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurseForgeAndRuntimeCompatibilityArchitectureTest {

    @Test
    void fabricModJsonMustTargetMinecraft263AndJava25() throws IOException {
        Path fabricModJson = Path.of("src", "main", "resources", "fabric.mod.json");
        assertTrue(Files.exists(fabricModJson), "fabric.mod.json must exist");

        String content = Files.readString(fabricModJson);
        assertTrue(content.contains("\"minecraft\": \">=26.2 <=26.3\""), "fabric.mod.json must target Minecraft 26.3");
        assertTrue(content.contains("\"java\": \">=25\""), "fabric.mod.json must require Java 25");
        assertTrue(content.contains("\"fabricloader\": \">=0.19.0\""), "fabric.mod.json must declare loader 0.19.0+");
        assertFalse(content.contains("1.21.4"), "fabric.mod.json must never claim 1.21.4 compatibility");
    }

    @Test
    void mixinConfigsMustDeclareJava25CompatibilityLevel() throws IOException {
        Path serverMixin = Path.of("src", "main", "resources", "sandstorm.mixins.json");
        Path clientMixin = Path.of("src", "client", "resources", "sandstorm.client.mixins.json");

        assertTrue(Files.exists(serverMixin), "sandstorm.mixins.json must exist");
        assertTrue(Files.exists(clientMixin), "sandstorm.client.mixins.json must exist");

        String serverContent = Files.readString(serverMixin);
        String clientContent = Files.readString(clientMixin);

        assertTrue(serverContent.contains("\"compatibilityLevel\": \"JAVA_25\""), "Server mixin must declare JAVA_25");
        assertTrue(clientContent.contains("\"compatibilityLevel\": \"JAVA_25\""), "Client mixin must declare JAVA_25");
    }

    @Test
    void buildGradleMustTargetJava25Bytecode() throws IOException {
        Path buildGradle = Path.of("build.gradle");
        assertTrue(Files.exists(buildGradle), "build.gradle must exist");

        String content = Files.readString(buildGradle);
        assertTrue(content.contains("it.options.release = 25"), "build.gradle must compile for release 25");
        assertTrue(content.contains("sourceCompatibility = JavaVersion.VERSION_25"), "build.gradle sourceCompatibility must be Java 25");
        assertTrue(content.contains("targetCompatibility = JavaVersion.VERSION_25"), "build.gradle targetCompatibility must be Java 25");
    }

    @Test
    void gradlePropertiesMustDeclareTargetVersionAndJava25() throws IOException {
        Path gradleProperties = Path.of("gradle.properties");
        assertTrue(Files.exists(gradleProperties), "gradle.properties must exist");

        String content = Files.readString(gradleProperties);
        assertTrue(content.contains("java_version=25"), "gradle.properties must specify java_version=25");
        assertTrue(content.contains("minecraft_target_version=26.3"), "gradle.properties must target 26.3");
    }

    @Test
    void mixinConfigsMustDeclareRefmapAndSafeDefaultRequire() throws IOException {
        Path serverMixin = Path.of("src", "main", "resources", "sandstorm.mixins.json");
        Path clientMixin = Path.of("src", "client", "resources", "sandstorm.client.mixins.json");
        Path refmap = Path.of("src", "main", "resources", "sandstorm.refmap.json");

        assertTrue(Files.exists(refmap), "sandstorm.refmap.json must physically exist");

        String serverContent = Files.readString(serverMixin);
        String clientContent = Files.readString(clientMixin);

        assertTrue(serverContent.contains("\"defaultRequire\": 0"), "Server mixin must use defaultRequire 0 to prevent fatal crashes");
        assertTrue(clientContent.contains("\"defaultRequire\": 0"), "Client mixin must use defaultRequire 0 to prevent fatal crashes");

        assertTrue(serverContent.contains("\"refmap\": \"sandstorm.refmap.json\""), "Server mixin must specify refmap");
        assertTrue(clientContent.contains("\"refmap\": \"sandstorm.refmap.json\""), "Client mixin must specify refmap");
    }

    @Test
    void dummyLegacyMixinsMustNeverExist() throws IOException {
        Path serverMixinJava = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "mixin", "SandStormServerMixin.java");
        Path clientMixinJava = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "SandStormClientMixin.java");

        assertFalse(Files.exists(serverMixinJava), "SandStormServerMixin.java must not exist");
        assertFalse(Files.exists(clientMixinJava), "SandStormClientMixin.java must not exist");

        Path serverMixin = Path.of("src", "main", "resources", "sandstorm.mixins.json");
        Path clientMixin = Path.of("src", "client", "resources", "sandstorm.client.mixins.json");

        String serverContent = Files.readString(serverMixin);
        String clientContent = Files.readString(clientMixin);

        assertFalse(serverContent.contains("SandStormServerMixin"), "Server mixin config must not reference SandStormServerMixin");
        assertFalse(clientContent.contains("SandStormClientMixin"), "Client mixin config must not reference SandStormClientMixin");
    }

    @Test
    void sandStormModMustUsePayloadRegistryHelperToAvoidNetworkingApiNoSuchMethodError() throws IOException {
        Path modJava = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "core", "SandStormMod.java");
        Path helperJava = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "core", "PayloadRegistryHelper.java");

        assertTrue(Files.exists(modJava), "SandStormMod.java must exist");
        assertTrue(Files.exists(helperJava), "PayloadRegistryHelper.java must exist");

        String content = Files.readString(modJava);
        assertFalse(content.contains("PayloadTypeRegistry.clientboundPlay()"), "SandStormMod must not invoke clientboundPlay() directly");
        assertFalse(content.contains("PayloadTypeRegistry.serverboundPlay()"), "SandStormMod must not invoke serverboundPlay() directly");
        assertTrue(content.contains("PayloadRegistryHelper.registerClientbound("), "SandStormMod must register clientbound packets via PayloadRegistryHelper");
        assertTrue(content.contains("PayloadRegistryHelper.registerServerbound("), "SandStormMod must register serverbound packets via PayloadRegistryHelper");
    }

    @Test
    void ciWorkflowMustNeverHardcodeLegacyMinecraft1214() throws IOException {
        Path workflow = Path.of(".github", "workflows", "publish-curseforge.yml");
        assertTrue(Files.exists(workflow), "publish-curseforge.yml must exist");

        String content = Files.readString(workflow);
        assertFalse(content.contains("MC_VERSION=\"1.21.4\""), "CI workflow must never hardcode legacy 1.21.4 fallback");
        assertTrue(content.contains("MC_VERSION=\"26.3\""), "CI workflow must target Minecraft 26.3 by default");
    }

    @Test
    void curseForgeProjectMdMustExplicitlyWarnAboutMinecraft263() throws IOException {
        Path projectDoc = Path.of("CURSEFORGE_PROJECT.md");
        assertTrue(Files.exists(projectDoc), "CURSEFORGE_PROJECT.md must exist");

        String content = Files.readString(projectDoc);
        assertTrue(content.contains("Minecraft 26.3"), "CURSEFORGE_PROJECT.md must state Minecraft 26.3");
        assertTrue(content.contains("Java 25"), "CURSEFORGE_PROJECT.md must state Java 25");
        assertTrue(content.contains("não é compatível com versões legadas como Minecraft 1.21.4")
                || content.contains("Não é compatível com versões legadas como Minecraft 1.21.4"),
                "CURSEFORGE_PROJECT.md must warn about 1.21.4 incompatibility");
    }

    @Test
    void readmeMustExplicitlyStateMinecraft263AndJava25() throws IOException {
        Path readme = Path.of("README.md");
        assertTrue(Files.exists(readme), "README.md must exist");

        String content = Files.readString(readme);
        assertTrue(content.contains("Minecraft 26.3 (Fabric / Java 25)"), "README.md must state Minecraft 26.3 and Java 25");
        assertFalse(content.contains("Minecraft-1.21.4%20%7C%2026.3"), "README.md badge must not claim 1.21.4 compatibility");
    }

    @Test
    void playBatMustTargetMinecraft263() throws IOException {
        Path playBat = Path.of("play.bat");
        assertTrue(Files.exists(playBat), "play.bat must exist");

        String content = Files.readString(playBat);
        assertTrue(content.contains("SANDSTORM MOD - MINECRAFT 26.3"), "play.bat must target Minecraft 26.3");
    }
}
