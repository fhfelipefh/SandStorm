package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.block.CryoReactorCoreBlock;
import com.fhfelipefh.sandstorm.content.block.CryoSprayerBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class CryoCellCoolerItem extends Item {

    public static final int MAX_DURABILITY = 600;
    public static final int COOLDOWN_TICKS = 60;

    public CryoCellCoolerItem(Properties properties) {
        super(properties
                .durability(MAX_DURABILITY)
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof ServerPlayer player) {
            if (player.tickCount % 20 == 0) {
                SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player);
                double currentTemp = suit.getThermal().getCurrentTemperature();
                if (currentTemp > 37.5) {
                    suit.getThermal().setCurrentTemperature(Math.max(37.0, currentTemp - 1.2));
                    stack.hurtAndBreak(1, level, player, item -> {});
                    level.sendParticles(ParticleTypes.SNOWFLAKE,
                            player.getX(), player.getY() + 1.0, player.getZ(),
                            3, 0.2, 0.2, 0.2, 0.02);
                }
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        boolean isCoolantSource = state.getBlock() instanceof CryoReactorCoreBlock
                || state.getBlock() instanceof CryoSprayerBlock
                || state.is(SandStormBlocks.BRITTLE_PERMAFROST)
                || state.is(SandStormBlocks.SUBGLACIAL_ICE_SPIKE)
                || state.is(Blocks.BLUE_ICE)
                || state.is(Blocks.PACKED_ICE);

        if (isCoolantSource) {
            if (stack.getDamageValue() > 0) {
                stack.setDamageValue(0);
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 1.0f, 1.5f);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                            pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                            15, 0.3, 0.3, 0.3, 0.05);
                }
                if (player != null) {
                    player.sendSystemMessage(Component.translatable("hud.sandstorm.cryo_cell_cooler.recharged").withStyle(ChatFormatting.AQUA));
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        if (heldStack.getDamageValue() >= MAX_DURABILITY - 10) {
            return InteractionResult.FAIL;
        }

        player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(serverPlayer);
            suit.getThermal().setCurrentTemperature(37.0);

            player.clearFire();
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, true, true));

            heldStack.hurtAndBreak(30, serverLevel, serverPlayer, item -> {});

            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    25, 0.4, 0.5, 0.4, 0.08);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_HURT_FREEZE, SoundSource.PLAYERS, 1.2f, 1.2f);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.cryo_cell_cooler.desc1").withStyle(ChatFormatting.AQUA));
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.cryo_cell_cooler.desc2").withStyle(ChatFormatting.DARK_AQUA));
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.cryo_cell_cooler.recharge").withStyle(ChatFormatting.BLUE));
    }
}
