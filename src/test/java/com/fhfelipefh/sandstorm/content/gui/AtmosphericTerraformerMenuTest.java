package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericTerraformerBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtmosphericTerraformerMenuTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldCreateAtmosphericTerraformerMenuAndVerifySlots() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(AtmosphericTerraformerBlockEntity.CONTAINER_SIZE);
        SimpleContainerData data = new SimpleContainerData(AtmosphericTerraformerMenu.DATA_COUNT);

        AtmosphericTerraformerMenu menu = new AtmosphericTerraformerMenu(null, 1, playerInv, null, container, data);
        assertEquals(43, menu.slots.size());
        assertFalse(menu.slots.get(AtmosphericTerraformerBlockEntity.SLOT_WATER_OUT).mayPlace(ItemStack.EMPTY));
        assertTrue(menu.slots.get(AtmosphericTerraformerBlockEntity.SLOT_WATER_IN).mayPlace(ItemStack.EMPTY));
        assertTrue(menu.slots.get(AtmosphericTerraformerBlockEntity.SLOT_MINERAL).mayPlace(ItemStack.EMPTY));
        assertTrue(menu.slots.get(AtmosphericTerraformerBlockEntity.SLOT_SEEDS).mayPlace(ItemStack.EMPTY));
        assertTrue(menu.slots.get(AtmosphericTerraformerBlockEntity.SLOT_SAPLINGS).mayPlace(ItemStack.EMPTY));
        assertTrue(menu.slots.get(AtmosphericTerraformerBlockEntity.SLOT_FUEL).mayPlace(ItemStack.EMPTY));
        assertTrue(menu.slots.get(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE).mayPlace(ItemStack.EMPTY));
    }

    @Test
    void shouldSyncContainerDataCorrectly() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(AtmosphericTerraformerBlockEntity.CONTAINER_SIZE);
        SimpleContainerData data = new SimpleContainerData(AtmosphericTerraformerMenu.DATA_COUNT);

        int storedEnergy = 150000;
        int maxEnergy = 250000;
        int waterAmount = 30000;
        int maxWater = 50000;

        data.set(0, storedEnergy & 0xFFFF);
        data.set(1, (storedEnergy >> 16) & 0xFFFF);
        data.set(2, maxEnergy & 0xFFFF);
        data.set(3, (maxEnergy >> 16) & 0xFFFF);
        data.set(4, waterAmount & 0xFFFF);
        data.set(5, (waterAmount >> 16) & 0xFFFF);
        data.set(6, maxWater & 0xFFFF);
        data.set(7, (maxWater >> 16) & 0xFFFF);
        data.set(8, 64);
        data.set(9, 32);
        data.set(10, 16);
        data.set(11, 2);
        data.set(12, 1);
        data.set(13, 1);
        data.set(14, 120);

        AtmosphericTerraformerMenu menu = new AtmosphericTerraformerMenu(null, 1, playerInv, null, container, data);

        assertEquals(150000, menu.getEnergy());
        assertEquals(250000, menu.getMaxEnergy());
        assertEquals(30000, menu.getWaterAmount());
        assertEquals(50000, menu.getMaxWater());
        assertEquals(64, menu.getMineralUnits());
        assertEquals(32, menu.getSeedUnits());
        assertEquals(16, menu.getSaplingCount());
        assertEquals(2, menu.getTier());
        assertTrue(menu.isActive());
        assertTrue(menu.isLightningEnabled());
        assertEquals(120, menu.getDissipatingTicks());
        assertTrue(menu.isDissipating());
        assertEquals(48, menu.getRadius());

        assertEquals(60, menu.getEnergyScaled(100));
        assertEquals(60, menu.getWaterScaled(100));
    }

    @Test
    void shouldHandleButtonClicksForLightningAndTier() {
        AtmosphericTerraformerBlockEntity terraformer = new AtmosphericTerraformerBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        Inventory playerInv = new Inventory(null, null);
        AtmosphericTerraformerMenu menu = new AtmosphericTerraformerMenu(null, 1, playerInv, terraformer, terraformer, terraformer.getDataAccess());

        assertFalse(terraformer.isLightningEnabled());
        boolean clickResult0 = menu.clickMenuButton(null, AtmosphericTerraformerMenu.BUTTON_TOGGLE_LIGHTNING);
        assertTrue(clickResult0);
        assertTrue(terraformer.isLightningEnabled());

        menu.clickMenuButton(null, AtmosphericTerraformerMenu.BUTTON_TOGGLE_LIGHTNING);
        assertFalse(terraformer.isLightningEnabled());

        assertEquals(1, terraformer.getTier());
        boolean clickResult1 = menu.clickMenuButton(null, AtmosphericTerraformerMenu.BUTTON_CYCLE_TIER);
        assertTrue(clickResult1);
        assertEquals(2, terraformer.getTier());

        assertFalse(menu.clickMenuButton(null, -1));
        assertFalse(menu.clickMenuButton(null, 99));
    }
}
