package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.block.WptRelayTowerBlock;
import com.fhfelipefh.sandstorm.content.block.WptRelayTowerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WptRelayTowerBlockEntity extends BlockEntity {
    private boolean active = false;

    public WptRelayTowerBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.WPT_RELAY_TOWER_BE, pos, state);
    }

    public boolean isActive() {
        return active;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WptRelayTowerBlockEntity entity) {
        if ((level.getGameTime() + pos.hashCode()) % 20 != 0) {
            return;
        }

        long primaryCharge = WirelessSolarReceiverManager.getPrimaryWptChargeAt(level, pos);
        boolean shouldBeActive = primaryCharge > 0L;

        if (entity.active != shouldBeActive) {
            entity.active = shouldBeActive;
            if (state.hasProperty(WptRelayTowerBlock.ACTIVE)) {
                level.setBlock(pos, state.setValue(WptRelayTowerBlock.ACTIVE, shouldBeActive), 3);
            }
        }

        if (shouldBeActive) {
            WptRelayTowerManager.registerTower(level.dimension(), pos, WptRelayTowerManager.DEFAULT_TRANSFER_RATE);
        } else {
            WptRelayTowerManager.unregisterTower(level.dimension(), pos);
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide()) {
            WptRelayTowerManager.unregisterTower(level.dimension(), worldPosition);
        }
        super.setRemoved();
    }
}
