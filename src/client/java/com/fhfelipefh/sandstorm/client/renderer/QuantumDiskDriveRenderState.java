package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class QuantumDiskDriveRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public float animationTicks;
    public long lastActivityTick;
    public boolean isIoActive;
    public final boolean[] hasCartridge = new boolean[8];
    public final long[] storedCount = new long[8];
    public final long[] capacity = new long[8];
    public final int[] fillPercentage = new int[8];
    public long totalStored;
    public long totalCapacity;
    public int activeDisks;
}
