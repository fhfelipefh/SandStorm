package com.fhfelipefh.sandstorm.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class BlockItemPropertiesArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .importPath(Path.of("build", "classes", "java", "main"));
    }

    @Test
    void itemPropertiesMustBeCreatedViaHelperToEnforceSetId() {
        noClasses()
                .that().doNotHaveSimpleName("SandStormItems")
                .and().doNotHaveSimpleName("SandStormBlocks")
                .should().callConstructor(Item.Properties.class)
                .because("Item.Properties must be created via SandStormItems.properties() to ensure .setId() is called, which is required in 1.21.2+.");
    }

    @Test
    void blockPropertiesMustBeCreatedViaHelperToEnforceSetId() {
        noClasses()
                .that().doNotHaveSimpleName("SandStormBlocks")
                .should().callMethod(BlockBehaviour.Properties.class, "of")
                .because("BlockBehaviour.Properties must be created via SandStormBlocks helper methods to ensure .setId() is called, which is required in 1.21.2+.");
    }
}
