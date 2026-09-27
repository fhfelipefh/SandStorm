package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeaponEnergyAndCombatTest {

    @Test
    void shouldDefinePlasmaRifleSpecifications() {
        assertEquals(12.0f, PlasmaRifleItem.DAMAGE_AMOUNT, 0.001f);
        assertEquals(7, PlasmaRifleItem.COOLDOWN_TICKS);
        assertEquals(48.0, PlasmaRifleItem.RIFLE_RANGE, 0.001);
        assertEquals(800L, PlasmaRifleItem.ENERGY_COST);
    }

    @Test
    void shouldDefineVibroCrysknifeSpecifications() {
        assertEquals(9.0f, VibroCrysknifeItem.BASE_DAMAGE, 0.001f);
        assertEquals(200L, VibroCrysknifeItem.ENERGY_COST);
        assertEquals(4.5f, VibroCrysknifeItem.ARMOR_PIERCE_BONUS, 0.001f);
    }

    @Test
    void shouldDefineSonicCannonSpecifications() {
        assertEquals(20.0f, SonicCannonItem.DAMAGE_AMOUNT, 0.001f);
        assertEquals(25, SonicCannonItem.COOLDOWN_TICKS);
        assertEquals(24.0, SonicCannonItem.CANNON_RANGE, 0.001);
        assertEquals(2500L, SonicCannonItem.ENERGY_COST);
    }

    @Test
    void shouldConsumeEnergyWhenSuitHasSufficientPower() {
        SuitPowerComponent power = new SuitPowerComponent();
        power.setStoredEnergy(5000L);

        boolean sonicResult = power.consumeEnergy(SonicCannonItem.ENERGY_COST);
        assertTrue(sonicResult);
        assertEquals(2500L, power.getStoredEnergy());

        boolean plasmaResult = power.consumeEnergy(PlasmaRifleItem.ENERGY_COST);
        assertTrue(plasmaResult);
        assertEquals(1700L, power.getStoredEnergy());

        boolean vibroResult = power.consumeEnergy(VibroCrysknifeItem.ENERGY_COST);
        assertTrue(vibroResult);
        assertEquals(1500L, power.getStoredEnergy());
    }

    @Test
    void shouldFailConsumptionWhenSuitHasInsufficientPower() {
        SuitPowerComponent power = new SuitPowerComponent();
        power.setStoredEnergy(500L);

        boolean sonicResult = power.consumeEnergy(SonicCannonItem.ENERGY_COST);
        assertFalse(sonicResult);
        assertEquals(500L, power.getStoredEnergy());

        boolean plasmaResult = power.consumeEnergy(PlasmaRifleItem.ENERGY_COST);
        assertFalse(plasmaResult);
        assertEquals(500L, power.getStoredEnergy());

        boolean vibroResult = power.consumeEnergy(VibroCrysknifeItem.ENERGY_COST);
        assertTrue(vibroResult);
        assertEquals(300L, power.getStoredEnergy());

        boolean secondVibro = power.consumeEnergy(VibroCrysknifeItem.ENERGY_COST);
        assertTrue(secondVibro);
        assertEquals(100L, power.getStoredEnergy());

        boolean thirdVibro = power.consumeEnergy(VibroCrysknifeItem.ENERGY_COST);
        assertFalse(thirdVibro);
        assertEquals(100L, power.getStoredEnergy());
    }
}
