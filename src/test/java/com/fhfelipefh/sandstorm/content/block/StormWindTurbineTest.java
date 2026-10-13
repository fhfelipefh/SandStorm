package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.StormWindTurbineBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StormWindTurbineTest {

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

        assertNotNull(SandStormBlocks.STORM_WIND_TURBINE);

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
    void testBlockDefinitionAndFacing() {
        assertNotNull(SandStormBlocks.STORM_WIND_TURBINE);
        assertTrue(SandStormBlocks.STORM_WIND_TURBINE.defaultBlockState().hasProperty(StormWindTurbineBlock.FACING));
        assertEquals(Direction.NORTH, SandStormBlocks.STORM_WIND_TURBINE.defaultBlockState().getValue(StormWindTurbineBlock.FACING));
    }

    @Test
    void testBlockEntityInitialization() {
        StormWindTurbineBlockEntity turbine = new StormWindTurbineBlockEntity(BlockPos.ZERO, SandStormBlocks.STORM_WIND_TURBINE.defaultBlockState());
        assertNotNull(turbine.getEnergyStorage());
        assertEquals(0L, turbine.getEnergyStorage().getStoredEnergy());
        assertEquals(StormWindTurbineBlockEntity.TURBINE_CAPACITY, turbine.getEnergyStorage().getCapacity());
        assertEquals(0L, turbine.getCurrentGenerationRate());
    }

    @Test
    void testEnergyExtractionLimits() {
        StormWindTurbineBlockEntity turbine = new StormWindTurbineBlockEntity(BlockPos.ZERO, SandStormBlocks.STORM_WIND_TURBINE.defaultBlockState());
        turbine.getEnergyStorage().setStoredEnergy(1000L);
        long extracted = turbine.getEnergyStorage().extractEnergy(500L);
        assertEquals(StormWindTurbineBlockEntity.TURBINE_MAX_EXTRACT, extracted);
        assertEquals(750L, turbine.getEnergyStorage().getStoredEnergy());
    }
}
