package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface OverridableMachine {
    boolean isOverridden();

    void setOverridden(boolean overridden);

    UUID getOverrideOwner();

    void setOverrideOwner(UUID owner);

    boolean canBeRiddenBy(Player player);
}

