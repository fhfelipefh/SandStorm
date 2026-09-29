package com.fhfelipefh.sandstorm.content.storage;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.QuantumDiskDriveMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuantumDiskDriveBlockEntity extends BlockEntity implements Container, MenuProvider, QuantumNetworkNode {

    public static final int SLOT_COUNT = 8;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public QuantumDiskDriveBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.QUANTUM_DISK_DRIVE_BE, pos, state);
    }

    public List<StoredItemEntry> getAllStoredItems() {
        List<StoredItemEntry> result = new ArrayList<>();
        for (ItemStack cartridge : this.items) {
            if (!cartridge.isEmpty() && cartridge.getItem() instanceof QuantumStorageCartridgeItem) {
                List<StoredItemEntry> diskEntries = QuantumDiskStorage.getStoredItems(cartridge);
                for (StoredItemEntry entry : diskEntries) {
                    boolean merged = false;
                    for (int i = 0; i < result.size(); i++) {
                        if (result.get(i).matches(entry.template())) {
                            result.set(i, new StoredItemEntry(result.get(i).template(), result.get(i).count() + entry.count()));
                            merged = true;
                            break;
                        }
                    }
                    if (!merged) {
                        result.add(entry);
                    }
                }
            }
        }
        return result;
    }

    public long getTotalCapacity() {
        long total = 0;
        for (ItemStack cartridge : this.items) {
            if (!cartridge.isEmpty() && cartridge.getItem() instanceof QuantumStorageCartridgeItem cItem) {
                total += cItem.getTier().getCapacity();
            }
        }
        return total;
    }

    public long getTotalStoredCount() {
        long total = 0;
        for (ItemStack cartridge : this.items) {
            if (!cartridge.isEmpty() && cartridge.getItem() instanceof QuantumStorageCartridgeItem) {
                total += QuantumDiskStorage.getTotalItemCount(cartridge);
            }
        }
        return total;
    }

    public long insertIntoDrive(ItemStack toInsert) {
        if (toInsert.isEmpty()) {
            return 0;
        }

        long totalInserted = 0;
        ItemStack remaining = toInsert.copy();

        for (ItemStack cartridge : this.items) {
            if (remaining.isEmpty()) {
                break;
            }
            if (!cartridge.isEmpty() && cartridge.getItem() instanceof QuantumStorageCartridgeItem cItem) {
                long inserted = QuantumDiskStorage.insertItem(cartridge, remaining, cItem.getTier());
                if (inserted > 0) {
                    totalInserted += inserted;
                    remaining.shrink((int) inserted);
                    this.setChanged();
                }
            }
        }

        return totalInserted;
    }

    public ItemStack extractFromDrive(ItemStack filterStack, int maxExtract) {
        if (filterStack.isEmpty() || maxExtract <= 0) {
            return ItemStack.EMPTY;
        }

        int needed = maxExtract;
        ItemStack accumulated = ItemStack.EMPTY;

        for (ItemStack cartridge : this.items) {
            if (needed <= 0) {
                break;
            }
            if (!cartridge.isEmpty() && cartridge.getItem() instanceof QuantumStorageCartridgeItem) {
                ItemStack extracted = QuantumDiskStorage.extractItem(cartridge, filterStack, needed);
                if (!extracted.isEmpty()) {
                    if (accumulated.isEmpty()) {
                        accumulated = extracted;
                    } else {
                        accumulated.grow(extracted.getCount());
                    }
                    needed -= extracted.getCount();
                    this.setChanged();
                }
            }
        }

        return accumulated;
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
        if (!result.isEmpty()) {
            this.setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.getItem() instanceof QuantumStorageCartridgeItem;
    }

    @Override
    public void clearContent() {
        Collections.fill(this.items, ItemStack.EMPTY);
        this.setChanged();
    }

    @Override
    public BlockPos getNodePos() {
        return this.worldPosition;
    }

    @Override
    public boolean isNodeActive() {
        return true;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.sandstorm.quantum_disk_drive");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new QuantumDiskDriveMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        Collections.fill(this.items, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }
}
