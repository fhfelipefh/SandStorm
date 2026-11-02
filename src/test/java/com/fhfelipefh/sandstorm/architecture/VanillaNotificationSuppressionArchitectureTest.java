package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.client.SandStormClientWorldHelper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VanillaNotificationSuppressionArchitectureTest {

    @Test
    void filesMustExist() {
        Path helper = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "SandStormClientWorldHelper.java");
        Path tutorialMixin = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "TutorialMixin.java");
        Path toastMixin = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "ToastManagerMixin.java");

        assertTrue(Files.exists(helper), "SandStormClientWorldHelper.java must exist");
        assertTrue(Files.exists(tutorialMixin), "TutorialMixin.java must exist");
        assertTrue(Files.exists(toastMixin), "ToastManagerMixin.java must exist");
    }

    @Test
    void clientMixinsMustDeclareTutorialAndToastMixins() throws IOException {
        Path clientMixin = Path.of("src", "client", "resources", "sandstorm.client.mixins.json");
        assertTrue(Files.exists(clientMixin), "sandstorm.client.mixins.json must exist");

        String content = Files.readString(clientMixin);
        assertTrue(content.contains("\"TutorialMixin\""), "sandstorm.client.mixins.json must declare TutorialMixin");
        assertTrue(content.contains("\"ToastManagerMixin\""), "sandstorm.client.mixins.json must declare ToastManagerMixin");
    }

    @Test
    void worldHelperTestingAndFlagIntegrity() {
        SandStormClientWorldHelper.setForcedClientSandStormForTesting(true);
        assertTrue(SandStormClientWorldHelper.isSandStormWorld());

        SandStormClientWorldHelper.setForcedClientSandStormForTesting(false);
        assertFalse(SandStormClientWorldHelper.isSandStormWorld());

        SandStormClientWorldHelper.setForcedClientSandStormForTesting(null);

        SandStormClientWorldHelper.setConnectedToSandStorm(true);
        assertTrue(SandStormClientWorldHelper.isConnectedToSandStorm());
        assertTrue(SandStormClientWorldHelper.isSandStormWorld());

        SandStormClientWorldHelper.setConnectedToSandStorm(false);
        assertFalse(SandStormClientWorldHelper.isConnectedToSandStorm());
    }

    @Test
    void tutorialMixinMustInterceptTickStartAndSetStep() throws IOException {
        Path tutorialMixin = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "TutorialMixin.java");
        String content = Files.readString(tutorialMixin);

        assertTrue(content.contains("sandstorm$disableTutorialStart"));
        assertTrue(content.contains("sandstorm$disableTutorialTick"));
        assertTrue(content.contains("sandstorm$disableTutorialSetStep"));
        assertTrue(content.contains("this.stop()"));
        assertTrue(content.contains("ci.cancel()"));
    }

    @Test
    void toastManagerMixinMustInterceptAddToast() throws IOException {
        Path toastMixin = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "ToastManagerMixin.java");
        String content = Files.readString(toastMixin);

        assertTrue(content.contains("sandstorm$suppressVanillaToasts"));
        assertTrue(content.contains("isVanillaNotification"));
        assertTrue(content.contains("ci.cancel()"));
    }

    @Test
    void clientJoinMustClearAndDisableTutorial() throws IOException {
        Path clientJava = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "SandStormClient.java");
        String content = Files.readString(clientJava);

        assertTrue(content.contains("TutorialSteps.NONE"));
        assertTrue(content.contains("toastManager().clear()"));
        assertTrue(content.contains("client.getTutorial().stop()"));
    }

    @Test
    void versionMustBeBumpedTo11976() throws IOException {
        Path props = Path.of("gradle.properties");
        String content = Files.readString(props);
        assertTrue(content.contains("version=1.19.76"), "gradle.properties must be 1.19.76");
    }
}

