package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.OrbitalMassDriverBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.OrbitalMassDriverMenu;
import com.fhfelipefh.sandstorm.content.item.OrbitalKineticLanceSatelliteItem;
import com.fhfelipefh.sandstorm.content.item.OrbitalSolarReflectorSatelliteItem;
import com.fhfelipefh.sandstorm.content.item.SarGeologicalSatelliteItem;
import com.fhfelipefh.sandstorm.content.item.WeatherReconSatelliteItem;
import com.fhfelipefh.sandstorm.content.satellite.SatelliteNetworkManager;
import com.fhfelipefh.sandstorm.content.satellite.SatelliteType;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
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

public class OrbitalMassDriverBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int MAX_ENERGY = 500000;
    public static final int LAUNCH_COST = 250000;
    private static final int[] SLOTS = new int[]{0};

    private int storedEnergy = MAX_ENERGY;
    private int launchCooldown = 0;
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEnergy;
                case 1 -> MAX_ENERGY;
                case 2 -> launchCooldown;
                case 3 -> canLaunch() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                storedEnergy = value;
            } else if (index == 2) {
                launchCooldown = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public OrbitalMassDriverBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.ORBITAL_MASS_DRIVER_BE, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (storedEnergy < MAX_ENERGY) {
            storedEnergy = Math.min(MAX_ENERGY, storedEnergy + 500);
            setChanged();
        }

        if (launchCooldown > 0) {
            launchCooldown--;
            if (launchCooldown == 0 && state.getValue(OrbitalMassDriverBlock.LIT)) {
                level.setBlock(pos, state.setValue(OrbitalMassDriverBlock.LIT, false), 3);
            }
            setChanged();
        }

        boolean powered = level.hasNeighborSignal(pos);
        if (powered && canLaunch() && launchCooldown == 0) {
            launchSatellite(null);
        }
    }

    public boolean canLaunch() {
        if (this.level == null || storedEnergy < LAUNCH_COST || launchCooldown > 0) {
            return false;
        }
        if (items.getFirst().isEmpty()) {
            return false;
        }
        return this.level.canSeeSky(this.worldPosition.above());
    }

    public boolean launchSatellite(Player player) {
        if (!canLaunch() || this.level == null || this.level.isClientSide()) {
            return false;
        }

        ServerLevel serverLevel = (ServerLevel) this.level;
        ItemStack payload = items.getFirst();
        SatelliteType type = getSatelliteTypeFromPayload(payload);

        storedEnergy -= LAUNCH_COST;
        launchCooldown = 100;
        payload.shrink(1);
        setChanged();

        BlockState state = getBlockState();
        if (!state.getValue(OrbitalMassDriverBlock.LIT)) {
            serverLevel.setBlock(this.worldPosition, state.setValue(OrbitalMassDriverBlock.LIT, true), 3);
        }

        SatelliteNetworkManager.registerSpecificSatelliteLaunched(serverLevel, player, type);

        double bx = this.worldPosition.getX() + 0.5;
        double by = this.worldPosition.getY() + 1.0;
        double bz = this.worldPosition.getZ() + 0.5;

        for (int y = (int) by; y <= 320; y += 8) {
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, bx, y, bz, 1, 0.2, 1.0, 0.2, 0.05);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, bx, y, bz, 6, 0.5, 1.0, 0.5, 0.15);
        }

        serverLevel.playSound(null, this.worldPosition, SandStormSoundEvents.MEGAZORD_SHOCKWAVE, SoundSource.BLOCKS, 2.5f, 1.2f);
        serverLevel.playSound(null, this.worldPosition, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.BLOCKS, 2.0f, 0.6f);

        if (player != null) {
            player.sendSystemMessage(Component.translatable("telemetry.sandstorm.mass_driver_launch_success", Component.translatable("item.sandstorm." + type.getId())));
        }

        return true;
    }

    private SatelliteType getSatelliteTypeFromPayload(ItemStack stack) {
        if (stack.getItem() instanceof WeatherReconSatelliteItem) {
            return SatelliteType.WEATHER_RECON;
        } else if (stack.getItem() instanceof OrbitalSolarReflectorSatelliteItem) {
            return SatelliteType.SOLAR_REFLECTOR;
        } else if (stack.getItem() instanceof SarGeologicalSatelliteItem) {
            return SatelliteType.SAR_GEOLOGICAL;
        } else if (stack.getItem() instanceof OrbitalKineticLanceSatelliteItem) {
            return SatelliteType.KINETIC_LANCE;
        }
        return SatelliteType.SURVEY;
    }

    public int getStoredEnergy() {
        return storedEnergy;
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.clamp(energy, 0, MAX_ENERGY);
        setChanged();
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.orbital_mass_driver");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new OrbitalMassDriverMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.getFirst().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    public void setEnergy(int energy) {
        this.storedEnergy = energy;
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putInt("launchCooldown", this.launchCooldown);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.storedEnergy = input.getIntOr("storedEnergy", MAX_ENERGY);
        this.launchCooldown = input.getIntOr("launchCooldown", 0);
        ContainerHelper.loadAllItems(input, this.items);
    }
}
