package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.HoloTacticalSpireBlockEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSwarmManager;
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

public class HoloTacticalSpireMenu extends AbstractContainerMenu {
    public static final int PROBE_SLOT = 0;
    public static final int PLAYER_INVENTORY_START = 1;
    public static final int PLAYER_INVENTORY_END = 37;

    private final HoloTacticalSpireBlockEntity blockEntity;
    private final Container container;
    private final ContainerData data;

    public HoloTacticalSpireMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.HOLO_TACTICAL_SPIRE_MENU, syncId, playerInventory, null, new SimpleContainer(1), new SimpleContainerData(5));
    }

    public HoloTacticalSpireMenu(int syncId, Inventory playerInventory, HoloTacticalSpireBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.HOLO_TACTICAL_SPIRE_MENU, syncId, playerInventory, blockEntity, blockEntity, data);
    }

    public HoloTacticalSpireMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, HoloTacticalSpireBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerDataCount(data, 5);
        this.blockEntity = blockEntity;
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 152, 46));

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
        return this.container.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id <= 3) {
            this.data.set(2, id);
            CyborgSwarmManager.getInstance().setGlobalTacticalOrder(id);
            if (this.blockEntity != null) {
                this.blockEntity.setTacticalOrder(id);
            }
            return true;
        }
        return false;
    }

    public int getStoredEnergy() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public int getActiveTacticalOrder() {
        return this.data.get(2);
    }

    public int getConnectedCyborgsCount() {
        return this.data.get(3);
    }

    public boolean isSeismicThreat() {
        return this.data.get(4) == 1;
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
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            if (index == PROBE_SLOT) {
                if (!this.moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, PROBE_SLOT, PROBE_SLOT + 1, false)) {
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
