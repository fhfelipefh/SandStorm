package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;

public class MorphingAlloyDoorBlock extends DoorBlock {

    public MorphingAlloyDoorBlock(BlockBehaviour.Properties properties) {
        super(BlockSetType.IRON, properties);
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_CYAN)
                .strength(50.0f, 1200.0f)
                .sound(SoundType.HEAVY_CORE)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }
}
