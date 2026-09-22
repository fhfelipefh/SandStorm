package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.item.SuitUpgradeItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SuitUpgradeTest {

    @Test
    void shouldDefineAllUpgradeTypesCorrectly() {
        assertEquals("battery", SuitUpgradeItem.UpgradeType.BATTERY.getId());
        assertEquals("thermal", SuitUpgradeItem.UpgradeType.THERMAL.getId());
        assertEquals("seismic", SuitUpgradeItem.UpgradeType.SEISMIC.getId());
        assertEquals("visor", SuitUpgradeItem.UpgradeType.VISOR.getId());

        assertEquals("item.sandstorm.suit_upgrade_battery", SuitUpgradeItem.UpgradeType.BATTERY.getTranslationKey());
        assertEquals("item.sandstorm.suit_upgrade_thermal", SuitUpgradeItem.UpgradeType.THERMAL.getTranslationKey());
        assertEquals("item.sandstorm.suit_upgrade_seismic", SuitUpgradeItem.UpgradeType.SEISMIC.getTranslationKey());
        assertEquals("item.sandstorm.suit_upgrade_visor", SuitUpgradeItem.UpgradeType.VISOR.getTranslationKey());
    }

    @Test
    void shouldSupportUpgradesInSuitDataEntry() {
        UUID uuid = UUID.randomUUID();
        PlayerSuitSavedData.Entry emptyEntry = new PlayerSuitSavedData.Entry(uuid, 50000L, 37.0);
        assertTrue(emptyEntry.upgrades().isEmpty());

        PlayerSuitSavedData.Entry upgradedEntry = new PlayerSuitSavedData.Entry(
                uuid,
                150000L,
                37.0,
                List.of("battery", "seismic")
        );
        assertEquals(2, upgradedEntry.upgrades().size());
        assertTrue(upgradedEntry.upgrades().contains("battery"));
        assertTrue(upgradedEntry.upgrades().contains("seismic"));
        assertFalse(upgradedEntry.upgrades().contains("thermal"));
    }

    @Test
    void shouldAllowCapacityExpansionOnEnergyComponents() {
        EnergyStorageComponent storage = new EnergyStorageComponent(100000L, 500L, 500L);
        assertEquals(100000L, storage.getCapacity());

        storage.setCapacity(150000L);
        assertEquals(150000L, storage.getCapacity());

        SuitPowerComponent suitPower = new SuitPowerComponent();
        assertEquals(100000L, suitPower.getEnergyStorage().getCapacity());

        suitPower.setCapacity(150000L);
        assertEquals(150000L, suitPower.getEnergyStorage().getCapacity());
    }
}
