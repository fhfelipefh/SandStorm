package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.defense.KineticShieldTracker;
import com.fhfelipefh.sandstorm.content.entity.BuilderDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.gui.MegastructureConstructorMenu;
import com.fhfelipefh.sandstorm.content.megastructure.MegastructureBlueprint;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class MegastructureConstructorBlockEntity extends BaseMachineBlockEntity {
    public static final int STATE_IDLE = 0;
    public static final int STATE_CALIBRATING = 1;
    public static final int STATE_BUILDING = 2;
    public static final int STATE_PAUSED = 3;
    public static final int STATE_COMPLETED = 4;

    private int blueprintIndex = 0;
    private int buildState = STATE_IDLE;
    private int placementIndex = 0;
    private int buildSpeedMode = 1;
    private int processTickCounter = 0;

    private final ContainerData constructorDataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy;
                case 1 -> maxEnergy;
                case 2 -> getPlacementPercent();
                case 3 -> 100;
                case 4 -> wptConnected ? 1 : 0;
                case 5 -> buildState;
                case 6 -> blueprintIndex;
                case 7 -> placementIndex;
                case 8 -> getTotalPlacements();
                case 9 -> getCurrentLayerY();
                case 10 -> buildSpeedMode;
                case 11 -> getMaterialReadinessPercent();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energy = value;
                case 1 -> maxEnergy = value;
                case 4 -> wptConnected = (value == 1);
                case 5 -> buildState = value;
                case 6 -> setBlueprintIndex(value);
                case 7 -> placementIndex = value;
                case 10 -> buildSpeedMode = value;
            }
        }

        @Override
        public int getCount() {
            return 12;
        }
    };

    public MegastructureConstructorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 18, 100);
        this.maxEnergy = 500000;
        this.energyCostPerTick = 15;
    }

    public MegastructureConstructorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR_BE, pos, state);
    }

    public MegastructureBlueprint getBlueprint() {
        return MegastructureBlueprint.byIndex(blueprintIndex);
    }

    public int getBlueprintIndex() {
        return blueprintIndex;
    }

    public void setBlueprintIndex(int index) {
        if (this.blueprintIndex != index) {
            this.blueprintIndex = index;
            this.placementIndex = 0;
            this.buildState = STATE_IDLE;
            if (this.level != null) {
                this.level.playSound(null, this.worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.8f, 1.6f);
            }
            setChanged();
            if (this.level != null && !this.level.isClientSide()) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }

    public int getBuildState() {
        return buildState;
    }

    public void setBuildState(int state) {
        this.buildState = state;
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public int completeInstantly() {
        if (level == null) {
            return 0;
        }
        List<MegastructureBlueprint.BlockPlacement> placements = getBlueprint().getPlacements();
        int placedCount = 0;
        for (MegastructureBlueprint.BlockPlacement placement : placements) {
            BlockPos target = getBlockPos().offset(placement.relativePos());
            if (level.isLoaded(target) && !level.getBlockState(target).equals(placement.state())) {
                level.setBlock(target, placement.state(), 3);
                placedCount++;
            }
        }
        this.placementIndex = placements.size();
        this.buildState = STATE_COMPLETED;
        MegastructureBlueprint bp = getBlueprint();
        double radius = Math.max(bp.getSizeX(), bp.getSizeZ()) * 0.5 + 4.0;
        KineticShieldTracker.registerShield(level.dimension(), getBlockPos(), radius);
        level.playSound(null, getBlockPos(), SandStormSoundEvents.MEGASTRUCTURE_COMPLETE, SoundSource.BLOCKS, 2.0f, 1.0f);
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
        return placedCount;
    }

    public int getPlacementIndex() {
        return placementIndex;
    }

    public int getTotalPlacements() {
        return getBlueprint().getPlacements().size();
    }

    public int getPlacementPercent() {
        int total = getTotalPlacements();
        if (total == 0) {
            return 0;
        }
        return Math.min(100, (int) ((placementIndex * 100.0) / total));
    }

    public int getCurrentLayerY() {
        List<MegastructureBlueprint.BlockPlacement> placements = getBlueprint().getPlacements();
        if (placements.isEmpty() || placementIndex >= placements.size()) {
            return 0;
        }
        return placements.get(placementIndex).relativePos().getY();
    }

    public int getBuildSpeedMode() {
        return buildSpeedMode;
    }

    public void setBuildSpeedMode(int mode) {
        this.buildSpeedMode = mode;
        setChanged();
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(this.maxEnergy, Math.max(0, energy));
        setChanged();
    }

    public ContainerData getConstructorDataAccess() {
        return constructorDataAccess;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

        if (buildState != STATE_BUILDING) {
            return;
        }

        int energyCost = switch (buildSpeedMode) {
            case 0 -> 5;
            case 2 -> 40;
            default -> 15;
        };

        if (energy < energyCost) {
            buildState = STATE_PAUSED;
            setChanged();
            return;
        }
        energy -= energyCost;

        processTickCounter++;
        int interval = switch (buildSpeedMode) {
            case 0 -> 20;
            case 2 -> 4;
            default -> 10;
        };

        if (processTickCounter % interval != 0) {
            return;
        }

        List<MegastructureBlueprint.BlockPlacement> placements = getBlueprint().getPlacements();
        if (placementIndex >= placements.size()) {
            buildState = STATE_COMPLETED;
            MegastructureBlueprint bp = getBlueprint();
            double radius = Math.max(bp.getSizeX(), bp.getSizeZ()) * 0.5 + 4.0;
            KineticShieldTracker.registerShield(level.dimension(), pos, radius);
            level.playSound(null, pos, SandStormSoundEvents.MEGASTRUCTURE_COMPLETE, SoundSource.BLOCKS, 2.0f, 1.0f);
            setChanged();
            return;
        }

        MegastructureBlueprint.BlockPlacement targetPlacement = placements.get(placementIndex);
        BlockPos worldTarget = pos.offset(targetPlacement.relativePos());

        if (!level.isLoaded(worldTarget)) {
            return;
        }

        if (level.getBlockState(worldTarget).equals(targetPlacement.state())) {
            advancePlacement(placements);
            return;
        }

        int slot = findItemSlotForBlock(targetPlacement.state());
        if (slot != -1) {
            ItemStack stack = getItem(slot);
            stack.shrink(1);
            level.setBlock(worldTarget, targetPlacement.state(), 3);
            spawnBuilderDroneIfNeeded((ServerLevel) level, pos, worldTarget);
            advancePlacement(placements);
        } else if (extractFromAdjacentContainer(targetPlacement.state())) {
            level.setBlock(worldTarget, targetPlacement.state(), 3);
            spawnBuilderDroneIfNeeded((ServerLevel) level, pos, worldTarget);
            advancePlacement(placements);
        } else {
            buildState = STATE_PAUSED;
            setChanged();
        }
    }

    private void advancePlacement(List<MegastructureBlueprint.BlockPlacement> placements) {
        int previousY = placements.get(placementIndex).relativePos().getY();
        placementIndex++;
        if (placementIndex < placements.size()) {
            int nextY = placements.get(placementIndex).relativePos().getY();
            if (nextY > previousY && level != null) {
                level.playSound(null, getBlockPos(), SandStormSoundEvents.MEGASTRUCTURE_LAYER_COMPLETE, SoundSource.BLOCKS, 1.5f, 1.0f);
            }
        }
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    private int findItemSlotForBlock(BlockState state) {
        for (int i = 0; i < 18; i++) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty() && stack.is(state.getBlock().asItem())) {
                return i;
            }
        }
        return -1;
    }

    private boolean extractFromAdjacentContainer(BlockState state) {
        if (level == null) {
            return false;
        }
        Item item = state.getBlock().asItem();
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(worldPosition.relative(dir));
            if (be instanceof Container adj && !(be instanceof MegastructureConstructorBlockEntity)) {
                for (int i = 0; i < adj.getContainerSize(); i++) {
                    ItemStack st = adj.getItem(i);
                    if (!st.isEmpty() && st.is(item)) {
                        st.shrink(1);
                        adj.setChanged();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int countAvailableItems(Item item) {
        int count = 0;
        for (int i = 0; i < 18; i++) {
            ItemStack st = getItem(i);
            if (!st.isEmpty() && st.is(item)) {
                count += st.getCount();
            }
        }
        if (level != null) {
            for (Direction dir : Direction.values()) {
                BlockEntity be = level.getBlockEntity(worldPosition.relative(dir));
                if (be instanceof Container adj && !(be instanceof MegastructureConstructorBlockEntity)) {
                    for (int i = 0; i < adj.getContainerSize(); i++) {
                        ItemStack st = adj.getItem(i);
                        if (!st.isEmpty() && st.is(item)) {
                            count += st.getCount();
                        }
                    }
                }
            }
        }
        return count;
    }

    public int getMaterialReadinessPercent() {
        MegastructureBlueprint bp = getBlueprint();
        List<MegastructureBlueprint.MaterialCost> costs = bp.getMaterialCosts();
        if (costs.isEmpty()) {
            return 100;
        }
        int totalNeeded = 0;
        int totalFound = 0;
        for (MegastructureBlueprint.MaterialCost cost : costs) {
            totalNeeded += cost.count();
            int avail = countAvailableItems(cost.item());
            totalFound += Math.min(cost.count(), avail);
        }
        if (totalNeeded <= 0) {
            return 100;
        }
        return Math.min(100, (int) ((totalFound * 100.0) / totalNeeded));
    }

    private void spawnBuilderDroneIfNeeded(ServerLevel serverLevel, BlockPos center, BlockPos target) {
        List<BuilderDroneEntity> drones = serverLevel.getEntitiesOfClass(
                BuilderDroneEntity.class,
                new AABB(center).inflate(32.0),
                d -> d.getConstructorPos().equals(center)
        );
        if (drones.isEmpty()) {
            BuilderDroneEntity drone = new BuilderDroneEntity(SandStormEntities.BUILDER_DRONE, serverLevel);
            drone.setPos(center.getX() + 0.5, center.getY() + 1.8, center.getZ() + 0.5);
            drone.setConstructorPos(center);
            drone.setTargetPos(target);
            drone.setHasBlock(true);
            serverLevel.addFreshEntity(drone);
        } else {
            BuilderDroneEntity drone = drones.getFirst();
            drone.setTargetPos(target);
            drone.setHasBlock(true);
        }
    }

    @Override
    protected boolean canProcess() {
        return buildState == STATE_BUILDING;
    }

    @Override
    protected void processRecipe() {
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SandStormSoundEvents.MEGASTRUCTURE_CONSTRUCTOR_LASER;
    }

    @Override
    protected int getBatterySlotIndex() {
        return 0;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        int[] slots = new int[18];
        for (int i = 0; i < 18; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.megastructure_constructor");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new MegastructureConstructorMenu(syncId, playerInventory, this, this.constructorDataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("blueprintIndex", this.blueprintIndex);
        output.putInt("buildState", this.buildState);
        output.putInt("placementIndex", this.placementIndex);
        output.putInt("buildSpeedMode", this.buildSpeedMode);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        this.blueprintIndex = input.getIntOr("blueprintIndex", 0);
        this.buildState = input.getIntOr("buildState", 0);
        this.placementIndex = input.getIntOr("placementIndex", 0);
        this.buildSpeedMode = input.getIntOr("buildSpeedMode", 1);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = registries != null ? super.getUpdateTag(registries) : new CompoundTag();
        tag.putInt("blueprintIndex", this.blueprintIndex);
        tag.putInt("buildState", this.buildState);
        tag.putInt("placementIndex", this.placementIndex);
        tag.putInt("buildSpeedMode", this.buildSpeedMode);
        tag.putInt("materialReadiness", getMaterialReadinessPercent());
        return tag;
    }

    public BlockPos getCurrentTargetRelPos() {
        List<MegastructureBlueprint.BlockPlacement> placements = getBlueprint().getPlacements();
        if (placements.isEmpty() || placementIndex >= placements.size()) {
            return null;
        }
        return placements.get(placementIndex).relativePos();
    }
}
