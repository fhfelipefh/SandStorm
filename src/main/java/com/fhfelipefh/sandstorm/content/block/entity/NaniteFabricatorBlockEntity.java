package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.NaniteFabricatorMenu;
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

public class NaniteFabricatorBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{0, 1, 3};
    private static final int[] SLOTS_BOTTOM = new int[]{2, 3};
    private static final int[] SLOTS_SIDES = new int[]{1, 0, 3, 2};

    public NaniteFabricatorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.NANITE_FABRICATOR_BE, pos, state);
    }

    public NaniteFabricatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 4, 120);
    }

    @Override
    protected boolean canProcess() {
        ItemStack in0 = items.get(0);
        ItemStack in1 = items.get(1);
        if (!in0.is(SandStormItems.CIRCUIT_BOARD)) {
            return false;
        }
        if (!in1.is(SandStormItems.SANDWORM_CHITIN) && !in1.is(SandStormItems.SCRAP_METAL)) {
            return false;
        }
        ItemStack out = items.get(2);
        if (out.isEmpty()) {
            return true;
        }
        return out.is(SandStormItems.NANO_ACTUATOR) && out.getCount() < out.getMaxStackSize();
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
            items.set(2, new ItemStack(SandStormItems.NANO_ACTUATOR));
        } else {
            out.grow(1);
        }
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.BEACON_ACTIVATE;
    }

    @Override
    protected int getBatterySlotIndex() {
        return 3;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.nanite_fabricator");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new NaniteFabricatorMenu(syncId, playerInventory, this, this.dataAccess);
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
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) {
            return stack.is(SandStormItems.CIRCUIT_BOARD);
        }
        if (slot == 1) {
            return stack.is(SandStormItems.SANDWORM_CHITIN) || stack.is(SandStormItems.SCRAP_METAL);
        }
        if (slot == 3) {
            return getFuelEnergy(stack) > 0;
        }
        return false;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN) {
            return slot == 3 && getFuelEnergy(stack) > 0;
        }
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return slot == 2;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 2;
    }
}
