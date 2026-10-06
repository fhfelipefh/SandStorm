package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import com.fhfelipefh.sandstorm.content.world.EmpParalysisHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CyberneticGolemEntity extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> DATA_OVERDRIVE = SynchedEntityData.defineId(CyberneticGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_HEAT = SynchedEntityData.defineId(CyberneticGolemEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> DATA_TIER = SynchedEntityData.defineId(CyberneticGolemEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_ATTACK_ANIM = SynchedEntityData.defineId(CyberneticGolemEntity.class, EntityDataSerializers.INT);

    private static final Identifier OVERDRIVE_SPEED_MODIFIER_ID = Identifier.fromNamespaceAndPath("sandstorm", "cybernetic_golem_overdrive");

    private Optional<UUID> ownerUUID = Optional.empty();
    private boolean sentinelMode = false;
    private boolean permanentOverdrive = false;
    private int sandTrappedTicks = 0;

    public CyberneticGolemEntity(EntityType<? extends CyberneticGolemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FOLLOW_RANGE, 128.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OVERDRIVE, false);
        builder.define(DATA_HEAT, 0.0f);
        builder.define(DATA_TIER, "iron");
        builder.define(DATA_ATTACK_ANIM, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(3, new CyberneticFollowOwnerGoal(this, 1.05, 4.5f, 6.5f));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new CyberneticDefendOwnerGoal(this));
        this.targetSelector.addGoal(3, new HostileTargetGoal(this));
    }

    public boolean isOverdrive() {
        return this.entityData.get(DATA_OVERDRIVE);
    }

    public void setOverdrive(boolean overdrive) {
        this.entityData.set(DATA_OVERDRIVE, overdrive);
        applyOverdriveSpeedModifier(overdrive);
    }

    public float getHeat() {
        return this.entityData.get(DATA_HEAT);
    }

    public void setHeat(float heat) {
        this.entityData.set(DATA_HEAT, Math.max(0.0f, Math.min(1.0f, heat)));
    }

    public GolemMetalTier getMetalTier() {
        return GolemMetalTier.byId(this.entityData.get(DATA_TIER));
    }

    public void setMetalTier(GolemMetalTier tier) {
        if (tier == null) {
            tier = GolemMetalTier.IRON;
        }
        this.entityData.set(DATA_TIER, tier.getId());
        applyTierAttributes(tier);
    }

    public int getAttackAnimTicks() {
        return this.entityData.get(DATA_ATTACK_ANIM);
    }

    public void setAttackAnimTicks(int ticks) {
        this.entityData.set(DATA_ATTACK_ANIM, ticks);
    }

    public Optional<UUID> getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = Optional.ofNullable(uuid);
    }

    public boolean isOwner(LivingEntity entity) {
        return entity != null && this.ownerUUID.map(u -> u.equals(entity.getUUID())).orElse(false);
    }

    public Player getOwner() {
        return this.ownerUUID.map(this.level()::getPlayerByUUID).orElse(null);
    }

    public boolean isSentinelMode() {
        return this.sentinelMode;
    }

    public void setSentinelMode(boolean sentinel) {
        this.sentinelMode = sentinel;
    }

    public boolean isPermanentOverdrive() {
        return this.permanentOverdrive;
    }

    public void setPermanentOverdrive(boolean permanent) {
        this.permanentOverdrive = permanent;
        if (permanent) {
            setOverdrive(true);
            setHeat(1.0f);
        }
    }

    public void onRemoteRecallCalled(ServerPlayer player) {
        this.sentinelMode = false;
        LivingEntity currentTarget = this.getTarget();
        if (currentTarget != null && !EmpParalysisHandler.isAndroidOrAutomaton(currentTarget)) {
            this.setTarget(null);
        }

        double distSq = this.distanceToSqr(player);
        if (distSq > 256.0 && this.getHeat() <= 0.05f) {
            setOverdrive(true);
            setHeat(1.0f);
        }

        this.getNavigation().moveTo(player, 1.4);

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    this.getX(), this.getY() + 2.4, this.getZ(), 20, 0.4, 0.5, 0.4, 0.1);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1.0f, 1.8f);
        }
    }

    public void applyTierAttributes(GolemMetalTier tier) {
        AttributeInstance hp = this.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            hp.setBaseValue(tier.getMaxHealth());
            this.setHealth((float) tier.getMaxHealth());
        }
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.setBaseValue(tier.getArmor());
        }
        AttributeInstance toughness = this.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughness != null) {
            toughness.setBaseValue(tier.getArmorToughness());
        }
        AttributeInstance dmg = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) {
            dmg.setBaseValue(tier.getAttackDamage());
        }
        AttributeInstance kb = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kb != null) {
            kb.setBaseValue(tier.getKnockbackResistance());
        }
    }

    private void applyOverdriveSpeedModifier(boolean active) {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        speed.removeModifier(OVERDRIVE_SPEED_MODIFIER_ID);
        if (active) {
            GolemMetalTier tier = getMetalTier();
            double boostAmount = tier.getBoostSpeed() - 0.25;
            speed.addTransientModifier(new AttributeModifier(OVERDRIVE_SPEED_MODIFIER_ID, boostAmount, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public void tick() {
        super.tick();

        int anim = getAttackAnimTicks();
        if (anim > 0) {
            setAttackAnimTicks(anim - 1);
        }

        Level lvl = this.level();
        if (lvl.isClientSide()) {
            if (isOverdrive()) {
                double jointY = this.random.nextBoolean() ? (this.getY() + 0.75) : (this.getY() + 1.45);
                double angle = this.random.nextDouble() * Math.PI * 2.0;
                double radius = 0.35 + this.random.nextDouble() * 0.15;
                double px = this.getX() + Math.cos(angle) * radius;
                double pz = this.getZ() + Math.sin(angle) * radius;
                lvl.addParticle(ParticleTypes.ELECTRIC_SPARK, px, jointY, pz, 0.0, 0.01, 0.0);
            } else if (getHeat() > 0.05f) {
                if (this.tickCount % 6 == 0) {
                    lvl.addParticle(ParticleTypes.SMOKE,
                            this.getX() + (this.random.nextDouble() - 0.5) * 0.4,
                            this.getY() + 1.5,
                            this.getZ() + (this.random.nextDouble() - 0.5) * 0.4,
                            0.0, 0.02, 0.0);
                }
            }
            return;
        }

        ServerLevel serverLevel = (ServerLevel) lvl;
        handleSandTrappedEscape(serverLevel);
        LivingEntity target = this.getTarget();

        if (target != null && target.isAlive()) {
            if (!isOverdrive() && getHeat() <= 0.01f) {
                double distanceSq = this.distanceToSqr(target);
                if (distanceSq > 12.0) {
                    setOverdrive(true);
                    setHeat(1.0f);
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.NEUTRAL, 0.8f, 1.6f);
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.BEACON_POWER_SELECT, SoundSource.NEUTRAL, 0.9f, 1.2f);
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            this.getX(), this.getY() + 1.2, this.getZ(), 24, 0.4, 0.6, 0.4, 0.12);
                }
            }
        } else {
            if (this.permanentOverdrive) {
                if (!isOverdrive()) {
                    setOverdrive(true);
                }
                setHeat(1.0f);
            } else {
                if (isOverdrive()) {
                    setOverdrive(false);
                }

                float currentHeat = getHeat();
                if (currentHeat > 0.0f) {
                    int cooldown = getMetalTier().getCooldownTicks();
                    float nextHeat = currentHeat - (1.0f / (float) cooldown);
                    if (nextHeat < 0.0f) {
                        nextHeat = 0.0f;
                    }
                    setHeat(nextHeat);

                    if (this.tickCount % 4 == 0) {
                        serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                this.getX(), this.getY() + 1.1, this.getZ(), 2, 0.25, 0.35, 0.25, 0.01);
                    }

                    if (this.tickCount % 25 == 0) {
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                                SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 0.35f, 0.65f);
                    }
                }
            }
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        setAttackAnimTicks(10);
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (isOverdrive()) {
            damage *= 1.4f;
        }

        DamageSource source = this.damageSources().mobAttack(this);
        boolean success = target.hurtServer(level, source, damage);

        if (success) {
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.42, 0.0));
            level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.IRON_GOLEM_ATTACK, SoundSource.NEUTRAL, 1.0f, 1.0f);

            if (isOverdrive()) {
                level.sendParticles(ParticleTypes.CRIT,
                        target.getX(), target.getY() + 1.0, target.getZ(), 12, 0.3, 0.3, 0.3, 0.1);
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        target.getX(), target.getY() + 0.8, target.getZ(), 10, 0.4, 0.4, 0.4, 0.08);
            }
        }

        return success;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        float currentHealth = this.getHealth();
        float maxHealth = this.getMaxHealth();

        if (currentHealth < maxHealth) {
            float healAmount = 0.0f;
            if (held.is(Items.IRON_INGOT)) {
                healAmount = 25.0f;
            } else if (held.is(Items.COPPER_INGOT)) {
                healAmount = 20.0f;
            } else if (held.is(Items.GOLD_INGOT)) {
                healAmount = 30.0f;
            } else if (held.is(Items.NETHERITE_INGOT)) {
                healAmount = 100.0f;
            }

            if (healAmount > 0.0f) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                this.heal(healAmount);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.IRON_GOLEM_REPAIR, SoundSource.NEUTRAL, 1.0f, 1.0f + (this.random.nextFloat() - 0.5f) * 0.2f);
                return InteractionResult.SUCCESS;
            }
        }

        if (this.ownerUUID.isEmpty()) {
            this.setOwnerUUID(player.getUUID());
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1.0f, 1.8f);
            player.sendSystemMessage(Component.translatable("message.sandstorm.golem_linked"));
            return InteractionResult.SUCCESS;
        }

        if (isOwner(player) && player.isShiftKeyDown()) {
            this.sentinelMode = !this.sentinelMode;
            if (this.sentinelMode) {
                this.getNavigation().stop();
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.IRON_GOLEM_STEP, SoundSource.NEUTRAL, 1.0f, 0.7f);
                player.sendSystemMessage(Component.translatable("message.sandstorm.golem_mode_sentinel"));
            } else {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.BEACON_POWER_SELECT, SoundSource.NEUTRAL, 1.0f, 1.5f);
                player.sendSystemMessage(Component.translatable("message.sandstorm.golem_mode_escort"));
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player || target instanceof CyberneticGolemEntity) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_DAMAGE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0f, 1.0f);
    }

    public int getDynamicLightLevel() {
        if (this.isOverdrive() || this.getHeat() > 0.05f) {
            return 9;
        }
        return 0;
    }

    public int getSandTrappedTicks() {
        return this.sandTrappedTicks;
    }

    public void setSandTrappedTicks(int ticks) {
        this.sandTrappedTicks = ticks;
    }

    public static boolean isManufacturedBlock(BlockState state) {
        if (state.isAir()) {
            return false;
        }
        Block block = state.getBlock();
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
        if (path.contains("planks")
                || path.contains("door")
                || path.contains("trapdoor")
                || path.contains("fence")
                || path.contains("gate")
                || path.contains("slab")
                || path.contains("stairs")
                || path.contains("wall")
                || path.contains("bed")
                || path.contains("wool")
                || path.contains("carpet")
                || path.contains("torch")
                || path.contains("lantern")
                || path.contains("sign")
                || path.contains("glass")
                || path.contains("brick")
                || path.contains("chest")
                || path.contains("barrel")
                || path.contains("furnace")
                || path.contains("smoker")
                || path.contains("anvil")
                || path.contains("crafting")
                || path.contains("hopper")
                || path.contains("lever")
                || path.contains("piston")
                || path.contains("repeater")
                || path.contains("comparator")
                || path.contains("redstone")
                || path.contains("copper")
                || path.contains("smooth_sandstone")
                || path.contains("cut_sandstone")
                || path.contains("chiseled")) {
            return true;
        }
        if (block == Blocks.COBBLESTONE || block == Blocks.MOSSY_COBBLESTONE
                || block == Blocks.IRON_BLOCK || block == Blocks.GOLD_BLOCK) {
            return true;
        }
        if (state.is(BlockTags.PLANKS)
                || state.is(BlockTags.WOODEN_DOORS)
                || state.is(BlockTags.WOODEN_TRAPDOORS)
                || state.is(BlockTags.WOODEN_FENCES)
                || state.is(BlockTags.FENCE_GATES)
                || state.is(BlockTags.WOODEN_SLABS)
                || state.is(BlockTags.WOODEN_STAIRS)
                || state.is(BlockTags.BEDS)
                || state.is(BlockTags.WOOL)
                || state.is(BlockTags.WOOL_CARPETS)
                || state.is(BlockTags.SIGNS)
                || state.is(BlockTags.WALL_HANGING_SIGNS)) {
            return true;
        }
        return BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("sandstorm");
    }

    public static boolean isNaturalSandEnvironment(ServerLevel serverLevel, BlockPos center) {
        if (SeismicSurvivalHandler.getTracker().isInsideSafeZone(center.getX(), center.getZ())) {
            return false;
        }
        int radius = 4;
        int verticalRadius = 3;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -verticalRadius; y <= verticalRadius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = center.offset(x, y, z);
                    BlockState checkState = serverLevel.getBlockState(checkPos);
                    if (isManufacturedBlock(checkState)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public List<BlockPos> getTrappingSandBlocks(ServerLevel serverLevel) {
        List<BlockPos> list = new ArrayList<>();
        if (!isNaturalSandEnvironment(serverLevel, this.blockPosition())) {
            return list;
        }

        AABB box = this.getBoundingBox();
        int minX = Mth.floor(box.minX + 0.1);
        int maxX = Mth.floor(box.maxX - 0.1);
        int minY = Mth.floor(box.minY + 0.1);
        int maxY = Mth.floor(box.maxY - 0.1);
        int minZ = Mth.floor(box.minZ + 0.1);
        int maxZ = Mth.floor(box.maxZ - 0.1);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = serverLevel.getBlockState(pos);
                    if (state.is(BlockTags.SAND)) {
                        list.add(pos);
                    }
                }
            }
        }
        return list;
    }

    public boolean attemptBreakFreeFromSand(ServerLevel serverLevel) {
        List<BlockPos> trapped = getTrappingSandBlocks(serverLevel);
        if (trapped.isEmpty()) {
            return false;
        }

        BlockPos targetPos = trapped.get(0);
        double eyeY = this.getEyeY();
        for (BlockPos pos : trapped) {
            if (Math.abs(pos.getY() + 0.5 - eyeY) < Math.abs(targetPos.getY() + 0.5 - eyeY)) {
                targetPos = pos;
            }
        }

        setAttackAnimTicks(10);
        serverLevel.destroyBlock(targetPos, true, this);
        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.IRON_GOLEM_ATTACK, SoundSource.NEUTRAL, 1.0f, 1.0f);
        serverLevel.sendParticles(ParticleTypes.CRIT,
                targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                12, 0.25, 0.25, 0.25, 0.1);

        if (this.isOverdrive()) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                    10, 0.3, 0.3, 0.3, 0.08);
        }

        return true;
    }

    private void handleSandTrappedEscape(ServerLevel serverLevel) {
        List<BlockPos> trapped = getTrappingSandBlocks(serverLevel);
        if (trapped.isEmpty()) {
            this.sandTrappedTicks = 0;
            return;
        }

        this.sandTrappedTicks++;
        if (this.sandTrappedTicks >= 60) {
            if (this.sandTrappedTicks % 20 == 0 && this.random.nextFloat() < 0.25f) {
                if (attemptBreakFreeFromSand(serverLevel)) {
                    this.sandTrappedTicks = Math.max(0, this.sandTrappedTicks - 40);
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Overdrive", this.isOverdrive());
        output.putBoolean("PermanentOverdrive", this.permanentOverdrive);
        output.putFloat("Heat", this.getHeat());
        output.putString("MetalTier", this.entityData.get(DATA_TIER));
        output.putBoolean("SentinelMode", this.sentinelMode);
        output.putInt("SandTrappedTicks", this.sandTrappedTicks);
        this.ownerUUID.ifPresent(uuid -> output.putString("OwnerUUID", uuid.toString()));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setOverdrive(input.getBooleanOr("Overdrive", false));
        this.setPermanentOverdrive(input.getBooleanOr("PermanentOverdrive", false));
        this.setHeat(input.getFloatOr("Heat", 0.0f));
        String tierId = input.getStringOr("MetalTier", "iron");
        this.setMetalTier(GolemMetalTier.byId(tierId));
        this.sentinelMode = input.getBooleanOr("SentinelMode", false);
        this.sandTrappedTicks = input.getIntOr("SandTrappedTicks", 0);
        String ownerStr = input.getStringOr("OwnerUUID", "");
        if (!ownerStr.isEmpty()) {
            try {
                this.ownerUUID = Optional.of(UUID.fromString(ownerStr));
            } catch (IllegalArgumentException ignored) {
                this.ownerUUID = Optional.empty();
            }
        }
    }

    private static class HostileTargetGoal extends NearestAttackableTargetGoal<Monster> {
        public HostileTargetGoal(CyberneticGolemEntity golem) {
            super(golem, Monster.class, false);
        }
    }

    private static class CyberneticFollowOwnerGoal extends Goal {
        private final CyberneticGolemEntity golem;
        private Player targetPlayer;
        private final double speedModifier;
        private final float stopDist;
        private final float startDist;

        public CyberneticFollowOwnerGoal(CyberneticGolemEntity golem, double speedModifier, float stopDist, float startDist) {
            this.golem = golem;
            this.speedModifier = speedModifier;
            this.stopDist = stopDist;
            this.startDist = startDist;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.golem.isSentinelMode() || this.golem.getTarget() != null) {
                return false;
            }

            Player owner = this.golem.getOwner();
            if (owner != null && owner.isAlive() && !owner.isSpectator() && !owner.isCreative()) {
                this.targetPlayer = owner;
                return this.golem.distanceToSqr(owner) > (this.startDist * this.startDist);
            }

            if (this.golem.getOwnerUUID().isEmpty()) {
                List<Player> nearby = this.golem.level().getEntitiesOfClass(
                        Player.class,
                        this.golem.getBoundingBox().inflate(32.0)
                );
                for (Player player : nearby) {
                    if (!player.isSpectator() && !player.isCreative()) {
                        this.targetPlayer = player;
                        return this.golem.distanceToSqr(player) > (this.startDist * this.startDist);
                    }
                }
            }

            return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.targetPlayer == null || !this.targetPlayer.isAlive() || this.targetPlayer.isSpectator()
                    || this.golem.isSentinelMode() || this.golem.getTarget() != null) {
                return false;
            }
            double distSq = this.golem.distanceToSqr(this.targetPlayer);
            if (distSq <= (this.stopDist * this.stopDist)) {
                this.golem.getNavigation().stop();
                return false;
            }
            return true;
        }

        @Override
        public void tick() {
            if (this.targetPlayer == null) {
                return;
            }

            this.golem.getLookControl().setLookAt(this.targetPlayer, 10.0f, (float) this.golem.getMaxHeadXRot());
            double distSq = this.golem.distanceToSqr(this.targetPlayer);

            if (distSq <= (this.stopDist * this.stopDist)) {
                this.golem.getNavigation().stop();
                return;
            }

            double currentSpeed = this.speedModifier;
            if (this.targetPlayer.isSprinting() || distSq > 144.0) {
                currentSpeed *= 1.45;
                if (distSq > 400.0 && this.golem.getHeat() <= 0.01f && !this.golem.isOverdrive()) {
                    this.golem.setOverdrive(true);
                    this.golem.setHeat(1.0f);
                }
            }

            double dx = this.golem.getX() - this.targetPlayer.getX();
            double dz = this.golem.getZ() - this.targetPlayer.getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len > 0.1) {
                double targetOffset = 4.0;
                double destX = this.targetPlayer.getX() + (dx / len) * targetOffset;
                double destZ = this.targetPlayer.getZ() + (dz / len) * targetOffset;
                this.golem.getNavigation().moveTo(destX, this.targetPlayer.getY(), destZ, currentSpeed);
            } else {
                this.golem.getNavigation().moveTo(this.targetPlayer, currentSpeed);
            }
        }

        @Override
        public void stop() {
            this.targetPlayer = null;
            this.golem.getNavigation().stop();
        }
    }

    private static class CyberneticDefendOwnerGoal extends Goal {
        private final CyberneticGolemEntity golem;
        private LivingEntity threatTarget;

        public CyberneticDefendOwnerGoal(CyberneticGolemEntity golem) {
            this.golem = golem;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            Player owner = this.golem.getOwner();
            if (owner != null && owner.isAlive() && !owner.isSpectator()) {
                LivingEntity attacker = owner.getLastHurtByMob();
                if (attacker != null && attacker.isAlive() && !(attacker instanceof Player) && !(attacker instanceof CyberneticGolemEntity)) {
                    this.threatTarget = attacker;
                    if (EmpParalysisHandler.isAndroidOrAutomaton(attacker) && !this.golem.isOverdrive() && this.golem.getHeat() <= 0.01f) {
                        this.golem.setOverdrive(true);
                        this.golem.setHeat(1.0f);
                    }
                    return true;
                }

                LivingEntity playerTarget = owner.getLastHurtMob();
                if (playerTarget != null && playerTarget.isAlive() && !(playerTarget instanceof Player) && !(playerTarget instanceof CyberneticGolemEntity)) {
                    this.threatTarget = playerTarget;
                    return true;
                }

                List<Mob> nearbyMobs = this.golem.level().getEntitiesOfClass(
                        Mob.class,
                        this.golem.getBoundingBox().inflate(24.0)
                );
                for (Mob mob : nearbyMobs) {
                    if (mob.getTarget() == owner && mob.isAlive() && !(mob instanceof CyberneticGolemEntity)) {
                        this.threatTarget = mob;
                        return true;
                    }
                }
            }

            if (this.golem.getOwnerUUID().isEmpty()) {
                List<Player> players = this.golem.level().getEntitiesOfClass(
                        Player.class,
                        this.golem.getBoundingBox().inflate(24.0)
                );
                for (Player player : players) {
                    if (player.isSpectator()) {
                        continue;
                    }
                    LivingEntity attacker = player.getLastHurtByMob();
                    if (attacker != null && attacker.isAlive() && !(attacker instanceof Player) && !(attacker instanceof CyberneticGolemEntity)) {
                        this.threatTarget = attacker;
                        return true;
                    }
                }
            }

            return false;
        }

        @Override
        public void start() {
            this.golem.setTarget(this.threatTarget);
            super.start();
        }
    }
}
