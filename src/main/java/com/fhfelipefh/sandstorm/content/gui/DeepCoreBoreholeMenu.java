package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.DeepCoreBoreholeBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
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
import net.minecraft.world.item.Items;

public class DeepCoreBoreholeMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SLOTS_COUNT = 12;

    private final DeepCoreBoreholeBlockEntity blockEntity;
    private final Container container;
    private final ContainerData data;

    public DeepCoreBoreholeMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.DEEP_CORE_BOREHOLE_MENU, syncId, playerInventory, null, new SimpleContainer(CONTAINER_SLOTS_COUNT), new SimpleContainerData(12));
    }

    public DeepCoreBoreholeMenu(int syncId, Inventory playerInventory, DeepCoreBoreholeBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.DEEP_CORE_BOREHOLE_MENU, syncId, playerInventory, blockEntity, blockEntity, data);
    }

    public DeepCoreBoreholeMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, DeepCoreBoreholeBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerDataCount(data, 12);
        this.blockEntity = blockEntity;
        this.container = container;
        this.data = data;

        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, DeepCoreBoreholeBlockEntity.SLOT_DRILL_BIT, 12, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(SandStormItems.GEOTHERMAL_CORE_DRILL_BIT);
            }
        });
        this.addSlot(new Slot(container, DeepCoreBoreholeBlockEntity.SLOT_COOLANT_IN, 34, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.WATER_BUCKET);
            }
        });
        this.addSlot(new Slot(container, DeepCoreBoreholeBlockEntity.SLOT_COOLANT_OUT, 52, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                int index = DeepCoreBoreholeBlockEntity.OUTPUT_START + col + row * 3;
                this.addSlot(new Slot(container, index, 98 + col * 18, 20 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
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
        return (this.data.get(0) & 0xFFFF) | (this.data.get(1) << 16);
    }

    public int getMaxEnergy() {
        return (this.data.get(2) & 0xFFFF) | (this.data.get(3) << 16);
    }

    public int getProgress() {
        return this.data.get(4);
    }

    public int getCycleTicks() {
        return this.data.get(5);
    }

    public int getCurrentDepth() {
        return this.data.get(6);
    }

    public int getTemperature() {
        return this.data.get(7);
    }

    public int getPressure() {
        return this.data.get(8);
    }

    public int getFluidAmount() {
        return this.data.get(9);
    }

    public boolean isDrilling() {
        return this.data.get(10) == 1;
    }

    public boolean hasDrillBit() {
        return this.data.get(11) == 1;
    }

    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        if (max <= 0) {
            return 0;
        }
        return (int) ((long) getStoredEnergy() * pixels / max);
    }

    public int getFluidScaled(int pixels) {
        return this.getFluidAmount() * pixels / DeepCoreBoreholeBlockEntity.MAX_FLUID;
    }

    public int getProgressScaled(int pixels) {
        int cycle = getCycleTicks();
        if (cycle <= 0) {
            return 0;
        }
        return getProgress() * pixels / cycle;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            int containerSlots = CONTAINER_SLOTS_COUNT;
            int totalSlots = this.slots.size();

            if (index < containerSlots) {
                if (!this.moveItemStackTo(stackInSlot, containerSlots, totalSlots, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stackInSlot.is(SandStormItems.GEOTHERMAL_CORE_DRILL_BIT)) {
                    if (!this.moveItemStackTo(stackInSlot, DeepCoreBoreholeBlockEntity.SLOT_DRILL_BIT, DeepCoreBoreholeBlockEntity.SLOT_DRILL_BIT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stackInSlot.is(Items.WATER_BUCKET)) {
                    if (!this.moveItemStackTo(stackInSlot, DeepCoreBoreholeBlockEntity.SLOT_COOLANT_IN, DeepCoreBoreholeBlockEntity.SLOT_COOLANT_IN + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < containerSlots + 27) {
                    if (!this.moveItemStackTo(stackInSlot, containerSlots + 27, totalSlots, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stackInSlot, containerSlots, containerSlots + 27, false)) {
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

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public DeepCoreBoreholeBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
