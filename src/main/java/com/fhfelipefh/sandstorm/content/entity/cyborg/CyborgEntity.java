package com.fhfelipefh.sandstorm.content.entity.cyborg;

import com.fhfelipefh.sandstorm.content.gui.CyborgTelemetryMenu;
import com.fhfelipefh.sandstorm.content.item.CyborgUpgradeItem;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public abstract class CyborgEntity extends PathfinderMob implements MenuProvider {
    public static final int MAX_ENERGY = 50000;
    public static final int MAX_COOLANT = 4000;
    public static final int MAX_INTEGRITY = 100;

    private static final EntityDataAccessor<Integer> DATA_ROUTINE = SynchedEntityData.defineId(CyborgEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_VISOR_STATE = SynchedEntityData.defineId(CyborgEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ENERGY = SynchedEntityData.defineId(CyborgEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COOLANT = SynchedEntityData.defineId(CyborgEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_INTEGRITY = SynchedEntityData.defineId(CyborgEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockPos> DATA_ZONE_MIN = SynchedEntityData.defineId(CyborgEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockPos> DATA_ZONE_MAX = SynchedEntityData.defineId(CyborgEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<String> DATA_OWNER_UUID = SynchedEntityData.defineId(CyborgEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_UPGRADES_MASK = SynchedEntityData.defineId(CyborgEntity.class, EntityDataSerializers.INT);

    protected final SimpleContainer inventory = new SimpleContainer(CyborgTelemetryMenu.CYBORG_SLOTS);
    private int patrolWaypointIndex = 0;
    private int patrolScanTicks = 0;

    public CyborgEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setEnergy(MAX_ENERGY);
        this.setCoolant(MAX_COOLANT);
        this.setIntegrity(MAX_INTEGRITY);
    }

    public abstract CyborgSpecialty getSpecialty();

    protected abstract void tickAutonomousWork(ServerLevel level);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ROUTINE, CyborgRoutine.AUTONOMOUS_WORK.ordinal());
        builder.define(DATA_VISOR_STATE, 0);
        builder.define(DATA_ENERGY, MAX_ENERGY);
        builder.define(DATA_COOLANT, MAX_COOLANT);
        builder.define(DATA_INTEGRITY, MAX_INTEGRITY);
        builder.define(DATA_ZONE_MIN, BlockPos.ZERO);
        builder.define(DATA_ZONE_MAX, BlockPos.ZERO);
        builder.define(DATA_OWNER_UUID, "");
        builder.define(DATA_UPGRADES_MASK, 0);
    }

    public CyborgRoutine getRoutine() {
        return CyborgRoutine.fromOrdinal(this.entityData.get(DATA_ROUTINE));
    }

    public void setRoutine(CyborgRoutine routine) {
        this.entityData.set(DATA_ROUTINE, routine.ordinal());
        updateVisorColor(routine);
    }

    private void updateVisorColor(CyborgRoutine routine) {
        int colorCode = switch (routine) {
            case AUTONOMOUS_WORK -> 0;
            case FOLLOW_OPERATOR -> 1;
            case PATROL_PERIMETER -> 2;
            case RETURN_TO_DOCK -> 3;
            case STANDBY -> 0;
        };
        this.entityData.set(DATA_VISOR_STATE, colorCode);
    }

    public int getVisorColor() {
        return this.entityData.get(DATA_VISOR_STATE);
    }

    public void setVisorState(int state) {
        this.entityData.set(DATA_VISOR_STATE, state);
    }

    public int getEnergy() {
        return this.entityData.get(DATA_ENERGY);
    }

    public int getMaxEnergy() {
        return hasUpgrade(CyborgUpgradeItem.CyborgUpgradeType.CRYO_TREHALOSE_CELL) ? MAX_ENERGY * 3 : MAX_ENERGY;
    }

    public double getOperationalRadius() {
        return hasUpgrade(CyborgUpgradeItem.CyborgUpgradeType.LONG_RANGE_LIDAR_LENS) ? 96.0 : 32.0;
    }

    public void setEnergy(int energy) {
        this.entityData.set(DATA_ENERGY, Math.clamp(energy, 0, getMaxEnergy()));
    }

    public int consumeEnergy(int amount) {
        int current = getEnergy();
        int consumed = Math.min(current, amount);
        setEnergy(current - consumed);
        return consumed;
    }

    public void addEnergy(int amount) {
        setEnergy(getEnergy() + amount);
    }

    public int getCoolant() {
        return this.entityData.get(DATA_COOLANT);
    }

    public void setCoolant(int coolant) {
        this.entityData.set(DATA_COOLANT, Math.clamp(coolant, 0, MAX_COOLANT));
    }

    public int consumeCoolant(int amount) {
        int current = getCoolant();
        int consumed = Math.min(current, amount);
        setCoolant(current - consumed);
        return consumed;
    }

    public int getIntegrity() {
        return this.entityData.get(DATA_INTEGRITY);
    }

    public void setIntegrity(int integrity) {
        this.entityData.set(DATA_INTEGRITY, Math.clamp(integrity, 0, MAX_INTEGRITY));
    }

    public void chargeEnergy(int amount) {
        addEnergy(amount);
    }

    public void repairIntegrity(int amount) {
        setIntegrity(getIntegrity() + amount);
    }

    public int getUpgradesMask() {
        return this.entityData.get(DATA_UPGRADES_MASK);
    }

    public void setUpgradesMask(int mask) {
        this.entityData.set(DATA_UPGRADES_MASK, mask);
    }

    public boolean hasUpgrade(CyborgUpgradeItem.CyborgUpgradeType type) {
        if (type == null) {
            return false;
        }
        return (getUpgradesMask() & type.getMaskBit()) != 0;
    }

    public boolean installUpgrade(CyborgUpgradeItem.CyborgUpgradeType type) {
        if (type == null || hasUpgrade(type)) {
            return false;
        }
        setUpgradesMask(getUpgradesMask() | type.getMaskBit());
        return true;
    }

    public boolean uninstallUpgrade(CyborgUpgradeItem.CyborgUpgradeType type) {
        if (type == null || !hasUpgrade(type)) {
            return false;
        }
        setUpgradesMask(getUpgradesMask() & ~type.getMaskBit());
        if (getEnergy() > getMaxEnergy()) {
            setEnergy(getMaxEnergy());
        }
        return true;
    }

    public BlockPos getZoneMin() {
        return this.entityData.get(DATA_ZONE_MIN);
    }

    public void setZoneMin(BlockPos pos) {
        this.entityData.set(DATA_ZONE_MIN, pos != null ? pos : BlockPos.ZERO);
    }

    public BlockPos getZoneMax() {
        return this.entityData.get(DATA_ZONE_MAX);
    }

    public void setZoneMax(BlockPos pos) {
        this.entityData.set(DATA_ZONE_MAX, pos != null ? pos : BlockPos.ZERO);
    }

    public boolean hasOperationalZone() {
        return !getZoneMin().equals(BlockPos.ZERO) && !getZoneMax().equals(BlockPos.ZERO);
    }

    public boolean hasZone() {
        return hasOperationalZone();
    }

    public void clearZone() {
        setZoneMin(BlockPos.ZERO);
        setZoneMax(BlockPos.ZERO);
    }

    public void setDemarcatedZone(BlockPos cornerA, BlockPos cornerB) {
        int minX = Math.min(cornerA.getX(), cornerB.getX());
        int minY = Math.min(cornerA.getY(), cornerB.getY());
        int minZ = Math.min(cornerA.getZ(), cornerB.getZ());
        int maxX = Math.max(cornerA.getX(), cornerB.getX());
        int maxY = Math.max(cornerA.getY(), cornerB.getY());
        int maxZ = Math.max(cornerA.getZ(), cornerB.getZ());
        setZoneMin(new BlockPos(minX, minY, minZ));
        setZoneMax(new BlockPos(maxX, maxY, maxZ));
    }

    public UUID getOwnerUUID() {
        String s = this.entityData.get(DATA_OWNER_UUID);
        if (s == null || s.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setOwnerUUID(UUID uuid) {
        this.entityData.set(DATA_OWNER_UUID, uuid != null ? uuid.toString() : "");
    }

    public boolean isOwner(Player player) {
        UUID owner = getOwnerUUID();
        return owner != null && owner.equals(player.getUUID());
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public boolean isWorking() {
        return getRoutine() == CyborgRoutine.AUTONOMOUS_WORK && getEnergy() > 0;
    }

    public void unloadInventory(Player player) {
        Level currentLevel = this.level();
        if (currentLevel.isClientSide()) {
            return;
        }
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (player != null) {
                    if (!player.getInventory().add(stack)) {
                        Containers.dropItemStack(currentLevel, this.getX(), this.getY() + 0.5, this.getZ(), stack);
                    }
                } else {
                    Containers.dropItemStack(currentLevel, this.getX(), this.getY() + 0.5, this.getZ(), stack);
                }
                this.inventory.setItem(i, ItemStack.EMPTY);
            }
        }
        this.inventory.setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getType().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new CyborgTelemetryMenu(syncId, playerInventory, this.inventory, this.createContainerData());
    }

    private ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> getEnergy();
                    case 1 -> getMaxEnergy();
                    case 2 -> getCoolant();
                    case 3 -> MAX_COOLANT;
                    case 4 -> getIntegrity();
                    case 5 -> getRoutine().ordinal();
                    case 6 -> getVisorColor();
                    case 7 -> getSpecialty().ordinal();
                    case 8 -> getUpgradesMask();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> setEnergy(value);
                    case 2 -> setCoolant(value);
                    case 4 -> setIntegrity(value);
                    case 5 -> setRoutine(CyborgRoutine.fromOrdinal(value));
                    case 6 -> setVisorState(value);
                    case 8 -> setUpgradesMask(value);
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 9;
            }
        };
    }

    @Override
    public void tick() {
        super.tick();

        Level currentLevel = this.level();
        if (currentLevel.isClientSide()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) currentLevel;

        if (hasUpgrade(CyborgUpgradeItem.CyborgUpgradeType.PIEZO_HOVER_THRUSTER)) {
            this.resetFallDistance();
            if (this.getDeltaMovement().horizontalDistanceSqr() > 0.005 && this.random.nextFloat() < 0.25f) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        this.getX(), this.getY() + 0.05, this.getZ(),
                        2, 0.15, 0.05, 0.15, 0.02);
            }
        }

        if (getEnergy() <= 0) {
            setVisorState(0);
            this.getNavigation().stop();
            return;
        }

        if (this.tickCount % 60 == 0) {
            consumeEnergy(1);
        }

        int globalOrder = CyborgSwarmManager.getInstance().getGlobalTacticalOrder();
        if (globalOrder == 1) {
            tickReturnToDock(serverLevel);
            setVisorState(2);
            return;
        } else if (globalOrder == 3) {
            tickStandby(serverLevel);
            return;
        }

        if (getEnergy() < 4000 && getRoutine() == CyborgRoutine.AUTONOMOUS_WORK) {
            tickReturnToDock(serverLevel);
            return;
        }

        switch (getRoutine()) {
            case AUTONOMOUS_WORK -> tickAutonomousWork(serverLevel);
            case FOLLOW_OPERATOR -> tickFollowOperator(serverLevel);
            case PATROL_PERIMETER -> tickPatrolPerimeter(serverLevel);
            case RETURN_TO_DOCK -> tickReturnToDock(serverLevel);
            case STANDBY -> tickStandby(serverLevel);
        }
    }

    protected void tickReturnToDock(ServerLevel level) {
        BlockPos dock = CyborgSwarmManager.getInstance().findNearestAvailableDock(level, this.blockPosition(), getOperationalRadius());
        if (dock != null) {
            double distSq = this.distanceToSqr(dock.getX() + 0.5, dock.getY() + 0.5, dock.getZ() + 0.5);
            if (distSq > 2.25) {
                this.getNavigation().moveTo(dock.getX() + 0.5, dock.getY() + 0.5, dock.getZ() + 0.5, 1.15);
                setVisorState(3);
            } else {
                this.getNavigation().stop();
                setVisorState(0);
                chargeEnergy(500);
                repairIntegrity(1);
                if (getCoolant() < MAX_COOLANT) {
                    setCoolant(Math.min(MAX_COOLANT, getCoolant() + 50));
                }
                unloadToConnectedStorage(level, dock);
                if (this.random.nextFloat() < 0.2f) {
                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            dock.getX() + 0.5, dock.getY() + 0.2, dock.getZ() + 0.5,
                            3, 0.2, 0.1, 0.2, 0.05);
                }
            }
        } else {
            this.getNavigation().stop();
            setVisorState(0);
        }
    }

    private void unloadToConnectedStorage(ServerLevel level, BlockPos dockPos) {
        Direction[] directions = Direction.values();
        for (Direction dir : directions) {
            BlockPos targetPos = dockPos.relative(dir);
            BlockEntity targetBe = level.getBlockEntity(targetPos);
            if (targetBe instanceof Container targetContainer) {
                for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                    ItemStack stack = this.inventory.getItem(i);
                    if (!stack.isEmpty()) {
                        ItemStack remainder = depositIntoContainer(targetContainer, stack);
                        this.inventory.setItem(i, remainder);
                    }
                }
                this.inventory.setChanged();
                targetContainer.setChanged();
                break;
            }
        }
    }

    private ItemStack depositIntoContainer(Container container, ItemStack stack) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack existing = container.getItem(slot);
            if (existing.isEmpty()) {
                container.setItem(slot, stack.copy());
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameComponents(existing, stack) && existing.getCount() < existing.getMaxStackSize()) {
                int transferable = Math.min(stack.getCount(), existing.getMaxStackSize() - existing.getCount());
                existing.grow(transferable);
                stack.shrink(transferable);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }

    protected void tickFollowOperator(ServerLevel level) {
        Player owner = null;
        if (getOwnerUUID() != null) {
            owner = level.getPlayerByUUID(getOwnerUUID());
        }

        if (owner == null) {
            this.getNavigation().stop();
            setVisorState(0);
            return;
        }

        double distSq = this.distanceToSqr(owner);
        if (distSq > 48.0 * 48.0 && !owner.isSpectator()) {
            this.teleportTo(owner.getX(), owner.getY(), owner.getZ());
            this.getNavigation().stop();
            return;
        }

        if (distSq > 16.0) {
            this.getNavigation().moveTo(owner, 1.25);
            setVisorState(1);
        } else if (distSq < 6.0) {
            this.getNavigation().stop();
            this.getLookControl().setLookAt(owner, 30.0f, 30.0f);
            setVisorState(0);
        }

        LivingEntity ownerLastHurt = owner.getLastHurtByMob();
        if (ownerLastHurt != null && ownerLastHurt.isAlive() && this.distanceToSqr(ownerLastHurt) < 144.0) {
            this.setTarget(ownerLastHurt);
            setVisorState(2);
        }
    }

    protected void tickPatrolPerimeter(ServerLevel level) {
        patrolScanTicks++;
        if (patrolScanTicks % 20 == 0) {
            AABB threatScan = this.getBoundingBox().inflate(16.0);
            List<LivingEntity> threats = level.getEntitiesOfClass(LivingEntity.class, threatScan, e -> e instanceof Enemy && e.isAlive());
            if (!threats.isEmpty()) {
                LivingEntity priorityThreat = threats.getFirst();
                this.setTarget(priorityThreat);
                setVisorState(2);
                CyborgSwarmManager.getInstance().broadcastEmergency(level, this, null);
                return;
            }
        }

        if (hasZone()) {
            BlockPos min = getZoneMin();
            BlockPos max = getZoneMax();
            BlockPos[] waypoints = new BlockPos[]{
                    new BlockPos(min.getX(), min.getY(), min.getZ()),
                    new BlockPos(max.getX(), min.getY(), min.getZ()),
                    new BlockPos(max.getX(), min.getY(), max.getZ()),
                    new BlockPos(min.getX(), min.getY(), max.getZ())
            };
            BlockPos targetWaypoint = waypoints[patrolWaypointIndex % waypoints.length];
            double distSq = this.distanceToSqr(targetWaypoint.getX() + 0.5, targetWaypoint.getY(), targetWaypoint.getZ() + 0.5);
            if (distSq < 4.0) {
                patrolWaypointIndex = (patrolWaypointIndex + 1) % waypoints.length;
            } else {
                this.getNavigation().moveTo(targetWaypoint.getX() + 0.5, targetWaypoint.getY(), targetWaypoint.getZ() + 0.5, 1.0);
                setVisorState(1);
            }
        } else {
            setVisorState(0);
        }
    }

    protected void tickStandby(ServerLevel level) {
        this.getNavigation().stop();
        setVisorState(0);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (hasUpgrade(CyborgUpgradeItem.CyborgUpgradeType.ACID_CHITIN_PLATING)) {
            amount *= 0.75f;
        }
        CyborgSwarmManager.getInstance().broadcastEmergency(level, this, source);
        return super.hurtServer(level, source, amount);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        if (hasUpgrade(CyborgUpgradeItem.CyborgUpgradeType.ACID_CHITIN_PLATING) && effectInstance.getEffect().equals(MobEffects.POISON)) {
            return false;
        }
        return super.canBeAffected(effectInstance);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof CyborgUpgradeItem upgradeItem) {
            CyborgUpgradeItem.CyborgUpgradeType type = upgradeItem.getUpgradeType();
            if (!hasUpgrade(type)) {
                if (!this.level().isClientSide()) {
                    installUpgrade(type);
                    if (!player.hasInfiniteMaterials()) {
                        held.shrink(1);
                    }
                    this.playSound(SoundEvents.ARMOR_EQUIP_NETHERITE.value(), 1.0f, 1.0f);
                    player.sendSystemMessage(Component.translatable("message.sandstorm.cyborg_upgrade_installed",
                            Component.translatable(upgradeItem.getDescriptionId())));
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (held.is(SandStormItems.CYBERNETIC_COMMAND_UPLINK) || player.isShiftKeyDown() || isOwner(player)) {
            if (!this.level().isClientSide()) {
                if (getOwnerUUID() == null) {
                    setOwnerUUID(player.getUUID());
                }
                player.openMenu(this);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Energy", getEnergy());
        output.putInt("Coolant", getCoolant());
        output.putInt("Integrity", getIntegrity());
        output.putInt("Routine", getRoutine().ordinal());
        output.putInt("VisorState", getVisorColor());
        output.putLong("ZoneMin", getZoneMin().asLong());
        output.putLong("ZoneMax", getZoneMax().asLong());
        output.putInt("UpgradesMask", getUpgradesMask());
        String owner = this.entityData.get(DATA_OWNER_UUID);
        if (owner != null && !owner.isEmpty()) {
            output.putString("OwnerUUID", owner);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setUpgradesMask(input.getIntOr("UpgradesMask", 0));
        setEnergy(input.getIntOr("Energy", 0));
        setCoolant(input.getIntOr("Coolant", 0));
        setIntegrity(input.getIntOr("Integrity", 100));
        setRoutine(CyborgRoutine.fromOrdinal(input.getIntOr("Routine", 0)));
        setVisorState(input.getIntOr("VisorState", 0));
        setZoneMin(BlockPos.of(input.getLongOr("ZoneMin", 0L)));
        setZoneMax(BlockPos.of(input.getLongOr("ZoneMax", 0L)));
        input.getString("OwnerUUID").ifPresent(ownerStr -> this.entityData.set(DATA_OWNER_UUID, ownerStr));
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        CyborgSwarmManager.getInstance().releaseAll(this.getUUID());
        Containers.dropContents(this.level(), this, this.inventory);
    }
}
