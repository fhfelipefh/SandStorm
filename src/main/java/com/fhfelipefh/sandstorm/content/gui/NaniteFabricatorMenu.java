package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.recipe.MachineRecipe;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipeRegistry;
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

import java.util.List;

public class NaniteFabricatorMenu extends AbstractContainerMenu implements MachineMenu {
    private final Container container;
    private final ContainerData data;

    public NaniteFabricatorMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.NANITE_FABRICATOR_MENU, syncId, playerInventory, new SimpleContainer(4), new SimpleContainerData(6));
    }

    public NaniteFabricatorMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.NANITE_FABRICATOR_MENU, syncId, playerInventory, container, data);
    }

    public NaniteFabricatorMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 4);
        checkContainerDataCount(data, 6);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 44, 26));
        this.addSlot(new Slot(container, 1, 44, 48));
        this.addSlot(new Slot(container, 2, 116, 37) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 3, 8, 48));

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

    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        return max > 0 ? (getEnergy() * pixels) / max : 0;
    }

    public int getProgressScaled(int pixels) {
        int max = getMaxProgress();
        return max > 0 ? (getProgress() * pixels) / max : 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            if (index == 2) {
                if (!this.moveItemStackTo(stackInSlot, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemStack);
            } else if (index < 4) {
                if (!this.moveItemStackTo(stackInSlot, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, 2, false)
                        && !this.moveItemStackTo(stackInSlot, 3, 4, false)) {
                    if (index < 31) {
                        if (!this.moveItemStackTo(stackInSlot, 31, 40, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(stackInSlot, 4, 31, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
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
    public boolean clickMenuButton(Player player, int id) {
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:nanite_fabricator");
        if (id >= 0 && id < recipes.size()) {
            MachineRecipe recipe = recipes.get(id);
            fillSlot(0, recipe.getSlot0Inputs());
            fillSlot(1, recipe.getSlot1Inputs());
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
            if (this.moveItemStackTo(current, 4, 40, false)) {
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
        for (int i = 4; i < 40; i++) {
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
