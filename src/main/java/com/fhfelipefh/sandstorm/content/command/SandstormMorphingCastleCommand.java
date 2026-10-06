package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.entity.MorphingMatrixCoreBlockEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SandstormMorphingCastleCommand {

    private static final int WALL_HEIGHT = 10;
    private static final int TOWER_HEIGHT = 15;
    private static final int CASTLE_HALF = 12;

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandstorm")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("morphing")
                        .executes(SandstormMorphingCastleCommand::execute)
                        .then(Commands.literal("castle")
                                .executes(SandstormMorphingCastleCommand::execute)
                        )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        Vec3 pos = source.getPosition();

        BlockPos origin = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
        BlockPos corePos = origin.above(WALL_HEIGHT + 1);

        List<BlockPos> castlePositions = buildCastleBlueprint(origin);

        for (BlockPos blockPos : castlePositions) {
            level.setBlock(blockPos, SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState(), 3);
        }

        level.setBlock(corePos, SandStormBlocks.MORPHING_MATRIX_CORE.defaultBlockState(), 3);
        level.setBlock(corePos.above(), Blocks.LEVER.defaultBlockState(), 3);

        level.getServer().execute(() -> {
            BlockEntity be = level.getBlockEntity(corePos);
            if (be instanceof MorphingMatrixCoreBlockEntity core) {
                List<BlockPos> relativePositions = new ArrayList<>();
                for (BlockPos p : castlePositions) {
                    relativePositions.add(p.subtract(corePos));
                }
                relativePositions.sort(Comparator.comparingInt(BlockPos::getY));
                core.getSavedRelativePositions().clear();
                core.getSavedRelativePositions().addAll(relativePositions);
                core.setReserveBlocks(MorphingMatrixCoreBlockEntity.MAX_RESERVE);
                core.setState(MorphingMatrixCoreBlockEntity.MatrixState.IDLE_SOLID);
                level.sendBlockUpdated(corePos, level.getBlockState(corePos), level.getBlockState(corePos), 3);
            }
        });

        source.sendSuccess(() -> Component.literal("§6[SandStorm]§r §bCastelo Morfogenético de Metal Líquido §fconstruído! §7(" + castlePositions.size() + " blocos)"), true);
        source.sendSuccess(() -> Component.literal("§aO Núcleo da Matriz foi posicionado em §f" + corePos.toShortString() + "§a."), true);
        source.sendSuccess(() -> Component.literal("§7Use um §fSinal de Redstone §7no Núcleo para liquefazer a fortaleza."), true);
        source.sendSuccess(() -> Component.literal("§7Remova o sinal para remontá-la. §dSaldo máximo §f(" + MorphingMatrixCoreBlockEntity.MAX_RESERVE + " blocos) §7carregado."), true);

        return 1;
    }

    private static List<BlockPos> buildCastleBlueprint(BlockPos origin) {
        List<BlockPos> positions = new ArrayList<>();

        buildWalls(positions, origin);
        buildTower(positions, origin, -CASTLE_HALF, -CASTLE_HALF);
        buildTower(positions, origin, CASTLE_HALF, -CASTLE_HALF);
        buildTower(positions, origin, -CASTLE_HALF, CASTLE_HALF);
        buildTower(positions, origin, CASTLE_HALF, CASTLE_HALF);
        buildGate(positions, origin);
        buildThroneRoom(positions, origin);

        return positions;
    }

    private static void buildWalls(List<BlockPos> positions, BlockPos origin) {
        int half = CASTLE_HALF;
        for (int y = 0; y < WALL_HEIGHT; y++) {
            for (int x = -half; x <= half; x++) {
                positions.add(origin.offset(x, y, -half));
                positions.add(origin.offset(x, y, half));
            }
            for (int z = -half + 1; z < half; z++) {
                positions.add(origin.offset(-half, y, z));
                positions.add(origin.offset(half, y, z));
            }
        }

        for (int x = -half; x <= half; x += 3) {
            positions.add(origin.offset(x, WALL_HEIGHT, -half));
            positions.add(origin.offset(x, WALL_HEIGHT, half));
        }
        for (int z = -half; z <= half; z += 3) {
            positions.add(origin.offset(-half, WALL_HEIGHT, z));
            positions.add(origin.offset(half, WALL_HEIGHT, z));
        }
    }

    private static void buildTower(List<BlockPos> positions, BlockPos origin, int dx, int dz) {
        int towerRadius = 3;
        for (int y = 0; y < TOWER_HEIGHT; y++) {
            for (int tx = -towerRadius; tx <= towerRadius; tx++) {
                for (int tz = -towerRadius; tz <= towerRadius; tz++) {
                    boolean isWall = Math.abs(tx) == towerRadius || Math.abs(tz) == towerRadius;
                    boolean isBattlement = y == TOWER_HEIGHT - 1 && (Math.abs(tx) + Math.abs(tz)) % 2 == 0;
                    if (isWall || isBattlement) {
                        positions.add(origin.offset(dx + tx, y, dz + tz));
                    }
                }
            }
        }
    }

    private static void buildGate(List<BlockPos> positions, BlockPos origin) {
        int half = CASTLE_HALF;
        for (int y = 0; y < WALL_HEIGHT; y++) {
            for (int x = -half; x <= half; x++) {
                boolean isGateOpening = x >= -2 && x <= 2 && y < 5;
                if (!isGateOpening) {
                    positions.remove(origin.offset(x, y, -half));
                    positions.add(origin.offset(x, y, -half));
                }
            }
        }

        for (int y = 5; y <= WALL_HEIGHT + 2; y++) {
            positions.add(origin.offset(-3, y, -half));
            positions.add(origin.offset(3, y, -half));
            positions.add(origin.offset(-2, y, -half));
            positions.add(origin.offset(2, y, -half));
        }

        for (int x = -2; x <= 2; x++) {
            positions.add(origin.offset(x, 5, -half));
        }
    }

    private static void buildThroneRoom(List<BlockPos> positions, BlockPos origin) {
        int roomSize = 5;
        for (int y = 0; y < 7; y++) {
            for (int x = -roomSize; x <= roomSize; x++) {
                for (int z = -roomSize; z <= roomSize; z++) {
                    boolean isWall = Math.abs(x) == roomSize || Math.abs(z) == roomSize || y == 6;
                    if (isWall) {
                        positions.add(origin.offset(x, y, z));
                    }
                }
            }
        }

        for (int step = 0; step < 3; step++) {
            int size = roomSize - step - 1;
            int yLevel = step;
            for (int x = -size; x <= size; x++) {
                positions.add(origin.offset(x, yLevel, size + 1));
            }
        }
    }
}
