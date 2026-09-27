package com.fhfelipefh.sandstorm.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ModernMinecraftRegistryArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .importPath(Path.of("build", "classes", "java", "main"));
    }

    @Test
    void shouldNotInstantiateIdentifierDirectlyOutsideOfCore() {
        noClasses()
                .that().resideOutsideOfPackage("..core..")
                .and().doNotHaveSimpleName("SandStormItems")
                .and().doNotHaveSimpleName("SandStormBlocks")
                .should().callMethod(Identifier.class, "of", String.class, String.class)
                .orShould().callMethod(Identifier.class, "of", String.class)
                .because("Identifiers should be created using SandStormMod.id() to centralize the namespace and avoid typos.");
    }
}
