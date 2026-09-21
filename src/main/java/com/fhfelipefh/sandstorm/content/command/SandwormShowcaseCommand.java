package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.Vec3;

public class SandwormShowcaseCommand {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandworm_showcase")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(SandwormShowcaseCommand::execute)
        );

        dispatcher.register(Commands.literal("sandstorm")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("showcase")
                        .executes(SandwormShowcaseCommand::execute)
                )
                .then(Commands.literal("sandworm")
                        .then(Commands.literal("showcase")
                                .executes(SandwormShowcaseCommand::execute)
                        )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        Vec3 pos = source.getPosition();

        SandwormEntity worm = SandStormEntities.SANDWORM.create(level, EntitySpawnReason.COMMAND);
        if (worm != null) {
            worm.setPos(pos.x, pos.y, pos.z);
            worm.setShowcaseMode(true);
            level.addFreshEntity(worm);
            source.sendSuccess(() -> Component.literal("§6[SandStorm]§r Sandworm Showcase iniciado! O monstro repetirá todas as animações e ações em loop contínuo."), true);
            return 1;
        }

        source.sendFailure(Component.literal("§c[SandStorm] Falha ao criar Sandworm Showcase."));
        return 0;
    }
}
