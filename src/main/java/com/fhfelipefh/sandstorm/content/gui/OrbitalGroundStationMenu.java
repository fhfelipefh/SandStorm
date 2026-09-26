package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.OrbitalGroundStationBlockEntity;
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

public class OrbitalGroundStationMenu extends AbstractContainerMenu {
    public static final int TARGET_SLOT = 0;
    public static final int PLAYER_INVENTORY_START = 1;
    public static final int PLAYER_INVENTORY_END = 37;

    private final Container container;
    private final ContainerData data;
    private final OrbitalGroundStationBlockEntity blockEntity;

    public OrbitalGroundStationMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.ORBITAL_GROUND_STATION_MENU, syncId, playerInventory, null, new SimpleContainer(1), new SimpleContainerData(8));
    }

    public OrbitalGroundStationMenu(int syncId, Inventory playerInventory, OrbitalGroundStationBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.ORBITAL_GROUND_STATION_MENU, syncId, playerInventory, blockEntity, blockEntity, data);
    }

    public OrbitalGroundStationMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, OrbitalGroundStationBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerDataCount(data, 8);
        this.blockEntity = blockEntity;
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 152, 58));

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
        return this.container.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && this.blockEntity != null) {
            BlockPos targetPos = player.blockPosition();
            return this.blockEntity.triggerKineticStrike(targetPos, player);
        }
        return false;
    }

    public int getStoredEnergy() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public int getSatelliteCount() {
        return this.data.get(2);
    }

    public boolean isWeatherActive() {
        return this.data.get(3) == 1;
    }

    public boolean isSolarActive() {
        return this.data.get(4) == 1;
    }

    public boolean isSarActive() {
        return this.data.get(5) == 1;
    }

    public boolean isLanceActive() {
        return this.data.get(6) == 1;
    }

    public int getSecondsToStorm() {
        return this.data.get(7);
    }

    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        if (max <= 0) {
            return 0;
        }
        return Math.min(pixels, (int) ((long) getStoredEnergy() * pixels / max));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            if (index == TARGET_SLOT) {
                if (!this.moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, TARGET_SLOT, TARGET_SLOT + 1, false)) {
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
