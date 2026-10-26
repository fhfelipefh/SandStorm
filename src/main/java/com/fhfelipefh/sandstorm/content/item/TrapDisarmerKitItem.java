package com.fhfelipefh.sandstorm.content.item;

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
import com.fhfelipefh.sandstorm.content.block.SeismicDecoyTrapBlock;
import com.fhfelipefh.sandstorm.content.block.SerratedJawTrapBlock;
import com.fhfelipefh.sandstorm.content.block.ThermalVentTrapBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TrapDisarmerKitItem extends Item {

    public TrapDisarmerKitItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (isDisarmableTrap(block)) {
            if (!level.isClientSide()) {
                Block.popResource(level, pos, new ItemStack(block.asItem()));
                level.removeBlock(pos, false);

                Player player = context.getPlayer();
                if (player != null) {
                    context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                    player.sendSystemMessage(Component.translatable("message.sandstorm.trap_disarmed"));
                }

                level.playSound(null, pos, SoundEvents.COPPER_BREAK, SoundSource.BLOCKS, 0.9f, 1.4f);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SCRAPE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 12, 0.25, 0.25, 0.25, 0.05);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private boolean isDisarmableTrap(Block block) {
        return block instanceof SerratedJawTrapBlock
                || block instanceof PiezoShockTrapBlock
                || block instanceof CollapsibleTrapdoorBlock
                || block instanceof KineticEjectorTrapBlock
                || block instanceof ThermalVentTrapBlock
                || block instanceof MicroSingularityTrapBlock
                || block instanceof NaniteQuicksandBlock
                || block instanceof HoloLaserTripwireBlock
                || block instanceof SeismicDecoyTrapBlock
                || block instanceof CryoFreezeMineBlock
                || block instanceof MirageHologramTrapBlock
                || block instanceof NaniteWebSnareBlock
                || block instanceof NeurotoxicSpikesBlock;
    }
}
