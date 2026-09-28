package com.fhfelipefh.sandstorm.content.defense;

import com.fhfelipefh.sandstorm.content.block.CrushingSpikeGateBlock;
import com.fhfelipefh.sandstorm.content.block.entity.CrushingSpikeGateBlockEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrushingSpikeGateBlockEntityTest {

    private static CrushingSpikeGateBlock gate;

    @BeforeAll
    static void init() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }

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
        holdersField.set(BuiltInRegistries.CREATIVE_MODE_TAB, new IdentityHashMap<>());

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("crushing_spike_gate"));
        gate = new CrushingSpikeGateBlock(BlockBehaviour.Properties.of().setId(blockKey));
    }

    @Test
    void shouldAnimateGraduallyWhenClosing() {
        BlockState openState = gate.defaultBlockState().setValue(CrushingSpikeGateBlock.OPEN, true);
        BlockEntityType<CrushingSpikeGateBlockEntity> type = new BlockEntityType<>(CrushingSpikeGateBlockEntity::new, Set.of(gate));
        CrushingSpikeGateBlockEntity entity = new CrushingSpikeGateBlockEntity(type, BlockPos.ZERO, openState);
        
        assertEquals(0.0f, entity.getProgress(0.0f), 0.01f);

        BlockState closedState = openState.setValue(CrushingSpikeGateBlock.OPEN, false);
        
        CrushingSpikeGateBlockEntity.tick(null, BlockPos.ZERO, closedState, entity);
        
        float progressAfterOneTick = entity.getProgress(1.0f);
        assertTrue(progressAfterOneTick > 0.0f);
        assertTrue(progressAfterOneTick < 1.0f);
        
        for (int i = 0; i < 10; i++) {
            CrushingSpikeGateBlockEntity.tick(null, BlockPos.ZERO, closedState, entity);
        }
        assertEquals(1.0f, entity.getProgress(1.0f), 0.01f);
    }
    
    @Test
    void shouldAnimateGraduallyWhenOpening() {
        BlockState closedState = gate.defaultBlockState().setValue(CrushingSpikeGateBlock.OPEN, false);
        BlockEntityType<CrushingSpikeGateBlockEntity> type = new BlockEntityType<>(CrushingSpikeGateBlockEntity::new, Set.of(gate));
        CrushingSpikeGateBlockEntity entity = new CrushingSpikeGateBlockEntity(type, BlockPos.ZERO, closedState);
        
        assertEquals(1.0f, entity.getProgress(1.0f), 0.01f);

        BlockState openState = closedState.setValue(CrushingSpikeGateBlock.OPEN, true);
        
        CrushingSpikeGateBlockEntity.tick(null, BlockPos.ZERO, openState, entity);
        
        float progressAfterOneTick = entity.getProgress(1.0f);
        assertTrue(progressAfterOneTick < 1.0f);
        assertTrue(progressAfterOneTick > 0.0f);
        
        for (int i = 0; i < 15; i++) {
            CrushingSpikeGateBlockEntity.tick(null, BlockPos.ZERO, openState, entity);
        }
        assertEquals(0.0f, entity.getProgress(1.0f), 0.01f);
    }
}
