package com.fhfelipefh.sandstorm.content.block;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class RustMigrationManager {
    private record PendingSection(ServerLevel level, ChunkPos chunk, int section) {}

    private static final Queue<PendingSection> PENDING = new ConcurrentLinkedQueue<>();

    private RustMigrationManager() {}

    public static void initialize() {
        ServerChunkEvents.CHUNK_LOAD.register((level, chunk, isNewChunk) -> {
            LevelChunkSection[] sections = chunk.getSections();
            for (int index = 0; index < sections.length; index++) {
                if (sections[index].maybeHas(state -> state.getBlock() instanceof LegacyRustBlock)) {
                    PENDING.add(new PendingSection(level, chunk.getPos(), index));
                }
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
                        ItemStack stack = player.getInventory().getItem(slot);
                        if (stack.getItem() instanceof BlockItem item && item.getBlock() instanceof LegacyRustBlock) {
                            BlockState replacement = RustingBlock.convertLegacy(item.getBlock().defaultBlockState());
                            player.getInventory().setItem(slot, stack.transmuteCopy(replacement.getBlock()));
                        }
                    }
                }
            }
            PendingSection pending = PENDING.poll();
            if (pending == null || pending.level().getServer() != server) {
                return;
            }
            ServerLevel level = pending.level();
            int baseX = pending.chunk().getMinBlockX();
            int baseZ = pending.chunk().getMinBlockZ();
            int baseY = level.getSectionYFromSectionIndex(pending.section()) * 16;
            if (!level.isLoaded(new BlockPos(baseX, baseY, baseZ))) {
                return;
            }
            LevelChunk chunk = level.getChunk(pending.chunk().x(), pending.chunk().z());
            LevelChunkSection section = chunk.getSection(pending.section());
            if (!section.maybeHas(state -> state.getBlock() instanceof LegacyRustBlock)) {
                return;
            }
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (state.getBlock() instanceof LegacyRustBlock) {
                            level.setBlock(new BlockPos(baseX + x, baseY + y, baseZ + z), RustingBlock.convertLegacy(state), 3);
                        }
                    }
                }
            }
        });
    }
}
