package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.DesalinationFilterMenu;
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

public class DesalinationFilterBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_BOTTOM = new int[]{1, 2};
    private static final int[] SLOTS_SIDES = new int[]{3};

    public DesalinationFilterBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.DESALINATION_FILTER_BE, pos, state);
    }

    public DesalinationFilterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 4, 80);
    }

    @Override
    protected boolean canProcess() {
        ItemStack in = items.get(0);
        if (!in.is(SandStormItems.BRACKISH_WATER_BOTTLE)) {
            return false;
        }
        ItemStack outWater = items.get(1);
        if (!outWater.isEmpty() && (!outWater.is(SandStormItems.POTABLE_WATER_BOTTLE) || outWater.getCount() >= outWater.getMaxStackSize())) {
            return false;
        }
        ItemStack outSalt = items.get(2);
        if (!outSalt.isEmpty() && (!outSalt.is(SandStormItems.MINERAL_SALT) || outSalt.getCount() + 2 > outSalt.getMaxStackSize())) {
            return false;
        }
        return true;
    }

    @Override
    protected void processRecipe() {
        if (!canProcess()) {
            return;
        }
        items.get(0).shrink(1);
        ItemStack outWater = items.get(1);
        if (outWater.isEmpty()) {
            items.set(1, new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE));
        } else {
            outWater.grow(1);
        }

        ItemStack outSalt = items.get(2);
        if (outSalt.isEmpty()) {
            items.set(2, new ItemStack(SandStormItems.MINERAL_SALT, 2));
        } else {
            outSalt.grow(2);
        }
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SandStormSoundEvents.DESALINATION_PROCESS;
    }

    @Override
    protected int getBatterySlotIndex() {
        return 3;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.desalination_filter");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new DesalinationFilterMenu(syncId, playerInventory, this, this.dataAccess);
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
            return stack.is(SandStormItems.BRACKISH_WATER_BOTTLE);
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && (slot == 1 || slot == 2);
    }
}
