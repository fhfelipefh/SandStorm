package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.survival.SpawnSafety;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DimensionPortalRestrictionHandler {

    public static void initialize() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!SandStormWorldHelper.isSandStormWorld(level)) {
                return InteractionResult.PASS;
            }
            BlockState state = level.getBlockState(hitResult.getBlockPos());
            ItemStack held = player.getItemInHand(hand);

            if (isNetherIgnitionAttempt(state, held)) {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(
                            Component.translatable("telemetry.sandstorm.portal_disabled")
                    );
                }
                return InteractionResult.FAIL;
            }

            if (isEndPortalActivationAttempt(state, held)) {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(
                            Component.translatable("telemetry.sandstorm.end_portal_disabled")
                    );
                }
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> {
            if (!SandStormWorldHelper.isSandStormWorld(destination)) {
                return;
            }
            if (destination != null && isForbiddenDimension(destination.dimension())) {
                BlockPos cabinPos = SpaceshipLandingManager.getCabinSpawnPos();
                SpawnSafety.teleportSafely(player, cabinPos);
                player.sendSystemMessage(
                        Component.translatable("telemetry.sandstorm.dimension_travel_blocked")
                );
            }
        });
    }

    public static boolean isNetherIgnition(Block block, Item item) {
        if (block == null || item == null) {
            return false;
        }
        boolean isObsidian = block == Blocks.OBSIDIAN;
        boolean isIgniter = item == Items.FLINT_AND_STEEL || item == Items.FIRE_CHARGE;
        return isObsidian && isIgniter;
    }

    public static boolean isEndPortalActivation(Block block, Item item) {
        if (block == null || item == null) {
            return false;
        }
        return block == Blocks.END_PORTAL_FRAME && item == Items.ENDER_EYE;
    }

    public static boolean isNetherIgnitionAttempt(BlockState state, ItemStack held) {
        if (state == null || held == null || held.isEmpty()) {
            return false;
        }
        return isNetherIgnition(state.getBlock(), held.getItem());
    }

    public static boolean isEndPortalActivationAttempt(BlockState state, ItemStack held) {
        if (state == null || held == null || held.isEmpty()) {
            return false;
        }
        return isEndPortalActivation(state.getBlock(), held.getItem());
    }

    public static boolean isForbiddenDimension(ResourceKey<Level> dimension) {
        if (dimension == null) {
            return false;
        }
        return Level.NETHER.equals(dimension) || Level.END.equals(dimension);
    }
}
