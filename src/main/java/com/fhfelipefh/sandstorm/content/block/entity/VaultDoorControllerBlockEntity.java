package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.VaultDoorControllerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class VaultDoorControllerBlockEntity extends BlockEntity {
    private static final float TOTAL_ANIMATION_TICKS = 60.0f;
    private float progress;
    private float prevProgress;

    public VaultDoorControllerBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.VAULT_DOOR_CONTROLLER_BE, pos, state);
    }

    public VaultDoorControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        boolean open = state.hasProperty(VaultDoorControllerBlock.OPEN) && state.getValue(VaultDoorControllerBlock.OPEN);
        this.progress = open ? 1.0f : 0.0f;
        this.prevProgress = this.progress;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VaultDoorControllerBlockEntity entity) {
        entity.prevProgress = entity.progress;
        boolean open = state.getValue(VaultDoorControllerBlock.OPEN);
        float step = 1.0f / TOTAL_ANIMATION_TICKS;
        if (open && entity.progress < 1.0f) {
            entity.progress = Math.min(1.0f, entity.progress + step);
        } else if (!open && entity.progress > 0.0f) {
            entity.progress = Math.max(0.0f, entity.progress - step);
        }
    }

    public float getProgress(float partialTick) {
        return Mth.clamp(Mth.lerp(partialTick, this.prevProgress, this.progress), 0.0f, 1.0f);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putFloat("progress", this.progress);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.progress = input.getFloatOr("progress", 0.0f);
        this.prevProgress = this.progress;
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
