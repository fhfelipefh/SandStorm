package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class ElectricArcFurnaceRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean isProcessing = false;
    public float animationTicks = 0.0f;
    public boolean hasItem = false;
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
}

