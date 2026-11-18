package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.ai.CompanionTrapHelper;
import com.fhfelipefh.sandstorm.content.world.EmpParalysisHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Consumer;

public class AutoEmpGrenadeItem extends Item {

    public AutoEmpGrenadeItem(Properties properties) {
        super(properties);
    }

    public static boolean triggerEmergencyEmp(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof AutoEmpGrenadeItem) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                detonate((ServerLevel) player.level(), player);
                return true;
            }
        }
        return false;
    }

    public static void detonate(ServerLevel level, LivingEntity center) {
        double radius = 10.0;
        AABB box = AABB.ofSize(center.position(), radius * 2.0, radius, radius * 2.0);
        List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, box);

        for (Mob mob : nearbyMobs) {
            if (CompanionTrapHelper.isTamedCompanion(mob)) {
                continue;
            }
            EmpParalysisHandler.paralyze(mob, 300);
            mob.hurtServer(level, level.damageSources().magic(), 4.0f);
        }

        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                center.getX(), center.getY() + 1.0, center.getZ(),
                45, 1.8, 0.8, 1.8, 0.15);

        level.playSound(null, center.blockPosition(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.7f, 1.8f);
        level.playSound(null, center.blockPosition(),
                SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.PLAYERS, 1.0f, 0.7f);

        if (center instanceof ServerPlayer sp) {
            sp.sendSystemMessage(Component.translatable("message.sandstorm.auto_emp_detonated"), true);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(held, 20);

        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            detonate((ServerLevel) level, sp);
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, flag);
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.auto_emp_grenade.desc").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.auto_emp_grenade.trigger").withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.auto_emp_grenade.manual").withStyle(ChatFormatting.GOLD));
    }
}
