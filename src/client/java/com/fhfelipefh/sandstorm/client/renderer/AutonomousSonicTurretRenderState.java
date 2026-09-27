package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class AutonomousSonicTurretRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public float yaw = 0.0f;
    public float pitch = 0.0f;
    public boolean hasTarget = false;
    public int shootFlashTicks = 0;
    public float animationTicks = 0.0f;
    public int energy = 0;
}
