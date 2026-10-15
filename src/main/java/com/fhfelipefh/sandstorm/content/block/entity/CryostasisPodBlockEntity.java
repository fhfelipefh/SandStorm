package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.CryostasisPodBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

public class CryostasisPodBlockEntity extends BlockEntity {

    private UUID occupantUuid = null;
    private int freezeTimer = 0;

    public CryostasisPodBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.CRYOSTASIS_POD_BE, pos, state);
    }

    public CryostasisPodBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CryostasisPodBlockEntity be) {
        if (be.occupantUuid == null) {
            be.freezeTimer = 0;
            return;
        }

        Player player = level.getPlayerByUUID(be.occupantUuid);
        if (player == null || !player.isAlive() || player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 9.0) {
            be.occupantUuid = null;
            be.freezeTimer = 0;
            level.setBlock(pos, state.setValue(CryostasisPodBlock.OCCUPIED, false), 3);
            return;
        }

        player.teleportTo(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);

        if (level.getGameTime() % 10 == 0) {
            player.heal(1.0F);
            player.removeEffect(MobEffects.POISON);
            player.removeEffect(MobEffects.WITHER);
        }

        be.freezeTimer++;
        if (be.freezeTimer > 100) {
            player.setTicksFrozen(player.getTicksFrozen() + 6);
        }

        if (state.getValue(CryostasisPodBlock.POWERED)) {
            be.ejectPlayer(player, state.getValue(CryostasisPodBlock.FACING));
        }
    }

    public void enterPlayer(Player player) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        this.occupantUuid = player.getUUID();
        this.freezeTimer = 0;
        this.level.setBlock(this.worldPosition, this.getBlockState().setValue(CryostasisPodBlock.OCCUPIED, true), 3);
        this.level.playSound(null, this.worldPosition, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 0.9F);
        player.teleportTo(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.1, this.worldPosition.getZ() + 0.5);
        this.setChanged();
    }

    public void ejectPlayer(Player player, Direction facing) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        this.occupantUuid = null;
        this.freezeTimer = 0;
        this.level.setBlock(this.worldPosition, this.getBlockState().setValue(CryostasisPodBlock.OCCUPIED, false), 3);
        this.level.playSound(null, this.worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.4F);

        double exitX = this.worldPosition.getX() + 0.5 + facing.getStepX() * 1.1;
        double exitY = this.worldPosition.getY() + 0.1;
        double exitZ = this.worldPosition.getZ() + 0.5 + facing.getStepZ() * 1.1;
        player.teleportTo(exitX, exitY, exitZ);
        player.setDeltaMovement(facing.getStepX() * 0.45, 0.25, facing.getStepZ() * 0.45);
        this.setChanged();
    }

    public boolean isOccupiedBy(Player player) {
        return this.occupantUuid != null && this.occupantUuid.equals(player.getUUID());
    }

    public UUID getOccupantUuid() {
        return this.occupantUuid;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("freezeTimer", this.freezeTimer);
        if (this.occupantUuid != null) {
            output.putString("occupantUuid", this.occupantUuid.toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.freezeTimer = input.getIntOr("freezeTimer", 0);
        String uuidStr = input.getStringOr("occupantUuid", "");
        if (!uuidStr.isEmpty()) {
            try {
                this.occupantUuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException ignored) {
                this.occupantUuid = null;
            }
        }
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
