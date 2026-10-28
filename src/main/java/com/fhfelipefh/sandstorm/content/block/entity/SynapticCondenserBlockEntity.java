package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.fluid.SandStormFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Iterator;
import java.util.List;

public class SynapticCondenserBlockEntity extends BlockEntity implements SingleSlotStorage<FluidVariant> {
    public static final int MAX_CAPACITY = 64000;
    private int fluidAmount = 0;

    public SynapticCondenserBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.SYNAPTIC_CONDENSER_BE, pos, state);
    }

    public int getFluidAmount() {
        return this.fluidAmount;
    }

    public int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public int insertFluid(int amount) {
        int space = MAX_CAPACITY - this.fluidAmount;
        int toInsert = Math.min(space, amount);
        if (toInsert > 0) {
            this.fluidAmount += toInsert;
            setChanged();
        }
        return toInsert;
    }

    public int extractFluid(int amount) {
        int toExtract = Math.min(this.fluidAmount, amount);
        if (toExtract > 0) {
            this.fluidAmount -= toExtract;
            setChanged();
        }
        return toExtract;
    }

    public Storage<FluidVariant> getFluidStorage(Direction side) {
        return this;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("fluidAmount", this.fluidAmount);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.fluidAmount = input.getIntOr("fluidAmount", 0);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("fluidAmount", this.fluidAmount);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (!resource.isOf(SandStormFluids.SYNAPTIC_FLUID)) {
            return 0;
        }
        long spaceDroplets = (long) (MAX_CAPACITY - this.fluidAmount) * (FluidConstants.BUCKET / 1000);
        long insertedDroplets = Math.min(spaceDroplets, maxAmount);
        int insertedMb = (int) (insertedDroplets / (FluidConstants.BUCKET / 1000));
        if (insertedMb > 0) {
            transaction.addCloseCallback((t, result) -> {
                if (result.wasCommitted()) {
                    this.fluidAmount += insertedMb;
                    setChanged();
                }
            });
            return (long) insertedMb * (FluidConstants.BUCKET / 1000);
        }
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (!resource.isOf(SandStormFluids.SYNAPTIC_FLUID)) {
            return 0;
        }
        long availableDroplets = (long) this.fluidAmount * (FluidConstants.BUCKET / 1000);
        long extractedDroplets = Math.min(availableDroplets, maxAmount);
        int extractedMb = (int) (extractedDroplets / (FluidConstants.BUCKET / 1000));
        if (extractedMb > 0) {
            transaction.addCloseCallback((t, result) -> {
                if (result.wasCommitted()) {
                    this.fluidAmount -= extractedMb;
                    setChanged();
                }
            });
            return (long) extractedMb * (FluidConstants.BUCKET / 1000);
        }
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return this.fluidAmount <= 0;
    }

    @Override
    public FluidVariant getResource() {
        return FluidVariant.of(SandStormFluids.SYNAPTIC_FLUID);
    }

    @Override
    public long getAmount() {
        return (long) this.fluidAmount * (FluidConstants.BUCKET / 1000);
    }

    @Override
    public long getCapacity() {
        return (long) MAX_CAPACITY * (FluidConstants.BUCKET / 1000);
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return List.<StorageView<FluidVariant>>of(this).iterator();
    }
}

