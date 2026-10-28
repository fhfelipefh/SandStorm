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
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.Iterator;
import java.util.List;

public class XpVacuumBlockEntity extends BlockEntity implements SingleSlotStorage<FluidVariant> {
    public static final int MAX_CAPACITY = 16000;
    private int fluidAmount = 0;

    public XpVacuumBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.XP_VACUUM_BE, pos, state);
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

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, XpVacuumBlockEntity vacuum) {
        if (vacuum.fluidAmount < MAX_CAPACITY) {
            AABB searchBox = new AABB(pos).inflate(8.0);
            List<ExperienceOrb> orbs = level.getEntitiesOfClass(ExperienceOrb.class, searchBox);
            for (ExperienceOrb orb : orbs) {
                if (orb.isAlive()) {
                    double dx = pos.getX() + 0.5 - orb.getX();
                    double dy = pos.getY() + 0.5 - orb.getY();
                    double dz = pos.getZ() + 0.5 - orb.getZ();
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq < 2.25) {
                        int orbVal = orb.getValue();
                        int space = MAX_CAPACITY - vacuum.fluidAmount;
                        int absorbed = Math.min(space, orbVal);
                        if (absorbed > 0) {
                            vacuum.insertFluid(absorbed);
                            orb.discard();
                            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0.05);
                        }
                    } else {
                        double speed = 0.18;
                        double len = Math.sqrt(distSq);
                        orb.setDeltaMovement(dx / len * speed, dy / len * speed, dz / len * speed);
                    }
                }
            }
        }

        if (vacuum.fluidAmount > 0 && level.getGameTime() % 10 == 0) {
            for (Direction dir : Direction.values()) {
                BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
                if (neighbor instanceof SynapticCondenserBlockEntity condenser) {
                    int space = condenser.getMaxCapacity() - condenser.getFluidAmount();
                    int toTransfer = Math.min(space, vacuum.fluidAmount);
                    if (toTransfer > 0) {
                        condenser.insertFluid(toTransfer);
                        vacuum.extractFluid(toTransfer);
                        break;
                    }
                }
            }
        }
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
