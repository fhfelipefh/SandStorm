package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.item.MolecularUpgradeItem;
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

public class MolecularModifierMenu extends AbstractContainerMenu implements MachineMenu {
    private final Container container;
    private final ContainerData data;

    public MolecularModifierMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.MOLECULAR_MODIFIER_MENU, syncId, playerInventory, new SimpleContainer(7), new SimpleContainerData(6));
    }

    public MolecularModifierMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        this(SandStormMenus.MOLECULAR_MODIFIER_MENU, syncId, playerInventory, container, data);
    }

    public MolecularModifierMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 7);
        checkContainerDataCount(data, 6);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, 0, 26, 35));
        this.addSlot(new Slot(container, 1, 62, 17));
        this.addSlot(new Slot(container, 2, 62, 35));
        this.addSlot(new Slot(container, 3, 62, 53));
        this.addSlot(new Slot(container, 4, 84, 35));
        this.addSlot(new Slot(container, 5, 8, 53));
        this.addSlot(new Slot(container, 6, 134, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

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

    @Override
    public int getEnergy() {
        return data.get(0);
    }

    @Override
    public int getMaxEnergy() {
        return data.get(1);
    }

    @Override
    public int getProgress() {
        return data.get(2);
    }

    @Override
    public int getMaxProgress() {
        return data.get(3);
    }

    @Override
    public boolean isWptConnected() {
        return data.get(4) == 1;
    }

    @Override
    public boolean isProcessing() {
        return data.get(5) == 1;
    }

    @Override
    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        return max > 0 ? (getEnergy() * pixels) / max : 0;
    }

    @Override
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

            if (index == 6) {
                if (!this.moveItemStackTo(stackInSlot, 7, 43, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemStack);
            } else if (index < 6) {
                if (!this.moveItemStackTo(stackInSlot, 7, 43, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (BaseMachineBlockEntity.getFuelEnergy(stackInSlot) > 0 && this.moveItemStackTo(stackInSlot, 5, 6, false)) {
                } else if (stackInSlot.getItem() instanceof MolecularUpgradeItem upgrade) {
                    if (upgrade.getUpgradeType().getSlotType() == MolecularUpgradeItem.ModuleSlotType.NANOCOATING) {
                        if (!this.moveItemStackTo(stackInSlot, 4, 5, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        if (!this.moveItemStackTo(stackInSlot, 1, 4, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                    if (index < 34) {
                        if (!this.moveItemStackTo(stackInSlot, 34, 43, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(stackInSlot, 7, 34, false)) {
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
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:molecular_modifier");
        if (id >= 0 && id < recipes.size()) {
            MachineRecipe recipe = recipes.get(id);
            fillSlot(0, recipe.getSlot0Inputs());
            List<ItemStack> slot1Inputs = recipe.getSlot1Inputs();
            if (!slot1Inputs.isEmpty()) {
                ItemStack first = slot1Inputs.get(0);
                if (first.getItem() instanceof MolecularUpgradeItem up && up.getUpgradeType().getSlotType() == MolecularUpgradeItem.ModuleSlotType.NANOCOATING) {
                    fillSlot(4, slot1Inputs);
                } else {
                    fillSlot(1, slot1Inputs);
                }
            }
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
            if (this.moveItemStackTo(current, 7, 43, false)) {
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
        for (int i = 7; i < 43; i++) {
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
