package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.quest.PlayerQuestSavedData;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class SurvivalDatapadItem extends Item {
    private static Runnable clientScreenOpener;

    public SurvivalDatapadItem(Properties properties) {
        super(properties);
    }

    public static void setClientScreenOpener(Runnable opener) {
        clientScreenOpener = opener;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            MinecraftServer server = sp.level().getServer();
            if (server != null) {
                PlayerQuestSavedData data = PlayerQuestSavedData.get(server);
                QuestRewardHandler.syncPlayerQuests(sp, data);
            }
        }
        if (level.isClientSide() && clientScreenOpener != null) {
            clientScreenOpener.run();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("item.sandstorm.survival_datapad.desc").withStyle(ChatFormatting.GRAY));
    }
}
