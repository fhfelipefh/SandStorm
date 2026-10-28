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
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Iterator;
import java.util.List;

public class SynapticRepairerBlockEntity extends BlockEntity implements SingleSlotStorage<FluidVariant> {
    public static final int MAX_CAPACITY = 8000;
    private int fluidAmount = 0;
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    public SynapticRepairerBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.SYNAPTIC_REPAIRER_BE, pos, state);
    }

    public int getFluidAmount() {
        return this.fluidAmount;
    }

    public int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public ItemStack getStoredItem() {
        return this.items.get(0);
    }

    public void setStoredItem(ItemStack item) {
        this.items.set(0, item);
        setChanged();
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

    public Storage<FluidVariant> getFluidStorage(Direction side) {
        return this;
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, SynapticRepairerBlockEntity repairer) {
        if (repairer.fluidAmount < MAX_CAPACITY && level.getGameTime() % 10 == 0) {
            for (Direction dir : Direction.values()) {
                BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
                if (neighbor instanceof SynapticCondenserBlockEntity condenser) {
                    int space = MAX_CAPACITY - repairer.fluidAmount;
                    int pulled = condenser.extractFluid(space);
                    if (pulled > 0) {
                        repairer.fluidAmount += pulled;
                        repairer.setChanged();
                        break;
                    }
                }
            }
        }

        ItemStack item = repairer.items.get(0);
        if (!item.isEmpty() && item.isDamaged() && repairer.fluidAmount >= 2) {
            repairer.fluidAmount -= 2;
            int newDamage = item.getDamageValue() - 1;
            item.setDamageValue(Math.max(0, newDamage));
            repairer.setChanged();
            if (level.getGameTime() % 5 == 0) {
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 2, 0.15, 0.1, 0.15, 0.02);
            }
            if (!item.isDamaged()) {
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 5, 0.2, 0.1, 0.2, 0.02);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("fluidAmount", this.fluidAmount);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.fluidAmount = input.getIntOr("fluidAmount", 0);
        ContainerHelper.loadAllItems(input, this.items);
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
