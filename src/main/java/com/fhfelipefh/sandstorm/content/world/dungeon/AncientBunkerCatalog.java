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

public class AncientBunkerCatalog {

    public static List<DungeonComponent> getComponents() {
        return List.of(
                new EntranceRoom(),
                new CorridorStraight(),
                new CorridorCorner(),
                new CorridorTJunction(),
                new CorridorCrossroad(),
                new DeadEnd(),
                new DataCoreVault(),
                new EnergyChamber(),
                new ServerArchive(),
                new SludgeCorridor(),
                new WasteStorageDepot(),
                new DefectiveSentinelWorkshop(),
                new SolidRock()
        );
    }

    public static DungeonComponent getEntranceComponent() {
        return new EntranceRoom();
    }

    public static DungeonComponent getDataCoreVaultComponent() {
        return new DataCoreVault();
    }

    public static class EntranceRoom extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_entrance";
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
            }
        }
    }

    public static class CorridorStraight extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_corridor_straight";
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
            return (direction == DungeonDirection.NORTH || direction == DungeonDirection.SOUTH)
                    ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
        }
    }

    public static class CorridorCorner extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_corridor_corner";
        }

        @Override
        public int weight() {
            return 18;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return (direction == DungeonDirection.NORTH || direction == DungeonDirection.EAST)
                    ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            setLocalBlock(level, origin, rotation, 1, 1, 5, size, Blocks.CHISELED_DEEPSLATE.defaultBlockState());
        }
    }

    public static class CorridorTJunction extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_corridor_t_junction";
        }

        @Override
        public int weight() {
            return 14;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return direction != DungeonDirection.SOUTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
        }
    }

    public static class CorridorCrossroad extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_corridor_crossroad";
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
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            setLocalBlock(level, origin, rotation, 1, 1, 1, size, Blocks.CHISELED_DEEPSLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, Blocks.CHISELED_DEEPSLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 5, size, Blocks.CHISELED_DEEPSLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 5, size, Blocks.CHISELED_DEEPSLATE.defaultBlockState());
        }
    }

    public static class DeadEnd extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_dead_end";
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
            return direction == DungeonDirection.NORTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            BlockPos chestPos = origin.offset(rotation.rotateOffset(3, 1, 5, size));
            setLocalBlock(level, origin, rotation, 3, 1, 5, size, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH), 3);
            if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
                populateLoot(chest, random);
            }
            setLocalBlock(level, origin, rotation, 1, 1, 5, size, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 5, size, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState());
        }
    }

    public static class DataCoreVault extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_data_core_vault";
        }

        @Override
        public int weight() {
            return 4;
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
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            setLocalBlock(level, origin, rotation, 3, 0, 3, size, SandStormBlocks.PIEZO_QUARTZ_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 3, size, Blocks.CHISELED_DEEPSLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 3, size, SandStormBlocks.ANCIENT_DATA_CORE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 3, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 2, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 2, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 2, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 4, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 5, size, SandStormBlocks.PIEZO_QUARTZ_CLUSTER.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 5, size, SandStormBlocks.PIEZO_QUARTZ_CLUSTER.defaultBlockState());
        }
    }

    public static class EnergyChamber extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_energy_chamber";
        }

        @Override
        public int weight() {
            return 4;
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
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            BlockPos batteryPos = origin.offset(rotation.rotateOffset(3, 1, 3, size));
            level.setBlock(batteryPos, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
            if (level.getBlockEntity(batteryPos) instanceof SolidStateAccumulatorBlockEntity accumulator) {
                accumulator.setStoredEnergy(SolidStateAccumulatorBlockEntity.DEFAULT_CAPACITY / 2);
            }
            setLocalBlock(level, origin, rotation, 2, 1, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.PIEZO_QUARTZ_CLUSTER.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.PIEZO_QUARTZ_CLUSTER.defaultBlockState());
        }
    }

    public static class ServerArchive extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_server_archive";
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
            return (direction == DungeonDirection.NORTH || direction == DungeonDirection.SOUTH)
                    ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
            for (int y = 1; y <= 3; y++) {
                setLocalBlock(level, origin, rotation, 1, y, 2, size, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 1, y, 4, size, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, y, 2, size, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, y, 4, size, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState());
            }
        }
    }

    public static class SolidRock extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_solid_rock";
        }

        @Override
        public int weight() {
            return 5;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return DungeonSocket.SOLID;
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

    public static class SludgeCorridor extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_sludge_corridor";
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
            return (direction == DungeonDirection.NORTH || direction == DungeonDirection.SOUTH)
                    ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);

            for (int lz = 0; lz < size; lz++) {
                setLocalBlock(level, origin, rotation, 2, 0, lz, size, SandStormBlocks.RADIOACTIVE_SLUDGE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 3, 0, lz, size, SandStormBlocks.RADIOACTIVE_SLUDGE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 4, 0, lz, size, SandStormBlocks.RADIOACTIVE_SLUDGE.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 3, 1, 1, size, Blocks.POLISHED_DEEPSLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 3, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 5, size, Blocks.POLISHED_DEEPSLATE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 1, 1, 3, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 3, size, Blocks.IRON_BARS.defaultBlockState());
        }
    }

    public static class WasteStorageDepot extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_waste_storage_depot";
        }

        @Override
        public int weight() {
            return 5;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return direction == DungeonDirection.NORTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 1, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 2, size, SandStormBlocks.RADIOACTIVE_WASTE_BARREL.defaultBlockState());

            setLocalBlock(level, origin, rotation, 2, 0, 2, size, SandStormBlocks.RADIOACTIVE_SLUDGE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 0, 2, size, SandStormBlocks.RADIOACTIVE_SLUDGE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 0, 1, size, SandStormBlocks.RADIOACTIVE_SLUDGE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 3, 1, 2, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 2, size, Blocks.IRON_BARS.defaultBlockState());

            BlockPos chestPos = origin.offset(rotation.rotateOffset(3, 1, 4, size));
            setLocalBlock(level, origin, rotation, 3, 1, 4, size, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH), 3);
            if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chestBe) {
                populateLoot(chestBe, random);
            }
        }
    }

    public static class DefectiveSentinelWorkshop extends BaseDungeonComponent {
        @Override
        public String id() {
            return "bunker_sentinel_workshop";
        }

        @Override
        public int weight() {
            return 5;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public DungeonSocket getSocket(DungeonDirection direction) {
            return direction == DungeonDirection.NORTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 7, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            setLocalBlock(level, origin, rotation, 3, 6, 3, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);

            setLocalBlock(level, origin, rotation, 1, 1, 2, size, SandStormBlocks.SANDSTONE_WORKBENCH.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 3, size, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 4, size, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 2, size, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 3, size, Blocks.ANVIL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 4, size, SandStormBlocks.PIEZO_QUARTZ_CLUSTER.defaultBlockState());

            BlockPos spawnPos = origin.offset(rotation.rotateOffset(3, 1, 3, size));
            if (DefectiveSentinelEntity.canSpawnInArea(level, spawnPos)) {
                DefectiveSentinelEntity sentinel = new DefectiveSentinelEntity(SandStormEntities.DEFECTIVE_SENTINEL, level);
                sentinel.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                sentinel.setPersistenceRequired();
                level.addFreshEntity(sentinel);
            }
        }
    }

    private static void populateLoot(Container chest, RandomSource random) {
        int slots = chest.getContainerSize();
        chest.setItem(random.nextInt(slots), new ItemStack(SandStormItems.SILICON_WAFER, 1 + random.nextInt(3)));
        if (random.nextBoolean()) {
            chest.setItem(random.nextInt(slots), new ItemStack(SandStormItems.PIEZO_QUARTZ_SHARD, 2 + random.nextInt(4)));
        }
        if (random.nextInt(3) == 0) {
            chest.setItem(random.nextInt(slots), new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 1));
        }
        if (random.nextInt(4) == 0) {
            chest.setItem(random.nextInt(slots), new ItemStack(Items.REDSTONE, 4 + random.nextInt(8)));
        }
    }
}
