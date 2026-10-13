package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FiberglassToolsTest {

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
        assertNotNull(SandStormItems.FIBERGLASS_TURBINE_BLADE);

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
    void testCompositeRecurveBowProperties() {
        assertNotNull(SandStormItems.COMPOSITE_RECURVE_BOW);
        ItemStack bowStack = new ItemStack(SandStormItems.COMPOSITE_RECURVE_BOW);
        assertFalse(bowStack.isEmpty());
        assertEquals(SandStormItems.COMPOSITE_RECURVE_BOW, bowStack.getItem());
        assertEquals(15, SandStormItems.COMPOSITE_RECURVE_BOW.getDrawTimeTicks());
        assertEquals(3.8f, SandStormItems.COMPOSITE_RECURVE_BOW.getProjectileVelocity());
    }

    @Test
    void testSandDepthProbeProperties() {
        assertNotNull(SandStormItems.SAND_DEPTH_PROBE);
        ItemStack probeStack = new ItemStack(SandStormItems.SAND_DEPTH_PROBE);
        assertFalse(probeStack.isEmpty());
        assertEquals(SandStormItems.SAND_DEPTH_PROBE, probeStack.getItem());
        assertEquals(5, SandStormItems.SAND_DEPTH_PROBE.getScanDepth());
    }

    @Test
    void testFiberglassTurbineBladeItem() {
        assertNotNull(SandStormItems.FIBERGLASS_TURBINE_BLADE);
        ItemStack bladeStack = new ItemStack(SandStormItems.FIBERGLASS_TURBINE_BLADE);
        assertFalse(bladeStack.isEmpty());
        assertEquals(SandStormItems.FIBERGLASS_TURBINE_BLADE, bladeStack.getItem());
    }
}
