package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class SandwormRenderState extends LivingEntityRenderState {
    public boolean burrowed;
    public boolean breaching;
    public boolean surfaced;
    public boolean submerging;
    public float breachProgress;
    public float submergeProgress;
    public float biteProgress;
    public float bodyPitch;
    public float bodyYaw;
    public boolean hasTarget;
    public float targetDistance;
    public float targetPitch;
    public float targetRelativeYaw;
    public float rearingProgress;
    public float slitherProgress;
    public boolean isSlithering;
    public float groundSink;
    public float groundSlopePitch;
    public float groundSlopeRoll;
}
