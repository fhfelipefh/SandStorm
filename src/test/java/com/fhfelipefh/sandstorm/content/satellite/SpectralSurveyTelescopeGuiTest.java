package com.fhfelipefh.sandstorm.content.satellite;

import com.fhfelipefh.sandstorm.content.gui.SpectralSurveyTelescopeMenu;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpectralSurveyTelescopeGuiTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldInitializeTelescopeMenuWithCorrectSlotsAndData() {
        Inventory inventory = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(1);
        SimpleContainerData data = new SimpleContainerData(8);
        data.set(0, 12500);
        data.set(1, 50000);
        data.set(2, SpectralSurveyTelescopeMenu.FILTER_INFRARED);
        data.set(3, SpectralSurveyTelescopeMenu.TARGET_CRAB_NEBULA);
        data.set(4, 50);
        data.set(5, 1);
        data.set(6, 2);
        data.set(7, 180);

        SpectralSurveyTelescopeMenu menu = new SpectralSurveyTelescopeMenu(
                null,
                1,
                inventory,
                null,
                container,
                data
        );

        assertEquals(37, menu.slots.size());
        assertEquals(12500, menu.getStoredEnergy());
        assertEquals(50000, menu.getMaxEnergy());
        assertEquals(SpectralSurveyTelescopeMenu.FILTER_INFRARED, menu.getActiveFilter());
        assertEquals(SpectralSurveyTelescopeMenu.TARGET_CRAB_NEBULA, menu.getCelestialTarget());
        assertEquals(50, menu.getScanProgress());
        assertTrue(menu.hasSkyAccess());
        assertEquals(2, menu.getStormThreat());
        assertEquals(180, menu.getSecondsToStorm());
    }

    @Test
    void shouldVerifyTargetAndFilterConstants() {
        assertEquals(0, SpectralSurveyTelescopeMenu.FILTER_OPTICAL);
        assertEquals(1, SpectralSurveyTelescopeMenu.FILTER_INFRARED);
        assertEquals(2, SpectralSurveyTelescopeMenu.FILTER_RADIO_XRAY);

        assertEquals(0, SpectralSurveyTelescopeMenu.TARGET_ANDROMEDA);
        assertEquals(1, SpectralSurveyTelescopeMenu.TARGET_CRAB_NEBULA);
        assertEquals(2, SpectralSurveyTelescopeMenu.TARGET_KEPLER_EXOPLANET);
        assertEquals(3, SpectralSurveyTelescopeMenu.TARGET_CYGNUS_PULSAR);
        assertEquals(4, SpectralSurveyTelescopeMenu.TARGET_SOLAR_CORONA);
    }
}
