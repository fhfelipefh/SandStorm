package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThermalGeneratorTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @BeforeEach
    void setUp() {
        ThermalGeneratorManager.clear();
    }

    @Test
    void testFuelAdditionAndCapacity() {
        BlockPos pos = new BlockPos(10, 64, 10);
        ThermalGeneratorManager.registerGenerator(Level.OVERWORLD, pos, 0);

        int fuelAfterOne = ThermalGeneratorManager.addFuel(Level.OVERWORLD, pos, ThermalGeneratorManager.TICKS_PER_BUCKET);
        assertEquals(4000, fuelAfterOne);
        assertEquals(4000, ThermalGeneratorManager.getBurnTime(Level.OVERWORLD, pos));

        ThermalGeneratorManager.addFuel(Level.OVERWORLD, pos, 20000);
        assertEquals(ThermalGeneratorManager.MAX_BURN_TICKS, ThermalGeneratorManager.getBurnTime(Level.OVERWORLD, pos));
    }

    @Test
    void testRegistrationAndUnregistration() {
        BlockPos pos = new BlockPos(20, 64, 20);
        ThermalGeneratorManager.registerGenerator(Level.OVERWORLD, pos, 2000);
        assertEquals(2000, ThermalGeneratorManager.getBurnTime(Level.OVERWORLD, pos));

        ThermalGeneratorManager.unregisterGenerator(Level.OVERWORLD, pos);
        assertEquals(0, ThermalGeneratorManager.getBurnTime(Level.OVERWORLD, pos));
    }
}
