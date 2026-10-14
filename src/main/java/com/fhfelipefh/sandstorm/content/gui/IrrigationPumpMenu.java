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

public class IrrigationPumpMenu extends AbstractContainerMenu implements MachineMenu {
    public static final int SLOT_WATER_IN = 0;
    public static final int SLOT_WATER_OUT = 1;
    public static final int SLOT_NUTRIENTS = 2;
    public static final int SLOT_BATTERY = 3;

    private final Container container;
    private final ContainerData data;

    public IrrigationPumpMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.IRRIGATION_PUMP_MENU, syncId, playerInventory, new SimpleContainer(4), new SimpleContainerData(9));
    }

    public IrrigationPumpMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.IRRIGATION_PUMP_MENU, syncId, playerInventory, container, data);
    }

    public IrrigationPumpMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 4);
        checkContainerDataCount(data, 9);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, SLOT_WATER_IN, 28, 22));
        this.addSlot(new Slot(container, SLOT_WATER_OUT, 28, 50) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, SLOT_NUTRIENTS, 132, 22));
        this.addSlot(new Slot(container, SLOT_BATTERY, 132, 50));

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
    public int getEnergy() {
        return this.data.get(0);
    }

    @Override
    public int getMaxEnergy() {
        return Math.max(1, this.data.get(1));
    }

    public int getWaterAmount() {
        return this.data.get(2);
    }

    public int getMaxWater() {
        return Math.max(1, this.data.get(3));
    }

    public int getNutrients() {
        return this.data.get(4);
    }

    public int getMaxNutrients() {
        return Math.max(1, this.data.get(5));
    }

    public int getConnectedSprinklers() {
        return this.data.get(6);
    }

    public int getPressureRadius() {
        return this.data.get(7);
    }

    public boolean isActive() {
        return this.data.get(8) == 1;
    }

    @Override
    public int getProgress() {
        return 0;
    }

    @Override
    public int getMaxProgress() {
        return 100;
    }

    @Override
    public boolean isWptConnected() {
        return false;
    }

    @Override
    public boolean isProcessing() {
        return isActive();
    }

    @Override
    public int getEnergyScaled(int pixels) {
        int e = getEnergy();
        int max = getMaxEnergy();
        return (int) ((long) e * pixels / max);
    }

    @Override
    public int getProgressScaled(int pixels) {
        return 0;
    }

    public int getWaterScaled(int pixels) {
        int w = getWaterAmount();
        int max = getMaxWater();
        return (int) ((long) w * pixels / max);
    }

    public int getNutrientsScaled(int pixels) {
        int n = getNutrients();
        int max = getMaxNutrients();
        return (int) ((long) n * pixels / max);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (invSlot < 4) {
                if (!this.moveItemStackTo(stackInSlot, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, 4, false)) {
                    if (invSlot < 31) {
                        if (!this.moveItemStackTo(stackInSlot, 31, 40, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (invSlot < 40 && !this.moveItemStackTo(stackInSlot, 4, 31, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }
        return itemstack;
    }
}
