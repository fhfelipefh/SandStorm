package com.fhfelipefh.sandstorm.content.automation;

import com.fhfelipefh.sandstorm.content.quest.PlayerQuestSavedData;
import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AutoModeManager {
    public enum Mode {
        FAST,
        STRICT
    }

    public record Status(boolean active, Mode mode, String questId, int claimed, int total, String reason) {
    }

    private static final Map<UUID, AutoState> ACTIVE = new HashMap<>();
    private static final int TICK_INTERVAL = 5;

    private AutoModeManager() {
    }

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(AutoModeManager::tick);
    }

    public static boolean start(ServerPlayer player, Mode mode) {
        if (ACTIVE.containsKey(player.getUUID())) {
            return false;
        }
        ACTIVE.put(player.getUUID(), new AutoState(mode));
        player.sendSystemMessage(Component.literal("[AUTO] iniciado no modo " + mode.name().toLowerCase()));
        return true;
    }

    public static boolean stop(ServerPlayer player) {
        if (ACTIVE.remove(player.getUUID()) == null) {
            return false;
        }
        player.sendSystemMessage(Component.literal("[AUTO] interrompido."));
        return true;
    }

    public static Status getStatus(ServerPlayer player) {
        AutoState state = ACTIVE.get(player.getUUID());
        PlayerQuestSavedData data = PlayerQuestSavedData.get(player.level().getServer());
        int claimed = (int) QuestRegistry.getAllQuests().values().stream()
                .filter(quest -> data.isClaimed(player.getUUID(), quest.id()))
                .count();
        String questId = state == null ? null : state.currentQuest;
        String reason = state == null ? "inativo" : state.reason;
        return new Status(state != null, state == null ? null : state.mode, questId, claimed,
                QuestRegistry.getAllQuests().size(), reason);
    }

    private static void tick(MinecraftServer server) {
        if (server.getTickCount() % TICK_INTERVAL != 0 || ACTIVE.isEmpty()) {
            return;
        }
        ACTIVE.entrySet().removeIf(entry -> {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                return true;
            }
            return !advance(player, entry.getValue());
        });
    }

    private static boolean advance(ServerPlayer player, AutoState state) {
        PlayerQuestSavedData data = PlayerQuestSavedData.get(player.level().getServer());
        QuestData next = null;
        for (QuestData quest : QuestRegistry.getAllQuests().values()) {
            if (!data.isClaimed(player.getUUID(), quest.id())
                    && QuestRewardHandler.arePrerequisitesMet(player.getUUID(), quest, data)) {
                next = quest;
                break;
            }
        }

        if (next == null) {
            boolean complete = data.getClaimedQuests(player.getUUID()).size() == QuestRegistry.getAllQuests().size();
            state.currentQuest = null;
            state.reason = complete ? "concluído" : "bloqueado: nenhuma quest elegível";
            if (complete) {
                player.sendSystemMessage(Component.literal("[AUTO] progressão concluída."));
                return false;
            }
            player.sendSystemMessage(Component.literal("[AUTO] bloqueado: nenhuma quest elegível."));
            return false;
        }

        state.currentQuest = next.id();
        state.reason = "processando";
        if (next.isConditionBased()) {
            if (state.mode == Mode.STRICT && !QuestRewardHandler.hasRequiredItem(player, next, data)) {
                state.reason = "aguardando condição " + next.conditionTag();
                return true;
            }
            data.markConditionCompleted(player.getUUID(), next.conditionTag());
            player.addTag(next.conditionTag());
        }

        if (!next.isConditionBased() && !QuestRewardHandler.hasRequiredItem(player, next, data)) {
            ItemStack required = new ItemStack(next.getRequiredItem());
            int slot = findTemporarySlot(player, required);
            ItemStack original = player.getInventory().getItem(slot).copy();
            player.getInventory().setItem(slot, required);
            claimAndRestore(player, next, slot, original, required);
        } else {
            QuestRewardHandler.handleClaim(player, next.id());
        }

        if (data.isClaimed(player.getUUID(), next.id())) {
            player.sendSystemMessage(Component.literal("[AUTO] concluída: " + next.id()));
        } else {
            state.reason = "falha ao concluir " + next.id();
        }
        return true;
    }

    private static void claimAndRestore(ServerPlayer player, QuestData quest, int slot, ItemStack original,
                                        ItemStack required) {
        ItemStack overflow = ItemStack.EMPTY;
        try {
            QuestRewardHandler.handleClaim(player, quest.id());
            ItemStack current = player.getInventory().getItem(slot);
            if (current.is(required.getItem()) && current.getCount() > required.getCount()) {
                overflow = current.copy();
                overflow.setCount(current.getCount() - required.getCount());
            }
        } finally {
            player.getInventory().setItem(slot, original);
            if (!overflow.isEmpty()) {
                boolean added = player.getInventory().add(overflow);
                if ((!added || !overflow.isEmpty()) && player.level() instanceof ServerLevel level) {
                    player.spawnAtLocation(level, overflow);
                }
            }
            player.containerMenu.broadcastChanges();
            player.inventoryMenu.broadcastChanges();
        }
    }

    private static int findTemporarySlot(ServerPlayer player, ItemStack required) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && !stack.is(required.getItem())) {
                return i;
            }
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (!player.getInventory().getItem(i).isEmpty()) {
                return i;
            }
        }
        return 0;
    }

    private static final class AutoState {
        private final Mode mode;
        private String currentQuest;
        private String reason = "preparando";

        private AutoState(Mode mode) {
            this.mode = mode;
        }
    }
}
