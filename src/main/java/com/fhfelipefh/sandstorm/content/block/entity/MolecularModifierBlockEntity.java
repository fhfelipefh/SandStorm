package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.MolecularModifierMenu;
import com.fhfelipefh.sandstorm.content.item.MolecularUpgradeItem;
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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MolecularModifierBlockEntity extends BaseMachineBlockEntity {
    public static final int SLOT_TARGET = 0;
    public static final int SLOT_MODULE_1 = 1;
    public static final int SLOT_MODULE_2 = 2;
    public static final int SLOT_MODULE_3 = 3;
    public static final int SLOT_NANOCOATING = 4;
    public static final int SLOT_BATTERY = 5;
    public static final int SLOT_OUTPUT = 6;

    private static final int[] SLOTS_TOP = new int[]{SLOT_TARGET};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_OUTPUT, SLOT_BATTERY};
    private static final int[] SLOTS_SIDES = new int[]{SLOT_MODULE_1, SLOT_MODULE_2, SLOT_MODULE_3, SLOT_NANOCOATING, SLOT_BATTERY};

    public MolecularModifierBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.MOLECULAR_MODIFIER_BE, pos, state);
    }

    public MolecularModifierBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 7, 50);
        this.energyCostPerTick = 10;
    }

    @Override
    public boolean canProcess() {
        ItemStack target = items.get(SLOT_TARGET);
        if (target.isEmpty()) {
            return false;
        }

        ItemStack output = items.get(SLOT_OUTPUT);
        if (!output.isEmpty()) {
            return false;
        }

        if (this.energy < 500) {
            return false;
        }

        boolean hasApplicableModule = false;
        for (int slot = SLOT_MODULE_1; slot <= SLOT_NANOCOATING; slot++) {
            ItemStack modStack = items.get(slot);
            if (!modStack.isEmpty() && modStack.getItem() instanceof MolecularUpgradeItem upgrade) {
                if (upgrade.getUpgradeType().getCategory().isApplicableTo(target)) {
                    hasApplicableModule = true;
                    break;
                }
            }
        }

        return hasApplicableModule;
    }

    @Override
    public void processRecipe() {
        if (!canProcess()) {
            return;
        }

        ItemStack target = items.get(SLOT_TARGET);
        ItemStack result = target.copy();
        result.setCount(1);

        for (int slot = SLOT_MODULE_1; slot <= SLOT_NANOCOATING; slot++) {
            ItemStack modStack = items.get(slot);
            if (!modStack.isEmpty() && modStack.getItem() instanceof MolecularUpgradeItem upgrade) {
                if (upgrade.getUpgradeType().getCategory().isApplicableTo(target)) {
                    upgrade.getUpgradeType().applyTo(result, this.level);
                    modStack.shrink(1);
                }
            }
        }

        target.shrink(1);
        items.set(SLOT_OUTPUT, result);
    }

    @Override
    protected int getBatterySlotIndex() {
        return SLOT_BATTERY;
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.BEACON_ACTIVATE;
    }

    @Override
    protected void onProcessCompleted(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.85f, 1.5f);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.75f, 1.3f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 16, 0.3, 0.2, 0.3, 0.5);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 6, 0.2, 0.1, 0.2, 0.04);
            serverLevel.sendParticles(ParticleTypes.GLOW, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 6, 0.2, 0.1, 0.2, 0.02);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) {
            return SLOTS_TOP;
        }
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        }
        return SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
        if (slot == SLOT_OUTPUT) {
            return false;
        }
        if (slot == SLOT_BATTERY) {
            return BaseMachineBlockEntity.getFuelEnergy(stack) > 0;
        }
        if (slot >= SLOT_MODULE_1 && slot <= SLOT_MODULE_3) {
            return stack.getItem() instanceof MolecularUpgradeItem up && up.getUpgradeType().getSlotType() == MolecularUpgradeItem.ModuleSlotType.OVERCLOCK;
        }
        if (slot == SLOT_NANOCOATING) {
            return stack.getItem() instanceof MolecularUpgradeItem up && up.getUpgradeType().getSlotType() == MolecularUpgradeItem.ModuleSlotType.NANOCOATING;
        }
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == SLOT_OUTPUT;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.molecular_modifier");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new MolecularModifierMenu(syncId, playerInventory, this, this.dataAccess);
    }
}
