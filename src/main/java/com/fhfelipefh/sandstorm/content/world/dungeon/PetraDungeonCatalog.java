package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class PetraDungeonCatalog {

    public static List<DungeonComponent> getComponents() {
        return List.of(
                new EntranceVestibule(),
                new ColonnadeHall(),
                new PotterySanctum(),
                new StatueGallery(),
                new CorridorStraight(),
                new CorridorCorner(),
                new CorridorTJunction(),
                new CorridorCrossroad(),
                new CatacombDeadEnd(),
                new RoyalCrypt(),
                new SunAltarVault(),
                new SolidRock()
        );
    }

    public static DungeonComponent getEntranceComponent() {
        return new EntranceVestibule();
    }

    public static DungeonComponent getRoyalCryptComponent() {
        return new RoyalCrypt();
    }

    public static DungeonComponent getSunAltarComponent() {
        return new SunAltarVault();
    }

    public static class EntranceVestibule extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_entrance_vestibule";
        }

        @Override
        public int weight() {
            return 10;
        }

        @Override
        public boolean isUnique() {
            return true;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return switch (direction) {
                case NORTH -> DungeonSocket.CORRIDOR;
                case EAST, WEST -> DungeonSocket.CORRIDOR;
                case SOUTH -> DungeonSocket.DOOR;
            };
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            buildPetraShell(level, origin, rotation, roomSize, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.DOOR, DungeonSocket.CORRIDOR);

            setLocalBlock(level, origin, rotation, 2, 1, 2, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 2, 2, roomSize, Blocks.CUT_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 3, 2, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 4, 1, 2, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 2, 2, roomSize, Blocks.CUT_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 3, 2, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 2, 1, 4, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 2, 4, roomSize, Blocks.CUT_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 3, 4, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 4, 1, 4, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 2, 4, roomSize, Blocks.CUT_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 3, 4, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
        }
    }

    public static class ColonnadeHall extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_colonnade_hall";
        }

        @Override
        public int weight() {
            return 15;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return switch (direction) {
                case NORTH, SOUTH -> DungeonSocket.CORRIDOR;
                case EAST, WEST -> DungeonSocket.WALL;
            };
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            buildPetraShell(level, origin, rotation, roomSize, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int z = 1; z <= 5; z += 2) {
                setLocalBlock(level, origin, rotation, 1, 1, z, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 1, 2, z, roomSize, Blocks.CUT_RED_SANDSTONE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 1, 3, z, roomSize, Blocks.CUT_RED_SANDSTONE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 1, 4, z, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());

                setLocalBlock(level, origin, rotation, 5, 1, z, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, 2, z, roomSize, Blocks.CUT_RED_SANDSTONE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, 3, z, roomSize, Blocks.CUT_RED_SANDSTONE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, 4, z, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            }
        }
    }

    public static class PotterySanctum extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_pottery_sanctum";
        }

        @Override
        public int weight() {
            return 12;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return switch (direction) {
                case SOUTH -> DungeonSocket.DOOR;
                case NORTH, EAST, WEST -> DungeonSocket.WALL;
            };
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            buildPetraShell(level, origin, rotation, roomSize, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.DOOR, DungeonSocket.WALL);

            for (int x = 1; x <= 5; x++) {
                setLocalBlock(level, origin, rotation, x, 1, 1, roomSize, Blocks.SMOOTH_RED_SANDSTONE_SLAB.defaultBlockState());
                if (x % 2 == 1) {
                    setLocalBlock(level, origin, rotation, x, 2, 1, roomSize, SandStormBlocks.TERRACOTTA_AMPHORA.defaultBlockState());
                } else {
                    setLocalBlock(level, origin, rotation, x, 2, 1, roomSize, SandStormBlocks.CLAY_URN.defaultBlockState());
                }
            }

            setLocalBlock(level, origin, rotation, 1, 1, 3, roomSize, SandStormBlocks.TERRACOTTA_AMPHORA.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 4, roomSize, SandStormBlocks.CLAY_URN.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 3, roomSize, SandStormBlocks.CLAY_URN.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 4, roomSize, SandStormBlocks.TERRACOTTA_AMPHORA.defaultBlockState());
        }
    }

    public static class StatueGallery extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_statue_gallery";
        }

        @Override
        public int weight() {
            return 10;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return switch (direction) {
                case NORTH, SOUTH -> DungeonSocket.CORRIDOR;
                case EAST, WEST -> DungeonSocket.WALL;
            };
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            buildPetraShell(level, origin, rotation, roomSize, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 1, 1, 2, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 2, roomSize, SandStormBlocks.SANDSTONE_STATUE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));

            setLocalBlock(level, origin, rotation, 1, 1, 4, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 4, roomSize, SandStormBlocks.SANDSTONE_STATUE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));

            setLocalBlock(level, origin, rotation, 5, 1, 2, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 2, 2, roomSize, SandStormBlocks.SANDSTONE_STATUE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST));

            setLocalBlock(level, origin, rotation, 5, 1, 4, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 2, 4, roomSize, SandStormBlocks.SANDSTONE_STATUE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST));
        }
    }

    public static class CorridorStraight extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_corridor_straight";
        }

        @Override
        public int weight() {
            return 25;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return switch (direction) {
                case NORTH, SOUTH -> DungeonSocket.CORRIDOR;
                case EAST, WEST -> DungeonSocket.WALL;
            };
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            buildPetraShell(level, origin, rotation, roomSize, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
        }
    }

    public static class CorridorCorner extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_corridor_corner";
        }

        @Override
        public int weight() {
            return 20;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return switch (direction) {
                case NORTH, EAST -> DungeonSocket.CORRIDOR;
                case SOUTH, WEST -> DungeonSocket.WALL;
            };
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            buildPetraShell(level, origin, rotation, roomSize, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL);
        }
    }

    public static class CorridorTJunction extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_corridor_t_junction";
        }

        @Override
        public int weight() {
            return 15;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return switch (direction) {
                case NORTH, SOUTH, EAST -> DungeonSocket.CORRIDOR;
                case WEST -> DungeonSocket.WALL;
            };
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            buildPetraShell(level, origin, rotation, roomSize, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
        }
    }

    public static class CorridorCrossroad extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_corridor_crossroad";
        }

        @Override
        public int weight() {
            return 10;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            buildPetraShell(level, origin, rotation, roomSize, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);
        }
    }

    public static class CatacombDeadEnd extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_catacomb_dead_end";
        }

        @Override
        public int weight() {
            return 15;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return switch (direction) {
                case SOUTH -> DungeonSocket.CORRIDOR;
                case NORTH, EAST, WEST -> DungeonSocket.WALL;
            };
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            buildPetraShell(level, origin, rotation, roomSize, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 2, 1, 1, roomSize, Blocks.CUT_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 1, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 1, roomSize, Blocks.CUT_RED_SANDSTONE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 2, 2, 1, roomSize, SandStormBlocks.CLAY_URN.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 2, 1, roomSize, SandStormBlocks.TERRACOTTA_AMPHORA.defaultBlockState());
        }
    }

    public static class RoyalCrypt extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_royal_crypt";
        }

        @Override
        public int weight() {
            return 8;
        }

        @Override
        public boolean isUnique() {
            return true;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return switch (direction) {
                case SOUTH -> DungeonSocket.DOOR;
                case NORTH, EAST, WEST -> DungeonSocket.WALL;
            };
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            buildPetraShell(level, origin, rotation, roomSize, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.DOOR, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 3, 1, 2, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 3, roomSize, Blocks.SMOOTH_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 4, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 3, 2, 2, roomSize, Blocks.SMOOTH_RED_SANDSTONE_SLAB.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 3, roomSize, Blocks.SMOOTH_RED_SANDSTONE_SLAB.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 4, roomSize, Blocks.SMOOTH_RED_SANDSTONE_SLAB.defaultBlockState());

            setLocalBlock(level, origin, rotation, 1, 1, 1, roomSize, SandStormBlocks.SANDSTONE_STATUE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
            setLocalBlock(level, origin, rotation, 5, 1, 1, roomSize, SandStormBlocks.SANDSTONE_STATUE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));

            setLocalBlock(level, origin, rotation, 1, 1, 5, roomSize, SandStormBlocks.TERRACOTTA_AMPHORA.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 5, roomSize, SandStormBlocks.CLAY_URN.defaultBlockState());
        }
    }

    public static class SunAltarVault extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_sun_altar_vault";
        }

        @Override
        public int weight() {
            return 6;
        }

        @Override
        public boolean isUnique() {
            return true;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return switch (direction) {
                case SOUTH -> DungeonSocket.DOOR;
                case NORTH, EAST, WEST -> DungeonSocket.WALL;
            };
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            buildPetraShell(level, origin, rotation, roomSize, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.DOOR, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 2, 1, 3, roomSize, Blocks.RED_SANDSTONE_STAIRS.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));
            setLocalBlock(level, origin, rotation, 4, 1, 3, roomSize, Blocks.RED_SANDSTONE_STAIRS.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST));
            setLocalBlock(level, origin, rotation, 3, 1, 3, roomSize, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 3, roomSize, Blocks.CUT_RED_SANDSTONE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 1, 1, 1, roomSize, SandStormBlocks.TERRACOTTA_AMPHORA.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, roomSize, SandStormBlocks.CLAY_URN.defaultBlockState());
        }
    }

    public static class SolidRock extends BasePetraDungeonComponent {
        @Override
        public String id() {
            return "petra_solid_rock";
        }

        @Override
        public int weight() {
            return 5;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return DungeonSocket.SOLID;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int roomSize) {
            for (int lx = 0; lx < roomSize; lx++) {
                for (int lz = 0; lz < roomSize; lz++) {
                    for (int ly = 0; ly <= 5; ly++) {
                        setLocalBlock(level, origin, rotation, lx, ly, lz, roomSize, Blocks.RED_SANDSTONE.defaultBlockState());
                    }
                }
            }
        }
    }
}
