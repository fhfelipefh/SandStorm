package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.KineticRailgunBlockEntity;
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

public class KineticRailgunMenu extends AbstractContainerMenu {
    public static final int AMMO_SLOTS_COUNT = 9;

    private final KineticRailgunBlockEntity blockEntity;
    private final Container container;
    private final ContainerData data;

    public KineticRailgunMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.KINETIC_RAILGUN_MENU, syncId, playerInventory, null, new SimpleContainer(AMMO_SLOTS_COUNT), new SimpleContainerData(4));
    }

    public KineticRailgunMenu(int syncId, Inventory playerInventory, KineticRailgunBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.KINETIC_RAILGUN_MENU, syncId, playerInventory, blockEntity, blockEntity, data);
    }

    public KineticRailgunMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, KineticRailgunBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerDataCount(data, 4);
        this.blockEntity = blockEntity;
        this.container = container;
        this.data = data;

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new Slot(container, col + row * 3, 62 + col * 18, 18 + row * 18));
            }
        }

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

    public int getStoredEnergy() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public int getCooldown() {
        return this.data.get(2);
    }

    public int getTotalShotsFired() {
        return this.data.get(3);
    }

    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        if (max <= 0) {
            return 0;
        }
        return (int) ((long) getStoredEnergy() * pixels / max);
    }

    public int getCooldownScaled(int pixels) {
        int cd = getCooldown();
        return cd > 0 ? (60 - cd) * pixels / 60 : pixels;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            int containerSlots = AMMO_SLOTS_COUNT;
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

    public KineticRailgunBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
