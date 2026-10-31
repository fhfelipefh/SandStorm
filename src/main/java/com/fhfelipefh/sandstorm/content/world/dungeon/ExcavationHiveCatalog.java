package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.RustingBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class ExcavationHiveCatalog {

    public static List<DungeonComponent> getComponents() {
        return List.of(
                getEntranceComponent(),
                getStairwellDownComponent(),
                getStairwellUpComponent(),
                new ExcavationCorridorStraight(),
                new ExcavationCorridorCorner(),
                new ExcavationCorridorTJunction(),
                new ExcavationCorridorCrossroad(),
                new CollapsedDeadEnd(),
                new WormBurrowStraight(),
                new WormBurrowTurn(),
                new IndustrialBreachChamber(),
                new SandwormIncubationNursery(),
                new PiezoGeodeChamber(),
                new CollapsingGalleryRoom(),
                getQueensReliquaryComponent(),
                new SolidRock()
        );
    }

    public static DungeonComponent getEntranceComponent() {
        return new EntranceShaftRoom();
    }

    public static DungeonComponent getStairwellDownComponent() {
        return new StairwellDown();
    }

    public static DungeonComponent getStairwellUpComponent() {
        return new StairwellUp();
    }

    public static DungeonComponent getQueensReliquaryComponent() {
        return new QueensReliquaryVault();
    }

    public static DungeonComponent getMacroDrillQuadrantNW() {
        return new MacroDrillQuadrantNW();
    }

    public static DungeonComponent getMacroDrillQuadrantNE() {
        return new MacroDrillQuadrantNE();
    }

    public static DungeonComponent getMacroDrillQuadrantSW() {
        return new MacroDrillQuadrantSW();
    }

    public static DungeonComponent getMacroDrillQuadrantSE() {
        return new MacroDrillQuadrantSE();
    }

    private static BlockState glowLichenFloor() {
        return Blocks.GLOW_LICHEN.defaultBlockState().setValue(BlockStateProperties.DOWN, true);
    }

    private static BlockState glowLichenCeiling() {
        return Blocks.GLOW_LICHEN.defaultBlockState().setValue(BlockStateProperties.UP, true);
    }

    private static BlockState glowLichenWall(Direction facing) {
        return switch (facing) {
            case NORTH -> Blocks.GLOW_LICHEN.defaultBlockState().setValue(BlockStateProperties.NORTH, true);
            case SOUTH -> Blocks.GLOW_LICHEN.defaultBlockState().setValue(BlockStateProperties.SOUTH, true);
            case WEST -> Blocks.GLOW_LICHEN.defaultBlockState().setValue(BlockStateProperties.WEST, true);
            default -> Blocks.GLOW_LICHEN.defaultBlockState().setValue(BlockStateProperties.EAST, true);
        };
    }

    private static void setSafeChest(ServerLevel level, BlockPos pos, Direction facing, boolean isVault, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof Container container) {
            container.clearContent();
        }
        level.setBlock(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing), 3);
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            if (isVault) {
                populateVaultLoot(chest, random);
            } else {
                populateMiningLoot(chest, random);
            }
        }
    }

    private static void populateMiningLoot(Container chest, RandomSource random) {
        int slots = chest.getContainerSize();
        chest.setItem(random.nextInt(slots), new ItemStack(Items.RAW_IRON, 3 + random.nextInt(6)));
        chest.setItem(random.nextInt(slots), new ItemStack(Items.RAW_COPPER, 3 + random.nextInt(6)));
        chest.setItem(random.nextInt(slots), new ItemStack(SandStormItems.RAW_SILICON, 2 + random.nextInt(4)));
        if (random.nextBoolean()) {
            chest.setItem(random.nextInt(slots), new ItemStack(SandStormItems.PIEZO_QUARTZ_SHARD, 1 + random.nextInt(3)));
        }
        if (random.nextBoolean()) {
            chest.setItem(random.nextInt(slots), new ItemStack(SandStormItems.SANDWORM_CHITIN, 1 + random.nextInt(2)));
        }
        if (random.nextInt(3) == 0) {
            chest.setItem(random.nextInt(slots), new ItemStack(SandStormItems.SCRAP_METAL, 2 + random.nextInt(4)));
        }
    }

    private static void populateVaultLoot(Container chest, RandomSource random) {
        int slots = chest.getContainerSize();
        chest.setItem(0, new ItemStack(SandStormItems.TECH_DISC, 1));
        chest.setItem(1, new ItemStack(SandStormItems.SANDWORM_TOOTH, 2 + random.nextInt(2)));
        chest.setItem(2, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 2 + random.nextInt(3)));
        chest.setItem(3, new ItemStack(SandStormItems.PIEZO_RESONATOR, 1));
        chest.setItem(random.nextInt(slots), new ItemStack(SandStormItems.SUIT_UPGRADE_SEISMIC, 1));
        chest.setItem(random.nextInt(slots), new ItemStack(SandStormItems.GEOLOGICAL_SCANNER, 1));
        chest.setItem(random.nextInt(slots), new ItemStack(SandStormItems.SANDWORM_CHITIN, 4 + random.nextInt(5)));
        chest.setItem(random.nextInt(slots), new ItemStack(SandStormItems.PIEZO_QUARTZ_SHARD, 4 + random.nextInt(6)));
    }

    public static class EntranceShaftRoom extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_entrance_shaft";
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

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 5, lz, size, Blocks.IRON_BARS.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                }
            }

            for (int ly = 1; ly <= 4; ly++) {
                setLocalBlock(level, origin, rotation, 2, ly, 2, size, Blocks.SCAFFOLDING.defaultBlockState());
                setLocalBlock(level, origin, rotation, 4, ly, 2, size, Blocks.SCAFFOLDING.defaultBlockState());
                setLocalBlock(level, origin, rotation, 2, ly, 4, size, Blocks.IRON_CHAIN.defaultBlockState());
                setLocalBlock(level, origin, rotation, 4, ly, 4, size, Blocks.IRON_CHAIN.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 3, 1, 3, size, Blocks.ANVIL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.REINFORCED_CONCRETE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, glowLichenFloor());

            BlockPos chestPos = origin.offset(rotation.rotateOffset(1, 1, 2, size));
            setSafeChest(level, chestPos, rotation.rotateDirection(Direction.EAST), false, random);
        }
    }

    public static class StairwellDown extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_stairwell_down";
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
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    for (int dy = 0; dy >= -8; dy--) {
                        if (lx == 2 && lz == 2) {
                            setLocalBlock(level, origin, rotation, lx, dy, lz, size, SandStormBlocks.WEATHERED_REINFORCED_CONCRETE.defaultBlockState());
                        } else if (lx == 2 && lz == 3) {
                            setLocalBlock(level, origin, rotation, lx, dy, lz, size, Blocks.LADDER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.SOUTH)));
                        } else if (lx == 4 && lz == 4) {
                            setLocalBlock(level, origin, rotation, lx, dy, lz, size, Blocks.IRON_CHAIN.defaultBlockState());
                        } else {
                            setLocalBlock(level, origin, rotation, lx, dy, lz, size, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, glowLichenFloor());
            setLocalBlock(level, origin, rotation, 5, 1, 5, size, glowLichenFloor());
        }
    }

    public static class StairwellUp extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_stairwell_up";
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
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 5, lz, size, Blocks.AIR.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, 6, lz, size, Blocks.AIR.defaultBlockState());
                }
            }

            for (int ly = 1; ly <= 8; ly++) {
                setLocalBlock(level, origin, rotation, 2, ly, 2, size, SandStormBlocks.WEATHERED_REINFORCED_CONCRETE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 2, ly, 3, size, Blocks.LADDER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.SOUTH)));
            }

            setLocalBlock(level, origin, rotation, 1, 1, 5, size, glowLichenFloor());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, glowLichenFloor());
        }
    }

    public static class ExcavationCorridorStraight extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_corridor_straight";
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
            if (direction == DungeonDirection.NORTH || direction == DungeonDirection.SOUTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lz = 1; lz <= 5; lz++) {
                setLocalBlock(level, origin, rotation, 3, 1, lz, size, Blocks.RAIL.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 1, 2, 3, size, Blocks.DARK_OAK_FENCE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 3, 3, size, Blocks.DARK_OAK_FENCE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 2, 3, size, Blocks.DARK_OAK_FENCE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 3, 3, size, Blocks.DARK_OAK_FENCE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 4, 3, size, Blocks.DARK_OAK_SLAB.defaultBlockState());

            if (random.nextInt(3) == 0) {
                setLocalBlock(level, origin, rotation, 2, 1, 2, size, Blocks.GRAVEL.defaultBlockState());
            }
        }
    }

    public static class ExcavationCorridorCorner extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_corridor_corner";
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
            if (direction == DungeonDirection.NORTH || direction == DungeonDirection.EAST) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 2, 1, 2, size, SandStormBlocks.WEATHERED_REINFORCED_CONCRETE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 2, 2, size, Blocks.IRON_CHAIN.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 3, 2, size, glowLichenWall(Direction.NORTH));

            setLocalBlock(level, origin, rotation, 3, 1, 1, size, Blocks.RAIL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 2, size, Blocks.RAIL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 3, size, Blocks.RAIL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 3, size, Blocks.RAIL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 3, size, Blocks.RAIL.defaultBlockState());
        }
    }

    public static class ExcavationCorridorTJunction extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_corridor_t_junction";
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
            if (direction == DungeonDirection.WEST) {
                return DungeonSocket.WALL;
            }
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lz = 1; lz <= 5; lz++) {
                setLocalBlock(level, origin, rotation, 3, 1, lz, size, Blocks.RAIL.defaultBlockState());
            }
            for (int lx = 3; lx <= 5; lx++) {
                setLocalBlock(level, origin, rotation, lx, 1, 3, size, Blocks.RAIL.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 1, 1, 2, size, Blocks.SCAFFOLDING.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 2, size, Blocks.SCAFFOLDING.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 4, size, Blocks.RAW_COPPER_BLOCK.defaultBlockState());
        }
    }

    public static class ExcavationCorridorCrossroad extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_corridor_crossroad";
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
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);

            for (int i = 1; i <= 5; i++) {
                setLocalBlock(level, origin, rotation, 3, 1, i, size, Blocks.RAIL.defaultBlockState());
                setLocalBlock(level, origin, rotation, i, 1, 3, size, Blocks.RAIL.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 2, 1, 2, size, SandStormBlocks.REINFORCED_CONCRETE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 2, size, SandStormBlocks.REINFORCED_CONCRETE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 1, 4, size, SandStormBlocks.REINFORCED_CONCRETE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 4, size, SandStormBlocks.REINFORCED_CONCRETE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 4, 3, size, Blocks.IRON_CHAIN.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 3, 3, size, glowLichenCeiling());
        }
    }

    public static class CollapsedDeadEnd extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_collapsed_dead_end";
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
            return direction == DungeonDirection.SOUTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lx = 1; lx <= 5; lx++) {
                for (int ly = 1; ly <= 3; ly++) {
                    BlockState fill = (lx + ly) % 2 == 0 ? Blocks.SANDSTONE.defaultBlockState() : Blocks.GRAVEL.defaultBlockState();
                    setLocalBlock(level, origin, rotation, lx, ly, 1, size, fill);
                    if (ly <= 2 && (lx == 2 || lx == 3 || lx == 4)) {
                        setLocalBlock(level, origin, rotation, lx, ly, 2, size, Blocks.SUSPICIOUS_SAND.defaultBlockState());
                    }
                }
            }

            BlockPos chestPos = origin.offset(rotation.rotateOffset(2, 1, 3, size));
            setSafeChest(level, chestPos, rotation.rotateDirection(Direction.NORTH), false, random);
        }
    }

    public static class WormBurrowStraight extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_worm_burrow_straight";
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
            if (direction == DungeonDirection.NORTH || direction == DungeonDirection.SOUTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lz = 1; lz <= 5; lz++) {
                setLocalBlock(level, origin, rotation, 2, 0, lz, size, Blocks.SMOOTH_BASALT.defaultBlockState());
                setLocalBlock(level, origin, rotation, 3, 0, lz, size, Blocks.SAND.defaultBlockState());
                setLocalBlock(level, origin, rotation, 4, 0, lz, size, Blocks.SMOOTH_BASALT.defaultBlockState());

                if (lz % 2 == 0) {
                    setLocalBlock(level, origin, rotation, 1, 2, lz, size, Blocks.BONE_BLOCK.defaultBlockState());
                    setLocalBlock(level, origin, rotation, 5, 2, lz, size, Blocks.BONE_BLOCK.defaultBlockState());
                    setLocalBlock(level, origin, rotation, 3, 4, lz, size, Blocks.BONE_BLOCK.defaultBlockState());
                } else {
                    setLocalBlock(level, origin, rotation, 1, 2, lz, size, Blocks.SMOOTH_SANDSTONE.defaultBlockState());
                    setLocalBlock(level, origin, rotation, 5, 2, lz, size, Blocks.SMOOTH_SANDSTONE.defaultBlockState());
                }
            }

            if (random.nextBoolean()) {
                setLocalBlock(level, origin, rotation, 3, 1, 3, size, Blocks.COBWEB.defaultBlockState());
            }
        }
    }

    public static class WormBurrowTurn extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_worm_burrow_turn";
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
            if (direction == DungeonDirection.NORTH || direction == DungeonDirection.EAST) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL);

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, Blocks.SMOOTH_BASALT.defaultBlockState());
                }
            }

            setLocalBlock(level, origin, rotation, 1, 1, 3, size, Blocks.BONE_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 3, size, Blocks.BONE_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 5, size, Blocks.BONE_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 5, size, Blocks.BONE_BLOCK.defaultBlockState());

            setLocalBlock(level, origin, rotation, 2, 1, 2, size, Blocks.COBWEB.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 4, size, Blocks.SAND.defaultBlockState());
        }
    }

    public static class IndustrialBreachChamber extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_industrial_breach_chamber";
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
            if (direction == DungeonDirection.NORTH || direction == DungeonDirection.SOUTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lz = 1; lz <= 5; lz++) {
                setLocalBlock(level, origin, rotation, 1, 1, lz, size, SandStormBlocks.LEAD_BLOCK.defaultBlockState());
                setLocalBlock(level, origin, rotation, 1, 2, lz, size, SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE.defaultBlockState());
            }

            for (int lx = 3; lx <= 5; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, Blocks.GRAVEL.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, 1, lz, size, Blocks.SAND.defaultBlockState());
                }
            }

            setLocalBlock(level, origin, rotation, 5, 2, 3, size, Blocks.AIR.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 3, 3, size, Blocks.AIR.defaultBlockState());

            setLocalBlock(level, origin, rotation, 2, 1, 3, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 2, 3, size, glowLichenWall(Direction.WEST));

            BlockPos chestPos = origin.offset(rotation.rotateOffset(1, 1, 2, size));
            setSafeChest(level, chestPos, rotation.rotateDirection(Direction.EAST), false, random);
        }
    }

    public static class SandwormIncubationNursery extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_sandworm_incubation_nursery";
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
            return direction == DungeonDirection.NORTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.WALL);

            for (int lx = 1; lx <= 5; lx++) {
                for (int lz = 1; lz <= 5; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, Blocks.SOUL_SAND.defaultBlockState());
                }
            }

            setLocalBlock(level, origin, rotation, 2, 1, 3, size, Blocks.DRAGON_EGG.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 3, size, Blocks.DRAGON_EGG.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 4, size, Blocks.SUSPICIOUS_SAND.defaultBlockState());

            setLocalBlock(level, origin, rotation, 2, 1, 2, size, Blocks.COBWEB.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 2, size, Blocks.COBWEB.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 2, size, Blocks.BONE_BLOCK.defaultBlockState());

            setLocalBlock(level, origin, rotation, 3, 5, 3, size, Blocks.POLISHED_DEEPSLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 4, 3, size, glowLichenCeiling());
        }
    }

    public static class PiezoGeodeChamber extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_piezo_geode_chamber";
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
            if (direction == DungeonDirection.SOUTH || direction == DungeonDirection.EAST) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lx = 1; lx <= 5; lx++) {
                for (int lz = 1; lz <= 5; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, Blocks.SMOOTH_BASALT.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, 5, lz, size, Blocks.AMETHYST_BLOCK.defaultBlockState());
                }
            }

            setLocalBlock(level, origin, rotation, 2, 1, 2, size, Blocks.AMETHYST_CLUSTER.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP));
            setLocalBlock(level, origin, rotation, 4, 1, 2, size, Blocks.AMETHYST_CLUSTER.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP));
            setLocalBlock(level, origin, rotation, 2, 4, 4, size, Blocks.AMETHYST_CLUSTER.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.DOWN));
            setLocalBlock(level, origin, rotation, 4, 4, 4, size, Blocks.AMETHYST_CLUSTER.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.DOWN));

            setLocalBlock(level, origin, rotation, 3, 1, 3, size, glowLichenFloor());
            setLocalBlock(level, origin, rotation, 3, 4, 3, size, glowLichenCeiling());
        }
    }

    public static class CollapsingGalleryRoom extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_collapsing_gallery";
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
            if (direction == DungeonDirection.WEST) {
                return DungeonSocket.WALL;
            }
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 5, lz, size, Blocks.SUSPICIOUS_SAND.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, 6, lz, size, Blocks.GRAVEL.defaultBlockState());
                }
            }

            setLocalBlock(level, origin, rotation, 2, 1, 2, size, Blocks.DARK_OAK_FENCE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 2, 2, size, Blocks.DARK_OAK_FENCE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 3, 2, size, Blocks.DARK_OAK_FENCE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 4, 2, size, Blocks.DARK_OAK_FENCE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 4, 1, 4, size, Blocks.DARK_OAK_FENCE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 2, 4, size, Blocks.DARK_OAK_FENCE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 3, 4, size, Blocks.DARK_OAK_FENCE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 4, 4, size, Blocks.DARK_OAK_FENCE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 3, 1, 3, size, Blocks.TNT.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 0, 3, size, Blocks.REDSTONE_BLOCK.defaultBlockState());
        }
    }

    public static class MacroDrillQuadrantNW extends BaseDungeonComponent {
        @Override
        public String id() {
            return "macro_drill_quadrant_nw";
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
            if (direction == DungeonDirection.EAST || direction == DungeonDirection.SOUTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lx = 2; lx <= 6; lx++) {
                for (int ly = 1; ly <= 4; ly++) {
                    setLocalBlock(level, origin, rotation, lx, ly, 1, size, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState().setValue(RustingBlock.RUST, RustingBlock.selectRuinRust(random)));
                }
            }

            for (int ly = 1; ly <= 4; ly++) {
                setLocalBlock(level, origin, rotation, 4, ly, 3, size, Blocks.IRON_BLOCK.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, ly, 3, size, Blocks.HEAVY_CORE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 6, ly, 3, size, Blocks.GRINDSTONE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.SOUTH)));
            }

            setLocalBlock(level, origin, rotation, 3, 1, 5, size, Blocks.SCAFFOLDING.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 5, size, Blocks.SCAFFOLDING.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 3, 5, size, glowLichenWall(Direction.SOUTH));
        }
    }

    public static class MacroDrillQuadrantNE extends BaseDungeonComponent {
        @Override
        public String id() {
            return "macro_drill_quadrant_ne";
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
                return DungeonSocket.WALL;
            }
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);

            for (int lx = 0; lx <= 4; lx++) {
                for (int ly = 1; ly <= 4; ly++) {
                    setLocalBlock(level, origin, rotation, lx, ly, 1, size, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState().setValue(RustingBlock.RUST, RustingBlock.selectRuinRust(random)));
                }
            }

            for (int ly = 1; ly <= 4; ly++) {
                setLocalBlock(level, origin, rotation, 0, ly, 3, size, Blocks.GRINDSTONE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.SOUTH)));
                setLocalBlock(level, origin, rotation, 1, ly, 3, size, Blocks.HEAVY_CORE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 2, ly, 3, size, Blocks.IRON_BLOCK.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 4, 1, 5, size, Blocks.SCAFFOLDING.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 2, 5, size, Blocks.SCAFFOLDING.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 3, 5, size, glowLichenWall(Direction.SOUTH));
        }
    }

    public static class MacroDrillQuadrantSW extends BaseDungeonComponent {
        @Override
        public String id() {
            return "macro_drill_quadrant_sw";
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
            if (direction == DungeonDirection.SOUTH) {
                return DungeonSocket.WALL;
            }
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR);

            for (int lx = 1; lx <= 6; lx++) {
                for (int lz = 1; lz <= 5; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, Blocks.AIR.defaultBlockState());
                }
            }

            for (int lz = 0; lz <= 5; lz++) {
                setLocalBlock(level, origin, rotation, 3, 1, lz, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 4, 1, lz, size, Blocks.IRON_BARS.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 2, 4, 2, size, Blocks.IRON_CHAIN.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 3, 2, size, Blocks.IRON_CHAIN.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 2, 2, size, glowLichenWall(Direction.NORTH));
        }
    }

    public static class MacroDrillQuadrantSE extends BaseDungeonComponent {
        @Override
        public String id() {
            return "macro_drill_quadrant_se";
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
            if (direction == DungeonDirection.EAST) {
                return DungeonSocket.WALL;
            }
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildBaseShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);

            for (int lx = 0; lx <= 5; lx++) {
                for (int lz = 1; lz <= 5; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, Blocks.AIR.defaultBlockState());
                }
            }

            for (int lz = 0; lz <= 5; lz++) {
                setLocalBlock(level, origin, rotation, 2, 1, lz, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 3, 1, lz, size, Blocks.IRON_BARS.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 4, 4, 4, size, Blocks.IRON_CHAIN.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 3, 4, size, Blocks.IRON_CHAIN.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 2, 4, size, glowLichenWall(Direction.NORTH));
        }
    }

    public static class QueensReliquaryVault extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_queens_reliquary_vault";
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

            for (int lx = 1; lx <= 5; lx++) {
                for (int lz = 1; lz <= 5; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, SandStormBlocks.LEAD_SHIELDING_BLOCK.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, 5, lz, size, SandStormBlocks.LEAD_SHIELDING_BLOCK.defaultBlockState());
                }
            }

            for (int ly = 1; ly <= 4; ly++) {
                setLocalBlock(level, origin, rotation, 1, ly, 1, size, SandStormBlocks.VAULT_WALL_SEAM.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, ly, 1, size, SandStormBlocks.VAULT_WALL_SEAM.defaultBlockState());
                setLocalBlock(level, origin, rotation, 1, ly, 5, size, SandStormBlocks.VAULT_WALL_SEAM.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, ly, 5, size, SandStormBlocks.VAULT_WALL_SEAM.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 2, 1, 3, size, Blocks.BONE_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 3, size, Blocks.BONE_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 2, size, Blocks.BONE_BLOCK.defaultBlockState());

            setLocalBlock(level, origin, rotation, 3, 5, 3, size, SandStormBlocks.FULGURITE_GLASS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 4, 3, size, Blocks.IRON_CHAIN.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 3, 3, size, glowLichenCeiling());

            BlockPos chestPos = origin.offset(rotation.rotateOffset(3, 1, 3, size));
            setSafeChest(level, chestPos, rotation.rotateDirection(Direction.SOUTH), true, random);
        }
    }

    public static class SolidRock extends BaseDungeonComponent {
        @Override
        public String id() {
            return "excavation_solid_rock";
        }

        @Override
        public int weight() {
            return 2;
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
                        BlockState state = (lx + lz + ly) % 2 == 0
                                ? Blocks.DEEPSLATE.defaultBlockState()
                                : Blocks.SMOOTH_SANDSTONE.defaultBlockState();
                        setLocalBlock(level, origin, rotation, lx, ly, lz, size, state);
                    }
                }
            }
        }
    }
}
