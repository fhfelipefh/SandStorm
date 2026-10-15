package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.CryoRevivalVatBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CryoRevivalVatBlock extends BaseEntityBlock {

    public CryoRevivalVatBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CryoRevivalVatBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, SandStormBlocks.CRYO_REVIVAL_VAT_BE, CryoRevivalVatBlockEntity::serverTick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CryoRevivalVatBlockEntity vat) {
                ItemStack out = vat.getItem(CryoRevivalVatBlockEntity.SLOT_OUTPUT);
                if (!out.isEmpty()) {
                    ItemStack ejected = out.copy();
                    vat.setItem(CryoRevivalVatBlockEntity.SLOT_OUTPUT, ItemStack.EMPTY);
                    if (!player.getInventory().add(ejected)) {
                        Block.popResource(level, pos.above(), ejected);
                    }
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8f, 1.2f);
                    return InteractionResult.SUCCESS;
                }

                ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
                boolean isCoolant = held.is(SandStormBlocks.BRITTLE_PERMAFROST.asItem())
                        || held.is(SandStormBlocks.SUBGLACIAL_ICE_SPIKE.asItem())
                        || held.is(SandStormItems.MINERAL_SALT);

                boolean isSample = held.is(SandStormBlocks.FOSSILIZED_AMBER.asItem())
                        || held.is(SandStormItems.XENO_GRASS_SEEDS)
                        || held.is(Items.WHEAT_SEEDS)
                        || held.is(Items.MELON_SEEDS)
                        || held.is(Items.PUMPKIN_SEEDS)
                        || held.is(Items.BEETROOT_SEEDS)
                        || held.is(SandStormBlocks.SUBGLACIAL_ICE_SPIKE.asItem());

                if (isCoolant && vat.getItem(CryoRevivalVatBlockEntity.SLOT_COOLANT).isEmpty()) {
                    vat.setItem(CryoRevivalVatBlockEntity.SLOT_COOLANT, held.split(1));
                    level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.8f, 1.4f);
                    return InteractionResult.SUCCESS;
                }

                if (isSample && vat.getItem(CryoRevivalVatBlockEntity.SLOT_INPUT).isEmpty()) {
                    vat.setItem(CryoRevivalVatBlockEntity.SLOT_INPUT, held.split(1));
                    level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 0.8f, 1.2f);
                    return InteractionResult.SUCCESS;
                }

                if (vat.canProcess()) {
                    int percent = (vat.getProgress() * 100) / CryoRevivalVatBlockEntity.MAX_PROGRESS;
                    player.sendSystemMessage(Component.translatable("hud.sandstorm.cryo_revival_vat.incubating", percent)
                            .withStyle(ChatFormatting.AQUA));
                } else {
                    player.sendSystemMessage(Component.translatable("hud.sandstorm.cryo_revival_vat.empty")
                            .withStyle(ChatFormatting.GRAY));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CryoRevivalVatBlockEntity vat) {
            Containers.dropContents(level, pos, vat);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
}
