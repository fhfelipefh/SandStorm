package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.AmnioticIncubatorBlockEntity;
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

public class AmnioticIncubatorMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 14;
    public static final int BUTTON_CYCLE_SPECIES = 0;
    public static final int BUTTON_TOGGLE_AUTO = 1;
    public static final int BUTTON_CYCLE_QUOTA = 2;
    public static final int BUTTON_TOGGLE_START = 3;

    private final AmnioticIncubatorBlockEntity blockEntity;
    private final Container container;
    private final ContainerData data;

    public AmnioticIncubatorMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.AMNIOTIC_INCUBATOR_MENU, syncId, playerInventory, null, new SimpleContainer(AmnioticIncubatorBlockEntity.CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public AmnioticIncubatorMenu(int syncId, Inventory playerInventory, AmnioticIncubatorBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.AMNIOTIC_INCUBATOR_MENU, syncId, playerInventory, blockEntity, blockEntity, data);
    }

    public AmnioticIncubatorMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, AmnioticIncubatorBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, AmnioticIncubatorBlockEntity.CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.blockEntity = blockEntity;
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, AmnioticIncubatorBlockEntity.SLOT_WATER_IN, 24, 25));
        this.addSlot(new Slot(container, AmnioticIncubatorBlockEntity.SLOT_WATER_OUT, 24, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, AmnioticIncubatorBlockEntity.SLOT_BIOMASS, 50, 39));

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
            if (id == BUTTON_CYCLE_SPECIES) {
                this.blockEntity.cycleSpecies();
                return true;
            }
            if (id == BUTTON_TOGGLE_AUTO) {
                this.blockEntity.setAutoEcologicalMode(!this.blockEntity.isAutoEcologicalMode());
                return true;
            }
            if (id == BUTTON_CYCLE_QUOTA) {
                this.blockEntity.cycleTargetPopulationQuota();
                return true;
            }
            if (id == BUTTON_TOGGLE_START) {
                this.blockEntity.toggleManualIncubation();
                return true;
            }
        }
        return false;
    }

    public int getEnergy() {
        return (this.data.get(0) & 0xFFFF) | ((this.data.get(1) & 0xFFFF) << 16);
    }

    public int getMaxEnergy() {
        return (this.data.get(2) & 0xFFFF) | ((this.data.get(3) & 0xFFFF) << 16);
    }

    public int getWaterAmount() {
        return this.data.get(4);
    }

    public int getMaxWater() {
        return this.data.get(5);
    }

    public int getBiomassUnits() {
        return this.data.get(6);
    }

    public int getMaxBiomass() {
        return this.data.get(7);
    }

    public int getProgressTicks() {
        return this.data.get(8);
    }

    public int getMaxProgressTicks() {
        return this.data.get(9);
    }

    public int getSpeciesIndex() {
        return this.data.get(10);
    }

    public boolean isAutoEcologicalMode() {
        return this.data.get(11) == 1;
    }

    public int getTargetPopulationQuota() {
        return this.data.get(12);
    }

    public boolean isActive() {
        return this.data.get(13) == 1;
    }

    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        if (max <= 0) {
            return 0;
        }
        return (int) ((long) getEnergy() * pixels / max);
    }

    public int getWaterScaled(int pixels) {
        int max = getMaxWater();
        if (max <= 0) {
            return 0;
        }
        return (int) ((long) getWaterAmount() * pixels / max);
    }

    public int getBiomassScaled(int pixels) {
        int max = getMaxBiomass();
        if (max <= 0) {
            return 0;
        }
        return (int) ((long) getBiomassUnits() * pixels / max);
    }

    public int getProgressScaled(int pixels) {
        int max = getMaxProgressTicks();
        if (max <= 0) {
            return 0;
        }
        return (int) ((long) getProgressTicks() * pixels / max);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            int containerSlots = AmnioticIncubatorBlockEntity.CONTAINER_SIZE;
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
}
