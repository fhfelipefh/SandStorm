package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.client.renderer.ClientUplinkRenderer;
import com.fhfelipefh.sandstorm.content.network.SyncUplinkZonePayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CyberneticCommandUplinkItemTest {

    @Test
    void testUplinkModesAndColors() {
        assertEquals(0x00E5FF, CyberneticCommandUplinkItem.UplinkMode.MINING.getColor());
        assertEquals(0xFF9100, CyberneticCommandUplinkItem.UplinkMode.BUILDING.getColor());
        assertEquals(0x00E676, CyberneticCommandUplinkItem.UplinkMode.HARVESTING.getColor());
        assertEquals(0xD500F9, CyberneticCommandUplinkItem.UplinkMode.PATROL.getColor());
        assertEquals(0xFFFFFF, CyberneticCommandUplinkItem.UplinkMode.MOVE_ORDER.getColor());

        assertEquals("mining", CyberneticCommandUplinkItem.UplinkMode.MINING.getId());
        assertEquals("building", CyberneticCommandUplinkItem.UplinkMode.BUILDING.getId());
        assertEquals("harvesting", CyberneticCommandUplinkItem.UplinkMode.HARVESTING.getId());
        assertEquals("patrol", CyberneticCommandUplinkItem.UplinkMode.PATROL.getId());
        assertEquals("move_order", CyberneticCommandUplinkItem.UplinkMode.MOVE_ORDER.getId());

        assertEquals(CyberneticCommandUplinkItem.UplinkMode.BUILDING, CyberneticCommandUplinkItem.UplinkMode.MINING.next());
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.HARVESTING, CyberneticCommandUplinkItem.UplinkMode.BUILDING.next());
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.PATROL, CyberneticCommandUplinkItem.UplinkMode.HARVESTING.next());
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MOVE_ORDER, CyberneticCommandUplinkItem.UplinkMode.PATROL.next());
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MINING, CyberneticCommandUplinkItem.UplinkMode.MOVE_ORDER.next());

        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MINING, CyberneticCommandUplinkItem.UplinkMode.fromOrdinal(0));
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MOVE_ORDER, CyberneticCommandUplinkItem.UplinkMode.fromOrdinal(4));
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MINING, CyberneticCommandUplinkItem.UplinkMode.fromOrdinal(-1));
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MINING, CyberneticCommandUplinkItem.UplinkMode.fromOrdinal(99));
    }

    @Test
    void testSyncUplinkZonePayloadCodecWithValues() {
        BlockPos cornerA = new BlockPos(10, 64, 20);
        BlockPos cornerB = new BlockPos(25, 70, 35);
        BlockPos moveTarget = new BlockPos(15, 65, 25);
        SyncUplinkZonePayload payload = new SyncUplinkZonePayload(cornerA, cornerB, 1, moveTarget);

        assertEquals(cornerA, payload.cornerA());
        assertEquals(cornerB, payload.cornerB());
        assertEquals(1, payload.modeOrdinal());
        assertEquals(moveTarget, payload.moveTarget());
        assertEquals("sandstorm:sync_uplink_zone", SyncUplinkZonePayload.TYPE.id().toString());
        assertNotNull(SyncUplinkZonePayload.STREAM_CODEC);

        ByteBuf underlying = Unpooled.buffer();
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(underlying, null);
        SyncUplinkZonePayload.STREAM_CODEC.encode(buf, payload);

        SyncUplinkZonePayload decoded = SyncUplinkZonePayload.STREAM_CODEC.decode(buf);
        assertEquals(cornerA, decoded.cornerA());
        assertEquals(cornerB, decoded.cornerB());
        assertEquals(1, decoded.modeOrdinal());
        assertEquals(moveTarget, decoded.moveTarget());
    }

    @Test
    void testSyncUplinkZonePayloadCodecWithNulls() {
        SyncUplinkZonePayload payload = new SyncUplinkZonePayload(null, null, 3);
        assertNull(payload.cornerA());
        assertNull(payload.cornerB());
        assertEquals(3, payload.modeOrdinal());
        assertNull(payload.moveTarget());

        ByteBuf underlying = Unpooled.buffer();
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(underlying, null);
        SyncUplinkZonePayload.STREAM_CODEC.encode(buf, payload);

        SyncUplinkZonePayload decoded = SyncUplinkZonePayload.STREAM_CODEC.decode(buf);
        assertNull(decoded.cornerA());
        assertNull(decoded.cornerB());
        assertEquals(3, decoded.modeOrdinal());
        assertNull(decoded.moveTarget());
    }

    @Test
    void testClientUplinkRendererStateManagement() {
        ClientUplinkRenderer.reset();
        assertNull(ClientUplinkRenderer.getClientCornerA());
        assertNull(ClientUplinkRenderer.getClientCornerB());
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MINING, ClientUplinkRenderer.getClientMode());
        assertNull(ClientUplinkRenderer.getClientMoveTarget());

        BlockPos posA = new BlockPos(1, 2, 3);
        BlockPos posB = new BlockPos(4, 5, 6);
        BlockPos target = new BlockPos(7, 8, 9);
        ClientUplinkRenderer.setClientZone(posA, posB, 2, target);

        assertEquals(posA, ClientUplinkRenderer.getClientCornerA());
        assertEquals(posB, ClientUplinkRenderer.getClientCornerB());
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.HARVESTING, ClientUplinkRenderer.getClientMode());
        assertEquals(target, ClientUplinkRenderer.getClientMoveTarget());

        ClientUplinkRenderer.reset();
        assertNull(ClientUplinkRenderer.getClientCornerA());
        assertNull(ClientUplinkRenderer.getClientCornerB());
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MINING, ClientUplinkRenderer.getClientMode());
        assertNull(ClientUplinkRenderer.getClientMoveTarget());
    }
}
