package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.KineticRailgunBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.gui.KineticRailgunMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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

public class KineticRailgunBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int MAX_ENERGY = 250000;
    public static final int SHOT_ENERGY_COST = 5000;
    public static final int COOLDOWN_TICKS = 60;
    public static final double TARGET_RANGE = 64.0;
    private static final int[] ALL_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};

    private final NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private int storedEnergy = MAX_ENERGY;
    private int cooldown = 0;
    private int totalShotsFired = 0;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEnergy;
                case 1 -> MAX_ENERGY;
                case 2 -> cooldown;
                case 3 -> totalShotsFired;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                storedEnergy = value;
            } else if (index == 2) {
                cooldown = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public KineticRailgunBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.KINETIC_RAILGUN_BE, pos, state);
    }

    public KineticRailgunBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        if (this.cooldown > 0) {
            this.cooldown--;
        }

        boolean hasAmmo = hasAmmoAvailable();
        boolean readyToFire = hasAmmo && this.storedEnergy >= SHOT_ENERGY_COST;

        if (readyToFire && this.cooldown <= 0 && level instanceof ServerLevel serverLevel) {
            LivingEntity target = acquireTarget(serverLevel, pos);
            if (target != null) {
                fireRailgun(serverLevel, pos, target);
            }
        }

        if (state.hasProperty(KineticRailgunBlock.LIT) && state.getValue(KineticRailgunBlock.LIT) != readyToFire) {
            level.setBlock(pos, state.setValue(KineticRailgunBlock.LIT, readyToFire), 3);
            setChanged();
        }
    }

    private boolean hasAmmoAvailable() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private LivingEntity acquireTarget(ServerLevel level, BlockPos pos) {
        AABB bounds = new AABB(pos).inflate(TARGET_RANGE);
        List<SandwormEntity> worms = level.getEntitiesOfClass(SandwormEntity.class, bounds, w -> !w.isRemoved());
        if (!worms.isEmpty()) {
            return worms.get(0);
        }

        List<LivingEntity> enemies = level.getEntitiesOfClass(LivingEntity.class, bounds, e -> e instanceof Enemy && !e.isRemoved() && e.isAlive() && !(e instanceof Player));
        if (!enemies.isEmpty()) {
            return enemies.get(0);
        }

        return null;
    }

    private void fireRailgun(ServerLevel level, BlockPos pos, LivingEntity target) {
        consumeOneAmmo();
        this.storedEnergy -= SHOT_ENERGY_COST;
        this.cooldown = COOLDOWN_TICKS;
        this.totalShotsFired++;

        target.hurtServer(level, level.damageSources().generic(), 50.0f);

        Vec3 pushVec = target.position().subtract(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).normalize().scale(1.5);
        target.push(pushVec.x, 0.35, pushVec.z);

        double dx = target.getX() - (pos.getX() + 0.5);
        double dy = (target.getY() + target.getBbHeight() * 0.5) - (pos.getY() + 0.8);
        double dz = target.getZ() - (pos.getZ() + 0.5);
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int steps = Math.max(1, (int) (dist * 2.0));
        for (int i = 0; i <= steps; i++) {
            double frac = (double) i / steps;
            level.sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5 + dx * frac, pos.getY() + 0.8 + dy * frac, pos.getZ() + 0.5 + dz * frac, 1, 0.0, 0.0, 0.0, 0.0);
        }

        level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.8f, 1.6f);
        setChanged();
    }

    private void consumeOneAmmo() {
        for (int i = 0; i < this.items.size(); i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty()) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    this.items.set(i, ItemStack.EMPTY);
                }
                setChanged();
                return;
            }
        }
    }

    public int getStoredEnergy() {
        return this.storedEnergy;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.max(0, Math.min(MAX_ENERGY, energy));
        setChanged();
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public int getTotalShotsFired() {
        return this.totalShotsFired;
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return ALL_SLOTS;
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
        return Component.translatable("container.sandstorm.kinetic_railgun");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new KineticRailgunMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putInt("cooldown", this.cooldown);
        output.putInt("totalShotsFired", this.totalShotsFired);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        this.storedEnergy = input.getIntOr("storedEnergy", MAX_ENERGY);
        this.cooldown = input.getIntOr("cooldown", 0);
        this.totalShotsFired = input.getIntOr("totalShotsFired", 0);
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }
}
