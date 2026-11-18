package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.item.AutoEmpGrenadeItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class CompanionProtectionHandler {

    public static void initialize() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof AbstractHorse horse && source.is(DamageTypes.IN_WALL)) {
                if (entity.level() instanceof ServerLevel serverLevel) {
                    if (tryRelieveHorseSuffocation(horse, serverLevel)) {
                        return false;
                    }
                }
            }

            if (entity instanceof ServerPlayer player) {
                Entity attacker = source.getEntity();
                if (attacker instanceof Mob) {
                    if (AutoEmpGrenadeItem.triggerEmergencyEmp(player)) {
                        return false;
                    }
                }
            }

            if (CompanionTrapHelper.isTamedCompanion(entity)) {
                if (source.is(DamageTypeTags.IS_FALL)) {
                    return false;
                }

                Entity attacker = source.getEntity();
                if (attacker != null) {
                    if (CompanionTrapHelper.isTamedCompanion(attacker)) {
                        return false;
                    }
                    if (attacker instanceof Player player) {
                        ItemStack held = player.getMainHandItem();
                        if (!held.is(ItemTags.SWORDS)) {
                            return false;
                        }
                    }
                }
            }

            return true;
        });

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (CompanionTrapHelper.isTamedCompanion(entity)) {
                ItemStack held = player.getItemInHand(hand);
                if (!held.is(ItemTags.SWORDS)) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });
    }

    public static boolean tryRelieveHorseSuffocation(AbstractHorse horse, ServerLevel level) {
        BlockPos current = horse.blockPosition();
        int[][] offsets = {
                {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},
                {1, 0, 1}, {-1, 0, -1}, {1, 0, -1}, {-1, 0, 1},
                {2, 0, 0}, {-2, 0, 0}, {0, 0, 2}, {0, 0, -2},
                {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1},
                {0, 2, 0}
        };

        for (int[] off : offsets) {
            BlockPos target = current.offset(off[0], off[1], off[2]);
            if (level.getBlockState(target).isAir()
                    && level.getBlockState(target.above()).isAir()
                    && level.getBlockState(target.below()).isSolid()) {
                horse.teleportTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
                horse.setDeltaMovement(Vec3.ZERO);
                horse.resetFallDistance();
                level.playSound(null, target, SoundEvents.HORSE_STEP, SoundSource.NEUTRAL, 0.9f, 1.2f);
                level.sendParticles(ParticleTypes.POOF, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 6, 0.2, 0.2, 0.2, 0.05);
                return true;
            }
        }

        return false;
    }
}
