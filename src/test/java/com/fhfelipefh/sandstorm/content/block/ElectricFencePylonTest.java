package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.ElectricFencePylonBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.ElectricFencePylonMenu;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElectricFencePylonTest {

    @BeforeAll
    static void setup() throws Exception {
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

        DataComponentMap defaultComponents = DataComponentMap.builder()
                .set(DataComponents.MAX_STACK_SIZE, 64)
                .build();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
    }

    private ElectricFencePylonBlockEntity createTestEntity() {
        return new ElectricFencePylonBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
    }

    @Test
    void shouldDefinePropertiesCorrectly() {
        assertEquals("lit", ElectricFencePylonBlock.LIT.getName());
        assertEquals("connected", ElectricFencePylonBlock.CONNECTED.getName());
        assertTrue(ElectricFencePylonBlock.LIT.getPossibleValues().contains(true));
        assertTrue(ElectricFencePylonBlock.LIT.getPossibleValues().contains(false));
        assertTrue(ElectricFencePylonBlock.CONNECTED.getPossibleValues().contains(true));
        assertTrue(ElectricFencePylonBlock.CONNECTED.getPossibleValues().contains(false));
    }

    @Test
    void shouldCreateBlockEntity() {
        ElectricFencePylonBlockEntity be = createTestEntity();
        assertNotNull(be);
        assertEquals(0, be.getEnergy());
        assertEquals(0, be.getMode());
        assertFalse(be.isArmed());
        assertFalse(be.isConnected());
        assertNotNull(be.getDisplayName());
    }

    @Test
    void shouldHandleEnergyClamping() {
        ElectricFencePylonBlockEntity be = createTestEntity();

        be.setEnergy(5000);
        assertEquals(5000, be.getEnergy());

        be.setEnergy(ElectricFencePylonBlockEntity.MAX_ENERGY + 10000);
        assertEquals(ElectricFencePylonBlockEntity.MAX_ENERGY, be.getEnergy());

        be.setEnergy(-500);
        assertEquals(0, be.getEnergy());
    }

    @Test
    void shouldCycleModesCorrectly() {
        ElectricFencePylonBlockEntity be = createTestEntity();

        assertEquals(0, be.getMode());

        be.cycleMode();
        assertEquals(1, be.getMode());

        be.cycleMode();
        assertEquals(2, be.getMode());

        be.cycleMode();
        assertEquals(0, be.getMode());
    }

    @Test
    void shouldRespectArmingRulesByMode() {
        ElectricFencePylonBlockEntity be = createTestEntity();

        be.setMode(1);
        be.setEnergy(0);
        assertFalse(be.isArmed());

        be.setEnergy(1000);
        assertTrue(be.isArmed());

        be.setMode(2);
        assertFalse(be.isArmed());
    }

    @Test
    void shouldSyncDataWithMenu() {
        ElectricFencePylonBlockEntity be = createTestEntity();

        be.setEnergy(12345);
        be.setMode(1);

        ContainerData data = be.getDataAccess();
        int low = data.get(0);
        int high = data.get(1);
        int reconstructed = (low & 0xFFFF) | ((high & 0xFFFF) << 16);
        assertEquals(12345, reconstructed);
        assertEquals(1, data.get(2));
        assertEquals(1, data.get(3));
        assertEquals(0, data.get(4));
        assertEquals(0, data.get(5));
    }

    @Test
    void shouldValidateFuelItems() {
        assertEquals(400, BaseMachineBlockEntity.getFuelEnergy(new ItemStack(Items.REDSTONE)));
        assertEquals(3600, BaseMachineBlockEntity.getFuelEnergy(new ItemStack(Items.REDSTONE_BLOCK)));
        assertEquals(0, BaseMachineBlockEntity.getFuelEnergy(new ItemStack(Items.COBBLESTONE)));
    }

    @Test
    void shouldCreateMenuAndExposeProperties() {
        ElectricFencePylonBlockEntity be = createTestEntity();
        be.setEnergy(15000);
        be.setMode(1);

        ElectricFencePylonMenu menu = new ElectricFencePylonMenu(null, 0, new Inventory(null, null), be, be.getDataAccess());
        assertEquals(15000, menu.getStoredEnergy());
        assertEquals(ElectricFencePylonBlockEntity.MAX_ENERGY, menu.getMaxEnergy());
        assertEquals(1, menu.getMode());
        assertTrue(menu.isArmed());
        assertFalse(menu.isConnected());
        assertEquals(0, menu.getConnectedCount());
        assertTrue(menu.getChargePercentage() > 0.7f);
    }
}
