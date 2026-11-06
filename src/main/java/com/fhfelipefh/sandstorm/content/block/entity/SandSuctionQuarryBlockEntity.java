package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SandSuctionQuarryBlock;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.gui.SandStormMenus;
import com.fhfelipefh.sandstorm.content.gui.SandSuctionQuarryMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public class SandSuctionQuarryBlockEntity extends BaseMachineBlockEntity {
    public static final TagKey<Block> SUCTION_EXCAVATABLE = TagKey.create(Registries.BLOCK, SandStormMod.id("suction_excavatable"));
    public static final int ENERGY_CAPACITY = 50000;
    public static final int ENERGY_PER_BLOCK = 1000;
    public static final int ENERGY_IDLE_DRAIN = 2;
    public static final int CYCLE_TIME = 20;

    private int radius = 16;
    private boolean paused = false;
    private int status = 0;
    private int scanX = 0;
    private int scanY = 319;
    private int scanZ = 0;
    private boolean initializedScan = false;
    private int blocksExcavatedTotal = 0;
    private int ejectCooldown = 0;

    private final ContainerData quarryData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy & 0xFFFF;
                case 1 -> (energy >> 16) & 0xFFFF;
                case 2 -> maxEnergy & 0xFFFF;
                case 3 -> (maxEnergy >> 16) & 0xFFFF;
                case 4 -> radius;
                case 5 -> progress;
                case 6 -> paused ? 1 : 0;
                case 7 -> status;
                case 8 -> scanY + 100;
                case 9 -> blocksExcavatedTotal & 0xFFFF;
                case 10 -> (blocksExcavatedTotal >> 16) & 0xFFFF;
                case 11 -> wptConnected ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energy = (energy & ~0xFFFF) | (value & 0xFFFF);
                case 1 -> energy = (energy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 2 -> maxEnergy = (maxEnergy & ~0xFFFF) | (value & 0xFFFF);
                case 3 -> maxEnergy = (maxEnergy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 4 -> radius = Math.max(1, Math.min(64, value));
                case 5 -> progress = value;
                case 6 -> paused = (value == 1);
                case 7 -> status = value;
                case 8 -> scanY = value - 100;
                case 9 -> blocksExcavatedTotal = (blocksExcavatedTotal & ~0xFFFF) | (value & 0xFFFF);
                case 10 -> blocksExcavatedTotal = (blocksExcavatedTotal & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 11 -> wptConnected = (value == 1);
            }
        }

        @Override
        public int getCount() {
            return 12;
        }
    };

    public SandSuctionQuarryBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.SAND_SUCTION_QUARRY_BE, pos, state);
    }

    public SandSuctionQuarryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 9, CYCLE_TIME);
        this.maxEnergy = ENERGY_CAPACITY;
        this.energyCostPerTick = 40;
    }

    @Override
    protected int getBatterySlotIndex() {
        return -1;
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.SAND_BREAK;
    }

    @Override
    protected boolean canProcess() {
        return false;
    }

    @Override
    protected void processRecipe() {
    }

    public static boolean isExcavatable(BlockState state) {
        if (state.isAir()) {
            return false;
        }
        try {
            if (state.is(SUCTION_EXCAVATABLE)) {
                return true;
            }
            if (state.is(BlockTags.SAND)) {
                return true;
            }
        } catch (IllegalStateException ignored) {
        }
        if (state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.SUSPICIOUS_SAND)) {
            return true;
        }
        if (state.is(Blocks.GRAVEL) || state.is(Blocks.SUSPICIOUS_GRAVEL)) {
            return true;
        }
        if (state.getBlock() instanceof FallingBlock) {
            return true;
        }
        return state.is(SandStormBlocks.SALINIZED_SAND)
                || state.is(SandStormBlocks.ELECTRIFIED_SAND)
                || state.is(SandStormBlocks.NANITE_QUICKSAND);
    }

    public void resetScan() {
        int minX = worldPosition.getX() - radius;
        int minZ = worldPosition.getZ() - radius;
        int topY = (level != null) ? level.getMaxY() - 1 : 319;
        this.scanY = topY;
        this.scanX = minX;
        this.scanZ = minZ;
        this.status = paused ? 0 : 1;
        this.progress = 0;
        this.initializedScan = true;
    }

    private void advanceScan() {
        int minX = worldPosition.getX() - radius;
        int maxX = worldPosition.getX() + radius;
        int minZ = worldPosition.getZ() - radius;
        int maxZ = worldPosition.getZ() + radius;

        scanX++;
        if (scanX > maxX) {
            scanX = minX;
            scanZ++;
            if (scanZ > maxZ) {
                scanZ = minZ;
                scanY--;
            }
        }
    }

    public boolean handleMenuButton(int id) {
        switch (id) {
            case SandSuctionQuarryMenu.BUTTON_DEC_1 -> {
                this.radius = Math.max(1, this.radius - 1);
                resetScan();
                setChanged();
                return true;
            }
            case SandSuctionQuarryMenu.BUTTON_INC_1 -> {
                this.radius = Math.min(64, this.radius + 1);
                resetScan();
                setChanged();
                return true;
            }
            case SandSuctionQuarryMenu.BUTTON_DEC_8 -> {
                this.radius = Math.max(1, this.radius - 8);
                resetScan();
                setChanged();
                return true;
            }
            case SandSuctionQuarryMenu.BUTTON_INC_8 -> {
                this.radius = Math.min(64, this.radius + 8);
                resetScan();
                setChanged();
                return true;
            }
            case SandSuctionQuarryMenu.BUTTON_TOGGLE_PAUSE -> {
                this.paused = !this.paused;
                if (!this.paused && this.status == 0) {
                    this.status = 1;
                }
                setChanged();
                return true;
            }
            case SandSuctionQuarryMenu.BUTTON_RESET_SCAN -> {
                resetScan();
                setChanged();
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private boolean isShowcaseWorld(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            String levelName = serverLevel.getServer().getWorldData().getLevelName();
            return levelName != null && levelName.toLowerCase().contains("showcase");
        }
        return false;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (isShowcaseWorld(level)) {
            this.paused = true;
            this.status = 0;
            updateActiveState(level, pos, state, false);
            return;
        }

        if (!initializedScan) {
            resetScan();
        }

        if (cachedWptCharge < 0.0f || (level.getGameTime() + pos.hashCode()) % 20 == 0) {
            cachedWptCharge = WirelessSolarReceiverManager.getWptChargeAt(level, pos);
            wptConnected = (cachedWptCharge > 0.0f);
        }

        if (cachedWptCharge > 0.0f && energy < maxEnergy) {
            int toAdd = Math.round(cachedWptCharge * 15.0f);
            energy = Math.min(maxEnergy, energy + Math.max(1, toAdd));
        }

        if (energy < maxEnergy) {
            for (Direction dir : Direction.values()) {
                BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
                if (neighbor instanceof SolidStateAccumulatorBlockEntity acc && acc.getStoredEnergy() > 0) {
                    int needed = maxEnergy - energy;
                    int drain = Math.min(needed, Math.min(500, acc.getStoredEnergy()));
                    acc.setStoredEnergy(acc.getStoredEnergy() - drain);
                    energy += drain;
                    if (energy >= maxEnergy) {
                        break;
                    }
                }
            }
        }

        ejectCooldown++;
        if (ejectCooldown >= 5) {
            ejectCooldown = 0;
            autoEject(level, pos);
        }

        if (paused) {
            this.status = 0;
            updateActiveState(level, pos, state, false);
            return;
        }

        if (energy < ENERGY_PER_BLOCK) {
            this.status = 0;
            updateActiveState(level, pos, state, false);
            return;
        }

        boolean hasSpace = hasOutputSpace() || canEjectAny(level, pos);
        if (!hasSpace) {
            this.status = 2;
            updateActiveState(level, pos, state, false);
            return;
        }

        int minY = level.getMinY();
        if (scanY < minY) {
            this.status = 3;
            updateActiveState(level, pos, state, false);
            return;
        }

        int checks = 0;
        BlockPos foundPos = null;
        BlockState foundState = null;

        while (scanY >= minY && checks < 128) {
            checks++;
            BlockPos checkPos = new BlockPos(scanX, scanY, scanZ);
            if (checkPos.equals(pos)) {
                advanceScan();
                continue;
            }
            if (!level.isLoaded(checkPos)) {
                advanceScan();
                continue;
            }

            BlockState stateAt = level.getBlockState(checkPos);
            if (isExcavatable(stateAt)) {
                foundPos = checkPos;
                foundState = stateAt;
                break;
            }
            advanceScan();
        }

        if (foundPos != null && level instanceof ServerLevel serverLevel) {
            this.status = 1;
            updateActiveState(level, pos, state, true);

            progress++;
            if (progress % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        foundPos.getX() + 0.5, foundPos.getY() + 0.8, foundPos.getZ() + 0.5,
                        2, 0.1, 0.1, 0.1, 0.02);
            }

            if (progress >= maxProgress) {
                extractBlock(serverLevel, foundPos, foundState);
                advanceScan();
                progress = 0;
                setChanged();
            }
        } else if (scanY < minY) {
            this.status = 3;
            updateActiveState(level, pos, state, false);
        } else {
            this.status = 1;
        }
    }

    private void extractBlock(ServerLevel serverLevel, BlockPos targetPos, BlockState targetState) {
        List<ItemStack> drops = Block.getDrops(targetState, serverLevel, targetPos, null);
        if (drops.isEmpty()) {
            ItemStack fallback = new ItemStack(targetState.getBlock().asItem());
            if (!fallback.isEmpty()) {
                drops = List.of(fallback);
            }
        }

        routeExtractedDrops(serverLevel, drops);

        serverLevel.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
        energy = Math.max(0, energy - ENERGY_PER_BLOCK);
        blocksExcavatedTotal++;

        serverLevel.playSound(null, targetPos, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 0.8f, 1.1f);
        serverLevel.playSound(null, worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 0.4f, 1.8f);

        serverLevel.sendParticles(ParticleTypes.CLOUD, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, 6, 0.2, 0.2, 0.2, 0.05);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 6, 0.2, 0.1, 0.2, 0.05);
    }

    private void routeExtractedDrops(ServerLevel level, List<ItemStack> drops) {
        for (ItemStack drop : drops) {
            ItemStack stack = drop.copy();
            for (Direction dir : Direction.values()) {
                Storage<ItemVariant> targetStorage = ItemStorage.SIDED.find(level, worldPosition.relative(dir), dir.getOpposite());
                if (targetStorage != null && !stack.isEmpty()) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        long inserted = targetStorage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
                        if (inserted > 0) {
                            transaction.commit();
                            stack.shrink((int) inserted);
                        }
                    }
                }
            }
            if (!stack.isEmpty()) {
                insertIntoBuffer(stack);
            }
        }
    }

    private void insertIntoBuffer(ItemStack stack) {
        for (int i = 0; i < 9; i++) {
            ItemStack slotStack = items.get(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, stack)) {
                int space = slotStack.getMaxStackSize() - slotStack.getCount();
                int toAdd = Math.min(space, stack.getCount());
                if (toAdd > 0) {
                    slotStack.grow(toAdd);
                    stack.shrink(toAdd);
                    if (stack.isEmpty()) {
                        setChanged();
                        return;
                    }
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            ItemStack slotStack = items.get(i);
            if (slotStack.isEmpty()) {
                items.set(i, stack.copy());
                stack.setCount(0);
                setChanged();
                return;
            }
        }
    }

    public void autoEject(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            Storage<ItemVariant> targetStorage = ItemStorage.SIDED.find(level, pos.relative(dir), dir.getOpposite());
            if (targetStorage != null) {
                Storage<ItemVariant> sourceStorage = ContainerStorage.of(this, dir);
                StorageUtil.move(sourceStorage, targetStorage, filter -> true, 64, null);
            }
        }
    }

    public boolean hasOutputSpace() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    public boolean canEjectAny(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            Storage<ItemVariant> targetStorage = ItemStorage.SIDED.find(level, pos.relative(dir), dir.getOpposite());
            if (targetStorage != null && targetStorage.supportsInsertion()) {
                return true;
            }
        }
        return false;
    }

    private void updateActiveState(Level level, BlockPos pos, BlockState state, boolean active) {
        if (state.hasProperty(SandSuctionQuarryBlock.ACTIVE) && state.getValue(SandSuctionQuarryBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(SandSuctionQuarryBlock.ACTIVE, active), 3);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.sand_suction_quarry");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new SandSuctionQuarryMenu(SandStormMenus.SAND_SUCTION_QUARRY_MENU, syncId, playerInventory, this, quarryData, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Radius", this.radius);
        output.putBoolean("Paused", this.paused);
        output.putInt("ScanX", this.scanX);
        output.putInt("ScanY", this.scanY);
        output.putInt("ScanZ", this.scanZ);
        output.putBoolean("InitializedScan", this.initializedScan);
        output.putInt("BlocksExcavatedTotal", this.blocksExcavatedTotal);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.radius = Math.max(1, Math.min(64, input.getIntOr("Radius", 16)));
        this.paused = input.getBooleanOr("Paused", false);
        this.scanX = input.getIntOr("ScanX", 0);
        this.scanY = input.getIntOr("ScanY", 319);
        this.scanZ = input.getIntOr("ScanZ", 0);
        this.initializedScan = input.getBooleanOr("InitializedScan", false);
        this.blocksExcavatedTotal = input.getIntOr("BlocksExcavatedTotal", 0);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("Radius", radius);
        tag.putBoolean("Paused", paused);
        tag.putInt("Status", status);
        tag.putInt("ScanY", scanY);
        tag.putInt("Total", blocksExcavatedTotal);
        return tag;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = Math.max(1, Math.min(64, radius));
        resetScan();
        setChanged();
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        setChanged();
    }

    public int getStatus() {
        return status;
    }

    public int getScanY() {
        return scanY;
    }

    public int getBlocksExcavatedTotal() {
        return blocksExcavatedTotal;
    }
}
