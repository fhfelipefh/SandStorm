package com.fhfelipefh.sandstorm.content.gui;

import net.minecraft.core.BlockPos;
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

public class AutonomousSonicTurretMenu extends AbstractContainerMenu implements MachineMenu {
    private final Container container;
    private final ContainerData data;

    public AutonomousSonicTurretMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.AUTONOMOUS_SONIC_TURRET_MENU, syncId, playerInventory, new SimpleContainer(1), new SimpleContainerData(11));
    }

    public AutonomousSonicTurretMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.AUTONOMOUS_SONIC_TURRET_MENU, syncId, playerInventory, container, data);
    }

    public AutonomousSonicTurretMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 1);
        checkContainerDataCount(data, 11);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, -2000, -2000));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, -2000, -2000));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, -2000, -2000));
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
    public int getEnergyScaled(int pixels) {
        int energy = getEnergy();
        int max = getMaxEnergy();
        return max != 0 && energy != 0 ? energy * pixels / max : 0;
    }

    public int getCooldown() {
        return data.get(2);
    }

    public int getMaxCooldown() {
        return data.get(3);
    }

    @Override
    public boolean isWptConnected() {
        return data.get(4) == 1;
    }

    public int getFilterMode() {
        return data.get(5);
    }

    public int getTargetingStrategy() {
        return data.get(6);
    }

    public int getTargetCount() {
        return data.get(7);
    }

    public BlockPos getTurretPos() {
        return new BlockPos(data.get(8), data.get(9), data.get(10));
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
    public int getProgressScaled(int pixels) {
        return 0;
    }

    @Override
    public boolean isProcessing() {
        return data.get(2) > 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            if (slotIndex == 0) {
                if (!this.moveItemStackTo(stackInSlot, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                return ItemStack.EMPTY;
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

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}
