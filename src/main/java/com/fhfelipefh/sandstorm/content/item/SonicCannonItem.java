package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
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
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SonicCannonItem extends Item {
    public static final int COOLDOWN_TICKS = 25;
    public static final double CANNON_RANGE = 24.0;
    public static final float DAMAGE_AMOUNT = 20.0f;

    public SonicCannonItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Vec3 eyePos = player.getEyePosition();
            Vec3 lookVec = player.getLookAngle();
            Vec3 targetCenter = eyePos.add(lookVec.scale(CANNON_RANGE / 2.0));
            AABB effectBounds = new AABB(eyePos, eyePos.add(lookVec.scale(CANNON_RANGE))).inflate(4.0);

            List<LivingEntity> enemies = serverLevel.getEntitiesOfClass(LivingEntity.class, effectBounds, entity ->
                    entity != player && (entity instanceof Enemy || entity instanceof SandwormEntity)
            );

            for (LivingEntity enemy : enemies) {
                enemy.hurtServer(serverLevel, player.damageSources().sonicBoom(player), DAMAGE_AMOUNT);
                Vec3 knockback = lookVec.scale(1.5);
                enemy.setDeltaMovement(knockback.x, 0.4, knockback.z);
            }

            serverLevel.playSound(null, player.blockPosition(), SandStormSoundEvents.SONIC_CANNON_BLAST, SoundSource.PLAYERS, 1.2f, 1.2f);

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.sonic_blast", enemies.size()), true);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
