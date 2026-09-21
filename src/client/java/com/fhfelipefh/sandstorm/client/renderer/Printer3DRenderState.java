package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class Printer3DRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public int progress = 0;
    public int maxProgress = 100;
    public boolean isProcessing = false;
    public float animationTicks = 0.0f;
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
    public boolean hasItem = false;
}
