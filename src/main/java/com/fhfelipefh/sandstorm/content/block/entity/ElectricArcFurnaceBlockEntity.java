package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.ElectricArcFurnaceBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.ElectricArcFurnaceMenu;
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
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ElectricArcFurnaceBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_BOTTOM = new int[]{1};
    private static final int[] SLOTS_SIDES = new int[]{0, 2};

    private final RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> smeltingCheck = RecipeManager.createCheck(RecipeType.SMELTING);
    private final RecipeManager.CachedCheck<SingleRecipeInput, BlastingRecipe> blastingCheck = RecipeManager.createCheck(RecipeType.BLASTING);

    public ElectricArcFurnaceBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.ELECTRIC_ARC_FURNACE_BE, pos, state);
    }

    public ElectricArcFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 3, 80);
        this.energyCostPerTick = 12;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        boolean processing = isProcessing();
        if (state.hasProperty(ElectricArcFurnaceBlock.LIT) && state.getValue(ElectricArcFurnaceBlock.LIT) != processing) {
            level.setBlock(pos, state.setValue(ElectricArcFurnaceBlock.LIT, processing), 3);
        }
    }

    @Override
    protected boolean canProcess() {
        ItemStack input = items.get(0);
        if (input.isEmpty() || level == null || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        ItemStack result = getSmeltingResult(serverLevel, input);
        if (result.isEmpty()) {
            return false;
        }
        ItemStack output = items.get(1);
        if (output.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    @Override
    protected void processRecipe() {
        if (level == null || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack input = items.get(0);
        if (input.isEmpty()) {
            return;
        }
        ItemStack result = getSmeltingResult(serverLevel, input);
        if (result.isEmpty()) {
            return;
        }
        ItemStack output = items.get(1);
        if (output.isEmpty()) {
            items.set(1, result.copy());
        } else if (ItemStack.isSameItemSameComponents(output, result)) {
            output.grow(result.getCount());
        }
        input.shrink(1);
    }

    public ItemStack getSmeltingResult(ServerLevel serverLevel, ItemStack input) {
        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        Optional<RecipeHolder<SmeltingRecipe>> smeltingMatch = smeltingCheck.getRecipeFor(recipeInput, serverLevel);
        if (smeltingMatch.isPresent()) {
            return smeltingMatch.get().value().assemble(recipeInput);
        }
        Optional<RecipeHolder<BlastingRecipe>> blastingMatch = blastingCheck.getRecipeFor(recipeInput, serverLevel);
        if (blastingMatch.isPresent()) {
            return blastingMatch.get().value().assemble(recipeInput);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.FURNACE_FIRE_CRACKLE;
    }

    @Override
    protected int getBatterySlotIndex() {
        return 2;
    }

    @Override
    protected void spawnProcessCompletedParticles(ServerLevel level, BlockPos pos) {
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 6, 0.2, 0.1, 0.2, 0.05);
        level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 4, 0.1, 0.1, 0.1, 0.02);
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
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
        if (index == 1) {
            return false;
        }
        if (index == 2) {
            return getFuelEnergy(itemStack) > 0;
        }
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack itemStack, Direction direction) {
        return index == 1;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.electric_arc_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new ElectricArcFurnaceMenu(syncId, playerInventory, this, this.dataAccess);
    }
}
