package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChemicalRefineryBlockTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldDefinePropertiesCorrectly() {
        assertEquals("facing", ChemicalRefineryBlock.FACING.getName());
        assertTrue(ChemicalRefineryBlock.FACING.getPossibleValues().contains(Direction.NORTH));
        assertTrue(ChemicalRefineryBlock.FACING.getPossibleValues().contains(Direction.SOUTH));
        assertTrue(ChemicalRefineryBlock.FACING.getPossibleValues().contains(Direction.EAST));
        assertTrue(ChemicalRefineryBlock.FACING.getPossibleValues().contains(Direction.WEST));
    }

    @Test
    void shouldImplementInteractionMethods() throws Exception {
        Method useItemOn = ChemicalRefineryBlock.class.getDeclaredMethod("useItemOn", ItemStack.class, BlockState.class, Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class);
        assertNotNull(useItemOn);
        assertEquals(InteractionResult.class, useItemOn.getReturnType());

        Method useWithoutItem = ChemicalRefineryBlock.class.getDeclaredMethod("useWithoutItem", BlockState.class, Level.class, BlockPos.class, Player.class, BlockHitResult.class);
        assertNotNull(useWithoutItem);
        assertEquals(InteractionResult.class, useWithoutItem.getReturnType());
    }

    @Test
    void shouldHaveLootTableDroppingSelf() throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/loot_table/blocks/chemical_refinery.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("sandstorm:chemical_refinery"));
        }
    }

    @Test
    void shouldHaveValidRecipes() throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/recipe/chemical_refinery.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("sandstorm:chemical_refinery"));
        }

        try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/recipe/empty_cartridge.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("sandstorm:empty_cartridge"));
        }

        try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/recipe/suit_upgrade_jetpack.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("sandstorm:suit_upgrade_jetpack"));
        }
    }
}
