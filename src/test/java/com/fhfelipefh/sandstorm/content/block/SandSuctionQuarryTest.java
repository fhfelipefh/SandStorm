package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.SandSuctionQuarryBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.SandStormMenus;
import com.fhfelipefh.sandstorm.content.gui.SandSuctionQuarryMenu;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandSuctionQuarryTest {

    @BeforeAll
    static void setup() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BuiltInRegistries.BLOCK, false);
        frozenField.set(BuiltInRegistries.ITEM, false);
        frozenField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);
        frozenField.set(BuiltInRegistries.MENU, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());
    }

    @Test
    void testRegistrations() {
        assertNotNull(SandStormBlocks.SAND_SUCTION_QUARRY);
        assertNotNull(SandStormBlocks.SAND_SUCTION_QUARRY_BE);
        assertNotNull(SandStormMenus.SAND_SUCTION_QUARRY_MENU);
    }

    @Test
    void testExcavatableFilter() {
        assertTrue(SandSuctionQuarryBlockEntity.isExcavatable(Blocks.SAND.defaultBlockState()));
        assertTrue(SandSuctionQuarryBlockEntity.isExcavatable(Blocks.RED_SAND.defaultBlockState()));
        assertTrue(SandSuctionQuarryBlockEntity.isExcavatable(Blocks.SUSPICIOUS_SAND.defaultBlockState()));
        assertTrue(SandSuctionQuarryBlockEntity.isExcavatable(Blocks.GRAVEL.defaultBlockState()));
        assertTrue(SandSuctionQuarryBlockEntity.isExcavatable(SandStormBlocks.SALINIZED_SAND.defaultBlockState()));

        assertFalse(SandSuctionQuarryBlockEntity.isExcavatable(Blocks.AIR.defaultBlockState()));
        assertFalse(SandSuctionQuarryBlockEntity.isExcavatable(Blocks.STONE.defaultBlockState()));
        assertFalse(SandSuctionQuarryBlockEntity.isExcavatable(Blocks.DIRT.defaultBlockState()));
        assertFalse(SandSuctionQuarryBlockEntity.isExcavatable(Blocks.IRON_BLOCK.defaultBlockState()));
    }

    @Test
    void testRadiusControlsAndClamping() {
        BlockPos pos = new BlockPos(0, 64, 0);
        BlockState state = SandStormBlocks.SAND_SUCTION_QUARRY.defaultBlockState();
        SandSuctionQuarryBlockEntity quarry = new SandSuctionQuarryBlockEntity(pos, state);

        assertEquals(16, quarry.getRadius());
        assertFalse(quarry.isPaused());

        quarry.handleMenuButton(SandSuctionQuarryMenu.BUTTON_INC_1);
        assertEquals(17, quarry.getRadius());

        quarry.handleMenuButton(SandSuctionQuarryMenu.BUTTON_INC_8);
        assertEquals(25, quarry.getRadius());

        quarry.handleMenuButton(SandSuctionQuarryMenu.BUTTON_DEC_1);
        assertEquals(24, quarry.getRadius());

        quarry.handleMenuButton(SandSuctionQuarryMenu.BUTTON_DEC_8);
        assertEquals(16, quarry.getRadius());

        quarry.setRadius(63);
        quarry.handleMenuButton(SandSuctionQuarryMenu.BUTTON_INC_8);
        assertEquals(64, quarry.getRadius());

        quarry.setRadius(2);
        quarry.handleMenuButton(SandSuctionQuarryMenu.BUTTON_DEC_8);
        assertEquals(1, quarry.getRadius());

        quarry.handleMenuButton(SandSuctionQuarryMenu.BUTTON_TOGGLE_PAUSE);
        assertTrue(quarry.isPaused());
        quarry.handleMenuButton(SandSuctionQuarryMenu.BUTTON_TOGGLE_PAUSE);
        assertFalse(quarry.isPaused());
    }

    @Test
    void testOutputSpaceInitial() {
        BlockPos pos = new BlockPos(0, 64, 0);
        BlockState state = SandStormBlocks.SAND_SUCTION_QUARRY.defaultBlockState();
        SandSuctionQuarryBlockEntity quarry = new SandSuctionQuarryBlockEntity(pos, state);

        assertTrue(quarry.hasOutputSpace());
        assertEquals(0, quarry.getBlocksExcavatedTotal());
    }
}
