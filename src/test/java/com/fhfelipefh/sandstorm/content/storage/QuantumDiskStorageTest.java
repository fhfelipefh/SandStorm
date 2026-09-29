package com.fhfelipefh.sandstorm.content.storage;

import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuantumDiskStorageTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        DataComponentMap defaultComponents = DataComponentMap.builder()
                .set(DataComponents.MAX_STACK_SIZE, 64)
                .build();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
    }

    @Test
    void shouldInsertAndExtractItemsPreservingExactCount() {
        ItemStack cartridge = new ItemStack(Items.STICK);

        ItemStack cobblestone = new ItemStack(Items.COBBLESTONE, 500);
        cobblestone.set(DataComponents.MAX_STACK_SIZE, 64);
        long insertedCobble = QuantumDiskStorage.insertItem(cartridge, cobblestone, StorageCartridgeTier.TIER_1K);
        assertEquals(500, insertedCobble);
        assertEquals(500, QuantumDiskStorage.getTotalItemCount(cartridge));
        assertEquals(1, QuantumDiskStorage.getStoredTypeCount(cartridge));

        ItemStack iron = new ItemStack(Items.IRON_INGOT, 200);
        iron.set(DataComponents.MAX_STACK_SIZE, 64);
        long insertedIron = QuantumDiskStorage.insertItem(cartridge, iron, StorageCartridgeTier.TIER_1K);
        assertEquals(200, insertedIron);
        assertEquals(700, QuantumDiskStorage.getTotalItemCount(cartridge));
        assertEquals(2, QuantumDiskStorage.getStoredTypeCount(cartridge));

        ItemStack filterCobble = new ItemStack(Items.COBBLESTONE);
        filterCobble.set(DataComponents.MAX_STACK_SIZE, 64);
        ItemStack extractedCobble = QuantumDiskStorage.extractItem(cartridge, filterCobble, 64);
        assertEquals(64, extractedCobble.getCount());
        assertEquals(Items.COBBLESTONE, extractedCobble.getItem());
        assertEquals(636, QuantumDiskStorage.getTotalItemCount(cartridge));

        ItemStack filterIron = new ItemStack(Items.IRON_INGOT);
        filterIron.set(DataComponents.MAX_STACK_SIZE, 64);
        ItemStack extractedIron = QuantumDiskStorage.extractItem(cartridge, filterIron, 500);
        assertEquals(64, extractedIron.getCount());
        assertEquals(Items.IRON_INGOT, extractedIron.getItem());
        assertEquals(572, QuantumDiskStorage.getTotalItemCount(cartridge));
        assertEquals(2, QuantumDiskStorage.getStoredTypeCount(cartridge));
    }

    @Test
    void shouldRespectMaxCapacityOfTier() {
        ItemStack cartridge = new ItemStack(Items.STICK);
        ItemStack sand = new ItemStack(Items.SAND, 1500);

        long inserted = QuantumDiskStorage.insertItem(cartridge, sand, StorageCartridgeTier.TIER_1K);
        assertEquals(1000, inserted);
        assertEquals(1000, QuantumDiskStorage.getTotalItemCount(cartridge));

        long insertedOverflow = QuantumDiskStorage.insertItem(cartridge, new ItemStack(Items.DIRT, 10), StorageCartridgeTier.TIER_1K);
        assertEquals(0, insertedOverflow);
        assertEquals(1000, QuantumDiskStorage.getTotalItemCount(cartridge));
    }

    @Test
    void shouldRespectMaxTypesOfTier() {
        ItemStack cartridge = new ItemStack(Items.STICK);

        for (int i = 0; i < 64; i++) {
            ItemStack uniqueItem = new ItemStack(Items.STICK, 1);
            uniqueItem.set(DataComponents.CUSTOM_NAME, Component.literal("Item#" + i));
            long inserted = QuantumDiskStorage.insertItem(cartridge, uniqueItem, StorageCartridgeTier.TIER_1K);
            assertEquals(1, inserted);
        }

        assertEquals(64, QuantumDiskStorage.getStoredTypeCount(cartridge));

        ItemStack sixtyFifth = new ItemStack(Items.STICK, 1);
        sixtyFifth.set(DataComponents.CUSTOM_NAME, Component.literal("Item#65"));
        long rejected = QuantumDiskStorage.insertItem(cartridge, sixtyFifth, StorageCartridgeTier.TIER_1K);
        assertEquals(0, rejected);
        assertEquals(64, QuantumDiskStorage.getStoredTypeCount(cartridge));
    }

    @Test
    void shouldPreserveCustomDataAndComponentsWithoutLoss() {
        ItemStack cartridge = new ItemStack(Items.STICK);
        ItemStack customSword = new ItemStack(Items.DIAMOND_SWORD, 1);
        customSword.set(DataComponents.CUSTOM_NAME, Component.literal("Excalibur"));
        customSword.set(DataComponents.DAMAGE, 45);

        long inserted = QuantumDiskStorage.insertItem(cartridge, customSword, StorageCartridgeTier.TIER_1K);
        assertEquals(1, inserted);

        ItemStack extracted = QuantumDiskStorage.extractItem(cartridge, customSword, 1);
        assertFalse(extracted.isEmpty());
        assertEquals("Excalibur", extracted.getHoverName().getString());
        assertEquals(45, (int) extracted.getOrDefault(DataComponents.DAMAGE, 0));
    }

    @Test
    void shouldHandleEmptyAndInvalidExtractionsGracefully() {
        ItemStack cartridge = new ItemStack(Items.STICK);
        assertTrue(QuantumDiskStorage.getStoredItems(cartridge).isEmpty());
        assertEquals(0, QuantumDiskStorage.getTotalItemCount(cartridge));

        ItemStack extracted = QuantumDiskStorage.extractItem(cartridge, new ItemStack(Items.DIAMOND), 64);
        assertTrue(extracted.isEmpty());

        ItemStack nullExtracted = QuantumDiskStorage.extractItem(null, new ItemStack(Items.DIAMOND), 64);
        assertTrue(nullExtracted.isEmpty());
    }

    @Test
    void shouldProperlyListStoredItemsInCorrectFormat() {
        ItemStack cartridge = new ItemStack(Items.STICK);
        QuantumDiskStorage.insertItem(cartridge, new ItemStack(Items.COPPER_INGOT, 50), StorageCartridgeTier.TIER_4K);
        QuantumDiskStorage.insertItem(cartridge, new ItemStack(Items.GOLD_INGOT, 25), StorageCartridgeTier.TIER_4K);

        List<StoredItemEntry> entries = QuantumDiskStorage.getStoredItems(cartridge);
        assertEquals(2, entries.size());

        boolean hasCopper = entries.stream().anyMatch(e -> e.template().getItem() == Items.COPPER_INGOT && e.count() == 50);
        boolean hasGold = entries.stream().anyMatch(e -> e.template().getItem() == Items.GOLD_INGOT && e.count() == 25);
        assertTrue(hasCopper);
        assertTrue(hasGold);
    }
}
