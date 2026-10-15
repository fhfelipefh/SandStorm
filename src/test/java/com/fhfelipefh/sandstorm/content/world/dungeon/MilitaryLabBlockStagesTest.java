package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MilitaryLabBlockStagesTest {

    @Test
    void allMilitaryPlateStagesMustBeRegistered() {
        List<Block> plateStages = List.of(
                SandStormBlocks.MILITARY_ARMOR_PLATE,
                SandStormBlocks.WEATHERED_MILITARY_PLATE,
                SandStormBlocks.RUSTED_MILITARY_PLATE,
                SandStormBlocks.FRACTURED_MILITARY_PLATE
        );
        for (Block block : plateStages) {
            assertNotNull(block);
            assertNotNull(block.defaultBlockState());
        }
    }

    @Test
    void allHazardStripeStagesMustBeRegistered() {
        List<Block> stripeStages = List.of(
                SandStormBlocks.HAZARD_STRIPE_BLOCK,
                SandStormBlocks.WEATHERED_HAZARD_STRIPE,
                SandStormBlocks.CORRODED_HAZARD_STRIPE
        );
        for (Block block : stripeStages) {
            assertNotNull(block);
            assertNotNull(block.defaultBlockState());
        }
    }

    @Test
    void allVentDuctStagesMustBeRegistered() {
        List<Block> ductStages = List.of(
                SandStormBlocks.REINFORCED_VENT_DUCT,
                SandStormBlocks.RUSTED_VENT_DUCT,
                SandStormBlocks.DAMAGED_VENT_DUCT
        );
        for (Block block : ductStages) {
            assertNotNull(block);
            assertNotNull(block.defaultBlockState());
        }
    }

    @Test
    void allSteelVentGrateStagesMustBeRegistered() {
        List<Block> grateStages = List.of(
                SandStormBlocks.STEEL_VENT_GRATE,
                SandStormBlocks.RUSTED_STEEL_VENT_GRATE,
                SandStormBlocks.BROKEN_STEEL_VENT_GRATE
        );
        for (Block block : grateStages) {
            assertNotNull(block);
            assertNotNull(block.defaultBlockState());
        }
    }

    @Test
    void allMunitionsCrateAndRadarConsoleStagesMustBeRegistered() {
        assertNotNull(SandStormBlocks.MILITARY_MUNITIONS_CRATE);
        assertNotNull(SandStormBlocks.DAMAGED_MUNITIONS_CRATE);
        assertNotNull(SandStormBlocks.TACTICAL_RADAR_CONSOLE);
        assertNotNull(SandStormBlocks.BROKEN_RADAR_CONSOLE);
    }

    @Test
    void clutterPassMustInstantiateCleanly() {
        MilitaryLabClutterPass pass = new MilitaryLabClutterPass();
        assertNotNull(pass);
    }
}
