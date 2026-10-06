package com.fhfelipefh.sandstorm.performance;

import com.fhfelipefh.sandstorm.component.SeismicTrackerComponent;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.defense.AcousticDefenseTracker;
import com.fhfelipefh.sandstorm.content.defense.KineticShieldTracker;
import com.fhfelipefh.sandstorm.content.entity.CyberneticGolemEntity;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import com.fhfelipefh.sandstorm.metrics.GameMetricsTracker;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandStormPerformanceBenchmarkTest {

    private ResourceKey<Level> overworld;

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @BeforeEach
    void setUp() {
        overworld = Level.OVERWORLD;
        AcousticDefenseTracker.clearAll();
        KineticShieldTracker.clearAll();
    }

    @AfterEach
    void tearDown() {
        AcousticDefenseTracker.clearAll();
        KineticShieldTracker.clearAll();
    }

    @Test
    void benchmarkCyberneticGolemManufacturedBlockScan() {
        BlockState sandState = Blocks.SAND.defaultBlockState();
        BlockState oakPlanksState = Blocks.OAK_PLANKS.defaultBlockState();
        BlockState glassState = Blocks.GLASS.defaultBlockState();
        BlockState sandstoneState = Blocks.SANDSTONE.defaultBlockState();
        BlockState airState = Blocks.AIR.defaultBlockState();

        BlockState[] testStates = new BlockState[]{
                sandState, oakPlanksState, glassState, sandstoneState, airState
        };

        for (int i = 0; i < 5000; i++) {
            CyberneticGolemEntity.isManufacturedBlock(testStates[i % testStates.length]);
        }

        int iterations = 100_000;
        long startTime = System.nanoTime();
        int manufacturedCount = 0;

        for (int i = 0; i < iterations; i++) {
            BlockState state = testStates[i % testStates.length];
            if (CyberneticGolemEntity.isManufacturedBlock(state)) {
                manufacturedCount++;
            }
        }

        long durationNs = System.nanoTime() - startTime;
        double durationMs = durationNs / 1_000_000.0;
        double opsPerSec = (iterations / (durationNs / 1_000_000_000.0));

        assertEquals(40_000, manufacturedCount);
        assertTrue(durationMs < 500.0, "Manufactured block evaluation exceeded threshold: " + durationMs + "ms");
        System.out.printf("[BENCHMARK] CyberneticGolem isManufacturedBlock: %d ops in %.2f ms (%.0f ops/sec)%n",
                iterations, durationMs, opsPerSec);
    }

    @Test
    void benchmarkAcousticDefenseSpatialQueryUnderLoad() {
        int pylonCount = 200;
        Random random = new Random(42);

        for (int i = 0; i < pylonCount; i++) {
            BlockPos pylonPos = new BlockPos(
                    random.nextInt(2000) - 1000,
                    64,
                    random.nextInt(2000) - 1000
            );
            AcousticDefenseTracker.registerPylon(overworld, pylonPos, 32.0);
        }

        List<BlockPos> samplePositions = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            samplePositions.add(new BlockPos(
                    random.nextInt(2000) - 1000,
                    64,
                    random.nextInt(2000) - 1000
            ));
        }

        for (BlockPos pos : samplePositions) {
            AcousticDefenseTracker.isInsideAcousticDamping(overworld, pos);
        }

        int queries = 50_000;
        long startTime = System.nanoTime();
        int insideCount = 0;

        for (int i = 0; i < queries; i++) {
            BlockPos queryPos = samplePositions.get(i % samplePositions.size());
            if (AcousticDefenseTracker.isInsideAcousticDamping(overworld, queryPos)) {
                insideCount++;
            }
        }

        long durationNs = System.nanoTime() - startTime;
        double durationMs = durationNs / 1_000_000.0;
        double opsPerSec = (queries / (durationNs / 1_000_000_000.0));

        assertTrue(insideCount >= 0);
        assertTrue(durationMs < 1000.0, "Acoustic spatial query under load exceeded threshold: " + durationMs + "ms");
        System.out.printf("[BENCHMARK] AcousticDefenseTracker isInside (200 pylons): %d queries in %.2f ms (%.0f ops/sec)%n",
                queries, durationMs, opsPerSec);
    }

    @Test
    void benchmarkKineticShieldSpatialQueryUnderLoad() {
        int shieldCount = 150;
        Random random = new Random(1337);

        for (int i = 0; i < shieldCount; i++) {
            BlockPos shieldPos = new BlockPos(
                    random.nextInt(2000) - 1000,
                    70,
                    random.nextInt(2000) - 1000
            );
            KineticShieldTracker.registerShield(overworld, shieldPos, 24.0);
        }

        List<BlockPos> testPoints = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            testPoints.add(new BlockPos(
                    random.nextInt(2000) - 1000,
                    70,
                    random.nextInt(2000) - 1000
            ));
        }

        for (BlockPos pos : testPoints) {
            KineticShieldTracker.isInsideShield(overworld, pos);
        }

        int queries = 50_000;
        long startTime = System.nanoTime();
        int shieldedHits = 0;

        for (int i = 0; i < queries; i++) {
            BlockPos pos = testPoints.get(i % testPoints.size());
            if (KineticShieldTracker.isInsideShield(overworld, pos)) {
                shieldedHits++;
            }
        }

        long durationNs = System.nanoTime() - startTime;
        double durationMs = durationNs / 1_000_000.0;
        double opsPerSec = (queries / (durationNs / 1_000_000_000.0));

        assertTrue(shieldedHits >= 0);
        assertTrue(durationMs < 1000.0, "Kinetic shield query exceeded threshold: " + durationMs + "ms");
        System.out.printf("[BENCHMARK] KineticShieldTracker isInside (150 shields): %d queries in %.2f ms (%.0f ops/sec)%n",
                queries, durationMs, opsPerSec);
    }

    @Test
    void benchmarkSeismicTrackerVibrationDecayAndQueries() {
        SeismicTrackerComponent tracker = new SeismicTrackerComponent(0, 0, 96.0);
        Random random = new Random(999);

        int chunkCount = 1000;
        for (int i = 0; i < chunkCount; i++) {
            tracker.addVibration(random.nextInt(100) - 50, random.nextInt(100) - 50, 10.0 + random.nextDouble() * 50.0);
        }

        for (int i = 0; i < 10; i++) {
            tracker.decayAll(0.5);
        }

        int iterations = 20_000;
        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            int cx = (i % 100) - 50;
            int cz = ((i / 100) % 100) - 50;
            tracker.isWormAttackTriggered(cx, cz, 40.0);
            if (i % 100 == 0) {
                tracker.decayAll(0.1);
            }
        }

        long durationNs = System.nanoTime() - startTime;
        double durationMs = durationNs / 1_000_000.0;
        double opsPerSec = (iterations / (durationNs / 1_000_000_000.0));

        assertTrue(durationMs < 500.0, "SeismicTracker operations exceeded threshold: " + durationMs + "ms");
        System.out.printf("[BENCHMARK] SeismicTrackerComponent vibration & decay: %d cycles in %.2f ms (%.0f ops/sec)%n",
                iterations, durationMs, opsPerSec);
    }

    @Test
    void benchmarkSuitPowerThermalAndEnergyThroughput() {
        SuitPowerComponent suit = new SuitPowerComponent(100_000, 50, 5, 10);
        suit.updateEquippedArmorCount(4);
        suit.getEnergyStorage().setStoredEnergy(50_000);

        for (int i = 0; i < 1000; i++) {
            suit.tick(true, 42.0, false);
        }

        int cycles = 100_000;
        long startTime = System.nanoTime();

        for (int i = 0; i < cycles; i++) {
            suit.getEnergyStorage().setStoredEnergy(50_000);
            suit.tick(i % 2 == 0, 30.0 + (i % 25), i % 10 == 0);
        }

        long durationNs = System.nanoTime() - startTime;
        double durationMs = durationNs / 1_000_000.0;
        double opsPerSec = (cycles / (durationNs / 1_000_000_000.0));

        assertTrue(suit.getEnergyStorage().getStoredEnergy() > 0);
        assertTrue(durationMs < 500.0, "SuitPowerComponent simulation exceeded threshold: " + durationMs + "ms");
        System.out.printf("[BENCHMARK] SuitPowerComponent thermal/energy simulation: %d cycles in %.2f ms (%.0f ops/sec)%n",
                cycles, durationMs, opsPerSec);
    }

    @Test
    void benchmarkGameMetricsConcurrentStressTest() throws InterruptedException {
        GameMetricsTracker tracker = new GameMetricsTracker();
        int threadCount = 8;
        int operationsPerThread = 25_000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicLong totalDurationNs = new AtomicLong();

        for (int t = 0; t < threadCount; t++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    long threadStart = System.nanoTime();

                    for (int i = 0; i < operationsPerThread; i++) {
                        tracker.recordEnergyGenerated(100);
                        tracker.recordEnergyConsumed(40);
                        tracker.recordWaterFiltered(25);
                        tracker.recordTerraformedBlock();
                        tracker.recordDroneDispatched();
                    }

                    totalDurationNs.addAndGet(System.nanoTime() - threadStart);
                } catch (InterruptedException ignored) {
                } finally {
                    endLatch.countDown();
                }
            });
        }

        long globalStart = System.nanoTime();
        startLatch.countDown();
        boolean completed = endLatch.await(5, TimeUnit.SECONDS);
        long globalDurationNs = System.nanoTime() - globalStart;

        executor.shutdown();
        assertTrue(completed, "Concurrent metrics test timed out");

        int totalOperations = threadCount * operationsPerThread * 5;
        double globalDurationMs = globalDurationNs / 1_000_000.0;
        double opsPerSec = (totalOperations / (globalDurationNs / 1_000_000_000.0));

        assertEquals((long) threadCount * operationsPerThread * 100, tracker.getTotalEnergyGenerated());
        assertEquals((long) threadCount * operationsPerThread * 40, tracker.getTotalEnergyConsumed());
        assertEquals((long) threadCount * operationsPerThread, tracker.getTotalSandBlocksTerraformed());
        assertTrue(globalDurationMs < 1500.0, "Concurrent metrics throughput exceeded threshold: " + globalDurationMs + "ms");

        System.out.printf("[BENCHMARK] GameMetricsTracker concurrent stress (8 threads): %d ops in %.2f ms (%.0f ops/sec)%n",
                totalOperations, globalDurationMs, opsPerSec);
    }

    @Test
    void benchmarkSeismicSafeZoneMathFastPath() {
        SeismicTrackerComponent tracker = new SeismicTrackerComponent(100, 200, 96.0);

        int iterations = 1_000_000;
        long startTime = System.nanoTime();
        int insideCount = 0;

        for (int i = 0; i < iterations; i++) {
            int blockX = (i % 400) - 100;
            int blockZ = ((i / 400) % 400);
            if (tracker.isInsideSafeZone(blockX, blockZ)) {
                insideCount++;
            }
        }

        long durationNs = System.nanoTime() - startTime;
        double durationMs = durationNs / 1_000_000.0;
        double opsPerSec = (iterations / (durationNs / 1_000_000_000.0));

        assertTrue(insideCount > 0);
        assertTrue(durationMs < 200.0, "Safe zone math fast path exceeded threshold: " + durationMs + "ms");
        System.out.printf("[BENCHMARK] SeismicTracker safe zone check: %d calculations in %.2f ms (%.0f ops/sec)%n",
                iterations, durationMs, opsPerSec);
    }

    @Test
    void benchmarkSeismicSafeBlockClassification() {
        BlockState sandstoneState = Blocks.SANDSTONE.defaultBlockState();
        BlockState slabState = Blocks.SMOOTH_SANDSTONE_SLAB.defaultBlockState();
        BlockState stairsState = Blocks.SANDSTONE_STAIRS.defaultBlockState();
        BlockState obsidianState = Blocks.OBSIDIAN.defaultBlockState();
        BlockState sandState = Blocks.SAND.defaultBlockState();

        BlockState[] testStates = new BlockState[]{
                sandstoneState, slabState, stairsState, obsidianState, sandState
        };

        for (int i = 0; i < 2000; i++) {
            SeismicSurvivalHandler.isSeismicSafeBlock(testStates[i % testStates.length]);
        }

        int iterations = 200_000;
        long startTime = System.nanoTime();
        int safeCount = 0;

        for (int i = 0; i < iterations; i++) {
            if (SeismicSurvivalHandler.isSeismicSafeBlock(testStates[i % testStates.length])) {
                safeCount++;
            }
        }

        long durationNs = System.nanoTime() - startTime;
        double durationMs = durationNs / 1_000_000.0;
        double opsPerSec = (iterations / (durationNs / 1_000_000_000.0));

        assertEquals(120_000, safeCount);
        assertTrue(durationMs < 300.0, "Seismic safe block classification exceeded threshold: " + durationMs + "ms");
        System.out.printf("[BENCHMARK] SeismicSurvivalHandler isSeismicSafeBlock: %d checks in %.2f ms (%.0f ops/sec)%n",
                iterations, durationMs, opsPerSec);
    }

    @Test
    void benchmarkCyberneticGolemFullVolumeEvaluation() {
        BlockState sand = Blocks.SAND.defaultBlockState();
        BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState planks = Blocks.OAK_PLANKS.defaultBlockState();

        BlockState[] simulatedVolume = new BlockState[567];
        for (int i = 0; i < simulatedVolume.length; i++) {
            if (i == 566) {
                simulatedVolume[i] = planks;
            } else if (i % 3 == 0) {
                simulatedVolume[i] = sand;
            } else if (i % 3 == 1) {
                simulatedVolume[i] = sandstone;
            } else {
                simulatedVolume[i] = air;
            }
        }

        int volumeScans = 1_000;
        long startTime = System.nanoTime();
        int manufacturedBlocksFound = 0;

        for (int v = 0; v < volumeScans; v++) {
            for (BlockState state : simulatedVolume) {
                if (CyberneticGolemEntity.isManufacturedBlock(state)) {
                    manufacturedBlocksFound++;
                    break;
                }
            }
        }

        long durationNs = System.nanoTime() - startTime;
        double durationMs = durationNs / 1_000_000.0;
        double scansPerSec = (volumeScans / (durationNs / 1_000_000_000.0));

        assertEquals(volumeScans, manufacturedBlocksFound);
        assertTrue(durationMs < 500.0, "Full volume scan exceeded threshold: " + durationMs + "ms");
        System.out.printf("[BENCHMARK] CyberneticGolem full volume scan (567 blocks x %d scans): %.2f ms (%.0f scans/sec)%n",
                volumeScans, durationMs, scansPerSec);
    }
}
