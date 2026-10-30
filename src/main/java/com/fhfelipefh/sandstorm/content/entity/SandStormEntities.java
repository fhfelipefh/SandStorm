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
                    .eyeHeight(4.3f)
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

    public static final ResourceKey<EntityType<?>> STUN_GRENADE_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("stun_grenade")
    );

    public static final EntityType<StunGrenadeEntity> STUN_GRENADE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("stun_grenade"),
            EntityType.Builder.<StunGrenadeEntity>of(StunGrenadeEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(STUN_GRENADE_KEY)
    );

    public static final ResourceKey<EntityType<?>> SMOKE_GRENADE_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("smoke_grenade")
    );

    public static final EntityType<SmokeGrenadeEntity> SMOKE_GRENADE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("smoke_grenade"),
            EntityType.Builder.<SmokeGrenadeEntity>of(SmokeGrenadeEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(SMOKE_GRENADE_KEY)
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

    public static final ResourceKey<EntityType<?>> SCRAP_SENTINEL_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("scrap_sentinel")
    );

    public static final EntityType<ScrapSentinelEntity> SCRAP_SENTINEL = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("scrap_sentinel"),
            EntityType.Builder.of(ScrapSentinelEntity::new, MobCategory.MONSTER)
                    .sized(0.9f, 1.6f)
                    .build(SCRAP_SENTINEL_KEY)
    );

    public static final ResourceKey<EntityType<?>> DEFECTIVE_SENTINEL_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("defective_sentinel")
    );

    public static final EntityType<DefectiveSentinelEntity> DEFECTIVE_SENTINEL = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("defective_sentinel"),
            EntityType.Builder.of(DefectiveSentinelEntity::new, MobCategory.MONSTER)
                    .sized(0.9f, 1.6f)
                    .build(DEFECTIVE_SENTINEL_KEY)
    );

    public static final ResourceKey<EntityType<?>> AQUIFER_BEETLE_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("aquifer_beetle")
    );

    public static final EntityType<AquiferBeetleEntity> AQUIFER_BEETLE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("aquifer_beetle"),
            EntityType.Builder.of(AquiferBeetleEntity::new, MobCategory.CREATURE)
                    .sized(1.4f, 0.85f)
                    .build(AQUIFER_BEETLE_KEY)
    );

    public static final ResourceKey<EntityType<?>> NOMAD_SCAVENGER_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("nomad_scavenger")
    );

    public static final EntityType<NomadScavengerEntity> NOMAD_SCAVENGER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("nomad_scavenger"),
            EntityType.Builder.of(NomadScavengerEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build(NOMAD_SCAVENGER_KEY)
    );

    public static final ResourceKey<EntityType<?>> DERELICT_AUTOMATON_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("derelict_automaton")
    );

    public static final EntityType<DerelictAutomatonEntity> DERELICT_AUTOMATON = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("derelict_automaton"),
            EntityType.Builder.of(DerelictAutomatonEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .build(DERELICT_AUTOMATON_KEY)
    );

    public static final ResourceKey<EntityType<?>> CYBER_HOUND_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("cyber_hound")
    );

    public static final EntityType<CyberHoundEntity> CYBER_HOUND = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("cyber_hound"),
            EntityType.Builder.of(CyberHoundEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 0.85f)
                    .build(CYBER_HOUND_KEY)
    );

    public static final ResourceKey<EntityType<?>> CYBER_STEED_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("cyber_steed")
    );

    public static final EntityType<CyberSteedEntity> CYBER_STEED = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("cyber_steed"),
            EntityType.Builder.of(CyberSteedEntity::new, MobCategory.CREATURE)
                    .sized(1.4f, 1.6f)
                    .eyeHeight(1.5f)
                    .build(CYBER_STEED_KEY)
    );

    public static final ResourceKey<EntityType<?>> LABORER_UNIT_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("laborer_unit")
    );

    public static final EntityType<LaborerUnitEntity> LABORER_UNIT = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("laborer_unit"),
            EntityType.Builder.of(LaborerUnitEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .build(LABORER_UNIT_KEY)
    );

    public static final ResourceKey<EntityType<?>> SCOUT_DRONE_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("scout_drone")
    );

    public static final EntityType<ScoutDroneEntity> SCOUT_DRONE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("scout_drone"),
            EntityType.Builder.of(ScoutDroneEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.99f)
                    .build(SCOUT_DRONE_KEY)
    );

    public static final ResourceKey<EntityType<?>> CRAWLER_DRONE_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("crawler_drone")
    );

    public static final EntityType<CrawlerDroneEntity> CRAWLER_DRONE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("crawler_drone"),
            EntityType.Builder.of(CrawlerDroneEntity::new, MobCategory.MONSTER)
                    .sized(1.4f, 0.9f)
                    .build(CRAWLER_DRONE_KEY)
    );

    public static final ResourceKey<EntityType<?>> CYBERNETIC_GOLEM_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("cybernetic_golem")
    );

    public static final EntityType<CyberneticGolemEntity> CYBERNETIC_GOLEM = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("cybernetic_golem"),
            EntityType.Builder.of(CyberneticGolemEntity::new, MobCategory.MISC)
                    .sized(1.4f, 2.7f)
                    .build(CYBERNETIC_GOLEM_KEY)
    );

    public static final ResourceKey<EntityType<?>> RAPPEL_HARNESS_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("rappel_harness")
    );

    public static final EntityType<RappelHarnessEntity> RAPPEL_HARNESS = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("rappel_harness"),
            EntityType.Builder.of(RappelHarnessEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.8f)
                    .build(RAPPEL_HARNESS_KEY)
    );

    public static final ResourceKey<EntityType<?>> MIRAGE_DECOY_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("mirage_decoy")
    );

    public static final EntityType<MirageDecoyEntity> MIRAGE_DECOY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("mirage_decoy"),
            EntityType.Builder.of(MirageDecoyEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.8f)
                    .build(MIRAGE_DECOY_KEY)
    );

    public static final ResourceKey<EntityType<?>> GRAZER_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("grazer")
    );

    public static final EntityType<GrazerEntity> GRAZER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("grazer"),
            EntityType.Builder.of(GrazerEntity::new, MobCategory.CREATURE)
                    .sized(1.4f, 1.6f)
                    .build(GRAZER_KEY)
    );

    public static final ResourceKey<EntityType<?>> FANGHORN_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("fanghorn")
    );

    public static final EntityType<FanghornEntity> FANGHORN = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("fanghorn"),
            EntityType.Builder.of(FanghornEntity::new, MobCategory.MONSTER)
                    .sized(1.4f, 1.7f)
                    .build(FANGHORN_KEY)
    );

    public static final ResourceKey<EntityType<?>> FROSTCLAW_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("frostclaw")
    );

    public static final EntityType<FrostclawEntity> FROSTCLAW = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("frostclaw"),
            EntityType.Builder.of(FrostclawEntity::new, MobCategory.MONSTER)
                    .sized(2.2f, 2.6f)
                    .build(FROSTCLAW_KEY)
    );

    public static final ResourceKey<EntityType<?>> GLINTHAWK_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("glinthawk")
    );

    public static final EntityType<GlinthawkEntity> GLINTHAWK = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("glinthawk"),
            EntityType.Builder.of(GlinthawkEntity::new, MobCategory.MONSTER)
                    .sized(1.5f, 1.2f)
                    .build(GLINTHAWK_KEY)
    );

    public static final ResourceKey<EntityType<?>> WATERWING_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            SandStormMod.id("waterwing")
    );

    public static final EntityType<WaterwingEntity> WATERWING = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            SandStormMod.id("waterwing"),
            EntityType.Builder.of(WaterwingEntity::new, MobCategory.CREATURE)
                    .sized(2.0f, 1.8f)
                    .build(WATERWING_KEY)
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
        FabricDefaultAttributeRegistry.register(SCRAP_SENTINEL, ScrapSentinelEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(DEFECTIVE_SENTINEL, DefectiveSentinelEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(AQUIFER_BEETLE, AquiferBeetleEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(NOMAD_SCAVENGER, NomadScavengerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(DERELICT_AUTOMATON, DerelictAutomatonEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CYBER_HOUND, CyberHoundEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CYBER_STEED, CyberSteedEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(LABORER_UNIT, LaborerUnitEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SCOUT_DRONE, ScoutDroneEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CRAWLER_DRONE, CrawlerDroneEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CYBERNETIC_GOLEM, CyberneticGolemEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RAPPEL_HARNESS, RappelHarnessEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(MIRAGE_DECOY, MirageDecoyEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GRAZER, GrazerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FANGHORN, FanghornEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FROSTCLAW, FrostclawEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GLINTHAWK, GlinthawkEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(WATERWING, WaterwingEntity.createAttributes());
    }
}
