package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.QuantumSleeperPodBlockEntity;
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

public class QuantumSleeperMenu extends AbstractContainerMenu {
    public static final int CLONE_INV_SIZE = 41;
    public static final int NUTRIENT_SLOT = 41;
    public static final int TOTAL_CONTAINER_SLOTS = 42;

    private final QuantumSleeperPodBlockEntity blockEntity;
    private final Container container;
    private final ContainerData data;

    public QuantumSleeperMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.QUANTUM_SLEEPER_MENU, syncId, playerInventory, null, new SimpleContainer(TOTAL_CONTAINER_SLOTS), new SimpleContainerData(5));
    }

    public QuantumSleeperMenu(int syncId, Inventory playerInventory, QuantumSleeperPodBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.QUANTUM_SLEEPER_MENU, syncId, playerInventory, blockEntity, blockEntity, data);
    }

    public QuantumSleeperMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, QuantumSleeperPodBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerDataCount(data, 5);
        this.blockEntity = blockEntity;
        this.container = container;
        this.data = data;

        for (int col = 0; col < 4; ++col) {
            this.addSlot(new Slot(container, 36 + col, 8 + col * 18, 18));
        }
        this.addSlot(new Slot(container, 40, 80, 18));

        this.addSlot(new Slot(container, NUTRIENT_SLOT, 152, 18));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(container, col + row * 9, 8 + col * 18, 40 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(container, 27 + col, 8 + col * 18, 98));
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 122 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 180));
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
                return this.blockEntity.triggerCloneGestation(player);
            }
            if (id == 1) {
                return this.blockEntity.executeConsciousnessTransfer(player);
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

    public int getBioNutrients() {
        return this.data.get(2);
    }

    public boolean hasClone() {
        return this.data.get(3) == 1;
    }

    public int getConnectedPodsCount() {
        return this.data.get(4);
    }

    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        if (max <= 0) {
            return 0;
        }
        return (int) ((long) getStoredEnergy() * pixels / max);
    }

    public int getNutrientsScaled(int pixels) {
        return getBioNutrients() * pixels / 100;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            int containerSlots = TOTAL_CONTAINER_SLOTS;
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

    public QuantumSleeperPodBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
