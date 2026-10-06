package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.effect.PlasmaBeamVisualEffect;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class HeavyPlasmaCannonItem extends Item {
    public static final int COOLDOWN_TICKS = 18;
    public static final double CANNON_RANGE = 72.0;
    public static final float DAMAGE_AMOUNT = 32.0f;
    public static final long ENERGY_COST = 1800L;

    public HeavyPlasmaCannonItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .rarity(Rarity.EPIC));
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
            Vec3 end = origin.add(direction.scale(CANNON_RANGE));

            BlockHitResult blockHit = serverLevel.clip(new ClipContext(origin, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            Vec3 beamEnd = blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation() : end;

            List<LivingEntity> piercedTargets = tracePiercingTargets(serverLevel, player, origin, beamEnd);
            for (LivingEntity target : piercedTargets) {
                target.hurtServer(serverLevel, player.damageSources().playerAttack(player), DAMAGE_AMOUNT);
                target.igniteForSeconds(5.0f);
            }

            if (blockHit.getType() == HitResult.Type.BLOCK) {
                scorchBlockAtHit(serverLevel, blockHit.getBlockPos());
            }

            PlasmaBeamVisualEffect.spawnPlasmaBeam(serverLevel, player, beamEnd, CANNON_RANGE);
            serverLevel.playSound(null, player.blockPosition(), SandStormSoundEvents.PLASMA_RIFLE_FIRE, SoundSource.PLAYERS, 1.5f, 0.8f);

            if (player instanceof ServerPlayer serverPlayer && !piercedTargets.isEmpty()) {
                serverPlayer.sendSystemMessage(
                        Component.translatable("telemetry.sandstorm.heavy_plasma_pierce", piercedTargets.size()),
                        true
                );
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.CROSSBOW;
    }

    public static List<LivingEntity> tracePiercingTargets(ServerLevel level, Player shooter, Vec3 origin, Vec3 beamEnd) {
        AABB searchBox = new AABB(origin, beamEnd).inflate(1.5);
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, searchBox, entity ->
                entity != shooter && entity.isAlive() && (entity instanceof Enemy || entity instanceof SandwormEntity)
        );

        List<LivingEntity> hits = new ArrayList<>();
        for (LivingEntity candidate : candidates) {
            AABB box = candidate.getBoundingBox().inflate(0.5);
            Optional<Vec3> optHit = box.clip(origin, beamEnd);
            if (optHit.isPresent()) {
                hits.add(candidate);
            }
        }
        return hits;
    }

    private static void scorchBlockAtHit(ServerLevel level, BlockPos pos) {
        BlockState hitState = level.getBlockState(pos);
        if (hitState.is(Blocks.SAND) || hitState.is(Blocks.RED_SAND)) {
            level.setBlock(pos, Blocks.GLASS.defaultBlockState(), 3);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.weapon.damage", (int) DAMAGE_AMOUNT).withStyle(ChatFormatting.RED));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.weapon.energy_cost", ENERGY_COST).withStyle(ChatFormatting.GOLD));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.heavy_plasma.pierce").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
