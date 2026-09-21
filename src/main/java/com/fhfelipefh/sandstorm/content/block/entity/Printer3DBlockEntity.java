package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.Printer3DMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class Printer3DBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{0, 1};
    private static final int[] SLOTS_BOTTOM = new int[]{2};
    private static final int[] SLOTS_SIDES = new int[]{3};

    public Printer3DBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.PRINTER_3D_BE, pos, state);
    }

    public Printer3DBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 4, 100);
    }

    @Override
    protected boolean canProcess() {
        ItemStack in0 = items.get(0);
        ItemStack in1 = items.get(1);
        if (!in0.is(SandStormItems.SILICON_WAFER)) {
            return false;
        }
        if (!in1.is(SandStormItems.RAW_SILICON) && !in1.is(SandStormItems.SCRAP_METAL)) {
            return false;
        }
        ItemStack out = items.get(2);
        if (out.isEmpty()) {
            return true;
        }
        return out.is(SandStormItems.CIRCUIT_BOARD) && out.getCount() < out.getMaxStackSize();
    }

    @Override
    protected void processRecipe() {
        if (!canProcess()) {
            return;
        }
        items.get(0).shrink(1);
        items.get(1).shrink(1);
        ItemStack out = items.get(2);
        if (out.isEmpty()) {
            items.set(2, new ItemStack(SandStormItems.CIRCUIT_BOARD));
        } else {
            out.grow(1);
        }
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SandStormSoundEvents.PRINTER_3D_CRAFT;
    }

    @Override
    protected int getBatterySlotIndex() {
        return 3;
    }

    public ItemStack getPrintingItem() {
        if (this.isProcessing()) {
            return new ItemStack(SandStormItems.CIRCUIT_BOARD);
        }
        return this.getItem(2);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.printer_3d");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new Printer3DMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        }
        if (side == Direction.UP) {
            return SLOTS_TOP;
        }
        return SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN) {
            return false;
        }
        if (slot == 3) {
            return getFuelEnergy(stack) > 0;
        }
        if (slot == 0) {
            return stack.is(SandStormItems.SILICON_WAFER);
        }
        if (slot == 1) {
            return stack.is(SandStormItems.RAW_SILICON) || stack.is(SandStormItems.SCRAP_METAL);
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == 2;
    }
}
