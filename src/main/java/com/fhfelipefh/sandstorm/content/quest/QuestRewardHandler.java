package com.fhfelipefh.sandstorm.content.quest;

import com.fhfelipefh.sandstorm.content.network.ClaimQuestRewardPayload;
import com.fhfelipefh.sandstorm.content.network.SyncPlayerQuestsPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class QuestRewardHandler {
    private static final Map<UUID, Set<String>> NOTIFIED_CACHE = new HashMap<>();
    private static int tickCounter = 0;

    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(ClaimQuestRewardPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> handleClaim(player, payload.questId()));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            PlayerQuestSavedData data = PlayerQuestSavedData.get(server);
            syncPlayerQuests(player, data);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            NOTIFIED_CACHE.remove(handler.getPlayer().getUUID());
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter % 20 != 0) {
                return;
            }
            PlayerQuestSavedData data = PlayerQuestSavedData.get(server.overworld());
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                checkPlayerNotifications(player, data);
            }
        });
    }

    public static void handleClaim(ServerPlayer player, String questId) {
        QuestData quest = QuestRegistry.getQuest(questId);
        if (quest == null) {
            return;
        }
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return;
        }
        PlayerQuestSavedData data = PlayerQuestSavedData.get(server);
        if (data.isClaimed(player.getUUID(), questId)) {
            return;
        }
        if (!arePrerequisitesMet(player.getUUID(), quest, data)) {
            return;
        }
        if (!hasRequiredItem(player, quest)) {
            return;
        }

        data.markClaimed(player.getUUID(), questId);

        if (quest.getRewardItem() != null && quest.rewardCount() > 0) {
            ItemStack reward = new ItemStack(quest.getRewardItem(), quest.rewardCount());
            if (!player.getInventory().add(reward)) {
                if (player.level() instanceof ServerLevel sl) {
                    player.spawnAtLocation(sl, reward);
                }
            }
        }

        player.level().playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.2f);
        syncPlayerQuests(player, data);
    }

    public static void syncPlayerQuests(ServerPlayer player, PlayerQuestSavedData data) {
        List<String> list = new ArrayList<>(data.getClaimedQuests(player.getUUID()));
        List<String> conditions = player.entityTags().stream().filter(t -> t.startsWith("sandstorm.")).toList();
        ServerPlayNetworking.send(player, new SyncPlayerQuestsPayload(list, conditions));
    }

    public static boolean arePrerequisitesMet(UUID playerUuid, QuestData quest, PlayerQuestSavedData data) {
        if (quest.prerequisiteIds().isEmpty()) {
            return true;
        }
        for (String preId : quest.prerequisiteIds()) {
            if (!data.isClaimed(playerUuid, preId)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasRequiredItem(Player player, QuestData quest) {
        if (player == null || quest == null) {
            return false;
        }
        if (quest.isConditionBased()) {
            return player.entityTags().contains(quest.conditionTag());
        }
        if (quest.getRequiredItem() == null) {
            return false;
        }
        Item req = quest.getRequiredItem();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.is(req)) {
                return true;
            }
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(req)) {
                return true;
            }
        }
        return false;
    }

    private static void checkPlayerNotifications(ServerPlayer player, PlayerQuestSavedData data) {
        UUID uuid = player.getUUID();
        Set<String> notified = NOTIFIED_CACHE.computeIfAbsent(uuid, k -> new HashSet<>());
        for (QuestData quest : QuestRegistry.getAllQuests().values()) {
            String qId = quest.id();
            if (data.isClaimed(uuid, qId) || notified.contains(qId)) {
                continue;
            }
            if (arePrerequisitesMet(uuid, quest, data) && hasRequiredItem(player, quest)) {
                notified.add(qId);
                player.sendSystemMessage(
                        Component.translatable("hud.sandstorm.quest_ready", Component.translatable(quest.titleKey())),
                        true
                );
                break;
            }
        }
    }
}
