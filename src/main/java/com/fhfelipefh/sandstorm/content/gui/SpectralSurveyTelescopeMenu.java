package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.SpectralSurveyTelescopeBlockEntity;
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

public class SpectralSurveyTelescopeMenu extends AbstractContainerMenu {
    public static final int FILTER_OPTICAL = 0;
    public static final int FILTER_INFRARED = 1;
    public static final int FILTER_RADIO_XRAY = 2;

    public static final int TARGET_ANDROMEDA = 0;
    public static final int TARGET_CRAB_NEBULA = 1;
    public static final int TARGET_KEPLER_EXOPLANET = 2;
    public static final int TARGET_CYGNUS_PULSAR = 3;
    public static final int TARGET_SOLAR_CORONA = 4;

    public static final int BUTTON_CYCLE_TARGET = 0;
    public static final int BUTTON_FILTER_OPTICAL = 1;
    public static final int BUTTON_FILTER_INFRARED = 2;
    public static final int BUTTON_FILTER_RADIO = 3;
    public static final int BUTTON_TRIGGER_SCAN = 4;

    public static final int OPTICAL_SLOT = 0;
    public static final int PLAYER_INVENTORY_START = 1;
    public static final int PLAYER_INVENTORY_END = 37;

    private final Container container;
    private final ContainerData data;
    private final SpectralSurveyTelescopeBlockEntity blockEntity;

    public SpectralSurveyTelescopeMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.SPECTRAL_SURVEY_TELESCOPE_MENU, syncId, playerInventory, null, new SimpleContainer(1), new SimpleContainerData(8));
    }

    public SpectralSurveyTelescopeMenu(int syncId, Inventory playerInventory, SpectralSurveyTelescopeBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.SPECTRAL_SURVEY_TELESCOPE_MENU, syncId, playerInventory, blockEntity, blockEntity, data);
    }

    public SpectralSurveyTelescopeMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, SpectralSurveyTelescopeBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerDataCount(data, 8);
        this.blockEntity = blockEntity;
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 198, 88));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 35 + col * 18, 122 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 35 + col * 18, 180));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.blockEntity != null) {
            return this.blockEntity.handleMenuButton(id);
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            if (slotIndex == OPTICAL_SLOT) {
                if (!this.moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, true)) {
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

    public int getStoredEnergy() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public int getActiveFilter() {
        return this.data.get(2);
    }

    public int getCelestialTarget() {
        return this.data.get(3);
    }

    public int getScanProgress() {
        return this.data.get(4);
    }

    public boolean hasSkyAccess() {
        return this.data.get(5) == 1;
    }

    public int getStormThreat() {
        return this.data.get(6);
    }

    public int getSecondsToStorm() {
        return this.data.get(7);
    }
}
