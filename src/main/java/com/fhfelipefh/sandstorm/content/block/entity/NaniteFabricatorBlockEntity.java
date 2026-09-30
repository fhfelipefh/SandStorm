package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.NaniteFabricatorMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
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

public class NaniteFabricatorBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{0, 1, 3};
    private static final int[] SLOTS_BOTTOM = new int[]{2, 3};
    private static final int[] SLOTS_SIDES = new int[]{1, 0, 3, 2};

    public NaniteFabricatorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.NANITE_FABRICATOR_BE, pos, state);
    }

    public NaniteFabricatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 4, 120);
    }

    @Override
    protected boolean canProcess() {
        ItemStack in0 = items.get(0);
        ItemStack in1 = items.get(1);
        if (in0.is(SandStormItems.CIRCUIT_BOARD)) {
            if (!in1.is(SandStormItems.SANDWORM_CHITIN) && !in1.is(SandStormItems.SCRAP_METAL)) {
                return false;
            }
            ItemStack out = items.get(2);
            if (out.isEmpty()) {
                return true;
            }
            return out.is(SandStormItems.NANO_ACTUATOR) && out.getCount() < out.getMaxStackSize();
        }
        if (in0.is(SandStormItems.SANDWORM_TOOTH)) {
            if (!in1.is(SandStormItems.NANO_ACTUATOR)) {
                return false;
            }
            ItemStack out = items.get(2);
            return out.isEmpty();
        }
        if (in0.is(SandStormItems.SANDWORM_CHITIN)) {
            if (!in1.is(SandStormItems.SCRAP_METAL) && !in1.is(SandStormItems.NANO_ACTUATOR)) {
                return false;
            }
            ItemStack out = items.get(2);
            if (out.isEmpty()) {
                return true;
            }
            return out.is(SandStormItems.TITANIUM_CHITIN_COMPOSITE) && out.getCount() < out.getMaxStackSize();
        }
        return false;
    }

    @Override
    protected void processRecipe() {
        if (!canProcess()) {
            return;
        }
        ItemStack in0 = items.get(0);
        ItemStack in1 = items.get(1);
        if (in0.is(SandStormItems.CIRCUIT_BOARD)) {
            in0.shrink(1);
            in1.shrink(1);
            ItemStack out = items.get(2);
            if (out.isEmpty()) {
                items.set(2, new ItemStack(SandStormItems.NANO_ACTUATOR));
            } else {
                out.grow(1);
            }
        } else if (in0.is(SandStormItems.SANDWORM_TOOTH)) {
            in0.shrink(1);
            in1.shrink(1);
            items.set(2, new ItemStack(SandStormItems.VIBRO_CRYSKNIFE));
        } else if (in0.is(SandStormItems.SANDWORM_CHITIN)) {
            in0.shrink(1);
            in1.shrink(1);
            ItemStack out = items.get(2);
            if (out.isEmpty()) {
                items.set(2, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE));
            } else {
                out.grow(1);
            }
        }
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.BEACON_ACTIVATE;
    }

    @Override
    protected void onProcessCompleted(Level level, BlockPos pos) {
        level.playSound(null, pos, SandStormSoundEvents.NANITE_ACTIVATE, SoundSource.BLOCKS, 0.85f, 1.2f);
        level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 0.65f, 1.4f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.WAX_OFF, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 14, 0.25, 0.15, 0.25, 0.05);
            serverLevel.sendParticles(ParticleTypes.GLOW, pos.getX() + 0.5, pos.getY() + 0.65, pos.getZ() + 0.5, 8, 0.2, 0.1, 0.2, 0.02);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 6, 0.2, 0.1, 0.2, 0.03);
        }
    }

    @Override
    protected int getBatterySlotIndex() {
        return 3;
    }

    public ItemStack getFabricatingItem() {
        if (this.isProcessing()) {
            ItemStack in0 = items.get(0);
            if (in0.is(SandStormItems.SANDWORM_TOOTH)) {
                return new ItemStack(SandStormItems.VIBRO_CRYSKNIFE);
            }
            if (in0.is(SandStormItems.SANDWORM_CHITIN)) {
                return new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE);
            }
            return new ItemStack(SandStormItems.NANO_ACTUATOR);
        }
        return this.getItem(2);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.nanite_fabricator");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new NaniteFabricatorMenu(syncId, playerInventory, this, this.dataAccess);
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
            return stack.is(SandStormItems.CIRCUIT_BOARD) || stack.is(SandStormItems.SANDWORM_TOOTH) || stack.is(SandStormItems.SANDWORM_CHITIN);
        }
        if (slot == 1) {
            return stack.is(SandStormItems.SANDWORM_CHITIN) || stack.is(SandStormItems.SCRAP_METAL) || stack.is(SandStormItems.NANO_ACTUATOR);
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
        return slot == 2;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 2;
    }
}
