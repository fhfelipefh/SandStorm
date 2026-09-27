package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSpecialty;
import com.fhfelipefh.sandstorm.content.gui.CyborgTelemetryMenu;
import com.fhfelipefh.sandstorm.content.item.CyberneticCommandUplinkItem;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.SimpleContainerData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AndroidIntelligenceAndOrderTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldValidateAllCyborgRoutinesIncludingStandby() {
        assertEquals(CyborgRoutine.AUTONOMOUS_WORK, CyborgRoutine.fromOrdinal(0));
        assertEquals(CyborgRoutine.FOLLOW_OPERATOR, CyborgRoutine.fromOrdinal(1));
        assertEquals(CyborgRoutine.PATROL_PERIMETER, CyborgRoutine.fromOrdinal(2));
        assertEquals(CyborgRoutine.RETURN_TO_DOCK, CyborgRoutine.fromOrdinal(3));
        assertEquals(CyborgRoutine.STANDBY, CyborgRoutine.fromOrdinal(4));
        assertEquals(CyborgRoutine.AUTONOMOUS_WORK, CyborgRoutine.fromOrdinal(99));

        assertEquals("autonomous_work", CyborgRoutine.AUTONOMOUS_WORK.getId());
        assertEquals("follow_operator", CyborgRoutine.FOLLOW_OPERATOR.getId());
        assertEquals("patrol_perimeter", CyborgRoutine.PATROL_PERIMETER.getId());
        assertEquals("return_to_dock", CyborgRoutine.RETURN_TO_DOCK.getId());
        assertEquals("standby", CyborgRoutine.STANDBY.getId());
    }

    @Test
    void shouldValidateCyberneticCommandUplinkModes() {
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MINING, CyberneticCommandUplinkItem.UplinkMode.fromOrdinal(0));
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.BUILDING, CyberneticCommandUplinkItem.UplinkMode.fromOrdinal(1));
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.HARVESTING, CyberneticCommandUplinkItem.UplinkMode.fromOrdinal(2));
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.PATROL, CyberneticCommandUplinkItem.UplinkMode.fromOrdinal(3));
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MOVE_ORDER, CyberneticCommandUplinkItem.UplinkMode.fromOrdinal(4));
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MINING, CyberneticCommandUplinkItem.UplinkMode.fromOrdinal(999));

        CyberneticCommandUplinkItem.UplinkMode current = CyberneticCommandUplinkItem.UplinkMode.MINING;
        current = current.next();
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.BUILDING, current);
        current = current.next();
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.HARVESTING, current);
        current = current.next();
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.PATROL, current);
        current = current.next();
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MOVE_ORDER, current);
        current = current.next();
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MINING, current);

        CyberneticCommandUplinkItem.UplinkMode workLoop = CyberneticCommandUplinkItem.UplinkMode.MINING;
        workLoop = workLoop.cycle();
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.BUILDING, workLoop);
        workLoop = workLoop.cycle();
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.HARVESTING, workLoop);
        workLoop = workLoop.cycle();
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MINING, workLoop);

        assertNotNull(CyberneticCommandUplinkItem.UplinkMode.PATROL.getDisplayName());
        assertNotNull(CyberneticCommandUplinkItem.UplinkMode.MOVE_ORDER.getDisplayName());
    }

    @Test
    void shouldValidateTelemetryMenuDataChannelsAndSlots() {
        SimpleContainer container = new SimpleContainer(CyborgTelemetryMenu.CYBORG_SLOTS);
        SimpleContainerData data = new SimpleContainerData(9);
        data.set(0, 15000);
        data.set(1, 50000);
        data.set(2, 2500);
        data.set(3, 4);
        data.set(4, 1);
        data.set(5, 100);
        data.set(6, 64);
        data.set(7, 200);
        data.set(8, 1);

        assertEquals(15000, data.get(0));
        assertEquals(50000, data.get(1));
        assertEquals(4, data.get(3));
        assertEquals(1, data.get(4));

        CyborgSpecialty specialty = CyborgSpecialty.fromOrdinal(data.get(4));
        CyborgRoutine routine = CyborgRoutine.fromOrdinal(data.get(3));
        assertEquals(CyborgSpecialty.BUILDER, specialty);
        assertEquals(CyborgRoutine.STANDBY, routine);
        assertEquals(18, container.getContainerSize());
    }

    @Test
    void shouldVerifyLowPowerDockingThreshold() {
        int maxEnergy = 50000;
        int lowEnergy = 4500;
        int normalEnergy = 25000;

        boolean isLowPower = lowEnergy < (maxEnergy * 0.10) || lowEnergy < 5000;
        boolean isNormalPower = normalEnergy < (maxEnergy * 0.10) || normalEnergy < 5000;

        assertTrue(isLowPower);
        assertFalse(isNormalPower);
    }
}
