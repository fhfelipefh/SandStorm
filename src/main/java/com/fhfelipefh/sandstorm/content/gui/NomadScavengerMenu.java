package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class NomadScavengerMenu extends AbstractContainerMenu {
    public static final int TRADE_SCRAP_METAL = 0;
    public static final int TRADE_CIRCUIT_BOARD = 1;
    public static final int TRADE_ELECTRIC_COMPONENT = 2;
    public static final int TRADE_TECH_DISC = 3;
    public static final int BUTTON_BARTER = 10;

    private final Container tradeContainer;
    private final ContainerData tradeData;

    public NomadScavengerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(2), new SimpleContainerData(1));
    }

    public NomadScavengerMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(SandStormMenus.NOMAD_SCAVENGER_MENU, containerId);
        checkContainerSize(container, 2);
        checkContainerDataCount(data, 1);
        this.tradeContainer = container;
        this.tradeData = data;
        this.addDataSlots(data);

        this.addSlot(new Slot(container, 0, 70, 36) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(SandStormItems.POTABLE_WATER_BOTTLE);
            }
        });

        this.addSlot(new Slot(container, 1, 130, 36) {
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
    }

    public int getSelectedTrade() {
        return this.tradeData.get(0);
    }

    public void setSelectedTrade(int tradeIndex) {
        this.tradeData.set(0, tradeIndex);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id <= 3) {
            setSelectedTrade(id);
            return true;
        } else if (id == BUTTON_BARTER) {
            return executeBarter(player);
        }
        return false;
    }

    public boolean executeBarter(Player player) {
        ItemStack waterStack = this.tradeContainer.getItem(0);
        if (!waterStack.is(SandStormItems.POTABLE_WATER_BOTTLE)) {
            return false;
        }

        int selected = getSelectedTrade();
        int waterCost = getWaterCost(selected);
        if (waterStack.getCount() < waterCost) {
            return false;
        }

        ItemStack reward = getRewardItem(selected);
        ItemStack currentOutput = this.tradeContainer.getItem(1);
        if (!currentOutput.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(currentOutput, reward)) {
                return false;
            }
            if (currentOutput.getCount() + reward.getCount() > currentOutput.getMaxStackSize()) {
                return false;
            }
        }

        waterStack.shrink(waterCost);
        if (waterStack.isEmpty()) {
            this.tradeContainer.setItem(0, ItemStack.EMPTY);
        }

        ItemStack emptyBottles = new ItemStack(Items.GLASS_BOTTLE, waterCost);
        if (!player.getInventory().add(emptyBottles) && player.level() instanceof ServerLevel serverLevel) {
            player.spawnAtLocation(serverLevel, emptyBottles);
        }

        if (currentOutput.isEmpty()) {
            this.tradeContainer.setItem(1, reward.copy());
        } else {
            currentOutput.grow(reward.getCount());
        }

        this.broadcastChanges();
        return true;
    }

    public static int getWaterCost(int tradeIndex) {
        return switch (tradeIndex) {
            case TRADE_SCRAP_METAL -> 1;
            case TRADE_CIRCUIT_BOARD -> 2;
            case TRADE_ELECTRIC_COMPONENT -> 3;
            case TRADE_TECH_DISC -> 4;
            default -> 1;
        };
    }

    public static ItemStack getRewardItem(int tradeIndex) {
        return switch (tradeIndex) {
            case TRADE_SCRAP_METAL -> new ItemStack(SandStormItems.SCRAP_METAL, 2);
            case TRADE_CIRCUIT_BOARD -> new ItemStack(SandStormItems.CIRCUIT_BOARD, 1);
            case TRADE_ELECTRIC_COMPONENT -> new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 1);
            case TRADE_TECH_DISC -> new ItemStack(SandStormItems.TECH_DISC, 1);
            default -> new ItemStack(SandStormItems.SCRAP_METAL, 1);
        };
    }

    @Override
    public boolean stillValid(Player player) {
        return this.tradeContainer.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, this.tradeContainer);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (slotIndex == 1) {
                if (!this.moveItemStackTo(slotStack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, itemStack);
            } else if (slotIndex == 0) {
                if (!this.moveItemStackTo(slotStack, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotStack.is(SandStormItems.POTABLE_WATER_BOTTLE)) {
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 2 && slotIndex < 29) {
                if (!this.moveItemStackTo(slotStack, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 29 && slotIndex < 38) {
                if (!this.moveItemStackTo(slotStack, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }
        return itemStack;
    }
}
