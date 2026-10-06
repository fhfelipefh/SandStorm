package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.effect.PlasmaBeamVisualEffect;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PlasmaRifleItem extends Item {
    public static final int COOLDOWN_TICKS = 7;
    public static final double RIFLE_RANGE = 48.0;
    public static final float DAMAGE_AMOUNT = 12.0f;
    public static final long ENERGY_COST = 800L;

    public record HitTarget(LivingEntity entity, Vec3 hitPos) {}

    public PlasmaRifleItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        SuitPowerComponent suit = (player instanceof ServerPlayer sp) ? SuitSurvivalHandler.getOrCreateSuit(sp) : SuitSurvivalHandler.getOrCreateSuit(player.getUUID());
        if (!suit.consumeEnergy(ENERGY_COST)) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("tooltip.sandstorm.weapon.no_power").withStyle(ChatFormatting.RED), true);
                serverPlayer.level().playSound(null, player.blockPosition(), SandStormSoundEvents.SUIT_BATTERY_LOW, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            return InteractionResult.FAIL;
        }

        player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Vec3 origin = player.getEyePosition();
            Vec3 direction = player.getLookAngle().normalize();
            HitTarget target = traceHit(serverLevel, player, origin, direction, RIFLE_RANGE);

            if (target.entity() != null) {
                target.entity().hurtServer(serverLevel, player.damageSources().playerAttack(player), DAMAGE_AMOUNT);
                target.entity().igniteForSeconds(3.0f);
            }

            PlasmaBeamVisualEffect.spawnPlasmaBeam(serverLevel, player, target.hitPos(), RIFLE_RANGE);
            serverLevel.playSound(null, player.blockPosition(), SandStormSoundEvents.PLASMA_RIFLE_FIRE, SoundSource.PLAYERS, 1.2f, 1.4f);

            if (player instanceof ServerPlayer serverPlayer && target.entity() != null) {
                serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.plasma_hit", target.entity().getDisplayName()), true);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.CROSSBOW;
    }

    public static HitTarget traceHit(ServerLevel level, Player player, Vec3 origin, Vec3 direction, double maxRange) {
        Vec3 end = origin.add(direction.scale(maxRange));
        BlockHitResult blockHit = level.clip(new ClipContext(origin, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double maxDist = blockHit.getType() != HitResult.Type.MISS ? origin.distanceTo(blockHit.getLocation()) : maxRange;

        AABB searchBox = new AABB(origin, end).inflate(1.0);
        List<LivingEntity> potentialTargets = level.getEntitiesOfClass(LivingEntity.class, searchBox, entity ->
                entity != player && entity.isAlive() && (entity instanceof Enemy || entity instanceof SandwormEntity)
        );

        LivingEntity closestEntity = null;
        double closestDist = maxDist;
        Vec3 hitPos = blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation() : end;

        for (LivingEntity candidate : potentialTargets) {
            AABB targetBox = candidate.getBoundingBox().inflate(0.3);
            Optional<Vec3> optHit = targetBox.clip(origin, end);
            if (optHit.isPresent()) {
                double dist = origin.distanceTo(optHit.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    closestEntity = candidate;
                    hitPos = optHit.get();
                }
            }
        }

        return new HitTarget(closestEntity, hitPos);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.weapon.damage", (int) DAMAGE_AMOUNT).withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.weapon.energy_cost", ENERGY_COST).withStyle(ChatFormatting.GOLD));
    }
}
