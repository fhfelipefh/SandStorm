package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.megastructure.MegastructureBlueprint;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class MegastructureConstructorRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public MegastructureBlueprint blueprint = null;
    public int constructedBlocks = 0;
    public int totalBlocks = 0;
    public boolean isBuilding = false;
    public boolean isDone = false;
    public float animationTicks = 0.0f;
    public BlockPos targetRelPos = null;
    public BlockPos minPos = null;
    public BlockPos maxPos = null;
    public int materialReadiness = 0;
}
