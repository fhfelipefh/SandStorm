package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.defense.KineticShieldTracker;
import com.fhfelipefh.sandstorm.content.gui.KineticShieldMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class KineticShieldGeneratorBlockEntity extends BaseMachineBlockEntity {
    public static final double SHIELD_RADIUS = 20.0;
    public static final int UPKEEP_COST = 5;
    public static final int DEFLECTION_COST = 50;

    private boolean shieldActive = false;
    private boolean userEnabled = true;
    private int totalDeflections = 0;

    private final ContainerData shieldDataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy;
                case 1 -> maxEnergy;
                case 2 -> (shieldActive && userEnabled) ? 1 : 0;
                case 3 -> (int) SHIELD_RADIUS;
                case 4 -> totalDeflections;
                case 5 -> userEnabled ? 1 : 0;
                case 6 -> wptConnected ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energy = value;
                case 1 -> maxEnergy = value;
                case 2 -> shieldActive = (value == 1);
                case 4 -> totalDeflections = value;
                case 5 -> userEnabled = (value == 1);
                case 6 -> wptConnected = (value == 1);
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public KineticShieldGeneratorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.KINETIC_SHIELD_GENERATOR_BE, pos, state);
    }

    public KineticShieldGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 1, 100);
        this.maxEnergy = 20000;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

        if (!level.isClientSide()) {
            boolean wasActive = shieldActive;

            if (userEnabled && energy >= UPKEEP_COST) {
                energy -= UPKEEP_COST;
                shieldActive = true;
                KineticShieldTracker.registerShield(level.dimension(), pos, SHIELD_RADIUS);
                deflectProjectiles((ServerLevel) level, pos);
            } else {
                shieldActive = false;
                KineticShieldTracker.unregisterShield(level.dimension(), pos);
            }

            if (wasActive != shieldActive) {
                setChanged();
            }
        }
    }

    private void deflectProjectiles(ServerLevel level, BlockPos pos) {
        AABB shieldBounds = new AABB(pos).inflate(SHIELD_RADIUS);
        List<Projectile> projectiles = level.getEntitiesOfClass(Projectile.class, shieldBounds, p -> !p.isRemoved());

        for (Projectile projectile : projectiles) {
            if (projectile.position().distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= (SHIELD_RADIUS * SHIELD_RADIUS)) {
                if (energy >= DEFLECTION_COST) {
                    energy -= DEFLECTION_COST;
                }
                totalDeflections++;
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, projectile.getX(), projectile.getY(), projectile.getZ(), 8, 0.2, 0.2, 0.2, 0.05);
                level.playSound(null, projectile.blockPosition(), SoundEvents.SHIELD_BLOCK.value(), SoundSource.BLOCKS, 1.0f, 1.4f);
                projectile.discard();
                setChanged();
            }
        }
    }

    public boolean isShieldActive() {
        return shieldActive;
    }

    public void toggleShield() {
        this.userEnabled = !this.userEnabled;
        setChanged();
    }

    public boolean isUserEnabled() {
        return userEnabled;
    }

    public int getTotalDeflections() {
        return totalDeflections;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("userEnabled", this.userEnabled);
        output.putInt("totalDeflections", this.totalDeflections);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.userEnabled = input.getBooleanOr("userEnabled", true);
        this.totalDeflections = input.getIntOr("totalDeflections", 0);
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide()) {
            KineticShieldTracker.unregisterShield(level.dimension(), getBlockPos());
        }
        super.setRemoved();
    }

    @Override
    protected boolean canProcess() {
        return false;
    }

    @Override
    protected void processRecipe() {
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.BEACON_AMBIENT;
    }

    @Override
    protected int getBatterySlotIndex() {
        return 0;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.kinetic_shield_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new KineticShieldMenu(containerId, playerInventory, this, this.shieldDataAccess);
    }
}
