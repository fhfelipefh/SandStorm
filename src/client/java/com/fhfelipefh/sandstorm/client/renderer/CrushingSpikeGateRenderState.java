package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class CrushingSpikeGateRenderState extends BlockEntityRenderState {
    public float progress;
    public Direction facing = Direction.NORTH;
}
