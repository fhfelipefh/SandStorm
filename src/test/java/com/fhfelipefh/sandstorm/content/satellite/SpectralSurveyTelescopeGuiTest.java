package com.fhfelipefh.sandstorm.content.satellite;

import com.fhfelipefh.sandstorm.content.gui.SpectralSurveyTelescopeMenu;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        SimpleContainerData data = new SimpleContainerData(10);
        data.set(0, 12500 & 0xFFFF);
        data.set(1, (12500 >> 16) & 0xFFFF);
        data.set(2, 50000 & 0xFFFF);
        data.set(3, (50000 >> 16) & 0xFFFF);
        data.set(4, SpectralSurveyTelescopeMenu.FILTER_INFRARED);
        data.set(5, SpectralSurveyTelescopeMenu.TARGET_CRAB_NEBULA);
        data.set(6, 50);
        data.set(7, 1);
        data.set(8, 2);
        data.set(9, 180);

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
    void shouldCorrectlyHandleLargeEnergyValuesAcross16BitNetworkSync() {
        Inventory inventory = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(1);
        SimpleContainerData data = new SimpleContainerData(10);
        short lowStored = (short) 50000;
        short highStored = (short) 0;
        short lowMax = (short) 50000;
        short highMax = (short) 0;
        data.set(0, lowStored);
        data.set(1, highStored);
        data.set(2, lowMax);
        data.set(3, highMax);

        SpectralSurveyTelescopeMenu menu = new SpectralSurveyTelescopeMenu(
                null,
                1,
                inventory,
                null,
                container,
                data
        );

        assertEquals(50000, menu.getStoredEnergy());
        assertEquals(50000, menu.getMaxEnergy());
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

    @Test
    void shouldVerifyScreenLayoutSeparationAndNonOverlappingLabels() throws IOException {
        Path screenPath = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "gui", "SpectralSurveyTelescopeScreen.java");
        String content = Files.readString(screenPath);

        assertTrue(content.contains("drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 90, 0xFF78909C);"));
        assertTrue(content.contains("drawAdaptiveText(extractor, weatherText, x + 142, y + 111, 78, weatherColor);"));
        assertTrue(content.contains("drawAdaptiveText(extractor, skyHud, vx + 6, vy + vh - 13, 56, skyColor);"));
        assertFalse(content.contains("drawAdaptiveText(extractor, skyText, x + 10, y + 110"));
        assertFalse(content.contains("\\n"));
        assertFalse(content.contains("\\r"));
    }
}
