package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.AutoAssemblyLineMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AutoAssemblyLineBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_INPUT = new int[]{0};
    private static final int[] SLOTS_OUTPUT = new int[]{1};
    private static final int[] SLOTS_ALL = new int[]{0, 1, 2};

    public AutoAssemblyLineBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.AUTO_ASSEMBLY_LINE_BE, pos, state);
    }

    public AutoAssemblyLineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 3, 60);
    }

    @Override
    protected boolean canProcess() {
        ItemStack input = items.get(0);
        if (!input.is(SandStormItems.SCRAP_METAL)) {
            return false;
        }
        ItemStack output = items.get(1);
        if (output.isEmpty()) {
            return true;
        }
        return output.is(SandStormItems.CIRCUIT_BOARD) && output.getCount() < output.getMaxStackSize();
    }

    @Override
    protected void processRecipe() {
        if (!canProcess()) {
            return;
        }
        items.get(0).shrink(1);
        ItemStack output = items.get(1);
        if (output.isEmpty()) {
            items.set(1, new ItemStack(SandStormItems.CIRCUIT_BOARD));
        } else {
            output.grow(1);
        }
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.ANVIL_USE;
    }

    @Override
    protected int getBatterySlotIndex() {
        return 2;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.auto_assembly_line");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new AutoAssemblyLineMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_OUTPUT;
        }
        if (side == Direction.UP) {
            return SLOTS_INPUT;
        }
        return SLOTS_ALL;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) {
            return stack.is(SandStormItems.SCRAP_METAL);
        }
        if (slot == 2) {
            return getFuelEnergy(stack) > 0;
        }
        return false;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN) {
            return slot == 2 && getFuelEnergy(stack) > 0;
        }
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return slot == 1;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 1;
    }
}
