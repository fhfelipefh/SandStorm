package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.content.storage.StoredItemEntry;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record SyncTerminalGridPayload(
        BlockPos terminalPos,
        int energyStored,
        int maxEnergy,
        long totalStored,
        long totalCapacity,
        List<StoredItemEntry> items
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncTerminalGridPayload> TYPE =
            new CustomPacketPayload.Type<>(SandStormMod.id("sync_terminal_grid"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncTerminalGridPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBlockPos(payload.terminalPos());
                buf.writeInt(payload.energyStored());
                buf.writeInt(payload.maxEnergy());
                buf.writeLong(payload.totalStored());
                buf.writeLong(payload.totalCapacity());
                buf.writeVarInt(payload.items().size());
                for (StoredItemEntry entry : payload.items()) {
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, entry.template());
                    buf.writeVarLong(entry.count());
                }
            },
            buf -> {
                BlockPos pos = buf.readBlockPos();
                int energy = buf.readInt();
                int maxEnergy = buf.readInt();
                long totalStored = buf.readLong();
                long totalCapacity = buf.readLong();
                int size = buf.readVarInt();
                List<StoredItemEntry> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    ItemStack template = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
                    long count = buf.readVarLong();
                    list.add(new StoredItemEntry(template, count));
                }
                return new SyncTerminalGridPayload(pos, energy, maxEnergy, totalStored, totalCapacity, list);
            }
    );

    @Override
    public CustomPacketPayload.Type<SyncTerminalGridPayload> type() {
        return TYPE;
    }
}
