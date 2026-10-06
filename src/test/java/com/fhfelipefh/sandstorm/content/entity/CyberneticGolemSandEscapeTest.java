package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CyberneticGolemSandEscapeTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void manufacturedBlockDetectionCorrectlyDistinguishesPlayerBlocksFromNatural() {
        assertTrue(CyberneticGolemEntity.isManufacturedBlock(Blocks.OAK_PLANKS.defaultBlockState()));
        assertTrue(CyberneticGolemEntity.isManufacturedBlock(Blocks.SPRUCE_DOOR.defaultBlockState()));
        assertTrue(CyberneticGolemEntity.isManufacturedBlock(Blocks.OAK_FENCE.defaultBlockState()));
        assertTrue(CyberneticGolemEntity.isManufacturedBlock(Blocks.CHEST.defaultBlockState()));
        assertTrue(CyberneticGolemEntity.isManufacturedBlock(Blocks.FURNACE.defaultBlockState()));
        assertTrue(CyberneticGolemEntity.isManufacturedBlock(Blocks.GLASS.defaultBlockState()));
        assertTrue(CyberneticGolemEntity.isManufacturedBlock(Blocks.TORCH.defaultBlockState()));
        assertTrue(CyberneticGolemEntity.isManufacturedBlock(Blocks.LANTERN.defaultBlockState()));
        assertTrue(CyberneticGolemEntity.isManufacturedBlock(Blocks.CRAFTING_TABLE.defaultBlockState()));
        assertTrue(CyberneticGolemEntity.isManufacturedBlock(Blocks.STONE_BRICKS.defaultBlockState()));

        assertFalse(CyberneticGolemEntity.isManufacturedBlock(Blocks.AIR.defaultBlockState()));
        assertFalse(CyberneticGolemEntity.isManufacturedBlock(Blocks.SAND.defaultBlockState()));
        assertFalse(CyberneticGolemEntity.isManufacturedBlock(Blocks.RED_SAND.defaultBlockState()));
        assertFalse(CyberneticGolemEntity.isManufacturedBlock(Blocks.SANDSTONE.defaultBlockState()));
        assertFalse(CyberneticGolemEntity.isManufacturedBlock(Blocks.STONE.defaultBlockState()));
        assertFalse(CyberneticGolemEntity.isManufacturedBlock(Blocks.DIRT.defaultBlockState()));
    }

    @Test
    void sandTrappedEscapeMethodsAndReflectionsMustExist() throws NoSuchMethodException {
        Method getSandTrappedTicks = CyberneticGolemEntity.class.getMethod("getSandTrappedTicks");
        assertNotNull(getSandTrappedTicks);

        Method setSandTrappedTicks = CyberneticGolemEntity.class.getMethod("setSandTrappedTicks", int.class);
        assertNotNull(setSandTrappedTicks);

        Method getTrappingSandBlocks = CyberneticGolemEntity.class.getMethod("getTrappingSandBlocks", ServerLevel.class);
        assertNotNull(getTrappingSandBlocks);

        Method attemptBreakFreeFromSand = CyberneticGolemEntity.class.getMethod("attemptBreakFreeFromSand", ServerLevel.class);
        assertNotNull(attemptBreakFreeFromSand);

        Method isNaturalSandEnvironment = CyberneticGolemEntity.class.getMethod("isNaturalSandEnvironment", ServerLevel.class, BlockPos.class);
        assertNotNull(isNaturalSandEnvironment);
    }
}
