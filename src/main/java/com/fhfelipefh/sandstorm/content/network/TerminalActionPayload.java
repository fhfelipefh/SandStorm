package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record TerminalActionPayload(
        BlockPos terminalPos,
        ItemStack filterStack,
        int actionType
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TerminalActionPayload> TYPE =
            new CustomPacketPayload.Type<>(SandStormMod.id("terminal_action"));

    public static final int ACTION_EXTRACT_STACK = 0;
    public static final int ACTION_EXTRACT_HALF = 1;
    public static final int ACTION_SHIFT_EXTRACT = 2;
    public static final int ACTION_INSERT_HELD = 3;

    public static final StreamCodec<RegistryFriendlyByteBuf, TerminalActionPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBlockPos(payload.terminalPos());
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, payload.filterStack());
                buf.writeVarInt(payload.actionType());
            },
            buf -> {
                BlockPos pos = buf.readBlockPos();
                ItemStack filter = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
                int action = buf.readVarInt();
                return new TerminalActionPayload(pos, filter, action);
            }
    );

    @Override
    public CustomPacketPayload.Type<TerminalActionPayload> type() {
        return TYPE;
    }
}
