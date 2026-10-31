package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.RustingBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;
import java.util.IdentityHashMap;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MilitaryLabBlockStagesTest {
    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        Field holders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holders.setAccessible(true);
        for (MappedRegistry<?> registry : List.of((MappedRegistry<?>) BuiltInRegistries.BLOCK,
                (MappedRegistry<?>) BuiltInRegistries.ITEM, (MappedRegistry<?>) BuiltInRegistries.BLOCK_ENTITY_TYPE,
                (MappedRegistry<?>) BuiltInRegistries.ENTITY_TYPE)) {
            frozen.set(registry, false);
            if (holders.get(registry) == null) {
                holders.set(registry, new IdentityHashMap<>());
            }
        }
        frozen.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);
    }

    @Test
    void eachFamilyHasOneBlockWithThreeStages() {
        for (Block block : List.of(SandStormBlocks.MILITARY_ARMOR_PLATE, SandStormBlocks.HAZARD_STRIPE_BLOCK,
                SandStormBlocks.REINFORCED_VENT_DUCT, SandStormBlocks.STEEL_VENT_GRATE)) {
            assertInstanceOf(RustingBlock.class, block);
            assertEquals(0, block.defaultBlockState().getValue(RustingBlock.RUST));
            block.defaultBlockState().initCache();
            block.defaultBlockState().setValue(RustingBlock.RUST, 2).initCache();
            assertTrue(block.defaultBlockState().isRandomlyTicking());
            assertFalse(block.defaultBlockState().setValue(RustingBlock.RUST, 2).isRandomlyTicking());
            for (int stage = 0; stage <= 2; stage++) {
                BlockState state = block.defaultBlockState().setValue(RustingBlock.RUST, stage);
                assertSame(block, state.getBlock());
                assertEquals(stage, state.getValue(RustingBlock.RUST));
            }
        }
    }

    @Test
    void oldBlocksMigrateToTheirFamilyAndStage() {
        assertMigration(SandStormBlocks.WEATHERED_MILITARY_PLATE, SandStormBlocks.MILITARY_ARMOR_PLATE, 1);
        assertMigration(SandStormBlocks.RUSTED_MILITARY_PLATE, SandStormBlocks.MILITARY_ARMOR_PLATE, 2);
        assertMigration(SandStormBlocks.FRACTURED_MILITARY_PLATE, SandStormBlocks.MILITARY_ARMOR_PLATE, 2);
        assertMigration(SandStormBlocks.WEATHERED_HAZARD_STRIPE, SandStormBlocks.HAZARD_STRIPE_BLOCK, 1);
        assertMigration(SandStormBlocks.CORRODED_HAZARD_STRIPE, SandStormBlocks.HAZARD_STRIPE_BLOCK, 2);
        assertMigration(SandStormBlocks.RUSTED_VENT_DUCT, SandStormBlocks.REINFORCED_VENT_DUCT, 1);
        assertMigration(SandStormBlocks.DAMAGED_VENT_DUCT, SandStormBlocks.REINFORCED_VENT_DUCT, 2);
        assertMigration(SandStormBlocks.RUSTED_STEEL_VENT_GRATE, SandStormBlocks.STEEL_VENT_GRATE, 1);
        assertMigration(SandStormBlocks.BROKEN_STEEL_VENT_GRATE, SandStormBlocks.STEEL_VENT_GRATE, 2);
    }

    @Test
    void ruinedStructuresPreferAdvancedStages() {
        RandomSource random = RandomSource.create(7821L);
        int[] counts = new int[3];
        for (int i = 0; i < 10000; i++) {
            counts[RustingBlock.selectRuinRust(random)]++;
        }
        assertTrue(counts[0] > 300 && counts[0] < 700);
        assertTrue(counts[1] > 2200 && counts[1] < 2800);
        assertTrue(counts[2] > 6600 && counts[2] < 7400);
    }

    @Test
    void otherMilitaryBlocksRemainRegistered() {
        assertNotNull(SandStormBlocks.MILITARY_MUNITIONS_CRATE);
        assertNotNull(SandStormBlocks.DAMAGED_MUNITIONS_CRATE);
        assertNotNull(SandStormBlocks.TACTICAL_RADAR_CONSOLE);
        assertNotNull(SandStormBlocks.BROKEN_RADAR_CONSOLE);
        assertNotNull(new MilitaryLabClutterPass());
    }

    private static void assertMigration(Block oldBlock, Block newBlock, int rust) {
        BlockState migrated = RustingBlock.convertLegacy(oldBlock.defaultBlockState());
        assertSame(newBlock, migrated.getBlock());
        assertEquals(rust, migrated.getValue(RustingBlock.RUST));
    }
}
