package com.fhfelipefh.sandstorm.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .importPath(Path.of("build", "classes", "java", "main"));
    }

    @Test
    void classesMustBeImported() {
        assertFalse(importedClasses.isEmpty());
    }

    @Test
    void nonClientClassesMustNotDependOnClient() {
        noClasses()
                .that().resideOutsideOfPackage("..client..")
                .should().dependOnClassesThat().resideInAnyPackage("..client..", "net.minecraft.client..")
                .check(importedClasses);
    }

    @Test
    void componentsMustNotDependOnContentOrCore() {
        noClasses()
                .that().resideInAPackage("..component..")
                .should().dependOnClassesThat().resideInAnyPackage("..content..", "..core..")
                .check(importedClasses);
    }

    @Test
    void componentsMustBePublicAndFollowNamingConvention() {
        classes()
                .that().resideInAPackage("..component..")
                .and().areTopLevelClasses()
                .should().haveSimpleNameEndingWith("Component")
                .andShould().bePublic()
                .check(importedClasses);
    }

    @Test
    void metricsMustFollowNamingConvention() {
        classes()
                .that().resideInAPackage("..metrics..")
                .and().areTopLevelClasses()
                .should().haveSimpleNameEndingWith("Tracker")
                .check(importedClasses);
    }

    @Test
    void metricsMustNotDependOnClient() {
        noClasses()
                .that().resideInAPackage("..metrics..")
                .should().dependOnClassesThat().resideInAnyPackage("..client..", "net.minecraft.client..")
                .check(importedClasses);
    }
}
