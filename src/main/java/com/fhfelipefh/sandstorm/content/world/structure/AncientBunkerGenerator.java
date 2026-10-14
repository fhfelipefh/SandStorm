package com.fhfelipefh.sandstorm.content.world.structure;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.entity.SolidStateAccumulatorBlockEntity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AncientBunkerGenerator {

    public static int generate(ServerLevel level, BlockPos origin) {
        int placedCount = 0;
        RandomSource random = level.getRandom();
        int baseY = origin.getY();
        BlockPos center = new BlockPos(origin.getX(), baseY, origin.getZ());
        int minFoundationY = Math.max(level.getMinY(), baseY - 4);

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                BlockPos floorPos = center.offset(dx, 0, dz);
                boolean isOuterBorder = Math.abs(dx) == 5 || Math.abs(dz) == 5;
                BlockState floorState = isOuterBorder
                        ? Blocks.CHISELED_DEEPSLATE.defaultBlockState()
                        : ((dx + dz) % 2 == 0 ? Blocks.POLISHED_DEEPSLATE.defaultBlockState() : Blocks.DEEPSLATE_BRICKS.defaultBlockState());

                if (Math.abs(dx) <= 2 && Math.abs(dz) <= 2 && (dx == 0 || dz == 0)) {
                    floorState = SandStormBlocks.PIEZO_QUARTZ_BLOCK.defaultBlockState();
                }

                level.setBlock(floorPos, floorState, 2);

                for (int fy = baseY - 1; fy >= minFoundationY; fy--) {
                    BlockPos foundationPos = new BlockPos(floorPos.getX(), fy, floorPos.getZ());
                    if (level.getBlockState(foundationPos).isAir() || !level.getBlockState(foundationPos).isSolid()) {
                        level.setBlock(foundationPos, Blocks.DEEPSLATE.defaultBlockState(), 2);
                    }
                }

                for (int dy = 1; dy <= 4; dy++) {
                    BlockPos innerPos = center.offset(dx, dy, dz);
                    if (isOuterBorder) {
                        if (dz == -5 && dx == 0) {
                            if (dy <= 2) {
                                level.setBlock(innerPos, Blocks.AIR.defaultBlockState(), 2);
                            } else if (dy == 3) {
                                level.setBlock(innerPos, Blocks.IRON_BARS.defaultBlockState(), 2);
                            } else {
                                level.setBlock(innerPos, Blocks.CHISELED_DEEPSLATE.defaultBlockState(), 2);
                            }
                        } else if (dy == 2 && (Math.abs(dx) == 2 || Math.abs(dz) == 2)) {
                            level.setBlock(innerPos, SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                        } else {
                            BlockState wallState = (Math.abs(dx * 3 + dz * 7 + dy) % 4 == 0)
                                    ? Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState()
                                    : Blocks.DEEPSLATE_BRICKS.defaultBlockState();
                            level.setBlock(innerPos, wallState, 2);
                        }
                    } else {
                        if (!level.getBlockState(innerPos).isAir()) {
                            level.setBlock(innerPos, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }

                BlockPos ceilingPos = center.offset(dx, 5, dz);
                level.setBlock(ceilingPos, Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
            }
        }

        BlockPos skylight = center.offset(0, 5, 0);
        level.setBlock(skylight, SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        level.setBlock(center.offset(0, 7, 0), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        level.setBlock(center.offset(0, 6, 0), Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);

        BlockPos pedestal = center.offset(0, 1, 0);
        level.setBlock(pedestal, Blocks.CHISELED_DEEPSLATE.defaultBlockState(), 3);
        BlockPos dataCorePos = center.offset(0, 2, 0);
        level.setBlock(dataCorePos, SandStormBlocks.ANCIENT_DATA_CORE.defaultBlockState(), 3);
        level.setBlock(center.offset(0, 3, 0), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        level.setBlock(center.offset(1, 2, 0), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        level.setBlock(center.offset(-1, 2, 0), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        level.setBlock(center.offset(0, 2, 1), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        level.setBlock(center.offset(0, 2, -1), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);

        level.setBlock(center.offset(-4, 1, -4), SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);
        level.setBlock(center.offset(-4, 2, -4), SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);
        level.setBlock(center.offset(4, 1, -4), SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);
        level.setBlock(center.offset(4, 1, 4), SandStormBlocks.PIEZO_QUARTZ_CLUSTER.defaultBlockState(), 3);

        BlockPos accumulatorPos = center.offset(-4, 1, 1);
        level.setBlock(accumulatorPos, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
        if (level.getBlockEntity(accumulatorPos) instanceof SolidStateAccumulatorBlockEntity accumulator) {
            accumulator.setStoredEnergy(10000);
        }

        level.setBlock(center.offset(-4, 1, 2), SandStormBlocks.GRID_MONITOR_CONSOLE.defaultBlockState(), 3);
        level.setBlock(center.offset(-4, 2, 1), SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState(), 3);
        level.setBlock(center.offset(-4, 3, 1), SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState(), 3);
        level.setBlock(center.offset(-4, 4, 1), SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState(), 3);

        level.setBlock(center.offset(4, 1, 1), SandStormBlocks.NANITE_FABRICATOR.defaultBlockState(), 3);
        level.setBlock(center.offset(4, 1, 2), SandStormBlocks.PRINTER_3D.defaultBlockState(), 3);

        level.setBlock(center.offset(-2, 1, 4), SandStormBlocks.TITANIUM_SPIKE_WALL.defaultBlockState(), 2);
        level.setBlock(center.offset(2, 1, 4), SandStormBlocks.TITANIUM_SPIKE_WALL.defaultBlockState(), 2);

        BlockPos chest1Pos = center.offset(-3, 1, -3);
        Direction facing1 = determineOpenFacing(level, chest1Pos, Direction.SOUTH);
        level.setBlock(chest1Pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing1), 3);
        if (level.getBlockEntity(chest1Pos) instanceof ChestBlockEntity chest1) {
            populateChest(chest1, random);
        }
        placedCount += 22;

        BlockPos chest2Pos = center.offset(3, 1, 3);
        Direction facing2 = determineOpenFacing(level, chest2Pos, Direction.NORTH);
        level.setBlock(chest2Pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing2), 3);
        if (level.getBlockEntity(chest2Pos) instanceof ChestBlockEntity chest2) {
            populateChest(chest2, random);
        }
        placedCount++;

        return placedCount;
    }

    public static Direction determineOpenFacing(ServerLevel level, BlockPos pos, Direction preferredFacing) {
        if (!level.getBlockState(pos.relative(preferredFacing)).isSolid()) {
            return preferredFacing;
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (!level.getBlockState(pos.relative(dir)).isSolid()) {
                return dir;
            }
        }
        return preferredFacing;
    }

    public static void populateChest(Container chest, RandomSource random) {
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 27; i++) {
            slots.add(i);
        }
        Collections.shuffle(slots);
        int slotIdx = 0;

        if (random.nextFloat() < 0.75f) {
            chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.SCRAP_METAL, 3 + random.nextInt(4)));
        }
        if (random.nextFloat() < 0.60f) {
            chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.RAW_SILICON, 3 + random.nextInt(5)));
        }
        if (random.nextFloat() < 0.50f) {
            chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.SILICON_WAFER, 2 + random.nextInt(3)));
        }
        if (random.nextFloat() < 0.45f) {
            chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 1 + random.nextInt(3)));
        }

        int archetype = random.nextInt(5);
        switch (archetype) {
            case 0 -> {
                if (random.nextBoolean()) {
                    chest.setItem(slots.get(slotIdx++), new ItemStack(Items.REDSTONE_BLOCK, 1));
                } else {
                    chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.CIRCUIT_BOARD, 1 + random.nextInt(2)));
                }
            }
            case 1 -> {
                if (random.nextBoolean()) {
                    chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.NANO_ACTUATOR, 1));
                } else {
                    chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.STRUCTURAL_PLATE, 2 + random.nextInt(3)));
                }
            }
            case 2 -> {
                if (random.nextBoolean()) {
                    chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.TECH_DISC, 1 + random.nextInt(2)));
                } else {
                    chest.setItem(slots.get(slotIdx++), new ItemStack(Blocks.TINTED_GLASS, 2));
                }
            }
            case 3 -> {
                if (random.nextBoolean()) {
                    chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.PIEZO_QUARTZ_SHARD, 2 + random.nextInt(3)));
                } else {
                    chest.setItem(slots.get(slotIdx++), new ItemStack(Items.AMETHYST_SHARD, 2 + random.nextInt(2)));
                }
            }
            default -> {
                if (random.nextBoolean()) {
                    chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE, 2 + random.nextInt(3)));
                } else {
                    chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.FILTER_CARTRIDGE, 1));
                }
            }
        }

        chest.setChanged();
    }
}
