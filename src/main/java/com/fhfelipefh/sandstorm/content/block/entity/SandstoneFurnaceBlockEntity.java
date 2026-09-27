package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SandstoneFurnaceBlockEntity extends AbstractFurnaceBlockEntity {

    public SandstoneFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.SANDSTONE_FURNACE_BE, pos, state, RecipeType.SMELTING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.sandstorm.sandstone_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new FurnaceMenu(syncId, playerInventory, this, this.dataAccess);
    }
}
