package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSpecialty;
import com.fhfelipefh.sandstorm.content.item.CyborgUpgradeItem;
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

public class CyborgTelemetryMenu extends AbstractContainerMenu {
    public static final int CYBORG_SLOTS = 18;
    public static final int PLAYER_INVENTORY_SLOTS = 36;
    public static final int TOTAL_SLOTS = CYBORG_SLOTS + PLAYER_INVENTORY_SLOTS;

    private final Container container;
    private final ContainerData data;

    public CyborgTelemetryMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.CYBORG_TELEMETRY_MENU, syncId, playerInventory, new SimpleContainer(CYBORG_SLOTS), new SimpleContainerData(9));
    }

    public CyborgTelemetryMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.CYBORG_TELEMETRY_MENU, syncId, playerInventory, container, data);
    }

    public CyborgTelemetryMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, CYBORG_SLOTS);
        checkContainerDataCount(data, 9);
        this.container = container;
        this.data = data;

        for (int row = 0; row < 2; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(container, col + row * 9, 8 + col * 18, 54 + row * 18));
            }
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 162));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id <= 3) {
            this.data.set(5, id);
            return true;
        }
        return false;
    }

    public int getEnergy() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public int getCoolant() {
        return this.data.get(2);
    }

    public int getMaxCoolant() {
        return this.data.get(3);
    }

    public int getIntegrity() {
        return this.data.get(4);
    }

    public CyborgRoutine getRoutine() {
        return CyborgRoutine.fromOrdinal(this.data.get(5));
    }

    public int getVisorState() {
        return this.data.get(6);
    }

    public int getVisorColor() {
        return getVisorState();
    }

    public CyborgSpecialty getSpecialty() {
        return CyborgSpecialty.fromOrdinal(this.data.get(7));
    }

    public int getUpgradesMask() {
        return this.data.get(8);
    }

    public boolean hasUpgrade(CyborgUpgradeItem.CyborgUpgradeType type) {
        return (getUpgradesMask() & type.getBitmask()) != 0;
    }

    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        if (max <= 0) {
            return 0;
        }
        return Math.min(pixels, (int) ((long) getEnergy() * pixels / max));
    }

    public int getCoolantScaled(int pixels) {
        int max = getMaxCoolant();
        if (max <= 0) {
            return 0;
        }
        return Math.min(pixels, (int) ((long) getCoolant() * pixels / max));
    }

    public int getIntegrityScaled(int pixels) {
        return Math.min(pixels, getIntegrity() * pixels / 100);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            if (index < CYBORG_SLOTS) {
                if (!this.moveItemStackTo(stackInSlot, CYBORG_SLOTS, TOTAL_SLOTS, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, CYBORG_SLOTS, false)) {
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
