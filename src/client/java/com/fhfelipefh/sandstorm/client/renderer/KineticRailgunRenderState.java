package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class KineticRailgunRenderState extends BlockEntityRenderState {
    public float animationTicks = 0.0f;
    public int cooldown = 0;
    public boolean readyToFire = false;
    public int storedEnergy = 0;
}
