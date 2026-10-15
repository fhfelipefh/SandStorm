package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;
import java.util.Map;

public class MilitaryLabCatalog {

    public static List<DungeonComponent> getComponents() {
        return List.of(
                getEntranceComponent(),
                getStairwellDownComponent(),
                getStairwellUpComponent(),
                new CorridorStraight(),
                new CorridorCorner(),
                new CorridorTJunction(),
                new CorridorCrossroad(),
                new DeadEnd(),
                new SpecimenContainment(),
                new BallisticsTesting(),
                new TacticalCommand(),
                new MilitaryArmory(),
                new CryoStasis(),
                new PowerSubstation(),
                getClassifiedVaultComponent(),
                new SolidRock()
        );
    }

    public static DungeonComponent getEntranceComponent() {
        Identifier id = Identifier.fromNamespaceAndPath("sandstorm", "military_lab/military_lab_entrance");
        Map<DungeonDirection, DungeonSocket> sockets = Map.of(
                DungeonDirection.NORTH, DungeonSocket.WALL,
                DungeonDirection.EAST, DungeonSocket.WALL,
                DungeonDirection.SOUTH, DungeonSocket.CORRIDOR,
                DungeonDirection.WEST, DungeonSocket.WALL
        );
        return new NbtDungeonComponent("military_lab_entrance", id, 1, true, sockets, new EntranceAirlockFallback());
    }

    public static DungeonComponent getStairwellDownComponent() {
        Identifier id = Identifier.fromNamespaceAndPath("sandstorm", "military_lab/military_lab_stairwell_down");
        Map<DungeonDirection, DungeonSocket> sockets = Map.of(
                DungeonDirection.NORTH, DungeonSocket.CORRIDOR,
                DungeonDirection.EAST, DungeonSocket.CORRIDOR,
                DungeonDirection.SOUTH, DungeonSocket.CORRIDOR,
                DungeonDirection.WEST, DungeonSocket.CORRIDOR
        );
        return new NbtDungeonComponent("military_lab_stairwell_down", id, 1, true, sockets, new StairwellDownFallback());
    }

    public static DungeonComponent getStairwellUpComponent() {
        Identifier id = Identifier.fromNamespaceAndPath("sandstorm", "military_lab/military_lab_stairwell_up");
        Map<DungeonDirection, DungeonSocket> sockets = Map.of(
                DungeonDirection.NORTH, DungeonSocket.CORRIDOR,
                DungeonDirection.EAST, DungeonSocket.CORRIDOR,
                DungeonDirection.SOUTH, DungeonSocket.CORRIDOR,
                DungeonDirection.WEST, DungeonSocket.CORRIDOR
        );
        return new NbtDungeonComponent("military_lab_stairwell_up", id, 1, true, sockets, new StairwellUpFallback());
    }

    public static DungeonComponent getClassifiedVaultComponent() {
        Identifier id = Identifier.fromNamespaceAndPath("sandstorm", "military_lab/military_lab_classified_vault");
        Map<DungeonDirection, DungeonSocket> sockets = Map.of(
                DungeonDirection.NORTH, DungeonSocket.WALL,
                DungeonDirection.EAST, DungeonSocket.WALL,
                DungeonDirection.SOUTH, DungeonSocket.CORRIDOR,
                DungeonDirection.WEST, DungeonSocket.WALL
        );
        return new NbtDungeonComponent("military_lab_classified_vault", id, 1, true, sockets, new ClassifiedVaultFallback());
    }

