package com.fhfelipefh.sandstorm.content.storage;

import com.fhfelipefh.sandstorm.content.gui.QuantumTerminalMenu;
import com.fhfelipefh.sandstorm.content.network.TerminalActionPayload;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.network.chat.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuantumTerminalMenuTest {

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
    void shouldUpdateClientTerminalPosAndState() {
        SimpleContainer mockInventoryContainer = new SimpleContainer(36);
        Inventory mockInventory = new Inventory(null, null) {
            @Override
            public ItemStack getItem(int slot) {
                return mockInventoryContainer.getItem(slot);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                mockInventoryContainer.setItem(slot, stack);
            }

            @Override
            public int getContainerSize() {
                return mockInventoryContainer.getContainerSize();
            }
        };

        QuantumTerminalMenu menu = new QuantumTerminalMenu(null, 1, mockInventory, BlockPos.ZERO);
        assertEquals(BlockPos.ZERO, menu.getTerminalPos());

        BlockPos realPos = new BlockPos(100, 64, -200);
        List<StoredItemEntry> items = List.of(
                new StoredItemEntry(new ItemStack(Items.IRON_INGOT), 1500)
        );

        menu.updateClientState(realPos, items, 5000, 100000, 1500, 4000);

        assertEquals(realPos, menu.getTerminalPos());
        assertEquals(1, menu.getClientItems().size());
        assertEquals(5000, menu.getClientEnergy());
        assertEquals(100000, menu.getClientMaxEnergy());
        assertEquals(1500, menu.getClientTotalStored());
        assertEquals(4000, menu.getClientTotalCapacity());
    }

    @Test
    void shouldValidateStorageCartridgeTiers() {
        assertEquals(1000, StorageCartridgeTier.TIER_1K.getCapacity());
        assertEquals(4000, StorageCartridgeTier.TIER_4K.getCapacity());
        assertEquals(16000, StorageCartridgeTier.TIER_16K.getCapacity());
        assertEquals(64000, StorageCartridgeTier.TIER_64K.getCapacity());
        assertEquals(256000, StorageCartridgeTier.DIMENSIONAL.getCapacity());

        assertEquals(64, StorageCartridgeTier.TIER_1K.getMaxTypes());
        assertEquals(128, StorageCartridgeTier.TIER_4K.getMaxTypes());
        assertEquals(256, StorageCartridgeTier.TIER_16K.getMaxTypes());
        assertEquals(512, StorageCartridgeTier.TIER_64K.getMaxTypes());
        assertEquals(1024, StorageCartridgeTier.DIMENSIONAL.getMaxTypes());
    }

    @Test
    void shouldValidateActionPayloadActionTypes() {
        assertEquals(0, TerminalActionPayload.ACTION_EXTRACT_STACK);
        assertEquals(1, TerminalActionPayload.ACTION_EXTRACT_HALF);
        assertEquals(2, TerminalActionPayload.ACTION_SHIFT_EXTRACT);
        assertEquals(3, TerminalActionPayload.ACTION_INSERT_HELD);

        BlockPos pos = new BlockPos(10, 20, 30);
        ItemStack stack = new ItemStack(Items.GOLD_INGOT, 32);
        TerminalActionPayload payload = new TerminalActionPayload(pos, stack, TerminalActionPayload.ACTION_EXTRACT_STACK);

        assertEquals(pos, payload.terminalPos());
        assertEquals(stack, payload.filterStack());
        assertEquals(TerminalActionPayload.ACTION_EXTRACT_STACK, payload.actionType());
        assertNotNull(payload.type());
    }

    @Test
    void shouldVerifyDiskStorageEdgeCases() {
        ItemStack cartridge = new ItemStack(Items.STICK);
        ItemStack diamonds = new ItemStack(Items.DIAMOND, 64);
        diamonds.set(DataComponents.MAX_STACK_SIZE, 64);

        long inserted = QuantumDiskStorage.insertItem(cartridge, diamonds, StorageCartridgeTier.TIER_1K);
        assertEquals(64, inserted);

        ItemStack filterEmerald = new ItemStack(Items.EMERALD);
        filterEmerald.set(DataComponents.MAX_STACK_SIZE, 64);
        ItemStack extractedNone = QuantumDiskStorage.extractItem(cartridge, filterEmerald, 64);
        assertTrue(extractedNone.isEmpty());

        ItemStack filterDiamond = new ItemStack(Items.DIAMOND);
        filterDiamond.set(DataComponents.MAX_STACK_SIZE, 64);
        ItemStack extractedDiamonds = QuantumDiskStorage.extractItem(cartridge, filterDiamond, 10);
        assertEquals(10, extractedDiamonds.getCount());
        assertEquals(54, QuantumDiskStorage.getTotalItemCount(cartridge));
    }

    @Test
    void shouldTrackClientStateDirtyFlag() {
        SimpleContainer mockInventoryContainer = new SimpleContainer(36);
        Inventory mockInventory = new Inventory(null, null) {
            @Override
            public ItemStack getItem(int slot) {
                return mockInventoryContainer.getItem(slot);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                mockInventoryContainer.setItem(slot, stack);
            }

            @Override
            public int getContainerSize() {
                return mockInventoryContainer.getContainerSize();
            }
        };

        QuantumTerminalMenu menu = new QuantumTerminalMenu(null, 1, mockInventory, BlockPos.ZERO);
        assertFalse(menu.isClientStateDirty());

        menu.updateClientState(new BlockPos(10, 20, 30), List.of(), 500, 1000, 50, 100);
        assertTrue(menu.isClientStateDirty());

        menu.clearClientStateDirty();
        assertFalse(menu.isClientStateDirty());
    }

    @Test
    void shouldValidateTerminalBlockEntityDisplayName() {
        QuantumAccessTerminalBlockEntity terminal = new QuantumAccessTerminalBlockEntity(
                BlockPos.ZERO,
                SandStormBlocks.QUANTUM_ACCESS_TERMINAL.defaultBlockState()
        );
        assertEquals(Component.translatable("container.sandstorm.quantum_access_terminal"), terminal.getDisplayName());
    }
}
