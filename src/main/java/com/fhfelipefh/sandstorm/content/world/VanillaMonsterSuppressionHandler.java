package com.fhfelipefh.sandstorm.content.world;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;

public class VanillaMonsterSuppressionHandler {

    public static final String ALLOWED_TAG = "sandstorm.allowed";

    public static void initialize() {
        ServerLevelEvents.LOAD.register((server, level) -> applyGameRules(server));
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverLevel) -> {
            if (shouldSuppressEntity(entity)) {
                entity.discard();
            }
        });
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!SandStormWorldHelper.isSandStormWorld(level)) {
                return InteractionResult.PASS;
            }
            BlockState hitState = level.getBlockState(hitResult.getBlockPos());
            ItemStack held = player.getItemInHand(hand);
            if (hitState.is(Blocks.IRON_BLOCK) && (held.is(Items.CARVED_PUMPKIN) || held.is(Items.JACK_O_LANTERN) || held.is(Items.PUMPKIN))) {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(Component.translatable("telemetry.sandstorm.iron_golem_disabled"));
                }
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }

    public static void applyGameRules(MinecraftServer server) {
        if (server == null || !SandStormWorldHelper.isSandStormWorld(server)) {
            return;
        }
        server.getGameRules().set(GameRules.SPAWN_MOBS, false, server);
        server.getGameRules().set(GameRules.SPAWN_MONSTERS, false, server);
        server.getGameRules().set(GameRules.SPAWN_PATROLS, false, server);
        server.getGameRules().set(GameRules.SPAWN_PHANTOMS, false, server);
        server.getGameRules().set(GameRules.SPAWN_WANDERING_TRADERS, false, server);
        server.getGameRules().set(GameRules.SPAWN_WARDENS, false, server);
        server.getGameRules().set(GameRules.WATER_SOURCE_CONVERSION, false, server);
        server.getGameRules().set(GameRules.BLOCK_DROPS, true, server);
    }

    public static boolean shouldSuppressEntity(Entity entity) {
        if (entity == null || !SandStormWorldHelper.isSandStormWorld(entity.level())) {
            return false;
        }

        if (!(entity instanceof Mob mob)) {
            return false;
        }

        Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        if (entityId == null || !"minecraft".equals(entityId.getNamespace())) {
            return false;
        }

        if (mob.entityTags().contains(ALLOWED_TAG) || mob.hasCustomName()) {
            return false;
        }

        EntitySpawnReason reason = mob.spawnReason();
        if (entityId != null && "iron_golem".equals(entityId.getPath())) {
            if (reason == EntitySpawnReason.TRIGGERED || reason == EntitySpawnReason.DISPENSER || reason == EntitySpawnReason.MOB_SUMMONED) {
                return true;
            }
        }

        if (reason == EntitySpawnReason.SPAWN_ITEM_USE
                || reason == EntitySpawnReason.COMMAND
                || reason == EntitySpawnReason.BREEDING
                || reason == EntitySpawnReason.DISPENSER
                || reason == EntitySpawnReason.MOB_SUMMONED
                || reason == EntitySpawnReason.TRIGGERED) {
            mob.addTag(ALLOWED_TAG);
            return false;
        }

        return true;
    }
}
