package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class BuilderDroneRenderState extends LivingEntityRenderState {
    public boolean isWelding = false;
    public boolean hasBlock = false;
    public BlockPos targetPos = BlockPos.ZERO;
    public Vec3 targetRelVec = null;
    public float animationTicks = 0.0f;
}
