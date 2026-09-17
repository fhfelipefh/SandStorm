package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CargoDroneEntity extends PathfinderMob {
    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(10000L, 100L, 100L);
    private ItemStack cargoStack = ItemStack.EMPTY;

    public CargoDroneEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.energyStorage.receiveEnergy(10000L);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.6));
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public ItemStack getCargoStack() {
        return cargoStack;
    }

    public void setCargoStack(ItemStack cargo) {
        this.cargoStack = cargo != null ? cargo : ItemStack.EMPTY;
    }

    public float getSeismicVibrationOutput() {
        return 0.0f;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.energyStorage.hasEnergy(1L)) {
            this.energyStorage.extractEnergy(1L);
            this.setNoGravity(true);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        Level level = this.level();
        ItemStack held = player.getItemInHand(hand);

        if (!held.isEmpty() && this.cargoStack.isEmpty()) {
            if (!level.isClientSide()) {
                this.cargoStack = held.split(1);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable(
                            "telemetry.sandstorm.drone_loaded",
                            this.cargoStack.getHoverName()
                    ), true);
                }
                level.playSound(null, this.blockPosition(), SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }

        if (held.isEmpty() && !this.cargoStack.isEmpty()) {
            if (!level.isClientSide()) {
                player.getInventory().add(this.cargoStack);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.drone_unloaded"), true);
                }
                level.playSound(null, this.blockPosition(), SoundEvents.BUNDLE_REMOVE_ONE, SoundSource.PLAYERS, 1.0f, 1.0f);
                this.cargoStack = ItemStack.EMPTY;
            }
            return InteractionResult.SUCCESS;
        }

        if (held.isEmpty() && this.cargoStack.isEmpty()) {
            if (!level.isClientSide()) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable(
                            "telemetry.sandstorm.drone_status",
                            this.energyStorage.getStoredEnergy(),
                            this.energyStorage.getCapacity()
                    ), true);
                }
                level.playSound(null, this.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8f, 1.5f);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }
}
