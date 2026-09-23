package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.DeepCoreDrillBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.DeepCoreDrillMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeepCoreDrillTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testDeepCoreDrillBlockProperties() {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id("deep_core_drill"));
        assertNotNull(key);
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals("deep_core_drill", key.identifier().getPath());

        assertEquals("drilling", DeepCoreDrillBlock.DRILLING.getName());
        assertEquals("facing", DeepCoreDrillBlock.FACING.getName());
        assertTrue(DeepCoreDrillBlock.DRILLING.getPossibleValues().contains(true));
        assertTrue(DeepCoreDrillBlock.DRILLING.getPossibleValues().contains(false));
    }

    @Test
    void testDeepCoreDrillBlockEntity() {
        DeepCoreDrillBlockEntity be = new DeepCoreDrillBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        assertEquals(8, be.getContainerSize());
        assertEquals(0, be.getStoredEnergy());
        assertEquals(50000, be.getMaxEnergy());
        assertEquals(0, be.getFluidAmount());

        Storage<FluidVariant> fluidStorage = be.getFluidStorage(Direction.UP);
        assertNotNull(fluidStorage);
        assertTrue(fluidStorage.supportsExtraction());
        assertFalse(fluidStorage.supportsInsertion());
    }

    @Test
    void testDeepCoreDrillMenu() {
        DeepCoreDrillBlockEntity be = new DeepCoreDrillBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        Inventory playerInv = new Inventory(null, null);
        DeepCoreDrillMenu menu = new DeepCoreDrillMenu(null, 1, playerInv, be, be.getContainerData());

        assertEquals(44, menu.slots.size());
        assertEquals(50000, menu.getMaxEnergy());
        assertEquals(0, menu.getEnergy());
        assertEquals(4000, menu.getMaxFluid());
        assertEquals(0, menu.getFluidAmount());
        assertEquals(0, menu.getFluidScaled(52));
        assertFalse(menu.isDrilling());
        assertFalse(menu.isProcessing());
        assertFalse(menu.isWptConnected());
    }
}