    public abstract static class BaseMilitaryComponent extends BaseDungeonComponent {
        protected void buildMilitaryShell(ServerLevel level, BlockPos origin, DungeonRotation rotation, int size,
                                          DungeonSocket north, DungeonSocket east, DungeonSocket south, DungeonSocket west) {
            int midMin = size / 2 - 1;
            int midMax = size / 2 + 1;

            for (int lx = 0; lx < size; lx++) {
                for (int lz = 0; lz < size; lz++) {
                    boolean isCenterline = (lx == size / 2 && (north == DungeonSocket.CORRIDOR || south == DungeonSocket.CORRIDOR))
                            || (lz == size / 2 && (east == DungeonSocket.CORRIDOR || west == DungeonSocket.CORRIDOR));

                    BlockState floorState;
                    if (isCenterline && (lx + lz) % 2 == 0) {
                        floorState = ((lx + lz) % 4 == 0)
                                ? SandStormBlocks.RUSTED_STEEL_VENT_GRATE.defaultBlockState()
                                : SandStormBlocks.STEEL_VENT_GRATE.defaultBlockState();
                    } else if ((lx * 3 + lz * 5) % 6 == 0) {
                        floorState = SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState();
                    } else if ((lx * 3 + lz * 5) % 4 == 0) {
                        floorState = SandStormBlocks.WEATHERED_MILITARY_PLATE.defaultBlockState();
                    } else {
                        floorState = SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState();
                    }
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, floorState);

                    BlockPos worldFloor = origin.offset(rotation.rotateOffset(lx, 0, lz, size));
                    for (int fy = 1; fy <= 3; fy++) {
                        BlockPos foundationPos = worldFloor.below(fy);
                        if (level.getBlockState(foundationPos).isAir()) {
                            level.setBlock(foundationPos, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState(), 2);
                        }
                    }

                    BlockState ceilingState;
                    if (lx == size / 2 || lz == size / 2) {
                        ceilingState = ((lx + lz) % 3 == 0)
                                ? SandStormBlocks.RUSTED_VENT_DUCT.defaultBlockState()
                                : SandStormBlocks.REINFORCED_VENT_DUCT.defaultBlockState();
                    } else if ((lx * 2 + lz) % 5 == 0) {
                        ceilingState = SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState();
                    } else if ((lx * 2 + lz) % 3 == 0) {
                        ceilingState = SandStormBlocks.WEATHERED_MILITARY_PLATE.defaultBlockState();
                    } else {
                        ceilingState = SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState();
                    }
                    setLocalBlock(level, origin, rotation, lx, 5, lz, size, ceilingState);
                    setLocalBlock(level, origin, rotation, lx, 6, lz, size, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState());

                    for (int ly = 1; ly <= 4; ly++) {
                        boolean isNorthWall = (lz == 0);
                        boolean isSouthWall = (lz == size - 1);
                        boolean isWestWall = (lx == 0);
                        boolean isEastWall = (lx == size - 1);

                        if (isNorthWall || isSouthWall || isWestWall || isEastWall) {
                            boolean isDoorway = false;
                            if (isNorthWall && north == DungeonSocket.CORRIDOR && lx >= midMin && lx <= midMax && ly <= 3) {
                                isDoorway = true;
                            } else if (isSouthWall && south == DungeonSocket.CORRIDOR && lx >= midMin && lx <= midMax && ly <= 3) {
                                isDoorway = true;
                            } else if (isEastWall && east == DungeonSocket.CORRIDOR && lz >= midMin && lz <= midMax && ly <= 3) {
                                isDoorway = true;
                            } else if (isWestWall && west == DungeonSocket.CORRIDOR && lz >= midMin && lz <= midMax && ly <= 3) {
                                isDoorway = true;
                            }

                            if (isDoorway) {
                                setLocalBlock(level, origin, rotation, lx, ly, lz, size, Blocks.AIR.defaultBlockState());
                            } else {
                                BlockState wallState;
                                if (ly == 1) {
                                    int mod = (lx + lz) % 4;
                                    wallState = (mod == 0)
                                            ? SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState()
                                            : (mod == 1)
                                            ? SandStormBlocks.WEATHERED_MILITARY_PLATE.defaultBlockState()
                                            : SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState();
                                } else if (ly == 2) {
                                    int mod = (lx * 2 + lz) % 4;
                                    wallState = (mod == 0)
                                            ? SandStormBlocks.CORRODED_HAZARD_STRIPE.defaultBlockState()
                                            : (mod == 1 || mod == 2)
                                            ? SandStormBlocks.WEATHERED_HAZARD_STRIPE.defaultBlockState()
                                            : SandStormBlocks.HAZARD_STRIPE_BLOCK.defaultBlockState();
                                } else if (ly == 3) {
                                    int mod = (lx * 3 + lz) % 5;
                                    wallState = (mod == 0)
                                            ? SandStormBlocks.FRACTURED_MILITARY_PLATE.defaultBlockState()
                                            : (mod == 1)
                                            ? SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState()
                                            : (mod == 2)
                                            ? SandStormBlocks.WEATHERED_MILITARY_PLATE.defaultBlockState()
                                            : SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState();
                                } else {
                                    wallState = ((lx + lz) % 2 == 0)
                                            ? SandStormBlocks.REINFORCED_VENT_DUCT.defaultBlockState()
                                            : SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState();
                                }
                                setLocalBlock(level, origin, rotation, lx, ly, lz, size, wallState);
                            }
                        }
                    }
                }
            }

