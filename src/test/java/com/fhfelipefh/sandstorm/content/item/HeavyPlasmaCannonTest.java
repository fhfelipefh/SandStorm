package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeavyPlasmaCannonTest {

    @Test
    void shouldDefineHeavyPlasmaCannonSpecifications() {
        assertEquals(18, HeavyPlasmaCannonItem.COOLDOWN_TICKS);
        assertEquals(72.0, HeavyPlasmaCannonItem.CANNON_RANGE, 0.001);
        assertEquals(32.0f, HeavyPlasmaCannonItem.DAMAGE_AMOUNT, 0.001f);
        assertEquals(1800L, HeavyPlasmaCannonItem.ENERGY_COST);
    }

    @Test
    void shouldConsumeEnergyWhenSuitHasSufficientPower() {
        SuitPowerComponent power = new SuitPowerComponent();
        power.setStoredEnergy(5000L);

        boolean result = power.consumeEnergy(HeavyPlasmaCannonItem.ENERGY_COST);
        assertTrue(result);
        assertEquals(3200L, power.getStoredEnergy());

        boolean secondResult = power.consumeEnergy(HeavyPlasmaCannonItem.ENERGY_COST);
        assertTrue(secondResult);
        assertEquals(1400L, power.getStoredEnergy());

        boolean thirdResult = power.consumeEnergy(HeavyPlasmaCannonItem.ENERGY_COST);
        assertFalse(thirdResult);
        assertEquals(1400L, power.getStoredEnergy());
    }
}
