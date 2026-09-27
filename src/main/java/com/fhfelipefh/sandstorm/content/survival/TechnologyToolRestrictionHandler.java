package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.block.SandstoneFurnaceBlock;
import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.fhfelipefh.sandstorm.content.world.SandStormWorldHelper;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TechnologyToolRestrictionHandler {

    public static void initialize() {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            return canPlayerBreakBlock(player, state);
        });

        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (!level.isClientSide() && player != null && player.isCreative() && isSandBlock(state)) {
                Block.popResource(level, pos, new ItemStack(Blocks.SAND));
            }
        });

        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (!SandStormWorldHelper.isSandStormWorld(level)) {
                return InteractionResult.PASS;
            }
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
            if (!SandStormWorldHelper.isSandStormWorld(level)) {
                return InteractionResult.PASS;
            }
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
        if (player == null || player.isCreative() || !SandStormWorldHelper.isSandStormWorld(player.level())) {
            return true;
        }

        ItemStack mainHandItem = player.getMainHandItem();
        if (isRestrictedVanillaTool(mainHandItem.getItem())) {
            return false;
        }

        if (requiresRoboticsToBreak(state)) {
            if (isPilotingMiningRobot(player)) {
                return true;
            }
            return isAuthorizedMiningTool(mainHandItem);
        }

        return true;
    }

    public static boolean isAuthorizedMiningTool(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && isAuthorizedMiningTool(id.getNamespace(), id.getPath());
    }

    public static boolean isAuthorizedMiningTool(String namespace, String path) {
        return "sandstorm".equals(namespace) && "silicon_pickaxe".equals(path);
    }

    public static boolean requiresRoboticsToBreak(BlockState state) {
        if (state == null) {
            return false;
        }
        if (state.getBlock() instanceof CraftingTableBlock || state.getBlock() instanceof SandstoneFurnaceBlock) {
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

    public static boolean isSandBlock(BlockState state) {
        if (state == null) {
            return false;
        }
        if (state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.SUSPICIOUS_SAND)) {
            return true;
        }
        Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return id != null && "sandstorm".equals(id.getNamespace()) && "salinized_sand".equals(id.getPath());
    }
}
