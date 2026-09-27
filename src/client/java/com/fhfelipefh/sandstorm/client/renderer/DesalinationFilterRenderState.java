package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class DesalinationFilterRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public int progress = 0;
    public int maxProgress = 80;
    public boolean isProcessing = false;
    public float animationTicks = 0.0f;
    public int waterInput = 0;
    public int waterOutput = 0;
    public int maxWater = 4000;
    public boolean hasCartridge = false;
}
