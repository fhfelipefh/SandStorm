package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurseForgeAndRuntimeCompatibilityArchitectureTest {

    @Test
    void fabricModJsonMustSupportMinecraft1214AndJava21() throws IOException {
        Path fabricModJson = Path.of("src", "main", "resources", "fabric.mod.json");
        assertTrue(Files.exists(fabricModJson), "fabric.mod.json must exist");

        String content = Files.readString(fabricModJson);
        assertTrue(content.contains("\"minecraft\": \">=1.21.4"), "fabric.mod.json must allow Minecraft 1.21.4");
        assertTrue(content.contains("\"java\": \">=21\""), "fabric.mod.json must allow Java 21");
        assertFalse(content.contains("\"java\": \">=25\""), "fabric.mod.json must never require Java 25");
        assertFalse(content.contains("\"minecraft\": \">=26.2\""), "fabric.mod.json must never exclude Minecraft 1.21.4");
        assertTrue(content.contains("\"fabricloader\": \">=0.16.0\""), "fabric.mod.json must declare backwards compatible loader");
    }

    @Test
    void mixinConfigsMustDeclareJava21CompatibilityLevel() throws IOException {
        Path serverMixin = Path.of("src", "main", "resources", "sandstorm.mixins.json");
        Path clientMixin = Path.of("src", "client", "resources", "sandstorm.client.mixins.json");

        assertTrue(Files.exists(serverMixin), "sandstorm.mixins.json must exist");
        assertTrue(Files.exists(clientMixin), "sandstorm.client.mixins.json must exist");

        String serverContent = Files.readString(serverMixin);
        String clientContent = Files.readString(clientMixin);

        assertTrue(serverContent.contains("\"compatibilityLevel\": \"JAVA_21\""), "Server mixin must declare JAVA_21");
        assertTrue(clientContent.contains("\"compatibilityLevel\": \"JAVA_21\""), "Client mixin must declare JAVA_21");

        assertFalse(serverContent.contains("JAVA_25"), "Server mixin must never declare JAVA_25");
        assertFalse(clientContent.contains("JAVA_25"), "Client mixin must never declare JAVA_25");
    }

    @Test
    void buildGradleMustTargetJava21Bytecode() throws IOException {
        Path buildGradle = Path.of("build.gradle");
        assertTrue(Files.exists(buildGradle), "build.gradle must exist");

        String content = Files.readString(buildGradle);
        assertTrue(content.contains("it.options.release = 21"), "build.gradle must compile for release 21");
        assertTrue(content.contains("sourceCompatibility = JavaVersion.VERSION_21"), "build.gradle sourceCompatibility must be Java 21");
        assertTrue(content.contains("targetCompatibility = JavaVersion.VERSION_21"), "build.gradle targetCompatibility must be Java 21");
        assertFalse(content.contains("it.options.release = 25"), "build.gradle must not use release 25");
        assertFalse(content.contains("VERSION_25"), "build.gradle must not use VERSION_25");
    }

    @Test
    void gradlePropertiesMustDeclareTargetVersionAndJava21() throws IOException {
        Path gradleProperties = Path.of("gradle.properties");
        assertTrue(Files.exists(gradleProperties), "gradle.properties must exist");

        String content = Files.readString(gradleProperties);
        assertTrue(content.contains("java_version=21"), "gradle.properties must specify java_version=21");
        assertTrue(content.contains("minecraft_target_version=1.21.4"), "gradle.properties must target 1.21.4 for CurseForge");
        assertFalse(content.contains("java_version=25"), "gradle.properties must not specify java_version=25");
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
}
