package com.fhfelipefh.sandstorm.content.storage;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.gui.QuantumControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class QuantumNetworkControllerBlockEntity extends BlockEntity implements MenuProvider, QuantumNetworkNode {

    public static final int DEFAULT_CAPACITY = 100000;
    public static final int BASE_CONSUMPTION_PER_TICK = 1;

    private int storedEnergy = DEFAULT_CAPACITY;
    private int maxEnergy = DEFAULT_CAPACITY;
    private int connectedNodes = 1;
    private int consumptionPerTick = BASE_CONSUMPTION_PER_TICK;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEnergy & 0xFFFF;
                case 1 -> (storedEnergy >> 16) & 0xFFFF;
                case 2 -> maxEnergy & 0xFFFF;
                case 3 -> (maxEnergy >> 16) & 0xFFFF;
                case 4 -> connectedNodes;
                case 5 -> consumptionPerTick;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> storedEnergy = (storedEnergy & ~0xFFFF) | (value & 0xFFFF);
                case 1 -> storedEnergy = (storedEnergy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 2 -> maxEnergy = (maxEnergy & ~0xFFFF) | (value & 0xFFFF);
                case 3 -> maxEnergy = (maxEnergy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 4 -> connectedNodes = value;
                case 5 -> consumptionPerTick = value;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public QuantumNetworkControllerBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.QUANTUM_NETWORK_CONTROLLER_BE, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, QuantumNetworkControllerBlockEntity controller) {
        if (level == null || level.isClientSide()) {
            return;
        }

        long wpt = WirelessSolarReceiverManager.getWptChargeAt(level, pos);
        if (wpt > 0) {
            controller.receiveEnergy((int) Math.min(wpt, 500L));
        } else if (level.getSkyDarken() < 4 && level.canSeeSky(pos.above())) {
            controller.receiveEnergy(2);
        }

        if (level.getGameTime() % 20 == 0) {
            QuantumNetworkHelper.NetworkCluster cluster = QuantumNetworkHelper.scanNetwork(level, pos);
            controller.connectedNodes = Math.max(1, cluster.nodeCount());
            controller.consumptionPerTick = BASE_CONSUMPTION_PER_TICK + (cluster.drives().size() * 2) + Math.max(0, cluster.nodeCount() / 4);
        }

        if (controller.storedEnergy >= controller.consumptionPerTick) {
            controller.storedEnergy -= controller.consumptionPerTick;
        } else {
            controller.storedEnergy = 0;
        }

        boolean isActive = controller.storedEnergy > 0;
        if (state.getValue(QuantumNetworkControllerBlock.ACTIVE) != isActive) {
            level.setBlock(pos, state.setValue(QuantumNetworkControllerBlock.ACTIVE, isActive), 3);
        }
    }

    public boolean drainEnergy(int amount) {
        if (this.storedEnergy >= amount) {
            this.storedEnergy -= amount;
            this.setChanged();
            return true;
        }
        return false;
    }

    public int receiveEnergy(int amount) {
        int space = this.maxEnergy - this.storedEnergy;
        int accepted = Math.min(space, amount);
        this.storedEnergy += accepted;
        if (accepted > 0) {
            this.setChanged();
        }
        return accepted;
    }

    public int getStoredEnergy() {
        return storedEnergy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getConnectedNodes() {
        return connectedNodes;
    }

    public int getConsumptionPerTick() {
        return consumptionPerTick;
    }

    @Override
    public BlockPos getNodePos() {
        return this.worldPosition;
    }

    @Override
    public boolean isNodeActive() {
        return this.storedEnergy > 0;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.sandstorm.quantum_network_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new QuantumControllerMenu(containerId, playerInventory, this.dataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putInt("maxEnergy", this.maxEnergy);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.storedEnergy = input.getIntOr("storedEnergy", DEFAULT_CAPACITY);
        this.maxEnergy = input.getIntOr("maxEnergy", DEFAULT_CAPACITY);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("storedEnergy", this.storedEnergy);
        tag.putInt("maxEnergy", this.maxEnergy);
        return tag;
    }
}
