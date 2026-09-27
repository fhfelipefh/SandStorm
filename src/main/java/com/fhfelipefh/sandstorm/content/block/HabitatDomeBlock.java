package com.fhfelipefh.sandstorm.content.block;

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

public class HabitatDomeBlock extends Block {
    private static final int PRESSURE_KPA = 101;
    private static final int OXYGEN_PERCENT = 21;

    public HabitatDomeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable(
                    "telemetry.sandstorm.habitat_status",
                    PRESSURE_KPA,
                    OXYGEN_PERCENT
            ), true);
            level.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 0.6f, 1.4f);
        }
        return InteractionResult.SUCCESS;
    }
}
