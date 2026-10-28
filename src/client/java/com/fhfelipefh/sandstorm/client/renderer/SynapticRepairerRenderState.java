package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class SynapticRepairerRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
    public boolean hasItem = false;
    public boolean isRepairing = false;
    public float animationTicks = 0.0f;
    public int fluidAmount = 0;
    public int damageValue = 0;
    public int maxDamage = 0;
}

