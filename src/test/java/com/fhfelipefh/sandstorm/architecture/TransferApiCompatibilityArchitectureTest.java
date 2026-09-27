package com.fhfelipefh.sandstorm.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class TransferApiCompatibilityArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .importPath(Path.of("build", "classes", "java", "main"));
    }

    @Test
    void fluidHandlingBlockEntitiesMustExposeFluidStorage() {
        classes()
                .that().resideInAPackage("..content.block.entity..")
                .and().areAssignableTo("net.minecraft.world.level.block.entity.BlockEntity")
                .and(new DescribedPredicate<>("handle fluids or contain fluid in name or methods") {
                    @Override
                    public boolean test(JavaClass item) {
                        return item.getSimpleName().contains("Fluid") ||
                                item.getSimpleName().contains("Filter") ||
                                item.getSimpleName().contains("Aquifer") ||
                                item.getAllMethods().stream().anyMatch(m -> m.getName().contains("Water") || m.getName().contains("Fluid"));
                    }
                })
                .should(new ArchCondition<>("expose Fabric FluidStorage method getFluidStorage(Direction)") {
                    @Override
                    public void check(JavaClass item, ConditionEvents events) {
                        boolean hasMethod = item.getAllMethods().stream().anyMatch(m ->
                                m.getName().equals("getFluidStorage") &&
                                m.getRawParameterTypes().size() == 1 &&
                                m.getRawParameterTypes().get(0).isAssignableTo(Direction.class) &&
                                m.getRawReturnType().isAssignableTo("net.fabricmc.fabric.api.transfer.v1.storage.Storage"));
                        if (!hasMethod) {
                            events.add(SimpleConditionEvent.violated(item,
                                    item.getName() + " handles fluids but does not declare getFluidStorage(Direction) returning Storage<FluidVariant>."));
                        }
                    }
                })
                .because("Any block entity managing fluids must expose a getFluidStorage(Direction) method for Fabric Transfer API compatibility.")
                .check(importedClasses);
    }

    @Test
    void machineBlockEntitiesMustImplementWorldlyContainerForFabricItemStorage() {
        classes()
                .that().resideInAPackage("..content.block.entity..")
                .and().areAssignableTo("com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity")
                .should().beAssignableTo(WorldlyContainer.class)
                .because("All machine block entities handling items must implement WorldlyContainer to support Fabric Transfer API via ContainerStorage::of.")
                .check(importedClasses);
    }

    @Test
    void energyMachineBlockEntitiesMustExposeStandardEnergyContract() {
        classes()
                .that().resideInAPackage("..content.block.entity..")
                .and().areAssignableTo("com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity")
                .should(new ArchCondition<>("expose standard energy contract (getEnergy, getMaxEnergy, isProcessing)") {
                    @Override
                    public void check(JavaClass item, ConditionEvents events) {
                        boolean hasGetEnergy = item.getAllMethods().stream().anyMatch(m ->
                                m.getName().equals("getEnergy") && m.getRawParameterTypes().isEmpty() && m.getRawReturnType().getName().equals("int"));
                        boolean hasGetMaxEnergy = item.getAllMethods().stream().anyMatch(m ->
                                m.getName().equals("getMaxEnergy") && m.getRawParameterTypes().isEmpty() && m.getRawReturnType().getName().equals("int"));
                        boolean hasIsProcessing = item.getAllMethods().stream().anyMatch(m ->
                                m.getName().equals("isProcessing") && m.getRawParameterTypes().isEmpty() && m.getRawReturnType().getName().equals("boolean"));

                        if (!hasGetEnergy || !hasGetMaxEnergy || !hasIsProcessing) {
                            events.add(SimpleConditionEvent.violated(item,
                                    item.getName() + " must implement getEnergy(), getMaxEnergy(), and isProcessing() for energy compatibility."));
                        }
                    }
                })
                .because("All machine block entities with energy must provide a uniform energy contract for Fabric energy integration.")
                .check(importedClasses);
    }

    @Test
    void sandStormBlocksMustRegisterTransferApiForMachinesAndFluids() {
        classes()
                .that().haveSimpleName("SandStormBlocks")
                .should(new ArchCondition<>("register Fabric ItemStorage and FluidStorage") {
                    @Override
                    public void check(JavaClass item, ConditionEvents events) {
                        boolean callsRegister = item.getMethodCallsFromSelf().stream().anyMatch(call ->
                                call.getTarget().getName().equals("registerForBlockEntity"));
                        boolean accessesItemStorage = item.getFieldAccessesFromSelf().stream().anyMatch(field ->
                                field.getTarget().getOwner().getName().contains("ItemStorage"));
                        boolean accessesFluidStorage = item.getFieldAccessesFromSelf().stream().anyMatch(field ->
                                field.getTarget().getOwner().getName().contains("FluidStorage"));

                        if (!callsRegister || !accessesItemStorage) {
                            events.add(SimpleConditionEvent.violated(item, "SandStormBlocks does not register ItemStorage.SIDED for machines."));
                        }
                        if (!callsRegister || !accessesFluidStorage) {
                            events.add(SimpleConditionEvent.violated(item, "SandStormBlocks does not register FluidStorage.SIDED for fluid blocks."));
                        }
                    }
                })
                .because("SandStormBlocks must register ItemStorage.SIDED and FluidStorage.SIDED for machines and fluids.")
                .check(importedClasses);
    }
}
