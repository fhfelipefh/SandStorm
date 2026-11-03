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

public class MaterialDeconstructorMenu extends AbstractContainerMenu implements MachineMenu {
    private final Container container;
    private final ContainerData data;

    public MaterialDeconstructorMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.MATERIAL_DECONSTRUCTOR_MENU, syncId, playerInventory, new SimpleContainer(3), new SimpleContainerData(6));
    }

    public MaterialDeconstructorMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.MATERIAL_DECONSTRUCTOR_MENU, syncId, playerInventory, container, data);
    }

    public MaterialDeconstructorMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 3);
        checkContainerDataCount(data, 6);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 44, 35));
        this.addSlot(new Slot(container, 1, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 2, 26, 53));

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

    @Override
    public int getEnergy() {
        return data.get(0);
    }

    @Override
    public int getMaxEnergy() {
        return data.get(1);
    }

    @Override
    public int getProgress() {
        return data.get(2);
    }

    @Override
    public int getMaxProgress() {
        return data.get(3);
    }

    @Override
    public boolean isWptConnected() {
        return data.get(4) == 1;
    }

    @Override
    public boolean isProcessing() {
        return data.get(2) > 0;
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
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot == 1) {
                if (!this.moveItemStackTo(originalStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(originalStack, newStack);
            } else if (invSlot < 3) {
                if (!this.moveItemStackTo(originalStack, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(originalStack, 0, 1, false)
                        && !this.moveItemStackTo(originalStack, 2, 3, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (originalStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, originalStack);
        }
        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}

