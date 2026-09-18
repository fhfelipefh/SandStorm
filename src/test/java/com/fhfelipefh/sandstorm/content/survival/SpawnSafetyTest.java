package com.fhfelipefh.sandstorm.content.survival;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnSafetyTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldRejectSuffocatingFeetOrHead() {
        TestBlockGetter level = new TestBlockGetter();
        BlockPos pos = new BlockPos(0, 65, 0);

        level.set(pos.below(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
        level.set(pos, Blocks.SAND.defaultBlockState());
        level.set(pos.above(), Blocks.AIR.defaultBlockState());

        assertFalse(SpawnSafety.isSafePosition(level, pos));

        level.set(pos, Blocks.AIR.defaultBlockState());
        level.set(pos.above(), Blocks.SANDSTONE.defaultBlockState());

        assertFalse(SpawnSafety.isSafePosition(level, pos));
    }

    @Test
    void shouldRejectLavaAndFire() {
        TestBlockGetter level = new TestBlockGetter();
        BlockPos pos = new BlockPos(0, 65, 0);

        level.set(pos.below(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
        level.set(pos, Blocks.LAVA.defaultBlockState());
        level.set(pos.above(), Blocks.AIR.defaultBlockState());

        assertFalse(SpawnSafety.isSafePosition(level, pos));

        level.set(pos, Blocks.AIR.defaultBlockState());
        level.set(pos.above(), Blocks.FIRE.defaultBlockState());

        assertFalse(SpawnSafety.isSafePosition(level, pos));
    }

    @Test
    void shouldRejectAirFloorToPreventFalling() {
        TestBlockGetter level = new TestBlockGetter();
        BlockPos pos = new BlockPos(0, 65, 0);

        level.set(pos.below(), Blocks.AIR.defaultBlockState());
        level.set(pos, Blocks.AIR.defaultBlockState());
        level.set(pos.above(), Blocks.AIR.defaultBlockState());

        assertFalse(SpawnSafety.isSafePosition(level, pos));
    }

    @Test
    void shouldAcceptAirPocketsWithSolidFloor() {
        TestBlockGetter level = new TestBlockGetter();
        BlockPos pos = new BlockPos(0, 65, 0);

        level.set(pos.below(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
        level.set(pos, Blocks.AIR.defaultBlockState());
        level.set(pos.above(), Blocks.AIR.defaultBlockState());

        assertTrue(SpawnSafety.isSafePosition(level, pos));

        level.set(pos.below(), Blocks.IRON_BLOCK.defaultBlockState());
        assertTrue(SpawnSafety.isSafePosition(level, pos));
    }

    @Test
    void spaceshipStructureMustHaveExplicitAirInCabinAndNoSuffocation() throws IOException {
        Path structurePath = Path.of("src", "main", "resources", "data", "sandstorm", "structure", "spaceship_crash_site.nbt");
        assertTrue(Files.exists(structurePath));

        CompoundTag tag = NbtIo.readCompressed(structurePath, NbtAccounter.unlimitedHeap());
        var palette = tag.getListOrEmpty("palette");
        int airIndex = -1;
        for (int i = 0; i < palette.size(); i++) {
            if ("minecraft:air".equals(palette.getCompoundOrEmpty(i).getStringOr("id", ""))) {
                airIndex = i;
                break;
            }
        }
        assertTrue(airIndex >= 0);

        var blocks = tag.getListOrEmpty("blocks");
        assertEquals(13 * 7 * 16, blocks.size());

        Map<String, Integer> blockStatesAtPos = new HashMap<>();
        for (int i = 0; i < blocks.size(); i++) {
            var block = blocks.getCompoundOrEmpty(i);
            var posList = block.getListOrEmpty("pos");
            int x = posList.getInt(0).orElse(-1);
            int y = posList.getInt(1).orElse(-1);
            int z = posList.getInt(2).orElse(-1);
            int state = block.getIntOr("state", -1);
            blockStatesAtPos.put(x + "," + y + "," + z, state);
        }

        for (int x = 4; x <= 7; x++) {
            for (int y = 1; y <= 4; y++) {
                assertEquals(airIndex, blockStatesAtPos.get(x + "," + y + ",4"));
                assertEquals(airIndex, blockStatesAtPos.get(x + "," + y + ",2"));
                assertEquals(airIndex, blockStatesAtPos.get(x + "," + y + ",8"));
            }
        }

        assertFalse(airIndex == blockStatesAtPos.get("2,2,2"));
    }

    private static class TestBlockGetter implements BlockGetter {
        private final Map<BlockPos, BlockState> map = new HashMap<>();

        void set(BlockPos pos, BlockState state) {
            map.put(pos, state);
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return map.getOrDefault(pos, Blocks.AIR.defaultBlockState());
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return getBlockState(pos).getFluidState();
        }

        @Override
        public BlockEntity getBlockEntity(BlockPos pos) {
            return null;
        }

        @Override
        public int getHeight() {
            return 384;
        }

        @Override
        public int getMinY() {
            return -64;
        }
    }
}
