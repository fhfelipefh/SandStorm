package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DesertMobEcosystemArchitectureTest {

    private static final List<String> ARTIFICIAL_MOB_FILES = List.of(
            "DerelictAutomatonEntity.java",
            "LaborerUnitEntity.java",
            "ScoutDroneEntity.java",
            "CrawlerDroneEntity.java",
            "CyberHoundEntity.java"
    );

    @Test
    void allArtificialMobsMustUseDesertExplorationWanderGoal() throws IOException {
        Path entityDir = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity");
        for (String filename : ARTIFICIAL_MOB_FILES) {
            Path file = entityDir.resolve(filename);
            assertTrue(Files.exists(file), "Missing artificial mob file: " + filename);
            String content = Files.readString(file);
            assertTrue(content.contains("DesertExplorationWanderGoal"),
                    filename + " must register DesertExplorationWanderGoal");
            assertTrue(!content.contains("WaterAvoidingRandomStrollGoal"),
                    filename + " must not use vanilla WaterAvoidingRandomStrollGoal");
        }
    }

    @Test
    void spawnManagerMustImplementRaycastAndFovOcclusion() throws IOException {
        Path spawnManagerFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "DesertMobSpawnManager.java");
        assertTrue(Files.exists(spawnManagerFile), "DesertMobSpawnManager.java must exist");
        String content = Files.readString(spawnManagerFile);
        assertTrue(content.contains("ClipContext.Block.VISUAL"),
                "DesertMobSpawnManager must check visual raycasting");
        assertTrue(content.contains("isPositionVisibleToAnyPlayer"),
                "DesertMobSpawnManager must validate visibility before spawning");
    }

    @Test
    void predationHandlerMustImplementSandwormAmbush() throws IOException {
        Path predationFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "DesertPredationHandler.java");
        assertTrue(Files.exists(predationFile), "DesertPredationHandler.java must exist");
        String content = Files.readString(predationFile);
        assertTrue(content.contains("SANDWORM_RUMBLE"),
                "DesertPredationHandler must play seismic rumble warning");
        assertTrue(content.contains("triggerBreachShockwave"),
                "DesertPredationHandler must trigger breach shockwave");
    }

    @Test
    void versionMustBeOnePointTenOne() throws IOException {
        Path gradleProps = Path.of("gradle.properties");
        String content = Files.readString(gradleProps);
        assertTrue(content.contains("version=1.10.1"), "gradle.properties must be 1.10.1");
    }

    @Test
    void empBlasterMustImplementTacticalBacklashAndAutomatonParalysis() throws IOException {
        Path empItemFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "EmpBlasterItem.java");
        Path empHandlerFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "EmpParalysisHandler.java");
        Path empClientHandlerFile = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "handler", "EmpDeafenClientHandler.java");
        assertTrue(Files.exists(empItemFile), "EmpBlasterItem.java must exist");
        assertTrue(Files.exists(empHandlerFile), "EmpParalysisHandler.java must exist");
        assertTrue(Files.exists(empClientHandlerFile), "EmpDeafenClientHandler.java must exist");

        String empItemContent = Files.readString(empItemFile);
        assertTrue(empItemContent.contains("EMP_RADIUS"), "Must specify EMP radius");
        assertTrue(empItemContent.contains("PLAYER_BACKLASH_TICKS"), "Must specify player backlash duration");
        assertTrue(empItemContent.contains("DARKNESS"), "Must apply darkness backlash");
        assertTrue(empItemContent.contains("BLINDNESS"), "Must apply blindness backlash");
        assertTrue(empItemContent.contains("SLOWNESS"), "Must apply slowness backlash");

        String empHandlerContent = Files.readString(empHandlerFile);
        assertTrue(empHandlerContent.contains("isAndroidOrAutomaton"), "Must filter androids and automata");
        assertTrue(empHandlerContent.contains("paralyze"), "Must support mass paralysis");
    }

    @Test
    void vanillaIronGolemSpawningMustBeSuppressed() throws IOException {
        Path suppressionFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "VanillaMonsterSuppressionHandler.java");
        assertTrue(Files.exists(suppressionFile), "VanillaMonsterSuppressionHandler.java must exist");
        String content = Files.readString(suppressionFile);
        assertTrue(content.contains("iron_golem"), "Must suppress iron_golem entity type");
        assertTrue(content.contains("UseBlockCallback.EVENT"), "Must register UseBlockCallback for pumpkin interception");
        assertTrue(content.contains("telemetry.sandstorm.iron_golem_disabled"), "Must notify player when iron golem creation is blocked");
    }

    @Test
    void cyberneticGolemMustImplementHighTechBoostAndMetalTiers() throws IOException {
        Path golemFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "CyberneticGolemEntity.java");
        Path headBlockFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "CyberneticGolemHeadBlock.java");
        Path tierFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "GolemMetalTier.java");
        assertTrue(Files.exists(golemFile), "CyberneticGolemEntity.java must exist");
        assertTrue(Files.exists(headBlockFile), "CyberneticGolemHeadBlock.java must exist");
        assertTrue(Files.exists(tierFile), "GolemMetalTier.java must exist");

        String golemContent = Files.readString(golemFile);
        assertTrue(golemContent.contains("DATA_OVERDRIVE"), "Must synchronize overdrive state");
        assertTrue(golemContent.contains("DATA_HEAT"), "Must synchronize thermal heat state");
        assertTrue(golemContent.contains("ELECTRIC_SPARK"), "Must spawn spark particles during overdrive");
        assertTrue(golemContent.contains("CAMPFIRE_COSY_SMOKE"), "Must spawn cooling smoke when cooling down");

        String tierContent = Files.readString(tierFile);
        assertTrue(tierContent.contains("IRON"), "Must support Iron tier");
        assertTrue(tierContent.contains("COPPER"), "Must support Copper tier");
        assertTrue(tierContent.contains("GOLD"), "Must support Gold tier");
        assertTrue(tierContent.contains("NETHERITE"), "Must support Netherite tier");
        assertTrue(tierContent.contains("COMPOSITE"), "Must support Composite tier");

        Path modelFile = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer", "CyberneticGolemModel.java");
        assertTrue(Files.exists(modelFile), "CyberneticGolemModel.java must exist");
        String modelContent = Files.readString(modelFile);
        assertTrue(modelContent.contains("this.head = root.getChild(\"head\")"), "Head must be root child");
        assertTrue(modelContent.contains("this.body = root.getChild(\"body\")"), "Body must be root child");
        assertTrue(modelContent.contains("this.rightArm = root.getChild(\"right_arm\")"), "Right arm must be root child");
        assertTrue(modelContent.contains("this.leftArm = root.getChild(\"left_arm\")"), "Left arm must be root child");
    }
}
