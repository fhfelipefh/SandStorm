package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.satellite.SatelliteNetworkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class OrbitalSurveySatelliteItem extends Item {

    public OrbitalSurveySatelliteItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (!level.canSeeSky(player.blockPosition().above())) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(
                        Component.translatable("telemetry.sandstorm.satellite_launch_blocked").withStyle(ChatFormatting.RED),
                        true
                );
            }
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            SatelliteNetworkManager.registerSatelliteLaunched(serverLevel, player);

            for (int i = 0; i < 20; i++) {
                double px = player.getX() + (level.getRandom().nextDouble() - 0.5) * 1.5;
                double py = player.getY() + level.getRandom().nextDouble() * 2.0;
                double pz = player.getZ() + (level.getRandom().nextDouble() - 0.5) * 1.5;
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, px, py, pz, 1, 0.0, 0.4, 0.0, 0.05);
                serverLevel.sendParticles(ParticleTypes.FLAME, px, py, pz, 1, 0.0, 0.3, 0.0, 0.03);
            }

            serverLevel.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.5f, 0.6f);
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.2f, 1.5f);
        }

        if (!player.isCreative()) {
            held.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.satellite.desc").withStyle(ChatFormatting.AQUA));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.satellite.launch_hint").withStyle(ChatFormatting.GRAY));
    }
}
