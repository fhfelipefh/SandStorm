package com.fhfelipefh.sandstorm.content.defense;

import com.fhfelipefh.sandstorm.content.block.CollapsibleTrapdoorBlock;
import com.fhfelipefh.sandstorm.content.block.CryoFreezeMineBlock;
import com.fhfelipefh.sandstorm.content.block.HoloLaserTripwireBlock;
import com.fhfelipefh.sandstorm.content.block.KineticEjectorTrapBlock;
import com.fhfelipefh.sandstorm.content.block.MicroSingularityTrapBlock;
import com.fhfelipefh.sandstorm.content.block.MirageHologramTrapBlock;
import com.fhfelipefh.sandstorm.content.block.NaniteQuicksandBlock;
import com.fhfelipefh.sandstorm.content.block.NaniteWebSnareBlock;
import com.fhfelipefh.sandstorm.content.block.NeurotoxicSpikesBlock;
import com.fhfelipefh.sandstorm.content.block.PiezoShockTrapBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SeismicDecoyTrapBlock;
import com.fhfelipefh.sandstorm.content.block.SerratedJawTrapBlock;
import com.fhfelipefh.sandstorm.content.block.ThermalVentTrapBlock;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.item.SmokeGrenadeItem;
import com.fhfelipefh.sandstorm.content.item.StunGrenadeItem;
import com.fhfelipefh.sandstorm.content.item.TacticalDetonatorItem;
import com.fhfelipefh.sandstorm.content.item.TrapDisarmerKitItem;
import net.minecraft.SharedConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TacticalOrdnanceTrapsTest {

    @BeforeAll
    static void setup() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BuiltInRegistries.BLOCK, false);
        frozenField.set(BuiltInRegistries.ITEM, false);
        frozenField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());
    }

    @Test
    void testSerratedJawTrapRegistrationAndStates() {
        assertNotNull(SandStormBlocks.SERRATED_JAW_TRAP);
        BlockState defaultState = SandStormBlocks.SERRATED_JAW_TRAP.defaultBlockState();
        assertFalse(defaultState.getValue(SerratedJawTrapBlock.SNAPPED));
        assertEquals(Direction.NORTH, defaultState.getValue(SerratedJawTrapBlock.FACING));

        BlockState snappedState = defaultState.setValue(SerratedJawTrapBlock.SNAPPED, true);
        assertTrue(snappedState.getValue(SerratedJawTrapBlock.SNAPPED));
    }

    @Test
    void testPiezoShockTrapRegistrationAndStates() {
        assertNotNull(SandStormBlocks.PIEZO_SHOCK_TRAP);
        BlockState defaultState = SandStormBlocks.PIEZO_SHOCK_TRAP.defaultBlockState();
        assertTrue(defaultState.getValue(PiezoShockTrapBlock.CHARGED));

        BlockState dischargedState = defaultState.setValue(PiezoShockTrapBlock.CHARGED, false);
        assertFalse(dischargedState.getValue(PiezoShockTrapBlock.CHARGED));
    }

    @Test
    void testCollapsibleTrapdoorRegistrationAndStates() {
        assertNotNull(SandStormBlocks.COLLAPSIBLE_TRAPDOOR);
        BlockState defaultState = SandStormBlocks.COLLAPSIBLE_TRAPDOOR.defaultBlockState();
        assertFalse(defaultState.getValue(CollapsibleTrapdoorBlock.OPEN));
        assertEquals(Direction.NORTH, defaultState.getValue(CollapsibleTrapdoorBlock.FACING));

        BlockState openState = defaultState.setValue(CollapsibleTrapdoorBlock.OPEN, true);
        assertTrue(openState.getValue(CollapsibleTrapdoorBlock.OPEN));

        assertTrue(defaultState.isPathfindable(PathComputationType.LAND));
    }

    @Test
    void testKineticEjectorTrapRegistrationAndStates() {
        assertNotNull(SandStormBlocks.KINETIC_EJECTOR_TRAP);
        BlockState defaultState = SandStormBlocks.KINETIC_EJECTOR_TRAP.defaultBlockState();
        assertTrue(defaultState.getValue(KineticEjectorTrapBlock.CHARGED));
        assertEquals(Direction.NORTH, defaultState.getValue(KineticEjectorTrapBlock.FACING));

        BlockState dischargedState = defaultState.setValue(KineticEjectorTrapBlock.CHARGED, false);
        assertFalse(dischargedState.getValue(KineticEjectorTrapBlock.CHARGED));
    }

    @Test
    void testThermalVentTrapRegistrationAndStates() {
        assertNotNull(SandStormBlocks.THERMAL_VENT_TRAP);
        BlockState defaultState = SandStormBlocks.THERMAL_VENT_TRAP.defaultBlockState();
        assertFalse(defaultState.getValue(ThermalVentTrapBlock.ACTIVE));

        BlockState activeState = defaultState.setValue(ThermalVentTrapBlock.ACTIVE, true);
        assertTrue(activeState.getValue(ThermalVentTrapBlock.ACTIVE));
    }

    @Test
    void testTacticalGrenadesRegistration() {
        assertNotNull(SandStormItems.STUN_GRENADE);
        assertTrue(SandStormItems.STUN_GRENADE instanceof StunGrenadeItem);

        assertNotNull(SandStormItems.SMOKE_GRENADE);
        assertTrue(SandStormItems.SMOKE_GRENADE instanceof SmokeGrenadeItem);

        assertNotNull(SandStormEntities.STUN_GRENADE);
        assertNotNull(SandStormEntities.SMOKE_GRENADE);
    }

    @Test
    void testMicroSingularityTrapRegistrationAndStates() {
        assertNotNull(SandStormBlocks.MICRO_SINGULARITY_TRAP);
        BlockState defaultState = SandStormBlocks.MICRO_SINGULARITY_TRAP.defaultBlockState();
        assertFalse(defaultState.getValue(MicroSingularityTrapBlock.ACTIVE));

        BlockState activeState = defaultState.setValue(MicroSingularityTrapBlock.ACTIVE, true);
        assertTrue(activeState.getValue(MicroSingularityTrapBlock.ACTIVE));
    }

    @Test
    void testNaniteQuicksandRegistrationAndProperties() {
        assertNotNull(SandStormBlocks.NANITE_QUICKSAND);
        assertTrue(SandStormBlocks.NANITE_QUICKSAND instanceof NaniteQuicksandBlock);
        BlockState defaultState = SandStormBlocks.NANITE_QUICKSAND.defaultBlockState();
        assertTrue(defaultState.isPathfindable(PathComputationType.LAND));
    }

    @Test
    void testHoloLaserTripwireRegistrationAndStates() {
        assertNotNull(SandStormBlocks.HOLO_LASER_TRIPWIRE);
        BlockState defaultState = SandStormBlocks.HOLO_LASER_TRIPWIRE.defaultBlockState();
        assertFalse(defaultState.getValue(HoloLaserTripwireBlock.POWERED));
        assertEquals(Direction.NORTH, defaultState.getValue(HoloLaserTripwireBlock.FACING));

        BlockState poweredState = defaultState.setValue(HoloLaserTripwireBlock.POWERED, true);
        assertTrue(poweredState.getValue(HoloLaserTripwireBlock.POWERED));
    }

    @Test
    void testSeismicDecoyTrapRegistrationAndStates() {
        assertNotNull(SandStormBlocks.SEISMIC_DECOY_TRAP);
        BlockState defaultState = SandStormBlocks.SEISMIC_DECOY_TRAP.defaultBlockState();
        assertFalse(defaultState.getValue(SeismicDecoyTrapBlock.ACTIVE));

        BlockState activeState = defaultState.setValue(SeismicDecoyTrapBlock.ACTIVE, true);
        assertTrue(activeState.getValue(SeismicDecoyTrapBlock.ACTIVE));
    }

    @Test
    void testCryoFreezeMineRegistrationAndStates() {
        assertNotNull(SandStormBlocks.CRYO_FREEZE_MINE);
        BlockState defaultState = SandStormBlocks.CRYO_FREEZE_MINE.defaultBlockState();
        assertFalse(defaultState.getValue(CryoFreezeMineBlock.ACTIVE));

        BlockState activeState = defaultState.setValue(CryoFreezeMineBlock.ACTIVE, true);
        assertTrue(activeState.getValue(CryoFreezeMineBlock.ACTIVE));
    }

    @Test
    void testMirageHologramTrapRegistrationAndStates() {
        assertNotNull(SandStormBlocks.MIRAGE_HOLOGRAM_TRAP);
        assertTrue(SandStormBlocks.MIRAGE_HOLOGRAM_TRAP instanceof MirageHologramTrapBlock);
        BlockState defaultState = SandStormBlocks.MIRAGE_HOLOGRAM_TRAP.defaultBlockState();
        assertFalse(defaultState.getValue(MirageHologramTrapBlock.ACTIVE));

        BlockState activeState = defaultState.setValue(MirageHologramTrapBlock.ACTIVE, true);
        assertTrue(activeState.getValue(MirageHologramTrapBlock.ACTIVE));
    }

    @Test
    void testNaniteWebSnareRegistrationAndStates() {
        assertNotNull(SandStormBlocks.NANITE_WEB_SNARE);
        assertTrue(SandStormBlocks.NANITE_WEB_SNARE instanceof NaniteWebSnareBlock);
        BlockState defaultState = SandStormBlocks.NANITE_WEB_SNARE.defaultBlockState();
        assertFalse(defaultState.getValue(NaniteWebSnareBlock.ACTIVE));

        BlockState activeState = defaultState.setValue(NaniteWebSnareBlock.ACTIVE, true);
        assertTrue(activeState.getValue(NaniteWebSnareBlock.ACTIVE));
    }

    @Test
    void testNeurotoxicSpikesRegistration() {
        assertNotNull(SandStormBlocks.NEUROTOXIC_SPIKES);
        assertTrue(SandStormBlocks.NEUROTOXIC_SPIKES instanceof NeurotoxicSpikesBlock);
    }

    @Test
    void testTacticalDetonatorAndDisarmerKit() {
        assertNotNull(SandStormItems.TACTICAL_DETONATOR);
        assertTrue(SandStormItems.TACTICAL_DETONATOR instanceof TacticalDetonatorItem);

        assertNotNull(SandStormItems.TRAP_DISARMER_KIT);
        assertTrue(SandStormItems.TRAP_DISARMER_KIT instanceof TrapDisarmerKitItem);

        assertNotNull(SandStormEntities.MIRAGE_DECOY);
    }
}
