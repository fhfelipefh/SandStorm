package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.PlasmaShieldGeneratorBlockEntity;
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

public class PlasmaShieldMenu extends AbstractContainerMenu {
    public static final int TOROID_SLOT = 0;

    private final PlasmaShieldGeneratorBlockEntity blockEntity;
    private final Container container;
    private final ContainerData data;

    public PlasmaShieldMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.PLASMA_SHIELD_MENU, syncId, playerInventory, null, new SimpleContainer(1), new SimpleContainerData(5));
    }

    public PlasmaShieldMenu(int syncId, Inventory playerInventory, PlasmaShieldGeneratorBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.PLASMA_SHIELD_MENU, syncId, playerInventory, blockEntity, blockEntity, data);
    }

    public PlasmaShieldMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, PlasmaShieldGeneratorBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerDataCount(data, 5);
        this.blockEntity = blockEntity;
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, TOROID_SLOT, 152, 40));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.blockEntity != null && this.blockEntity.getLevel() != null) {
            return this.blockEntity.getLevel().getBlockEntity(this.blockEntity.getBlockPos()) == this.blockEntity
                    && player.distanceToSqr(this.blockEntity.getBlockPos().getX() + 0.5, this.blockEntity.getBlockPos().getY() + 0.5, this.blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
        }
        return this.container.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.blockEntity != null) {
            if (id == 0) {
                this.blockEntity.toggleShield();
                return true;
            }
            if (id == 1) {
                this.blockEntity.cycleRadius();
                return true;
            }
        }
        return false;
    }

    public int getStoredEnergy() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public boolean isShieldActive() {
        return this.data.get(2) == 1;
    }

    public int getShieldRadius() {
        return this.data.get(3);
    }

    public int getThreatCount() {
        return this.data.get(4);
    }

    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        if (max <= 0) {
            return 0;
        }
        return (int) ((long) getStoredEnergy() * pixels / max);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            int containerSlots = 1;
            int totalSlots = this.slots.size();

            if (index < containerSlots) {
                if (!this.moveItemStackTo(stackInSlot, containerSlots, totalSlots, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    public PlasmaShieldGeneratorBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
