package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class GeologicalSurveyorDroneItem extends Item {

    public static final int SCAN_DEPTH = 48;
    public static final int SCAN_HORIZONTAL_RADIUS = 2;
    public static final int COOLDOWN_TICKS = 60;

    public GeologicalSurveyorDroneItem(Properties properties) {
        super(properties
                .durability(100)
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);

            if (!player.isCreative()) {
                heldStack.hurtAndBreak(1, serverLevel, player instanceof ServerPlayer sp ? sp : null, item -> {});
            }

            int originX = player.getBlockX();
            int originY = player.getBlockY();
            int originZ = player.getBlockZ();
            int minY = Math.max(serverLevel.getMinY(), originY - SCAN_DEPTH);

            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.6f);

            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM,
                    player.getX(), player.getY() + 0.2, player.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0);

            boolean cryoFound = false;
            int cryoDepth = 0;
            boolean cavityFound = false;
            int cavityY = 0;
            int mineralCount = 0;

            for (int dy = originY - 3; dy >= minY; dy--) {
                for (int dx = -SCAN_HORIZONTAL_RADIUS; dx <= SCAN_HORIZONTAL_RADIUS; dx++) {
                    for (int dz = -SCAN_HORIZONTAL_RADIUS; dz <= SCAN_HORIZONTAL_RADIUS; dz++) {
                        BlockPos checkPos = new BlockPos(originX + dx, dy, originZ + dz);
                        BlockState state = serverLevel.getBlockState(checkPos);

                        if (state.is(SandStormBlocks.CRYO_REACTOR_CORE)
                                || state.is(SandStormBlocks.CRYOSTASIS_POD)
                                || state.is(SandStormBlocks.BRITTLE_PERMAFROST)
                                || state.is(SandStormBlocks.SUBGLACIAL_ICE_SPIKE)) {
                            if (!cryoFound) {
                                cryoFound = true;
                                cryoDepth = originY - dy;
                            }
                        }

                        if (state.is(SandStormBlocks.PIEZO_QUARTZ_BLOCK)
                                || state.is(SandStormBlocks.BUDDING_PIEZO_QUARTZ)
                                || state.is(SandStormBlocks.FOSSILIZED_AMBER)
                                || state.is(SandStormBlocks.DEEP_CORE_BOREHOLE)
                                || state.is(SandStormBlocks.LITHO_PLASMA_EXTRACTOR)
                                || state.is(SandStormBlocks.ANCIENT_DATA_CORE)
                                || state.is(SandStormBlocks.BURIED_TECH_RUINS)
                                || state.is(Blocks.DIAMOND_ORE)
                                || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)
                                || state.is(Blocks.ANCIENT_DEBRIS)) {
                            mineralCount++;
                        }

                        if (!cavityFound && state.isAir() && dy < originY - 6) {
                            BlockState ceiling = serverLevel.getBlockState(checkPos.above(3));
                            if (!ceiling.isAir()) {
                                cavityFound = true;
                                cavityY = dy;
                            }
                        }
                    }
                }
            }

            if (cryoFound) {
                player.sendSystemMessage(Component.translatable(
                        "hud.sandstorm.surveyor_drone.cryo_signature", cryoDepth
                ).withStyle(ChatFormatting.AQUA));
            } else if (cavityFound) {
                int depth = originY - cavityY;
                player.sendSystemMessage(Component.translatable(
                        "hud.sandstorm.surveyor_drone.cavity_found", cavityY, depth
                ).withStyle(ChatFormatting.GOLD));
            }

            if (mineralCount > 0) {
                player.sendSystemMessage(Component.translatable(
                        "hud.sandstorm.surveyor_drone.minerals_found", mineralCount
                ).withStyle(ChatFormatting.GREEN));
            }

            if (!cryoFound && !cavityFound && mineralCount == 0) {
                player.sendSystemMessage(Component.translatable(
                        "hud.sandstorm.surveyor_drone.no_anomalies"
                ).withStyle(ChatFormatting.GRAY));
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.geological_surveyor_drone.desc1").withStyle(ChatFormatting.YELLOW));
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.geological_surveyor_drone.desc2").withStyle(ChatFormatting.GOLD));
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.geological_surveyor_drone.range").withStyle(ChatFormatting.GRAY));
    }
}
