package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.block.entity.WirelessSolarReceiverBlockEntity;
import com.fhfelipefh.sandstorm.content.survival.PlayerSuitSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Consumer;

public class AdvancedRefractionLensItem extends Item {
    public AdvancedRefractionLensItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.advanced_refraction_lens_usage").withStyle(ChatFormatting.GRAY));
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.advanced_refraction_lens_desc").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof WirelessSolarReceiverBlockEntity receiver) {
            if (!receiver.hasRefractionLens()) {
                if (!level.isClientSide()) {
                    receiver.setHasRefractionLens(true);
                    Player player = context.getPlayer();
                    if (player != null && !player.isCreative()) {
                        context.getItemInHand().shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.3f);
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.advanced_lens_installed_receiver"));
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.getItemBySlot(EquipmentSlot.CHEST).is(SandStormItems.SPACE_SUIT_CHESTPLATE)) {
            if (!level.isClientSide()) {
                MinecraftServer server = level.getServer();
                if (server != null) {
                    PlayerSuitSavedData data = PlayerSuitSavedData.get(server);
                    if (data.hasUpgrade(player.getUUID(), "advanced_lens")) {
                        if (player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.advanced_lens_already_installed"));
                        }
                        return InteractionResult.FAIL;
                    }
                    data.addUpgrade(player.getUUID(), "advanced_lens");
                    if (!player.isCreative()) {
                        player.getItemInHand(hand).shrink(1);
                    }
                    level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.5f);
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.advanced_lens_installed_suit"));
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(level, player, hand);
    }
}
