package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class DeepCoreDrillRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean isDrilling = false;
    public float animationTicks = 0.0f;
}

