package com.fhfelipefh.sandstorm.content.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class SynapticFluidBlock extends LiquidBlock {

    public SynapticFluidBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
        super(fluid, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        if (!level.isClientSide() && entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false, false));
            if (level.getGameTime() % 40 == 0) {
                living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, false, false));
            }
        }
        if (level.isClientSide() && level.getRandom().nextInt(10) == 0) {
            level.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + level.getRandom().nextDouble(), pos.getY() + 0.8, pos.getZ() + level.getRandom().nextDouble(), 0.0, 0.02, 0.0);
        }
    }
}
