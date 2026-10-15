package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Collections;

public class CryoRevivalVatBlockEntity extends BlockEntity implements WorldlyContainer {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_COOLANT = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int MAX_PROGRESS = 100;

    private static final int[] SLOTS_TOP = new int[]{SLOT_INPUT};
    private static final int[] SLOTS_SIDES = new int[]{SLOT_COOLANT};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_OUTPUT};

    private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int progress = 0;

    public CryoRevivalVatBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.CRYO_REVIVAL_VAT_BE, pos, state);
    }

    public CryoRevivalVatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CryoRevivalVatBlockEntity be) {
        if (be.canProcess()) {
            be.progress++;
            if (level instanceof ServerLevel serverLevel && level.getGameTime() % 10 == 0) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                        pos.getX() + 0.5, pos.getY() + 0.85, pos.getZ() + 0.5,
                        2, 0.2, 0.1, 0.2, 0.01);
            }

            if (be.progress >= MAX_PROGRESS) {
                be.processRecipe();
                be.progress = 0;
                be.setChanged();

                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.9f, 1.2f);
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.7f, 1.5f);

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5,
                            6, 0.25, 0.15, 0.25, 0.05);
                }
            }
        } else {
            be.progress = 0;
        }
    }

    public ItemStack getExpectedOutput() {
        ItemStack in = this.items.get(SLOT_INPUT);
        ItemStack cool = this.items.get(SLOT_COOLANT);

        if (in.isEmpty() || cool.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (in.is(SandStormBlocks.FOSSILIZED_AMBER.asItem()) && (cool.is(SandStormBlocks.BRITTLE_PERMAFROST.asItem()) || cool.is(SandStormBlocks.SUBGLACIAL_ICE_SPIKE.asItem()))) {
            return new ItemStack(SandStormItems.BIO_NEURAL_CORE);
        }

        if (in.is(SandStormItems.XENO_GRASS_SEEDS) && (cool.is(SandStormBlocks.BRITTLE_PERMAFROST.asItem()) || cool.is(SandStormItems.MINERAL_SALT))) {
            return new ItemStack(SandStormItems.CRYO_TREHALOSE_CELL);
        }

        if ((in.is(Items.WHEAT_SEEDS) || in.is(Items.MELON_SEEDS) || in.is(Items.PUMPKIN_SEEDS) || in.is(Items.BEETROOT_SEEDS)) && (cool.is(SandStormItems.MINERAL_SALT) || cool.is(SandStormBlocks.BRITTLE_PERMAFROST.asItem()))) {
            return new ItemStack(SandStormItems.XENO_GRASS_SEEDS, 2);
        }

        if (in.is(SandStormBlocks.SUBGLACIAL_ICE_SPIKE.asItem()) && cool.is(SandStormItems.MINERAL_SALT)) {
            return new ItemStack(SandStormItems.BIO_COOLANT_CANISTER);
        }

        return ItemStack.EMPTY;
    }

    public boolean canProcess() {
        ItemStack expected = getExpectedOutput();
        if (expected.isEmpty()) {
            return false;
        }
        ItemStack out = this.items.get(SLOT_OUTPUT);
        if (out.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(out, expected) && out.getCount() + expected.getCount() <= out.getMaxStackSize();
    }

    public void processRecipe() {
        if (!canProcess()) {
            return;
        }
        ItemStack expected = getExpectedOutput();
        this.items.get(SLOT_INPUT).shrink(1);
        this.items.get(SLOT_COOLANT).shrink(1);

        ItemStack out = this.items.get(SLOT_OUTPUT);
        if (out.isEmpty()) {
            this.items.set(SLOT_OUTPUT, expected.copy());
        } else {
            out.grow(expected.getCount());
        }
    }

    public int getProgress() {
        return this.progress;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("progress", this.progress);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        Collections.fill(this.items, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.progress = input.getIntOr("progress", 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
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
        if (slot == SLOT_COOLANT) {
            return side != Direction.UP && (stack.is(SandStormBlocks.BRITTLE_PERMAFROST.asItem()) || stack.is(SandStormBlocks.SUBGLACIAL_ICE_SPIKE.asItem()) || stack.is(SandStormItems.MINERAL_SALT));
        }
        return slot == SLOT_INPUT && side == Direction.UP;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == SLOT_OUTPUT;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack item : this.items) {
            if (!item.isEmpty()) {
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
            this.setChanged();
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
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }
}
