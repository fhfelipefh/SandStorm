package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.ChemicalRefineryBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChemicalRefineryBlockEntityTest {

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

        assertNotNull(SandStormItems.POTABLE_WATER_BOTTLE);

        DataComponentMap defaultComponents = DataComponentMap.builder()
                .set(DataComponents.MAX_STACK_SIZE, 64)
                .build();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
    }

    private ChemicalRefineryBlockEntity createTestEntity() {
        return new ChemicalRefineryBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
    }

    @Test
    void shouldInitializeWithDefaultValues() {
        ChemicalRefineryBlockEntity be = createTestEntity();

        assertEquals(5, be.getContainerSize());
        assertTrue(be.isEmpty());
        assertNotNull(be.getDisplayName());
    }

    @Test
    void shouldValidateSlotFilteringRules() {
        ChemicalRefineryBlockEntity be = createTestEntity();

        assertTrue(be.canPlaceItem(0, new ItemStack(SandStormItems.MINERAL_SALT)));
        assertFalse(be.canPlaceItem(0, new ItemStack(Items.SUGAR)));

        assertTrue(be.canPlaceItem(1, new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE)));
        assertTrue(be.canPlaceItem(1, new ItemStack(SandStormItems.BRACKISH_WATER_BOTTLE)));
        assertFalse(be.canPlaceItem(1, new ItemStack(Items.WATER_BUCKET)));

        assertTrue(be.canPlaceItem(2, new ItemStack(SandStormItems.EMPTY_CARTRIDGE)));
        assertFalse(be.canPlaceItem(2, new ItemStack(Items.GLASS_BOTTLE)));

        assertTrue(be.canPlaceItem(3, new ItemStack(Items.REDSTONE)));
        assertTrue(be.canPlaceItem(3, new ItemStack(Items.REDSTONE_BLOCK)));
        assertFalse(be.canPlaceItem(3, new ItemStack(Items.COAL)));

        assertFalse(be.canPlaceItem(4, new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE)));
    }

    @Test
    void shouldValidateAutomationFaceAccess() {
        ChemicalRefineryBlockEntity be = createTestEntity();

        int[] upSlots = be.getSlotsForFace(Direction.UP);
        assertEquals(4, upSlots.length);
        assertEquals(0, upSlots[0]);
        assertEquals(1, upSlots[1]);
        assertEquals(2, upSlots[2]);
        assertEquals(3, upSlots[3]);

        int[] downSlots = be.getSlotsForFace(Direction.DOWN);
        assertEquals(2, downSlots.length);
        assertEquals(4, downSlots[0]);
        assertEquals(3, downSlots[1]);

        int[] sideSlots = be.getSlotsForFace(Direction.NORTH);
        assertEquals(5, sideSlots.length);

        assertTrue(be.canTakeItem(null, 4, new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE)));
        assertFalse(be.canTakeItem(null, 0, new ItemStack(SandStormItems.MINERAL_SALT)));
        assertFalse(be.canTakeItem(null, 1, new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE)));
        assertFalse(be.canTakeItem(null, 2, new ItemStack(SandStormItems.EMPTY_CARTRIDGE)));

        assertTrue(be.canPlaceItemThroughFace(3, new ItemStack(Items.REDSTONE), Direction.DOWN));
        assertFalse(be.canPlaceItemThroughFace(0, new ItemStack(SandStormItems.MINERAL_SALT), Direction.DOWN));
    }

    @Test
    void shouldProducePropellantCartridgesWhenInputsValid() {
        ChemicalRefineryBlockEntity be = createTestEntity();

        be.setItem(0, new ItemStack(SandStormItems.MINERAL_SALT, 2));
        be.setItem(1, new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE, 2));
        be.setItem(2, new ItemStack(SandStormItems.EMPTY_CARTRIDGE, 2));

        assertTrue(be.canProcess());

        be.processRecipe();

        assertEquals(1, be.getItem(0).getCount());
        assertEquals(1, be.getItem(1).getCount());
        assertEquals(1, be.getItem(2).getCount());
        assertEquals(1, be.getItem(4).getCount());
        assertEquals(SandStormItems.PROPELLANT_CARTRIDGE, be.getItem(4).getItem());

        be.processRecipe();

        assertTrue(be.getItem(0).isEmpty());
        assertTrue(be.getItem(1).isEmpty());
        assertTrue(be.getItem(2).isEmpty());
        assertEquals(2, be.getItem(4).getCount());

        assertFalse(be.canProcess());
    }

    @Test
    void shouldSynchronizeContainerData() {
        ChemicalRefineryBlockEntity be = createTestEntity();
        be.setEnergy(5000);

        ContainerData data = be.getDataAccess();
        assertEquals(6, data.getCount());
        assertEquals(5000, data.get(0));
        assertEquals(10000, data.get(1));
        assertEquals(0, data.get(2));
        assertEquals(100, data.get(3));
    }
}
