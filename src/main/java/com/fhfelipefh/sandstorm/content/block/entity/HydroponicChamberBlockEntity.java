package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.AtmosphericTerraformerBlock;
import com.fhfelipefh.sandstorm.content.block.HydroponicChamberBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.HydroponicChamberMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HydroponicChamberBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{0, 1, 2};
    private static final int[] SLOTS_BOTTOM = new int[]{3, 4};
    private static final int[] SLOTS_SIDES = new int[]{0, 1, 2, 3, 4};

    private boolean insideDome = false;

    public HydroponicChamberBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.HYDROPONIC_CHAMBER_BE, pos, state);
    }

    public HydroponicChamberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 5, 120);
    }

    public boolean isInsideDome() {
        return insideDome;
    }

    @Override
    protected int getBatterySlotIndex() {
        return -1;
    }

    @Override
    protected boolean canProcess() {
        ItemStack seed = items.get(0);
        ItemStack water = items.get(1);
        ItemStack nutrient = items.get(2);

        if (seed.isEmpty() || !isValidSeed(seed)) {
            return false;
        }
        if (!isValidHydrationItem(water)) {
            return false;
        }
        if (SandStormItems.MINERAL_SALT == null || !nutrient.is(SandStormItems.MINERAL_SALT)) {
            return false;
        }

        ItemStack targetOutput = getHarvestResult(seed);
        if (targetOutput.isEmpty()) {
            return false;
        }

        ItemStack currentOut = items.get(3);
        if (!currentOut.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(currentOut, targetOutput)) {
                return false;
            }
            if (currentOut.getCount() + targetOutput.getCount() > currentOut.getMaxStackSize()) {
                return false;
            }
        }

        ItemStack byproduct = items.get(4);
        ItemStack targetByproduct = getByproduct(water);
        if (!byproduct.isEmpty() && !targetByproduct.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(byproduct, targetByproduct)) {
                return false;
            }
            if (byproduct.getCount() + targetByproduct.getCount() > byproduct.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidSeed(ItemStack stack) {
        return stack.is(Items.WHEAT_SEEDS) || stack.is(Items.CARROT) || stack.is(Items.POTATO)
                || stack.is(Items.BEETROOT_SEEDS) || stack.is(Items.MELON_SEEDS) || stack.is(Items.PUMPKIN_SEEDS)
                || stack.is(Items.NETHER_WART)
                || (SandStormItems.XENO_GRASS_SEEDS != null && stack.is(SandStormItems.XENO_GRASS_SEEDS))
                || (SandStormBlocks.HALOPHYTE_PLANT != null && stack.is(SandStormBlocks.HALOPHYTE_PLANT.asItem()));
    }

    private boolean isValidHydrationItem(ItemStack stack) {
        return (SandStormItems.POTABLE_WATER_BOTTLE != null && stack.is(SandStormItems.POTABLE_WATER_BOTTLE))
                || stack.is(Items.POTION) || stack.is(Items.WATER_BUCKET);
    }

    private ItemStack getByproduct(ItemStack water) {
        if (water.is(Items.WATER_BUCKET)) {
            return new ItemStack(Items.BUCKET);
        }
        return new ItemStack(Items.GLASS_BOTTLE);
    }

    private ItemStack getHarvestResult(ItemStack seed) {
        int bonus = insideDome ? 1 : 0;
        if (seed.is(Items.WHEAT_SEEDS)) {
            return new ItemStack(Items.WHEAT, 2 + bonus);
        }
        if (seed.is(Items.CARROT)) {
            return new ItemStack(Items.CARROT, 3 + bonus);
        }
        if (seed.is(Items.POTATO)) {
            return new ItemStack(Items.POTATO, 3 + bonus);
        }
        if (seed.is(Items.BEETROOT_SEEDS)) {
            return new ItemStack(Items.BEETROOT, 2 + bonus);
        }
        if (seed.is(Items.MELON_SEEDS)) {
            return new ItemStack(Items.MELON_SLICE, 4 + bonus);
        }
        if (seed.is(Items.PUMPKIN_SEEDS)) {
            return new ItemStack(Items.PUMPKIN, 1 + bonus);
        }
        if (seed.is(Items.NETHER_WART)) {
            return new ItemStack(Items.NETHER_WART, 3 + bonus);
        }
        if (SandStormItems.XENO_GRASS_SEEDS != null && seed.is(SandStormItems.XENO_GRASS_SEEDS)) {
            return new ItemStack(SandStormItems.XENO_GRASS_SEEDS, 3 + bonus);
        }
        if (SandStormBlocks.HALOPHYTE_PLANT != null && seed.is(SandStormBlocks.HALOPHYTE_PLANT.asItem())) {
            return new ItemStack(SandStormBlocks.HALOPHYTE_PLANT.asItem(), 2 + bonus);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void processRecipe() {
        if (!canProcess()) {
            return;
        }

        ItemStack seed = items.get(0);
        ItemStack water = items.get(1);
        ItemStack harvest = getHarvestResult(seed);
        ItemStack byproduct = getByproduct(water);

        seed.shrink(1);
        water.shrink(1);
        items.get(2).shrink(1);

        ItemStack currentOut = items.get(3);
        if (currentOut.isEmpty()) {
            items.set(3, harvest);
        } else {
            currentOut.grow(harvest.getCount());
        }

        if (!byproduct.isEmpty()) {
            ItemStack currentByproduct = items.get(4);
            if (currentByproduct.isEmpty()) {
                items.set(4, byproduct);
            } else {
                currentByproduct.grow(byproduct.getCount());
            }
        }
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.getGameTime() % 40 == 0) {
            checkTerraformingDome();
        }

        if (isProcessing() && insideDome) {
            progress = Math.min(maxProgress, progress + 2);
        }

        super.serverTick(level, pos, state);

        boolean active = isProcessing();
        if (state.getValue(HydroponicChamberBlock.LIT) != active) {
            level.setBlock(pos, state.setValue(HydroponicChamberBlock.LIT, active), 3);
        }
    }

    private void checkTerraformingDome() {
        if (level == null) {
            return;
        }
        insideDome = false;
        int checkRadius = 24;
        BlockPos.betweenClosedStream(worldPosition.offset(-checkRadius, -6, -checkRadius), worldPosition.offset(checkRadius, 6, checkRadius))
                .forEach(pos -> {
                    if (insideDome) {
                        return;
                    }
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock() instanceof AtmosphericTerraformerBlock terraformer) {
                        if (terraformer.getTerraformingIndex().getProgress() > 0) {
                            int radius = terraformer.getTerraformingIndex().getDomeRadius();
                            if (worldPosition.distSqr(pos) <= (double) (radius * radius)) {
                                insideDome = true;
                            }
                        }
                    }
                });
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.WATER_AMBIENT;
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
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (slot >= 3) {
            return false;
        }
        if (slot == 0) {
            return isValidSeed(stack);
        }
        if (slot == 1) {
            return isValidHydrationItem(stack);
        }
        return SandStormItems.MINERAL_SALT != null && stack.is(SandStormItems.MINERAL_SALT);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 3 || slot == 4;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.hydroponic_chamber");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new HydroponicChamberMenu(syncId, playerInventory, this, dataAccess);
    }
}
