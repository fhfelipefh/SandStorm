package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class MorphingAlloyBlock extends Block {

    public MorphingAlloyBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_GRAY)
                .strength(50.0f, 1200.0f)
                .sound(SoundType.HEAVY_CORE)
                .requiresCorrectToolForDrops();
    }
}
