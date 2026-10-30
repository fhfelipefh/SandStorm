package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Consumer;

public class ApexRailgunHarpoonItem extends Item {
    private static final double RANGE = 48.0;

    public ApexRailgunHarpoonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0f);
        Vec3 maxEnd = eyePos.add(look.scale(RANGE));

        BlockHitResult blockHit = level.clip(new ClipContext(eyePos, maxEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 hitPos = blockHit.getLocation();

        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 2.0f, 1.8f);

        double dist = eyePos.distanceTo(hitPos);
        int particleCount = (int) (dist * 4);
        for (int i = 0; i < particleCount; i++) {
            double progress = (double) i / particleCount;
            Vec3 pPos = eyePos.lerp(hitPos, progress);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pPos.x, pPos.y, pPos.z, 2, 0.05, 0.05, 0.05, 0.02);
            if (i % 6 == 0) {
                serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, pPos.x, pPos.y, pPos.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }

        AABB beamBox = new AABB(eyePos, hitPos).inflate(1.2);
        List<LivingEntity> potentialTargets = serverLevel.getEntitiesOfClass(LivingEntity.class, beamBox, e -> e != player && e.isAlive());

        for (LivingEntity target : potentialTargets) {
            Vec3 targetCenter = target.position().add(0, target.getBbHeight() / 2.0, 0);
            double distToRay = targetCenter.subtract(eyePos).cross(look).length() / look.length();

            if (distToRay <= target.getBbWidth() + 0.5) {
                target.hurtServer(serverLevel, level.damageSources().playerAttack(player), 32.0f);
                target.push(look.x * 1.5, 0.4, look.z * 1.5);
            }
        }

        player.getCooldowns().addCooldown(stack, 40);
        stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, tooltipFlag);
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.apex_harpoon"));
    }
}
