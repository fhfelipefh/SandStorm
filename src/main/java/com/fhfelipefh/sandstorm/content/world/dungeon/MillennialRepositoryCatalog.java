package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.entity.SolidStateAccumulatorBlockEntity;
import com.fhfelipefh.sandstorm.content.entity.DefectiveSentinelEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class MillennialRepositoryCatalog {

    public static List<DungeonComponent> getComponents() {
        return List.of(
                new SurfaceAccessTerminal(),
                new StairwellDown(),
                new StairwellUp(),
                new CargoHoistShaft(),
                new CorridorStraight(),
                new CorridorCorner(),
                new CorridorTJunction(),
                new CorridorCrossroad(),
                new DeadEnd(),
                new SludgeCorridor(),
                new CorrodedWasteDepot(),
                new DefectiveSentinelWorkshop(),
                new SpentFuelPoolVault(),
                new SolidRock()
        );
    }

    public static DungeonComponent getEntranceComponent() {
        return new SurfaceAccessTerminal();
    }

    public static DungeonComponent getStairwellDownComponent() {
        return new StairwellDown();
    }

    public static DungeonComponent getStairwellUpComponent() {
        return new StairwellUp();
    }

    public static DungeonComponent getCargoHoistShaftComponent() {
        return new CargoHoistShaft();
    }

    public static DungeonComponent getSpentFuelPoolVaultComponent() {
        return new SpentFuelPoolVault();
    }

    public static class SurfaceAccessTerminal extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_surface_access";
        }

        @Override
        public int weight() {
            return 1;
        }

        @Override
        public boolean isUnique() {
            return true;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return direction == DungeonDirection.SOUTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);

            for (int y = 1; y <= 4; y++) {
                setLocalBlock(level, origin, rotation, 1, y, 1, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, y, 1, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 1, y, 5, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, y, 5, size, Blocks.IRON_BARS.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 2, 1, 1, size, SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 1, size, SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState());
        }
    }

    public static class StairwellDown extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_stairwell_down";
        }

        @Override
        public int weight() {
            return 3;
        }

        @Override
        public boolean isUnique() {
            return true;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return direction == DungeonDirection.SOUTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int ly = -8; ly <= 4; ly++) {
                setLocalBlock(level, origin, rotation, 3, ly, 3, size, Blocks.IRON_BARS.defaultBlockState());
            }

            int[][] spiralOffsets = {
                    {3, 4},
                    {2, 4},
                    {2, 3},
                    {2, 2},
                    {3, 2},
                    {4, 2},
                    {4, 3},
                    {4, 4}
            };

            for (int step = 0; step < 8; step++) {
                int sy = -step;
                int sx = spiralOffsets[step][0];
                int sz = spiralOffsets[step][1];
                setLocalBlock(level, origin, rotation, sx, sy, sz, size, Blocks.POLISHED_DEEPSLATE.defaultBlockState());
                for (int clr = 1; clr <= 3; clr++) {
                    setLocalBlock(level, origin, rotation, sx, sy + clr, sz, size, Blocks.AIR.defaultBlockState());
                }
            }

            setLocalBlock(level, origin, rotation, 3, 1, 5, size, Blocks.POLISHED_DEEPSLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, -8, 4, size, Blocks.POLISHED_DEEPSLATE.defaultBlockState());

            for (int ly = 1; ly <= 4; ly++) {
                setLocalBlock(level, origin, rotation, 1, ly, 1, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, ly, 1, size, Blocks.IRON_BARS.defaultBlockState());
            }
        }
    }

    public static class StairwellUp extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_stairwell_up";
        }

        @Override
        public int weight() {
            return 3;
        }

        @Override
        public boolean isUnique() {
            return true;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return direction == DungeonDirection.NORTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.WALL);

            for (int ly = 1; ly <= 5; ly++) {
                setLocalBlock(level, origin, rotation, 3, ly, 3, size, Blocks.IRON_BARS.defaultBlockState());
            }

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 5, lz, size, Blocks.AIR.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, Blocks.POLISHED_DEEPSLATE.defaultBlockState());
                }
            }

            setLocalBlock(level, origin, rotation, 1, 1, 5, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true));
            setLocalBlock(level, origin, rotation, 1, 0, 5, size, Blocks.REDSTONE_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 5, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true));
            setLocalBlock(level, origin, rotation, 5, 0, 5, size, Blocks.REDSTONE_BLOCK.defaultBlockState());
        }
    }

    public static class CargoHoistShaft extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_cargo_hoist";
        }

        @Override
        public int weight() {
            return 4;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            if (direction == DungeonDirection.NORTH || direction == DungeonDirection.SOUTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    for (int ly = -8; ly <= 5; ly++) {
                        setLocalBlock(level, origin, rotation, lx, ly, lz, size, Blocks.AIR.defaultBlockState());
                    }
                }
            }

            for (int ly = -8; ly <= 5; ly++) {
                setLocalBlock(level, origin, rotation, 3, ly, 3, size, Blocks.IRON_CHAIN.defaultBlockState());
                setLocalBlock(level, origin, rotation, 2, ly, 1, size, Blocks.LADDER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
            }

            for (int lx = 2; lx <= 4; lx++) {
                setLocalBlock(level, origin, rotation, lx, 1, 1, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, lx, 1, 5, size, Blocks.IRON_BARS.defaultBlockState());
            }
            for (int lz = 2; lz <= 4; lz++) {
                setLocalBlock(level, origin, rotation, 1, 1, lz, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, 1, lz, size, Blocks.IRON_BARS.defaultBlockState());
            }
        }
    }

    public static class CorridorStraight extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_corridor_straight";
        }

        @Override
        public int weight() {
            return 25;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            if (direction == DungeonDirection.NORTH || direction == DungeonDirection.SOUTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        }
    }

    public static class CorridorCorner extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_corridor_corner";
        }

        @Override
        public int weight() {
            return 20;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            if (direction == DungeonDirection.NORTH || direction == DungeonDirection.EAST) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        }
    }

    public static class CorridorTJunction extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_corridor_t";
        }

        @Override
        public int weight() {
            return 15;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            if (direction == DungeonDirection.SOUTH) {
                return DungeonSocket.WALL;
            }
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        }
    }

    public static class CorridorCrossroad extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_corridor_cross";
        }

        @Override
        public int weight() {
            return 10;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        }
    }

    public static class DeadEnd extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_dead_end";
        }

        @Override
        public int weight() {
            return 12;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            if (direction == DungeonDirection.NORTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 1, 3, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
        }
    }

    public static class SludgeCorridor extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_sludge_corridor";
        }

        @Override
        public int weight() {
            return 8;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            if (direction == DungeonDirection.NORTH || direction == DungeonDirection.SOUTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lz = 1; lz <= 5; lz++) {
                for (int lx = 2; lx <= 4; lx++) {
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, SandStormBlocks.RADIOACTIVE_SLUDGE.defaultBlockState());
                }
            }

            setLocalBlock(level, origin, rotation, 3, 0, 2, size, Blocks.POLISHED_DEEPSLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 0, 4, size, Blocks.POLISHED_DEEPSLATE.defaultBlockState());

            for (int ly = 1; ly <= 3; ly++) {
                setLocalBlock(level, origin, rotation, 1, ly, 2, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, ly, 2, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 1, ly, 4, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, ly, 4, size, Blocks.IRON_BARS.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 3, 5, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        }
    }

    public static class CorrodedWasteDepot extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_waste_depot";
        }

        @Override
        public int weight() {
            return 7;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            if (direction == DungeonDirection.SOUTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 1, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());

            setLocalBlock(level, origin, rotation, 4, 1, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 2, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());

            setLocalBlock(level, origin, rotation, 1, 1, 4, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 4, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());

            setLocalBlock(level, origin, rotation, 2, 0, 2, size, SandStormBlocks.RADIOACTIVE_SLUDGE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 0, 2, size, SandStormBlocks.RADIOACTIVE_SLUDGE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 3, 1, 1, size, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH));
            BlockPos chestPos = origin.offset(rotation.rotateOffset(3, 1, 1, size));
            if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
                populateDepotChest(chest, random);
            }
        }

        private void populateDepotChest(Container container, RandomSource random) {
            container.setItem(0, new ItemStack(SandStormItems.RADIOACTIVE_SLUDGE_BUCKET, 1));
            container.setItem(4, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 2 + random.nextInt(3)));
            container.setItem(12, new ItemStack(Items.IRON_INGOT, 4 + random.nextInt(6)));
            container.setItem(20, new ItemStack(Items.REDSTONE, 8 + random.nextInt(8)));
        }
    }

    public static class DefectiveSentinelWorkshop extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_sentinel_workshop";
        }

        @Override
        public int weight() {
            return 6;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            if (direction == DungeonDirection.SOUTH || direction == DungeonDirection.NORTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 1, 1, 2, size, Blocks.ANVIL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 4, size, Blocks.CRAFTING_TABLE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 2, size, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 4, size, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState());

            BlockPos accPos = origin.offset(rotation.rotateOffset(5, 1, 4, size));
            if (level.getBlockEntity(accPos) instanceof SolidStateAccumulatorBlockEntity acc) {
                acc.setStoredEnergy(15000);
            }

            BlockPos spawnPos = origin.offset(rotation.rotateOffset(3, 1, 3, size));
            if (DefectiveSentinelEntity.canSpawnInArea(level, spawnPos)) {
                DefectiveSentinelEntity sentinel = new DefectiveSentinelEntity(SandStormEntities.DEFECTIVE_SENTINEL, level);
                sentinel.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                sentinel.setPersistenceRequired();
                level.addFreshEntity(sentinel);
            }
        }
    }

    public static class SpentFuelPoolVault extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_spent_fuel_vault";
        }

        @Override
        public int weight() {
            return 1;
        }

        @Override
        public boolean isUnique() {
            return true;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            if (direction == DungeonDirection.NORTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.WALL);

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, SandStormBlocks.RADIOACTIVE_SLUDGE.defaultBlockState());
                }
            }

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 2, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());

            setLocalBlock(level, origin, rotation, 3, 1, 5, size, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH));
            BlockPos chestPos = origin.offset(rotation.rotateOffset(3, 1, 5, size));
            if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
                populateVaultChest(chest, random);
            }

            for (int ly = 1; ly <= 4; ly++) {
                setLocalBlock(level, origin, rotation, 1, ly, 3, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, ly, 3, size, Blocks.IRON_BARS.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 3, 5, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        }

        private void populateVaultChest(Container container, RandomSource random) {
            container.setItem(0, new ItemStack(SandStormBlocks.ANCIENT_DATA_CORE.asItem(), 1));
            container.setItem(4, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 4 + random.nextInt(4)));
            container.setItem(8, new ItemStack(Items.DIAMOND, 2 + random.nextInt(3)));
            container.setItem(13, new ItemStack(Items.EMERALD, 4 + random.nextInt(5)));
            container.setItem(18, new ItemStack(Items.REDSTONE, 16 + random.nextInt(16)));
            container.setItem(22, new ItemStack(SandStormBlocks.SOLID_STATE_ACCUMULATOR.asItem(), 1));
        }
    }

    public static class SolidRock extends BaseDungeonComponent {
        @Override
        public String id() {
            return "repository_solid_rock";
        }

        @Override
        public int weight() {
            return 30;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            for (int lx = 0; lx < size; lx++) {
                for (int lz = 0; lz < size; lz++) {
                    for (int ly = 0; ly <= 5; ly++) {
                        setLocalBlock(level, origin, rotation, lx, ly, lz, size, Blocks.DEEPSLATE.defaultBlockState());
                    }
                }
            }
        }
    }
}
