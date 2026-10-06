package com.fhfelipefh.sandstorm.content.command;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.entity.MorphingMatrixCoreBlockEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

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

        Map<BlockPos, BlockState> castleStructure = buildCastleStructure(origin);

        for (Map.Entry<BlockPos, BlockState> entry : castleStructure.entrySet()) {
            level.setBlock(entry.getKey(), entry.getValue(), 3);
        }

        level.setBlock(corePos, SandStormBlocks.MORPHING_MATRIX_CORE.defaultBlockState(), 3);
        level.setBlock(corePos.above(), Blocks.LEVER.defaultBlockState(), 3);

        level.getServer().execute(() -> {
            BlockEntity be = level.getBlockEntity(corePos);
            if (be instanceof MorphingMatrixCoreBlockEntity core) {
                core.saveBlueprint(castleStructure, corePos);
                core.setReserveBlocks(MorphingMatrixCoreBlockEntity.MAX_RESERVE);
                core.setState(MorphingMatrixCoreBlockEntity.MatrixState.IDLE_SOLID);
                level.sendBlockUpdated(corePos, level.getBlockState(corePos), level.getBlockState(corePos), 3);
            }
        });

        source.sendSuccess(() -> Component.literal("§6[SandStorm]§r §bCastelo Morfogenético de Metal Líquido §fconstruído! §7(" + castleStructure.size() + " blocos com piso, portas e janelas)"), true);
        source.sendSuccess(() -> Component.literal("§aO Núcleo da Matriz foi posicionado em §f" + corePos.toShortString() + "§a."), true);
        source.sendSuccess(() -> Component.literal("§7Use o §fGUI do Núcleo §7ou um §fSinal de Redstone §7para liquefazer e remontar a fortaleza."), true);
        source.sendSuccess(() -> Component.literal("§7Saldo máximo §f(" + MorphingMatrixCoreBlockEntity.MAX_RESERVE + " blocos) §7carregado."), true);

        return 1;
    }

    private static Map<BlockPos, BlockState> buildCastleStructure(BlockPos origin) {
        Map<BlockPos, BlockState> structure = new HashMap<>();

        buildFloor(structure, origin);
        buildWalls(structure, origin);
        buildTower(structure, origin, -CASTLE_HALF, -CASTLE_HALF);
        buildTower(structure, origin, CASTLE_HALF, -CASTLE_HALF);
        buildTower(structure, origin, -CASTLE_HALF, CASTLE_HALF);
        buildTower(structure, origin, CASTLE_HALF, CASTLE_HALF);
        buildGate(structure, origin);
        buildThroneRoom(structure, origin);

        return structure;
    }

    private static void buildFloor(Map<BlockPos, BlockState> structure, BlockPos origin) {
        int half = CASTLE_HALF;
        BlockState floorState = SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState();
        for (int x = -half; x <= half; x++) {
            for (int z = -half; z <= half; z++) {
                structure.put(origin.offset(x, 0, z), floorState);
            }
        }
    }

    private static void buildWalls(Map<BlockPos, BlockState> structure, BlockPos origin) {
        int half = CASTLE_HALF;
        BlockState blockState = SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState();
        BlockState windowState = SandStormBlocks.MORPHING_ALLOY_WINDOW.defaultBlockState();

        for (int y = 1; y < WALL_HEIGHT; y++) {
            for (int x = -half; x <= half; x++) {
                boolean isWindow = (y == 4 || y == 5) && (Math.abs(x) % 4 == 0) && Math.abs(x) < half - 2;
                structure.put(origin.offset(x, y, -half), isWindow ? windowState : blockState);
                structure.put(origin.offset(x, y, half), isWindow ? windowState : blockState);
            }
            for (int z = -half + 1; z < half; z++) {
                boolean isWindow = (y == 4 || y == 5) && (Math.abs(z) % 4 == 0) && Math.abs(z) < half - 2;
                structure.put(origin.offset(-half, y, z), isWindow ? windowState : blockState);
                structure.put(origin.offset(half, y, z), isWindow ? windowState : blockState);
            }
        }

        for (int x = -half; x <= half; x += 3) {
            structure.put(origin.offset(x, WALL_HEIGHT, -half), blockState);
            structure.put(origin.offset(x, WALL_HEIGHT, half), blockState);
        }
        for (int z = -half; z <= half; z += 3) {
            structure.put(origin.offset(-half, WALL_HEIGHT, z), blockState);
            structure.put(origin.offset(half, WALL_HEIGHT, z), blockState);
        }
    }

    private static void buildTower(Map<BlockPos, BlockState> structure, BlockPos origin, int dx, int dz) {
        int towerRadius = 3;
        BlockState blockState = SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState();
        BlockState windowState = SandStormBlocks.MORPHING_ALLOY_WINDOW.defaultBlockState();

        for (int y = 1; y < TOWER_HEIGHT; y++) {
            for (int tx = -towerRadius; tx <= towerRadius; tx++) {
                for (int tz = -towerRadius; tz <= towerRadius; tz++) {
                    boolean isWall = Math.abs(tx) == towerRadius || Math.abs(tz) == towerRadius;
                    boolean isBattlement = y == TOWER_HEIGHT - 1 && (Math.abs(tx) + Math.abs(tz)) % 2 == 0;
                    if (isWall || isBattlement) {
                        boolean isTowerWindow = (y == 6 || y == 10) && ((Math.abs(tx) == towerRadius && tz == 0) || (Math.abs(tz) == towerRadius && tx == 0));
                        structure.put(origin.offset(dx + tx, y, dz + tz), isTowerWindow ? windowState : blockState);
                    }
                }
            }
        }
    }

    private static void buildGate(Map<BlockPos, BlockState> structure, BlockPos origin) {
        int half = CASTLE_HALF;
        BlockState blockState = SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState();

        for (int y = 1; y < 5; y++) {
            for (int x = -2; x <= 2; x++) {
                structure.remove(origin.offset(x, y, -half));
            }
        }

        BlockState doorLower = SandStormBlocks.MORPHING_ALLOY_DOOR.defaultBlockState()
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(DoorBlock.FACING, Direction.SOUTH);
        BlockState doorUpper = SandStormBlocks.MORPHING_ALLOY_DOOR.defaultBlockState()
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                .setValue(DoorBlock.FACING, Direction.SOUTH);

        structure.put(origin.offset(-1, 1, -half), doorLower);
        structure.put(origin.offset(-1, 2, -half), doorUpper);
        structure.put(origin.offset(1, 1, -half), doorLower);
        structure.put(origin.offset(1, 2, -half), doorUpper);

        for (int y = 5; y <= WALL_HEIGHT + 2; y++) {
            structure.put(origin.offset(-3, y, -half), blockState);
            structure.put(origin.offset(3, y, -half), blockState);
            structure.put(origin.offset(-2, y, -half), blockState);
            structure.put(origin.offset(2, y, -half), blockState);
        }

        for (int x = -2; x <= 2; x++) {
            structure.put(origin.offset(x, 5, -half), blockState);
        }
    }

    private static void buildThroneRoom(Map<BlockPos, BlockState> structure, BlockPos origin) {
        int roomSize = 5;
        BlockState blockState = SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState();
        BlockState windowState = SandStormBlocks.MORPHING_ALLOY_WINDOW.defaultBlockState();

        for (int y = 1; y < 7; y++) {
            for (int x = -roomSize; x <= roomSize; x++) {
                for (int z = -roomSize; z <= roomSize; z++) {
                    boolean isWall = Math.abs(x) == roomSize || Math.abs(z) == roomSize || y == 6;
                    if (isWall) {
                        boolean isThroneWindow = z == roomSize && (y == 3 || y == 4) && Math.abs(x) <= 2;
                        structure.put(origin.offset(x, y, z), isThroneWindow ? windowState : blockState);
                    }
                }
            }
        }

        for (int step = 0; step < 3; step++) {
            int size = roomSize - step - 1;
            int yLevel = step + 1;
            for (int x = -size; x <= size; x++) {
                structure.put(origin.offset(x, yLevel, size + 1), blockState);
            }
        }
    }
}
