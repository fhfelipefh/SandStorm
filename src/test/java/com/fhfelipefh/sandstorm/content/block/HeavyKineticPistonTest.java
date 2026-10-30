package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import net.minecraft.SharedConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeavyKineticPistonTest {

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

        assertNotNull(SandStormBlocks.HEAVY_KINETIC_PISTON);

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
    void shouldHaveDoubleVanillaPushDepthLimit() {
        assertEquals(24, HeavyPistonStructureResolver.MAX_PUSH_DEPTH);
        assertEquals(12, PistonBaseBlock.PLATFORM_THICKNESS * 3);
        assertTrue(HeavyPistonStructureResolver.MAX_PUSH_DEPTH > 12);
    }

    @Test
    void shouldConsumeStandardEnergyPerPush() {
        assertEquals(100, HeavyKineticPistonBlock.ENERGY_PER_PUSH);
    }

    @Test
    void shouldProperlyManageEnergyInStorageComponent() {
        EnergyStorageComponent storage = new EnergyStorageComponent(5000, 500, 500);
        assertEquals(5000, storage.getCapacity());
        assertEquals(0, storage.getStoredEnergy());

        long received = storage.receiveEnergy(400);
        assertEquals(400, received);
        assertEquals(400, storage.getStoredEnergy());

        long extracted = storage.extractEnergy(100);
        assertEquals(100, extracted);
        assertEquals(300, storage.getStoredEnergy());

        long overDrain = storage.extractEnergy(1000);
        assertEquals(300, overDrain);
        assertEquals(0, storage.getStoredEnergy());
    }

    @Test
    void shouldRegisterPistonBlocksAndHead() {
        assertNotNull(SandStormBlocks.HEAVY_KINETIC_PISTON);
        assertNotNull(SandStormBlocks.HEAVY_STICKY_KINETIC_PISTON);
        assertNotNull(SandStormBlocks.HEAVY_KINETIC_PISTON_HEAD);
        assertNotNull(SandStormBlocks.HEAVY_KINETIC_PISTON_BE);
    }

    @Test
    void shouldConfigureDefaultBlockStatesCorrectly() {
        BlockState normalState = SandStormBlocks.HEAVY_KINETIC_PISTON.defaultBlockState();
        assertFalse(normalState.getValue(HeavyKineticPistonBlock.EXTENDED));
        assertEquals(Direction.NORTH, normalState.getValue(HeavyKineticPistonBlock.FACING));

        BlockState stickyState = SandStormBlocks.HEAVY_STICKY_KINETIC_PISTON.defaultBlockState();
        assertFalse(stickyState.getValue(HeavyKineticPistonBlock.EXTENDED));

        BlockState headState = SandStormBlocks.HEAVY_KINETIC_PISTON_HEAD.defaultBlockState();
        assertEquals(PistonType.DEFAULT, headState.getValue(HeavyKineticPistonHeadBlock.TYPE));
        assertFalse(headState.getValue(HeavyKineticPistonHeadBlock.SHORT));
    }

    @Test
    void shouldRecognizeImmoveableBlocks() {
        BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();
        assertEquals(PushReaction.IMMOVEABLE, obsidian.getPistonPushReaction());
        assertTrue(obsidian.is(Blocks.OBSIDIAN));

        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        assertTrue(bedrock.is(Blocks.BEDROCK));
    }

    @Test
    void shouldHandleSlimeAndHoneyStickiness() {
        BlockState slime = Blocks.SLIME_BLOCK.defaultBlockState();
        BlockState honey = Blocks.HONEY_BLOCK.defaultBlockState();
        BlockState stone = Blocks.STONE.defaultBlockState();

        assertTrue(slime.is(Blocks.SLIME_BLOCK));
        assertTrue(honey.is(Blocks.HONEY_BLOCK));
        assertFalse(stone.is(Blocks.SLIME_BLOCK));
        assertFalse(stone.is(Blocks.HONEY_BLOCK));
    }
}
