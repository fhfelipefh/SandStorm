package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericTerraformerBlockEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class SandstormTerraformerCommand {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandstorm")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("terraformer")
                        .then(Commands.literal("tier1")
                                .executes(ctx -> executeTier(ctx, 1, false))
                                .then(Commands.literal("lightning")
                                        .executes(ctx -> executeTier(ctx, 1, true))
                                )
                        )
                        .then(Commands.literal("tier2")
                                .executes(ctx -> executeTier(ctx, 2, false))
                                .then(Commands.literal("lightning")
                                        .executes(ctx -> executeTier(ctx, 2, true))
                                )
                        )
                        .then(Commands.literal("tier3")
                                .executes(ctx -> executeTier(ctx, 3, false))
                                .then(Commands.literal("lightning")
                                        .executes(ctx -> executeTier(ctx, 3, true))
                                )
                        )
                        .then(Commands.literal("lightning")
                                .then(Commands.argument("enabled", BoolArgumentType.bool())
                                        .executes(ctx -> executeLightningToggle(ctx, BoolArgumentType.getBool(ctx, "enabled")))
                                )
                        )
                        .then(Commands.literal("thunder")
                                .then(Commands.argument("enabled", BoolArgumentType.bool())
                                        .executes(ctx -> executeLightningToggle(ctx, BoolArgumentType.getBool(ctx, "enabled")))
                                )
                        )
                )
        );

        dispatcher.register(Commands.literal("terraformer")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("tier1")
                        .executes(ctx -> executeTier(ctx, 1, false))
                        .then(Commands.literal("lightning")
                                .executes(ctx -> executeTier(ctx, 1, true))
                        )
                )
                .then(Commands.literal("tier2")
                        .executes(ctx -> executeTier(ctx, 2, false))
                        .then(Commands.literal("lightning")
                                .executes(ctx -> executeTier(ctx, 2, true))
                        )
                )
                .then(Commands.literal("tier3")
                        .executes(ctx -> executeTier(ctx, 3, false))
                        .then(Commands.literal("lightning")
                                .executes(ctx -> executeTier(ctx, 3, true))
                        )
                )
                .then(Commands.literal("lightning")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> executeLightningToggle(ctx, BoolArgumentType.getBool(ctx, "enabled")))
                        )
                )
                .then(Commands.literal("thunder")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> executeLightningToggle(ctx, BoolArgumentType.getBool(ctx, "enabled")))
                        )
                )
        );
    }

    private static int executeTier(CommandContext<CommandSourceStack> ctx, int tier, boolean lightning) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        ServerPlayer player = source.getPlayer();
        BlockPos origin = player != null ? player.blockPosition() : BlockPos.containing(source.getPosition());

        level.setBlock(origin.east(), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
        level.setBlock(origin.west(), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
        level.setBlock(origin, SandStormBlocks.ATMOSPHERIC_TERRAFORMER.defaultBlockState(), 3);

        if (level.getBlockEntity(origin) instanceof AtmosphericTerraformerBlockEntity terraformer) {
            terraformer.setupTier(tier);
            terraformer.setLightningEnabled(lightning);
        }

        level.playSound(null, origin, SandStormSoundEvents.MEGASTRUCTURE_COMPLETE, SoundSource.BLOCKS, 1.0f, 1.0f);

        int radius = tier == 3 ? 80 : tier == 2 ? 48 : 24;
        source.sendSuccess(() -> Component.literal("§a[Terraformer] Máquina Tier " + tier + " configurada!"), true);
        source.sendSuccess(() -> Component.literal("§bRaio: " + radius + "m | Recursos e energia a 100%!"), true);
        String stormType = lightning ? "§eTempestade Elétrica (Raios ATIVADOS)!" : "§7Chuva mineral suave iniciada.";
        source.sendSuccess(() -> Component.literal(stormType), true);

        return 1;
    }

    private static int executeLightningToggle(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        ServerPlayer player = source.getPlayer();
        BlockPos origin = player != null ? player.blockPosition() : BlockPos.containing(source.getPosition());

        AtmosphericTerraformerBlockEntity found = null;
        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -5; dz <= 5; dz++) {
                    BlockPos p = origin.offset(dx, dy, dz);
                    if (level.getBlockEntity(p) instanceof AtmosphericTerraformerBlockEntity tf) {
                        found = tf;
                        break;
                    }
                }
                if (found != null) {
                    break;
                }
            }
            if (found != null) {
                break;
            }
        }

        if (found != null) {
            found.setLightningEnabled(enabled);
            level.playSound(null, found.getBlockPos(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 0.5f, enabled ? 1.4f : 0.8f);
            String msg = enabled ? "§b[Terraformer] Raios HABILITADOS!" : "§e[Terraformer] Raios DESABILITADOS!";
            source.sendSuccess(() -> Component.literal(msg), true);
            return 1;
        }

        source.sendFailure(Component.literal("§cNenhum Terraformador Atmosférico próximo!"));
        return 0;
    }
}
