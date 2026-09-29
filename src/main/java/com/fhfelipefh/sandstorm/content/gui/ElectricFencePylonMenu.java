package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.ElectricFencePylonBlockEntity;
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

public class ElectricFencePylonMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public ElectricFencePylonMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.ELECTRIC_FENCE_PYLON_MENU, syncId, playerInventory, new SimpleContainer(1), new SimpleContainerData(6));
    }

    public ElectricFencePylonMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.ELECTRIC_FENCE_PYLON_MENU, syncId, playerInventory, container, data);
    }

    public ElectricFencePylonMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 1);
        checkContainerDataCount(data, 6);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 80, 42) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return BaseMachineBlockEntity.getFuelEnergy(stack) > 0;
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
        return ElectricFencePylonBlockEntity.MAX_ENERGY;
    }

    public float getChargePercentage() {
        return (float) getStoredEnergy() / (float) Math.max(1, getMaxEnergy());
    }

    public int getMode() {
        return data.get(2);
    }

    public boolean isArmed() {
        return data.get(3) == 1;
    }

    public boolean isConnected() {
        return data.get(4) == 1;
    }

    public int getConnectedCount() {
        return data.get(5);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && this.container instanceof ElectricFencePylonBlockEntity pylon) {
            pylon.cycleMode();
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

            if (slotIndex == 0) {
                if (!this.moveItemStackTo(stackInSlot, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemStack);
            } else {
                if (BaseMachineBlockEntity.getFuelEnergy(stackInSlot) > 0) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 1 && slotIndex < 28) {
                    if (!this.moveItemStackTo(stackInSlot, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 28 && slotIndex < 37) {
                    if (!this.moveItemStackTo(stackInSlot, 1, 28, false)) {
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
