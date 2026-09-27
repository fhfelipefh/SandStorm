package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SmartFluidPipeBlock;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SmartFluidPipeBlockEntity extends BlockEntity {
    public static final long PIPE_CAPACITY = FluidConstants.BUCKET;
    public static final long TRANSFER_RATE = 1000L;

    private Direction lastReceivedDirection = null;

    private final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return PIPE_CAPACITY;
        }

        @Override
        protected void onFinalCommit() {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    public SmartFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.SMART_FLUID_PIPE_BE, pos, state);
    }

    public Storage<FluidVariant> getFluidStorage(Direction side) {
        return fluidStorage;
    }

    public SingleVariantStorage<FluidVariant> getInternalStorage() {
        return fluidStorage;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SmartFluidPipeBlockEntity entity) {
        boolean movedAny = false;

        for (Direction direction : Direction.values()) {
            if (!state.getValue(SmartFluidPipeBlock.PROPERTY_BY_DIRECTION.get(direction))) {
                continue;
            }

            BlockPos neighborPos = pos.relative(direction);
            Storage<FluidVariant> neighborStorage = FluidStorage.SIDED.find(level, neighborPos, direction.getOpposite());
            if (neighborStorage == null) {
                continue;
            }

            BlockState neighborState = level.getBlockState(neighborPos);
            boolean isPipe = neighborState.getBlock() instanceof SmartFluidPipeBlock;

            if (!isPipe && entity.fluidStorage.amount < entity.fluidStorage.getCapacity()) {
                long moved = StorageUtil.move(neighborStorage, entity.fluidStorage, filter -> true, TRANSFER_RATE, null);
                if (moved > 0) {
                    entity.lastReceivedDirection = direction;
                    movedAny = true;
                }
            }

            if (entity.fluidStorage.amount > 0 && direction != entity.lastReceivedDirection) {
                long moved = StorageUtil.move(entity.fluidStorage, neighborStorage, filter -> true, TRANSFER_RATE, null);
                if (moved > 0) {
                    movedAny = true;
                }
            }
        }

        if (movedAny && level.getGameTime() % 40 == 0) {
            level.playSound(null, pos, SandStormSoundEvents.FLUID_PIPE_FLOW, SoundSource.BLOCKS, 0.15f, 1.0f);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("fluidAmount", this.fluidStorage.amount);
        output.putString("fluid", BuiltInRegistries.FLUID.getKey(this.fluidStorage.variant.getFluid()).toString());
        if (this.lastReceivedDirection != null) {
            output.putString("lastDirection", this.lastReceivedDirection.name());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        long amount = input.getLongOr("fluidAmount", 0L);
        String fluidId = input.getStringOr("fluid", "");
        if (!fluidId.isEmpty() && amount > 0L) {
            Identifier id = Identifier.tryParse(fluidId);
            if (id != null) {
                Fluid fluid = BuiltInRegistries.FLUID.getValue(id);
                if (fluid != null && fluid != Fluids.EMPTY) {
                    this.fluidStorage.variant = FluidVariant.of(fluid);
                    this.fluidStorage.amount = Math.min(amount, this.fluidStorage.getCapacity());
                }
            }
        }
        String dirName = input.getStringOr("lastDirection", "");
        this.lastReceivedDirection = dirName.isEmpty() ? null : Direction.byName(dirName);
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
