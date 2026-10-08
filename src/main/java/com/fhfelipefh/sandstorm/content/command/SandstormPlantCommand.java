package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.block.AncientReedBlock;
import com.fhfelipefh.sandstorm.content.block.ChitinolyticFungusBlock;
import com.fhfelipefh.sandstorm.content.block.CryoXerophilicLichenBlock;
import com.fhfelipefh.sandstorm.content.block.DuneEphedraBlock;
import com.fhfelipefh.sandstorm.content.block.HalophytePlantBlock;
import com.fhfelipefh.sandstorm.content.block.HalophyteSucculentBlock;
import com.fhfelipefh.sandstorm.content.block.HeavySapCactusBlock;
import com.fhfelipefh.sandstorm.content.block.RadiotrophicMyceliumBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class SandstormPlantCommand {

    private record PlantSpecies(
            String id,
            String displayName,
            Supplier<BlockState> substrate,
            Supplier<BlockState> plantState,
            Supplier<BlockState> upperPlantState
    ) {}

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandstorm")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("plants")
                        .executes(ctx -> executeLine(ctx, 4, null))
                        .then(Commands.literal("line")
                                .executes(ctx -> executeLine(ctx, 4, null))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 32))
                                        .executes(ctx -> executeLine(ctx, IntegerArgumentType.getInteger(ctx, "count"), null))
                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                .executes(ctx -> executeLine(ctx, IntegerArgumentType.getInteger(ctx, "count"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                                        )
                                )
                        )
                        .then(Commands.literal("strip")
                                .executes(ctx -> executeLine(ctx, 4, null))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 32))
                                        .executes(ctx -> executeLine(ctx, IntegerArgumentType.getInteger(ctx, "count"), null))
                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                .executes(ctx -> executeLine(ctx, IntegerArgumentType.getInteger(ctx, "count"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                                        )
                                )
                        )
                        .then(Commands.literal("rows")
                                .executes(ctx -> executeRows(ctx, 7, null))
                                .then(Commands.argument("length", IntegerArgumentType.integer(2, 32))
                                        .executes(ctx -> executeRows(ctx, IntegerArgumentType.getInteger(ctx, "length"), null))
                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                .executes(ctx -> executeRows(ctx, IntegerArgumentType.getInteger(ctx, "length"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                                        )
                                )
                        )
                        .then(Commands.literal("field")
                                .executes(ctx -> executeRows(ctx, 7, null))
                                .then(Commands.argument("length", IntegerArgumentType.integer(2, 32))
                                        .executes(ctx -> executeRows(ctx, IntegerArgumentType.getInteger(ctx, "length"), null))
                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                .executes(ctx -> executeRows(ctx, IntegerArgumentType.getInteger(ctx, "length"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                                        )
                                )
                        )
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> executeLine(ctx, 4, BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                        )
                )
                .then(Commands.literal("plant")
                        .executes(ctx -> executeLine(ctx, 4, null))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> executeLine(ctx, 4, BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                        )
                )
                .then(Commands.literal("plant_field")
                        .executes(ctx -> executeRows(ctx, 7, null))
                        .then(Commands.argument("length", IntegerArgumentType.integer(2, 32))
                                .executes(ctx -> executeRows(ctx, IntegerArgumentType.getInteger(ctx, "length"), null))
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(ctx -> executeRows(ctx, IntegerArgumentType.getInteger(ctx, "length"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                                )
                        )
                )
        );

        dispatcher.register(Commands.literal("sandstorm_plants")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeLine(ctx, 4, null))
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 32))
                        .executes(ctx -> executeLine(ctx, IntegerArgumentType.getInteger(ctx, "count"), null))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> executeLine(ctx, IntegerArgumentType.getInteger(ctx, "count"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                        )
                )
                .then(Commands.literal("at")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> executeLine(ctx, 4, BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                        )
                )
        );

        dispatcher.register(Commands.literal("sandstorm_plant_field")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeRows(ctx, 7, null))
                .then(Commands.argument("length", IntegerArgumentType.integer(2, 32))
                        .executes(ctx -> executeRows(ctx, IntegerArgumentType.getInteger(ctx, "length"), null))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> executeRows(ctx, IntegerArgumentType.getInteger(ctx, "length"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                        )
                )
        );

        dispatcher.register(Commands.literal("sandstorm_plantar")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> executeLine(ctx, 4, null))
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 32))
                        .executes(ctx -> executeLine(ctx, IntegerArgumentType.getInteger(ctx, "count"), null))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> executeLine(ctx, IntegerArgumentType.getInteger(ctx, "count"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                        )
                )
                .then(Commands.literal("campo")
                        .executes(ctx -> executeRows(ctx, 7, null))
                        .then(Commands.argument("length", IntegerArgumentType.integer(2, 32))
                                .executes(ctx -> executeRows(ctx, IntegerArgumentType.getInteger(ctx, "length"), null))
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(ctx -> executeRows(ctx, IntegerArgumentType.getInteger(ctx, "length"), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))
                                )
                        )
                )
        );
    }

    public static int executeLine(CommandContext<CommandSourceStack> ctx, int countPerSpecies, BlockPos explicitPos) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        BlockPos start = resolveTargetPos(source, explicitPos);
        Direction facing = resolveFacing(source);
        Direction right = facing.getClockWise();
        Direction left = facing.getCounterClockWise();

        List<PlantSpecies> speciesList = getSpeciesList();
        int totalPlanted = 0;

        int currentStep = 0;
        placeEndcap(level, start.relative(facing, currentStep), right, left);
        currentStep++;

        for (int i = 0; i < speciesList.size(); i++) {
            PlantSpecies species = speciesList.get(i);
            for (int k = 0; k < countPerSpecies; k++) {
                BlockPos bedPos = start.relative(facing, currentStep);
                placeStripSlice(level, bedPos, right, left, species);
                totalPlanted++;
                currentStep++;
            }
            if (i < speciesList.size() - 1) {
                BlockPos dividerPos = start.relative(facing, currentStep);
                placeStripDivider(level, dividerPos, right, left);
                currentStep++;
            }
        }

        placeEndcap(level, start.relative(facing, currentStep), right, left);

        level.playSound(null, start, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0f, 1.0f);
        final int plantedCount = totalPlanted;
        source.sendSuccess(() -> Component.literal(String.format(
                Locale.ROOT,
                "§6[SandStorm]§a Plantação linear gerada com sucesso! §f(%d espécimes de %d espécies)",
                plantedCount,
                speciesList.size()
        )), true);

        return totalPlanted;
    }

    public static int executeRows(CommandContext<CommandSourceStack> ctx, int rowLength, BlockPos explicitPos) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        BlockPos start = resolveTargetPos(source, explicitPos);
        Direction facing = resolveFacing(source);
        Direction right = facing.getClockWise();

        List<PlantSpecies> speciesList = getSpeciesList();
        int totalPlanted = 0;
        int totalRows = speciesList.size() * 2 + 1;

        for (int r = 0; r <= totalRows; r++) {
            BlockPos rowStart = start.relative(facing, r);
            for (int c = -2; c < rowLength + 2; c++) {
                BlockPos p = rowStart.relative(right, c);
                level.setBlock(p.above(2), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(p.above(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        for (int c = -2; c < rowLength + 2; c++) {
            level.setBlock(start.relative(right, c).below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
            level.setBlock(start.relative(facing, totalRows).relative(right, c).below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        }

        for (int r = 0; r <= totalRows; r++) {
            BlockPos leftEdge = start.relative(facing, r).relative(right, -2);
            BlockPos canal = start.relative(facing, r).relative(right, -1);
            BlockPos rightEdge = start.relative(facing, r).relative(right, rowLength);
            BlockPos outerRight = start.relative(facing, r).relative(right, rowLength + 1);

            level.setBlock(leftEdge.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
            level.setBlock(canal.below(2), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
            level.setBlock(canal.below(), SandStormBlocks.BRACKISH_AQUIFER.defaultBlockState(), 3);
            level.setBlock(rightEdge.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3);
            level.setBlock(outerRight.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        }

        for (int i = 0; i < speciesList.size(); i++) {
            PlantSpecies species = speciesList.get(i);
            int rowStep = 1 + i * 2;
            BlockPos rowOrigin = start.relative(facing, rowStep);

            for (int col = 0; col < rowLength; col++) {
                BlockPos plantPos = rowOrigin.relative(right, col);
                level.setBlock(plantPos.below(), species.substrate().get(), 3);
                level.setBlock(plantPos, species.plantState().get(), 3);
                if (species.upperPlantState() != null) {
                    level.setBlock(plantPos.above(), species.upperPlantState().get(), 3);
                }
                totalPlanted++;
            }

            int walkStep = rowStep + 1;
            if (walkStep < totalRows) {
                BlockPos walkOrigin = start.relative(facing, walkStep);
                for (int col = 0; col < rowLength; col++) {
                    BlockPos walkPos = walkOrigin.relative(right, col);
                    level.setBlock(walkPos.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3);
                }
            }
        }

        level.playSound(null, start, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0f, 1.0f);
        final int plantedCount = totalPlanted;
        source.sendSuccess(() -> Component.literal(String.format(
                Locale.ROOT,
                "§6[SandStorm]§a Campo de cultivo linear gerado com sucesso! §f(%d fileiras, %d espécimes)",
                speciesList.size(),
                plantedCount
        )), true);

        return totalPlanted;
    }

    private static void placeStripSlice(ServerLevel level, BlockPos bedPos, Direction right, Direction left, PlantSpecies species) {
        BlockPos canal = bedPos.relative(left);
        BlockPos borderL = canal.relative(left);
        BlockPos walk = bedPos.relative(right);
        BlockPos borderR = walk.relative(right);

        level.setBlock(borderL.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderL, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderL.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);

        level.setBlock(canal.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(canal, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(canal.below(2), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(canal.below(), SandStormBlocks.BRACKISH_AQUIFER.defaultBlockState(), 3);

        level.setBlock(bedPos.above(2), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(bedPos.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(bedPos, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(bedPos.below(), species.substrate().get(), 3);
        level.setBlock(bedPos, species.plantState().get(), 3);
        if (species.upperPlantState() != null) {
            level.setBlock(bedPos.above(), species.upperPlantState().get(), 3);
        }

        level.setBlock(walk.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(walk, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(walk.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3);

        level.setBlock(borderR.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderR, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderR.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
    }

    private static void placeStripDivider(ServerLevel level, BlockPos bedPos, Direction right, Direction left) {
        BlockPos canal = bedPos.relative(left);
        BlockPos borderL = canal.relative(left);
        BlockPos walk = bedPos.relative(right);
        BlockPos borderR = walk.relative(right);

        level.setBlock(borderL.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderL, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderL.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);

        level.setBlock(canal.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(canal, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(canal.below(2), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
        level.setBlock(canal.below(), SandStormBlocks.BRACKISH_AQUIFER.defaultBlockState(), 3);

        level.setBlock(bedPos.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(bedPos, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(bedPos.below(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), 3);

        level.setBlock(walk.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(walk, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(walk.below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 3);

        level.setBlock(borderR.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderR, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderR.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
    }

    private static void placeEndcap(ServerLevel level, BlockPos center, Direction right, Direction left) {
        BlockPos canal = center.relative(left);
        BlockPos borderL = canal.relative(left);
        BlockPos walk = center.relative(right);
        BlockPos borderR = walk.relative(right);

        level.setBlock(borderL.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderL, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderL.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);

        level.setBlock(canal.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(canal, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(canal.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);

        level.setBlock(center.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(center, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(center.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);

        level.setBlock(walk.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(walk, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(walk.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);

        level.setBlock(borderR.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderR, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(borderR.below(), Blocks.CUT_SANDSTONE.defaultBlockState(), 3);
    }

    private static BlockPos resolveTargetPos(CommandSourceStack source, BlockPos explicitPos) {
        if (explicitPos != null) {
            return explicitPos;
        }
        if (source.getEntity() instanceof ServerPlayer player) {
            Direction facing = player.getDirection();
            return player.blockPosition().relative(facing, 2);
        }
        return BlockPos.containing(source.getPosition());
    }

    private static Direction resolveFacing(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            return player.getDirection();
        }
        return Direction.SOUTH;
    }

    public static List<String> getSpeciesIds() {
        return getSpeciesList().stream().map(PlantSpecies::id).toList();
    }

    public static int getSpeciesCount() {
        return getSpeciesList().size();
    }

    private static List<PlantSpecies> getSpeciesList() {
        return List.of(
                new PlantSpecies(
                        "halophyte_plant",
                        "Halophyte Plant",
                        () -> SandStormBlocks.SALINIZED_SAND.defaultBlockState(),
                        () -> SandStormBlocks.HALOPHYTE_PLANT.defaultBlockState().setValue(HalophytePlantBlock.AGE, 3),
                        null
                ),
                new PlantSpecies(
                        "halophyte_succulent",
                        "Halophyte Succulent",
                        () -> SandStormBlocks.SALINIZED_SAND.defaultBlockState(),
                        () -> SandStormBlocks.HALOPHYTE_SUCCULENT.defaultBlockState().setValue(HalophyteSucculentBlock.AGE, 3),
                        null
                ),
                new PlantSpecies(
                        "dune_ephedra",
                        "Dune Ephedra",
                        () -> Blocks.RED_SAND.defaultBlockState(),
                        () -> SandStormBlocks.DUNE_EPHEDRA.defaultBlockState().setValue(DuneEphedraBlock.AGE, 3),
                        null
                ),
                new PlantSpecies(
                        "heavy_sap_cactus",
                        "Heavy Sap Cactus",
                        () -> Blocks.SAND.defaultBlockState(),
                        () -> SandStormBlocks.HEAVY_SAP_CACTUS.defaultBlockState()
                                .setValue(HeavySapCactusBlock.SAP_LEVEL, 3)
                                .setValue(HeavySapCactusBlock.AGE, 0),
                        () -> SandStormBlocks.HEAVY_SAP_CACTUS.defaultBlockState()
                                .setValue(HeavySapCactusBlock.SAP_LEVEL, 3)
                                .setValue(HeavySapCactusBlock.AGE, 0)
                ),
                new PlantSpecies(
                        "ancient_reed",
                        "Ancient Reed",
                        () -> Blocks.MUD.defaultBlockState(),
                        () -> SandStormBlocks.ANCIENT_REED_BLOCK.defaultBlockState().setValue(AncientReedBlock.AGE, 3),
                        null
                ),
                new PlantSpecies(
                        "radiotrophic_mycelium",
                        "Radiotrophic Mycelium",
                        () -> SandStormBlocks.ELECTRIFIED_SAND.defaultBlockState(),
                        () -> SandStormBlocks.RADIOTROPHIC_MYCELIUM.defaultBlockState().setValue(RadiotrophicMyceliumBlock.AGE, 3),
                        null
                ),
                new PlantSpecies(
                        "chitinolytic_fungus",
                        "Chitinolytic Fungus",
                        () -> SandStormBlocks.BURIED_TECH_RUINS.defaultBlockState(),
                        () -> SandStormBlocks.CHITINOLYTIC_FUNGUS.defaultBlockState().setValue(ChitinolyticFungusBlock.AGE, 3),
                        null
                ),
                new PlantSpecies(
                        "cryo_xerophilic_lichen",
                        "Cryo-Xerophilic Lichen",
                        () -> Blocks.SMOOTH_SANDSTONE.defaultBlockState(),
                        () -> SandStormBlocks.CRYO_XEROPHILIC_LICHEN.defaultBlockState().setValue(CryoXerophilicLichenBlock.AGE, 3),
                        null
                ),
                new PlantSpecies(
                        "xeno_grass",
                        "Xeno Grass",
                        () -> Blocks.DIRT.defaultBlockState(),
                        () -> SandStormBlocks.XENO_GRASS_BLOCK.defaultBlockState(),
                        null
                )
        );
    }
}
