package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.entity.MegastructureConstructorBlockEntity;
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
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ShowcaseAutomation {

    private static final Set<UUID> GENERATED_PLAYERS = new HashSet<>();
    private static BlockPos regeneratingRockPos = null;
    private static CyborgExcavatorEntity loopMiner = null;
    private static boolean showcaseActive = false;

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
            if (server.getTickCount() % 40 == 0 && regeneratingRockPos != null) {
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
        });
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandstorm")
                .then(Commands.literal("showcase")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCommand)
                )
        );

        dispatcher.register(Commands.literal("sandstorm_debug")
                .then(Commands.literal("showcase")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseAutomation::executeCommand)
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

    public static void buildShowcase(ServerLevel level, ServerPlayer player) {
        showcaseActive = true;
        BlockPos center = new BlockPos(0, 160, 0);

        AABB showcaseArea = new AABB(center.getX() - 120, center.getY() - 100, center.getZ() - 120, center.getX() + 120, center.getY() + 120, center.getZ() + 120);
        List<Entity> oldEntities = level.getEntitiesOfClass(Entity.class, showcaseArea, e -> !(e instanceof ServerPlayer));
        for (Entity old : oldEntities) {
            old.discard();
        }

        clearUnderneath(level, center);
        buildCentralPlaza(level, center);
        buildBlockCheckerboard(level, center);
        buildItemWing(level, center);
        buildEntityWing(level, center, player);
        buildMegastructureDomeShowcase(level, center);

        player.setGameMode(GameType.CREATIVE);
        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;
        player.onUpdateAbilities();
        player.teleportTo(level, 0.5, 162.0, 0.5, Set.of(), 0.0f, 0.0f, true);

        level.getServer().getGameRules().set(GameRules.ADVANCE_WEATHER, false, level.getServer());
        level.getServer().getGameRules().set(GameRules.ADVANCE_TIME, false, level.getServer());
        level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack(), "time set noon");
        level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack(), "weather clear");
        SandstormWeatherHandler.getWeather().stopSandstorm();

        level.playSound(null, center, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
        player.sendSystemMessage(Component.literal("§6=================================================="));
        player.sendSystemMessage(Component.literal("§e§l SandStorm - Vitrine Void do Mod (Fases 1 a 33)"));
        player.sendSystemMessage(Component.literal("§b Todos os blocos, maquinarios, itens e entidades"));
        player.sendSystemMessage(Component.literal("§b posicionados em tabuleiro de xadrez no vacuo absoluto!"));
        player.sendSystemMessage(Component.literal("§a Androides operacionais e mineracao em loop ativo!"));
        player.sendSystemMessage(Component.literal("§6=================================================="));
    }

    private static void clearUnderneath(ServerLevel level, BlockPos center) {
        for (int x = -26; x <= 26; x++) {
            for (int z = -48; z <= 60; z++) {
                for (int y = -64; y < center.getY(); y++) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (!level.getBlockState(p).isAir()) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
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

        spawnMannequin(level, center.offset(0, 1, -2), "§6Canhao Pesado & Radar",
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(SandStormItems.HEAVY_PLASMA_CANNON), new ItemStack(SandStormItems.ANOMALY_RADAR));

        spawnMannequin(level, center.offset(0, 1, 2), "§aKit de Voo & Propulsao",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(Items.ELYTRA),
                ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(SandStormItems.SUIT_UPGRADE_JETPACK), new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE));
    }

    private static void buildBlockCheckerboard(ServerLevel level, BlockPos center) {
        List<Block> blocks = BuiltInRegistries.BLOCK.stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(SandStormMod.MOD_ID))
                .filter(b -> !b.equals(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR))
                .sorted(Comparator.comparing(b -> BuiltInRegistries.BLOCK.getKey(b).toString()))
                .toList();

        int cols = 8;
        int spacing = 4;
        int startZ = 8;

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < 8; row++) {
                int px = (col - (cols / 2)) * spacing;
                int pz = startZ + (row * spacing);
                BlockPos p = center.offset(px, 1, pz);
                if (!level.getBlockState(p).isAir()) {
                    level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        for (int i = 0; i < blocks.size(); i++) {
            int col = i % cols;
            int row = i / cols;

            int px = (col - (cols / 2)) * spacing;
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
            level.setBlock(pedestalCenter.above(), targetBlock.defaultBlockState(), 3);
        }
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

                    if (distSq >= (radius - 1) * (radius - 1)) {
                        boolean light = (Math.abs(x) + Math.abs(z)) % 4 == 0;
                        level.setBlock(p, light ? Blocks.SEA_LANTERN.defaultBlockState() : Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
                    } else if (x == 0 && z == 0) {
                        level.setBlock(p, Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 2);
                    } else if (z == 0 && x <= 0) {
                        level.setBlock(p, Blocks.DIRT_PATH.defaultBlockState(), 2);
                    } else if (x >= 3 && x <= 7 && z >= 2 && z <= 6) {
                        boolean pondEdge = (x == 3 || x == 7 || z == 2 || z == 6);
                        if (pondEdge) {
                            level.setBlock(p, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
                        } else {
                            level.setBlock(below, Blocks.SEA_LANTERN.defaultBlockState(), 2);
                            level.setBlock(p, Blocks.WATER.defaultBlockState(), 2);
                            if (x == 5 && z == 4) {
                                level.setBlock(p.above(), Blocks.LILY_PAD.defaultBlockState(), 2);
                            }
                        }
                    } else if (x >= -7 && x <= -3 && z >= 2 && z <= 6) {
                        boolean farmEdge = (x == -7 || x == -3 || z == 2 || z == 6);
                        if (farmEdge) {
                            level.setBlock(p, Blocks.PACKED_MUD.defaultBlockState(), 2);
                        } else {
                            level.setBlock(p, Blocks.FARMLAND.defaultBlockState(), 2);
                            if (x == -5 && z == 4) {
                                level.setBlock(p.above(), Blocks.MELON.defaultBlockState(), 2);
                            } else if (x == -4 && z == 4) {
                                level.setBlock(p.above(), Blocks.PUMPKIN.defaultBlockState(), 2);
                            } else if (z == 3) {
                                level.setBlock(p.above(), Blocks.CARROTS.defaultBlockState(), 2);
                            } else {
                                level.setBlock(p.above(), Blocks.WHEAT.defaultBlockState(), 2);
                            }
                        }
                    } else if (x == 4 && z == -4) {
                        level.setBlock(p, Blocks.ROOTED_DIRT.defaultBlockState(), 2);
                        level.setBlock(p.above(), Blocks.OAK_LOG.defaultBlockState(), 2);
                        level.setBlock(p.above(2), Blocks.OAK_LOG.defaultBlockState(), 2);
                        for (int lx = -1; lx <= 1; lx++) {
                            for (int lz = -1; lz <= 1; lz++) {
                                level.setBlock(p.offset(lx, 3, lz), Blocks.OAK_LEAVES.defaultBlockState(), 2);
                            }
                        }
                        level.setBlock(p.offset(0, 4, 0), Blocks.OAK_LEAVES.defaultBlockState(), 2);
                    } else if (x == -4 && z == -4) {
                        level.setBlock(p, Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                        level.setBlock(p.above(), Blocks.BAMBOO.defaultBlockState(), 2);
                        level.setBlock(p.above(2), Blocks.BAMBOO.defaultBlockState(), 2);
                    } else {
                        boolean moss = (Math.abs(x * 7 + z * 13) % 5 == 0);
                        boolean podzol = (Math.abs(x * 3 + z * 11) % 7 == 0);
                        Block floorBlock = moss ? Blocks.MOSS_BLOCK : (podzol ? Blocks.PODZOL : Blocks.GRASS_BLOCK);
                        level.setBlock(p, floorBlock.defaultBlockState(), 2);

                        int decorHash = Math.abs(x * 31 + z * 17) % 12;
                        if (decorHash == 1) {
                            level.setBlock(p.above(), Blocks.FLOWERING_AZALEA.defaultBlockState(), 2);
                        } else if (decorHash == 2) {
                            level.setBlock(p.above(), Blocks.POPPY.defaultBlockState(), 2);
                        } else if (decorHash == 3) {
                            level.setBlock(p.above(), Blocks.CORNFLOWER.defaultBlockState(), 2);
                        } else if (decorHash == 4) {
                            level.setBlock(p.above(), Blocks.FERN.defaultBlockState(), 2);
                        } else if (decorHash == 5) {
                            level.setBlock(p.above(), Blocks.DANDELION.defaultBlockState(), 2);
                        } else if (decorHash == 6) {
                            level.setBlock(p.above(), Blocks.MOSS_CARPET.defaultBlockState(), 2);
                        } else if (decorHash == 7) {
                            level.setBlock(p.above(), Blocks.SHORT_GRASS.defaultBlockState(), 2);
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
            int side = (c % 2 == 0) ? -18 : 18;
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

    private static void buildEntityWing(ServerLevel level, BlockPos center, ServerPlayer player) {
        int baseY = center.getY();
        double entityY = baseY + 1.0;

        AABB entityArea = new AABB(center.getX() - 80, center.getY() - 30, center.getZ() - 80, center.getX() + 80, center.getY() + 80, center.getZ() + 80);
        List<Entity> oldEntities = level.getEntitiesOfClass(Entity.class, entityArea, e -> !(e instanceof ServerPlayer));
        for (Entity old : oldEntities) {
            old.discard();
        }

        placeEntityPedestal(level, new BlockPos(-12, baseY, -14));
        spawnMannequin(level, new BlockPos(-12, baseY + 1, -14), "§b[Megazord Titan] Chassis Padrao",
                new ItemStack(Items.NETHERITE_HELMET), new ItemStack(Items.NETHERITE_CHESTPLATE), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_BOOTS),
                new ItemStack(SandStormItems.VIBRO_CRYSKNIFE), ItemStack.EMPTY);
        MegazordEntity mechaStd = new MegazordEntity(SandStormEntities.MEGAZORD, level);
        mechaStd.setPos(-12.0, entityY, -14.0);
        mechaStd.setPersistenceRequired();
        mechaStd.getEnergyStorage().receiveEnergy(100000L);
        level.addFreshEntity(mechaStd);

        placeEntityPedestal(level, new BlockPos(-4, baseY, -14));
        spawnMannequin(level, new BlockPos(-4, baseY + 1, -14), "§e[Megazord Titan] Modulo de Voo",
                new ItemStack(SandStormItems.SPACE_SUIT_HELMET), new ItemStack(Items.ELYTRA), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_BOOTS),
                new ItemStack(SandStormItems.MEGAZORD_FLIGHT_MODULE), new ItemStack(SandStormItems.CYBERNETIC_COMMAND_UPLINK));
        MegazordEntity mechaFlight = new MegazordEntity(SandStormEntities.MEGAZORD, level);
        mechaFlight.setPos(-4.0, entityY, -14.0);
        mechaFlight.setFlightModule(true);
        mechaFlight.setPersistenceRequired();
        mechaFlight.getEnergyStorage().receiveEnergy(100000L);
        level.addFreshEntity(mechaFlight);

        placeEntityPedestal(level, new BlockPos(4, baseY, -14));
        spawnMannequin(level, new BlockPos(4, baseY + 1, -14), "§9[Megazord Titan] Casco Submersivel",
                new ItemStack(Items.TURTLE_HELMET), new ItemStack(Items.NETHERITE_CHESTPLATE), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_BOOTS),
                new ItemStack(SandStormItems.MEGAZORD_SUBMERSIBLE_HULL), new ItemStack(Items.TRIDENT));
        MegazordEntity mechaSub = new MegazordEntity(SandStormEntities.MEGAZORD, level);
        mechaSub.setPos(4.0, entityY, -14.0);
        mechaSub.setSubmersibleModule(true);
        mechaSub.setPersistenceRequired();
        mechaSub.getEnergyStorage().receiveEnergy(100000L);
        level.addFreshEntity(mechaSub);

        placeEntityPedestal(level, new BlockPos(12, baseY, -14));
        spawnMannequin(level, new BlockPos(12, baseY + 1, -14), "§c[Megazord Titan] Sobrecarga Tatica Apex",
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
        spawnMannequin(level, new BlockPos(-12, baseY + 1, -22), "§6Veiculo Escavador Pesado",
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
        cargoDrone.setCustomName(Component.literal("§3Drone de Carga Logistico"));
        cargoDrone.setCustomNameVisible(true);
        cargoDrone.setCargoStack(new ItemStack(SandStormItems.TECH_DISC));
        cargoDrone.getEnergyStorage().receiveEnergy(10000L);
        level.addFreshEntity(cargoDrone);

        placeEntityPedestal(level, new BlockPos(12, baseY, -22));
        BuilderDroneEntity builderDrone = new BuilderDroneEntity(SandStormEntities.BUILDER_DRONE, level);
        builderDrone.setPos(12.0, entityY + 1.0, -22.0);
        builderDrone.setNoGravity(true);
        builderDrone.setPersistenceRequired();
        builderDrone.setCustomName(Component.literal("§bDrone Construtor Autonomo"));
        builderDrone.setCustomNameVisible(true);
        level.addFreshEntity(builderDrone);

        BlockPos miningPlatform = new BlockPos(0, baseY, -30);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = miningPlatform.offset(dx, 0, dz);
                Block b = (Math.abs(dx) == 2 || Math.abs(dz) == 2) ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE;
                level.setBlock(p, b.defaultBlockState(), 3);
            }
        }
        level.setBlock(miningPlatform.below(), Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 3);

        regeneratingRockPos = miningPlatform.above();
        level.setBlock(regeneratingRockPos, SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);

        CyborgExcavatorEntity cyborgExc = new CyborgExcavatorEntity(SandStormEntities.CYBORG_EXCAVATOR, level);
        cyborgExc.setPos(-1.5, entityY, -30.0);
        cyborgExc.setOwnerUUID(player.getUUID());
        cyborgExc.setEnergy(50000);
        cyborgExc.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
        cyborgExc.setZoneMin(regeneratingRockPos.offset(-1, -1, -1));
        cyborgExc.setZoneMax(regeneratingRockPos.offset(1, 1, 1));
        cyborgExc.setCustomName(Component.literal("§6Androide Minerador - Operacao em Loop"));
        cyborgExc.setCustomNameVisible(true);
        level.addFreshEntity(cyborgExc);
        loopMiner = cyborgExc;

        CyborgBuilderEntity cyborgBld = new CyborgBuilderEntity(SandStormEntities.CYBORG_BUILDER, level);
        cyborgBld.setPos(1.5, entityY, -30.0);
        cyborgBld.setOwnerUUID(player.getUUID());
        cyborgBld.setEnergy(50000);
        cyborgBld.setRoutine(CyborgRoutine.PATROL_PERIMETER);
        cyborgBld.setCustomName(Component.literal("§eInspetor Androide - Supervisor"));
        cyborgBld.setCustomNameVisible(true);
        level.addFreshEntity(cyborgBld);

        CyborgHarvesterEntity cyborgHarv = new CyborgHarvesterEntity(SandStormEntities.CYBORG_HARVESTER, level);
        cyborgHarv.setPos(0.0, entityY, -32.0);
        cyborgHarv.setOwnerUUID(player.getUUID());
        cyborgHarv.setEnergy(50000);
        cyborgHarv.setRoutine(CyborgRoutine.PATROL_PERIMETER);
        cyborgHarv.setCustomName(Component.literal("§aAndroide Agricola"));
        cyborgHarv.setCustomNameVisible(true);
        level.addFreshEntity(cyborgHarv);

        SandwormEntity worm = new SandwormEntity(SandStormEntities.SANDWORM, level);
        worm.setPos(0.0, entityY, -42.0);
        worm.setShowcaseMode(true);
        worm.setWormSize(2, true);
        level.addFreshEntity(worm);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = new BlockPos(dx, baseY, -42 + dz);
                level.setBlock(p, Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3);
            }
        }
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
}
