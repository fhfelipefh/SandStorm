package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SandDepthProbeItem extends Item {
    public static final int SCAN_DEPTH = 5;

    public SandDepthProbeItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public int getScanDepth() {
        return SCAN_DEPTH;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockPos playerPos = player.blockPosition();

        if (level instanceof ServerLevel serverLevel) {
            int detectedCavityDepth = scanForCavities(serverLevel, playerPos);
            if (player instanceof ServerPlayer serverPlayer) {
                if (detectedCavityDepth > 0) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.sand_depth_probe.cavity"), true);
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 0.8f, 1.8f);
                } else {
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.sand_depth_probe.solid"), true);
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5f, 1.2f);
                }
            }
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.hurtAndBreak(1, player, slot);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof ServerPlayer player && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)) {
            if (level.getGameTime() % 20 == 0) {
                int detectedCavityDepth = scanForCavities(level, player.blockPosition());
                if (detectedCavityDepth > 0) {
                    player.sendSystemMessage(Component.translatable("message.sandstorm.sand_depth_probe.cavity"), true);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.35f, 1.8f);
                }
            }
        }
    }

    public static int scanForCavities(Level level, BlockPos startPos) {
        boolean passedSolid = false;
        for (int depth = 1; depth <= SCAN_DEPTH; depth++) {
            BlockPos checkPos = startPos.below(depth);
            BlockState state = level.getBlockState(checkPos);
            if (state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.GRAVEL) || state.isSolid()) {
                passedSolid = true;
            } else if (state.isAir() && passedSolid) {
                return depth;
            }
        }
        return 0;
    }
}
