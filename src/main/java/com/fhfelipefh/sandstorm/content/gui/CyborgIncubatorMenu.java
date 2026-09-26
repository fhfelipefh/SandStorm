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

public class CyborgIncubatorMenu extends AbstractContainerMenu implements MachineMenu {
    private final Container container;
    private final ContainerData data;

    public CyborgIncubatorMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.CYBORG_INCUBATOR_MENU, syncId, playerInventory, new SimpleContainer(6), new SimpleContainerData(8));
    }

    public CyborgIncubatorMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.CYBORG_INCUBATOR_MENU, syncId, playerInventory, container, data);
    }

    public CyborgIncubatorMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 6);
        checkContainerDataCount(data, 8);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 52, 24));
        this.addSlot(new Slot(container, 1, 52, 46));
        this.addSlot(new Slot(container, 2, 72, 24));
        this.addSlot(new Slot(container, 3, 72, 46));
        this.addSlot(new Slot(container, 4, 26, 35));
        this.addSlot(new Slot(container, 5, 150, 35) {
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
        return this.data.get(2);
    }

    @Override
    public int getMaxEnergy() {
        return this.data.get(3);
    }

    public int getFluidAmount() {
        return this.data.get(4);
    }

    public int getMaxFluid() {
        return this.data.get(5);
    }

    public int getCurrentStage() {
        return this.data.get(6);
    }

    public int getTissueCompatibility() {
        return this.data.get(7);
    }

    @Override
    public boolean isWptConnected() {
        return getEnergy() > 0;
    }

    @Override
    public int getProgress() {
        return this.data.get(0);
    }

    @Override
    public int getMaxProgress() {
        return this.data.get(1);
    }

    @Override
    public boolean isProcessing() {
        return getProgress() > 0 && getCurrentStage() >= 4;
    }

    @Override
    public int getProgressScaled(int pixels) {
        int max = getMaxProgress();
        if (max <= 0) {
            return 0;
        }
        return Math.min(pixels, (int) ((long) getProgress() * pixels / max));
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
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            if (index == 5) {
                if (!this.moveItemStackTo(stackInSlot, 6, 42, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemStack);
            } else if (index < 5) {
                if (!this.moveItemStackTo(stackInSlot, 6, 42, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stackInSlot.is(SandStormItems.BIOMECHANICAL_CHASSIS_FRAME) && this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                } else if (stackInSlot.is(SandStormItems.SYNTHETIC_MYOMER_BUNDLE) && this.moveItemStackTo(stackInSlot, 1, 2, false)) {
                } else if (stackInSlot.is(SandStormItems.BIO_NEURAL_CORE) && this.moveItemStackTo(stackInSlot, 2, 3, false)) {
                } else if ((stackInSlot.is(SandStormItems.BIO_COOLANT_CANISTER) || stackInSlot.is(SandStormItems.OSMOLYTE_GLYCEROL)
                        || stackInSlot.is(Items.WATER_BUCKET) || stackInSlot.is(SandStormItems.POTABLE_WATER_BOTTLE))
                        && this.moveItemStackTo(stackInSlot, 3, 4, false)) {
                } else if (BaseMachineBlockEntity.getFuelEnergy(stackInSlot) > 0 && this.moveItemStackTo(stackInSlot, 4, 5, false)) {
                } else if (index < 33) {
                    if (!this.moveItemStackTo(stackInSlot, 33, 42, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 42 && !this.moveItemStackTo(stackInSlot, 6, 33, false)) {
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
