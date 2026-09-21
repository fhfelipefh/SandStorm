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

class MachineMenuTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldCreatePrinter3DMenuAndVerifySlots() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(4);
        SimpleContainerData data = new SimpleContainerData(6);
        data.set(0, 500);
        data.set(1, 10000);
        data.set(2, 50);
        data.set(3, 100);
        data.set(4, 1);
        data.set(5, 1);

        Printer3DMenu menu = new Printer3DMenu(null, 1, playerInv, container, data);
        assertEquals(40, menu.slots.size());
        assertEquals(500, menu.getEnergy());
        assertEquals(10000, menu.getMaxEnergy());
        assertEquals(50, menu.getProgress());
        assertEquals(100, menu.getMaxProgress());
        assertTrue(menu.isWptConnected());
        assertTrue(menu.isProcessing());
        assertEquals(5, menu.getProgressScaled(10));
        assertEquals(1, menu.getEnergyScaled(32));
        assertFalse(menu.slots.get(2).mayPlace(ItemStack.EMPTY));
    }

    @Test
    void shouldCreateNaniteFabricatorMenuAndVerifySlots() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(4);
        SimpleContainerData data = new SimpleContainerData(6);

        NaniteFabricatorMenu menu = new NaniteFabricatorMenu(null, 1, playerInv, container, data);
        assertEquals(40, menu.slots.size());
        assertFalse(menu.slots.get(2).mayPlace(ItemStack.EMPTY));
    }

    @Test
    void shouldCreateDesalinationFilterMenuAndVerifySlots() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(5);
        SimpleContainerData data = new SimpleContainerData(10);

        DesalinationFilterMenu menu = new DesalinationFilterMenu(null, 1, playerInv, container, data);
        assertEquals(41, menu.slots.size());
        assertFalse(menu.slots.get(1).mayPlace(ItemStack.EMPTY));
        assertFalse(menu.slots.get(2).mayPlace(ItemStack.EMPTY));
    }
}
