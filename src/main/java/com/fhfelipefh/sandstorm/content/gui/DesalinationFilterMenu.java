package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.recipe.MachineRecipe;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipeRegistry;
import java.util.List;
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

public class DesalinationFilterMenu extends AbstractContainerMenu implements MachineMenu {
    private final Container container;
    private final ContainerData data;

    public DesalinationFilterMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.DESALINATION_FILTER_MENU, syncId, playerInventory, new SimpleContainer(5), new SimpleContainerData(10));
    }

    public DesalinationFilterMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.DESALINATION_FILTER_MENU, syncId, playerInventory, container, data);
    }

    public DesalinationFilterMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 5);
        checkContainerDataCount(data, 10);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 44, 37));
        this.addSlot(new Slot(container, 1, 116, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 2, 116, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 3, 8, 48));
        this.addSlot(new Slot(container, 4, 80, 58));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    public int getEnergy() {
        return data.get(0);
    }

    public int getMaxEnergy() {
        return data.get(1);
    }

    public int getProgress() {
        return data.get(2);
    }

    public int getMaxProgress() {
        return data.get(3);
    }

    public boolean isWptConnected() {
        return data.get(4) == 1;
    }

    public boolean isProcessing() {
        return data.get(5) == 1;
    }

    public int getWaterInput() {
        return data.get(6);
    }

    public int getWaterOutput() {
        return data.get(7);
    }

    public int getMaxWater() {
        int max = data.get(8);
        return max > 0 ? max : 4000;
    }

    public boolean hasCartridge() {
        return data.get(9) == 1;
    }

    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        return max > 0 ? (getEnergy() * pixels) / max : 0;
    }

    public int getProgressScaled(int pixels) {
        int max = getMaxProgress();
        return max > 0 ? (getProgress() * pixels) / max : 0;
    }

    public int getWaterInputScaled(int pixels) {
        return (getWaterInput() * pixels) / getMaxWater();
    }

    public int getWaterOutputScaled(int pixels) {
        return (getWaterOutput() * pixels) / getMaxWater();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();

            if (index == 1 || index == 2) {
                if (!this.moveItemStackTo(originalStack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(originalStack, newStack);
            } else if (index != 0 && index != 3 && index != 4) {
                if (!this.moveItemStackTo(originalStack, 0, 1, false)
                        && !this.moveItemStackTo(originalStack, 4, 5, false)
                        && !this.moveItemStackTo(originalStack, 3, 4, false)) {
                    if (index >= 5 && index < 32) {
                        if (!this.moveItemStackTo(originalStack, 32, 41, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 32 && index < 41 && !this.moveItemStackTo(originalStack, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(originalStack, 5, 41, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (originalStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, originalStack);
        }
        return newStack;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:desalination_filter");
        if (id >= 0 && id < recipes.size()) {
            MachineRecipe recipe = recipes.get(id);
            fillSlot(0, recipe.getSlot0Inputs());
            fillSlot(4, recipe.getSlot1Inputs());
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    private void fillSlot(int targetSlotIndex, List<ItemStack> accepted) {
        Slot targetSlot = this.slots.get(targetSlotIndex);
        if (targetSlot.hasItem()) {
            boolean matches = false;
            for (ItemStack candidate : accepted) {
                if (targetSlot.getItem().is(candidate.getItem())) {
                    matches = true;
                    break;
                }
            }
            if (matches) {
                return;
            }
            ItemStack current = targetSlot.getItem();
            if (this.moveItemStackTo(current, 5, 41, false)) {
                if (current.isEmpty()) {
                    targetSlot.set(ItemStack.EMPTY);
                } else {
                    targetSlot.setChanged();
                    return;
                }
            } else {
                return;
            }
        }
        for (int i = 5; i < 41; i++) {
            Slot invSlot = this.slots.get(i);
            if (invSlot.hasItem()) {
                ItemStack invStack = invSlot.getItem();
                for (ItemStack candidate : accepted) {
                    if (invStack.is(candidate.getItem())) {
                        if (!targetSlot.hasItem()) {
                            ItemStack moved = invSlot.remove(1);
                            targetSlot.set(moved);
                            return;
                        } else if (ItemStack.isSameItemSameComponents(targetSlot.getItem(), invStack)
                                && targetSlot.getItem().getCount() < targetSlot.getItem().getMaxStackSize()) {
                            invSlot.remove(1);
                            targetSlot.getItem().grow(1);
                            targetSlot.setChanged();
                            return;
                        }
                    }
                }
            }
        }
    }
}
