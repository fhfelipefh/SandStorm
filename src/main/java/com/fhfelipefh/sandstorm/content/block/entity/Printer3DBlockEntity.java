package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.Printer3DMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class Printer3DBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{0, 1, 3};
    private static final int[] SLOTS_BOTTOM = new int[]{2, 3};
    private static final int[] SLOTS_SIDES = new int[]{1, 0, 3, 2};

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
        if (in0.is(SandStormItems.SILICON_WAFER)) {
            if (!in1.is(SandStormItems.RAW_SILICON) && !in1.is(SandStormItems.SCRAP_METAL)) {
                return false;
            }
            ItemStack out = items.get(2);
            if (out.isEmpty()) {
                return true;
            }
            return out.is(SandStormItems.CIRCUIT_BOARD) && out.getCount() < out.getMaxStackSize();
        }
        if (in0.is(SandStormItems.CIRCUIT_BOARD)) {
            if (!in1.is(SandStormItems.NANO_ACTUATOR)) {
                return false;
            }
            ItemStack out = items.get(2);
            return out.isEmpty();
        }
        if (in0.is(SandStormItems.RAW_SILICON)) {
            if (!in1.is(SandStormItems.RAW_SILICON)) {
                return false;
            }
            ItemStack out = items.get(2);
            if (out.isEmpty()) {
                return true;
            }
            return out.is(SandStormItems.ELECTRIC_COMPONENT) && out.getCount() < out.getMaxStackSize();
        }
        if (in0.is(SandStormItems.TITANIUM_CHITIN_COMPOSITE)) {
            if (!in1.is(SandStormItems.PRESSURE_SEAL)) {
                return false;
            }
            ItemStack out = items.get(2);
            return out.isEmpty();
        }
        return false;
    }

    @Override
    protected void processRecipe() {
        if (!canProcess()) {
            return;
        }
        ItemStack in0 = items.get(0);
        ItemStack in1 = items.get(1);
        if (in0.is(SandStormItems.SILICON_WAFER)) {
            in0.shrink(1);
            in1.shrink(1);
            ItemStack out = items.get(2);
            if (out.isEmpty()) {
                items.set(2, new ItemStack(SandStormItems.CIRCUIT_BOARD));
            } else {
                out.grow(1);
            }
        } else if (in0.is(SandStormItems.CIRCUIT_BOARD)) {
            in0.shrink(1);
            in1.shrink(1);
            items.set(2, new ItemStack(SandStormItems.PLASMA_RIFLE));
        } else if (in0.is(SandStormItems.RAW_SILICON)) {
            in0.shrink(1);
            in1.shrink(1);
            ItemStack out = items.get(2);
            if (out.isEmpty()) {
                items.set(2, new ItemStack(SandStormItems.ELECTRIC_COMPONENT));
            } else {
                out.grow(1);
            }
        } else if (in0.is(SandStormItems.TITANIUM_CHITIN_COMPOSITE)) {
            in0.shrink(1);
            in1.shrink(1);
            items.set(2, new ItemStack(SandStormItems.HYPO_INJECTOR));
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
            ItemStack in0 = items.get(0);
            if (in0.is(SandStormItems.TITANIUM_CHITIN_COMPOSITE)) {
                return new ItemStack(SandStormItems.HYPO_INJECTOR);
            }
            if (in0.is(SandStormItems.CIRCUIT_BOARD)) {
                return new ItemStack(SandStormItems.PLASMA_RIFLE);
            }
            if (in0.is(SandStormItems.RAW_SILICON)) {
                return new ItemStack(SandStormItems.ELECTRIC_COMPONENT);
            }
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
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) {
            return stack.is(SandStormItems.SILICON_WAFER) || stack.is(SandStormItems.CIRCUIT_BOARD) || stack.is(SandStormItems.RAW_SILICON) || stack.is(SandStormItems.TITANIUM_CHITIN_COMPOSITE);
        }
        if (slot == 1) {
            return stack.is(SandStormItems.RAW_SILICON) || stack.is(SandStormItems.SCRAP_METAL) || stack.is(SandStormItems.NANO_ACTUATOR) || stack.is(SandStormItems.PRESSURE_SEAL);
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
