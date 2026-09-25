package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
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

public class BioRegenerationPodMenu extends AbstractContainerMenu implements MachineMenu {
    private final Container container;
    private final ContainerData data;

    public BioRegenerationPodMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.BIO_REGENERATION_POD_MENU, syncId, playerInventory, new SimpleContainer(4), new SimpleContainerData(8));
    }

    public BioRegenerationPodMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.BIO_REGENERATION_POD_MENU, syncId, playerInventory, container, data);
    }

    public BioRegenerationPodMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 4);
        checkContainerDataCount(data, 8);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 26, 35));
        this.addSlot(new Slot(container, 1, 50, 35));
        this.addSlot(new Slot(container, 2, 8, 53));
        this.addSlot(new Slot(container, 3, 134, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
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
        return this.data.get(1);
    }

    public int getFluidAmount() {
        return this.data.get(2);
    }

    public int getMaxFluid() {
        return this.data.get(3);
    }

    @Override
    public boolean isWptConnected() {
        return this.data.get(4) == 1;
    }

    public boolean isOccupied() {
        return this.data.get(5) == 1;
    }

    public int getHeartRate() {
        return this.data.get(6);
    }

    public int getHealthPercent() {
        return this.data.get(7);
    }

    public int getFluidScaled(int pixels) {
        int max = getMaxFluid();
        if (max <= 0) {
            return 0;
        }
        return Math.min(pixels, (int) ((long) getFluidAmount() * pixels / max));
    }

    @Override
    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        if (max <= 0) {
            return 0;
        }
        return Math.min(pixels, (int) ((long) getEnergy() * pixels / max));
    }

    @Override
    public int getProgress() {
        return isOccupied() ? 100 : 0;
    }

    @Override
    public int getMaxProgress() {
        return 100;
    }

    @Override
    public boolean isProcessing() {
        return isOccupied();
    }

    @Override
    public int getProgressScaled(int pixels) {
        return isOccupied() ? pixels : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            if (index == 3) {
                if (!this.moveItemStackTo(stackInSlot, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemStack);
            } else if (index < 3) {
                if (!this.moveItemStackTo(stackInSlot, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (BaseMachineBlockEntity.getFuelEnergy(stackInSlot) > 0 && this.moveItemStackTo(stackInSlot, 2, 3, false)) {
                } else if ((stackInSlot.is(SandStormItems.POTABLE_WATER_BOTTLE) || stackInSlot.is(SandStormItems.BRACKISH_WATER_BOTTLE) || stackInSlot.is(Items.WATER_BUCKET))
                        && this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                } else if ((stackInSlot.is(SandStormItems.CHITOSAN_EXTRACT) || stackInSlot.is(SandStormItems.TREHALOSE_SUGAR) || stackInSlot.is(SandStormItems.RADIOPROTECTIVE_MELANIN) || stackInSlot.is(SandStormItems.BIOFOAM_CARTRIDGE))
                        && this.moveItemStackTo(stackInSlot, 1, 2, false)) {
                } else if (index < 31) {
                    if (!this.moveItemStackTo(stackInSlot, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 40 && !this.moveItemStackTo(stackInSlot, 4, 31, false)) {
                    return ItemStack.EMPTY;
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
