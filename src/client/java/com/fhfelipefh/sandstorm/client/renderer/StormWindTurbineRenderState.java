package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class StormWindTurbineRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public float animationTicks = 0.0f;
    public long generationRate = 0L;
}
