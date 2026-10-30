package com.fhfelipefh.sandstorm.content.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public record MultiblockBlockRequirement(
        BlockPos offset,
        Block expectedBlock,
        int colorRgb,
        String roleLabel
) {
}

