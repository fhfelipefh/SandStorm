package com.fhfelipefh.sandstorm.content.survival;

import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TooltipDisplay;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class FusedSpaceSuitHandlerTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldInitializeWithoutException() {
        assertDoesNotThrow(FusedSpaceSuitHandler::initialize);
    }

    @Test
    void shouldHandleNullAndEmptySuitItemsSafely() {
        assertNull(FusedSpaceSuitHandler.getSlotForSuitItem(null));
        assertFalse(FusedSpaceSuitHandler.isMatchingSuitPiece(ItemStack.EMPTY, EquipmentSlot.HEAD));
        assertFalse(FusedSpaceSuitHandler.isMatchingSuitPiece(ItemStack.EMPTY, EquipmentSlot.CHEST));
        assertFalse(FusedSpaceSuitHandler.isMatchingSuitPiece(ItemStack.EMPTY, EquipmentSlot.LEGS));
        assertFalse(FusedSpaceSuitHandler.isMatchingSuitPiece(ItemStack.EMPTY, EquipmentSlot.FEET));
        assertFalse(FusedSpaceSuitHandler.isSuitPiece(ItemStack.EMPTY, null));
    }

    @Test
    void createFusedPieceMustHideEnchantmentTooltipAndDisableGlint() {
        ItemStack piece = FusedSpaceSuitHandler.createFusedPiece(Items.IRON_HELMET, null);
        assertFalse(piece.getOrDefault(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
        TooltipDisplay td = piece.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
        assertFalse(td.shows(DataComponents.ENCHANTMENTS));
    }
}
