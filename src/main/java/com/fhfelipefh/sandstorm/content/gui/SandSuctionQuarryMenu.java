package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.SandSuctionQuarryBlockEntity;
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

public class SandSuctionQuarryMenu extends AbstractContainerMenu implements MachineMenu {
    public static final int CONTAINER_SIZE = 9;
    public static final int DATA_COUNT = 12;

    public static final int BUTTON_DEC_1 = 0;
    public static final int BUTTON_INC_1 = 1;
    public static final int BUTTON_DEC_8 = 2;
    public static final int BUTTON_INC_8 = 3;
    public static final int BUTTON_TOGGLE_PAUSE = 4;
    public static final int BUTTON_RESET_SCAN = 5;

    private final Container container;
    private final ContainerData data;
    private final SandSuctionQuarryBlockEntity blockEntity;

    public SandSuctionQuarryMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT), null);
    }

    public SandSuctionQuarryMenu(int syncId, Inventory playerInventory, Container container, ContainerData data, SandSuctionQuarryBlockEntity blockEntity) {
        this(SandStormMenus.SAND_SUCTION_QUARRY_MENU, syncId, playerInventory, container, data, blockEntity);
    }

    public SandSuctionQuarryMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data, SandSuctionQuarryBlockEntity blockEntity) {
        super(menuType, syncId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;
        this.blockEntity = blockEntity;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(container, col + row * 3, 106 + col * 18, 18 + row * 18));
            }
        }

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
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.blockEntity != null) {
            return this.blockEntity.handleMenuButton(id);
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            if (index < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(stackInSlot, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, CONTAINER_SIZE, false)) {
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
    public int getEnergy() {
        return (data.get(0) & 0xFFFF) | ((data.get(1) & 0xFFFF) << 16);
    }

    @Override
    public int getMaxEnergy() {
        return (data.get(2) & 0xFFFF) | ((data.get(3) & 0xFFFF) << 16);
    }

    @Override
    public int getProgress() {
        return data.get(5);
    }

    @Override
    public int getMaxProgress() {
        return 20;
    }

    @Override
    public boolean isWptConnected() {
        return data.get(11) == 1;
    }

    @Override
    public boolean isProcessing() {
        return data.get(7) == 1;
    }

    @Override
    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        int cur = getEnergy();
        return max > 0 && cur > 0 ? (int) (((long) cur * pixels) / max) : 0;
    }

    @Override
    public int getProgressScaled(int pixels) {
        int max = getMaxProgress();
        int cur = getProgress();
        return max > 0 && cur > 0 ? (cur * pixels) / max : 0;
    }

    public int getRadius() {
        return data.get(4);
    }

    public boolean isPaused() {
        return data.get(6) == 1;
    }

    public int getStatus() {
        return data.get(7);
    }

    public int getScanY() {
        return data.get(8) - 100;
    }

    public int getTotalExcavated() {
        return (data.get(9) & 0xFFFF) | ((data.get(10) & 0xFFFF) << 16);
    }
}

