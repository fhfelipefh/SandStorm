package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CryoReactorMechanicTest {

    private final ResourceKey<Level> testDim = Level.OVERWORLD;
    private final BlockPos reactorPos = new BlockPos(100, 60, 100);
    private final BlockPos permafrostPos = new BlockPos(105, 60, 100);

    @BeforeEach
    void setUp() {
        CryoVaultReactorManager.clear();
    }

    @Test
    void shouldRegisterAndTrackOperationalReactor() {
        assertNotNull(SandStormBlocks.CRYO_REACTOR_CORE);
        assertTrue(SandStormBlocks.CRYO_REACTOR_CORE.defaultBlockState().getValue(CryoReactorCoreBlock.OPERATIONAL));

        CryoVaultReactorManager.registerReactor(testDim, reactorPos, true);
        assertTrue(CryoVaultReactorManager.isReactorOperational(testDim, reactorPos));
        assertFalse(CryoVaultReactorManager.hasBrokenReactor(testDim));
    }

    @Test
    void shouldDetectBrokenReactorWhenDamaged() {
        CryoVaultReactorManager.registerReactor(testDim, reactorPos, true);
        assertFalse(CryoVaultReactorManager.hasBrokenReactor(testDim));

        CryoVaultReactorManager.setReactorOperational(testDim, reactorPos, false);
        assertFalse(CryoVaultReactorManager.isReactorOperational(testDim, reactorPos));
        assertTrue(CryoVaultReactorManager.hasBrokenReactor(testDim));
    }

    @Test
    void shouldRestoreOperationalStateWhenCooled() {
        CryoVaultReactorManager.registerReactor(testDim, reactorPos, false);
        assertTrue(CryoVaultReactorManager.hasBrokenReactor(testDim));

        CryoVaultReactorManager.setReactorOperational(testDim, reactorPos, true);
        assertTrue(CryoVaultReactorManager.isReactorOperational(testDim, reactorPos));
        assertFalse(CryoVaultReactorManager.hasBrokenReactor(testDim));
    }

    @Test
    void shouldTrackPermafrostPositionsInManager() {
        CryoVaultReactorManager.registerPermafrost(testDim, permafrostPos);
        assertTrue(CryoVaultReactorManager.getTrackedPermafrost(testDim).contains(permafrostPos));

        CryoVaultReactorManager.unregisterPermafrost(testDim, permafrostPos);
        assertFalse(CryoVaultReactorManager.getTrackedPermafrost(testDim).contains(permafrostPos));
    }

    @Test
    void shouldUnregisterReactorWhenDestroyedCompletely() {
        CryoVaultReactorManager.registerReactor(testDim, reactorPos, true);
        assertTrue(CryoVaultReactorManager.isReactorOperational(testDim, reactorPos));

        CryoVaultReactorManager.unregisterReactor(testDim, reactorPos);
        assertFalse(CryoVaultReactorManager.isReactorOperational(testDim, reactorPos));
        assertEquals(0, CryoVaultReactorManager.getTrackedPermafrost(testDim).size());
    }
}
