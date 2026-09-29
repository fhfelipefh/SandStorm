package com.fhfelipefh.sandstorm.content.world.structure;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.entity.ScrapSentinelEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class FuelSiloGenerator {

    public static boolean generate(ServerLevel level, BlockPos origin, int surfaceY) {
        RandomSource random = level.getRandom();
        int siloY = Math.clamp(origin.getY(), 30, 44);
        BlockPos center = new BlockPos(origin.getX(), siloY, origin.getZ());

        BlockPos surfaceCenter = new BlockPos(origin.getX(), surfaceY, origin.getZ());
        level.setBlock(surfaceCenter, Blocks.IRON_BLOCK.defaultBlockState(), 2);
        level.setBlock(surfaceCenter.above(), SandStormBlocks.FLUID_PIPE.defaultBlockState(), 2);
        level.setBlock(surfaceCenter.above(2), Blocks.IRON_BARS.defaultBlockState(), 2);

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                level.setBlock(center.offset(dx, 0, dz), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);

                boolean isEdge = Math.abs(dx) == 4 || Math.abs(dz) == 4;
                for (int dy = 1; dy <= 5; dy++) {
                    BlockPos wallPos = center.offset(dx, dy, dz);
                    if (isEdge) {
                        if (dy == 3 && (Math.abs(dx) == 2 || Math.abs(dz) == 2)) {
                            level.setBlock(wallPos, Blocks.IRON_BARS.defaultBlockState(), 2);
                        } else {
                            level.setBlock(wallPos, Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
                        }
                    } else {
                        level.setBlock(wallPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }

                level.setBlock(center.offset(dx, 6, dz), Blocks.SMOOTH_BASALT.defaultBlockState(), 2);
            }
        }

        level.setBlock(center.offset(0, 1, 0), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 3);
        level.setBlock(center.offset(0, 2, 0), Blocks.RAW_COPPER_BLOCK.defaultBlockState(), 3);
        level.setBlock(center.offset(0, 3, 0), Blocks.IRON_BLOCK.defaultBlockState(), 3);

        level.setBlock(center.offset(0, 5, 0), SandStormBlocks.FLUID_PIPE.defaultBlockState(), 3);
        level.setBlock(center.offset(0, 5, 1), SandStormBlocks.FLUID_PIPE.defaultBlockState(), 3);
        level.setBlock(center.offset(0, 5, 2), SandStormBlocks.FLUID_PIPE.defaultBlockState(), 3);
        level.setBlock(center.offset(0, 4, 2), SandStormBlocks.FLUID_PIPE.defaultBlockState(), 3);
        level.setBlock(center.offset(0, 3, 2), SandStormBlocks.FLUID_PIPE.defaultBlockState(), 3);
        level.setBlock(center.offset(0, 2, 2), SandStormBlocks.FLUID_PIPE.defaultBlockState(), 3);

        BlockPos refineryPos = center.offset(0, 1, 2);
        level.setBlock(refineryPos, SandStormBlocks.CHEMICAL_REFINERY.defaultBlockState(), 3);

        BlockPos fuelChestPos = center.offset(-2, 1, -2);
        level.setBlock(fuelChestPos, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(fuelChestPos) instanceof ChestBlockEntity fuelChest) {
            fuelChest.setItem(0, new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE, 2 + random.nextInt(4)));
            fuelChest.setItem(1, new ItemStack(SandStormItems.EMPTY_CARTRIDGE, 4 + random.nextInt(6)));
            fuelChest.setItem(2, new ItemStack(SandStormItems.FILTER_CARTRIDGE, 1));
            fuelChest.setItem(3, new ItemStack(SandStormItems.SCRAP_METAL, 3 + random.nextInt(4)));
            fuelChest.setChanged();
        }

        BlockPos reagentChestPos = center.offset(2, 1, -2);
        level.setBlock(reagentChestPos, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(reagentChestPos) instanceof ChestBlockEntity reagentChest) {
            reagentChest.setItem(0, new ItemStack(SandStormItems.MINERAL_SALT, 8 + random.nextInt(8)));
            reagentChest.setItem(1, new ItemStack(Items.GUNPOWDER, 4 + random.nextInt(5)));
            reagentChest.setItem(2, new ItemStack(Items.REDSTONE, 6 + random.nextInt(6)));
            reagentChest.setItem(3, new ItemStack(SandStormItems.TECH_BUCKET, 1));
            reagentChest.setItem(4, new ItemStack(Items.COAL, 6 + random.nextInt(6)));
            reagentChest.setChanged();
        }

        ScrapSentinelEntity sentinel = new ScrapSentinelEntity(SandStormEntities.SCRAP_SENTINEL, level);
        sentinel.setPos(center.getX() + 0.5, center.getY() + 1.0, center.getZ() + 0.5);
        sentinel.setDormant(true);
        sentinel.setPersistenceRequired();
        level.addFreshEntity(sentinel);

        return true;
    }
}
