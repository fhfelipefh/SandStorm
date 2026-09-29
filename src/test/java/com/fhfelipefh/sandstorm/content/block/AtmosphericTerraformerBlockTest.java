package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericTerraformerBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtmosphericTerraformerBlockTest {

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

        assertNotNull(SandStormBlocks.ATMOSPHERIC_TERRAFORMER);

        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldDefineAtmosphericTerraformerSpecifications() {
        assertEquals(250000L, AtmosphericTerraformerBlock.DEFAULT_CAPACITY);
        assertEquals(2500L, AtmosphericTerraformerBlock.DEFAULT_TRANSFER_RATE);
        assertEquals(100000L, AtmosphericTerraformerBlock.INITIAL_CHARGE);
        assertEquals(2000L, AtmosphericTerraformerBlock.ENERGY_PER_CYCLE);

        EnergyStorageComponent storage = new EnergyStorageComponent(
                AtmosphericTerraformerBlock.DEFAULT_CAPACITY,
                AtmosphericTerraformerBlock.DEFAULT_TRANSFER_RATE
        );
        storage.setStoredEnergy(AtmosphericTerraformerBlock.INITIAL_CHARGE);

        assertEquals(100000L, storage.getStoredEnergy());
        assertTrue(storage.hasEnergy(AtmosphericTerraformerBlock.ENERGY_PER_CYCLE));
    }

    @Test
    void shouldCreateNewBlockEntityAndProvideTerraformingIndex() {
        AtmosphericTerraformerBlock block = SandStormBlocks.ATMOSPHERIC_TERRAFORMER;
        assertNotNull(block);
        assertNotNull(block.getTerraformingIndex());

        BlockEntity be = block.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
        assertNotNull(be);
        assertTrue(be instanceof AtmosphericTerraformerBlockEntity);
    }
}
