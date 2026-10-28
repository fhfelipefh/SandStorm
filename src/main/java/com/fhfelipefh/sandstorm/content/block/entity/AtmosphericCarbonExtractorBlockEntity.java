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

public class AtmosphericCarbonExtractorBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{};
    private static final int[] SLOTS_BOTTOM = new int[]{0};
    private static final int[] SLOTS_SIDES = new int[]{1};

    public AtmosphericCarbonExtractorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.ATMOSPHERIC_CARBON_EXTRACTOR_BE, pos, state);
    }

    public AtmosphericCarbonExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 2, 1200);
        this.energyCostPerTick = 20;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        boolean processing = isProcessing();
        if (state.hasProperty(AtmosphericCarbonExtractorBlock.LIT) && state.getValue(AtmosphericCarbonExtractorBlock.LIT) != processing) {
            level.setBlock(pos, state.setValue(AtmosphericCarbonExtractorBlock.LIT, processing), 3);
        }

        if (processing && level instanceof ServerLevel serverLevel && level.getGameTime() % 10 == 0) {
            serverLevel.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 2, 0.1, 0.1, 0.1, 0.02);
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
        ItemStack out = items.get(0);
        if (out.isEmpty()) {
            return true;
        }
        return out.is(Items.COAL) && out.getCount() < out.getMaxStackSize();
    }

    @Override
    protected void processRecipe() {
        ItemStack out = items.get(0);
        if (out.isEmpty()) {
            items.set(0, new ItemStack(Items.COAL, 1));
        } else if (out.is(Items.COAL) && out.getCount() < out.getMaxStackSize()) {
            out.grow(1);
        }
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.ITEM_PICKUP;
    }

    @Override
    protected int getBatterySlotIndex() {
        return 1;
    }

    @Override
    protected void spawnProcessCompletedParticles(ServerLevel level, BlockPos pos) {
        level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 8, 0.2, 0.1, 0.2, 0.03);
        level.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 6, 0.1, 0.1, 0.1, 0.05);
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
            return false;
        }
        if (index == 1) {
            return getFuelEnergy(itemStack) > 0;
        }
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack itemStack, Direction direction) {
        return index == 0;
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
