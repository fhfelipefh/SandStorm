package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.item.SpaceSuitItem;
import com.fhfelipefh.sandstorm.content.world.SpaceshipLandingManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

public class FusedSpaceSuitHandler {

    public static void initialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerJoin(handler.getPlayer(), server));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            for (String tag : oldPlayer.entityTags()) {
                newPlayer.addTag(tag);
            }
            onPlayerRespawn(newPlayer);
        });
    }

    public static void onPlayerJoin(ServerPlayer player, MinecraftServer server) {
        SpaceshipLandingManager.ensureSpaceshipPlaced(server, player.level());
        if (!player.entityTags().contains("sandstorm.fused_suit")) {
            player.addTag("sandstorm.fused_suit");
            BlockPos spawnPos = SpaceshipLandingManager.getCabinSpawnPos();
            SpawnSafety.teleportSafely(player, spawnPos);
            equipFusedSuit(player);
            giveStarterSurvivalKit(player);
            SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player);
            suit.getEnergyStorage().setStoredEnergy(50000);
            PlayerSuitSavedData.get(server).setSuitData(player.getUUID(), 50000, 37.0);
        } else if (!SpawnSafety.isSafePosition(player.level(), player.blockPosition()) || player.getY() <= player.level().getMinY() + 10) {
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
        BlockPos spawnPos = SpaceshipLandingManager.getCabinSpawnPos();
        SpawnSafety.teleportSafely(player, spawnPos);
        equipFusedSuit(player);
        enforceFusedSuit(player);
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
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        boolean headOk = isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.HEAD), EquipmentSlot.HEAD);
        boolean chestOk = isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST);
        boolean legsOk = isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.LEGS), EquipmentSlot.LEGS);
        boolean feetOk = isMatchingSuitPiece(player.getItemBySlot(EquipmentSlot.FEET), EquipmentSlot.FEET);

        if (headOk && chestOk && legsOk && feetOk) {
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
