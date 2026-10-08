package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.automation.AutoModeManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class SandstormAutoCommand {
    private SandstormAutoCommand() {
    }

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandstorm")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("auto")
                        .then(Commands.literal("start")
                                .executes(context -> start(context, AutoModeManager.Mode.FAST))
                                .then(Commands.argument("mode", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            builder.suggest("fast");
                                            builder.suggest("strict");
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> start(context, parseMode(context)))
                                )
                        )
                        .then(Commands.literal("stop")
                                .executes(SandstormAutoCommand::stop)
                        )
                        .then(Commands.literal("status")
                                .executes(SandstormAutoCommand::status)
                        )
                )
        );
    }

    private static int start(CommandContext<CommandSourceStack> context, AutoModeManager.Mode mode) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        if (!AutoModeManager.start(player, mode)) {
            context.getSource().sendFailure(Component.literal("[AUTO] já está ativo."));
            return 0;
        }
        return 1;
    }

    private static int stop(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        if (!AutoModeManager.stop(player)) {
            context.getSource().sendFailure(Component.literal("[AUTO] não está ativo."));
            return 0;
        }
        return 1;
    }

    private static int status(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        AutoModeManager.Status status = AutoModeManager.getStatus(player);
        String mode = status.mode() == null ? "-" : status.mode().name().toLowerCase();
        String quest = status.questId() == null ? "-" : status.questId();
        context.getSource().sendSuccess(() -> Component.literal(
                "[AUTO] " + (status.active() ? "ativo" : "inativo")
                        + " | modo: " + mode
                        + " | progresso: " + status.claimed() + "/" + status.total()
                        + " | quest: " + quest
                        + " | estado: " + status.reason()), false);
        return 1;
    }

    private static AutoModeManager.Mode parseMode(CommandContext<CommandSourceStack> context) {
        String mode = StringArgumentType.getString(context, "mode");
        return "strict".equalsIgnoreCase(mode) ? AutoModeManager.Mode.STRICT : AutoModeManager.Mode.FAST;
    }

    private static ServerPlayer getPlayer(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            return player;
        }
        context.getSource().sendFailure(Component.literal("[AUTO] este comando só pode ser usado por um jogador."));
        return null;
    }
}
