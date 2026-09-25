package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.CyborgDockingStationBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CyborgDockingStationMenu extends AbstractContainerMenu {
    public static final int PLAYER_INVENTORY_START = 0;
    public static final int PLAYER_INVENTORY_END = 36;

    private final CyborgDockingStationBlockEntity blockEntity;
    private final ContainerData data;

    public CyborgDockingStationMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.CYBORG_DOCKING_STATION_MENU, syncId, playerInventory, null, new SimpleContainerData(6));
    }

    public CyborgDockingStationMenu(int syncId, Inventory playerInventory, CyborgDockingStationBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.CYBORG_DOCKING_STATION_MENU, syncId, playerInventory, blockEntity, data);
    }

    public CyborgDockingStationMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, CyborgDockingStationBlockEntity blockEntity, ContainerData data) {
        super(menuType, syncId);
        checkContainerDataCount(data, 6);
        this.blockEntity = blockEntity;
        this.data = data;

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 98 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 156));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.blockEntity != null && this.blockEntity.getLevel() != null) {
            return this.blockEntity.getLevel().getBlockEntity(this.blockEntity.getBlockPos()) == this.blockEntity
                    && player.distanceToSqr(this.blockEntity.getBlockPos().getX() + 0.5, this.blockEntity.getBlockPos().getY() + 0.5, this.blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
        }
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && this.blockEntity != null) {
            this.blockEntity.undockCyborg();
            return true;
        }
        return false;
    }

    public boolean isDocked() {
        return this.data.get(0) == 1;
    }

    public int getDockEnergy() {
        return this.data.get(1);
    }

    public int getMaxDockEnergy() {
        return this.data.get(2);
    }

    public int getCyborgEnergy() {
        return this.data.get(3);
    }

    public int getCyborgMaxEnergy() {
        return this.data.get(4);
    }

    public int getCyborgIntegrity() {
        return this.data.get(5);
    }

    public int getDockEnergyScaled(int pixels) {
        int max = getMaxDockEnergy();
        if (max <= 0) {
            return 0;
        }
        return Math.min(pixels, (int) ((long) getDockEnergy() * pixels / max));
    }

    public int getCyborgEnergyScaled(int pixels) {
        int max = getCyborgMaxEnergy();
        if (max <= 0) {
            return 0;
        }
        return Math.min(pixels, (int) ((long) getCyborgEnergy() * pixels / max));
    }

    public int getCyborgIntegrityScaled(int pixels) {
        return Math.min(pixels, getCyborgIntegrity() * pixels / 100);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            if (index < 27) {
                if (!this.moveItemStackTo(stackInSlot, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, 27, false)) {
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
