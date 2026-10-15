package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class VaultDoorRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean open;
    public float progress;
}
