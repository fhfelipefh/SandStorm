package com.fhfelipefh.sandstorm.client.renderer;

import java.util.UUID;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class QuantumSleeperPodRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean active = true;
    public boolean hasClone = false;
    public boolean open = false;
    public float doorProgress = 0.0f;
    public float animationTicks = 0.0f;
    public int bioNutrients = 100;
    public int energy = 100000;
    public UUID ownerUuid = null;
    public String customPodName = "";
}

