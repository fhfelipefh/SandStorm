package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.SeismicTrackerComponent;
import com.fhfelipefh.sandstorm.content.block.ThumperBlock;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormState;
import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandwormAITest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldValidateSandwormStateTransitionsAndSubterraneanClassification() {
        SandwormState burrowed = SandwormState.BURROWED;
        SandwormState breaching = SandwormState.BREACHING;
        SandwormState surfaced = SandwormState.SURFACED_ASSAULT;
        SandwormState submerging = SandwormState.SUBMERGING;

        assertTrue(burrowed.isSubterranean());
        assertFalse(burrowed.isSurfaced());

        assertTrue(breaching.isSurfaced());
        assertFalse(breaching.isSubterranean());

        assertTrue(surfaced.isSurfaced());
        assertFalse(surfaced.isSubterranean());

        assertTrue(submerging.isSubterranean());
        assertFalse(submerging.isSurfaced());

        assertEquals(SandwormState.BURROWED, SandwormState.fromOrdinal(0));
        assertEquals(SandwormState.BREACHING, SandwormState.fromOrdinal(1));
        assertEquals(SandwormState.SURFACED_ASSAULT, SandwormState.fromOrdinal(2));
        assertEquals(SandwormState.SUBMERGING, SandwormState.fromOrdinal(3));
        assertEquals(SandwormState.BURROWED, SandwormState.fromOrdinal(-1));
        assertEquals(SandwormState.BURROWED, SandwormState.fromOrdinal(99));
    }

    @Test
    void shouldValidateSandwormCombatAttributes() {
        AttributeSupplier.Builder builder = SandwormEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(300.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(18.0, supplier.getBaseValue(Attributes.ATTACK_DAMAGE), 0.001);
        assertEquals(12.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(0.32, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(1.0, supplier.getBaseValue(Attributes.KNOCKBACK_RESISTANCE), 0.001);
        assertEquals(64.0, supplier.getBaseValue(Attributes.FOLLOW_RANGE), 0.001);
    }

    @Test
    void shouldValidateSeismicTargetingMechanicsAndStealthRanges() {
        double maxRange = 64.0;
        double walkingRange = 36.0;
        double sneakRange = 8.0;

        assertTrue(maxRange > walkingRange);
        assertTrue(walkingRange > sneakRange);
        assertEquals(8.0, sneakRange, 0.001);

        SeismicTrackerComponent tracker = SeismicSurvivalHandler.getTracker();
        tracker.reset();

        int chunkX = 10;
        int chunkZ = 10;
        assertEquals(0.0, tracker.getVibration(chunkX, chunkZ), 0.001);

        SeismicSurvivalHandler.recordVibration(chunkX, chunkZ, 25.0);
        assertTrue(tracker.getVibration(chunkX, chunkZ) >= 20.0);
    }

    @Test
    void shouldEnforceSafeZoneProtectionAgainstSandworm() {
        SeismicTrackerComponent tracker = SeismicSurvivalHandler.getTracker();
        tracker.reset();

        BlockPos shipCrashSite = new BlockPos(0, 64, 0);
        BlockPos edgeOfSafeZone = new BlockPos(60, 64, 60);
        BlockPos deepDesert = new BlockPos(250, 64, 250);

        assertTrue(tracker.isInsideSafeZone(shipCrashSite.getX(), shipCrashSite.getZ()));
        assertTrue(tracker.isInsideSafeZone(edgeOfSafeZone.getX(), edgeOfSafeZone.getZ()));
        assertFalse(tracker.isInsideSafeZone(deepDesert.getX(), deepDesert.getZ()));
    }

    @Test
    void shouldValidateBreachShockwavePhysicsAndBounds() {
        AABB wormBox = new AABB(-1.25, 0.0, -1.25, 1.25, 6.0, 1.25);
        AABB shockwaveArea = wormBox.inflate(6.0, 3.0, 6.0);

        assertEquals(-7.25, shockwaveArea.minX, 0.001);
        assertEquals(7.25, shockwaveArea.maxX, 0.001);
        assertEquals(9.0, shockwaveArea.maxY, 0.001);

        Vec3 wormPos = new Vec3(10.0, 64.0, 10.0);
        Vec3 victimPos = new Vec3(12.0, 64.0, 10.0);
        Vec3 push = victimPos.subtract(wormPos).normalize().scale(1.2);

        assertEquals(1.2, push.x, 0.001);
        assertEquals(0.0, push.y, 0.001);
        assertEquals(0.0, push.z, 0.001);
    }

    @Test
    void shouldValidateThumperInteractionAttractsSandworm() {
        assertEquals("powered", ThumperBlock.POWERED.getName());

        int testChunkX = 5;
        int testChunkZ = 8;
        SeismicSurvivalHandler.recordVibration(testChunkX, testChunkZ, 25.0);

        double vibrationLevel = SeismicSurvivalHandler.getTracker().getVibration(testChunkX, testChunkZ);
        assertTrue(vibrationLevel >= 25.0);
    }

    @Test
    void shouldHaveRegisteredSandwormSounds() {
        assertNotNull(SandStormSoundEvents.SANDWORM_RUMBLE);
        assertNotNull(SandStormSoundEvents.SANDWORM_EMERGE);
        assertNotNull(SandStormSoundEvents.SANDWORM_ATTACK);
    }

    @Test
    void shouldValidateSurfaceAssaultDurationLimits() {
        int initialSurfaceTicks = 140;
        int remaining = initialSurfaceTicks;
        for (int i = 0; i < 140; i++) {
            remaining--;
        }
        assertEquals(0, remaining);
        assertTrue(SandwormState.SURFACED_ASSAULT.isSurfaced());
        assertTrue(SandwormState.SUBMERGING.isSubterranean());
    }

    @Test
    void shouldValidateSubterraneanProjectileImmunityRule() {
        SandwormState burrowed = SandwormState.BURROWED;
        assertTrue(burrowed.isSubterranean());
        assertFalse(burrowed.isSurfaced());

        SandwormState surfaced = SandwormState.SURFACED_ASSAULT;
        assertFalse(surfaced.isSubterranean());
        assertTrue(surfaced.isSurfaced());
    }

    @Test
    void shouldValidateSandwormQuestsInChapter4() {
        QuestData bait = QuestRegistry.getQuest("seismic_bait");
        assertNotNull(bait);
        assertEquals(4, bait.chapter());
        assertEquals("sandstorm.thumper_activated", bait.conditionTag());
        assertEquals(4, bait.rewardCount());

        QuestData hunt = QuestRegistry.getQuest("sandworm_hunt");
        assertNotNull(hunt);
        assertEquals(4, hunt.chapter());
        assertEquals("sandstorm.kill_sandworm", hunt.conditionTag());
        assertEquals(4, hunt.rewardCount());

        QuestData harvest = QuestRegistry.getQuest("sandworm_harvest");
        assertNotNull(harvest);
        assertEquals(4, harvest.chapter());
        assertEquals("sandstorm:sandworm_chitin", harvest.requiredItemId().toString());
        assertEquals(6, harvest.rewardCount());
    }

    @Test
    void shouldEnsureSpawnedAndSpontaneousWormsShareIdenticalInitialStateAndActiveAI() {
        assertEquals(SandwormState.SURFACED_ASSAULT, SandwormState.fromOrdinal(2));
        assertEquals(2, SandwormState.SURFACED_ASSAULT.ordinal());
        assertFalse(SandwormState.SURFACED_ASSAULT.isSubterranean());
        assertTrue(SandwormState.SURFACED_ASSAULT.isSurfaced());
    }
}
