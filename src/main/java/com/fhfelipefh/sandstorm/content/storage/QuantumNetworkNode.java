package com.fhfelipefh.sandstorm.content.storage;

import net.minecraft.core.BlockPos;

public interface QuantumNetworkNode {
    BlockPos getNodePos();
    boolean isNodeActive();
}
