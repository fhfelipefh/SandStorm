package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class SandStormBlocks {
    public static final BrackishWaterBlock BRACKISH_AQUIFER = register("brackish_aquifer",
            new BrackishWaterBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(1.5f)
                    .sound(SoundType.MUD)));

    public static <T extends Block> T register(String path, T block) {
        T registeredBlock = Registry.register(BuiltInRegistries.BLOCK, SandStormMod.id(path), block);
        SandStormItems.register(path, new BlockItem(registeredBlock, new Item.Properties()));
        return registeredBlock;
    }

    public static void initialize() {
    }
}
