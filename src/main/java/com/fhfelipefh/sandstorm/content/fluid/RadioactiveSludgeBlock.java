package com.fhfelipefh.sandstorm.content.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class RadioactiveSludgeBlock extends LiquidBlock {

    public RadioactiveSludgeBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
        super(fluid, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        if (!level.isClientSide() && entity instanceof LivingEntity living) {
            if (living instanceof Player player && (player.isCreative() || player.isSpectator())) {
                return;
            }
            living.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 140, 0));
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1));
            if (level.getGameTime() % 20 == 0) {
                living.hurt(level.damageSources().magic(), 2.0f);
            }
        }
    }
}
