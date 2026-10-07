package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.BiosphereDoorBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BiosphereDoorBlockEntity extends BlockEntity {
    private static final long ENERGY_PER_TRANSITION = 20L;
    private static final int CHECK_INTERVAL = 5;
    private int checkCooldown;

    public BiosphereDoorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.BIOSPHERE_DOOR_BE, pos, state);
    }

    public BiosphereDoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide() || --checkCooldown > 0) {
            return;
        }
        checkCooldown = CHECK_INTERVAL;
        boolean playerNearby = !level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(4.0), Player::isAlive).isEmpty();
        boolean open = state.getValue(BiosphereDoorBlock.OPEN);
        if (playerNearby == open || WirelessSolarReceiverManager.getWptChargeAt(level, pos) < ENERGY_PER_TRANSITION) {
            return;
        }
        level.setBlock(pos, state.setValue(BiosphereDoorBlock.OPEN, playerNearby), 3);
        setChanged();
    }
}
