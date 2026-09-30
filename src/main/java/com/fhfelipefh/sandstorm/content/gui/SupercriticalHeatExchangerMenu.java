package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SupercriticalHeatExchangerBlockEntity;
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

public class SupercriticalHeatExchangerMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SLOTS_COUNT = 4;

    private final SupercriticalHeatExchangerBlockEntity blockEntity;
    private final Container container;
    private final ContainerData data;

    public SupercriticalHeatExchangerMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.SUPERCRITICAL_HEAT_EXCHANGER_MENU, syncId, playerInventory, null, new SimpleContainer(CONTAINER_SLOTS_COUNT), new SimpleContainerData(8));
    }

    public SupercriticalHeatExchangerMenu(int syncId, Inventory playerInventory, SupercriticalHeatExchangerBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.SUPERCRITICAL_HEAT_EXCHANGER_MENU, syncId, playerInventory, blockEntity, blockEntity, data);
    }

    public SupercriticalHeatExchangerMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, SupercriticalHeatExchangerBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerDataCount(data, 8);
        this.blockEntity = blockEntity;
        this.container = container;
        this.data = data;

        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, SupercriticalHeatExchangerBlockEntity.SLOT_WATER_IN, 32, 23) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.WATER_BUCKET);
            }
        });
        this.addSlot(new Slot(container, SupercriticalHeatExchangerBlockEntity.SLOT_WATER_OUT, 32, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, SupercriticalHeatExchangerBlockEntity.SLOT_THERMAL_CORE, 68, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(SandStormItems.THERMAL_RADIATOR_FIN) || stack.is(SandStormItems.SUPERHEATED_LITHIUM_CAPSULE);
            }
        });
        this.addSlot(new Slot(container, SupercriticalHeatExchangerBlockEntity.SLOT_BATTERY, 92, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return BaseMachineBlockEntity.getFuelEnergy(stack) > 0;
            }
        });

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

    public int getWaterAmount() {
        return this.data.get(4);
    }

    public int getCurrentGenRate() {
        return this.data.get(5);
    }

    public int getSteamPressure() {
        return this.data.get(6);
    }

    public boolean isOperating() {
        return this.data.get(7) == 1;
    }

    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        if (max <= 0) {
            return 0;
        }
        return (int) ((long) getStoredEnergy() * pixels / max);
    }

    public int getWaterScaled(int pixels) {
        return this.getWaterAmount() * pixels / SupercriticalHeatExchangerBlockEntity.MAX_WATER;
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
                if (stackInSlot.is(Items.WATER_BUCKET)) {
                    if (!this.moveItemStackTo(stackInSlot, SupercriticalHeatExchangerBlockEntity.SLOT_WATER_IN, SupercriticalHeatExchangerBlockEntity.SLOT_WATER_IN + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stackInSlot.is(SandStormItems.THERMAL_RADIATOR_FIN) || stackInSlot.is(SandStormItems.SUPERHEATED_LITHIUM_CAPSULE)) {
                    if (!this.moveItemStackTo(stackInSlot, SupercriticalHeatExchangerBlockEntity.SLOT_THERMAL_CORE, SupercriticalHeatExchangerBlockEntity.SLOT_THERMAL_CORE + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (BaseMachineBlockEntity.getFuelEnergy(stackInSlot) > 0) {
                    if (!this.moveItemStackTo(stackInSlot, SupercriticalHeatExchangerBlockEntity.SLOT_BATTERY, SupercriticalHeatExchangerBlockEntity.SLOT_BATTERY + 1, false)) {
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

    public SupercriticalHeatExchangerBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
