package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DroneDockBlock extends Block {
    public static final long DEFAULT_CAPACITY = 50000L;
    public static final long DEFAULT_TRANSFER_RATE = 500L;
    public static final long INITIAL_CHARGE = 25000L;

    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(DEFAULT_CAPACITY, DEFAULT_TRANSFER_RATE, DEFAULT_TRANSFER_RATE);

    public DroneDockBlock(Properties properties) {
        super(properties);
        energyStorage.setStoredEnergy(INITIAL_CHARGE);
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable(
                    "telemetry.sandstorm.dock_status",
                    energyStorage.getStoredEnergy(),
                    energyStorage.getCapacity()
            ), true);
            level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.8f, 1.2f);
        }
        return InteractionResult.SUCCESS;
    }
}
