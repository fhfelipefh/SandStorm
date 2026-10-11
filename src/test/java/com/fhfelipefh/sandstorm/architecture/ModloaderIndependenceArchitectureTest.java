package com.fhfelipefh.sandstorm.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ModloaderIndependenceArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .importPath(Path.of("build", "classes", "java", "main"));
    }

    @Test
    void coreClassesMustNotDependOnFabric() {
        noClasses()
                .that().resideInAnyPackage("com.fhfelipefh.sandstorm.core..", "com.fhfelipefh.sandstorm.content..", "com.fhfelipefh.sandstorm.component..")
                .and().doNotHaveSimpleName("SandStormMod")
                .and().doNotHaveSimpleName("SandStormClient")
                .and().resideOutsideOfPackage("com.fhfelipefh.sandstorm.platform..")
                // Ignorar classes que já possuem violações legadas (para permitir refatoração gradual)
                .and().doNotHaveSimpleName("SandStormConfig")
                .and().doNotHaveSimpleName("AutoModeManager")
                .and().doNotHaveSimpleName("DesertPredationHandler")
                .and().doNotHaveSimpleName("NutrientTerraformingManager")
                .and().doNotHaveSimpleName("DesertMobSpawnManager")
                .and().doNotHaveSimpleName("EmpParalysisHandler")
                .and().doNotHaveSimpleName("VanillaMonsterSuppressionHandler")
                .and().doNotHaveSimpleName("DimensionPortalRestrictionHandler")
                .and().doNotHaveSimpleName("SandStormWorldGen")
                .and().doNotHaveSimpleName("SpaceshipLandingManager")
                .and().doNotHaveSimpleName("SandstormWeatherHandler")
                .and().doNotHaveSimpleName("ShowcaseAutomation")
                .and().doNotHaveSimpleName("ProceduralRuinsManager")
                .and().doNotHaveSimpleName("ExhibitionGalleryManager")
                .and().doNotHaveSimpleName("RecipeUnlockHandler")
                .and().doNotHaveSimpleName("QuestRewardHandler")
                .and().doNotHaveSimpleName("SandstormTerraformerCommand")
                .and().doNotHaveSimpleName("SandstormDebugCommand")
                .and().doNotHaveSimpleName("SandstormWeatherCommand")
                .and().doNotHaveSimpleName("SandstormPlantCommand")
                .and().doNotHaveSimpleName("SandwormShowcaseCommand")
                .and().doNotHaveSimpleName("SandstormBuildCommand")
                .and().doNotHaveSimpleName("SandstormClaimCommand")
                .and().doNotHaveSimpleName("SandstormAutoCommand")
                .and().doNotHaveSimpleName("SandstormMorphingCastleCommand")
                .and().doNotHaveSimpleName("ThermalGeneratorManager")
                .and().doNotHaveSimpleName("SupercriticalHeatExchangerManager")
                .and().doNotHaveSimpleName("SandStormBlocks")
                .and().doNotHaveSimpleName("WirelessSolarReceiverManager")
                .and().doNotHaveSimpleName("SmartFluidPipeBlock")
                .and().doNotHaveSimpleName("ThermalGeneratorBlockEntity")
                .and().doNotHaveSimpleName("SmartFluidPipeBlockEntity")
                .and().doNotHaveSimpleName("SupercriticalHeatExchangerBlockEntity")
                .and().doNotHaveSimpleName("DewCondenserBlockEntity")
                .and().doNotHaveSimpleName("CyborgIncubatorVatBlockEntity")
                .and().doNotHaveSimpleName("AtmosphericTerraformerBlockEntity")
                .and().doNotHaveSimpleName("AmnioticIncubatorBlockEntity")
                .and().doNotHaveSimpleName("DeepCoreDrillBlockEntity")
                .and().doNotHaveSimpleName("DesalinationFilterBlockEntity")
                .and().doNotHaveSimpleName("BioRegenerationPodBlockEntity")
                .and().doNotHaveSimpleName("DeepCoreBoreholeBlockEntity")
                .and().doNotHaveSimpleName("NomadScavengerEntity")
                .and().doNotHaveSimpleName("SandStormEntities")
                .and().doNotHaveSimpleName("MegazordPilotReachHandler")
                .and().doNotHaveSimpleName("CyberneticCommandUplinkItem")
                .and().doNotHaveSimpleName("SandStormItems")
                .and().doNotHaveSimpleName("EmpBlasterItem")
                .and().doNotHaveSimpleName("QuantumTerminalMenu")
                .and().doNotHaveSimpleName("SeismicSurvivalHandler")
                .and().doNotHaveSimpleName("TechnologyToolRestrictionHandler")
                .and().doNotHaveSimpleName("MultiplayerSpawnHandler")
                .and().doNotHaveSimpleName("MagicSuppressionHandler")
                .and().doNotHaveSimpleName("BedRestrictionHandler")
                .and().doNotHaveSimpleName("FusedSpaceSuitHandler")
                .and().doNotHaveSimpleName("SuitSurvivalHandler")
                .should().dependOnClassesThat().resideInAPackage("net.fabricmc..")
                .because("Core and content logic must remain modloader-agnostic. Use platform abstraction layers instead.")
                .check(importedClasses);
    }

    @Test
    void coreClassesMustNotDependOnNeoForge() {
        noClasses()
                .that().resideInAnyPackage("com.fhfelipefh.sandstorm.core..", "com.fhfelipefh.sandstorm.content..", "com.fhfelipefh.sandstorm.component..")
                .and().doNotHaveSimpleName("SandStormMod")
                .and().doNotHaveSimpleName("SandStormClient")
                .and().resideOutsideOfPackage("com.fhfelipefh.sandstorm.platform..")
                .should().dependOnClassesThat().resideInAPackage("net.neoforged..")
                .because("Core and content logic must remain modloader-agnostic. Use platform abstraction layers instead.")
                .check(importedClasses);
    }
}
