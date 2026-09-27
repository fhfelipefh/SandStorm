package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.WirelessChargerComponent;
import com.fhfelipefh.sandstorm.content.block.entity.WirelessSolarReceiverBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WirelessSolarReceiverBlockEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldInitializeTier1WithCorrectDefaults() {
        WirelessSolarReceiverBlockEntity be = new WirelessSolarReceiverBlockEntity(
                BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState(), 1
        );
        assertEquals(1, be.getTier());
        assertEquals(0L, be.getEnergyStorage().getStoredEnergy());
        assertEquals(WirelessSolarReceiverBlockEntity.TIER1_CAPACITY, be.getEnergyStorage().getCapacity());
        assertEquals(24.0, be.getCharger().getBaseRadius());
        assertEquals(50L, be.getCharger().getBaseTransferRate());
    }

    @Test
    void shouldInitializeTier2WithCorrectDefaults() {
        WirelessSolarReceiverBlockEntity be = new WirelessSolarReceiverBlockEntity(
                BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState(), 2
        );
        assertEquals(2, be.getTier());
        assertEquals(0L, be.getEnergyStorage().getStoredEnergy());
        assertEquals(WirelessSolarReceiverBlockEntity.TIER2_CAPACITY, be.getEnergyStorage().getCapacity());
        assertEquals(36.0, be.getCharger().getBaseRadius());
        assertEquals(100L, be.getCharger().getBaseTransferRate());
    }

    @Test
    void shouldCalculateSolarHarvestingRates() {
        WirelessChargerComponent charger = new WirelessChargerComponent(1);

        assertFalse(charger.canHarvestSunlight(false, true));
        assertFalse(charger.canHarvestSunlight(true, false));
        assertTrue(charger.canHarvestSunlight(true, true));

        assertEquals(0.0, charger.calculateSunFactor(false, true, 0, 1.0));
        assertEquals(0.0, charger.calculateSunFactor(true, false, 0, 1.0));

        double fullSun = charger.calculateSunFactor(true, true, 0, 1.0);
        assertEquals(1.0, fullSun, 0.001);

        long maxRate = charger.calculateTransferRate(true, true, 0, 1.0);
        assertEquals(50L, maxRate);

        long halfWeatherRate = charger.calculateTransferRate(true, true, 0, 0.5);
        assertEquals(25L, halfWeatherRate);
    }

    @Test
    void shouldManageEnergyStorageOperations() {
        WirelessSolarReceiverBlockEntity be = new WirelessSolarReceiverBlockEntity(
                BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState(), 1
        );

        long received = be.getEnergyStorage().receiveEnergy(50L);
        assertEquals(50L, received);
        assertEquals(50L, be.getEnergyStorage().getStoredEnergy());

        long excessiveReceive = be.getEnergyStorage().receiveEnergy(200L);
        assertEquals(50L, excessiveReceive);
        assertEquals(100L, be.getEnergyStorage().getStoredEnergy());

        long extracted = be.getEnergyStorage().extractEnergy(60L);
        assertEquals(60L, extracted);
        assertEquals(40L, be.getEnergyStorage().getStoredEnergy());
    }
}
