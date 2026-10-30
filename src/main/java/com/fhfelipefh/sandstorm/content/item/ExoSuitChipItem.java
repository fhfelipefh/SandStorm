package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.ExoSuitChipComponent;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class ExoSuitChipItem extends Item {
    private final ExoSuitChipComponent chipType;

    public ExoSuitChipItem(ExoSuitChipComponent chipType, Properties properties) {
        super(properties);
        this.chipType = chipType;
    }

    public ExoSuitChipComponent getChipType() {
        return chipType;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!(chestplate.getItem() instanceof SpaceSuitItem)) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.translatable("message.sandstorm.chip_require_suit"));
            }
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        SuitPowerComponent suit = (player instanceof ServerPlayer sp)
                ? SuitSurvivalHandler.getOrCreateSuit(sp)
                : SuitSurvivalHandler.getOrCreateSuit(player.getUUID());

        if (suit.hasChip(this.chipType)) {
            player.sendSystemMessage(Component.translatable("message.sandstorm.chip_already_installed"));
            return InteractionResult.FAIL;
        }

        if (!suit.installChip(this.chipType)) {
            player.sendSystemMessage(Component.translatable("message.sandstorm.chip_slots_full"));
            return InteractionResult.FAIL;
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_NETHERITE.value(), SoundSource.PLAYERS, 1.0f, 1.3f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 0.8f, 1.8f);

        player.sendSystemMessage(Component.translatable("message.sandstorm.chip_installed", Component.translatable(this.chipType.getNameKey())));

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, tooltipFlag);
        tooltipConsumer.accept(Component.translatable(this.chipType.getTooltipKey()));
    }
}
