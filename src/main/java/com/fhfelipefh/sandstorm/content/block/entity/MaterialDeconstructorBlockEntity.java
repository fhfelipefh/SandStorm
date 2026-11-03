package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.MaterialDeconstructorBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.MaterialDeconstructorMenu;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipe;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class MaterialDeconstructorBlockEntity extends BaseMachineBlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_BATTERY = 2;

    private static final int[] SLOTS_TOP = new int[]{SLOT_INPUT};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_OUTPUT};
    private static final int[] SLOTS_SIDES = new int[]{SLOT_INPUT, SLOT_BATTERY};

    public MaterialDeconstructorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.MATERIAL_DECONSTRUCTOR_BE, pos, state);
    }

    public MaterialDeconstructorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 3, 50);
        this.maxEnergy = 10000;
        this.energyCostPerTick = 20;
    }

    private MachineRecipe findRecipe(ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("material_deconstructor");
        for (MachineRecipe recipe : recipes) {
            for (ItemStack ingredient : recipe.getSlot0Inputs()) {
                if (ItemStack.isSameItem(input, ingredient)) {
                    return recipe;
                }
            }
        }
        return null;
    }

    @Override
    public boolean canProcess() {
        ItemStack input = items.get(SLOT_INPUT);
        if (input.isEmpty()) {
            return false;
        }
        MachineRecipe recipe = findRecipe(input);
        if (recipe == null) {
            return false;
        }
        ItemStack expectedOutput = recipe.getOutput();
        ItemStack currentOutput = items.get(SLOT_OUTPUT);
        if (currentOutput.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(currentOutput, expectedOutput)) {
            return false;
        }
        return currentOutput.getCount() + expectedOutput.getCount() <= currentOutput.getMaxStackSize();
    }

    @Override
    public void processRecipe() {
        ItemStack input = items.get(SLOT_INPUT);
        MachineRecipe recipe = findRecipe(input);
        if (recipe == null) {
            return;
        }
        ItemStack result = recipe.getOutput();
        ItemStack currentOutput = items.get(SLOT_OUTPUT);
        if (currentOutput.isEmpty()) {
            items.set(SLOT_OUTPUT, result.copy());
        } else {
            currentOutput.grow(result.getCount());
        }
        input.shrink(1);
        setChanged();
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        boolean isProcessing = this.progress > 0;
        if (state.getValue(MaterialDeconstructorBlock.LIT) != isProcessing) {
            level.setBlock(pos, state.setValue(MaterialDeconstructorBlock.LIT, isProcessing), 3);
        }
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.GRINDSTONE_USE;
    }

    @Override
    protected int getBatterySlotIndex() {
        return SLOT_BATTERY;
    }

    @Override
    protected void spawnProcessCompletedParticles(ServerLevel serverLevel, BlockPos pos) {
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 8, 0.2, 0.2, 0.2, 0.05);
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
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (slot == SLOT_INPUT) {
            return findRecipe(stack) != null;
        }
        if (slot == SLOT_BATTERY) {
            return getFuelEnergy(stack) > 0;
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == SLOT_OUTPUT;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.material_deconstructor");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new MaterialDeconstructorMenu(syncId, playerInventory, this, this.dataAccess);
    }
}

