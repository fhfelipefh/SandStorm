package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.CyborgDockingStationBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.gui.CyborgDockingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class CyborgDockingStationBlockEntity extends BlockEntity implements MenuProvider {
    public static final int MAX_DOCK_ENERGY = 50000;

    private int dockEnergy = MAX_DOCK_ENERGY;
    private UUID dockedCyborgUUID = null;
    private CyborgEntity cachedCyborg = null;
    private int tickCounter = 0;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> dockedCyborgUUID != null ? 1 : 0;
                case 1 -> dockEnergy;
                case 2 -> MAX_DOCK_ENERGY;
                case 3 -> cachedCyborg != null ? cachedCyborg.getEnergy() : 0;
                case 4 -> cachedCyborg != null ? cachedCyborg.getMaxEnergy() : 0;
                case 5 -> cachedCyborg != null ? cachedCyborg.getIntegrity() : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 1) {
                dockEnergy = value < 0 ? (value & 0xFFFF) : value;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public CyborgDockingStationBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.CYBORG_DOCKING_STATION_BE, pos, state);
    }

    public void serverTick() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        tickCounter++;

        if (dockEnergy < MAX_DOCK_ENERGY) {
            dockEnergy = Math.min(MAX_DOCK_ENERGY, dockEnergy + 25);
        }

        if (dockedCyborgUUID == null) {
            if (tickCounter % 10 == 0) {
                scanAndDockNearbyCyborg();
            }
        } else {
            processDockedCyborg();
        }
    }

    private void scanAndDockNearbyCyborg() {
        if (this.level == null) {
            return;
        }
        AABB scanBox = new AABB(
                this.worldPosition.getX() - 0.2, this.worldPosition.getY(), this.worldPosition.getZ() - 0.2,
                this.worldPosition.getX() + 1.2, this.worldPosition.getY() + 1.8, this.worldPosition.getZ() + 1.2
        );
        List<CyborgEntity> cyborgs = this.level.getEntitiesOfClass(CyborgEntity.class, scanBox);
        for (CyborgEntity cyborg : cyborgs) {
            if (cyborg.isAlive() && (cyborg.getRoutine() == CyborgRoutine.RETURN_TO_DOCK || cyborg.getEnergy() < cyborg.getMaxEnergy() || cyborg.getIntegrity() < 100 || !isCyborgInventoryEmpty(cyborg))) {
                dockCyborg(cyborg);
                break;
            }
        }
    }

    private void processDockedCyborg() {
        if (this.level == null || !(this.level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (cachedCyborg == null || !cachedCyborg.isAlive()) {
            Entity found = serverLevel.getEntity(dockedCyborgUUID);
            if (found instanceof CyborgEntity cyborg && cyborg.isAlive()) {
                cachedCyborg = cyborg;
            } else {
                undockCyborg();
                return;
            }
        }

        cachedCyborg.teleportTo(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.25, this.worldPosition.getZ() + 0.5);
        cachedCyborg.setDeltaMovement(Vec3.ZERO);
        cachedCyborg.getNavigation().stop();

        if (dockEnergy > 0 && cachedCyborg.getEnergy() < cachedCyborg.getMaxEnergy()) {
            int transfer = Math.min(500, Math.min(dockEnergy, cachedCyborg.getMaxEnergy() - cachedCyborg.getEnergy()));
            cachedCyborg.chargeEnergy(transfer);
            dockEnergy -= transfer;
        }

        if (tickCounter % 2 == 0 && cachedCyborg.getIntegrity() < 100) {
            cachedCyborg.repairIntegrity(1);
        }

        if (tickCounter % 4 == 0) {
            unloadCyborgInventory(cachedCyborg);
        }

        if (cachedCyborg.getEnergy() >= cachedCyborg.getMaxEnergy() && cachedCyborg.getIntegrity() >= 100 && isCyborgInventoryEmpty(cachedCyborg)) {
            cachedCyborg.setRoutine(CyborgRoutine.AUTONOMOUS_WORK);
            undockCyborg();
        }
    }

    public void dockCyborg(CyborgEntity cyborg) {
        this.dockedCyborgUUID = cyborg.getUUID();
        this.cachedCyborg = cyborg;
        cyborg.setVisorState(3);
        cyborg.getNavigation().stop();
        if (this.level != null) {
            BlockState state = this.getBlockState();
            if (state.hasProperty(CyborgDockingStationBlock.OCCUPIED) && state.hasProperty(CyborgDockingStationBlock.CHARGING)) {
                this.level.setBlock(this.worldPosition, state.setValue(CyborgDockingStationBlock.OCCUPIED, true).setValue(CyborgDockingStationBlock.CHARGING, true), 3);
            }
            setChanged();
        }
    }

    public void undockCyborg() {
        if (this.cachedCyborg != null) {
            this.cachedCyborg.setVisorState(0);
        }
        this.dockedCyborgUUID = null;
        this.cachedCyborg = null;
        if (this.level != null) {
            BlockState state = this.getBlockState();
            if (state.hasProperty(CyborgDockingStationBlock.OCCUPIED) && state.hasProperty(CyborgDockingStationBlock.CHARGING)) {
                this.level.setBlock(this.worldPosition, state.setValue(CyborgDockingStationBlock.OCCUPIED, false).setValue(CyborgDockingStationBlock.CHARGING, false), 3);
            }
            setChanged();
        }
    }

    private void unloadCyborgInventory(CyborgEntity cyborg) {
        if (this.level == null) {
            return;
        }
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP) {
                continue;
            }
            BlockPos targetPos = this.worldPosition.relative(dir);
            BlockEntity be = this.level.getBlockEntity(targetPos);
            if (be instanceof Container neighbor) {
                for (int i = 0; i < cyborg.getInventory().getContainerSize(); i++) {
                    ItemStack stack = cyborg.getInventory().getItem(i);
                    if (!stack.isEmpty()) {
                        ItemStack remainder = insertItemIntoContainer(neighbor, stack);
                        cyborg.getInventory().setItem(i, remainder);
                        if (remainder.isEmpty()) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private ItemStack insertItemIntoContainer(Container dest, ItemStack stack) {
        ItemStack toInsert = stack.copy();
        for (int slot = 0; slot < dest.getContainerSize(); slot++) {
            if (!dest.canPlaceItem(slot, toInsert)) {
                continue;
            }
            ItemStack inSlot = dest.getItem(slot);
            if (inSlot.isEmpty()) {
                dest.setItem(slot, toInsert);
                dest.setChanged();
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameComponents(inSlot, toInsert)) {
                int space = inSlot.getMaxStackSize() - inSlot.getCount();
                if (space > 0) {
                    int add = Math.min(space, toInsert.getCount());
                    inSlot.grow(add);
                    toInsert.shrink(add);
                    dest.setChanged();
                    if (toInsert.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return toInsert;
    }

    private boolean isCyborgInventoryEmpty(CyborgEntity cyborg) {
        for (int i = 0; i < cyborg.getInventory().getContainerSize(); i++) {
            if (!cyborg.getInventory().getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    public int getDockEnergy() {
        return this.dockEnergy;
    }

    public boolean isOccupied() {
        return this.dockedCyborgUUID != null;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.cyborg_docking_station");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new CyborgDockingStationMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("dockEnergy", this.dockEnergy);
        if (this.dockedCyborgUUID != null) {
            output.putString("dockedUUID", this.dockedCyborgUUID.toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.dockEnergy = input.getIntOr("dockEnergy", MAX_DOCK_ENERGY);
        String uuidStr = input.getStringOr("dockedUUID", "");
        if (!uuidStr.isEmpty()) {
            try {
                this.dockedCyborgUUID = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException ignored) {
                this.dockedCyborgUUID = null;
            }
        }
    }
}
