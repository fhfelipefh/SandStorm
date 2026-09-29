package com.fhfelipefh.sandstorm.content.storage;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.QuantumTerminalMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class QuantumAccessTerminalBlockEntity extends BlockEntity implements MenuProvider, QuantumNetworkNode {

    public QuantumAccessTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.QUANTUM_ACCESS_TERMINAL_BE, pos, state);
    }

    public QuantumNetworkHelper.NetworkCluster getCluster() {
        if (this.level == null) {
            return new QuantumNetworkHelper.NetworkCluster(null, List.of(), 0);
        }
        return QuantumNetworkHelper.scanNetwork(this.level, this.worldPosition);
    }

    public boolean isNetworkOnline() {
        QuantumNetworkHelper.NetworkCluster cluster = getCluster();
        if (cluster.controller() != null) {
            return cluster.controller().isNodeActive();
        }
        return !cluster.drives().isEmpty();
    }

    public List<StoredItemEntry> getAggregatedItems() {
        if (!isNetworkOnline()) {
            return List.of();
        }

        QuantumNetworkHelper.NetworkCluster cluster = getCluster();
        List<StoredItemEntry> aggregated = new ArrayList<>();

        for (QuantumDiskDriveBlockEntity drive : cluster.drives()) {
            List<StoredItemEntry> driveItems = drive.getAllStoredItems();
            for (StoredItemEntry entry : driveItems) {
                boolean merged = false;
                for (int i = 0; i < aggregated.size(); i++) {
                    if (aggregated.get(i).matches(entry.template())) {
                        aggregated.set(i, new StoredItemEntry(aggregated.get(i).template(), aggregated.get(i).count() + entry.count()));
                        merged = true;
                        break;
                    }
                }
                if (!merged) {
                    aggregated.add(entry);
                }
            }
        }

        return aggregated;
    }

    public long insertItem(ItemStack toInsert) {
        if (!isNetworkOnline() || toInsert == null || toInsert.isEmpty()) {
            return 0;
        }

        QuantumNetworkHelper.NetworkCluster cluster = getCluster();
        long totalInserted = 0;
        ItemStack remaining = toInsert.copy();

        for (QuantumDiskDriveBlockEntity drive : cluster.drives()) {
            if (remaining.isEmpty()) {
                break;
            }
            long inserted = drive.insertIntoDrive(remaining);
            if (inserted > 0) {
                totalInserted += inserted;
                remaining.shrink((int) inserted);
            }
        }

        if (totalInserted > 0 && cluster.controller() != null) {
            cluster.controller().drainEnergy(1);
        }

        return totalInserted;
    }

    public ItemStack extractItem(ItemStack filterStack, int maxExtract) {
        if (!isNetworkOnline() || filterStack == null || filterStack.isEmpty() || maxExtract <= 0) {
            return ItemStack.EMPTY;
        }

        QuantumNetworkHelper.NetworkCluster cluster = getCluster();
        int needed = maxExtract;
        ItemStack accumulated = ItemStack.EMPTY;

        for (QuantumDiskDriveBlockEntity drive : cluster.drives()) {
            if (needed <= 0) {
                break;
            }
            ItemStack extracted = drive.extractFromDrive(filterStack, needed);
            if (!extracted.isEmpty()) {
                if (accumulated.isEmpty()) {
                    accumulated = extracted;
                } else {
                    accumulated.grow(extracted.getCount());
                }
                needed -= extracted.getCount();
            }
        }

        if (!accumulated.isEmpty() && cluster.controller() != null) {
            cluster.controller().drainEnergy(1);
        }

        return accumulated;
    }

    @Override
    public BlockPos getNodePos() {
        return this.worldPosition;
    }

    @Override
    public boolean isNodeActive() {
        return isNetworkOnline();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.sandstorm.quantum_access_terminal");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new QuantumTerminalMenu(containerId, playerInventory, this.worldPosition);
    }
}
