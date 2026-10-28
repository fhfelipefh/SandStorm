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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Iterator;
import java.util.List;

public class NeuralOverdriveModuleBlockEntity extends BlockEntity implements SingleSlotStorage<FluidVariant> {
    public static final int MAX_CAPACITY = 8000;
    private int fluidAmount = 0;
    private boolean active = false;

    public NeuralOverdriveModuleBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.NEURAL_OVERDRIVE_MODULE_BE, pos, state);
    }

    public int getFluidAmount() {
        return this.fluidAmount;
    }

    public int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public boolean isActive() {
        return this.active;
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

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, NeuralOverdriveModuleBlockEntity module) {
        if (module.fluidAmount < MAX_CAPACITY && level.getGameTime() % 10 == 0) {
            for (Direction dir : Direction.values()) {
                BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
                if (neighbor instanceof SynapticCondenserBlockEntity condenser) {
                    int space = MAX_CAPACITY - module.fluidAmount;
                    int pulled = condenser.extractFluid(space);
                    if (pulled > 0) {
                        module.fluidAmount += pulled;
                        module.setChanged();
                        break;
                    }
                }
            }
        }

        boolean working = false;
        if (module.fluidAmount > 0) {
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = pos.relative(dir);
                BlockEntity neighbor = level.getBlockEntity(neighborPos);
                BlockState neighborState = level.getBlockState(neighborPos);
                if (neighbor instanceof AbstractFurnaceBlockEntity furnace) {
                    AbstractFurnaceBlockEntity.serverTick(level, neighborPos, neighborState, furnace);
                    working = true;
                    if (level.getGameTime() % 2 == 0) {
                        module.fluidAmount -= 1;
                        module.setChanged();
                    }
                    if (level.getGameTime() % 6 == 0) {
                        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, neighborPos.getX() + 0.5, neighborPos.getY() + 0.5, neighborPos.getZ() + 0.5, 2, 0.1, 0.1, 0.1, 0.02);
                    }
                } else if (neighbor instanceof DesalinationFilterBlockEntity filter) {
                    filter.serverTick(level, neighborPos, neighborState);
                    working = true;
                    if (level.getGameTime() % 2 == 0) {
                        module.fluidAmount -= 1;
                        module.setChanged();
                    }
                    if (level.getGameTime() % 6 == 0) {
                        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, neighborPos.getX() + 0.5, neighborPos.getY() + 0.5, neighborPos.getZ() + 0.5, 2, 0.1, 0.1, 0.1, 0.02);
                    }
                }
            }
        }
        module.active = working;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("fluidAmount", this.fluidAmount);
        output.putBoolean("active", this.active);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.fluidAmount = input.getIntOr("fluidAmount", 0);
        this.active = input.getBooleanOr("active", false);
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
