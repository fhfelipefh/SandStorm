package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtmosphericAnalyzerItem extends Item {
    public static final int COOLDOWN_TICKS = 20;
    public static final double SCAN_RADIUS = 64.0;

    public AtmosphericAnalyzerItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            BlockPos playerPos = serverPlayer.blockPosition();
            AtomicBoolean found = new AtomicBoolean(false);

            BlockPos.betweenClosedStream(
                    playerPos.offset(-32, -16, -32),
                    playerPos.offset(32, 16, 32)
            ).forEach(pos -> {
                if (found.get()) {
                    return;
                }
                BlockState state = level.getBlockState(pos);
                if (state.is(SandStormBlocks.ATMOSPHERIC_TERRAFORMER)) {
                    found.set(true);
                }
            });

            if (found.get()) {
                serverPlayer.sendSystemMessage(Component.translatable(
                        "telemetry.sandstorm.analyzer_active_signal",
                        (int) SCAN_RADIUS
                ), true);
                level.playSound(null, playerPos, com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents.ATMOSPHERIC_ANALYZER_SCAN, SoundSource.PLAYERS, 1.0f, 1.5f);
            } else {
                serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.analyzer_no_signal"), true);
                level.playSound(null, playerPos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.6f, 0.8f);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
