package com.fhfelipefh.sandstorm.content.crash;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.component.SandstormWeatherComponent;
import com.fhfelipefh.sandstorm.content.network.ClaimQuestRewardPayload;
import com.fhfelipefh.sandstorm.content.network.FlashlightTogglePayload;
import com.fhfelipefh.sandstorm.content.network.MagneticInterferencePayload;
import com.fhfelipefh.sandstorm.content.network.SandstormWeatherPayload;
import com.fhfelipefh.sandstorm.content.network.SuitSyncPayload;
import com.fhfelipefh.sandstorm.content.network.SyncPlayerQuestsPayload;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AntiCrashRuntimeBatteryTest {

    @Test
    void energyStorageComponentMustNotCrashOnExtremeOrNegativeValues() {
        EnergyStorageComponent storage = new EnergyStorageComponent(100000L, 500L);

        assertEquals(0L, storage.receiveEnergy(-100L));
        assertEquals(0L, storage.receiveEnergy(0L));
        assertEquals(0L, storage.extractEnergy(-500L));
        assertEquals(0L, storage.extractEnergy(0L));

        storage.setStoredEnergy(-9999L);
        assertEquals(0L, storage.getStoredEnergy());

        storage.setStoredEnergy(Long.MAX_VALUE);
        assertEquals(100000L, storage.getStoredEnergy());

        storage.setCapacity(0L);
        assertEquals(0L, storage.getCapacity());
        assertEquals(0L, storage.getStoredEnergy());
        assertEquals(0.0, storage.getEnergyRatio(), 0.0001);

        assertDoesNotThrow(() -> {
            EnergyStorageComponent zeroStorage = new EnergyStorageComponent(0L, 0L, 0L);
            zeroStorage.receiveEnergy(100L);
            zeroStorage.extractEnergy(50L);
            zeroStorage.getEnergyRatio();
            zeroStorage.transferTo(null, 100L);
            zeroStorage.transferTo(zeroStorage, 100L);
        });
    }

    @Test
    void numberFormatMustNotCrashOnExtremeLongValues() {
        assertDoesNotThrow(() -> {
            NumberFormat.compact(0L);
            NumberFormat.compact(1L);
            NumberFormat.compact(-1L);
            NumberFormat.compact(999L);
            NumberFormat.compact(1000L);
            NumberFormat.compact(1000000L);
            NumberFormat.compact(1000000000L);
            NumberFormat.compact(1000000000000L);
            NumberFormat.compact(Long.MAX_VALUE);
            NumberFormat.compact(Long.MIN_VALUE);
            NumberFormat.formatExact(0L);
            NumberFormat.formatExact(Long.MAX_VALUE);
            NumberFormat.formatExact(Long.MIN_VALUE);
        });

        assertEquals("0", NumberFormat.compact(0L));
        assertEquals("-100", NumberFormat.compact(-100L));
        assertTrue(NumberFormat.compact(Long.MAX_VALUE).endsWith("T"));
        assertTrue(NumberFormat.compact(Long.MIN_VALUE).startsWith("-"));
    }

    @Test
    void weatherMultipliersMustNotProduceNanOrCrash() {
        SandstormWeatherComponent comp = new SandstormWeatherComponent();
        double[] testIntensities = new double[]{0.0, 0.25, 0.5, 0.85, 1.0, -1.0, 5.0};
        for (double intensity : testIntensities) {
            comp.startSandstorm(100, intensity);
            comp.tick();
            double solar = comp.getSolarEfficiencyMultiplier();
            double vibration = comp.getVibrationDampingFactor();
            double fog = comp.getFogDistanceMultiplier();
            double audio = comp.getExternalAudioDampingFactor();

            assertFalse(Double.isNaN(solar), "Solar efficiency must not be NaN");
            assertFalse(Double.isInfinite(solar), "Solar efficiency must not be Infinite");
            assertTrue(solar >= 0.0, "Solar efficiency must be non-negative");

            assertFalse(Double.isNaN(vibration), "Vibration damping must not be NaN");
            assertFalse(Double.isInfinite(vibration), "Vibration damping must not be Infinite");
            assertTrue(vibration >= 0.0, "Vibration damping must be non-negative");

            assertFalse(Double.isNaN(fog), "Fog distance must not be NaN");
            assertFalse(Double.isInfinite(fog), "Fog distance must not be Infinite");
            assertTrue(fog >= 0.0, "Fog distance must be non-negative");

            assertFalse(Double.isNaN(audio), "Audio damping must not be NaN");
            assertFalse(Double.isInfinite(audio), "Audio damping must not be Infinite");
            assertTrue(audio >= 0.0, "Audio damping must be non-negative");
        }
    }

    @Test
    void suitSyncPayloadCodecMustSurviveBoundaryValues() {
        SuitSyncPayload payload = new SuitSyncPayload(0L, Long.MAX_VALUE, -273.15, 0);

        ByteBuf underlying = Unpooled.buffer();
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(underlying, null);

        SuitSyncPayload.STREAM_CODEC.encode(buf, payload);
        SuitSyncPayload decoded = SuitSyncPayload.STREAM_CODEC.decode(buf);

        assertEquals(0L, decoded.storedEnergy());
        assertEquals(Long.MAX_VALUE, decoded.capacity());
        assertEquals(-273.15, decoded.temperature(), 0.001);
        assertEquals(0, decoded.armorCount());
    }

    @Test
    void sandstormWeatherPayloadCodecMustSurviveBoundaryValues() {
        SandstormWeatherPayload payload = new SandstormWeatherPayload(true, 999.99);

        ByteBuf underlying = Unpooled.buffer();
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(underlying, null);

        SandstormWeatherPayload.STREAM_CODEC.encode(buf, payload);
        SandstormWeatherPayload decoded = SandstormWeatherPayload.STREAM_CODEC.decode(buf);

        assertTrue(decoded.active());
        assertEquals(999.99, decoded.intensity(), 0.001);
    }

    @Test
    void magneticInterferencePayloadCodecMustSurviveBoundaryValues() {
        MagneticInterferencePayload payload = new MagneticInterferencePayload(Integer.MAX_VALUE);

        ByteBuf underlying = Unpooled.buffer();
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(underlying, null);

        MagneticInterferencePayload.STREAM_CODEC.encode(buf, payload);
        MagneticInterferencePayload decoded = MagneticInterferencePayload.STREAM_CODEC.decode(buf);

        assertEquals(Integer.MAX_VALUE, decoded.durationTicks());
    }

    @Test
    void flashlightTogglePayloadCodecMustSurviveBoundaryValues() {
        FlashlightTogglePayload payload = new FlashlightTogglePayload(-1);

        ByteBuf underlying = Unpooled.buffer();
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(underlying, null);

        FlashlightTogglePayload.STREAM_CODEC.encode(buf, payload);
        FlashlightTogglePayload decoded = FlashlightTogglePayload.STREAM_CODEC.decode(buf);

        assertEquals(-1, decoded.mode());
    }

    @Test
    void questPayloadsCodecMustSurviveEmptyAndLargePayloads() {
        ClaimQuestRewardPayload claim = new ClaimQuestRewardPayload("", List.of());
        ByteBuf buf1 = Unpooled.buffer();
        RegistryFriendlyByteBuf regBuf1 = new RegistryFriendlyByteBuf(buf1, null);
        ClaimQuestRewardPayload.STREAM_CODEC.encode(regBuf1, claim);
        ClaimQuestRewardPayload decodedClaim = ClaimQuestRewardPayload.STREAM_CODEC.decode(regBuf1);
        assertEquals("", decodedClaim.questId());
        assertTrue(decodedClaim.clientConditions().isEmpty());

        List<String> largeList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            largeList.add("condition_identifier_string_" + i);
        }
        SyncPlayerQuestsPayload sync = new SyncPlayerQuestsPayload(List.of(), largeList);
        ByteBuf buf2 = Unpooled.buffer();
        RegistryFriendlyByteBuf regBuf2 = new RegistryFriendlyByteBuf(buf2, null);
        SyncPlayerQuestsPayload.STREAM_CODEC.encode(regBuf2, sync);
        SyncPlayerQuestsPayload decodedSync = SyncPlayerQuestsPayload.STREAM_CODEC.decode(regBuf2);
        assertTrue(decodedSync.claimedQuestIds().isEmpty());
        assertEquals(50, decodedSync.completedConditions().size());
    }
}
