package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.CryoRevivalVatBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.CryoRevivalVatMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
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

class CryoRevivalVatGuiAndTransferApiTest {

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
        frozenField.set(BuiltInRegistries.MENU, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());

        assertNotNull(SandStormBlocks.CRYO_REVIVAL_VAT);
        assertNotNull(SandStormItems.BIO_NEURAL_CORE);

        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void testMenuSlotsAndInteraction() {
        Inventory inventory = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(3);
        SimpleContainerData data = new SimpleContainerData(6);
        CryoRevivalVatMenu menu = new CryoRevivalVatMenu(null, 1, inventory, container, data);

        assertEquals(39, menu.slots.size());
        assertFalse(menu.slots.get(2).mayPlace(new ItemStack(SandStormItems.BIO_NEURAL_CORE)));
        assertTrue(menu.slots.get(0).mayPlace(new ItemStack(SandStormBlocks.FOSSILIZED_AMBER)));
        assertTrue(menu.slots.get(1).mayPlace(new ItemStack(SandStormBlocks.BRITTLE_PERMAFROST)));
    }

    @Test
    void testMenuProgressAndFluidScalers() {
        SimpleContainer container = new SimpleContainer(3);
        SimpleContainerData data = new SimpleContainerData(6);
        data.set(0, 50);
        data.set(1, 100);
        data.set(2, 2000);
        data.set(3, 4000);
        data.set(4, 1);
        data.set(5, -196);

        Inventory inventory = new Inventory(null, null);
        CryoRevivalVatMenu menu = new CryoRevivalVatMenu(null, 1, inventory, container, data);

        assertEquals(50, menu.getProgress());
        assertEquals(100, menu.getMaxProgress());
        assertEquals(34, menu.getProgressScaled(68));
        assertEquals(2000, menu.getFluidAmount());
        assertEquals(4000, menu.getMaxFluid());
        assertEquals(24, menu.getFluidScaled(48));
        assertEquals(1, menu.getStatus());
        assertEquals(-196, menu.getTemperature());
    }

    @Test
    void testBlockEntityFabricTransferApiIntegration() {
        CryoRevivalVatBlockEntity be = new CryoRevivalVatBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        Storage<FluidVariant> fluidStorage = be.getFluidStorage(Direction.NORTH);
        assertNotNull(fluidStorage);
        assertTrue(fluidStorage.supportsInsertion());
        assertFalse(fluidStorage.supportsExtraction());

        int[] topSlots = be.getSlotsForFace(Direction.UP);
        assertEquals(1, topSlots.length);
        assertEquals(CryoRevivalVatBlockEntity.SLOT_INPUT, topSlots[0]);

        int[] bottomSlots = be.getSlotsForFace(Direction.DOWN);
        assertEquals(1, bottomSlots.length);
        assertEquals(CryoRevivalVatBlockEntity.SLOT_OUTPUT, bottomSlots[0]);

        int[] sideSlots = be.getSlotsForFace(Direction.EAST);
        assertEquals(2, sideSlots.length);
        assertEquals(CryoRevivalVatBlockEntity.SLOT_INPUT, sideSlots[0]);
        assertEquals(CryoRevivalVatBlockEntity.SLOT_COOLANT, sideSlots[1]);

        assertTrue(be.canPlaceItemThroughFace(CryoRevivalVatBlockEntity.SLOT_INPUT, new ItemStack(SandStormBlocks.FOSSILIZED_AMBER), Direction.UP));
        assertTrue(be.canPlaceItemThroughFace(CryoRevivalVatBlockEntity.SLOT_COOLANT, new ItemStack(SandStormBlocks.BRITTLE_PERMAFROST), Direction.EAST));
        assertFalse(be.canPlaceItemThroughFace(CryoRevivalVatBlockEntity.SLOT_OUTPUT, new ItemStack(SandStormItems.BIO_NEURAL_CORE), Direction.DOWN));

        assertTrue(be.canTakeItemThroughFace(CryoRevivalVatBlockEntity.SLOT_OUTPUT, new ItemStack(SandStormItems.BIO_NEURAL_CORE), Direction.DOWN));
        assertFalse(be.canTakeItemThroughFace(CryoRevivalVatBlockEntity.SLOT_INPUT, new ItemStack(SandStormBlocks.FOSSILIZED_AMBER), Direction.DOWN));
    }

    @Test
    void testSolidCoolantRecipeIncubation() {
        CryoRevivalVatBlockEntity be = new CryoRevivalVatBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        be.setItem(CryoRevivalVatBlockEntity.SLOT_INPUT, new ItemStack(SandStormBlocks.FOSSILIZED_AMBER));
        be.setItem(CryoRevivalVatBlockEntity.SLOT_COOLANT, new ItemStack(SandStormBlocks.BRITTLE_PERMAFROST));

        assertTrue(be.canProcess());
        assertEquals(SandStormItems.BIO_NEURAL_CORE, be.getExpectedOutput().getItem());
        assertEquals(1, be.getStatus());
        assertEquals(-196, be.getTemperature());

        be.processRecipe();

        assertEquals(SandStormItems.BIO_NEURAL_CORE, be.getItem(CryoRevivalVatBlockEntity.SLOT_OUTPUT).getItem());
        assertEquals(1, be.getItem(CryoRevivalVatBlockEntity.SLOT_OUTPUT).getCount());
        assertTrue(be.getItem(CryoRevivalVatBlockEntity.SLOT_INPUT).isEmpty());
        assertTrue(be.getItem(CryoRevivalVatBlockEntity.SLOT_COOLANT).isEmpty());
    }

    @Test
    void testLiquidCoolantRecipeIncubationWithoutSolidCoolant() {
        CryoRevivalVatBlockEntity be = new CryoRevivalVatBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        be.setItem(CryoRevivalVatBlockEntity.SLOT_INPUT, new ItemStack(SandStormBlocks.FOSSILIZED_AMBER));
        assertFalse(be.canProcess());
        assertEquals(2, be.getStatus());

        be.setFluidAmount(1000);

        assertTrue(be.hasLiquidCoolant());
        assertTrue(be.canProcess());
        assertEquals(1, be.getStatus());

        be.processRecipe();

        assertEquals(SandStormItems.BIO_NEURAL_CORE, be.getItem(CryoRevivalVatBlockEntity.SLOT_OUTPUT).getItem());
        assertEquals(750, be.getFluidAmount());
    }

    @Test
    void testBucketCoolantDrain() {
        CryoRevivalVatBlockEntity be = new CryoRevivalVatBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        be.setItem(CryoRevivalVatBlockEntity.SLOT_COOLANT, new ItemStack(Items.WATER_BUCKET));
        CryoRevivalVatBlockEntity.serverTick(null, BlockPos.ZERO, Blocks.BARREL.defaultBlockState(), be);

        assertEquals(Items.BUCKET, be.getItem(CryoRevivalVatBlockEntity.SLOT_COOLANT).getItem());
        assertEquals(1000, be.getFluidAmount());
    }
}
