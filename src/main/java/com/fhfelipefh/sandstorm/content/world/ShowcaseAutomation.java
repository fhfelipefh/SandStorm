package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.block.BiosphereDoorBlock;
import com.fhfelipefh.sandstorm.content.block.SandMaglevRailBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SolidStateAccumulatorBlock;
import com.fhfelipefh.sandstorm.content.block.VaultDoorControllerBlock;
import com.fhfelipefh.sandstorm.content.block.VaultDoorDummyBlock;
import com.fhfelipefh.sandstorm.content.block.VaultWallPanelBlock;
import com.fhfelipefh.sandstorm.content.block.VerticalConnection;
import com.fhfelipefh.sandstorm.content.block.entity.AmnioticIncubatorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericTerraformerBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.CryogenicAtmosphericChillerBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.MegastructureConstructorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SolidStateAccumulatorBlockEntity;
import com.fhfelipefh.sandstorm.content.entity.BuilderDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CargoDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.entity.SandboardEntity;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgBuilderEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgExcavatorEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgHarvesterEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.storage.QuantumAccessTerminalBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskDriveBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskDriveBlockEntity;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskStorage;
import com.fhfelipefh.sandstorm.content.storage.QuantumNetworkControllerBlock;
import com.fhfelipefh.sandstorm.content.storage.QuantumNetworkControllerBlockEntity;
import com.fhfelipefh.sandstorm.content.storage.StoredItemEntry;
import com.fhfelipefh.sandstorm.content.world.dungeon.AbandonedMilitaryLabDungeon;
import com.fhfelipefh.sandstorm.content.world.dungeon.AncientBunkerComplexDungeon;
import com.fhfelipefh.sandstorm.content.world.dungeon.MillennialRepositoryDungeon;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SignTextSlot;
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

    private static final Set<UUID> GENERATED_PLAYERS = new HashSet<>();
    private static final List<ShowcaseTeleportPad> TELEPORT_PADS = new ArrayList<>();
    private static final Map<UUID, Long> TELEPORT_COOLDOWNS = new HashMap<>();
    private static BlockPos regeneratingRockPos = null;
    private static CyborgExcavatorEntity loopMiner = null;
    private static boolean showcaseActive = false;

    private record ShowcaseTeleportPad(BlockPos triggerPos, BlockPos destination, String label) {}

    public static boolean isShowcaseActive() {
        return showcaseActive;
    }

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            String levelName = player.level().getServer().getWorldData().getLevelName();
            if (levelName != null && levelName.toLowerCase().contains("showcase")) {
                if (!GENERATED_PLAYERS.contains(player.getUUID())) {
                    GENERATED_PLAYERS.add(player.getUUID());
                    buildShowcase((ServerLevel) player.level(), player);
                }
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long tick = server.getTickCount();
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
                        if (distX <= 1.0 && distZ <= 1.0 && distY <= 1.5) {
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
                .then(Commands.literal("military_lab")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, "military_lab"))
                )
                .then(Commands.literal("dungeon")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, "west"))
                        .then(Commands.argument("sector", StringArgumentType.word())
                                .suggests((c, b) -> suggestDungeonSectors(b))
                                .executes(ctx -> executeDungeonTeleportCommand(ctx, StringArgumentType.getString(ctx, "sector")))
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
                .then(Commands.literal("military_lab")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, "military_lab"))
                )
                .then(Commands.literal("dungeon")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, "west"))
                        .then(Commands.argument("sector", StringArgumentType.word())
                                .suggests((c, b) -> suggestDungeonSectors(b))
                                .executes(ctx -> executeDungeonTeleportCommand(ctx, StringArgumentType.getString(ctx, "sector")))
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

        dispatcher.register(Commands.literal("sandstorm_showcase_dungeon")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeDungeonTeleportCommand(ctx, "west"))
                .then(Commands.argument("sector", StringArgumentType.word())
                        .suggests((c, b) -> suggestDungeonSectors(b))
                        .executes(ctx -> executeDungeonTeleportCommand(ctx, StringArgumentType.getString(ctx, "sector")))
                )
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
            BlockPos targetPos = BlockPos.containing(player.position());
            buildQuantumComputerStation(source.getLevel(), targetPos);
            source.sendSuccess(() -> Component.literal("§6[SandStorm]§a Computador Quântico completo e energizado montado com sucesso!"), true);
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
                BlockPos targetPos = new BlockPos(28, 160, -14);
                player.teleportTo(level, targetPos.getX() + 0.5, targetPos.getY() + 1.0, targetPos.getZ() + 8.0, Set.of(), 180.0f, 0.0f, true);
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

    public static void buildShowcase(ServerLevel level, ServerPlayer player) {
        showcaseActive = true;
        TELEPORT_PADS.clear();
        BlockPos center = new BlockPos(0, 160, 0);
        int frontZ = getBiosphereFrontZ();
        int backZ = frontZ + 60;
        int maxZ = backZ + AtmosphericTerraformerBlockEntity.TIER1_RADIUS + 20;

        AABB showcaseArea = new AABB(center.getX() - 140, center.getY() - 100, center.getZ() - 140, center.getX() + 140, center.getY() + 120, center.getZ() + maxZ);
        List<Entity> oldEntities = level.getEntitiesOfClass(Entity.class, showcaseArea, e -> !(e instanceof ServerPlayer));
        for (Entity old : oldEntities) {
            old.discard();
        }

        clearUnderneath(level, center, maxZ);
        buildCentralPlaza(level, center);
        buildBlockCheckerboard(level, center);
        buildItemWing(level, center);
        buildEntityWing(level, center);
        buildMegastructureDomeShowcase(level, center);
        buildMaglevTestTrack(level, center);
        buildAutonomousCyborgFacility(level, center, player);
        buildQuantumComputerStation(level, center.offset(16, 0, 0));
        buildQuantumComputerWalkway(level, center);
        buildVaultShowcase(level, center.offset(28, 0, -14));
        buildVaultWalkway(level, center);
        buildBiospheresShowcase(level, center);
        buildUndergroundDungeonNetwork(level, center);

        player.setGameMode(GameType.CREATIVE);
        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;
        player.onUpdateAbilities();
        player.teleportTo(level, 0.5, 162.0, 0.5, Set.of(), 0.0f, 0.0f, true);

        level.getServer().getGameRules().set(GameRules.ADVANCE_WEATHER, false, level.getServer());
        level.getServer().getGameRules().set(GameRules.ADVANCE_TIME, false, level.getServer());
        level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack(), "time set noon");
        level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack(), "weather clear");
    }

    private static void clearUnderneath(ServerLevel level, BlockPos center, int maxZ) {
        for (int x = -70; x <= 70; x++) {
            for (int z = -90; z <= maxZ; z++) {
                for (int y = -64; y < center.getY(); y++) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (!level.getBlockState(p).isAir()) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private static void buildCentralPlaza(ServerLevel level, BlockPos center) {
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                BlockPos pos = center.offset(x, 0, z);
                boolean edge = Math.abs(x) == 4 || Math.abs(z) == 4;
                boolean corner = Math.abs(x) == 4 && Math.abs(z) == 4;
                Block floorBlock = corner ? Blocks.OCHRE_FROGLIGHT : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                level.setBlock(pos, floorBlock.defaultBlockState(), 3);
            }
        }

        BlockPos chestPos = center.above();
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
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

        AABB clearBox = new AABB(center).inflate(8.0);
        List<ArmorStand> existingStands = level.getEntitiesOfClass(ArmorStand.class, clearBox);
        for (ArmorStand stand : existingStands) {
            stand.discard();
        }

        spawnMannequin(level, center.offset(-2, 1, 0), "§bTraje Espacial Completo",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE),
                new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS), new ItemStack(SandStormItems.SPACE_SUIT_BOOTS),
                new ItemStack(SandStormItems.VIBRO_CRYSKNIFE), new ItemStack(SandStormItems.SURVIVAL_DATAPAD));

        spawnMannequin(level, center.offset(2, 1, 0), "§dArsenal de Plasma & Som",
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(SandStormItems.PLASMA_RIFLE), new ItemStack(SandStormItems.SONIC_CANNON));

        spawnMannequin(level, center.offset(0, 1, -2), "§6Canhão Pesado & Radar",
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(SandStormItems.HEAVY_PLASMA_CANNON), new ItemStack(SandStormItems.ANOMALY_RADAR));

        spawnMannequin(level, center.offset(0, 1, 2), "§aKit de Voo & Propulsão",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(Items.ELYTRA),
                ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(SandStormItems.SUIT_UPGRADE_JETPACK), new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE));
    }

    private static void buildBlockCheckerboard(ServerLevel level, BlockPos center) {
        List<Block> blocks = BuiltInRegistries.BLOCK.stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(SandStormMod.MOD_ID))
                .filter(b -> !b.equals(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR) && !b.equals(SandStormBlocks.VAULT_DOOR_DUMMY))
                .sorted(Comparator.comparing(b -> BuiltInRegistries.BLOCK.getKey(b).toString()))
                .toList();

        int cols = 8;
        int spacing = 4;
        int startZ = 8;
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
            } else {
                level.setBlock(targetPos, targetBlock.defaultBlockState(), 3);
            }
            spawnBlockHologram(level, targetPos, targetBlock);
            placeIdentificationSign(level, pedestalCenter.offset(0, 0, -2), targetBlock);
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

    private static void placeIdentificationSign(ServerLevel level, BlockPos signPos, Block block) {
        level.setBlock(signPos, Blocks.OAK_WALL_SIGN.defaultBlockState().setValue(WallSignBlock.FACING, Direction.NORTH), 3);
        if (level.getBlockEntity(signPos) instanceof SignBlockEntity signBe) {
            String name = block.getName().getString();
            String role = getBlockExplanation(block);
            String l0 = name.length() > 15 ? name.substring(0, 15) : name;
            String l1 = role.length() > 15 ? role.substring(0, 15) : role;
            String l2 = role.length() > 15 ? (role.length() > 30 ? role.substring(15, 30) : role.substring(15)) : "";
            SignText text = signBe.getText(SignTextSlot.FRONT).asMutable()
                    .setLine(0, Component.literal("§6§l" + l0))
                    .setLine(1, Component.literal("§f" + l1))
                    .setLine(2, Component.literal("§7" + l2))
                    .setLine(3, Component.literal("§b[SandStorm]"))
                    .asImmutable();
            signBe.setText(text, SignTextSlot.FRONT);
            signBe.setChanged();
        }
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
        if (block == SandStormBlocks.SMART_FLUID_PIPE) return "Duto Inteligente de Fluidos e Gases";
        return "Tecnologia Estrutural SandStorm";
    }

    private static void buildMaglevTestTrack(ServerLevel level, BlockPos center) {
        int baseY = center.getY();

        for (int x = -5; x >= -38; x--) {
            for (int z = 9; z <= 11; z++) {
                BlockPos bp = new BlockPos(x, baseY, z);
                Block b = (z != 10) ? Blocks.CUT_SANDSTONE : (Math.abs(x) % 6 == 0 ? Blocks.SEA_LANTERN : Blocks.SMOOTH_SANDSTONE);
                level.setBlock(bp, b.defaultBlockState(), 2);
            }
        }

        int xMin = -58;
        int xMax = -38;
        int zMin = -2;
        int zMax = 22;

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

        BlockPos chestPos = new BlockPos(xMax + 1, baseY + 1, 10);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.SANDBOARD, 1));
            chest.setItem(1, new ItemStack(Items.MINECART, 4));
            chest.setItem(2, new ItemStack(Items.CHEST_MINECART, 2));
            chest.setItem(3, new ItemStack(SandStormBlocks.SAND_MAGLEV_RAIL, 64));
        }

        Minecart cart = new Minecart(EntityTypes.MINECART, level);
        cart.setPos(xMin + 0.5, baseY + 1.2, (zMin + zMax) / 2.0);
        level.addFreshEntity(cart);

        BlockPos trackHolo = new BlockPos((xMin + xMax) / 2, baseY + 1, 10);
        spawnHologram(level, trackHolo.getX() + 0.5, baseY + 3.0, trackHolo.getZ() + 0.5, "§b[Circuito de Testes: Trilhos Maglev]");
        spawnHologram(level, trackHolo.getX() + 0.5, baseY + 2.7, trackHolo.getZ() + 0.5, "§7Curvas dinâmicas e propulsão magnética de 1.8x");
    }

    private static void buildAutonomousCyborgFacility(ServerLevel level, BlockPos center, ServerPlayer player) {
        int baseY = center.getY();

        for (int z = -25; z >= -36; z--) {
            for (int x = -1; x <= 1; x++) {
                BlockPos bp = new BlockPos(x, baseY, z);
                Block b = (x != 0) ? Blocks.CUT_SANDSTONE : (Math.abs(z) % 4 == 0 ? Blocks.SEA_LANTERN : Blocks.SMOOTH_SANDSTONE);
                level.setBlock(bp, b.defaultBlockState(), 2);
            }
        }

        int xMin = -20;
        int xMax = 20;
        int zMin = -72;
        int zMax = -36;

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

        BlockPos entranceHolo = new BlockPos(0, baseY + 1, -38);
        spawnHologram(level, entranceHolo.getX() + 0.5, entranceHolo.getY() + 1.2, entranceHolo.getZ() + 0.5, "§6[Complexo Industrial de Ciborgues]");
        spawnHologram(level, entranceHolo.getX() + 0.5, entranceHolo.getY() + 0.9, entranceHolo.getZ() + 0.5, "§bDemonstração de Operações Autônomas");

        BlockPos quarryCenter = new BlockPos(-10, baseY, -52);
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

        BlockPos farmCenter = new BlockPos(10, baseY, -52);
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
                    level.setBlock(p, Blocks.FARMLAND.defaultBlockState(), 2);
                    Block crop = (Math.abs(dx + dz) % 3 == 0) ? Blocks.CARROTS : Blocks.WHEAT;
                    level.setBlock(p.above(), crop.defaultBlockState(), 2);
                }
            }
        }

        BlockPos farmChest = farmCenter.offset(5, 1, 0);
        level.setBlock(farmChest, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(farmChest) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(Items.WHEAT, 64));
            chest.setItem(1, new ItemStack(Items.CARROT, 64));
            chest.setItem(2, new ItemStack(Items.WHEAT_SEEDS, 64));
        }

        CyborgHarvesterEntity cyborgHarv = new CyborgHarvesterEntity(SandStormEntities.CYBORG_HARVESTER, level);
        cyborgHarv.setPos(farmCenter.getX() + 1.5, baseY + 1.0, farmCenter.getZ() + 0.5);
        cyborgHarv.setOwnerUUID(player.getUUID());
        cyborgHarv.setEnergy(50000);
        cyborgHarv.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
        cyborgHarv.setZoneMin(farmCenter.offset(-3, 0, -3));
        cyborgHarv.setZoneMax(farmCenter.offset(3, 2, 3));
        cyborgHarv.setCustomName(Component.literal("§aAndroide Agrícola Autônomo"));
        cyborgHarv.setCustomNameVisible(true);
        cyborgHarv.setPersistenceRequired();
        level.addFreshEntity(cyborgHarv);

        spawnHologram(level, farmCenter.getX() + 0.5, baseY + 3.2, farmCenter.getZ() + 0.5, "§a[Zona 2: Agricultura Autônoma]");
        spawnHologram(level, farmCenter.getX() + 0.5, baseY + 2.9, farmCenter.getZ() + 0.5, "§7Colheita e replantio de culturas");

        BlockPos builderCenter = new BlockPos(0, baseY, -64);
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos p = builderCenter.offset(dx, 0, dz);
                boolean edge = (Math.abs(dx) == 4 || Math.abs(dz) == 3);
                level.setBlock(p, edge ? Blocks.CUT_SANDSTONE.defaultBlockState() : Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                if (edge) {
                    level.setBlock(p.above(), Blocks.IRON_BARS.defaultBlockState(), 2);
                }
            }
        }

        BlockPos dockPos = builderCenter.offset(-2, 1, 0);
        level.setBlock(dockPos, SandStormBlocks.CYBORG_DOCKING_STATION.defaultBlockState(), 3);

        BlockPos incubatorPos = builderCenter.offset(2, 1, 0);
        level.setBlock(incubatorPos, SandStormBlocks.CYBORG_INCUBATOR_VAT.defaultBlockState(), 3);

        CyborgBuilderEntity cyborgBld = new CyborgBuilderEntity(SandStormEntities.CYBORG_BUILDER, level);
        cyborgBld.setPos(builderCenter.getX() + 0.5, baseY + 1.0, builderCenter.getZ() + 1.5);
        cyborgBld.setOwnerUUID(player.getUUID());
        cyborgBld.setEnergy(50000);
        cyborgBld.setRoutine(CyborgRoutine.PATROL_PERIMETER);
        cyborgBld.setZoneMin(builderCenter.offset(-3, 0, -3));
        cyborgBld.setZoneMax(builderCenter.offset(3, 2, 3));
        cyborgBld.setCustomName(Component.literal("§bAndroide Construtor Supervisor"));
        cyborgBld.setCustomNameVisible(true);
        cyborgBld.setPersistenceRequired();
        level.addFreshEntity(cyborgBld);

        spawnHologram(level, builderCenter.getX() + 0.5, baseY + 3.2, builderCenter.getZ() + 0.5, "§b[Zona 3: Manutenção & Doca WPT]");
        spawnHologram(level, builderCenter.getX() + 0.5, baseY + 2.9, builderCenter.getZ() + 0.5, "§7Recarga, reparos e patrulha da base");
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
        List<Item> items = BuiltInRegistries.ITEM.stream()
                .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(SandStormMod.MOD_ID))
                .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                .toList();

        int itemsPerChest = 27;
        int chestCount = (items.size() + itemsPerChest - 1) / itemsPerChest;

        for (int c = 0; c < chestCount; c++) {
            int side = (c % 2 == 0) ? -24 : 24;
            int row = c / 2;
            int pz = 8 + (row * 6);

            BlockPos pedestalCenter = center.offset(side, 0, pz);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos p = pedestalCenter.offset(dx, 0, dz);
                    Block b = (dx == 0 && dz == 0) ? Blocks.OCHRE_FROGLIGHT : Blocks.CUT_SANDSTONE;
                    level.setBlock(p, b.defaultBlockState(), 3);
                }
            }

            BlockPos chestPos = pedestalCenter.above();
            level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
            if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
                int startIdx = c * itemsPerChest;
                int endIdx = Math.min(startIdx + itemsPerChest, items.size());
                for (int i = startIdx; i < endIdx; i++) {
                    int slot = i - startIdx;
                    chest.setItem(slot, new ItemStack(items.get(i), 64));
                }
            }
        }
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

        SandwormEntity worm = new SandwormEntity(SandStormEntities.SANDWORM, level);
        worm.setPos(0.0, entityY, -82.0);
        worm.setShowcaseMode(true);
        worm.setWormSize(2, true);
        worm.setNoGravity(true);
        worm.setPersistenceRequired();
        level.addFreshEntity(worm);
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                BlockPos under = new BlockPos(dx, baseY - 1, -82 + dz);
                level.setBlock(under, Blocks.CUT_SANDSTONE.defaultBlockState(), 2);

                BlockPos p = new BlockPos(dx, baseY, -82 + dz);
                boolean edge = Math.abs(dx) == 5 || Math.abs(dz) == 5;
                Block b = edge ? Blocks.CUT_SANDSTONE : Blocks.SAND;
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }

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

        spawnHologram(level, 4.0, entityY + 2.2, -22.0, "§3[Drone de Carga Logístico]");
        spawnHologram(level, 4.0, entityY + 1.9, -22.0, "§7Transporte Aéreo WPT");

        spawnHologram(level, 12.0, entityY + 2.2, -22.0, "§b[Drone Construtor Autônomo]");
        spawnHologram(level, 12.0, entityY + 1.9, -22.0, "§7Montagem Autônoma de Blocos");

        spawnHologram(level, 0.0, entityY + 4.2, -82.0, "§4[Verme de Areia Adulto]");
        spawnHologram(level, 0.0, entityY + 3.9, -82.0, "§7Predador Ápice do Deserto");
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

    @SuppressWarnings("unchecked")
    private static void spawnHologram(ServerLevel level, double x, double y, double z, String text) {
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

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                for (int dy = 1; dy <= 5; dy++) {
                    BlockPos p = stationCenter.offset(dx, dy, dz);
                    if (!level.getBlockState(p).isAir()) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos p = stationCenter.offset(dx, 0, dz);
                boolean edge = Math.abs(dx) == 3 || Math.abs(dz) == 3;
                boolean corner = Math.abs(dx) == 3 && Math.abs(dz) == 3;
                Block b = corner ? Blocks.OCHRE_FROGLIGHT : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 3);
                if (edge && !corner) {
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

        BlockPos accumulatorPos = stationCenter.offset(-2, 1, 0);
        BlockPos solarPos = stationCenter.offset(-2, 1, -1);
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

        BlockPos chestPos = stationCenter.offset(2, 1, 0);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
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
    }

    private static void buildQuantumComputerWalkway(ServerLevel level, BlockPos center) {
        for (int x = 5; x <= 12; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos p = center.offset(x, 0, z);
                boolean edge = (z != 0);
                boolean light = (x % 3 == 0 && z == 0);
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
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
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
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 3);
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

        spawnMannequin(level, stationCenter.offset(-2, 1, -1), "§6Guardião de Segurança Alfa",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE),
                new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS), new ItemStack(SandStormItems.SPACE_SUIT_BOOTS),
                new ItemStack(SandStormItems.PLASMA_RIFLE), ItemStack.EMPTY);

        spawnMannequin(level, stationCenter.offset(2, 1, -1), "§6Guardião de Segurança Beta",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE),
                new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS), new ItemStack(SandStormItems.SPACE_SUIT_BOOTS),
                new ItemStack(SandStormItems.HEAVY_PLASMA_CANNON), ItemStack.EMPTY);

        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 6.2, stationCenter.getZ() + 4.5, "§6§l[Câmara de Cofre Blindada SandStorm]");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 5.85, stationCenter.getZ() + 4.5, "§aPorta Mecânica 3x3 Multi-Bloco com Volante");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 5.5, stationCenter.getZ() + 4.5, "§eClique na Porta ou na Alavanca para Abrir/Fechar");

        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 3.4, stationCenter.getZ() + 0.5, "§6§l[Reserva Blindada de Alta Tecnologia]");
        spawnHologram(level, stationCenter.getX() + 0.5, baseY + 3.1, stationCenter.getZ() + 0.5, "§7Ligas de Titânio, Processadores Quânticos e Tesouros");

        setupReturnPad(level, stationCenter.offset(-3, 1, -3));
    }

    private static void buildVaultWalkway(ServerLevel level, BlockPos center) {
        for (int x = 19; x <= 28; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos p = center.offset(x, 0, z);
                level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = (z != 0);
                boolean light = (x % 3 == 0 && z == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
        for (int z = -2; z >= -9; z--) {
            for (int dx = -1; dx <= 1; dx++) {
                BlockPos p = center.offset(28 + dx, 0, z);
                level.setBlock(p.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                boolean edge = (dx != 0);
                boolean light = (Math.abs(z) % 3 == 0 && dx == 0);
                Block b = light ? Blocks.SEA_LANTERN : (edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_STONE);
                level.setBlock(p, b.defaultBlockState(), 2);
            }
        }
        setupTerminalTrigger(level, center.offset(4, 0, -2), new BlockPos(28, 161, -6), "Câmara de Cofre Blindada", "Porta Mecânica 3x3 | Estrutura Blindada");
    }

    public static int getBiosphereFrontZ() {
        List<Block> blocks = BuiltInRegistries.BLOCK.stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(SandStormMod.MOD_ID))
                .filter(b -> !b.equals(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR) && !b.equals(SandStormBlocks.VAULT_DOOR_DUMMY))
                .toList();
        int cols = 8;
        int spacing = 4;
        int startZ = 8;
        int totalRows = (blocks.size() + cols - 1) / cols;
        int maxBlockZ = startZ + (totalRows * spacing) + 4;

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
        BlockPos westOrigin = center.offset(-58, -40, 8);
        buildFoundationPlate(level, westOrigin.getX() - 2, westOrigin.getX() + 58, westOrigin.getZ() - 2, westOrigin.getZ() + 58, westOrigin.getY() - 40, 3);
        MillennialRepositoryDungeon.generate(level, westOrigin, 6, 8, 8);
        buildRoofCap(level, westOrigin.getX(), westOrigin.getX() + 56, westOrigin.getZ(), westOrigin.getZ() + 56, westOrigin.getY() + 5);
        BlockPos westEntry = westOrigin.offset(4 * 7 + 3, 1, 3);
        BlockPos westReturn = westOrigin.offset(4 * 7 + 3, 1, 1);
        setupReturnPad(level, westReturn);

        BlockPos eastOrigin = center.offset(4, -40, 8);
        buildFoundationPlate(level, eastOrigin.getX() - 2, eastOrigin.getX() + 58, eastOrigin.getZ() - 2, eastOrigin.getZ() + 58, eastOrigin.getY() - 1, 3);
        AncientBunkerComplexDungeon.generate(level, eastOrigin, 8, 8);
        buildRoofCap(level, eastOrigin.getX(), eastOrigin.getX() + 56, eastOrigin.getZ(), eastOrigin.getZ() + 56, eastOrigin.getY() + 5);
        BlockPos eastEntry = eastOrigin.offset(4 * 7 + 3, 1, 3);
        BlockPos eastReturn = eastOrigin.offset(4 * 7 + 3, 1, 1);
        setupReturnPad(level, eastReturn);

        BlockPos southOrigin = center.offset(-24, -45, -60);
        buildFoundationPlate(level, southOrigin.getX() - 2, southOrigin.getX() + 51, southOrigin.getZ() - 2, southOrigin.getZ() + 51, southOrigin.getY() - 24, 3);
        MillennialRepositoryDungeon.generate(level, southOrigin, 4, 7, 7);
        buildRoofCap(level, southOrigin.getX(), southOrigin.getX() + 49, southOrigin.getZ(), southOrigin.getZ() + 49, southOrigin.getY() + 5);
        BlockPos southEntry = southOrigin.offset(3 * 7 + 3, 1, 3);
        BlockPos southReturn = southOrigin.offset(3 * 7 + 3, 1, 1);
        setupReturnPad(level, southReturn);

        BlockPos northOrigin = center.offset(-24, -45, 74);
        buildFoundationPlate(level, northOrigin.getX() - 2, northOrigin.getX() + 51, northOrigin.getZ() - 2, northOrigin.getZ() + 51, northOrigin.getY() - 24, 3);
        AbandonedMilitaryLabDungeon.generate(level, northOrigin, 4, 7, 7);
        buildRoofCap(level, northOrigin.getX(), northOrigin.getX() + 49, northOrigin.getZ(), northOrigin.getZ() + 49, northOrigin.getY() + 5);
        BlockPos northEntry = northOrigin.offset(3 * 7 + 3, 1, 3);
        BlockPos northReturn = northOrigin.offset(3 * 7 + 3, 1, 1);
        setupReturnPad(level, northReturn);

        BlockPos westShaftTop = center.offset(-4, 0, 4);
        buildGlassObservationTube(level, westShaftTop, westEntry.getY());
        buildCatwalk(level, new BlockPos(westShaftTop.getX(), westEntry.getY(), westShaftTop.getZ()), westEntry.north());
        level.setBlock(westEntry.north(), Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(westEntry.north().above(), Blocks.AIR.defaultBlockState(), 2);
        setupShaftTrigger(level, westShaftTop, westEntry, "Megadungeon Oeste (6 Andares, 8x8)", "6 Andares | Grade 8x8 | 384 Salas");

        BlockPos eastShaftTop = center.offset(4, 0, 4);
        buildGlassObservationTube(level, eastShaftTop, eastEntry.getY());
        buildCatwalk(level, new BlockPos(eastShaftTop.getX(), eastEntry.getY(), eastShaftTop.getZ()), eastEntry.north());
        level.setBlock(eastEntry.north(), Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(eastEntry.north().above(), Blocks.AIR.defaultBlockState(), 2);
        setupShaftTrigger(level, eastShaftTop, eastEntry, "Complexo Bunker Leste (8x8)", "1 Andar Colossal | Grade 8x8 | 64 Salas");

        BlockPos southTerminal = center.offset(0, 0, -4);
        setupTerminalTrigger(level, southTerminal, southEntry, "Depósito Geológico Sul (4 Andares)", "4 Andares | Grade 7x7 | 196 Salas");

        int maxBlockZ = getCheckerboardMaxZ();
        BlockPos northTerminal = center.offset(0, 0, maxBlockZ);
        setupTerminalTrigger(level, northTerminal, northEntry, "Laboratório Militar Abandonado Norte", "4 Andares | Grade 7x7 | 196 Salas | Black Site");
    }

    private static void setupReturnPad(ServerLevel level, BlockPos pos) {
        level.setBlock(pos.below(), SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState(), 3);
        level.setBlock(pos, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.defaultBlockState(), 3);
        spawnHologram(level, pos.getX() + 0.5, pos.getY() + 1.8, pos.getZ() + 0.5, "§a§l▲ RETORNAR AO SHOWCASE ▲");
        spawnHologram(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, "§7Pise para voltar à Praça Central");
        TELEPORT_PADS.add(new ShowcaseTeleportPad(pos, new BlockPos(0, 161, 0), "Praça Central do Showcase"));
    }

    private static void setupShaftTrigger(ServerLevel level, BlockPos top, BlockPos destination, String title, String subtitle) {
        level.setBlock(top, SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState(), 3);
        level.setBlock(top.above(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.defaultBlockState(), 3);
        spawnHologram(level, top.getX() + 0.5, top.getY() + 2.5, top.getZ() + 0.5, "§6§l▼ " + title + " ▼");
        spawnHologram(level, top.getX() + 0.5, top.getY() + 2.2, top.getZ() + 0.5, "§7" + subtitle);
        spawnHologram(level, top.getX() + 0.5, top.getY() + 1.9, top.getZ() + 0.5, "§ePise para teleporte ou use o tubo");
        TELEPORT_PADS.add(new ShowcaseTeleportPad(top.above(), destination, title));
    }

    private static void setupTerminalTrigger(ServerLevel level, BlockPos floorPos, BlockPos destination, String title, String subtitle) {
        level.setBlock(floorPos, SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState(), 3);
        level.setBlock(floorPos.above(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.defaultBlockState(), 3);
        spawnHologram(level, floorPos.getX() + 0.5, floorPos.getY() + 2.5, floorPos.getZ() + 0.5, "§d§l▼ " + title + " ▼");
        spawnHologram(level, floorPos.getX() + 0.5, floorPos.getY() + 2.2, floorPos.getZ() + 0.5, "§7" + subtitle);
        spawnHologram(level, floorPos.getX() + 0.5, floorPos.getY() + 1.9, floorPos.getZ() + 0.5, "§ePise para teleporte rápido");
        TELEPORT_PADS.add(new ShowcaseTeleportPad(floorPos.above(), destination, title));
    }

    private static void buildGlassObservationTube(ServerLevel level, BlockPos top, int bottomY) {
        for (int y = bottomY; y <= top.getY(); y++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos p = new BlockPos(top.getX() + dx, y, top.getZ() + dz);
                    if (dx == 0 && dz == 0) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                        level.setBlock(new BlockPos(top.getX(), y, top.getZ() - 1), Blocks.LADDER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 2);
                        if (y % 8 == 0) {
                            level.setBlock(p, Blocks.LANTERN.defaultBlockState(), 2);
                        }
                    } else {
                        level.setBlock(p, SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                    }
                }
            }
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

    private static int getCheckerboardMaxZ() {
        List<Block> blocks = BuiltInRegistries.BLOCK.stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(SandStormMod.MOD_ID))
                .filter(b -> !b.equals(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR) && !b.equals(SandStormBlocks.VAULT_DOOR_DUMMY))
                .toList();
        int cols = 8;
        int spacing = 4;
        int startZ = 8;
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
                case "east", "bunker", "leste", "single" -> center.offset(4 + 4 * 7 + 3, -39, 8 + 3);
                case "south", "sul" -> center.offset(-24 + 3 * 7 + 3, -44, -60 + 3);
                case "north", "norte", "military_lab", "military", "lab", "militar" -> center.offset(-24 + 3 * 7 + 3, -44, 74 + 3);
                default -> center.offset(-58 + 4 * 7 + 3, -39, 8 + 3);
            };
            player.teleportTo(level, dest.getX() + 0.5, dest.getY() + 0.1, dest.getZ() + 0.5, Set.of(), 0.0f, 0.0f, true);
            level.playSound(null, player.blockPosition(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.2f);
            String label = (norm.equals("military_lab") || norm.equals("military") || norm.equals("lab") || norm.equals("militar") || norm.equals("north") || norm.equals("norte"))
                    ? "Laboratório Militar Abandonado (Setor Norte)"
                    : "Subsolo do Showcase (" + norm + ")";
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
        return builder.buildFuture();
    }
}
