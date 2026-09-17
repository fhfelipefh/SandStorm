package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class SandStormEntities {
    public static final ResourceKey<EntityType<?>> SANDWORM_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("sandworm")
    );

    public static final EntityType<SandwormEntity> SANDWORM = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("sandworm"),
            EntityType.Builder.of(SandwormEntity::new, MobCategory.MONSTER)
                    .sized(2.5f, 6.0f)
                    .build(SANDWORM_KEY)
    );

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(SANDWORM, SandwormEntity.createAttributes());
    }
}
