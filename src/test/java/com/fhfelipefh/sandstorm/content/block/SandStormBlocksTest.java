package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SandStormBlocksTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "brackish_aquifer",
            "thumper",
            "printer_3d",
            "desalination_filter",
            "nanite_fabricator",
            "buried_tech_ruins",
            "ancient_data_core",
            "drone_dock",
            "assembly_bay",
            "atmospheric_terraformer"
    })
    void shouldCreateValidBlockKeysForCoreBlocks(String blockPath) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id(blockPath));
        assertNotNull(key);
        assertEquals(Registries.BLOCK, key.registryKey());
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(blockPath, key.identifier().getPath());
    }

    @Test
    void shouldSupportNoOcclusionProperties() {
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of().noOcclusion();
        assertNotNull(props);
    }
}
