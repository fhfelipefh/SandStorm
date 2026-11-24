package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.ExoSuitChipComponent;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
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

        return tryInstallChip(level, player, stack) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (action == ClickAction.SECONDARY && slot.getItem().getItem() instanceof SpaceSuitItem) {
            Level level = player.level();
            if (level.isClientSide()) {
                return true;
            }
            tryInstallChip(level, player, stack);
            return true;
        }
        return false;
    }

    private boolean tryInstallChip(Level level, Player player, ItemStack stack) {
        SuitPowerComponent suit = (player instanceof ServerPlayer sp)
                ? SuitSurvivalHandler.getOrCreateSuit(sp)
                : SuitSurvivalHandler.getOrCreateSuit(player.getUUID());

        if (suit.hasChip(this.chipType)) {
            player.sendSystemMessage(Component.translatable("message.sandstorm.chip_already_installed"));
            return false;
        }

        ExoSuitChipComponent incompatible = suit.findIncompatibleInstalledChip(this.chipType);
        if (!suit.installChip(this.chipType)) {
            player.sendSystemMessage(Component.translatable("message.sandstorm.chip_slots_full"));
            return false;
        }

        if (incompatible != null) {
            ejectIncompatibleChip(level, player, incompatible);
        }

        player.addTag("sandstorm.chip." + this.chipType.name().toLowerCase());

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_NETHERITE.value(), SoundSource.PLAYERS, 1.0f, 1.3f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 0.8f, 1.8f);

        player.sendSystemMessage(Component.translatable("message.sandstorm.chip_installed", Component.translatable(this.chipType.getNameKey())));

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return true;
    }

    public static void ejectIncompatibleChip(Level level, Player player, ExoSuitChipComponent incompatible) {
        player.removeTag("sandstorm.chip." + incompatible.name().toLowerCase());
        Item ejectedItem = SandStormItems.getChipItem(incompatible);
        if (ejectedItem != null) {
            ItemStack ejectedStack = new ItemStack(ejectedItem);
            if (!player.getInventory().add(ejectedStack) && level instanceof ServerLevel serverLevel) {
                player.spawnAtLocation(serverLevel, ejectedStack);
            }
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY() + 1.0, player.getZ(), 16, 0.35, 0.45, 0.35, 0.08);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK.value(), SoundSource.PLAYERS, 0.8f, 1.5f);
        player.sendSystemMessage(Component.translatable("message.sandstorm.chip_ejected_incompatible", Component.translatable(incompatible.getNameKey())));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, tooltipFlag);
        tooltipConsumer.accept(Component.translatable(this.chipType.getTooltipKey()));
    }
}
