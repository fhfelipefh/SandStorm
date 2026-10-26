package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.block.CollapsibleTrapdoorBlock;
import com.fhfelipefh.sandstorm.content.block.CryoFreezeMineBlock;
import com.fhfelipefh.sandstorm.content.block.KineticEjectorTrapBlock;
import com.fhfelipefh.sandstorm.content.block.MicroSingularityTrapBlock;
import com.fhfelipefh.sandstorm.content.block.MirageHologramTrapBlock;
import com.fhfelipefh.sandstorm.content.block.NaniteWebSnareBlock;
import com.fhfelipefh.sandstorm.content.block.PiezoShockTrapBlock;
import com.fhfelipefh.sandstorm.content.block.SeismicDecoyTrapBlock;
import com.fhfelipefh.sandstorm.content.block.ThermalVentTrapBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TacticalDetonatorItem extends Item {
    private static final Map<UUID, List<BlockPos>> LINKED_TRAPS = new HashMap<>();

    public TacticalDetonatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (isTriggerableTrap(block)) {
            if (!level.isClientSide()) {
                UUID uuid = player.getUUID();
                List<BlockPos> list = LINKED_TRAPS.computeIfAbsent(uuid, k -> new ArrayList<>());
                if (list.contains(pos)) {
                    list.remove(pos);
                    player.sendSystemMessage(Component.translatable("message.sandstorm.detonator_unlinked"));
                } else {
                    if (list.size() >= 8) {
                        list.remove(0);
                    }
                    list.add(pos);
                    player.sendSystemMessage(Component.translatable("message.sandstorm.detonator_linked"));
                }
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.8f, 1.5f);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        UUID uuid = player.getUUID();
        List<BlockPos> list = LINKED_TRAPS.get(uuid);
        if (list == null || list.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.sandstorm.detonator_empty"));
            return InteractionResult.PASS;
        }

        int triggeredCount = 0;
        BlockPos playerPos = player.blockPosition();

        for (BlockPos targetPos : new ArrayList<>(list)) {
            if (targetPos.distSqr(playerPos) <= 64 * 64 && level.isLoaded(targetPos)) {
                BlockState state = level.getBlockState(targetPos);
                Block block = state.getBlock();
                if (triggerTrap(level, targetPos, state, block)) {
                    triggeredCount++;
                }
            }
        }

        if (triggeredCount > 0) {
            level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 1.0f, 1.8f);
            player.sendSystemMessage(Component.translatable("message.sandstorm.detonator_fired"));
            player.getCooldowns().addCooldown(stack, 20);
            return InteractionResult.SUCCESS;
        }

        player.sendSystemMessage(Component.translatable("message.sandstorm.detonator_out_of_range"));
        return InteractionResult.PASS;
    }

    private boolean isTriggerableTrap(Block block) {
        return block instanceof MicroSingularityTrapBlock
                || block instanceof PiezoShockTrapBlock
                || block instanceof ThermalVentTrapBlock
                || block instanceof CryoFreezeMineBlock
                || block instanceof SeismicDecoyTrapBlock
                || block instanceof CollapsibleTrapdoorBlock
                || block instanceof KineticEjectorTrapBlock
                || block instanceof MirageHologramTrapBlock
                || block instanceof NaniteWebSnareBlock;
    }

    private boolean triggerTrap(Level level, BlockPos pos, BlockState state, Block block) {
        if (block instanceof MicroSingularityTrapBlock singularity) {
            if (!state.getValue(MicroSingularityTrapBlock.ACTIVE)) {
                singularity.triggerSingularity(level, pos, state);
                return true;
            }
        } else if (block instanceof PiezoShockTrapBlock) {
            if (state.getValue(PiezoShockTrapBlock.CHARGED)) {
                level.setBlock(pos, state.setValue(PiezoShockTrapBlock.CHARGED, false), 3);
                level.scheduleTick(pos, block, 100);
                level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 1.0f, 1.6f);
                return true;
            }
        } else if (block instanceof ThermalVentTrapBlock) {
            if (!state.getValue(ThermalVentTrapBlock.ACTIVE)) {
                level.setBlock(pos, state.setValue(ThermalVentTrapBlock.ACTIVE, true), 3);
                level.scheduleTick(pos, block, 40);
                level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                return true;
            }
        } else if (block instanceof CryoFreezeMineBlock cryo) {
            if (!state.getValue(CryoFreezeMineBlock.ACTIVE)) {
                cryo.triggerCryoDetonation(level, pos, state);
                return true;
            }
        } else if (block instanceof SeismicDecoyTrapBlock decoy) {
            if (!state.getValue(SeismicDecoyTrapBlock.ACTIVE)) {
                decoy.triggerDecoy(level, pos, state);
                return true;
            }
        } else if (block instanceof CollapsibleTrapdoorBlock) {
            if (!state.getValue(CollapsibleTrapdoorBlock.OPEN)) {
                level.setBlock(pos, state.setValue(CollapsibleTrapdoorBlock.OPEN, true), 3);
                level.scheduleTick(pos, block, 50);
                level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                return true;
            }
        } else if (block instanceof KineticEjectorTrapBlock) {
            if (state.getValue(KineticEjectorTrapBlock.CHARGED)) {
                level.setBlock(pos, state.setValue(KineticEjectorTrapBlock.CHARGED, false), 3);
                level.scheduleTick(pos, block, 60);
                level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 1.0f, 1.4f);
                return true;
            }
        } else if (block instanceof MirageHologramTrapBlock mirage) {
            if (!state.getValue(MirageHologramTrapBlock.ACTIVE)) {
                mirage.triggerHologram(level, pos, state);
                return true;
            }
        } else if (block instanceof NaniteWebSnareBlock snare) {
            if (!state.getValue(NaniteWebSnareBlock.ACTIVE)) {
                snare.triggerSnare(level, pos, state, null);
                return true;
            }
        }
        return false;
    }
}
