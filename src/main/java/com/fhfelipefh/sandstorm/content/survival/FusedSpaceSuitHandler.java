package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.block.QuantumSleeperPodBlock;
import com.fhfelipefh.sandstorm.content.block.entity.QuantumSleeperPodBlockEntity;
import com.fhfelipefh.sandstorm.content.clone.CloneNetworkSavedData;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.item.SpaceSuitItem;
import com.fhfelipefh.sandstorm.content.quest.PlayerQuestSavedData;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import com.fhfelipefh.sandstorm.content.world.SandStormWorldHelper;
import com.fhfelipefh.sandstorm.content.world.SpaceshipLandingManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class FusedSpaceSuitHandler {

    public static void initialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerJoin(handler.getPlayer(), server));
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (entity instanceof ServerPlayer player) {
                if (tryActivateQuantumClone(player)) {
                    return false;
                }
            }
            return true;
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            for (String tag : oldPlayer.entityTags()) {
                newPlayer.addTag(tag);
            }
            MinecraftServer server = newPlayer.level().getServer();
            if (server != null) {
                PlayerQuestSavedData data = PlayerQuestSavedData.get(server);
                for (String cond : data.getCompletedConditions(newPlayer.getUUID())) {
                    newPlayer.addTag(cond);
                }
                QuestRewardHandler.syncPlayerQuests(newPlayer, data);
            }
            onPlayerRespawn(oldPlayer, newPlayer);
        });
    }

    public static boolean tryActivateQuantumClone(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return false;
        }
        String dim = player.level().dimension().identifier().toString();
        BlockPos deathPos = player.blockPosition();
        CloneNetworkSavedData cloneData = CloneNetworkSavedData.get(server);
        for (int attempts = 0; attempts < 5; attempts++) {
            Optional<CloneNetworkSavedData.ClonePodRecord> podRecordOpt = cloneData.findNearestReadyPod(player.getUUID(), deathPos, dim);
            if (podRecordOpt.isEmpty()) {
                podRecordOpt = cloneData.findAnyReadyPod(player.getUUID());
            }
            if (podRecordOpt.isEmpty()) {
                return false;
            }
            CloneNetworkSavedData.ClonePodRecord podRecord = podRecordOpt.get();
            ServerLevel targetLevel = null;
            for (ServerLevel lvl : server.getAllLevels()) {
                if (lvl.dimension().identifier().toString().equals(podRecord.dimensionId())) {
                    targetLevel = lvl;
                    break;
                }
            }
            if (targetLevel == null) {
                cloneData.updateCloneStatus(null, podRecord.pos(), podRecord.dimensionId(), false, server.overworld().getGameTime());
                continue;
            }
            BlockPos podPos = podRecord.pos();
            BlockEntity be = targetLevel.getBlockEntity(podPos);
            if (!(be instanceof QuantumSleeperPodBlockEntity podBe) || !podBe.hasClone()) {
                cloneData.updateCloneStatus(null, podPos, podRecord.dimensionId(), false, targetLevel.getGameTime());
                continue;
            }

            boolean keepInventory = Boolean.TRUE.equals(player.level().getGameRules().get(GameRules.KEEP_INVENTORY));
            if (!keepInventory) {
                dropNonSuitInventory(player);
            }

            Direction facing = targetLevel.getBlockState(podPos).getValue(QuantumSleeperPodBlock.FACING);

            player.setHealth(player.getMaxHealth());
            player.setAirSupply(player.getMaxAirSupply());
            player.removeAllEffects();
            player.clearFire();
            player.resetFallDistance();
            player.setDeltaMovement(Vec3.ZERO);
            if (player.isPassenger()) {
                player.stopRiding();
            }

            podBe.deployCloneToPlayer(player);
            SpawnSafety.teleportToPod(player, targetLevel, podPos, facing);
            enforceFusedSuit(player);
            SuitSurvivalHandler.getOrCreateSuit(player);

            if (player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                player.setGameMode(GameType.SURVIVAL);
            }

            targetLevel.playSound(null, podPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.5f);
            targetLevel.playSound(null, podPos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
            player.sendSystemMessage(Component.translatable("message.sandstorm.clone_respawned", podRecord.name()), false);
            return true;
        }
        return false;
    }

    public static void dropNonSuitInventory(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && !(stack.getItem() instanceof SpaceSuitItem)) {
                ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack.copy());
                itemEntity.setDefaultPickUpDelay();
                player.level().addFreshEntity(itemEntity);
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }
    }

    public static void onPlayerJoin(ServerPlayer player, MinecraftServer server) {
        if (player == null || !SandStormWorldHelper.isSandStormWorld(player.level())) {
            return;
        }
        SpaceshipLandingManager.ensureSpaceshipPlaced(server, player.level());
        if (!player.entityTags().contains("sandstorm.fused_suit")) {
            player.addTag("sandstorm.fused_suit");
            if (player.level().dimension() == Level.OVERWORLD) {
                BlockPos spawnPos = SpaceshipLandingManager.getCabinSpawnPos();
                SpawnSafety.teleportSafely(player, spawnPos);
            }
            equipFusedSuit(player);
            giveStarterSurvivalKit(player);
            SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player);
            suit.getEnergyStorage().setStoredEnergy(50000);
            PlayerSuitSavedData.get(server).setSuitData(player.getUUID(), 50000, 37.0);
        } else if (player.level().dimension() == Level.OVERWORLD && (!SpawnSafety.isSafePosition(player.level(), player.blockPosition()) || player.getY() <= player.level().getMinY() + 10)) {
            BlockPos spawnPos = SpaceshipLandingManager.getCabinSpawnPos();
            SpawnSafety.teleportSafely(player, spawnPos);
            SuitSurvivalHandler.getOrCreateSuit(player);
        } else {
            SuitSurvivalHandler.getOrCreateSuit(player);
        }
        enforceFusedSuit(player);
    }

    public static void giveStarterSurvivalKit(ServerPlayer player) {
        if (!player.getInventory().contains(SandStormItems.SURVIVAL_DATAPAD.getDefaultInstance())) {
            player.getInventory().add(new ItemStack(SandStormItems.SURVIVAL_DATAPAD));
        }
        player.getInventory().add(new ItemStack(SandStormItems.SPACE_RATION, 3));
        player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE, 3));
    }

    public static void onPlayerRespawn(ServerPlayer player) {
        onPlayerRespawn(player, player);
    }

    public static void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        if (newPlayer == null) {
            return;
        }
        MinecraftServer server = newPlayer.level().getServer();
        if (server != null) {
            String dim = oldPlayer.level().dimension().identifier().toString();
            BlockPos deathPos = oldPlayer.blockPosition();
            Optional<GlobalPos> deathLoc = oldPlayer.getLastDeathLocation();
            if (deathLoc.isPresent()) {
                dim = deathLoc.get().dimension().identifier().toString();
                deathPos = deathLoc.get().pos();
            }
            CloneNetworkSavedData cloneData = CloneNetworkSavedData.get(server);
            for (int attempts = 0; attempts < 5; attempts++) {
                Optional<CloneNetworkSavedData.ClonePodRecord> podRecordOpt = cloneData.findNearestReadyPod(newPlayer.getUUID(), deathPos, dim);
                if (podRecordOpt.isEmpty()) {
                    podRecordOpt = cloneData.findAnyReadyPod(newPlayer.getUUID());
                }
                if (podRecordOpt.isEmpty()) {
                    break;
                }
                CloneNetworkSavedData.ClonePodRecord podRecord = podRecordOpt.get();
                ServerLevel targetLevel = null;
                for (ServerLevel lvl : server.getAllLevels()) {
                    if (lvl.dimension().identifier().toString().equals(podRecord.dimensionId())) {
                        targetLevel = lvl;
                        break;
                    }
                }
                if (targetLevel == null) {
                    cloneData.updateCloneStatus(null, podRecord.pos(), podRecord.dimensionId(), false, server.overworld().getGameTime());
                    continue;
                }
                BlockPos podPos = podRecord.pos();
                BlockEntity be = targetLevel.getBlockEntity(podPos);
                if (!(be instanceof QuantumSleeperPodBlockEntity podBe) || !podBe.hasClone()) {
                    cloneData.updateCloneStatus(null, podPos, podRecord.dimensionId(), false, targetLevel.getGameTime());
                    continue;
                }
                Direction facing = targetLevel.getBlockState(podPos).getValue(QuantumSleeperPodBlock.FACING);
                podBe.deployCloneToPlayer(newPlayer);
                SpawnSafety.teleportToPod(newPlayer, targetLevel, podPos, facing);
                enforceFusedSuit(newPlayer);
                SuitSurvivalHandler.getOrCreateSuit(newPlayer);
                if (server.isHardcore() || newPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                    server.execute(() -> {
                        if (newPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                            newPlayer.setGameMode(GameType.SURVIVAL);
                        }
                    });
                }
                newPlayer.sendSystemMessage(Component.translatable("message.sandstorm.clone_respawned", podRecord.name()), false);
                return;
            }
        }
        if (server != null && server.isHardcore()) {
            return;
        }
        if (SandStormWorldHelper.isSandStormWorld(newPlayer.level())) {
            BlockPos spawnPos = SpaceshipLandingManager.getCabinSpawnPos();
            SpawnSafety.teleportSafely(newPlayer, spawnPos);
            equipFusedSuit(newPlayer);
            enforceFusedSuit(newPlayer);
        }
    }

    public static void equipFusedSuit(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (!isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.HEAD), EquipmentSlot.HEAD)) {
            player.setItemSlot(EquipmentSlot.HEAD, createFusedPiece(SandStormItems.SPACE_SUIT_HELMET, server));
        }
        if (!isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST)) {
            player.setItemSlot(EquipmentSlot.CHEST, createFusedPiece(SandStormItems.SPACE_SUIT_CHESTPLATE, server));
        }
        if (!isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.LEGS), EquipmentSlot.LEGS)) {
            player.setItemSlot(EquipmentSlot.LEGS, createFusedPiece(SandStormItems.SPACE_SUIT_LEGGINGS, server));
        }
        if (!isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.FEET), EquipmentSlot.FEET)) {
            player.setItemSlot(EquipmentSlot.FEET, createFusedPiece(SandStormItems.SPACE_SUIT_BOOTS, server));
        }
    }

    public static void enforceFusedSuit(ServerPlayer player) {
        if (player == null || player.isCreative() || player.isSpectator() || !SandStormWorldHelper.isSandStormWorld(player.level())) {
            return;
        }
        boolean headOk = isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.HEAD), EquipmentSlot.HEAD);
        boolean chestOk = isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST);
        boolean legsOk = isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.LEGS), EquipmentSlot.LEGS);
        boolean feetOk = isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.FEET), EquipmentSlot.FEET);

        if (headOk && chestOk && legsOk && feetOk) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                    ItemStack currentStack = player.getItemBySlot(slot);
                    if (!currentStack.isEmpty() && currentStack.getItem() instanceof SpaceSuitItem) {
                        TooltipDisplay td = currentStack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
                        if (td.shows(DataComponents.ENCHANTMENTS)) {
                            currentStack.set(DataComponents.TOOLTIP_DISPLAY, td.withHidden(DataComponents.ENCHANTMENTS, true));
                        }
                    }
                }
            }
            return;
        }

        equipFusedSuit(player);
        removeExtraSuitItemsFromInventory(player);
    }

    private static void removeExtraSuitItemsFromInventory(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof SpaceSuitItem) {
                EquipmentSlot targetSlot = getSlotForSuitItem(stack.getItem());
                if (targetSlot != null && !isMatchingSuitPiece(player.getItemBySlot(targetSlot), targetSlot)) {
                    player.setItemSlot(targetSlot, stack);
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }
        }
    }

    public static ItemStack createFusedPiece(Item item, MinecraftServer server) {
        ItemStack stack = new ItemStack(item);
        if (server != null) {
            server.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
                reg.get(Enchantments.BINDING_CURSE).ifPresent(enchantment -> stack.enchant(enchantment, 1));
                reg.get(Enchantments.VANISHING_CURSE).ifPresent(enchantment -> stack.enchant(enchantment, 1));
            });
        }
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
        stack.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.ENCHANTMENTS, true));
        return stack;
    }

    public static EquipmentSlot getSlotForSuitItem(Item item) {
        if (item instanceof SpaceSuitItem suit) {
            return suit.getArmorType().getSlot();
        }
        return null;
    }

    public static boolean isMatchingSuitPiece(ItemStack stack, EquipmentSlot slot) {
        return !stack.isEmpty() && stack.getItem() instanceof SpaceSuitItem suit && suit.getArmorType().getSlot() == slot;
    }

    public static boolean isSuitPiece(ItemStack stack, Item expected) {
        return !stack.isEmpty() && stack.is(expected);
    }
}
