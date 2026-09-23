package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.effect.SonicBlastVisualEffect;
import com.fhfelipefh.sandstorm.content.gui.AutonomousSonicTurretMenu;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.level.block.entity.BlockEntityType;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutonomousSonicTurretBlockEntity extends BaseMachineBlockEntity {
    public static final double RANGE = 20.0;
    public static final int ENERGY_PER_SHOT = 400;
    public static final float DAMAGE_AMOUNT = 16.0f;
    public static final int COOLDOWN_TICKS = 20;

    private int filterMode = 0;
    private int targetingStrategy = 0;
    private int cooldown = 0;
    private float targetYaw = 0.0f;
    private float targetPitch = 0.0f;
    private final Set<String> targetEntityIds = new HashSet<>();

    private final ContainerData turretDataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy;
                case 1 -> maxEnergy;
                case 2 -> cooldown;
                case 3 -> COOLDOWN_TICKS;
                case 4 -> wptConnected ? 1 : 0;
                case 5 -> filterMode;
                case 6 -> targetingStrategy;
                case 7 -> targetEntityIds.size();
                case 8 -> getBlockPos().getX();
                case 9 -> getBlockPos().getY();
                case 10 -> getBlockPos().getZ();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energy = value;
                case 1 -> maxEnergy = value;
                case 2 -> cooldown = value;
                case 4 -> wptConnected = (value == 1);
                case 5 -> filterMode = value;
                case 6 -> targetingStrategy = value;
            }
        }

        @Override
        public int getCount() {
            return 11;
        }
    };

    public AutonomousSonicTurretBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.AUTONOMOUS_SONIC_TURRET_BE, pos, state);
    }

    public AutonomousSonicTurretBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 1, 100);
        this.maxEnergy = 10000;
        initDefaultHostiles();
    }

    private void initDefaultHostiles() {
        targetEntityIds.add("minecraft:zombie");
        targetEntityIds.add("minecraft:skeleton");
        targetEntityIds.add("minecraft:creeper");
        targetEntityIds.add("minecraft:spider");
        targetEntityIds.add("minecraft:cave_spider");
        targetEntityIds.add("minecraft:enderman");
        targetEntityIds.add("minecraft:witch");
        targetEntityIds.add("minecraft:slime");
        targetEntityIds.add("minecraft:magma_cube");
        targetEntityIds.add("minecraft:phantom");
        targetEntityIds.add("minecraft:drowned");
        targetEntityIds.add("minecraft:husk");
        targetEntityIds.add("minecraft:stray");
        targetEntityIds.add("minecraft:silverfish");
        targetEntityIds.add("minecraft:blaze");
        targetEntityIds.add("minecraft:ghast");
        targetEntityIds.add("minecraft:pillager");
        targetEntityIds.add("minecraft:ravager");
        targetEntityIds.add("minecraft:vindicator");
        targetEntityIds.add("minecraft:evoker");
        targetEntityIds.add("minecraft:vex");
        targetEntityIds.add("minecraft:warden");
        targetEntityIds.add("sandstorm:sandworm");
    }

    public int getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(int mode) {
        this.filterMode = mode;
        setChanged();
    }

    public int getTargetingStrategy() {
        return targetingStrategy;
    }

    public void setTargetingStrategy(int strategy) {
        this.targetingStrategy = strategy;
        setChanged();
    }

    public Set<String> getTargetEntityIds() {
        return Collections.unmodifiableSet(targetEntityIds);
    }

    public void setTargetEntityIds(List<String> ids) {
        targetEntityIds.clear();
        targetEntityIds.addAll(ids);
        setChanged();
    }

    public void toggleTargetEntity(String entityId) {
        if (targetEntityIds.contains(entityId)) {
            targetEntityIds.remove(entityId);
        } else {
            targetEntityIds.add(entityId);
        }
        setChanged();
    }

    public float getTargetYaw() {
        return targetYaw;
    }

    public float getTargetPitch() {
        return targetPitch;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

        if (cooldown > 0) {
            cooldown--;
        }

        if (energy >= ENERGY_PER_SHOT && cooldown <= 0 && level instanceof ServerLevel serverLevel) {
            LivingEntity target = findBestTarget(serverLevel, pos);
            if (target != null) {
                aimAt(pos, target);
                fireSonicBlast(serverLevel, pos, target);
                energy -= ENERGY_PER_SHOT;
                cooldown = COOLDOWN_TICKS;
                setChanged();
            }
        }
    }

    private void aimAt(BlockPos pos, LivingEntity target) {
        Vec3 origin = Vec3.atCenterOf(pos).add(0.0, 0.5, 0.0);
        Vec3 diff = target.getEyePosition().subtract(origin);
        double horizontalDist = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        this.targetYaw = (float) (Math.atan2(diff.z, diff.x) * (180.0 / Math.PI)) - 90.0f;
        this.targetPitch = (float) (-(Math.atan2(diff.y, horizontalDist) * (180.0 / Math.PI)));
    }

    private LivingEntity findBestTarget(ServerLevel level, BlockPos pos) {
        Vec3 center = Vec3.atCenterOf(pos);
        AABB searchBox = new AABB(pos).inflate(RANGE);
        List<LivingEntity> potentialTargets = level.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> isValidTarget(entity, level, center)
        );

        if (potentialTargets.isEmpty()) {
            return null;
        }

        return switch (targetingStrategy) {
            case 1 -> Collections.min(potentialTargets, Comparator.comparingDouble(LivingEntity::getHealth));
            case 2 -> Collections.max(potentialTargets, Comparator.comparingDouble(LivingEntity::getMaxHealth));
            default -> Collections.min(potentialTargets, Comparator.comparingDouble(e -> e.distanceToSqr(center)));
        };
    }

    private boolean isValidTarget(LivingEntity entity, ServerLevel level, Vec3 center) {
        if (!entity.isAlive() || entity.isSpectator()) {
            return false;
        }
        if (entity instanceof Player player) {
            if (player.isCreative()) {
                return false;
            }
            if (!targetEntityIds.contains("minecraft:player")) {
                return false;
            }
        }
        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
        if (filterMode == 0) {
            if (!targetEntityIds.contains(entityId)) {
                return false;
            }
        } else {
            if (targetEntityIds.contains(entityId)) {
                return false;
            }
        }
        if (entity.distanceToSqr(center) > RANGE * RANGE) {
            return false;
        }
        BlockHitResult hit = level.clip(new ClipContext(
                center.add(0.0, 0.5, 0.0),
                entity.getEyePosition(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                entity
        ));
        return hit.getType() == HitResult.Type.MISS;
    }

    private void fireSonicBlast(ServerLevel serverLevel, BlockPos pos, LivingEntity target) {
        Vec3 origin = Vec3.atCenterOf(pos).add(0.0, 0.5, 0.0);
        Vec3 targetPos = target.getEyePosition();
        SonicBlastVisualEffect.spawnSonicBlastFromPoint(serverLevel, origin, targetPos);
        serverLevel.playSound(null, pos, SandStormSoundEvents.SONIC_CANNON_BLAST, SoundSource.BLOCKS, 1.2f, 1.0f);
        target.hurtServer(serverLevel, serverLevel.damageSources().magic(), DAMAGE_AMOUNT);
        Vec3 pushDir = targetPos.subtract(origin).normalize();
        target.push(pushDir.x * 1.1, 0.35, pushDir.z * 1.1);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("filter_mode", this.filterMode);
        output.putInt("strategy", this.targetingStrategy);
        output.putString("target_list", String.join(";", this.targetEntityIds));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.filterMode = input.getIntOr("filter_mode", 0);
        this.targetingStrategy = input.getIntOr("strategy", 0);
        String listStr = input.getStringOr("target_list", "");
        if (!listStr.isEmpty()) {
            this.targetEntityIds.clear();
            for (String s : listStr.split(";")) {
                if (!s.isBlank()) {
                    this.targetEntityIds.add(s);
                }
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("filter_mode", this.filterMode);
        tag.putInt("strategy", this.targetingStrategy);
        tag.putString("target_list", String.join(";", this.targetEntityIds));
        tag.putFloat("target_yaw", this.targetYaw);
        tag.putFloat("target_pitch", this.targetPitch);
        return tag;
    }

    public void readClientData(CompoundTag tag) {
        if (tag.contains("filter_mode")) {
            this.filterMode = tag.getInt("filter_mode").orElse(0);
        }
        if (tag.contains("strategy")) {
            this.targetingStrategy = tag.getInt("strategy").orElse(0);
        }
        if (tag.contains("target_yaw")) {
            this.targetYaw = tag.getFloat("target_yaw").orElse(0.0f);
        }
        if (tag.contains("target_pitch")) {
            this.targetPitch = tag.getFloat("target_pitch").orElse(0.0f);
        }
        if (tag.contains("target_list")) {
            String listStr = tag.getString("target_list").orElse("");
            if (!listStr.isEmpty()) {
                this.targetEntityIds.clear();
                for (String s : listStr.split(";")) {
                    if (!s.isBlank()) {
                        this.targetEntityIds.add(s);
                    }
                }
            }
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 0 && getFuelEnergy(stack) > 0;
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    protected boolean canProcess() {
        return false;
    }

    @Override
    protected void processRecipe() {
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SandStormSoundEvents.SONIC_CANNON_BLAST;
    }

    @Override
    protected int getBatterySlotIndex() {
        return 0;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.sandstorm.autonomous_sonic_turret");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new AutonomousSonicTurretMenu(syncId, playerInventory, this, this.turretDataAccess);
    }
}
