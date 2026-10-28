package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.AtmosphericCarbonExtractorBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.AtmosphericCarbonExtractorMenu;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

public class AtmosphericCarbonExtractorBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_BOTTOM = new int[]{1};
    private static final int[] SLOTS_SIDES = new int[]{2};

    private boolean wasDiamondMode = false;

    public AtmosphericCarbonExtractorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.ATMOSPHERIC_CARBON_EXTRACTOR_BE, pos, state);
    }

    public AtmosphericCarbonExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 3, 1200);
        this.energyCostPerTick = 20;
    }

    public boolean isDiamondMode() {
        ItemStack seed = items.get(0);
        return !seed.isEmpty() && seed.is(Items.DIAMOND);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        boolean diamond = isDiamondMode();
        this.wasDiamondMode = diamond;
        this.maxProgress = diamond ? 4800 : 1200;
        this.energyCostPerTick = diamond ? 35 : 20;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean diamond = isDiamondMode();
        if (diamond != wasDiamondMode) {
            wasDiamondMode = diamond;
            this.progress = 0;
            this.maxProgress = diamond ? 4800 : 1200;
            this.energyCostPerTick = diamond ? 35 : 20;
            setChanged();
        }

        super.serverTick(level, pos, state);
        boolean processing = isProcessing();
        if (state.hasProperty(AtmosphericCarbonExtractorBlock.LIT) && state.getValue(AtmosphericCarbonExtractorBlock.LIT) != processing) {
            level.setBlock(pos, state.setValue(AtmosphericCarbonExtractorBlock.LIT, processing), 3);
        }

        if (processing && level instanceof ServerLevel serverLevel && level.getGameTime() % 10 == 0) {
            if (diamond) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 3, 0.15, 0.15, 0.15, 0.02);
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 2, 0.1, 0.1, 0.1, 0.02);
            } else {
                serverLevel.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 2, 0.1, 0.1, 0.1, 0.02);
            }
        }
    }

    @Override
    protected boolean canProcess() {
        if (level == null) {
            return false;
        }
        BlockPos above = getBlockPos().above();
        if (!level.canSeeSky(above) && !level.getBlockState(above).isAir()) {
            return false;
        }
        ItemStack seed = items.get(0);
        if (!seed.isEmpty() && !seed.is(Items.DIAMOND)) {
            return false;
        }
        ItemStack out = items.get(1);
        if (isDiamondMode()) {
            if (out.isEmpty()) {
                return true;
            }
            return out.is(Items.DIAMOND) && out.getCount() < out.getMaxStackSize();
        } else {
            if (out.isEmpty()) {
                return true;
            }
            return out.is(Items.COAL) && out.getCount() < out.getMaxStackSize();
        }
    }

    @Override
    protected void processRecipe() {
        ItemStack out = items.get(1);
        if (isDiamondMode()) {
            if (out.isEmpty()) {
                items.set(1, new ItemStack(Items.DIAMOND, 1));
            } else if (out.is(Items.DIAMOND) && out.getCount() < out.getMaxStackSize()) {
                out.grow(1);
            }
        } else {
            if (out.isEmpty()) {
                items.set(1, new ItemStack(Items.COAL, 1));
            } else if (out.is(Items.COAL) && out.getCount() < out.getMaxStackSize()) {
                out.grow(1);
            }
        }
    }

    @Override
    protected SoundEvent getProcessSound() {
        return isDiamondMode() ? SoundEvents.AMETHYST_BLOCK_CHIME : SoundEvents.ITEM_PICKUP;
    }

    @Override
    protected int getBatterySlotIndex() {
        return 2;
    }

    @Override
    protected void spawnProcessCompletedParticles(ServerLevel level, BlockPos pos) {
        if (isDiamondMode()) {
            level.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 12, 0.2, 0.2, 0.2, 0.05);
            level.sendParticles(ParticleTypes.FIREWORK, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 8, 0.15, 0.15, 0.15, 0.04);
        } else {
            level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 8, 0.2, 0.1, 0.2, 0.03);
            level.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 6, 0.1, 0.1, 0.1, 0.05);
        }
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
        if (index == 0) {
            return itemStack.is(Items.DIAMOND);
        }
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
        return Component.translatable("container.sandstorm.atmospheric_carbon_extractor");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new AtmosphericCarbonExtractorMenu(syncId, playerInventory, this, this.dataAccess);
    }
}
