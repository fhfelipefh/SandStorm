package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;

public class CryoVaultCatalog {

    public static List<DungeonComponent> getComponents() {
        return List.of(
                getEntranceComponent(),
                getStairwellDownComponent(),
                getStairwellUpComponent(),
                new CryoCorridorStraight(),
                new CryoCorridorCorner(),
                new CryoCorridorTJunction(),
                new CryoCorridorCrossroad(),
                new CryoDeadEnd(),
                new CryoStasisBay(),
                new NitrogenCompressorChamber(),
                new PermafrostChasmCrossing(),
                getSubZeroSeedVaultComponent(),
                new SolidRock()
        );
    }

    public static DungeonComponent getEntranceComponent() {
        return new EntranceAirLockRoom();
    }

    public static DungeonComponent getStairwellDownComponent() {
        return new StairwellDown();
    }

    public static DungeonComponent getStairwellUpComponent() {
        return new StairwellUp();
    }

    public static DungeonComponent getSubZeroSeedVaultComponent() {
        return new SubZeroSeedVault();
    }

    public abstract static class BaseCryoVaultComponent extends BaseDungeonComponent {
        protected void buildCryoShell(ServerLevel level, BlockPos origin, DungeonRotation rotation, int size,
                                      DungeonSocket north, DungeonSocket east, DungeonSocket south, DungeonSocket west) {
            int midMin = size / 2 - 1;
            int midMax = size / 2 + 1;

            for (int lx = 0; lx < size; lx++) {
                for (int lz = 0; lz < size; lz++) {
                    boolean isCenterline = (lx == size / 2 && (north == DungeonSocket.CORRIDOR || south == DungeonSocket.CORRIDOR))
                            || (lz == size / 2 && (east == DungeonSocket.CORRIDOR || west == DungeonSocket.CORRIDOR));

                    BlockState floorState;
                    if (isCenterline) {
                        floorState = (lx + lz) % 2 == 0 ? Blocks.BLUE_ICE.defaultBlockState() : Blocks.PACKED_ICE.defaultBlockState();
                    } else {
                        floorState = (lx + lz) % 2 == 0 ? SandStormBlocks.CRYO_INSULATION.defaultBlockState() : Blocks.POLISHED_DEEPSLATE.defaultBlockState();
                    }
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, floorState);

                    BlockPos worldFloor = origin.offset(rotation.rotateOffset(lx, 0, lz, size));
                    for (int fy = 1; fy <= 3; fy++) {
                        BlockPos foundationPos = worldFloor.below(fy);
                        if (level.getBlockState(foundationPos).isAir()) {
                            level.setBlock(foundationPos, Blocks.PACKED_ICE.defaultBlockState(), 2);
                        }
                    }

                    BlockState ceilingState = (lx == 0 || lx == size - 1 || lz == 0 || lz == size - 1)
                            ? SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState()
                            : SandStormBlocks.CRYO_INSULATION.defaultBlockState();
                    setLocalBlock(level, origin, rotation, lx, 5, lz, size, ceilingState);

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
                                if (ly == 1 || ly == 4) {
                                    wallState = SandStormBlocks.CRYO_INSULATION.defaultBlockState();
                                } else if ((lx + lz) % 3 == 0) {
                                    wallState = SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState();
                                } else {
                                    wallState = Blocks.PACKED_ICE.defaultBlockState();
                                }
                                setLocalBlock(level, origin, rotation, lx, ly, lz, size, wallState);
                            }
                        } else {
                            setLocalBlock(level, origin, rotation, lx, ly, lz, size, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    public static class EntranceAirLockRoom extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_entrance";
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
            buildCryoShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 2, 1, 2, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 2, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 2, 2, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 2, 2, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 3, 2, size, SandStormBlocks.CRYO_INSULATION.defaultBlockState());

            setLocalBlock(level, origin, rotation, 3, 1, 2, size, Blocks.IRON_DOOR.defaultBlockState()
                    .setValue(DoorBlock.FACING, rotation.rotateDirection(Direction.NORTH))
                    .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
            setLocalBlock(level, origin, rotation, 3, 2, 2, size, Blocks.IRON_DOOR.defaultBlockState()
                    .setValue(DoorBlock.FACING, rotation.rotateDirection(Direction.NORTH))
                    .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));

            setLocalBlock(level, origin, rotation, 2, 2, 3, size, Blocks.LEVER.defaultBlockState()
                    .setValue(LeverBlock.FACING, rotation.rotateDirection(Direction.SOUTH))
                    .setValue(LeverBlock.FACE, AttachFace.WALL));

            setLocalBlock(level, origin, rotation, 1, 2, 3, size, SandStormBlocks.CRYO_SPRAYER.defaultBlockState()
                    .setValue(BlockStateProperties.FACING, rotation.rotateDirection(Direction.EAST)));
            setLocalBlock(level, origin, rotation, 5, 2, 3, size, SandStormBlocks.CRYO_SPRAYER.defaultBlockState()
                    .setValue(BlockStateProperties.FACING, rotation.rotateDirection(Direction.WEST)));

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
        }
    }

