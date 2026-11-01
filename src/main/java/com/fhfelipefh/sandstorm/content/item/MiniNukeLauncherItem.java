package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.MiniNukeProjectileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class MiniNukeLauncherItem extends Item {

    public static final int RELOAD_COOLDOWN_TICKS = 100;

    public MiniNukeLauncherItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(128)
                .rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        ItemStack ammoStack = ItemStack.EMPTY;
        if (player.getAbilities().instabuild) {
            ammoStack = new ItemStack(SandStormItems.MINI_NUKE);
        } else {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                if (invStack.is(SandStormItems.MINI_NUKE)) {
                    ammoStack = invStack;
                    break;
                }
            }
        }

        if (ammoStack.isEmpty()) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.translatable("tooltip.sandstorm.mini_nuke_launcher.no_ammo").withStyle(ChatFormatting.RED));
            }
            level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 0.8f);
            return InteractionResult.FAIL;
        }

        if (!player.getAbilities().instabuild) {
            ammoStack.shrink(1);
        }

        heldStack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        player.getCooldowns().addCooldown(heldStack, RELOAD_COOLDOWN_TICKS);

        level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.5f, 0.5f);
        level.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 2.0f, 0.6f);
        level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 0.6f);

        Vec3 look = player.getLookAngle();
        player.push(-look.x * 0.6, 0.1, -look.z * 0.6);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            MiniNukeProjectileEntity nuke = new MiniNukeProjectileEntity(serverLevel, player);
            nuke.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 1.8f, 1.0f);
            serverLevel.addFreshEntity(nuke);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.CROSSBOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.mini_nuke_launcher.damage").withStyle(ChatFormatting.RED));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.mini_nuke_launcher.ammo").withStyle(ChatFormatting.GOLD));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.mini_nuke_launcher.warning").withStyle(ChatFormatting.DARK_GREEN));
    }
}
