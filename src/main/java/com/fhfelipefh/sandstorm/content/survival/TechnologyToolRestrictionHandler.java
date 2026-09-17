package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TechnologyToolRestrictionHandler {

    public static void initialize() {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            return canPlayerBreakBlock(player, state);
        });

        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            BlockState state = level.getBlockState(pos);
            if (!canPlayerBreakBlock(player, state)) {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(
                            Component.translatable("telemetry.sandstorm.mining_requires_robot")
                    );
                }
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (isRestrictedVanillaTool(stack.getItem())) {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(
                            Component.translatable("telemetry.sandstorm.tool_restricted")
                    );
                }
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }

    public static boolean canPlayerBreakBlock(Player player, BlockState state) {
        if (player == null || player.isCreative()) {
            return true;
        }

        ItemStack mainHandItem = player.getMainHandItem();
        if (isRestrictedVanillaTool(mainHandItem.getItem())) {
            return false;
        }

        if (requiresRoboticsToBreak(state)) {
            return isPilotingMiningRobot(player);
        }

        return true;
    }

    public static boolean requiresRoboticsToBreak(BlockState state) {
        if (state == null) {
            return false;
        }
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public static boolean isPilotingMiningRobot(Player player) {
        if (player == null) {
            return false;
        }
        return player.getVehicle() instanceof ExcavatorVehicleEntity
                || player.getVehicle() instanceof MegazordEntity;
    }

    public static boolean isRestrictedVanillaTool(Item item) {
        if (item == null) {
            return false;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null || !"minecraft".equals(id.getNamespace())) {
            return false;
        }
        return isRestrictedToolName(id.getPath());
    }

    public static boolean isRestrictedToolName(String path) {
        if (path == null || isShovel(path)) {
            return false;
        }
        return path.endsWith("_pickaxe")
                || path.endsWith("_axe")
                || path.endsWith("_sword")
                || path.endsWith("_hoe");
    }

    public static boolean isShovel(String path) {
        return path != null && path.endsWith("_shovel");
    }
}
