package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SteelFurnaceBlockEntity extends AbstractFurnaceBlockEntity {

    public SteelFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.STEEL_FURNACE_BE, pos, state, RecipeType.SMELTING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.sandstorm.steel_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new FurnaceMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected int getBurnDuration(ServerLevel level, ItemStack stack) {
        return super.getBurnDuration(level, stack) * 2;
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, SteelFurnaceBlockEntity furnace) {
        if (furnace.dataAccess.get(DATA_LIT_TIME) > 0) {
            int progress = furnace.dataAccess.get(DATA_COOKING_PROGRESS);
            int total = furnace.dataAccess.get(DATA_COOKING_TOTAL_TIME);
            if (progress > 0 && progress < total - 1) {
                furnace.dataAccess.set(DATA_COOKING_PROGRESS, progress + 1);
            }
        }
        AbstractFurnaceBlockEntity.serverTick(level, pos, state, furnace);
    }
}
