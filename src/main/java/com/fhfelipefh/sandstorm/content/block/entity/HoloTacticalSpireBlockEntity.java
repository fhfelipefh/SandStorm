package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.HoloTacticalSpireBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSwarmManager;
import com.fhfelipefh.sandstorm.content.gui.HoloTacticalSpireMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HoloTacticalSpireBlockEntity extends BlockEntity implements MenuProvider, Container {
    public static final int MAX_ENERGY = 100000;

    private int storedEnergy = MAX_ENERGY;
    private int activeTacticalOrder = 0;
    private int connectedCyborgsCount = 0;
    private int seismicThreatDetected = 0;
    private ItemStack probeSlot = ItemStack.EMPTY;
    private int tickCounter = 0;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEnergy;
                case 1 -> MAX_ENERGY;
                case 2 -> activeTacticalOrder;
                case 3 -> connectedCyborgsCount;
                case 4 -> seismicThreatDetected;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                storedEnergy = value;
            } else if (index == 2) {
                activeTacticalOrder = value;
                CyborgSwarmManager.getInstance().setGlobalTacticalOrder(value);
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public HoloTacticalSpireBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.HOLO_TACTICAL_SPIRE_BE, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) {
            return;
        }

        tickCounter++;
        if (tickCounter % 20 == 0) {
            ServerLevel serverLevel = (ServerLevel) level;
            AABB scanBox = new AABB(pos).inflate(64.0);
            List<CyborgEntity> cyborgs = serverLevel.getEntitiesOfClass(CyborgEntity.class, scanBox);
            this.connectedCyborgsCount = cyborgs.size();

            this.seismicThreatDetected = serverLevel.isThundering() ? 1 : 0;

            boolean shouldBeActive = this.storedEnergy > 0;
            if (state.getValue(HoloTacticalSpireBlock.ACTIVE) != shouldBeActive) {
                level.setBlock(pos, state.setValue(HoloTacticalSpireBlock.ACTIVE, shouldBeActive), 3);
            }

            setChanged();
        }
    }

    public int getStoredEnergy() {
        return this.storedEnergy;
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    public int getActiveTacticalOrder() {
        return this.activeTacticalOrder;
    }

    public void setTacticalOrder(int order) {
        this.activeTacticalOrder = order;
        CyborgSwarmManager.getInstance().setGlobalTacticalOrder(order);
        setChanged();
    }

    public int getConnectedCyborgsCount() {
        return this.connectedCyborgsCount;
    }

    public boolean isSeismicThreatDetected() {
        return this.seismicThreatDetected == 1;
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.holo_tactical_spire");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new HoloTacticalSpireMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.probeSlot.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? this.probeSlot : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0 && !this.probeSlot.isEmpty()) {
            ItemStack split = this.probeSlot.split(amount);
            setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack stack = this.probeSlot;
            this.probeSlot = ItemStack.EMPTY;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            this.probeSlot = stack;
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.probeSlot = ItemStack.EMPTY;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putInt("activeTacticalOrder", this.activeTacticalOrder);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.storedEnergy = input.getIntOr("storedEnergy", MAX_ENERGY);
        this.activeTacticalOrder = input.getIntOr("activeTacticalOrder", 0);
    }
}
