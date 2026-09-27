package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class HypoInjectorItem extends Item {

    public HypoInjectorItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(256)
                .rarity(Rarity.RARE));
    }

    public static ItemStack findStim(Player player) {
        if (player == null) {
            return ItemStack.EMPTY;
        }

        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() instanceof PharmacologicalStimItem) {
            return offhand;
        }

        if (player.getHealth() < 12.0f) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.is(SandStormItems.BIOFOAM_CARTRIDGE)) {
                    return stack;
                }
            }
        }

        boolean hasHarmfulEffect = player.getActiveEffects().stream()
                .anyMatch(instance -> !instance.getEffect().value().isBeneficial());
        if (hasHarmfulEffect) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.is(SandStormItems.DETOX_AMPOULE)) {
                    return stack;
                }
            }
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof PharmacologicalStimItem) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public static int countTotalStims(Player player) {
        if (player == null) {
            return 0;
        }
        int count = 0;
        if (player.getOffhandItem().getItem() instanceof PharmacologicalStimItem) {
            count += player.getOffhandItem().getCount();
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof PharmacologicalStimItem) {
                count += stack.getCount();
            }
        }
        return count;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack injector = player.getItemInHand(hand);
        ItemStack stimStack = findStim(player);

        if (stimStack == null || stimStack.isEmpty()) {
            if (!level.isClientSide()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8f, 1.2f);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.hypo_injector.no_stim").withStyle(ChatFormatting.RED), true);
                }
            }
            return InteractionResult.FAIL;
        }

        if (stimStack.getItem() instanceof PharmacologicalStimItem stimItem) {
            if (!level.isClientSide()) {
                stimItem.applyStimEffect(player);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SandStormSoundEvents.HYPO_INJECTOR_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

                if (!player.getAbilities().instabuild) {
                    stimStack.shrink(1);
                    ItemStack emptyCartridge = new ItemStack(SandStormItems.EMPTY_CARTRIDGE);
                    if (!player.getInventory().add(emptyCartridge)) {
                        if (level instanceof ServerLevel serverLevel) {
                            player.spawnAtLocation(serverLevel, emptyCartridge);
                        }
                    }
                    injector.hurtAndBreak(1, player, player.getEquipmentSlotForItem(injector));
                }

                player.getCooldowns().addCooldown(injector, 4);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.hypo_injector.injected", stimStack.getHoverName()).withStyle(ChatFormatting.GREEN), true);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.hypo_injector.header").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.hypo_injector.desc").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.hypo_injector.usage").withStyle(ChatFormatting.YELLOW));
    }
}
