package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum GolemMetalTier {
    IRON("iron", 100.0, 10.0, 2.0, 15.0, 0.6, 1.30, 180),
    COPPER("copper", 85.0, 7.0, 1.0, 13.0, 0.4, 1.45, 120),
    GOLD("gold", 75.0, 6.0, 0.0, 18.0, 0.3, 1.55, 100),
    NETHERITE("netherite", 200.0, 20.0, 8.0, 26.0, 1.0, 1.35, 160),
    COMPOSITE("composite", 110.0, 12.0, 2.0, 16.0, 0.5, 1.35, 170);

    private final String id;
    private final double maxHealth;
    private final double armor;
    private final double armorToughness;
    private final double attackDamage;
    private final double knockbackResistance;
    private final double boostSpeed;
    private final int cooldownTicks;

    GolemMetalTier(String id, double maxHealth, double armor, double armorToughness,
                   double attackDamage, double knockbackResistance, double boostSpeed, int cooldownTicks) {
        this.id = id;
        this.maxHealth = maxHealth;
        this.armor = armor;
        this.armorToughness = armorToughness;
        this.attackDamage = attackDamage;
        this.knockbackResistance = knockbackResistance;
        this.boostSpeed = boostSpeed;
        this.cooldownTicks = cooldownTicks;
    }

    public String getId() {
        return id;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getArmor() {
        return armor;
    }

    public double getArmorToughness() {
        return armorToughness;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public double getKnockbackResistance() {
        return knockbackResistance;
    }

    public double getBoostSpeed() {
        return boostSpeed;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    public static boolean isMetallicBlock(BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }
        if (state.is(Blocks.IRON_BLOCK)
                || state.is(Blocks.GOLD_BLOCK)
                || state.is(Blocks.NETHERITE_BLOCK)
                || state.is(Blocks.RAW_IRON_BLOCK)
                || state.is(Blocks.RAW_GOLD_BLOCK)
                || state.is(Blocks.HEAVY_CORE)
                || state.is(BlockTags.ANVIL)) {
            return true;
        }
        SoundType sound = state.getSoundType();
        if (sound == SoundType.METAL
                || sound == SoundType.COPPER
                || sound == SoundType.ANVIL
                || sound == SoundType.NETHERITE_BLOCK
                || sound == SoundType.HEAVY_CORE) {
            return true;
        }
        Identifier key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (key != null) {
            String path = key.getPath();
            return path.contains("copper") || path.contains("iron") || path.contains("gold")
                    || path.contains("netherite") || path.contains("metal") || path.contains("steel")
                    || path.contains("titanium") || path.contains("bronze");
        }
        return false;
    }

    public static GolemMetalTier fromBlockState(BlockState state) {
        if (state == null || state.isAir()) {
            return null;
        }
        Identifier key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String path = key != null ? key.getPath() : "";

        if (state.is(Blocks.NETHERITE_BLOCK) || path.contains("netherite")) {
            return NETHERITE;
        }
        if (state.is(Blocks.GOLD_BLOCK) || state.is(Blocks.RAW_GOLD_BLOCK) || path.contains("gold")) {
            return GOLD;
        }
        if (state.getSoundType() == SoundType.COPPER || path.contains("copper")) {
            return COPPER;
        }
        if (state.is(Blocks.IRON_BLOCK) || state.is(Blocks.RAW_IRON_BLOCK) || path.contains("iron")) {
            return IRON;
        }
        if (isMetallicBlock(state)) {
            return COMPOSITE;
        }
        return null;
    }

    public static GolemMetalTier determineTier(List<BlockState> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return IRON;
        }
        Map<GolemMetalTier, Integer> counts = new HashMap<>();
        for (BlockState state : blocks) {
            GolemMetalTier tier = fromBlockState(state);
            if (tier != null) {
                counts.put(tier, counts.getOrDefault(tier, 0) + 1);
            }
        }
        GolemMetalTier best = IRON;
        int maxCount = -1;
        for (Map.Entry<GolemMetalTier, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                best = entry.getKey();
            }
        }
        return best;
    }

    public static GolemMetalTier byId(String id) {
        if (id == null) {
            return IRON;
        }
        for (GolemMetalTier tier : values()) {
            if (tier.id.equalsIgnoreCase(id)) {
                return tier;
            }
        }
        return IRON;
    }
}
