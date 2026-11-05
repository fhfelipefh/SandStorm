package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.QuantumSleeperPodBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
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
        this(SandStormMenus.QUANTUM_SLEEPER_MENU, syncId, playerInventory, null, new SimpleContainer(TOTAL_CONTAINER_SLOTS), new SimpleContainerData(7));
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

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(container, col, 27 + col * 18, 102));
        }

        for (int i = 9; i < 36; ++i) {
            this.addSlot(new Slot(container, i, -2000, -2000));
        }

        this.addSlot(new Slot(container, 36, 12, 80));
        this.addSlot(new Slot(container, 37, 12, 60));
        this.addSlot(new Slot(container, 38, 12, 40));
        this.addSlot(new Slot(container, 39, 12, 20));
        this.addSlot(new Slot(container, 40, 32, 50));

        this.addSlot(new Slot(container, NUTRIENT_SLOT, 176, 62));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 27 + col * 18, 136 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 27 + col * 18, 196));
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
        if (this.blockEntity != null) {
            return this.blockEntity.getStoredEnergy();
        }
        if (this.data.getCount() >= 7) {
            return ((this.data.get(5) & 0xFFFF) << 16) | (this.data.get(0) & 0xFFFF);
        }
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        if (this.blockEntity != null) {
            return QuantumSleeperPodBlockEntity.MAX_ENERGY;
        }
        if (this.data.getCount() >= 7) {
            return ((this.data.get(6) & 0xFFFF) << 16) | (this.data.get(1) & 0xFFFF);
        }
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
                if (isNutrientItem(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, NUTRIENT_SLOT, NUTRIENT_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    EquipmentSlot type = player.getEquipmentSlotForItem(stackInSlot);
                    int targetSlot = switch (type) {
                        case FEET -> 36;
                        case LEGS -> 37;
                        case CHEST -> 38;
                        case HEAD -> 39;
                        default -> -1;
                    };
                    if (targetSlot != -1) {
                        if (!this.moveItemStackTo(stackInSlot, targetSlot, targetSlot + 1, false)) {
                            if (!this.moveItemStackTo(stackInSlot, 0, 9, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    } else if (!this.moveItemStackTo(stackInSlot, 0, 9, false)) {
                        if (index < containerSlots + 27) {
                            if (!this.moveItemStackTo(stackInSlot, containerSlots + 27, totalSlots, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else if (!this.moveItemStackTo(stackInSlot, containerSlots, containerSlots + 27, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
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

    private boolean isNutrientItem(ItemStack stack) {
        return stack.is(SandStormItems.CHITOSAN_EXTRACT)
                || stack.is(SandStormItems.TREHALOSE_SUGAR)
                || stack.is(SandStormItems.OSMOLYTE_GLYCEROL)
                || stack.is(SandStormItems.POTABLE_WATER_BOTTLE);
    }

    public QuantumSleeperPodBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
