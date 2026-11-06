package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.FiberglassChemicalPipeBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SmartFluidPipeBlock;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DungeonPipeIntegrityAndGroundConnectionTest {

    @BeforeAll
    static void init() throws Exception {
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

        assertNotNull(SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE);
        assertNotNull(SandStormBlocks.SMART_FLUID_PIPE);

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
    void testPipePropertiesAndInitialDefaultState() {
        assertEquals(6, FiberglassChemicalPipeBlock.PROPERTY_BY_DIRECTION.size());
        assertEquals(6, SmartFluidPipeBlock.PROPERTY_BY_DIRECTION.size());

        BlockState defaultState = SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE.defaultBlockState();
        for (Direction dir : Direction.values()) {
            assertFalse(defaultState.getValue(FiberglassChemicalPipeBlock.PROPERTY_BY_DIRECTION.get(dir)));
        }
    }

    @Test
    void testPostProcessPipesConnectsAdjacentPipesAndGroundsFloatingRisers() {
        Map<BlockPos, BlockState> world = new HashMap<>();
        LevelAccessor mockLevel = createMockLevel(world);

        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                world.put(new BlockPos(x, 0, z), Blocks.POLISHED_DEEPSLATE.defaultBlockState());
            }
        }

        BlockPos floatingPipePos = new BlockPos(2, 3, 2);
        BlockPos adjacentPipePos = new BlockPos(3, 3, 2);
        world.put(floatingPipePos, SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE.defaultBlockState());
        world.put(adjacentPipePos, SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE.defaultBlockState());

        BaseDungeonComponent.postProcessPipes(mockLevel, BlockPos.ZERO, DungeonRotation.NONE, 7);

        BlockPos pipeY2 = new BlockPos(2, 2, 2);
        BlockPos pipeY1 = new BlockPos(2, 1, 2);
        assertTrue(world.get(pipeY2).getBlock() instanceof FiberglassChemicalPipeBlock);
        assertTrue(world.get(pipeY1).getBlock() instanceof FiberglassChemicalPipeBlock);

        BlockState stateAtGround = world.get(pipeY1);
        assertTrue(stateAtGround.getValue(FiberglassChemicalPipeBlock.DOWN));
        assertTrue(stateAtGround.getValue(FiberglassChemicalPipeBlock.UP));
        assertFalse(stateAtGround.getValue(FiberglassChemicalPipeBlock.NORTH));
        assertFalse(stateAtGround.getValue(FiberglassChemicalPipeBlock.SOUTH));

        BlockState stateAtMid = world.get(pipeY2);
        assertTrue(stateAtMid.getValue(FiberglassChemicalPipeBlock.DOWN));
        assertTrue(stateAtMid.getValue(FiberglassChemicalPipeBlock.UP));

        BlockState stateAtTop = world.get(floatingPipePos);
        assertTrue(stateAtTop.getValue(FiberglassChemicalPipeBlock.DOWN));
        assertTrue(stateAtTop.getValue(FiberglassChemicalPipeBlock.EAST));

        BlockState adjacentState = world.get(adjacentPipePos);
        assertTrue(adjacentState.getValue(FiberglassChemicalPipeBlock.WEST));
        assertTrue(adjacentState.getValue(FiberglassChemicalPipeBlock.DOWN));
    }

    @Test
    void testModularDungeonBuilderConnectDungeonPipesAcrossRooms() {
        Map<BlockPos, BlockState> world = new HashMap<>();
        LevelAccessor mockLevel = createMockLevel(world);

        for (int x = 0; x < 14; x++) {
            for (int z = 0; z < 14; z++) {
                world.put(new BlockPos(x, 0, z), Blocks.SMOOTH_STONE.defaultBlockState());
            }
        }

        for (int x = 4; x <= 8; x++) {
            world.put(new BlockPos(x, 2, 3), SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState());
        }

        ModularDungeonBuilder.connectDungeonPipes(mockLevel, BlockPos.ZERO, 2, 2, 7);

        for (int x = 4; x <= 8; x++) {
            BlockPos groundRiser = new BlockPos(x, 1, 3);
            BlockState riserState = world.get(groundRiser);
            assertNotNull(riserState);
            assertTrue(riserState.getBlock() instanceof SmartFluidPipeBlock);
            assertTrue(riserState.getValue(SmartFluidPipeBlock.DOWN));
            assertTrue(riserState.getValue(SmartFluidPipeBlock.UP));

            BlockPos pipeline = new BlockPos(x, 2, 3);
            BlockState pipeState = world.get(pipeline);
            assertNotNull(pipeState);
            assertTrue(pipeState.getValue(SmartFluidPipeBlock.DOWN));
            if (x > 4) {
                assertTrue(pipeState.getValue(SmartFluidPipeBlock.WEST));
            }
            if (x < 8) {
                assertTrue(pipeState.getValue(SmartFluidPipeBlock.EAST));
            }
        }
    }

    @Test
    void testCatalogsEnforcePipePostProcessingAndPodCompleteness() throws IOException {
        Path militaryCatalogPath = Path.of(
                "src", "main", "java", "com", "fhfelipefh", "sandstorm",
                "content", "world", "dungeon", "MilitaryLabCatalog.java"
        );
        String militaryContent = Files.readString(militaryCatalogPath);
        assertTrue(militaryContent.contains("postProcessPipes(level, origin, rotation, size)"));
        assertTrue(militaryContent.contains("DoubleBlockHalf.LOWER"));
        assertTrue(militaryContent.contains("DoubleBlockHalf.UPPER"));

        Path excavationCatalogPath = Path.of(
                "src", "main", "java", "com", "fhfelipefh", "sandstorm",
                "content", "world", "dungeon", "ExcavationHiveCatalog.java"
        );
        String excavationContent = Files.readString(excavationCatalogPath);
        assertTrue(excavationContent.contains("postProcessPipes(level, origin, rotation, size)"));

        Path builderPath = Path.of(
                "src", "main", "java", "com", "fhfelipefh", "sandstorm",
                "content", "world", "dungeon", "ModularDungeonBuilder.java"
        );
        String builderContent = Files.readString(builderPath);
        assertTrue(builderContent.contains("connectDungeonPipes(level, origin, gridWidth, gridDepth, roomSize)"));
    }

    private static LevelAccessor createMockLevel(Map<BlockPos, BlockState> world) {
        return (LevelAccessor) Proxy.newProxyInstance(
                LevelAccessor.class.getClassLoader(),
                new Class<?>[]{LevelAccessor.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("getBlockState".equals(name)) {
                        BlockPos pos = (BlockPos) args[0];
                        return world.getOrDefault(pos, Blocks.AIR.defaultBlockState());
                    }
                    if ("setBlock".equals(name)) {
                        BlockPos pos = (BlockPos) args[0];
                        BlockState state = (BlockState) args[1];
                        world.put(pos, state);
                        return true;
                    }
                    Class<?> returnType = method.getReturnType();
                    if (returnType == boolean.class) {
                        return false;
                    }
                    if (returnType == int.class) {
                        return 0;
                    }
                    return null;
                }
        );
    }
}

