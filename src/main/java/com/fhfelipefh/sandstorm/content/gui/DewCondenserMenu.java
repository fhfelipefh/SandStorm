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

public class DewCondenserMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public DewCondenserMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.DEW_CONDENSER_MENU, syncId, playerInventory, new SimpleContainer(2), new SimpleContainerData(5));
    }

    public DewCondenserMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.DEW_CONDENSER_MENU, syncId, playerInventory, container, data);
    }

    public DewCondenserMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 2);
        checkContainerDataCount(data, 5);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 56, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.GLASS_BOTTLE) || stack.is(Items.BUCKET);
            }
        });

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
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public int getWater() {
        return this.data.get(0);
    }

    public int getMaxWater() {
        return this.data.get(1);
    }

    public int getProgress() {
        return this.data.get(2);
    }

    public int getMaxProgress() {
        return this.data.get(3);
    }

    public int getStatus() {
        return this.data.get(4);
    }

    public float getWaterPercentage() {
        int max = getMaxWater();
        return max > 0 ? (float) getWater() / (float) max : 0f;
    }

    public float getProgressPercentage() {
        int max = getMaxProgress();
        return max > 0 ? (float) getProgress() / (float) max : 0f;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            if (slotIndex == 0 || slotIndex == 1) {
                if (!this.moveItemStackTo(stackInSlot, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemStack);
            } else {
                if (stackInSlot.is(Items.GLASS_BOTTLE) || stackInSlot.is(Items.BUCKET)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 2 && slotIndex < 29) {
                    if (!this.moveItemStackTo(stackInSlot, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 29 && slotIndex < 38) {
                    if (!this.moveItemStackTo(stackInSlot, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return itemStack;
    }
}
