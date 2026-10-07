package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.CryogenicAtmosphericChillerBlockEntity;
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

public class CryogenicAtmosphericChillerMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 8;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = 36;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int MAIN_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_SLOT_COUNT - HOTBAR_SLOT_COUNT;

    private final CryogenicAtmosphericChillerBlockEntity blockEntity;
    private final Container container;
    private final ContainerData data;

    public CryogenicAtmosphericChillerMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.CRYOGENIC_ATMOSPHERIC_CHILLER_MENU, syncId, playerInventory, null, new SimpleContainer(0), new SimpleContainerData(DATA_COUNT));
    }

    public CryogenicAtmosphericChillerMenu(int syncId, Inventory playerInventory, CryogenicAtmosphericChillerBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.CRYOGENIC_ATMOSPHERIC_CHILLER_MENU, syncId, playerInventory, blockEntity, new SimpleContainer(0), data);
    }

    private CryogenicAtmosphericChillerMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, CryogenicAtmosphericChillerBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerDataCount(data, DATA_COUNT);
        this.blockEntity = blockEntity;
        this.container = container;
        this.data = data;

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

    public int getEnergy() {
        return combineUnsignedShorts(0);
    }

    public int getMaxEnergy() {
        return combineUnsignedShorts(2);
    }

    public int getEnergyCostPerTick() {
        return combineUnsignedShorts(4);
    }

    public int getRadius() {
        return this.data.get(6);
    }

    public boolean isActive() {
        return this.data.get(7) == 1;
    }

    public int getEnergyScaled(int pixels) {
        int maxEnergy = getMaxEnergy();
        if (maxEnergy <= 0) {
            return 0;
        }
        return (int) ((long) getEnergy() * pixels / maxEnergy);
    }

    private int combineUnsignedShorts(int startIndex) {
        return (this.data.get(startIndex) & 0xFFFF) | ((this.data.get(startIndex + 1) & 0xFFFF) << 16);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            if (index < MAIN_INVENTORY_SLOT_COUNT) {
                if (!this.moveItemStackTo(stackInSlot, MAIN_INVENTORY_SLOT_COUNT, PLAYER_INVENTORY_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, MAIN_INVENTORY_SLOT_COUNT, false)) {
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
