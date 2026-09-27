package com.fhfelipefh.sandstorm.content.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GridMonitorConsoleMenu extends AbstractContainerMenu {
    private final ContainerData data;

    public GridMonitorConsoleMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.GRID_MONITOR_CONSOLE_MENU, syncId, playerInventory, new SimpleContainerData(12));
    }

    public GridMonitorConsoleMenu(int syncId, Inventory playerInventory, ContainerData data) {
        this(SandStormMenus.GRID_MONITOR_CONSOLE_MENU, syncId, playerInventory, data);
    }

    public GridMonitorConsoleMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, ContainerData data) {
        super(menuType, syncId);
        checkContainerDataCount(data, 12);
        this.data = data;

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
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public int getSolarCount() {
        return data.get(0);
    }

    public int getSolarGenRate() {
        return data.get(1);
    }

    public int getThermalCount() {
        return data.get(2);
    }

    public int getThermalGenRate() {
        return data.get(3);
    }

    public int getRelayCount() {
        return data.get(4);
    }

    public int getAccumulatorCount() {
        return data.get(5);
    }

    public int getTotalStoredEnergy() {
        return (data.get(6) & 0xFFFF) | ((data.get(7) & 0xFFFF) << 16);
    }

    public int getTotalCapacity() {
        return (data.get(8) & 0xFFFF) | ((data.get(9) & 0xFFFF) << 16);
    }

    public int getGridStatus() {
        return data.get(10);
    }

    public int getLocalCoverageCharge() {
        return data.get(11);
    }
}
