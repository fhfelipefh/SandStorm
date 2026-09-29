package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.ElectricFencePylonBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SolidStateAccumulatorManager;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.gui.ElectricFencePylonMenu;
import com.fhfelipefh.sandstorm.content.item.SpaceSuitItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ElectricFencePylonBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int MAX_ENERGY = 20000;
    public static final int MAX_RANGE = 16;
    public static final double MAX_RANGE_SQ = 256.0;

    private int energy = 0;
    private int mode = 0;
    private boolean redstonePowered = false;
    private boolean armed = false;
    private boolean connected = false;
    private int checkCooldown = 0;
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private final List<BlockPos> connectedPylons = new ArrayList<>();

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy & 0xFFFF;
                case 1 -> (energy >> 16) & 0xFFFF;
                case 2 -> mode;
                case 3 -> isArmed() ? 1 : 0;
                case 4 -> isConnected() ? 1 : 0;
                case 5 -> connectedPylons.size();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energy = (energy & 0xFFFF0000) | (value & 0xFFFF);
                case 1 -> energy = (energy & 0x0000FFFF) | ((value & 0xFFFF) << 16);
                case 2 -> mode = value;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public ElectricFencePylonBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.ELECTRIC_FENCE_PYLON_BE, pos, state);
    }

    public ElectricFencePylonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getEnergy() {
        return this.energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(MAX_ENERGY, Math.max(0, energy));
        setChanged();
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = Math.floorMod(mode, 3);
        setChanged();
    }

    public void cycleMode() {
        this.mode = (this.mode + 1) % 3;
        setChanged();
    }

    public boolean isArmed() {
        if (this.mode == 2) {
            return false;
        }
        if (this.energy <= 0) {
            return false;
        }
        if (this.mode == 1) {
            return true;
        }
        return this.redstonePowered;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public List<BlockPos> getConnectedPylons() {
        return Collections.unmodifiableList(this.connectedPylons);
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ElectricFencePylonBlockEntity be) {
        long gameTime = level.getGameTime();

        if ((gameTime + pos.hashCode()) % 20 == 0) {
            float wptSolar = WirelessSolarReceiverManager.getWptChargeAt(level, pos);
            long wptAccumulator = SolidStateAccumulatorManager.getWptChargeAt(level, pos);
            long totalIncome = Math.round(wptSolar * 15.0f) + wptAccumulator;
            if (totalIncome > 0 && be.energy < MAX_ENERGY) {
                be.energy = Math.min(MAX_ENERGY, be.energy + (int) Math.min(MAX_ENERGY - be.energy, totalIncome));
                be.setChanged();
            }
        }

        ItemStack batteryStack = be.items.get(0);
        int fuelVal = BaseMachineBlockEntity.getFuelEnergy(batteryStack);
        if (fuelVal > 0 && be.energy + fuelVal <= MAX_ENERGY) {
            be.energy += fuelVal;
            batteryStack.shrink(1);
            be.setChanged();
        }

        be.redstonePowered = level.hasNeighborSignal(pos);
        boolean armedNow = be.isArmed();
        be.armed = armedNow;

        if (be.checkCooldown > 0) {
            be.checkCooldown--;
        } else {
            be.checkCooldown = 10;
            be.scanAndConnectNeighbors(level, pos, armedNow);
        }

        if (armedNow && be.connected) {
            int drain = 1 + (be.connectedPylons.size() * 2);
            be.energy = Math.max(0, be.energy - drain);
            be.setChanged();

            if (be.energy <= 0) {
                be.connectedPylons.clear();
                be.connected = false;
                be.updateBlockState(level, pos, state, false, false);
                return;
            }

            be.processActiveBeams(level, pos);
        }

        boolean stateLit = state.getValue(ElectricFencePylonBlock.LIT);
        boolean stateConnected = state.getValue(ElectricFencePylonBlock.CONNECTED);
        if (stateLit != be.armed || stateConnected != be.connected) {
            be.updateBlockState(level, pos, state, be.armed, be.connected);
        }
    }

    private void scanAndConnectNeighbors(Level level, BlockPos pos, boolean armedNow) {
        this.connectedPylons.clear();

        if (!armedNow) {
            this.connected = false;
            return;
        }

        for (int dx = -MAX_RANGE; dx <= MAX_RANGE; dx++) {
            for (int dz = -MAX_RANGE; dz <= MAX_RANGE; dz++) {
                double distSq = (double) dx * dx + (double) dz * dz;
                if (distSq <= 0.0 || distSq > MAX_RANGE_SQ) {
                    continue;
                }
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos neighborPos = pos.offset(dx, dy, dz);
                    if (!level.isLoaded(neighborPos)) {
                        continue;
                    }
                    BlockEntity targetBe = level.getBlockEntity(neighborPos);
                    if (targetBe instanceof ElectricFencePylonBlockEntity neighborPylon) {
                        if (neighborPylon.isArmed() && hasClearLineOfSight(level, pos, neighborPos)) {
                            this.connectedPylons.add(neighborPos.immutable());
                        }
                    }
                }
            }
        }

        this.connected = !this.connectedPylons.isEmpty();
    }

    private void processActiveBeams(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 p1 = Vec3.atCenterOf(pos);

        for (BlockPos neighborPos : this.connectedPylons) {
            if (pos.asLong() >= neighborPos.asLong()) {
                continue;
            }

            Vec3 p2 = Vec3.atCenterOf(neighborPos);
            AABB beamBox = new AABB(p1, p2).inflate(0.5);
            List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, beamBox);

            for (LivingEntity living : targets) {
                Vec3 closest = getClosestPointOnSegment(living.position(), p1, p2);
                if (living.position().distanceToSqr(closest) <= 0.8) {
                    boolean fullyInsulated = living.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof SpaceSuitItem
                            && living.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof SpaceSuitItem
                            && living.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof SpaceSuitItem
                            && living.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof SpaceSuitItem;

                    float damage = fullyInsulated ? 2.0f : 8.0f;
                    boolean hurt = living.hurtServer(serverLevel, serverLevel.damageSources().lightningBolt(), damage);

                    if (hurt) {
                        living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 2));

                        Vec3 push = living.position().subtract(closest);
                        if (push.lengthSqr() < 1e-4) {
                            push = new Vec3(0.0, 0.25, 0.0);
                        } else {
                            push = push.normalize().scale(0.4).add(0.0, 0.15, 0.0);
                        }
                        living.push(push.x, push.y, push.z);

                        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, living.getX(), living.getY() + living.getBbHeight() * 0.5, living.getZ(), 10, 0.25, 0.25, 0.25, 0.15);
                        serverLevel.playSound(null, living.blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 0.6f, 1.8f);
                    }
                }
            }

            if (serverLevel.getGameTime() % 4 == 0) {
                spawnBeamParticles(serverLevel, p1, p2);
            }
        }
    }

    private static void spawnBeamParticles(ServerLevel level, Vec3 p1, Vec3 p2) {
        Vec3 diff = p2.subtract(p1);
        double length = diff.length();
        int particleCount = Math.max(2, (int) Math.round(length * 2.0));

        for (int i = 0; i <= particleCount; i++) {
            double frac = (double) i / (double) particleCount;
            double offsetX = (level.getRandom().nextDouble() - 0.5) * 0.2;
            double offsetY = (level.getRandom().nextDouble() - 0.5) * 0.2;
            double offsetZ = (level.getRandom().nextDouble() - 0.5) * 0.2;

            double px = p1.x + diff.x * frac + offsetX;
            double py = p1.y + diff.y * frac + offsetY;
            double pz = p1.z + diff.z * frac + offsetZ;

            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public static boolean hasClearLineOfSight(Level level, BlockPos pos1, BlockPos pos2) {
        int dx = pos2.getX() - pos1.getX();
        int dy = pos2.getY() - pos1.getY();
        int dz = pos2.getZ() - pos1.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist <= 1.0) {
            return true;
        }

        int steps = (int) Math.ceil(dist * 2.0);
        for (int i = 1; i < steps; i++) {
            double frac = (double) i / (double) steps;
            int x = (int) Math.floor(pos1.getX() + 0.5 + dx * frac);
            int y = (int) Math.floor(pos1.getY() + 0.5 + dy * frac);
            int z = (int) Math.floor(pos1.getZ() + 0.5 + dz * frac);
            BlockPos stepPos = new BlockPos(x, y, z);

            if (!stepPos.equals(pos1) && !stepPos.equals(pos2)) {
                BlockState state = level.getBlockState(stepPos);
                if (state.isSolidRender()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Vec3 getClosestPointOnSegment(Vec3 point, Vec3 segStart, Vec3 segEnd) {
        Vec3 seg = segEnd.subtract(segStart);
        double lenSq = seg.lengthSqr();
        if (lenSq < 1e-6) {
            return segStart;
        }

        double t = point.subtract(segStart).dot(seg) / lenSq;
        t = Math.max(0.0, Math.min(1.0, t));
        return segStart.add(seg.scale(t));
    }

    private void updateBlockState(Level level, BlockPos pos, BlockState state, boolean newLit, boolean newConnected) {
        level.setBlock(pos, state.setValue(ElectricFencePylonBlock.LIT, newLit).setValue(ElectricFencePylonBlock.CONNECTED, newConnected), 3);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("energy", this.energy);
        output.putInt("mode", this.mode);
        output.putBoolean("redstonePowered", this.redstonePowered);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        Collections.fill(this.items, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.energy = input.getIntOr("energy", 0);
        this.mode = input.getIntOr("mode", 0);
        this.redstonePowered = input.getBooleanOr("redstonePowered", false);
        this.armed = this.isArmed();
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
        ItemStack result = ContainerHelper.takeItem(this.items, slot);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
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
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 0 && BaseMachineBlockEntity.getFuelEnergy(stack) > 0;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.electric_fence_pylon");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new ElectricFencePylonMenu(syncId, playerInventory, this, this.dataAccess);
    }
}
