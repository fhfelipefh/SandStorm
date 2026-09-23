package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SolidStateAccumulatorBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SolidStateAccumulatorMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public SolidStateAccumulatorMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.SOLID_STATE_ACCUMULATOR_MENU, syncId, playerInventory, new SimpleContainer(2), new SimpleContainerData(7));
    }

    public SolidStateAccumulatorMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.SOLID_STATE_ACCUMULATOR_MENU, syncId, playerInventory, container, data);
    }

    public SolidStateAccumulatorMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 2);
        checkContainerDataCount(data, 7);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 56, 35));
        this.addSlot(new Slot(container, 1, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    public int getStoredEnergy() {
        return (data.get(0) & 0xFFFF) | ((data.get(1) & 0xFFFF) << 16);
    }

    public int getMaxEnergy() {
        return (data.get(2) & 0xFFFF) | ((data.get(3) & 0xFFFF) << 16);
    }

    public int getMode() {
        return data.get(4);
    }

    public boolean isDischarging() {
        return data.get(5) == 1;
    }

    public boolean isCharging() {
        return data.get(6) == 1;
    }

    public float getChargePercentage() {
        return (float) getStoredEnergy() / (float) Math.max(1, getMaxEnergy());
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && container instanceof SolidStateAccumulatorBlockEntity accumulator) {
            accumulator.cycleMode();
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            if (slotIndex == 0 || slotIndex == 1) {
                if (!this.moveItemStackTo(stackInSlot, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemStack);
            } else {
                if (BaseMachineBlockEntity.getFuelEnergy(stackInSlot) > 0) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 2 && slotIndex < 29) {
                    if (!this.moveItemStackTo(stackInSlot, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 29 && slotIndex < 38) {
                    if (!this.moveItemStackTo(stackInSlot, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return itemStack;
    }
}
