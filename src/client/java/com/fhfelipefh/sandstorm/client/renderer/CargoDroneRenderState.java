package com.fhfelipefh.sandstorm.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;

public class CargoDroneRenderState extends LivingEntityRenderState {
    public ItemStack cargoStack = ItemStack.EMPTY;
    public float animationTicks = 0.0f;
}
