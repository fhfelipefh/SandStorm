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

public class ThermalGeneratorMenu extends AbstractContainerMenu implements MachineMenu {
    private final Container container;
    private final ContainerData data;

    public ThermalGeneratorMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.THERMAL_GENERATOR_MENU, syncId, playerInventory, new SimpleContainer(2), new SimpleContainerData(6));
    }

    public ThermalGeneratorMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.THERMAL_GENERATOR_MENU, syncId, playerInventory, container, data);
    }

    public ThermalGeneratorMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 2);
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
        return data.get(4) == 1;
    }

    public int getLavaAmount() {
        return data.get(5);
    }

    public int getMaxLava() {
        return 4000;
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

    public int getLavaScaled(int pixels) {
        return (getLavaAmount() * pixels) / getMaxLava();
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

            if (index == 1) {
                if (!this.moveItemStackTo(originalStack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(originalStack, newStack);
            } else if (index != 0) {
                if (originalStack.is(Items.LAVA_BUCKET)) {
                    if (!this.moveItemStackTo(originalStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 2 && index < 29) {
                    if (!this.moveItemStackTo(originalStack, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && index < 38 && !this.moveItemStackTo(originalStack, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 2, 38, false)) {
                return ItemStack.EMPTY;
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
