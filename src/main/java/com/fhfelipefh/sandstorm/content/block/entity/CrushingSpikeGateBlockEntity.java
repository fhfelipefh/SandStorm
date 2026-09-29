package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.CrushingSpikeGateBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class CrushingSpikeGateBlockEntity extends BlockEntity {
    private float progress;
    private float prevProgress;
    private boolean hasCrushedThisCycle;

    public CrushingSpikeGateBlockEntity(BlockPos pos, BlockState blockState) {
        super(SandStormBlocks.CRUSHING_SPIKE_GATE_BE, pos, blockState);
        this.progress = blockState.getValue(CrushingSpikeGateBlock.OPEN) ? 0.0f : 1.0f;
        this.prevProgress = this.progress;
        this.hasCrushedThisCycle = !blockState.getValue(CrushingSpikeGateBlock.OPEN);
    }

    public CrushingSpikeGateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.progress = blockState.getValue(CrushingSpikeGateBlock.OPEN) ? 0.0f : 1.0f;
        this.prevProgress = this.progress;
        this.hasCrushedThisCycle = !blockState.getValue(CrushingSpikeGateBlock.OPEN);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrushingSpikeGateBlockEntity entity) {
        entity.prevProgress = entity.progress;
        boolean open = state.getValue(CrushingSpikeGateBlock.OPEN);

        if (open && entity.progress > 0.0f) {
            entity.progress = Math.max(0.0f, entity.progress - 0.1f);
            entity.hasCrushedThisCycle = false;
        } else if (!open && entity.progress < 1.0f) {
            entity.progress = Math.min(1.0f, entity.progress + 0.3f);
        }

        if (level != null && !level.isClientSide() && !open && entity.progress >= 0.8f && !entity.hasCrushedThisCycle) {
            entity.hasCrushedThisCycle = true;
            level.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.7f, 1.2f);
            
            if (level instanceof ServerLevel serverLevel) {
                AABB aabb = new AABB(pos);
                List<LivingEntity> victims = serverLevel.getEntitiesOfClass(LivingEntity.class, aabb);
                for (LivingEntity victim : victims) {
                    victim.hurtServer(serverLevel, serverLevel.damageSources().generic(), 12.0f);
                }
            }
        }
    }

    public float getProgress(float partialTick) {
        return this.prevProgress + (this.progress - this.prevProgress) * partialTick;
    }
}
