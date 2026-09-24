package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.megastructure.MegastructureBlueprint;
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

public class MegastructureConstructorMenu extends AbstractContainerMenu implements MachineMenu {
    private final Container container;
    private final ContainerData data;

    public MegastructureConstructorMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.MEGASTRUCTURE_CONSTRUCTOR_MENU, syncId, playerInventory, new SimpleContainer(18), new SimpleContainerData(11));
    }

    public MegastructureConstructorMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.MEGASTRUCTURE_CONSTRUCTOR_MENU, syncId, playerInventory, container, data);
    }

    public MegastructureConstructorMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 18);
        checkContainerDataCount(data, 11);
        this.container = container;
        this.data = data;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(container, col + row * 9, 8 + col * 18, 20 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 68 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 126));
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
        return data.get(5) == 2;
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
    public int getProgressScaled(int pixels) {
        int max = getTotalPlacements();
        if (max <= 0) {
            return 0;
        }
        return Math.min(pixels, (int) ((long) getPlacementIndex() * pixels / max));
    }

    public int getConstructedBlocks() {
        return getPlacementIndex();
    }

    public int getTotalBlocks() {
        return getTotalPlacements();
    }

    public int getCompletionPercentage() {
        int total = getTotalPlacements();
        if (total <= 0) {
            return 0;
        }
        return Math.min(100, (int) ((getPlacementIndex() * 100.0) / total));
    }

    public String getBlueprintName() {
        return MegastructureBlueprint.byIndex(getBlueprintIndex()).getDisplayName();
    }

    public boolean isBuilding() {
        return getBuildState() == 2;
    }

    public boolean isDone() {
        return getBuildState() == 4;
    }

    public boolean isPausedStorm() {
        return getBuildState() == 3;
    }

    public boolean getLaserActive() {
        return isBuilding() && getEnergy() >= 15;
    }

    public int getBuildState() {
        return data.get(5);
    }

    public int getBlueprintIndex() {
        return data.get(6);
    }

    public int getPlacementIndex() {
        return data.get(7);
    }

    public int getTotalPlacements() {
        return data.get(8);
    }

    public int getCurrentLayerY() {
        return data.get(9);
    }

    public int getBuildSpeedMode() {
        return data.get(10);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            if (slotIndex < 18) {
                if (!this.moveItemStackTo(stackInSlot, 18, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, 18, false)) {
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
}