    public static class StairwellDown extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_stairwell_down";
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
            buildCryoShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);

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
                setLocalBlock(level, origin, rotation, 2, ly, 2, size, SandStormBlocks.CRYO_INSULATION.defaultBlockState());
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

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
        }
    }

    public static class StairwellUp extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_stairwell_up";
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
            buildCryoShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 5, lz, size, Blocks.AIR.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, 6, lz, size, Blocks.AIR.defaultBlockState());
                }
            }

            for (int ly = 1; ly <= 8; ly++) {
                setLocalBlock(level, origin, rotation, 2, ly, 2, size, SandStormBlocks.CRYO_INSULATION.defaultBlockState());
                setLocalBlock(level, origin, rotation, 2, ly, 3, size, Blocks.LADDER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.SOUTH)));
            }

            setLocalBlock(level, origin, rotation, 1, 1, 5, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 5, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
        }
    }

    public static class SubZeroSeedVault extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_subzero_seed_vault";
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
            buildCryoShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 3, 1, 2, size, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, rotation.rotateDirection(Direction.SOUTH)));
            populateVaultChest(level, origin, rotation, 3, 1, 2, size, random);

            setLocalBlock(level, origin, rotation, 3, 1, 1, size, SandStormBlocks.LEAD_SHIELDING_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 1, size, SandStormBlocks.LEAD_SHIELDING_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 1, 2, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 2, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 0, 2, size, Blocks.BLUE_ICE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 1, 2, 2, size, SandStormBlocks.CRYO_SPRAYER.defaultBlockState().setValue(BlockStateProperties.FACING, rotation.rotateDirection(Direction.EAST)));
            setLocalBlock(level, origin, rotation, 5, 2, 2, size, SandStormBlocks.CRYO_SPRAYER.defaultBlockState().setValue(BlockStateProperties.FACING, rotation.rotateDirection(Direction.WEST)));

            setLocalBlock(level, origin, rotation, 1, 1, 2, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 2, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());

            setLocalBlock(level, origin, rotation, 1, 1, 4, size, SandStormBlocks.CRYOSTASIS_POD.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.EAST)));
            setLocalBlock(level, origin, rotation, 5, 1, 4, size, SandStormBlocks.CRYOSTASIS_POD.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.WEST)));

            setLocalBlock(level, origin, rotation, 2, 1, 4, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 4, size, Blocks.IRON_BARS.defaultBlockState());
        }

        private void populateVaultChest(ServerLevel level, BlockPos origin, DungeonRotation rotation, int lx, int ly, int lz, int size, RandomSource random) {
            BlockPos pos = origin.offset(rotation.rotateOffset(lx, ly, lz, size));
            if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
                chest.setItem(0, new ItemStack(Items.PITCHER_POD, 2 + random.nextInt(3)));
                chest.setItem(1, new ItemStack(Items.TORCHFLOWER_SEEDS, 2 + random.nextInt(3)));
                chest.setItem(2, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 3 + random.nextInt(4)));
                chest.setItem(3, new ItemStack(Items.DIAMOND, 2 + random.nextInt(3)));
                chest.setItem(4, new ItemStack(SandStormBlocks.CRYO_SPRAYER.asItem(), 1));
                chest.setItem(5, new ItemStack(SandStormBlocks.CRYOSTASIS_POD.asItem(), 1));
                chest.setItem(6, new ItemStack(Items.ECHO_SHARD, 1 + random.nextInt(2)));
            }
        }
    }

    public static class CryoStasisBay extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_stasis_bay";
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
            if (direction == DungeonDirection.NORTH || direction == DungeonDirection.SOUTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildCryoShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 1, 1, 2, size, SandStormBlocks.CRYOSTASIS_POD.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.EAST)));
            setLocalBlock(level, origin, rotation, 1, 1, 4, size, SandStormBlocks.CRYOSTASIS_POD.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.EAST)));

            setLocalBlock(level, origin, rotation, 5, 1, 2, size, SandStormBlocks.CRYOSTASIS_POD.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.WEST)));
            setLocalBlock(level, origin, rotation, 5, 1, 4, size, SandStormBlocks.CRYOSTASIS_POD.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotateDirection(Direction.WEST)));

            setLocalBlock(level, origin, rotation, 1, 2, 1, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 3, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 5, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());

            setLocalBlock(level, origin, rotation, 5, 2, 1, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 2, 3, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 2, 5, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());

            setLocalBlock(level, origin, rotation, 2, 1, 1, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 1, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 1, 5, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 5, size, Blocks.IRON_BARS.defaultBlockState());
        }
    }

    public static class NitrogenCompressorChamber extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_nitrogen_compressor";
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
            if (direction == DungeonDirection.NORTH) {
                return DungeonSocket.WALL;
            }
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildCryoShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);

            setLocalBlock(level, origin, rotation, 3, 1, 2, size, SandStormBlocks.LEAD_SHIELDING_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 2, size, SandStormBlocks.CRYO_SPRAYER.defaultBlockState().setValue(BlockStateProperties.FACING, rotation.rotateDirection(Direction.SOUTH)));

            setLocalBlock(level, origin, rotation, 3, 1, 1, size, SandStormBlocks.LEAD_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 1, size, SandStormBlocks.LEAD_SHIELDING_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 1, 1, size, SandStormBlocks.LEAD_SHIELDING_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 1, size, SandStormBlocks.LEAD_SHIELDING_BLOCK.defaultBlockState());

            setLocalBlock(level, origin, rotation, 2, 1, 2, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 1, 2, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 2, 2, 2, size, Blocks.IRON_BARS.defaultBlockState());
            setLocalBlock(level, origin, rotation, 4, 2, 2, size, Blocks.IRON_BARS.defaultBlockState());

            setLocalBlock(level, origin, rotation, 3, 1, 3, size, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 1, 4, size, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.LEAD_SHIELDING_BLOCK.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.LEAD_SHIELDING_BLOCK.defaultBlockState());
        }
    }

    public static class PermafrostChasmCrossing extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_permafrost_chasm";
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
            if (direction == DungeonDirection.NORTH || direction == DungeonDirection.SOUTH) {
                return DungeonSocket.CORRIDOR;
            }
            return DungeonSocket.WALL;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildCryoShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lx = 1; lx <= 5; lx++) {
                for (int lz = 1; lz <= 5; lz++) {
                    setLocalBlock(level, origin, rotation, lx, 0, lz, size, Blocks.AIR.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, -1, lz, size, Blocks.AIR.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, -2, lz, size, SandStormBlocks.SUBGLACIAL_ICE_SPIKE.defaultBlockState());
                    setLocalBlock(level, origin, rotation, lx, -3, lz, size, Blocks.BLUE_ICE.defaultBlockState());
                }
            }

            for (int lz = 1; lz <= 5; lz++) {
                setLocalBlock(level, origin, rotation, 3, 1, lz, size, SandStormBlocks.BRITTLE_PERMAFROST.defaultBlockState());
            }

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, Blocks.PACKED_ICE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, Blocks.PACKED_ICE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 1, 5, size, Blocks.PACKED_ICE.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 5, size, Blocks.PACKED_ICE.defaultBlockState());
        }
    }

    public static class CryoCorridorStraight extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_corridor_straight";
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
            buildCryoShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            for (int lz = 1; lz <= 5; lz++) {
                setLocalBlock(level, origin, rotation, 1, 3, lz, size, Blocks.IRON_BARS.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, 3, lz, size, Blocks.IRON_BARS.defaultBlockState());
            }
        }
    }

    public static class CryoCorridorCorner extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_corridor_corner";
        }

        @Override
        public int weight() {
            return 3;
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
            buildCryoShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 1, 1, 5, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 1, 2, 5, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
        }
    }

    public static class CryoCorridorTJunction extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_corridor_t_junction";
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
            if (direction == DungeonDirection.SOUTH) {
                return DungeonSocket.WALL;
            }
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildCryoShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.WALL, DungeonSocket.CORRIDOR);

            setLocalBlock(level, origin, rotation, 3, 1, 5, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 3, 2, 5, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
        }
    }

    public static class CryoCorridorCrossroad extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_corridor_crossroad";
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
            return DungeonSocket.CORRIDOR;
        }

        @Override
        public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
            buildCryoShell(level, origin, rotation, size, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR, DungeonSocket.CORRIDOR);

            for (int ly = 1; ly <= 4; ly++) {
                setLocalBlock(level, origin, rotation, 1, ly, 1, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, ly, 1, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
                setLocalBlock(level, origin, rotation, 1, ly, 5, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
                setLocalBlock(level, origin, rotation, 5, ly, 5, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            }
        }
    }

    public static class CryoDeadEnd extends BaseCryoVaultComponent {
        @Override
        public String id() {
            return "cryo_vault_dead_end";
        }

        @Override
        public int weight() {
            return 1;
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
            buildCryoShell(level, origin, rotation, size, DungeonSocket.WALL, DungeonSocket.WALL, DungeonSocket.CORRIDOR, DungeonSocket.WALL);

            setLocalBlock(level, origin, rotation, 3, 1, 2, size, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, rotation.rotateDirection(Direction.SOUTH)));
            populateDeadEndChest(level, origin, rotation, 3, 1, 2, size, random);

            setLocalBlock(level, origin, rotation, 3, 2, 1, size, SandStormBlocks.CRYO_SPRAYER.defaultBlockState().setValue(BlockStateProperties.FACING, rotation.rotateDirection(Direction.SOUTH)));
            setLocalBlock(level, origin, rotation, 3, 1, 3, size, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.defaultBlockState());

            setLocalBlock(level, origin, rotation, 1, 1, 1, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
            setLocalBlock(level, origin, rotation, 5, 1, 1, size, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState());
        }

        private void populateDeadEndChest(ServerLevel level, BlockPos origin, DungeonRotation rotation, int lx, int ly, int lz, int size, RandomSource random) {
            BlockPos pos = origin.offset(rotation.rotateOffset(lx, ly, lz, size));
            if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
                chest.setItem(0, new ItemStack(SandStormBlocks.BRITTLE_PERMAFROST.asItem(), 4 + random.nextInt(4)));
                chest.setItem(1, new ItemStack(Items.SNOWBALL, 8 + random.nextInt(8)));
                chest.setItem(2, new ItemStack(Items.IRON_INGOT, 2 + random.nextInt(3)));
            }
        }
    }

    public static class SolidRock extends BaseDungeonComponent {
        @Override
        public String id() {
            return "cryo_vault_solid_rock";
        }

        @Override
        public int weight() {
            return 1;
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
                        BlockState state = (lx + lz + ly) % 2 == 0
                                ? Blocks.PACKED_ICE.defaultBlockState()
                                : SandStormBlocks.CRYO_INSULATION.defaultBlockState();
                        setLocalBlock(level, origin, rotation, lx, ly, lz, size, state);
                    }
                }
            }
        }
    }
}
