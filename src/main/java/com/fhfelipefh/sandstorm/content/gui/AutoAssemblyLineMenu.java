package com.fhfelipefh.sandstorm.content.gui;

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

public class AutoAssemblyLineMenu extends AbstractContainerMenu implements MachineMenu {
    private final Container container;
    private final ContainerData data;

    public AutoAssemblyLineMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.AUTO_ASSEMBLY_LINE_MENU, syncId, playerInventory, new SimpleContainer(3), new SimpleContainerData(6));
    }

    public AutoAssemblyLineMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.AUTO_ASSEMBLY_LINE_MENU, syncId, playerInventory, container, data);
    }

    public AutoAssemblyLineMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 3);
        checkContainerDataCount(data, 6);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 44, 28));
        this.addSlot(new Slot(container, 1, 116, 28) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 2, 44, 50));

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

    public int getEnergy() {
        return data.get(0);
    }

    public int getMaxEnergy() {
        return data.get(1);
    }

    public int getProgress() {
        return data.get(2);
    }

    public int getMaxProgress() {
        return data.get(3);
    }

    public boolean isWptConnected() {
        return data.get(4) == 1;
    }

    public boolean isProcessing() {
        return data.get(5) == 1;
    }

    @Override
    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        return max > 0 ? (getEnergy() * pixels) / max : 0;
    }

    @Override
    public int getProgressScaled(int pixels) {
        int max = getMaxProgress();
        return max > 0 ? (getProgress() * pixels) / max : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < 3) {
                if (!this.moveItemStackTo(originalStack, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}
