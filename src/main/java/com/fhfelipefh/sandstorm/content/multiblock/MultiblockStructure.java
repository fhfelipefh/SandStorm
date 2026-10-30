package com.fhfelipefh.sandstorm.content.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiblockStructure {
    private final String id;
    private final String titleKey;
    private final String descKey;
    private final String associatedQuestId;
    private final List<MultiblockBlockRequirement> requirements;
    private final Map<Block, Integer> materialCounts;

    public MultiblockStructure(String id, String titleKey, String descKey, String associatedQuestId, List<MultiblockBlockRequirement> requirements) {
        this.id = id;
        this.titleKey = titleKey;
        this.descKey = descKey;
        this.associatedQuestId = associatedQuestId;
        this.requirements = List.copyOf(requirements);

        Map<Block, Integer> counts = new LinkedHashMap<>();
        for (MultiblockBlockRequirement req : this.requirements) {
            counts.put(req.expectedBlock(), counts.getOrDefault(req.expectedBlock(), 0) + 1);
        }
        this.materialCounts = Collections.unmodifiableMap(counts);
    }

    public String getId() {
        return id;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public String getDescKey() {
        return descKey;
    }

    public String getAssociatedQuestId() {
        return associatedQuestId;
    }

    public List<MultiblockBlockRequirement> getRequirements() {
        return requirements;
    }

    public Map<Block, Integer> getMaterialCounts() {
        return materialCounts;
    }

    public int getTotalBlocks() {
        return requirements.size();
    }

    public static BlockPos rotateOffset(BlockPos offset, Direction facing) {
        if (facing == null) {
            return offset;
        }
        return switch (facing) {
            case NORTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case EAST -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            case WEST -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            default -> offset;
        };
    }
}

