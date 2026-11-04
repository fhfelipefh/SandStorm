package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.block.CollapsibleTrapdoorBlock;
import com.fhfelipefh.sandstorm.content.block.CorrosiveChitinSpikeWallBlock;
import com.fhfelipefh.sandstorm.content.block.CrushingSpikeGateBlock;
import com.fhfelipefh.sandstorm.content.block.CryoFreezeMineBlock;
import com.fhfelipefh.sandstorm.content.block.ElectrifiedSpikeBarrierBlock;
import com.fhfelipefh.sandstorm.content.block.FiberglassSpikesBlock;
import com.fhfelipefh.sandstorm.content.block.KineticEjectorTrapBlock;
import com.fhfelipefh.sandstorm.content.block.KineticFloorSpikesBlock;
import com.fhfelipefh.sandstorm.content.block.MicroSingularityTrapBlock;
import com.fhfelipefh.sandstorm.content.block.NaniteQuicksandBlock;
import com.fhfelipefh.sandstorm.content.block.NaniteWebSnareBlock;
import com.fhfelipefh.sandstorm.content.block.NeurotoxicSpikesBlock;
import com.fhfelipefh.sandstorm.content.block.PiezoShockTrapBlock;
import com.fhfelipefh.sandstorm.content.block.RetractableSpikeWallBlock;
import com.fhfelipefh.sandstorm.content.block.SerratedJawTrapBlock;
import com.fhfelipefh.sandstorm.content.block.SubglacialIceSpikeBlock;
import com.fhfelipefh.sandstorm.content.block.ThermalVentTrapBlock;
import com.fhfelipefh.sandstorm.content.block.TitaniumSpikeWallBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CompanionTrapHelper {
    public static boolean isTamedCompanion(Entity entity) {
        if (entity instanceof TamableAnimal tamable) {
            return tamable.isTame();
        }
        if (entity instanceof AbstractHorse horse) {
            return horse.isTamed();
        }
        return false;
    }

    public static boolean isSpikeTrapBlock(BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }
        Block block = state.getBlock();
        return block instanceof KineticFloorSpikesBlock
                || block instanceof NeurotoxicSpikesBlock
                || block instanceof FiberglassSpikesBlock
                || block instanceof SubglacialIceSpikeBlock
                || block instanceof TitaniumSpikeWallBlock
                || block instanceof RetractableSpikeWallBlock
                || block instanceof ElectrifiedSpikeBarrierBlock
                || block instanceof CorrosiveChitinSpikeWallBlock
                || block instanceof CrushingSpikeGateBlock;
    }

    public static boolean isTrapOrHazardBlock(BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }
        if (isSpikeTrapBlock(state)) {
            return true;
        }
        Block block = state.getBlock();
        return block instanceof SerratedJawTrapBlock
                || block instanceof PiezoShockTrapBlock
                || block instanceof CryoFreezeMineBlock
                || block instanceof ThermalVentTrapBlock
                || block instanceof KineticEjectorTrapBlock
                || block instanceof MicroSingularityTrapBlock
                || block instanceof NaniteWebSnareBlock
                || block instanceof NaniteQuicksandBlock
                || block instanceof CollapsibleTrapdoorBlock
                || state.is(BlockTags.TRAPDOORS)
                || state.is(Blocks.CACTUS)
                || state.is(Blocks.SWEET_BERRY_BUSH)
                || state.is(Blocks.POINTED_DRIPSTONE);
    }
}

