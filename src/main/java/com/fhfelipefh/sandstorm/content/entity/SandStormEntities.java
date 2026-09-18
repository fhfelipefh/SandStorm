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

    public static final ResourceKey<EntityType<?>> CARGO_DRONE_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("cargo_drone")
    );

    public static final EntityType<CargoDroneEntity> CARGO_DRONE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("cargo_drone"),
            EntityType.Builder.of(CargoDroneEntity::new, MobCategory.MISC)
                    .sized(1.2f, 0.8f)
                    .build(CARGO_DRONE_KEY)
    );

    public static final ResourceKey<EntityType<?>> EXCAVATOR_VEHICLE_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("excavator_vehicle")
    );

    public static final EntityType<ExcavatorVehicleEntity> EXCAVATOR_VEHICLE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("excavator_vehicle"),
            EntityType.Builder.of(ExcavatorVehicleEntity::new, MobCategory.MISC)
                    .sized(2.4f, 1.8f)
                    .build(EXCAVATOR_VEHICLE_KEY)
    );

    public static final ResourceKey<EntityType<?>> MEGAZORD_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("megazord")
    );

    public static final EntityType<MegazordEntity> MEGAZORD = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("megazord"),
            EntityType.Builder.of(MegazordEntity::new, MobCategory.MISC)
                    .sized(3.5f, 5.0f)
                    .build(MEGAZORD_KEY)
    );
    public static final ResourceKey<EntityType<?>> NUTRIENT_BOMB_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("nutrient_bomb")
    );

    public static final EntityType<NutrientBombEntity> NUTRIENT_BOMB = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("nutrient_bomb"),
            EntityType.Builder.<NutrientBombEntity>of(NutrientBombEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(NUTRIENT_BOMB_KEY)
    );

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(SANDWORM, SandwormEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CARGO_DRONE, CargoDroneEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(EXCAVATOR_VEHICLE, ExcavatorVehicleEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(MEGAZORD, MegazordEntity.createAttributes());
    }
}
