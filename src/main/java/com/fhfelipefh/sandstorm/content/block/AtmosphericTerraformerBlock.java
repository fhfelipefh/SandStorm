package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.component.TerraformingIndexComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AtmosphericTerraformerBlock extends Block {
    public static final long DEFAULT_CAPACITY = 250000L;
    public static final long DEFAULT_TRANSFER_RATE = 2500L;
    public static final long INITIAL_CHARGE = 100000L;
    public static final long ENERGY_PER_CYCLE = 2000L;

    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(DEFAULT_CAPACITY, DEFAULT_TRANSFER_RATE, DEFAULT_TRANSFER_RATE);
    private final TerraformingIndexComponent terraformingIndex = new TerraformingIndexComponent();

    public AtmosphericTerraformerBlock(Properties properties) {
        super(properties);
        energyStorage.setStoredEnergy(INITIAL_CHARGE);
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public TerraformingIndexComponent getTerraformingIndex() {
        return terraformingIndex;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (energyStorage.hasEnergy(ENERGY_PER_CYCLE)) {
                energyStorage.extractEnergy(ENERGY_PER_CYCLE);
                terraformingIndex.addProgress(2.5);
                int radius = terraformingIndex.getDomeRadius();

                BlockPos.betweenClosedStream(pos.offset(-radius, -2, -radius), pos.offset(radius, 2, radius)).forEach(targetPos -> {
                    BlockState targetState = level.getBlockState(targetPos);
                    if (targetState.is(Blocks.SAND)) {
                        level.setBlockAndUpdate(targetPos, Blocks.GRASS_BLOCK.defaultBlockState());
                    }
                });

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable(
                            "telemetry.sandstorm.terraformer_cycle",
                            (int) terraformingIndex.getProgress(),
                            radius,
                            (int) terraformingIndex.getHumidity(),
                            (int) terraformingIndex.getTemperatureCelsius()
                    ), true);
                }
                level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0f, 1.2f);
            } else {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable(
                            "telemetry.sandstorm.terraformer_no_energy",
                            energyStorage.getStoredEnergy(),
                            ENERGY_PER_CYCLE
                    ), true);
                }
                level.playSound(null, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.8f, 0.8f);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
