package com.fhfelipefh.sandstorm.content.survival;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
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
}
