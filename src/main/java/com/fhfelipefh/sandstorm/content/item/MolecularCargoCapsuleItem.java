package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class MolecularCargoCapsuleItem extends Item {

    public MolecularCargoCapsuleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer, stack, hand);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHULKER_BOX_OPEN, SoundSource.PLAYERS, 0.8f, 1.2f);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return this.use(context.getLevel(), context.getPlayer(), context.getHand());
    }

    private void openMenu(ServerPlayer player, ItemStack capsuleStack, InteractionHand hand) {
        player.openMenu(new SimpleMenuProvider((syncId, playerInventory, playerEntity) -> {
            SimpleContainer container = new SimpleContainer(27) {
                @Override
                public void setChanged() {
                    super.setChanged();
                    saveContents(this, capsuleStack);
                }
            };
            ItemContainerContents contents = capsuleStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
            contents.copyInto(items);
            for (int i = 0; i < 27; i++) {
                container.setItem(i, items.get(i));
            }

            return new ShulkerBoxMenu(syncId, playerInventory, container) {
                @Override
                public boolean stillValid(Player p) {
                    return p.isAlive() && p.getItemInHand(hand) == capsuleStack;
                }

                @Override
                public void clicked(int slotId, int button, ContainerInput containerInput, Player p) {
                    if (slotId >= 0 && slotId < slots.size()) {
                        Slot slot = slots.get(slotId);
                        if (slot.getItem() == capsuleStack) {
                            return;
                        }
                    }
                    if (containerInput == ContainerInput.SWAP) {
                        if (hand == InteractionHand.MAIN_HAND && p.getInventory().getSelectedSlot() == button) {
                            return;
                        }
                    }
                    super.clicked(slotId, button, containerInput, p);
                }

                @Override
                public void removed(Player p) {
                    super.removed(p);
                    saveContents(container, capsuleStack);
                    p.level().playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.SHULKER_BOX_CLOSE, SoundSource.PLAYERS, 0.8f, 1.2f);
                }
            };
        }, capsuleStack.getHoverName()));
    }

    private static void saveContents(SimpleContainer container, ItemStack capsuleStack) {
        NonNullList<ItemStack> list = NonNullList.withSize(27, ItemStack.EMPTY);
        for (int i = 0; i < 27; i++) {
            list.set(i, container.getItem(i));
        }
        capsuleStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, flag);
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.molecular_cargo_capsule.desc").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.molecular_cargo_capsule.use").withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.molecular_cargo_capsule.anti_nesting").withStyle(ChatFormatting.DARK_PURPLE));
    }
}
