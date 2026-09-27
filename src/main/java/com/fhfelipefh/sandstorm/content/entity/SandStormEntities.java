package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgBuilderEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgExcavatorEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgHarvesterEntity;
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
                    .sized(3.8f, 10.0f)
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

    public static final ResourceKey<EntityType<?>> SANDBOARD_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("sandboard")
    );

    public static final EntityType<SandboardEntity> SANDBOARD = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("sandboard"),
            EntityType.Builder.of(SandboardEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.3f)
                    .build(SANDBOARD_KEY)
    );

    public static final ResourceKey<EntityType<?>> BUILDER_DRONE_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("builder_drone")
    );

    public static final EntityType<BuilderDroneEntity> BUILDER_DRONE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("builder_drone"),
            EntityType.Builder.of(BuilderDroneEntity::new, MobCategory.MISC)
                    .sized(1.0f, 0.6f)
                    .build(BUILDER_DRONE_KEY)
    );

    public static final ResourceKey<EntityType<?>> MEDBAY_SEAT_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("medbay_seat")
    );

    public static final EntityType<MedBaySeatEntity> MEDBAY_SEAT = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("medbay_seat"),
            EntityType.Builder.<MedBaySeatEntity>of(MedBaySeatEntity::new, MobCategory.MISC)
                    .sized(0.01f, 0.01f)
                    .noSave()
                    .build(MEDBAY_SEAT_KEY)
    );

    public static final ResourceKey<EntityType<?>> CYBORG_EXCAVATOR_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("cyborg_excavator")
    );

    public static final EntityType<CyborgExcavatorEntity> CYBORG_EXCAVATOR = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("cyborg_excavator"),
            EntityType.Builder.of(CyborgExcavatorEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.95f)
                    .build(CYBORG_EXCAVATOR_KEY)
    );

    public static final ResourceKey<EntityType<?>> CYBORG_BUILDER_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("cyborg_builder")
    );

    public static final EntityType<CyborgBuilderEntity> CYBORG_BUILDER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("cyborg_builder"),
            EntityType.Builder.of(CyborgBuilderEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.95f)
                    .build(CYBORG_BUILDER_KEY)
    );

    public static final ResourceKey<EntityType<?>> CYBORG_HARVESTER_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("cyborg_harvester")
    );

    public static final EntityType<CyborgHarvesterEntity> CYBORG_HARVESTER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("cyborg_harvester"),
            EntityType.Builder.of(CyborgHarvesterEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.95f)
                    .build(CYBORG_HARVESTER_KEY)
    );

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(SANDWORM, SandwormEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CARGO_DRONE, CargoDroneEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(EXCAVATOR_VEHICLE, ExcavatorVehicleEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(MEGAZORD, MegazordEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SANDBOARD, SandboardEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(BUILDER_DRONE, BuilderDroneEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CYBORG_EXCAVATOR, CyborgExcavatorEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CYBORG_BUILDER, CyborgBuilderEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CYBORG_HARVESTER, CyborgHarvesterEntity.createAttributes());
    }
}
