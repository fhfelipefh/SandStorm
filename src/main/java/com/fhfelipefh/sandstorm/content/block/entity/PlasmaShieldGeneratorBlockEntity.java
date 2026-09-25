package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.PlasmaShieldGeneratorBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.defense.KineticShieldTracker;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.gui.PlasmaShieldMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PlasmaShieldGeneratorBlockEntity extends BlockEntity implements MenuProvider, Container {
    public static final int MAX_ENERGY = 1000000;
    public static final int UPKEEP_COST = 100;
    public static final int DEFLECTION_COST = 100;
    public static final int REPEL_COST = 250;

    private int storedEnergy = MAX_ENERGY;
    private boolean shieldActive = false;
    private int shieldRadius = 48;
    private int threatCount = 0;
    private ItemStack toroidSlot = ItemStack.EMPTY;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEnergy;
                case 1 -> MAX_ENERGY;
                case 2 -> shieldActive ? 1 : 0;
                case 3 -> shieldRadius;
                case 4 -> threatCount;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                storedEnergy = value;
            } else if (index == 2) {
                shieldActive = value == 1;
            } else if (index == 3) {
                shieldRadius = value;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public PlasmaShieldGeneratorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.PLASMA_SHIELD_GENERATOR_BE, pos, state);
    }

    public PlasmaShieldGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        boolean previouslyActive = this.shieldActive;

        if (this.shieldActive) {
            if (this.storedEnergy >= UPKEEP_COST) {
                this.storedEnergy -= UPKEEP_COST;
                KineticShieldTracker.registerShield(level.dimension(), pos, this.shieldRadius);

                if (level.getGameTime() % 5L == 0L && level instanceof ServerLevel serverLevel) {
                    deflectProjectiles(serverLevel, pos);
                }

                if (level.getGameTime() % 10L == 0L && level instanceof ServerLevel serverLevel) {
                    repelHostileThreats(serverLevel, pos);
                }
            } else {
                this.shieldActive = false;
                KineticShieldTracker.unregisterShield(level.dimension(), pos);
            }
        } else {
            KineticShieldTracker.unregisterShield(level.dimension(), pos);
        }

        if (previouslyActive != this.shieldActive) {
            if (state.hasProperty(PlasmaShieldGeneratorBlock.ACTIVE) && state.getValue(PlasmaShieldGeneratorBlock.ACTIVE) != this.shieldActive) {
                level.setBlock(pos, state.setValue(PlasmaShieldGeneratorBlock.ACTIVE, this.shieldActive), 3);
            }
            setChanged();
        }
    }

    private void deflectProjectiles(ServerLevel level, BlockPos pos) {
        double radSq = (double) this.shieldRadius * this.shieldRadius;
        AABB bounds = new AABB(pos).inflate(this.shieldRadius);
        List<Projectile> projectiles = level.getEntitiesOfClass(Projectile.class, bounds, p -> !p.isRemoved());

        for (Projectile projectile : projectiles) {
            if (projectile.position().distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= radSq) {
                if (this.storedEnergy >= DEFLECTION_COST) {
                    this.storedEnergy -= DEFLECTION_COST;
                }
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, projectile.getX(), projectile.getY(), projectile.getZ(), 8, 0.2, 0.2, 0.2, 0.05);
                level.playSound(null, projectile.blockPosition(), SoundEvents.SHIELD_BLOCK.value(), SoundSource.BLOCKS, 1.0f, 1.4f);
                projectile.discard();
                this.threatCount++;
                setChanged();
            }
        }
    }

    private void repelHostileThreats(ServerLevel level, BlockPos pos) {
        double radSq = (double) this.shieldRadius * this.shieldRadius;
        AABB bounds = new AABB(pos).inflate(this.shieldRadius);
        List<LivingEntity> enemies = level.getEntitiesOfClass(LivingEntity.class, bounds, e -> (e instanceof Enemy || e instanceof SandwormEntity) && !e.isRemoved() && e.isAlive() && !(e instanceof Player));

        for (LivingEntity living : enemies) {
            if (living.position().distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= radSq) {
                if (this.storedEnergy >= REPEL_COST) {
                    this.storedEnergy -= REPEL_COST;
                }
                living.hurtServer(level, level.damageSources().generic(), 4.0f);
                Vec3 pushVec = living.position().subtract(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).normalize().scale(0.8);
                living.push(pushVec.x, 0.2, pushVec.z);
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, living.getX(), living.getY() + 1.0, living.getZ(), 6, 0.3, 0.3, 0.3, 0.02);
                this.threatCount++;
                setChanged();
            }
        }
    }

    public void toggleShield() {
        this.shieldActive = !this.shieldActive;
        setChanged();
    }

    public void cycleRadius() {
        if (this.shieldRadius == 32) {
            this.shieldRadius = 40;
        } else if (this.shieldRadius == 40) {
            this.shieldRadius = 48;
        } else {
            this.shieldRadius = 32;
        }
        setChanged();
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    public void setShieldActive(boolean active) {
        this.shieldActive = active;
        setChanged();
    }

    public int getFieldRadius() {
        return this.shieldRadius;
    }

    public void setFieldRadius(int radius) {
        this.shieldRadius = radius;
        setChanged();
    }

    public void cleanup() {
        if (this.level != null && !this.level.isClientSide()) {
            KineticShieldTracker.unregisterShield(this.level.dimension(), getBlockPos());
        }
    }

    @Override
    public void setRemoved() {
        cleanup();
        super.setRemoved();
    }

    public int getStoredEnergy() {
        return this.storedEnergy;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.max(0, Math.min(MAX_ENERGY, energy));
        setChanged();
    }

    public boolean isShieldActive() {
        return this.shieldActive;
    }

    public int getShieldRadius() {
        return this.shieldRadius;
    }

    public int getThreatCount() {
        return this.threatCount;
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.plasma_shield_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new PlasmaShieldMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.toroidSlot.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? this.toroidSlot : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0 && !this.toroidSlot.isEmpty()) {
            ItemStack split = this.toroidSlot.split(amount);
            setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack stack = this.toroidSlot;
            this.toroidSlot = ItemStack.EMPTY;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            this.toroidSlot = stack;
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.toroidSlot = ItemStack.EMPTY;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putBoolean("shieldActive", this.shieldActive);
        output.putInt("shieldRadius", this.shieldRadius);
        output.putInt("threatCount", this.threatCount);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.storedEnergy = input.getIntOr("storedEnergy", MAX_ENERGY);
        this.shieldActive = input.getBooleanOr("shieldActive", false);
        this.shieldRadius = input.getIntOr("shieldRadius", 48);
        this.threatCount = input.getIntOr("threatCount", 0);
    }
}
