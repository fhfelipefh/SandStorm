package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WptRelayTowerManager;
import com.fhfelipefh.sandstorm.content.block.entity.AutonomousSonicTurretBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.DeepCoreDrillBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.KineticShieldGeneratorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.MegastructureConstructorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SolidStateAccumulatorBlockEntity;
import com.fhfelipefh.sandstorm.content.defense.KineticShieldTracker;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.megastructure.MegastructureBlueprint;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.world.structure.ColossalCastleGenerator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SandstormBuildCommand {

    private static final Map<String, String> STRUCTURE_MAP = new LinkedHashMap<>();
    private static final List<String> STRUCTURE_NAMES = List.of(
            "biosphere_dome",
            "planetary_citadel",
            "orbital_launch_silo",
            "desert_tech_pyramid",
            "colony_outpost",
            "defense_perimeter",
            "hydroponics_greenhouse",
            "mining_complex",
            "energy_grid",
            "ruins_laboratory",
            "colossal_castle"
    );

    static {
        STRUCTURE_MAP.put("biosphere_dome", "biosphere_dome");
        STRUCTURE_MAP.put("dome", "biosphere_dome");
        STRUCTURE_MAP.put("planetary_citadel", "planetary_citadel");
        STRUCTURE_MAP.put("citadel", "planetary_citadel");
        STRUCTURE_MAP.put("orbital_launch_silo", "orbital_launch_silo");
        STRUCTURE_MAP.put("silo", "orbital_launch_silo");
        STRUCTURE_MAP.put("desert_tech_pyramid", "desert_tech_pyramid");
        STRUCTURE_MAP.put("pyramid", "desert_tech_pyramid");
        STRUCTURE_MAP.put("colony_outpost", "colony_outpost");
        STRUCTURE_MAP.put("colony", "colony_outpost");
        STRUCTURE_MAP.put("defense_perimeter", "defense_perimeter");
        STRUCTURE_MAP.put("defense", "defense_perimeter");
        STRUCTURE_MAP.put("hydroponics_greenhouse", "hydroponics_greenhouse");
        STRUCTURE_MAP.put("greenhouse", "hydroponics_greenhouse");
        STRUCTURE_MAP.put("mining_complex", "mining_complex");
        STRUCTURE_MAP.put("mining", "mining_complex");
        STRUCTURE_MAP.put("energy_grid", "energy_grid");
        STRUCTURE_MAP.put("energy", "energy_grid");
        STRUCTURE_MAP.put("ruins_laboratory", "ruins_laboratory");
        STRUCTURE_MAP.put("ruins", "ruins_laboratory");
        STRUCTURE_MAP.put("colossal_castle", "colossal_castle");
        STRUCTURE_MAP.put("castle", "colossal_castle");
        STRUCTURE_MAP.put("stone_castle", "colossal_castle");
        STRUCTURE_MAP.put("castelo", "colossal_castle");
        STRUCTURE_MAP.put("gigantic_castle", "colossal_castle");
        STRUCTURE_MAP.put("blackstone_castle", "colossal_castle");
    }

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandstorm")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("build")
                        .then(Commands.argument("structure", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggestStructures(builder))
                                .executes(ctx -> executeBuild(ctx, StringArgumentType.getString(ctx, "structure"), null))
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(ctx -> executeBuild(ctx, StringArgumentType.getString(ctx, "structure"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                                )
                        )
                )
                .then(Commands.literal("structure")
                        .then(Commands.argument("structure", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggestStructures(builder))
                                .executes(ctx -> executeBuild(ctx, StringArgumentType.getString(ctx, "structure"), null))
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(ctx -> executeBuild(ctx, StringArgumentType.getString(ctx, "structure"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                                )
                        )
                )
                .then(Commands.literal("instant_build")
                        .executes(SandstormBuildCommand::executeInstantFinish)
                )
                .then(Commands.literal("finish_build")
                        .executes(SandstormBuildCommand::executeInstantFinish)
                )
                .then(Commands.literal("clear_area")
                        .executes(ctx -> executeClearArea(ctx, 16))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, 64))
                                .executes(ctx -> executeClearArea(ctx, IntegerArgumentType.getInteger(ctx, "radius")))
                        )
                )
        );

        dispatcher.register(Commands.literal("sandstorm_build")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("structure", StringArgumentType.word())
                        .suggests((ctx, builder) -> suggestStructures(builder))
                        .executes(ctx -> executeBuild(ctx, StringArgumentType.getString(ctx, "structure"), null))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> executeBuild(ctx, StringArgumentType.getString(ctx, "structure"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                        )
                )
        );

        dispatcher.register(Commands.literal("build_structure")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("structure", StringArgumentType.word())
                        .suggests((ctx, builder) -> suggestStructures(builder))
                        .executes(ctx -> executeBuild(ctx, StringArgumentType.getString(ctx, "structure"), null))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> executeBuild(ctx, StringArgumentType.getString(ctx, "structure"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                        )
                )
        );

        dispatcher.register(Commands.literal("sandstorm_finish")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(SandstormBuildCommand::executeInstantFinish)
        );

        dispatcher.register(Commands.literal("sandstorm_instant")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(SandstormBuildCommand::executeInstantFinish)
        );

        dispatcher.register(Commands.literal("sandstorm_clear")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeClearArea(ctx, 16))
                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 64))
                        .executes(ctx -> executeClearArea(ctx, IntegerArgumentType.getInteger(ctx, "radius")))
                )
        );

        registerDirectShortcuts(dispatcher);
    }

    private static void registerDirectShortcuts(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("build_colony")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "colony_outpost", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "colony_outpost", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_defense")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "defense_perimeter", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "defense_perimeter", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_greenhouse")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "hydroponics_greenhouse", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "hydroponics_greenhouse", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_mining")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "mining_complex", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "mining_complex", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_energy")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "energy_grid", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "energy_grid", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_ruins")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "ruins_laboratory", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "ruins_laboratory", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_dome")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "biosphere_dome", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "biosphere_dome", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_citadel")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "planetary_citadel", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "planetary_citadel", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_silo")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "orbital_launch_silo", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "orbital_launch_silo", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_pyramid")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "desert_tech_pyramid", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "desert_tech_pyramid", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_castle")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "colossal_castle", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "colossal_castle", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_colossal_castle")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "colossal_castle", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "colossal_castle", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );

        dispatcher.register(Commands.literal("build_castelo")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeBuild(ctx, "colossal_castle", null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> executeBuild(ctx, "colossal_castle", BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                )
        );
    }

    private static CompletableFuture<Suggestions> suggestStructures(SuggestionsBuilder builder) {
        for (String id : STRUCTURE_NAMES) {
            builder.suggest(id);
        }
        return builder.buildFuture();
    }

    private static int executeBuild(CommandContext<CommandSourceStack> ctx, String structureName, BlockPos explicitPos) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        BlockPos targetPos = (explicitPos != null) ? explicitPos : BlockPos.containing(source.getPosition());
        String normalizedId = STRUCTURE_MAP.get(structureName.toLowerCase(Locale.ROOT));

        if (normalizedId == null) {
            source.sendFailure(Component.literal(String.format(Locale.ROOT, "§c[SandStorm]§r Estrutura desconhecida: '%s'. Opções válidas: %s", structureName, String.join(", ", STRUCTURE_NAMES))));
            return 0;
        }

        int blocksPlaced = build(level, targetPos, normalizedId);
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "§a[SandStorm]§r Estrutura '%s' construída com sucesso em [%d, %d, %d]! (%d blocos gerados)", normalizedId, targetPos.getX(), targetPos.getY(), targetPos.getZ(), blocksPlaced)), true);
        return blocksPlaced;
    }

    private static int executeInstantFinish(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        BlockPos center = BlockPos.containing(source.getPosition());
        MegastructureConstructorBlockEntity targetConstructor = null;

        if (source.getEntity() instanceof ServerPlayer player) {
            Vec3 eyePos = player.getEyePosition();
            Vec3 lookVec = player.getViewVector(1.0f);
            Vec3 endPos = eyePos.add(lookVec.scale(16.0));
            BlockHitResult hit = level.clip(new ClipContext(eyePos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos hitPos = hit.getBlockPos();
                if (level.getBlockEntity(hitPos) instanceof MegastructureConstructorBlockEntity be) {
                    targetConstructor = be;
                }
            }
        }

        if (targetConstructor == null) {
            double closestDistSq = Double.MAX_VALUE;
            for (int dx = -16; dx <= 16; dx++) {
                for (int dy = -8; dy <= 8; dy++) {
                    for (int dz = -16; dz <= 16; dz++) {
                        BlockPos checkPos = center.offset(dx, dy, dz);
                        if (level.getBlockEntity(checkPos) instanceof MegastructureConstructorBlockEntity be) {
                            double distSq = checkPos.distSqr(center);
                            if (distSq < closestDistSq) {
                                closestDistSq = distSq;
                                targetConstructor = be;
                            }
                        }
                    }
                }
            }
        }

        if (targetConstructor == null) {
            source.sendFailure(Component.literal("§c[SandStorm]§r Nenhum Construtor de Megastruturas encontrado por perto. Mire em um construtor ou fique a até 16 blocos de distância."));
            return 0;
        }

        int blocksPlaced = targetConstructor.completeInstantly();
        MegastructureBlueprint blueprint = targetConstructor.getBlueprint();
        BlockPos pos = targetConstructor.getBlockPos();
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "§a[SandStorm]§r Construtor em [%d, %d, %d] com '%s' finalizado instantaneamente! (%d blocos colocados)", pos.getX(), pos.getY(), pos.getZ(), blueprint.getDisplayName(), blocksPlaced)), true);
        return blocksPlaced;
    }

    private static int executeClearArea(CommandContext<CommandSourceStack> ctx, int radius) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        BlockPos center = BlockPos.containing(source.getPosition());
        int clearedCount = clearArea(level, center, radius);
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "§a[SandStorm]§r Área de raio %d limpa em [%d, %d, %d]! (%d blocos modificados)", radius, center.getX(), center.getY(), center.getZ(), clearedCount)), true);
        return clearedCount;
    }

    public static int clearArea(ServerLevel level, BlockPos center, int radius) {
        int clampedRadius = Math.clamp(radius, 1, 64);
        int clearedCount = 0;
        int rSq = clampedRadius * clampedRadius;
        for (int dx = -clampedRadius; dx <= clampedRadius; dx++) {
            for (int dz = -clampedRadius; dz <= clampedRadius; dz++) {
                if (dx * dx + dz * dz <= rSq) {
                    BlockPos floorPos = center.offset(dx, -1, dz);
                    if (level.isLoaded(floorPos) && !level.getBlockState(floorPos).isSolid()) {
                        level.setBlock(floorPos, Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                        clearedCount++;
                    }
                    for (int dy = 0; dy <= 25; dy++) {
                        BlockPos target = center.offset(dx, dy, dz);
                        if (level.isLoaded(target) && !level.getBlockState(target).isAir()) {
                            level.setBlock(target, Blocks.AIR.defaultBlockState(), 2);
                            clearedCount++;
                        }
                    }
                }
            }
        }
        return clearedCount;
    }

    public static int build(ServerLevel level, BlockPos center, String id) {
        return switch (id) {
            case "biosphere_dome" -> buildMegastructure(level, center, 0);
            case "planetary_citadel" -> buildMegastructure(level, center, 1);
            case "orbital_launch_silo" -> buildMegastructure(level, center, 2);
            case "desert_tech_pyramid" -> buildMegastructure(level, center, 3);
            case "colony_outpost" -> buildColonyOutpost(level, center);
            case "defense_perimeter" -> buildDefensePerimeter(level, center);
            case "hydroponics_greenhouse" -> buildHydroponicsGreenhouse(level, center);
            case "mining_complex" -> buildMiningComplex(level, center);
            case "energy_grid" -> buildEnergyGrid(level, center);
            case "ruins_laboratory" -> buildRuinsLaboratory(level, center);
            case "colossal_castle" -> ColossalCastleGenerator.generate(level, center);
            default -> 0;
        };
    }

    private static int buildMegastructure(ServerLevel level, BlockPos center, int blueprintIndex) {
        level.setBlock(center, SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR.defaultBlockState(), 3);
        if (level.getBlockEntity(center) instanceof MegastructureConstructorBlockEntity be) {
            be.setBlueprintIndex(blueprintIndex);
            be.setEnergy(be.getMaxEnergy());
            return be.completeInstantly();
        }
        return 0;
    }

    private static int buildColonyOutpost(ServerLevel level, BlockPos origin) {
        int placedCount = 0;
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                BlockPos floorPos = origin.offset(dx, 0, dz);
                boolean isEdge = Math.abs(dx) == 6 || Math.abs(dz) == 6;
                level.setBlock(floorPos, isEdge ? Blocks.POLISHED_ANDESITE.defaultBlockState() : Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                placedCount++;

                for (int dy = 1; dy <= 6; dy++) {
                    BlockPos airPos = origin.offset(dx, dy, dz);
                    if (!level.getBlockState(airPos).isAir()) {
                        level.setBlock(airPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }

                if (isEdge && !(dx == 0 && dz == 6)) {
                    level.setBlock(origin.offset(dx, 1, dz), SandStormBlocks.TITANIUM_SPIKE_WALL.defaultBlockState(), 2);
                    placedCount++;
                }
            }
        }

        BlockPos centerDome = origin.offset(0, 1, 0);
        level.setBlock(centerDome, SandStormBlocks.HABITAT_DOME.defaultBlockState(), 3);
        placedCount++;

        BlockPos towerPos = origin.offset(-4, 1, -4);
        level.setBlock(towerPos, SandStormBlocks.WPT_RELAY_TOWER.defaultBlockState(), 3);
        WptRelayTowerManager.registerTower(level.dimension(), towerPos, WptRelayTowerManager.DEFAULT_TRANSFER_RATE);
        placedCount++;

        BlockPos accumulatorPos = origin.offset(4, 1, -4);
        level.setBlock(accumulatorPos, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
        if (level.getBlockEntity(accumulatorPos) instanceof SolidStateAccumulatorBlockEntity acc) {
            acc.setStoredEnergy(acc.getMaxEnergy());
        }
        placedCount++;

        BlockPos consolePos = origin.offset(4, 1, 4);
        level.setBlock(consolePos, SandStormBlocks.GRID_MONITOR_CONSOLE.defaultBlockState(), 3);
        placedCount++;

        BlockPos printerPos = origin.offset(3, 1, 0);
        level.setBlock(printerPos, SandStormBlocks.PRINTER_3D.defaultBlockState(), 3);
        setMachineEnergy(level, printerPos, 10000);
        placedCount++;

        BlockPos nanitePos = origin.offset(3, 1, 1);
        level.setBlock(nanitePos, SandStormBlocks.NANITE_FABRICATOR.defaultBlockState(), 3);
        setMachineEnergy(level, nanitePos, 10000);
        placedCount++;

        BlockPos workbenchPos = origin.offset(3, 1, -1);
        level.setBlock(workbenchPos, SandStormBlocks.SANDSTONE_WORKBENCH.defaultBlockState(), 3);
        placedCount++;

        BlockPos furnacePos = origin.offset(3, 1, -2);
        level.setBlock(furnacePos, SandStormBlocks.SANDSTONE_FURNACE.defaultBlockState(), 3);
        placedCount++;

        BlockPos desalinatorPos = origin.offset(-3, 1, 0);
        level.setBlock(desalinatorPos, SandStormBlocks.DESALINATION_FILTER.defaultBlockState(), 3);
        setMachineEnergy(level, desalinatorPos, 10000);
        placedCount++;

        BlockPos dewPos = origin.offset(-3, 1, 1);
        level.setBlock(dewPos, SandStormBlocks.DEW_CONDENSER.defaultBlockState(), 3);
        placedCount++;

        BlockPos refineryPos = origin.offset(-3, 1, -1);
        level.setBlock(refineryPos, SandStormBlocks.CHEMICAL_REFINERY.defaultBlockState(), 3);
        setMachineEnergy(level, refineryPos, 10000);
        placedCount++;

        BlockPos pipePos = origin.offset(-3, 1, -2);
        level.setBlock(pipePos, SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState(), 3);
        placedCount++;

        BlockPos chestPos = origin.offset(0, 1, -4);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.RAW_SILICON, 16));
            chest.setItem(1, new ItemStack(SandStormItems.SILICON_WAFER, 8));
            chest.setItem(2, new ItemStack(SandStormItems.SANDWORM_CHITIN, 16));
            chest.setItem(3, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 4));
            chest.setItem(4, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 8));
            chest.setItem(5, new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE, 8));
            chest.setItem(6, new ItemStack(SandStormItems.SPACE_RATION, 16));
            chest.setChanged();
        }
        placedCount++;

        int[][] lanterns = {{-2, -2}, {2, -2}, {-2, 2}, {2, 2}};
        for (int[] lp : lanterns) {
            level.setBlock(origin.offset(lp[0], 1, lp[1]), Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
            level.setBlock(origin.offset(lp[0], 2, lp[1]), Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
            level.setBlock(origin.offset(lp[0], 3, lp[1]), Blocks.SEA_LANTERN.defaultBlockState(), 2);
            placedCount += 3;
        }

        level.playSound(null, origin, SandStormSoundEvents.MEGASTRUCTURE_COMPLETE, SoundSource.BLOCKS, 2.0f, 1.0f);
        return placedCount;
    }

    private static int buildDefensePerimeter(ServerLevel level, BlockPos origin) {
        int placedCount = 0;
        int radius = 8;
        for (int dx = -radius - 1; dx <= radius + 1; dx++) {
            for (int dz = -radius - 1; dz <= radius + 1; dz++) {
                BlockPos floorPos = origin.offset(dx, 0, dz);
                level.setBlock(floorPos, Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
                placedCount++;

                for (int dy = 1; dy <= 6; dy++) {
                    BlockPos airPos = origin.offset(dx, dy, dz);
                    if (!level.getBlockState(airPos).isAir()) {
                        level.setBlock(airPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }

                double dist = Math.sqrt(dx * dx + dz * dz);
                if (Math.abs(dist - (radius + 1)) <= 0.6) {
                    level.setBlock(origin.offset(dx, 1, dz), SandStormBlocks.KINETIC_FLOOR_SPIKES.defaultBlockState(), 2);
                    placedCount++;
                } else if (Math.abs(dist - radius) <= 0.6) {
                    if (!(dx == 0 && dz == radius)) {
                        level.setBlock(origin.offset(dx, 1, dz), SandStormBlocks.TITANIUM_SPIKE_WALL.defaultBlockState(), 2);
                        level.setBlock(origin.offset(dx, 2, dz), SandStormBlocks.TITANIUM_SPIKE_WALL.defaultBlockState(), 2);
                        placedCount += 2;
                    }
                } else if (Math.abs(dist - (radius - 1)) <= 0.6) {
                    level.setBlock(origin.offset(dx, 1, dz), SandStormBlocks.ELECTRIFIED_SPIKE_BARRIER.defaultBlockState(), 2);
                    placedCount++;
                }
            }
        }

        BlockPos gatePos = origin.offset(0, 1, radius);
        level.setBlock(gatePos, SandStormBlocks.CRUSHING_SPIKE_GATE.defaultBlockState(), 3);
        level.setBlock(origin.offset(-1, 1, radius), SandStormBlocks.RETRACTABLE_SPIKE_WALL.defaultBlockState(), 3);
        level.setBlock(origin.offset(1, 1, radius), SandStormBlocks.RETRACTABLE_SPIKE_WALL.defaultBlockState(), 3);
        placedCount += 3;

        BlockPos shieldPos = origin.offset(0, 1, 0);
        level.setBlock(shieldPos, SandStormBlocks.KINETIC_SHIELD_GENERATOR.defaultBlockState(), 3);
        if (level.getBlockEntity(shieldPos) instanceof KineticShieldGeneratorBlockEntity shieldBe) {
            shieldBe.setEnergy(20000);
            KineticShieldTracker.registerShield(level.dimension(), shieldPos, KineticShieldGeneratorBlockEntity.SHIELD_RADIUS);
        }
        placedCount++;

        BlockPos accPos = origin.offset(0, 1, -2);
        level.setBlock(accPos, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
        if (level.getBlockEntity(accPos) instanceof SolidStateAccumulatorBlockEntity acc) {
            acc.setStoredEnergy(acc.getMaxEnergy());
        }
        placedCount++;

        BlockPos towerPos = origin.offset(0, 1, 2);
        level.setBlock(towerPos, SandStormBlocks.WPT_RELAY_TOWER.defaultBlockState(), 3);
        WptRelayTowerManager.registerTower(level.dimension(), towerPos, WptRelayTowerManager.DEFAULT_TRANSFER_RATE);
        placedCount++;

        int[][] turretOffsets = {{0, -5}, {0, 5}, {5, 0}, {-5, 0}};
        for (int[] to : turretOffsets) {
            BlockPos pillarBottom = origin.offset(to[0], 1, to[1]);
            level.setBlock(pillarBottom, Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
            level.setBlock(pillarBottom.above(), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
            BlockPos turretPos = pillarBottom.above(2);
            level.setBlock(turretPos, SandStormBlocks.AUTONOMOUS_SONIC_TURRET.defaultBlockState(), 3);
            if (level.getBlockEntity(turretPos) instanceof AutonomousSonicTurretBlockEntity turret) {
                turret.setEnergy(10000);
            }
            placedCount += 3;
        }

        level.playSound(null, origin, SandStormSoundEvents.MEGASTRUCTURE_COMPLETE, SoundSource.BLOCKS, 2.0f, 1.0f);
        return placedCount;
    }

    private static int buildHydroponicsGreenhouse(ServerLevel level, BlockPos origin) {
        int placedCount = 0;
        int radius = 6;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos floorPos = origin.offset(dx, 0, dz);
                level.setBlock(floorPos, Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 2);
                placedCount++;

                for (int dy = 1; dy <= radius; dy++) {
                    BlockPos airPos = origin.offset(dx, dy, dz);
                    if (!level.getBlockState(airPos).isAir()) {
                        level.setBlock(airPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        for (int y = 1; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + y * y + z * z);
                    if (Math.abs(dist - radius) <= 0.75) {
                        BlockPos domePos = origin.offset(x, y, z);
                        level.setBlock(domePos, SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
                        placedCount++;
                    }
                }
            }
        }

        BlockPos chamberPos = origin.offset(0, 1, 0);
        level.setBlock(chamberPos, SandStormBlocks.HYDROPONIC_CHAMBER.defaultBlockState(), 3);
        setMachineEnergy(level, chamberPos, 10000);
        placedCount++;

        for (int dz = -2; dz <= 2; dz++) {
            level.setBlock(origin.offset(-2, 1, dz), SandStormBlocks.XENO_GRASS_BLOCK.defaultBlockState(), 2);
            level.setBlock(origin.offset(2, 1, dz), SandStormBlocks.SALINIZED_SAND.defaultBlockState(), 2);
            level.setBlock(origin.offset(2, 2, dz), SandStormBlocks.HALOPHYTE_PLANT.defaultBlockState(), 2);
            placedCount += 3;
        }

        level.setBlock(origin.offset(0, 1, -2), SandStormBlocks.HEAVY_SAP_CACTUS.defaultBlockState(), 2);
        level.setBlock(origin.offset(0, 1, 2), SandStormBlocks.HEAVY_SAP_CACTUS.defaultBlockState(), 2);
        placedCount += 2;

        level.setBlock(origin.offset(-3, 1, 3), SandStormBlocks.DEW_CONDENSER.defaultBlockState(), 3);
        level.setBlock(origin.offset(3, 1, 3), SandStormBlocks.DESALINATION_FILTER.defaultBlockState(), 3);
        setMachineEnergy(level, origin.offset(3, 1, 3), 10000);
        level.setBlock(origin.offset(0, 1, -4), SandStormBlocks.ATMOSPHERIC_TERRAFORMER.defaultBlockState(), 3);
        level.setBlock(origin.offset(-1, 1, 0), SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState(), 3);
        level.setBlock(origin.offset(1, 1, 0), SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState(), 3);
        placedCount += 5;

        level.playSound(null, origin, SandStormSoundEvents.MEGASTRUCTURE_COMPLETE, SoundSource.BLOCKS, 2.0f, 1.0f);
        return placedCount;
    }

    private static int buildMiningComplex(ServerLevel level, BlockPos origin) {
        int placedCount = 0;
        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                BlockPos floorPos = origin.offset(dx, 0, dz);
                level.setBlock(floorPos, Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
                placedCount++;

                for (int dy = 1; dy <= 6; dy++) {
                    BlockPos airPos = origin.offset(dx, dy, dz);
                    if (!level.getBlockState(airPos).isAir()) {
                        level.setBlock(airPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        for (int dy = 0; dy >= -6; dy--) {
            level.setBlock(origin.offset(0, dy, 0), Blocks.AIR.defaultBlockState(), 2);
        }

        BlockPos drillPos = origin.offset(0, 1, 0);
        level.setBlock(drillPos, SandStormBlocks.DEEP_CORE_DRILL.defaultBlockState(), 3);
        if (level.getBlockEntity(drillPos) instanceof DeepCoreDrillBlockEntity drill) {
            drill.setEnergy(50000);
        }
        placedCount++;

        BlockPos springPos = origin.offset(2, 0, 0);
        level.setBlock(springPos, SandStormBlocks.THERMAL_SPRING_STONE.defaultBlockState(), 3);
        BlockPos genPos = origin.offset(2, 1, 0);
        level.setBlock(genPos, SandStormBlocks.THERMAL_GENERATOR.defaultBlockState(), 3);
        placedCount += 2;

        BlockPos bayPos = origin.offset(-4, 1, -2);
        level.setBlock(bayPos, SandStormBlocks.ASSEMBLY_BAY.defaultBlockState(), 3);
        BlockPos dockPos = origin.offset(-4, 1, 2);
        level.setBlock(dockPos, SandStormBlocks.DRONE_DOCK.defaultBlockState(), 3);
        placedCount += 2;

        for (int dx = -3; dx <= 0; dx++) {
            level.setBlock(origin.offset(dx, 1, -4), SandStormBlocks.SAND_MAGLEV_RAIL.defaultBlockState(), 2);
            placedCount++;
        }

        level.setBlock(origin.offset(1, 1, 0), SandStormBlocks.SMART_FLUID_PIPE.defaultBlockState(), 3);
        level.setBlock(origin.offset(4, 1, 2), SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);
        placedCount += 2;

        BlockPos chestPos = origin.offset(4, 1, -2);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.RAW_SILICON, 32));
            chest.setItem(1, new ItemStack(SandStormItems.SILICON_WAFER, 16));
            chest.setItem(2, new ItemStack(SandStormItems.SCRAP_METAL, 24));
            chest.setItem(3, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 8));
            chest.setChanged();
        }
        placedCount++;

        level.playSound(null, origin, SandStormSoundEvents.MEGASTRUCTURE_COMPLETE, SoundSource.BLOCKS, 2.0f, 1.0f);
        return placedCount;
    }

    private static int buildEnergyGrid(ServerLevel level, BlockPos origin) {
        int placedCount = 0;
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                BlockPos floorPos = origin.offset(dx, 0, dz);
                level.setBlock(floorPos, Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
                placedCount++;

                for (int dy = 1; dy <= 5; dy++) {
                    BlockPos airPos = origin.offset(dx, dy, dz);
                    if (!level.getBlockState(airPos).isAir()) {
                        level.setBlock(airPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        BlockPos towerPos = origin.offset(0, 1, 0);
        level.setBlock(towerPos, SandStormBlocks.WPT_RELAY_TOWER.defaultBlockState(), 3);
        WptRelayTowerManager.registerTower(level.dimension(), towerPos, WptRelayTowerManager.DEFAULT_TRANSFER_RATE);
        placedCount++;

        BlockPos consolePos = origin.offset(0, 1, 1);
        level.setBlock(consolePos, SandStormBlocks.GRID_MONITOR_CONSOLE.defaultBlockState(), 3);
        placedCount++;

        int[][] accOffsets = {{-2, -2}, {2, -2}, {-2, 2}, {2, 2}};
        for (int[] ao : accOffsets) {
            BlockPos ap = origin.offset(ao[0], 1, ao[1]);
            level.setBlock(ap, SandStormBlocks.SOLID_STATE_ACCUMULATOR.defaultBlockState(), 3);
            if (level.getBlockEntity(ap) instanceof SolidStateAccumulatorBlockEntity acc) {
                acc.setStoredEnergy(acc.getMaxEnergy());
            }
            placedCount++;
        }

        int[][] solarOffsets = {{-4, -3}, {-4, 0}, {-4, 3}, {4, -3}, {4, 0}, {4, 3}};
        for (int[] so : solarOffsets) {
            BlockPos pillar = origin.offset(so[0], 1, so[1]);
            level.setBlock(pillar, Blocks.IRON_BARS.defaultBlockState(), 2);
            BlockPos solar = origin.offset(so[0], 2, so[1]);
            level.setBlock(solar, SandStormBlocks.WIRELESS_SOLAR_RECEIVER_TIER2.defaultBlockState(), 3);
            placedCount += 2;
        }

        BlockPos springPos = origin.offset(0, 0, -4);
        level.setBlock(springPos, SandStormBlocks.THERMAL_SPRING_STONE.defaultBlockState(), 3);
        BlockPos genPos = origin.offset(0, 1, -4);
        level.setBlock(genPos, SandStormBlocks.THERMAL_GENERATOR.defaultBlockState(), 3);
        placedCount += 2;

        level.playSound(null, origin, SandStormSoundEvents.MEGASTRUCTURE_COMPLETE, SoundSource.BLOCKS, 2.0f, 1.0f);
        return placedCount;
    }

    private static int buildRuinsLaboratory(ServerLevel level, BlockPos origin) {
        int placedCount = 0;
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                BlockPos floorPos = origin.offset(dx, 0, dz);
                level.setBlock(floorPos, Blocks.POLISHED_ANDESITE.defaultBlockState(), 2);
                placedCount++;

                for (int dy = 1; dy <= 5; dy++) {
                    BlockPos airPos = origin.offset(dx, dy, dz);
                    if (!level.getBlockState(airPos).isAir()) {
                        level.setBlock(airPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        BlockPos corePedestal = origin.offset(0, 1, 0);
        level.setBlock(corePedestal, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);
        BlockPos corePos = origin.offset(0, 2, 0);
        level.setBlock(corePos, SandStormBlocks.ANCIENT_DATA_CORE.defaultBlockState(), 3);
        level.setBlock(origin.offset(0, 3, 0), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        level.setBlock(origin.offset(1, 2, 0), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        level.setBlock(origin.offset(-1, 2, 0), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        level.setBlock(origin.offset(0, 2, 1), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        level.setBlock(origin.offset(0, 2, -1), SandStormBlocks.FULGURITE_GLASS.defaultBlockState(), 2);
        placedCount += 7;

        level.setBlock(origin.offset(2, 1, -2), SandStormBlocks.PIEZO_QUARTZ_BLOCK.defaultBlockState(), 3);
        level.setBlock(origin.offset(2, 2, -2), SandStormBlocks.PIEZO_QUARTZ_CLUSTER.defaultBlockState(), 3);
        level.setBlock(origin.offset(-2, 1, -2), SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(), 3);
        level.setBlock(origin.offset(-2, 2, -2), SandStormBlocks.FOSSILIZED_AMBER.defaultBlockState(), 3);
        placedCount += 4;

        BlockPos printerPos = origin.offset(3, 1, 1);
        level.setBlock(printerPos, SandStormBlocks.PRINTER_3D.defaultBlockState(), 3);
        setMachineEnergy(level, printerPos, 10000);
        BlockPos nanitePos = origin.offset(3, 1, 2);
        level.setBlock(nanitePos, SandStormBlocks.NANITE_FABRICATOR.defaultBlockState(), 3);
        setMachineEnergy(level, nanitePos, 10000);
        placedCount += 2;

        BlockPos chestPos = origin.offset(-3, 1, 1);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SandStormItems.TECH_DISC, 2));
            chest.setItem(1, new ItemStack(SandStormItems.CIRCUIT_BOARD, 4));
            chest.setItem(2, new ItemStack(SandStormItems.SILICON_WAFER, 8));
            chest.setItem(3, new ItemStack(SandStormItems.RAW_SILICON, 16));
            chest.setChanged();
        }
        placedCount++;

        level.playSound(null, origin, SandStormSoundEvents.MEGASTRUCTURE_COMPLETE, SoundSource.BLOCKS, 2.0f, 1.0f);
        return placedCount;
    }

    private static void setMachineEnergy(ServerLevel level, BlockPos pos, int energy) {
        if (level.getBlockEntity(pos) instanceof BaseMachineBlockEntity machine) {
            machine.setEnergy(energy);
        }
    }
}
