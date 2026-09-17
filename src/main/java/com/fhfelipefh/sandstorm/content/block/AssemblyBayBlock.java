package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.entity.CargoDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AssemblyBayBlock extends Block {
    public static final long DEFAULT_CAPACITY = 100000L;
    public static final long DEFAULT_TRANSFER_RATE = 1000L;
    public static final long INITIAL_CHARGE = 50000L;

    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(DEFAULT_CAPACITY, DEFAULT_TRANSFER_RATE, DEFAULT_TRANSFER_RATE);

    public AssemblyBayBlock(Properties properties) {
        super(properties);
        energyStorage.setStoredEnergy(INITIAL_CHARGE);
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(SandStormItems.NANO_ACTUATOR) && energyStorage.hasEnergy(5000L)) {
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                stack.shrink(1);
                energyStorage.extractEnergy(5000L);

                CargoDroneEntity drone = new CargoDroneEntity(SandStormEntities.CARGO_DRONE, serverLevel);
                drone.setPos(pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5);
                serverLevel.addFreshEntity(drone);

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.bay_assembled_drone"), true);
                }
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.5f);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable(
                    "telemetry.sandstorm.bay_status",
                    energyStorage.getStoredEnergy(),
                    energyStorage.getCapacity()
            ), true);
            level.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 0.8f, 0.8f);
        }
        return InteractionResult.SUCCESS;
    }
}
