package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

import java.util.List;
import java.util.function.Consumer;

public class CryoLanceItem extends Item {

    public static final double RAY_RANGE = 28.0;
    public static final int COOLDOWN_TICKS = 20;
    public static final long ENERGY_COST = 500L;

    public CryoLanceItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(450)
                .rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            boolean poweredBySuit = false;
            if (player instanceof ServerPlayer serverPlayer) {
                SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(serverPlayer);
                if (suit.consumeEnergy(ENERGY_COST)) {
                    poweredBySuit = true;
                }
            }

            if (!poweredBySuit && !player.isCreative()) {
                heldStack.hurtAndBreak(1, serverLevel, player instanceof ServerPlayer sp ? sp : null, item -> {});
            }

            player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);

            Vec3 startPos = player.getEyePosition();
            Vec3 lookVec = player.getLookAngle();
            Vec3 endPos = startPos.add(lookVec.scale(RAY_RANGE));

            BlockHitResult blockHit = serverLevel.clip(new ClipContext(
                    startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, player
            ));

            Vec3 targetPoint = blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation() : endPos;
            double actualDistance = startPos.distanceTo(targetPoint);

            for (double d = 0.5; d < actualDistance; d += 0.5) {
                Vec3 point = startPos.add(lookVec.scale(d));
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, point.x, point.y, point.z, 2, 0.05, 0.05, 0.05, 0.01);
                if (d % 1.5 < 0.5) {
                    serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL, point.x, point.y, point.z, 1, 0.02, 0.02, 0.02, 0.0);
                }
            }

            AABB corridor = new AABB(startPos, targetPoint).inflate(1.2);
            List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, corridor, entity ->
                    entity != player && entity.isAlive() && !entity.isSpectator()
            );

            for (LivingEntity target : targets) {
                target.setTicksFrozen(target.getTicksFrozen() + 300);
                target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 3, false, true, true));

                if (target.isFullyFrozen() || target.getTicksFrozen() >= 140) {
                    target.hurtServer(serverLevel, player.damageSources().freeze(), 32.0f);
                    serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.4f, 1.2f);
                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY() + 0.5, target.getZ(),
                            20, 0.4, 0.6, 0.4, 0.1);
                } else {
                    target.hurtServer(serverLevel, player.damageSources().freeze(), 10.0f);
                    serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.PLAYER_HURT_FREEZE, SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            }

            if (blockHit.getType() == HitResult.Type.BLOCK) {
                BlockPos hitPos = blockHit.getBlockPos();
                BlockState state = serverLevel.getBlockState(hitPos);
                if (state.is(Blocks.WATER)) {
                    serverLevel.setBlockAndUpdate(hitPos, Blocks.ICE.defaultBlockState());
                } else if (state.is(Blocks.LAVA)) {
                    serverLevel.setBlockAndUpdate(hitPos, Blocks.OBSIDIAN.defaultBlockState());
                }
            }

            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOW_GOLEM_SHOOT, SoundSource.PLAYERS, 1.2f, 1.3f);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 0.8f, 1.8f);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.cryo_lance.desc1").withStyle(ChatFormatting.AQUA));
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.cryo_lance.desc2").withStyle(ChatFormatting.DARK_AQUA));
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.cryo_lance.desc3").withStyle(ChatFormatting.BLUE));
    }
}
