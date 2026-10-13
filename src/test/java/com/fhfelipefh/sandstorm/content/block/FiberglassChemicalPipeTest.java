package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.FiberglassChemicalPipeBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.SharedConstants;
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

class FiberglassChemicalPipeTest {

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

        assertNotNull(SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE);

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
    void testPipeBlockProperties() {
        assertNotNull(SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE);
        assertNotNull(FiberglassChemicalPipeBlock.NORTH);
        assertNotNull(FiberglassChemicalPipeBlock.SOUTH);
        assertNotNull(FiberglassChemicalPipeBlock.EAST);
        assertNotNull(FiberglassChemicalPipeBlock.WEST);
        assertNotNull(FiberglassChemicalPipeBlock.UP);
        assertNotNull(FiberglassChemicalPipeBlock.DOWN);

        assertEquals(FiberglassChemicalPipeBlock.NORTH, FiberglassChemicalPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.NORTH));
        assertEquals(FiberglassChemicalPipeBlock.SOUTH, FiberglassChemicalPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.SOUTH));
        assertEquals(FiberglassChemicalPipeBlock.EAST, FiberglassChemicalPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.EAST));
        assertEquals(FiberglassChemicalPipeBlock.WEST, FiberglassChemicalPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.WEST));
        assertEquals(FiberglassChemicalPipeBlock.UP, FiberglassChemicalPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.UP));
        assertEquals(FiberglassChemicalPipeBlock.DOWN, FiberglassChemicalPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.DOWN));
    }

    @Test
    void testPipeBlockEntityConstantsAndRegistration() {
        assertNotNull(SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE_BE);
        assertEquals(FluidConstants.BUCKET, FiberglassChemicalPipeBlockEntity.PIPE_CAPACITY);
        assertEquals(16200L, FiberglassChemicalPipeBlockEntity.TRANSFER_RATE);
    }
}
