package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.quest.PlayerQuestSavedData;
import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SandstormClaimCommand {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandstorm_claim")
                .executes(SandstormClaimCommand::claimAll)
                .then(Commands.literal("all")
                        .executes(SandstormClaimCommand::claimAll)
                )
                .then(Commands.argument("questId", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (String id : QuestRegistry.getAllQuests().keySet()) {
                                builder.suggest(id);
                            }
                            return builder.buildFuture();
                        })
                        .executes(SandstormClaimCommand::claimSpecific)
                )
        );

        dispatcher.register(Commands.literal("sandstorm_resgatar")
                .executes(SandstormClaimCommand::claimAll)
                .then(Commands.literal("tudo")
                        .executes(SandstormClaimCommand::claimAll)
                )
                .then(Commands.argument("questId", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (String id : QuestRegistry.getAllQuests().keySet()) {
                                builder.suggest(id);
                            }
                            return builder.buildFuture();
                        })
                        .executes(SandstormClaimCommand::claimSpecific)
                )
        );

        dispatcher.register(Commands.literal("sandstorm")
                .then(Commands.literal("claim")
                        .executes(SandstormClaimCommand::claimAll)
                        .then(Commands.literal("all")
                                .executes(SandstormClaimCommand::claimAll)
                        )
                        .then(Commands.argument("questId", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (String id : QuestRegistry.getAllQuests().keySet()) {
                                        builder.suggest(id);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(SandstormClaimCommand::claimSpecific)
                        )
                )
        );
    }

    private static int claimAll(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can claim quests."));
            return 0;
        }

        PlayerQuestSavedData data = PlayerQuestSavedData.get(player.level().getServer());
        int claimedCount = 0;
        for (QuestData quest : QuestRegistry.getAllQuests().values()) {
            if (!data.isClaimed(player.getUUID(), quest.id())
                    && QuestRewardHandler.arePrerequisitesMet(player.getUUID(), quest, data)
                    && QuestRewardHandler.hasRequiredItem(player, quest, data)) {
                QuestRewardHandler.handleClaim(player, quest.id());
                claimedCount++;
            }
        }

        if (claimedCount == 0) {
            source.sendSuccess(() -> Component.translatable("command.sandstorm.claim.none"), false);
            return 0;
        }

        int finalCount = claimedCount;
        source.sendSuccess(() -> Component.translatable("command.sandstorm.claim.success", finalCount), false);
        return claimedCount;
    }

    private static int claimSpecific(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can claim quests."));
            return 0;
        }

        String questId = StringArgumentType.getString(context, "questId");
        QuestData quest = QuestRegistry.getQuest(questId);
        if (quest == null) {
            source.sendFailure(Component.literal("Unknown quest ID: " + questId));
            return 0;
        }

        PlayerQuestSavedData data = PlayerQuestSavedData.get(player.level().getServer());
        if (data.isClaimed(player.getUUID(), questId)) {
            source.sendFailure(Component.translatable("gui.sandstorm.datapad.tooltip.already_claimed"));
            return 0;
        }
        if (!QuestRewardHandler.arePrerequisitesMet(player.getUUID(), quest, data)) {
            source.sendFailure(Component.translatable("gui.sandstorm.datapad.tooltip.prereqs_missing"));
            return 0;
        }
        if (!QuestRewardHandler.hasRequiredItem(player, quest, data)) {
            source.sendFailure(Component.translatable("gui.sandstorm.datapad.tooltip.item_missing"));
            return 0;
        }

        QuestRewardHandler.handleClaim(player, questId);
        return 1;
    }
}
