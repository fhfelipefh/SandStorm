package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class RappelAnchorRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public int state = 0;
    public int deployedDepth = 0;
    public float spoolAngle = 0.0f;
    public float animationTicks = 0.0f;
}
