package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SpectralSurveyTelescopeBlock;
import com.fhfelipefh.sandstorm.content.gui.SpectralSurveyTelescopeMenu;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SpectralSurveyTelescopeBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int MAX_ENERGY = 50000;
    private static final int[] SLOTS = new int[]{0};

    private int storedEnergy = MAX_ENERGY;
    private int activeFilter = 0;
    private int celestialTarget = 0;
    private int scanProgress = 0;
    private boolean hasSky = false;
    private int stormThreat = 0;
    private int secondsToStorm = 0;
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEnergy;
                case 1 -> MAX_ENERGY;
                case 2 -> activeFilter;
                case 3 -> celestialTarget;
                case 4 -> scanProgress;
                case 5 -> hasSky ? 1 : 0;
                case 6 -> stormThreat;
                case 7 -> secondsToStorm;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> storedEnergy = value;
                case 2 -> activeFilter = value;
                case 3 -> celestialTarget = value;
                case 4 -> scanProgress = value;
                case 5 -> hasSky = (value == 1);
                case 6 -> stormThreat = value;
                case 7 -> secondsToStorm = value;
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public SpectralSurveyTelescopeBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.SPECTRAL_SURVEY_TELESCOPE_BE, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean canSee = level.canSeeSky(pos.above());
        this.hasSky = canSee;

        if (this.hasSky && storedEnergy < MAX_ENERGY) {
            storedEnergy = Math.min(MAX_ENERGY, storedEnergy + 50);
            setChanged();
        }

        if (scanProgress > 0 && scanProgress < 100 && storedEnergy >= 10) {
            if (level.getGameTime() % 10 == 0) {
                storedEnergy -= 10;
                scanProgress = Math.min(100, scanProgress + 1);
                setChanged();
            }
        }

        if (level.getGameTime() % 20 == 0) {
            updateWeatherTelemetry(level);
        }

        if (level.getGameTime() % 40 == 0 && level instanceof ServerLevel serverLevel) {
            if (hasSky && state.getValue(SpectralSurveyTelescopeBlock.ACTIVE)) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                        2, 0.1, 0.2, 0.1, 0.02);
            }

            if (state.getValue(SpectralSurveyTelescopeBlock.ACTIVE) != hasSky) {
                level.setBlock(pos, state.setValue(SpectralSurveyTelescopeBlock.ACTIVE, hasSky), 3);
            }
        }
    }

    private void updateWeatherTelemetry(Level level) {
        if (SandstormWeatherHandler.getWeather().isActive()) {
            this.stormThreat = 2;
            this.secondsToStorm = (int) (SandstormWeatherHandler.getWeather().getRemainingTicks() / 20);
        } else {
            long diff = SandstormWeatherHandler.getNextSandstormGameTime() - level.getGameTime();
            if (diff <= 1200 && diff > 0) {
                this.stormThreat = 1;
                this.secondsToStorm = (int) (diff / 20);
            } else {
                this.stormThreat = 0;
                this.secondsToStorm = (int) Math.max(0, diff / 20);
            }
        }
    }

    public boolean handleMenuButton(int buttonId) {
        if (this.level == null) {
            return false;
        }

        if (buttonId == SpectralSurveyTelescopeMenu.BUTTON_CYCLE_TARGET) {
            this.celestialTarget = (this.celestialTarget + 1) % 5;
            this.scanProgress = 0;
            this.level.playSound(null, this.worldPosition, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            setChanged();
            return true;
        }

        if (buttonId == SpectralSurveyTelescopeMenu.BUTTON_FILTER_OPTICAL) {
            this.activeFilter = SpectralSurveyTelescopeMenu.FILTER_OPTICAL;
            this.level.playSound(null, this.worldPosition, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.0f);
            setChanged();
            return true;
        }

        if (buttonId == SpectralSurveyTelescopeMenu.BUTTON_FILTER_INFRARED) {
            this.activeFilter = SpectralSurveyTelescopeMenu.FILTER_INFRARED;
            this.level.playSound(null, this.worldPosition, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.0f);
            setChanged();
            return true;
        }

        if (buttonId == SpectralSurveyTelescopeMenu.BUTTON_FILTER_RADIO) {
            this.activeFilter = SpectralSurveyTelescopeMenu.FILTER_RADIO_XRAY;
            this.level.playSound(null, this.worldPosition, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.0f);
            setChanged();
            return true;
        }

        if (buttonId == SpectralSurveyTelescopeMenu.BUTTON_TRIGGER_SCAN) {
            if (this.storedEnergy >= 500 && this.hasSky) {
                this.storedEnergy -= 500;
                this.scanProgress = Math.min(100, this.scanProgress + 25);
                this.level.playSound(null, this.worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.6f);
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.2, this.worldPosition.getZ() + 0.5,
                            8, 0.2, 0.3, 0.2, 0.05);
                }
                setChanged();
                return true;
            }
        }

        return false;
    }

    public int getStoredEnergy() {
        return storedEnergy;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.clamp(energy, 0, MAX_ENERGY);
        setChanged();
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new SpectralSurveyTelescopeMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.spectral_survey_telescope");
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
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
            setChanged();
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
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putInt("activeFilter", this.activeFilter);
        output.putInt("celestialTarget", this.celestialTarget);
        output.putInt("scanProgress", this.scanProgress);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.storedEnergy = input.getIntOr("storedEnergy", MAX_ENERGY);
        this.activeFilter = input.getIntOr("activeFilter", 0);
        this.celestialTarget = input.getIntOr("celestialTarget", 0);
        this.scanProgress = input.getIntOr("scanProgress", 0);
        ContainerHelper.loadAllItems(input, this.items);
    }
}
