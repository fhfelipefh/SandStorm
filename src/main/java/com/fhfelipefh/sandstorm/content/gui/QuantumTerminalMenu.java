package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.network.SyncTerminalGridPayload;
import com.fhfelipefh.sandstorm.content.network.TerminalActionPayload;
import com.fhfelipefh.sandstorm.content.storage.QuantumAccessTerminalBlockEntity;
import com.fhfelipefh.sandstorm.content.storage.StoredItemEntry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class QuantumTerminalMenu extends AbstractContainerMenu {

    private final BlockPos terminalPos;
    private final Player player;
    private List<StoredItemEntry> clientItems = new ArrayList<>();
    private int clientEnergy = 0;
    private int clientMaxEnergy = 0;
    private long clientTotalStored = 0;
    private long clientTotalCapacity = 0;
    private int syncTimer = 0;

    public QuantumTerminalMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, BlockPos.ZERO);
    }

    public QuantumTerminalMenu(int syncId, Inventory playerInventory, BlockPos terminalPos) {
        super(SandStormMenus.QUANTUM_ACCESS_TERMINAL_MENU, syncId);
        this.terminalPos = terminalPos;
        this.player = playerInventory.player;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 138 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 196));
        }
    }

    public BlockPos getTerminalPos() {
        return terminalPos;
    }

    public List<StoredItemEntry> getClientItems() {
        return clientItems;
    }

    public int getClientEnergy() {
        return clientEnergy;
    }

    public int getClientMaxEnergy() {
        return clientMaxEnergy;
    }

    public long getClientTotalStored() {
        return clientTotalStored;
    }

    public long getClientTotalCapacity() {
        return clientTotalCapacity;
    }

    public void updateClientState(List<StoredItemEntry> items, int energy, int maxEnergy, long totalStored, long totalCapacity) {
        this.clientItems = items;
        this.clientEnergy = energy;
        this.clientMaxEnergy = maxEnergy;
        this.clientTotalStored = totalStored;
        this.clientTotalCapacity = totalCapacity;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (player instanceof ServerPlayer serverPlayer) {
            syncTimer++;
            if (syncTimer % 10 == 0) {
                syncToClient(serverPlayer);
            }
        }
    }

    public void syncToClient(ServerPlayer serverPlayer) {
        Level level = serverPlayer.level();
        if (level == null) {
            return;
        }

        BlockEntity be = level.getBlockEntity(terminalPos);
        if (be instanceof QuantumAccessTerminalBlockEntity terminal) {
            List<StoredItemEntry> items = terminal.getAggregatedItems();
            var cluster = terminal.getCluster();
            int energy = cluster.controller() != null ? cluster.controller().getStoredEnergy() : 0;
            int maxEnergy = cluster.controller() != null ? cluster.controller().getMaxEnergy() : 0;

            long totalStored = 0;
            long totalCap = 0;
            for (var drive : cluster.drives()) {
                totalStored += drive.getTotalStoredCount();
                totalCap += drive.getTotalCapacity();
            }

            SyncTerminalGridPayload payload = new SyncTerminalGridPayload(
                    terminalPos, energy, maxEnergy, totalStored, totalCap, items
            );
            ServerPlayNetworking.send(serverPlayer, payload);
        }
    }

    public void handleAction(ServerPlayer serverPlayer, ItemStack filterStack, int actionType) {
        Level level = serverPlayer.level();
        if (level == null) {
            return;
        }

        BlockEntity be = level.getBlockEntity(terminalPos);
        if (!(be instanceof QuantumAccessTerminalBlockEntity terminal)) {
            return;
        }

        if (actionType == TerminalActionPayload.ACTION_INSERT_HELD) {
            ItemStack carried = this.getCarried();
            if (!carried.isEmpty()) {
                long inserted = terminal.insertItem(carried);
                if (inserted > 0) {
                    carried.shrink((int) inserted);
                    this.setCarried(carried);
                }
            }
        } else if (actionType == TerminalActionPayload.ACTION_EXTRACT_STACK) {
            ItemStack extracted = terminal.extractItem(filterStack, filterStack.getMaxStackSize());
            if (!extracted.isEmpty()) {
                ItemStack carried = this.getCarried();
                if (carried.isEmpty()) {
                    this.setCarried(extracted);
                } else if (ItemStack.isSameItemSameComponents(carried, extracted)) {
                    int add = Math.min(extracted.getCount(), carried.getMaxStackSize() - carried.getCount());
                    carried.grow(add);
                }
            }
        } else if (actionType == TerminalActionPayload.ACTION_EXTRACT_HALF) {
            int amount = Math.max(1, filterStack.getMaxStackSize() / 2);
            ItemStack extracted = terminal.extractItem(filterStack, amount);
            if (!extracted.isEmpty()) {
                ItemStack carried = this.getCarried();
                if (carried.isEmpty()) {
                    this.setCarried(extracted);
                } else if (ItemStack.isSameItemSameComponents(carried, extracted)) {
                    int add = Math.min(extracted.getCount(), carried.getMaxStackSize() - carried.getCount());
                    carried.grow(add);
                }
            }
        } else if (actionType == TerminalActionPayload.ACTION_SHIFT_EXTRACT) {
            ItemStack extracted = terminal.extractItem(filterStack, filterStack.getMaxStackSize());
            if (!extracted.isEmpty()) {
                if (!serverPlayer.getInventory().add(extracted)) {
                    Containers.dropItemStack(serverPlayer.level(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), extracted);
                }
            }
        }

        syncToClient(serverPlayer);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                BlockEntity be = player.level().getBlockEntity(terminalPos);
                if (be instanceof QuantumAccessTerminalBlockEntity terminal) {
                    long inserted = terminal.insertItem(itemstack1);
                    if (inserted > 0) {
                        itemstack1.shrink((int) inserted);
                        slot.setChanged();
                        syncToClient(serverPlayer);
                        return itemstack;
                    }
                }
            }

            if (invSlot < 27) {
                if (!this.moveItemStackTo(itemstack1, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
