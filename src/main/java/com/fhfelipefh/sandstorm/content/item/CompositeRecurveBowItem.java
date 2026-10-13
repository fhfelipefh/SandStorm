package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class CompositeRecurveBowItem extends BowItem {
    public static final int DRAW_TIME_TICKS = 15;
    public static final float BASE_PROJECTILE_VELOCITY = 3.8f;

    public CompositeRecurveBowItem(Properties properties) {
        super(properties);
    }

    public int getDrawTimeTicks() {
        return DRAW_TIME_TICKS;
    }

    public float getProjectileVelocity() {
        return BASE_PROJECTILE_VELOCITY;
    }

    public static float getCustomPowerForTime(int charge) {
        float f = (float) charge / 15.0f;
        f = (f * f + f * 2.0f) / 3.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) {
            return false;
        }
        ItemStack projectileStack = player.getProjectile(stack);
        if (projectileStack.isEmpty()) {
            return false;
        }
        int charge = this.getUseDuration(stack, livingEntity) - timeLeft;
        float power = getCustomPowerForTime(charge);
        if (power < 0.1f) {
            return false;
        }
        List<ItemStack> list = draw(stack, projectileStack, player);
        if (level instanceof ServerLevel serverLevel && !list.isEmpty()) {
            this.shoot(serverLevel, player, player.getUsedItemHand(), stack, list, power * 3.8f, 0.4f, power == 1.0f, null);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f / (level.getRandom().nextFloat() * 0.4f + 1.2f) + power * 0.5f);
        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }
}
