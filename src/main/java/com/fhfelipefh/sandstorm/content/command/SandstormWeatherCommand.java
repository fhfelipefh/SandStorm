package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.network.SandstormWeatherPayload;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

public class SandstormWeatherCommand {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandstorm")
                .then(Commands.literal("start")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(SandstormWeatherCommand::startDefault)
                        .then(Commands.argument("durationTicks", IntegerArgumentType.integer(20))
                                .executes(SandstormWeatherCommand::startWithDuration)
                                .then(Commands.argument("intensity", DoubleArgumentType.doubleArg(0.05, 1.0))
                                        .executes(SandstormWeatherCommand::startWithDurationAndIntensity)
                                )
                        )
                )
                .then(Commands.literal("stop")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(SandstormWeatherCommand::stop)
                )
                .then(Commands.literal("toggle")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(SandstormWeatherCommand::toggle)
                )
                .then(Commands.literal("weather")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("start")
                                .executes(SandstormWeatherCommand::startDefault)
                                .then(Commands.argument("durationTicks", IntegerArgumentType.integer(20))
                                        .executes(SandstormWeatherCommand::startWithDuration)
                                        .then(Commands.argument("intensity", DoubleArgumentType.doubleArg(0.05, 1.0))
                                                .executes(SandstormWeatherCommand::startWithDurationAndIntensity)
                                        )
                                )
                        )
                        .then(Commands.literal("stop")
                                .executes(SandstormWeatherCommand::stop)
                        )
                        .then(Commands.literal("toggle")
                                .executes(SandstormWeatherCommand::toggle)
                        )
                )
        );
    }

    public static int startDefault(CommandContext<CommandSourceStack> context) {
        return start(context.getSource(), 6000, 0.85);
    }

    public static int startWithDuration(CommandContext<CommandSourceStack> context) {
        int duration = IntegerArgumentType.getInteger(context, "durationTicks");
        return start(context.getSource(), duration, 0.85);
    }

    public static int startWithDurationAndIntensity(CommandContext<CommandSourceStack> context) {
        int duration = IntegerArgumentType.getInteger(context, "durationTicks");
        double intensity = DoubleArgumentType.getDouble(context, "intensity");
        return start(context.getSource(), duration, intensity);
    }

    public static int start(CommandSourceStack source, int durationTicks, double intensity) {
        ServerLevel level = source.getLevel();
        SandstormWeatherHandler.triggerSandstorm(durationTicks, intensity);
        SandstormWeatherPayload payload = new SandstormWeatherPayload(true, intensity);
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, payload);
        }
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "§6[SandStorm]§r Tempestade de areia iniciada! (Duração: %d ticks, Intensidade: %.2f)", durationTicks, intensity)), true);
        return 1;
    }

    public static int stop(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        SandstormWeatherHandler.stopSandstorm();
        SandstormWeatherPayload payload = new SandstormWeatherPayload(false, 0.0);
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, payload);
        }
        source.sendSuccess(() -> Component.literal("§6[SandStorm]§r Tempestade de areia interrompida."), true);
        return 1;
    }

    public static int toggle(CommandContext<CommandSourceStack> context) {
        if (SandstormWeatherHandler.getWeather().isActive()) {
            return stop(context);
        } else {
            return startDefault(context);
        }
    }
}
