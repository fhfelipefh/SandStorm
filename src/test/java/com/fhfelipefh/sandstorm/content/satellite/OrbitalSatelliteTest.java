package com.fhfelipefh.sandstorm.content.satellite;

import com.fhfelipefh.sandstorm.client.gui.DatapadClientHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrbitalSatelliteTest {

    @Test
    void shouldInitializeDefaultSavedData() {
        SatelliteSavedData data = new SatelliteSavedData();
        assertFalse(data.isSatelliteActive());
        assertEquals(0L, data.getLaunchGameTime());

        data.setSatelliteActive(true);
        assertTrue(data.isSatelliteActive());

        data.setLaunchGameTime(24000L);
        assertEquals(24000L, data.getLaunchGameTime());
    }

    @Test
    void shouldTrackSatelliteTelemetryOnClient() {
        DatapadClientHelper.setSatelliteActive(false);
        assertFalse(DatapadClientHelper.isSatelliteActive());

        DatapadClientHelper.setSatelliteActive(true);
        assertTrue(DatapadClientHelper.isSatelliteActive());

        DatapadClientHelper.setNextStormSeconds(360);
        assertEquals(360, DatapadClientHelper.getNextStormSeconds());
    }
}
