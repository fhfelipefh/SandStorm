package com.fhfelipefh.sandstorm.content.world;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;

public class VanillaMonsterSuppressionHandler {

    public static void initialize() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverLevel) -> {
            if (shouldSuppressEntity(entity)) {
                entity.discard();
            }
        });
    }

    public static boolean shouldSuppressEntity(Entity entity) {
        if (!(entity instanceof Enemy) && !(entity instanceof Monster)) {
            return false;
        }

        Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return entityId != null && "minecraft".equals(entityId.getNamespace());
    }
}
