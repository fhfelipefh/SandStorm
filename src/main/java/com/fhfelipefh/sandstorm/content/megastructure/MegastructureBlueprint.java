package com.fhfelipefh.sandstorm.content.megastructure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum MegastructureBlueprint {
    BIOSPHERE_DOME("biosphere_dome", "Biosphere Dome", 25, 13, 25),
    PLANETARY_CITADEL("planetary_citadel", "Planetary Citadel", 31, 18, 31),
    ORBITAL_LAUNCH_SILO("orbital_launch_silo", "Orbital Launch Silo", 19, 32, 19),
    DESERT_TECH_PYRAMID("desert_tech_pyramid", "Desert Tech Pyramid", 29, 15, 29);

    private final String id;
    private final String displayName;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private List<BlockPlacement> cachedPlacements;
    private BlockPos cachedMinPos;
    private BlockPos cachedMaxPos;

    MegastructureBlueprint(String id, String displayName, int sizeX, int sizeY, int sizeZ) {
        this.id = id;
        this.displayName = displayName;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    public synchronized List<BlockPlacement> getPlacements() {
        if (cachedPlacements == null) {
            cachedPlacements = Collections.unmodifiableList(generatePlacements());
            calculateBounds();
        }
        return cachedPlacements;
    }

    public synchronized BlockPos getMinPos() {
        if (cachedMinPos == null) {
            getPlacements();
        }
        return cachedMinPos;
    }

    public synchronized BlockPos getMaxPos() {
        if (cachedMaxPos == null) {
            getPlacements();
        }
        return cachedMaxPos;
    }

    private void calculateBounds() {
        List<BlockPlacement> list = cachedPlacements;
        int minX = 0;
        int minY = 0;
        int minZ = 0;
        int maxX = 0;
        int maxY = 0;
        int maxZ = 0;
        if (list != null && !list.isEmpty()) {
            BlockPos first = list.getFirst().relativePos();
            minX = first.getX();
            minY = first.getY();
            minZ = first.getZ();
            maxX = minX;
            maxY = minY;
            maxZ = minZ;
            for (BlockPlacement bp : list) {
                BlockPos p = bp.relativePos();
                if (p.getX() < minX) {
                    minX = p.getX();
                }
                if (p.getX() > maxX) {
                    maxX = p.getX();
                }
                if (p.getY() < minY) {
                    minY = p.getY();
                }
                if (p.getY() > maxY) {
                    maxY = p.getY();
                }
                if (p.getZ() < minZ) {
                    minZ = p.getZ();
                }
                if (p.getZ() > maxZ) {
                    maxZ = p.getZ();
                }
            }
        }
        cachedMinPos = new BlockPos(minX, minY, minZ);
        cachedMaxPos = new BlockPos(maxX, maxY, maxZ);
    }

    public static MegastructureBlueprint byIndex(int index) {
        MegastructureBlueprint[] values = values();
        if (index < 0 || index >= values.length) {
            return BIOSPHERE_DOME;
        }
        return values[index];
    }

    private List<BlockPlacement> generatePlacements() {
        List<BlockPlacement> list = new ArrayList<>();
        switch (this) {
            case BIOSPHERE_DOME -> generateBiosphereDome(list);
            case PLANETARY_CITADEL -> generatePlanetaryCitadel(list);
            case ORBITAL_LAUNCH_SILO -> generateOrbitalLaunchSilo(list);
            case DESERT_TECH_PYRAMID -> generateDesertTechPyramid(list);
        }
        list.sort(Comparator.comparingInt((BlockPlacement p) -> p.relativePos().getY())
                .thenComparingDouble(p -> p.relativePos().distSqr(BlockPos.ZERO)));
        return list;
    }

    private void generateBiosphereDome(List<BlockPlacement> list) {
        int radius = 12;
        BlockState iron = Blocks.IRON_BLOCK.defaultBlockState();
        BlockState glass = Blocks.TINTED_GLASS.defaultBlockState();
        BlockState lantern = Blocks.SEA_LANTERN.defaultBlockState();

        for (int y = 0; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + y * y + z * z);
                    if (Math.abs(dist - radius) <= 0.85) {
                        BlockState block;
                        if (y == radius) {
                            block = lantern;
                        } else if (y == 0 || x % 4 == 0 || z % 4 == 0) {
                            block = iron;
                        } else {
                            block = glass;
                        }
                        list.add(new BlockPlacement(new BlockPos(x, y, z), block));
                    }
                }
            }
        }
    }

    private void generatePlanetaryCitadel(List<BlockPlacement> list) {
        int half = 15;
        BlockState smoothSandstone = Blocks.SMOOTH_SANDSTONE.defaultBlockState();
        BlockState cutSandstone = Blocks.CUT_SANDSTONE.defaultBlockState();
        BlockState chiseledSandstone = Blocks.CHISELED_SANDSTONE.defaultBlockState();
        BlockState spikeWall = Blocks.IRON_BARS.defaultBlockState();
        BlockState iron = Blocks.IRON_BLOCK.defaultBlockState();

        for (int x = -half; x <= half; x++) {
            for (int z = -half; z <= half; z++) {
                list.add(new BlockPlacement(new BlockPos(x, 0, z), smoothSandstone));
            }
        }

        for (int y = 1; y <= 6; y++) {
            for (int x = -half; x <= half; x++) {
                for (int z = -half; z <= half; z++) {
                    boolean isWall = Math.abs(x) == half || Math.abs(z) == half;
                    boolean isGate = (x == 0 && Math.abs(z) == half && y <= 3);
                    if (isWall && !isGate) {
                        list.add(new BlockPlacement(new BlockPos(x, y, z), cutSandstone));
                    }
                }
            }
        }

        int bastionRadius = 3;
        int[] corners = {-half + bastionRadius, half - bastionRadius};
        for (int cx : corners) {
            for (int cz : corners) {
                for (int y = 1; y <= 10; y++) {
                    for (int bx = cx - bastionRadius; bx <= cx + bastionRadius; bx++) {
                        for (int bz = cz - bastionRadius; bz <= cz + bastionRadius; bz++) {
                            boolean isEdge = (bx == cx - bastionRadius || bx == cx + bastionRadius
                                    || bz == cz - bastionRadius || bz == cz + bastionRadius);
                            if (isEdge) {
                                BlockState block = (y == 10) ? spikeWall : ((y % 3 == 0) ? chiseledSandstone : iron);
                                list.add(new BlockPlacement(new BlockPos(bx, y, bz), block));
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateOrbitalLaunchSilo(List<BlockPlacement> list) {
        int radius = 8;
        BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();
        BlockState iron = Blocks.IRON_BLOCK.defaultBlockState();
        BlockState copper = Blocks.RAW_COPPER_BLOCK.defaultBlockState();
        BlockState pipe = Blocks.IRON_BARS.defaultBlockState();

        for (int y = 0; y <= 16; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist >= radius - 1 && dist <= radius + 0.5) {
                        BlockState block = (y % 4 == 0) ? obsidian : iron;
                        list.add(new BlockPlacement(new BlockPos(x, y, z), block));
                    }
                }
            }
        }

        for (int y = 17; y <= 31; y++) {
            list.add(new BlockPlacement(new BlockPos(-radius, y, -radius), copper));
            list.add(new BlockPlacement(new BlockPos(-radius, y, radius), copper));
            list.add(new BlockPlacement(new BlockPos(radius, y, -radius), copper));
            list.add(new BlockPlacement(new BlockPos(radius, y, radius), copper));
            if (y % 3 == 0) {
                list.add(new BlockPlacement(new BlockPos(0, y, radius), pipe));
            }
        }
    }

    private void generateDesertTechPyramid(List<BlockPlacement> list) {
        int half = 14;
        BlockState chiseled = Blocks.CHISELED_SANDSTONE.defaultBlockState();
        BlockState cut = Blocks.CUT_SANDSTONE.defaultBlockState();
        BlockState gold = Blocks.GOLD_BLOCK.defaultBlockState();
        BlockState lantern = Blocks.SEA_LANTERN.defaultBlockState();

        for (int y = 0; y <= half; y++) {
            int currentHalf = half - y;
            for (int x = -currentHalf; x <= currentHalf; x++) {
                for (int z = -currentHalf; z <= currentHalf; z++) {
                    boolean isBorder = (Math.abs(x) == currentHalf || Math.abs(z) == currentHalf);
                    boolean isVein = (x == 0 || z == 0);
                    if (y == half) {
                        list.add(new BlockPlacement(new BlockPos(x, y, z), lantern));
                    } else if (isBorder) {
                        BlockState block = isVein ? gold : cut;
                        list.add(new BlockPlacement(new BlockPos(x, y, z), block));
                    } else if (y == 0) {
                        list.add(new BlockPlacement(new BlockPos(x, y, z), chiseled));
                    }
                }
            }
        }
    }

    public record BlockPlacement(BlockPos relativePos, BlockState state) {
    }
}
