package com.fhfelipefh.sandstorm.content.world.structure;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ColossalCastleGenerator {

    public static int generate(ServerLevel level, BlockPos origin) {
        int ox = origin.getX();
        int oy = origin.getY();
        int oz = origin.getZ();
        int placedCount = 0;

        placedCount += generateFoundationAndClearing(level, ox, oy, oz);
        placedCount += generateOuterCurtainWalls(level, ox, oy, oz);
        placedCount += generateCornerBastions(level, ox, oy, oz);
        placedCount += generateSouthGatehouse(level, ox, oy, oz);
        placedCount += generateOuterCourtyard(level, ox, oy, oz);
        placedCount += generateColossalKeep(level, ox, oy, oz);
        placedCount += generateCelestialSpire(level, ox, oy, oz);
        placedCount += generateFoliageAndDetails(level, ox, oy, oz);

        level.playSound(null, origin, SandStormSoundEvents.MEGASTRUCTURE_COMPLETE, SoundSource.BLOCKS, 2.5f, 0.8f);
        return placedCount;
    }

    private static int generateFoundationAndClearing(ServerLevel level, int ox, int oy, int oz) {
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        for (int dx = -40; dx <= 40; dx++) {
            for (int dz = -40; dz <= 40; dz++) {
                int wx = ox + dx;
                int wz = oz + dz;
                double distCenter = Math.sqrt(dx * dx + dz * dz);

                int maxClearY = distCenter <= 24 ? 66 : 28;
                for (int dy = 1; dy <= maxClearY; dy++) {
                    mpos.set(wx, oy + dy, wz);
                    if (!level.getBlockState(mpos).isAir()) {
                        level.setBlock(mpos, Blocks.AIR.defaultBlockState(), 2);
                        count++;
                    }
                }

                mpos.set(wx, oy, wz);
                BlockState floorState = getFloorBlock(wx, wz);
                level.setBlock(mpos, floorState, 2);
                count++;

                for (int dy = -1; dy >= -10; dy--) {
                    mpos.set(wx, oy + dy, wz);
                    if (level.getBlockState(mpos).isAir() || !level.getBlockState(mpos).isSolid()) {
                        level.setBlock(mpos, getFoundationBlock(wx, oy + dy, wz), 2);
                        count++;
                    } else {
                        break;
                    }
                }
            }
        }
        return count;
    }

    private static int generateOuterCurtainWalls(ServerLevel level, int ox, int oy, int oz) {
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        count += buildWallSegment(level, ox - 30, ox + 30, oz - 36, oz - 34, oy, true);
        count += buildWallSegment(level, ox - 30, ox - 10, oz + 34, oz + 36, oy, true);
        count += buildWallSegment(level, ox + 10, ox + 30, oz + 34, oz + 36, oy, true);
        count += buildWallSegment(level, ox - 36, ox - 34, oz - 30, oz + 30, oy, false);
        count += buildWallSegment(level, ox + 34, ox + 36, oz - 30, oz + 30, oy, false);

        int[][] intermediateTowers = {{ox - 35, oz}, {ox + 35, oz}, {ox, oz - 35}};
        for (int[] tw : intermediateTowers) {
            count += buildCylindricalTower(level, tw[0], tw[1], oy, 20, 4.5, 3.2, 8);
        }

        return count;
    }

    private static int buildWallSegment(ServerLevel level, int minX, int maxX, int minZ, int maxZ, int oy, boolean alongX) {
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = 1; y <= 11; y++) {
                    mpos.set(x, oy + y, z);
                    level.setBlock(mpos, getWallBlock(x, oy + y, z), 2);
                    count++;
                }

                mpos.set(x, oy + 11, z);
                level.setBlock(mpos, Blocks.STONE_BRICKS.defaultBlockState(), 2);
                count++;

                boolean isOuter = alongX
                        ? (minZ < 0 ? z == minZ : z == maxZ)
                        : (minX < 0 ? x == minX : x == maxX);

                boolean isInner = alongX
                        ? (minZ < 0 ? z == maxZ : z == minZ)
                        : (minX < 0 ? x == maxX : x == minX);

                if (isOuter) {
                    mpos.set(x, oy + 12, z);
                    level.setBlock(mpos, Blocks.STONE_BRICKS.defaultBlockState(), 2);
                    count++;

                    int coord = alongX ? x : z;
                    if (coord % 2 == 0) {
                        mpos.set(x, oy + 13, z);
                        level.setBlock(mpos, Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
                        count++;
                    }
                } else if (isInner) {
                    mpos.set(x, oy + 12, z);
                    level.setBlock(mpos, Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
                    count++;
                }

                int intervalCoord = alongX ? x : z;
                if (!isOuter && !isInner && intervalCoord % 8 == 0) {
                    mpos.set(x, oy + 12, z);
                    level.setBlock(mpos, Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
                    mpos.set(x, oy + 13, z);
                    level.setBlock(mpos, Blocks.LANTERN.defaultBlockState(), 2);
                    count += 2;
                }
            }
        }
        return count;
    }

    private static int generateCornerBastions(ServerLevel level, int ox, int oy, int oz) {
        int count = 0;
        int[][] corners = {
                {ox - 36, oz - 36},
                {ox + 36, oz - 36},
                {ox - 36, oz + 36},
                {ox + 36, oz + 36}
        };

        for (int[] corner : corners) {
            count += buildCylindricalTower(level, corner[0], corner[1], oy, 26, 6.2, 4.5, 12);
        }
        return count;
    }

    private static int buildCylindricalTower(ServerLevel level, int cx, int cz, int oy, int height, double rOuter, double rInner, int roofHeight) {
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        int intRadius = (int) Math.ceil(rOuter) + 1;

        for (int y = 1; y <= height; y++) {
            boolean isFloor = (y % 7 == 1) || (y == height);
            for (int dx = -intRadius; dx <= intRadius; dx++) {
                for (int dz = -intRadius; dz <= intRadius; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    int wx = cx + dx;
                    int wy = oy + y;
                    int wz = cz + dz;
                    mpos.set(wx, wy, wz);

                    if (dist <= rOuter && dist >= rInner) {
                        boolean isWindow = (y % 7 == 4) && (Math.abs(dx) <= 1 || Math.abs(dz) <= 1) && dist >= rOuter - 0.7;
                        if (isWindow) {
                            level.setBlock(mpos, Blocks.IRON_BARS.defaultBlockState(), 2);
                        } else {
                            level.setBlock(mpos, getWallBlock(wx, wy, wz), 2);
                        }
                        count++;
                    } else if (dist < rInner) {
                        if (isFloor) {
                            level.setBlock(mpos, Blocks.SPRUCE_PLANKS.defaultBlockState(), 2);
                            count++;
                        } else {
                            if (dx == 0 && dz == 0 && y > 1) {
                                level.setBlock(mpos, Blocks.LADDER.defaultBlockState(), 2);
                                count++;
                            } else {
                                level.setBlock(mpos, Blocks.AIR.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
        }

        int balconyY = oy + height;
        int balconyRadius = (int) Math.ceil(rOuter + 1.2);
        for (int dx = -balconyRadius; dx <= balconyRadius; dx++) {
            for (int dz = -balconyRadius; dz <= balconyRadius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= rOuter + 1.2 && dist >= rOuter - 0.5) {
                    mpos.set(cx + dx, balconyY, cz + dz);
                    level.setBlock(mpos, Blocks.POLISHED_ANDESITE.defaultBlockState(), 2);
                    mpos.set(cx + dx, balconyY + 1, cz + dz);
                    level.setBlock(mpos, Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
                    count += 2;
                }
            }
        }

        count += buildConicalRoof(level, cx, cz, balconyY + 1, roofHeight, rOuter + 0.5);
        return count;
    }

    private static int buildConicalRoof(ServerLevel level, int cx, int cz, int startY, int height, double baseRadius) {
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        for (int h = 0; h < height; h++) {
            double currentRadius = baseRadius * (1.0 - (double) h / height);
            int intR = (int) Math.ceil(currentRadius);
            int currentY = startY + h;

            for (int dx = -intR; dx <= intR; dx++) {
                for (int dz = -intR; dz <= intR; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist <= currentRadius) {
                        mpos.set(cx + dx, currentY, cz + dz);
                        if (dist >= currentRadius - 1.2 || h >= height - 2) {
                            BlockState roofState = ((h + dx + dz) % 3 == 0)
                                    ? Blocks.DARK_OAK_PLANKS.defaultBlockState()
                                    : Blocks.SPRUCE_PLANKS.defaultBlockState();
                            level.setBlock(mpos, roofState, 2);
                            count++;
                        } else {
                            level.setBlock(mpos, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        int spireY = startY + height;
        mpos.set(cx, spireY, cz);
        level.setBlock(mpos, Blocks.POLISHED_DEEPSLATE_WALL.defaultBlockState(), 2);
        mpos.set(cx, spireY + 1, cz);
        level.setBlock(mpos, Blocks.IRON_BARS.defaultBlockState(), 2);
        count += 2;

        return count;
    }

    private static int generateSouthGatehouse(ServerLevel level, int ox, int oy, int oz) {
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        count += buildCylindricalTower(level, ox - 8, oz + 36, oy, 22, 5.0, 3.5, 10);
        count += buildCylindricalTower(level, ox + 8, oz + 36, oy, 22, 5.0, 3.5, 10);

        for (int x = ox - 4; x <= ox + 4; x++) {
            for (int z = oz + 33; z <= oz + 39; z++) {
                for (int y = 1; y <= 6; y++) {
                    mpos.set(x, oy + y, z);
                    level.setBlock(mpos, Blocks.AIR.defaultBlockState(), 2);
                }

                for (int y = 7; y <= 16; y++) {
                    mpos.set(x, oy + y, z);
                    boolean isOuter = (z == oz + 33 || z == oz + 39 || x == ox - 4 || x == ox + 4);
                    if (isOuter) {
                        level.setBlock(mpos, getWallBlock(x, oy + y, z), 2);
                    } else if (y == 7 || y == 16) {
                        level.setBlock(mpos, Blocks.SPRUCE_PLANKS.defaultBlockState(), 2);
                    } else {
                        level.setBlock(mpos, Blocks.AIR.defaultBlockState(), 2);
                    }
                    count++;
                }

                mpos.set(x, oy + 17, z);
                if (z == oz + 33 || z == oz + 39 || x == ox - 4 || x == ox + 4) {
                    level.setBlock(mpos, Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
                    count++;
                }
            }
        }

        for (int x = ox - 3; x <= ox + 3; x++) {
            for (int y = 4; y <= 6; y++) {
                mpos.set(x, oy + y, oz + 36);
                level.setBlock(mpos, Blocks.IRON_BARS.defaultBlockState(), 2);
                count++;
            }
        }

        for (int dz = 40; dz <= 46; dz++) {
            int stepDown = dz - 39;
            int stepY = Math.max(oy - stepDown, oy - 4);
            for (int dx = -5; dx <= 5; dx++) {
                mpos.set(ox + dx, stepY, oz + dz);
                level.setBlock(mpos, Blocks.STONE_BRICKS.defaultBlockState(), 2);
                count++;
                if (Math.abs(dx) == 5) {
                    mpos.set(ox + dx, stepY + 1, oz + dz);
                    level.setBlock(mpos, Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
                    count++;
                }
            }
        }

        BlockPos chestPos = new BlockPos(ox - 2, oy + 8, oz + 35);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        populateChest(level, chestPos, 0);
        count++;

        return count;
    }

    private static int generateOuterCourtyard(ServerLevel level, int ox, int oy, int oz) {
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        int wellX = ox;
        int wellZ = oz + 24;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                mpos.set(wellX + dx, oy, wellZ + dz);
                if (dx == 0 && dz == 0) {
                    level.setBlock(mpos, Blocks.WATER.defaultBlockState(), 2);
                } else {
                    level.setBlock(mpos, Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
                }
                count++;
            }
        }
        int[][] wellPillars = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] wp : wellPillars) {
            mpos.set(wellX + wp[0], oy + 1, wellZ + wp[1]);
            level.setBlock(mpos, Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
            mpos.set(wellX + wp[0], oy + 2, wellZ + wp[1]);
            level.setBlock(mpos, Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
            count += 2;
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                mpos.set(wellX + dx, oy + 3, wellZ + dz);
                level.setBlock(mpos, Blocks.SPRUCE_SLAB.defaultBlockState(), 2);
                count++;
            }
        }
        mpos.set(wellX, oy + 2, wellZ);
        level.setBlock(mpos, Blocks.LANTERN.defaultBlockState(), 2);
        count++;

        int forgeX = ox + 22;
        int forgeZ = oz + 20;
        mpos.set(forgeX, oy + 1, forgeZ);
        level.setBlock(mpos, Blocks.BLAST_FURNACE.defaultBlockState(), 3);
        mpos.set(forgeX + 1, oy + 1, forgeZ);
        level.setBlock(mpos, Blocks.ANVIL.defaultBlockState(), 2);
        mpos.set(forgeX - 1, oy + 1, forgeZ);
        level.setBlock(mpos, Blocks.CAULDRON.defaultBlockState(), 2);
        mpos.set(forgeX, oy + 1, forgeZ + 1);
        level.setBlock(mpos, Blocks.CAMPFIRE.defaultBlockState(), 2);
        BlockPos forgeChest = new BlockPos(forgeX + 2, oy + 1, forgeZ);
        level.setBlock(forgeChest, Blocks.CHEST.defaultBlockState(), 3);
        populateChest(level, forgeChest, 1);
        count += 5;

        int stableX = ox - 22;
        int stableZ = oz + 20;
        for (int dx = -3; dx <= 3; dx++) {
            mpos.set(stableX + dx, oy + 1, stableZ);
            level.setBlock(mpos, Blocks.SPRUCE_FENCE.defaultBlockState(), 2);
            mpos.set(stableX + dx, oy + 1, stableZ + 4);
            level.setBlock(mpos, Blocks.SPRUCE_FENCE.defaultBlockState(), 2);
            count += 2;
        }
        for (int dz = 0; dz <= 4; dz++) {
            mpos.set(stableX - 3, oy + 1, stableZ + dz);
            level.setBlock(mpos, Blocks.SPRUCE_FENCE.defaultBlockState(), 2);
            mpos.set(stableX + 3, oy + 1, stableZ + dz);
            level.setBlock(mpos, Blocks.SPRUCE_FENCE.defaultBlockState(), 2);
            count += 2;
        }
        mpos.set(stableX - 1, oy + 1, stableZ + 1);
        level.setBlock(mpos, Blocks.HAY_BLOCK.defaultBlockState(), 2);
        mpos.set(stableX - 1, oy + 2, stableZ + 1);
        level.setBlock(mpos, Blocks.HAY_BLOCK.defaultBlockState(), 2);
        mpos.set(stableX + 1, oy + 1, stableZ + 1);
        level.setBlock(mpos, Blocks.CAULDRON.defaultBlockState(), 2);
        count += 3;

        return count;
    }

    private static int generateColossalKeep(ServerLevel level, int ox, int oy, int oz) {
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        int minX = ox - 18;
        int maxX = ox + 18;
        int minZ = oz - 22;
        int maxZ = oz + 12;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                boolean isEdge = (x == minX || x == maxX || z == minZ || z == maxZ);

                for (int y = 1; y <= 16; y++) {
                    mpos.set(x, oy + y, z);
                    if (isEdge) {
                        boolean isMainGate = (z == maxZ && Math.abs(x - ox) <= 2 && y <= 5);
                        if (isMainGate) {
                            level.setBlock(mpos, Blocks.AIR.defaultBlockState(), 2);
                        } else {
                            level.setBlock(mpos, getWallBlock(x, oy + y, z), 2);
                        }
                    } else {
                        level.setBlock(mpos, Blocks.AIR.defaultBlockState(), 2);
                    }
                    count++;
                }

                mpos.set(x, oy + 16, z);
                level.setBlock(mpos, Blocks.POLISHED_ANDESITE.defaultBlockState(), 2);
                count++;

                for (int y = 17; y <= 28; y++) {
                    mpos.set(x, oy + y, z);
                    if (isEdge) {
                        boolean isWindow = (y == 21 || y == 22) && ((x - ox) % 5 == 0 || (z - oz) % 5 == 0);
                        if (isWindow) {
                            level.setBlock(mpos, Blocks.IRON_BARS.defaultBlockState(), 2);
                        } else {
                            level.setBlock(mpos, getWallBlock(x, oy + y, z), 2);
                        }
                    } else {
                        level.setBlock(mpos, Blocks.AIR.defaultBlockState(), 2);
                    }
                    count++;
                }

                mpos.set(x, oy + 28, z);
                level.setBlock(mpos, Blocks.STONE_BRICKS.defaultBlockState(), 2);
                count++;

                if (isEdge) {
                    mpos.set(x, oy + 29, z);
                    level.setBlock(mpos, Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
                    if ((x + z) % 2 == 0) {
                        mpos.set(x, oy + 30, z);
                        level.setBlock(mpos, Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
                        count++;
                    }
                    count++;
                }
            }
        }

        int[][] pillars = {
                {ox - 8, oz - 8}, {ox + 8, oz - 8},
                {ox - 8, oz + 4}, {ox + 8, oz + 4}
        };
        for (int[] p : pillars) {
            for (int y = 1; y <= 15; y++) {
                for (int px = -1; px <= 1; px++) {
                    for (int pz = -1; pz <= 1; pz++) {
                        mpos.set(p[0] + px, oy + y, p[1] + pz);
                        BlockState pBlock = (y == 1 || y == 15 || px == 0 || pz == 0)
                                ? Blocks.CHISELED_STONE_BRICKS.defaultBlockState()
                                : Blocks.STONE_BRICKS.defaultBlockState();
                        level.setBlock(mpos, pBlock, 2);
                        count++;
                    }
                }
            }
        }

        for (int z = oz - 16; z <= oz + 11; z++) {
            for (int dx = -1; dx <= 1; dx++) {
                mpos.set(ox + dx, oy, z);
                BlockState runner = (dx == 0)
                        ? Blocks.GOLD_BLOCK.defaultBlockState()
                        : Blocks.POLISHED_BLACKSTONE.defaultBlockState();
                level.setBlock(mpos, runner, 2);
                count++;
            }
        }

        int throneZ = oz - 18;
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -2; dz <= 1; dz++) {
                mpos.set(ox + dx, oy + 1, throneZ + dz);
                level.setBlock(mpos, Blocks.POLISHED_BLACKSTONE.defaultBlockState(), 2);
                count++;
            }
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                mpos.set(ox + dx, oy + 2, throneZ + dz);
                level.setBlock(mpos, Blocks.POLISHED_BLACKSTONE.defaultBlockState(), 2);
                count++;
            }
        }
        mpos.set(ox, oy + 3, throneZ);
        level.setBlock(mpos, Blocks.POLISHED_BLACKSTONE_STAIRS.defaultBlockState(), 2);
        mpos.set(ox, oy + 4, throneZ - 1);
        level.setBlock(mpos, Blocks.GOLD_BLOCK.defaultBlockState(), 2);
        mpos.set(ox - 1, oy + 3, throneZ);
        level.setBlock(mpos, Blocks.GOLD_BLOCK.defaultBlockState(), 2);
        mpos.set(ox + 1, oy + 3, throneZ);
        level.setBlock(mpos, Blocks.GOLD_BLOCK.defaultBlockState(), 2);
        count += 4;

        int[][] chandeliers = {{ox - 5, oz - 2}, {ox + 5, oz - 2}};
        for (int[] ch : chandeliers) {
            for (int y = 15; y >= 11; y--) {
                mpos.set(ch[0], oy + y, ch[1]);
                level.setBlock(mpos, Blocks.IRON_BARS.defaultBlockState(), 2);
                count++;
            }
            mpos.set(ch[0], oy + 10, ch[1]);
            level.setBlock(mpos, Blocks.SEA_LANTERN.defaultBlockState(), 2);
            count++;
        }

        BlockPos treasuryChest1 = new BlockPos(ox + 12, oy + 17, oz - 10);
        level.setBlock(treasuryChest1, Blocks.CHEST.defaultBlockState(), 3);
        populateChest(level, treasuryChest1, 2);
        BlockPos treasuryChest2 = new BlockPos(ox + 12, oy + 17, oz - 8);
        level.setBlock(treasuryChest2, Blocks.CHEST.defaultBlockState(), 3);
        populateChest(level, treasuryChest2, 3);
        count += 2;

        int libX = ox - 12;
        int libZ = oz - 10;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = 1; dy <= 4; dy++) {
                mpos.set(libX + dx, oy + 16 + dy, libZ);
                level.setBlock(mpos, Blocks.BOOKSHELF.defaultBlockState(), 2);
                count++;
            }
        }
        mpos.set(libX, oy + 17, libZ + 2);
        level.setBlock(mpos, Blocks.ENCHANTING_TABLE.defaultBlockState(), 2);
        mpos.set(libX + 2, oy + 17, libZ + 2);
        level.setBlock(mpos, Blocks.BREWING_STAND.defaultBlockState(), 2);
        count += 2;

        int[][] keepCorners = {
                {minX, minZ},
                {maxX, minZ},
                {minX, maxZ},
                {maxX, maxZ}
        };
        for (int[] kc : keepCorners) {
            count += buildCylindricalTower(level, kc[0], kc[1], oy, 38, 5.0, 3.5, 12);
        }

        return count;
    }

    private static int generateCelestialSpire(ServerLevel level, int ox, int oy, int oz) {
        int count = 0;
        int spireCx = ox;
        int spireCz = oz - 4;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        int startY = oy + 28;
        int spireWallHeight = 22;
        count += buildCylindricalTower(level, spireCx, spireCz, startY, spireWallHeight, 5.5, 4.0, 14);

        int observationY = startY + spireWallHeight;
        mpos.set(spireCx, observationY + 1, spireCz);
        level.setBlock(mpos, Blocks.BEACON.defaultBlockState(), 3);
        mpos.set(spireCx, observationY, spireCz);
        level.setBlock(mpos, Blocks.NETHERITE_BLOCK.defaultBlockState(), 2);
        count += 2;

        BlockPos royalChest = new BlockPos(spireCx + 2, observationY + 1, spireCz);
        level.setBlock(royalChest, Blocks.CHEST.defaultBlockState(), 3);
        populateChest(level, royalChest, 3);
        count++;

        return count;
    }

    private static int generateFoliageAndDetails(ServerLevel level, int ox, int oy, int oz) {
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        for (int dx = -38; dx <= 38; dx += 4) {
            int wx = ox + dx;
            int wz = oz + 37;
            for (int y = 1; y <= 6; y++) {
                if ((dx + y) % 3 == 0) {
                    mpos.set(wx, oy + y, wz);
                    if (level.getBlockState(mpos).isAir()) {
                        level.setBlock(mpos, Blocks.VINE.defaultBlockState().setValue(VineBlock.NORTH, true), 2);
                        count++;
                    }
                }
            }
        }

        int[][] shrubLocations = {
                {ox - 30, oz + 38}, {ox + 30, oz + 38},
                {ox - 12, oz + 38}, {ox + 12, oz + 38},
                {ox - 20, oz + 14}, {ox + 20, oz + 14}
        };
        for (int[] sl : shrubLocations) {
            mpos.set(sl[0], oy + 1, sl[1]);
            level.setBlock(mpos, Blocks.OAK_LEAVES.defaultBlockState(), 2);
            mpos.set(sl[0], oy + 2, sl[1]);
            level.setBlock(mpos, Blocks.AZALEA_LEAVES.defaultBlockState(), 2);
            count += 2;
        }

        return count;
    }

    private static void populateChest(ServerLevel level, BlockPos pos, int chestType) {
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            switch (chestType) {
                case 0 -> {
                    chest.setItem(0, new ItemStack(Items.IRON_SWORD));
                    chest.setItem(1, new ItemStack(Items.BOW));
                    chest.setItem(2, new ItemStack(Items.ARROW, 32));
                    chest.setItem(3, new ItemStack(Items.IRON_HELMET));
                    chest.setItem(4, new ItemStack(Items.BREAD, 16));
                    chest.setItem(5, new ItemStack(SandStormItems.SPACE_RATION, 8));
                }
                case 1 -> {
                    chest.setItem(0, new ItemStack(Items.IRON_INGOT, 16));
                    chest.setItem(1, new ItemStack(SandStormItems.RAW_SILICON, 24));
                    chest.setItem(2, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 6));
                    chest.setItem(3, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 8));
                    chest.setItem(4, new ItemStack(Items.COAL, 32));
                }
                case 2 -> {
                    chest.setItem(0, new ItemStack(Items.DIAMOND, 8));
                    chest.setItem(1, new ItemStack(Items.EMERALD, 16));
                    chest.setItem(2, new ItemStack(SandStormItems.SILICON_WAFER, 12));
                    chest.setItem(3, new ItemStack(SandStormBlocks.ANCIENT_DATA_CORE.asItem(), 2));
                    chest.setItem(4, new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE, 8));
                }
                case 3 -> {
                    chest.setItem(0, new ItemStack(SandStormItems.ADVANCED_REFRACTION_LENS, 1));
                    chest.setItem(1, new ItemStack(SandStormBlocks.ANCIENT_DATA_CORE.asItem(), 4));
                    chest.setItem(2, new ItemStack(Items.NETHERITE_INGOT, 2));
                    chest.setItem(3, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 2));
                    chest.setItem(4, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 16));
                }
            }
            chest.setChanged();
        }
    }

    private static BlockState getWallBlock(int x, int y, int z) {
        int hash = Math.abs((x * 3129871) ^ (y * 618293) ^ (z * 423719)) % 100;
        if (hash < 55) {
            return Blocks.STONE_BRICKS.defaultBlockState();
        } else if (hash < 72) {
            return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        } else if (hash < 88) {
            return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        } else {
            return Blocks.COBBLESTONE.defaultBlockState();
        }
    }

    private static BlockState getFoundationBlock(int x, int y, int z) {
        int hash = Math.abs((x * 7919) ^ (y * 6271) ^ (z * 3571)) % 100;
        if (hash < 50) {
            return Blocks.STONE.defaultBlockState();
        } else if (hash < 80) {
            return Blocks.COBBLESTONE.defaultBlockState();
        } else {
            return Blocks.STONE_BRICKS.defaultBlockState();
        }
    }

    private static BlockState getFloorBlock(int x, int z) {
        int hash = Math.abs((x * 1237) ^ (z * 7823)) % 100;
        if (hash < 40) {
            return Blocks.SMOOTH_STONE.defaultBlockState();
        } else if (hash < 75) {
            return Blocks.STONE_BRICKS.defaultBlockState();
        } else if (hash < 90) {
            return Blocks.POLISHED_ANDESITE.defaultBlockState();
        } else {
            return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        }
    }
}
