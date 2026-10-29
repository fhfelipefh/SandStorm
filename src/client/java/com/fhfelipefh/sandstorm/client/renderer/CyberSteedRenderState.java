package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class CyberSteedRenderState extends LivingEntityRenderState {
    public boolean isTame = false;
    public boolean isSitting = false;
    public boolean isDeactivated = false;
    public boolean isCanisterExploded = false;
    public boolean hasSonicTurret = false;
    public boolean hasBatteryPack = false;
    public boolean hasSpeedOverdrive = false;
    public boolean hasCargoPanniers = false;
    public float animationTicks = 0.0f;
}
