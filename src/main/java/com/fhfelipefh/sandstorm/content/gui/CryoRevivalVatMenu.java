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

public class CryoRevivalVatMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 6;
    public static final int CONTAINER_SIZE = 3;

    private final Container container;
    private final ContainerData data;

    public CryoRevivalVatMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.CRYO_REVIVAL_VAT_MENU, syncId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public CryoRevivalVatMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.CRYO_REVIVAL_VAT_MENU, syncId, playerInventory, container, data);
    }

    public CryoRevivalVatMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 38, 35));
        this.addSlot(new Slot(container, 1, 76, 55));
        this.addSlot(new Slot(container, 2, 132, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int hotbar = 0; hotbar < 9; ++hotbar) {
            this.addSlot(new Slot(playerInventory, hotbar, 8 + hotbar * 18, 142));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        int max = this.data.get(1);
        return max <= 0 ? 100 : max;
    }

    public int getProgressScaled(int pixels) {
        int progress = getProgress();
        int max = getMaxProgress();
        return (progress * pixels) / max;
    }

    public int getFluidAmount() {
        return this.data.get(2);
    }

    public int getMaxFluid() {
        int max = this.data.get(3);
        return max <= 0 ? 4000 : max;
    }

    public int getFluidScaled(int pixels) {
        int fluid = getFluidAmount();
        int max = getMaxFluid();
        if (fluid <= 0 || max <= 0) {
            return 0;
        }
        return Math.min(pixels, (fluid * pixels) / max);
    }

    public int getStatus() {
        return this.data.get(4);
    }

    public int getTemperature() {
        return this.data.get(5);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();

            if (invSlot < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(originalStack, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(originalStack, 0, 2, false)) {
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
}

