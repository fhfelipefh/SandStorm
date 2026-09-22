package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.survival.PlayerSuitSavedData;
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
import net.minecraft.world.level.Level;

public class SuitUpgradeItem extends Item {
    public enum UpgradeType {
        BATTERY("battery", "item.sandstorm.suit_upgrade_battery"),
        THERMAL("thermal", "item.sandstorm.suit_upgrade_thermal"),
        SEISMIC("seismic", "item.sandstorm.suit_upgrade_seismic"),
        VISOR("visor", "item.sandstorm.suit_upgrade_visor");

        private final String id;
        private final String translationKey;

        UpgradeType(String id, String translationKey) {
            this.id = id;
            this.translationKey = translationKey;
        }

        public String getId() {
            return id;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }

    private final UpgradeType upgradeType;

    public SuitUpgradeItem(UpgradeType upgradeType, Properties properties) {
        super(properties);
        this.upgradeType = upgradeType;
    }

    public UpgradeType getUpgradeType() {
        return upgradeType;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            if (!serverPlayer.entityTags().contains("sandstorm.fused_suit")) {
                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.suit_upgrade_no_suit"), true);
                return InteractionResult.FAIL;
            }

            ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
            PlayerSuitSavedData data = PlayerSuitSavedData.get(serverLevel);
            if (data.hasUpgrade(serverPlayer.getUUID(), upgradeType.getId())) {
                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.suit_upgrade_already_installed"), true);
                return InteractionResult.FAIL;
            }

            boolean installed = data.addUpgrade(serverPlayer.getUUID(), upgradeType.getId());
            if (installed) {
                stack.shrink(1);
                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.suit_upgrade_success", Component.translatable(upgradeType.getTranslationKey())), true);
                serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.5f);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(), 20, 0.3, 0.5, 0.3, 0.05);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}
