package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class RadioactiveWasteBarrelBlockEntity extends BlockEntity {

    public RadioactiveWasteBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.RADIOACTIVE_WASTE_BARREL_BE, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RadioactiveWasteBarrelBlockEntity blockEntity) {
        if (level.isClientSide()) {
            if (level.getRandom().nextFloat() < 0.2f) {
                double px = pos.getX() + 0.2 + level.getRandom().nextDouble() * 0.6;
                double py = pos.getY() + 0.9 + level.getRandom().nextDouble() * 0.3;
                double pz = pos.getZ() + 0.2 + level.getRandom().nextDouble() * 0.6;
                level.addParticle(ParticleTypes.WARPED_SPORE, px, py, pz, 0.0, 0.015, 0.0);
            }
            return;
        }

        if (level.getGameTime() % 20 != 0) {
            return;
        }

        AABB radius = new AABB(pos).inflate(4.0);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, radius);
        for (LivingEntity living : entities) {
            if (living instanceof Player player && (player.isCreative() || player.isSpectator())) {
                continue;
            }
            double distSq = living.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (distSq <= 16.0) {
                int amplifier = distSq <= 4.0 ? 1 : 0;
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, amplifier, false, true, true));
                living.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 0, false, false, false));
            }
        }
    }
}
