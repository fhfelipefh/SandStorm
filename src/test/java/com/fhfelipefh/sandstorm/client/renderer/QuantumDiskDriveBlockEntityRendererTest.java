package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskDriveBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskDriveBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuantumDiskDriveBlockEntityRendererTest {

    @BeforeAll
    static void init() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BuiltInRegistries.BLOCK, false);
        frozenField.set(BuiltInRegistries.ITEM, false);
        frozenField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());

        assertNotNull(SandStormBlocks.QUANTUM_DISK_DRIVE);
        assertNotNull(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_1K);

        DataComponentMap defaultComponents = DataComponentMap.builder()
                .set(DataComponents.MAX_STACK_SIZE, 64)
                .build();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
        for (Block block : BuiltInRegistries.BLOCK) {
            Item blockItem = block.asItem();
            if (blockItem != null && !blockItem.builtInRegistryHolder().areComponentsBound()) {
                blockItem.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
    }

    @Test
    void testRenderStateDefaultsAndCartridgeSlots() {
        QuantumDiskDriveRenderState state = new QuantumDiskDriveRenderState();
        assertEquals(Direction.NORTH, state.facing);
        assertEquals(0, state.totalStored);
        assertEquals(0, state.totalCapacity);
        assertEquals(0, state.activeDisks);
        assertFalse(state.isIoActive);

        for (int i = 0; i < 8; i++) {
            assertFalse(state.hasCartridge[i]);
            assertEquals(0, state.storedCount[i]);
            assertEquals(0, state.capacity[i]);
            assertEquals(0, state.fillPercentage[i]);
        }

        state.hasCartridge[0] = true;
        state.storedCount[0] = 500;
        state.capacity[0] = 1000;
        state.fillPercentage[0] = 50;

        state.hasCartridge[3] = true;
        state.storedCount[3] = 950;
        state.capacity[3] = 1000;
        state.fillPercentage[3] = 95;

        state.totalStored = 1450;
        state.totalCapacity = 2000;
        state.activeDisks = 2;

        assertTrue(state.hasCartridge[0]);
        assertEquals(50, state.fillPercentage[0]);
        assertTrue(state.hasCartridge[3]);
        assertEquals(95, state.fillPercentage[3]);
        assertEquals(1450, state.totalStored);
        assertEquals(2000, state.totalCapacity);
        assertEquals(2, state.activeDisks);
    }

    @Test
    void testIoActivityDetection() {
        QuantumDiskDriveRenderState state = new QuantumDiskDriveRenderState();
        state.animationTicks = 100.0f;
        state.lastActivityTick = 90L;
        state.isIoActive = (state.animationTicks - state.lastActivityTick) >= 0.0f
                && (state.animationTicks - state.lastActivityTick) < 18.0f;
        assertTrue(state.isIoActive);

        state.animationTicks = 120.0f;
        state.isIoActive = (state.animationTicks - state.lastActivityTick) >= 0.0f
                && (state.animationTicks - state.lastActivityTick) < 18.0f;
        assertFalse(state.isIoActive);
    }

    @Test
    void testBlockEntityContainerRules() {
        QuantumDiskDriveBlockEntity drive = new QuantumDiskDriveBlockEntity(
                BlockPos.ZERO,
                SandStormBlocks.QUANTUM_DISK_DRIVE.defaultBlockState().setValue(QuantumDiskDriveBlock.FACING, Direction.SOUTH)
        );

        assertEquals(8, drive.getContainerSize());
        assertEquals(0, drive.getLastActivityTick());

        ItemStack cartridgeStack = new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_1K);
        ItemStack invalidStack = new ItemStack(Items.DIRT);

        assertTrue(drive.canPlaceItem(0, cartridgeStack));
        assertFalse(drive.canPlaceItem(0, invalidStack));
        assertFalse(drive.canPlaceItem(0, ItemStack.EMPTY));
    }
}
