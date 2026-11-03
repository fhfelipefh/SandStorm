package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.BasicThermalGeneratorBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BasicThermalGeneratorTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        DataComponentMap defaultComponents = DataComponentMap.builder()
                .set(DataComponents.MAX_STACK_SIZE, 64)
                .build();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
    }

    @Test
    void shouldInitializeWithCorrectDefaults() {
        BasicThermalGeneratorBlockEntity be = new BasicThermalGeneratorBlockEntity(
                BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState()
        );
        assertEquals(0L, be.getEnergyStorage().getStoredEnergy());
        assertEquals(BasicThermalGeneratorBlockEntity.CAPACITY, be.getEnergyStorage().getCapacity());
        assertEquals(0, be.getBurnTime());
    }

    @Test
    void shouldRecognizeSolidFuels() {
        assertTrue(BasicThermalGeneratorBlockEntity.getSolidFuelBurnDuration(new ItemStack(Items.COAL)) > 0);
        assertTrue(BasicThermalGeneratorBlockEntity.getSolidFuelBurnDuration(new ItemStack(Items.CHARCOAL)) > 0);
        assertTrue(BasicThermalGeneratorBlockEntity.getSolidFuelBurnDuration(new ItemStack(Items.STICK)) > 0);
        assertTrue(BasicThermalGeneratorBlockEntity.getSolidFuelBurnDuration(new ItemStack(Items.OAK_PLANKS)) > 0);
        assertEquals(0, BasicThermalGeneratorBlockEntity.getSolidFuelBurnDuration(new ItemStack(Items.DIRT)));
    }

    @Test
    void shouldManageGeneratorsAndWpt() {
        BasicThermalGeneratorManager.clear();
        BlockPos pos = new BlockPos(10, 64, 10);
        BasicThermalGeneratorManager.registerGenerator(Level.OVERWORLD, pos, 100);
        assertEquals(100, BasicThermalGeneratorManager.getBurnTime(Level.OVERWORLD, pos));
        assertEquals(16L, BasicThermalGeneratorManager.getWptChargeAt(Level.OVERWORLD, new BlockPos(10, 64, 14)));
        assertEquals(0L, BasicThermalGeneratorManager.getWptChargeAt(Level.OVERWORLD, new BlockPos(10, 64, 15)));
        BasicThermalGeneratorManager.unregisterGenerator(Level.OVERWORLD, pos);
        assertEquals(0, BasicThermalGeneratorManager.getBurnTime(Level.OVERWORLD, pos));
        assertEquals(0L, BasicThermalGeneratorManager.getWptChargeAt(Level.OVERWORLD, new BlockPos(10, 64, 14)));
    }
}

