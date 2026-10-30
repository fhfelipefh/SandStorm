package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.entity.ApexSandwormEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SeismicResonanceCatalystBlock extends Block {

    public SeismicResonanceCatalystBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (pos.getY() > 0) {
            player.sendSystemMessage(Component.translatable("message.sandstorm.catalyst_depth_error"));
            level.playSound(null, pos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0f, 0.8f);
            return InteractionResult.CONSUME;
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 3, 0.0, 0.0, 0.0, 0.0);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 2, 0.5, 0.5, 0.5, 0.0);
            serverLevel.playSound(null, pos, SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.5f, 0.7f);

            ApexSandwormEntity boss = SandStormEntities.APEX_SANDWORM.create(serverLevel, EntitySpawnReason.EVENT);
            if (boss != null) {
                boss.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                boss.setYRot(serverLevel.getRandom().nextFloat() * 360.0f);
                serverLevel.addFreshEntity(boss);
                player.sendSystemMessage(Component.translatable("message.sandstorm.catalyst_cleared"));
            }

            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }

        return InteractionResult.SUCCESS;
    }
}
