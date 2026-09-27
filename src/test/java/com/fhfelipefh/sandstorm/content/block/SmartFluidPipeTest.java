package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SmartFluidPipeTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testDirectionalProperties() {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("smart_fluid_pipe"));
        assertNotNull(blockKey);
        assertEquals("smart_fluid_pipe", blockKey.identifier().getPath());

        ResourceKey<BlockEntityType<?>> beKey = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, SandStormMod.id("smart_fluid_pipe"));
        assertNotNull(beKey);
        assertEquals("smart_fluid_pipe", beKey.identifier().getPath());

        assertNotNull(SmartFluidPipeBlock.NORTH);
        assertNotNull(SmartFluidPipeBlock.SOUTH);
        assertNotNull(SmartFluidPipeBlock.EAST);
        assertNotNull(SmartFluidPipeBlock.WEST);
        assertNotNull(SmartFluidPipeBlock.UP);
        assertNotNull(SmartFluidPipeBlock.DOWN);
    }

    @Test
    void testPropertyMapMapping() {
        assertEquals(SmartFluidPipeBlock.NORTH, SmartFluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.NORTH));
        assertEquals(SmartFluidPipeBlock.SOUTH, SmartFluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.SOUTH));
        assertEquals(SmartFluidPipeBlock.EAST, SmartFluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.EAST));
        assertEquals(SmartFluidPipeBlock.WEST, SmartFluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.WEST));
        assertEquals(SmartFluidPipeBlock.UP, SmartFluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.UP));
        assertEquals(SmartFluidPipeBlock.DOWN, SmartFluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.DOWN));
    }
}
