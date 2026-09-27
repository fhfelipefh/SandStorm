package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.QuantumSleeperPodBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.clone.CloneNetworkSavedData;
import com.fhfelipefh.sandstorm.content.gui.QuantumSleeperMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.survival.SpawnSafety;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class QuantumSleeperPodBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int MAX_ENERGY = 100000;
    public static final int TOTAL_SLOTS = 42;
    public static final int NUTRIENT_SLOT = 41;

    private final NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
    private int storedEnergy = MAX_ENERGY;
    private int bioNutrients = 100;
    private boolean hasClone = false;
    private UUID ownerUuid = null;
    private String customPodName = "Sleeper Pod";
    private int tickCounter = 0;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEnergy;
                case 1 -> MAX_ENERGY;
                case 2 -> bioNutrients;
                case 3 -> hasClone ? 1 : 0;
                case 4 -> getConnectedPodsCount();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> storedEnergy = value;
                case 2 -> bioNutrients = value;
                case 3 -> hasClone = value == 1;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public QuantumSleeperPodBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.QUANTUM_SLEEPER_POD_BE, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, QuantumSleeperPodBlockEntity be) {
        be.tickCounter++;

        if (be.storedEnergy < MAX_ENERGY) {
            be.storedEnergy = Math.min(MAX_ENERGY, be.storedEnergy + 50);
        }

        if (be.bioNutrients < 100) {
            ItemStack nutrientStack = be.items.get(NUTRIENT_SLOT);
            if (!nutrientStack.isEmpty()) {
                if (nutrientStack.is(SandStormItems.CHITOSAN_EXTRACT)
                        || nutrientStack.is(SandStormItems.TREHALOSE_SUGAR)
                        || nutrientStack.is(SandStormItems.OSMOLYTE_GLYCEROL)) {
                    be.bioNutrients = Math.min(100, be.bioNutrients + 25);
                    nutrientStack.shrink(1);
                    be.setChanged();
                } else if (nutrientStack.is(SandStormItems.POTABLE_WATER_BOTTLE)) {
                    be.bioNutrients = Math.min(100, be.bioNutrients + 15);
                    be.items.set(NUTRIENT_SLOT, new ItemStack(Items.GLASS_BOTTLE));
                    be.setChanged();
                }
            }
        }

        boolean active = be.storedEnergy >= 500;
        boolean occupied = be.hasClone;
        if (state.getValue(QuantumSleeperPodBlock.ACTIVE) != active || state.getValue(QuantumSleeperPodBlock.OCCUPIED) != occupied) {
            level.setBlock(pos, state.setValue(QuantumSleeperPodBlock.ACTIVE, active).setValue(QuantumSleeperPodBlock.OCCUPIED, occupied), 3);
        }

        if (be.tickCounter % 100 == 0 && be.ownerUuid != null && level.getServer() != null) {
            CloneNetworkSavedData networkData = CloneNetworkSavedData.get(level.getServer());
            networkData.registerPod(be.ownerUuid, pos, level.dimension().identifier().toString(), be.customPodName, be.hasClone, level.getGameTime());
        }
    }

    public boolean triggerCloneGestation(Player player) {
        if (this.hasClone) {
            return false;
        }
        if (this.storedEnergy < 5000 || this.bioNutrients < 20) {
            return false;
        }
        this.storedEnergy -= 5000;
        this.bioNutrients -= 20;
        this.hasClone = true;
        if (this.ownerUuid == null) {
            this.ownerUuid = player.getUUID();
        }
        if (this.level != null && !this.level.isClientSide()) {
            this.setChanged();
            if (this.level.getServer() != null) {
                CloneNetworkSavedData networkData = CloneNetworkSavedData.get(this.level.getServer());
                networkData.registerPod(this.ownerUuid, this.worldPosition, this.level.dimension().identifier().toString(), this.customPodName, true, this.level.getGameTime());
            }
            this.level.playSound(null, this.worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.2f);
        }
        return true;
    }

    public boolean executeConsciousnessTransfer(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || this.level == null || this.level.isClientSide()) {
            return false;
        }
        MinecraftServer server = serverPlayer.level().getServer();
        if (server == null) {
            return false;
        }
        UUID playerUuid = serverPlayer.getUUID();
        CloneNetworkSavedData networkData = CloneNetworkSavedData.get(server);
        String dim = this.level.dimension().identifier().toString();
        Optional<CloneNetworkSavedData.ClonePodRecord> targetRecordOpt = networkData.findTargetPodForTransfer(playerUuid, this.worldPosition, dim);
        if (targetRecordOpt.isEmpty()) {
            return false;
        }
        CloneNetworkSavedData.ClonePodRecord targetRecord = targetRecordOpt.get();
        BlockPos targetPos = targetRecord.pos();
        ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
        BlockEntity targetBeRaw = serverLevel.getBlockEntity(targetPos);
        if (!(targetBeRaw instanceof QuantumSleeperPodBlockEntity targetBe) || !targetBe.hasClone()) {
            return false;
        }

        for (int i = 0; i < 36; i++) {
            this.items.set(i, serverPlayer.getInventory().getItem(i).copy());
        }
        this.items.set(36, serverPlayer.getItemBySlot(EquipmentSlot.FEET).copy());
        this.items.set(37, serverPlayer.getItemBySlot(EquipmentSlot.LEGS).copy());
        this.items.set(38, serverPlayer.getItemBySlot(EquipmentSlot.CHEST).copy());
        this.items.set(39, serverPlayer.getItemBySlot(EquipmentSlot.HEAD).copy());
        this.items.set(40, serverPlayer.getItemBySlot(EquipmentSlot.OFFHAND).copy());
        this.hasClone = true;
        this.setChanged();

        serverPlayer.getInventory().clearContent();

        BlockPos arrivalPos = targetPos.relative(serverLevel.getBlockState(targetPos).getValue(QuantumSleeperPodBlock.FACING));
        if (!SpawnSafety.isSafePosition(serverLevel, arrivalPos)) {
            arrivalPos = targetPos.above();
        }
        SpawnSafety.teleportSafely(serverPlayer, arrivalPos);

        for (int i = 0; i < 36; i++) {
            serverPlayer.getInventory().setItem(i, targetBe.items.get(i).copy());
            targetBe.items.set(i, ItemStack.EMPTY);
        }
        serverPlayer.setItemSlot(EquipmentSlot.FEET, targetBe.items.get(36).copy());
        serverPlayer.setItemSlot(EquipmentSlot.LEGS, targetBe.items.get(37).copy());
        serverPlayer.setItemSlot(EquipmentSlot.CHEST, targetBe.items.get(38).copy());
        serverPlayer.setItemSlot(EquipmentSlot.HEAD, targetBe.items.get(39).copy());
        serverPlayer.setItemSlot(EquipmentSlot.OFFHAND, targetBe.items.get(40).copy());
        for (int i = 36; i <= 40; i++) {
            targetBe.items.set(i, ItemStack.EMPTY);
        }

        targetBe.hasClone = false;
        targetBe.setChanged();

        networkData.updateCloneStatus(playerUuid, this.worldPosition, dim, true, serverLevel.getGameTime());
        networkData.updateCloneStatus(playerUuid, targetPos, dim, false, serverLevel.getGameTime());

        serverPlayer.containerMenu.broadcastChanges();
        serverPlayer.inventoryMenu.broadcastChanges();
        serverPlayer.closeContainer();

        serverLevel.playSound(null, targetPos, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.2f);
        serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.consciousness_transferred"), true);
        return true;
    }

    public void deployCloneToPlayer(ServerPlayer player) {
        player.getInventory().clearContent();
        for (int i = 0; i < 36; i++) {
            player.getInventory().setItem(i, this.items.get(i).copy());
            this.items.set(i, ItemStack.EMPTY);
        }
        player.setItemSlot(EquipmentSlot.FEET, this.items.get(36).copy());
        player.setItemSlot(EquipmentSlot.LEGS, this.items.get(37).copy());
        player.setItemSlot(EquipmentSlot.CHEST, this.items.get(38).copy());
        player.setItemSlot(EquipmentSlot.HEAD, this.items.get(39).copy());
        player.setItemSlot(EquipmentSlot.OFFHAND, this.items.get(40).copy());
        for (int i = 36; i <= 40; i++) {
            this.items.set(i, ItemStack.EMPTY);
        }
        this.hasClone = false;
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            if (state.hasProperty(QuantumSleeperPodBlock.OCCUPIED)) {
                this.level.setBlock(this.worldPosition, state.setValue(QuantumSleeperPodBlock.OCCUPIED, false), 3);
            }
            if (this.level.getServer() != null && this.ownerUuid != null) {
                CloneNetworkSavedData networkData = CloneNetworkSavedData.get(this.level.getServer());
                networkData.updateCloneStatus(this.ownerUuid, this.worldPosition, this.level.dimension().identifier().toString(), false, this.level.getGameTime());
            }
        }
        player.containerMenu.broadcastChanges();
        player.inventoryMenu.broadcastChanges();
    }

    public int getConnectedPodsCount() {
        if (this.level == null || this.level.getServer() == null || this.ownerUuid == null) {
            return 1;
        }
        CloneNetworkSavedData networkData = CloneNetworkSavedData.get(this.level.getServer());
        List<CloneNetworkSavedData.ClonePodRecord> list = networkData.getPodsForPlayer(this.ownerUuid);
        return Math.max(1, list.size());
    }

    public boolean hasClone() {
        return this.hasClone;
    }

    public void setHasClone(boolean hasClone) {
        this.hasClone = hasClone;
        this.setChanged();
    }

    public int getStoredEnergy() {
        return this.storedEnergy;
    }

    public void setStoredEnergy(int storedEnergy) {
        this.storedEnergy = storedEnergy;
        this.setChanged();
    }

    public int getBioNutrients() {
        return this.bioNutrients;
    }

    public void setBioNutrients(int bioNutrients) {
        this.bioNutrients = bioNutrients;
        this.setChanged();
    }

    public UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
        this.setChanged();
    }

    public String getCustomPodName() {
        return this.customPodName;
    }

    public void setCustomPodName(String customPodName) {
        this.customPodName = customPodName;
        this.setChanged();
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.quantum_sleeper_pod");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new QuantumSleeperMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return TOTAL_SLOTS;
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
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null) {
            return false;
        }
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        Collections.fill(this.items, ItemStack.EMPTY);
        this.setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == NUTRIENT_SLOT) {
            return stack.is(SandStormItems.CHITOSAN_EXTRACT)
                    || stack.is(SandStormItems.TREHALOSE_SUGAR)
                    || stack.is(SandStormItems.OSMOLYTE_GLYCEROL)
                    || stack.is(SandStormItems.POTABLE_WATER_BOTTLE);
        }
        return true;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{NUTRIENT_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == NUTRIENT_SLOT && canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == NUTRIENT_SLOT && stack.is(Items.GLASS_BOTTLE);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putInt("bioNutrients", this.bioNutrients);
        output.putBoolean("hasClone", this.hasClone);
        output.putString("customPodName", this.customPodName);
        if (this.ownerUuid != null) {
            output.putString("ownerUuid", this.ownerUuid.toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        Collections.fill(this.items, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.storedEnergy = input.getIntOr("storedEnergy", MAX_ENERGY);
        this.bioNutrients = input.getIntOr("bioNutrients", 100);
        this.hasClone = input.getBooleanOr("hasClone", false);
        this.customPodName = input.getStringOr("customPodName", "Sleeper Pod");
        String uuidStr = input.getStringOr("ownerUuid", "");
        if (!uuidStr.isEmpty()) {
            try {
                this.ownerUuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException ignored) {
                this.ownerUuid = null;
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }
}
