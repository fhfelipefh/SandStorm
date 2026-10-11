package com.fhfelipefh.sandstorm.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

/**
 * Interface that abstracts modloader-specific features.
 * In a real multi-loader setup, this would be implemented in each platform's source set
 * and loaded via ServiceLoader.
 */
public interface IPlatformHelper {
    String getPlatformName();
    boolean isModLoaded(String modId);
    boolean isDevelopmentEnvironment();

    // Define a platform-agnostic event system or abstraction for specific events if needed
}
