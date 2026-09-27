package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.world.SandStormWorldHelper;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.util.EventResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BedRestrictionHandler {

    public static void initialize() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!SandStormWorldHelper.isSandStormWorld(level)) {
                return InteractionResult.PASS;
            }
            BlockState state = level.getBlockState(hitResult.getBlockPos());
            if (isBedBlock(state)) {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(
                            Component.translatable("telemetry.sandstorm.bed_disabled")
                    );
                }
                return InteractionResult.FAIL;
            }

            ItemStack held = player.getItemInHand(hand);
            if (isBedItem(held.getItem())) {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(
                            Component.translatable("telemetry.sandstorm.bed_disabled")
                    );
                }
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (!SandStormWorldHelper.isSandStormWorld(level)) {
                return InteractionResult.PASS;
            }
            ItemStack held = player.getItemInHand(hand);
            if (isBedItem(held.getItem())) {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(
                            Component.translatable("telemetry.sandstorm.bed_disabled")
                    );
                }
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        EntitySleepEvents.ALLOW_BED.register((entity, pos, state, vanillaResult) -> {
            if (!SandStormWorldHelper.isSandStormWorld(entity.level())) {
                return EventResult.PASS;
            }
            return EventResult.DENY;
        });

        EntitySleepEvents.ALLOW_SLEEPING.register((player, pos) -> {
            if (!SandStormWorldHelper.isSandStormWorld(player.level())) {
                return null;
            }
            return Player.BedSleepingProblem.OTHER_PROBLEM;
        });

        EntitySleepEvents.ALLOW_RESETTING_TIME.register(player -> {
            if (!SandStormWorldHelper.isSandStormWorld(player.level())) {
                return true;
            }
            return false;
        });

        EntitySleepEvents.ALLOW_SETTING_SPAWN.register((player, pos) -> {
            if (!SandStormWorldHelper.isSandStormWorld(player.level())) {
                return true;
            }
            return false;
        });
    }

    public static boolean isBedBlock(BlockState state) {
        if (state == null) {
            return false;
        }
        if (state.is(BlockTags.BEDS)) {
            return true;
        }
        if (state.getBlock() instanceof BedBlock) {
            return true;
        }
        Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return isBedIdentifier(id);
    }

    public static boolean isBedItem(Item item) {
        if (item == null) {
            return false;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        return isBedIdentifier(id);
    }

    public static boolean isBedIdentifier(Identifier id) {
        if (id == null || !"minecraft".equals(id.getNamespace())) {
            return false;
        }
        return isBedPath(id.getPath());
    }

    public static boolean isBedPath(String path) {
        if (path == null) {
            return false;
        }
        return path.endsWith("_bed");
    }
}
