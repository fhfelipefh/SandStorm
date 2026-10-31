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
import net.minecraft.world.item.Items;

public class StellarSingularityReactorMenu extends AbstractContainerMenu implements MachineMenu {
    private final Container container;
    private final ContainerData data;

    public StellarSingularityReactorMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.STELLAR_SINGULARITY_REACTOR_MENU, syncId, playerInventory, new SimpleContainer(1), new SimpleContainerData(9));
    }

    public StellarSingularityReactorMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.STELLAR_SINGULARITY_REACTOR_MENU, syncId, playerInventory, container, data);
    }

    public StellarSingularityReactorMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 1);
        checkContainerDataCount(data, 9);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 80, 45) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.NETHER_STAR);
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

    public long getStoredEnergyLong() {
        return (((long) data.get(1)) << 16) | (data.get(0) & 0xFFFFL);
    }

    public long getMaxEnergyLong() {
        return (((long) data.get(3)) << 16) | (data.get(2) & 0xFFFFL);
    }

    public int getBurnTime() {
        return (data.get(5) << 16) | (data.get(4) & 0xFFFF);
    }

    public int getMaxBurnTime() {
        return (data.get(7) << 16) | (data.get(6) & 0xFFFF);
    }

    @Override
    public int getEnergy() {
        return (int) Math.min(Integer.MAX_VALUE, getStoredEnergyLong());
    }

    @Override
    public int getMaxEnergy() {
        return (int) Math.min(Integer.MAX_VALUE, getMaxEnergyLong());
    }

    @Override
    public int getProgress() {
        return getBurnTime();
    }

    @Override
    public int getMaxProgress() {
        return getMaxBurnTime();
    }

    @Override
    public boolean isWptConnected() {
        return true;
    }

    @Override
    public boolean isProcessing() {
        return data.get(8) == 1;
    }

    @Override
    public int getEnergyScaled(int pixels) {
        long max = getMaxEnergyLong();
        return max > 0 ? (int) ((getStoredEnergyLong() * pixels) / max) : 0;
    }

    @Override
    public int getProgressScaled(int pixels) {
        int max = getMaxBurnTime();
        return max > 0 ? (getBurnTime() * pixels) / max : 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();

            if (index == 0) {
                if (!this.moveItemStackTo(originalStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(originalStack, newStack);
            } else {
                if (originalStack.is(Items.NETHER_STAR)) {
                    if (!this.moveItemStackTo(originalStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 1 && index < 28) {
                    if (!this.moveItemStackTo(originalStack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 28 && index < 37 && !this.moveItemStackTo(originalStack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
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
}

