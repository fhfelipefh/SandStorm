package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.tracker.TransponderTrackerSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

public class TransponderTagItem extends Item {

    public TransponderTagItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!player.level().isClientSide() && player.level() instanceof ServerLevel serverLevel) {
            String name = stack.has(DataComponents.CUSTOM_NAME)
                    ? stack.get(DataComponents.CUSTOM_NAME).getString()
                    : entity.getName().getString();
            entity.setCustomName(Component.literal(name));
            if (entity instanceof Mob mob) {
                mob.setPersistenceRequired();
            }
            TransponderTrackerSavedData.get(serverLevel).register(
                    entity.getUUID(),
                    name,
                    serverLevel.dimension().identifier().toString(),
                    entity.blockPosition(),
                    false
            );
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.sendOverlayMessage(Component.translatable("message.sandstorm.transponder_tag.entity_tagged", name));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        if (player != null && player.isShiftKeyDown() && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
            String name = stack.has(DataComponents.CUSTOM_NAME)
                    ? stack.get(DataComponents.CUSTOM_NAME).getString()
                    : level.getBlockState(pos).getBlock().getName().getString();
            UUID blockUuid = UUID.nameUUIDFromBytes((level.dimension().identifier().toString() + pos.toString()).getBytes(StandardCharsets.UTF_8));
            TransponderTrackerSavedData.get(serverLevel).register(
                    blockUuid,
                    name,
                    serverLevel.dimension().identifier().toString(),
                    pos,
                    true
            );
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.sendOverlayMessage(Component.translatable("message.sandstorm.transponder_tag.block_tagged", name));
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, flag);
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.transponder_tag.desc").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.transponder_tag.use").withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.transponder_tag.block").withStyle(ChatFormatting.YELLOW));
    }
}