            setLocalBlock(level, origin, rotation, size / 2, 5, size / 2, size, Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true));
            setLocalBlock(level, origin, rotation, size / 2, 6, size / 2, size, Blocks.REDSTONE_BLOCK.defaultBlockState());
        }
    }

    public static class EntranceAirlockFallback extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_entrance";
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
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.TACTICAL_RADAR_CONSOLE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 1, size, SandStormBlocks.TACTICAL_RADAR_CONSOLE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 2, 1, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 2, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 5, size, SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 5, size, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState());
        }
    }

    public static class CorridorStraight extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_corridor_straight";
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
                    ? DungeonSocket.CORRIDOR
                    : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            for (int z = 1; z <= 5; z++) {
                setLocalBlock(level, origin, rotation, 1, 2, z, size, SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, 2, z, size, SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE.defaultBlockState());
            }
            setLocalBlock(level, origin, rotation, 1, 1, 3, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 4, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
        }
    }

    public static class CorridorCorner extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_corridor_corner";
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
            return (direction == DungeonDirection.NORTH || direction == DungeonDirection.EAST)
                    ? DungeonSocket.CORRIDOR
                    : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 1, 1, 5, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 5, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 1, 5, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.TACTICAL_RADAR_CONSOLE.defaultBlockState());
        }
    }

    public static class CorridorTJunction extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_corridor_t_junction";
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
            return direction == DungeonDirection.WEST ? DungeonSocket.WALL : DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 1, 1, 3, size, SandStormBlocks.TACTICAL_RADAR_CONSOLE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 3, size, SandStormBlocks.TACTICAL_RADAR_CONSOLE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 5, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
        }
    }

    public static class CorridorCrossroad extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_corridor_crossroad";
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
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);
            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 5, size, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 5, size, SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState());
        }
    }

    public static class DeadEnd extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_dead_end";
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
            return direction == DungeonDirection.SOUTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            for (int x = 1; x <= 5; x++) {
                setLocalBlock(level, origin, rotation, x, 1, 2, size, (x % 2 == 0)
                        ? SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState()
                        : SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState());
                setLocalBlock(level, origin, rotation, x, 2, 2, size, (x % 2 == 0)
                        ? SandStormBlocks.WEATHERED_HAZARD_STRIPE.defaultBlockState()
                        : SandStormBlocks.HAZARD_STRIPE_BLOCK.defaultBlockState());
            }
            setLocalBlock(level, origin, rotation, 3, 1, 1, size, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH));
            populateChest(level, origin, rotation, 3, 1, 1, size, random);
        }

        private void populateChest(ServerLevel level, BlockPos origin, DungeonRotation rotation, int lx, int ly, int lz, int size, RandomSource random) {
            BlockPos pos = origin.offset(rotation.rotateOffset(lx, ly, lz, size));
            if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
                chest.setItem(random.nextInt(chest.getContainerSize()), new ItemStack(Items.IRON_INGOT, 3 + random.nextInt(4)));
                if (random.nextBoolean()) {
                    chest.setItem(random.nextInt(chest.getContainerSize()), new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 1 + random.nextInt(2)));
                }
            }
        }
    }

    public static class SpecimenContainment extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_specimen_containment";
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
            return direction == DungeonDirection.SOUTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 1, 3, size, SandStormBlocks.AMNIOTIC_INCUBATOR.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 3, size, SandStormBlocks.MORPHING_ALLOY_WINDOW.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 3, 3, size, SandStormBlocks.MORPHING_ALLOY_WINDOW.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 4, 3, size, SandStormBlocks.REINFORCED_VENT_DUCT.defaultBlockState());

            setLocalBlock(level, origin, rotation, 2, 1, 3, size, SandStormBlocks.WEATHERED_HAZARD_STRIPE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 3, size, SandStormBlocks.WEATHERED_HAZARD_STRIPE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 2, size, SandStormBlocks.HAZARD_STRIPE_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 4, size, SandStormBlocks.HAZARD_STRIPE_BLOCK.defaultBlockState());

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.TACTICAL_RADAR_CONSOLE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 1, size, SandStormBlocks.TACTICAL_RADAR_CONSOLE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 2, 1, size, SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE.defaultBlockState());
        }
    }

    public static class BallisticsTesting extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_ballistics_testing";
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
            return direction == DungeonDirection.SOUTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            for (int x = 1; x <= 5; x++) {
                setLocalBlock(level, origin, rotation, x, 1, 1, size, SandStormBlocks.LEAD_SHIELDING_BLOCK.defaultBlockState());
                setLocalBlock(level, origin, rotation, x, 2, 1, size, (x % 2 == 0)
                        ? SandStormBlocks.CORRODED_HAZARD_STRIPE.defaultBlockState()
                        : SandStormBlocks.WEATHERED_HAZARD_STRIPE.defaultBlockState());
                setLocalBlock(level, origin, rotation, x, 3, 1, size, (x % 2 == 0)
                        ? SandStormBlocks.FRACTURED_MILITARY_PLATE.defaultBlockState()
                        : SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState());
            }
            setLocalBlock(level, origin, rotation, 3, 1, 4, size, SandStormBlocks.AUTONOMOUS_SONIC_TURRET.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 5, size, SandStormBlocks.DAMAGED_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 5, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 5, size, SandStormBlocks.BROKEN_RADAR_CONSOLE.defaultBlockState());
        }
    }

    public static class TacticalCommand extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_tactical_command";
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
            return direction == DungeonDirection.SOUTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 1, 3, size, SandStormBlocks.HOLO_TACTICAL_SPIRE.defaultBlockState());
            for (int x = 1; x <= 5; x++) {
                BlockState console = (x == 2 || x == 4)
                        ? SandStormBlocks.BROKEN_RADAR_CONSOLE.defaultBlockState()
                        : SandStormBlocks.TACTICAL_RADAR_CONSOLE.defaultBlockState();
                setLocalBlock(level, origin, rotation, x, 1, 1, size, console);
                setLocalBlock(level, origin, rotation, x, 2, 1, size, console);
            }
            setLocalBlock(level, origin, rotation, 1, 1, 4, size, SandStormBlocks.DAMAGED_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 4, size, SandStormBlocks.QUANTUM_ACCESS_TERMINAL.defaultBlockState());
        }
    }

    public static class MilitaryArmory extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_armory";
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
            return direction == DungeonDirection.SOUTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 1, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 1, 1, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 2, 1, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 1, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 3, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 3, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 2, 1, 4, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 4, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 2, size, SandStormBlocks.TACTICAL_RADAR_CONSOLE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 3, 1, 1, size, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH));
            populateArmoryChest(level, origin, rotation, 3, 1, 1, size, random);
        }

        private void populateArmoryChest(ServerLevel level, BlockPos origin, DungeonRotation rotation, int lx, int ly, int lz, int size, RandomSource random) {
            BlockPos pos = origin.offset(rotation.rotateOffset(lx, ly, lz, size));
            if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
                chest.setItem(0, new ItemStack(Items.IRON_SWORD));
                chest.setItem(1, new ItemStack(Items.BOW));
                chest.setItem(2, new ItemStack(Items.ARROW, 16 + random.nextInt(16)));
                if (random.nextBoolean()) {
                    chest.setItem(3, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 2));
                }
            }
        }
    }

    public static class CryoStasis extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_cryo_stasis";
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
            return direction == DungeonDirection.SOUTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 2, 1, 2, size, SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 2, size, SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 2, 2, size, SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 2, 2, size, SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 4, size, SandStormBlocks.TACTICAL_RADAR_CONSOLE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 4, size, SandStormBlocks.WEATHERED_HAZARD_STRIPE.defaultBlockState());
        }
    }

    public static class PowerSubstation extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_power_substation";
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
            return direction == DungeonDirection.SOUTH ? DungeonSocket.CORRIDOR : DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 1, 2, size, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 1, 2, size, SandStormBlocks.DAMAGED_VENT_DUCT.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 2, size, SandStormBlocks.RUSTED_VENT_DUCT.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 2, size, SandStormBlocks.REINFORCED_VENT_DUCT.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 4, size, SandStormBlocks.CORRODED_HAZARD_STRIPE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 4, size, SandStormBlocks.BROKEN_RADAR_CONSOLE.defaultBlockState());
        }
    }

    public static class StairwellDownFallback extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_stairwell_down";
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
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    for (int ly = -7; ly <= 0; ly++) {
                        if (lx == 2 && lz == 2) {
                            continue;
                        }
                        setLocalBlock(level, origin, rotation, lx, ly, lz, size, Blocks.AIR.defaultBlockState());
                    }
                }
            }

            for (int ly = -7; ly <= 4; ly++) {
                setLocalBlock(level, origin, rotation, 2, ly, 2, size, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState());
            }

            for (int ly = -7; ly <= 2; ly++) {
                setLocalBlock(level, origin, rotation, 2, ly, 3, size, Blocks.LADDER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.SOUTH)));
            }

            setLocalBlock(level, origin, rotation, 3, 1, 1, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 1, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 2, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 3, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 4, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 4, size, Blocks.IRON_BARS.defaultBlockState());

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.DAMAGED_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.TACTICAL_RADAR_CONSOLE.defaultBlockState());
        }
    }

    public static class StairwellUpFallback extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_stairwell_up";
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
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 5, lz, size, Blocks.AIR.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, 6, lz, size, Blocks.AIR.defaultBlockState());
                }
            }

            for (int ly = 1; ly <= 8; ly++) {
                setLocalBlock(level, origin, rotation, 2, ly, 2, size, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState());
                setLocalBlock(level, origin, rotation, 2, ly, 3, size, Blocks.LADDER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.SOUTH)));
            }

            setLocalBlock(level, origin, rotation, 1, 1, 5, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 5, size, SandStormBlocks.BROKEN_RADAR_CONSOLE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState());
        }
    }

    public static class ClassifiedVaultFallback extends BaseMilitaryComponent {
        @Override
        public String id() {
            return "military_lab_classified_vault";
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
            buildMilitaryShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);
            setLocalBlock(level, origin, rotation, 3, 1, 2, size, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH));
            populateVaultChest(level, origin, rotation, 3, 1, 2, size, random);

            setLocalBlock(level, origin, rotation, 2, 1, 2, size, SandStormBlocks.FRACTURED_MILITARY_PLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 2, size, SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 2, 2, size, SandStormBlocks.CORRODED_HAZARD_STRIPE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 2, 2, size, SandStormBlocks.WEATHERED_HAZARD_STRIPE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 2, size, Blocks.IRON_BARS.defaultBlockState());

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.BROKEN_RADAR_CONSOLE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.QUANTUM_ACCESS_TERMINAL.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 4, size, SandStormBlocks.HOLO_TACTICAL_SPIRE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 3, size, SandStormBlocks.DAMAGED_MUNITIONS_CRATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 3, size, SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState());
        }

        private void populateVaultChest(ServerLevel level, BlockPos origin, DungeonRotation rotation, int lx, int ly, int lz, int size, RandomSource random) {
            BlockPos pos = origin.offset(rotation.rotateOffset(lx, ly, lz, size));
            if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
                chest.setItem(0, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 4 + random.nextInt(4)));
                chest.setItem(1, new ItemStack(Items.DIAMOND, 2 + random.nextInt(3)));
                chest.setItem(2, new ItemStack(Items.NETHERITE_SCRAP, 1));
            }
        }
    }

    public static class SolidRock extends BaseDungeonComponent {
        @Override
        public String id() {
            return "military_lab_solid_rock";
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
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            for (int lx = 0; lx < size; lx++) {
                for (int ly = 0; ly < size; ly++) {
                    for (int lz = 0; lz < size; lz++) {
                        setLocalBlock(level, origin, rotation, lx, ly, lz, size, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState());
                    }
                }
            }
        }
    }
}
