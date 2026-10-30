package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.block.AcousticDefensePylonBlock;
import com.fhfelipefh.sandstorm.content.block.AtmosphericCarbonExtractorBlock;
import com.fhfelipefh.sandstorm.content.block.AutonomousSonicTurretBlock;
import com.fhfelipefh.sandstorm.content.block.BiosphereDoorBlock;
import com.fhfelipefh.sandstorm.content.block.CompactSlabPistonBlock;
import com.fhfelipefh.sandstorm.content.block.CorrosiveChitinSpikeWallBlock;
import com.fhfelipefh.sandstorm.content.block.CryoFreezeMineBlock;
import com.fhfelipefh.sandstorm.content.block.CryoReactorCoreBlock;
import com.fhfelipefh.sandstorm.content.block.ElectricArcFurnaceBlock;
import com.fhfelipefh.sandstorm.content.block.HeavyKineticPistonBlock;
import com.fhfelipefh.sandstorm.content.block.HoloLaserTripwireBlock;
import com.fhfelipefh.sandstorm.content.block.KineticEjectorTrapBlock;
import com.fhfelipefh.sandstorm.content.block.KineticRailgunBlock;
import com.fhfelipefh.sandstorm.content.block.MicroSingularityTrapBlock;
import com.fhfelipefh.sandstorm.content.block.PiezoShockTrapBlock;
import com.fhfelipefh.sandstorm.content.block.PlasmaShieldGeneratorBlock;
import com.fhfelipefh.sandstorm.content.block.RappelAnchorBlock;
import com.fhfelipefh.sandstorm.content.block.SandMaglevRailBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SerratedJawTrapBlock;
import com.fhfelipefh.sandstorm.content.block.SolidStateAccumulatorBlock;
import com.fhfelipefh.sandstorm.content.block.SolidStateAccumulatorManager;
import com.fhfelipefh.sandstorm.content.block.TectonicKineticPistonBlock;
import com.fhfelipefh.sandstorm.content.block.ThermalVentTrapBlock;
import com.fhfelipefh.sandstorm.content.block.VaultDoorControllerBlock;
import com.fhfelipefh.sandstorm.content.block.VaultDoorDummyBlock;
import com.fhfelipefh.sandstorm.content.block.VaultWallPanelBlock;
import com.fhfelipefh.sandstorm.content.block.VerticalConnection;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.block.entity.AcousticDefensePylonBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.AmnioticIncubatorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericCarbonExtractorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericTerraformerBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.AutonomousSonicTurretBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.CryogenicAtmosphericChillerBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.ElectricArcFurnaceBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.KineticRailgunBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.KineticShieldGeneratorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.MegastructureConstructorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.OrbitalGroundStationBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.OrbitalMassDriverBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.PlasmaShieldGeneratorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.RappelAnchorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SandstoneCrateBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SandstoneFurnaceBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SolidStateAccumulatorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SubspaceGatewayBlockEntity;
import com.fhfelipefh.sandstorm.content.entity.BuilderDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CargoDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CyberSteedEntity;
import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
import com.fhfelipefh.sandstorm.content.entity.FanghornEntity;
import com.fhfelipefh.sandstorm.content.entity.FrostclawEntity;
import com.fhfelipefh.sandstorm.content.entity.GlinthawkEntity;
import com.fhfelipefh.sandstorm.content.entity.GrazerEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.fhfelipefh.sandstorm.content.entity.MirageDecoyEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.entity.SandboardEntity;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.entity.WaterwingEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgBuilderEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgExcavatorEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgHarvesterEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSwarmManager;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.storage.QuantumAccessTerminalBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskDriveBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskDriveBlockEntity;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskStorage;
import com.fhfelipefh.sandstorm.content.storage.QuantumNetworkControllerBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumNetworkControllerBlockEntity;
import com.fhfelipefh.sandstorm.content.storage.StoredItemEntry;
import com.fhfelipefh.sandstorm.content.world.dungeon.AbandonedExcavationHiveDungeon;
import com.fhfelipefh.sandstorm.content.world.dungeon.AbandonedMilitaryLabDungeon;
import com.fhfelipefh.sandstorm.content.world.dungeon.AncientBunkerComplexDungeon;
import com.fhfelipefh.sandstorm.content.world.dungeon.MillennialRepositoryDungeon;
import com.fhfelipefh.sandstorm.content.world.dungeon.SubglacialCryoVaultDungeon;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ShowcaseAutomation {

    private static final int CHECKERBOARD_START_Z = 20;
    private static final Set<UUID> GENERATED_PLAYERS = new HashSet<>();
    private static final List<ShowcaseTeleportPad> TELEPORT_PADS = new ArrayList<>();
    private static final Map<UUID, Long> TELEPORT_COOLDOWNS = new HashMap<>();
    private static BlockPos regeneratingRockPos = null;
    private static CyborgExcavatorEntity loopMiner = null;
    private static final List<BlockPos> FARM_CROP_POSITIONS = new ArrayList<>();
    private static final List<CyborgHarvesterEntity> ACTIVE_FARMERS = new ArrayList<>();
    private static CyborgBuilderEntity loopBuilder = null;
    private static final List<BlockPos> BUILDER_CONSTRUCTION_POSITIONS = new ArrayList<>();
    private static int builderResetCooldown = 0;
    private static final List<ShowcasePatrolDrone> ACTIVE_DRONES = new ArrayList<>();
    private static boolean showcaseActive = false;
    private static BlockPos defenseKineticShieldPos = null;
    private static BlockPos defenseSonicTurretPos = null;
    private static BlockPos defensePlasmaShieldPos = null;
    private static BlockPos defensePylonPos = null;
    private static BlockPos defenseThermalVentPos = null;
    private static BlockPos defenseCryoMinePos = null;
    private static BlockPos defenseSingularityPos = null;
    private static BlockPos defenseMiragePos = null;
    private static BlockPos defenseEjectorPos = null;
    private static UUID defenseSonicTargetId = null;
    private static UUID defenseMirageDecoyId = null;

    private record ShowcaseTeleportPad(BlockPos triggerPos, BlockPos destination, String label) {}

    private record ShowcasePatrolDrone(CargoDroneEntity drone, List<BlockPos> waypoints, int[] targetIndex) {}

    public record ShowcaseFootprint(String name, int minX, int maxX, int minZ, int maxZ) {
        public boolean intersects(ShowcaseFootprint other) {
            return minX <= other.maxX && maxX >= other.minX && minZ <= other.maxZ && maxZ >= other.minZ;
        }
    }

    public static List<ShowcaseFootprint> getShowcaseAttractionFootprints() {
        int biosphereRadius = AtmosphericTerraformerBlockEntity.TIER1_RADIUS;
        int biosphereFrontZ = getBiosphereFrontZ();
        return List.of(
                new ShowcaseFootprint("Praça Central", -10, 10, -10, 10),
                new ShowcaseFootprint("Terminal de Trânsito", -53, -30, -21, 4),
                new ShowcaseFootprint("Circuito Maglev", -106, -62, -16, 16),
                new ShowcaseFootprint("Indústria de Carbono", 23, 47, -37, -13),
                new ShowcaseFootprint("Computador Quântico", 78, 92, -12, 2),
                new ShowcaseFootprint("Domo de Megaestruturas", 39, 71, 2, 34),
                new ShowcaseFootprint("Cofre Blindado", 81, 89, -49, -40),
                new ShowcaseFootprint("Portal Subespacial", 106, 114, -49, -41),
                new ShowcaseFootprint("Estação de Rapel", 38, 52, -72, -58),
                new ShowcaseFootprint("Campo de Pistões", 36, 64, -118, -82),
                new ShowcaseFootprint("Polígono de Defesa", -90, -44, -118, -34),
                new ShowcaseFootprint("Complexo de Ciborgues", -20, 20, -115, -80),
                new ShowcaseFootprint("Arena do Sandworm", -26, 26, -196, -144),
                new ShowcaseFootprint("Base Orbital", 108, 122, 13, 27),
                new ShowcaseFootprint("Hipódromo Cibernético", -100, -50, 25, 58),
                new ShowcaseFootprint("Biosferas", -130, 130, biosphereFrontZ - biosphereRadius - 8,
                        biosphereFrontZ + 60 + biosphereRadius + 10)
        );
    }

    public static void validateShowcaseLayout() {
        List<ShowcaseFootprint> footprints = getShowcaseAttractionFootprints();
        for (int index = 0; index < footprints.size(); index++) {
            for (int otherIndex = index + 1; otherIndex < footprints.size(); otherIndex++) {
                ShowcaseFootprint first = footprints.get(index);
                ShowcaseFootprint second = footprints.get(otherIndex);
                if (first.intersects(second)) {
                    throw new IllegalStateException("Showcase attractions overlap: " + first.name() + " and " + second.name());
                }
            }
        }
    }

    public static boolean isShowcaseActive() {
        return showcaseActive;
    }

    public static void initialize() {
        ShowcaseCinematicManager.initialize();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            String levelName = player.level().getServer().getWorldData().getLevelName();
            if (levelName != null && levelName.toLowerCase().contains("showcase")) {
                ServerLevel level = (ServerLevel) player.level();
                cleanupShowcaseDroppedItems(level);
                if (isShowcaseAlreadyBuilt(level)) {
                    showcaseActive = true;
                    TELEPORT_PADS.clear();
                    CyborgSwarmManager.getInstance().releaseAll();
                    buildTransitTerminal(level, new BlockPos(0, 160, 0));
                    buildBlockCheckerboard(level, new BlockPos(0, 160, 0));
                    buildAutonomousCyborgFacility(level, new BlockPos(0, 160, 0), player);
                    buildPistonTestingRange(level, new BlockPos(-50, 160, -65));
                    buildPistonWalkway(level, new BlockPos(0, 160, 0));
                    preparePlayerForShowcase(level, player);
                } else if (!GENERATED_PLAYERS.contains(player.getUUID())) {
                    GENERATED_PLAYERS.add(player.getUUID());
                    buildShowcase(level, player);
                }
                ShowcaseCinematicManager.scheduleAutoTourIfRequested(player);
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long tick = server.getTickCount();
            if (tick % 100 == 0 && showcaseActive) {
                ServerLevel overworld = server.overworld();
                if (overworld != null) {
                    cleanupShowcaseDroppedItems(overworld);
                }
            }
            if (showcaseActive) {
                ServerLevel overworld = server.overworld();
                if (overworld != null) {
                    tickDefenseDemos(overworld, tick);
                }
            }
            if (tick % 40 == 0 && regeneratingRockPos != null) {
                ServerLevel level = server.overworld();
                if (level != null && level.isLoaded(regeneratingRockPos)) {
                    if (level.getBlockState(regeneratingRockPos).isAir()) {
                        level.setBlock(regeneratingRockPos, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);
                    }
                    if (loopMiner != null && loopMiner.isAlive()) {
                        loopMiner.setEnergy(50000);
                    }
                }
            }

            if (tick % 20 == 0 && !FARM_CROP_POSITIONS.isEmpty()) {
                ServerLevel overworld = server.overworld();
                if (overworld != null) {
                    for (BlockPos cropPos : FARM_CROP_POSITIONS) {
                        if (overworld.isLoaded(cropPos)) {
                            BlockState state = overworld.getBlockState(cropPos);
                            if (state.getBlock() instanceof CropBlock cropBlock) {
                                if (!cropBlock.isMaxAge(state)) {
                                    overworld.setBlock(cropPos, cropBlock.getStateForAge(cropBlock.getMaxAge()), 3);
                                    overworld.sendParticles(ParticleTypes.HAPPY_VILLAGER, cropPos.getX() + 0.5, cropPos.getY() + 0.3, cropPos.getZ() + 0.5, 3, 0.2, 0.1, 0.2, 0.05);
                                }
                            }
                        }
                    }
                }
            }

            if (tick % 30 == 0 && !ACTIVE_FARMERS.isEmpty()) {
                for (CyborgHarvesterEntity farmer : ACTIVE_FARMERS) {
                    if (farmer.isAlive()) {
                        farmer.setEnergy(50000);
                        for (int i = 0; i < farmer.getInventory().getContainerSize(); i++) {
                            if (farmer.getInventory().getItem(i).getCount() >= 32) {
                                farmer.getInventory().setItem(i, ItemStack.EMPTY);
                            }
                        }
                    }
                }
            }

            if (tick % 20 == 0) {
                ServerLevel overworld = server.overworld();
                if (overworld != null) {
                    if (BUILDER_CONSTRUCTION_POSITIONS.isEmpty()) {
                        BlockPos bCenter = new BlockPos(0, 160, -104);
                        for (int y = 1; y <= 2; y++) {
                            for (int x = 0; x <= 3; x++) {
                                BUILDER_CONSTRUCTION_POSITIONS.add(bCenter.offset(x, y, -1));
                            }
                        }
                    }

                    if (loopBuilder == null || !loopBuilder.isAlive()) {
                        AABB builderBox = new AABB(-10.0, 159.0, -112.0, 10.0, 166.0, -96.0);
                        List<CyborgBuilderEntity> existing = overworld.getEntitiesOfClass(CyborgBuilderEntity.class, builderBox);
                        BlockPos bCenter = new BlockPos(0, 160, -104);
                        if (!existing.isEmpty()) {
                            loopBuilder = existing.get(0);
                        } else {
                            CyborgBuilderEntity newBuilder = new CyborgBuilderEntity(SandStormEntities.CYBORG_BUILDER, overworld);
                            newBuilder.setPos(bCenter.getX() + 0.5, 161.0, bCenter.getZ() + 1.5);
                            newBuilder.setPersistenceRequired();
                            newBuilder.setCustomName(Component.literal("§bAndroide Construtor Autônomo"));
                            newBuilder.setCustomNameVisible(true);
                            overworld.addFreshEntity(newBuilder);
                            loopBuilder = newBuilder;
                        }
                        loopBuilder.setEnergy(50000);
                        loopBuilder.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
                        loopBuilder.setZoneMin(bCenter.offset(0, 1, -1));
                        loopBuilder.setZoneMax(bCenter.offset(3, 2, -1));
                    }

                    if (loopBuilder != null && loopBuilder.isAlive()) {
                        loopBuilder.setEnergy(50000);
                        boolean hasBlocks = false;
                        for (int i = 0; i < loopBuilder.getInventory().getContainerSize(); i++) {
                            ItemStack st = loopBuilder.getInventory().getItem(i);
                            if (!st.isEmpty() && st.getItem() instanceof BlockItem) {
                                hasBlocks = true;
                                break;
                            }
                        }
                        if (!hasBlocks) {
                            loopBuilder.getInventory().addItem(new ItemStack(Blocks.CUT_SANDSTONE, 32));
                        }

                        boolean allBuilt = true;
                        for (BlockPos pos : BUILDER_CONSTRUCTION_POSITIONS) {
                            if (overworld.isLoaded(pos) && overworld.isEmptyBlock(pos)) {
                                allBuilt = false;
                                break;
                            }
                        }

                        if (allBuilt) {
                            builderResetCooldown++;
                            if (builderResetCooldown == 1 && !BUILDER_CONSTRUCTION_POSITIONS.isEmpty()) {
                                BlockPos lead = BUILDER_CONSTRUCTION_POSITIONS.get(0);
                                overworld.playSound(null, lead, SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.BLOCKS, 1.0f, 1.8f);
                                for (BlockPos pos : BUILDER_CONSTRUCTION_POSITIONS) {
                                    overworld.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4, 0.2, 0.2, 0.2, 0.05);
                                }
                            }
                            if (builderResetCooldown >= 3) {
                                builderResetCooldown = 0;
                                if (!BUILDER_CONSTRUCTION_POSITIONS.isEmpty()) {
                                    BlockPos lead = BUILDER_CONSTRUCTION_POSITIONS.get(0);
                                    overworld.playSound(null, lead, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.2f, 0.85f);
                                    overworld.playSound(null, lead, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.BLOCKS, 0.9f, 1.3f);
                                }
                                for (BlockPos pos : BUILDER_CONSTRUCTION_POSITIONS) {
                                    if (overworld.isLoaded(pos)) {
                                        overworld.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                                        overworld.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 2, 0.3, 0.3, 0.3, 0.02);
                                        overworld.sendParticles(ParticleTypes.SONIC_BOOM, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0.0, 0.0, 0.0, 0.0);
                                        overworld.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.05);
                                    }
                                }
                                CyborgSwarmManager.getInstance().releaseAll();
                                loopBuilder.getInventory().clearContent();
                                loopBuilder.getInventory().addItem(new ItemStack(Blocks.CUT_SANDSTONE, 64));
                                if (!BUILDER_CONSTRUCTION_POSITIONS.isEmpty()) {
                                    overworld.playSound(null, BUILDER_CONSTRUCTION_POSITIONS.get(0), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.BLOCKS, 1.0f, 1.2f);
                                    overworld.playSound(null, BUILDER_CONSTRUCTION_POSITIONS.get(0), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0f, 1.0f);
                                }
                            }
                        } else {
                            builderResetCooldown = 0;
                        }
                    }
                }
            }

            if (tick % 30 == 0 && ACTIVE_DRONES.isEmpty()) {
                ServerLevel overworld = server.overworld();
                if (overworld != null) {
                    AABB colonyBox = new AABB(-30.0, 158.0, -120.0, 30.0, 172.0, -78.0);
                    List<CargoDroneEntity> existing = overworld.getEntitiesOfClass(CargoDroneEntity.class, colonyBox);
                    if (!existing.isEmpty()) {
                        for (int i = 0; i < existing.size(); i++) {
                            CargoDroneEntity d = existing.get(i);
                            List<BlockPos> wps = (i % 2 == 0)
                                    ? List.of(new BlockPos(10, 163, -92), new BlockPos(0, 164, -104), new BlockPos(0, 163, -110), new BlockPos(0, 164, -88))
                                    : List.of(new BlockPos(-10, 163, -92), new BlockPos(0, 164, -98), new BlockPos(10, 163, -92), new BlockPos(0, 163, -110));
                            ACTIVE_DRONES.add(new ShowcasePatrolDrone(d, wps, new int[]{0}));
                        }
                    }
                }
            }

            if (!ACTIVE_DRONES.isEmpty()) {
                for (ShowcasePatrolDrone patrol : ACTIVE_DRONES) {
                    CargoDroneEntity drone = patrol.drone();
                    if (!drone.isAlive()) {
                        continue;
                    }
                    drone.getEnergyStorage().receiveEnergy(100L);
                    drone.setNoGravity(true);
                    List<BlockPos> wps = patrol.waypoints();
                    int curIdx = patrol.targetIndex()[0];
                    BlockPos target = wps.get(curIdx);
                    double tx = target.getX() + 0.5;
                    double ty = target.getY() + 0.5;
                    double tz = target.getZ() + 0.5;
                    double dx = tx - drone.getX();
                    double dy = ty - drone.getY();
                    double dz = tz - drone.getZ();
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq < 1.0) {
                        int nextIdx = (curIdx + 1) % wps.size();
                        patrol.targetIndex()[0] = nextIdx;
                        if (nextIdx == 0) {
                            drone.setCargoStack(new ItemStack(Items.GOLDEN_CARROT, 8));
                        } else if (nextIdx == 1) {
                            drone.setCargoStack(new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 4));
                        } else if (nextIdx == 2) {
                            drone.setCargoStack(new ItemStack(SandStormItems.CIRCUIT_BOARD, 4));
                        }
                    } else {
                        double dist = Math.sqrt(distSq);
                        double speed = 0.20;
                        double moveDist = Math.min(dist, speed);
                        double mx = (dx / dist) * moveDist;
                        double my = (dy / dist) * moveDist;
                        double mz = (dz / dist) * moveDist;
                        drone.setPos(drone.getX() + mx, drone.getY() + my, drone.getZ() + mz);
                        drone.setDeltaMovement(mx, my, mz);
                        float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
                        drone.setYRot(targetYaw);
                        drone.yRotO = targetYaw;
                        drone.yHeadRot = targetYaw;
                        if (drone.level() instanceof ServerLevel sl) {
                            if (tick % 2 == 0) {
                                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, drone.getX(), drone.getY() + 0.1, drone.getZ(), 1, 0.04, 0.04, 0.04, 0.01);
                            }
                            if (tick % 4 == 0) {
                                sl.sendParticles(ParticleTypes.CLOUD, drone.getX(), drone.getY() - 0.1, drone.getZ(), 1, 0.02, 0.01, 0.02, 0.01);
                            }
                        }
                    }
                }
            }

            if (!TELEPORT_PADS.isEmpty()) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    long lastTp = TELEPORT_COOLDOWNS.getOrDefault(player.getUUID(), 0L);
                    if (tick - lastTp < 25) {
                        continue;
                    }
                    ServerLevel level = (ServerLevel) player.level();
                    for (ShowcaseTeleportPad pad : TELEPORT_PADS) {
                        double distX = Math.abs(player.getX() - (pad.triggerPos().getX() + 0.5));
                        double distZ = Math.abs(player.getZ() - (pad.triggerPos().getZ() + 0.5));
                        double distY = Math.abs(player.getY() - pad.triggerPos().getY());
                        if (distX <= 0.35 && distZ <= 0.35 && distY <= 0.8) {
                            TELEPORT_COOLDOWNS.put(player.getUUID(), tick);
                            BlockPos dest = pad.destination();
                            player.teleportTo(level, dest.getX() + 0.5, dest.getY() + 0.1, dest.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot(), true);
                            level.playSound(null, player.blockPosition(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.8f, 1.2f);
                            player.sendSystemMessage(Component.literal("§6[SandStorm] §aTeleportado para: §f" + pad.label()), true);
                            break;
                        }
                    }
                }
            }
        });
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandstorm")
                .then(Commands.literal("showcase")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCommand)
                )
                .then(Commands.literal("computer")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeComputerCommand)
                )
                .then(Commands.literal("biospheres")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeBiospheresCommand)
                )
                .then(Commands.literal("vault")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeVaultCommand)
                        .then(Commands.literal("build")
                                .executes(ShowcaseAutomation::executeVaultBuildCommand)
                        )
                )
                .then(Commands.literal("gateway")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeGatewayCommand)
                )
                .then(Commands.literal("portal")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeGatewayCommand)
                )
                .then(Commands.literal("rappel")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeRappelCommand)
                )
                .then(Commands.literal("defense")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeDefenseCommand)
                )
                .then(Commands.literal("sandworm")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeWormCommand)
                )
                .then(Commands.literal("worm")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeWormCommand)
                )
                .then(Commands.literal("orbital")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeOrbitalCommand)
                )
                .then(Commands.literal("carbon")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCarbonCommand)
                )
                .then(Commands.literal("extractor")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCarbonCommand)
                )
                .then(Commands.literal("furnace")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCarbonCommand)
                )
                .then(Commands.literal("induction")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCarbonCommand)
                )
                .then(Commands.literal("steed")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeSteedCommand)
                )
                .then(Commands.literal("pistons")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executePistonsCommand)
                )
                .then(Commands.literal("horse")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeSteedCommand)
                )
                .then(Commands.literal("military_lab")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, "military_lab"))
                )
                .then(Commands.literal("cryo_vault")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, "cryo_vault"))
                )
                .then(Commands.literal("dungeon")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, "west"))
                        .then(Commands.argument("sector", StringArgumentType.word())
                                .suggests((c, b) -> suggestDungeonSectors(b))
                                .executes(ctx -> executeDungeonTeleportCommand(ctx, StringArgumentType.getString(ctx, "sector")))
                        )
                )
                .then(Commands.literal("cinematic")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> ShowcaseCinematicManager.startCinematic(ctx.getSource(), "dungeon"))
                        .then(Commands.literal("stop")
                                .executes(ctx -> ShowcaseCinematicManager.stopCinematic(ctx.getSource()))
                        )
                        .then(Commands.argument("route", StringArgumentType.word())
                                .suggests((c, b) -> suggestCinematicRoutes(b))
                                .executes(ctx -> ShowcaseCinematicManager.startCinematic(ctx.getSource(), StringArgumentType.getString(ctx, "route")))
                        )
                )
        );

        dispatcher.register(Commands.literal("sandstorm_debug")
                .then(Commands.literal("showcase")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCommand)
                )
                .then(Commands.literal("computer")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeComputerCommand)
                )
                .then(Commands.literal("biospheres")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeBiospheresCommand)
                )
                .then(Commands.literal("vault")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeVaultCommand)
                        .then(Commands.literal("build")
                                .executes(ShowcaseAutomation::executeVaultBuildCommand)
                        )
                )
                .then(Commands.literal("gateway")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeGatewayCommand)
                )
                .then(Commands.literal("portal")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeGatewayCommand)
                )
                .then(Commands.literal("rappel")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeRappelCommand)
                )
                .then(Commands.literal("defense")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeDefenseCommand)
                )
                .then(Commands.literal("sandworm")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeWormCommand)
                )
                .then(Commands.literal("worm")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeWormCommand)
                )
                .then(Commands.literal("orbital")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeOrbitalCommand)
                )
                .then(Commands.literal("carbon")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCarbonCommand)
                )
                .then(Commands.literal("extractor")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCarbonCommand)
                )
                .then(Commands.literal("furnace")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCarbonCommand)
                )
                .then(Commands.literal("induction")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCarbonCommand)
                )
                .then(Commands.literal("steed")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeSteedCommand)
                )
                .then(Commands.literal("pistons")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executePistonsCommand)
                )
                .then(Commands.literal("horse")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeSteedCommand)
                )
                .then(Commands.literal("military_lab")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, "military_lab"))
                )
                .then(Commands.literal("cryo_vault")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, "cryo_vault"))
                )
                .then(Commands.literal("dungeon")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, "west"))
                        .then(Commands.argument("sector", StringArgumentType.word())
                                .suggests((c, b) -> suggestDungeonSectors(b))
                                .executes(ctx -> executeDungeonTeleportCommand(ctx, StringArgumentType.getString(ctx, "sector")))
                        )
                )
                .then(Commands.literal("cinematic")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> ShowcaseCinematicManager.startCinematic(ctx.getSource(), "dungeon"))
                        .then(Commands.literal("stop")
                                .executes(ctx -> ShowcaseCinematicManager.stopCinematic(ctx.getSource()))
                        )
                        .then(Commands.argument("route", StringArgumentType.word())
                                .suggests((c, b) -> suggestCinematicRoutes(b))
                                .executes(ctx -> ShowcaseCinematicManager.startCinematic(ctx.getSource(), StringArgumentType.getString(ctx, "route")))
                        )
                )
        );

        dispatcher.register(Commands.literal("sandstorm_vault")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeVaultCommand)
                .then(Commands.literal("build")
                        .executes(ShowcaseAutomation::executeVaultBuildCommand)
                )
        );

        dispatcher.register(Commands.literal("sandstorm_gateway")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeGatewayCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_portal")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeGatewayCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_rappel")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeRappelCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_defense")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeDefenseCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_sandworm")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeWormCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_worm")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeWormCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_orbital")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeOrbitalCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_computer")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeComputerCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_biospheres")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeBiospheresCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_military_lab")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeDungeonTeleportCommand(ctx, "military_lab"))
        );

        dispatcher.register(Commands.literal("sandstorm_cryo_vault")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeDungeonTeleportCommand(ctx, "cryo_vault"))
        );

        dispatcher.register(Commands.literal("sandstorm_showcase_dungeon")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeDungeonTeleportCommand(ctx, "west"))
                .then(Commands.argument("sector", StringArgumentType.word())
                        .suggests((c, b) -> suggestDungeonSectors(b))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, StringArgumentType.getString(ctx, "sector")))
                )
        );

        dispatcher.register(Commands.literal("sandstorm_showcase_cinematic")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> ShowcaseCinematicManager.startCinematic(ctx.getSource(), "dungeon"))
                .then(Commands.literal("stop")
                        .executes(ctx -> ShowcaseCinematicManager.stopCinematic(ctx.getSource()))
                )
                .then(Commands.argument("route", StringArgumentType.word())
                        .suggests((c, b) -> suggestCinematicRoutes(b))
                        .executes(ctx -> ShowcaseCinematicManager.startCinematic(ctx.getSource(), StringArgumentType.getString(ctx, "route")))
                )
        );

        dispatcher.register(Commands.literal("sandstorm_cinematic")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> ShowcaseCinematicManager.startCinematic(ctx.getSource(), "dungeon"))
                .then(Commands.literal("stop")
                        .executes(ctx -> ShowcaseCinematicManager.stopCinematic(ctx.getSource()))
                )
                .then(Commands.argument("route", StringArgumentType.word())
                        .suggests((c, b) -> suggestCinematicRoutes(b))
                        .executes(ctx -> ShowcaseCinematicManager.startCinematic(ctx.getSource(), StringArgumentType.getString(ctx, "route")))
                )
        );

        dispatcher.register(Commands.literal("sandstorm_carbon")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeCarbonCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_extractor")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeCarbonCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_steed")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeSteedCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_horse")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeSteedCommand)
        );

        dispatcher.register(Commands.literal("sandstorm_furnace")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseAutomation::executeCarbonCommand)
        );
    }

    public static int executeCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            buildShowcase(source.getLevel(), player);
            source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Vitrine Void completa gerada com sucesso!"), true);
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executeComputerCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            if (showcaseActive) {
                BlockPos targetPos = new BlockPos(50, 161, 0);
                player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, Set.of(), -90.0f, 0.0f, true);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Teleportado para o Computador Quântico!"), true);
            } else {
                BlockPos targetPos = BlockPos.containing(player.position());
                buildQuantumComputerStation(source.getLevel(), targetPos);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Computador Quântico completo e energizado montado com sucesso!"), true);
            }
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executeBiospheresCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            BlockPos center = new BlockPos(0, 160, 0);
            buildBiospheresShowcase(source.getLevel(), center);
            source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Complexo com 7 Biosferas e caminhos de conexão montado com sucesso!"), true);
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executeVaultCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            if (showcaseActive) {
                BlockPos targetPos = new BlockPos(50, 161, -40);
                player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY() + 1.0, targetPos.getZ() + 5.0, Set.of(), 180.0f, 0.0f, true);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Teleportado para a Vitrine de Cofres Blindados!"), true);
            } else {
                BlockPos targetPos = BlockPos.containing(player.position()).relative(player.getDirection(), 4);
                buildVaultShowcase(level, targetPos);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Câmara de Cofre Blindada 3x3 construída à sua frente!"), true);
            }
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executeVaultBuildCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            BlockPos targetPos = BlockPos.containing(player.position()).relative(player.getDirection(), 4);
            buildVaultShowcase(level, targetPos);
            source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Câmara de Cofre Blindada 3x3 construída com sucesso!"), true);
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executeGatewayCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            if (showcaseActive) {
                BlockPos targetPos = new BlockPos(80, 161, -45);
                player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, Set.of(), 0.0f, 0.0f, true);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Teleportado para a Estação do Portal do Subespaço!"), true);
            } else {
                BlockPos targetPos = BlockPos.containing(player.position()).relative(player.getDirection(), 4);
                buildSubspaceGatewayStation(level, targetPos);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Estação do Portal do Subespaço montada com sucesso!"), true);
            }
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executeRappelCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            if (showcaseActive) {
                BlockPos targetPos = new BlockPos(45, 161, -48);
                player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, Set.of(), 180.0f, 0.0f, true);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Teleportado para o Abismo e Torre de Rapel (128 Blocos)!"), true);
            } else {
                BlockPos targetPos = BlockPos.containing(player.position()).relative(player.getDirection(), 4);
                buildRappelTestTowerAndChasm(level, targetPos);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Estação de Testes de Rapel montada com sucesso!"), true);
            }
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executeDefenseCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            if (showcaseActive) {
                BlockPos targetPos = new BlockPos(-65, 161, -38);
                player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, Set.of(), 180.0f, 0.0f, true);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Teleportado para o Perímetro de Defesa & Balística!"), true);
            } else {
                BlockPos targetPos = BlockPos.containing(player.position()).relative(player.getDirection(), 4);
                buildDefenseAndBallisticsRange(level, targetPos);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Perímetro de Defesa & Balística montado com sucesso!"), true);
            }
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executeWormCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            if (showcaseActive) {
                BlockPos targetPos = new BlockPos(0, 161, -149);
                player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, Set.of(), 180.0f, 0.0f, true);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Teleportado para a Arena Sísmica do Sandworm!"), true);
            } else {
                BlockPos targetPos = BlockPos.containing(player.position()).relative(player.getDirection(), 6);
                buildSandwormColosseum(level, targetPos);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Arena Sísmica do Sandworm construída com sucesso!"), true);
            }
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executeOrbitalCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            if (showcaseActive) {
                BlockPos targetPos = new BlockPos(80, 161, 25);
                player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, Set.of(), 180.0f, 0.0f, true);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Teleportado para a Estação de Lançamento Orbital!"), true);
            } else {
                BlockPos targetPos = BlockPos.containing(player.position()).relative(player.getDirection(), 4);
                buildOrbitalLaunchStation(level, targetPos);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Estação Orbital montada com sucesso!"), true);
            }
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executeCarbonCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            if (showcaseActive) {
                BlockPos targetPos = new BlockPos(-50, 161, 0);
                player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, Set.of(), 90.0f, 0.0f, true);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Teleportado para o Complexo de Carbono & Indução Térmica!"), true);
            } else {
                BlockPos targetPos = BlockPos.containing(player.position()).relative(player.getDirection(), 4);
                buildCarbonAndInductionStation(level, targetPos);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Complexo de Carbono & Indução Térmica montado com sucesso!"), true);
            }
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executeSteedCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            if (showcaseActive) {
                BlockPos targetPos = new BlockPos(-75, 161, 40);
                player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, Set.of(), 180.0f, 0.0f, true);
                source.sendSuccess(() -> Component.literal("§6[SandStorm] §aTeleportado ao Setor Hípico!"), true);
            } else {
                BlockPos targetPos = BlockPos.containing(player.position()).relative(player.getDirection(), 4);
                buildCyberSteedShowcase(level, targetPos, player);
                source.sendSuccess(() -> Component.literal("§6[SandStorm] §aSetor Hípico montado com sucesso!"), true);
            }
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static int executePistonsCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            if (showcaseActive) {
                BlockPos targetPos = new BlockPos(-50, 161, -65);
                player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, Set.of(), 180.0f, 0.0f, true);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Teleportado para o Campo de Provas dos Super Pistões!"), true);
            } else {
                BlockPos targetPos = BlockPos.containing(player.position()).relative(player.getDirection(), 4);
                buildPistonTestingRange(level, targetPos);
                source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Campo de Provas dos Super Pistões montado com sucesso!"), true);
            }
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    public static void buildShowcase(ServerLevel level, ServerPlayer player) {
        validateShowcaseLayout();
        showcaseActive = true;
        TELEPORT_PADS.clear();
        CyborgSwarmManager.getInstance().releaseAll();
        BlockPos center = new BlockPos(0, 160, 0);
        int frontZ = getBiosphereFrontZ();
        int backZ = frontZ + 60;
        int maxZ = backZ + AtmosphericTerraformerBlockEntity.TIER1_RADIUS + 20;

        AABB showcaseArea = new AABB(center.getX() - 160, center.getY() - 100, center.getZ() - 210, center.getX() + 160, center.getY() + 120, center.getZ() + maxZ + 20);
        List<Entity> oldEntities = level.getEntitiesOfClass(Entity.class, showcaseArea, e -> !(e instanceof ServerPlayer));
        for (Entity old : oldEntities) {
            old.discard();
        }

        clearUnderneath(level, center, maxZ);
        buildCentralPlaza(level, center);
        buildTransitTerminal(level, center.offset(-20, 0, 12));
        buildBlockCheckerboard(level, center);
        buildItemWing(level, center);
        buildEntityWing(level, center);
        buildMegastructureDomeShowcase(level, center);
        buildMaglevTestTrack(level, center);
        buildAutonomousCyborgFacility(level, center, player);
        buildPistonTestingRange(level, center.offset(50, 0, -100));
        buildShowcaseWalkway(level, center.offset(11, 0, -10), center.offset(65, 0, -10));
        buildShowcaseWalkway(level, center.offset(65, 0, -10), center.offset(65, 0, -100));
        buildSandwormColosseum(level, center);
        buildQuantumComputerStation(level, center.offset(85, 0, -5));
        buildShowcaseWalkway(level, center.offset(11, 0, -5), center.offset(78, 0, -5));
        buildCarbonAndInductionStation(level, center.offset(35, 0, -25));
        buildShowcaseWalkway(level, center.offset(20, 0, -25), center.offset(22, 0, -25));
        buildVaultShowcase(level, center.offset(85, 0, -45));
        buildShowcaseWalkway(level, center.offset(85, 0, -13), center.offset(85, 0, -39));
        buildSubspaceGatewayStation(level, center.offset(110, 0, -45));
        buildShowcaseWalkway(level, center.offset(91, 0, -45), center.offset(105, 0, -45));
        buildRappelTestTowerAndChasm(level, center.offset(45, 0, -65));
        buildShowcaseWalkway(level, center.offset(20, 0, -10), center.offset(20, 0, -65));
        buildShowcaseWalkway(level, center.offset(20, 0, -65), center.offset(37, 0, -65));
        buildDefenseAndBallisticsRange(level, center.offset(-65, 0, -50));
        buildDefenseWalkway(level, center);
        buildOrbitalLaunchStation(level, center.offset(115, 0, 20));
        buildShowcaseWalkway(level, center.offset(95, 0, -5), center.offset(95, 0, 20));
        buildShowcaseWalkway(level, center.offset(95, 0, 20), center.offset(107, 0, 20));
        buildCyberSteedShowcase(level, center.offset(-75, 0, 40), player);
        buildCyberSteedWalkway(level, center);
        buildBiospheresShowcase(level, center);
        buildUndergroundDungeonNetwork(level, center);

        preparePlayerForShowcase(level, player);
        cleanupShowcaseDroppedItems(level);
    }

    public static boolean isShowcaseAlreadyBuilt(ServerLevel level) {
        BlockPos center = new BlockPos(0, 160, 0);
        return level.getBlockState(center).is(Blocks.OCHRE_FROGLIGHT);
    }

    public static void preparePlayerForShowcase(ServerLevel level, ServerPlayer player) {
        player.setGameMode(GameType.CREATIVE);
        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;
        player.onUpdateAbilities();
        player.teleportTo(level, 0.5, 161.0, 0.5, Set.of(), 0.0f, 0.0f, true);

        level.getServer().getGameRules().set(GameRules.ADVANCE_WEATHER, false, level.getServer());
        level.getServer().getGameRules().set(GameRules.ADVANCE_TIME, false, level.getServer());
        level.getServer().getGameRules().set(GameRules.SEND_COMMAND_FEEDBACK, false, level.getServer());
        level.getServer().getGameRules().set(GameRules.LOG_ADMIN_COMMANDS, false, level.getServer());
        level.getServer().getGameRules().set(GameRules.SHOW_DEATH_MESSAGES, false, level.getServer());
        level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack().withSuppressedOutput(), "time set noon");
        level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack().withSuppressedOutput(), "weather clear");
    }

    public static void cleanupShowcaseDroppedItems(ServerLevel level) {
        BlockPos center = new BlockPos(0, 160, 0);
        int frontZ = getBiosphereFrontZ();
        int backZ = frontZ + 60;
        int maxZ = backZ + AtmosphericTerraformerBlockEntity.TIER1_RADIUS + 20;
        AABB showcaseArea = new AABB(center.getX() - 160, center.getY() - 100, center.getZ() - 210, center.getX() + 160, center.getY() + 120, center.getZ() + maxZ + 20);
        List<ItemEntity> droppedItems = level.getEntitiesOfClass(ItemEntity.class, showcaseArea);
        for (ItemEntity item : droppedItems) {
            item.discard();
        }
    }

    public static void setAirSafe(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Container container) {
            container.clearContent();
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
    }

    public static void setChestSafe(ServerLevel level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Container container) {
            container.clearContent();
        }
        level.setBlock(pos, state, 3);
    }

    private static void clearUnderneath(ServerLevel level, BlockPos center, int maxZ) {
        for (int x = -120; x <= 120; x++) {
            for (int z = -210; z <= maxZ; z++) {
                for (int y = -64; y <= 20; y++) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (!level.getBlockState(p).isAir()) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private static void buildCentralPlaza(ServerLevel level, BlockPos center) {
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                BlockPos pos = center.offset(x, 0, z);
                boolean edge = Math.abs(x) == 10 || Math.abs(z) == 10;
                boolean corner = Math.abs(x) == 10 && Math.abs(z) == 10;
                boolean innerAccent = (Math.abs(x) == 5 && Math.abs(z) == 5);
                Block floorBlock = corner ? Blocks.OCHRE_FROGLIGHT : (innerAccent ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE));
                level.setBlock(pos, floorBlock.defaultBlockState(), 3);
            }
        }

        BlockPos centerMonument = center.offset(0, 1, 0);
        level.setBlock(centerMonument, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        spawnHologram(level, center.getX() + 0.5, center.getY() + 3.2, center.getZ() + 0.5, "§6§l✦ COMPLEXO CENTRAL SANDSTORM ✦");
        spawnHologram(level, center.getX() + 0.5, center.getY() + 2.85, center.getZ() + 0.5, "§bExplore as 4 Grandes Avenidas ou o Terminal Noroeste");

        BlockPos chestPos = center.offset(0, 1, 3);
        level.setBlock(chestPos.below(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        setChestSafe(level, chestPos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.SURVIVAL_DATAPAD));
            chest.setItem(1, new ItemStack(SandStormItems.SPACE_SUIT_HELMET));
            chest.setItem(2, new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE));
            chest.setItem(3, new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS));
            chest.setItem(4, new ItemStack(SandStormItems.SPACE_SUIT_BOOTS));
            chest.setItem(5, new ItemStack(SandStormItems.CYBERNETIC_COMMAND_UPLINK));
            chest.setItem(6, new ItemStack(SandStormItems.MEGAZORD_FLIGHT_MODULE));
            chest.setItem(7, new ItemStack(SandStormItems.MEGAZORD_SUBMERSIBLE_HULL));
            chest.setItem(8, new ItemStack(SandStormItems.MEGAZORD_TACTICAL_OVERDRIVE));
            chest.setItem(9, new ItemStack(SandStormItems.ANOMALY_RADAR));
            chest.setItem(10, new ItemStack(SandStormItems.SANDBOARD));
            chest.setItem(11, new ItemStack(SandStormItems.WEATHER_RECON_SATELLITE));
            chest.setItem(12, new ItemStack(SandStormItems.ORBITAL_SOLAR_REFLECTOR_SATELLITE));
            chest.setItem(13, new ItemStack(SandStormItems.SAR_GEOLOGICAL_SATELLITE));
            chest.setItem(14, new ItemStack(SandStormItems.ORBITAL_KINETIC_LANCE_SATELLITE));
            chest.setItem(15, new ItemStack(SandStormBlocks.ORBITAL_MASS_DRIVER));
            chest.setItem(16, new ItemStack(SandStormBlocks.ORBITAL_GROUND_STATION));
            chest.setItem(17, new ItemStack(SandStormBlocks.SPECTRAL_SURVEY_TELESCOPE));
            chest.setItem(18, new ItemStack(SandStormBlocks.QUANTUM_ACCESS_TERMINAL, 2));
            chest.setItem(19, new ItemStack(SandStormBlocks.QUANTUM_DISK_DRIVE, 2));
            chest.setItem(20, new ItemStack(SandStormBlocks.QUANTUM_NETWORK_CONTROLLER, 2));
            chest.setItem(21, new ItemStack(SandStormBlocks.QUANTUM_NETWORK_CABLE, 32));
            chest.setItem(22, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_1K, 4));
            chest.setItem(23, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_4K, 4));
            chest.setItem(24, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_16K, 2));
            chest.setItem(25, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K, 2));
            chest.setItem(26, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_DIMENSIONAL, 2));
        }
        spawnHologram(level, center.getX() + 0.5, center.getY() + 2.5, center.getZ() + 3.5, "§6[Baú de Sobrevivência & Órbita]");

        BlockPos chestPos2 = center.offset(0, 1, -3);
        level.setBlock(chestPos2.below(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        setChestSafe(level, chestPos2, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos2) instanceof ChestBlockEntity chest2) {
            chest2.setItem(0, new ItemStack(SandStormItems.RAPPEL_WINCH));
            chest2.setItem(1, new ItemStack(SandStormItems.GLOWING_RAPPEL_WINCH));
            chest2.setItem(2, new ItemStack(SandStormBlocks.RAPPEL_ANCHOR, 8));
            chest2.setItem(3, new ItemStack(SandStormBlocks.GLOWING_RAPPEL_ANCHOR, 8));
            chest2.setItem(4, new ItemStack(SandStormBlocks.RAPPEL_ROPE, 64));
            chest2.setItem(5, new ItemStack(SandStormBlocks.GLOWING_RAPPEL_ROPE, 64));
            chest2.setItem(6, new ItemStack(SandStormItems.SUBSPACE_WARP_CORE, 16));
            chest2.setItem(7, new ItemStack(SandStormBlocks.SUBSPACE_GATEWAY, 4));
            chest2.setItem(8, new ItemStack(SandStormBlocks.SEISMIC_DAMPENER_PAVING, 32));
            chest2.setItem(9, new ItemStack(SandStormBlocks.KINETIC_RAILGUN, 2));
            chest2.setItem(10, new ItemStack(SandStormItems.KINETIC_SLUG, 64));
            chest2.setItem(11, new ItemStack(SandStormBlocks.AUTONOMOUS_SONIC_TURRET, 2));
            chest2.setItem(12, new ItemStack(SandStormBlocks.ACOUSTIC_DEFENSE_PYLON, 2));
            chest2.setItem(13, new ItemStack(SandStormBlocks.PLASMA_SHIELD_GENERATOR, 2));
            chest2.setItem(14, new ItemStack(SandStormBlocks.KINETIC_SHIELD_GENERATOR, 2));
            chest2.setItem(15, new ItemStack(SandStormBlocks.RETRACTABLE_SPIKE_WALL, 16));
            chest2.setItem(16, new ItemStack(SandStormBlocks.KINETIC_FLOOR_SPIKES, 16));
            chest2.setItem(17, new ItemStack(SandStormBlocks.CRUSHING_SPIKE_GATE, 16));
            chest2.setItem(18, new ItemStack(SandStormBlocks.SOLID_STATE_ACCUMULATOR, 4));
            chest2.setItem(19, new ItemStack(SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2, 4));
            chest2.setItem(20, new ItemStack(SandStormBlocks.WPT_RELAY_TOWER, 8));
            chest2.setItem(21, new ItemStack(SandStormItems.COMPOSITE_RECURVE_BOW));
            chest2.setItem(22, new ItemStack(SandStormItems.SAND_DEPTH_PROBE));
            chest2.setItem(23, new ItemStack(SandStormItems.CRYO_LANCE));
            chest2.setItem(24, new ItemStack(SandStormItems.CRYO_CELL_COOLER));
            chest2.setItem(25, new ItemStack(SandStormItems.SUBDERMAL_TREMOR_SENSOR));
            chest2.setItem(26, new ItemStack(SandStormItems.GEOLOGICAL_SURVEYOR_DRONE));
        }
        spawnHologram(level, center.getX() + 0.5, center.getY() + 2.5, center.getZ() - 2.5, "§b[Baú de Rapel, Dobra & Defesa]");

        BlockPos chestPos3 = center.offset(-3, 1, 0);
        level.setBlock(chestPos3.below(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        setChestSafe(level, chestPos3, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos3) instanceof ChestBlockEntity chest3) {
            chest3.setItem(0, new ItemStack(SandStormBlocks.ATMOSPHERIC_CARBON_EXTRACTOR, 4));
            chest3.setItem(1, new ItemStack(SandStormBlocks.ELECTRIC_ARC_FURNACE, 4));
            chest3.setItem(2, new ItemStack(SandStormBlocks.SANDSTONE_CRATE, 8));
            chest3.setItem(3, new ItemStack(SandStormBlocks.SANDSTONE_FURNACE, 4));
            chest3.setItem(4, new ItemStack(SandStormItems.SYNAPTIC_CARTRIDGE, 4));
            chest3.setItem(5, new ItemStack(SandStormItems.SIGNAL_TRACKER, 1));
            chest3.setItem(6, new ItemStack(SandStormItems.MOLECULAR_CARGO_CAPSULE, 4));
            chest3.setItem(7, new ItemStack(SandStormItems.RAW_SILICON, 64));
            chest3.setItem(8, new ItemStack(SandStormItems.SILICON_WAFER, 64));
            chest3.setItem(9, new ItemStack(Items.COAL, 64));
            chest3.setItem(10, new ItemStack(Items.DIAMOND, 16));
            chest3.setItem(11, new ItemStack(SandStormBlocks.SOLID_STATE_ACCUMULATOR, 4));
            chest3.setItem(12, new ItemStack(SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2, 4));
            chest3.setItem(13, new ItemStack(SandStormBlocks.WPT_RELAY_TOWER, 8));
            chest3.setItem(14, new ItemStack(Items.NETHERRACK, 16));
            chest3.setItem(15, new ItemStack(Items.FLINT_AND_STEEL, 1));
        }
        spawnHologram(level, center.getX() - 2.5, center.getY() + 2.5, center.getZ() + 0.5, "§a[Baú de Indução, Carbono & Logística]");

        BlockPos chestPos4 = center.offset(3, 1, 0);
        level.setBlock(chestPos4.below(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        setChestSafe(level, chestPos4, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos4) instanceof ChestBlockEntity chest4) {
            chest4.setItem(0, new ItemStack(SandStormItems.CYBER_STEED_SPAWN_EGG, 16));
            chest4.setItem(1, new ItemStack(SandStormItems.REPROGRAMMER_TOOL));
            chest4.setItem(2, new ItemStack(SandStormItems.REPAIR_TOOL));
            chest4.setItem(3, new ItemStack(SandStormItems.STEED_UPGRADE_SONIC_TURRET, 4));
            chest4.setItem(4, new ItemStack(SandStormItems.STEED_UPGRADE_BATTERY, 4));
            chest4.setItem(5, new ItemStack(SandStormItems.STEED_UPGRADE_SPEED, 4));
            chest4.setItem(6, new ItemStack(SandStormItems.STEED_UPGRADE_CARGO, 4));
            chest4.setItem(7, new ItemStack(SandStormItems.SCRAP_METAL, 64));
            chest4.setItem(8, new ItemStack(Items.SADDLE, 4));
            chest4.setItem(9, new ItemStack(Items.LEAD, 4));
            chest4.setItem(10, new ItemStack(Items.IRON_INGOT, 32));
            chest4.setItem(11, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 16));
            chest4.setItem(12, new ItemStack(SandStormItems.GRAZER_SPAWN_EGG, 16));
            chest4.setItem(13, new ItemStack(SandStormItems.FANGHORN_SPAWN_EGG, 16));
            chest4.setItem(14, new ItemStack(SandStormItems.FROSTCLAW_SPAWN_EGG, 16));
            chest4.setItem(15, new ItemStack(SandStormItems.GLINTHAWK_SPAWN_EGG, 16));
            chest4.setItem(16, new ItemStack(SandStormItems.WATERWING_SPAWN_EGG, 16));
        }
        spawnHologram(level, center.getX() + 2.5, center.getY() + 2.5, center.getZ() + 0.5, "§e[Baú de Montarias & Sobrescrita]");

        spawnHologram(level, center.getX() + 0.5, center.getY() + 3.0, center.getZ() - 10.0, "§6§l▲ AVENIDA NORTE: Defesa, Balística, Rapel & Ciborgues ▲");
        spawnHologram(level, center.getX() + 10.0, center.getY() + 3.0, center.getZ() + 0.5, "§b§l▶ AVENIDA LESTE: Computador Quântico, Cofre, Portal & Órbita ▶");
        spawnHologram(level, center.getX() - 10.0, center.getY() + 3.0, center.getZ() + 0.5, "§a§l◀ AVENIDA OESTE: Indústria de Carbono, Forjas & Maglev ◀");
        spawnHologram(level, center.getX() + 0.5, center.getY() + 3.0, center.getZ() + 10.0, "§e§l▼ AVENIDA SUL: Galeria de Tecnologias, Domo & Biosferas ▼");

        AABB clearBox = new AABB(center).inflate(14.0);
        List<ArmorStand> existingStands = level.getEntitiesOfClass(ArmorStand.class, clearBox);
        for (ArmorStand stand : existingStands) {
            stand.discard();
        }

        spawnMannequin(level, center.offset(-6, 1, -6), "§bTraje Espacial Completo",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE),
                new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS), new ItemStack(SandStormItems.SPACE_SUIT_BOOTS),
                new ItemStack(SandStormItems.VIBRO_CRYSKNIFE), new ItemStack(SandStormItems.SURVIVAL_DATAPAD));

        spawnMannequin(level, center.offset(6, 1, -6), "§dArsenal de Plasma & Som",
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(SandStormItems.PLASMA_RIFLE), new ItemStack(SandStormItems.SONIC_CANNON));

        spawnMannequin(level, center.offset(-6, 1, 6), "§6Canhão Pesado & Radar",
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(SandStormItems.HEAVY_PLASMA_CANNON), new ItemStack(SandStormItems.ANOMALY_RADAR));

        spawnMannequin(level, center.offset(6, 1, 6), "§aKit de Voo & Propulsão",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(Items.ELYTRA),
                ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(SandStormItems.SUIT_UPGRADE_JETPACK), new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE));

        spawnMannequin(level, center.offset(-7, 1, -4), "§eExplorador de Abismos",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE),
                new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS), new ItemStack(SandStormItems.SPACE_SUIT_BOOTS),
                new ItemStack(SandStormItems.RAPPEL_WINCH), new ItemStack(SandStormItems.GLOWING_RAPPEL_WINCH));

        spawnMannequin(level, center.offset(7, 1, -4), "§3Especialista Dimensional",
                ItemStack.EMPTY, new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE),
                ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(SandStormItems.SUBSPACE_WARP_CORE), new ItemStack(SandStormItems.VIBRO_CRYSKNIFE));

        spawnMannequin(level, center.offset(-7, 1, 4), "§cArtilheiro Balístico",
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(SandStormBlocks.KINETIC_RAILGUN), new ItemStack(SandStormItems.KINETIC_SLUG));

        spawnMannequin(level, center.offset(7, 1, 4), "§9Engenheiro Orbital",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(SandStormItems.ORBITAL_KINETIC_LANCE_SATELLITE), new ItemStack(SandStormItems.WEATHER_RECON_SATELLITE));

        spawnMannequin(level, center.offset(-4, 1, -8), "§4Especialista em Armadilhas",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE),
                ItemStack.EMPTY, new ItemStack(SandStormItems.SPACE_SUIT_BOOTS),
                new ItemStack(SandStormItems.STUN_GRENADE), new ItemStack(SandStormItems.SMOKE_GRENADE));
    }

    private static void buildTransitTerminal(ServerLevel level, BlockPos center) {
        int baseY = center.getY();
        BlockPos showcaseCenter = new BlockPos(0, baseY, 0);

        AABB terminalBox = new AABB(center.getX() - 36, baseY, center.getZ() - 36, center.getX() - 12, baseY + 8, center.getZ() - 12);
        level.getEntitiesOfClass(ArmorStand.class, terminalBox).forEach(Entity::discard);

        for (int x = -10; x >= -14; x--) {
            for (int z = -8; z >= -10; z--) {
                BlockPos p = center.offset(x, 0, z);
                level.setBlock(p, Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
            }
        }

        for (int x = -15; x >= -33; x--) {
            for (int z = -14; z >= -33; z--) {
                for (int dy = 1; dy <= 5; dy++) {
                    BlockPos p = center.offset(x, dy, z);
                    if (!level.getBlockState(p).isAir()) {
                        setAirSafe(level, p);
                    }
                }
                BlockPos p = center.offset(x, 0, z);
                boolean edge = (x == -15 || x == -33 || z == -14 || z == -33);
                boolean corner = (x == -15 || x == -33) && (z == -14 || z == -33);
                Block b = corner ? Blocks.OCHRE_FROGLIGHT : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
                if (edge && !corner && !(x >= -18 && x <= -15 && z == -14)) {
                    level.setBlock(p.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                }
            }
        }

        spawnHologram(level, center.getX() - 24.0, baseY + 4.2, center.getZ() - 14.5, "§d§l[TERMINAL DE TRÂNSITO RÁPIDO & TELEPORTE]");
        spawnHologram(level, center.getX() - 24.0, baseY + 3.85, center.getZ() - 14.5, "§7Pise no pedestal correspondente para viagem instantânea");

        setupPedestal(level, center.offset(-18, 0, -17), showcaseCenter.offset(35, 1, -25), "Setor Leste: Carbono & Forjas", "CO2 | Síntese CVD | Arco Elétrico", Blocks.COAL_BLOCK);
        setupPedestal(level, center.offset(-22, 0, -17), showcaseCenter.offset(-75, 1, 0), "Setor Oeste: Circuito Maglev", "Trilhos Magnéticos de Alta Velocidade", Blocks.IRON_BLOCK);
        setupPedestal(level, center.offset(-26, 0, -17), showcaseCenter.offset(85, 1, -5), "Setor Leste: Computador Quântico", "Rede Quântica & Discos de Armazenamento", Blocks.DIAMOND_BLOCK);
        setupPedestal(level, center.offset(-30, 0, -17), showcaseCenter.offset(85, 1, -45), "Setor Leste: Câmara de Cofre", "Porta Mecânica 3x3 Multi-Bloco", Blocks.GOLD_BLOCK);

        setupPedestal(level, center.offset(-18, 0, -20), showcaseCenter.offset(110, 1, -45), "Setor Leste: Portal Subespacial", "Viagem Dimensional Subespacial", Blocks.CRYING_OBSIDIAN);
        setupPedestal(level, center.offset(-22, 0, -20), showcaseCenter.offset(115, 1, 20), "Setor Leste: Base Espacial Orbital", "Canhão de Massa 500 kJ & Satélites", Blocks.RAW_COPPER_BLOCK);
        setupPedestal(level, center.offset(-26, 0, -20), showcaseCenter.offset(-65, 1, -38), "Setor Norte: Defesa & Balística", "Railgun 250 kJ | Torretas & Escudos", Blocks.NETHERITE_BLOCK);
        setupPedestal(level, center.offset(-30, 0, -20), showcaseCenter.offset(45, 1, -65), "Setor Norte: Abismo & Rapel (128m)", "Guincho de Rapel & Salto Vertical", Blocks.SMOOTH_BASALT);

        setupPedestal(level, center.offset(-18, 0, -23), showcaseCenter.offset(0, 1, -85), "Setor Norte: Complexo de Ciborgues", "Androides, Mineração & Construção em Loop", Blocks.EMERALD_BLOCK);
        setupPedestal(level, center.offset(-22, 0, -23), showcaseCenter.offset(0, 1, getBiosphereFrontZ()), "Setor Sul: Complexo de Biosferas", "7 Biosferas Planetárias Gigantes", Blocks.VERDANT_FROGLIGHT);
        setupPedestal(level, center.offset(-26, 0, -23), showcaseCenter.offset(-68 + 4 * 7 + 3, -39, 12 + 3), "Dungeon: Megadungeon Oeste", "6 Andares | Grade 8x8 | 384 Salas", Blocks.POLISHED_DEEPSLATE);
        setupPedestal(level, center.offset(-30, 0, -23), showcaseCenter.offset(14 + 4 * 7 + 3, -39, 12 + 3), "Dungeon: Bunker Leste", "1 Andar Colossal | Grade 8x8 | 64 Salas", Blocks.DEEPSLATE_BRICKS);

        setupPedestal(level, center.offset(-18, 0, -26), showcaseCenter.offset(-24 + 3 * 7 + 3, -44, -70 + 3), "Dungeon: Depósito Geológico Sul", "4 Andares | Grade 7x7 | 196 Salas", Blocks.TERRACOTTA);
        setupPedestal(level, center.offset(-22, 0, -26), showcaseCenter.offset(-24 + 3 * 7 + 3, -44, 84 + 3), "Dungeon: Lab Militar Norte", "4 Andares | Black Site | 196 Salas", Blocks.REDSTONE_LAMP);
        setupPedestal(level, center.offset(-26, 0, -26), showcaseCenter.offset(-96 + 2 * 7 + 3, -44, -70 + 3), "Dungeon: Mineração Infestada", "3 Andares | Mega-Broca | Sandworms", Blocks.PACKED_MUD);
        setupPedestal(level, center.offset(-30, 0, -26), showcaseCenter.offset(48 + 2 * 7 + 3, -44, -70 + 3), "Dungeon: Cofre Criogênico", "3 Andares | Câmaras Criogênicas Sub-Zero", Blocks.BLUE_ICE);

        setupPedestal(level, center.offset(-18, 0, -29), showcaseCenter.offset(-75, 1, 40), "Setor Sudoeste: Hipódromo Cibernético", "Pista de Galope, Saltos & Sobrescrita", Blocks.HAY_BLOCK);
        setupPedestal(level, center.offset(-22, 0, -29), showcaseCenter.offset(0, 1, -149), "Setor Norte: Arena do Colosso (Sandworm)", "Verme de Areia Adulto | Arena de Dunas & Fosso", Blocks.RED_SANDSTONE);
        setupPedestal(level, center.offset(-26, 0, -29), showcaseCenter.offset(50, 1, -100), "Setor Mecânica: Super Pistões", "Meia-Laje (7) | Cinético (24) | Tectônico (48 + Obsidiana)", Blocks.REDSTONE_BLOCK);
    }

    private static void setupPedestal(ServerLevel level, BlockPos floorPos, BlockPos destination, String title, String subtitle, Block baseBlock) {
        level.setBlock(floorPos, baseBlock.defaultBlockState(), 3);
        level.setBlock(floorPos.above(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.defaultBlockState(), 3);

        BlockPos postPos = floorPos.north();
        level.setBlock(postPos, Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(postPos.above(), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 3);

        spawnHologram(level, floorPos.getX() + 0.5, floorPos.getY() + 2.35, floorPos.getZ() + 0.5, "§6§l" + title);
        spawnHologram(level, floorPos.getX() + 0.5, floorPos.getY() + 2.0, floorPos.getZ() + 0.5, "§e▶ " + subtitle);
        TELEPORT_PADS.add(new ShowcaseTeleportPad(floorPos.above(), destination, title));
    }

    private static void buildBlockCheckerboard(ServerLevel level, BlockPos center) {
        List<Block> blocks = BuiltInRegistries.BLOCK.stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(SandStormMod.MOD_ID))
                .filter(b -> !b.equals(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR) && !b.equals(SandStormBlocks.VAULT_DOOR_DUMMY))
                .sorted(Comparator.comparing(b -> BuiltInRegistries.BLOCK.getKey(b).toString()))
                .toList();

        int cols = 8;
        int spacing = 5;
        int startZ = CHECKERBOARD_START_Z;
        int totalRows = (blocks.size() + cols - 1) / cols;

        for (int x = -26; x <= 26; x++) {
            for (int z = startZ - 2; z <= startZ + (totalRows * spacing) + 4; z++) {
                for (int y = 1; y <= 4; y++) {
                    BlockPos p = center.offset(x, y, z);
                    BlockState bs = level.getBlockState(p);
                    if (bs.getBlock() instanceof LiquidBlock || !bs.getFluidState().isEmpty()) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        for (int col = 0; col < cols; col++) {
            int px = getCheckerboardX(col);
            for (int row = 0; row < totalRows; row++) {
                int pz = startZ + (row * spacing);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        for (int dy = 1; dy <= 4; dy++) {
                            BlockPos p = center.offset(px + dx, dy, pz + dz);
                            if (!level.getBlockState(p).isAir()) {
                                level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < blocks.size(); i++) {
            int col = i % cols;
            int row = i / cols;

            int px = getCheckerboardX(col);
            int pz = startZ + (row * spacing);

            boolean isEvenTile = (col + row) % 2 == 0;
            Block borderBlock = isEvenTile ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE;
            Block lightBlock = isEvenTile ? Blocks.OCHRE_FROGLIGHT : Blocks.SEA_LANTERN;

            BlockPos pedestalCenter = center.offset(px, 0, pz);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos p = pedestalCenter.offset(dx, 0, dz);
                    Block blockChoice = (dx == 0 && dz == 0) ? lightBlock : borderBlock;
                    level.setBlock(p, blockChoice.defaultBlockState(), 3);
                }
            }

            Block targetBlock = blocks.get(i);
            BlockPos targetPos = pedestalCenter.above();
            if (targetBlock instanceof LiquidBlock) {
                level.setBlock(targetPos.north(), Blocks.GLASS.defaultBlockState(), 3);
                level.setBlock(targetPos.south(), Blocks.GLASS.defaultBlockState(), 3);
                level.setBlock(targetPos.east(), Blocks.GLASS.defaultBlockState(), 3);
                level.setBlock(targetPos.west(), Blocks.GLASS.defaultBlockState(), 3);
            }
            if (targetBlock instanceof DoorBlock) {
                level.setBlock(targetPos, targetBlock.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 3);
                level.setBlock(targetPos.above(), targetBlock.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);
            } else if (targetBlock instanceof VaultDoorControllerBlock) {
                Direction facing = Direction.SOUTH;
                level.setBlock(targetPos, targetBlock.defaultBlockState().setValue(VaultDoorControllerBlock.FACING, facing), 3);
                for (int c = 0; c <= 2; c++) {
                    for (int r = 0; r <= 2; r++) {
                        if (c == 1 && r == 0) {
                            continue;
                        }
                        BlockPos dummyPos = VaultDoorControllerBlock.getRelativePos(targetPos, facing, c, r);
                        BlockState dummyState = SandStormBlocks.VAULT_DOOR_DUMMY.defaultBlockState()
                                .setValue(VaultDoorDummyBlock.FACING, facing)
                                .setValue(VaultDoorDummyBlock.COL, c)
                                .setValue(VaultDoorDummyBlock.ROW, r);
                        level.setBlock(dummyPos, dummyState, 3);
                    }
                }
            } else if (targetBlock == SandStormBlocks.PLASMA_SHIELD_GENERATOR) {
                level.setBlock(targetPos, targetBlock.defaultBlockState().setValue(PlasmaShieldGeneratorBlock.ACTIVE, true), 3);
                if (level.getBlockEntity(targetPos) instanceof PlasmaShieldGeneratorBlockEntity psBe) {
                    psBe.setStoredEnergy(1000000);
                    psBe.setShieldActive(true);
                }
                level.setBlock(pedestalCenter, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
                SolidStateAccumulatorManager.registerAccumulator(level.dimension(), pedestalCenter, 500000, 500000, true, 1);
            } else if (targetBlock == SandStormBlocks.KINETIC_SHIELD_GENERATOR) {
                level.setBlock(targetPos, targetBlock.defaultBlockState(), 3);
                if (level.getBlockEntity(targetPos) instanceof KineticShieldGeneratorBlockEntity ksBe) {
                    ksBe.setEnergy(20000);
                }
            } else if (targetBlock == SandStormBlocks.AUTONOMOUS_SONIC_TURRET) {
                level.setBlock(targetPos, targetBlock.defaultBlockState(), 3);
                if (level.getBlockEntity(targetPos) instanceof AutonomousSonicTurretBlockEntity astBe) {
                    astBe.setEnergy(50000);
                }
            } else if (targetBlock == SandStormBlocks.ACOUSTIC_DEFENSE_PYLON) {
                level.setBlock(targetPos, targetBlock.defaultBlockState().setValue(AcousticDefensePylonBlock.ACTIVE, true), 3);
                if (level.getBlockEntity(targetPos) instanceof AcousticDefensePylonBlockEntity adpBe) {
                    adpBe.setStoredEnergy(50000);
                }
            } else if (targetBlock == SandStormBlocks.SOLID_STATE_ACCUMULATOR) {
                level.setBlock(targetPos, targetBlock.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
                if (level.getBlockEntity(targetPos) instanceof SolidStateAccumulatorBlockEntity ssaBe) {
                    ssaBe.setStoredEnergy(500000);
                }
                SolidStateAccumulatorManager.registerAccumulator(level.dimension(), targetPos, 500000, 500000, true, 1);
            } else if (targetBlock == SandStormBlocks.XENO_WHEAT_CROP) {
                level.setBlock(pedestalCenter, SandStormBlocks.HYDROPONIC_TRAY.defaultBlockState(), 3);
                CropBlock cropBlock = (CropBlock) targetBlock;
                level.setBlock(targetPos, cropBlock.getStateForAge(cropBlock.getMaxAge()), 3);
            } else if (targetBlock == SandStormBlocks.HYDROPONIC_TRAY) {
                level.setBlock(targetPos, targetBlock.defaultBlockState(), 3);
                CropBlock cropBlock = (CropBlock) SandStormBlocks.XENO_WHEAT_CROP;
                level.setBlock(targetPos.above(), cropBlock.getStateForAge(cropBlock.getMaxAge()), 3);
            } else {
                level.setBlock(targetPos, targetBlock.defaultBlockState(), 3);
            }

            if (targetBlock instanceof ThermalVentTrapBlock || targetBlock instanceof CryoFreezeMineBlock
                    || targetBlock instanceof KineticEjectorTrapBlock || targetBlock instanceof PiezoShockTrapBlock
                    || targetBlock instanceof SerratedJawTrapBlock || targetBlock instanceof HoloLaserTripwireBlock) {
                BlockPos leverPos = pedestalCenter.offset(1, 1, 0);
                level.setBlock(leverPos, Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACE, AttachFace.FLOOR).setValue(LeverBlock.FACING, Direction.WEST), 3);
            }

            spawnBlockHologram(level, targetPos, targetBlock);
        }

        int maxBlockZ = startZ + (totalRows * spacing) + 4;
        for (int z = 4; z <= maxBlockZ; z++) {
            level.setBlock(center.offset(0, 0, z), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 3);
            level.setBlock(center.offset(-1, 0, z), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 3);
            level.setBlock(center.offset(1, 0, z), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 3);
            Block edgeBlock = (z % 4 == 0) ? Blocks.OCHRE_FROGLIGHT : Blocks.CUT_SANDSTONE;
            level.setBlock(center.offset(-2, 0, z), edgeBlock.defaultBlockState(), 3);
            level.setBlock(center.offset(2, 0, z), edgeBlock.defaultBlockState(), 3);
        }

        for (int z = 8; z <= maxBlockZ; z += 16) {
            BlockPos wptPillar = center.offset(0, 1, z);
            level.setBlock(wptPillar, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
            SolidStateAccumulatorManager.registerAccumulator(level.dimension(), wptPillar, 500000, 500000, true, 1);
            level.setBlock(wptPillar.above(), SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
            WirelessSolarReceiverManager.registerReceiver(level.dimension(), wptPillar.above(), 2);
            level.setBlock(wptPillar.above(2), SandStormBlocks.WPT_RELAY_TOWER.defaultBlockState(), 3);
        }

        for (int row = 0; row < totalRows; row++) {
            int pz = startZ + (row * spacing);
            for (int x = -5; x <= -3; x++) {
                level.setBlock(center.offset(x, 0, pz), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3);
            }
            for (int x = 3; x <= 5; x++) {
                level.setBlock(center.offset(x, 0, pz), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3);
            }
        }
    }

    private static int getCheckerboardX(int col) {
        return (col < 4) ? (-18 + col * 4) : (6 + (col - 4) * 4);
    }

    private static void spawnBlockHologram(ServerLevel level, BlockPos blockPos, Block block) {
        String name = block.getName().getString();
        String role = getBlockExplanation(block);
        double yName = (block instanceof VaultDoorControllerBlock) ? 3.45 : ((block instanceof DoorBlock) ? 2.45 : 1.45);
        double yRole = (block instanceof VaultDoorControllerBlock) ? 3.15 : ((block instanceof DoorBlock) ? 2.15 : 1.15);
        spawnHologram(level, blockPos.getX() + 0.5, blockPos.getY() + yName, blockPos.getZ() + 0.5, "§e" + name);
        spawnHologram(level, blockPos.getX() + 0.5, blockPos.getY() + yRole, blockPos.getZ() + 0.5, "§7" + role);
    }

    private static String getBlockExplanation(Block block) {
        if (block == SandStormBlocks.VAULT_DOOR_CONTROLLER) return "Porta Mecânica de Cofre 3x3";
        if (block == SandStormBlocks.VAULT_WALL_PANEL) return "Chapas de Aço Escovado e Rebites";
        if (block == SandStormBlocks.VAULT_WALL_SEAM) return "Pilar Estrutural de Junção Blindado";
        if (block == SandStormBlocks.SAND_MAGLEV_RAIL) return "Trilho Maglev com Boost 1.8x";
        if (block == SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR) return "Montagem Robótica de Megaestruturas";
        if (block == SandStormBlocks.LITHO_PLASMA_EXTRACTOR) return "Extração Magmática de Lítio e Plasma";
        if (block == SandStormBlocks.SUPERCRITICAL_HEAT_EXCHANGER) return "Refrigeração Térmica Supercrítica";
        if (block == SandStormBlocks.AUTONOMOUS_SONIC_TURRET) return "Defesa Automática por Pulsos Sônicos";
        if (block == SandStormBlocks.PLASMA_SHIELD_GENERATOR) return "Cúpula Protetora contra Tempestades";
        if (block == SandStormBlocks.ORBITAL_MASS_DRIVER) return "Lançador Eletromagnético de Satélites";
        if (block == SandStormBlocks.ORBITAL_GROUND_STATION) return "Enlace de Telemetria e Satélites";
        if (block == SandStormBlocks.HOLO_TACTICAL_SPIRE) return "Radar Tático e Controle de Drones";
        if (block == SandStormBlocks.QUANTUM_SLEEPER_POD) return "Criogenia, Ponto de Respawn e Clonagem";
        if (block == SandStormBlocks.SOLID_STATE_ACCUMULATOR) return "Armazenamento Elétrico Massivo (500 kJ)";
        if (block == SandStormBlocks.GRID_MONITOR_CONSOLE) return "Monitoramento Geral da Rede WPT";
        if (block == SandStormBlocks.DEEP_CORE_BOREHOLE) return "Perfuração Geotérmica Profunda";
        if (block == SandStormBlocks.DEEP_CORE_DRILL) return "Mineração Robótica Automatizada";
        if (block == SandStormBlocks.PRINTER_3D) return "Manufatura de Componentes e Itens";
        if (block == SandStormBlocks.NANITE_FABRICATOR) return "Síntese de Nanotecnologia e Ligas";
        if (block == SandStormBlocks.MOLECULAR_MODIFIER) return "Upgrade Molecular de Trajes e Armas";
        if (block == SandStormBlocks.CYBORG_DOCKING_STATION) return "Recarga, Reparo e Manutenção WPT";
        if (block == SandStormBlocks.CYBORG_INCUBATOR_VAT) return "Montagem e Ativação de Ciborgues";
        if (block == SandStormBlocks.ATMOSPHERIC_TERRAFORMER) return "Restauração Ecológica e Oásis";
        if (block == SandStormBlocks.HYDROPONIC_CHAMBER) return "Cultivo Hidropônico Acelerado";
        if (block == SandStormBlocks.DESALINATION_FILTER) return "Dessalinização e Purificação de Água";
        if (block == SandStormBlocks.DEW_CONDENSER) return "Condensação de Umidade Atmosférica";
        if (block == SandStormBlocks.BIOREACTOR_VAT) return "Processamento de Matéria Orgânica";
        if (block == SandStormBlocks.CHEMICAL_REFINERY) return "Refino Químico e Síntese de Fluidos";
        if (block == SandStormBlocks.BIO_REGENERATION_POD) return "Tratamento Médico e Cura Biológica";
        if (block == SandStormBlocks.AUTO_ASSEMBLY_LINE) return "Automação Industrial em Linha";
        if (block == SandStormBlocks.THERMAL_GENERATOR) return "Conversão Termoelétrica em WPT";
        if (block == SandStormBlocks.ACOUSTIC_DEFENSE_PYLON) return "Repulsor Sônico contra Vermes";
        if (block == SandStormBlocks.SPECTRAL_SURVEY_TELESCOPE) return "Observatório Espectral Espacial";
        if (block == SandStormBlocks.KINETIC_RAILGUN) return "Canhão Eletromagnético Balístico";
        if (block == SandStormBlocks.WIRELESS_SOLAR_RECEIVER || block == SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2) return "Receptor Solar de Alta Eficiência";
        if (block == SandStormBlocks.WPT_RELAY_TOWER) return "Torre de Transmissão e Relé WPT";
        if (block == SandStormBlocks.BURIED_TECH_RUINS) return "Ruína Tecnológica com Sucatas";
        if (block == SandStormBlocks.KINETIC_SHIELD_GENERATOR) return "Gerador de Escudo Cinético";
        if (block == SandStormBlocks.HABITAT_DOME) return "Cúpula Selada de Habitação";
        if (block == SandStormBlocks.ANCIENT_DATA_CORE) return "Núcleo de Dados Antigo";
        if (block == SandStormBlocks.SANDSTONE_WORKBENCH) return "Bancada de Trabalho de Arenito";
        if (block == SandStormBlocks.SANDSTONE_FURNACE) return "Fornalha Solar de Arenito";
        if (block == SandStormBlocks.BRACKISH_AQUIFER) return "Aquífero Salobro Subterrâneo";
        if (block == SandStormBlocks.THUMPER) return "Dispositivo Sônico de Atração";
        if (block == SandStormBlocks.TITANIUM_SPIKE_WALL || block == SandStormBlocks.RETRACTABLE_SPIKE_WALL) return "Muralha de Espinhos Blindada";
        if (block == SandStormBlocks.ELECTRIFIED_SPIKE_BARRIER) return "Barreira de Espinhos Eletrizada";
        if (block == SandStormBlocks.CORROSIVE_CHITIN_SPIKE_WALL) return "Muralha de Quitina Corrosiva";
        if (block == SandStormBlocks.KINETIC_FLOOR_SPIKES || block == SandStormBlocks.CRUSHING_SPIKE_GATE) return "Defesa Tática de Espinhos";
        if (block == SandStormBlocks.QUANTUM_NETWORK_CONTROLLER) return "Processador Central Quântico";
        if (block == SandStormBlocks.QUANTUM_DISK_DRIVE) return "Unidade de Discos Quânticos (8x)";
        if (block == SandStormBlocks.QUANTUM_ACCESS_TERMINAL) return "Terminal de Gestão de Itens";
        if (block == SandStormBlocks.QUANTUM_NETWORK_CABLE) return "Cabo Óptico de Dados Quântico";
        if (block == SandStormBlocks.ELECTRIC_FENCE_PYLON) return "Poste de Cerca Elétrica Defensiva";
        if (block == SandStormBlocks.SUBSPACE_GATEWAY) return "Portal de Trânsito Subespacial";
        if (block == SandStormBlocks.SEISMIC_DAMPENER_PAVING) return "Pavimento Amortecedor Sísmico";
        if (block == SandStormBlocks.SALT_BRICKS || block == SandStormBlocks.SALT_BRICK_STAIRS || block == SandStormBlocks.SALT_BRICK_SLAB || block == SandStormBlocks.SALT_BRICK_WALL) return "Bloco de Sal Mineral Estrutural";
        if (block == SandStormBlocks.DRONE_DOCK) return "Doca de Pouso e Carga de Drones";
        if (block == SandStormBlocks.ASSEMBLY_BAY) return "Baía de Montagem Pesada";
        if (block == SandStormBlocks.PIEZO_QUARTZ_BLOCK || block == SandStormBlocks.BUDDING_PIEZO_QUARTZ || block == SandStormBlocks.PIEZO_QUARTZ_CLUSTER) return "Cristal de Quartzo Piezoelétrico";
        if (block == SandStormBlocks.THERMAL_SPRING_STONE) return "Rocha Hidrotermal Mineralizada";
        if (block == SandStormBlocks.ELECTRIFIED_SAND) return "Areia Eletrizada por Tempestades";
        if (block == SandStormBlocks.FOSSILIZED_AMBER) return "Âmbar Fóssil Energizado";
        if (block == SandStormBlocks.FULGURITE_GLASS) return "Vidro Translúcido de Fulgurita";
        if (block == SandStormBlocks.ANCIENT_REED_BLOCK) return "Juncos Antigos Fossilizados";
        if (block == SandStormBlocks.XENO_GRASS_BLOCK) return "Grama Xenófila do Deserto";
        if (block == SandStormBlocks.HEAVY_SAP_CACTUS) return "Cacto de Seiva Espessa Concentrada";
        if (block == SandStormBlocks.SALINIZED_SAND) return "Areia Salinizada Hiper-Mineral";
        if (block == SandStormBlocks.HALOPHYTE_PLANT || block == SandStormBlocks.HALOPHYTE_SUCCULENT) return "Vegetação Halófita Adaptativa";
        if (block == SandStormBlocks.RADIOTROPHIC_MYCELIUM) return "Micélio Radiotrófico do Solo";
        if (block == SandStormBlocks.CHITINOLYTIC_FUNGUS) return "Fungo Decompositor de Quitina";
        if (block == SandStormBlocks.CRYO_XEROPHILIC_LICHEN) return "Líquen Crio-Xerófilo Resistente";
        if (block == SandStormBlocks.DUNE_EPHEDRA) return "Éfedra das Dunas Medicinal";
        if (block == SandStormBlocks.SERRATED_JAW_TRAP) return "Armadilha de Mandíbula Mecânica";
        if (block == SandStormBlocks.PIEZO_SHOCK_TRAP) return "Mina Piezoelétrica de Descarga";
        if (block == SandStormBlocks.COLLAPSIBLE_TRAPDOOR) return "Alçapão Falso com Queda Livre";
        if (block == SandStormBlocks.KINETIC_EJECTOR_TRAP) return "Ejetor Pneumático Vertical 14m";
        if (block == SandStormBlocks.THERMAL_VENT_TRAP) return "Grelha Industrial a Vapor Quente";
        if (block == SandStormBlocks.MIRAGE_HOLOGRAM_TRAP) return "Projetor Holográfico de Isca";
        if (block == SandStormBlocks.NANITE_WEB_SNARE) return "Armadilha de Teto com Cabos Naníticos";
        if (block == SandStormBlocks.NEUROTOXIC_SPIKES) return "Piso de Espinhos Neurotóxicos";
        if (block == SandStormBlocks.CRYO_FREEZE_MINE) return "Mina Criogênica de Congelamento";
        if (block == SandStormBlocks.MICRO_SINGULARITY_TRAP) return "Armadilha de Micro-Singularidade";
        if (block == SandStormBlocks.SEISMIC_DECOY_TRAP) return "Isca Sísmica Terrestre";
        if (block == SandStormBlocks.HOLO_LASER_TRIPWIRE) return "Sensor Laser de Intrusão";
        if (block == SandStormBlocks.NANITE_QUICKSAND) return "Areia Movediça de Nanites";
        if (block == SandStormBlocks.SMART_FLUID_PIPE) return "Duto Inteligente de Fluidos e Gases";
        if (block == SandStormBlocks.ATMOSPHERIC_CARBON_EXTRACTOR) return "Capturador de Carbono e Síntese CVD";
        if (block == SandStormBlocks.ELECTRIC_ARC_FURNACE) return "Fornalha a Arco Elétrico de Indução";
        if (block == SandStormBlocks.SANDSTONE_CRATE) return "Caixote de Arenito com Tampa 3D";
        if (block == SandStormBlocks.SYNAPTIC_CONDENSER) return "Condensador Sináptico de XP";
        if (block == SandStormBlocks.SYNAPTIC_REPAIRER) return "Reparador Nanítico Sináptico";
        if (block == SandStormBlocks.HYDROPONIC_TRAY) return "Bandeja Hidropônica UV Estéril";
        if (block == SandStormBlocks.SEISMIC_RESONANCE_CATALYST) return "Catalisador de Invocação do Apex";
        if (block == SandStormBlocks.XENO_WHEAT_CROP) return "Cultivo Hidropônico de Xeno-Trigo";
        return "Tecnologia Estrutural SandStorm";
    }

    private static void buildMaglevTestTrack(ServerLevel level, BlockPos center) {
        int baseY = center.getY();

        for (int x = -63; x >= -75; x--) {
            for (int z = -2; z <= 2; z++) {
                BlockPos bp = center.offset(x, 0, z);
                Block b = (Math.abs(z) == 2) ? Blocks.CUT_SANDSTONE : (Math.abs(x) % 4 == 0 ? Blocks.SEA_LANTERN : Blocks.SMOOTH_STONE);
                level.setBlock(bp, b.defaultBlockState(), 2);
            }
        }

        int xMin = -105;
        int xMax = -75;
        int zMin = -15;
        int zMax = 15;

        for (int x = xMin - 1; x <= xMax + 1; x++) {
            for (int z = zMin - 1; z <= zMax + 1; z++) {
                BlockPos bp = new BlockPos(x, baseY, z);
                boolean isEdge = (x == xMin - 1 || x == xMax + 1 || z == zMin - 1 || z == zMax + 1);
                boolean isCorner = (x == xMin - 1 || x == xMax + 1) && (z == zMin - 1 || z == zMax + 1);
                Block floor = isCorner ? Blocks.OCHRE_FROGLIGHT : (isEdge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                level.setBlock(bp, floor.defaultBlockState(), 2);
            }
        }

        for (int z = zMin + 1; z < zMax; z++) {
            BlockPos pWest = new BlockPos(xMin, baseY + 1, z);
            BlockPos pEast = new BlockPos(xMax, baseY + 1, z);
            level.setBlock(pWest, SandStormBlocks.SAND_MAGLEV_RAIL.defaultBlockState().setValue(SandMaglevRailBlock.SHAPE, RailShape.NORTH_SOUTH), 3);
            level.setBlock(pEast, SandStormBlocks.SAND_MAGLEV_RAIL.defaultBlockState().setValue(SandMaglevRailBlock.SHAPE, RailShape.NORTH_SOUTH), 3);
        }

        for (int x = xMin + 1; x < xMax; x++) {
            BlockPos pNorth = new BlockPos(x, baseY + 1, zMin);
            BlockPos pSouth = new BlockPos(x, baseY + 1, zMax);
            level.setBlock(pNorth, SandStormBlocks.SAND_MAGLEV_RAIL.defaultBlockState().setValue(SandMaglevRailBlock.SHAPE, RailShape.EAST_WEST), 3);
            level.setBlock(pSouth, SandStormBlocks.SAND_MAGLEV_RAIL.defaultBlockState().setValue(SandMaglevRailBlock.SHAPE, RailShape.EAST_WEST), 3);
        }

        BlockPos cNW = new BlockPos(xMin, baseY + 1, zMin);
        BlockPos cNE = new BlockPos(xMax, baseY + 1, zMin);
        BlockPos cSW = new BlockPos(xMin, baseY + 1, zMax);
        BlockPos cSE = new BlockPos(xMax, baseY + 1, zMax);

        level.setBlock(cNW, SandStormBlocks.SAND_MAGLEV_RAIL.defaultBlockState().setValue(SandMaglevRailBlock.SHAPE, RailShape.SOUTH_EAST), 3);
        level.setBlock(cNE, SandStormBlocks.SAND_MAGLEV_RAIL.defaultBlockState().setValue(SandMaglevRailBlock.SHAPE, RailShape.SOUTH_WEST), 3);
        level.setBlock(cSW, SandStormBlocks.SAND_MAGLEV_RAIL.defaultBlockState().setValue(SandMaglevRailBlock.SHAPE, RailShape.NORTH_EAST), 3);
        level.setBlock(cSE, SandStormBlocks.SAND_MAGLEV_RAIL.defaultBlockState().setValue(SandMaglevRailBlock.SHAPE, RailShape.NORTH_WEST), 3);

        BlockPos chestPos = new BlockPos(xMax + 1, baseY + 1, 0);
        setChestSafe(level, chestPos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.SANDBOARD, 1));
            chest.setItem(1, new ItemStack(Items.MINECART, 4));
            chest.setItem(2, new ItemStack(Items.CHEST_MINECART, 2));
            chest.setItem(3, new ItemStack(SandStormBlocks.SAND_MAGLEV_RAIL, 64));
        }

        @SuppressWarnings("unchecked")
        EntityType<Minecart> minecartType = (EntityType<Minecart>) (EntityType<?>) BuiltInRegistries.ENTITY_TYPE.getValue(SandStormMod.mcId("minecart"));
        if (minecartType != null) {
            Minecart cart = new Minecart(minecartType, level);
            cart.setPos(xMin + 0.5, baseY + 1.2, (zMin + zMax) / 2.0);
            level.addFreshEntity(cart);
        }

        BlockPos trackHolo = new BlockPos((xMin + xMax) / 2, baseY + 1, 0);
        spawnHologram(level, trackHolo.getX() + 0.5, baseY + 3.0, trackHolo.getZ() + 0.5, "§b[Circuito de Testes: Trilhos Maglev]");
        spawnHologram(level, trackHolo.getX() + 0.5, baseY + 2.7, trackHolo.getZ() + 0.5, "§7Curvas dinâmicas e propulsão magnética de 1.8x");
    }

    private static void buildAutonomousCyborgFacility(ServerLevel level, BlockPos center, ServerPlayer player) {
        int baseY = center.getY();

        for (int z = -11; z >= -80; z--) {
            for (int x = -2; x <= 2; x++) {
                BlockPos bp = center.offset(x, 0, z);
                boolean edge = Math.abs(x) == 2;
                boolean light = (Math.abs(z) % 4 == 0 && x == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(bp, b.defaultBlockState(), 2);
            }
        }

        int xMin = -20;
        int xMax = 20;
        int zMin = -115;
        int zMax = -80;

        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                BlockPos bp = new BlockPos(x, baseY, z);
                boolean edge = (x == xMin || x == xMax || z == zMin || z == zMax);
                boolean light = (x % 5 == 0 && (z == zMin || z == zMax)) || (z % 5 == 0 && (x == xMin || x == xMax));
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                level.setBlock(bp, b.defaultBlockState(), 2);

                if (edge) {
                    level.setBlock(bp.above(), Blocks.SANDSTONE_WALL.defaultBlockState(), 2);
                    level.setBlock(bp.above(2), Blocks.IRON_BARS.defaultBlockState(), 2);
                }
            }
        }

        AABB facilityBox = new AABB(xMin - 2, baseY - 2, zMin - 5, xMax + 2, baseY + 15, zMax + 5);
        level.getEntitiesOfClass(ArmorStand.class, facilityBox).forEach(Entity::discard);
        level.getEntitiesOfClass(CyborgEntity.class, facilityBox).forEach(Entity::discard);
        level.getEntitiesOfClass(CargoDroneEntity.class, facilityBox).forEach(Entity::discard);
        CyborgSwarmManager.getInstance().releaseAll();

        BlockPos entranceHolo = new BlockPos(0, baseY + 1, -82);
        spawnHologram(level, entranceHolo.getX() + 0.5, entranceHolo.getY() + 1.2, entranceHolo.getZ() + 0.5, "§6[Complexo Industrial de Ciborgues]");
        spawnHologram(level, entranceHolo.getX() + 0.5, entranceHolo.getY() + 0.9, entranceHolo.getZ() + 0.5, "§bDemonstração de Operações Autônomas");

        BlockPos quarryCenter = new BlockPos(-10, baseY, -92);
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos p = quarryCenter.offset(dx, 0, dz);
                boolean border = (Math.abs(dx) == 4 || Math.abs(dz) == 4);
                Block b = border ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE;
                level.setBlock(p, b.defaultBlockState(), 2);
                if (border) {
                    level.setBlock(p.above(), Blocks.IRON_BARS.defaultBlockState(), 2);
                }
            }
        }
        level.setBlock(quarryCenter.below(), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 3);
        level.setBlock(quarryCenter.offset(-1, 0, -1), Blocks.RAW_COPPER_BLOCK.defaultBlockState(), 3);
        level.setBlock(quarryCenter.offset(1, 0, 1), Blocks.RAW_IRON_BLOCK.defaultBlockState(), 3);

        regeneratingRockPos = quarryCenter.above();
        level.setBlock(regeneratingRockPos, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);

        CyborgExcavatorEntity cyborgExc = new CyborgExcavatorEntity(SandStormEntities.CYBORG_EXCAVATOR, level);
        cyborgExc.setPos(quarryCenter.getX() - 1.5, baseY + 1.0, quarryCenter.getZ() + 0.5);
        cyborgExc.setOwnerUUID(player.getUUID());
        cyborgExc.setEnergy(50000);
        cyborgExc.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
        cyborgExc.setZoneMin(quarryCenter.offset(-3, -1, -3));
        cyborgExc.setZoneMax(quarryCenter.offset(3, 2, 3));
        cyborgExc.setCustomName(Component.literal("§6Androide Minerador Autônomo"));
        cyborgExc.setCustomNameVisible(true);
        cyborgExc.setPersistenceRequired();
        level.addFreshEntity(cyborgExc);
        loopMiner = cyborgExc;

        spawnHologram(level, quarryCenter.getX() + 0.5, baseY + 3.2, quarryCenter.getZ() + 0.5, "§6[Zona 1: Mineração em Loop]");
        spawnHologram(level, quarryCenter.getX() + 0.5, baseY + 2.9, quarryCenter.getZ() + 0.5, "§7Extração e regeneração contínua de minerais");

        BlockPos farmCenter = new BlockPos(10, baseY, -92);
        FARM_CROP_POSITIONS.clear();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos p = farmCenter.offset(dx, 0, dz);
                boolean border = (Math.abs(dx) == 4 || Math.abs(dz) == 4);
                if (border) {
                    level.setBlock(p, Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
                    level.setBlock(p.above(), Blocks.IRON_BARS.defaultBlockState(), 2);
                } else if (dx == 0 && dz == 0) {
                    level.setBlock(p.below(), Blocks.SEA_LANTERN.defaultBlockState(), 2);
                    level.setBlock(p, Blocks.WATER.defaultBlockState(), 2);
                } else {
                    Block crop = switch (Math.abs(dx + dz) % 5) {
                        case 0 -> Blocks.CARROTS;
                        case 1 -> Blocks.POTATOES;
                        case 2 -> Blocks.BEETROOTS;
                        case 3 -> SandStormBlocks.XENO_WHEAT_CROP;
                        default -> Blocks.WHEAT;
                    };
                    if (crop == SandStormBlocks.XENO_WHEAT_CROP) {
                        level.setBlock(p, SandStormBlocks.HYDROPONIC_TRAY.defaultBlockState(), 2);
                    } else {
                        level.setBlock(p, Blocks.FARMLAND.defaultBlockState(), 2);
                    }
                    if (crop instanceof CropBlock cropBlock) {
                        level.setBlock(p.above(), cropBlock.getStateForAge(cropBlock.getMaxAge()), 2);
                    } else {
                        level.setBlock(p.above(), crop.defaultBlockState(), 2);
                    }
                    FARM_CROP_POSITIONS.add(p.above());
                }
            }
        }

        BlockPos farmChest = farmCenter.offset(5, 1, 0);
        setChestSafe(level, farmChest, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(farmChest) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(Items.WHEAT, 64));
            chest.setItem(1, new ItemStack(Items.CARROT, 64));
            chest.setItem(2, new ItemStack(Items.POTATO, 64));
            chest.setItem(3, new ItemStack(Items.BEETROOT, 64));
            chest.setItem(4, new ItemStack(SandStormItems.XENO_WHEAT, 64));
            chest.setItem(5, new ItemStack(SandStormItems.XENO_WHEAT_SEEDS, 64));
        }

        ACTIVE_FARMERS.clear();
        CyborgHarvesterEntity cyborgHarv1 = new CyborgHarvesterEntity(SandStormEntities.CYBORG_HARVESTER, level);
        cyborgHarv1.setPos(farmCenter.getX() + 1.5, baseY + 1.0, farmCenter.getZ() + 1.5);
        cyborgHarv1.setOwnerUUID(player.getUUID());
        cyborgHarv1.setEnergy(50000);
        cyborgHarv1.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
        cyborgHarv1.setZoneMin(farmCenter.offset(-3, 0, -3));
        cyborgHarv1.setZoneMax(farmCenter.offset(3, 2, 3));
        cyborgHarv1.setCustomName(Component.literal("§aAndroide Agrícola Alfa"));
        cyborgHarv1.setCustomNameVisible(true);
        cyborgHarv1.setPersistenceRequired();
        level.addFreshEntity(cyborgHarv1);
        ACTIVE_FARMERS.add(cyborgHarv1);

        CyborgHarvesterEntity cyborgHarv2 = new CyborgHarvesterEntity(SandStormEntities.CYBORG_HARVESTER, level);
        cyborgHarv2.setPos(farmCenter.getX() - 1.5, baseY + 1.0, farmCenter.getZ() - 1.5);
        cyborgHarv2.setOwnerUUID(player.getUUID());
        cyborgHarv2.setEnergy(50000);
        cyborgHarv2.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
        cyborgHarv2.setZoneMin(farmCenter.offset(-3, 0, -3));
        cyborgHarv2.setZoneMax(farmCenter.offset(3, 2, 3));
        cyborgHarv2.setCustomName(Component.literal("§aAndroide Agrícola Beta"));
        cyborgHarv2.setCustomNameVisible(true);
        cyborgHarv2.setPersistenceRequired();
        level.addFreshEntity(cyborgHarv2);
        ACTIVE_FARMERS.add(cyborgHarv2);

        spawnHologram(level, farmCenter.getX() + 0.5, baseY + 3.2, farmCenter.getZ() + 0.5, "§a[Zona 2: Agricultura Autônoma]");
        spawnHologram(level, farmCenter.getX() + 0.5, baseY + 2.9, farmCenter.getZ() + 0.5, "§7Colheita contínua e replantio em tempo real");

        BlockPos builderCenter = new BlockPos(0, baseY, -104);

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos p = builderCenter.offset(dx, 0, dz);
                boolean edge = (Math.abs(dx) == 5 || Math.abs(dz) == 4);
                level.setBlock(p, edge ? Blocks.CUT_SANDSTONE.defaultBlockState() : Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                if (edge) {
                    level.setBlock(p.above(), Blocks.IRON_BARS.defaultBlockState(), 2);
                }
            }
        }

        BlockPos dockPos = builderCenter.offset(-3, 1, 1);
        level.setBlock(dockPos, SandStormBlocks.CYBORG_DOCKING_STATION.defaultBlockState(), 3);

        BlockPos incubatorPos = builderCenter.offset(-3, 1, 3);
        level.setBlock(incubatorPos, SandStormBlocks.CYBORG_INCUBATOR_VAT.defaultBlockState(), 3);

        BlockPos cratePos = builderCenter.offset(-3, 1, -1);
        level.setBlock(cratePos, Blocks.CHEST.defaultBlockState(), 3);
        BlockEntity chestBe = level.getBlockEntity(cratePos);
        if (chestBe instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(Blocks.CUT_SANDSTONE, 64));
            chest.setItem(1, new ItemStack(Blocks.CHISELED_SANDSTONE, 64));
            chest.setItem(2, new ItemStack(Blocks.SMOOTH_SANDSTONE, 64));
        }

        for (int x = 0; x <= 3; x++) {
            level.setBlock(builderCenter.offset(x, 0, -1), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        }

        BUILDER_CONSTRUCTION_POSITIONS.clear();
        for (int y = 1; y <= 2; y++) {
            for (int x = 0; x <= 3; x++) {
                BlockPos bPos = builderCenter.offset(x, y, -1);
                level.setBlock(bPos, Blocks.AIR.defaultBlockState(), 3);
                BUILDER_CONSTRUCTION_POSITIONS.add(bPos);
            }
        }

        CyborgBuilderEntity cyborgBld = new CyborgBuilderEntity(SandStormEntities.CYBORG_BUILDER, level);
        cyborgBld.setPos(builderCenter.getX() + 1.5, baseY + 1.0, builderCenter.getZ() + 1.5);
        cyborgBld.setOwnerUUID(player.getUUID());
        cyborgBld.setEnergy(50000);
        cyborgBld.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
        cyborgBld.setZoneMin(builderCenter.offset(0, 1, -1));
        cyborgBld.setZoneMax(builderCenter.offset(3, 2, -1));
        cyborgBld.setCustomName(Component.literal("§bAndroide Construtor Autônomo"));
        cyborgBld.setCustomNameVisible(true);
        cyborgBld.setPersistenceRequired();
        cyborgBld.getInventory().addItem(new ItemStack(Blocks.CUT_SANDSTONE, 64));
        level.addFreshEntity(cyborgBld);
        loopBuilder = cyborgBld;

        ACTIVE_DRONES.clear();

        CargoDroneEntity droneLogistics1 = new CargoDroneEntity(SandStormEntities.CARGO_DRONE, level);
        droneLogistics1.setPos(farmCenter.getX(), baseY + 3.0, farmCenter.getZ());
        droneLogistics1.setNoGravity(true);
        droneLogistics1.setPersistenceRequired();
        droneLogistics1.setCustomName(Component.literal("§3Drone Logístico #1 [Colheita]"));
        droneLogistics1.setCustomNameVisible(true);
        droneLogistics1.setCargoStack(new ItemStack(Items.GOLDEN_CARROT, 8));
        droneLogistics1.getEnergyStorage().receiveEnergy(10000L);
        level.addFreshEntity(droneLogistics1);
        List<BlockPos> waypoints1 = List.of(
                farmCenter.offset(0, 3, 0),
                new BlockPos(0, baseY + 4, -98),
                builderCenter.offset(0, 3, 0),
                new BlockPos(0, baseY + 4, -86)
        );
        ACTIVE_DRONES.add(new ShowcasePatrolDrone(droneLogistics1, waypoints1, new int[]{0}));

        CargoDroneEntity droneLogistics2 = new CargoDroneEntity(SandStormEntities.CARGO_DRONE, level);
        droneLogistics2.setPos(quarryCenter.getX(), baseY + 3.5, quarryCenter.getZ());
        droneLogistics2.setNoGravity(true);
        droneLogistics2.setPersistenceRequired();
        droneLogistics2.setCustomName(Component.literal("§6Drone Logístico #2 [Mineração]"));
        droneLogistics2.setCustomNameVisible(true);
        droneLogistics2.setCargoStack(new ItemStack(SandStormItems.CIRCUIT_BOARD, 4));
        droneLogistics2.getEnergyStorage().receiveEnergy(10000L);
        level.addFreshEntity(droneLogistics2);
        List<BlockPos> waypoints2 = List.of(
                quarryCenter.offset(0, 3, 0),
                new BlockPos(0, baseY + 4, -92),
                farmCenter.offset(0, 3, 0),
                builderCenter.offset(0, 3, 0)
        );
        ACTIVE_DRONES.add(new ShowcasePatrolDrone(droneLogistics2, waypoints2, new int[]{0}));

        spawnHologram(level, builderCenter.getX() + 0.5, baseY + 3.4, builderCenter.getZ() + 0.5, "§b[Zona 3: Engenharia & Construção Autônoma]");
        spawnHologram(level, builderCenter.getX() + 0.5, baseY + 3.1, builderCenter.getZ() + 0.5, "§7Montagem contínua de fortificações modulares");
        spawnHologram(level, builderCenter.getX() + 2.0, baseY + 2.6, builderCenter.getZ() - 1.0, "§e[Gabarito de Obra: Barricada Defensiva]");
    }

    private static void buildMegastructureDomeShowcase(ServerLevel level, BlockPos center) {
        BlockPos domeCenter = center.offset(55, 0, 18);
        int radius = 12;

        for (int x = -16; x <= 16; x++) {
            for (int z = -16; z <= 16; z++) {
                for (int y = -2; y <= 20; y++) {
                    BlockPos p = domeCenter.offset(x, y, z);
                    if (!level.getBlockState(p).isAir()) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int distSq = x * x + z * z;
                if (distSq <= radius * radius) {
                    BlockPos p = domeCenter.offset(x, 0, z);
                    BlockPos below = p.below();

                    level.setBlock(below, Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);

                    int pdx = x - 5;
                    int pdz = z - 4;
                    int pdistSq = pdx * pdx + pdz * pdz;
                    int fdx = x + 5;
                    int fdz = z - 4;
                    int fdistSq = fdx * fdx + fdz * fdz;

                    if (pdistSq <= 12) {
                        if (pdistSq >= 8) {
                            level.setBlock(p, (pdx + pdz) % 2 == 0 ? Blocks.MOSSY_COBBLESTONE.defaultBlockState() : Blocks.PACKED_MUD.defaultBlockState(), 2);
                        } else {
                            level.setBlock(below, Blocks.SEA_LANTERN.defaultBlockState(), 2);
                            level.setBlock(p, Blocks.WATER.defaultBlockState(), 2);
                            if (pdx == 0 && pdz == 0) {
                                setSafeFlora(level, p.above(), Blocks.LILY_PAD.defaultBlockState());
                            }
                        }
                    } else if (fdistSq <= 9) {
                        if (fdistSq >= 7) {
                            level.setBlock(p, Blocks.PACKED_MUD.defaultBlockState(), 2);
                        } else {
                            level.setBlock(p, Blocks.FARMLAND.defaultBlockState(), 2);
                            if (fdx == 0 && fdz == 0) {
                                setSafeFlora(level, p.above(), Blocks.MELON.defaultBlockState());
                            } else if (fdx == 1 && fdz == 0) {
                                setSafeFlora(level, p.above(), Blocks.PUMPKIN.defaultBlockState());
                            } else if (fdz == 1) {
                                setSafeFlora(level, p.above(), Blocks.CARROTS.defaultBlockState());
                            } else {
                                setSafeFlora(level, p.above(), Blocks.WHEAT.defaultBlockState());
                            }
                        }
                    } else if (x >= 3 && x <= 5 && z >= -6 && z <= -4) {
                        if (x == 4 && z == -5) {
                            level.setBlock(p, Blocks.ROOTED_DIRT.defaultBlockState(), 2);
                            for (int ty = 1; ty <= 4; ty++) {
                                level.setBlock(p.above(ty), Blocks.OAK_LOG.defaultBlockState(), 2);
                            }
                            for (int lx = -2; lx <= 2; lx++) {
                                for (int lz = -2; lz <= 2; lz++) {
                                    if (Math.abs(lx) + Math.abs(lz) <= 3) {
                                        level.setBlock(p.offset(lx, 4, lz), Blocks.OAK_LEAVES.defaultBlockState(), 2);
                                    }
                                    if (Math.abs(lx) <= 1 && Math.abs(lz) <= 1) {
                                        level.setBlock(p.offset(lx, 5, lz), Blocks.OAK_LEAVES.defaultBlockState(), 2);
                                    }
                                }
                            }
                            level.setBlock(p.offset(0, 6, 0), Blocks.OAK_LEAVES.defaultBlockState(), 2);
                        } else {
                            level.setBlock(p, Blocks.COARSE_DIRT.defaultBlockState(), 2);
                        }
                    } else if (x >= -6 && x <= -3 && z >= -6 && z <= -4) {
                        level.setBlock(p, Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                        int bambooHeight = Math.abs(x * 3 + z * 5) % 4 + 1;
                        for (int by = 1; by <= bambooHeight; by++) {
                            setSafeFlora(level, p.above(by), Blocks.BAMBOO.defaultBlockState());
                        }
                    } else {
                        Block floorBlock;
                        if (pdistSq <= 18) {
                            floorBlock = Blocks.MOSS_BLOCK;
                        } else if (Math.abs(x - 4) <= 2 && Math.abs(z + 5) <= 2) {
                            floorBlock = Blocks.PODZOL;
                        } else {
                            floorBlock = Blocks.GRASS_BLOCK;
                        }
                        level.setBlock(p, floorBlock.defaultBlockState(), 2);

                        if (floorBlock == Blocks.MOSS_BLOCK) {
                            if ((x + z) % 5 == 0) {
                                setSafeFlora(level, p.above(), Blocks.FLOWERING_AZALEA.defaultBlockState());
                            } else if ((x + z) % 7 == 0) {
                                setSafeFlora(level, p.above(), Blocks.MOSS_CARPET.defaultBlockState());
                            }
                        } else if (floorBlock == Blocks.GRASS_BLOCK) {
                            int decorHash = Math.abs(x * 31 + z * 17) % 15;
                            if (decorHash == 1) {
                                setSafeFlora(level, p.above(), Blocks.POPPY.defaultBlockState());
                            } else if (decorHash == 2) {
                                setSafeFlora(level, p.above(), Blocks.CORNFLOWER.defaultBlockState());
                            } else if (decorHash == 3) {
                                setSafeFlora(level, p.above(), Blocks.FERN.defaultBlockState());
                            } else if (decorHash == 4) {
                                setSafeFlora(level, p.above(), Blocks.DANDELION.defaultBlockState());
                            } else if (decorHash == 5) {
                                setSafeFlora(level, p.above(), Blocks.AZURE_BLUET.defaultBlockState());
                            } else if (decorHash == 6 || decorHash == 7) {
                                setSafeFlora(level, p.above(), Blocks.SHORT_GRASS.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }

        for (int bx = 20; bx <= 42; bx++) {
            for (int bz = 17; bz <= 19; bz++) {
                BlockPos bp = center.offset(bx, 0, bz);
                boolean edge = (bz != 18);
                boolean light = (bx % 5 == 0 && bz == 18);
                Block bridgeBlock = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                level.setBlock(bp, bridgeBlock.defaultBlockState(), 2);
            }
        }

        BlockPos constructorPos = domeCenter.above();
        level.setBlock(constructorPos, SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR.defaultBlockState(), 3);
        if (level.getBlockEntity(constructorPos) instanceof MegastructureConstructorBlockEntity constructor) {
            constructor.setEnergy(500000);
        }

        BlockPos terraformerPos = domeCenter.offset(-2, 1, 4);
        level.setBlock(terraformerPos, SandStormBlocks.ATMOSPHERIC_TERRAFORMER.defaultBlockState(), 3);

        BlockPos hydroponicPos = domeCenter.offset(4, 1, 1);
        level.setBlock(hydroponicPos, SandStormBlocks.HYDROPONIC_CHAMBER.defaultBlockState(), 3);

        BlockPos condenserPos = domeCenter.offset(-5, 1, -2);
        level.setBlock(condenserPos, SandStormBlocks.DEW_CONDENSER.defaultBlockState(), 3);
    }

    private static void buildItemWing(ServerLevel level, BlockPos center) {
        int baseY = center.getY();

        AABB westWingArea = new AABB(center.getX() - 32, baseY - 2, center.getZ() + 2, center.getX() - 16, baseY + 10, center.getZ() + 65);
        level.getEntitiesOfClass(ArmorStand.class, westWingArea).forEach(Entity::discard);
        AABB eastWingArea = new AABB(center.getX() + 16, baseY - 2, center.getZ() + 2, center.getX() + 32, baseY + 10, center.getZ() + 65);
        level.getEntitiesOfClass(ArmorStand.class, eastWingArea).forEach(Entity::discard);

        for (int z = 4; z <= 56; z++) {
            for (int dx = -2; dx <= 2; dx++) {
                BlockPos pWest = center.offset(-24 + dx, 0, z);
                boolean edgeWest = Math.abs(dx) == 2;
                boolean lightWest = (dx == 0 && z % 7 == 0);
                Block bWest = lightWest ? Blocks.OCHRE_FROGLIGHT : (edgeWest ? Blocks.CUT_SANDSTONE : SandStormBlocks.SEISMIC_DAMPENER_PAVING);
                level.setBlock(pWest, bWest.defaultBlockState(), 2);
                if (edgeWest && (z == 4 || z == 56 || Math.abs(dx) == 2)) {
                    if (z != 8 && z != 15 && z != 22 && z != 29 && z != 36 && z != 43 && z != 50) {
                        level.setBlock(pWest.above(), Blocks.SANDSTONE_WALL.defaultBlockState(), 2);
                    }
                }

                BlockPos pEast = center.offset(24 + dx, 0, z);
                boolean edgeEast = Math.abs(dx) == 2;
                boolean lightEast = (dx == 0 && z % 7 == 0);
                Block bEast = lightEast ? Blocks.SEA_LANTERN : (edgeEast ? Blocks.CUT_SANDSTONE : SandStormBlocks.SEISMIC_DAMPENER_PAVING);
                level.setBlock(pEast, bEast.defaultBlockState(), 2);
                if (edgeEast && (z == 4 || z == 56 || Math.abs(dx) == 2)) {
                    if (z != 8 && z != 15 && z != 22 && z != 29 && z != 36 && z != 43 && z != 50) {
                        level.setBlock(pEast.above(), Blocks.SANDSTONE_WALL.defaultBlockState(), 2);
                    }
                }
            }
        }

        int[] transverseZ = new int[]{8, 15, 22, 29, 36, 43, 50};
        for (int tz : transverseZ) {
            for (int x = -21; x <= -6; x++) {
                BlockPos p = center.offset(x, 0, tz);
                Block b = (x % 5 == 0) ? Blocks.OCHRE_FROGLIGHT : Blocks.CUT_SANDSTONE;
                level.setBlock(p, b.defaultBlockState(), 2);
            }
            for (int x = 6; x <= 21; x++) {
                BlockPos p = center.offset(x, 0, tz);
                Block b = (x % 5 == 0) ? Blocks.SEA_LANTERN : Blocks.CUT_SANDSTONE;
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }

        BlockPos westPortal = center.offset(-24, 0, 5);
        level.setBlock(westPortal.offset(-2, 1, 0), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(westPortal.offset(-2, 2, 0), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 3);
        level.setBlock(westPortal.offset(2, 1, 0), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(westPortal.offset(2, 2, 0), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 3);
        spawnHologram(level, westPortal.getX() + 0.5, baseY + 3.2, westPortal.getZ() + 0.5, "§6§l✦ ALA OESTE: ARSENAL & EQUIPAMENTOS ✦");
        spawnHologram(level, westPortal.getX() + 0.5, baseY + 2.85, westPortal.getZ() + 0.5, "§eTrajes Espaciais, Armas, Megazord & Sensores");

        BlockPos eastPortal = center.offset(24, 0, 5);
        level.setBlock(eastPortal.offset(-2, 1, 0), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(eastPortal.offset(-2, 2, 0), Blocks.SEA_LANTERN.defaultBlockState(), 3);
        level.setBlock(eastPortal.offset(2, 1, 0), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(eastPortal.offset(2, 2, 0), Blocks.SEA_LANTERN.defaultBlockState(), 3);
        spawnHologram(level, eastPortal.getX() + 0.5, baseY + 3.2, eastPortal.getZ() + 0.5, "§b§l✦ ALA LESTE: QUÂNTICA & ROBÓTICA ✦");
        spawnHologram(level, eastPortal.getX() + 0.5, baseY + 2.85, eastPortal.getZ() + 0.5, "§3Discos Quânticos, Satélites, Androides & Ligas");

        setupItemStation(level, center.offset(-24, 0, 8),
                "§6§l[TRAJES ESPACIAIS & VIDA]",
                "§eArmadura de Vácuo, Upgrades & Suporte Vital",
                List.of(
                        new ItemStack(SandStormItems.SPACE_SUIT_HELMET, 64),
                        new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE, 64),
                        new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS, 64),
                        new ItemStack(SandStormItems.SPACE_SUIT_BOOTS, 64),
                        new ItemStack(SandStormItems.SPACE_RATION, 64),
                        new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE, 64),
                        new ItemStack(SandStormItems.BRACKISH_WATER_BOTTLE, 64),
                        new ItemStack(SandStormItems.FILTER_CARTRIDGE, 64),
                        new ItemStack(SandStormItems.SUIT_UPGRADE_BATTERY, 64),
                        new ItemStack(SandStormItems.SUIT_UPGRADE_THERMAL, 64),
                        new ItemStack(SandStormItems.SUIT_UPGRADE_SEISMIC, 64),
                        new ItemStack(SandStormItems.SUIT_UPGRADE_VISOR, 64),
                        new ItemStack(SandStormItems.SUIT_UPGRADE_JETPACK, 64),
                        new ItemStack(SandStormItems.SEISMIC_SONAR_CHIP, 64),
                        new ItemStack(SandStormItems.JETBOOT_MODULE, 64),
                        new ItemStack(SandStormItems.GRAVITATIONAL_MAGNET_CHIP, 64),
                        new ItemStack(SandStormItems.OSMOTIC_RECYCLER_MODULE, 64),
                        new ItemStack(SandStormItems.KINETIC_DEFLECTOR_CHIP, 64)
                ),
                true,
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET),
                new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE),
                new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS),
                new ItemStack(SandStormItems.SPACE_SUIT_BOOTS),
                new ItemStack(SandStormItems.SURVIVAL_DATAPAD),
                ItemStack.EMPTY
        );

        setupItemStation(level, center.offset(-24, 0, 15),
                "§c§l[ARSENAL TÁTICO & ARMAS]",
                "§4Vibro-Lâminas, Rifles, Canhões & Granadas",
                List.of(
                        new ItemStack(SandStormItems.VIBRO_CRYSKNIFE, 64),
                        new ItemStack(SandStormItems.PLASMA_RIFLE, 64),
                        new ItemStack(SandStormItems.SONIC_CANNON, 64),
                        new ItemStack(SandStormItems.EMP_BLASTER, 64),
                        new ItemStack(SandStormItems.COMPOSITE_RECURVE_BOW, 64),
                        new ItemStack(SandStormItems.CRYO_LANCE, 64),
                        new ItemStack(SandStormItems.STUN_GRENADE, 64),
                        new ItemStack(SandStormItems.SMOKE_GRENADE, 64),
                        new ItemStack(SandStormItems.NUTRIENT_BOMB, 64),
                        new ItemStack(SandStormItems.TACTICAL_DETONATOR, 64),
                        new ItemStack(SandStormItems.KINETIC_SLUG, 64),
                        new ItemStack(SandStormItems.NEURAL_OVERRIDE_LANCE, 64),
                        new ItemStack(SandStormItems.APEX_RAILGUN_HARPOON, 64)
                ),
                true,
                ItemStack.EMPTY,
                new ItemStack(Items.NETHERITE_CHESTPLATE),
                new ItemStack(Items.NETHERITE_LEGGINGS),
                new ItemStack(Items.NETHERITE_BOOTS),
                new ItemStack(SandStormItems.VIBRO_CRYSKNIFE),
                new ItemStack(SandStormItems.PLASMA_RIFLE)
        );

        setupItemStation(level, center.offset(-24, 0, 22),
                "§b§l[MEGAZORD & MOBILIDADE]",
                "§3Módulos de Voo, Submersão & Sandboard",
                List.of(
                        new ItemStack(SandStormItems.MEGAZORD_FLIGHT_MODULE, 64),
                        new ItemStack(SandStormItems.MEGAZORD_SUBMERSIBLE_HULL, 64),
                        new ItemStack(SandStormItems.MEGAZORD_TACTICAL_OVERDRIVE, 64),
                        new ItemStack(SandStormItems.SANDBOARD, 64),
                        new ItemStack(SandStormItems.VECTORED_THRUSTER, 64),
                        new ItemStack(SandStormItems.HYDRO_BALLAST_PUMP, 64),
                        new ItemStack(SandStormItems.RAPPEL_WINCH, 64),
                        new ItemStack(SandStormItems.GLOWING_RAPPEL_WINCH, 64)
                ),
                true,
                ItemStack.EMPTY,
                new ItemStack(Items.IRON_CHESTPLATE),
                ItemStack.EMPTY,
                new ItemStack(Items.IRON_BOOTS),
                new ItemStack(SandStormItems.SANDBOARD),
                new ItemStack(SandStormItems.MEGAZORD_FLIGHT_MODULE)
        );

        setupItemStation(level, center.offset(-24, 0, 29),
                "§e§l[SENSORES & FERRAMENTAS]",
                "§6Datapad, Radar de Anomalias & Analisador",
                List.of(
                        new ItemStack(SandStormItems.SURVIVAL_DATAPAD, 64),
                        new ItemStack(SandStormItems.ANOMALY_RADAR, 64),
                        new ItemStack(SandStormItems.GEOLOGICAL_SCANNER, 64),
                        new ItemStack(SandStormItems.FIELD_PROBE, 64),
                        new ItemStack(SandStormItems.ATMOSPHERIC_ANALYZER, 64),
                        new ItemStack(SandStormItems.SAND_DEPTH_PROBE, 64),
                        new ItemStack(SandStormItems.SUBDERMAL_TREMOR_SENSOR, 64),
                        new ItemStack(SandStormItems.SIGNAL_TRACKER, 64),
                        new ItemStack(SandStormItems.TRANSPONDER_TAG, 64),
                        new ItemStack(SandStormItems.REPAIR_TOOL, 64),
                        new ItemStack(SandStormItems.TRAP_DISARMER_KIT, 64),
                        new ItemStack(SandStormItems.SILICON_PICKAXE, 64)
                ),
                true,
                ItemStack.EMPTY,
                new ItemStack(Items.GOLDEN_CHESTPLATE),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                new ItemStack(SandStormItems.SURVIVAL_DATAPAD),
                new ItemStack(SandStormItems.ANOMALY_RADAR)
        );

        setupItemStation(level, center.offset(-24, 0, 36),
                "§d§l[CHAVES, UPLINKS & RELÍQUIAS]",
                "§5Chaves de Bunkers, Uplink & Núcleos",
                List.of(
                        new ItemStack(SandStormItems.CYBERNETIC_COMMAND_UPLINK, 64),
                        new ItemStack(SandStormItems.TECH_DISC, 64),
                        new ItemStack(SandStormItems.EMPTY_CARTRIDGE, 64),
                        new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE, 64),
                        new ItemStack(SandStormItems.BIOFOAM_CARTRIDGE, 64),
                        new ItemStack(SandStormItems.SUBSPACE_WARP_CORE, 64),
                        new ItemStack(SandStormItems.MOLECULAR_CARGO_CAPSULE, 64),
                        new ItemStack(SandStormItems.BIO_NEURAL_CORE, 64),
                        new ItemStack(SandStormItems.NEURAL_SYNAPSE_LINK, 64)
                ),
                true,
                ItemStack.EMPTY,
                new ItemStack(Items.DIAMOND_CHESTPLATE),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                new ItemStack(SandStormItems.CYBERNETIC_COMMAND_UPLINK),
                new ItemStack(SandStormItems.SUBSPACE_WARP_CORE)
        );

        setupItemStation(level, center.offset(-24, 0, 43),
                "§a§l[BIOLOGIA DO DESERTO & FIBRAS]",
                "§2Quitina de Verme, Dente & Bio-Compostos",
                List.of(
                        new ItemStack(SandStormItems.SANDWORM_CHITIN, 64),
                        new ItemStack(SandStormItems.SANDWORM_TOOTH, 64),
                        new ItemStack(SandStormItems.THERMAL_CARAPACE, 64),
                        new ItemStack(SandStormItems.SALT_GLAND, 64),
                        new ItemStack(SandStormItems.FLEXIBLE_BIOPOLYMER, 64),
                        new ItemStack(SandStormItems.ANCIENT_REED, 64),
                        new ItemStack(SandStormItems.ANCIENT_SEED, 64),
                        new ItemStack(SandStormItems.SYNTHETIC_FIBER, 64),
                        new ItemStack(SandStormItems.FIBERGLASS, 64),
                        new ItemStack(SandStormItems.CHITOSAN_EXTRACT, 64),
                        new ItemStack(SandStormItems.TREHALOSE_SUGAR, 64),
                        new ItemStack(SandStormItems.OSMOLYTE_GLYCEROL, 64),
                        new ItemStack(SandStormItems.NEUROACTIVE_ALKALOIDS, 64),
                        new ItemStack(SandStormItems.RADIOPROTECTIVE_MELANIN, 64),
                        new ItemStack(SandStormItems.XENO_WHEAT_SEEDS, 64),
                        new ItemStack(SandStormItems.XENO_WHEAT, 64),
                        new ItemStack(SandStormItems.DUNE_MELON_SEEDS, 64),
                        new ItemStack(SandStormItems.DUNE_MELON_SLICE, 64),
                        new ItemStack(SandStormItems.CRYO_BERRIES, 64),
                        new ItemStack(SandStormItems.THERMAL_NUTRIENT_PASTE, 64),
                        new ItemStack(SandStormItems.ELECTROLYTE_JUICE, 64),
                        new ItemStack(SandStormItems.CRYO_GLAZED_RATION, 64),
                        new ItemStack(SandStormItems.HYDROPONIC_FEAST, 64),
                        new ItemStack(SandStormItems.APEX_HEART_MATRIX, 64)
                ),
                true,
                ItemStack.EMPTY,
                new ItemStack(Items.LEATHER_CHESTPLATE),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                new ItemStack(SandStormItems.SANDWORM_TOOTH),
                new ItemStack(SandStormItems.SANDWORM_CHITIN)
        );

        List<Item> allModItems = BuiltInRegistries.ITEM.stream()
                .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(SandStormMod.MOD_ID))
                .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                .toList();
        int halfCount = allModItems.size() / 2;
        List<ItemStack> part1Stacks = new ArrayList<>();
        for (int i = 0; i < halfCount && i < 27; i++) {
            part1Stacks.add(new ItemStack(allModItems.get(i), 64));
        }
        setupItemStation(level, center.offset(-24, 0, 50),
                "§f§l[CATÁLOGO COMPLETO - PARTE 1]",
                "§7Todos os Itens do SandStorm (A a M)",
                part1Stacks,
                false,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        setupItemStation(level, center.offset(24, 0, 8),
                "§d§l[REDE & DISCOS QUÂNTICOS]",
                "§5Armazenamento 1K a Dimensional & Matrizes",
                List.of(
                        new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_1K, 64),
                        new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_4K, 64),
                        new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_16K, 64),
                        new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K, 64),
                        new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_DIMENSIONAL, 64),
                        new ItemStack(SandStormItems.STORAGE_HOUSING, 64),
                        new ItemStack(SandStormItems.QUANTUM_PROCESSOR, 64),
                        new ItemStack(SandStormItems.QUANTUM_MIND_MATRIX, 64),
                        new ItemStack(SandStormItems.SYNAPTIC_CARTRIDGE, 64)
                ),
                true,
                ItemStack.EMPTY,
                new ItemStack(Items.DIAMOND_CHESTPLATE),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K),
                new ItemStack(SandStormItems.QUANTUM_PROCESSOR)
        );

        setupItemStation(level, center.offset(24, 0, 15),
                "§9§l[SATÉLITES & CARGAS ORBITAIS]",
                "§1Reconhecimento, Refletor Solar & Lança",
                List.of(
                        new ItemStack(SandStormItems.WEATHER_RECON_SATELLITE, 64),
                        new ItemStack(SandStormItems.ORBITAL_SOLAR_REFLECTOR_SATELLITE, 64),
                        new ItemStack(SandStormItems.SAR_GEOLOGICAL_SATELLITE, 64),
                        new ItemStack(SandStormItems.ORBITAL_KINETIC_LANCE_SATELLITE, 64),
                        new ItemStack(SandStormItems.ORBITAL_RECON_PROBE, 64),
                        new ItemStack(SandStormItems.GEOLOGICAL_SURVEYOR_DRONE, 64)
                ),
                true,
                ItemStack.EMPTY,
                new ItemStack(Items.IRON_CHESTPLATE),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                new ItemStack(SandStormItems.WEATHER_RECON_SATELLITE),
                new ItemStack(SandStormItems.ORBITAL_KINETIC_LANCE_SATELLITE)
        );

        setupItemStation(level, center.offset(24, 0, 22),
                "§3§l[ELETRÔNICA & NANOTECNOLOGIA]",
                "§bChips, Circuitos, Turbinas & Nanites",
                List.of(
                        new ItemStack(SandStormItems.CIRCUIT_BOARD, 64),
                        new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 64),
                        new ItemStack(SandStormItems.SILICON_WAFER, 64),
                        new ItemStack(SandStormItems.RAW_SILICON, 64),
                        new ItemStack(SandStormItems.NANO_ACTUATOR, 64),
                        new ItemStack(SandStormItems.SUPERCONDUCTOR_TOROID, 64),
                        new ItemStack(SandStormItems.PLASMA_FOCUS_CRYSTAL, 64),
                        new ItemStack(SandStormItems.NANITE_GALLIUM_COMPOSITE, 64),
                        new ItemStack(SandStormItems.FIBERGLASS_TURBINE_BLADE, 64),
                        new ItemStack(SandStormItems.CIRCUIT_MOUNT, 64),
                        new ItemStack(SandStormItems.PRESSURE_SEAL, 64),
                        new ItemStack(SandStormItems.STRUCTURAL_PLATE, 64)
                ),
                true,
                ItemStack.EMPTY,
                new ItemStack(Items.GOLDEN_CHESTPLATE),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                new ItemStack(SandStormItems.CIRCUIT_BOARD),
                new ItemStack(SandStormItems.SUPERCONDUCTOR_TOROID)
        );

        setupItemStation(level, center.offset(24, 0, 29),
                "§a§l[OVOS DE ANDROIDES & MÁQUINAS]",
                "§2Grazer, Frostclaw, Glinthawk & Ciborgues",
                List.of(
                        new ItemStack(SandStormItems.GRAZER_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.FANGHORN_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.FROSTCLAW_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.GLINTHAWK_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.WATERWING_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.CYBORG_EXCAVATOR_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.CYBORG_BUILDER_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.CYBORG_HARVESTER_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.SCRAP_SENTINEL_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.DEFECTIVE_SENTINEL_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.AQUIFER_BEETLE_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.NOMAD_SCAVENGER_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.DERELICT_AUTOMATON_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.CYBER_HOUND_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.CYBER_STEED_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.LABORER_UNIT_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.SCOUT_DRONE_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.CRAWLER_DRONE_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.SANDWORM_SPAWN_EGG, 64),
                        new ItemStack(SandStormItems.APEX_SANDWORM_SPAWN_EGG, 64)
                ),
                true,
                ItemStack.EMPTY,
                new ItemStack(Items.CHAINMAIL_CHESTPLATE),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                new ItemStack(SandStormItems.GRAZER_SPAWN_EGG),
                new ItemStack(SandStormItems.FROSTCLAW_SPAWN_EGG)
        );

        setupItemStation(level, center.offset(24, 0, 36),
                "§2§l[CARTUCHOS DE TERRAFORMAÇÃO]",
                "§aCápsulas de Biomas para o Terraformer",
                List.of(
                        new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_PLAINS, 64),
                        new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_FOREST, 64),
                        new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_JUNGLE, 64),
                        new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_CRYO, 64),
                        new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_FUNGAL, 64),
                        new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_MAGNETIC, 64),
                        new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_OASIS, 64)
                ),
                false,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        setupItemStation(level, center.offset(24, 0, 43),
                "§e§l[LIGAS, MINERAIS & FLUIDOS]",
                "§6Titânio-Quitina, Gálio, Chumbo & Fluidos",
                List.of(
                        new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 64),
                        new ItemStack(SandStormItems.MANTLE_ALLOY_INGOT, 64),
                        new ItemStack(SandStormItems.GALLIUM_INGOT, 64),
                        new ItemStack(SandStormItems.RAW_GALLIUM, 64),
                        new ItemStack(SandStormItems.LEAD_INGOT, 64),
                        new ItemStack(SandStormItems.RAW_LEAD, 64),
                        new ItemStack(SandStormItems.STEEL_REBAR, 64),
                        new ItemStack(SandStormItems.MINERAL_SALT, 64),
                        new ItemStack(SandStormItems.PIEZO_QUARTZ_SHARD, 64),
                        new ItemStack(SandStormItems.PIEZO_RESONATOR, 64),
                        new ItemStack(SandStormItems.TECH_BUCKET, 64),
                        new ItemStack(SandStormItems.SYNAPTIC_FLUID_BUCKET, 64),
                        new ItemStack(SandStormItems.PRESSURIZED_FOSSIL_FLUID_BUCKET, 64),
                        new ItemStack(SandStormItems.SUPERHEATED_LITHIUM_CAPSULE, 64),
                        new ItemStack(SandStormItems.CRYO_CELL_COOLER, 64)
                ),
                true,
                ItemStack.EMPTY,
                new ItemStack(Items.NETHERITE_CHESTPLATE),
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE),
                new ItemStack(SandStormItems.MANTLE_ALLOY_INGOT)
        );

        List<ItemStack> part2Stacks = new ArrayList<>();
        for (int i = halfCount; i < allModItems.size() && (i - halfCount) < 27; i++) {
            part2Stacks.add(new ItemStack(allModItems.get(i), 64));
        }
        setupItemStation(level, center.offset(24, 0, 50),
                "§f§l[CATÁLOGO COMPLETO - PARTE 2]",
                "§7Todos os Itens do SandStorm (N a Z)",
                part2Stacks,
                false,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );
    }

    private static void setupItemStation(ServerLevel level, BlockPos stationPos, String title, String subtitle, List<ItemStack> items, boolean isMannequin, ItemStack head, ItemStack chest, ItemStack legs, ItemStack boots, ItemStack mainHand, ItemStack offHand) {
        boolean isWest = stationPos.getX() < 0;
        Block lightBlock = isWest ? Blocks.OCHRE_FROGLIGHT : Blocks.SEA_LANTERN;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos p = stationPos.offset(dx, 0, dz);
                Block b = (dx == 0 && dz == 0) ? lightBlock : Blocks.CUT_SANDSTONE;
                level.setBlock(p, b.defaultBlockState(), 3);
            }
        }

        BlockPos chestPos = stationPos.above();
        setChestSafe(level, chestPos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chestBe) {
            for (int i = 0; i < items.size() && i < 27; i++) {
                chestBe.setItem(i, items.get(i));
            }
        }

        BlockPos showcasePos = stationPos.north(2);
        level.setBlock(showcasePos, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(showcasePos.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 3);

        if (isMannequin) {
            spawnMannequin(level, showcasePos.above(), title, head, chest, legs, boots, mainHand, offHand);
        }

        double hX = stationPos.getX() + 0.5;
        double hY = stationPos.getY() + 1.0;
        double hZ = stationPos.getZ() + 0.5;
        spawnHologram(level, hX, hY + 2.35, hZ, title);
        spawnHologram(level, hX, hY + 2.05, hZ, subtitle);
        spawnHologram(level, hX, hY + 1.75, hZ, "§7Abra o baú para suprimentos (64x cada)");
    }


    private static void buildEntityWing(ServerLevel level, BlockPos center) {
        int baseY = center.getY();
        double entityY = baseY + 1.0;

        AABB entityArea = new AABB(center.getX() - 80, center.getY() - 30, center.getZ() - 80, center.getX() + 80, center.getY() + 80, center.getZ() + 80);
        List<Entity> oldEntities = level.getEntitiesOfClass(Entity.class, entityArea, e -> !(e instanceof ServerPlayer));
        for (Entity old : oldEntities) {
            old.discard();
        }

        placeEntityPedestal(level, new BlockPos(-12, baseY, -14));
        spawnMannequin(level, new BlockPos(-12, baseY + 1, -14), "§b[Megazord Titan] Chassi Padrão",
                new ItemStack(Items.NETHERITE_HELMET), new ItemStack(Items.NETHERITE_CHESTPLATE), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_BOOTS),
                new ItemStack(SandStormItems.VIBRO_CRYSKNIFE), ItemStack.EMPTY);
        MegazordEntity mechaStd = new MegazordEntity(SandStormEntities.MEGAZORD, level);
        mechaStd.setPos(-12.0, entityY, -14.0);
        mechaStd.setPersistenceRequired();
        mechaStd.getEnergyStorage().receiveEnergy(100000L);
        level.addFreshEntity(mechaStd);

        placeEntityPedestal(level, new BlockPos(-4, baseY, -14));
        spawnMannequin(level, new BlockPos(-4, baseY + 1, -14), "§e[Megazord Titan] Módulo de Voo",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(Items.ELYTRA), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_BOOTS),
                new ItemStack(SandStormItems.MEGAZORD_FLIGHT_MODULE), new ItemStack(SandStormItems.CYBERNETIC_COMMAND_UPLINK));
        MegazordEntity mechaFlight = new MegazordEntity(SandStormEntities.MEGAZORD, level);
        mechaFlight.setPos(-4.0, entityY, -14.0);
        mechaFlight.setFlightModule(true);
        mechaFlight.setPersistenceRequired();
        mechaFlight.getEnergyStorage().receiveEnergy(100000L);
        level.addFreshEntity(mechaFlight);

        placeEntityPedestal(level, new BlockPos(4, baseY, -14));
        spawnMannequin(level, new BlockPos(4, baseY + 1, -14), "§9[Megazord Titan] Casco Submersível",
                new ItemStack(Items.TURTLE_HELMET), new ItemStack(Items.NETHERITE_CHESTPLATE), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_BOOTS),
                new ItemStack(SandStormItems.MEGAZORD_SUBMERSIBLE_HULL), new ItemStack(Items.TRIDENT));
        MegazordEntity mechaSub = new MegazordEntity(SandStormEntities.MEGAZORD, level);
        mechaSub.setPos(4.0, entityY, -14.0);
        mechaSub.setSubmersibleModule(true);
        mechaSub.setPersistenceRequired();
        mechaSub.getEnergyStorage().receiveEnergy(100000L);
        level.addFreshEntity(mechaSub);

        placeEntityPedestal(level, new BlockPos(12, baseY, -14));
        spawnMannequin(level, new BlockPos(12, baseY + 1, -14), "§c[Megazord Titan] Sobrecarga Tática Apex",
                new ItemStack(Items.NETHERITE_HELMET), new ItemStack(Items.NETHERITE_CHESTPLATE), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_BOOTS),
                new ItemStack(SandStormItems.MEGAZORD_TACTICAL_OVERDRIVE), new ItemStack(SandStormItems.HEAVY_PLASMA_CANNON));
        MegazordEntity mechaApex = new MegazordEntity(SandStormEntities.MEGAZORD, level);
        mechaApex.setPos(12.0, entityY, -14.0);
        mechaApex.setFlightModule(true);
        mechaApex.setSubmersibleModule(true);
        mechaApex.setOverdriveModule(true);
        mechaApex.setPersistenceRequired();
        mechaApex.getEnergyStorage().receiveEnergy(MegazordEntity.OVERDRIVE_BATTERY_CAPACITY);
        level.addFreshEntity(mechaApex);

        placeEntityPedestal(level, new BlockPos(-12, baseY, -22));
        spawnMannequin(level, new BlockPos(-12, baseY + 1, -22), "§6Veículo Escavador Pesado",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(Items.GOLDEN_CHESTPLATE), new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_BOOTS),
                new ItemStack(SandStormItems.SILICON_PICKAXE), new ItemStack(SandStormItems.GEOLOGICAL_SCANNER));
        ExcavatorVehicleEntity rover = new ExcavatorVehicleEntity(SandStormEntities.EXCAVATOR_VEHICLE, level);
        rover.setPos(-12.0, entityY, -22.0);
        rover.setPersistenceRequired();
        rover.getEnergyStorage().receiveEnergy(50000L);
        level.addFreshEntity(rover);

        placeEntityPedestal(level, new BlockPos(-4, baseY, -22));
        spawnMannequin(level, new BlockPos(-4, baseY + 1, -22), "§ePrancha de Areia (Sandboard)",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE), new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS), new ItemStack(SandStormItems.SPACE_SUIT_BOOTS),
                new ItemStack(SandStormItems.SANDBOARD), ItemStack.EMPTY);
        SandboardEntity board = new SandboardEntity(SandStormEntities.SANDBOARD, level);
        board.setPos(-4.0, entityY, -22.0);
        board.setPersistenceRequired();
        level.addFreshEntity(board);

        placeEntityPedestal(level, new BlockPos(0, baseY, -22));
        CyberSteedEntity steedExhibition = new CyberSteedEntity(SandStormEntities.CYBER_STEED, level);
        steedExhibition.setPos(0.0, entityY, -22.0);
        steedExhibition.setPersistenceRequired();
        steedExhibition.setTame(true, false);
        steedExhibition.setOrderedToSit(false);
        level.addFreshEntity(steedExhibition);

        placeEntityPedestal(level, new BlockPos(4, baseY, -22));
        CargoDroneEntity cargoDrone = new CargoDroneEntity(SandStormEntities.CARGO_DRONE, level);
        cargoDrone.setPos(4.0, entityY + 1.0, -22.0);
        cargoDrone.setNoGravity(true);
        cargoDrone.setPersistenceRequired();
        cargoDrone.setCustomName(Component.literal("§3Drone de Carga Logístico"));
        cargoDrone.setCustomNameVisible(true);
        cargoDrone.setCargoStack(new ItemStack(SandStormItems.TECH_DISC));
        cargoDrone.getEnergyStorage().receiveEnergy(10000L);
        level.addFreshEntity(cargoDrone);

        placeEntityPedestal(level, new BlockPos(12, baseY, -22));
        BuilderDroneEntity builderDrone = new BuilderDroneEntity(SandStormEntities.BUILDER_DRONE, level);
        builderDrone.setPos(12.0, entityY + 1.0, -22.0);
        builderDrone.setNoGravity(true);
        builderDrone.setPersistenceRequired();
        builderDrone.setCustomName(Component.literal("§bDrone Construtor Autônomo"));
        builderDrone.setCustomNameVisible(true);
        level.addFreshEntity(builderDrone);


        spawnHologram(level, -12.0, entityY + 3.2, -14.0, "§b[Megazord Titan - Padrão]");
        spawnHologram(level, -12.0, entityY + 2.9, -14.0, "§7Blindagem Pesada e Combate");

        spawnHologram(level, -4.0, entityY + 3.2, -14.0, "§e[Megazord Titan - Voo]");
        spawnHologram(level, -4.0, entityY + 2.9, -14.0, "§7Propulsão a Jato Integrada");

        spawnHologram(level, 4.0, entityY + 3.2, -14.0, "§9[Megazord Titan - Submersível]");
        spawnHologram(level, 4.0, entityY + 2.9, -14.0, "§7Navegação Aquática e Oxigênio");

        spawnHologram(level, 12.0, entityY + 3.2, -14.0, "§c[Megazord Titan - Apex]");
        spawnHologram(level, 12.0, entityY + 2.9, -14.0, "§7Sobrecarga Tática e Canhões");

        spawnHologram(level, -12.0, entityY + 2.2, -22.0, "§6[Veículo Escavador Pesado]");
        spawnHologram(level, -12.0, entityY + 1.9, -22.0, "§7Mineração e Coleta em Massa");

        spawnHologram(level, -4.0, entityY + 1.8, -22.0, "§e[Prancha de Areia (Sandboard)]");
        spawnHologram(level, -4.0, entityY + 1.5, -22.0, "§7Navegação Rápida em Dunas");

        spawnHologram(level, 0.0, entityY + 2.5, -22.0, "§e[Galope Cibernético (Horizon)]");
        spawnHologram(level, 0.0, entityY + 2.15, -22.0, "§7Montaria Robótica do Deserto");

        spawnHologram(level, 4.0, entityY + 2.2, -22.0, "§3[Drone de Carga Logístico]");
        spawnHologram(level, 4.0, entityY + 1.9, -22.0, "§7Transporte Aéreo WPT");

        spawnHologram(level, 12.0, entityY + 2.2, -22.0, "§b[Drone Construtor Autônomo]");
        spawnHologram(level, 12.0, entityY + 1.9, -22.0, "§7Montagem Autônoma de Blocos");


        placeEntityPedestal(level, new BlockPos(-16, baseY, -30));
        GrazerEntity grazer = new GrazerEntity(SandStormEntities.GRAZER, level);
        grazer.setPos(-16.0, entityY, -30.0);
        grazer.setPersistenceRequired();
        grazer.setCustomName(Component.literal("Pastador (Grazer)"));
        grazer.setCustomNameVisible(true);
        level.addFreshEntity(grazer);

        placeEntityPedestal(level, new BlockPos(-8, baseY, -30));
        FanghornEntity fanghorn = new FanghornEntity(SandStormEntities.FANGHORN, level);
        fanghorn.setPos(-8.0, entityY, -30.0);
        fanghorn.setPersistenceRequired();
        fanghorn.setCustomName(Component.literal("Presalonga (Fanghorn)"));
        fanghorn.setCustomNameVisible(true);
        level.addFreshEntity(fanghorn);

        placeEntityPedestal(level, new BlockPos(0, baseY, -30));
        FrostclawEntity frostclaw = new FrostclawEntity(SandStormEntities.FROSTCLAW, level);
        frostclaw.setPos(0.0, entityY, -30.0);
        frostclaw.setPersistenceRequired();
        frostclaw.setCustomName(Component.literal("Garra-Gelida (Frostclaw)"));
        frostclaw.setCustomNameVisible(true);
        level.addFreshEntity(frostclaw);

        placeEntityPedestal(level, new BlockPos(8, baseY, -30));
        GlinthawkEntity glinthawk = new GlinthawkEntity(SandStormEntities.GLINTHAWK, level);
        glinthawk.setPos(8.0, entityY + 1.0, -30.0);
        glinthawk.setNoGravity(true);
        glinthawk.setPersistenceRequired();
        glinthawk.setCustomName(Component.literal("Gaviao-Reluzente (Glinthawk)"));
        glinthawk.setCustomNameVisible(true);
        level.addFreshEntity(glinthawk);

        placeEntityPedestal(level, new BlockPos(16, baseY, -30));
        WaterwingEntity waterwing = new WaterwingEntity(SandStormEntities.WATERWING, level);
        waterwing.setPos(16.0, entityY, -30.0);
        waterwing.setPersistenceRequired();
        waterwing.setTame(true, false);
        waterwing.setOrderedToSit(false);
        waterwing.setCustomName(Component.literal("Asad'agua (Waterwing)"));
        waterwing.setCustomNameVisible(true);
        level.addFreshEntity(waterwing);

        spawnHologram(level, -16.0, entityY + 2.5, -30.0, "[Pastador (Grazer)]");
        spawnHologram(level, -16.0, entityY + 2.15, -30.0, "Coleta de Blaze e Terraformacao");

        spawnHologram(level, -8.0, entityY + 2.5, -30.0, "[Presalonga (Fanghorn)]");
        spawnHologram(level, -8.0, entityY + 2.15, -30.0, "Escolta Termica e Disparos de Fogo");

        spawnHologram(level, 0.0, entityY + 3.2, -30.0, "[Garra-Gelida (Frostclaw)]");
        spawnHologram(level, 0.0, entityY + 2.85, -30.0, "Guardiao Criogenico Apex");

        spawnHologram(level, 8.0, entityY + 2.5, -30.0, "[Gaviao-Reluzente (Glinthawk)]");
        spawnHologram(level, 8.0, entityY + 2.15, -30.0, "Predador Aereo Catador de Sucata");

        spawnHologram(level, 16.0, entityY + 2.5, -30.0, "[Asad'agua (Waterwing)]");
        spawnHologram(level, 16.0, entityY + 2.15, -30.0, "Montaria Anfibio: Voo e Mergulho");

        placeEntityPedestal(level, new BlockPos(0, baseY, -38));
        spawnMannequin(level, new BlockPos(0, baseY + 1, -38), "§5Apex Sandworm Sovereign",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(Items.NETHERITE_CHESTPLATE), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_BOOTS),
                new ItemStack(SandStormItems.APEX_RAILGUN_HARPOON), new ItemStack(SandStormItems.APEX_HEART_MATRIX));
        spawnHologram(level, 0.0, entityY + 3.2, -38.0, "§5[Apex Sandworm Sovereign]");
        spawnHologram(level, 0.0, entityY + 2.85, -38.0, "§dInvocação Sísmica no Manto (Y < 0)");
    }

    private static void placeEntityPedestal(ServerLevel level, BlockPos baseCenter) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos p = baseCenter.offset(x, 0, z);
                Block b = (x == 0 && z == 0) ? Blocks.OCHRE_FROGLIGHT : Blocks.CUT_SANDSTONE;
                level.setBlock(p, b.defaultBlockState(), 3);
            }
        }
    }

    private static void buildPistonTestingRange(ServerLevel level, BlockPos center) {
        int baseY = center.getY();

        AABB arenaBox = new AABB(center.getX() - 16, baseY - 2, center.getZ() - 10, center.getX() + 16, baseY + 12, center.getZ() + 32);
        level.getEntitiesOfClass(ArmorStand.class, arenaBox).forEach(Entity::discard);

        for (int dx = -14; dx <= 14; dx++) {
            for (int dz = -8; dz <= 28; dz++) {
                for (int dy = 1; dy <= 6; dy++) {
                    BlockPos p = center.offset(dx, dy, dz);
                    if (!level.getBlockState(p).isAir()) {
                        setAirSafe(level, p);
                    }
                }
                BlockPos floor = center.offset(dx, 0, dz);
                boolean border = (Math.abs(dx) == 14 || dz == -8 || dz == 28);
                Block b = border ? Blocks.POLISHED_DEEPSLATE : Blocks.SMOOTH_SANDSTONE;
                level.setBlock(floor, b.defaultBlockState(), 2);
                if (border && !(dx >= 10 && dx <= 14 && dz >= -1 && dz <= 1)) {
                    level.setBlock(floor.above(), Blocks.POLISHED_DEEPSLATE_WALL.defaultBlockState(), 2);
                }
            }
        }

        spawnHologram(level, center.getX() + 0.5, baseY + 4.2, center.getZ() - 6.0, "§e§l[CAMPO DE PROVAS: SUPER PISTÕES SANDSTORM]");
        spawnHologram(level, center.getX() + 0.5, baseY + 3.85, center.getZ() - 6.0, "§7Pistões de Alta Força, Meia-Laje e Empuxo Tectônico de Obsidiana");

        setupReturnPad(level, center.offset(11, 1, 0));

        BlockPos p1 = center.offset(-10, 1, 0);
        level.setBlock(p1, SandStormBlocks.COMPACT_SLAB_PISTON.defaultBlockState()
                .setValue(CompactSlabPistonBlock.FACING, Direction.SOUTH)
                .setValue(CompactSlabPistonBlock.TOP_SLAB, true), 3);
        level.setBlock(p1.north(), Blocks.LEVER.defaultBlockState(), 3);
        for (int i = 1; i <= 7; i++) {
            level.setBlock(p1.south(i), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
            level.setBlock(p1.south(i).below(), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 2);
        }
        spawnHologram(level, p1.getX() + 0.5, baseY + 2.5, p1.getZ() - 0.5, "§e§l[Meia-Laje]");
        spawnHologram(level, p1.getX() + 0.5, baseY + 2.15, p1.getZ() - 0.5, "§70.5b | Laje topo");
        spawnHologram(level, p1.getX() + 0.5, baseY + 1.8, p1.getZ() - 0.5, "§a7 blocos");

        BlockPos p2 = center.offset(-6, 1, 0);
        level.setBlock(p2, SandStormBlocks.COMPACT_STICKY_SLAB_PISTON.defaultBlockState()
                .setValue(CompactSlabPistonBlock.FACING, Direction.SOUTH)
                .setValue(CompactSlabPistonBlock.TOP_SLAB, true), 3);
        level.setBlock(p2.north(), Blocks.LEVER.defaultBlockState(), 3);
        for (int i = 1; i <= 7; i++) {
            level.setBlock(p2.south(i), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
            level.setBlock(p2.south(i).below(), Blocks.SEA_LANTERN.defaultBlockState(), 2);
        }
        spawnHologram(level, p2.getX() + 0.5, baseY + 2.5, p2.getZ() - 0.5, "§a§l[Meia-Laje Grudento]");
        spawnHologram(level, p2.getX() + 0.5, baseY + 2.15, p2.getZ() - 0.5, "§7Empurra & Puxa");
        spawnHologram(level, p2.getX() + 0.5, baseY + 1.8, p2.getZ() - 0.5, "§a7 blocos");

        BlockPos p3 = center.offset(-2, 1, 0);
        level.setBlock(p3, SandStormBlocks.HEAVY_KINETIC_PISTON.defaultBlockState()
                .setValue(HeavyKineticPistonBlock.FACING, Direction.SOUTH), 3);
        level.setBlock(p3.north(), Blocks.LEVER.defaultBlockState(), 3);
        for (int i = 1; i <= 24; i++) {
            level.setBlock(p3.south(i), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
            if (i % 4 == 0) {
                level.setBlock(p3.south(i).below(), Blocks.SEA_LANTERN.defaultBlockState(), 2);
            }
        }
        spawnHologram(level, p3.getX() + 0.5, baseY + 2.5, p3.getZ() - 0.5, "§6§l[Cinético]");
        spawnHologram(level, p3.getX() + 0.5, baseY + 2.15, p3.getZ() - 0.5, "§7Potência 2x");
        spawnHologram(level, p3.getX() + 0.5, baseY + 1.8, p3.getZ() - 0.5, "§a24 blocos");

        BlockPos p4 = center.offset(2, 1, 0);
        level.setBlock(p4, SandStormBlocks.HEAVY_STICKY_KINETIC_PISTON.defaultBlockState()
                .setValue(HeavyKineticPistonBlock.FACING, Direction.SOUTH), 3);
        level.setBlock(p4.north(), Blocks.LEVER.defaultBlockState(), 3);
        for (int i = 1; i <= 24; i++) {
            level.setBlock(p4.south(i), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3);
            if (i % 4 == 0) {
                level.setBlock(p4.south(i).below(), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 2);
            }
        }
        spawnHologram(level, p4.getX() + 0.5, baseY + 2.5, p4.getZ() - 0.5, "§6§l[Cinético Grudento]");
        spawnHologram(level, p4.getX() + 0.5, baseY + 2.15, p4.getZ() - 0.5, "§7Empurra & Retrai");
        spawnHologram(level, p4.getX() + 0.5, baseY + 1.8, p4.getZ() - 0.5, "§a24 blocos");

        BlockPos p5 = center.offset(6, 1, 0);
        level.setBlock(p5, SandStormBlocks.TECTONIC_KINETIC_PISTON.defaultBlockState()
                .setValue(TectonicKineticPistonBlock.FACING, Direction.SOUTH), 3);
        level.setBlock(p5.north(), Blocks.LEVER.defaultBlockState(), 3);
        level.setBlock(p5.south(1), Blocks.OBSIDIAN.defaultBlockState(), 3);
        level.setBlock(p5.south(2), Blocks.CRYING_OBSIDIAN.defaultBlockState(), 3);
        level.setBlock(p5.south(3), Blocks.OBSIDIAN.defaultBlockState(), 3);
        for (int i = 4; i <= 16; i++) {
            level.setBlock(p5.south(i), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        }
        for (int i = 1; i <= 16; i++) {
            if (i <= 3) {
                level.setBlock(p5.south(i).below(), Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
            } else if (i % 4 == 0) {
                level.setBlock(p5.south(i).below(), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 2);
            }
        }
        spawnHologram(level, p5.getX() + 0.5, baseY + 2.6, p5.getZ() - 0.5, "§d§l[Tectônico]");
        spawnHologram(level, p5.getX() + 0.5, baseY + 2.25, p5.getZ() - 0.5, "§c★ EMPURRA OBSIDIANA! ★");
        spawnHologram(level, p5.getX() + 0.5, baseY + 1.9, p5.getZ() - 0.5, "§7Até 48 blocos");

        BlockPos p6 = center.offset(10, 1, 0);
        level.setBlock(p6, SandStormBlocks.TECTONIC_STICKY_KINETIC_PISTON.defaultBlockState()
                .setValue(TectonicKineticPistonBlock.FACING, Direction.SOUTH), 3);
        level.setBlock(p6.north(), Blocks.LEVER.defaultBlockState(), 3);
        level.setBlock(p6.south(1), Blocks.CRYING_OBSIDIAN.defaultBlockState(), 3);
        level.setBlock(p6.south(2), Blocks.OBSIDIAN.defaultBlockState(), 3);
        for (int i = 3; i <= 16; i++) {
            level.setBlock(p6.south(i), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 3);
        }
        for (int i = 1; i <= 16; i++) {
            if (i <= 2) {
                level.setBlock(p6.south(i).below(), Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
            } else if (i % 4 == 0) {
                level.setBlock(p6.south(i).below(), Blocks.SEA_LANTERN.defaultBlockState(), 2);
            }
        }
        spawnHologram(level, p6.getX() + 0.5, baseY + 2.6, p6.getZ() - 0.5, "§d§l[Tectônico Grudento]");
        spawnHologram(level, p6.getX() + 0.5, baseY + 2.25, p6.getZ() - 0.5, "§c★ PUXA OBSIDIANA! ★");
        spawnHologram(level, p6.getX() + 0.5, baseY + 1.9, p6.getZ() - 0.5, "§7Força de 48 blocos");
    }

    private static void buildPistonWalkway(ServerLevel level, BlockPos center) {
        int baseY = center.getY();
        for (int x = -2; x >= -36; x--) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = center.offset(x, 0, -65 + dz);
                boolean edge = Math.abs(dz) == 2;
                boolean light = (Math.abs(x) % 4 == 0 && dz == 0);
                Block b = light ? Blocks.OCHRE_FROGLIGHT : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
                if (edge && Math.abs(x) % 6 == 0) {
                    level.setBlock(p.above(), Blocks.SANDSTONE_WALL.defaultBlockState(), 2);
                    level.setBlock(p.above(2), Blocks.SEA_LANTERN.defaultBlockState(), 2);
                }
            }
        }
        BlockPos portalArch = center.offset(-2, 0, -65);
        spawnHologram(level, portalArch.getX() - 1.5, baseY + 3.2, portalArch.getZ() + 0.5, "§e§l◀ SUPER PISTÕES SANDSTORM");
        spawnHologram(level, portalArch.getX() - 1.5, baseY + 2.85, portalArch.getZ() + 0.5, "§7Pistas de Teste: Meia-Laje, 24 Blocos & Obsidiana");
    }

    private static void buildShowcaseWalkway(ServerLevel level, BlockPos start, BlockPos end) {
        if (start.getX() != end.getX() && start.getZ() != end.getZ()) {
            throw new IllegalArgumentException("Showcase walkway must be straight");
        }
        int stepX = Integer.compare(end.getX(), start.getX());
        int stepZ = Integer.compare(end.getZ(), start.getZ());
        int length = Math.max(Math.abs(end.getX() - start.getX()), Math.abs(end.getZ() - start.getZ()));
        for (int index = 0; index <= length; index++) {
            BlockPos pathCenter = start.offset(stepX * index, 0, stepZ * index);
            for (int width = -2; width <= 2; width++) {
                BlockPos pathPos = stepX == 0 ? pathCenter.offset(width, 0, 0) : pathCenter.offset(0, 0, width);
                boolean edge = Math.abs(width) == 2;
                boolean light = index % 4 == 0 && width == 0;
                Block block = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(pathPos.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                level.setBlock(pathPos, block.defaultBlockState(), 2);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void spawnHologram(ServerLevel level, double x, double y, double z, String text) {
        AABB box = new AABB(x - 0.4, y - 0.4, z - 0.4, x + 0.4, y + 0.4, z + 0.4);
        level.getEntitiesOfClass(ArmorStand.class, box).forEach(Entity::discard);
        EntityType<ArmorStand> armorStandType = (EntityType<ArmorStand>) (EntityType<?>) BuiltInRegistries.ENTITY_TYPE.getValue(SandStormMod.mcId("armor_stand"));
        ArmorStand stand = new ArmorStand(armorStandType, level);
        stand.setPos(x, y, z);
        stand.setInvisible(true);
        stand.setNoGravity(true);
        stand.setNoBasePlate(true);
        byte flags = stand.getEntityData().get(ArmorStand.DATA_CLIENT_FLAGS);
        stand.getEntityData().set(ArmorStand.DATA_CLIENT_FLAGS, (byte) (flags | ArmorStand.CLIENT_FLAG_MARKER));
        stand.setCustomName(Component.literal(text));
        stand.setCustomNameVisible(true);
        level.addFreshEntity(stand);
    }

    @SuppressWarnings("unchecked")
    private static void spawnMannequin(ServerLevel level, BlockPos pos, String name, ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet, ItemStack mainhand, ItemStack offhand) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);

        EntityType<ArmorStand> armorStandType = (EntityType<ArmorStand>) (EntityType<?>) BuiltInRegistries.ENTITY_TYPE.getValue(SandStormMod.mcId("armor_stand"));
        ArmorStand stand = new ArmorStand(armorStandType, level);
        stand.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        stand.setShowArms(true);
        stand.setCustomName(Component.literal(name));
        stand.setCustomNameVisible(true);
        if (!head.isEmpty()) {
            stand.setItemSlot(EquipmentSlot.HEAD, head);
        }
        if (!chest.isEmpty()) {
            stand.setItemSlot(EquipmentSlot.CHEST, chest);
        }
        if (!legs.isEmpty()) {
            stand.setItemSlot(EquipmentSlot.LEGS, legs);
        }
        if (!feet.isEmpty()) {
            stand.setItemSlot(EquipmentSlot.FEET, feet);
        }
        if (!mainhand.isEmpty()) {
            stand.setItemSlot(EquipmentSlot.MAINHAND, mainhand);
        }
        if (!offhand.isEmpty()) {
            stand.setItemSlot(EquipmentSlot.OFFHAND, offhand);
        }
        level.addFreshEntity(stand);
    }

    public static void buildQuantumComputerStation(ServerLevel level, BlockPos stationCenter) {
        int baseY = stationCenter.getY();

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                for (int dy = 1; dy <= 6; dy++) {
                    BlockPos p = stationCenter.offset(dx, dy, dz);
                    if (!level.getBlockState(p).isAir()) {
                        setAirSafe(level, p);
                    }
                }
            }
        }

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                BlockPos p = stationCenter.offset(dx, 0, dz);
                boolean edge = Math.abs(dx) == 7 || Math.abs(dz) == 7;
                boolean corner = Math.abs(dx) == 7 && Math.abs(dz) == 7;
                Block b = corner ? Blocks.OCHRE_FROGLIGHT : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 3);
                if (edge && !corner && !(dx == -7 && Math.abs(dz) <= 2)) {
                    level.setBlock(p.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 3);
                }
            }
        }

        BlockPos controllerPos = stationCenter.offset(-1, 1, 0);
        BlockPos drivePos = stationCenter.offset(0, 1, 0);
        BlockPos terminalPos = stationCenter.offset(1, 1, 0);

        BlockPos cable1 = stationCenter.offset(-1, 1, -1);
        BlockPos cable2 = stationCenter.offset(0, 1, -1);
        BlockPos cable3 = stationCenter.offset(1, 1, -1);
        level.setBlock(cable1, SandStormBlocks.QUANTUM_NETWORK_CABLE.defaultBlockState(), 3);
        level.setBlock(cable2, SandStormBlocks.QUANTUM_NETWORK_CABLE.defaultBlockState(), 3);
        level.setBlock(cable3, SandStormBlocks.QUANTUM_NETWORK_CABLE.defaultBlockState(), 3);

        BlockPos accumulatorPos = stationCenter.offset(-3, 1, 0);
        BlockPos solarPos = stationCenter.offset(-3, 1, -1);
        level.setBlock(accumulatorPos, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
        level.setBlock(solarPos, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
        if (level.getBlockEntity(accumulatorPos) instanceof SolidStateAccumulatorBlockEntity accBe) {
            accBe.setStoredEnergy(500000);
        }

        level.setBlock(controllerPos, SandStormBlocks.QUANTUM_NETWORK_CONTROLLER.defaultBlockState().setValue(QuantumNetworkControllerBlock.ACTIVE, true), 3);
        if (level.getBlockEntity(controllerPos) instanceof QuantumNetworkControllerBlockEntity ctrlBe) {
            ctrlBe.receiveEnergy(100000);
            ctrlBe.setChanged();
        }

        BlockEntity existingDrive = level.getBlockEntity(drivePos);
        if (existingDrive instanceof Container container) {
            container.clearContent();
        }
        level.setBlock(drivePos, SandStormBlocks.QUANTUM_DISK_DRIVE.defaultBlockState().setValue(QuantumDiskDriveBlock.FACING, Direction.SOUTH), 3);
        if (level.getBlockEntity(drivePos) instanceof QuantumDiskDriveBlockEntity driveBe) {
            ItemStack c0 = new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K);
            QuantumDiskStorage.saveStoredItems(c0, List.of(
                    new StoredItemEntry(new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE), 1000L),
                    new StoredItemEntry(new ItemStack(SandStormItems.PIEZO_QUARTZ_SHARD), 2000L),
                    new StoredItemEntry(new ItemStack(SandStormItems.SUPERCONDUCTOR_TOROID), 500L),
                    new StoredItemEntry(new ItemStack(SandStormItems.QUANTUM_PROCESSOR), 250L),
                    new StoredItemEntry(new ItemStack(SandStormItems.BIO_NEURAL_CORE), 128L),
                    new StoredItemEntry(new ItemStack(SandStormItems.MANTLE_ALLOY_INGOT), 500L)
            ));
            driveBe.setItem(0, c0);

            ItemStack c1 = new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K);
            QuantumDiskStorage.saveStoredItems(c1, List.of(
                    new StoredItemEntry(new ItemStack(SandStormItems.RAW_LITHIUM_SALTS), 2500L),
                    new StoredItemEntry(new ItemStack(SandStormItems.RAW_SILICON), 2000L),
                    new StoredItemEntry(new ItemStack(SandStormItems.SILICON_WAFER), 1000L),
                    new StoredItemEntry(new ItemStack(SandStormItems.CIRCUIT_BOARD), 500L),
                    new StoredItemEntry(new ItemStack(SandStormItems.NANO_ACTUATOR), 500L),
                    new StoredItemEntry(new ItemStack(SandStormItems.SCRAP_METAL), 3000L)
            ));
            driveBe.setItem(1, c1);

            ItemStack c2 = new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_16K);
            QuantumDiskStorage.saveStoredItems(c2, List.of(
                    new StoredItemEntry(new ItemStack(SandStormItems.BIO_COOLANT_CANISTER), 256L),
                    new StoredItemEntry(new ItemStack(SandStormItems.SANDWORM_CHITIN), 500L),
                    new StoredItemEntry(new ItemStack(SandStormItems.SANDWORM_TOOTH), 128L),
                    new StoredItemEntry(new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE), 250L)
            ));
            driveBe.setItem(2, c2);

            ItemStack c3 = new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_16K);
            QuantumDiskStorage.saveStoredItems(c3, List.of(
                    new StoredItemEntry(new ItemStack(SandStormItems.KINETIC_SLUG), 1000L),
                    new StoredItemEntry(new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE), 200L),
                    new StoredItemEntry(new ItemStack(SandStormItems.PIEZO_RESONATOR), 100L),
                    new StoredItemEntry(new ItemStack(SandStormItems.ADRENAL_STIM), 64L),
                    new StoredItemEntry(new ItemStack(SandStormItems.BIOFOAM_CARTRIDGE), 64L)
            ));
            driveBe.setItem(3, c3);

            ItemStack c4 = new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_4K);
            driveBe.setItem(4, c4);

            ItemStack c5 = new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_1K);
            driveBe.setItem(5, c5);

            ItemStack c6 = new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_DIMENSIONAL);
            driveBe.setItem(6, c6);

            ItemStack c7 = new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_DIMENSIONAL);
            QuantumDiskStorage.saveStoredItems(c7, List.of(
                    new StoredItemEntry(new ItemStack(SandStormItems.SURVIVAL_DATAPAD), 1L),
                    new StoredItemEntry(new ItemStack(SandStormItems.ANOMALY_RADAR), 1L),
                    new StoredItemEntry(new ItemStack(SandStormItems.WEATHER_RECON_SATELLITE), 4L),
                    new StoredItemEntry(new ItemStack(SandStormItems.ORBITAL_SOLAR_REFLECTOR_SATELLITE), 4L),
                    new StoredItemEntry(new ItemStack(SandStormItems.SAR_GEOLOGICAL_SATELLITE), 4L),
                    new StoredItemEntry(new ItemStack(SandStormItems.ORBITAL_KINETIC_LANCE_SATELLITE), 4L)
            ));
            driveBe.setItem(7, c7);

            driveBe.setChanged();
        }

        level.setBlock(terminalPos, SandStormBlocks.QUANTUM_ACCESS_TERMINAL.defaultBlockState().setValue(QuantumAccessTerminalBlock.FACING, Direction.SOUTH), 3);

        BlockPos chestPos = stationCenter.offset(3, 1, 0);
        setChestSafe(level, chestPos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_1K, 4));
            chest.setItem(1, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_4K, 4));
            chest.setItem(2, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_16K, 2));
            chest.setItem(3, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K, 2));
            chest.setItem(4, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_DIMENSIONAL, 1));
            chest.setItem(5, new ItemStack(SandStormItems.STORAGE_HOUSING, 8));
            chest.setItem(6, new ItemStack(SandStormBlocks.QUANTUM_NETWORK_CABLE, 32));
            chest.setItem(7, new ItemStack(SandStormBlocks.QUANTUM_ACCESS_TERMINAL, 2));
            chest.setItem(8, new ItemStack(SandStormBlocks.QUANTUM_DISK_DRIVE, 2));
        }

        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 3.2, stationCenter.getZ() + 0.5, "§b§l[Computador Quântico SandStorm]");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 2.85, stationCenter.getZ() + 0.5, "§aOnline | 100.000 J | Rede WPT Ativa");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 2.5, stationCenter.getZ() + 0.5, "§e8/8 Discos Carregados | Clique no Terminal!");

        setupReturnPad(level, stationCenter.offset(-6, 1, -4));
    }

    private static void buildQuantumComputerWalkway(ServerLevel level, BlockPos center) {
        for (int x = 11; x <= 42; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos p = center.offset(x, 0, z);
                boolean edge = Math.abs(z) == 2;
                boolean light = (x % 4 == 0 && z == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
    }

    public static void buildCarbonAndInductionStation(ServerLevel level, BlockPos stationCenter) {
        int baseY = stationCenter.getY();

        for (int dx = -12; dx <= 12; dx++) {
            for (int dz = -12; dz <= 12; dz++) {
                for (int dy = 1; dy <= 7; dy++) {
                    BlockPos p = stationCenter.offset(dx, dy, dz);
                    if (!level.getBlockState(p).isAir()) {
                        setAirSafe(level, p);
                    }
                }
            }
        }

        for (int dx = -12; dx <= 12; dx++) {
            for (int dz = -12; dz <= 12; dz++) {
                BlockPos p = stationCenter.offset(dx, 0, dz);
                boolean edge = Math.abs(dx) == 12 || Math.abs(dz) == 12;
                boolean corner = Math.abs(dx) == 12 && Math.abs(dz) == 12;
                Block b = corner ? Blocks.OCHRE_FROGLIGHT : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 3);
                boolean isDoor = (Math.abs(dx) == 12 && Math.abs(dz) <= 2);
                if (edge && !corner && !isDoor) {
                    level.setBlock(p.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 3);
                }
            }
        }

        BlockPos eafPos = stationCenter.offset(-7, 1, -6);
        level.setBlock(eafPos, SandStormBlocks.ELECTRIC_ARC_FURNACE.defaultBlockState().setValue(ElectricArcFurnaceBlock.FACING, Direction.SOUTH).setValue(ElectricArcFurnaceBlock.LIT, true), 3);
        if (level.getBlockEntity(eafPos) instanceof ElectricArcFurnaceBlockEntity eafBe) {
            eafBe.setEnergy(10000);
            eafBe.setItem(0, new ItemStack(SandStormItems.RAW_SILICON, 32));
            eafBe.setItem(1, new ItemStack(SandStormItems.SILICON_WAFER, 8));
            eafBe.setChanged();
        }

        BlockPos eafAccPos = stationCenter.offset(-8, 1, -6);
        level.setBlock(eafAccPos, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
        if (level.getBlockEntity(eafAccPos) instanceof SolidStateAccumulatorBlockEntity accBe) {
            accBe.setStoredEnergy(500000);
        }
        BlockPos eafSolarPos = stationCenter.offset(-8, 2, -6);
        level.setBlock(eafSolarPos, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);

        spawnHologram(level, eafPos.getX() + 0.5, baseY + 3.2, eafPos.getZ() + 0.5, "§6§l[Fornalha a Arco Elétrico]");
        spawnHologram(level, eafPos.getX() + 0.5, baseY + 2.85, eafPos.getZ() + 0.5, "§eIndução Térmica | 40 J/t | Rede WPT");
        spawnHologram(level, eafPos.getX() + 0.5, baseY + 2.5, eafPos.getZ() + 0.5, "§aFundição de Silício Puro & Wafers");

        BlockPos carbCoalPos = stationCenter.offset(-7, 1, 6);
        level.setBlock(carbCoalPos, SandStormBlocks.ATMOSPHERIC_CARBON_EXTRACTOR.defaultBlockState().setValue(AtmosphericCarbonExtractorBlock.FACING, Direction.NORTH).setValue(AtmosphericCarbonExtractorBlock.LIT, true), 3);
        if (level.getBlockEntity(carbCoalPos) instanceof AtmosphericCarbonExtractorBlockEntity carbBe) {
            carbBe.setEnergy(10000);
            carbBe.setItem(1, new ItemStack(Items.COAL, 16));
            carbBe.setChanged();
        }

        BlockPos carbAccPos = stationCenter.offset(-8, 1, 6);
        level.setBlock(carbAccPos, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
        if (level.getBlockEntity(carbAccPos) instanceof SolidStateAccumulatorBlockEntity accBe) {
            accBe.setStoredEnergy(500000);
        }
        BlockPos carbSolarPos = stationCenter.offset(-8, 2, 6);
        level.setBlock(carbSolarPos, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);

        spawnHologram(level, carbCoalPos.getX() + 0.5, baseY + 3.2, carbCoalPos.getZ() + 0.5, "§b§l[Captura de Carbono Atmosférico]");
        spawnHologram(level, carbCoalPos.getX() + 0.5, baseY + 2.85, carbCoalPos.getZ() + 0.5, "§7Modo Carvão Sólido | 20 J/t");
        spawnHologram(level, carbCoalPos.getX() + 0.5, baseY + 2.5, carbCoalPos.getZ() + 0.5, "§aExtrai CO₂ Livre da Atmosfera");

        BlockPos cvdPos = stationCenter.offset(7, 1, 6);
        level.setBlock(cvdPos, SandStormBlocks.ATMOSPHERIC_CARBON_EXTRACTOR.defaultBlockState().setValue(AtmosphericCarbonExtractorBlock.FACING, Direction.NORTH).setValue(AtmosphericCarbonExtractorBlock.LIT, true), 3);
        if (level.getBlockEntity(cvdPos) instanceof AtmosphericCarbonExtractorBlockEntity cvdBe) {
            cvdBe.setEnergy(10000);
            cvdBe.setItem(0, new ItemStack(Items.DIAMOND, 1));
            cvdBe.setItem(1, new ItemStack(Items.DIAMOND, 4));
            cvdBe.setChanged();
        }

        BlockPos cvdAccPos = stationCenter.offset(8, 1, 6);
        level.setBlock(cvdAccPos, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
        if (level.getBlockEntity(cvdAccPos) instanceof SolidStateAccumulatorBlockEntity accBe) {
            accBe.setStoredEnergy(500000);
        }
        BlockPos cvdSolarPos = stationCenter.offset(8, 2, 6);
        level.setBlock(cvdSolarPos, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);

        spawnHologram(level, cvdPos.getX() + 0.5, baseY + 3.2, cvdPos.getZ() + 0.5, "§d§l[Síntese CVD de Diamantes]");
        spawnHologram(level, cvdPos.getX() + 0.5, baseY + 2.85, cvdPos.getZ() + 0.5, "§bFeixe de Plasma | Semente Natural");
        spawnHologram(level, cvdPos.getX() + 0.5, baseY + 2.5, cvdPos.getZ() + 0.5, "§eCrescimento Cristalográfico Carbonífero");

        BlockPos boostPos = stationCenter.offset(7, 1, -6);
        level.setBlock(stationCenter.offset(8, 0, -6), Blocks.NETHERRACK.defaultBlockState(), 3);
        level.setBlock(stationCenter.offset(8, 1, -6), Blocks.FIRE.defaultBlockState(), 3);
        level.setBlock(boostPos, SandStormBlocks.ATMOSPHERIC_CARBON_EXTRACTOR.defaultBlockState().setValue(AtmosphericCarbonExtractorBlock.FACING, Direction.SOUTH).setValue(AtmosphericCarbonExtractorBlock.LIT, true), 3);
        if (level.getBlockEntity(boostPos) instanceof AtmosphericCarbonExtractorBlockEntity boostBe) {
            boostBe.setEnergy(10000);
            boostBe.setItem(1, new ItemStack(Items.COAL, 32));
            boostBe.setChanged();
        }

        BlockPos boostAccPos = stationCenter.offset(7, 1, -7);
        level.setBlock(boostAccPos, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
        if (level.getBlockEntity(boostAccPos) instanceof SolidStateAccumulatorBlockEntity accBe) {
            accBe.setStoredEnergy(500000);
        }
        BlockPos boostSolarPos = stationCenter.offset(7, 2, -7);
        level.setBlock(boostSolarPos, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);

        spawnHologram(level, boostPos.getX() + 0.5, baseY + 3.2, boostPos.getZ() + 0.5, "§c§l[Arrasto Térmico de Combustão]");
        spawnHologram(level, boostPos.getX() + 0.5, baseY + 2.85, boostPos.getZ() + 0.5, "§6Fogo Adjacente: Velocidade +200%");
        spawnHologram(level, boostPos.getX() + 0.5, baseY + 2.5, boostPos.getZ() + 0.5, "§aConvecção de CO₂ Hiper-Eficiente");

        BlockPos furnacePos = stationCenter.offset(0, 1, -8);
        level.setBlock(furnacePos, SandStormBlocks.SANDSTONE_FURNACE.defaultBlockState(), 3);
        if (level.getBlockEntity(furnacePos) instanceof SandstoneFurnaceBlockEntity furnaceBe) {
            furnaceBe.setItem(0, new ItemStack(Blocks.SAND, 32));
            furnaceBe.setItem(1, new ItemStack(Items.COAL, 16));
        }
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 3.0, stationCenter.getZ() - 8.0, "§e§l[Fornalha Solar de Arenito]");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 2.65, stationCenter.getZ() - 8.0, "§7Fornalha Primária do Deserto");

        BlockPos cratePos = stationCenter.offset(0, 1, 8);
        level.setBlock(cratePos, SandStormBlocks.SANDSTONE_CRATE.defaultBlockState(), 3);
        if (level.getBlockEntity(cratePos) instanceof SandstoneCrateBlockEntity crateBe) {
            crateBe.setItem(0, new ItemStack(SandStormItems.RAW_SILICON, 64));
            crateBe.setItem(1, new ItemStack(SandStormItems.SILICON_WAFER, 64));
            crateBe.setItem(2, new ItemStack(Items.COAL, 64));
            crateBe.setItem(3, new ItemStack(Items.DIAMOND, 16));
            crateBe.setItem(4, new ItemStack(SandStormItems.SYNAPTIC_CARTRIDGE, 4));
            crateBe.setItem(5, new ItemStack(SandStormItems.SIGNAL_TRACKER, 1));
            crateBe.setItem(6, new ItemStack(SandStormItems.MOLECULAR_CARGO_CAPSULE, 4));
        }
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 3.0, stationCenter.getZ() + 8.0, "§6§l[Caixote de Arenito 3D]");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 2.65, stationCenter.getZ() + 8.0, "§7Armazenamento com Tampa 3D Animada");

        BlockPos chestPos = stationCenter.offset(-2, 1, 0);
        setChestSafe(level, chestPos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormBlocks.ATMOSPHERIC_CARBON_EXTRACTOR, 4));
            chest.setItem(1, new ItemStack(SandStormBlocks.ELECTRIC_ARC_FURNACE, 4));
            chest.setItem(2, new ItemStack(SandStormBlocks.SANDSTONE_CRATE, 8));
            chest.setItem(3, new ItemStack(SandStormBlocks.SANDSTONE_FURNACE, 4));
            chest.setItem(4, new ItemStack(SandStormItems.SYNAPTIC_CARTRIDGE, 4));
            chest.setItem(5, new ItemStack(SandStormItems.SIGNAL_TRACKER, 1));
            chest.setItem(6, new ItemStack(SandStormItems.MOLECULAR_CARGO_CAPSULE, 4));
            chest.setItem(7, new ItemStack(SandStormItems.RAW_SILICON, 64));
            chest.setItem(8, new ItemStack(SandStormItems.SILICON_WAFER, 64));
            chest.setItem(9, new ItemStack(Items.COAL, 64));
            chest.setItem(10, new ItemStack(Items.DIAMOND, 16));
            chest.setItem(11, new ItemStack(SandStormBlocks.SOLID_STATE_ACCUMULATOR, 4));
            chest.setItem(12, new ItemStack(SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2, 4));
            chest.setItem(13, new ItemStack(SandStormBlocks.WPT_RELAY_TOWER, 8));
            chest.setItem(14, new ItemStack(Items.NETHERRACK, 16));
            chest.setItem(15, new ItemStack(Items.FLINT_AND_STEEL, 1));
        }
        spawnHologram(level, stationCenter.getX() - 2.0, baseY + 2.5, stationCenter.getZ() + 0.5, "§6[Baú de Fornos, Carbono & Síntese]");

        setupReturnPad(level, stationCenter.offset(11, 1, -3));
    }

    private static void buildCarbonAndInductionWalkway(ServerLevel level, BlockPos center) {
        for (int x = -11; x >= -37; x--) {
            for (int z = -2; z <= 2; z++) {
                BlockPos p = center.offset(x, 0, z);
                boolean edge = Math.abs(z) == 2;
                boolean light = (Math.abs(x) % 4 == 0 && z == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
    }

    public static void buildVaultShowcase(ServerLevel level, BlockPos stationCenter) {
        int baseY = stationCenter.getY();

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 5; dz++) {
                for (int dy = 1; dy <= 5; dy++) {
                    BlockPos p = stationCenter.offset(dx, dy, dz);
                    if (!level.getBlockState(p).isAir()) {
                        setAirSafe(level, p);
                    }
                }
            }
        }

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 5; dz++) {
                level.setBlock(stationCenter.offset(dx, -2, dz), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
                Block subBlock = (Math.abs(dx) == 4 || dz == -4 || dz == 5)
                        ? SandStormBlocks.VAULT_WALL_SEAM
                        : SandStormBlocks.REINFORCED_CONCRETE;
                level.setBlock(stationCenter.offset(dx, -1, dz), subBlock.defaultBlockState(), 2);
            }
        }

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 5; dz++) {
                BlockPos p = stationCenter.offset(dx, 0, dz);
                boolean edge = Math.abs(dx) == 4 || dz == -4 || dz == 5;
                boolean corner = (Math.abs(dx) == 4 && (dz == -4 || dz == 5));
                Block b;
                if (corner) {
                    b = Blocks.OCHRE_FROGLIGHT;
                } else if (edge) {
                    b = SandStormBlocks.VAULT_WALL_SEAM;
                } else if (dz == 4) {
                    b = Math.abs(dx) <= 1 ? Blocks.POLISHED_DEEPSLATE : SandStormBlocks.VAULT_WALL_SEAM;
                } else if (dx == 0 && dz == 0) {
                    b = Blocks.SEA_LANTERN;
                } else if (Math.abs(dx) <= 1 && Math.abs(dz) <= 1) {
                    b = Blocks.POLISHED_DEEPSLATE;
                } else {
                    b = ((dx + dz) % 2 == 0) ? Blocks.POLISHED_DEEPSLATE : SandStormBlocks.REINFORCED_CONCRETE;
                }
                level.setBlock(p, b.defaultBlockState(), 3);
            }
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx != 0 || dz != 0) {
                    BlockPos pavingPos = stationCenter.offset(dx, 1, dz);
                    level.setBlock(pavingPos, SandStormBlocks.SEISMIC_DAMPENER_PAVING.defaultBlockState(), 3);
                }
            }
        }

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos p = stationCenter.offset(dx, 4, dz);
                boolean edge = Math.abs(dx) == 4 || Math.abs(dz) == 4;
                boolean centerLight = Math.abs(dx) <= 1 && Math.abs(dz) <= 1;
                Block b = centerLight ? Blocks.SEA_LANTERN : (edge ? SandStormBlocks.VAULT_WALL_SEAM : SandStormBlocks.WEATHERED_REINFORCED_CONCRETE);
                level.setBlock(p, b.defaultBlockState(), 3);
            }
        }

        for (int dy = 1; dy <= 3; dy++) {
            VerticalConnection conn = (dy == 1) ? VerticalConnection.BOTTOM : ((dy == 3) ? VerticalConnection.TOP : VerticalConnection.MIDDLE);

            level.setBlock(stationCenter.offset(-4, dy, -4), SandStormBlocks.VAULT_WALL_SEAM.defaultBlockState(), 3);
            level.setBlock(stationCenter.offset(4, dy, -4), SandStormBlocks.VAULT_WALL_SEAM.defaultBlockState(), 3);
            for (int dx = -3; dx <= 3; dx++) {
                level.setBlock(stationCenter.offset(dx, dy, -4), SandStormBlocks.VAULT_WALL_PANEL.defaultBlockState().setValue(VaultWallPanelBlock.VERTICAL, conn), 3);
            }

            for (int dz = -3; dz <= 3; dz++) {
                level.setBlock(stationCenter.offset(-4, dy, dz), SandStormBlocks.VAULT_WALL_PANEL.defaultBlockState().setValue(VaultWallPanelBlock.VERTICAL, conn), 3);
                level.setBlock(stationCenter.offset(4, dy, dz), SandStormBlocks.VAULT_WALL_PANEL.defaultBlockState().setValue(VaultWallPanelBlock.VERTICAL, conn), 3);
            }

            level.setBlock(stationCenter.offset(-4, dy, 4), SandStormBlocks.VAULT_WALL_SEAM.defaultBlockState(), 3);
            level.setBlock(stationCenter.offset(4, dy, 4), SandStormBlocks.VAULT_WALL_SEAM.defaultBlockState(), 3);
            level.setBlock(stationCenter.offset(-3, dy, 4), SandStormBlocks.VAULT_WALL_PANEL.defaultBlockState().setValue(VaultWallPanelBlock.VERTICAL, conn), 3);
            level.setBlock(stationCenter.offset(3, dy, 4), SandStormBlocks.VAULT_WALL_PANEL.defaultBlockState().setValue(VaultWallPanelBlock.VERTICAL, conn), 3);
            level.setBlock(stationCenter.offset(-2, dy, 4), SandStormBlocks.VAULT_WALL_SEAM.defaultBlockState(), 3);
            level.setBlock(stationCenter.offset(2, dy, 4), SandStormBlocks.VAULT_WALL_SEAM.defaultBlockState(), 3);
        }

        BlockPos doorControllerPos = stationCenter.offset(0, 1, 4);
        Direction doorFacing = Direction.SOUTH;
        level.setBlock(doorControllerPos, SandStormBlocks.VAULT_DOOR_CONTROLLER.defaultBlockState().setValue(VaultDoorControllerBlock.FACING, doorFacing), 3);
        for (int col = 0; col <= 2; col++) {
            for (int row = 0; row <= 2; row++) {
                if (col == 1 && row == 0) {
                    continue;
                }
                BlockPos dummyPos = VaultDoorControllerBlock.getRelativePos(doorControllerPos, doorFacing, col, row);
                BlockState dummyState = SandStormBlocks.VAULT_DOOR_DUMMY.defaultBlockState()
                        .setValue(VaultDoorDummyBlock.FACING, doorFacing)
                        .setValue(VaultDoorDummyBlock.COL, col)
                        .setValue(VaultDoorDummyBlock.ROW, row);
                level.setBlock(dummyPos, dummyState, 3);
            }
        }

        BlockPos leverWallPos = stationCenter.offset(2, 2, 5);
        level.setBlock(stationCenter.offset(2, 1, 5), Blocks.AIR.defaultBlockState(), 3);
        BlockState leverState = Blocks.LEVER.defaultBlockState()
                .setValue(LeverBlock.FACE, AttachFace.WALL)
                .setValue(LeverBlock.FACING, Direction.SOUTH);
        level.setBlock(leverWallPos, leverState, 3);

        BlockPos chestPos = stationCenter.offset(0, 1, 0);
        setChestSafe(level, chestPos, Blocks.CHEST.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 32));
            chest.setItem(1, new ItemStack(SandStormItems.MANTLE_ALLOY_INGOT, 16));
            chest.setItem(2, new ItemStack(SandStormItems.QUANTUM_PROCESSOR, 8));
            chest.setItem(3, new ItemStack(SandStormItems.BIO_NEURAL_CORE, 4));
            chest.setItem(4, new ItemStack(SandStormItems.SUPERCONDUCTOR_TOROID, 16));
            chest.setItem(5, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_DIMENSIONAL, 2));
            chest.setItem(6, new ItemStack(SandStormBlocks.ANCIENT_DATA_CORE, 1));
            chest.setItem(7, new ItemStack(SandStormItems.VIBRO_CRYSKNIFE, 1));
            chest.setItem(8, new ItemStack(SandStormItems.HEAVY_PLASMA_CANNON, 1));
        }

        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 6.2, stationCenter.getZ() + 4.5, "§6§l[Câmara de Cofre Blindada SandStorm]");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 5.85, stationCenter.getZ() + 4.5, "§aPorta Mecânica 3x3 Multi-Bloco com Volante");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 5.5, stationCenter.getZ() + 4.5, "§eClique na Porta ou na Alavanca para Abrir/Fechar");

        setupVaultReturnPad(level, stationCenter.offset(-3, 1, -3));
    }

    private static void setupVaultReturnPad(ServerLevel level, BlockPos pos) {
        level.setBlock(pos.below(), SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState(), 3);
        level.setBlock(pos, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.defaultBlockState(), 3);
        spawnHologram(level, pos.getX() + 0.5, pos.getY() + 1.8, pos.getZ() + 0.5, "§a§l▲ RETORNAR AO SHOWCASE ▲");
        spawnHologram(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, "§7Pise para voltar à Praça Central");
        TELEPORT_PADS.add(new ShowcaseTeleportPad(pos, new BlockPos(0, 161, 0), "Praça Central do Showcase"));
    }

    private static void buildVaultWalkway(ServerLevel level, BlockPos center) {
        for (int z = -3; z >= -39; z--) {
            for (int dx = -2; dx <= 2; dx++) {
                BlockPos p = center.offset(50 + dx, 0, z);
                level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = Math.abs(dx) == 2;
                boolean light = (Math.abs(z) % 4 == 0 && dx == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
    }

    public static void buildSubspaceGatewayStation(ServerLevel level, BlockPos stationCenter) {
        int baseY = stationCenter.getY();

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                for (int dy = 1; dy <= 5; dy++) {
                    BlockPos p = stationCenter.offset(dx, dy, dz);
                    if (!level.getBlockState(p).isAir()) {
                        setAirSafe(level, p);
                    }
                }
            }
        }

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos p = stationCenter.offset(dx, 0, dz);
                boolean edge = Math.abs(dx) == 4 || Math.abs(dz) == 4;
                boolean corner = Math.abs(dx) == 4 && Math.abs(dz) == 4;
                Block b = corner ? Blocks.OCHRE_FROGLIGHT : (edge ? Blocks.CUT_SANDSTONE : SandStormBlocks.MORPHING_ALLOY_BLOCK);
                level.setBlock(p, b.defaultBlockState(), 3);
                if (edge && !corner) {
                    level.setBlock(p.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 3);
                }
            }
        }

        BlockPos gatewayPos = stationCenter.offset(0, 1, 0);
        level.setBlock(gatewayPos, SandStormBlocks.SUBSPACE_GATEWAY.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
        if (level.getBlockEntity(gatewayPos) instanceof SubspaceGatewayBlockEntity gatewayBe) {
            gatewayBe.setEnergy(SubspaceGatewayBlockEntity.MAX_ENERGY);
        }

        BlockPos[] coils = {
                stationCenter.offset(-2, 1, 0),
                stationCenter.offset(2, 1, 0),
                stationCenter.offset(0, 1, -2),
                stationCenter.offset(0, 1, 2)
        };
        for (BlockPos coilPos : coils) {
            level.setBlock(coilPos, SandStormBlocks.PIEZO_QUARTZ_BLOCK.defaultBlockState(), 3);
            level.setBlock(coilPos.above(), Blocks.SEA_LANTERN.defaultBlockState(), 3);
        }

        BlockPos accumulatorPos = stationCenter.offset(-2, 1, -2);
        level.setBlock(accumulatorPos, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
        if (level.getBlockEntity(accumulatorPos) instanceof SolidStateAccumulatorBlockEntity accBe) {
            accBe.setStoredEnergy(500000);
        }

        BlockPos solarPos = stationCenter.offset(-2, 1, -3);
        level.setBlock(solarPos, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);

        BlockPos cable1 = stationCenter.offset(-1, 1, -2);
        BlockPos cable2 = stationCenter.offset(-1, 1, -1);
        level.setBlock(cable1, SandStormBlocks.QUANTUM_NETWORK_CABLE.defaultBlockState(), 3);
        level.setBlock(cable2, SandStormBlocks.QUANTUM_NETWORK_CABLE.defaultBlockState(), 3);

        BlockPos chestPos = stationCenter.offset(2, 1, 0);
        setChestSafe(level, chestPos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.SUBSPACE_WARP_CORE, 16));
            chest.setItem(1, new ItemStack(SandStormItems.SUPERCONDUCTOR_TOROID, 8));
            chest.setItem(2, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 16));
            chest.setItem(3, new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_DIMENSIONAL, 2));
            chest.setItem(4, new ItemStack(SandStormItems.SURVIVAL_DATAPAD, 1));
        }

        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 4.2, stationCenter.getZ() + 0.5, "§5§l[Estação de Trânsito Subespacial]");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 3.85, stationCenter.getZ() + 0.5, "§aOnline | 100.000 J | Portal de Dobra Ativo");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 3.5, stationCenter.getZ() + 0.5, "§ePise no portal para transitar entre dimensões!");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 3.15, stationCenter.getZ() + 0.5, "§7Alimente com Subspace Warp Core para recarga");

        setupReturnPad(level, stationCenter.offset(-3, 1, 3));
    }

    private static void buildSubspaceGatewayWalkway(ServerLevel level, BlockPos center) {
        for (int x = 55; x <= 75; x++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = center.offset(x, 0, -45 + dz);
                level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = Math.abs(dz) == 2;
                boolean light = (x % 4 == 0 && dz == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
    }

    public static void buildRappelTestTowerAndChasm(ServerLevel level, BlockPos stationCenter) {
        int baseY = stationCenter.getY();

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                for (int dy = 1; dy <= 6; dy++) {
                    BlockPos p = stationCenter.offset(dx, dy, dz);
                    if (!level.getBlockState(p).isAir()) {
                        setAirSafe(level, p);
                    }
                }
            }
        }

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                BlockPos p = stationCenter.offset(dx, 0, dz);
                boolean isDropHole = (dx >= -2 && dx <= 2 && dz >= -2 && dz <= 2);
                if (isDropHole) {
                    level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    continue;
                }
                boolean edge = Math.abs(dx) == 7 || Math.abs(dz) == 7;
                boolean corner = Math.abs(dx) == 7 && Math.abs(dz) == 7;
                boolean isEntrance = (dx <= -5 && Math.abs(dz) <= 1);
                Block b = corner ? Blocks.OCHRE_FROGLIGHT : (edge ? Blocks.CUT_SANDSTONE : SandStormBlocks.REINFORCED_CONCRETE);
                level.setBlock(p, b.defaultBlockState(), 3);
                if (edge && !corner && !isEntrance) {
                    level.setBlock(p.above(), Blocks.IRON_BARS.defaultBlockState(), 3);
                }
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            level.setBlock(stationCenter.offset(dx, 1, -3), SandStormBlocks.REINFORCED_CONCRETE.defaultBlockState(), 3);
        }

        BlockPos anchorPos1 = stationCenter.offset(-1, 1, -2);
        level.setBlock(anchorPos1, SandStormBlocks.RAPPEL_ANCHOR.defaultBlockState().setValue(RappelAnchorBlock.FACING, Direction.SOUTH), 3);

        BlockPos anchorPos2 = stationCenter.offset(1, 1, -2);
        level.setBlock(anchorPos2, SandStormBlocks.GLOWING_RAPPEL_ANCHOR.defaultBlockState().setValue(RappelAnchorBlock.FACING, Direction.SOUTH), 3);

        int maxDepth = RappelAnchorBlockEntity.MAX_DEPTH;
        for (int y = 1; y <= maxDepth; y++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos airPos = stationCenter.offset(dx, 1 - y, dz);
                    if (y < maxDepth || (dx != -1 && dx != 1) || dz != -2) {
                        level.setBlock(airPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
            BlockPos rPos1 = anchorPos1.below(y);
            level.setBlock(rPos1, SandStormBlocks.RAPPEL_ROPE.defaultBlockState(), 3);
            BlockPos rPos2 = anchorPos2.below(y);
            level.setBlock(rPos2, SandStormBlocks.GLOWING_RAPPEL_ROPE.defaultBlockState(), 3);
        }

        if (level.getBlockEntity(anchorPos1) instanceof RappelAnchorBlockEntity be1) {
            be1.setupShowcaseDeployed(maxDepth);
        }
        if (level.getBlockEntity(anchorPos2) instanceof RappelAnchorBlockEntity be2) {
            be2.setupShowcaseDeployed(maxDepth);
        }

        int bottomY = baseY - maxDepth;
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                BlockPos floorPos = new BlockPos(stationCenter.getX() + dx, bottomY, stationCenter.getZ() + dz);
                boolean isUnderRope = (dx == -1 || dx == 1) && dz == -2;
                Block b = isUnderRope ? SandStormBlocks.SEISMIC_DAMPENER_PAVING : Blocks.POLISHED_DEEPSLATE;
                if (Math.abs(dx) == 5 || Math.abs(dz) == 5) {
                    b = Blocks.OCHRE_FROGLIGHT;
                }
                level.setBlock(floorPos, b.defaultBlockState(), 3);
            }
        }

        BlockPos bottomChestPos = new BlockPos(stationCenter.getX() - 3, bottomY + 1, stationCenter.getZ());
        setChestSafe(level, bottomChestPos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(bottomChestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.RAPPEL_WINCH, 4));
            chest.setItem(1, new ItemStack(SandStormItems.GLOWING_RAPPEL_WINCH, 4));
            chest.setItem(2, new ItemStack(SandStormItems.SPACE_SUIT_BOOTS, 1));
            chest.setItem(3, new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE, 16));
        }

        BlockPos bottomReturnPos = new BlockPos(stationCenter.getX() + 3, bottomY + 1, stationCenter.getZ());
        setupReturnPad(level, bottomReturnPos);

        spawnHologram(level, stationCenter.getX() + 0.0, bottomY + 3.0, stationCenter.getZ() + 1.0, "§a§l[Fundo do Abismo - 128 Blocos]");
        spawnHologram(level, stationCenter.getX() + 0.0, bottomY + 2.7, stationCenter.getZ() + 1.0, "§7Escale com a corda ou pise na placa para subir");

        BlockPos topChestPos = stationCenter.offset(5, 1, -5);
        setChestSafe(level, topChestPos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(topChestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.RAPPEL_WINCH, 8));
            chest.setItem(1, new ItemStack(SandStormItems.GLOWING_RAPPEL_WINCH, 8));
            chest.setItem(2, new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE, 1));
            chest.setItem(3, new ItemStack(SandStormItems.SPACE_SUIT_BOOTS, 1));
            chest.setItem(4, new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE, 32));
        }

        spawnHologram(level, stationCenter.getX() + 0.0, baseY + 4.2, stationCenter.getZ() - 4.0, "§6§l[Zona de Testes: Rapel (128 Blocos)]");
        spawnHologram(level, stationCenter.getX() + 0.0, baseY + 3.85, stationCenter.getZ() - 4.0, "§bComprimento Máximo: 128 Blocos Verticais");
        spawnHologram(level, stationCenter.getX() + 0.0, baseY + 3.5, stationCenter.getZ() - 4.0, "§eClique com botão direito na corda ou no guincho");
        spawnHologram(level, stationCenter.getX() + 0.0, baseY + 3.15, stationCenter.getZ() - 4.0, "§f[W/Espaço] Subir | [S] Descer | [Ctrl] Acelerar | [Shift] Descer no Chão");
        spawnHologram(level, stationCenter.getX() + 0.0, baseY + 2.8, stationCenter.getZ() - 4.0, "§a[Anti-Queda] Pule no abismo e seja capturado automaticamente!");

        setupReturnPad(level, stationCenter.offset(5, 1, 5));
    }

    private static void buildRappelWalkway(ServerLevel level, BlockPos center) {
        for (int x = 3; x <= 37; x++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = center.offset(x, 0, -50 + dz);
                level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = Math.abs(dz) == 2;
                boolean light = (x % 4 == 0 && dz == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
    }

    private static void buildIsolatedTestingChamber(ServerLevel level, int minX, int maxX, int minZ, int maxZ, int baseY, int windowX, String title, String subtitle) {
        int centerX = (minX + maxX) / 2;
        int centerZ = (minZ + maxZ) / 2;

        BlockPos reactorPos = new BlockPos(centerX, baseY - 1, centerZ);
        level.setBlock(reactorPos, SandStormBlocks.CRYO_REACTOR_CORE.defaultBlockState().setValue(CryoReactorCoreBlock.OPERATIONAL, true), 3);
        level.setBlock(reactorPos.east(), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
        SolidStateAccumulatorManager.registerAccumulator(level.dimension(), reactorPos.east(), 500000, 500000, true, 1);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockPos floor = new BlockPos(x, baseY, z);
                level.setBlock(floor.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean isEdge = (x == minX || x == maxX || z == minZ || z == maxZ);
                Block floorBlock = isEdge ? Blocks.CUT_SANDSTONE : SandStormBlocks.SEISMIC_DAMPENER_PAVING;
                level.setBlock(floor, floorBlock.defaultBlockState(), 2);

                if (isEdge) {
                    boolean isWindowWall = (x == windowX);
                    boolean isDoorway = (z == centerZ);
                    for (int dy = 1; dy <= 3; dy++) {
                        BlockPos wallPos = floor.above(dy);
                        if (isWindowWall) {
                            if (isDoorway) {
                                setAirSafe(level, wallPos);
                            } else if (dy == 1) {
                                level.setBlock(wallPos, Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
                            } else {
                                level.setBlock(wallPos, SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 3);
                            }
                        } else {
                            Block wallBlock = (dy == 2 && (Math.abs(z - centerZ) % 3 == 0 || Math.abs(x - centerX) % 3 == 0))
                                    ? SandStormBlocks.FULGURITE_GLASS
                                    : SandStormBlocks.REINFORCED_CONCRETE;
                            level.setBlock(wallPos, wallBlock.defaultBlockState(), 3);
                        }
                    }
                    level.setBlock(floor.above(4), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
                }
            }
        }

        spawnHologram(level, windowX + 0.5, baseY + 4.5, centerZ + 0.5, title);
        spawnHologram(level, windowX + 0.5, baseY + 4.15, centerZ + 0.5, subtitle);
    }

    public static void buildSandwormColosseum(ServerLevel level, BlockPos center) {
        int baseY = center.getY();
        BlockPos colosseumCenter = center.offset(0, 0, -170);

        AABB clearBox = new AABB(colosseumCenter.getX() - 26, baseY - 10, colosseumCenter.getZ() - 26,
                colosseumCenter.getX() + 26, baseY + 25, colosseumCenter.getZ() + 26);
        List<Entity> old = level.getEntitiesOfClass(Entity.class, clearBox, e -> !(e instanceof ServerPlayer));
        for (Entity e : old) {
            e.discard();
        }

        for (int z = -115; z >= -146; z--) {
            for (int dx = -2; dx <= 2; dx++) {
                BlockPos p = center.offset(dx, 0, z);
                boolean edge = Math.abs(dx) == 2;
                boolean light = (Math.abs(z) % 6 == 0 && dx == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
                if (edge) {
                    level.setBlock(p.above(), Blocks.SANDSTONE_WALL.defaultBlockState(), 2);
                    level.setBlock(p.above(2), Blocks.IRON_BARS.defaultBlockState(), 2);
                }
            }
        }

        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -146; dz >= -152; dz--) {
                BlockPos p = center.offset(dx, 0, dz);
                boolean edge = (Math.abs(dx) == 8 || dz == -146 || dz == -152);
                boolean corner = (Math.abs(dx) == 8 && (dz == -146 || dz == -152));
                Block b = corner ? Blocks.OCHRE_FROGLIGHT : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                level.setBlock(p, b.defaultBlockState(), 2);
                if (edge && !(dx == 0 && dz == -146)) {
                    level.setBlock(p.above(), Blocks.SANDSTONE_WALL.defaultBlockState(), 2);
                    level.setBlock(p.above(2), Blocks.IRON_BARS.defaultBlockState(), 2);
                }
            }
        }

        spawnHologram(level, colosseumCenter.getX() + 0.5, baseY + 4.2, center.getZ() - 149.5, "§4§l[Setor Titã: Arena Sísmica do Sandworm]");
        spawnHologram(level, colosseumCenter.getX() + 0.5, baseY + 3.85, center.getZ() - 149.5, "§7Predador Ápice do Deserto (Sandworm) | Categoria Titã");

        BlockPos thumperPos = center.offset(3, 1, -149);
        level.setBlock(thumperPos, SandStormBlocks.SEISMIC_DECOY_TRAP.defaultBlockState(), 3);
        BlockPos baitChestPos = center.offset(-3, 1, -149);
        setChestSafe(level, baitChestPos, Blocks.CHEST.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
        if (level.getBlockEntity(baitChestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.SANDWORM_TOOTH, 64));
            chest.setItem(1, new ItemStack(SandStormItems.SANDWORM_CHITIN, 64));
            chest.setItem(2, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 64));
            chest.setItem(3, new ItemStack(SandStormItems.VIBRO_CRYSKNIFE, 1));
            chest.setItem(4, new ItemStack(SandStormBlocks.SEISMIC_DECOY_TRAP, 16));
        }

        int arenaRadius = 18;
        for (int dx = -arenaRadius; dx <= arenaRadius; dx++) {
            for (int dz = -arenaRadius; dz <= arenaRadius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > arenaRadius) {
                    continue;
                }
                BlockPos surfacePos = colosseumCenter.offset(dx, 0, dz);
                boolean rim = dist >= arenaRadius - 1.5;
                if (rim) {
                    level.setBlock(surfacePos, Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
                    level.setBlock(surfacePos.above(), Blocks.SANDSTONE_WALL.defaultBlockState(), 2);
                    level.setBlock(surfacePos.above(2), Blocks.IRON_BARS.defaultBlockState(), 2);
                    if (Math.abs(dx) % 5 == 0 || Math.abs(dz) % 5 == 0) {
                        level.setBlock(surfacePos.above(3), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 3);
                    }
                } else {
                    int depth = (dist < 8) ? 3 : 2;
                    for (int dy = 0; dy > -depth; dy--) {
                        setAirSafe(level, surfacePos.offset(0, dy, 0));
                    }
                    BlockPos floorPos = surfacePos.offset(0, -depth, 0);
                    level.setBlock(floorPos.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 2);

                    Block sandBlock = ((Math.abs(dx * 7 + dz * 13) % 5) == 0) ? Blocks.RED_SAND : Blocks.SAND;
                    if ((Math.abs(dx) + Math.abs(dz)) % 11 == 0) {
                        sandBlock = Blocks.SUSPICIOUS_SAND;
                    }
                    level.setBlock(floorPos, sandBlock.defaultBlockState(), 2);

                    if (dist > 9 && (dx == dz || dx == -dz)) {
                        level.setBlock(floorPos.above(), Blocks.BONE_BLOCK.defaultBlockState(), 2);
                    }
                }
            }
        }

        SandwormEntity worm = new SandwormEntity(SandStormEntities.SANDWORM, level);
        worm.setPos(colosseumCenter.getX() + 0.5, baseY - 2.0, colosseumCenter.getZ() + 0.5);
        worm.setShowcaseMode(true);
        worm.setWormSize(2, true);
        worm.setNoGravity(true);
        worm.setPersistenceRequired();
        level.addFreshEntity(worm);

        spawnHologram(level, colosseumCenter.getX() + 0.5, baseY + 6.0, colosseumCenter.getZ() + 0.5, "§4§l[Verme de Areia Adulto - Sandworm]");
        spawnHologram(level, colosseumCenter.getX() + 0.5, baseY + 5.65, colosseumCenter.getZ() + 0.5, "§6Tamanho: Titânico (Grau 2) | Ninho de Dunas Ativo");
    }

    public static void buildDefenseAndBallisticsRange(ServerLevel level, BlockPos stationCenter) {
        int baseY = stationCenter.getY();

        AABB clearBox = new AABB(-92.0, baseY - 5, -120.0, -42.0, baseY + 12, -34.0);
        List<ArmorStand> existingStands = level.getEntitiesOfClass(ArmorStand.class, clearBox);
        for (ArmorStand stand : existingStands) {
            stand.discard();
        }
        List<LivingEntity> existingMobs = level.getEntitiesOfClass(LivingEntity.class, clearBox, e -> !(e instanceof ServerPlayer));
        for (LivingEntity mob : existingMobs) {
            mob.discard();
        }

        for (int x = -90; x <= -44; x++) {
            for (int z = -34; z >= -118; z--) {
                for (int dy = 1; dy <= 8; dy++) {
                    BlockPos p = new BlockPos(x, baseY + dy, z);
                    if (!level.getBlockState(p).isAir()) {
                        setAirSafe(level, p);
                    }
                }
            }
        }

        for (int z = -36; z >= -114; z--) {
            for (int x = -67; x <= -63; x++) {
                BlockPos p = new BlockPos(x, baseY, z);
                level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = (x == -67 || x == -63);
                boolean light = (Math.abs(z) % 4 == 0 && x == -65);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }

        BlockPos portalArch = new BlockPos(-65, baseY, -36);
        level.setBlock(portalArch.offset(-2, 1, 0), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(portalArch.offset(-2, 2, 0), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(portalArch.offset(-2, 3, 0), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(portalArch.offset(2, 1, 0), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(portalArch.offset(2, 2, 0), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(portalArch.offset(2, 3, 0), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(portalArch.offset(-1, 4, 0), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(portalArch.offset(0, 4, 0), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 3);
        level.setBlock(portalArch.offset(1, 4, 0), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        spawnHologram(level, -64.5, baseY + 4.8, -35.5, "§c§l[Polígono de Defesa & Armadilhas Táticas]");
        spawnHologram(level, -64.5, baseY + 4.45, -35.5, "§7Complexo de Testes Isolados com Reatores Infinitos");

        buildIsolatedTestingChamber(level, -83, -69, -52, -42, baseY, -69,
                "§b§l[Câmara 1: Escudo Cinético de Deflexão]", "§7Interceptação balística contínua | Reator Ativo");
        BlockPos kShieldPos = new BlockPos(-76, baseY + 1, -47);
        level.setBlock(kShieldPos, SandStormBlocks.KINETIC_SHIELD_GENERATOR.defaultBlockState(), 3);
        if (level.getBlockEntity(kShieldPos) instanceof KineticShieldGeneratorBlockEntity kShieldBe) {
            kShieldBe.setEnergy(20000);
        }
        defenseKineticShieldPos = kShieldPos;
        BlockPos railgunPos = new BlockPos(-76, baseY + 1, -44);
        level.setBlock(railgunPos, SandStormBlocks.KINETIC_RAILGUN.defaultBlockState().setValue(KineticRailgunBlock.FACING, Direction.WEST), 3);
        if (level.getBlockEntity(railgunPos) instanceof KineticRailgunBlockEntity railgunBe) {
            railgunBe.setItem(0, new ItemStack(SandStormItems.KINETIC_SLUG, 64));
            railgunBe.setItem(1, new ItemStack(SandStormItems.KINETIC_SLUG, 64));
        }

        buildIsolatedTestingChamber(level, -83, -69, -68, -58, baseY, -69,
                "§b§l[Câmara 2: Torreta Sônica Autônoma]", "§7Rastreamento 360° e disparos sônicos em tempo real");
        BlockPos turretPos = new BlockPos(-73, baseY + 1, -63);
        level.setBlock(turretPos, SandStormBlocks.AUTONOMOUS_SONIC_TURRET.defaultBlockState().setValue(AutonomousSonicTurretBlock.FACING, Direction.WEST), 3);
        if (level.getBlockEntity(turretPos) instanceof AutonomousSonicTurretBlockEntity turretBe) {
            turretBe.setEnergy(50000);
        }
        defenseSonicTurretPos = turretPos;

        buildIsolatedTestingChamber(level, -83, -69, -84, -74, baseY, -69,
                "§b§l[Câmara 3: Escudo de Plasma & Pilar Sônico]", "§7Cúpula defensiva de 32m e pulso acústico");
        BlockPos plasmaPos = new BlockPos(-76, baseY + 1, -77);
        level.setBlock(plasmaPos, SandStormBlocks.PLASMA_SHIELD_GENERATOR.defaultBlockState(), 3);
        if (level.getBlockEntity(plasmaPos) instanceof PlasmaShieldGeneratorBlockEntity plasmaBe) {
            plasmaBe.setStoredEnergy(1000000);
            plasmaBe.setShieldActive(true);
            plasmaBe.setFieldRadius(32);
        }
        defensePlasmaShieldPos = plasmaPos;
        BlockPos pylonPos = new BlockPos(-76, baseY + 1, -81);
        level.setBlock(pylonPos, SandStormBlocks.ACOUSTIC_DEFENSE_PYLON.defaultBlockState().setValue(AcousticDefensePylonBlock.ACTIVE, true), 3);
        if (level.getBlockEntity(pylonPos) instanceof AcousticDefensePylonBlockEntity pylonBe) {
            pylonBe.setStoredEnergy(50000);
        }
        defensePylonPos = pylonPos;

        buildIsolatedTestingChamber(level, -83, -69, -100, -90, baseY, -69,
                "§c§l[Câmara 4: Ejetor Cinético & Fosso Oculto]", "§7Arremesso vertical de 12 blocos e piso retrátil");
        BlockPos ejectorPos = new BlockPos(-74, baseY + 1, -95);
        level.setBlock(ejectorPos, SandStormBlocks.KINETIC_EJECTOR_TRAP.defaultBlockState(), 3);
        defenseEjectorPos = ejectorPos;
        BlockPos pitTrapPos = new BlockPos(-78, baseY, -95);
        for (int py = baseY - 1; py >= baseY - 4; py--) {
            setAirSafe(level, new BlockPos(pitTrapPos.getX(), py, pitTrapPos.getZ()));
        }
        level.setBlock(new BlockPos(pitTrapPos.getX(), baseY - 4, pitTrapPos.getZ()), SandStormBlocks.KINETIC_FLOOR_SPIKES.defaultBlockState(), 3);
        level.setBlock(pitTrapPos, SandStormBlocks.COLLAPSIBLE_TRAPDOOR.defaultBlockState(), 3);
        BlockPos snareArchPos = new BlockPos(-76, baseY + 4, -98);
        level.setBlock(snareArchPos, Blocks.IRON_BLOCK.defaultBlockState(), 3);
        level.setBlock(snareArchPos.below(), SandStormBlocks.NANITE_WEB_SNARE.defaultBlockState(), 3);

        buildIsolatedTestingChamber(level, -61, -47, -52, -42, baseY, -61,
                "§6§l[Câmara 5: Grelha Térmica & Mina Criogênica]", "§7Vapor pressurizado contínuo e névoa criogênica");
        BlockPos thermalPos = new BlockPos(-54, baseY + 1, -45);
        level.setBlock(thermalPos, SandStormBlocks.THERMAL_VENT_TRAP.defaultBlockState().setValue(ThermalVentTrapBlock.ACTIVE, true), 3);
        defenseThermalVentPos = thermalPos;
        BlockPos cryoPos = new BlockPos(-54, baseY + 1, -49);
        level.setBlock(cryoPos, SandStormBlocks.CRYO_FREEZE_MINE.defaultBlockState().setValue(CryoFreezeMineBlock.ACTIVE, true), 3);
        defenseCryoMinePos = cryoPos;
        BlockPos spikesPos = new BlockPos(-52, baseY + 1, -47);
        level.setBlock(spikesPos, SandStormBlocks.NEUROTOXIC_SPIKES.defaultBlockState(), 3);

        buildIsolatedTestingChamber(level, -61, -47, -68, -58, baseY, -61,
                "§5§l[Câmara 6: Micro-Singularidade Gravitacional]", "§7Vórtice que suga e comprime alvos no núcleo");
        BlockPos singularityPos = new BlockPos(-54, baseY + 1, -63);
        level.setBlock(singularityPos, SandStormBlocks.MICRO_SINGULARITY_TRAP.defaultBlockState().setValue(MicroSingularityTrapBlock.ACTIVE, true), 3);
        defenseSingularityPos = singularityPos;
        BlockPos quicksandPos = new BlockPos(-52, baseY + 1, -63);
        level.setBlock(quicksandPos, SandStormBlocks.NANITE_QUICKSAND.defaultBlockState(), 3);

        buildIsolatedTestingChamber(level, -61, -47, -84, -74, baseY, -61,
                "§e§l[Câmara 7: Tripwire Laser & Choque Piezo]", "§7Feixe óptico de detecção e descarga de alta voltagem");
        BlockPos tripwirePos = new BlockPos(-54, baseY + 1, -76);
        level.setBlock(tripwirePos.south(), Blocks.IRON_BLOCK.defaultBlockState(), 3);
        level.setBlock(tripwirePos, SandStormBlocks.HOLO_LASER_TRIPWIRE.defaultBlockState().setValue(HoloLaserTripwireBlock.FACING, Direction.NORTH), 3);
        BlockPos piezoPos = new BlockPos(-54, baseY + 1, -81);
        level.setBlock(piezoPos, SandStormBlocks.PIEZO_SHOCK_TRAP.defaultBlockState(), 3);
        BlockPos jawPos = new BlockPos(-52, baseY + 1, -79);
        level.setBlock(jawPos, SandStormBlocks.SERRATED_JAW_TRAP.defaultBlockState(), 3);

        buildIsolatedTestingChamber(level, -61, -47, -100, -90, baseY, -61,
                "§d§l[Câmara 8: Engodo Holográfico & Quitina Corrosiva]", "§7Isca 3D ativa e espinhos com ácido corrosivo");
        BlockPos miragePos = new BlockPos(-54, baseY + 1, -95);
        level.setBlock(miragePos, SandStormBlocks.MIRAGE_HOLOGRAM_TRAP.defaultBlockState(), 3);
        defenseMiragePos = miragePos;
        BlockPos seismicPos = new BlockPos(-51, baseY + 1, -93);
        level.setBlock(seismicPos, SandStormBlocks.SEISMIC_DECOY_TRAP.defaultBlockState(), 3);
        BlockPos chitinPos1 = new BlockPos(-51, baseY + 1, -97);
        BlockPos chitinPos2 = new BlockPos(-52, baseY + 1, -97);
        level.setBlock(chitinPos1, SandStormBlocks.CORROSIVE_CHITIN_SPIKE_WALL.defaultBlockState().setValue(CorrosiveChitinSpikeWallBlock.FACING, Direction.UP), 3);
        level.setBlock(chitinPos2, SandStormBlocks.CORROSIVE_CHITIN_SPIKE_WALL.defaultBlockState().setValue(CorrosiveChitinSpikeWallBlock.FACING, Direction.UP), 3);
        BlockPos barrierPos = new BlockPos(-57, baseY + 1, -95);
        level.setBlock(barrierPos, SandStormBlocks.ELECTRIFIED_SPIKE_BARRIER.defaultBlockState(), 3);
        BlockPos gatePos = new BlockPos(-54, baseY + 1, -91);
        level.setBlock(gatePos, SandStormBlocks.CRUSHING_SPIKE_GATE.defaultBlockState(), 3);
        level.setBlock(gatePos.east(), Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACE, AttachFace.FLOOR).setValue(LeverBlock.FACING, Direction.NORTH), 3);

        for (int x = -73; x <= -57; x++) {
            for (int z = -105; z >= -115; z--) {
                BlockPos p = new BlockPos(x, baseY, z);
                level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = (x == -73 || x == -57 || z == -105 || z == -115);
                Block b = edge ? Blocks.CUT_SANDSTONE : SandStormBlocks.SEISMIC_DAMPENER_PAVING;
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
        BlockPos centralReactor = new BlockPos(-65, baseY - 1, -110);
        level.setBlock(centralReactor, SandStormBlocks.CRYO_REACTOR_CORE.defaultBlockState().setValue(CryoReactorCoreBlock.OPERATIONAL, true), 3);
        level.setBlock(centralReactor.west(), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
        level.setBlock(centralReactor.east(), SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
        SolidStateAccumulatorManager.registerAccumulator(level.dimension(), centralReactor.west(), 500000, 500000, true, 1);
        SolidStateAccumulatorManager.registerAccumulator(level.dimension(), centralReactor.east(), 500000, 500000, true, 1);

        BlockPos chestPos = new BlockPos(-68, baseY + 1, -110);
        setChestSafe(level, chestPos, Blocks.CHEST.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.KINETIC_SLUG, 64));
            chest.setItem(1, new ItemStack(SandStormItems.KINETIC_SLUG, 64));
            chest.setItem(2, new ItemStack(SandStormItems.SONIC_CANNON, 1));
            chest.setItem(3, new ItemStack(SandStormItems.HEAVY_PLASMA_CANNON, 1));
            chest.setItem(4, new ItemStack(SandStormItems.PLASMA_RIFLE, 1));
            chest.setItem(5, new ItemStack(SandStormItems.ANOMALY_RADAR, 1));
            chest.setItem(6, new ItemStack(SandStormItems.VIBRO_CRYSKNIFE, 1));
            chest.setItem(7, new ItemStack(SandStormItems.STUN_GRENADE, 16));
            chest.setItem(8, new ItemStack(SandStormItems.SMOKE_GRENADE, 16));
            chest.setItem(9, new ItemStack(SandStormBlocks.COLLAPSIBLE_TRAPDOOR, 16));
            chest.setItem(10, new ItemStack(SandStormBlocks.KINETIC_EJECTOR_TRAP, 16));
            chest.setItem(11, new ItemStack(SandStormBlocks.THERMAL_VENT_TRAP, 16));
            chest.setItem(12, new ItemStack(SandStormBlocks.SERRATED_JAW_TRAP, 16));
            chest.setItem(13, new ItemStack(SandStormBlocks.PIEZO_SHOCK_TRAP, 16));
            chest.setItem(14, new ItemStack(SandStormItems.TACTICAL_DETONATOR, 1));
            chest.setItem(15, new ItemStack(SandStormItems.TRAP_DISARMER_KIT, 1));
            chest.setItem(16, new ItemStack(SandStormBlocks.MIRAGE_HOLOGRAM_TRAP, 16));
            chest.setItem(17, new ItemStack(SandStormBlocks.NANITE_WEB_SNARE, 16));
            chest.setItem(18, new ItemStack(SandStormBlocks.NEUROTOXIC_SPIKES, 16));
            chest.setItem(19, new ItemStack(SandStormBlocks.CRYO_FREEZE_MINE, 16));
            chest.setItem(20, new ItemStack(SandStormBlocks.MICRO_SINGULARITY_TRAP, 16));
            chest.setItem(21, new ItemStack(SandStormBlocks.SEISMIC_DECOY_TRAP, 16));
            chest.setItem(22, new ItemStack(SandStormBlocks.HOLO_LASER_TRIPWIRE, 16));
            chest.setItem(23, new ItemStack(SandStormBlocks.NANITE_QUICKSAND, 16));
            chest.setItem(24, new ItemStack(SandStormBlocks.ELECTRIFIED_SPIKE_BARRIER, 16));
            chest.setItem(25, new ItemStack(SandStormBlocks.CORROSIVE_CHITIN_SPIKE_WALL, 16));
            chest.setItem(26, new ItemStack(SandStormBlocks.CRUSHING_SPIKE_GATE, 4));
        }

        BlockPos mannequinPos = new BlockPos(-33, baseY + 1, -110);
        spawnMannequin(level, mannequinPos, "§bManequim: Traje Espacial Isolante",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE),
                new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS), new ItemStack(SandStormItems.SPACE_SUIT_BOOTS),
                new ItemStack(SandStormItems.TACTICAL_DETONATOR), new ItemStack(SandStormItems.TRAP_DISARMER_KIT));

        BlockPos returnPos = new BlockPos(-36, baseY + 1, -113);
        setupReturnPad(level, returnPos);

        spawnHologram(level, -35.5, baseY + 4.5, -105.5, "§6§l[Arsenal Tático & Reator Central de Alimentação]");
        spawnHologram(level, -35.5, baseY + 4.15, -105.5, "§7Reatores Infinitos | Suprimentos e Retorno à Praça");
    }

    private static void tickDefenseDemos(ServerLevel level, long tick) {
        if (tick % 20 == 0) {
            if (defenseKineticShieldPos != null && level.isLoaded(defenseKineticShieldPos)
                    && level.getBlockEntity(defenseKineticShieldPos) instanceof KineticShieldGeneratorBlockEntity kShield) {
                kShield.setEnergy(20000);
            }
            if (defenseSonicTurretPos != null && level.isLoaded(defenseSonicTurretPos)
                    && level.getBlockEntity(defenseSonicTurretPos) instanceof AutonomousSonicTurretBlockEntity turret) {
                turret.setEnergy(50000);
            }
            if (defensePlasmaShieldPos != null && level.isLoaded(defensePlasmaShieldPos)
                    && level.getBlockEntity(defensePlasmaShieldPos) instanceof PlasmaShieldGeneratorBlockEntity plasma) {
                plasma.setStoredEnergy(1000000);
                plasma.setShieldActive(true);
            }
            if (defensePylonPos != null && level.isLoaded(defensePylonPos)
                    && level.getBlockEntity(defensePylonPos) instanceof AcousticDefensePylonBlockEntity pylon) {
                pylon.setStoredEnergy(50000);
            }
        }

        if (tick % 30 == 0 && defenseKineticShieldPos != null && level.isLoaded(defenseKineticShieldPos)) {
            EntityType<?> arrowType = BuiltInRegistries.ENTITY_TYPE.getValue(SandStormMod.mcId("arrow"));
            if (arrowType != null) {
                Entity arrowEntity = arrowType.create(level, EntitySpawnReason.TRIGGERED);
                if (arrowEntity instanceof Projectile arrow) {
                    arrow.setPos(defenseKineticShieldPos.getX() + 5.0, defenseKineticShieldPos.getY() + 1.2, defenseKineticShieldPos.getZ() + 0.5);
                    arrow.shoot(-1.0, 0.05, 0.0, 1.2f, 0.0f);
                    level.addFreshEntity(arrow);
                }
            }
        }

        if (tick % 30 == 0 && defenseSonicTurretPos != null && level.isLoaded(defenseSonicTurretPos)) {
            BlockPos targetPos = defenseSonicTurretPos.offset(-7, 0, 0);
            Entity existingTarget = defenseSonicTargetId != null ? level.getEntity(defenseSonicTargetId) : null;
            if (existingTarget instanceof LivingEntity living && living.isAlive()) {
                living.setHealth(living.getMaxHealth());
            } else {
                EntityType<?> huskType = BuiltInRegistries.ENTITY_TYPE.getValue(SandStormMod.mcId("husk"));
                if (huskType != null) {
                    Entity entity = huskType.create(level, EntitySpawnReason.TRIGGERED);
                    if (entity instanceof LivingEntity living) {
                        living.setPos(targetPos.getX() + 0.5, targetPos.getY() + 0.1, targetPos.getZ() + 0.5);
                        living.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
                        living.setCustomName(Component.literal("§c[Alvo Hostil de Teste]"));
                        living.setCustomNameVisible(true);
                        level.addFreshEntity(living);
                        defenseSonicTargetId = living.getUUID();
                    }
                }
            }
        }

        if (tick % 20 == 0 && defenseThermalVentPos != null && level.isLoaded(defenseThermalVentPos)) {
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, defenseThermalVentPos.getX() + 0.5, defenseThermalVentPos.getY() + 0.3, defenseThermalVentPos.getZ() + 0.5, 6, 0.2, 0.4, 0.2, 0.04);
            level.sendParticles(ParticleTypes.FLAME, defenseThermalVentPos.getX() + 0.5, defenseThermalVentPos.getY() + 0.2, defenseThermalVentPos.getZ() + 0.5, 3, 0.1, 0.05, 0.1, 0.02);
        }

        if (tick % 25 == 0 && defenseCryoMinePos != null && level.isLoaded(defenseCryoMinePos)) {
            level.sendParticles(ParticleTypes.SNOWFLAKE, defenseCryoMinePos.getX() + 0.5, defenseCryoMinePos.getY() + 0.3, defenseCryoMinePos.getZ() + 0.5, 8, 0.3, 0.2, 0.3, 0.02);
            level.sendParticles(ParticleTypes.CLOUD, defenseCryoMinePos.getX() + 0.5, defenseCryoMinePos.getY() + 0.2, defenseCryoMinePos.getZ() + 0.5, 4, 0.2, 0.1, 0.2, 0.02);
        }

        if (tick % 15 == 0 && defenseSingularityPos != null && level.isLoaded(defenseSingularityPos)) {
            level.sendParticles(ParticleTypes.PORTAL, defenseSingularityPos.getX() + 0.5, defenseSingularityPos.getY() + 0.4, defenseSingularityPos.getZ() + 0.5, 14, 0.4, 0.4, 0.4, -0.4);
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, defenseSingularityPos.getX() + 0.5, defenseSingularityPos.getY() + 0.3, defenseSingularityPos.getZ() + 0.5, 6, 0.2, 0.2, 0.2, -0.05);
        }

        if (tick % 60 == 0 && defenseMiragePos != null && level.isLoaded(defenseMiragePos)) {
            Entity existingDecoy = defenseMirageDecoyId != null ? level.getEntity(defenseMirageDecoyId) : null;
            if (!(existingDecoy instanceof MirageDecoyEntity) || !existingDecoy.isAlive()) {
                MirageDecoyEntity decoy = new MirageDecoyEntity(SandStormEntities.MIRAGE_DECOY, level);
                decoy.setPos(defenseMiragePos.getX() + 0.5, defenseMiragePos.getY() + 0.2, defenseMiragePos.getZ() + 0.5);
                level.addFreshEntity(decoy);
                defenseMirageDecoyId = decoy.getUUID();
            }
        }

        if (tick % 60 == 0 && defenseEjectorPos != null && level.isLoaded(defenseEjectorPos)) {
            @SuppressWarnings("unchecked")
            EntityType<ArmorStand> armorStandType = (EntityType<ArmorStand>) (EntityType<?>) BuiltInRegistries.ENTITY_TYPE.getValue(SandStormMod.mcId("armor_stand"));
            ArmorStand dummy = new ArmorStand(armorStandType, level);
            dummy.setPos(defenseEjectorPos.getX() + 0.5, defenseEjectorPos.getY() + 0.2, defenseEjectorPos.getZ() + 0.5);
            dummy.setCustomName(Component.literal("§e[Manequim Balístico]"));
            dummy.setCustomNameVisible(true);
            dummy.setDeltaMovement(0.0, 1.3, 0.0);
            level.addFreshEntity(dummy);
            level.sendParticles(ParticleTypes.POOF, defenseEjectorPos.getX() + 0.5, defenseEjectorPos.getY() + 0.5, defenseEjectorPos.getZ() + 0.5, 8, 0.2, 0.1, 0.2, 0.05);
            level.playSound(null, defenseEjectorPos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 1.0f, 1.2f);
        }
    }

    private static void buildDefenseWalkway(ServerLevel level, BlockPos center) {
        for (int x = -33; x >= -65; x--) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = center.offset(x, 0, -22 + dz);
                level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = Math.abs(dz) == 2;
                boolean light = (Math.abs(x) % 4 == 0 && dz == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
        for (int z = -22; z >= -36; z--) {
            for (int dx = -2; dx <= 2; dx++) {
                BlockPos p = center.offset(-65 + dx, 0, z);
                level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = Math.abs(dx) == 2;
                boolean light = (Math.abs(z) % 4 == 0 && dx == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
    }

    public static void buildOrbitalLaunchStation(ServerLevel level, BlockPos stationCenter) {
        int baseY = stationCenter.getY();

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                for (int dy = 1; dy <= 6; dy++) {
                    BlockPos p = stationCenter.offset(dx, dy, dz);
                    if (!level.getBlockState(p).isAir()) {
                        setAirSafe(level, p);
                    }
                }
            }
        }

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                BlockPos p = stationCenter.offset(dx, 0, dz);
                boolean edge = Math.abs(dx) == 7 || Math.abs(dz) == 7;
                boolean corner = Math.abs(dx) == 7 && Math.abs(dz) == 7;
                Block b = corner ? Blocks.OCHRE_FROGLIGHT : (edge ? Blocks.CUT_SANDSTONE : SandStormBlocks.MORPHING_ALLOY_BLOCK);
                level.setBlock(p, b.defaultBlockState(), 3);
                if (edge && !corner) {
                    level.setBlock(p.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 3);
                }
            }
        }

        BlockPos massDriverPos = stationCenter.offset(0, 1, 0);
        level.setBlock(massDriverPos, SandStormBlocks.ORBITAL_MASS_DRIVER.defaultBlockState(), 3);
        if (level.getBlockEntity(massDriverPos) instanceof OrbitalMassDriverBlockEntity driverBe) {
            driverBe.setEnergy(OrbitalMassDriverBlockEntity.MAX_ENERGY);
        }

        BlockPos groundStationPos = stationCenter.offset(-4, 1, 0);
        level.setBlock(groundStationPos, SandStormBlocks.ORBITAL_GROUND_STATION.defaultBlockState(), 3);
        if (level.getBlockEntity(groundStationPos) instanceof OrbitalGroundStationBlockEntity stationBe) {
            stationBe.setEnergy(100000);
        }

        BlockPos telescopePos = stationCenter.offset(4, 1, 0);
        level.setBlock(telescopePos, SandStormBlocks.SPECTRAL_SURVEY_TELESCOPE.defaultBlockState(), 3);

        BlockPos spirePos = stationCenter.offset(0, 1, 4);
        level.setBlock(spirePos, SandStormBlocks.HOLO_TACTICAL_SPIRE.defaultBlockState(), 3);

        BlockPos accumulatorPos = stationCenter.offset(-4, 1, -4);
        level.setBlock(accumulatorPos, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState().setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, 4), 3);
        if (level.getBlockEntity(accumulatorPos) instanceof SolidStateAccumulatorBlockEntity accBe) {
            accBe.setStoredEnergy(500000);
        }

        BlockPos solarPos = stationCenter.offset(-4, 1, -5);
        level.setBlock(solarPos, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);

        BlockPos cable = stationCenter.offset(-2, 1, 0);
        level.setBlock(cable, SandStormBlocks.QUANTUM_NETWORK_CABLE.defaultBlockState(), 3);

        BlockPos chestPos = stationCenter.offset(4, 1, -4);
        setChestSafe(level, chestPos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.WEATHER_RECON_SATELLITE, 4));
            chest.setItem(1, new ItemStack(SandStormItems.ORBITAL_SOLAR_REFLECTOR_SATELLITE, 4));
            chest.setItem(2, new ItemStack(SandStormItems.SAR_GEOLOGICAL_SATELLITE, 4));
            chest.setItem(3, new ItemStack(SandStormItems.ORBITAL_KINETIC_LANCE_SATELLITE, 4));
            chest.setItem(4, new ItemStack(SandStormItems.CYBERNETIC_COMMAND_UPLINK, 2));
            chest.setItem(5, new ItemStack(SandStormItems.ANOMALY_RADAR, 1));
        }

        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 4.2, stationCenter.getZ() + 0.5, "§3§l[Estação de Lançamento Orbital]");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 3.85, stationCenter.getZ() + 0.5, "§bCanhão de Massa Eletromagnético (500.000 J)");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 3.5, stationCenter.getZ() + 0.5, "§eInsira um Satélite no Canhão para lançar");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 3.15, stationCenter.getZ() + 0.5, "§7Use o Uplink Cibernético para comandar a órbita");

        setupReturnPad(level, stationCenter.offset(5, 1, 5));
    }

    private static void buildOrbitalWalkway(ServerLevel level, BlockPos center) {
        for (int z = 3; z <= 25; z++) {
            for (int dx = -2; dx <= 2; dx++) {
                BlockPos p = center.offset(50 + dx, 0, z);
                level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = Math.abs(dx) == 2;
                boolean light = (z % 4 == 0 && dx == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
        for (int x = 53; x <= 72; x++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = center.offset(x, 0, 25 + dz);
                level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = Math.abs(dz) == 2;
                boolean light = (x % 4 == 0 && dz == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
    }

    public static void buildCyberSteedShowcase(ServerLevel level, BlockPos stationCenter, ServerPlayer player) {
        int baseY = stationCenter.getY();

        AABB clearBox = new AABB(stationCenter.getX() - 26.0, baseY - 2, stationCenter.getZ() - 16.0, stationCenter.getX() + 26.0, baseY + 10, stationCenter.getZ() + 19.0);
        List<Entity> old = level.getEntitiesOfClass(Entity.class, clearBox, e -> !(e instanceof ServerPlayer));
        for (Entity e : old) {
            e.discard();
        }

        for (int dx = -25; dx <= 25; dx++) {
            for (int dz = -15; dz <= 18; dz++) {
                for (int dy = 1; dy <= 8; dy++) {
                    BlockPos p = stationCenter.offset(dx, dy, dz);
                    if (!level.getBlockState(p).isAir()) {
                        setAirSafe(level, p);
                    }
                }
            }
        }

        for (int dx = -25; dx <= 25; dx++) {
            for (int dz = -15; dz <= 18; dz++) {
                BlockPos floorPos = stationCenter.offset(dx, 0, dz);
                boolean edgeX = (dx == -25 || dx == 25);
                boolean edgeZ = (dz == -15 || dz == 18);
                boolean isEdge = edgeX || edgeZ;
                boolean isCorner = edgeX && edgeZ;

                Block b = isCorner ? Blocks.OCHRE_FROGLIGHT : (isEdge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                level.setBlock(floorPos, b.defaultBlockState(), 2);

                if (isEdge && !isCorner) {
                    if (!(dz == -15 && Math.abs(dx) <= 2)) {
                        level.setBlock(floorPos.above(), SandStormBlocks.BIOSPHERE_FENCE.defaultBlockState(), 2);
                    }
                }
            }
        }

        for (int dy = 1; dy <= 4; dy++) {
            level.setBlock(stationCenter.offset(-2, dy, -15), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 2);
            level.setBlock(stationCenter.offset(2, dy, -15), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 2);
        }
        for (int dx = -1; dx <= 1; dx++) {
            level.setBlock(stationCenter.offset(dx, 4, -15), Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
        }
        level.setBlock(stationCenter.offset(0, 4, -15), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 2);
        level.setBlock(stationCenter.offset(-2, 5, -15), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        level.setBlock(stationCenter.offset(2, 5, -15), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);

        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 4.8, stationCenter.getZ() - 15.0, "§6§l[SETOR HÍPICO: GALOPE CIBERNÉTICO]");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 4.45, stationCenter.getZ() - 15.0, "§7Pista de Testes, Saltos & Adestramento");

        BlockPos chestPos = stationCenter.offset(4, 1, -13);
        level.setBlock(chestPos.below(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        setChestSafe(level, chestPos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.CYBER_STEED_SPAWN_EGG, 64));
            chest.setItem(1, new ItemStack(SandStormItems.REPROGRAMMER_TOOL));
            chest.setItem(2, new ItemStack(SandStormItems.REPAIR_TOOL));
            chest.setItem(3, new ItemStack(SandStormItems.STEED_UPGRADE_SONIC_TURRET, 8));
            chest.setItem(4, new ItemStack(SandStormItems.STEED_UPGRADE_BATTERY, 8));
            chest.setItem(5, new ItemStack(SandStormItems.STEED_UPGRADE_SPEED, 8));
            chest.setItem(6, new ItemStack(SandStormItems.STEED_UPGRADE_CARGO, 8));
            chest.setItem(7, new ItemStack(SandStormItems.SCRAP_METAL, 64));
            chest.setItem(8, new ItemStack(SandStormItems.SPACE_SUIT_HELMET));
            chest.setItem(9, new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE));
            chest.setItem(10, new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS));
            chest.setItem(11, new ItemStack(SandStormItems.SPACE_SUIT_BOOTS));
            chest.setItem(12, new ItemStack(Items.SADDLE, 8));
            chest.setItem(13, new ItemStack(Items.LEAD, 8));
            chest.setItem(14, new ItemStack(Items.FLINT_AND_STEEL, 2));
            chest.setItem(15, new ItemStack(Items.IRON_INGOT, 32));
            chest.setItem(16, new ItemStack(SandStormBlocks.SOLID_STATE_ACCUMULATOR, 4));
            chest.setItem(17, new ItemStack(SandStormItems.VIBRO_CRYSKNIFE));
            chest.setItem(18, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 16));
        }
        spawnHologram(level, chestPos.getX() + 0.5, baseY + 2.4, chestPos.getZ() + 0.5, "§e[Baú do Cavaleiro Cibernético]");
        spawnHologram(level, chestPos.getX() + 0.5, baseY + 2.1, chestPos.getZ() + 0.5, "§7Upgrades, Sela, Reprogramador & Traje");

        setupReturnPad(level, stationCenter.offset(-4, 1, -13));

        for (int stallIdx = 0; stallIdx < 3; stallIdx++) {
            int stallZ = -8 + (stallIdx * 5);
            for (int sx = -18; sx <= -12; sx++) {
                for (int sz = stallZ - 2; sz <= stallZ + 2; sz++) {
                    BlockPos p = stationCenter.offset(sx, 0, sz);
                    boolean isBorder = (sx == -18 || sx == -12 || sz == stallZ - 2 || sz == stallZ + 2);
                    Block floor = isBorder ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE;
                    level.setBlock(p, floor.defaultBlockState(), 2);
                    if (sx == -18 || sz == stallZ - 2 || sz == stallZ + 2) {
                        level.setBlock(p.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                        level.setBlock(p.above(2), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                    }
                }
            }
            level.setBlock(stationCenter.offset(-12, 1, stallZ), SandStormBlocks.BIOSPHERE_FENCE_GATE.defaultBlockState(), 2);
            level.setBlock(stationCenter.offset(-12, 1, stallZ - 1), Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
            level.setBlock(stationCenter.offset(-12, 1, stallZ + 1), Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
            level.setBlock(stationCenter.offset(-12, 2, stallZ - 1), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 2);
            level.setBlock(stationCenter.offset(-12, 2, stallZ + 1), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 2);
        }

        CyberSteedEntity wildSteed = new CyberSteedEntity(SandStormEntities.CYBER_STEED, level);
        wildSteed.setPos(stationCenter.getX() - 15.0 + 0.5, baseY + 1.0, stationCenter.getZ() - 8.0 + 0.5);
        wildSteed.setPersistenceRequired();
        wildSteed.setTame(false, false);
        level.addFreshEntity(wildSteed);
        spawnHologram(level, stationCenter.getX() - 15.0 + 0.5, baseY + 2.6, stationCenter.getZ() - 8.0 + 0.5, "§c[1. Galope Selvagem (Autônomo)]");
        spawnHologram(level, stationCenter.getX() - 15.0 + 0.5, baseY + 2.3, stationCenter.getZ() - 8.0 + 0.5, "§7Visor Vermelho | Use o Reprogramador");

        CyberSteedEntity tamedSteed = new CyberSteedEntity(SandStormEntities.CYBER_STEED, level);
        tamedSteed.setPos(stationCenter.getX() - 15.0 + 0.5, baseY + 1.0, stationCenter.getZ() - 3.0 + 0.5);
        tamedSteed.setPersistenceRequired();
        if (player != null) {
            tamedSteed.tame(player);
        } else {
            tamedSteed.setTame(true, false);
        }
        tamedSteed.setOrderedToSit(false);
        level.addFreshEntity(tamedSteed);
        spawnHologram(level, stationCenter.getX() - 15.0 + 0.5, baseY + 2.6, stationCenter.getZ() - 3.0 + 0.5, "§b[2. Galope Sobrescrito (Montaria)]");
        spawnHologram(level, stationCenter.getX() - 15.0 + 0.5, baseY + 2.3, stationCenter.getZ() - 3.0 + 0.5, "§7Visor Ciano | Sela Pronta para Montar");

        CyberSteedEntity standbySteed = new CyberSteedEntity(SandStormEntities.CYBER_STEED, level);
        standbySteed.setPos(stationCenter.getX() - 15.0 + 0.5, baseY + 1.0, stationCenter.getZ() + 2.0 + 0.5);
        standbySteed.setPersistenceRequired();
        if (player != null) {
            standbySteed.tame(player);
        } else {
            standbySteed.setTame(true, false);
        }
        standbySteed.setOrderedToSit(true);
        standbySteed.setDeactivated(true);
        level.addFreshEntity(standbySteed);
        spawnHologram(level, stationCenter.getX() - 15.0 + 0.5, baseY + 2.6, stationCenter.getZ() + 2.0 + 0.5, "§8[3. Modo Standby / Desativado]");
        spawnHologram(level, stationCenter.getX() - 15.0 + 0.5, baseY + 2.3, stationCenter.getZ() + 2.0 + 0.5, "§7Patas Ancoradas | Sensível a EMP/Pausa");

        int chamberZ = 9;
        for (int cx = -18; cx <= -12; cx++) {
            for (int cz = chamberZ - 2; cz <= chamberZ + 2; cz++) {
                BlockPos p = stationCenter.offset(cx, 0, cz);
                level.setBlock(p, SandStormBlocks.VAULT_WALL_PANEL.defaultBlockState(), 2);
                boolean isBorder = (cx == -18 || cx == -12 || cz == chamberZ - 2 || cz == chamberZ + 2);
                if (isBorder) {
                    level.setBlock(p.above(), SandStormBlocks.VAULT_WALL_PANEL.defaultBlockState(), 2);
                    level.setBlock(p.above(2), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                    level.setBlock(p.above(3), SandStormBlocks.VAULT_WALL_PANEL.defaultBlockState(), 2);
                }
            }
        }
        level.setBlock(stationCenter.offset(-12, 1, chamberZ), SandStormBlocks.BIOSPHERE_FENCE_GATE.defaultBlockState(), 2);
        level.setBlock(stationCenter.offset(-12, 2, chamberZ), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);

        CyberSteedEntity canisterSteed = new CyberSteedEntity(SandStormEntities.CYBER_STEED, level);
        canisterSteed.setPos(stationCenter.getX() - 15.0 + 0.5, baseY + 1.0, stationCenter.getZ() + chamberZ + 0.5);
        canisterSteed.setPersistenceRequired();
        if (player != null) {
            canisterSteed.tame(player);
        } else {
            canisterSteed.setTame(true, false);
        }
        canisterSteed.setOrderedToSit(true);
        level.addFreshEntity(canisterSteed);
        spawnHologram(level, stationCenter.getX() - 15.0 + 0.5, baseY + 3.4, stationCenter.getZ() + chamberZ + 0.5, "§6[Câmara: Cilindro de Flama]");
        spawnHologram(level, stationCenter.getX() - 15.0 + 0.5, baseY + 3.1, stationCenter.getZ() + chamberZ + 0.5, "§7Ataque o dorso com fogo para detonar");

        for (int trackX = -8; trackX <= 22; trackX++) {
            for (int trackZ = -10; trackZ <= 14; trackZ++) {
                boolean inOuterLoop = (trackX >= 15 || trackX <= -4 || trackZ >= 9 || trackZ <= -6);
                if (inOuterLoop) {
                    BlockPos p = stationCenter.offset(trackX, 0, trackZ);
                    boolean isRedSand = (trackX + trackZ) % 4 == 0;
                    Block sandBlock = isRedSand ? Blocks.RED_SAND : Blocks.SAND;
                    level.setBlock(p, sandBlock.defaultBlockState(), 2);
                }
            }
        }

        for (int tx = 16; tx <= 20; tx++) {
            BlockPos startPos = stationCenter.offset(tx, 0, -6);
            Block startBlock = (tx % 2 == 0) ? Blocks.POLISHED_BLACKSTONE : Blocks.SMOOTH_QUARTZ;
            level.setBlock(startPos, startBlock.defaultBlockState(), 2);
            level.setBlock(startPos.above(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.defaultBlockState(), 2);
        }
        spawnHologram(level, stationCenter.getX() + 18.5, baseY + 3.2, stationCenter.getZ() - 6.0, "§e§l🏁 SPRINT NO DESERTO 🏁");
        spawnHologram(level, stationCenter.getX() + 18.5, baseY + 2.85, stationCenter.getZ() - 6.0, "§7Boost de velocidade e poeira na areia!");

        CyberSteedEntity trackRacer = new CyberSteedEntity(SandStormEntities.CYBER_STEED, level);
        trackRacer.setPos(stationCenter.getX() + 18.5, baseY + 1.0, stationCenter.getZ() - 4.5);
        trackRacer.setPersistenceRequired();
        if (player != null) {
            trackRacer.tame(player);
        } else {
            trackRacer.setTame(true, false);
        }
        trackRacer.setOrderedToSit(false);
        trackRacer.setHasSonicTurret(true);
        trackRacer.setHasBatteryPack(true);
        trackRacer.setHasSpeedOverdrive(true);
        trackRacer.setHasCargoPanniers(true);
        trackRacer.getCargoInventory().setItem(0, new ItemStack(SandStormItems.SPACE_RATION, 16));
        trackRacer.getCargoInventory().setItem(1, new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE, 8));
        trackRacer.getCargoInventory().setItem(2, new ItemStack(SandStormItems.SCRAP_METAL, 32));
        level.addFreshEntity(trackRacer);
        spawnHologram(level, stationCenter.getX() + 18.5, baseY + 3.9, stationCenter.getZ() - 4.5, "§a§l[CORCEL FULL UPGRADE]");
        spawnHologram(level, stationCenter.getX() + 18.5, baseY + 3.55, stationCenter.getZ() - 4.5, "§7Torreta Sônica | Bateria | Turbo | Carga");

        int hurdleX = 5;
        for (int hz = -6; hz <= 8; hz++) {
            for (int hx = hurdleX - 2; hx <= hurdleX + 2; hx++) {
                level.setBlock(stationCenter.offset(hx, 0, hz), Blocks.SANDSTONE.defaultBlockState(), 2);
            }
        }

        int h1Z = -4;
        for (int hx = hurdleX - 2; hx <= hurdleX + 2; hx++) {
            level.setBlock(stationCenter.offset(hx, 1, h1Z), Blocks.CUT_SANDSTONE_SLAB.defaultBlockState(), 2);
        }
        spawnHologram(level, stationCenter.getX() + hurdleX + 0.5, baseY + 2.2, stationCenter.getZ() + h1Z + 0.5, "§a[Obstáculo 1: Salto Baixo (1.0m)]");

        int h2Z = -1;
        for (int hx = hurdleX - 2; hx <= hurdleX + 2; hx++) {
            level.setBlock(stationCenter.offset(hx, 1, h2Z), SandStormBlocks.BIOSPHERE_FENCE.defaultBlockState(), 2);
        }
        spawnHologram(level, stationCenter.getX() + hurdleX + 0.5, baseY + 2.4, stationCenter.getZ() + h2Z + 0.5, "§e[Obstáculo 2: Salto Médio (1.5m)]");

        int h3Z = 3;
        for (int hx = hurdleX - 2; hx <= hurdleX + 2; hx++) {
            level.setBlock(stationCenter.offset(hx, 1, h3Z), Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
        }
        spawnHologram(level, stationCenter.getX() + hurdleX + 0.5, baseY + 2.6, stationCenter.getZ() + h3Z + 0.5, "§6[Obstáculo 3: Salto Alto (2.0m)]");

        int h4Z = 7;
        for (int hx = hurdleX - 2; hx <= hurdleX + 2; hx++) {
            level.setBlock(stationCenter.offset(hx, 1, h4Z), Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
            level.setBlock(stationCenter.offset(hx, 2, h4Z), Blocks.CUT_SANDSTONE_SLAB.defaultBlockState(), 2);
        }
        spawnHologram(level, stationCenter.getX() + hurdleX + 0.5, baseY + 3.4, stationCenter.getZ() + h4Z + 0.5, "§c[Obstáculo 4: Salto Máximo (2.5m)]");
        spawnHologram(level, stationCenter.getX() + hurdleX + 0.5, baseY + 3.05, stationCenter.getZ() + h4Z + 0.5, "§7Segure Espaço e solte no limite");

        CyberSteedEntity hurdleJumper = new CyberSteedEntity(SandStormEntities.CYBER_STEED, level);
        hurdleJumper.setPos(stationCenter.getX() + hurdleX + 0.5, baseY + 1.0, stationCenter.getZ() - 6.5);
        hurdleJumper.setPersistenceRequired();
        if (player != null) {
            hurdleJumper.tame(player);
        } else {
            hurdleJumper.setTame(true, false);
        }
        hurdleJumper.setOrderedToSit(false);
        level.addFreshEntity(hurdleJumper);
    }

    private static void buildCyberSteedWalkway(ServerLevel level, BlockPos center) {
        BlockPos stationCenter = center.offset(-75, 0, 40);
        int baseY = center.getY();

        for (int z = 16; z <= 25; z++) {
            for (int dx = -2; dx <= 2; dx++) {
                BlockPos p = new BlockPos(stationCenter.getX() + dx, baseY, z);
                boolean edge = Math.abs(dx) == 2;
                boolean light = (z % 4 == 0 && dx == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }

        for (int x = -37; x >= -75; x--) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = center.offset(x, 0, 20 + dz);
                boolean edge = Math.abs(dz) == 2;
                boolean light = (Math.abs(x) % 4 == 0 && dz == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
    }

    public static int getBiosphereFrontZ() {
        int maxBlockZ = getCheckerboardMaxZ();
        int startZ = CHECKERBOARD_START_Z;

        List<Item> items = BuiltInRegistries.ITEM.stream()
                .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(SandStormMod.MOD_ID))
                .toList();
        int itemsPerChest = 27;
        int chestCount = (items.size() + itemsPerChest - 1) / itemsPerChest;
        int maxItemZ = startZ + (((chestCount + 1) / 2) * 6) + 4;

        int exhibitionMaxZ = Math.max(maxBlockZ, maxItemZ);
        int domeRadius = AtmosphericTerraformerBlockEntity.TIER1_RADIUS;
        return exhibitionMaxZ + domeRadius + 16;
    }

    public static void buildBiospheresShowcase(ServerLevel level, BlockPos center) {
        int frontZ = getBiosphereFrontZ();
        int backZ = frontZ + 60;
        wipeBiospheresArea(level, center, frontZ, backZ);

        for (int z = 4; z <= frontZ; z++) {
            for (int x = -2; x <= 2; x++) {
                BlockPos bp = center.offset(x, 0, z);
                boolean edge = (Math.abs(x) == 2);
                boolean light = (x == 0 && z % 5 == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                level.setBlock(bp, b.defaultBlockState(), 2);
                if (edge && (z <= 8 || z >= frontZ - 30)) {
                    level.setBlock(bp.above(), Blocks.SANDSTONE_WALL.defaultBlockState(), 2);
                }
            }
        }

        for (int x = -122; x <= 122; x++) {
            for (int z = frontZ - 2; z <= frontZ + 2; z++) {
                BlockPos bp = center.offset(x, 0, z);
                boolean edge = (z == frontZ - 2 || z == frontZ + 2);
                boolean light = (x % 8 == 0 && z == frontZ);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                level.setBlock(bp, b.defaultBlockState(), 2);
                if (edge && (Math.abs(x) < 4 || Math.abs(x) == 16 || Math.abs(x) == 48 || Math.abs(x) == 80 || Math.abs(x) == 112)) {
                    level.setBlock(bp.above(), Blocks.SANDSTONE_WALL.defaultBlockState(), 2);
                }
            }
        }

        int holoZ = frontZ - AtmosphericTerraformerBlockEntity.TIER1_RADIUS - 6;
        BlockPos entranceHolo = center.offset(0, 1, holoZ);
        spawnHologram(level, entranceHolo.getX() + 0.5, entranceHolo.getY() + 1.2, entranceHolo.getZ() + 0.5, "§6[Complexo de Biosferas Planetárias]");
        spawnHologram(level, entranceHolo.getX() + 0.5, entranceHolo.getY() + 0.9, entranceHolo.getZ() + 0.5, "§bRestauração de Biomas & Síntese de Fauna");

        buildCryogenicDome(level, center.offset(-96, 0, frontZ));
        buildXenoFungalDome(level, center.offset(-32, 0, frontZ));
        buildPrimordialOasisDome(level, center.offset(32, 0, frontZ));
        buildMagneticForestDome(level, center.offset(96, 0, frontZ));
        buildTemperateShowcaseDome(level, center.offset(-64, 0, backZ), 0);
        buildTemperateShowcaseDome(level, center.offset(0, 0, backZ), 1);
        buildTemperateShowcaseDome(level, center.offset(64, 0, backZ), 2);
        buildBiospherePaths(level, center, frontZ, backZ);
        buildBiosphereFences(level, center, frontZ, backZ);
        placeBiosphereDoor(level, center.offset(-96, 0, frontZ));
        placeBiosphereDoor(level, center.offset(-32, 0, frontZ));
        placeBiosphereDoor(level, center.offset(32, 0, frontZ));
        placeBiosphereDoor(level, center.offset(96, 0, frontZ));
        placeBiosphereDoor(level, center.offset(-64, 0, backZ));
        placeBiosphereDoor(level, center.offset(0, 0, backZ));
        placeBiosphereDoor(level, center.offset(64, 0, backZ));
    }

    private static void buildBiosphereFences(ServerLevel level, BlockPos center, int frontZ, int backZ) {
        int radius = AtmosphericTerraformerBlockEntity.TIER1_RADIUS;
        BlockPos[] domeCenters = {
                center.offset(-96, 0, frontZ),
                center.offset(-32, 0, frontZ),
                center.offset(32, 0, frontZ),
                center.offset(96, 0, frontZ),
                center.offset(-64, 0, backZ),
                center.offset(0, 0, backZ),
                center.offset(64, 0, backZ)
        };
        for (BlockPos domeCenter : domeCenters) {
            for (int x = radius + 2; x <= radius + 5; x++) {
                level.setBlock(domeCenter.offset(x, 1, -4), SandStormBlocks.BIOSPHERE_FENCE.defaultBlockState(), 2);
                level.setBlock(domeCenter.offset(x, 1, 3), SandStormBlocks.BIOSPHERE_FENCE.defaultBlockState(), 2);
            }
            level.setBlock(domeCenter.offset(radius + 3, 1, -4), SandStormBlocks.BIOSPHERE_FENCE_GATE.defaultBlockState(), 2);
            level.setBlock(domeCenter.offset(radius + 3, 1, 3), SandStormBlocks.BIOSPHERE_FENCE_GATE.defaultBlockState(), 2);
        }
    }

    private static void buildBiospherePaths(ServerLevel level, BlockPos center, int frontZ, int backZ) {
        for (int x = -126; x <= 126; x++) {
            for (int z = frontZ - 2; z <= frontZ + 2; z++) {
                setShowcasePathBlock(level, center.offset(x, 0, z));
            }
        }
        for (int x : new int[]{-64, 0, 64}) {
            for (int z = frontZ + 3; z <= backZ; z++) {
                setShowcasePathBlock(level, center.offset(x, 0, z));
            }
        }
        for (int x = -78; x <= 78; x++) {
            for (int z = backZ - 2; z <= backZ + 2; z++) {
                setShowcasePathBlock(level, center.offset(x, 0, z));
            }
        }
    }

    private static void setShowcasePathBlock(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.canBeReplaced()) {
            boolean light = Math.floorMod(pos.getX() + pos.getZ(), 16) == 0;
            level.setBlock(pos, light ? Blocks.SEA_LANTERN.defaultBlockState() : Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
        }
    }

    private static void placeBiosphereDoor(ServerLevel level, BlockPos domeCenter) {
        int radius = AtmosphericTerraformerBlockEntity.TIER1_RADIUS;
        BlockPos receiverPos = domeCenter.offset(0, 1, 4);
        level.setBlock(receiverPos, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
        BlockPos base = domeCenter.offset(radius, 1, -1);
        for (int z = 0; z < 3; z++) {
            for (int y = 0; y < 2; y++) {
                level.setBlock(base.offset(0, y, z), SandStormBlocks.BIOSPHERE_DOOR.defaultBlockState()
                        .setValue(BiosphereDoorBlock.POSITION, z), 3);
            }
        }
    }

    private static void buildTemperateShowcaseDome(ServerLevel level, BlockPos domeCenter, int variant) {
        int radius = AtmosphericTerraformerBlockEntity.TIER1_RADIUS;
        int baseY = domeCenter.getY();
        for (int x = -radius - 1; x <= radius + 1; x++) {
            for (int z = -radius - 1; z <= radius + 1; z++) {
                if (x * x + z * z <= (radius + 1) * (radius + 1)) {
                    for (int y = -2; y <= 10; y++) {
                        BlockPos clearPos = domeCenter.offset(x, y, z);
                        if (!level.getBlockState(clearPos).isAir()) {
                            level.setBlock(clearPos, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int distance = x * x + z * z;
                if (distance > radius * radius) {
                    continue;
                }
                BlockPos floorPos = domeCenter.offset(x, 0, z);
                level.setBlock(floorPos.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = distance > (radius - 1) * (radius - 1);
                if (edge) {
                    boolean entrance = Math.abs(z) <= 1;
                    level.setBlock(floorPos, variant == 2 ? Blocks.MOSS_BLOCK.defaultBlockState() : Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
                    if (!entrance) {
                        level.setBlock(floorPos.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                        level.setBlock(floorPos.above(2), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                    }
                    continue;
                }
                int pondDistance = (x + 4) * (x + 4) + (z - 4) * (z - 4);
                BlockState floor;
                if (variant == 0) {
                    floor = pondDistance <= 7 ? Blocks.WATER.defaultBlockState() : (distance % 5 == 0 ? Blocks.FARMLAND.defaultBlockState() : Blocks.GRASS_BLOCK.defaultBlockState());
                } else if (variant == 1) {
                    floor = distance % 4 == 0 ? Blocks.PODZOL.defaultBlockState() : Blocks.GRASS_BLOCK.defaultBlockState();
                } else {
                    floor = pondDistance <= 9 ? Blocks.WATER.defaultBlockState() : (distance % 3 == 0 ? Blocks.MUD.defaultBlockState() : Blocks.MOSS_BLOCK.defaultBlockState());
                }
                level.setBlock(floorPos, floor, 2);
                int decor = Math.abs(x * 17 + z * 29) % 11;
                if (floor.is(Blocks.WATER)) {
                    if (decor == 0) {
                        setSafeFlora(level, floorPos.above(), Blocks.LILY_PAD.defaultBlockState());
                    }
                } else if (variant == 0 && floor.is(Blocks.FARMLAND)) {
                    setSafeFlora(level, floorPos.above(), decor % 2 == 0 ? Blocks.WHEAT.defaultBlockState() : Blocks.CARROTS.defaultBlockState());
                } else if (variant == 2 && decor < 2) {
                    setSafeFlora(level, floorPos.above(), decor == 0 ? Blocks.BAMBOO.defaultBlockState() : Blocks.FERN.defaultBlockState());
                } else if (decor < 3) {
                    setSafeFlora(level, floorPos.above(), decor == 0 ? Blocks.POPPY.defaultBlockState() : Blocks.SHORT_GRASS.defaultBlockState());
                }
            }
        }
        ItemStack cartridge = switch (variant) {
            case 0 -> new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_PLAINS);
            case 1 -> new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_FOREST);
            default -> new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_JUNGLE);
        };
        BlockPos terraformerPos = domeCenter.above();
        level.setBlock(terraformerPos, SandStormBlocks.ATMOSPHERIC_TERRAFORMER.defaultBlockState(), 3);
        if (level.getBlockEntity(terraformerPos) instanceof AtmosphericTerraformerBlockEntity terraformer) {
            terraformer.setupTier(1);
            terraformer.setItem(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE, cartridge);
        }
        String title = switch (variant) {
            case 0 -> "§a[Biosfera de Planícies - Epsilon]";
            case 1 -> "§2[Biosfera Florestal - Zeta]";
            default -> "§d[Biosfera Tropical - Eta]";
        };
        String subtitle = switch (variant) {
            case 0 -> "§7Agricultura, pradarias e zonas alagadas";
            case 1 -> "§7Sub-bosque, podzol e clareiras";
            default -> "§7Pântano, musgo e vegetação exuberante";
        };
        spawnHologram(level, domeCenter.getX() + 0.5, baseY + 3.4, domeCenter.getZ() + 0.5, title);
        spawnHologram(level, domeCenter.getX() + 0.5, baseY + 3.1, domeCenter.getZ() + 0.5, subtitle);
        if (variant == 0) {
            spawnBabyAnimal(level, domeCenter.getX() + 5.5, baseY + 1.0, domeCenter.getZ() + 5.5, "cow");
            spawnBabyAnimal(level, domeCenter.getX() - 5.5, baseY + 1.0, domeCenter.getZ() - 5.5, "sheep");
        } else if (variant == 1) {
            spawnBabyAnimal(level, domeCenter.getX() + 5.5, baseY + 1.0, domeCenter.getZ() - 5.5, "fox");
            spawnBabyAnimal(level, domeCenter.getX() - 5.5, baseY + 1.0, domeCenter.getZ() + 5.5, "bee");
        } else {
            spawnBabyAnimal(level, domeCenter.getX() + 5.5, baseY + 1.0, domeCenter.getZ() + 5.5, "parrot");
            spawnBabyAnimal(level, domeCenter.getX() - 5.5, baseY + 1.0, domeCenter.getZ() - 5.5, "frog");
        }
    }

    private static void wipeBiospheresArea(ServerLevel level, BlockPos center, int frontZ, int backZ) {
        int radius = AtmosphericTerraformerBlockEntity.TIER1_RADIUS;
        int startClearZ = frontZ - radius - 8;
        int endClearZ = backZ + radius + 10;
        AABB domesZone = new AABB(
            center.getX() - 130, center.getY() - 5, center.getZ() + startClearZ,
            center.getX() + 130, center.getY() + 30, center.getZ() + endClearZ
        );

        List<Entity> oldEntities = level.getEntitiesOfClass(Entity.class, domesZone, e -> !(e instanceof ServerPlayer));
        for (Entity e : oldEntities) {
            if (domesZone.contains(e.position())) {
                e.discard();
            }
        }
        for (int x = -126; x <= 126; x++) {
            for (int z = startClearZ; z <= endClearZ; z++) {
                for (int y = -5; y <= 30; y++) {
                    BlockPos p = center.offset(x, y, z);
                    if (!level.getBlockState(p).isAir()) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private static void buildCryogenicDome(ServerLevel level, BlockPos domeCenter) {
        int radius = AtmosphericTerraformerBlockEntity.TIER1_RADIUS;
        int baseY = domeCenter.getY();

        for (int x = -radius - 1; x <= radius + 1; x++) {
            for (int z = -radius - 1; z <= radius + 1; z++) {
                if (x * x + z * z <= (radius + 1) * (radius + 1)) {
                    for (int y = -2; y <= 14; y++) {
                        BlockPos p = domeCenter.offset(x, y, z);
                        if (!level.getBlockState(p).isAir()) {
                            level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int distSq = x * x + z * z;
                if (distSq <= radius * radius) {
                    BlockPos p = domeCenter.offset(x, 0, z);
                    level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                    boolean edge = distSq > (radius - 1) * (radius - 1);
                    if (edge) {
                        boolean entrance = Math.abs(z) <= 1;
                        level.setBlock(p, Blocks.PACKED_ICE.defaultBlockState(), 2);
                        if (!entrance) {
                            level.setBlock(p.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                            level.setBlock(p.above(2), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                        } else {
                            level.setBlock(p.above(3), Blocks.PACKED_ICE.defaultBlockState(), 2);
                        }
                    } else {
                        int iceDistSq = (x - 5) * (x - 5) + (z + 4) * (z + 4);
                        int rockDistSq = (x + 6) * (x + 6) + (z - 8) * (z - 8);

                        Block floor;
                        if (iceDistSq <= 10) {
                            floor = Blocks.BLUE_ICE;
                        } else if (iceDistSq <= 22) {
                            floor = Blocks.PACKED_ICE;
                        } else if (rockDistSq <= 12) {
                            floor = (x + z) % 2 == 0 ? Blocks.CALCITE : Blocks.PACKED_ICE;
                        } else {
                            floor = Blocks.SNOW_BLOCK;
                        }
                        level.setBlock(p, floor.defaultBlockState(), 2);

                        if (floor == Blocks.SNOW_BLOCK) {
                            if (Math.abs(x) > 2 || Math.abs(z) > 2) {
                                int decorHash = Math.abs(x * 13 + z * 19) % 11;
                                if (decorHash == 1 || decorHash == 2) {
                                    setSafeFlora(level, p.above(), Blocks.SNOW.defaultBlockState());
                                } else if (decorHash == 3) {
                                    setSafeFlora(level, p.above(), SandStormBlocks.CRYO_XEROPHILIC_LICHEN.defaultBlockState());
                                } else if (decorHash == 4 && (x + z) % 3 == 0) {
                                    setSafeFlora(level, p.above(), Blocks.FERN.defaultBlockState());
                                }
                            }
                        } else if (floor == Blocks.CALCITE || floor == Blocks.PACKED_ICE) {
                            if ((x * 7 + z * 5) % 6 == 0) {
                                setSafeFlora(level, p.above(), SandStormBlocks.CRYO_XEROPHILIC_LICHEN.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }

        buildSpruceTreeFeature(level, domeCenter.offset(-10, 1, -7), 7);
        buildSpruceTreeFeature(level, domeCenter.offset(-7, 1, -11), 5);
        buildSpruceTreeFeature(level, domeCenter.offset(-12, 1, -2), 4);
        buildSpruceTreeFeature(level, domeCenter.offset(9, 1, 8), 6);

        for (int lx = 2; lx <= 5; lx++) {
            BlockPos logPos = domeCenter.offset(lx, 1, 9);
            level.setBlock(logPos, Blocks.SPRUCE_LOG.defaultBlockState(), 2);
            setSafeFlora(level, logPos.above(), Blocks.SNOW.defaultBlockState());
        }

        BlockPos terraformerPos = domeCenter.above();
        level.setBlock(terraformerPos, SandStormBlocks.ATMOSPHERIC_TERRAFORMER.defaultBlockState(), 3);
        if (level.getBlockEntity(terraformerPos) instanceof AtmosphericTerraformerBlockEntity terraformer) {
            terraformer.setupTier(1);
            terraformer.setItem(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE, new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_CRYO));
        }

        BlockPos chillerPos = domeCenter.offset(3, 1, 0);
        level.setBlock(chillerPos, SandStormBlocks.CRYOGENIC_ATMOSPHERIC_CHILLER.defaultBlockState(), 3);
        if (level.getBlockEntity(chillerPos) instanceof CryogenicAtmosphericChillerBlockEntity chiller) {
            chiller.getEnergyStorage().receiveEnergy(500000L);
            chiller.setRadius(24);
        }

        BlockPos incubatorPos = domeCenter.offset(-3, 1, 3);
        level.setBlock(incubatorPos, SandStormBlocks.AMNIOTIC_INCUBATOR.defaultBlockState(), 3);
        if (level.getBlockEntity(incubatorPos) instanceof AmnioticIncubatorBlockEntity incubator) {
            incubator.getEnergyStorage().receiveEnergy(250000L);
            incubator.setWaterAmount(10000);
            incubator.setBiomassUnits(32);
            incubator.setSelectedSpecies("polar_bear");
            incubator.setAutoEcologicalMode(true);
        }

        spawnHologram(level, domeCenter.getX() + 0.5, baseY + 3.4, domeCenter.getZ() + 0.5, "§b[Biosfera Criogênica - Alpha]");
        spawnHologram(level, domeCenter.getX() + 0.5, baseY + 3.1, domeCenter.getZ() + 0.5, "§7Atmosfera Subzero & Permafrost");

        spawnBabyAnimal(level, domeCenter.getX() + 6.5, baseY + 1.0, domeCenter.getZ() + 6.5, "polar_bear");
        spawnBabyAnimal(level, domeCenter.getX() - 6.5, baseY + 1.0, domeCenter.getZ() - 6.5, "rabbit");
    }

    private static void buildSpruceTreeFeature(ServerLevel level, BlockPos treePos, int height) {
        for (int ty = 0; ty < height; ty++) {
            level.setBlock(treePos.above(ty), Blocks.SPRUCE_LOG.defaultBlockState(), 2);
        }
        for (int lx = -2; lx <= 2; lx++) {
            for (int lz = -2; lz <= 2; lz++) {
                if (Math.abs(lx) + Math.abs(lz) <= 3) {
                    BlockPos leafPos = treePos.offset(lx, height - 2, lz);
                    level.setBlock(leafPos, Blocks.SPRUCE_LEAVES.defaultBlockState(), 2);
                    setSafeFlora(level, leafPos.above(), Blocks.SNOW.defaultBlockState());
                }
            }
        }
        for (int lx = -1; lx <= 1; lx++) {
            for (int lz = -1; lz <= 1; lz++) {
                BlockPos leafPos = treePos.offset(lx, height - 1, lz);
                level.setBlock(leafPos, Blocks.SPRUCE_LEAVES.defaultBlockState(), 2);
                setSafeFlora(level, leafPos.above(), Blocks.SNOW.defaultBlockState());
            }
        }
        level.setBlock(treePos.above(height), Blocks.SPRUCE_LEAVES.defaultBlockState(), 2);
        setSafeFlora(level, treePos.above(height + 1), Blocks.SNOW.defaultBlockState());
    }

    private static void buildXenoFungalDome(ServerLevel level, BlockPos domeCenter) {
        int radius = AtmosphericTerraformerBlockEntity.TIER1_RADIUS;
        int baseY = domeCenter.getY();

        for (int x = -radius - 1; x <= radius + 1; x++) {
            for (int z = -radius - 1; z <= radius + 1; z++) {
                if (x * x + z * z <= (radius + 1) * (radius + 1)) {
                    for (int y = -2; y <= 14; y++) {
                        BlockPos p = domeCenter.offset(x, y, z);
                        if (!level.getBlockState(p).isAir()) {
                            level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int distSq = x * x + z * z;
                if (distSq <= radius * radius) {
                    BlockPos p = domeCenter.offset(x, 0, z);
                    level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                    boolean edge = distSq > (radius - 1) * (radius - 1);
                    if (edge) {
                        boolean entrance = Math.abs(z) <= 1;
                        level.setBlock(p, Blocks.CRIMSON_NYLIUM.defaultBlockState(), 2);
                        if (!entrance) {
                            level.setBlock(p.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                            level.setBlock(p.above(2), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                        } else {
                            level.setBlock(p.above(3), Blocks.CRIMSON_NYLIUM.defaultBlockState(), 2);
                        }
                    } else {
                        int radioDistSq = (x - 7) * (x - 7) + (z - 6) * (z - 6);
                        int warpDistSq = (x + 8) * (x + 8) + (z - 5) * (z - 5);
                        int crimDistSq = (x + 6) * (x + 6) + (z + 8) * (z + 8);

                        Block floor;
                        if (radioDistSq <= 18) {
                            floor = SandStormBlocks.RADIOTROPHIC_MYCELIUM;
                        } else if (warpDistSq <= 16) {
                            floor = Blocks.WARPED_NYLIUM;
                        } else if (crimDistSq <= 14) {
                            floor = Blocks.CRIMSON_NYLIUM;
                        } else {
                            floor = Blocks.MYCELIUM;
                        }
                        level.setBlock(p, floor.defaultBlockState(), 2);

                        if (floor == SandStormBlocks.RADIOTROPHIC_MYCELIUM) {
                            if ((x * 7 + z * 11) % 5 == 0) {
                                setSafeFlora(level, p.above(), SandStormBlocks.CHITINOLYTIC_FUNGUS.defaultBlockState());
                            }
                        } else if (floor == Blocks.WARPED_NYLIUM) {
                            if ((x * 5 + z * 13) % 4 == 0) {
                                setSafeFlora(level, p.above(), Blocks.WARPED_FUNGUS.defaultBlockState());
                            }
                        } else if (floor == Blocks.CRIMSON_NYLIUM) {
                            if ((x * 11 + z * 3) % 4 == 0) {
                                setSafeFlora(level, p.above(), Blocks.CRIMSON_FUNGUS.defaultBlockState());
                            }
                        } else {
                            int decorHash = Math.abs(x * 17 + z * 7) % 13;
                            if (decorHash == 1) {
                                setSafeFlora(level, p.above(), Blocks.BROWN_MUSHROOM.defaultBlockState());
                            } else if (decorHash == 2) {
                                setSafeFlora(level, p.above(), Blocks.RED_MUSHROOM.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }

        buildRedMushroomFeature(level, domeCenter.offset(7, 1, 6));
        buildBrownMushroomFeature(level, domeCenter.offset(-8, 1, 5));
        buildWarpedMushroomFeature(level, domeCenter.offset(-6, 1, -7));

        BlockPos glowingNode = domeCenter.offset(11, 1, -7);
        level.setBlock(glowingNode, Blocks.MUSHROOM_STEM.defaultBlockState(), 2);
        level.setBlock(glowingNode.above(), Blocks.MUSHROOM_STEM.defaultBlockState(), 2);
        level.setBlock(glowingNode.above(2), Blocks.SHROOMLIGHT.defaultBlockState(), 2);

        BlockPos terraformerPos = domeCenter.above();
        level.setBlock(terraformerPos, SandStormBlocks.ATMOSPHERIC_TERRAFORMER.defaultBlockState(), 3);
        if (level.getBlockEntity(terraformerPos) instanceof AtmosphericTerraformerBlockEntity terraformer) {
            terraformer.setupTier(1);
            terraformer.setItem(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE, new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_FUNGAL));
        }

        BlockPos incubatorPos = domeCenter.offset(-3, 1, 3);
        level.setBlock(incubatorPos, SandStormBlocks.AMNIOTIC_INCUBATOR.defaultBlockState(), 3);
        if (level.getBlockEntity(incubatorPos) instanceof AmnioticIncubatorBlockEntity incubator) {
            incubator.getEnergyStorage().receiveEnergy(250000L);
            incubator.setWaterAmount(10000);
            incubator.setBiomassUnits(32);
            incubator.setSelectedSpecies("cow");
            incubator.setAutoEcologicalMode(true);
        }

        spawnHologram(level, domeCenter.getX() + 0.5, baseY + 3.4, domeCenter.getZ() + 0.5, "§d[Biosfera Xeno-Fúngica - Beta]");
        spawnHologram(level, domeCenter.getX() + 0.5, baseY + 3.1, domeCenter.getZ() + 0.5, "§7Micélio Radiotrófico & Esporos");

        spawnBabyAnimal(level, domeCenter.getX() - 6.5, baseY + 1.0, domeCenter.getZ() - 6.5, "mooshroom");
        spawnBabyAnimal(level, domeCenter.getX() + 6.5, baseY + 1.0, domeCenter.getZ() + 6.5, "frog");
    }

    private static void buildRedMushroomFeature(ServerLevel level, BlockPos pos) {
        for (int ty = 0; ty < 5; ty++) {
            level.setBlock(pos.above(ty), Blocks.MUSHROOM_STEM.defaultBlockState(), 2);
        }
        level.setBlock(pos.above(4), Blocks.SHROOMLIGHT.defaultBlockState(), 2);
        for (int mx = -2; mx <= 2; mx++) {
            for (int mz = -2; mz <= 2; mz++) {
                if (Math.abs(mx) + Math.abs(mz) <= 3) {
                    level.setBlock(pos.offset(mx, 5, mz), Blocks.RED_MUSHROOM_BLOCK.defaultBlockState(), 2);
                }
            }
        }
        level.setBlock(pos.above(6), Blocks.RED_MUSHROOM_BLOCK.defaultBlockState(), 2);
    }

    private static void buildBrownMushroomFeature(ServerLevel level, BlockPos pos) {
        for (int ty = 0; ty < 4; ty++) {
            level.setBlock(pos.above(ty), Blocks.MUSHROOM_STEM.defaultBlockState(), 2);
        }
        level.setBlock(pos.above(3), Blocks.SHROOMLIGHT.defaultBlockState(), 2);
        for (int mx = -2; mx <= 2; mx++) {
            for (int mz = -2; mz <= 2; mz++) {
                level.setBlock(pos.offset(mx, 4, mz), Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState(), 2);
            }
        }
    }

    private static void buildWarpedMushroomFeature(ServerLevel level, BlockPos pos) {
        for (int ty = 0; ty < 5; ty++) {
            level.setBlock(pos.above(ty), Blocks.WARPED_STEM.defaultBlockState(), 2);
        }
        level.setBlock(pos.above(4), Blocks.SHROOMLIGHT.defaultBlockState(), 2);
        for (int mx = -1; mx <= 1; mx++) {
            for (int mz = -1; mz <= 1; mz++) {
                level.setBlock(pos.offset(mx, 5, mz), Blocks.WARPED_WART_BLOCK.defaultBlockState(), 2);
            }
        }
    }

    private static void buildPrimordialOasisDome(ServerLevel level, BlockPos domeCenter) {
        int radius = AtmosphericTerraformerBlockEntity.TIER1_RADIUS;
        int baseY = domeCenter.getY();

        for (int x = -radius - 1; x <= radius + 1; x++) {
            for (int z = -radius - 1; z <= radius + 1; z++) {
                if (x * x + z * z <= (radius + 1) * (radius + 1)) {
                    for (int y = -2; y <= 14; y++) {
                        BlockPos p = domeCenter.offset(x, y, z);
                        if (!level.getBlockState(p).isAir()) {
                            level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int distSq = x * x + z * z;
                if (distSq <= radius * radius) {
                    BlockPos p = domeCenter.offset(x, 0, z);
                    level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                    boolean edge = distSq > (radius - 1) * (radius - 1);
                    if (edge) {
                        boolean entrance = Math.abs(z) <= 1;
                        level.setBlock(p, Blocks.MOSS_BLOCK.defaultBlockState(), 2);
                        if (!entrance) {
                            level.setBlock(p.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                            level.setBlock(p.above(2), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                        } else {
                            level.setBlock(p.above(3), Blocks.MOSS_BLOCK.defaultBlockState(), 2);
                        }
                    } else {
                        int p1DistSq = (x + 7) * (x + 7) + (z + 6) * (z + 6);
                        int p2DistSq = (x + 10) * (x + 10) + (z + 3) * (z + 3);

                        if (p1DistSq <= 10 || p2DistSq <= 6) {
                            level.setBlock(p.below(), Blocks.SEA_LANTERN.defaultBlockState(), 2);
                            level.setBlock(p, Blocks.WATER.defaultBlockState(), 2);
                            if ((x + z) % 3 == 0) {
                                setSafeFlora(level, p.above(), Blocks.LILY_PAD.defaultBlockState());
                            }
                        } else if (p1DistSq <= 16 || p2DistSq <= 11) {
                            Block shoreBlock = x > -8 ? Blocks.SAND : Blocks.MOSS_BLOCK;
                            level.setBlock(p, shoreBlock.defaultBlockState(), 2);
                            if (shoreBlock == Blocks.MOSS_BLOCK) {
                                if ((x + z) % 4 == 0) {
                                    setSafeFlora(level, p.above(), Blocks.FLOWERING_AZALEA.defaultBlockState());
                                } else if ((x + z) % 5 == 0) {
                                    setSafeFlora(level, p.above(), Blocks.MOSS_CARPET.defaultBlockState());
                                }
                            }
                        } else {
                            int xenoDistSq = (x - 7) * (x - 7) + (z - 7) * (z - 7);
                            int palmRoot1 = (x - 11) * (x - 11) + (z + 7) * (z + 7);
                            int palmRoot2 = (x + 11) * (x + 11) + (z - 8) * (z - 8);

                            Block floor;
                            if (xenoDistSq <= 20) {
                                floor = SandStormBlocks.XENO_GRASS_BLOCK;
                            } else if (palmRoot1 <= 6 || palmRoot2 <= 6) {
                                floor = Blocks.PODZOL;
                            } else {
                                floor = Blocks.GRASS_BLOCK;
                            }
                            level.setBlock(p, floor.defaultBlockState(), 2);

                            if (floor == SandStormBlocks.XENO_GRASS_BLOCK) {
                                if ((x * 7 + z * 13) % 7 == 1) {
                                    setSafeFlora(level, p.above(), Blocks.FLOWERING_AZALEA.defaultBlockState());
                                } else if ((x * 7 + z * 13) % 7 == 2) {
                                    setSafeFlora(level, p.above(), Blocks.POPPY.defaultBlockState());
                                }
                            } else if (floor == Blocks.GRASS_BLOCK && (Math.abs(x) > 2 || Math.abs(z) > 2)) {
                                int decorHash = Math.abs(x * 23 + z * 11) % 13;
                                if (decorHash == 1) {
                                    setSafeFlora(level, p.above(), Blocks.POPPY.defaultBlockState());
                                } else if (decorHash == 2) {
                                    setSafeFlora(level, p.above(), Blocks.CORNFLOWER.defaultBlockState());
                                } else if (decorHash == 3) {
                                    setSafeFlora(level, p.above(), Blocks.FERN.defaultBlockState());
                                } else if (decorHash == 4) {
                                    setSafeFlora(level, p.above(), Blocks.DANDELION.defaultBlockState());
                                } else if (decorHash == 5) {
                                    setSafeFlora(level, p.above(), Blocks.AZURE_BLUET.defaultBlockState());
                                } else if (decorHash == 6 || decorHash == 7) {
                                    setSafeFlora(level, p.above(), Blocks.SHORT_GRASS.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }

        buildJunglePalmFeature(level, domeCenter.offset(11, 1, -7), 7);
        buildJunglePalmFeature(level, domeCenter.offset(-4, 1, -10), 5);
        buildJunglePalmFeature(level, domeCenter.offset(-11, 1, 8), 6);

        BlockPos companionPalm = domeCenter.offset(-10, 1, 9);
        for (int ty = 0; ty < 3; ty++) {
            level.setBlock(companionPalm.above(ty), Blocks.JUNGLE_LOG.defaultBlockState(), 2);
        }
        level.setBlock(companionPalm.above(3), Blocks.JUNGLE_LEAVES.defaultBlockState(), 2);

        for (int bz = -5; bz <= -2; bz++) {
            BlockPos bambooPos = domeCenter.offset(-4, 1, bz);
            int bambooH = Math.abs(bz * 3) % 4 + 2;
            for (int by = 0; by < bambooH; by++) {
                setSafeFlora(level, bambooPos.above(by), Blocks.BAMBOO.defaultBlockState());
            }
        }

        BlockPos terraformerPos = domeCenter.above();
        level.setBlock(terraformerPos, SandStormBlocks.ATMOSPHERIC_TERRAFORMER.defaultBlockState(), 3);
        if (level.getBlockEntity(terraformerPos) instanceof AtmosphericTerraformerBlockEntity terraformer) {
            terraformer.setupTier(1);
            terraformer.setItem(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE, new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_OASIS));
        }

        BlockPos incubatorPos = domeCenter.offset(3, 1, -3);
        level.setBlock(incubatorPos, SandStormBlocks.AMNIOTIC_INCUBATOR.defaultBlockState(), 3);
        if (level.getBlockEntity(incubatorPos) instanceof AmnioticIncubatorBlockEntity incubator) {
            incubator.getEnergyStorage().receiveEnergy(250000L);
            incubator.setWaterAmount(10000);
            incubator.setBiomassUnits(32);
            incubator.setSelectedSpecies("cow");
            incubator.setAutoEcologicalMode(true);
        }

        spawnHologram(level, domeCenter.getX() + 0.5, baseY + 3.4, domeCenter.getZ() + 0.5, "§a[Oásis Primordial - Gamma]");
        spawnHologram(level, domeCenter.getX() + 0.5, baseY + 3.1, domeCenter.getZ() + 0.5, "§7Regeneração Biológica & Hidratação");

        spawnBabyAnimal(level, domeCenter.getX() + 6.5, baseY + 1.0, domeCenter.getZ() - 6.5, "cow");
        spawnBabyAnimal(level, domeCenter.getX() - 6.5, baseY + 1.0, domeCenter.getZ() + 6.5, "sheep");
        spawnBabyAnimal(level, domeCenter.getX() + 4.5, baseY + 1.0, domeCenter.getZ() + 6.5, "chicken");
    }

    private static void buildJunglePalmFeature(ServerLevel level, BlockPos pos, int height) {
        for (int ty = 0; ty < height; ty++) {
            level.setBlock(pos.above(ty), Blocks.JUNGLE_LOG.defaultBlockState(), 2);
        }
        for (int lx = -2; lx <= 2; lx++) {
            for (int lz = -2; lz <= 2; lz++) {
                if (Math.abs(lx) + Math.abs(lz) <= 3) {
                    level.setBlock(pos.offset(lx, height - 1, lz), Blocks.JUNGLE_LEAVES.defaultBlockState(), 2);
                }
            }
        }
        level.setBlock(pos.above(height), Blocks.JUNGLE_LEAVES.defaultBlockState(), 2);
        setSafeFlora(level, pos.offset(0, height - 2, -1), Blocks.MELON.defaultBlockState());
    }

    private static void buildMagneticForestDome(ServerLevel level, BlockPos domeCenter) {
        int radius = AtmosphericTerraformerBlockEntity.TIER1_RADIUS;
        int baseY = domeCenter.getY();

        for (int x = -radius - 1; x <= radius + 1; x++) {
            for (int z = -radius - 1; z <= radius + 1; z++) {
                if (x * x + z * z <= (radius + 1) * (radius + 1)) {
                    for (int y = -2; y <= 14; y++) {
                        BlockPos p = domeCenter.offset(x, y, z);
                        if (!level.getBlockState(p).isAir()) {
                            level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int distSq = x * x + z * z;
                if (distSq <= radius * radius) {
                    BlockPos p = domeCenter.offset(x, 0, z);
                    level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                    boolean edge = distSq > (radius - 1) * (radius - 1);
                    if (edge) {
                        boolean entrance = Math.abs(z) <= 1;
                        level.setBlock(p, Blocks.POLISHED_BASALT.defaultBlockState(), 2);
                        if (!entrance) {
                            level.setBlock(p.above(), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                            level.setBlock(p.above(2), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                        } else {
                            level.setBlock(p.above(3), Blocks.POLISHED_BASALT.defaultBlockState(), 2);
                        }
                    } else {
                        int fissureOffset = Math.abs(z - (int)(x * 0.7 - 1));
                        int geodeDistSq = (x + 7) * (x + 7) + (z - 7) * (z - 7);

                        Block floor;
                        if (fissureOffset <= 1) {
                            floor = SandStormBlocks.ELECTRIFIED_SAND;
                        } else if (geodeDistSq <= 9) {
                            floor = Blocks.AMETHYST_BLOCK;
                        } else if (geodeDistSq <= 14) {
                            floor = Blocks.CALCITE;
                        } else {
                            floor = Blocks.SMOOTH_BASALT;
                        }
                        level.setBlock(p, floor.defaultBlockState(), 2);

                        if (geodeDistSq <= 9) {
                            if ((x + z) % 3 == 0) {
                                setSafeFlora(level, p.above(), Blocks.AMETHYST_CLUSTER.defaultBlockState());
                            } else if ((x + z) % 3 == 1) {
                                setSafeFlora(level, p.above(), Blocks.SMALL_AMETHYST_BUD.defaultBlockState());
                            }
                        } else if (floor == Blocks.SMOOTH_BASALT) {
                            int decorHash = Math.abs(x * 17 + z * 13) % 11;
                            if (decorHash == 1) {
                                setSafeFlora(level, p.above(), Blocks.MEDIUM_AMETHYST_BUD.defaultBlockState());
                            } else if (decorHash == 2) {
                                setSafeFlora(level, p.above(), Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }

        buildBasaltColumn(level, domeCenter.offset(10, 1, -7), 6);
        buildBasaltColumn(level, domeCenter.offset(9, 1, -7), 4);
        buildBasaltColumn(level, domeCenter.offset(10, 1, -6), 5);
        buildBasaltColumn(level, domeCenter.offset(11, 1, -7), 3);
        buildBasaltColumn(level, domeCenter.offset(9, 1, -6), 2);
        buildBasaltColumn(level, domeCenter.offset(11, 1, -6), 4);

        BlockPos spirePos = domeCenter.offset(-8, 1, -9);
        for (int sy = 0; sy < 5; sy++) {
            level.setBlock(spirePos.above(sy), Blocks.POLISHED_BASALT.defaultBlockState(), 2);
        }
        level.setBlock(spirePos.above(5), Blocks.AMETHYST_BLOCK.defaultBlockState(), 2);
        setSafeFlora(level, spirePos.above(6), Blocks.AMETHYST_CLUSTER.defaultBlockState());

        BlockPos subSpire = domeCenter.offset(6, 1, 10);
        for (int sy = 0; sy < 3; sy++) {
            level.setBlock(subSpire.above(sy), Blocks.POLISHED_BASALT.defaultBlockState(), 2);
        }
        level.setBlock(subSpire.above(3), Blocks.AMETHYST_BLOCK.defaultBlockState(), 2);
        setSafeFlora(level, subSpire.above(4), Blocks.AMETHYST_CLUSTER.defaultBlockState());

        BlockPos terraformerPos = domeCenter.above();
        level.setBlock(terraformerPos, SandStormBlocks.ATMOSPHERIC_TERRAFORMER.defaultBlockState(), 3);
        if (level.getBlockEntity(terraformerPos) instanceof AtmosphericTerraformerBlockEntity terraformer) {
            terraformer.setupTier(1);
            terraformer.setItem(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE, new ItemStack(SandStormItems.BIOSPHERE_CARTRIDGE_MAGNETIC));
        }

        BlockPos incubatorPos = domeCenter.offset(3, 1, 3);
        level.setBlock(incubatorPos, SandStormBlocks.AMNIOTIC_INCUBATOR.defaultBlockState(), 3);
        if (level.getBlockEntity(incubatorPos) instanceof AmnioticIncubatorBlockEntity incubator) {
            incubator.getEnergyStorage().receiveEnergy(250000L);
            incubator.setWaterAmount(10000);
            incubator.setBiomassUnits(32);
            incubator.setSelectedSpecies("wolf");
            incubator.setAutoEcologicalMode(true);
        }

        spawnHologram(level, domeCenter.getX() + 0.5, baseY + 3.4, domeCenter.getZ() + 0.5, "§6[Floresta Magnética - Delta]");
        spawnHologram(level, domeCenter.getX() + 0.5, baseY + 3.1, domeCenter.getZ() + 0.5, "§7Piezoeletricidade & Eletromagnetismo");

        spawnBabyAnimal(level, domeCenter.getX() - 6.5, baseY + 1.0, domeCenter.getZ() + 6.5, "wolf");
    }

    private static void buildBasaltColumn(ServerLevel level, BlockPos pos, int height) {
        for (int cy = 0; cy < height; cy++) {
            level.setBlock(pos.above(cy), Blocks.POLISHED_BASALT.defaultBlockState(), 2);
        }
        level.setBlock(pos.above(height), Blocks.SMOOTH_BASALT.defaultBlockState(), 2);
        setSafeFlora(level, pos.above(height + 1), Blocks.IRON_BARS.defaultBlockState());
    }

    private static void setSafeFlora(ServerLevel level, BlockPos pos, BlockState flora) {
        if (level.getBlockState(pos).isAir() && flora.canSurvive(level, pos)) {
            level.setBlock(pos, flora, 2);
        }
    }

    @SuppressWarnings("unchecked")
    private static void spawnBabyAnimal(ServerLevel level, double x, double y, double z, String entityName) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(SandStormMod.mcId(entityName));
        if (type != null) {
            Entity entity = type.create(level, EntitySpawnReason.COMMAND);
            if (entity instanceof Animal animal) {
                animal.setBaby(true);
                animal.setPos(x, y, z);
                animal.setPersistenceRequired();
                level.addFreshEntity(animal);
            }
        }
    }

    private static void buildUndergroundDungeonNetwork(ServerLevel level, BlockPos center) {
        for (int z = -5; z >= -9; z--) {
            for (int x = -6; x <= 6; x++) {
                BlockPos p = center.offset(x, 0, z);
                boolean edge = (Math.abs(x) == 6 || z == -9);
                boolean light = (x % 3 == 0 && (z == -6 || z == -9));
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
        for (int z = 4; z <= 6; z++) {
            for (int x = -8; x <= -4; x++) {
                BlockPos p = center.offset(x, 0, z);
                level.setBlock(p, Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
            }
            for (int x = 4; x <= 8; x++) {
                BlockPos p = center.offset(x, 0, z);
                level.setBlock(p, Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
            }
        }

        BlockPos westOrigin = center.offset(-68, -40, 12);
        buildFoundationPlate(level, westOrigin.getX() - 2, westOrigin.getX() + 58, westOrigin.getZ() - 2, westOrigin.getZ() + 58, westOrigin.getY() - 40, 3);
        MillennialRepositoryDungeon.generate(level, westOrigin, 6, 8, 8);
        buildRoofCap(level, westOrigin.getX(), westOrigin.getX() + 56, westOrigin.getZ(), westOrigin.getZ() + 56, westOrigin.getY() + 5);
        BlockPos westEntry = westOrigin.offset(4 * 7 + 3, 1, 3);
        BlockPos westReturn = westOrigin.offset(4 * 7 + 3, 1, 1);
        setupReturnPad(level, westReturn);

        BlockPos eastOrigin = center.offset(14, -40, 12);
        buildFoundationPlate(level, eastOrigin.getX() - 2, eastOrigin.getX() + 58, eastOrigin.getZ() - 2, eastOrigin.getZ() + 58, eastOrigin.getY() - 1, 3);
        AncientBunkerComplexDungeon.generate(level, eastOrigin, 8, 8);
        buildRoofCap(level, eastOrigin.getX(), eastOrigin.getX() + 56, eastOrigin.getZ(), eastOrigin.getZ() + 56, eastOrigin.getY() + 5);
        BlockPos eastEntry = eastOrigin.offset(4 * 7 + 3, 1, 3);
        BlockPos eastReturn = eastOrigin.offset(4 * 7 + 3, 1, 1);
        setupReturnPad(level, eastReturn);

        BlockPos southOrigin = center.offset(-24, -45, -70);
        buildFoundationPlate(level, southOrigin.getX() - 2, southOrigin.getX() + 51, southOrigin.getZ() - 2, southOrigin.getZ() + 51, southOrigin.getY() - 24, 3);
        MillennialRepositoryDungeon.generate(level, southOrigin, 4, 7, 7);
        buildRoofCap(level, southOrigin.getX(), southOrigin.getX() + 49, southOrigin.getZ(), southOrigin.getZ() + 49, southOrigin.getY() + 5);
        BlockPos southEntry = southOrigin.offset(3 * 7 + 3, 1, 3);
        BlockPos southReturn = southOrigin.offset(3 * 7 + 3, 1, 1);
        setupReturnPad(level, southReturn);

        BlockPos northOrigin = center.offset(-24, -45, 84);
        buildFoundationPlate(level, northOrigin.getX() - 2, northOrigin.getX() + 51, northOrigin.getZ() - 2, northOrigin.getZ() + 51, northOrigin.getY() - 24, 3);
        AbandonedMilitaryLabDungeon.generate(level, northOrigin, 4, 7, 7);
        buildRoofCap(level, northOrigin.getX(), northOrigin.getX() + 49, northOrigin.getZ(), northOrigin.getZ() + 49, northOrigin.getY() + 5);
        BlockPos northEntry = northOrigin.offset(3 * 7 + 3, 1, 3);
        BlockPos northReturn = northOrigin.offset(3 * 7 + 3, 1, 1);
        setupReturnPad(level, northReturn);

        BlockPos hiveOrigin = center.offset(-96, -45, -70);
        buildFoundationPlate(level, hiveOrigin.getX() - 2, hiveOrigin.getX() + 38, hiveOrigin.getZ() - 2, hiveOrigin.getZ() + 38, hiveOrigin.getY() - 24, 3);
        AbandonedExcavationHiveDungeon.generate(level, hiveOrigin, 3, 5, 5);
        buildRoofCap(level, hiveOrigin.getX(), hiveOrigin.getX() + 36, hiveOrigin.getZ(), hiveOrigin.getZ() + 36, hiveOrigin.getY() + 5);
        BlockPos hiveEntry = hiveOrigin.offset(2 * 7 + 3, 1, 3);
        BlockPos hiveReturn = hiveOrigin.offset(2 * 7 + 3, 1, 1);
        setupReturnPad(level, hiveReturn);

        BlockPos westShaftTop = center.offset(-8, 0, 5);
        buildGlassObservationTube(level, westShaftTop, westEntry.getY());
        buildCatwalk(level, new BlockPos(westShaftTop.getX(), westEntry.getY(), westShaftTop.getZ()), westEntry.north());
        level.setBlock(westEntry.north(), Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(westEntry.north().above(), Blocks.AIR.defaultBlockState(), 2);
        setupShaftTrigger(level, westShaftTop, westEntry, "Megadungeon Oeste (6 Andares, 8x8)", "6 Andares | Grade 8x8 | 384 Salas");

        BlockPos eastShaftTop = center.offset(8, 0, 5);
        buildGlassObservationTube(level, eastShaftTop, eastEntry.getY());
        buildCatwalk(level, new BlockPos(eastShaftTop.getX(), eastEntry.getY(), eastShaftTop.getZ()), eastEntry.north());
        level.setBlock(eastEntry.north(), Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(eastEntry.north().above(), Blocks.AIR.defaultBlockState(), 2);
        setupShaftTrigger(level, eastShaftTop, eastEntry, "Complexo Bunker Leste (8x8)", "1 Andar Colossal | Grade 8x8 | 64 Salas");

        BlockPos cryoOrigin = center.offset(48, -45, -70);
        buildFoundationPlate(level, cryoOrigin.getX() - 2, cryoOrigin.getX() + 38, cryoOrigin.getZ() - 2, cryoOrigin.getZ() + 38, cryoOrigin.getY() - 24, 3);
        SubglacialCryoVaultDungeon.generate(level, cryoOrigin, 3, 5, 5);
        buildRoofCap(level, cryoOrigin.getX(), cryoOrigin.getX() + 36, cryoOrigin.getZ(), cryoOrigin.getZ() + 36, cryoOrigin.getY() + 5);
        BlockPos cryoEntry = cryoOrigin.offset(2 * 7 + 3, 1, 3);
        BlockPos cryoReturn = cryoOrigin.offset(2 * 7 + 3, 1, 1);
        setupReturnPad(level, cryoReturn);
    }

    private static void setupReturnPad(ServerLevel level, BlockPos pos) {
        level.setBlock(pos.below(), SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState(), 3);
        level.setBlock(pos, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.defaultBlockState(), 3);

        BlockPos post = pos.north();
        level.setBlock(post, Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(post.above(), Blocks.VERDANT_FROGLIGHT.defaultBlockState(), 3);

        spawnHologram(level, pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5, "§a§l▲ RETORNAR AO SHOWCASE ▲");
        spawnHologram(level, pos.getX() + 0.5, pos.getY() + 1.7, pos.getZ() + 0.5, "§7Pise para voltar à Praça Central");
        TELEPORT_PADS.add(new ShowcaseTeleportPad(pos, new BlockPos(0, 161, 0), "Praça Central do Showcase"));
    }

    private static void setupShaftTrigger(ServerLevel level, BlockPos top, BlockPos destination, String title, String subtitle) {
        level.setBlock(top, SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState(), 3);
        level.setBlock(top.above(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.defaultBlockState(), 3);

        BlockPos post = top.north();
        level.setBlock(post, Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(post.above(), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 3);

        spawnHologram(level, top.getX() + 0.5, top.getY() + 2.5, top.getZ() + 0.5, "§6§l▼ " + title + " ▼");
        spawnHologram(level, top.getX() + 0.5, top.getY() + 2.2, top.getZ() + 0.5, "§7" + subtitle);
        spawnHologram(level, top.getX() + 0.5, top.getY() + 1.9, top.getZ() + 0.5, "§ePise para teleporte ou use o tubo");
        TELEPORT_PADS.add(new ShowcaseTeleportPad(top.above(), destination, title));
    }

    private static void buildGlassObservationTube(ServerLevel level, BlockPos top, int bottomY) {
        for (int y = bottomY; y <= top.getY(); y++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos p = new BlockPos(top.getX() + dx, y, top.getZ() + dz);
                    if (dx == 0 && dz == 0) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    } else if (dx == 0 && dz == -1) {
                        level.setBlock(p, SandStormBlocks.REINFORCED_CONCRETE.defaultBlockState(), 2);
                    } else {
                        level.setBlock(p, SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                    }
                }
            }
            level.setBlock(new BlockPos(top.getX(), y, top.getZ()), Blocks.LADDER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 2);
        }
    }

    private static void buildCatwalk(ServerLevel level, BlockPos start, BlockPos end) {
        int x1 = Math.min(start.getX(), end.getX());
        int x2 = Math.max(start.getX(), end.getX());
        int z1 = Math.min(start.getZ(), end.getZ());
        int z2 = Math.max(start.getZ(), end.getZ());
        int y = start.getY();

        for (int x = x1; x <= x2; x++) {
            level.setBlock(new BlockPos(x, y - 1, start.getZ()), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
            level.setBlock(new BlockPos(x, y, start.getZ()), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(new BlockPos(x, y + 1, start.getZ()), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(new BlockPos(x, y + 2, start.getZ()), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
            level.setBlock(new BlockPos(x, y, start.getZ() - 1), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
            level.setBlock(new BlockPos(x, y, start.getZ() + 1), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        }

        for (int z = z1; z <= z2; z++) {
            level.setBlock(new BlockPos(end.getX(), y - 1, z), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
            level.setBlock(new BlockPos(end.getX(), y, z), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(new BlockPos(end.getX(), y + 1, z), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(new BlockPos(end.getX(), y + 2, z), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
            level.setBlock(new BlockPos(end.getX() - 1, y, z), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
            level.setBlock(new BlockPos(end.getX() + 1, y, z), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        }
    }

    private static void buildFoundationPlate(ServerLevel level, int minX, int maxX, int minZ, int maxZ, int topY, int thickness) {
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int ty = 0; ty < thickness; ty++) {
                    int y = topY - ty;
                    Block block = (ty == 0) ? Blocks.DEEPSLATE_BRICKS : ((x + z) % 3 == 0 ? SandStormBlocks.PIEZO_QUARTZ_BLOCK : Blocks.SMOOTH_BASALT);
                    level.setBlock(new BlockPos(x, y, z), block.defaultBlockState(), 2);
                }
            }
        }
    }

    private static void buildRoofCap(ServerLevel level, int minX, int maxX, int minZ, int maxZ, int topY) {
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockPos p = new BlockPos(x, topY, z);
                if (level.getBlockState(p).isAir()) {
                    BlockState roofState = ((x + z) % 4 == 0)
                            ? SandStormBlocks.WEATHERED_REINFORCED_CONCRETE.defaultBlockState()
                            : SandStormBlocks.REINFORCED_CONCRETE.defaultBlockState();
                    level.setBlock(p, roofState, 2);
                }
            }
        }
    }

    public static int getCheckerboardMaxZ() {
        List<Block> blocks = BuiltInRegistries.BLOCK.stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(SandStormMod.MOD_ID))
                .filter(b -> !b.equals(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR) && !b.equals(SandStormBlocks.VAULT_DOOR_DUMMY))
                .toList();
        int cols = 8;
        int spacing = 5;
        int startZ = CHECKERBOARD_START_Z;
        int totalRows = (blocks.size() + cols - 1) / cols;
        return startZ + (totalRows * spacing) + 4;
    }

    public static int executeDungeonTeleportCommand(CommandContext<CommandSourceStack> ctx, String sector) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = source.getLevel();
            BlockPos center = new BlockPos(0, 160, 0);
            String norm = (sector != null) ? sector.toLowerCase(Locale.ROOT) : "west";
            BlockPos dest = switch (norm) {
                case "east", "bunker", "leste", "single" -> center.offset(14 + 4 * 7 + 3, -39, 12 + 3);
                case "south", "sul" -> center.offset(-24 + 3 * 7 + 3, -44, -70 + 3);
                case "north", "norte", "military_lab", "military", "lab", "militar" -> center.offset(-24 + 3 * 7 + 3, -44, 84 + 3);
                case "cryo", "cryo_vault", "vault_cryo", "gelo" -> center.offset(48 + 2 * 7 + 3, -44, -70 + 3);
                case "hive", "mineracao", "mineração", "escavacao", "escavação" -> center.offset(-96 + 2 * 7 + 3, -44, -70 + 3);
                default -> center.offset(-68 + 4 * 7 + 3, -39, 12 + 3);
            };
            player.teleportTo(level, dest.getX() + 0.5, dest.getY() + 0.1, dest.getZ() + 0.5, Set.of(), 0.0f, 0.0f, true);
            level.playSound(null, player.blockPosition(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.2f);
            String label = switch (norm) {
                case "military_lab", "military", "lab", "militar", "north", "norte" -> "Laboratório Militar Abandonado (Setor Norte)";
                case "cryo", "cryo_vault", "vault_cryo", "gelo" -> "Cofre Criogênico Subglacial";
                case "hive", "mineracao", "mineração", "escavacao", "escavação" -> "Complexo de Mineração Infestado";
                default -> "Subsolo do Showcase (" + norm + ")";
            };
            source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "§6[SandStorm]§a Teleportado para: §f%s §7em [%d, %d, %d]!", label, dest.getX(), dest.getY(), dest.getZ())), true);
            return 1;
        }
        source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
        return 0;
    }

    private static CompletableFuture<Suggestions> suggestDungeonSectors(SuggestionsBuilder builder) {
        builder.suggest("west");
        builder.suggest("east");
        builder.suggest("south");
        builder.suggest("north");
        builder.suggest("military_lab");
        builder.suggest("cryo_vault");
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestCinematicRoutes(SuggestionsBuilder builder) {
        builder.suggest("all");
        builder.suggest("dungeon");
        builder.suggest("farming");
        builder.suggest("terraforming");
        builder.suggest("stop");
        return builder.buildFuture();
    }
}

