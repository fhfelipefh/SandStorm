package com.fhfelipefh.sandstorm.content.quest;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.network.ClaimQuestRewardPayload;
import com.fhfelipefh.sandstorm.content.network.SyncPlayerQuestsPayload;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();
    private static final Map<UUID, Set<String>> NOTIFIED_CACHE = new HashMap<>();
    private static int tickCounter = 0;

    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(ClaimQuestRewardPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> {
                PlayerQuestSavedData data = PlayerQuestSavedData.get(context.server());
                if (payload.clientConditions() != null) {
                    for (String cond : payload.clientConditions()) {
                        data.markConditionCompleted(player.getUUID(), cond);
                        player.addTag(cond);
                    }
                }
                handleClaim(player, payload.questId());
            });
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
        SandStormMod.LOGGER.info("Handling claim for quest: {} for player: {}", questId, player.getName().getString());
        QuestData quest = QuestRegistry.getQuest(questId);
        if (quest == null) {
            SandStormMod.LOGGER.warn("Quest is null: {}", questId);
            return;
        }

        MinecraftServer server = player.level().getServer();
        if (server == null) {
            SandStormMod.LOGGER.warn("Server is null for player: {}", player.getName().getString());
            return;
        }
        PlayerQuestSavedData data = PlayerQuestSavedData.get(server);

        if (data.isClaimed(player.getUUID(), questId)) {
            SandStormMod.LOGGER.info("Quest {} already claimed for player {}", questId, player.getName().getString());
            syncPlayerQuests(player, data);
            return;
        }
        boolean prereqsMet = arePrerequisitesMet(player.getUUID(), quest, data);
        boolean hasItem = hasRequiredItem(player, quest, data);
        SandStormMod.LOGGER.info("Quest claim check for {} (player {}): prereqsMet={}, hasItem={}",
                questId, player.getName().getString(), prereqsMet, hasItem);

        if (!prereqsMet) {
            SandStormMod.LOGGER.warn("Prerequisites not met for quest {} and player {}", questId, player.getName().getString());
            syncPlayerQuests(player, data);
            return;
        }
        if (!hasItem) {
            SandStormMod.LOGGER.warn("Does not have required item for quest {} and player {}", questId, player.getName().getString());
            syncPlayerQuests(player, data);
            return;
        }

        SandStormMod.LOGGER.info("Marking quest {} claimed and giving reward to player {}", questId, player.getName().getString());

        data.markClaimed(player.getUUID(), questId);

        if (quest.getRewardItem() != null && quest.rewardCount() > 0) {
            ItemStack reward = new ItemStack(quest.getRewardItem(), quest.rewardCount());
            boolean added = player.getInventory().add(reward);
            if ((!added || !reward.isEmpty()) && player.level() instanceof ServerLevel sl && !reward.isEmpty()) {
                var itemEntity = player.spawnAtLocation(sl, reward);
                if (itemEntity != null) {
                    itemEntity.setNoPickUpDelay();
                    itemEntity.setTarget(player.getUUID());
                }
            }
            player.containerMenu.broadcastChanges();
            player.inventoryMenu.broadcastChanges();
        }

        Component questTitle = Component.translatable(quest.titleKey());
        Component rewardName = quest.getRewardItem() != null
                ? Component.translatable(quest.getRewardItem().getDescriptionId())
                : Component.literal("");
        player.sendSystemMessage(Component.translatable("gui.sandstorm.datapad.claimed_success", questTitle, quest.rewardCount(), rewardName));

        player.level().playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.2f);
        data.setDirty();
        server.overworld().getDataStorage().scheduleSave();
        syncPlayerQuests(player, data);
    }

    public static void syncPlayerQuests(ServerPlayer player, PlayerQuestSavedData data) {
        List<String> list = new ArrayList<>(data.getClaimedQuests(player.getUUID()));
        Set<String> conditions = new HashSet<>(data.getCompletedConditions(player.getUUID()));
        for (String tag : player.entityTags()) {
            if (tag.startsWith("sandstorm.")) {
                conditions.add(tag);
                data.markConditionCompleted(player.getUUID(), tag);
            }
        }
        for (String cond : conditions) {
            if (!player.entityTags().contains(cond)) {
                player.addTag(cond);
            }
        }
        ServerPlayNetworking.send(player, new SyncPlayerQuestsPayload(list, new ArrayList<>(conditions)));
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
        if (player != null && player.level().getServer() != null) {
            return hasRequiredItem(player, quest, PlayerQuestSavedData.get(player.level().getServer()));
        }
        return hasRequiredItem(player, quest, null);
    }

    public static boolean hasRequiredItem(Player player, QuestData quest, PlayerQuestSavedData data) {
        if (player == null || quest == null) {
            return false;
        }
        if (quest.isConditionBased()) {
            if (data != null && data.isConditionCompleted(player.getUUID(), quest.conditionTag())) {
                return true;
            }
            if (player.entityTags().contains(quest.conditionTag())) {
                if (data != null) {
                    data.markConditionCompleted(player.getUUID(), quest.conditionTag());
                }
                return true;
            }
            if ("sandstorm.battery_60".equals(quest.conditionTag())) {
                SuitPowerComponent suit = (player instanceof ServerPlayer sp) ? SuitSurvivalHandler.getOrCreateSuit(sp) : SuitSurvivalHandler.getOrCreateSuit(player.getUUID());
                if (suit.getEnergyStorage().getStoredEnergy() >= suit.getEnergyStorage().getCapacity() * 0.6) {
                    player.addTag("sandstorm.battery_60");
                    if (data != null) {
                        data.markConditionCompleted(player.getUUID(), "sandstorm.battery_60");
                    }
                    return true;
                }
            }
            return false;
        }
        if (quest.getRequiredItem() == null) {
            return false;
        }
        Item req = quest.getRequiredItem();
        for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && matchesQuestItem(quest, stack, req)) {
                return true;
            }
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && matchesQuestItem(quest, stack, req)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesQuestItem(QuestData quest, ItemStack stack, Item req) {
        if (req != null && stack.is(req)) {
            return true;
        }
        if ("sandstone_furnace".equals(quest.id())) {
            Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            return id.equals(SandStormMod.id("sandstone_furnace")) || id.equals(SandStormMod.mcId("furnace"));
        }
        return false;
    }

    public static void checkPlayerNotifications(ServerPlayer player, PlayerQuestSavedData data) {
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
                player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.7f, 1.2f);
                break;
            }
        }
    }
}
