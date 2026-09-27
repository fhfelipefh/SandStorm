package com.fhfelipefh.sandstorm.content.gui;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtendedMachineMenuTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldCreateThermalGeneratorMenuAndVerifyState() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(2);
        SimpleContainerData data = new SimpleContainerData(6);
        data.set(0, 250);
        data.set(1, 1000);
        data.set(2, 40);
        data.set(3, 100);
        data.set(4, 1);
        data.set(5, 2000);

        ThermalGeneratorMenu menu = new ThermalGeneratorMenu(null, 1, playerInv, container, data);
        assertEquals(38, menu.slots.size());
        assertEquals(250, menu.getEnergy());
        assertEquals(1000, menu.getMaxEnergy());
        assertEquals(40, menu.getProgress());
        assertEquals(100, menu.getMaxProgress());
        assertTrue(menu.isWptConnected());
        assertTrue(menu.isProcessing());
        assertEquals(2000, menu.getLavaAmount());
        assertEquals(4000, menu.getMaxLava());
        assertEquals(25, menu.getEnergyScaled(100));
        assertEquals(40, menu.getProgressScaled(100));
        assertEquals(50, menu.getLavaScaled(100));
        assertFalse(menu.slots.get(1).mayPlace(ItemStack.EMPTY));
    }

    @Test
    void shouldCreateChemicalRefineryMenuAndVerifyState() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(5);
        SimpleContainerData data = new SimpleContainerData(6);
        data.set(0, 400);
        data.set(1, 2000);
        data.set(2, 50);
        data.set(3, 100);
        data.set(4, 1);
        data.set(5, 1);

        ChemicalRefineryMenu menu = new ChemicalRefineryMenu(null, 1, playerInv, container, data);
        assertEquals(41, menu.slots.size());
        assertEquals(400, menu.getEnergy());
        assertEquals(2000, menu.getMaxEnergy());
        assertEquals(50, menu.getProgress());
        assertEquals(100, menu.getMaxProgress());
        assertTrue(menu.isWptConnected());
        assertTrue(menu.isProcessing());
        assertEquals(20, menu.getEnergyScaled(100));
        assertEquals(50, menu.getProgressScaled(100));
        assertFalse(menu.slots.get(4).mayPlace(ItemStack.EMPTY));
    }

    @Test
    void shouldCreateKineticShieldMenuAndVerifyState() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(1);
        SimpleContainerData data = new SimpleContainerData(7);
        data.set(0, 8000);
        data.set(1, 10000);
        data.set(2, 1);
        data.set(3, 24);
        data.set(4, 12);
        data.set(5, 1);
        data.set(6, 1);

        KineticShieldMenu menu = new KineticShieldMenu(null, 1, playerInv, null, container, data);
        assertEquals(37, menu.slots.size());
        assertEquals(8000, menu.getStoredEnergy());
        assertEquals(10000, menu.getMaxEnergy());
        assertTrue(menu.isShieldActive());
        assertEquals(24, menu.getShieldRadius());
        assertEquals(12, menu.getTotalDeflections());
        assertTrue(menu.isUserEnabled());
        assertTrue(menu.isWptConnected());
        assertEquals(80, menu.getEnergyScaled(100));
        assertFalse(menu.clickMenuButton(null, 0));
        assertFalse(menu.clickMenuButton(null, 99));
    }

    @Test
    void shouldCreatePlasmaShieldMenuAndVerifyState() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(1);
        SimpleContainerData data = new SimpleContainerData(5);
        data.set(0, 15000);
        data.set(1, 20000);
        data.set(2, 1);
        data.set(3, 32);
        data.set(4, 5);

        PlasmaShieldMenu menu = new PlasmaShieldMenu(null, 1, playerInv, null, container, data);
        assertEquals(37, menu.slots.size());
        assertEquals(15000, menu.getStoredEnergy());
        assertEquals(20000, menu.getMaxEnergy());
        assertTrue(menu.isShieldActive());
        assertEquals(32, menu.getShieldRadius());
        assertEquals(5, menu.getThreatCount());
        assertEquals(75, menu.getEnergyScaled(100));
        assertFalse(menu.clickMenuButton(null, 0));
        assertFalse(menu.clickMenuButton(null, 1));
        assertFalse(menu.clickMenuButton(null, 99));
    }

    @Test
    void shouldCreateOrbitalMassDriverMenuAndVerifyState() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(1);
        SimpleContainerData data = new SimpleContainerData(4);
        data.set(0, 50000);
        data.set(1, 100000);
        data.set(2, 0);
        data.set(3, 1);

        OrbitalMassDriverMenu menu = new OrbitalMassDriverMenu(null, 1, playerInv, null, container, data);
        assertEquals(37, menu.slots.size());
        assertEquals(50000, menu.getStoredEnergy());
        assertEquals(100000, menu.getMaxEnergy());
        assertEquals(0, menu.getLaunchCooldown());
        assertTrue(menu.canLaunch());
        assertEquals(50, menu.getEnergyScaled(100));
        assertFalse(menu.clickMenuButton(null, 0));
        assertFalse(menu.clickMenuButton(null, 99));
    }

    @Test
    void shouldCreateKineticRailgunMenuAndVerifyState() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(9);
        SimpleContainerData data = new SimpleContainerData(4);
        data.set(0, 12000);
        data.set(1, 15000);
        data.set(2, 30);
        data.set(3, 42);

        KineticRailgunMenu menu = new KineticRailgunMenu(null, 1, playerInv, null, container, data);
        assertEquals(45, menu.slots.size());
        assertEquals(12000, menu.getStoredEnergy());
        assertEquals(15000, menu.getMaxEnergy());
        assertEquals(30, menu.getCooldown());
        assertEquals(42, menu.getTotalShotsFired());
        assertEquals(80, menu.getEnergyScaled(100));
        assertEquals(50, menu.getCooldownScaled(100));
    }
}
