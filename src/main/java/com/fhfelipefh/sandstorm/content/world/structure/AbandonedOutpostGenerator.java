package com.fhfelipefh.sandstorm.content.world.structure;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbandonedOutpostGenerator {

    public static boolean generate(ServerLevel level, BlockPos origin) {
        RandomSource random = level.getRandom();
        int surfaceY = origin.getY();
        int buryDepth = 3 + (int) (Math.abs(origin.getX() ^ origin.getZ()) % 3);
        int roofY = surfaceY - buryDepth;
        int baseY = roofY - 5;

        BlockPos center = new BlockPos(origin.getX(), baseY, origin.getZ());

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                BlockPos floorPos = center.offset(dx, 0, dz);
                level.setBlock(floorPos, Blocks.POLISHED_ANDESITE.defaultBlockState(), 2);
                BlockPos underFloor = floorPos.below();
                while (underFloor.getY() > level.getMinY() && (level.getBlockState(underFloor).isAir() || !level.getBlockState(underFloor).isSolid())) {
                    level.setBlock(underFloor, Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                    underFloor = underFloor.below();
                }

                boolean isEdge = Math.abs(dx) == 5 || Math.abs(dz) == 5;
                for (int dy = 1; dy <= 4; dy++) {
                    BlockPos wallPos = center.offset(dx, dy, dz);
                    if (isEdge) {
                        if (dy == 2 && (Math.abs(dx) == 2 || Math.abs(dz) == 2)) {
                            level.setBlock(wallPos, Blocks.IRON_BARS.defaultBlockState(), 2);
                        } else if (dy == 1 && dx == 0 && dz == -5) {
                            level.setBlock(wallPos, Blocks.AIR.defaultBlockState(), 2);
                        } else if (dy == 2 && dx == 0 && dz == -5) {
                            level.setBlock(wallPos, Blocks.AIR.defaultBlockState(), 2);
                        } else {
                            BlockState mat = ((dx + dz + dy) % 3 == 0)
                                    ? Blocks.CUT_SANDSTONE.defaultBlockState()
                                    : Blocks.SMOOTH_SANDSTONE.defaultBlockState();
                            level.setBlock(wallPos, mat, 2);
                        }
                    } else {
                        level.setBlock(wallPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }

                BlockPos roofPos = center.offset(dx, 5, dz);
                if (Math.abs(dx) <= 2 && Math.abs(dz) <= 2 && random.nextFloat() < 0.35f) {
                    level.setBlock(roofPos, Blocks.SAND.defaultBlockState(), 2);
                    level.setBlock(center.offset(dx, 1, dz), Blocks.SAND.defaultBlockState(), 2);
                    if (random.nextBoolean()) {
                        level.setBlock(center.offset(dx, 2, dz), Blocks.SAND.defaultBlockState(), 2);
                    }
                } else {
                    level.setBlock(roofPos, Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                }

                for (int y = roofY + 1; y <= surfaceY; y++) {
                    BlockPos sandCapPos = new BlockPos(center.getX() + dx, y, center.getZ() + dz);
                    if (level.getBlockState(sandCapPos).isAir()) {
                        level.setBlock(sandCapPos, Blocks.SAND.defaultBlockState(), 2);
                    }
                }
            }
        }

        level.setBlock(center.offset(-4, 1, -4), Blocks.SAND.defaultBlockState(), 2);
        level.setBlock(center.offset(4, 1, 4), Blocks.SAND.defaultBlockState(), 2);
        level.setBlock(center.offset(-4, 1, 4), Blocks.SAND.defaultBlockState(), 2);

        BlockPos dataTerminalPedestal = center.offset(0, 1, 1);
        level.setBlock(dataTerminalPedestal, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        BlockPos dataTerminalPos = center.offset(0, 2, 1);
        level.setBlock(dataTerminalPos, SandStormBlocks.ANCIENT_DATA_CORE.defaultBlockState(), 3);

        level.setBlock(center.offset(-2, 0, -2), SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);
        level.setBlock(center.offset(2, 0, -2), SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);
        level.setBlock(center.offset(-3, 0, 2), SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);

        BlockPos workbenchPos = center.offset(-3, 1, -2);
        level.setBlock(workbenchPos, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);
        level.setBlock(center.offset(-3, 1, -1), SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);

        BlockPos chestPos = center.offset(3, 1, 2);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            List<Integer> slots = new ArrayList<>();
            for (int i = 0; i < 27; i++) {
                slots.add(i);
            }
            Collections.shuffle(slots);
            int slotIdx = 0;

            if (random.nextFloat() < 0.65f) {
                chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.SCRAP_METAL, 2 + random.nextInt(4)));
            }
            if (random.nextFloat() < 0.50f) {
                chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.RAW_SILICON, 3 + random.nextInt(5)));
            }
            if (random.nextFloat() < 0.45f) {
                chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.CIRCUIT_BOARD, 1 + random.nextInt(2)));
            }
            if (random.nextFloat() < 0.35f) {
                chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.SILICON_WAFER, 1 + random.nextInt(2)));
            }
            if (random.nextFloat() < 0.40f) {
                chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.TECH_DISC, 1));
            }
            if (random.nextFloat() < 0.25f) {
                chest.setItem(slots.get(slotIdx++), new ItemStack(SandStormItems.NANO_ACTUATOR, 1));
            }
            if (random.nextFloat() < 0.50f) {
                chest.setItem(slots.get(slotIdx++), new ItemStack(Items.REDSTONE, 4 + random.nextInt(8)));
            }
            if (random.nextFloat() < 0.30f) {
                ItemStack upgrade = switch (random.nextInt(3)) {
                    case 0 -> new ItemStack(SandStormItems.SUIT_UPGRADE_BATTERY);
                    case 1 -> new ItemStack(SandStormItems.SUIT_UPGRADE_VISOR);
                    default -> new ItemStack(SandStormItems.SUIT_UPGRADE_THERMAL);
                };
                chest.setItem(slots.get(slotIdx++), upgrade);
            }
            chest.setChanged();
        }

        return true;
    }
}
