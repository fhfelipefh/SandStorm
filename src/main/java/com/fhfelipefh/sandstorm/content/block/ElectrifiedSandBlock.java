package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SpaceSuitItem;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ElectrifiedSandBlock extends FallingBlock {

    public static final TagKey<EntityType<?>> MAGNETIC_FOREST_NATIVES =
            TagKey.create(Registries.ENTITY_TYPE, SandStormMod.id("magnetic_forest_native"));

    public ElectrifiedSandBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return 0xFFE5A000;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity living) {
            if (living.getType().builtInRegistryHolder().is(MAGNETIC_FOREST_NATIVES)) {
                return;
            }
            ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);
            if (!(boots.getItem() instanceof SpaceSuitItem)) {
                living.hurtServer(serverLevel, serverLevel.damageSources().lightningBolt(), 1.5f);
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 30, 0));
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, living.getX(), living.getY() + 0.5, living.getZ(), 6, 0.2, 0.2, 0.2, 0.05);
                serverLevel.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 0.4f, 1.8f);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.15f) {
            double px = pos.getX() + random.nextDouble();
            double py = pos.getY() + 1.05;
            double pz = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0.0, 0.04, 0.0);
        }
    }
}
