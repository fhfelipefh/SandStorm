package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SubspaceGatewayBlockEntity;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubspaceGatewayTest {

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

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());

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
    void shouldDefinePropertiesCorrectly() {
        assertEquals("lit", SubspaceGatewayBlock.LIT.getName());
        assertTrue(SubspaceGatewayBlock.LIT.getPossibleValues().contains(true));
        assertTrue(SubspaceGatewayBlock.LIT.getPossibleValues().contains(false));
    }

    @Test
    void shouldCreateBlockEntity() {
        BlockPos pos = BlockPos.ZERO;
        BlockState state = Blocks.BARREL.defaultBlockState();
        SubspaceGatewayBlockEntity be = new SubspaceGatewayBlockEntity(BlockEntityTypes.BARREL, pos, state);
        assertNotNull(be);
        assertEquals(0, be.getEnergy());
        assertFalse(be.isCharged());
    }

    @Test
    void shouldHandleEnergyStorageAndClamping() {
        BlockPos pos = BlockPos.ZERO;
        BlockState state = Blocks.BARREL.defaultBlockState();
        SubspaceGatewayBlockEntity be = new SubspaceGatewayBlockEntity(BlockEntityTypes.BARREL, pos, state);

        be.setEnergy(15000);
        assertEquals(15000, be.getEnergy());
        assertFalse(be.isCharged());

        be.setEnergy(SubspaceGatewayBlockEntity.JUMP_COST);
        assertEquals(SubspaceGatewayBlockEntity.JUMP_COST, be.getEnergy());
        assertTrue(be.isCharged());

        be.setEnergy(SubspaceGatewayBlockEntity.MAX_ENERGY + 50000);
        assertEquals(SubspaceGatewayBlockEntity.MAX_ENERGY, be.getEnergy());

        be.setEnergy(-100);
        assertEquals(0, be.getEnergy());
    }

    @Test
    void shouldCalculateFuelValuesForCharging() {
        assertEquals(400, BaseMachineBlockEntity.getFuelEnergy(new ItemStack(Items.REDSTONE)));
        assertEquals(3600, BaseMachineBlockEntity.getFuelEnergy(new ItemStack(Items.REDSTONE_BLOCK)));
        assertEquals(0, BaseMachineBlockEntity.getFuelEnergy(new ItemStack(Items.DIRT)));
        assertEquals(0, BaseMachineBlockEntity.getFuelEnergy(ItemStack.EMPTY));
    }
}
