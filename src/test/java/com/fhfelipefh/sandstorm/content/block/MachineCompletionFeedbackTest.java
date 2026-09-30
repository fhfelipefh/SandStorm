package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.AutoAssemblyLineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.BioreactorVatBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.ChemicalRefineryBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.CyborgIncubatorVatBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.DeepCoreDrillBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.DesalinationFilterBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.HydroponicChamberBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.LithoPlasmaExtractorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.MolecularModifierBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.NaniteFabricatorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.Printer3DBlockEntity;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MachineCompletionFeedbackTest {

    @Test
    void testBaseMachineDefinesCompletionTemplateMethods() {
        assertDoesNotThrow(() -> {
            Method onProcessCompleted = BaseMachineBlockEntity.class.getDeclaredMethod("onProcessCompleted", Level.class, BlockPos.class);
            assertNotNull(onProcessCompleted);

            Method getProcessSoundVolume = BaseMachineBlockEntity.class.getDeclaredMethod("getProcessSoundVolume");
            assertNotNull(getProcessSoundVolume);

            Method getProcessSoundPitch = BaseMachineBlockEntity.class.getDeclaredMethod("getProcessSoundPitch");
            assertNotNull(getProcessSoundPitch);

            Method spawnProcessCompletedParticles = BaseMachineBlockEntity.class.getDeclaredMethod("spawnProcessCompletedParticles", ServerLevel.class, BlockPos.class);
            assertNotNull(spawnProcessCompletedParticles);
        });
    }

    @Test
    void testAllKeyMachinesOverrideProcessCompletionFeedback() {
        List<Class<? extends BaseMachineBlockEntity>> machineClasses = List.of(
                Printer3DBlockEntity.class,
                NaniteFabricatorBlockEntity.class,
                ChemicalRefineryBlockEntity.class,
                MolecularModifierBlockEntity.class,
                AutoAssemblyLineBlockEntity.class,
                BioreactorVatBlockEntity.class,
                DesalinationFilterBlockEntity.class,
                CyborgIncubatorVatBlockEntity.class,
                DeepCoreDrillBlockEntity.class,
                HydroponicChamberBlockEntity.class
        );

        for (Class<? extends BaseMachineBlockEntity> machineClass : machineClasses) {
            boolean overridesCompletion = false;
            try {
                Method method = machineClass.getDeclaredMethod("onProcessCompleted", Level.class, BlockPos.class);
                overridesCompletion = method != null;
            } catch (NoSuchMethodException ignored) {
            }

            assertTrue(overridesCompletion, machineClass.getSimpleName() + " must override onProcessCompleted to provide distinctive audio and visual particle feedback");
        }
    }

    @Test
    void testLithoPlasmaExtractorDeclaresCentrifugeExtractionWithFeedback() {
        assertDoesNotThrow(() -> {
            Method method = LithoPlasmaExtractorBlockEntity.class.getDeclaredMethod("processCentrifugeExtraction", Level.class, BlockPos.class);
            assertNotNull(method);
        });
    }
}
