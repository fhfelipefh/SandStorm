package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.BioreactorVatMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BioreactorVatBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{0, 1};
    private static final int[] SLOTS_BOTTOM = new int[]{3, 2};
    private static final int[] SLOTS_SIDES = new int[]{0, 1, 2, 3};

    public BioreactorVatBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.BIOREACTOR_VAT_BE, pos, state);
    }

    public BioreactorVatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 4, 120);
    }

    private ItemStack getExpectedOutput() {
        ItemStack in0 = items.get(0);
        ItemStack in1 = items.get(1);

        if ((in0.is(SandStormBlocks.CHITINOLYTIC_FUNGUS.asItem()) || in0.is(SandStormItems.SANDWORM_CHITIN)) && (in1.is(SandStormItems.HEAVY_SAP_BOTTLE) || in1.is(SandStormItems.POTABLE_WATER_BOTTLE))) {
            return new ItemStack(SandStormItems.CHITOSAN_EXTRACT);
        }
        if ((in0.is(SandStormBlocks.RADIOTROPHIC_MYCELIUM.asItem()) || in0.is(SandStormBlocks.ELECTRIFIED_SAND.asItem())) && (in1.is(SandStormItems.RAW_SILICON) || in1.is(SandStormItems.POTABLE_WATER_BOTTLE))) {
            return new ItemStack(SandStormItems.RADIOPROTECTIVE_MELANIN);
        }
        if ((in0.is(SandStormBlocks.HALOPHYTE_SUCCULENT.asItem()) || in0.is(SandStormBlocks.SALINIZED_SAND.asItem())) && (in1.is(SandStormItems.BRACKISH_WATER_BOTTLE) || in1.is(SandStormItems.POTABLE_WATER_BOTTLE))) {
            return new ItemStack(SandStormItems.OSMOLYTE_GLYCEROL);
        }
        if ((in0.is(SandStormBlocks.CRYO_XEROPHILIC_LICHEN.asItem()) || in0.is(SandStormItems.MINERAL_SALT)) && in1.is(SandStormItems.POTABLE_WATER_BOTTLE)) {
            return new ItemStack(SandStormItems.TREHALOSE_SUGAR);
        }
        if ((in0.is(SandStormBlocks.DUNE_EPHEDRA.asItem()) || in0.is(SandStormItems.ANCIENT_SEED) || in0.is(SandStormItems.XENO_GRASS_SEEDS)) && (in1.is(SandStormItems.MINERAL_SALT) || in1.is(SandStormItems.POTABLE_WATER_BOTTLE))) {
            return new ItemStack(SandStormItems.NEUROACTIVE_ALKALOIDS);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected boolean canProcess() {
        ItemStack expected = getExpectedOutput();
        if (expected.isEmpty()) {
            return false;
        }
        ItemStack out = items.get(3);
        if (out.isEmpty()) {
            return true;
        }
        return out.is(expected.getItem()) && out.getCount() + expected.getCount() <= out.getMaxStackSize();
    }

    @Override
    protected void processRecipe() {
        if (!canProcess()) {
            return;
        }
        ItemStack expected = getExpectedOutput();
        items.get(0).shrink(1);
        if (items.get(1).is(SandStormItems.HEAVY_SAP_BOTTLE) || items.get(1).is(SandStormItems.BRACKISH_WATER_BOTTLE) || items.get(1).is(SandStormItems.POTABLE_WATER_BOTTLE)) {
            items.set(1, new ItemStack(Items.GLASS_BOTTLE));
        } else {
            items.get(1).shrink(1);
        }

        ItemStack out = items.get(3);
        if (out.isEmpty()) {
            items.set(3, expected.copy());
        } else {
            out.grow(expected.getCount());
        }
    }

    @Override
    protected int getBatterySlotIndex() {
        return 2;
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.BREWING_STAND_BREW;
    }

    @Override
    protected void onProcessCompleted(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.85f, 1.2f);
        level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 0.6f, 1.3f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 8, 0.25, 0.15, 0.25, 0.05);
            serverLevel.sendParticles(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 10, 0.2, 0.1, 0.2, 0.05);
            serverLevel.sendParticles(ParticleTypes.COMPOSTER, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, 4, 0.2, 0.1, 0.2, 0.02);
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
        if (slot == 3) {
            return false;
        }
        if (slot == 2) {
            return BaseMachineBlockEntity.getFuelEnergy(stack) > 0;
        }
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 3 || (slot == 1 && stack.is(Items.GLASS_BOTTLE));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.bioreactor_vat");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new BioreactorVatMenu(syncId, playerInventory, this, this.dataAccess);
    }
}
