package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.OverridableMachine;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class NeuralOverrideLanceItem extends Item {
    private static final int OVERRIDE_DURATION = 35;
    private static final double REACH_DISTANCE = 6.0;

    public NeuralOverrideLanceItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        EntityHitResult hit = findTargetMachine(player, REACH_DISTANCE);

        if (hit != null && hit.getEntity() instanceof OverridableMachine machine) {
            if (machine.isOverridden()) {
                if (!level.isClientSide() && player instanceof ServerPlayer sp) {
                    sp.sendSystemMessage(Component.translatable("message.sandstorm.override_already_overridden"));
                }
                return InteractionResult.FAIL;
            }
            player.startUsingItem(hand);
            if (!level.isClientSide()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.5f);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        int ticksUsed = getUseDuration(stack, livingEntity) - remainingUseDuration;
        EntityHitResult hit = findTargetMachine(player, REACH_DISTANCE);

        if (hit == null || !(hit.getEntity() instanceof OverridableMachine machine) || machine.isOverridden()) {
            player.stopUsingItem();
            return;
        }

        Entity target = hit.getEntity();
        Vec3 start = player.getEyePosition();
        Vec3 end = target.getBoundingBox().getCenter();

        if (level.isClientSide()) {
            Vec3 dir = end.subtract(start);
            int steps = 6;
            for (int i = 0; i < steps; i++) {
                double frac = (double) i / (double) steps;
                double px = start.x + dir.x * frac + (player.getRandom().nextDouble() - 0.5) * 0.15;
                double py = start.y + dir.y * frac + (player.getRandom().nextDouble() - 0.5) * 0.15;
                double pz = start.z + dir.z * frac + (player.getRandom().nextDouble() - 0.5) * 0.15;
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0.0, 0.02, 0.0);
            }
        }

        if (ticksUsed % 8 == 0 && !level.isClientSide()) {
            float pitch = 1.0f + (float) ticksUsed / (float) OVERRIDE_DURATION;
            level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 0.8f, pitch);
        }

        if (ticksUsed >= OVERRIDE_DURATION) {
            completeOverride(level, player, target, machine, stack);
            player.stopUsingItem();
        }
    }

    private void completeOverride(Level level, Player player, Entity target, OverridableMachine machine, ItemStack stack) {
        if (level.isClientSide()) {
            return;
        }

        machine.setOverridden(true);
        machine.setOverrideOwner(player.getUUID());

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, target.getX(), target.getY() + 1.0, target.getZ(), 30, 0.5, 0.5, 0.5, 0.15);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY() + 1.0, target.getZ(), 25, 0.4, 0.4, 0.4, 0.2);
        }

        level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.2f, 1.4f);

        if (player instanceof ServerPlayer sp) {
            sp.sendSystemMessage(Component.translatable("message.sandstorm.override_success", target.getName()));
        }

        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    private EntityHitResult findTargetMachine(Player player, double reach) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0f);
        Vec3 reachVec = eyePos.add(lookVec.scale(reach));
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(1.0);

        List<Entity> candidates = player.level().getEntities(player, searchBox, entity ->
                entity instanceof OverridableMachine && !entity.isSpectator() && entity.isAlive()
        );

        Entity closestEntity = null;
        Vec3 closestHit = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity candidate : candidates) {
            AABB box = candidate.getBoundingBox().inflate(0.3);
            Optional<Vec3> clip = box.clip(eyePos, reachVec);
            if (clip.isPresent()) {
                double dist = eyePos.distanceToSqr(clip.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    closestEntity = candidate;
                    closestHit = clip.get();
                }
            }
        }

        if (closestEntity != null) {
            return new EntityHitResult(closestEntity, closestHit);
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.neural_override_lance.desc").withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.neural_override_lance.info").withStyle(ChatFormatting.GRAY));
    }
}
