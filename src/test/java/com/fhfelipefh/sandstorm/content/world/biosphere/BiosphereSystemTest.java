package com.fhfelipefh.sandstorm.content.world.biosphere;

import com.fhfelipefh.sandstorm.content.block.CryogenicChillerManager;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.entity.AmnioticIncubatorBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BiosphereSystemTest {

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

        assertNotNull(SandStormBlocks.AMNIOTIC_INCUBATOR);
        assertNotNull(SandStormItems.BIOSPHERE_CARTRIDGE_CRYO);

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
    void shouldHaveSevenBiosphereTypesWithValidProperties() {
        BiosphereType[] types = BiosphereType.values();
        assertEquals(7, types.length);

        for (BiosphereType type : types) {
            assertNotNull(type.getId());
            assertNotNull(type.getTargetBiomeId());
            assertNotNull(type.getTranslationKey());
            assertNotNull(type.getDisplayName());
            assertEquals(type, BiosphereType.fromId(type.getId()));
        }

        assertEquals(BiosphereType.CRYO_TUNDRA, SandStormItems.BIOSPHERE_CARTRIDGE_CRYO.getBiosphereType());
        assertEquals(BiosphereType.XENO_FUNGAL, SandStormItems.BIOSPHERE_CARTRIDGE_FUNGAL.getBiosphereType());
        assertEquals(BiosphereType.MAGNETIC_FOREST, SandStormItems.BIOSPHERE_CARTRIDGE_MAGNETIC.getBiosphereType());
        assertEquals(BiosphereType.PRIMORDIAL_OASIS, SandStormItems.BIOSPHERE_CARTRIDGE_OASIS.getBiosphereType());
    }

    @Test
    void shouldManageCryogenicChillerRegistrationAndRadius() {
        BlockPos chillerPos = new BlockPos(100, 64, 100);

        CryogenicChillerManager.clear();
        assertFalse(CryogenicChillerManager.isPointChilled(Level.OVERWORLD, chillerPos));

        CryogenicChillerManager.registerChiller(Level.OVERWORLD, chillerPos, 48);
        assertTrue(CryogenicChillerManager.isPointChilled(Level.OVERWORLD, chillerPos));
        assertTrue(CryogenicChillerManager.isPointChilled(Level.OVERWORLD, chillerPos.offset(20, 0, 20)));
        assertFalse(CryogenicChillerManager.isPointChilled(Level.OVERWORLD, chillerPos.offset(50, 0, 0)));

        CryogenicChillerManager.unregisterChiller(Level.OVERWORLD, chillerPos);
        assertFalse(CryogenicChillerManager.isPointChilled(Level.OVERWORLD, chillerPos));
    }

    @Test
    void shouldConfigureAmnioticIncubatorParameters() {
        AmnioticIncubatorBlockEntity incubator = new AmnioticIncubatorBlockEntity(
                SandStormBlocks.AMNIOTIC_INCUBATOR_BE,
                BlockPos.ZERO,
                SandStormBlocks.AMNIOTIC_INCUBATOR.defaultBlockState()
        );

        assertEquals(3, incubator.getContainerSize());
        assertEquals("cow", incubator.getSelectedSpecies());
        assertEquals(4, incubator.getTargetPopulationQuota());
        assertFalse(incubator.isAutoEcologicalMode());

        incubator.cycleSpecies();
        assertEquals("sheep", incubator.getSelectedSpecies());

        incubator.setSelectedSpecies("polar_bear");
        assertEquals("polar_bear", incubator.getSelectedSpecies());

        incubator.setWaterAmount(5000);
        assertEquals(5000, incubator.getWaterAmount());

        incubator.setWaterAmount(99999);
        assertEquals(AmnioticIncubatorBlockEntity.MAX_WATER, incubator.getWaterAmount());

        incubator.setBiomassUnits(32);
        assertEquals(32, incubator.getBiomassUnits());

        incubator.setTargetPopulationQuota(12);
        assertEquals(12, incubator.getTargetPopulationQuota());

        incubator.setAutoEcologicalMode(true);
        assertTrue(incubator.isAutoEcologicalMode());

        incubator.setItem(AmnioticIncubatorBlockEntity.SLOT_BIOMASS, new ItemStack(SandStormItems.XENO_GRASS_SEEDS, 16));
        assertEquals(16, incubator.getItem(AmnioticIncubatorBlockEntity.SLOT_BIOMASS).getCount());

        incubator.setItem(AmnioticIncubatorBlockEntity.SLOT_WATER_IN, new ItemStack(Items.WATER_BUCKET));
        assertEquals(Items.WATER_BUCKET, incubator.getItem(AmnioticIncubatorBlockEntity.SLOT_WATER_IN).getItem());
    }
}
