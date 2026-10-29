package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import com.fhfelipefh.sandstorm.content.item.CyberSteedUpgradeItem;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CyberSteedEntity extends TamableAnimal implements DeactivatableAndroid, HasCustomInventoryScreen {
    public static final int COYOTE_TIME_TICKS = 2;
    public static final int JUMP_BUFFER_TICKS = 2;
    public static final double JUMP_VELOCITY = 0.62;

    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(CyberSteedEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CANISTER_EXPLODED = SynchedEntityData.defineId(CyberSteedEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_SONIC_TURRET = SynchedEntityData.defineId(CyberSteedEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_BATTERY_PACK = SynchedEntityData.defineId(CyberSteedEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_SPEED_OVERDRIVE = SynchedEntityData.defineId(CyberSteedEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_CARGO_PANNIERS = SynchedEntityData.defineId(CyberSteedEntity.class, EntityDataSerializers.BOOLEAN);

    private final SimpleContainer cargoInventory = new SimpleContainer(27);
    private int coyoteTicks = 0;
    private int jumpBufferTicks = 0;
    private boolean wasJumpingLastTick = false;
    private int sonicTurretCooldown = 0;

    public CyberSteedEntity(EntityType<? extends CyberSteedEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.STEP_HEIGHT, 1.25);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_CANISTER_EXPLODED, false);
        builder.define(DATA_HAS_SONIC_TURRET, false);
        builder.define(DATA_HAS_BATTERY_PACK, false);
        builder.define(DATA_HAS_SPEED_OVERDRIVE, false);
        builder.define(DATA_HAS_CARGO_PANNIERS, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.15, 10.0f, 3.0f));
        this.goalSelector.addGoal(4, new DesertExplorationWanderGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public boolean isDeactivated() {
        return this.entityData.get(DATA_DEACTIVATED);
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        this.entityData.set(DATA_DEACTIVATED, deactivated);
        this.setNoAi(deactivated);
        if (deactivated) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1.0);
            this.setHealth(1.0f);
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
            this.setTarget(null);
            this.getNavigation().stop();
            this.ejectPassengers();
        }
    }

    public boolean isCanisterExploded() {
        return this.entityData.get(DATA_CANISTER_EXPLODED);
    }

    public void setCanisterExploded(boolean exploded) {
        this.entityData.set(DATA_CANISTER_EXPLODED, exploded);
    }

    public boolean hasSonicTurret() {
        return this.entityData.get(DATA_HAS_SONIC_TURRET);
    }

    public void setHasSonicTurret(boolean hasTurret) {
        this.entityData.set(DATA_HAS_SONIC_TURRET, hasTurret);
    }

    public boolean hasBatteryPack() {
        return this.entityData.get(DATA_HAS_BATTERY_PACK);
    }

    public void setHasBatteryPack(boolean hasBattery) {
        this.entityData.set(DATA_HAS_BATTERY_PACK, hasBattery);
    }

    public boolean hasSpeedOverdrive() {
        return this.entityData.get(DATA_HAS_SPEED_OVERDRIVE);
    }

    public void setHasSpeedOverdrive(boolean hasSpeed) {
        this.entityData.set(DATA_HAS_SPEED_OVERDRIVE, hasSpeed);
    }

    public boolean hasCargoPanniers() {
        return this.entityData.get(DATA_HAS_CARGO_PANNIERS);
    }

    public void setHasCargoPanniers(boolean hasCargo) {
        this.entityData.set(DATA_HAS_CARGO_PANNIERS, hasCargo);
    }

    public SimpleContainer getCargoInventory() {
        return this.cargoInventory;
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (this.hasCargoPanniers()) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (syncId, playerInventory, p) -> ChestMenu.threeRows(syncId, playerInventory, this.cargoInventory),
                        Component.translatable("container.sandstorm.cyber_steed_cargo")
                ));
            } else {
                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_steed_no_cargo"), true);
            }
        }
    }

    @Override
    public Component getName() {
        if (this.isDeactivated()) {
            return Component.translatable(this.getType().getDescriptionId() + ".deactivated");
        }
        return super.getName();
    }

    @Override
    public boolean showVehicleHealth() {
        return false;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float scale) {
        return new Vec3(0.0, dimensions.height() * 0.72, -0.15 * scale);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    public boolean updateJumpBufferAndCoyote(boolean isJumping, boolean onGround) {
        if (onGround) {
            this.coyoteTicks = COYOTE_TIME_TICKS;
        }

        if (isJumping) {
            if (!this.wasJumpingLastTick) {
                this.jumpBufferTicks = JUMP_BUFFER_TICKS;
            }
            this.wasJumpingLastTick = true;
        } else {
            this.wasJumpingLastTick = false;
        }

        if (this.jumpBufferTicks > 0) {
            if (this.coyoteTicks > 0) {
                this.coyoteTicks = 0;
                this.jumpBufferTicks = 0;
                return true;
            }
            this.jumpBufferTicks--;
        }

        if (!onGround && this.coyoteTicks > 0) {
            this.coyoteTicks--;
        }
        return false;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5f);
        this.yRotO = this.getYRot();
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.yBodyRot;

        boolean shouldJump = updateJumpBufferAndCoyote(player.isJumping(), this.onGround());
        double jumpVel = this.hasSpeedOverdrive() ? 0.78 : JUMP_VELOCITY;
        if (shouldJump) {
            Vec3 currentDelta = this.getDeltaMovement();
            this.setDeltaMovement(currentDelta.x, jumpVel, currentDelta.z);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, this.blockPosition(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.PLAYERS, 0.9f, 1.4f);
                if (this.hasSpeedOverdrive()) {
                    serverLevel.playSound(null, this.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 0.8f, 1.6f);
                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            this.getX(), this.getY() + 0.5, this.getZ() - 0.8,
                            8, 0.2, 0.1, 0.2, 0.05);
                }
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        this.getX(), this.getY() + 0.3, this.getZ(),
                        10, 0.25, 0.15, 0.25, 0.05);
            }
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return new Vec3(player.xxa * 0.5f, 0.0, player.zza > 0 ? player.zza : player.zza * 0.35f);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        BlockState stateOn = this.getBlockStateOn();
        boolean onSand = stateOn.is(BlockTags.SAND) || stateOn.getBlock() instanceof SandBlock;
        float baseSpeed = 0.36f;
        if (this.hasSpeedOverdrive()) {
            baseSpeed += 0.12f;
        }
        if (onSand) {
            return baseSpeed + 0.06f;
        }
        return baseSpeed;
    }

    public void detonateCanister(ServerLevel serverLevel) {
        if (this.isCanisterExploded()) {
            return;
        }
        this.setCanisterExploded(true);
        serverLevel.sendParticles(ParticleTypes.FLAME,
                this.getX(), this.getY() + 1.2, this.getZ(),
                40, 0.5, 0.5, 0.5, 0.15);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                this.getX(), this.getY() + 1.2, this.getZ(),
                3, 0.2, 0.2, 0.2, 0.0);
        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.0f, 1.2f);

        AABB burstArea = this.getBoundingBox().inflate(4.0);
        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, burstArea);
        for (LivingEntity target : targets) {
            if (target != this && target != this.getOwner() && target != this.getFirstPassenger()) {
                target.hurtServer(serverLevel, this.damageSources().explosion(this, null), 8.0f);
                target.igniteForSeconds(4.0f);
            }
        }
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE, 1));
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        if (!this.isCanisterExploded()) {
            if (source.is(DamageTypes.IN_FIRE)
                    || source.is(DamageTypes.ON_FIRE)
                    || source.is(DamageTypes.LAVA)
                    || source.is(DamageTypes.HOT_FLOOR)
                    || source.is(DamageTypes.CAMPFIRE)) {
                this.detonateCanister(serverLevel);
            }
        }
        return super.hurtServer(serverLevel, source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isDeactivated()) {
            return InteractionResult.PASS;
        }
        ItemStack itemStack = player.getItemInHand(hand);
        Level currentLevel = this.level();

        if (this.isTame()) {
            if (this.isOwnedBy(player) || player.isCreative()) {
                if (itemStack.getItem() instanceof CyberSteedUpgradeItem upgradeItem) {
                    if (!currentLevel.isClientSide() && currentLevel instanceof ServerLevel serverLevel) {
                        boolean installed = installUpgrade(upgradeItem.getUpgradeType());
                        if (installed) {
                            if (!player.isCreative()) {
                                itemStack.shrink(1);
                            }
                            serverLevel.playSound(null, this.blockPosition(), SoundEvents.ARMOR_EQUIP_NETHERITE.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
                            serverLevel.playSound(null, this.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.8f, 1.6f);
                            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                                    this.getX(), this.getY() + 1.0, this.getZ(),
                                    25, 0.35, 0.45, 0.35, 0.08);
                            if (player instanceof ServerPlayer serverPlayer) {
                                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_steed_upgrade_installed"), true);
                            }
                        } else {
                            if (player instanceof ServerPlayer serverPlayer) {
                                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_steed_upgrade_already_present"), true);
                            }
                        }
                    }
                    return InteractionResult.SUCCESS;
                }

                if (itemStack.is(SandStormItems.REPAIR_TOOL) && player.isSecondaryUseActive()) {
                    if (!currentLevel.isClientSide() && currentLevel instanceof ServerLevel serverLevel) {
                        dismantleUpgrades(serverLevel, player);
                    }
                    return InteractionResult.SUCCESS;
                }

                if (itemStack.is(SandStormItems.SCRAP_METAL)) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(8.0f);
                        if (!player.isCreative()) {
                            itemStack.shrink(1);
                        }
                        if (currentLevel instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                                    this.getX(), this.getY() + 0.8, this.getZ(),
                                    15, 0.25, 0.35, 0.25, 0.05);
                            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                                    SoundEvents.ANVIL_USE, this.getSoundSource(), 0.8f, 1.4f);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.CONSUME;
                }

                if (player.isSecondaryUseActive()) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                    if (currentLevel instanceof ServerLevel serverLevel) {
                        Component status = this.isOrderedToSit()
                                ? Component.translatable("message.sandstorm.cyber_steed_standby")
                                : Component.translatable("message.sandstorm.cyber_steed_active");
                        if (player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendSystemMessage(status, true);
                        }
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                                SoundEvents.UI_BUTTON_CLICK.value(), this.getSoundSource(), 0.8f, this.isOrderedToSit() ? 0.9f : 1.4f);
                    }
                    return InteractionResult.SUCCESS;
                }

                if (this.isOrderedToSit()) {
                    if (this.hasCargoPanniers()) {
                        if (!currentLevel.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.openMenu(new SimpleMenuProvider(
                                    (syncId, playerInventory, p) -> ChestMenu.threeRows(syncId, playerInventory, this.cargoInventory),
                                    Component.translatable("container.sandstorm.cyber_steed_cargo")
                            ));
                        }
                    } else {
                        if (!currentLevel.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_steed_no_cargo"), true);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }

                if (!this.isVehicle() && !this.isOrderedToSit()) {
                    if (!currentLevel.isClientSide()) {
                        player.startRiding(this);
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if (itemStack.is(SandStormItems.REPROGRAMMER_TOOL)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_steed_already_tamed"), true);
                }
                return InteractionResult.FAIL;
            }
        } else {
            if (!itemStack.is(SandStormItems.REPROGRAMMER_TOOL)) {
                if (!currentLevel.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_steed_requires_override"), true);
                    if (currentLevel instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                                SoundEvents.IRON_GOLEM_ATTACK, this.getSoundSource(), 0.9f, 1.4f);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    private boolean installUpgrade(CyberSteedUpgradeItem.UpgradeType type) {
        return switch (type) {
            case SONIC_TURRET -> {
                if (this.hasSonicTurret()) {
                    yield false;
                }
                this.setHasSonicTurret(true);
                yield true;
            }
            case BATTERY -> {
                if (this.hasBatteryPack()) {
                    yield false;
                }
                this.setHasBatteryPack(true);
                yield true;
            }
            case SPEED -> {
                if (this.hasSpeedOverdrive()) {
                    yield false;
                }
                this.setHasSpeedOverdrive(true);
                yield true;
            }
            case CARGO -> {
                if (this.hasCargoPanniers()) {
                    yield false;
                }
                this.setHasCargoPanniers(true);
                yield true;
            }
        };
    }

    private void dismantleUpgrades(ServerLevel serverLevel, Player player) {
        boolean anyRemoved = false;
        if (this.hasSonicTurret()) {
            this.setHasSonicTurret(false);
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.STEED_UPGRADE_SONIC_TURRET));
            anyRemoved = true;
        }
        if (this.hasBatteryPack()) {
            this.setHasBatteryPack(false);
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.STEED_UPGRADE_BATTERY));
            anyRemoved = true;
        }
        if (this.hasSpeedOverdrive()) {
            this.setHasSpeedOverdrive(false);
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.STEED_UPGRADE_SPEED));
            anyRemoved = true;
        }
        if (this.hasCargoPanniers()) {
            this.setHasCargoPanniers(false);
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.STEED_UPGRADE_CARGO));
            Containers.dropContents(serverLevel, this.blockPosition(), this.cargoInventory);
            this.cargoInventory.clearContent();
            anyRemoved = true;
        }
        if (anyRemoved) {
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.SMITHING_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
            serverLevel.sendParticles(ParticleTypes.SCRAPE, this.getX(), this.getY() + 1.0, this.getZ(), 15, 0.3, 0.3, 0.3, 0.1);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_steed_upgrades_dismantled"), true);
            }
        } else {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_steed_no_upgrades"), true);
            }
        }
    }

    private void tickSonicTurret(ServerLevel serverLevel) {
        if (!this.hasSonicTurret() || this.isDeactivated()) {
            return;
        }
        this.sonicTurretCooldown--;
        if (this.sonicTurretCooldown > 0) {
            return;
        }
        this.sonicTurretCooldown = 40;

        AABB defenseZone = this.getBoundingBox().inflate(12.0);
        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, defenseZone, entity ->
                entity.isAlive()
                        && entity != this
                        && entity != this.getOwner()
                        && entity != this.getFirstPassenger()
                        && (entity instanceof Enemy || (entity instanceof Mob mob && (mob.getTarget() == this || mob.getTarget() == this.getOwner())))
        );

        if (targets.isEmpty()) {
            return;
        }

        LivingEntity closestTarget = null;
        double closestDist = Double.MAX_VALUE;
        for (LivingEntity candidate : targets) {
            double dist = this.distanceToSqr(candidate);
            if (dist < closestDist) {
                closestDist = dist;
                closestTarget = candidate;
            }
        }

        if (closestTarget != null) {
            fireSonicTurret(serverLevel, closestTarget);
        }
    }

    private void fireSonicTurret(ServerLevel serverLevel, LivingEntity target) {
        Vec3 start = this.position().add(0.0, 1.4, 0.0);
        Vec3 end = target.position().add(0.0, target.getEyeHeight() * 0.5, 0.0);
        Vec3 dir = end.subtract(start).normalize();

        serverLevel.playSound(null, this.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL, 0.85f, 1.75f);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 0.5f, 2.0f);

        for (int i = 1; i <= 10; i++) {
            double step = i * 0.1;
            Vec3 particlePos = start.lerp(end, step);
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, particlePos.x, particlePos.y, particlePos.z, 2, 0.1, 0.1, 0.1, 0.05);
        }

        target.hurtServer(serverLevel, this.damageSources().sonicBoom(this), 8.0f);
        target.push(-dir.x * 0.8, 0.25, -dir.z * 0.8);
    }

    private void tickBatteryPack(ServerLevel serverLevel) {
        if (!this.hasBatteryPack() || this.isDeactivated()) {
            return;
        }
        if (this.tickCount % 40 != 0) {
            return;
        }

        LivingEntity rider = this.getControllingPassenger();
        Player targetPlayer = rider instanceof Player p ? p : (this.getOwner() instanceof Player owner && this.distanceToSqr(owner) <= 36.0 ? owner : null);

        if (targetPlayer instanceof ServerPlayer serverPlayer) {
            SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(serverPlayer);
            if (suit != null && suit.getEnergyStorage() != null) {
                long needed = suit.getEnergyStorage().getCapacity() - suit.getEnergyStorage().getStoredEnergy();
                if (needed > 0) {
                    long toTransfer = Math.min(needed, 500L);
                    suit.getEnergyStorage().receiveEnergy(toTransfer);
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(),
                            2, 0.15, 0.25, 0.15, 0.02);
                }
            }
            if (serverPlayer.level().isDarkOutside()) {
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, true));
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60, 0, false, false, false));
            }
        }
    }

    @Override
    public void tick() {
        if (this.isDeactivated()) {
            super.tick();
            this.setDeltaMovement(0.0, Math.min(0.0, this.getDeltaMovement().y), 0.0);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
            return;
        }
        super.tick();
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            this.tickSonicTurret(serverLevel);
            this.tickBatteryPack(serverLevel);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(serverLevel, damageSource, recentlyHit);
        if (this.hasCargoPanniers()) {
            Containers.dropContents(serverLevel, this.blockPosition(), this.cargoInventory);
        }
        if (this.hasSonicTurret()) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.STEED_UPGRADE_SONIC_TURRET));
        }
        if (this.hasBatteryPack()) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.STEED_UPGRADE_BATTERY));
        }
        if (this.hasSpeedOverdrive()) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.STEED_UPGRADE_SPEED));
        }
        if (this.hasCargoPanniers()) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.STEED_UPGRADE_CARGO));
        }

        if (this.isDeactivated()) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SCRAP_METAL, 2));
            return;
        }
        int scrapCount = 2 + this.random.nextInt(3);
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SCRAP_METAL, scrapCount));
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.LOOSE_WIRES, 1 + this.random.nextInt(2)));
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.CIRCUIT_BOARD, 1));
        if (!this.isCanisterExploded()) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.PROPELLANT_CARTRIDGE, 1));
        }
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        return false;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return this.isDeactivated() ? SoundEvents.NETHERITE_BLOCK_BREAK : SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isDeactivated() ? SoundEvents.ITEM_BREAK.value() : SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Deactivated", this.isDeactivated());
        output.putBoolean("CanisterExploded", this.isCanisterExploded());
        output.putBoolean("SonicTurret", this.hasSonicTurret());
        output.putBoolean("BatteryPack", this.hasBatteryPack());
        output.putBoolean("SpeedOverdrive", this.hasSpeedOverdrive());
        output.putBoolean("CargoPanniers", this.hasCargoPanniers());
        for (int i = 0; i < this.cargoInventory.getContainerSize(); i++) {
            ItemStack stack = this.cargoInventory.getItem(i);
            if (!stack.isEmpty()) {
                output.store("Slot" + i, ItemStack.CODEC, stack);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        this.setCanisterExploded(input.getBooleanOr("CanisterExploded", false));
        this.setHasSonicTurret(input.getBooleanOr("SonicTurret", false));
        this.setHasBatteryPack(input.getBooleanOr("BatteryPack", false));
        this.setHasSpeedOverdrive(input.getBooleanOr("SpeedOverdrive", false));
        this.setHasCargoPanniers(input.getBooleanOr("CargoPanniers", false));
        for (int i = 0; i < this.cargoInventory.getContainerSize(); i++) {
            int slot = i;
            input.read("Slot" + i, ItemStack.CODEC).ifPresent(stack -> this.cargoInventory.setItem(slot, stack));
        }
    }
}
