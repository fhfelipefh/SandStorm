package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.ChemicalRefineryMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChemicalRefineryBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_BOTTOM = new int[]{4, 3};
    private static final int[] SLOTS_SIDES = new int[]{0, 1, 2, 3, 4};

    public ChemicalRefineryBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.CHEMICAL_REFINERY_BE, pos, state);
    }

    public ChemicalRefineryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 5, 100);
    }

    @Override
    public boolean canProcess() {
        ItemStack inSalt = items.get(0);
        ItemStack inFluid = items.get(1);
        ItemStack inCartridge = items.get(2);

        if (!inSalt.is(SandStormItems.MINERAL_SALT)) {
            return false;
        }
        if (!inFluid.is(SandStormItems.POTABLE_WATER_BOTTLE) && !inFluid.is(SandStormItems.BRACKISH_WATER_BOTTLE)) {
            return false;
        }
        if (!inCartridge.is(SandStormItems.EMPTY_CARTRIDGE)) {
            return false;
        }

        ItemStack out = items.get(4);
        if (out.isEmpty()) {
            return true;
        }
        return out.is(SandStormItems.PROPELLANT_CARTRIDGE) && out.getCount() < Math.max(out.getMaxStackSize(), 16);
    }

    @Override
    public void processRecipe() {
        if (!canProcess()) {
            return;
        }
        items.get(0).shrink(1);
        items.get(1).shrink(1);
        items.get(2).shrink(1);

        ItemStack out = items.get(4);
        if (out.isEmpty()) {
            items.set(4, new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE));
        } else {
            out.grow(1);
        }
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.BREWING_STAND_BREW;
    }

    @Override
    protected void onProcessCompleted(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.8f, 1.15f);
        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.75f, 0.9f);
        level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.5f, 1.4f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 8, 0.15, 0.2, 0.15, 0.05);
            serverLevel.sendParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 6, 0.1, 0.1, 0.1, 0.03);
            serverLevel.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 3, 0.2, 0.1, 0.2, 0.02);
        }
    }

    @Override
    protected int getBatterySlotIndex() {
        return 3;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.chemical_refinery");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new ChemicalRefineryMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        }
        if (side == Direction.UP) {
            return SLOTS_TOP;
        }
        return SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) {
            return stack.is(SandStormItems.MINERAL_SALT);
        }
        if (slot == 1) {
            return stack.is(SandStormItems.POTABLE_WATER_BOTTLE) || stack.is(SandStormItems.BRACKISH_WATER_BOTTLE);
        }
        if (slot == 2) {
            return stack.is(SandStormItems.EMPTY_CARTRIDGE);
        }
        if (slot == 3) {
            return getFuelEnergy(stack) > 0;
        }
        return false;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN) {
            return slot == 3 && getFuelEnergy(stack) > 0;
        }
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return slot == 4;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 4;
    }
}
