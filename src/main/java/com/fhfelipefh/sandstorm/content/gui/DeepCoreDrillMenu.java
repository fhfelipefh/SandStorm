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

public class DeepCoreDrillMenu extends AbstractContainerMenu implements MachineMenu {
    public static final int CONTAINER_SIZE = 8;
    public static final int DATA_COUNT = 8;

    private final Container container;
    private final ContainerData data;

    public DeepCoreDrillMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.DEEP_CORE_DRILL_MENU, syncId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public DeepCoreDrillMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;
        container.startOpen(playerInventory.player);

        addSlot(new Slot(container, 0, 44, 20));
        addSlot(new Slot(container, 1, 44, 56));

        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 3; c++) {
                addSlot(new Slot(container, 2 + r * 3 + c, 106 + c * 18, 24 + r * 22));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    @Override
    public boolean isWptConnected() {
        return getEnergy() > 0;
    }

    @Override
    public boolean isProcessing() {
        return isDrilling();
    }

    @Override
    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        return max > 0 ? (int) ((long) getEnergy() * pixels / max) : 0;
    }

    @Override
    public int getProgressScaled(int pixels) {
        int max = getMaxProgress();
        return max > 0 ? (int) ((long) getProgress() * pixels / max) : 0;
    }

    public int getEnergy() {
        return (data.get(0) & 0xFFFF) | ((data.get(1) & 0xFFFF) << 16);
    }

    public int getMaxEnergy() {
        return (data.get(2) & 0xFFFF) | ((data.get(3) & 0xFFFF) << 16);
    }

    public int getProgress() {
        return data.get(4);
    }

    public int getMaxProgress() {
        return data.get(5);
    }

    public int getFluidAmount() {
        return data.get(6);
    }

    public int getMaxFluid() {
        return 4000;
    }

    public int getFluidScaled(int pixels) {
        return getMaxFluid() > 0 ? (getFluidAmount() * pixels) / getMaxFluid() : 0;
    }

    public boolean isDrilling() {
        return data.get(7) == 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            if (index < CONTAINER_SIZE) {
                if (!moveItemStackTo(stackInSlot, CONTAINER_SIZE, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stackInSlot, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
