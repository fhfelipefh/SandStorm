package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.storage.QuantumStorageCartridgeItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class QuantumDiskDriveMenu extends AbstractContainerMenu {

    private final Container container;

    public QuantumDiskDriveMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(8));
    }

    public QuantumDiskDriveMenu(int syncId, Inventory playerInventory, Container container) {
        super(SandStormMenus.QUANTUM_DISK_DRIVE_MENU, syncId);
        checkContainerSize(container, 8);
        this.container = container;

        for (int i = 0; i < 8; i++) {
            int col = i % 4;
            int row = i / 4;
            this.addSlot(new Slot(container, i, 53 + col * 18, 20 + row * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() instanceof QuantumStorageCartridgeItem;
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (invSlot < 8) {
                if (!this.moveItemStackTo(itemstack1, 8, 44, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.getItem() instanceof QuantumStorageCartridgeItem) {
                    if (!this.moveItemStackTo(itemstack1, 0, 8, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (invSlot < 35) {
                    if (!this.moveItemStackTo(itemstack1, 35, 44, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 8, 35, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public Container getContainer() {
        return this.container;
    }
}
