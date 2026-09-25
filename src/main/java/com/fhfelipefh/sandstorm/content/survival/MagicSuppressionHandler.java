package com.fhfelipefh.sandstorm.content.survival;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MagicSuppressionHandler {

    public static void initialize() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            BlockState state = level.getBlockState(hitResult.getBlockPos());
            if (shouldSuppressBlock(state.getBlock())) {
                if (!level.isClientSide()) {
                    if (state.is(Blocks.ANVIL) || state.is(Blocks.CHIPPED_ANVIL) || state.is(Blocks.DAMAGED_ANVIL)) {
                        player.sendSystemMessage(Component.translatable("telemetry.sandstorm.anvil_redirect"));
                    } else if (state.is(Blocks.BREWING_STAND)) {
                        player.sendSystemMessage(Component.translatable("telemetry.sandstorm.magic_suppressed_potions"));
                    } else {
                        player.sendSystemMessage(Component.translatable("telemetry.sandstorm.magic_suppressed"));
                    }
                }
                return InteractionResult.FAIL;
            }

            ItemStack held = player.getItemInHand(hand);
            if (shouldSuppressItem(held.getItem())) {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(Component.translatable("telemetry.sandstorm.magic_suppressed"));
                }
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack held = player.getItemInHand(hand);
            if (shouldSuppressItem(held.getItem())) {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(Component.translatable("telemetry.sandstorm.magic_suppressed"));
                }
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, serverLevel) -> {
            if (shouldSuppressEntityType(entity.getType())) {
                entity.discard();
            }
        });
    }

    public static boolean shouldSuppressBlock(Block block) {
        if (block == null) {
            return false;
        }
        return block == Blocks.ENCHANTING_TABLE
                || block == Blocks.BREWING_STAND
                || block == Blocks.ANVIL
                || block == Blocks.CHIPPED_ANVIL
                || block == Blocks.DAMAGED_ANVIL;
    }

    public static boolean shouldSuppressItem(Item item) {
        if (item == null) {
            return false;
        }
        return item == Items.ENCHANTING_TABLE
                || item == Items.BREWING_STAND
                || item == Items.ENCHANTED_BOOK
                || item == Items.ANVIL
                || item == Items.CHIPPED_ANVIL
                || item == Items.DAMAGED_ANVIL
                || item == Items.SPLASH_POTION
                || item == Items.LINGERING_POTION
                || item == Items.EXPERIENCE_BOTTLE;
    }

    public static boolean shouldSuppressEntityType(EntityType<?> type) {
        if (type == null) {
            return false;
        }
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (id == null) {
            return false;
        }
        return "minecraft".equals(id.getNamespace()) && "witch".equals(id.getPath());
    }
}
