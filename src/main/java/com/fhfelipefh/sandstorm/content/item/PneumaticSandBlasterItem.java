package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PneumaticSandBlasterItem extends Item {
    public static final int MAX_USE_DURATION = 72000;
    public static final int MIN_CHARGE_TICKS = 6;
    public static final int FULL_CHARGE_TICKS = 14;
    public static final double MAX_BLAST_RANGE = 24.0;
    public static final int COOLDOWN_TICKS = 12;

    public PneumaticSandBlasterItem(Properties properties) {
        super(properties
                .durability(384)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return MAX_USE_DURATION;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.CROSSBOW;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }
        int charge = this.getUseDuration(stack, livingEntity) - remainingUseDuration;
        if (level instanceof ServerLevel serverLevel) {
            if (charge % 4 == 0) {
                BlockParticleOption sandDust = new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState());
                Vec3 feet = player.position();
                for (int i = 0; i < 3; i++) {
                    double ox = (player.getRandom().nextDouble() - 0.5) * 1.5;
                    double oz = (player.getRandom().nextDouble() - 0.5) * 1.5;
                    serverLevel.sendParticles(sandDust, feet.x + ox, feet.y + 0.1, feet.z + oz, 1, -ox * 0.15, 0.25, -oz * 0.15, 0.05);
                }
            }
            if (charge == FULL_CHARGE_TICKS) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8f, 1.9f);
            }
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) {
            return false;
        }
        int charge = this.getUseDuration(stack, livingEntity) - timeLeft;
        if (charge < MIN_CHARGE_TICKS) {
            return false;
        }

        boolean sandAvailable = isSandAvailable(level, player);
        if (!sandAvailable) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 1.6f);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.pneumatic_sand_blaster.no_sand"), true);
            }
            return false;
        }

        float chargeFraction = Math.min(1.0f, (float) charge / (float) FULL_CHARGE_TICKS);

        if (level instanceof ServerLevel serverLevel) {
            Vec3 eyePos = player.getEyePosition();
            Vec3 lookVec = player.getLookAngle();
            double currentRange = MAX_BLAST_RANGE * chargeFraction;
            Vec3 targetEnd = eyePos.add(lookVec.scale(currentRange));

            ClipContext clipContext = new ClipContext(eyePos, targetEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
            BlockHitResult blockHit = level.clip(clipContext);
            Vec3 impactPos = blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation() : targetEnd;
            double effectiveDistance = eyePos.distanceTo(impactPos);

            AABB searchBox = new AABB(eyePos, impactPos).inflate(1.2);
            List<LivingEntity> possibleTargets = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                    e -> e != player && e.isAlive() && !e.isSpectator() && !player.isAlliedTo(e));

            LivingEntity closestTarget = null;
            double closestDistanceSq = Double.MAX_VALUE;
            for (LivingEntity entity : possibleTargets) {
                AABB bb = entity.getBoundingBox().inflate(0.3);
                Optional<Vec3> hit = bb.clip(eyePos, impactPos);
                if (hit.isPresent()) {
                    double distSq = eyePos.distanceToSqr(hit.get());
                    if (distSq < closestDistanceSq) {
                        closestDistanceSq = distSq;
                        closestTarget = entity;
                    }
                }
            }

            if (closestTarget != null) {
                impactPos = closestTarget.position().add(0, closestTarget.getBbHeight() * 0.5, 0);
                effectiveDistance = eyePos.distanceTo(impactPos);
                applyBlastDamage(serverLevel, player, closestTarget, effectiveDistance, chargeFraction, lookVec);
            }

            spawnBlastParticles(serverLevel, eyePos, impactPos, effectiveDistance);
            playBlastSounds(serverLevel, player);
        }

        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
        return true;
    }

    public static float calculateDamage(double distance, float chargeFraction) {
        float baseDamage;
        if (distance <= 4.0) {
            baseDamage = 10.0f;
        } else if (distance <= 12.0) {
            baseDamage = 6.5f;
        } else if (distance <= 24.0) {
            baseDamage = 3.5f;
        } else {
            baseDamage = 0.0f;
        }
        return baseDamage * chargeFraction;
    }

    private void applyBlastDamage(ServerLevel serverLevel, Player player, LivingEntity target,
                                  double distance, float chargeFraction, Vec3 lookVec) {
        float damage = calculateDamage(distance, chargeFraction);
        if (damage > 0.0f) {
            target.hurtServer(serverLevel, player.damageSources().playerAttack(player), damage);
            double knockbackPower = distance <= 4.0 ? 1.2 * chargeFraction : (distance <= 12.0 ? 0.6 * chargeFraction : 0.3 * chargeFraction);
            Vec3 push = lookVec.scale(knockbackPower);
            target.setDeltaMovement(target.getDeltaMovement().add(push.x, 0.15, push.z));

            if (distance <= 4.0) {
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30, 0));
            } else if (distance > 12.0) {
                target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0));
            }
        }
    }

    private void spawnBlastParticles(ServerLevel serverLevel, Vec3 start, Vec3 end, double distance) {
        int steps = (int) Math.max(6.0, distance * 3.0);
        Vec3 path = end.subtract(start);
        BlockParticleOption sandDust = new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState());

        for (int i = 0; i <= steps; i++) {
            double fraction = i / (double) steps;
            Vec3 pos = start.add(path.scale(fraction));
            serverLevel.sendParticles(sandDust, pos.x, pos.y, pos.z, 2, 0.04, 0.04, 0.04, 0.01);
            if (i % 3 == 0) {
                serverLevel.sendParticles(ParticleTypes.CRIT, pos.x, pos.y, pos.z, 1, 0.01, 0.01, 0.01, 0.01);
            }
        }

        serverLevel.sendParticles(sandDust, end.x, end.y, end.z, 16, 0.2, 0.2, 0.2, 0.04);
        serverLevel.sendParticles(ParticleTypes.POOF, end.x, end.y, end.z, 4, 0.1, 0.1, 0.1, 0.02);
    }

    private void playBlastSounds(ServerLevel serverLevel, Player player) {
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SAND_BREAK, SoundSource.PLAYERS, 1.2f, 1.6f);
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 0.8f, 1.7f);
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.DISPENSER_LAUNCH, SoundSource.PLAYERS, 0.9f, 1.2f);
    }

    public static boolean isSandAvailable(Level level, Player player) {
        if (hasSandInInventory(player)) {
            return true;
        }

        Holder<Biome> biomeHolder = level.getBiome(player.blockPosition());
        if (isDesertOrBadlandsBiome(biomeHolder)) {
            return true;
        }

        BlockPos center = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-3, -2, -3), center.offset(3, 1, 3))) {
            BlockState state = level.getBlockState(pos);
            if (isSandOrGranularBlock(state)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSandInInventory(Player player) {
        int size = player.getInventory().getContainerSize();
        for (int i = 0; i < size; i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (!invStack.isEmpty()) {
                try {
                    if (invStack.is(ItemTags.SAND) || invStack.is(Blocks.SAND.asItem()) || invStack.is(Blocks.RED_SAND.asItem())) {
                        return true;
                    }
                } catch (Exception ignored) {
                    if (invStack.is(Blocks.SAND.asItem()) || invStack.is(Blocks.RED_SAND.asItem())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isDesertOrBadlandsBiome(Holder<Biome> biomeHolder) {
        try {
            if (biomeHolder.is(BiomeTags.IS_BADLANDS) || biomeHolder.is(BiomeTags.HAS_DESERT_PYRAMID)) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return biomeHolder.unwrapKey().map(key -> {
            String path = key.identifier().getPath();
            return path.contains("desert") || path.contains("badlands") || path.contains("dune");
        }).orElse(false);
    }

    private static boolean isSandOrGranularBlock(BlockState state) {
        try {
            if (state.is(BlockTags.SAND)) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return state.is(Blocks.SAND)
                || state.is(Blocks.RED_SAND)
                || state.is(Blocks.SANDSTONE)
                || state.is(Blocks.RED_SANDSTONE)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.SUSPICIOUS_SAND)
                || state.is(Blocks.SUSPICIOUS_GRAVEL);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.pneumatic_sand_blaster.desc").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.pneumatic_sand_blaster.damage").withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.pneumatic_sand_blaster.ammo").withStyle(ChatFormatting.GOLD));
    }
}

