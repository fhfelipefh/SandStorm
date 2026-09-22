package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.gui.SandstoneWorkbenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandstoneWorkbenchBlockTest {

    @Test
    void shouldExtendCraftingTableBlockAndImplementInteractivity() throws Exception {
        assertTrue(CraftingTableBlock.class.isAssignableFrom(SandstoneWorkbenchBlock.class));

        Method getMenuProvider = SandstoneWorkbenchBlock.class.getDeclaredMethod("getMenuProvider", BlockState.class, Level.class, BlockPos.class);
        assertNotNull(getMenuProvider);
        assertEquals(MenuProvider.class, getMenuProvider.getReturnType());

        Method useWithoutItem = SandstoneWorkbenchBlock.class.getDeclaredMethod("useWithoutItem", BlockState.class, Level.class, BlockPos.class, Player.class, BlockHitResult.class);
        assertNotNull(useWithoutItem);
        assertEquals(InteractionResult.class, useWithoutItem.getReturnType());

        Method useItemOn = SandstoneWorkbenchBlock.class.getDeclaredMethod("useItemOn", ItemStack.class, BlockState.class, Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class);
        assertNotNull(useItemOn);
        assertEquals(InteractionResult.class, useItemOn.getReturnType());
    }

    @Test
    void shouldInheritFromCraftingMenuAndOverrideStillValid() throws Exception {
        assertTrue(CraftingMenu.class.isAssignableFrom(SandstoneWorkbenchMenu.class));
        Method stillValid = SandstoneWorkbenchMenu.class.getDeclaredMethod("stillValid", Player.class);
        assertNotNull(stillValid);
        assertEquals(boolean.class, stillValid.getReturnType());
    }

    @Test
    void shouldHaveBlockLootTableDroppingSelf() throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/loot_table/blocks/sandstone_workbench.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("sandstorm:sandstone_workbench"));
            assertTrue(json.contains("minecraft:survives_explosion"));
        }
    }
}
